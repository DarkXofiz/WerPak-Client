/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.features.hud.HudElement;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;
import thunder.hack.utility.render.animation.AnimationUtility;

public class CandleHud
extends HudElement {
    private Setting<Integer> scale = new Setting<Integer>("Scale", 25, 15, 100);
    private Setting<For> mode = new Setting<For>("For", For.Win);
    private float xAnim;
    private float yAnim;
    private float prevPitch;

    public CandleHud() {
        super("Candle", 10, 100);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        float yDelta = CandleHud.mc.player.prevHeadYaw - CandleHud.mc.player.getHeadYaw();
        this.xAnim = yDelta > 0.0f ? AnimationUtility.fast(this.xAnim, -15.0f, 10.0f) : (yDelta < 0.0f ? AnimationUtility.fast(this.xAnim, 35.0f, 10.0f) : AnimationUtility.fast(this.xAnim, 10.0f, 10.0f));
        float pDelta = this.prevPitch - CandleHud.mc.player.getPitch();
        this.yAnim = pDelta > 0.0f ? AnimationUtility.fast(this.yAnim, -15.0f, 10.0f) : (pDelta < 0.0f ? AnimationUtility.fast(this.yAnim, 35.0f, 10.0f) : AnimationUtility.fast(this.yAnim, 10.0f, 10.0f));
        this.prevPitch = CandleHud.mc.player.getPitch();
        context.getMatrices().push();
        context.getMatrices().translate((float)((int)this.getPosX()), (float)((int)this.getPosY()), 0.0f);
        float scalefactor = (float)this.scale.getValue().intValue() / 100.0f;
        context.getMatrices().scale(scalefactor, scalefactor, 1.0f);
        context.drawTexture(TextureStorage.candle, 0, -5, 0.0f, 0.0f, 102, 529, 102, 529);
        context.getMatrices().pop();
        this.drawFire(context.getMatrices(), this.getPosX() + (55.0f - this.xAnim) * scalefactor, this.getPosY() + (10.0f - this.yAnim) * scalefactor, 7.0f * scalefactor, 7.0f * scalefactor, Render2DEngine.applyOpacity(new Color(16401935), (float)Math.sin((float)(CandleHud.mc.player.age + 10) / 15.0f) + 1.4f));
        this.drawFire(context.getMatrices(), this.getPosX() + (45.5f - this.xAnim / 2.0f) * scalefactor, this.getPosY() + (15.0f - this.yAnim / 2.0f) * scalefactor, 15.0f * scalefactor, 15.0f * scalefactor, Render2DEngine.applyOpacity(new Color(16748318), (float)Math.sin((float)(CandleHud.mc.player.age + 20) / 15.0f) + 1.5f));
        this.drawFire(context.getMatrices(), this.getPosX() + (41.5f - this.xAnim / 3.0f) * scalefactor, this.getPosY() + (27.0f - this.yAnim / 3.0f) * scalefactor, 20.0f * scalefactor, 20.0f * scalefactor, Render2DEngine.applyOpacity(new Color(16565074), (float)Math.sin((float)(CandleHud.mc.player.age + 30) / 15.0f) + 1.6f));
        this.drawFire(context.getMatrices(), this.getPosX() + (40.0f - this.xAnim / 4.0f) * scalefactor, this.getPosY() + (45.0f - this.yAnim / 4.0f) * scalefactor, 25.0f * scalefactor, 25.0f * scalefactor, Render2DEngine.applyOpacity(new Color(16568455), (float)Math.sin((float)(CandleHud.mc.player.age + 40) / 15.0f) + 1.7f));
        this.drawFire(context.getMatrices(), this.getPosX() + (44.0f - this.xAnim / 4.0f) * scalefactor, this.getPosY() + (43.0f - this.yAnim / 4.0f) * scalefactor, 15.0f * scalefactor, 15.0f * scalefactor, Render2DEngine.applyOpacity(new Color(6721782), (float)Math.sin((float)(CandleHud.mc.player.age + 40) / 15.0f) + 1.8f));
        this.setBounds(this.getPosX(), this.getPosY(), (int)(102.0f * scalefactor), (int)(529.0f * scalefactor));
    }

    private void drawFire(MatrixStack matrices, float x, float y, float width, float height, Color color) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.firefly);
        Render2DEngine.renderTexture(matrices, x -= 7.0f, y -= 7.0f, width, height += 14.0f, 0.0f, 0.0f, width += 14.0f, height, width, height);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private static enum For {
        Luck,
        Win,
        LowPing,
        AntiKick;

    }
}

