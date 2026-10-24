/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.RotationAxis
 */
package thunder.hack.gui.clickui.impl;

import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import thunder.hack.core.Managers;
import thunder.hack.gui.clickui.AbstractElement;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.SettingGroup;
import thunder.hack.utility.render.TextureStorage;
import thunder.hack.utility.render.animation.AnimationUtility;

public class ParentElement
extends AbstractElement {
    private final Setting<SettingGroup> parentSetting;
    private float animation;

    public ParentElement(Setting setting) {
        super(setting);
        this.parentSetting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        MatrixStack matrixStack = context.getMatrices();
        float tx = this.x + this.width - 11.0f;
        float ty = this.y + 7.5f;
        this.animation = AnimationUtility.fast(this.animation, this.getParentSetting().getValue().isExtended() ? 0.0f : 1.0f, 15.0f);
        matrixStack.push();
        matrixStack.translate(tx, ty, 0.0f);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-180.0f * this.animation));
        matrixStack.translate(-tx, -ty, 0.0f);
        matrixStack.translate(this.x + this.width - 14.0f, this.y + 4.5f, 0.0f);
        context.drawTexture(TextureStorage.guiArrow, 0, 0, 0.0f, 0.0f, 6, 6, 6, 6);
        matrixStack.translate(-(this.x + this.width - 14.0f), -(this.y + 4.5f), 0.0f);
        matrixStack.pop();
        FontRenderers.sf_medium_mini.drawString(matrixStack, this.setting.getName(), (double)(this.x + 6.0f + (float)(6 * this.getParentSetting().getValue().getHierarchy())), (double)(this.y + this.height / 2.0f - 1.0f), new Color(-1).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.hovered) {
            this.getParentSetting().getValue().setExtended(!this.getParentSetting().getValue().isExtended());
            if (this.getParentSetting().getValue().isExtended()) {
                Managers.SOUND.playSwipeIn();
            } else {
                Managers.SOUND.playSwipeOut();
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    public Setting<SettingGroup> getParentSetting() {
        return this.parentSetting;
    }
}

