/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.gui.clickui.impl;

import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.IManager;
import thunder.hack.gui.clickui.AbstractElement;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.windows.WindowsScreen;
import thunder.hack.gui.windows.impl.ItemSelectWindow;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ItemSelectSetting;

public class ItemSelectElement
extends AbstractElement {
    private final Setting<ItemSelectSetting> setting;

    public ItemSelectElement(Setting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        MatrixStack matrixStack = context.getMatrices();
        FontRenderers.icons.drawString(matrixStack, "H", (double)(this.x + this.width - 14.0f), (double)(this.y + 6.0f), new Color(-1250068, true).getRGB());
        FontRenderers.sf_medium_mini.drawString(matrixStack, this.setting.getName(), (double)(this.x + 6.0f), (double)(this.y + this.height / 2.0f - 1.0f), new Color(-1).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.hovered) {
            IManager.mc.setScreen((Screen)new WindowsScreen(new ItemSelectWindow(this.getItemSetting())));
            Managers.SOUND.playSwipeIn();
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    public Setting<ItemSelectSetting> getItemSetting() {
        return this.setting;
    }
}

