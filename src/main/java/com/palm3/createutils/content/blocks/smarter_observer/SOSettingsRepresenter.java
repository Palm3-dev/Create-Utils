package com.palm3.createutils.content.blocks.smarter_observer;

import com.palm3.createutils.CUGuiTextures;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@MethodsReturnNonnullByDefault
public class SOSettingsRepresenter {

    public static final String NOT_NULL_STILL_NULL = "internal_still_not_set";

    protected static class SelectionRepresenter {
        public static final String DONT_DETECT = "internal_detect_disabled";
        public static final String CANT_DETECT = "internal_cannot_detect";

        private final LinkedHashMap<Integer, String> propertiesByIndex = new LinkedHashMap<>();
        private final LinkedHashMap<String, Integer> propsIndexesByProp = new LinkedHashMap<>();

        private final LinkedHashMap<String, LinkedHashMap<Integer, String>> valuesByIndex_ByProp = new LinkedHashMap<>();
        private final LinkedHashMap<String, LinkedHashMap<String, Integer>> valuesIndexesByValue_ByProp = new LinkedHashMap<>();

        private final boolean hasProperties;


        public SelectionRepresenter(Collection<Property<?>> targetBlockProperties) {
            hasProperties = !targetBlockProperties.isEmpty();
            if (!hasProperties) {
                // Only fill with CANT_DETECT since there are no properties.
                propertiesByIndex.put(0, CANT_DETECT);
                propsIndexesByProp.put(CANT_DETECT, 0);
                LinkedHashMap<Integer, String> valuesByIndex = new LinkedHashMap<>();
                LinkedHashMap<String, Integer> valuesIndexesByValue = new LinkedHashMap<>();
                valuesByIndex.put(0, CANT_DETECT);
                valuesIndexesByValue.put(CANT_DETECT, 0);
                valuesByIndex_ByProp.put(CANT_DETECT, valuesByIndex);  // Only one property, CANT_DETECT
                valuesIndexesByValue_ByProp.put(CANT_DETECT, valuesIndexesByValue);  //
            } else {
                // First fill properties maps with DONT_DETECT.
                propertiesByIndex.put(0, DONT_DETECT);
                propsIndexesByProp.put(DONT_DETECT, 0);

                int propsIndex = 1;  // Start from 1 since 0 is occupied by DONT_DETECT.
                for (Property<?> property : targetBlockProperties) {
                    propertiesByIndex.put(propsIndex, property.getName());
                    propsIndexesByProp.put(property.getName(), propsIndex);

                    LinkedHashMap<Integer, String> valuesByIndex = new LinkedHashMap<>();
                    LinkedHashMap<String, Integer> valuesIndexesByValue = new LinkedHashMap<>();
                    valuesByIndex.put(0, DONT_DETECT);
                    valuesIndexesByValue.put(DONT_DETECT, 0);

                    AtomicInteger valuesIndex = new AtomicInteger(1);  // Start from 1 since 0 is occupied by DONT_DETECT.
                    property.getPossibleValues().forEach(value -> {
                        valuesByIndex.put(valuesIndex.get(), value.toString());
                        valuesIndexesByValue.put(value.toString(), valuesIndex.get());
                        valuesIndex.incrementAndGet();
                    });

                    // Adding for the DONT_DETECT property the map of values (not automated in for loop).
                    valuesByIndex_ByProp.put(DONT_DETECT, valuesByIndex);
                    valuesIndexesByValue_ByProp.put(DONT_DETECT, valuesIndexesByValue);

                    // Adding for all other properties.
                    valuesByIndex_ByProp.put(property.getName(), valuesByIndex);
                    valuesIndexesByValue_ByProp.put(property.getName(), valuesIndexesByValue);

                    propsIndex++;
                }
            }
        }

        public boolean hasProps() {
            return hasProperties;
        }

        public String getProp(Integer index) {
            if (propertiesByIndex.get(index) == null)
                throw new IllegalArgumentException("The requested property at given index [" + index + "] doesn't exist!");
            return propertiesByIndex.get(index);
        }

        public Integer getPropIndex(String prop) {
            if (hasProperties) {
                if (propsIndexesByProp.get(prop) == null)
                    throw new IllegalArgumentException("The requested index at the given property [" + prop + "] doesn't exist!");
                return propsIndexesByProp.get(prop);
            } else return 0; // Only one prop at index 0, CANT_DETECT.
        }

        public String getPValue(String correspondingProp, Integer valueIndex) {
            if (valuesByIndex_ByProp.get(correspondingProp) == null)
                throw new IllegalArgumentException("The requested LinkedHashMap<Integer, String> at the given property [" + correspondingProp + "] doesn't exist!");
            if (valuesByIndex_ByProp.get(correspondingProp).get(valueIndex) == null)
                throw new IllegalArgumentException("The requested value at the given index [" + valueIndex + "] doesn't exist!");
            return valuesByIndex_ByProp.get(correspondingProp).get(valueIndex);
        }

        public Integer getPValueIndex(String correspondingProp, String value) {
            if (hasProperties) {
                if (valuesIndexesByValue_ByProp.get(correspondingProp) == null)
                    throw new IllegalArgumentException("The requested LinkedHashMap<String, Integer> at the given property [" + correspondingProp + "] doesn't exist!");
                if (valuesIndexesByValue_ByProp.get(correspondingProp).get(value) == null)
                    throw new IllegalArgumentException("The requested index at the given value [" + value + "] doesn't exist!");
                return valuesIndexesByValue_ByProp.get(correspondingProp).get(value);
            } else return 0;  // Only one value at index 0 for one property, CANT_DETECT.
        }

        // If receives true, it ignores the DONT_DETECT property, otherwise also counted incrementing the total by one. This is done only if there are properties.
        public Integer getPropsNumber(boolean onlyGetBlockProps) {
            if (hasProperties) {
                if (onlyGetBlockProps) return propertiesByIndex.size() - 1;
                return propertiesByIndex.size();
            } else return 1;  // There's only CANT_DETECT.
        }

        // If receives true, it ignores the DONT_DETECT value, otherwise also counted incrementing the total by one. This is done only if there are properties.
        public Integer getPValuesNumber(String correspondingProp, boolean onlyGetBlockPValues) {
            if (hasProperties) {
                if (valuesByIndex_ByProp.get(correspondingProp) == null)
                    throw new IllegalArgumentException("The requested LinkedHashMap<Integer, String> at the give property [" + correspondingProp + "] doesn't exist!");
                if (onlyGetBlockPValues) return valuesByIndex_ByProp.get(correspondingProp).size() - 1;
                return valuesByIndex_ByProp.get(correspondingProp).size();
            } else return 1;  // There's only one property with one value, CANT_DETECT.
        }

        public static Component checkAndTranslateStatics(String stringToCheck) {
            if (stringToCheck.equals(DONT_DETECT)) return Component.translatable("gui.smarter_observer.internal_detect_disabled");
            if (stringToCheck.equals(CANT_DETECT)) return Component.translatable("gui.smarter_observer.internal_cannot_detect");
            return Component.literal(stringToCheck);
        }
    }

    protected static class DetectBehaviourRepresenter {
        public static final String DETECT_PLACED = "detect_placed";
        public static final String DETECT_REMOVED = "detect_removed";
        public static final String DETECT_BOTH = "detect_both";

        public static String getNext(String currentDetectBehaviour) {
            switch (currentDetectBehaviour) {
                case DETECT_PLACED -> { return DETECT_REMOVED; }
                case DETECT_REMOVED -> { return DETECT_BOTH; }
                case DETECT_BOTH -> { return DETECT_PLACED; }
                default -> throw new IllegalArgumentException("The given detect behaviour [" + currentDetectBehaviour + "] doesn't exist!");
            }
        }

        public static CUGuiTextures getIcon(String currentDetectBehaviour) {
            switch (currentDetectBehaviour) {
                case DETECT_PLACED -> { return CUGuiTextures.BLOCK_PLACED_I; }
                case DETECT_REMOVED -> { return CUGuiTextures.BLOCK_REMOVED_I; }
                case DETECT_BOTH -> { return CUGuiTextures.BLOCK_PLACED_REMOVED_I; }
                default -> throw new IllegalArgumentException("The given detect behaviour [" + currentDetectBehaviour + "] doesn't exist!");
            }
        }

        public static Component getTooltip(String currentDetectBehaviour) {
            switch (currentDetectBehaviour) {
                case DETECT_PLACED -> { return Component.translatable("gui.smarter_observer.detect_placed_tooltip"); }
                case DETECT_REMOVED -> { return Component.translatable("gui.smarter_observer.detect_removed_tooltip"); }
                case DETECT_BOTH -> { return Component.translatable("gui.smarter_observer.detect_both_tooltip"); }
                default -> throw new IllegalArgumentException("The given detect behaviour [" + currentDetectBehaviour + "] doesn't exist!");
            }
        }
    }
}
