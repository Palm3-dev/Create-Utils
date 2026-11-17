package com.palm3.createutils.content.blocks.smarter_observer.behaviour_settings;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockProperties {

    public static final String DONT_DETECT = "internal_detect_disabled";
    public static final String CANT_DETECT = "internal_cannot_detect";
    public static final String NOT_SET = "internal_still_not_set";

    // Two reciprocally inverted maps, that contains all the properties (as string) to display by index (and vice versa).
    private final LinkedHashMap<Integer, String> propertiesByIndex = new LinkedHashMap<>();
    private final LinkedHashMap<String, Integer> propsIndexesByProp = new LinkedHashMap<>();

    // Two reciprocally inverted maps, that contains all the values of a property to display (and vice versa), grouped (and only accessible) via property as string.
    private final LinkedHashMap<String, LinkedHashMap<Integer, String>> valuesByIndex_ByProp = new LinkedHashMap<>();
    private final LinkedHashMap<String, LinkedHashMap<String, Integer>> valuesIndexesByValue_ByProp = new LinkedHashMap<>();

    // All the actual minecraft properties (? extends Property<?>) accessible via the relative string property.
    private final LinkedHashMap<String, Property<?>> actualPropertiesByProp = new LinkedHashMap<>();

    // If this representer (and thus the block) has properties.
    public final boolean hasProperties;


    public BlockProperties(Block block) {
        this(block.getStateDefinition().getProperties());
    }

    public BlockProperties(Collection<Property<?>> targetBlockProperties) {
        hasProperties = !targetBlockProperties.isEmpty();
        if (!hasProperties) {
            // Only fill with CANT_DETECT since there are no properties.
            propertiesByIndex.put(0, CANT_DETECT);
            propsIndexesByProp.put(CANT_DETECT, 0);
            LinkedHashMap<Integer, String> valuesByIndex = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> valuesIndexesByValue = new LinkedHashMap<>();
            valuesByIndex.put(0, CANT_DETECT);
            valuesIndexesByValue.put(CANT_DETECT, 0);
            valuesByIndex_ByProp.put(CANT_DETECT, valuesByIndex);  // Only one property, CANT_DETECT.
            valuesIndexesByValue_ByProp.put(CANT_DETECT, valuesIndexesByValue);  // Same as above.
        } else {
            // First fill properties maps with DONT_DETECT.
            propertiesByIndex.put(0, DONT_DETECT);
            propsIndexesByProp.put(DONT_DETECT, 0);

            int propsIndex = 1;  // Start from 1 since 0 is occupied by DONT_DETECT.
            for (Property<?> property : targetBlockProperties) {
                propertiesByIndex.put(propsIndex, property.getName());
                propsIndexesByProp.put(property.getName(), propsIndex);
                actualPropertiesByProp.put(property.getName(), property);

                LinkedHashMap<Integer, String> valuesByIndex = new LinkedHashMap<>();
                LinkedHashMap<String, Integer> valuesIndexesByValue = new LinkedHashMap<>();

                // Fill values maps with DONT_DETECT.
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

    /**
     * @param index The {@link String} property index as {@link Integer}.
     * @return The property at the given index, <b>if it exists.</b>
     * @throws IllegalArgumentException If the requested property at the given index doesn't exist.
     */
    public String getProp(Integer index) {
        if (propertiesByIndex.get(index) == null)
            throw new IllegalArgumentException("The requested property at given index [" + index + "] doesn't exist!");
        return propertiesByIndex.get(index);
    }

    /**
     * @param prop The {@link String} property you want to get the index of.
     * @return If the block has properties, the index of the given {@link String} property, <b>if it exists</b>. If the block doesn't have properties, returns 0,
     * the index of {@link #CANT_DETECT}.
     * @throws IllegalArgumentException If the requested index of the given property doesn't exist.
     */
    public Integer getPropIndex(String prop) {
        if (hasProperties) {
            if (propsIndexesByProp.get(prop) == null)
                throw new IllegalArgumentException("The requested index at the given property [" + prop + "] doesn't exist!");
            return propsIndexesByProp.get(prop);
        } else return 0; // Only one prop at index 0, CANT_DETECT.
    }

    /**
     * @param correspondingProp The {@link String} property you want to get the value of.
     * @param valueIndex The index of the value you want to get.
     * @return The {@link String} property <b>value</b> at the given index of the given property.
     * @throws IllegalArgumentException In these cases:
     * <ul>
     *     <li>The given property doesn't exist.</li>
     *     <li>The requested property value at the given index doesn't exist.</li>
     * </ul>
     */
    public String getPValue(String correspondingProp, Integer valueIndex) {
        if (valuesByIndex_ByProp.get(correspondingProp) == null)
            throw new IllegalArgumentException("The requested LinkedHashMap<Integer, String> at the given property [" + correspondingProp + "] doesn't exist!");
        if (valuesByIndex_ByProp.get(correspondingProp).get(valueIndex) == null)
            throw new IllegalArgumentException("The requested value at the given index [" + valueIndex + "] doesn't exist!");
        return valuesByIndex_ByProp.get(correspondingProp).get(valueIndex);
    }

    /**
     * @param correspondingProp The {@link String} property you want to get the value of.
     * @param value The value of the given property you want to get the index of.
     * @return The index of the requested <b>value</b> of the given property.
     * @throws IllegalArgumentException In these cases:
     * <ul>
     *     <li>The requested property doesn't exist.</li>
     *     <li>The requested index of the given value of the given property doesn't exist.</li>
     * </ul>
     */
    public Integer getPValueIndex(String correspondingProp, String value) {
        if (hasProperties) {
            if (valuesIndexesByValue_ByProp.get(correspondingProp) == null)
                throw new IllegalArgumentException("The requested LinkedHashMap<String, Integer> at the given property [" + correspondingProp + "] doesn't exist!");
            if (valuesIndexesByValue_ByProp.get(correspondingProp).get(value) == null)
                throw new IllegalArgumentException("The requested index at the given value [" + value + "] doesn't exist!");
            return valuesIndexesByValue_ByProp.get(correspondingProp).get(value);
        } else return 0;  // Only one value at index 0 for one property, CANT_DETECT.
    }

    /**
     * @param onlyGetBlockProps Behaviour:
     *  <ul>
     *       <li>When there are properties: specifies if you want to include ({@code false}) or exclude ({@code true}) the non-property values, such as {@link #DONT_DETECT}.</li>
     *       <li>When there are no properties: the method returns 1 (being {@link #CANT_DETECT}).</li>
     * </ul>
     * @return The number of properties of this instance.
     */
    public Integer getPropsNumber(boolean onlyGetBlockProps) {// If receives true, it ignores the DONT_DETECT property, otherwise also counted incrementing the total by one. This is done only if there are properties.
        if (hasProperties) {
            if (onlyGetBlockProps) return propertiesByIndex.size() - 1;
            return propertiesByIndex.size();
        } else return 1;  // There's only CANT_DETECT.
    }

    /**
     * @param correspondingProp The {@link String} property you want to get the values <b>number</b> of.
     * @param onlyGetBlockPValues Behaviour:
     * <ul>
     *                            <li>When there are properties: specifies if you want to include ({@code false}) or exclude ({@code true})
     *                                the non-value values, such as {@link #DONT_DETECT}.
     *                            </li>
     *                            <li>When there are no properties: the method returns 1 (being {@link #CANT_DETECT})</li>
     * </ul>
     * @return the number of values of the given property.
     * @throws IllegalArgumentException If the given {@link String} property doesn't exist.
     */
    public Integer getPValuesNumber(String correspondingProp, boolean onlyGetBlockPValues) {
        if (hasProperties) {
            if (valuesByIndex_ByProp.get(correspondingProp) == null)
                throw new IllegalArgumentException("The requested LinkedHashMap<Integer, String> at the give property [" + correspondingProp + "] doesn't exist!");
            if (onlyGetBlockPValues) return valuesByIndex_ByProp.get(correspondingProp).size() - 1;
            return valuesByIndex_ByProp.get(correspondingProp).size();
        } else return 1;  // There's only one property with one value, CANT_DETECT.
    }

    /**
     * @param prop The {@link String} property you want to get as {@link Property}.
     * @return the given string property as minecraft property.
     * @throws IllegalArgumentException If the requested property doesn't exist.
     */
    public Property<?> getProp(String prop) {
        if (actualPropertiesByProp.get(prop) == null)
            throw new IllegalArgumentException("The given property [" + prop + "] doesn't exist in LinkedHashMap<String, Property<?>> (properties by property as string)!");
        return actualPropertiesByProp.get(prop);
    }

    /**
     * @param staticPropOrValue The static value in this class to translate, e.g. {@link #DONT_DETECT}.
     * @return The correct component for that static value.
     */
    public static Component getComponentForStatic(String staticPropOrValue) {  //todo Maybe make values an enum, would be better
        String baseKey = "create_utils.properties_representer.";
        return switch (staticPropOrValue) {
            case DONT_DETECT -> Component.translatable(baseKey + DONT_DETECT);
            case CANT_DETECT -> Component.translatable(baseKey + CANT_DETECT);
            case NOT_SET -> Component.translatable(baseKey + NOT_SET);
            default -> Component.literal(staticPropOrValue);
        };
    }

    // Used for controls, not for setting (usually).
    public boolean hasProp(String prop) {
        return propertiesByIndex.containsValue(prop);
    }

    public boolean hasPValue(String correspondingProp, String value) {
        if (valuesByIndex_ByProp.get(correspondingProp) == null)
            throw new IllegalArgumentException("The requested LinkedHashMap<Integer, String> at the give property [" + correspondingProp + "] doesn't exist!");
        return valuesByIndex_ByProp.get(correspondingProp).containsValue(value);
    }
}
