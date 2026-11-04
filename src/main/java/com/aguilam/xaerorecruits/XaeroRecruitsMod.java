package com.aguilam.xaerorecruits;

import com.aguilam.xaerorecruits.config.XRConfig;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(XaeroRecruitsMod.MOD_ID)
public final class XaeroRecruitsMod {
    public static final String MOD_ID = "xaerorecruits";
    public static final Logger LOGGER = LogUtils.getLogger();

    public XaeroRecruitsMod() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, XRConfig.SPEC);
            ClientSetup.init();
        }
    }
}
