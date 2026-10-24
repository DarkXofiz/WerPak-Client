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
import thunder.hack.utility.render.animation.EaseOutCirc;

public class TimerIndicator
extends HudElement {
    private final EaseOutCirc timerAnimation = new EaseOutCirc();

    public TimerIndicator() {
        super("TimerIndicator", 60, 10);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawRoundedBlur(context.getMatrices(), this.getPosX(), this.getPosY(), 65.0f, 15.0f, 3.0f, HudEditor.blurColor.getValue().getColorObject());
            Render2DEngine.drawRect(context.getMatrices(), this.getPosX(), this.getPosY(), 65.0f, 15.0f, 3.0f, 0.4f);
            FontRenderers.sf_bold_mini.drawCenteredString(context.getMatrices(), "100%", (double)(this.getPosX() + 32.0f), (double)(this.getPosY() + 5.5f), new Color(200, 200, 200, 255).getRGB());
            this.setBounds(this.getPosX(), this.getPosY(), 65.0f, 15.0f);
        } else {
            Render2DEngine.drawGradientBlurredShadow(context.getMatrices(), this.getPosX() - 1.0f, this.getPosY() - 1.0f, 62.0f, 12.0f, 6, HudEditor.getColor(90), HudEditor.getColor(180), HudEditor.getColor(0), HudEditor.getColor(270));
            Render2DEngine.drawRect(context.getMatrices(), this.getPosX(), this.getPosY(), 60.0f, 10.0f, new Color(-1644167168, true));
            Render2DEngine.draw2DGradientRect(context.getMatrices(), this.getPosX(), this.getPosY(), this.getPosX() + 60.0f, this.getPosY() + 10.0f, HudEditor.getColor(90), HudEditor.getColor(180), HudEditor.getColor(0), HudEditor.getColor(270));
            Render2DEngine.drawBlurredShadow(context.getMatrices(), this.getPosX() + 20.0f, this.getPosY(), 22.0f, 10.0f, 6, new Color(0x47000000, true));
            FontRenderers.sf_bold_mini.drawCenteredString(context.getMatrices(), "100%", (double)(this.getPosX() + 31.0f), (double)(this.getPosY() + 3.5f), new Color(200, 200, 200, 255).getRGB());
            this.setBounds(this.getPosX(), this.getPosY(), 60.0f, 10.0f);
        }
    }

    @Override
    public void onUpdate() {
        this.timerAnimation.update();
    }
}

