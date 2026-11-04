package com.aguilam.xaerorecruits.label;

import com.aguilam.xaerorecruits.claims.ClaimIndex;
import com.aguilam.xaerorecruits.config.XRConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import xaero.map.element.render.ElementReader;
import xaero.map.element.render.ElementRenderInfo;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.element.render.ElementRenderProvider;
import xaero.map.element.render.ElementRenderer;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.map.misc.Misc;

public final class WorldMapClaimLabelRenderer extends ElementRenderer<ClaimIndex.ClaimLabel, Object, WorldMapClaimLabelRenderer> {
    private boolean drawn;

    public WorldMapClaimLabelRenderer() {
        super(new Object(), new Provider(), new Reader());
    }

    @Override
    public void preRender(ElementRenderInfo renderInfo, BufferSource vanillaBufferSource,
                          MultiTextureRenderTypeRendererProvider rendererProvider, boolean shadow) {
    }

    @Override
    public void postRender(ElementRenderInfo renderInfo, BufferSource vanillaBufferSource,
                           MultiTextureRenderTypeRendererProvider rendererProvider, boolean shadow) {
        if (this.drawn) {
            vanillaBufferSource.endBatch();
            this.drawn = false;
        }
    }

    @Override
    public void renderElementShadow(ClaimIndex.ClaimLabel label, boolean highlighted, float optionalScale, double partialX,
                                    double partialY, ElementRenderInfo renderInfo, GuiGraphics guiGraphics,
                                    BufferSource vanillaBufferSource,
                                    MultiTextureRenderTypeRendererProvider rendererProvider) {
    }

    @Override
    public boolean renderElement(ClaimIndex.ClaimLabel label, boolean highlighted, double optionalDepth, float optionalScale,
                                 double partialX, double partialY, ElementRenderInfo renderInfo, GuiGraphics guiGraphics,
                                 BufferSource vanillaBufferSource,
                                 MultiTextureRenderTypeRendererProvider rendererProvider) {
        if (!XRConfig.CLIENT.showClaims.get()
                || renderInfo.scale < XRConfig.CLIENT.worldMapLabelMinScale.get()
                || renderInfo.scale > XRConfig.CLIENT.worldMapLabelMaxScale.get()) {
            return false;
        }

        Font font = Minecraft.getInstance().font;
        float scale = Math.max(0.1F, optionalScale
                * LabelSizing.baseScale(font)
                * XRConfig.CLIENT.worldMapLabelScale.get().floatValue());
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
    public boolean shouldRender(ElementRenderLocation location, boolean shadow) {
        return !shadow
                && location == ElementRenderLocation.WORLD_MAP
                && XRConfig.CLIENT.showClaims.get();
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private static final class Reader extends ElementReader<ClaimIndex.ClaimLabel, Object, WorldMapClaimLabelRenderer> {
        @Override
        public boolean isHidden(ClaimIndex.ClaimLabel element, Object context) {
            return false;
        }

        @Override
        public double getRenderX(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return element.blockX();
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
            return -128;
        }

        @Override
        public int getRenderBoxRight(ClaimIndex.ClaimLabel element, Object context, float partialTicks) {
            return 128;
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

    private static final class Provider extends ElementRenderProvider<ClaimIndex.ClaimLabel, Object> {
        private java.util.List<ClaimIndex.ClaimLabel> labels = java.util.List.of();
        private int index;

        @Override
        public void begin(ElementRenderLocation location, Object context) {
            this.labels = ClaimIndex.get().getSnapshot().labels();
            this.index = 0;
        }

        @Override
        public boolean hasNext(ElementRenderLocation location, Object context) {
            return this.index < this.labels.size();
        }

        @Override
        public ClaimIndex.ClaimLabel getNext(ElementRenderLocation location, Object context) {
            return this.labels.get(this.index++);
        }

        @Override
        public void end(ElementRenderLocation location, Object context) {
            this.labels = java.util.List.of();
            this.index = 0;
        }
    }
}
