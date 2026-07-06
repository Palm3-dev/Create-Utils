package com.palm3.createutils.content.blocks.smarter_observer;

import com.palm3.createutils.CUGuiTextures;
import com.palm3.createutils.CUMain;
import com.palm3.createutils.config.CUCommonConfig;
import com.palm3.createutils.content.blocks.smarter_observer.SOSettingsRepresenter.*;
import com.palm3.createutils.register.CUBlocks;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SmarterObserverScreen extends AbstractSimiScreen {

    // Current values (when screen opened).
    private final String currentTargetProperty;
    private final String currentTargetValue;
    private final String currentDetectBehaviour;
    private final Integer currentOnForTicks;

    // Final values.
    private String selectedTargetProperty;
    private String selectedTargetValue;
    private String selectedDetectBehaviour;
    private Integer selectedOnForTicks;

    // To get all the properties/values.
    private final SelectionRepresenter sr;

    // ScrollInput
    private ScrollInput targetValueSetter;  // ScrollInput to choose the property value to detect.
    private ScrollInput targetPropertySetter;  // ScrollInput to choose a property to detect.
    private ScrollInput onForTicksSetter;

    // Textures - buttons - others
    private IconButton detectBehaviourSetter;
    private IconButton confirmButton;
    private CUGuiTextures background;
    private ItemStack smarterObserver;
    private ItemStack targetBlockAsItem;

    private final SmarterObserverBlockEntity sobe;  // The BE to save the values in.

    public SmarterObserverScreen(SmarterObserverBlockEntity sobe) {
        super(Component.translatable("gui.smarter_observer.title"));
        background = CUGuiTextures.SMARTER_OBSERVER_BACKGROUND;
        this.sobe = sobe;

        smarterObserver = new ItemStack(CUBlocks.SMARTER_OBSERVER.get());
        targetBlockAsItem = new ItemStack(sobe.targetBlock);

        // Save current BE settings, uses those for the logic. Are set to CANT/DONT_DETECT when you set the block filter.
        currentTargetProperty = sobe.targetProperty;
        currentTargetValue = sobe.targetValue;
        currentDetectBehaviour = sobe.detectBehaviour;
        currentOnForTicks = sobe.onForTicks;

        // Safe, if nothing touched they need to be the same. Also needed for renderWindow().
        selectedTargetProperty = currentTargetProperty;
        selectedTargetValue = currentTargetValue;
        selectedDetectBehaviour = sobe.detectBehaviour;
        selectedOnForTicks = currentOnForTicks;

        sr = new SelectionRepresenter(sobe.getTargetBlockProps());
    }

    @Override
    protected void init() {
        this.windowWidth = CUGuiTextures.SMARTER_OBSERVER_BACKGROUND.getWidth();
        this.windowHeight = CUGuiTextures.SMARTER_OBSERVER_BACKGROUND.getHeight();
        setWindowSize(windowWidth, windowHeight);
        setWindowOffset(0, 0);
        super.init();

        int x = guiLeft; // Horizontal
        int y = guiTop;  // Vertical


        targetPropertySetter = new ScrollInput(x + 17, y + 23, 137, 16)
                .withRange(0, sr.getPropsNumber(false))
                .titled(Component.translatable("gui.smarter_observer.target_property_scroll"))
                .addHint(Component.translatable("gui.smarter_observer.target_property_hint"))
                .calling(i -> {
                    selectedTargetProperty = sr.getProp(i);
                    // Sets the range based on the current property, otherwise could crash due to the targetValueSetter going to search a value at non-existing indexes.
                    targetValueSetter.withRange(0, sr.getPValuesNumber(selectedTargetProperty, false));
                    if (i == 0) {  // Detect disabled.
                        targetValueSetter.withRange(0, 1);  // If no property, cannot change the value.
                        targetValueSetter.setState(0);  // If no property to detect, you can't detect a value, am i right?
                        selectedTargetValue = SelectionRepresenter.DONT_DETECT;  // Set to don't detect, not updated automatically after setState(0).
                    }
                })
                .withStepFunction(sc -> 1)
                .setState(sr.getPropIndex(currentTargetProperty));  // Startup only

        targetValueSetter = new ScrollInput(x + 17, y + 49, 137, 16)
                .withRange(0, 1)  // Range gets set by property scroll based on the selected property.
                .titled(Component.translatable("gui.smarter_observer.target_value_scroll"))
                .addHint(Component.translatable("gui.smarter_observer.target_value_hint"))
                .calling(i -> selectedTargetValue = sr.getPValue(selectedTargetProperty, i))
                .withStepFunction(sc -> 1)
                .setState(sr.getPValueIndex(selectedTargetProperty, currentTargetValue));  // Startup only

        onForTicksSetter = new ScrollInput(x + 17, y + 75, 48, 16)
                .withRange(1, CUCommonConfig.MAX_ON_FOR_TICKS.getAsInt() + 1)
                .titled(Component.translatable("gui.smarter_observer.on_for_ticks_scroll"))
                .addHint(Component.translatable("gui.smarter_observer.on_for_ticks_hint"))
                .calling(i -> selectedOnForTicks = i)
                .withStepFunction(sc -> {
                    if (sc.control) return 200;  // 10 seconds
                    if (sc.shift) return 20;  // 1 second
                    return 1;  // 1 tick
                })
                .setState(currentOnForTicks);  // Startup only

        detectBehaviourSetter = new IconButton(x + 120, y + 105, DetectBehaviourRepresenter.getIcon(currentDetectBehaviour));
        detectBehaviourSetter.setToolTip(DetectBehaviourRepresenter.getTooltip(currentDetectBehaviour));
        detectBehaviourSetter.withCallback(() -> {
            selectedDetectBehaviour = DetectBehaviourRepresenter.getNext(selectedDetectBehaviour);
            detectBehaviourSetter.setIcon(DetectBehaviourRepresenter.getIcon(selectedDetectBehaviour));
            detectBehaviourSetter.setToolTip(DetectBehaviourRepresenter.getTooltip(selectedDetectBehaviour));
        });

        confirmButton = new IconButton(x + 149, y + 105, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> onClose());

        addRenderableWidget(targetPropertySetter);
        addRenderableWidget(targetValueSetter);
        addRenderableWidget(onForTicksSetter);
        addRenderableWidget(detectBehaviourSetter);
        addRenderableWidget(confirmButton);
    }

    @Override
    public void removed() {
        if (CUCommonConfig.LOG_ALL.getAsBoolean()) {
            CUMain.LOGGER.info("");
            CUMain.LOGGER.info("Observer settings:");
            CUMain.LOGGER.info(" - Block: {}", sobe.targetBlock);
            CUMain.LOGGER.info(" - Property: {}", selectedTargetProperty);
            CUMain.LOGGER.info(" - Prop. value: {}", selectedTargetValue);
            CUMain.LOGGER.info(" - Detect behaviour: {}", selectedDetectBehaviour);
            CUMain.LOGGER.info(" - On for ticks: {}", selectedOnForTicks);
        }
        CatnipServices.NETWORK.sendToServer(new SmarterObserverPacket(
                sobe.getBlockPos(),
                selectedTargetProperty,
                selectedTargetValue,
                selectedDetectBehaviour,
                selectedOnForTicks
        ));
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, guiLeft, guiTop);

        // GUI title
        graphics.drawString(
                font,
                title,
                x + background.getWidth() / 2 - font.width(title) / 2 - 4,  // Offset cause it seems off center to me.
                y + 4,
                0x592424,
                false
        );

        // Current target property
        graphics.drawString(
                font,
                SelectionRepresenter.checkAndTranslateStatics(selectedTargetProperty),
                x + 23,
                y + 27,
                0xFCFCEB,
                true
        );

        // Current target value
        graphics.drawString(
                font,
                SelectionRepresenter.checkAndTranslateStatics(selectedTargetValue),
                x + 23,
                y + 53,
                0xFCFCEB,
                true
        );

        // Current on for ticks
        graphics.drawString(
                font,
                Component.literal(selectedOnForTicks.toString() + "t"),
                x + 23,
                y + 79,
                0xFCFCEB,
                true
        );

        // Smarter observer block render
        GuiGameElement.of(smarterObserver).<GuiGameElement.GuiRenderBuilder>at(x + background.getWidth() + 6, y + background.getHeight() - 56, -200)
                .scale(5)
                .render(graphics);

        // Target block
        GuiGameElement.of(targetBlockAsItem).<GuiGameElement.GuiRenderBuilder>at(x + 13, y + 106, 0).render(graphics);

        // Target block tooltip
        if ((mouseX > x + 13 && mouseX <= x + 29) && (mouseY > y + 106 && mouseY <= y + 122)) {
            CUMain.LOGGER.info("fd");
            graphics.renderTooltip(
                    font,
                    Component.translatable("gui.smarter_observer.target_block_hint"),
                    mouseX, mouseY
            );
        }
    }

    protected static void openScreen(SmarterObserverBlockEntity sobe, Level level) {
        if (sobe.targetBlock != null & sobe.targetBlock != Blocks.AIR)
            ScreenOpener.open(new SmarterObserverScreen(sobe));
        else if (!level.isClientSide)
            level.playSound(null, sobe.getBlockPos(), SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), SoundSource.BLOCKS, 1.0f, 0.90f);
    }
}