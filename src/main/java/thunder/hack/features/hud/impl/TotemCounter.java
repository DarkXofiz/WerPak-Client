/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.RotationAxis
 */
package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.RotationAxis;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.TotemPopEvent;
import thunder.hack.features.hud.HudElement;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public class TotemCounter
extends HudElement {
    private float angle;
    private float prevAngle;

    public TotemCounter() {
        super("TotemCounter", 0, 0);
    }

    @Override
    public void onRender2D(DrawContext context) {
        if (this.getItemCount(Items.TOTEM_OF_UNDYING) == 0) {
            return;
        }
        float xPos = ModuleManager.crosshair.getAnimatedPosX();
        float yPos = ModuleManager.crosshair.getAnimatedPosY();
        float factor = Math.abs(this.angle < 0.0f ? this.angle / 15.0f : 0.0f);
        context.getMatrices().push();
        context.getMatrices().translate(xPos, yPos, 0.0f);
        context.getMatrices().multiply(RotationAxis.NEGATIVE_Z.rotation((float)Math.toRadians(-Render2DEngine.interpolateFloat(this.prevAngle, this.angle, Render3DEngine.getTickDelta()))));
        context.getMatrices().translate(-xPos, -yPos, 0.0f);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        context.getMatrices().translate(xPos - 36.0f, yPos - 9.0f, 0.0f);
        context.drawItem(Items.TOTEM_OF_UNDYING.getDefaultStack(), 0, 0);
        context.getMatrices().translate(-(xPos - 36.0f), -(yPos - 9.0f), 0.0f);
        RenderSystem.setShaderColor((float)1.0f, (float)(1.0f - factor), (float)(1.0f - factor), (float)1.0f);
        if (factor > 0.0f) {
            Render2DEngine.drawBlurredShadow(context.getMatrices(), xPos - 34.0f, yPos - 6.0f, 11.0f, 11.0f, 8, Render2DEngine.injectAlpha(new Color(0xFF0000), (int)(255.0f * factor)));
        }
        FontRenderers.sf_bold_mini.drawCenteredString(context.getMatrices(), "" + this.getItemCount(Items.TOTEM_OF_UNDYING), (double)(xPos - 28.0f), (double)(yPos + 8.0f), -1);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        context.getMatrices().pop();
    }

    @EventHandler
    public void onTotemPop(TotemPopEvent e) {
        if (e.getEntity() == TotemCounter.mc.player) {
            this.angle = -15.0f;
        }
    }

    @Override
    public void onUpdate() {
        this.prevAngle = this.angle;
        if (this.angle < 0.0f) {
            this.angle += 1.0f;
        }
    }

    public int getItemCount(Item item) {
        if (TotemCounter.mc.player == null) {
            return 0;
        }
        int n = 0;
        int n2 = 44;
        for (int i = 0; i <= n2; ++i) {
            ItemStack itemStack = TotemCounter.mc.player.getInventory().getStack(i);
            if (itemStack.getItem() != item) continue;
            n += itemStack.getCount();
        }
        return n;
    }
}

