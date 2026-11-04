package com.aguilam.xaerorecruits.label;

import net.minecraft.client.gui.Font;

public final class LabelSizing {
    private static final float WAYPOINT_TEXT_SCALE = 3.0F;
    private static final float SIZE_REDUCTION_PIXELS = 2.0F;

    private LabelSizing() {
    }

    public static float baseScale(Font font) {
        int lineHeight = Math.max(1, font.lineHeight);
        return WAYPOINT_TEXT_SCALE - SIZE_REDUCTION_PIXELS / lineHeight;
    }
}
