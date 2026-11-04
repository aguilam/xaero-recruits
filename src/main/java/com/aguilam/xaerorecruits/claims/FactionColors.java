package com.aguilam.xaerorecruits.claims;

import com.talhanation.recruits.world.RecruitsFaction;

public final class FactionColors {
    private static final int[] RECRUITS_PALETTE = {
            0xFFFFFF, 0x000000, 0xD3D3D3, 0x808080, 0x404040,
            0x337B9A, 0x335C9A, 0x15253E, 0x269C08, 0x008000,
            0x0E3903, 0xB91515, 0x941010, 0x4A0808, 0x8C4C18,
            0x5E3B1D, 0x3F2713, 0x009696, 0x174444, 0x0B2222,
            0xEDCA12, 0xE56A11, 0xFF00FF, 0x570265, 0xDD9A19
    };

    private static final int[] FALLBACK_PALETTE = {
            0xFF4444, 0x44FF44, 0x4488FF, 0xFFFF44
    };

    private FactionColors() {
    }

    public static int colorFor(RecruitsFaction faction, String factionId) {
        if (faction != null) {
            int index = faction.getUnitColor() & 0xFF;
            if (index < RECRUITS_PALETTE.length) {
                return RECRUITS_PALETTE[index];
            }
        }

        int hash = factionId == null ? 0 : factionId.hashCode();
        return FALLBACK_PALETTE[(hash & 0x7FFFFFFF) % FALLBACK_PALETTE.length];
    }

    public static int toXaeroColor(int rgb, int alpha) {
        int red = rgb >> 16 & 0xFF;
        int green = rgb >> 8 & 0xFF;
        int blue = rgb & 0xFF;
        return blue << 24 | green << 16 | red << 8 | (alpha & 0xFF);
    }
}
