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
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

public class Speedometer
extends HudElement {
    public float speed = 0.0f;
    private final Setting<Boolean> bps = new Setting<Boolean>("BPS", false);
    private final Setting<Boolean> average = new Setting<Boolean>("Average", false);

    public Speedometer() {
        super("Speedometer", 50, 10);
    }

    @Override
    public void onRender2D(DrawContext context) {
        float pX;
        super.onRender2D(context);
        String str = "Speed " + String.valueOf(Formatting.WHITE);
        str = this.bps.getValue() == false ? str + MathUtility.round(this.getSpeedKpH() * ThunderHack.TICK_TIMER) + " km/h" : str + MathUtility.round(this.getSpeedMpS() * ThunderHack.TICK_TIMER) + " b/s";
        float f = pX = this.getPosX() > (float)mc.getWindow().getScaledWidth() / 2.0f ? this.getPosX() - FontRenderers.getModulesRenderer().getStringWidth(str) : this.getPosX();
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawRoundedBlur(context.getMatrices(), pX, this.getPosY(), FontRenderers.getModulesRenderer().getStringWidth(str) + 21.0f, 13.0f, 3.0f, HudEditor.blurColor.getValue().getColorObject());
            Render2DEngine.drawRect(context.getMatrices(), pX + 14.0f, this.getPosY() + 2.0f, 0.5f, 8.0f, new Color(0x44FFFFFF, true));
            Render2DEngine.setupRender();
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
            RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.speedometerIcon);
            Render2DEngine.renderGradientTexture(context.getMatrices(), pX + 2.0f, this.getPosY() + 1.0f, 10.0, 10.0, 0.0f, 0.0f, 512.0, 512.0, 512.0, 512.0, HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));
            Render2DEngine.endRender();
        }
        FontRenderers.getModulesRenderer().drawString(context.getMatrices(), str, (double)(pX + 18.0f), (double)(this.getPosY() + 5.0f), HudEditor.getColor(1).getRGB());
        this.setBounds(pX, this.getPosY(), FontRenderers.getModulesRenderer().getStringWidth(str) + 21.0f, 13.0f);
    }

    public float getSpeedKpH() {
        return (this.average.getValue() != false ? Managers.PLAYER.averagePlayerSpeed : Managers.PLAYER.currentPlayerSpeed) * 72.0f;
    }

    public float getSpeedMpS() {
        return (this.average.getValue() != false ? Managers.PLAYER.averagePlayerSpeed : Managers.PLAYER.currentPlayerSpeed) * 20.0f;
    }
}

