package com.aguilam.xaerorecruits.highlight;

import com.aguilam.xaerorecruits.claims.ClaimIndex;
import com.aguilam.xaerorecruits.claims.FactionColors;
import com.aguilam.xaerorecruits.config.XRConfig;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.map.highlight.ChunkHighlighter;

public final class WorldMapClaimHighlighter extends ChunkHighlighter {
    public WorldMapClaimHighlighter() {
        super(true);
    }

    @Override
    public boolean regionHasHighlights(ResourceKey<Level> dimension, int regionX, int regionZ) {
        return XRConfig.CLIENT.showClaims.get() && ClaimIndex.get().getSnapshot().hasRegion(regionX, regionZ);
    }

    @Override
    protected int[] getColors(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        if (!XRConfig.CLIENT.showClaims.get()) {
            return null;
        }

        ClaimIndex.Snapshot snapshot = ClaimIndex.get().getSnapshot();
        ClaimIndex.ChunkClaim claim = snapshot.getChunk(chunkX, chunkZ);
        if (claim == null) {
            return null;
        }

        int fillAlpha = 255 * XRConfig.CLAIM_FILL_OPACITY / 100;
        int borderAlpha = 255 * XRConfig.CLAIM_BORDER_OPACITY / 100;
        int centerColor = FactionColors.toXaeroColor(claim.color(), fillAlpha);
        int borderColor = FactionColors.toXaeroColor(claim.color(), borderAlpha);

        this.resultStore[0] = centerColor;
        this.resultStore[1] = sameClaim(snapshot, claim, chunkX, chunkZ - 1) ? centerColor : borderColor;
        this.resultStore[2] = sameClaim(snapshot, claim, chunkX + 1, chunkZ) ? centerColor : borderColor;
        this.resultStore[3] = sameClaim(snapshot, claim, chunkX, chunkZ + 1) ? centerColor : borderColor;
        this.resultStore[4] = sameClaim(snapshot, claim, chunkX - 1, chunkZ) ? centerColor : borderColor;
        return this.resultStore;
    }

    private static boolean sameClaim(ClaimIndex.Snapshot snapshot, ClaimIndex.ChunkClaim claim, int chunkX, int chunkZ) {
        ClaimIndex.ChunkClaim neighbor = snapshot.getChunk(chunkX, chunkZ);
        return neighbor != null && neighbor.claimId().equals(claim.claimId());
    }

    @Override
    public int calculateRegionHash(ResourceKey<Level> dimension, int regionX, int regionZ) {
        if (!XRConfig.CLIENT.showClaims.get()) {
            return 0;
        }

        ClaimIndex.Snapshot snapshot = ClaimIndex.get().getSnapshot();
        if (!snapshot.hasRegion(regionX, regionZ)) {
            return 0;
        }

        long hash = 1125899906842597L;
        hash = mix(hash, snapshot.getRegionHash(regionX, regionZ));
        return (int) (hash ^ hash >>> 32);
    }

    private static long mix(long hash, long value) {
        hash ^= value;
        hash *= 1099511628211L;
        return hash;
    }

    @Override
    public boolean chunkIsHighlit(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return XRConfig.CLIENT.showClaims.get() && ClaimIndex.get().getSnapshot().hasChunk(chunkX, chunkZ);
    }

    @Override
    public Component getChunkHighlightSubtleTooltip(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        ClaimIndex.ChunkClaim claim = ClaimIndex.get().getSnapshot().getChunk(chunkX, chunkZ);
        if (claim == null) {
            return null;
        }

        return Component.literal(claim.claimName())
                .withStyle(style -> style.withColor(claim.color() & 0xFFFFFF))
                .append(Component.literal(" - " + claim.factionName()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Component getChunkHighlightBluntTooltip(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return null;
    }

    @Override
    public void addMinimapBlockHighlightTooltips(List<Component> list, ResourceKey<Level> dimension, int blockX, int blockZ,
                                                 int width) {
    }
}
