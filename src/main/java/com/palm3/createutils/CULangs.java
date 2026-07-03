package com.palm3.createutils;

import static com.palm3.createutils.CUMain.CU_REGISTRATE;

public class CULangs {
    private static void add(String key, String value) {
        CU_REGISTRATE.addRawLang(key, value);
    }

    public static void addLangs() {
        add("itemGroup.createutils.main_creative_tab", "Create: Utils");

        add("gui.smarter_observer.title", "Observer Config");
        add("gui.smarter_observer.target_property_scroll", "Target Property");
        add("gui.smarter_observer.target_value_scroll", "Target Property Value");
    }
}
