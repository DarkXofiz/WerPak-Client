/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.hud.impl;

import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

public class Cooldowns
extends HudElement {
    private float animation1;
    private float animation2;

    public Cooldowns() {
        super("Cooldowns", 100, 100);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        Render2DEngine.drawHudBase(context.getMatrices(), this.getPosX(), this.getPosY(), 100.0f, 40.0f, HudEditor.hudRound.getValue().floatValue());
        this.animation1 = AnimationUtility.fast(this.animation1, Cooldowns.mc.player.getAttackCooldownProgress(0.5f), 50.0f);
        this.animation2 = AnimationUtility.fast(this.animation2, 1.0f - (float)Cooldowns.mc.player.hurtTime / 10.0f, 50.0f);
        Render2DEngine.drawGradientRound(context.getMatrices(), this.getPosX() + 30.0f, this.getPosY() + 20.0f, 65.0f, 5.0f, 1.5f, HudEditor.getColor(90).darker().darker().darker(), HudEditor.getColor(180).darker().darker().darker(), HudEditor.getColor(0).darker().darker().darker(), HudEditor.getColor(270).darker().darker().darker());
        Render2DEngine.drawGradientRound(context.getMatrices(), this.getPosX() + 30.0f, this.getPosY() + 30.0f, 65.0f, 5.0f, 1.5f, HudEditor.getColor(90).darker().darker().darker(), HudEditor.getColor(180).darker().darker().darker(), HudEditor.getColor(0).darker().darker().darker(), HudEditor.getColor(270).darker().darker().darker());
        Render2DEngine.drawRect(context.getMatrices(), this.getPosX() + 30.0f, this.getPosY() + 20.0f, 65.0f * this.animation1, 5.0f, 1.5f, 1.0f);
        Render2DEngine.drawRect(context.getMatrices(), this.getPosX() + 30.0f, this.getPosY() + 30.0f, 65.0f * this.animation2, 5.0f, 1.5f, 1.0f);
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Glowing)) {
            FontRenderers.sf_bold.drawCenteredString(context.getMatrices(), "Cooldowns", (double)(this.getPosX() + 50.0f), (double)(this.getPosY() + 4.0f), HudEditor.textColor.getValue().getColorObject());
        } else {
            FontRenderers.sf_bold.drawGradientCenteredString(context.getMatrices(), "Cooldowns", this.getPosX() + 50.0f, this.getPosY() + 4.0f, 10);
        }
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawRectDumbWay(context.getMatrices(), this.getPosX() + 4.0f, this.getPosY() + 13.0f, this.getPosX() + this.getWidth() - 4.0f, this.getPosY() + 13.5f, new Color(0x54FFFFFF, true));
        } else {
            Render2DEngine.horizontalGradient(context.getMatrices(), this.getPosX() + 2.0f, this.getPosY() + 13.7f, this.getPosX() + 2.0f + 50.0f - 2.0f, this.getPosY() + 14.0f, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
            Render2DEngine.horizontalGradient(context.getMatrices(), this.getPosX() + 2.0f + 50.0f - 2.0f, this.getPosY() + 13.7f, this.getPosX() + 2.0f + 100.0f - 4.0f, this.getPosY() + 14.0f, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
        }
        FontRenderers.sf_bold_mini.drawString(context.getMatrices(), "Attack", (double)(this.getPosX() + 5.0f), (double)(this.getPosY() + 20.0f), HudEditor.textColor.getValue().getColor());
        FontRenderers.sf_bold_mini.drawString(context.getMatrices(), "Hurt", (double)(this.getPosX() + 5.0f), (double)(this.getPosY() + 30.0f), HudEditor.textColor.getValue().getColor());
        this.setBounds(this.getPosX(), this.getPosY(), 100.0f, 40.0f);
    }
}

