package com.palm3.createutils.register;

import static com.palm3.createutils.CUMain.CU_REGISTRATE;

public class CULangs {
    private static void add(String key, String value) {
        CU_REGISTRATE.addRawLang(key, value);
    }

    public static void addLangs() {
        add("itemGroup.createutils.main_creative_tab", "Create: Utils");

        // Smarter Observer
        add("smarter_observer_be.target_block", "Target Block");
        add("gui.smarter_observer.title", "Observer Config");
        add("gui.smarter_observer.target_property_scroll", "Target Property");
        add("gui.smarter_observer.target_value_scroll", "Target Property Value");
        add("gui.smarter_observer.internal_detect_disabled", "Ignore");
        add("gui.smarter_observer.internal_cannot_detect", "Target has no props!");
        add("gui.smarter_observer.internal_still_not_set", "No target selected!");
        add("gui.smarter_observer.target_property_hint", "The property of the selected block you want to detect. You can disable it.");
        add("gui.smarter_observer.target_value_hint", "The property value of the selected block you want to detect. You can disable it.");
        add("gui.smarter_observer.on_for_ticks_scroll", "On For Ticks");
        add("gui.smarter_observer.on_for_ticks_hint", "Number of game ticks the observer should remain powered after detecting. Remember: 1 redstone tick = 2 tick.");
        add("gui.smarter_observer.detect_placed_tooltip", "Detect only when the target block is placed.");
        add("gui.smarter_observer.detect_removed_tooltip", "Detect only when the target block is removed.");
        add("gui.smarter_observer.detect_both_tooltip", "Detect when the target block is placed or removed.");
        add("gui.smarter_observer.target_block_hint", "Currently selected target");
        add("gui.smarter_observer.no_selected_target_block_hint", "No target selected.");
        add("gui.smarter_observer.detect_props_only_if_block_changes_tooltip", "Detect the block removal only if the property value matches the targets.");
        add("gui.smarter_observer.detect_block_if_changes_tooltip", "Detect the block removal even if it doesn't have matching property value.");
        add("gui.smarter_observer.locked_for_no_props_tooltip", "Locked for no properties!");
    }
}
