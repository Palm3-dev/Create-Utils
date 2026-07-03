package com.palm3.createutils.content.blocks.smarter_observer;

import com.palm3.createutils.CUGuiTextures;
import com.palm3.createutils.CUMain;
import com.palm3.createutils.config.CUCommonConfig;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.palm3.createutils.CUMain.l;
import static com.palm3.createutils.content.blocks.smarter_observer.PropertiesRepresenter.CANT_DETECT;
import static com.palm3.createutils.content.blocks.smarter_observer.PropertiesRepresenter.DONT_DETECT;

@ParametersAreNonnullByDefault
public class SmarterObserverScreen extends AbstractSimiScreen {














    protected static void tryOpenScreen(SmarterObserverBlockEntity sobe, Level level) {
        if (sobe.targetBlock != null & sobe.targetBlock != Blocks.AIR) ScreenOpener.open(new SmarterObserverScreen(sobe));
        else if (level.isClientSide) level.playSound(null, sobe.getBlockPos(), SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), SoundSource.BLOCKS, 1.0f, 0.90f);
    }












/*
    ///  Starts from 0, contains the PROPERTIES.
    private final LinkedHashMap<Integer, Property<?>> targetProps = new LinkedHashMap<>();
    // Map with all the target propValues, arranged by number.

    private final LinkedHashMap<String, Integer> targetPropsString = new LinkedHashMap<>();
    // USed only for the scroll value to set the state based on the property

    private final int propertiesAmount;  // The number of propValues the target block has.

    /// Contains the {@link List}s of VALUES, accessed via the respective {@link Property}.
    private final LinkedHashMap<Property<?>, List<String>> targetValuesPerProp = new LinkedHashMap<>();
    // Map with a List of all the possible property values (as string) for each Property.

    /// Contains the VALUES AMOUNTS accessible via the respective {@link Property}.
    private final LinkedHashMap<Property<?>, Integer> valuesAmountPerProp = new LinkedHashMap<>();
    // Map with the values number for each Property.*/




    private final PropertiesRepresenter propValues;



    // Selected things to detect
    //private @Nullable Property<?> targetPropertyProp;  // The selected property as Property<type>
    //private String targetPropertyString = DONT_DETECT;  // The selected property as String (send this to BE), default detecting is disabled.
    //private String targetValueString = DONT_DETECT;  // The selected value as String (send this to BE).

    // ScrollInput
    private ScrollInput targetValueSetter;  // ScrollInput to choose the property value to detect.
    private ScrollInput targetPropertySetter;  // ScrollInput to choose a property to detect.

    // Textures
    private IconButton confirmButton;
    private CUGuiTextures background;

    private SmarterObserverBlockEntity sobe;  // The BE to save the values in.

    public SmarterObserverScreen(SmarterObserverBlockEntity sobe) {
        super(Component.translatable("gui.smarter_observer.title"));
        background = CUGuiTextures.SMARTER_OBSERVER_BACKGROUND;
        this.sobe = sobe;



        propValues = new PropertiesRepresenter(sobe.getTargetBlockProps(), CUCommonConfig.LOG_ALL.getAsBoolean());
        if (propValues.hasProps()) {
            sobe.targetProperty = propValues.getPropString(0);
            sobe.targetValue = propValues.getValueString(propValues.getProp(0), 0);
        } else {
            sobe.targetProperty = CANT_DETECT;
            sobe.targetValue = CANT_DETECT;
        }




        /*

        // Set up all maps.
        int i = 0;
        for (Property<?> prop : sobe.getTargetBlockProps()) {
            // Add propValues to propValues map.
            targetProps.put(i, prop);
            targetPropsString.put(prop.getName(), prop);

            // Add list of values for each property.
            List<String> propValues = new ArrayList<>();
            prop.getPossibleValues().forEach(value -> propValues.add(value.toString()));
            targetValuesPerProp.put(prop, propValues);
            i++;
        }

        // Set propValues amount.
        propertiesAmount = targetProps.size();

        // Adds the number of values for each property.
        targetValuesPerProp.forEach((prop, values) -> valuesAmountPerProp.put(prop, values.size()));

        // Logging
        if (CUCommonConfig.LOG_ALL.getAsBoolean()) {
            CUMain.LOGGER.info("Selected block propValues: ");
            if (!sobe.targetBlockHasProps()) CUMain.LOGGER.info("null, block doesn't have propValues.");
            targetValuesPerProp.forEach((prop, propValues) -> {
                CUMain.LOGGER.info(" {}", prop.getName());
                CUMain.LOGGER.info("    values:");
                propValues.forEach(value -> CUMain.LOGGER.info("      {}", value));
            });
        }*/
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

        l("======== Screen init() ========");
        l("targetPropertySetter scroll input:");
        //AtomicInteger targetPropertyState = new AtomicInteger();
        targetPropertySetter = new ScrollInput(x + 14, y + 23, 59, 16)
                .withRange(0, propValues.getPropsNumber())
                .titled(Component.translatable("gui.smarter_observer.target_property_scroll"))
                .calling(i -> {
                    CUMain.LOGGER.info("i = {}", i);
                    //targetPropertyState.set(i);
                    if (!propValues.hasProps()) {
                        sobe.targetProperty = CANT_DETECT;
                        sobe.targetValue = CANT_DETECT;
                    } else {
                        if (i == 0) {  // Don't detect.
                            sobe.targetProperty = DONT_DETECT;
                            sobe.targetValue = DONT_DETECT;
                        } else {
                            sobe.targetProperty = propValues.getPropString(i - 1);
                            sobe.targetValue = "shall set";
                        }
                    }
                })
                .withStepFunction(sc -> 1)
                .setState(propValues.getPropIndex(sobe.targetProperty));

        //int maxValue = targetPropertyProp == null ? 1 : valuesAmountPerProp.get(targetPropertyProp);
        //AtomicInteger targetValueState = new AtomicInteger();
        targetValueSetter = new ScrollInput(x + 70, y + 23, 59, 16)
                .withRange(0, propValues.getValuesNumber(sobe.targetProperty) + 1)
                .titled(Component.translatable("gui.smarter_observer.target_value_scroll"))
                .calling(i -> {
                    //targetValueState.set(i);

                    //if (targetPropertyProp == null) CUMain.LOGGER.info("targetPropertyProp is 'null'");

                    if (i == 0) CUMain.LOGGER.info("value is 0");
                    //else targetValueString = propValues.getValueString(propValues.getProp(sobe.targetProperty), i - 1);

                })
                .withStepFunction(sc -> 1)
                .setState(propValues.getValueIndex(propValues.getProp(sobe.targetProperty), sobe.targetValue));

        confirmButton = new IconButton(x + 149, y + 79, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> onClose());

        addRenderableWidget(targetPropertySetter);
        //addRenderableWidget(targetValueSetter);
        addRenderableWidget(confirmButton);
    }

    @Override
    public void removed() {
        CUMain.LOGGER.info("Should send packet!");
        /*CatnipServices.NETWORK.sendToServer(new AcceleratorMotorPacket(
                blockEntity.getBlockPos(), accelerateToValue.getState(),
                negativeDirectionValue.getState() == 1,
                increasedRpmPerTickValue.getState(),
                increaseEveryValue.getState(),
                showOnlyTicks
        ));*/
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

        // Current target property
        graphics.drawString(
                font,
                Component.literal(sobe.targetProperty == null ? "null" : sobe.targetProperty),
                x + 20,
                y + 27,
                0xFCFCEB,
                true
        );

        // Current target value
        graphics.drawString(
                font,
                Component.literal("ignore"),
                x + 20,
                y + 53,
                0xFCFCEB,
                true
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}
