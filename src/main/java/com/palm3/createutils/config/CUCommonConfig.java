package com.palm3.createutils.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CUCommonConfig {
    public static final ModConfigSpec COMMON_CONFIG;

    // Logging
    public static final ModConfigSpec.BooleanValue LOG_ALL;
    public static final ModConfigSpec.BooleanValue LOG_SMARTER_OBSERVER;

    // Smarter Observer
    public static final ModConfigSpec.IntValue MAX_ON_FOR_TICKS;



    //public static final ModConfigSpec.BooleanValue ENABLE_SPEED_OVERRIDES;

    // Scroll values settings
    //public static final ModConfigSpec.BooleanValue ENABLE_OLD_LIKE_SPEED_SETTINGS;
    //public static final ModConfigSpec.EnumValue<DisableScrollValuesMode> DISABLE_SCROLL_VALUES_MODE;

    public enum DisableScrollValuesMode {
        ALWAYS,
        WHEN_SCREWDRIVER,
        NEVER;

        DisableScrollValuesMode() {}
    }

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

        //------------ Screwdriver ----------
        /*builder.push("Screwdriver");
        ENABLE_SPEED_OVERRIDES = builder
                .comment("Defines if create speed settings behaviours should be replaced with screwdriver (e.g. no more scroll value).")
                .define("Enable configs override", true);


        builder.pop();*/

        //------------ Scroll values settings ----------
        /*builder.push("Generators modifications");
        ENABLE_OLD_LIKE_SPEED_SETTINGS = builder
                .comment("Enables the old Create speed settings method achieved via mouse scroll on generator blocks. This will be applied to all the blocks implementing Create's KineticScrollValue. Values needs to be modified with Screwdriver item.")
                .define("Old-Like Speed Settings", true);

        DISABLE_SCROLL_VALUES_MODE = builder
                .comment("Defines if all the Create native scroll values (KineticScrollValue) on the blocks implementing it should be disabled. Dependant on 'Old-Like Speed Settings' that must be enabled, otherwise this wont do anything!")
                .defineEnum("Disable scroll values mode", DisableScrollValuesMode.WHEN_SCREWDRIVER);
        builder.pop();*/

        COMMON_CONFIG = builder.build();
    }
}
