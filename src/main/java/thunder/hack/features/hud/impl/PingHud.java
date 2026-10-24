/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.core.Managers;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

public class PingHud
extends HudElement {
    public PingHud() {
        super("Ping", 50, 10);
    }

    @Override
    public void onRender2D(DrawContext context) {
        float pX;
        super.onRender2D(context);
        String str = "Ping " + String.valueOf(Formatting.WHITE) + Managers.SERVER.getPing();
        float f = pX = this.getPosX() > (float)mc.getWindow().getScaledWidth() / 2.0f ? this.getPosX() - FontRenderers.getModulesRenderer().getStringWidth(str) : this.getPosX();
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawRoundedBlur(context.getMatrices(), pX, this.getPosY(), FontRenderers.getModulesRenderer().getStringWidth(str) + 21.0f, 13.0f, 3.0f, HudEditor.blurColor.getValue().getColorObject());
            Render2DEngine.drawRect(context.getMatrices(), pX + 14.0f, this.getPosY() + 2.0f, 0.5f, 8.0f, new Color(0x44FFFFFF, true));
            Render2DEngine.setupRender();
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
            RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.pingIcon);
            Render2DEngine.renderGradientTexture(context.getMatrices(), pX + 2.0f, this.getPosY() + 1.0f, 10.0, 10.0, 0.0f, 0.0f, 512.0, 512.0, 512.0, 512.0, HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));
            Render2DEngine.endRender();
        }
        FontRenderers.getModulesRenderer().drawString(context.getMatrices(), str, (double)(pX + 18.0f), (double)(this.getPosY() + 5.0f), HudEditor.getColor(1).getRGB());
        this.setBounds(pX, this.getPosY(), FontRenderers.getModulesRenderer().getStringWidth(str) + 21.0f, 13.0f);
    }
}

