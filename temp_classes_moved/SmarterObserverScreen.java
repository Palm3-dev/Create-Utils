package com.palm3.createutils.content.blocks.temp_code;

import com.palm3.createutils.CUGuiTextures;
import com.palm3.createutils.content.blocks.smarter_observer.SmarterObserverBlockEntity;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SmarterObserverScreen extends AbstractSimiScreen {
    private ScrollInput onForTicks;  // For how many ticks should the observer emit redstone

    private IconButton confirmButton;
    private CUGuiTextures background;

    private int previousTickValue = 0;
    private int tickStepValue = 1;
    private boolean showOnlyTicks = false;

    public SmarterObserverScreen(SmarterObserverBlockEntity be) {
        super(Component.translatable("gui.accelerator_motor.title"));
        background = CUGuiTextures.SMARTER_OBSERVER_BACKGROUND;
        this.blockEntity = be;
    }

    @Override
    protected void init() {
        this.windowWidth = 182;
        this.windowHeight = 103;
        setWindowSize(windowWidth, windowHeight);
        setWindowOffset(0, 0);
        super.init();

        int x = guiLeft;
        int y = guiTop;

        showOnlyTicks = blockEntity.getGuiShowsOnlyTicks();

        // Accelerate to max speed scroll
        onForTicks = new ScrollInput(x + 14, y + 23, 59, 16)
                .withRange(0, 72000 + 1)  // 1 hour
                .titled(Component.translatable("gui.smarter_observer.onForTicks"))
                .calling(state -> blockEntity.accelerateTo = state)
                .withStepFunction(sc -> {
                    if (sc.control) return 100;
                    if (sc.shift) return 10;
                    return 1;
                })
                .setState(blockEntity.accelerateTo);

        /*// If the direction is negative
        negativeDirectionValue = new ScrollInput(x + 14, y + 49, 59, 16)
                .withRange(0, 2)
                .titled(Component.translatable("gui.accelerator_motor.direction_scroll"))
                .calling(state -> {blockEntity.negativeDirection = (state == 1);})
                .withStepFunction(sc -> 1)
                .setState(blockEntity.negativeDirection ? 1 : 0);

        // How many rpm to increment for every tick
        increasedRpmPerTickValue = new ScrollInput(x + 98, y + 23, 59, 16)
                .withRange(1, CSCommonConfig.AcceleratorMotor_maxIncreasedRpmPerTick.getAsInt() + 1)
                .titled(Component.translatable("gui.accelerator_motor.increased_rpm_per_tick_scroll"))
                .calling(state -> blockEntity.increasedRpmPerTick = state)
                .withStepFunction(sc -> {
                    if (sc.shift) return 10;
                    return 1;
                })
                .setState(blockEntity.increasedRpmPerTick);

        // How often to increment the speed (in ticks)
        increaseEveryValue = new ScrollInput(x + 98, y + 49, 59, 16)
                .withRange(10, CSCommonConfig.AcceleratorMotor_maxIncreaseEvery.getAsInt() + 1)
                .titled(Component.translatable("gui.accelerator_motor.increase_every_scroll"))
                .calling(state -> { blockEntity.increaseEvery = state;})
                .withStepFunction(sc -> {
                    if (showOnlyTicks) {
                        if(sc.shift) return 10;
                        return 1;
                    } else {
                        if (sc.shift) return 100;
                        return 20;
                    }
                })
                .setState(blockEntity.increaseEvery);*/

        // Confirm button with exit function
        confirmButton = new IconButton(x + 149, y + 79, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> onClose());

        // Only ticks toggle
        /*onlyTicksToggleButton = new IconButton(x + 121, y + 79, showOnlyTicks ? CSGuiTextures.I_TICKS_BUTTON : CSGuiTextures.I_TICKS_SECONDS_BUTTON);
        onlyTicksToggleButton.withCallback(() -> {
            showOnlyTicks = !showOnlyTicks;
            if (increaseEveryValue.getState() % 20 != 0) {  // If it's showing seconds and ticks, rounds ticks to the corresponding seconds
                int ticks = increaseEveryValue.getState() / 20;
                increaseEveryValue.setState(ticks * 20);
            }
            onlyTicksToggleButton.setIcon(showOnlyTicks ? CSGuiTextures.I_TICKS_BUTTON : CSGuiTextures.I_TICKS_SECONDS_BUTTON);
        });*/

        // Adding widgets
        //addRenderableWidget(accelerateToValue);
        //addRenderableWidget(negativeDirectionValue);
        //addRenderableWidget(increasedRpmPerTickValue);
        //addRenderableWidget(increaseEveryValue);
        addRenderableWidget(confirmButton);
        //addRenderableWidget(onlyTicksToggleButton);

        // Set variable first value at GUI creation
        //previousTickValue = increaseEveryValue.getState();

    }

    @Override
    public void removed() {
        CatnipServices.NETWORK.sendToServer(new AcceleratorMotorPacket(
                blockEntity.getBlockPos(), accelerateToValue.getState(),
                negativeDirectionValue.getState() == 1,
                increasedRpmPerTickValue.getState(),
                increaseEveryValue.getState(),
                showOnlyTicks
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
                x + background.getWidth() / 2 - font.width(title) / 2,
                y + 4,
                0x592424,
                false
        );

        // Current accelerateTo value
        graphics.drawString(
                font,
                Component.literal(accelerateToValue.getState() + " RPM"),
                x + 20,
                y + 27,
                0xFCFCEB,
                true
        );

        // Current negativeDirection value
        graphics.drawString(
                font,
                Component.literal(negativeDirectionValue.getState() == 1 ? "Negative" : "Positive"),
                x + 20,
                y + 53,
                0xFCFCEB,
                true
        );

        // Current increasedRpmPerTick value
        graphics.drawString(
                font,
                Component.literal(increasedRpmPerTickValue.getState() + " RPM"),
                x + 104,
                y + 27,
                0xFCFCEB,
                true
        );

        // Current increasedEvery value
        if (showOnlyTicks) {
            graphics.drawString(
                    font,
                    Component.literal(increaseEveryValue.getState() + "t"),
                    x + 104,
                    y + 53,
                    0xFCFCEB,
                    true
            );
        } else {
            if (increaseEveryValue.getState() / 20 == 0) {
                graphics.drawString(
                        font,
                        Component.literal(increaseEveryValue.getState() + "t"),
                        x + 104,
                        y + 53,
                        0xFCFCEB,
                        true
                );
            } else {
                graphics.drawString(
                        font,
                        Component.literal(increaseEveryValue.getState() / 20 + "s"),  // From tick to seconds, 20 tick = 1 second
                        x + 104,
                        y + 53,
                        0xFCFCEB,
                        true
                );
            }
        }
    }

    // Block render near gui ----------------- TO-DO --------------------
    private final ItemStack icon = CSBlocks.ACCELERATOR_MOTOR_BLOCK.asStack();

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        int effectiveX = mouseX - guiLeft;
        int effectiveY = mouseY - guiTop;

        // Block dispaly near GUI
        GuiGameElement.of(icon)
                .at(2, 5)
                .render(graphics);

        // Ticks mode button tooltip
        if ((effectiveX > 121 && effectiveX < 140) && (effectiveY > 79 && effectiveY < 98)) {
            if (showOnlyTicks) {
                graphics.renderTooltip(
                        font,
                        Component.translatable("gui.accelerator_motor.tooltip.ticks_button")
                                .withColor(0xFCFCEB),
                        mouseX,
                        mouseY
                );
            } else {
                graphics.renderTooltip(
                        font,
                        Component.translatable("gui.accelerator_motor.tooltip.ticks_seconds_button")
                                .withColor(0xFCFCEB),
                        mouseX,
                        mouseY
                );
            }

        }
    }
}
