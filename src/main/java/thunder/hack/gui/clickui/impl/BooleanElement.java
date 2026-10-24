/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  org.lwjgl.glfw.GLFW
 */
package thunder.hack.gui.clickui.impl;

import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.IManager;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.clickui.AbstractElement;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

public class BooleanElement
extends AbstractElement {
    float animation = 0.0f;
    float animation2 = 0.0f;

    public BooleanElement(Setting setting) {
        super(setting);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        Render2DEngine.drawRound(context.getMatrices(), this.x + this.width - 21.0f, this.y + this.height / 2.0f - 4.0f, 15.0f, 8.0f, 1.0f, 7.0f * this.animation > 4.0f ? HudEditor.getColor(0) : new Color(0x28FFFFFF, true));
        this.animation = AnimationUtility.fast(this.animation, (Boolean)this.setting.getValue() != false ? 1.0f : 0.0f, 20.0f);
        this.animation2 = AnimationUtility.fast(this.animation2, (Boolean)this.setting.getValue() != false ? 1.0f : 0.0f, 8.0f);
        Render2DEngine.drawRound(context.getMatrices(), this.x + this.width - 20.0f + 7.0f * this.animation, this.y + this.height / 2.0f - 3.0f, 6.0f, 6.0f, 1.0f, new Color(-1));
        Render2DEngine.drawRound(context.getMatrices(), this.x + this.width - 20.0f + 7.0f * this.animation2, this.y + this.height / 2.0f - 3.0f, 6.0f, 6.0f, 1.0f, new Color(-1));
        if (7.0f * this.animation > 4.0f) {
            FontRenderers.sf_bold_mini.drawString(context.getMatrices(), "v", (double)(this.x + this.width - 19.0f), (double)(this.y + this.height / 2.0f - 2.0f), new Color(-1).getRGB());
        } else {
            FontRenderers.sf_bold_mini.drawString(context.getMatrices(), "x", (double)(this.x + this.width - 12.0f), (double)(this.y + this.height / 2.0f - 2.0f), new Color(-1).getRGB());
        }
        if (Render2DEngine.isHovered(mouseX, mouseY, this.x + this.width - 21.0f, this.y + this.height / 2.0f - 4.0f, 15.0, 8.0)) {
            if (GLFW.glfwGetPlatform() != 393219) {
                GLFW.glfwSetCursor((long)IManager.mc.getWindow().getHandle(), (long)GLFW.glfwCreateStandardCursor((int)221188));
            }
            ClickGUI.anyHovered = true;
        }
        FontRenderers.sf_medium_mini.drawString(context.getMatrices(), this.setting.getName(), (double)((this.setting.group != null ? 2.0f : 0.0f) + (this.x + 6.0f)), (double)(this.y + this.height / 2.0f - 3.0f + 2.0f), new Color(-1).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.hovered && button == 0) {
            this.setting.setValue((Boolean)this.setting.getValue() == false);
            Managers.SOUND.playBoolean();
        }
        super.mouseClicked(mouseX, mouseY, button);
    }
}

