/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.gui.thundergui.components;

import java.awt.Color;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.thundergui.ThunderGui;
import thunder.hack.gui.thundergui.components.SettingElement;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.SettingGroup;
import thunder.hack.utility.render.Render2DEngine;

public class ParentComponent
extends SettingElement {
    public ParentComponent(Setting setting) {
        super(setting);
        SettingGroup settingGroup = (SettingGroup)setting.getValue();
        settingGroup.setExtended(true);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.getY() > (float)(ThunderGui.getInstance().main_posY + ThunderGui.getInstance().height) || this.getY() < (float)ThunderGui.getInstance().main_posY) {
            return;
        }
        FontRenderers.modules.drawCenteredString(stack, this.getSetting().getName(), (double)(this.getX() + this.width / 2.0f), (double)(this.getY() + 2.0f), new Color(-1325400065, true).getRGB());
        Render2DEngine.draw2DGradientRect(stack, this.getX() + 10.0f, this.getY() + 6.0f, this.getX() + this.width / 2.0f - 20.0f, this.getY() + 7.0f, new Color(0xFFFFFF, true), new Color(0xFFFFFF, true), new Color(-1325400065, true), new Color(-1325400065, true));
        Render2DEngine.draw2DGradientRect(stack, this.getX() + this.width / 2.0f + 20.0f, this.getY() + 6.0f, this.getX() + this.width - 10.0f, this.getY() + 7.0f, new Color(-1325400065, true), new Color(-1325400065, true), new Color(0xFFFFFF, true), new Color(0xFFFFFF, true));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.getY() > (float)(ThunderGui.getInstance().main_posY + ThunderGui.getInstance().height) || this.getY() < (float)ThunderGui.getInstance().main_posY) {
            return;
        }
        if (this.hovered) {
            SettingGroup settingGroup = (SettingGroup)this.setting.getValue();
            settingGroup.setExtended(!settingGroup.isExtended());
        }
    }
}

