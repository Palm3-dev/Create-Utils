package com.palm3.createutils.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CUCommonConfig {
    public static final ModConfigSpec COMMON_CONFIG;

    public static final ModConfigSpec.BooleanValue LOG_ALL;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        //------------ Logging ------------
        builder.push("Logging");
        LOG_ALL = builder
                .comment("If enabled, all log infos will be printed.")
                .define("Extensive Logging", false);

        builder.pop();

        COMMON_CONFIG = builder.build();
    }
}
