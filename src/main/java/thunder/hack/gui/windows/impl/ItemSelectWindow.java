/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.util.Formatting
 *  net.minecraft.item.Item
 *  net.minecraft.block.Block
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.StringHelper
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.registry.Registries
 *  net.minecraft.client.render.BuiltBuffer
 */
package thunder.hack.gui.windows.impl;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.StringHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.registry.Registries;
import net.minecraft.client.render.BuiltBuffer;
import thunder.hack.core.Managers;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.clickui.impl.SliderElement;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.windows.WindowBase;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ItemSelectSetting;
import thunder.hack.utility.render.Render2DEngine;

public class ItemSelectWindow
extends WindowBase {
    private Setting<ItemSelectSetting> itemSetting;
    private ArrayList<ItemPlate> itemPlates = new ArrayList();
    private ArrayList<ItemPlate> allItems = new ArrayList();
    private boolean allTab = true;
    private boolean listening = false;
    private String search = "Search";

    public ItemSelectWindow(Setting<ItemSelectSetting> itemSetting) {
        this((float)Module.mc.getWindow().getScaledWidth() / 2.0f - 100.0f, (float)Module.mc.getWindow().getScaledHeight() / 2.0f - 150.0f, 200.0f, 300.0f, itemSetting);
    }

    public ItemSelectWindow(float x, float y, float width, float height, Setting<ItemSelectSetting> itemSetting) {
        super(x, y, width, height, "Items / " + String.valueOf(Formatting.GRAY) + itemSetting.getModule().getName(), null, null);
        this.itemSetting = itemSetting;
        this.refreshItemPlates();
        int id1 = 0;
        for (Block block : Registries.BLOCK) {
            this.allItems.add(new ItemPlate(id1, id1 * 20, block.asItem(), block.getTranslationKey()));
            ++id1;
        }
        for (Item item : Registries.ITEM) {
            this.allItems.add(new ItemPlate(id1, id1 * 20, item, item.getTranslationKey()));
            ++id1;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY) {
        super.render(context, mouseX, mouseY);
        boolean hover1 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 90.0f, this.getY() + 3.0f, 70.0, 10.0);
        Render2DEngine.drawRect(context.getMatrices(), this.getX() + this.getWidth() - 90.0f, this.getY() + 3.0f, 70.0f, 10.0f, hover1 ? new Color(-981236861, true) : new Color(-984131753, true));
        FontRenderers.sf_medium_mini.drawString(context.getMatrices(), this.search, (double)(this.getX() + this.getWidth() - 86.0f), (double)(this.getY() + 7.0f), new Color(0xD5D5D5).getRGB());
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        int tabColor1 = this.allTab ? new Color(0xD5D5D5).getRGB() : Color.GRAY.getRGB();
        int tabColor2 = this.allTab ? Color.GRAY.getRGB() : new Color(0xBDBDBD).getRGB();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(this.getX() + 1.5f, this.getY() + 29.0f, 0.0f).color(Color.DARK_GRAY.getRGB());
        bufferBuilder.vertex(this.getX() + 8.0f, this.getY() + 29.0f, 0.0f).color(tabColor1);
        bufferBuilder.vertex(this.getX() + 8.0f, this.getY() + 19.0f, 0.0f).color(tabColor1);
        bufferBuilder.vertex(this.getX() + 48.0f, this.getY() + 19.0f, 0.0f).color(tabColor1);
        bufferBuilder.vertex(this.getX() + 54.0f, this.getY() + 29.0f, 0.0f).color(tabColor1);
        bufferBuilder.vertex(this.getX() + 52.0f, this.getY() + 25.0f, 0.0f).color(tabColor2);
        bufferBuilder.vertex(this.getX() + 52.0f, this.getY() + 19.0f, 0.0f).color(tabColor2);
        bufferBuilder.vertex(this.getX() + 92.0f, this.getY() + 19.0f, 0.0f).color(tabColor2);
        bufferBuilder.vertex(this.getX() + 100.0f, this.getY() + 29.0f, 0.0f).color(Color.GRAY.getRGB());
        bufferBuilder.vertex(this.getX() + this.getWidth() - 1.0f, this.getY() + 29.0f, 0.0f).color(Color.DARK_GRAY.getRGB());
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
        FontRenderers.sf_medium_mini.drawString(context.getMatrices(), "All", (double)(this.getX() + 25.0f), (double)(this.getY() + 25.0f), tabColor1);
        FontRenderers.sf_medium_mini.drawString(context.getMatrices(), "Selected", (double)(this.getX() + 60.0f), (double)(this.getY() + 25.0f), tabColor2);
        if (!this.allTab && this.itemPlates.isEmpty()) {
            FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), ClientSettings.isRu() ? "\u0422\u0443\u0442 \u043f\u043e\u043a\u0430 \u043f\u0443\u0441\u0442\u043e" : "It's empty here yet", (double)(this.getX() + this.getWidth() / 2.0f), (double)(this.getY() + this.getHeight() / 2.0f), new Color(0xBDBDBD).getRGB());
        }
        Render2DEngine.addWindow(context.getMatrices(), this.getX(), this.getY() + 30.0f, this.getX() + this.getWidth(), this.getY() + this.getHeight() - 1.0f, 1.0);
        for (ItemPlate itemPlate : this.allTab ? this.allItems : this.itemPlates) {
            if (itemPlate.offset + this.getY() + 25.0f + this.getScrollOffset() > this.getY() + this.getHeight() || itemPlate.offset + this.getScrollOffset() + this.getY() + 10.0f < this.getY()) continue;
            context.getMatrices().push();
            context.getMatrices().translate(this.getX() + 6.0f, itemPlate.offset + this.getY() + 32.0f + this.getScrollOffset(), 0.0f);
            context.drawItem(itemPlate.item().getDefaultStack(), 0, 0);
            context.getMatrices().pop();
            FontRenderers.sf_medium.drawString(context.getMatrices(), I18n.translate((String)itemPlate.key(), (Object[])new Object[0]), (double)(this.getX() + 26.0f), (double)(itemPlate.offset + this.getY() + 38.0f + this.getScrollOffset()), new Color(0xBDBDBD).getRGB());
            boolean hover2 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 20.0f, itemPlate.offset + this.getY() + 35.0f + this.getScrollOffset(), 11.0, 11.0);
            Render2DEngine.drawRect(context.getMatrices(), this.getX() + this.getWidth() - 20.0f, itemPlate.offset + this.getY() + 35.0f + this.getScrollOffset(), 11.0f, 11.0f, hover2 ? new Color(-981828998, true) : new Color(-984131753, true));
            boolean selected = this.itemPlates.stream().anyMatch(sI -> Objects.equals(sI.key, itemPlate.key));
            if (this.allTab && !selected) {
                FontRenderers.categories.drawString(context.getMatrices(), "+", (double)(this.getX() + this.getWidth() - 17.0f), (double)(itemPlate.offset + this.getY() + 39.0f + this.getScrollOffset()), -1);
                continue;
            }
            FontRenderers.icons.drawString(context.getMatrices(), "w", (double)(this.getX() + this.getWidth() - 19.5f), (double)(itemPlate.offset + this.getY() + 39.0f + this.getScrollOffset()), -1);
        }
        this.setMaxElementsHeight((this.allTab ? this.allItems : this.itemPlates).size() * 20);
        Render2DEngine.popWindow();
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (Render2DEngine.isHovered(mouseX, mouseY, this.getX() + 8.0f, this.getY() + 19.0f, 52.0, 19.0)) {
            this.allTab = true;
            this.resetScroll();
            Managers.SOUND.playBoolean();
        }
        if (Render2DEngine.isHovered(mouseX, mouseY, this.getX() + 54.0f, this.getY() + 19.0f, 70.0, 19.0)) {
            this.allTab = false;
            this.resetScroll();
            Managers.SOUND.playBoolean();
        }
        if (Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 90.0f, this.getY() + 3.0f, 70.0, 10.0)) {
            this.listening = true;
            this.search = "";
        }
        if (Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 3.0f, 10.0, 10.0)) {
            Module.mc.setScreen((Screen)ClickGUI.getClickGui());
        }
        var copy = Lists.newArrayList(this.allTab ? this.allItems : this.itemPlates);
        for (ItemPlate itemPlate : copy) {
            if ((float)((int)(itemPlate.offset + this.getY() + 50.0f)) + this.getScrollOffset() > this.getY() + this.getHeight()) continue;
            String name = itemPlate.key().replace("item.minecraft.", "").replace("block.minecraft.", "");
            if (!Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 20.0f, itemPlate.offset + this.getY() + 35.0f + this.getScrollOffset(), 10.0, 10.0)) continue;
            boolean selected = this.itemPlates.stream().anyMatch(sI -> Objects.equals(sI.key(), itemPlate.key));
            if (this.allTab && !selected) {
                if (this.itemSetting.getValue().getItemsById().contains(name)) continue;
                this.itemSetting.getValue().getItemsById().add(name);
                this.refreshItemPlates();
            } else {
                this.itemSetting.getValue().getItemsById().remove(name);
                this.refreshItemPlates();
            }
            Managers.SOUND.playScroll();
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 70 && (InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)341) || InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)345))) {
            this.listening = !this.listening;
            return;
        }
        if (this.listening) {
            switch (keyCode) {
                case 256: {
                    this.listening = false;
                    this.search = "Search";
                    this.refreshAllItems();
                    break;
                }
                case 259: {
                    this.search = SliderElement.removeLastChar(this.search);
                    this.refreshAllItems();
                    if (!Objects.equals(this.search, "")) break;
                    this.listening = false;
                    this.search = "Search";
                    break;
                }
                case 32: {
                    this.search = this.search + " ";
                }
            }
        }
    }

    @Override
    public void charTyped(char key, int keyCode) {
        if (StringHelper.isValidChar((char)key) && this.listening) {
            this.search = this.search + key;
            this.refreshAllItems();
        }
    }

    private void refreshItemPlates() {
        this.itemPlates.clear();
        int id = 0;
        for (Block block : Registries.BLOCK) {
            if (!this.itemSetting.getValue().getItemsById().contains(block.getTranslationKey().replace("block.minecraft.", ""))) continue;
            this.itemPlates.add(new ItemPlate(id, id * 20, block.asItem(), block.getTranslationKey()));
            ++id;
        }
        for (Item item : Registries.ITEM) {
            if (!this.itemSetting.getValue().getItemsById().contains(item.getTranslationKey().replace("item.minecraft.", ""))) continue;
            this.itemPlates.add(new ItemPlate(id, id * 20, item, item.getTranslationKey()));
            ++id;
        }
    }

    private void refreshAllItems() {
        this.allItems.clear();
        this.resetScroll();
        int id1 = 0;
        for (Block block : Registries.BLOCK) {
            if (!this.search.equals("Search") && !this.search.isEmpty() && !block.getTranslationKey().contains(this.search) && !I18n.translate((String)block.getTranslationKey(), (Object[])new Object[0]).toLowerCase().contains(this.search.toLowerCase())) continue;
            this.allItems.add(new ItemPlate(id1, id1 * 20, block.asItem(), block.getTranslationKey()));
            ++id1;
        }
        for (Item item : Registries.ITEM) {
            if (!this.search.equals("Search") && !this.search.isEmpty() && !item.getTranslationKey().contains(this.search) && !item.getName().getString().toLowerCase().contains(this.search.toLowerCase())) continue;
            this.allItems.add(new ItemPlate(id1, id1 * 20, item, item.getTranslationKey()));
            ++id1;
        }
    }

    private record ItemPlate(float id, float offset, Item item, String key) {
    }
}

