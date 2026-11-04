package com.aguilam.xaerorecruits;

import com.aguilam.xaerorecruits.claims.ClaimIndex;
import com.aguilam.xaerorecruits.claims.XaeroRefresher;
import com.aguilam.xaerorecruits.config.XRConfig;
import com.aguilam.xaerorecruits.config.XRConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class ClientSetup {
    private ClientSetup() {
    }

    public static void init() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(ClientSetup::onConfigEvent);
        MinecraftForge.EVENT_BUS.addListener(ClientSetup::onClientTick);

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> new XRConfigScreen(parent)));
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClaimIndex.get().tick();
        }
    }

    private static void onConfigEvent(ModConfigEvent event) {
        if (event.getConfig().getSpec() == XRConfig.SPEC) {
            ClaimIndex.get().markDirty();
            XaeroRefresher.refreshAll();
        }
    }
}
