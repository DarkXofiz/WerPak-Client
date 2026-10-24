/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.StringHelper
 *  org.lwjgl.glfw.GLFW
 */
package thunder.hack.gui.clickui.impl;

import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.StringHelper;
import org.lwjgl.glfw.GLFW;
import thunder.hack.ThunderHack;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.clickui.AbstractElement;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.clickui.impl.SliderElement;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;

public class StringElement
extends AbstractElement {
    public boolean listening;
    private String currentString = "";

    public StringElement(Setting setting) {
        super(setting);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        Render2DEngine.drawRect(context.getMatrices(), this.getX() + 5.0f, this.getY() + 2.0f, this.getWidth() - 11.0f, 10.0f, new Color(-1811939328, true));
        FontRenderers.sf_medium_mini.drawString(context.getMatrices(), (String)(this.listening ? this.currentString + (Module.mc.player == null || Module.mc.player.age % 5 == 0 ? "_" : "") : (String)this.setting.getValue()), (double)(this.x + 6.0f), (double)(this.y + this.height / 2.0f), -1);
        if (Render2DEngine.isHovered(mouseX, mouseY, this.getX() + 5.0f, this.getY() + 2.0f, this.getWidth() - 11.0f, 10.0)) {
            if (GLFW.glfwGetPlatform() != 393219) {
                GLFW.glfwSetCursor((long)Module.mc.getWindow().getHandle(), (long)GLFW.glfwCreateStandardCursor((int)221186));
            }
            ClickGUI.anyHovered = true;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.hovered && button == 0) {
            boolean bl = this.listening = !this.listening;
        }
        if (this.listening) {
            ThunderHack.currentKeyListener = ThunderHack.KeyListening.Strings;
            this.currentString = (String)this.setting.getValue();
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void charTyped(char key, int keyCode) {
        if (StringHelper.isValidChar((char)key)) {
            this.currentString = this.currentString + key;
        }
    }

    @Override
    public void keyTyped(int keyCode) {
        if (ThunderHack.currentKeyListener != ThunderHack.KeyListening.Strings) {
            return;
        }
        if (this.listening) {
            switch (keyCode) {
                case 256: {
                    break;
                }
                case 257: {
                    this.setting.setValue(this.currentString == null || this.currentString.isEmpty() ? this.setting.getDefaultValue() : this.currentString);
                    this.currentString = "";
                    this.listening = !this.listening;
                    break;
                }
                case 259: {
                    this.currentString = SliderElement.removeLastChar(this.currentString);
                    break;
                }
                case 32: {
                    this.currentString = this.currentString + " ";
                }
            }
        }
    }
}

