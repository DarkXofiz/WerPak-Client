/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.StringHelper
 *  net.minecraft.client.util.InputUtil
 *  org.lwjgl.glfw.GLFW
 */
package thunder.hack.gui.clickui.impl;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.StringHelper;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import thunder.hack.ThunderHack;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.clickui.AbstractButton;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.clickui.impl.SliderElement;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.Render2DEngine;

public class SearchBar
extends AbstractButton {
    public static String moduleName = "";
    public static boolean listening;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        Render2DEngine.drawGuiBase(context.getMatrices(), this.x + 4.0f, this.y + 1.0f, this.width - 8.0f, this.height - 2.0f, 1.0f, Render2DEngine.isHovered(mouseX, mouseY, this.x, this.y, this.width, this.height) ? 0.8f : 0.0f);
        if (!listening) {
            FontRenderers.sf_medium.drawGradientString(context.getMatrices(), "Search...", this.x + 7.0f, this.y + this.height / 2.0f - 3.0f, 2);
        } else {
            FontRenderers.sf_medium.drawGradientString(context.getMatrices(), moduleName + (Module.mc.player == null || Module.mc.player.age / 10 % 2 == 0 ? " " : "_"), this.x + 7.0f, this.y + 5.0f, 2);
        }
        if (Render2DEngine.isHovered(mouseX, mouseY, this.x, this.y, this.width, this.height)) {
            if (GLFW.glfwGetPlatform() != 393219) {
                GLFW.glfwSetCursor((long)Module.mc.getWindow().getHandle(), (long)GLFW.glfwCreateStandardCursor((int)221186));
            }
            ClickGUI.anyHovered = true;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        boolean isHovered = Render2DEngine.isHovered(mouseX, mouseY, this.x, this.y, this.width, this.height);
        if (isHovered) {
            listening = true;
        } else {
            moduleName = "";
            listening = false;
        }
        if (listening) {
            ThunderHack.currentKeyListener = ThunderHack.KeyListening.Search;
        }
    }

    @Override
    public void charTyped(char key, int keyCode) {
        if (StringHelper.isValidChar((char)key) && listening) {
            moduleName = moduleName + key;
        }
    }

    @Override
    public void keyTyped(int keyCode) {
        super.keyTyped(keyCode);
        if (keyCode == 70 && (InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)341) || InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)345))) {
            listening = !listening;
            ThunderHack.currentKeyListener = ThunderHack.KeyListening.Search;
            return;
        }
        if (ThunderHack.currentKeyListener != ThunderHack.KeyListening.Search) {
            return;
        }
        if (listening) {
            switch (keyCode) {
                case 256: 
                case 257: {
                    listening = false;
                    moduleName = "";
                    break;
                }
                case 259: {
                    moduleName = SliderElement.removeLastChar(moduleName);
                    break;
                }
                case 32: {
                    moduleName = moduleName + " ";
                }
            }
        }
    }
}

