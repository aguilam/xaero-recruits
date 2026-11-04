package com.aguilam.xaerorecruits.mixin;

import com.aguilam.xaerorecruits.label.MinimapClaimLabelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.hud.minimap.Minimap;

@Mixin(value = Minimap.class, remap = false)
public abstract class MinimapMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void xaerorecruits$addClaimLabelRenderer(HudMod modMain, CallbackInfo callbackInfo) {
        ((Minimap) (Object) this).getOverMapRendererHandler().add(new MinimapClaimLabelRenderer());
    }
}
