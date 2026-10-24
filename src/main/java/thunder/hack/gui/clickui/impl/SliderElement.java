/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.StringHelper
 *  net.minecraft.client.util.math.MatrixStack
 *  org.lwjgl.glfw.GLFW
 */
package thunder.hack.gui.clickui.impl;

import java.awt.Color;
import java.util.Objects;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.StringHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.IManager;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.clickui.AbstractElement;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;

public class SliderElement
extends AbstractElement {
    private final float min;
    private final float max;
    private float animation;
    private float prevValue;
    private boolean dragging;
    private boolean listening;
    public String Stringnumber = "";

    public SliderElement(Setting setting) {
        super(setting);
        this.min = ((Number)setting.getMin()).floatValue();
        this.max = ((Number)setting.getMax()).floatValue();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.animation = Render2DEngine.scrollAnimate(this.animation, (((Number)this.setting.getValue()).floatValue() - this.min) / (this.max - this.min), 0.4f);
        MatrixStack matrixStack = context.getMatrices();
        if (this.setting.group != null) {
            Render2DEngine.drawRect(context.getMatrices(), this.x + 4.0f, this.y, 1.0f, 18.0f, HudEditor.getColor(1));
        }
        if (!this.dragging) {
            FontRenderers.sf_medium_mini.drawString(matrixStack, this.setting.getName(), (double)((this.setting.group != null ? 2.0f : 0.0f) + this.x + 6.0f), (double)(this.y + 4.0f), new Color(-1).getRGB());
            FontRenderers.sf_medium_mini.drawString(matrixStack, (String)(this.listening ? (Objects.equals(this.Stringnumber, "") ? "..." : this.Stringnumber) : String.valueOf(this.setting.getValue())), (double)((int)(this.x + this.width - 6.0f - FontRenderers.sf_medium_mini.getStringWidth((String)(this.listening ? (Objects.equals(this.Stringnumber, "") ? "..." : this.Stringnumber) : String.valueOf(this.setting.getValue()))))), (double)(this.y + 5.0f), new Color(-1).getRGB());
        } else {
            if (this.animation > 0.2f) {
                FontRenderers.sf_medium_mini.drawString(matrixStack, String.valueOf(this.setting.getMin()), (double)(this.x + 6.0f), (double)(this.y + 4.0f), new Color(-1).getRGB());
            }
            if (this.animation < 0.8f) {
                FontRenderers.sf_medium_mini.drawString(matrixStack, String.valueOf(this.setting.getMax()), (double)(this.x + this.width - FontRenderers.sf_medium_mini.getStringWidth(String.valueOf(this.setting.getMax())) - 6.0f), (double)(this.y + 4.0f), new Color(-1).getRGB());
            }
            FontRenderers.sf_medium_mini.drawString(matrixStack, (String)(this.listening ? (Objects.equals(this.Stringnumber, "") ? "..." : this.Stringnumber) : String.valueOf(this.setting.getValue())), this.animation > 0.2f ? (this.animation < 0.8f ? (double)(this.x + 6.0f + (this.width - 14.0f) * this.animation - FontRenderers.sf_medium_mini.getStringWidth(String.valueOf(this.setting.getValue())) / 2.0f) : (double)(this.x + this.width - FontRenderers.sf_medium_mini.getStringWidth(String.valueOf(this.setting.getMax())) - 6.0f)) : (double)(this.x + 6.0f), (double)(this.y + 4.0f), new Color(-1).getRGB());
        }
        Render2DEngine.drawRect(matrixStack, this.x + 6.0f, this.y + this.height - 6.0f, this.width - 12.0f, 2.0f, new Color(0x28FFFFFF, true));
        Render2DEngine.draw2DGradientRect(matrixStack, this.x + 6.0f, this.y + this.height - 6.0f, this.x + 6.0f + (this.width - 12.0f) * this.animation, this.y + this.height - 4.0f, HudEditor.getColor(180), HudEditor.getColor(180), HudEditor.getColor(0), HudEditor.getColor(0));
        Render2DEngine.drawRect(matrixStack, this.x + 6.0f + (this.width - 14.0f) * this.animation, this.y + this.height - 7.5f, 2.0f, 5.0f, new Color(-1973791));
        this.animation = MathUtility.clamp(this.animation, 0.0f, 1.0f);
        if (this.dragging) {
            this.setValue(mouseX, this.x + 7.0f, this.width - 14.0f);
        }
        if (Render2DEngine.isHovered(mouseX, mouseY, this.x + 6.0f, this.y + this.height - 7.0f, this.width - 12.0f, 3.0)) {
            if (GLFW.glfwGetPlatform() != 393219) {
                GLFW.glfwSetCursor((long)IManager.mc.getWindow().getHandle(), (long)GLFW.glfwCreateStandardCursor((int)221189));
            }
            ClickGUI.anyHovered = true;
        }
    }

    private void setValue(int mouseX, double x, double width) {
        float value = Render2DEngine.interpolateFloat(((Number)this.setting.getMin()).floatValue(), ((Number)this.setting.getMax()).floatValue(), MathHelper.clamp((double)(((double)mouseX - x) / width), (double)0.0, (double)1.0));
        if (this.setting.getValue() instanceof Float) {
            this.setting.setValue(Float.valueOf(MathUtility.round2(value)));
        } else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue((int)value);
        }
        if (value != this.prevValue) {
            Managers.SOUND.playSlider();
        }
        this.prevValue = value;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0 && this.hovered) {
            this.dragging = true;
        } else if (this.hovered) {
            this.Stringnumber = "";
            this.listening = true;
        }
        if (this.listening) {
            ThunderHack.currentKeyListener = ThunderHack.KeyListening.Sliders;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        this.dragging = false;
    }

    @Override
    public void keyTyped(int keyCode) {
        if (ThunderHack.currentKeyListener != ThunderHack.KeyListening.Sliders) {
            return;
        }
        if (this.listening) {
            switch (keyCode) {
                case 256: {
                    this.listening = false;
                    this.Stringnumber = "";
                    return;
                }
                case 257: {
                    try {
                        this.searchNumber();
                    }
                    catch (Exception e) {
                        this.Stringnumber = "";
                        this.listening = false;
                    }
                    return;
                }
                case 259: {
                    this.Stringnumber = SliderElement.removeLastChar(this.Stringnumber);
                    return;
                }
                case 261: {
                    this.Stringnumber = "";
                    this.listening = false;
                    return;
                }
            }
        }
    }

    @Override
    public void charTyped(char key, int keyCode) {
        if (StringHelper.isValidChar((char)key)) {
            String k = key == '-' ? "-" : ".";
            try {
                k = String.valueOf(Integer.parseInt(String.valueOf(key)));
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.Stringnumber = this.Stringnumber + k;
        }
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && !str.isEmpty()) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    private void searchNumber() {
        if (this.setting.getValue() instanceof Float) {
            this.setting.setValue(Float.valueOf(this.Stringnumber));
            this.Stringnumber = "";
            this.listening = false;
        } else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue(Integer.valueOf(this.Stringnumber));
            this.Stringnumber = "";
            this.listening = false;
        }
    }
}

