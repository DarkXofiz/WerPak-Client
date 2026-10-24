/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.util.math.MatrixStack
 *  org.lwjgl.glfw.GLFW
 */
package thunder.hack.gui.thundergui.components;

import java.awt.Color;
import java.util.Objects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import thunder.hack.ThunderHack;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.thundergui.ThunderGui;
import thunder.hack.gui.thundergui.components.SettingElement;
import thunder.hack.setting.Setting;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;

public class SliderComponent
extends SettingElement {
    private final float min;
    private final float max;
    public boolean listening;
    public String Stringnumber = "";
    private float animation;
    private double stranimation;
    private boolean dragging;

    public SliderComponent(Setting setting) {
        super(setting);
        this.min = ((Number)setting.getMin()).floatValue();
        this.max = ((Number)setting.getMax()).floatValue();
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && !str.isEmpty()) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.getY() > (float)(ThunderGui.getInstance().main_posY + ThunderGui.getInstance().height) || this.getY() < (float)ThunderGui.getInstance().main_posY) {
            return;
        }
        FontRenderers.modules.drawString(stack, this.getSetting().getName(), (double)this.getX(), (double)(this.getY() + 5.0f), this.isHovered() ? -1 : new Color(-1325400065, true).getRGB());
        double currentPos = (((Number)this.setting.getValue()).floatValue() - this.min) / (this.max - this.min);
        this.stranimation += ((double)(((Number)this.setting.getValue()).floatValue() * 100.0f / 100.0f) - this.stranimation) / 2.0;
        this.animation = Render2DEngine.scrollAnimate(this.animation, (float)currentPos, 0.5f);
        Color color = new Color(-1973791);
        Render2DEngine.drawRound(stack, this.x + 54.0f, this.y + this.height - 8.0f, 90.0f, 1.0f, 0.5f, new Color(-15856114));
        Render2DEngine.drawRound(stack, this.x + 54.0f, this.y + this.height - 8.0f, 90.0f * this.animation, 1.0f, 0.5f, color);
        Render2DEngine.drawRound(stack, this.x + 52.0f + 90.0f * this.animation, this.y + this.height - 9.5f, 4.0f, 4.0f, 1.5f, color);
        if ((float)mouseX > this.x + 154.0f && (float)mouseX < this.x + 176.0f && (float)mouseY > this.y + this.height - 11.0f && (float)mouseY < this.y + this.height - 4.0f) {
            Render2DEngine.drawRound(stack, this.x + 154.0f, this.y + this.height - 11.0f, 22.0f, 7.0f, 0.5f, new Color(82, 57, 100, 178));
        } else {
            Render2DEngine.drawRound(stack, this.x + 154.0f, this.y + this.height - 11.0f, 22.0f, 7.0f, 0.5f, new Color(50, 35, 60, 178));
        }
        if (!this.listening) {
            if (this.setting.getValue() instanceof Float) {
                FontRenderers.modules.drawString(stack, String.valueOf(MathUtility.round(((Float)this.setting.getValue()).floatValue(), 2)), (double)(this.x + 156.0f), (double)(this.y + this.height - 9.0f), new Color(-1157627905, true).getRGB());
            }
            if (this.setting.getValue() instanceof Integer) {
                FontRenderers.modules.drawString(stack, String.valueOf(this.setting.getValue()), (double)(this.x + 156.0f), (double)(this.y + this.height - 9.0f), new Color(-1157627905, true).getRGB());
            }
        } else if (Objects.equals(this.Stringnumber, "")) {
            FontRenderers.modules.drawString(stack, "...", (double)(this.x + 156.0f), (double)(this.y + this.height - 9.0f), new Color(-1157627905, true).getRGB());
        } else {
            FontRenderers.modules.drawString(stack, this.Stringnumber, (double)(this.x + 156.0f), (double)(this.y + this.height - 9.0f), new Color(-1157627905, true).getRGB());
        }
        this.animation = MathUtility.clamp(this.animation, 0.0f, 1.0f);
        if (this.dragging) {
            this.setValue(mouseX, this.x + 54.0f, 90.0);
        }
    }

    private void setValue(int mouseX, double x, double width) {
        double diff = ((Number)this.setting.getMax()).floatValue() - ((Number)this.setting.getMin()).floatValue();
        double percentBar = MathHelper.clamp((double)(((double)mouseX - x) / width), (double)0.0, (double)1.0);
        double value = (double)((Number)this.setting.getMin()).floatValue() + percentBar * diff;
        if (this.setting.getValue() instanceof Float) {
            this.setting.setValue(Float.valueOf((float)value));
        } else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue((int)value);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.getY() > (float)(ThunderGui.getInstance().main_posY + ThunderGui.getInstance().height) || this.getY() < (float)ThunderGui.getInstance().main_posY) {
            return;
        }
        if ((float)mouseX > this.x + 154.0f && (float)mouseX < this.x + 176.0f && (float)mouseY > this.y + this.height - 11.0f && (float)mouseY < this.y + this.height - 4.0f) {
            this.Stringnumber = "";
            this.listening = true;
        } else if (button == 0 && this.hovered) {
            this.dragging = true;
        }
        if (this.listening) {
            ThunderHack.currentKeyListener = ThunderHack.KeyListening.Sliders;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        this.dragging = false;
    }

    @Override
    public void resetAnimation() {
        this.dragging = false;
        this.animation = 0.0f;
        this.stranimation = 0.0;
    }

    @Override
    public void keyTyped(String typedChar, int keyCode) {
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
                    this.Stringnumber = SliderComponent.removeLastChar(this.Stringnumber);
                    return;
                }
            }
            this.Stringnumber = this.Stringnumber + GLFW.glfwGetKeyName((int)keyCode, (int)0);
        }
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

    @Override
    public void checkMouseWheel(float value) {
        super.checkMouseWheel(value);
        if (!this.isHovered()) {
            return;
        }
        ThunderGui.scroll_lock = true;
        if (value < 0.0f) {
            if (this.setting.getValue() instanceof Float) {
                this.setting.setValue(Float.valueOf(((Float)this.setting.getValue()).floatValue() + 0.01f));
            } else if (this.setting.getValue() instanceof Integer) {
                this.setting.setValue((Integer)this.setting.getValue() + 1);
            }
        } else if (value > 0.0f) {
            if (this.setting.getValue() instanceof Float) {
                this.setting.setValue(Float.valueOf(((Float)this.setting.getValue()).floatValue() - 0.01f));
            } else if (this.setting.getValue() instanceof Integer) {
                this.setting.setValue((Integer)this.setting.getValue() - 1);
            }
        }
    }
}

