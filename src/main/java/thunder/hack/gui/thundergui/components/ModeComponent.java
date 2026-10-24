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

public class ModeComponent
extends SettingElement {
    int progress = 0;
    private double wheight;
    private boolean open;

    public ModeComponent(Setting setting) {
        super(setting);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.getY() > (float)(ThunderGui.getInstance().main_posY + ThunderGui.getInstance().height) || this.getY() < (float)ThunderGui.getInstance().main_posY) {
            return;
        }
        FontRenderers.modules.drawString(stack, this.getSetting().getName(), (double)this.getX(), (double)(this.getY() + 5.0f), this.isHovered() ? -1 : new Color(-1325400065, true).getRGB());
        if (this.open) {
            double offsetY2 = 0.0;
            for (int i = 0; i <= this.setting.getModes().length - 1; ++i) {
                offsetY2 += 12.0;
            }
            Render2DEngine.drawRound(stack, this.x + 114.0f, this.y + 2.0f, 62.0f, (float)(11.0 + offsetY2), 0.5f, new Color(50, 35, 60, 121));
        }
        if ((float)mouseX > this.x + 114.0f && (float)mouseX < this.x + 176.0f && (float)mouseY > this.y + 2.0f && (float)mouseY < this.y + 15.0f) {
            Render2DEngine.drawRound(stack, this.x + 114.0f, this.y + 2.0f, 62.0f, 11.0f, 0.5f, new Color(82, 57, 100, 178));
        } else {
            Render2DEngine.drawRound(stack, this.x + 114.0f, this.y + 2.0f, 62.0f, 11.0f, 0.5f, new Color(50, 35, 60, 178));
        }
        FontRenderers.modules.drawString(stack, this.setting.currentEnumName(), (double)(this.x + 116.0f), (double)(this.y + 6.0f), new Color(-1325400065, true).getRGB());
        String arrow = switch (this.progress) {
            case 1 -> "o";
            case 2 -> "p";
            case 3 -> "q";
            case 4 -> "r";
            default -> "n";
        };
        FontRenderers.icons.drawString(stack, arrow, (double)((int)(this.x + 166.0f)), (double)((int)(this.y + 7.0f)), -1);
        double offsetY = 13.0;
        if (this.open) {
            Color color = HudEditor.getColor(1);
            for (int i = 0; i <= this.setting.getModes().length - 1; ++i) {
                FontRenderers.settings.drawString(stack, this.setting.getModes()[i], (double)(this.x + 116.0f), (double)((float)((double)(this.y + 5.0f) + offsetY)), this.setting.currentEnumName().equalsIgnoreCase(this.setting.getModes()[i]) ? color.getRGB() : -1);
                offsetY += 12.0;
            }
        }
    }

    @Override
    public void onTick() {
        if (this.open && this.progress > 0) {
            --this.progress;
        }
        if (!this.open && this.progress < 4) {
            ++this.progress;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.getY() > (float)(ThunderGui.getInstance().main_posY + ThunderGui.getInstance().height) || this.getY() < (float)ThunderGui.getInstance().main_posY) {
            return;
        }
        if ((float)mouseX > this.x + 114.0f && (float)mouseX < this.x + 176.0f && (float)mouseY > this.y + 2.0f && (float)mouseY < this.y + 15.0f) {
            this.open = !this.open;
            boolean bl = this.open;
        }
        if (this.open) {
            double offsetY = 0.0;
            for (int i = 0; i <= this.setting.getModes().length - 1; ++i) {
                if (Render2DEngine.isHovered(mouseX, mouseY, this.x, (double)this.y + this.wheight + offsetY, this.width, 12.0) && button == 0) {
                    this.setting.setEnumByNumber(i);
                }
                offsetY += 12.0;
            }
        }
    }

    public void setWHeight(double height) {
        this.wheight = height;
    }

    public boolean isOpen() {
        return this.open;
    }
}

