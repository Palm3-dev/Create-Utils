package com.palm3.createutils;

import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public enum CUGuiTextures implements ScreenElement, TextureSheetSegment {

    // Icons
    BLOCK_PLACED_I("icons", 0, 0, 16, 16),
    BLOCK_REMOVED_I("icons", 16, 0, 16, 16),
    BLOCK_PLACED_REMOVED_I("icons", 32, 0, 16, 16),
    TICK_I("icons", 48, 0, 16, 16),
    TICK_SECONDS_I("icons", 64, 0, 16, 16),

    // Backgrounds
    SMARTER_OBSERVER_BACKGROUND("smarter_observer_background", 182, 129);


    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation textureLocation;
    private final int width;
    private final int height;
    private final int startX;
    private final int startY;

    CUGuiTextures(String locationInGuiDir, int width, int height) {
        this(locationInGuiDir, 0, 0, width, height);
    }

    CUGuiTextures(String locationInGuiDir, int startX, int startY, int width, int height) {
        this(CUMain.MOD_ID, locationInGuiDir, startX, startY, width, height);
    }

    CUGuiTextures(String namespace, String locationInGuiDir, int startX, int startY, int width, int height) {
        this.textureLocation = ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + locationInGuiDir + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    public ResourceLocation getLocation() {
        return textureLocation;
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(textureLocation, x, y, startX, startY, width, height);
    }

    @Override
    public int getStartX() {
        return startX;
    }

    @Override
    public int getStartY() {
        return startY;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
