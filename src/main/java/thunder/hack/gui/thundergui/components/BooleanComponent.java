/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.gui.thundergui.components;

import java.awt.Color;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.thundergui.ThunderGui;
import thunder.hack.gui.thundergui.components.SettingElement;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

public class BooleanComponent
extends SettingElement {
    float animation = 0.0f;

    public BooleanComponent(Setting setting) {
        super(setting);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.getY() > (float)(ThunderGui.getInstance().main_posY + ThunderGui.getInstance().height) || this.getY() < (float)ThunderGui.getInstance().main_posY) {
            return;
        }
        FontRenderers.modules.drawString(stack, this.getSetting().getName(), (double)this.getX(), (double)(this.getY() + 5.0f), this.isHovered() ? -1 : new Color(-1325400065, true).getRGB());
        this.animation = AnimationUtility.fast(this.animation, (Boolean)this.setting.getValue() != false ? 1.0f : 0.0f, 15.0f);
        double paddingX = 7.0f * this.animation;
        Color color = HudEditor.getColor(1);
        Render2DEngine.drawRound(stack, this.x + this.width - 18.0f, this.y + this.height / 2.0f - 4.0f, 15.0f, 8.0f, 4.0f, paddingX > 4.0 ? color : new Color(-5066319));
        Render2DEngine.drawRound(stack, (float)((double)(this.x + this.width - 17.0f) + paddingX), this.y + this.height / 2.0f - 3.0f, 6.0f, 6.0f, 3.0f, new Color(-1));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.getY() > (float)(ThunderGui.getInstance().main_posY + ThunderGui.getInstance().height) || this.getY() < (float)ThunderGui.getInstance().main_posY) {
            return;
        }
        if (this.isHovered()) {
            this.setting.setValue((Boolean)this.setting.getValue() == false);
        }
    }
}

