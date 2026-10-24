/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.RotationAxis
 *  org.lwjgl.glfw.GLFW
 */
package thunder.hack.gui.clickui.impl;

import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.glfw.GLFW;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.IManager;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.clickui.AbstractElement;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;
import thunder.hack.utility.render.animation.AnimationUtility;

public class BooleanParentElement
extends AbstractElement {
    private final Setting<BooleanSettingGroup> parentSetting;
    float animation;
    float arrowAnimation;

    public BooleanParentElement(Setting setting) {
        super(setting);
        this.parentSetting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        MatrixStack matrixStack = context.getMatrices();
        float tx = this.x + this.width - 11.0f;
        float ty = this.y + 7.5f;
        this.arrowAnimation = AnimationUtility.fast(this.arrowAnimation, this.getParentSetting().getValue().isExtended() ? 0.0f : 1.0f, 15.0f);
        matrixStack.push();
        matrixStack.translate(tx, ty, 0.0f);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-180.0f * this.arrowAnimation));
        matrixStack.translate(-tx, -ty, 0.0f);
        matrixStack.translate(this.x + this.width - 14.0f, this.y + 4.5f, 0.0f);
        context.drawTexture(TextureStorage.guiArrow, 0, 0, 0.0f, 0.0f, 6, 6, 6, 6);
        matrixStack.translate(-(this.x + this.width - 14.0f), -(this.y + 4.5f), 0.0f);
        matrixStack.pop();
        FontRenderers.sf_medium_mini.drawString(matrixStack, this.setting.getName(), (double)(this.x + 6.0f), (double)(this.y + this.height / 2.0f - 1.0f), new Color(-1).getRGB());
        this.animation = AnimationUtility.fast(this.animation, this.getParentSetting().getValue().isEnabled() ? 1.0f : 0.0f, 15.0f);
        float paddingX = 7.0f * this.animation;
        Color color = HudEditor.getColor(0);
        Render2DEngine.drawRound(context.getMatrices(), this.x + this.width - 36.0f, this.y + this.height / 2.0f - 4.0f, 15.0f, 8.0f, 1.0f, paddingX > 4.0f ? color : new Color(0x28FFFFFF, true));
        Render2DEngine.drawRound(context.getMatrices(), this.x + this.width - 35.0f + paddingX, this.y + this.height / 2.0f - 3.0f, 6.0f, 6.0f, 1.0f, new Color(-1));
        if (7.0f * this.animation > 4.0f) {
            FontRenderers.sf_bold_mini.drawString(context.getMatrices(), "v", (double)(this.x + this.width - 34.0f), (double)(this.y + this.height / 2.0f - 2.0f), new Color(-1).getRGB());
        } else {
            FontRenderers.sf_bold_mini.drawString(context.getMatrices(), "x", (double)(this.x + this.width - 27.0f), (double)(this.y + this.height / 2.0f - 2.0f), new Color(-1).getRGB());
        }
        if (Render2DEngine.isHovered(mouseX, mouseY, this.x + this.width - 36.0f, this.y + this.height / 2.0f - 4.0f, 15.0, 8.0)) {
            if (GLFW.glfwGetPlatform() != 393219) {
                GLFW.glfwSetCursor((long)IManager.mc.getWindow().getHandle(), (long)GLFW.glfwCreateStandardCursor((int)221188));
            }
            ClickGUI.anyHovered = true;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.hovered) {
            if (button == 0) {
                this.getParentSetting().getValue().setEnabled(!this.getParentSetting().getValue().isEnabled());
                Managers.SOUND.playBoolean();
            } else {
                this.getParentSetting().getValue().setExtended(!this.getParentSetting().getValue().isExtended());
                if (this.getParentSetting().getValue().isExtended()) {
                    Managers.SOUND.playSwipeIn();
                } else {
                    Managers.SOUND.playSwipeOut();
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    public Setting<BooleanSettingGroup> getParentSetting() {
        return this.parentSetting;
    }
}

