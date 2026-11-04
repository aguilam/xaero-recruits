package com.aguilam.xaerorecruits.highlight;

import com.aguilam.xaerorecruits.claims.ClaimIndex;
import com.aguilam.xaerorecruits.claims.FactionColors;
import com.aguilam.xaerorecruits.config.XRConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.minimap.highlight.ChunkHighlighter;
import xaero.hud.minimap.info.render.compile.InfoDisplayCompiler;

public final class MinimapClaimHighlighter extends ChunkHighlighter {
    public MinimapClaimHighlighter() {
        super(true);
    }

    private static boolean minimapVisible() {
        return XRConfig.CLIENT.showClaims.get() && XRConfig.CLIENT.showOnMinimap.get();
    }

    @Override
    public boolean regionHasHighlights(ResourceKey<Level> dimension, int regionX, int regionZ) {
        return ClaimIndex.get().getSnapshot().hasRegion(regionX, regionZ);
    }

    @Override
    protected int[] getColors(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        if (!minimapVisible()) {
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
    public boolean chunkIsHighlit(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return ClaimIndex.get().getSnapshot().hasChunk(chunkX, chunkZ);
    }

    @Override
    public void addChunkHighlightTooltips(InfoDisplayCompiler compiler, ResourceKey<Level> dimension, int chunkX, int chunkZ,
                                          int width) {
        if (!minimapVisible()) {
            return;
        }

        ClaimIndex.ChunkClaim claim = ClaimIndex.get().getSnapshot().getChunk(chunkX, chunkZ);
        if (claim == null) {
            return;
        }

        compiler.addLine(Component.literal(claim.claimName())
                .withStyle(style -> style.withColor(claim.color() & 0xFFFFFF)));
        compiler.addLine(Component.literal(claim.factionName())
                .withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true)));
    }
}
