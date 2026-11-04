package com.aguilam.xaerorecruits.claims;

import com.aguilam.xaerorecruits.XaeroRecruitsMod;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.highlight.DimensionHighlighterHandler;
import xaero.common.minimap.write.MinimapWriter;
import xaero.map.MapProcessor;
import xaero.map.WorldMapSession;
import xaero.map.world.MapDimension;

public final class XaeroRefresher {
    private XaeroRefresher() {
    }

    public static void refreshAll() {
        refreshMinimap();
        refreshWorldMap();
    }

    private static void refreshMinimap() {
        try {
            XaeroMinimapSession session = XaeroMinimapSession.getCurrentSession();
            if (session == null || session.getMinimapProcessor() == null) {
                return;
            }
            MinimapWriter writer = session.getMinimapProcessor().getMinimapWriter();
            if (writer == null) {
                return;
            }
            writer.setClearBlockColours(true);
            session.getMinimapProcessor().setToResetImage(true);
            DimensionHighlighterHandler handler = writer.getDimensionHighlightHandler();
            if (handler != null) {
                handler.requestRefresh();
            }
        } catch (Throwable throwable) {
            XaeroRecruitsMod.LOGGER.debug("Could not refresh the minimap highlights", throwable);
        }
    }

    private static void refreshWorldMap() {
        try {
            WorldMapSession session = WorldMapSession.getCurrentSession();
            if (session == null || session.getMapProcessor() == null) {
                return;
            }
            MapProcessor processor = session.getMapProcessor();
            if (processor.getMapWorld() == null) {
                return;
            }
            for (MapDimension dimension : processor.getMapWorld().getDimensionsList()) {
                if (dimension != null && dimension.getHighlightHandler() != null) {
                    dimension.getHighlightHandler().clearCachedHashes();
                }
            }
        } catch (Throwable throwable) {
            XaeroRecruitsMod.LOGGER.debug("Could not refresh the world map highlights", throwable);
        }
    }
}
