package com.palm3.createutils.register;

import static com.palm3.createutils.CUMain.CU_REGISTRATE;

public class CULangs {

    private static void add(String key, String value) {
        CU_REGISTRATE.addRawLang(key, value);
    }

    private enum Tooltips {
        SUMMARY(".tooltip.summary"),
        BEHAVIOUR(".tooltip.behaviour"),
        CONDITION(".tooltip.condition");

        public final String key;

        Tooltips(String key) {
            this.key = key;
        }
    }

    public static void addLangs() {
        // TABS - Main tab
        add("itemGroup.create_utils.main_creative_tab", "Create: Utils");

        // BLOCKS - All adjustable speed controllers
        add("block.create_utils.adjustable_speed_controllers.output_rotation_direction", "Output Rotation Direction");

        // BLOCKS - Compact Adjustable Speed Controller
        add("block.create_utils.adjustable_speed_controller_compact.tooltip.summary", "A compact _Speed Controller_ with settable direction.");
        add("block.create_utils.adjustable_speed_controller_compact.tooltip.condition1", "When analog powered");
        add("block.create_utils.adjustable_speed_controller_compact.tooltip.behaviour1", "Regulates speed from 0 to _input speed_ minus one _speed step._");

        // ITEMS - Screwdriver
        add("item.create_utils.screwdriver.tooltip.summary", "Multi-purpose tool for in-depth _Create_ configurations.");
        add("item.create_utils.screwdriver.tooltip.condition1", "When used on Blocks");
        add("item.create_utils.screwdriver.tooltip.behaviour1", "Selects the clicked block.");
        add("item.create_utils.screwdriver.tooltip.condition2", "When used on Filtered Blocks");
        add("item.create_utils.screwdriver.tooltip.behaviour2", "Sets the _block filter_ to the previously selected one.");
        add("item.create_utils.screwdriver.tooltip.condition3", "When scrolling on generators");
        add("item.create_utils.screwdriver.tooltip.behaviour3", "_Increments_ or _decrements_ the eventual _generator speed._");
        add("item.create_utils.screwdriver.tooltip.condition4", "When right-clicked on generators");
        add("item.create_utils.screwdriver.tooltip.behaviour4", "Sets the modified speed value.");
        add("item.create_utils.screwdriver.tooltip.condition5", "When left-clicked on generators");
        add("item.create_utils.screwdriver.tooltip.behaviour5", "Reverses the rotation direction.");



        /*// Block properties representer
        add("create_utils.properties_representer.internal_detect_disabled", "Ignore");
        add("create_utils.properties_representer.internal_detect_disabled.internal_cannot_detect", "Target has no props!");
        add("create_utils.properties_representer.internal_still_not_set", "No target selected!");*/

        /*// Smarter Observer
        add("smarter_observer_be.target_block", "Target Block");
        add("gui.smarter_observer.title", "Observer Config");
        add("gui.smarter_observer.target_property_scroll", "Target Property");
        add("gui.smarter_observer.target_value_scroll", "Target Property Value");
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
        add("gui.smarter_observer.locked_for_no_props_tooltip", "Locked for no properties!");*/
    }
}
