/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.hud.impl;

import java.awt.Color;
import net.minecraft.util.Formatting;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

public class PotionHud
extends HudElement {
    private float vAnimation;
    private float hAnimation;
    private final Setting<Boolean> colored = new Setting<Boolean>("Colored", false);

    public PotionHud() {
        super("Potions", 100, 100);
    }

    public static String getDuration(StatusEffectInstance pe) {
        if (pe.isInfinite()) {
            return "*:*";
        }
        int var1 = pe.getDuration();
        int mins = var1 / 1200;
        String sec = String.format("%02d", var1 % 1200 / 20);
        return mins + ":" + sec;
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        int y_offset1 = 0;
        float max_width = 50.0f;
        float pointerX = 0.0f;
        for (StatusEffectInstance potionEffect : PotionHud.mc.player.getStatusEffects()) {
            float timeWidth;
            StatusEffect potion = (StatusEffect)potionEffect.getEffectType().value();
            if (y_offset1 == 0) {
                y_offset1 += 4;
            }
            y_offset1 += 9;
            float nameWidth = FontRenderers.sf_bold_mini.getStringWidth(potion.getName().getString() + " " + (potionEffect.getAmplifier() + 1));
            float width = (nameWidth + (timeWidth = FontRenderers.sf_bold_mini.getStringWidth(PotionHud.getDuration(potionEffect)))) * 1.4f;
            if (width > max_width) {
                max_width = width;
            }
            if (!(timeWidth > pointerX)) continue;
            pointerX = timeWidth;
        }
        this.vAnimation = AnimationUtility.fast(this.vAnimation, 14 + y_offset1, 15.0f);
        this.hAnimation = AnimationUtility.fast(this.hAnimation, max_width, 15.0f);
        Render2DEngine.drawHudBase(context.getMatrices(), this.getPosX(), this.getPosY(), this.hAnimation, this.vAnimation, HudEditor.hudRound.getValue().floatValue());
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Glowing)) {
            FontRenderers.sf_bold.drawCenteredString(context.getMatrices(), "Potions", (double)(this.getPosX() + this.hAnimation / 2.0f), (double)(this.getPosY() + 4.0f), HudEditor.textColor.getValue().getColorObject());
        } else {
            FontRenderers.sf_bold.drawGradientCenteredString(context.getMatrices(), "Potions", this.getPosX() + this.hAnimation / 2.0f, this.getPosY() + 4.0f, 10);
        }
        if (y_offset1 > 0) {
            if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
                Render2DEngine.drawRectDumbWay(context.getMatrices(), this.getPosX() + 4.0f, this.getPosY() + 13.0f, this.getPosX() + this.getWidth() - 4.0f, this.getPosY() + 13.5f, new Color(0x54FFFFFF, true));
            } else {
                Render2DEngine.horizontalGradient(context.getMatrices(), this.getPosX() + 2.0f, this.getPosY() + 13.7f, this.getPosX() + 2.0f + this.hAnimation / 2.0f - 2.0f, this.getPosY() + 14.0f, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
                Render2DEngine.horizontalGradient(context.getMatrices(), this.getPosX() + 2.0f + this.hAnimation / 2.0f - 2.0f, this.getPosY() + 13.7f, this.getPosX() + 2.0f + this.hAnimation - 4.0f, this.getPosY() + 14.0f, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
            }
        }
        Render2DEngine.addWindow(context.getMatrices(), this.getPosX(), this.getPosY(), this.getPosX() + this.hAnimation, this.getPosY() + this.vAnimation, 1.0);
        int y_offset = 0;
        for (StatusEffectInstance potionEffect : PotionHud.mc.player.getStatusEffects()) {
            StatusEffect potion = (StatusEffect)potionEffect.getEffectType().value();
            float px = this.getPosX() + (max_width - pointerX - 10.0f);
            context.getMatrices().push();
            context.getMatrices().translate(this.getPosX() + 2.0f, this.getPosY() + 16.0f + (float)y_offset, 0.0f);
            context.drawSprite(0, 0, 0, 8, 8, mc.getStatusEffectSpriteManager().getSprite(potionEffect.getEffectType()));
            context.getMatrices().pop();
            FontRenderers.sf_bold_mini.drawString(context.getMatrices(), potion.getName().getString() + " " + String.valueOf(Formatting.RED) + (potionEffect.getAmplifier() + 1), (double)(this.getPosX() + 12.0f), (double)(this.getPosY() + 19.0f + (float)y_offset), HudEditor.textColor.getValue().getColor());
            FontRenderers.sf_bold_mini.drawCenteredString(context.getMatrices(), PotionHud.getDuration(potionEffect), (double)(px + (this.getPosX() + max_width - px) / 2.0f), (double)(this.getPosY() + 19.0f + (float)y_offset), HudEditor.textColor.getValue().getColor());
            Render2DEngine.drawRect(context.getMatrices(), px, this.getPosY() + 17.0f + (float)y_offset, 0.5f, 8.0f, new Color(0x44FFFFFF, true));
            y_offset += 9;
        }
        Render2DEngine.popWindow();
        this.setBounds(this.getPosX(), this.getPosY(), this.hAnimation, this.vAnimation);
    }
}

