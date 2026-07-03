package com.palm3.createutils.content.blocks.smarter_observer;

import com.palm3.createutils.CUMain;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PropertiesRepresenter {
    public static final String DONT_DETECT = "internal_detect_disabled";
    public static final String CANT_DETECT = "internal_cannot_detect";

    private final LinkedHashMap<Integer, String> propertiesAsStringNumbered = new LinkedHashMap<>();
    private final LinkedHashMap<String, Integer> propertiesNumbersByString = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, Property<?>> propertiesAsPropertyNumbered = new LinkedHashMap<>();
    private final LinkedHashMap<Property<?>, Integer> propertiesNumbersByProperty = new LinkedHashMap<>();
    private final Integer totalPropertiesNumber;

    //private final LinkedHashMap<Property<?>, LinkedHashMap<Integer, List<String>>> valuesByProperty = new LinkedHashMap<>();
    private final LinkedHashMap<Property<?>, LinkedHashMap<Integer, String>> valuesNumberedByProperty = new LinkedHashMap<>();
    private final LinkedHashMap<Property<?>, LinkedHashMap<String, Integer>> valuesNumbersByStringByProperty = new LinkedHashMap<>();

    private final boolean hasProps;

    private final boolean doLog;
    private void l(String msg) {
        if (doLog) {
            if (msg.equals("empty")) {
                CUMain.LOGGER.info("|");
                CUMain.LOGGER.info("|");
            } else CUMain.LOGGER.info(msg);
        }
    }

    public PropertiesRepresenter(Collection<Property<?>> targetBlockProperties, boolean logEvenMyAAAAH) {
        doLog = logEvenMyAAAAH;
        Integer mapsIndex = 0;
        l("======== PropertiesRepresenter.class ========");
        l("Now filling all LinkedHashMaps...");
        for (Property<?> property : targetBlockProperties) {
            l("Iteration " + mapsIndex + " - Properties maps:");
            propertiesAsStringNumbered.put(mapsIndex, property.getName());
            l("   propertiesAsStringNumbered | <Integer, String> --> " + mapsIndex + ",  " + property.getName());
            propertiesNumbersByString.put(property.getName(), mapsIndex);
            l("   propertiesNumbersByString | <String, Integer> --> " + property.getName() + ",  " + mapsIndex);
            propertiesAsPropertyNumbered.put(mapsIndex, property);
            l("   propertiesAsPropertyNumbered | <Integer, Property<?>> --> " + mapsIndex + ",  " + property);
            propertiesNumbersByProperty.put(property, mapsIndex);
            l("   propertiesNumbersByProperty | <Property<?>, Integer> --> " + property + ",  " + mapsIndex);

            //valuesListsByProperty.put(property, new LinkedHashMap<>());

            l("empty");
            l("Iteration " + mapsIndex + " - Values maps:");
            LinkedHashMap<Integer, String> propertyValuesByNumber = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> propertyNumbersByValue = new LinkedHashMap<>();
            AtomicInteger valuesIndex = new AtomicInteger(0);
            property.getPossibleValues().forEach(value -> {
                propertyValuesByNumber.put(valuesIndex.get(), value.toString());
                l("   propertyValuesByNumber | <Integer, String> --> " + valuesIndex + ",  " + property.getName());
                propertyNumbersByValue.put(value.toString(), valuesIndex.get());
                l("   propertyNumbersByValue | <String, Integer> --> " + property.getName() + ",  " + valuesIndex);
                valuesIndex.set(valuesIndex.get() + 1);
            });
            valuesNumberedByProperty.put(property, propertyValuesByNumber);
            l("   valuesNumberedByProperty | <Property<?>, LHMap<Integer, String>> --> " + property + ",  propertyValuesByNumber");
            valuesNumbersByStringByProperty.put(property, propertyNumbersByValue);
            l("   valuesNumbersByStringByProperty | <Property<?>, LHMap<String, Integer>> --> " + property + ",  propertyNumbersByValue");

            mapsIndex++;
            l("empty");
        }

        l("empty");
        l("Maps filling ended...");
        l("Other vars:");
        totalPropertiesNumber = mapsIndex + 1;
        l("   totalPropertiesNumber | Integer --> " + totalPropertiesNumber);
        hasProps = !targetBlockProperties.isEmpty();
        l("   hasProps | boolean --> " + hasProps);
    }

    // Properties
    public boolean hasProps() {
        return hasProps;
    }

    public Integer getPropIndex(Property<?> prop) {
        return propertiesNumbersByProperty.get(prop);
    }

    /// Returns 0 if the property doesn't exist.
    public Integer getPropIndex(String prop) {
        if (prop.equals(DONT_DETECT) || prop.equals(CANT_DETECT)) return 0;
        return propertiesNumbersByString.get(prop);
    }

    public Property<?> getProp(Integer propNumber) {
        return propertiesAsPropertyNumbered.get(propNumber);
    }

    public Property<?> getProp(String prop) {
        return propertiesAsPropertyNumbered.get(propertiesNumbersByString.get(prop));
    }

    public String getPropString(Integer propNumber) {
        return propertiesAsStringNumbered.get(propNumber);
    }

    public Integer getPropsNumber() {
        return totalPropertiesNumber;
    }

    // Properties values
    public Integer getValuesNumber(Property<?> property) {
        return valuesNumberedByProperty.get(property).size();
    }

    public Integer getValuesNumber(String property) {
        return valuesNumberedByProperty.get(getProp(property)).size();
    }

    public String getValueString(Property<?> selectedProperty, Integer valueIndexInMap) {
        return valuesNumberedByProperty.get(selectedProperty).get(valueIndexInMap);
    }

    public Integer getValueIndex(Property<?> prop, String value) {
        return valuesNumbersByStringByProperty.get(prop).get(value);
    }
}
