package com.aguilam.xaerorecruits.claims;

import com.aguilam.xaerorecruits.XaeroRecruitsMod;
import com.talhanation.recruits.client.ClientManager;
import com.talhanation.recruits.world.RecruitsClaim;
import com.talhanation.recruits.world.RecruitsFaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;

public final class ClaimIndex {
    private static final ClaimIndex INSTANCE = new ClaimIndex();
    private static final long REFRESH_INTERVAL_MS = 500L;

    private volatile Snapshot snapshot = Snapshot.EMPTY;
    private boolean dirty = true;
    private long lastCheck;
    private long lastSignature = Long.MIN_VALUE;

    private ClaimIndex() {
    }

    public static ClaimIndex get() {
        return INSTANCE;
    }

    public Snapshot getSnapshot() {
        return this.snapshot;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void tick() {
        if (Minecraft.getInstance().level == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (!this.dirty && now - this.lastCheck < REFRESH_INTERVAL_MS) {
            return;
        }
        this.lastCheck = now;
        this.dirty = false;

        Snapshot next;
        try {
            next = this.build();
        } catch (RuntimeException exception) {

            XaeroRecruitsMod.LOGGER.debug("Could not rebuild the claims snapshot", exception);
            return;
        }

        if (next.signature() != this.lastSignature) {
            this.snapshot = next;
            this.lastSignature = next.signature();
            XaeroRefresher.refreshAll();
        }
    }

    private Snapshot build() {
        List<RecruitsClaim> claims;
        try {
            claims = new ArrayList<>(ClientManager.recruitsClaims);
        } catch (RuntimeException exception) {
            return this.snapshot;
        }

        long signature = 1469598103934665603L;

        Map<Long, ChunkClaim> chunks = new HashMap<>();
        for (RecruitsClaim claim : claims) {
            if (claim == null || claim.isRemoved) {
                continue;
            }

            RecruitsFaction faction = claim.getOwnerFaction();
            if (faction == null) {
                continue;
            }

            String factionId = faction.getStringID();
            if (factionId == null) {
                factionId = "";
            }
            String factionName = faction.getTeamDisplayName();
            if (factionName == null || factionName.isBlank()) {
                factionName = factionId;
            }
            int color = FactionColors.colorFor(faction, factionId);
            String claimName = claim.getName() == null ? "" : claim.getName();
            String claimId = claim.getUUID() == null ? "" : claim.getUUID().toString();

            signature = mix(signature, factionId.hashCode());
            signature = mix(signature, factionName.hashCode());
            signature = mix(signature, color);
            signature = mix(signature, claimId.hashCode());
            signature = mix(signature, claimName.hashCode());

            List<ChunkPos> claimedChunks = claim.getClaimedChunks();
            if (claimedChunks == null) {
                continue;
            }

            for (ChunkPos chunk : claimedChunks) {
                if (chunk == null) {
                    continue;
                }
                long key = key(chunk.x, chunk.z);
                signature = mix(signature, key);
                signature = mix(signature, factionId.hashCode());
                signature = mix(signature, claimId.hashCode());

                chunks.putIfAbsent(key, new ChunkClaim(claimId, factionId, factionName, claimName, color));
            }
        }

        if (signature == this.lastSignature) {
            return this.snapshot;
        }

        if (chunks.isEmpty()) {
            return new Snapshot(Map.of(), Set.of(), List.of(), Map.of(), signature);
        }

        Map<String, LabelAccumulator> labelAccumulators = new LinkedHashMap<>();
        Map<Long, RegionHashAccumulator> regionAccumulators = new HashMap<>();
        Set<Long> regions = new HashSet<>();
        for (Map.Entry<Long, ChunkClaim> entry : chunks.entrySet()) {
            long key = entry.getKey();
            int chunkX = (int) (key >> 32);
            int chunkZ = (int) key;
            ChunkClaim chunkClaim = entry.getValue();

            labelAccumulators.computeIfAbsent(
                    chunkClaim.factionId(),
                    id -> new LabelAccumulator(chunkClaim.factionName())
            ).add(chunkX, chunkZ);

            int regionX = chunkX >> 5;
            int regionZ = chunkZ >> 5;
            regions.add(key(regionX, regionZ));

            addRegionHash(regionAccumulators, regionX, regionZ, hashChunk(chunkX, chunkZ, chunkClaim));
            addBorderRegionHashes(regionAccumulators, regionX, regionZ, chunkX, chunkZ, chunkClaim.claimId());
        }

        Map<Long, Integer> regionHashes = new HashMap<>(regions.size());
        for (long regionKey : regions) {
            RegionHashAccumulator accumulator = regionAccumulators.get(regionKey);
            regionHashes.put(regionKey, accumulator == null ? 0 : accumulator.finish());
        }

        List<ClaimLabel> labels = new ArrayList<>(labelAccumulators.size());
        for (Map.Entry<String, LabelAccumulator> entry : labelAccumulators.entrySet()) {
            LabelAccumulator accumulator = entry.getValue();
            labels.add(new ClaimLabel(
                    accumulator.name.toUpperCase(Locale.ROOT),
                    accumulator.centerX(),
                    accumulator.centerZ()
            ));
        }
        labels.sort(Comparator.comparing(ClaimLabel::name, String.CASE_INSENSITIVE_ORDER));

        return new Snapshot(
                Collections.unmodifiableMap(chunks),
                Collections.unmodifiableSet(regions),
                Collections.unmodifiableList(labels),
                Collections.unmodifiableMap(regionHashes),
                signature
        );
    }

    private static long mix(long hash, long value) {
        hash ^= value;
        hash *= 1099511628211L;
        return hash;
    }

    private static long hashChunk(int chunkX, int chunkZ, ChunkClaim claim) {
        long hash = 1125899906842597L;
        hash = mix(hash, key(chunkX, chunkZ));
        hash = mix(hash, claim.hashCode());
        return hash;
    }

    private static long hashBorderChunk(int chunkX, int chunkZ, String claimId) {
        long hash = 0x9E3779B97F4A7C15L;
        hash = mix(hash, key(chunkX, chunkZ));
        hash = mix(hash, claimId == null ? 0 : claimId.hashCode());
        return hash;
    }

    private static void addRegionHash(Map<Long, RegionHashAccumulator> accumulators, int regionX, int regionZ, long value) {
        accumulators.computeIfAbsent(key(regionX, regionZ), ignored -> new RegionHashAccumulator()).add(value);
    }

    private static void addBorderRegionHashes(Map<Long, RegionHashAccumulator> accumulators, int regionX, int regionZ,
                                              int chunkX, int chunkZ, String claimId) {
        int localX = chunkX & 31;
        int localZ = chunkZ & 31;
        if (localX != 0 && localX != 31 && localZ != 0 && localZ != 31) {
            return;
        }

        long borderHash = hashBorderChunk(chunkX, chunkZ, claimId);
        boolean west = localX == 0;
        boolean east = localX == 31;
        boolean north = localZ == 0;
        boolean south = localZ == 31;

        if (west) {
            addRegionHash(accumulators, regionX - 1, regionZ, borderHash);
        }
        if (east) {
            addRegionHash(accumulators, regionX + 1, regionZ, borderHash);
        }
        if (north) {
            addRegionHash(accumulators, regionX, regionZ - 1, borderHash);
        }
        if (south) {
            addRegionHash(accumulators, regionX, regionZ + 1, borderHash);
        }
        if (west && north) {
            addRegionHash(accumulators, regionX - 1, regionZ - 1, borderHash);
        }
        if (west && south) {
            addRegionHash(accumulators, regionX - 1, regionZ + 1, borderHash);
        }
        if (east && north) {
            addRegionHash(accumulators, regionX + 1, regionZ - 1, borderHash);
        }
        if (east && south) {
            addRegionHash(accumulators, regionX + 1, regionZ + 1, borderHash);
        }
    }

    private static long key(int chunkX, int chunkZ) {
        return (long) chunkX << 32 | chunkZ & 0xFFFFFFFFL;
    }

    public record Snapshot(Map<Long, ChunkClaim> chunks, Set<Long> regions, List<ClaimLabel> labels,
                           Map<Long, Integer> regionHashes, long signature) {
        public static final Snapshot EMPTY = new Snapshot(Map.of(), Set.of(), List.of(), Map.of(), 0L);

        public ChunkClaim getChunk(int chunkX, int chunkZ) {
            return this.chunks.get(key(chunkX, chunkZ));
        }

        public boolean hasChunk(int chunkX, int chunkZ) {
            return this.chunks.containsKey(key(chunkX, chunkZ));
        }

        public boolean hasRegion(int regionX, int regionZ) {
            return this.regions.contains(key(regionX, regionZ));
        }

        public int getRegionHash(int regionX, int regionZ) {
            Integer hash = this.regionHashes.get(key(regionX, regionZ));
            return hash == null ? 0 : hash;
        }
    }

    public record ChunkClaim(String claimId, String factionId, String factionName, String claimName, int color) {
    }

    public record ClaimLabel(String name, int blockX, int blockZ) {
    }

    private static final class RegionHashAccumulator {
        private long sum;
        private long xored;
        private int count;

        private void add(long value) {
            this.sum += value;
            this.xored ^= Long.rotateLeft(value, 31);
            this.count++;
        }

        private int finish() {
            long hash = 1125899906842597L;
            hash = mix(hash, this.count);
            hash = mix(hash, this.sum);
            hash = mix(hash, this.xored);
            return (int) (hash ^ hash >>> 32);
        }
    }

    private static final class LabelAccumulator {
        private final String name;
        private long sumX;
        private long sumZ;
        private int count;

        private LabelAccumulator(String name) {
            this.name = name;
        }

        private void add(int chunkX, int chunkZ) {
            this.sumX += (long) chunkX * 16L + 8L;
            this.sumZ += (long) chunkZ * 16L + 8L;
            this.count++;
        }

        private int centerX() {
            return this.count == 0 ? 0 : (int) (this.sumX / this.count);
        }

        private int centerZ() {
            return this.count == 0 ? 0 : (int) (this.sumZ / this.count);
        }
    }
}
