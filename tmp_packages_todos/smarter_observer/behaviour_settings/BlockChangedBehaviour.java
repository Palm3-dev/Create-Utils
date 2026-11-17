package com.palm3.createutils.content.blocks.smarter_observer.behaviour_settings;

import com.palm3.createutils.CUGuiTextures;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class BlockChangedBehaviour {
    /*
        Dev note: remember to always sync these configs with properties config.
                  If properties are set to don't detect this class values should also be set to
                  Values.DETECT_ONLY_BLOCK
     */

    private static final List<Values> VALUES = new ArrayList<>(EnumSet.allOf(Values.class));
    private final String tooltipsBaseKey;
    private int currentValue;

    public BlockChangedBehaviour(String tooltipsBaseKey, Values initialValue) {
        this.tooltipsBaseKey = tooltipsBaseKey;
        currentValue = initialValue.ordinal();
    }

    public Values getNextMode() {
        currentValue++;
        if (currentValue >= VALUES.size()) currentValue = 0;
        return VALUES.get(currentValue);
    }

    public String getTooltipsBaseKey() {
        return tooltipsBaseKey;
    }

    public enum Values {
        DETECT_ONLY_PROPS("detect_only_props", CUGuiTextures.GRASS_SNOW_I),
        DETECT_PROPS_AND_BLOCK("detect_props_block", CUGuiTextures.GRASS_PLANKS_SNOW_I),
        DETECT_ONLY_BLOCK("detect_only_block", CUGuiTextures.GRASS_PLANKS_I);

        private final String value;
        private final CUGuiTextures icon;

        Values(String value, CUGuiTextures icon) {
            this.value = value;
            this.icon = icon;
        }

        // Returns value as string
        public String getValue() {
            return value;
        }

        public CUGuiTextures getIcon() {
            return icon;
        }

        public Component getTooltipKey(String baseKey) {
            return Component.translatable(baseKey + "." + value + "_tooltip");
        }
    }
}
