package com.palm3.createutils.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CUCommonConfig {
    public static final ModConfigSpec COMMON_CONFIG;

    // Logging
    public static final ModConfigSpec.BooleanValue LOG_ALL;

    // Smarter Observer
    public static final ModConfigSpec.IntValue MAX_ON_FOR_TICKS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        //------------ Logging ------------
        builder.push("Logging");
        LOG_ALL = builder
                .comment("If enabled, all log infos will be printed.")
                .define("Extensive Logging", false);

        builder.pop();

        //------------ Smarter observer ----------

        builder.push("Smarter Observer");
        MAX_ON_FOR_TICKS = builder
                .comment("The max number of game ticks the observer should remain powered after detecting a block. 1s = 20tick, 1min = 1200tick, 1h = 72000tick")
                .defineInRange("Max ticks on", 18000, 0, Integer.MAX_VALUE);

        builder.pop();

        COMMON_CONFIG = builder.build();
    }
}
