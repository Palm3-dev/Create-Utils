package com.palm3.createutils.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CUCommonConfig {
    public static final ModConfigSpec COMMON_CONFIG;

    // Logging
    public static final ModConfigSpec.BooleanValue LOG_ALL;
    public static final ModConfigSpec.BooleanValue LOG_SMARTER_OBSERVER;

    // Smarter Observer
    public static final ModConfigSpec.IntValue MAX_ON_FOR_TICKS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        //------------ Logging ------------
        builder.push("Logging");
        LOG_ALL = builder
                .comment("If enabled, all log infos (for all blocks) will be printed.")
                .define("Log all", false);

        LOG_SMARTER_OBSERVER = builder
                .comment("If enabled, smarter observer related logs will be printed.")
                .define("Smarter Observer", false);

        builder.pop();

        //------------ Smarter observer ----------
        builder.push("Smarter Observer");
        MAX_ON_FOR_TICKS = builder
                .comment("The max number of game ticks the observer should remain powered after detecting a block. 1s = 20tick, 1min = 1200tick, 1h = 72000tick")
                .defineInRange("Max ticks on", 18000, 1, 72000);

        builder.pop();

        COMMON_CONFIG = builder.build();
    }

    // Logging utils
    public static boolean logAll() {
        return LOG_ALL.getAsBoolean();
    }
    public static boolean logSmarterObserver() {
        return LOG_ALL.getAsBoolean() || LOG_SMARTER_OBSERVER.getAsBoolean();
    }
}
