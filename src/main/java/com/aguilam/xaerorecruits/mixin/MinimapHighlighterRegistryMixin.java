package com.aguilam.xaerorecruits.mixin;

import com.aguilam.xaerorecruits.highlight.MinimapClaimHighlighter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.minimap.highlight.HighlighterRegistry;

@Mixin(value = HighlighterRegistry.class, remap = false)
public abstract class MinimapHighlighterRegistryMixin {
    @Inject(method = "end", at = @At("HEAD"))
    private void xaerorecruits$registerClaimHighlighter(CallbackInfo callbackInfo) {
        ((HighlighterRegistry) (Object) this).register(new MinimapClaimHighlighter());
    }
}
