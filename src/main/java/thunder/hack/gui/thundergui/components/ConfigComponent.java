/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.gui.thundergui.components;

import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.core.Managers;
import thunder.hack.features.modules.client.ThunderHackGui;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.thundergui.ThunderGui;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

public class ConfigComponent {
    float scroll_animation = 0.0f;
    private final String name;
    private final String date;
    private int posX;
    private int posY;
    private int progress;
    private int fade;
    private final int index;
    private boolean first_open = true;
    private float scrollPosY;
    private float prevPosY;

    public ConfigComponent(String name, String date, int posX, int posY, int index) {
        this.name = name;
        this.date = date;
        this.posX = posX;
        this.posY = posY;
        this.fade = 0;
        this.index = index * 5;
        this.scrollPosY = posY;
        this.scroll_animation = 0.0f;
    }

    public void render(DrawContext context, int MouseX, int MouseY) {
        if (this.scrollPosY != (float)this.posY) {
            this.scroll_animation = AnimationUtility.fast(this.scroll_animation, 1.0f, 15.0f);
            this.posY = (int)Render2DEngine.interpolate(this.prevPosY, this.scrollPosY, this.scroll_animation);
        }
        if (this.posY > ThunderGui.getInstance().main_posY + ThunderGui.getInstance().height || this.posY < ThunderGui.getInstance().main_posY) {
            return;
        }
        if (Managers.CONFIG.currentConfig.getName().equals(this.name + ".th")) {
            Render2DEngine.drawGradientRound(context.getMatrices(), this.posX + 5, this.posY, 285.0f, 30.0f, 4.0f, Render2DEngine.applyOpacity(new Color(55, 44, 66, 255), this.getFadeFactor()), Render2DEngine.applyOpacity(new Color(25, 20, 30, 255), this.getFadeFactor()), Render2DEngine.applyOpacity(ThunderHackGui.onColor1.getValue().getColorObject(), this.getFadeFactor()), Render2DEngine.applyOpacity(ThunderHackGui.onColor2.getValue().getColorObject(), this.getFadeFactor()));
        } else {
            Render2DEngine.drawRound(context.getMatrices(), this.posX + 5, this.posY, 285.0f, 30.0f, 4.0f, Render2DEngine.applyOpacity(new Color(44, 35, 52, 255), this.getFadeFactor()));
        }
        if (this.first_open) {
            Render2DEngine.addWindow(context.getMatrices(), this.posX + 5, this.posY, this.posX + 5 + 285, this.posY + 30, 1.0);
            Render2DEngine.drawBlurredShadow(context.getMatrices(), MouseX - 20, MouseY - 20, 40.0f, 40.0f, 60, Render2DEngine.applyOpacity(new Color(-1017816450, true), this.getFadeFactor()));
            Render2DEngine.popWindow();
            this.first_open = false;
        }
        if (this.isHovered(MouseX, MouseY)) {
            Render2DEngine.addWindow(context.getMatrices(), this.posX + 5, this.posY, this.posX + 5 + 285, this.posY + 30, 1.0);
            Render2DEngine.drawBlurredShadow(context.getMatrices(), MouseX - 20, MouseY - 20, 40.0f, 40.0f, 60, Render2DEngine.applyOpacity(new Color(-1017816450, true), this.getFadeFactor()));
            Render2DEngine.popWindow();
        }
        Render2DEngine.drawRound(context.getMatrices(), this.posX + 250, this.posY + 8, 30.0f, 14.0f, 2.0f, Render2DEngine.applyOpacity(new Color(25, 20, 30, 255), this.getFadeFactor()));
        if (Render2DEngine.isHovered(MouseX, MouseY, this.posX + 252, this.posY + 10, 10.0, 10.0)) {
            Render2DEngine.drawRound(context.getMatrices(), this.posX + 252, this.posY + 10, 10.0f, 10.0f, 2.0f, Render2DEngine.applyOpacity(new Color(21, 58, 0, 255), this.getFadeFactor()));
        } else {
            Render2DEngine.drawRound(context.getMatrices(), this.posX + 252, this.posY + 10, 10.0f, 10.0f, 2.0f, Render2DEngine.applyOpacity(new Color(32, 89, 0, 255), this.getFadeFactor()));
        }
        if (Render2DEngine.isHovered(MouseX, MouseY, this.posX + 268, this.posY + 10, 10.0, 10.0)) {
            Render2DEngine.drawRound(context.getMatrices(), this.posX + 268, this.posY + 10, 10.0f, 10.0f, 2.0f, Render2DEngine.applyOpacity(new Color(65, 1, 13, 255), this.getFadeFactor()));
        } else {
            Render2DEngine.drawRound(context.getMatrices(), this.posX + 268, this.posY + 10, 10.0f, 10.0f, 2.0f, Render2DEngine.applyOpacity(new Color(94, 1, 18, 255), this.getFadeFactor()));
        }
        FontRenderers.icons.drawString(context.getMatrices(), "x", (double)(this.posX + 252), (double)(this.posY + 13), Render2DEngine.applyOpacity(-1, this.getFadeFactor()));
        FontRenderers.icons.drawString(context.getMatrices(), "w", (double)(this.posX + 268), (double)(this.posY + 13), Render2DEngine.applyOpacity(-1, this.getFadeFactor()));
        FontRenderers.mid_icons.drawString(context.getMatrices(), "u", (double)(this.posX + 7), (double)(this.posY + 5), Render2DEngine.applyOpacity(-1, this.getFadeFactor()));
        FontRenderers.modules.drawString(context.getMatrices(), this.name, (double)(this.posX + 37), (double)(this.posY + 6), Render2DEngine.applyOpacity(-1, this.getFadeFactor()));
        FontRenderers.settings.drawString(context.getMatrices(), "updated on: " + this.date, (double)(this.posX + 37), (double)(this.posY + 17), Render2DEngine.applyOpacity(new Color(-4342339, true).getRGB(), this.getFadeFactor()));
    }

    private float getFadeFactor() {
        return (float)this.fade / (5.0f + (float)this.index);
    }

    public void onTick() {
        if (this.progress > 4) {
            this.progress = 0;
        }
        ++this.progress;
        if (this.fade < 10 + this.index) {
            ++this.fade;
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX > this.posX && mouseX < this.posX + 295 && mouseY > this.posY && mouseY < this.posY + 30;
    }

    public void movePosition(float deltaX, float deltaY) {
        this.posY = (int)((float)this.posY + deltaY);
        this.posX = (int)((float)this.posX + deltaX);
        this.scrollPosY = this.posY;
    }

    public void mouseClicked(int MouseX, int MouseY, int clickedButton) {
        if (this.posY > ThunderGui.getInstance().main_posY + ThunderGui.getInstance().height || this.posY < ThunderGui.getInstance().main_posY) {
            return;
        }
        if (Render2DEngine.isHovered(MouseX, MouseY, this.posX + 252, this.posY + 10, 10.0, 10.0)) {
            Managers.CONFIG.load(this.name);
        }
        if (Render2DEngine.isHovered(MouseX, MouseY, this.posX + 268, this.posY + 10, 10.0, 10.0)) {
            Managers.CONFIG.delete(this.name);
            ThunderGui.getInstance().loadConfigs();
        }
    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public void scrollElement(float deltaY) {
        this.scroll_animation = 0.0f;
        this.prevPosY = this.posY;
        this.scrollPosY += deltaY;
    }
}

