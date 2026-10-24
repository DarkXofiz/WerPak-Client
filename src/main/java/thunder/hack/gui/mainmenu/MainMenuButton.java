/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.gui.mainmenu;

import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.Render2DEngine;

public class MainMenuButton {
    private float posX;
    private float posY;
    private float width;
    private float height;
    private final String name;
    private final String subtitle;
    private final String icon;
    private final Runnable action;
    private float hoverProgress = 0.0f;

    public MainMenuButton(float posX, float posY, float width, float height, @NotNull String name, String subtitle, String icon, Runnable action) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.name = name;
        this.subtitle = subtitle;
        this.icon = icon;
        this.action = action;
    }

    public void setBounds(float posX, float posY, float width, float height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public void onRender(DrawContext context, float mouseX, float mouseY) {
        boolean hovered = Render2DEngine.isHovered(mouseX, mouseY, this.posX, this.posY, this.width, this.height);
        this.hoverProgress = Render2DEngine.scrollAnimate(hovered ? 1.0f : 0.0f, this.hoverProgress, 0.28f);
        float slideX = this.hoverProgress * 3.5f;
        float drawX = this.posX + slideX;
        float drawW = this.width - slideX;
        Color bgColor = Render2DEngine.interpolateColorC(new Color(20, 16, 32, 190), new Color(44, 30, 72, 245), this.hoverProgress);
        Render2DEngine.drawRound(context.getMatrices(), drawX, this.posY, drawW, this.height, 7.0f, bgColor);
        if (this.hoverProgress > 0.02f) {
            Color barColor = Render2DEngine.interpolateColorC(new Color(0, 210, 255), new Color(155, 89, 182), this.hoverProgress);
            Render2DEngine.drawRound(context.getMatrices(), drawX + 2.0f, this.posY + 4.0f, 3.0f, this.height - 8.0f, 1.5f, Render2DEngine.applyOpacity(barColor, this.hoverProgress));
        }
        float textStartX = drawX + 12.0f;
        if (this.icon != null && !this.icon.isEmpty()) {
            Color iconColor = Render2DEngine.interpolateColorC(new Color(140, 135, 160, 200), new Color(0, 210, 255, 255), this.hoverProgress);
            FontRenderers.sf_bold.drawString(context.getMatrices(), this.icon, (double)textStartX, (double)(this.posY + this.height / 2.0f - 4.5f), iconColor.getRGB());
            textStartX += 16.0f;
        }
        Color textColor = Render2DEngine.interpolateColorC(new Color(215, 215, 225), Color.WHITE, this.hoverProgress);
        if (this.subtitle != null && !this.subtitle.isEmpty()) {
            FontRenderers.sf_bold.drawString(context.getMatrices(), this.name, (double)textStartX, (double)(this.posY + 5.0f), textColor.getRGB());
            FontRenderers.sf_medium_mini.drawString(context.getMatrices(), this.subtitle, (double)textStartX, (double)(this.posY + 19.0f), Render2DEngine.applyOpacity(new Color(135, 130, 155).getRGB(), 0.75f + this.hoverProgress * 0.25f));
        } else {
            FontRenderers.sf_bold.drawString(context.getMatrices(), this.name, (double)textStartX, (double)(this.posY + this.height / 2.0f - 4.5f), textColor.getRGB());
        }
    }

    public boolean onClick(int mouseX, int mouseY) {
        boolean hovered = Render2DEngine.isHovered(mouseX, mouseY, this.posX, this.posY, this.width, this.height);
        if (hovered && this.action != null) {
            this.action.run();
            return true;
        }
        return false;
    }
}

