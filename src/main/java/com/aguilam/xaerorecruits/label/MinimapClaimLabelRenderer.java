package com.aguilam.xaerorecruits.label;

import com.aguilam.xaerorecruits.claims.ClaimIndex;
import com.aguilam.xaerorecruits.config.XRConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import xaero.common.XaeroMinimapSession;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.misc.Misc;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderProvider;
import xaero.hud.minimap.element.render.MinimapElementRenderer;

public final class MinimapClaimLabelRenderer extends MinimapElementRenderer<ClaimIndex.ClaimLabel, Object> {
    private boolean drawn;

    public MinimapClaimLabelRenderer() {
        super(new Reader(), new Provider(), new Object());
    }

    private static boolean minimapVisible() {
        return XRConfig.CLIENT.showClaims.get() && XRConfig.CLIENT.showOnMinimap.get();
    }

    @Override
    public boolean renderElement(ClaimIndex.ClaimLabel label, boolean highlighted, boolean outOfBounds, double optionalDepth,
                                 float optionalScale, double partialX, double partialY, MinimapElementRenderInfo renderInfo,
                                 GuiGraphics guiGraphics, BufferSource vanillaBufferSource) {
        if (!minimapVisible()) {
            return false;
        }

        try {
            XaeroMinimapSession session = XaeroMinimapSession.getCurrentSession();
            if (session != null && session.getMinimapProcessor() != null
                    && session.getMinimapProcessor().getMinimapZoom() > XRConfig.CLIENT.minimapLabelMaxZoom.get()) {
                return false;
            }
        } catch (Throwable ignored) {

        }

        Font font = Minecraft.getInstance().font;
        float scale = Math.max(0.1F, optionalScale
                * LabelSizing.baseScale(font)
                * XRConfig.CLIENT.minimapLabelScale.get().floatValue());
        String text = label.name();
        float textWidth = (float) font.width(text);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(partialX, partialY, optionalDepth);
        poseStack.scale(scale, scale, 1.0F);
        Misc.drawNormalText(poseStack, text, -textWidth / 2.0F, -font.lineHeight / 2.0F, 0xFFFFFFFF, true, vanillaBufferSource);
        poseStack.popPose();

        this.drawn = true;
        return true;
    }

    @Override
    public void preRender(MinimapElementRenderInfo renderInfo, BufferSource vanillaBufferSource,
                          MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers) {
    }

    @Override
    public void postRender(MinimapElementRenderInfo renderInfo, BufferSource vanillaBufferSource,
                           MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers) {
        if (this.drawn) {
            vanillaBufferSource.endBatch();
            this.drawn = false;
        }
    }

    @Override
    public boolean shouldRender(MinimapElementRenderLocation location) {
        return location == MinimapElementRenderLocation.OVER_MINIMAP
                && minimapVisible();
    }

    @Override
    public int getOrder() {
        return 50;
    }

    private static final class Reader extends MinimapElementReader<ClaimIndex.ClaimLabel, Object> {
        @Override
        public boolean isHidden(ClaimIndex.ClaimLabel element, Object context) {
            return false;
        }

        @Override
        public double getRenderX(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return element.blockX();
        }

        @Override
        public double getRenderY(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return 0.0D;
        }

        @Override
        public double getRenderZ(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return element.blockZ();
        }

        @Override
        public int getInteractionBoxLeft(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return 0;
        }

        @Override
        public int getInteractionBoxRight(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return 0;
        }

        @Override
        public int getInteractionBoxTop(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return 0;
        }

        @Override
        public int getInteractionBoxBottom(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return 0;
        }

        @Override
        public int getRenderBoxLeft(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return -64;
        }

        @Override
        public int getRenderBoxRight(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return 64;
        }

        @Override
        public int getRenderBoxTop(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return -8;
        }

        @Override
        public int getRenderBoxBottom(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return 8;
        }

        @Override
        public int getLeftSideLength(ClaimIndex.ClaimLabel element, Minecraft minecraft) {
            return 0;
        }

        @Override
        public String getMenuName(ClaimIndex.ClaimLabel element) {
            return "Claim";
        }

        @Override
        public String getFilterName(ClaimIndex.ClaimLabel element) {
            return "Claim";
        }

        @Override
        public int getMenuTextFillLeftPadding(ClaimIndex.ClaimLabel element) {
            return 0;
        }

        @Override
        public int getRightClickTitleBackgroundColor(ClaimIndex.ClaimLabel element) {
            return 0;
        }

        @Override
        public boolean shouldScaleBoxWithOptionalScale() {
            return false;
        }
    }

    private static final class Provider extends MinimapElementRenderProvider<ClaimIndex.ClaimLabel, Object> {
        private java.util.List<ClaimIndex.ClaimLabel> labels = java.util.List.of();
        private int index;

        @Override
        public void begin(MinimapElementRenderLocation location, Object context) {
            this.labels = ClaimIndex.get().getSnapshot().labels();
            this.index = 0;
        }

        @Override
        public boolean hasNext(MinimapElementRenderLocation location, Object context) {
            return this.index < this.labels.size();
        }

        @Override
        public ClaimIndex.ClaimLabel getNext(MinimapElementRenderLocation location, Object context) {
            return this.labels.get(this.index++);
        }

        @Override
        public void end(MinimapElementRenderLocation location, Object context) {
            this.labels = java.util.List.of();
            this.index = 0;
        }
    }
}
