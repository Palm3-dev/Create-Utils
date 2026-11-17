package com.palm3.createutils.content.blocks.smarter_observer.behaviour_settings;

import com.palm3.createutils.CUGuiTextures;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class BlockDetectMode {
    private static final List<Values> VALUES = new ArrayList<>(EnumSet.allOf(Values.class));
    private final String tooltipsBaseKey;
    private int currentValue;

    public BlockDetectMode(String tooltipsBaseKey, Values initialValue) {
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
        DETECT_PLACED("detect_placed", CUGuiTextures.BLOCK_PLACED_I),
        DETECT_REMOVED("detect_removed", CUGuiTextures.BLOCK_REMOVED_I),
        DETECT_BOTH("detect_both", CUGuiTextures.BLOCK_PLACED_REMOVED_I);

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
