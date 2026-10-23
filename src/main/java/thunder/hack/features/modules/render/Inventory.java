/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.modules.render;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.events.impl.EventMouse;
import thunder.hack.features.hud.HudElement;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;

public class Inventory
extends HudElement {
    public final Setting<ThemeStyle> themeStyle = new Setting<ThemeStyle>("Style", ThemeStyle.Transparent);

    public Inventory() {
        super("Inventory", 162, 54);
    }

    @Override
    @EventHandler
    public void onMouse(EventMouse event) {
        if (event.getAction() == 1 && event.getButton() == 1 && this.isHovering()) {
            ThemeStyle[] styles = ThemeStyle.values();
            this.themeStyle.setValue(styles[(this.themeStyle.getValue().ordinal() + 1) % styles.length]);
        }
        super.onMouse(event);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        if (Inventory.mc.player == null) {
            return;
        }
        float x = this.getPosX();
        float y = this.getPosY();
        if (this.themeStyle.getValue() == ThemeStyle.Solid) {
            Render2DEngine.drawRound(context.getMatrices(), x, y, 162.0f, 54.0f, 4.0f, new Color(15, 15, 15, 230));
        } else {
            Render2DEngine.drawRound(context.getMatrices(), x, y, 162.0f, 54.0f, 4.0f, new Color(0, 0, 0, 100));
        }
        DefaultedList inventory = Inventory.mc.player.getInventory().main;
        for (int i = 9; i < 36; ++i) {
            ItemStack stack = (ItemStack)inventory.get(i);
            if (stack.isEmpty()) continue;
            int slotX = (int)x + (i - 9) % 9 * 18;
            int slotY = (int)y + (i - 9) / 9 * 18;
            context.drawItem(stack, slotX, slotY);
            context.drawItemInSlot(Inventory.mc.textRenderer, stack, slotX, slotY);
        }
        this.setBounds(x, y, 162.0f, 54.0f);
    }

    public static enum ThemeStyle {
        Transparent,
        Solid;

    }
}

