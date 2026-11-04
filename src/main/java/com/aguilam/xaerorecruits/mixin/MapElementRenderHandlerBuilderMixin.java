package com.aguilam.xaerorecruits.mixin;

import com.aguilam.xaerorecruits.label.WorldMapClaimLabelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.element.MapElementRenderHandler;

@Mixin(value = MapElementRenderHandler.Builder.class, remap = false)
public abstract class MapElementRenderHandlerBuilderMixin {
    @Inject(method = "build", at = @At("RETURN"))
    private void xaerorecruits$addClaimLabelRenderer(CallbackInfoReturnable<MapElementRenderHandler> callbackInfo) {
        MapElementRenderHandler handler = callbackInfo.getReturnValue();
        if (handler != null) {
            handler.add(new WorldMapClaimLabelRenderer());
        }
    }
}
