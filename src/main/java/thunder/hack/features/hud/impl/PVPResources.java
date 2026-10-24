/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.hud.impl;

import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemConvertible;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.Render2DEngine;

public class PVPResources
extends HudElement {
    public PVPResources() {
        super("PVPResources", 50, 50);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        Render2DEngine.drawHudBase(context.getMatrices(), this.getPosX(), this.getPosY(), 50.0f, 50.0f, HudEditor.hudRound.getValue().floatValue());
        this.setBounds(this.getPosX(), this.getPosY(), 50.0f, 50.0f);
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawRectDumbWay(context.getMatrices(), this.getPosX(), this.getPosY() + 24.5f, this.getPosX() + 50.0f, this.getPosY() + 25.0f, new Color(0x54FFFFFF, true));
            Render2DEngine.drawRectDumbWay(context.getMatrices(), this.getPosX() + 24.5f, this.getPosY() - 1.0f, this.getPosX() + 25.0f, this.getPosY() + 49.0f, new Color(0x54FFFFFF, true));
        } else {
            Render2DEngine.horizontalGradient(context.getMatrices(), this.getPosX() + 2.0f, this.getPosY() + 24.5f, this.getPosX() + 26.0f, this.getPosY() + 25.0f, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
            Render2DEngine.horizontalGradient(context.getMatrices(), this.getPosX() + 26.0f, this.getPosY() + 24.5f, this.getPosX() + 48.0f, this.getPosY() + 25.0f, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
            Render2DEngine.verticalGradient(context.getMatrices(), this.getPosX() + 25.5f, this.getPosY() + 2.0f, this.getPosX() + 26.0f, this.getPosY() + 23.0f, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
            Render2DEngine.verticalGradient(context.getMatrices(), this.getPosX() + 25.5f, this.getPosY() + 23.0f, this.getPosX() + 26.0f, this.getPosY() + 48.0f, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
        }
        int totemCount = this.getItemCount(Items.TOTEM_OF_UNDYING);
        int xpCount = this.getItemCount(Items.EXPERIENCE_BOTTLE);
        int crystalCount = this.getItemCount(Items.END_CRYSTAL);
        int gappleCount = this.getItemCount(Items.ENCHANTED_GOLDEN_APPLE);
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        if (totemCount > 0) {
            list.add(new ItemStack((ItemConvertible)Items.TOTEM_OF_UNDYING, totemCount));
        }
        if (xpCount > 0) {
            list.add(new ItemStack((ItemConvertible)Items.EXPERIENCE_BOTTLE, xpCount));
        }
        if (crystalCount > 0) {
            list.add(new ItemStack((ItemConvertible)Items.END_CRYSTAL, crystalCount));
        }
        if (gappleCount > 0) {
            list.add(new ItemStack((ItemConvertible)Items.ENCHANTED_GOLDEN_APPLE, gappleCount));
        }
        for (int i = 0; i < list.size(); ++i) {
            int offsetX = i % 2 * 25;
            int offsetY = i / 2 * 25;
            context.drawItem((ItemStack)list.get(i), (int)(this.getPosX() + (float)offsetX + 4.0f), (int)(this.getPosY() + (float)offsetY + 4.0f));
            context.getMatrices().push();
            context.getMatrices().translate(0.0f, 0.0f, 151.0f);
            Render2DEngine.drawBlurredShadow(context.getMatrices(), this.getPosX() + (float)offsetX + 8.0f, this.getPosY() + (float)offsetY + 8.0f, 9.0f, 9.0f, 12, Color.BLACK);
            FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), String.valueOf(((ItemStack)list.get(i)).getCount()), (double)((int)(this.getPosX() + (float)offsetX + 12.0f)), (double)((int)(this.getPosY() + (float)offsetY + 11.0f)), HudEditor.textColor.getValue().getColor());
            context.getMatrices().pop();
        }
    }

    public int getItemCount(Item item) {
        if (PVPResources.mc.player == null) {
            return 0;
        }
        int n = 0;
        int n2 = 44;
        for (int i = 0; i <= n2; ++i) {
            ItemStack itemStack = PVPResources.mc.player.getInventory().getStack(i);
            if (itemStack.getItem() != item) continue;
            n += itemStack.getCount();
        }
        return n;
    }
}

