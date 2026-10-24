/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.gui.clickui.impl;

import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.ThunderHack;
import thunder.hack.gui.clickui.AbstractElement;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;

public class ColorPickerElement
extends AbstractElement {
    private float hue;
    private float saturation;
    private float brightness;
    private int alpha;
    private boolean afocused;
    private boolean hfocused;
    private boolean sbfocused;
    private float spos;
    private float bpos;
    private float hpos;
    private float apos;
    private Color prevColor;
    private boolean firstInit;
    private boolean extended;
    private final Setting colorSetting;

    public ColorSetting getColorSetting() {
        return (ColorSetting)this.colorSetting.getValue();
    }

    public ColorPickerElement(Setting setting) {
        super(setting);
        this.colorSetting = setting;
        this.prevColor = this.getColorSetting().getColorObject();
        this.updatePos();
        this.firstInit = true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrixStack = context.getMatrices();
        boolean colorHovered = Render2DEngine.isHovered(mouseX, mouseY, this.x, this.y + 5.0f, 90.0, 7.0);
        FontRenderers.sf_medium_mini.drawString(matrixStack, this.setting.getName(), (double)(this.x + 6.0f), (double)(this.y + 8.0f), new Color(-1).getRGB());
        Render2DEngine.drawBlurredShadow(matrixStack, this.x + this.width - 22.0f, this.y + 5.0f, 14.0f, 7.0f, colorHovered ? 6 : 10, this.getColorSetting().getColorObject());
        if (colorHovered) {
            Render2DEngine.drawRound(matrixStack, this.x + this.width - 22.5f, this.y + 4.5f, 15.0f, 8.0f, 1.0f, this.getColorSetting().getColorObject());
        } else {
            Render2DEngine.drawRound(matrixStack, this.x + this.width - 22.0f, this.y + 5.0f, 14.0f, 7.0f, 1.0f, this.getColorSetting().getColorObject());
        }
        if (!this.extended) {
            return;
        }
        boolean rainbowHovered = Render2DEngine.isHovered(mouseX, mouseY, this.x + 36.0f, this.y + 54.0f, 24.0, 7.0);
        boolean copyHovered = Render2DEngine.isHovered(mouseX, mouseY, this.x + 9.0f, this.y + 54.0f, 24.0, 7.0);
        boolean pasteHovered = Render2DEngine.isHovered(mouseX, mouseY, this.x + 63.0f, this.y + 54.0f, 24.0, 7.0);
        boolean dark = Render2DEngine.isDark(ThunderHack.copy_color);
        boolean dark2 = Render2DEngine.isDark(this.getColorSetting().getColorObject());
        Render2DEngine.drawRect(matrixStack, this.x + 9.0f, this.y + 54.0f, 24.0f, 7.0f, new Color(0x424242));
        FontRenderers.sf_medium_mini.drawString(matrixStack, "\u0421opy", (double)(this.x + 13.0f), (double)(this.y + 56.5f), copyHovered ? new Color(-1543503873, true).getRGB() : Color.WHITE.getRGB());
        Render2DEngine.drawRect(matrixStack, this.x + 36.0f, this.y + 54.0f, 24.0f, 7.0f, this.getColorSetting().isRainbow() ? this.getColorSetting().getColorObject() : new Color(0x424242));
        FontRenderers.sf_medium_mini.drawString(matrixStack, "RB", (double)(this.x + 44.0f), (double)(this.y + 56.5f), rainbowHovered ? new Color(-1543503873, true).getRGB() : (dark2 ? Color.WHITE.getRGB() : Color.BLACK.getRGB()));
        Render2DEngine.drawRect(matrixStack, this.x + 63.0f, this.y + 54.0f, 24.0f, 7.0f, ThunderHack.copy_color);
        FontRenderers.sf_medium_mini.drawString(matrixStack, "Paste", (double)(this.x + 67.0f), (double)(this.y + 56.5f), pasteHovered ? new Color(-1543503873, true).getRGB() : (dark ? Color.WHITE.getRGB() : Color.BLACK.getRGB()));
        this.renderPicker(matrixStack, mouseX, mouseY, this.getColorSetting().getColorObject());
    }

    @Override
    public float getHeight() {
        return this.extended ? 66.0f : 15.0f;
    }

    private void renderPicker(MatrixStack matrixStack, int mouseX, int mouseY, Color color) {
        double cx = this.x + 6.0f;
        double cy = this.y + 16.0f;
        double cw = this.width - 38.0f;
        double ch = this.height - 30.0f;
        if (this.prevColor != this.getColorSetting().getColorObject()) {
            this.updatePos();
            this.prevColor = this.getColorSetting().getColorObject();
        }
        if (this.firstInit) {
            this.spos = (float)(cx + cw - (cw - cw * (double)this.saturation));
            this.bpos = (float)(cy + (ch - ch * (double)this.brightness));
            this.hpos = (float)(cy + (ch - 3.0 + (ch - 3.0) * (double)this.hue));
            this.apos = (float)(cy + (ch - 3.0 - (ch - 3.0) * (double)((float)this.alpha / 255.0f)));
            this.firstInit = false;
        }
        this.spos = Render2DEngine.scrollAnimate(this.spos, (float)(cx + cw - (cw - cw * (double)this.saturation)), 0.6f);
        this.bpos = Render2DEngine.scrollAnimate(this.bpos, (float)(cy + (ch - ch * (double)this.brightness)), 0.6f);
        this.hpos = Render2DEngine.scrollAnimate(this.hpos, (float)(cy + (ch - 3.0 + (ch - 3.0) * (double)this.hue)), 0.6f);
        this.apos = Render2DEngine.scrollAnimate(this.apos, (float)(cy + (ch - 3.0 - (ch - 3.0) * (double)((float)this.alpha / 255.0f))), 0.6f);
        Color colorA = Color.getHSBColor(this.hue, 0.0f, 1.0f);
        Color colorB = Color.getHSBColor(this.hue, 1.0f, 1.0f);
        Color colorC = new Color(0, 0, 0, 0);
        Color colorD = new Color(0, 0, 0);
        Render2DEngine.horizontalGradient(matrixStack, (float)cx + 2.0f, (float)cy, (float)(cx + cw), (float)(cy + ch), colorA, colorB);
        Render2DEngine.verticalGradient(matrixStack, (float)(cx + 2.0), (float)cy, (float)(cx + cw), (float)(cy + ch), colorC, colorD);
        float i = 1.0f;
        while ((double)i < ch - 2.0) {
            float curHue = (float)(1.0 / (ch / (double)i));
            Render2DEngine.drawRect(matrixStack, (float)(cx + cw + 4.0), (float)(cy + (double)i), 8.0f, 1.0f, Color.getHSBColor(curHue, 1.0f, 1.0f));
            i += 1.0f;
        }
        Render2DEngine.drawRect(matrixStack, (float)(cx + cw + 17.0), (float)(cy + 1.0), 8.0f, (float)(ch - 3.0), new Color(-1));
        Render2DEngine.verticalGradient(matrixStack, (float)(cx + cw + 17.0), (float)(cy + (double)0.8f), (float)(cx + cw + 25.0), (float)(cy + ch - 2.0), new Color(color.getRed(), color.getGreen(), color.getBlue(), 255), new Color(0, 0, 0, 0));
        Render2DEngine.drawRect(matrixStack, (float)(cx + cw + 3.0), this.hpos + 0.5f, 10.0f, 1.0f, Color.WHITE);
        Render2DEngine.drawRect(matrixStack, (float)(cx + cw + 16.0), this.apos + 0.5f, 10.0f, 1.0f, Color.WHITE);
        Render2DEngine.drawRound(matrixStack, this.spos - 1.5f, this.bpos - 1.5f, 3.0f, 3.0f, 1.5f, new Color(-1));
        Color value = Color.getHSBColor(this.hue, this.saturation, this.brightness);
        if (this.sbfocused) {
            this.saturation = (float)((double)MathUtility.clamp((float)((double)mouseX - cx), 0.0f, (float)cw) / cw);
            this.brightness = (float)((ch - (double)MathUtility.clamp((float)((double)mouseY - cy), 0.0f, (float)ch)) / ch);
            value = Color.getHSBColor(this.hue, this.saturation, this.brightness);
            this.setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), this.alpha));
        }
        if (this.hfocused) {
            this.hue = (float)(-((ch - (double)MathUtility.clamp((float)((double)mouseY - cy), 0.0f, (float)ch)) / ch));
            value = Color.getHSBColor(this.hue, this.saturation, this.brightness);
            this.setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), this.alpha));
        }
        if (this.afocused) {
            this.alpha = (int)((ch - (double)MathUtility.clamp((float)((double)mouseY - cy), 0.0f, (float)ch)) / ch * 255.0);
            this.setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), this.alpha));
        }
    }

    private void updatePos() {
        float[] hsb = Color.RGBtoHSB(this.getColorSetting().getColorObject().getRed(), this.getColorSetting().getColorObject().getGreen(), this.getColorSetting().getColorObject().getBlue(), null);
        this.hue = -1.0f + hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        this.alpha = this.getColorSetting().getAlpha();
    }

    private void setColor(Color color) {
        this.getColorSetting().setColor(color.getRGB());
        this.prevColor = color;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        double cx = this.x + 4.0f;
        double cy = this.y + 17.0f;
        double cw = this.width - 34.0f;
        double ch = this.height - 30.0f;
        boolean rainbowHovered = Render2DEngine.isHovered(mouseX, mouseY, this.x + 36.0f, this.y + 54.0f, 24.0, 7.0);
        boolean copyHovered = Render2DEngine.isHovered(mouseX, mouseY, this.x + 9.0f, this.y + 54.0f, 24.0, 7.0);
        boolean pasteHovered = Render2DEngine.isHovered(mouseX, mouseY, this.x + 63.0f, this.y + 54.0f, 24.0, 7.0);
        boolean colorHovered = Render2DEngine.isHovered(mouseX, mouseY, this.x, this.y, 90.0, 11.0);
        if (colorHovered) {
            boolean bl = this.extended = !this.extended;
        }
        if (!this.extended) {
            return;
        }
        if (Render2DEngine.isHovered(mouseX, mouseY, cx + cw + 17.0, cy, 8.0, ch) && button == 0) {
            this.afocused = true;
        } else if (Render2DEngine.isHovered(mouseX, mouseY, cx + cw + 4.0, cy, 8.0, ch) && button == 0) {
            this.hfocused = true;
        } else if (Render2DEngine.isHovered(mouseX, mouseY, cx, cy, cw, ch) && button == 0) {
            this.sbfocused = true;
        } else if (rainbowHovered && button == 0) {
            this.getColorSetting().setRainbow(!this.getColorSetting().isRainbow());
        } else if (copyHovered) {
            ThunderHack.copy_color = this.getColorSetting().getColorObject();
        } else if (pasteHovered) {
            this.getColorSetting().setColor(ThunderHack.copy_color.getRGB());
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        this.hfocused = false;
        this.afocused = false;
        this.sbfocused = false;
    }

    @Override
    public void onClose() {
        this.hfocused = false;
        this.afocused = false;
        this.sbfocused = false;
    }
}

