/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.item.ArmorItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.features.hud.HudElement;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;

public class ArmorHud
extends HudElement {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.V2);

    public ArmorHud() {
        super("ArmorHud", 60, 25);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        float xItemOffset = this.getPosX();
        for (ItemStack itemStack : ArmorHud.mc.player.getInventory().armor.reversed()) {
            if (itemStack.isEmpty()) continue;
            if (this.mode.is(Mode.V1)) {
                context.drawItem(itemStack, (int)xItemOffset, (int)this.getPosY());
                context.drawItemInSlot(ArmorHud.mc.textRenderer, itemStack, (int)xItemOffset, (int)this.getPosY());
            } else {
                ArmorItem ai;
                RenderSystem.setShaderColor((float)0.4f, (float)0.4f, (float)0.4f, (float)0.35f);
                context.drawItem(itemStack, (int)xItemOffset, (int)this.getPosY());
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                Item class_17922 = itemStack.getItem();
                float offset = class_17922 instanceof ArmorItem && (ai = (ArmorItem)class_17922).getSlotType() == EquipmentSlot.HEAD ? -4 : 0;
                Render2DEngine.addWindow(context.getMatrices(), (int)xItemOffset, this.getPosY() + offset + (15.0f - offset) * ((float)itemStack.getDamage() / (float)itemStack.getMaxDamage()), xItemOffset + 15.0f, this.getPosY() + 15.0f, 1.0);
                context.drawItem(itemStack, (int)xItemOffset, (int)this.getPosY());
                Render2DEngine.popWindow();
            }
            xItemOffset += 20.0f;
        }
        this.setBounds(this.getPosX(), this.getPosY(), 60.0f, 25.0f);
    }

    private static enum Mode {
        V1,
        V2;

    }
}

