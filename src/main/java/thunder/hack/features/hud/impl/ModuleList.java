/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.hud.impl;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.Formatting;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.core.Managers;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;

public class ModuleList
extends HudElement {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.ColorText);
    private final Setting<Integer> gste = new Setting<Integer>("GS", 30, 1, 50);
    private final Setting<Boolean> glow = new Setting<Boolean>("glow", false);
    private final Setting<Boolean> hrender = new Setting<Boolean>("HideHud", true);
    private final Setting<Boolean> hhud = new Setting<Boolean>("HideRender", true);
    private final Setting<ColorSetting> color3 = new Setting<ColorSetting>("RectColor", new ColorSetting(-16777216));
    private final Setting<ColorSetting> color4 = new Setting<ColorSetting>("SideRectColor", new ColorSetting(-16777216));

    public ModuleList() {
        super("ArrayList", 50, 30);
    }

    @Override
    public void onRender2D(DrawContext context) {
        int stringWidth;
        Color color1;
        List<Module> list;
        super.onRender2D(context);
        boolean reverse = this.getPosX() > (float)mc.getWindow().getScaledWidth() / 2.0f;
        int offset = 0;
        float maxWidth = 0.0f;
        float reversedX = this.getPosX();
        try {
            list = Managers.MODULE.getEnabledModules().stream().sorted(Comparator.comparing(module -> Float.valueOf(FontRenderers.modules.getStringWidth(module.getFullArrayString()) * -1.0f))).toList();
        }
        catch (IllegalArgumentException ex) {
            return;
        }
        for (Module module2 : list) {
            if (!this.shouldRender(module2)) continue;
            color1 = HudEditor.getColor(offset);
            stringWidth = (int)(FontRenderers.modules.getStringWidth(module2.getDisplayName() + String.valueOf(Formatting.GRAY) + (String)(module2.getDisplayInfo() != null ? " [" + String.valueOf(Formatting.WHITE) + module2.getDisplayInfo() + String.valueOf(Formatting.GRAY) + "]" : "")) + 3.0f);
            if (this.glow.getValue().booleanValue()) {
                Render2DEngine.drawBlurredShadow(context.getMatrices(), reverse ? reversedX - (float)stringWidth - 3.0f : this.getPosX(), this.getPosY() + (float)offset - 1.0f, stringWidth + 4, 9.0f, this.gste.getValue(), color1);
            }
            if ((float)stringWidth > maxWidth) {
                maxWidth = stringWidth;
            }
            offset += 9;
        }
        offset = 0;
        for (Module module2 : list) {
            if (!this.shouldRender(module2)) continue;
            stringWidth = (int)(FontRenderers.modules.getStringWidth(module2.getDisplayName() + String.valueOf(Formatting.GRAY) + (String)(module2.getDisplayInfo() != null ? " [" + String.valueOf(Formatting.WHITE) + module2.getDisplayInfo() + String.valueOf(Formatting.GRAY) + "]" : "")) + 3.0f);
            color1 = HudEditor.getColor(offset);
            if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
                Render2DEngine.drawRoundedBlur(context.getMatrices(), reverse ? reversedX - (float)stringWidth : this.getPosX(), this.getPosY() + (float)offset, (float)stringWidth + 1.0f, 9.0f, 2.0f, HudEditor.blurColor.getValue().getColorObject());
            } else {
                Render2DEngine.drawRect(context.getMatrices(), reverse ? reversedX - (float)stringWidth : this.getPosX(), this.getPosY() + (float)offset, (float)stringWidth + 1.0f, 9.0f, this.mode.getValue() == Mode.ColorRect ? color1 : this.color3.getValue().getColorObject());
                Render2DEngine.drawRect(context.getMatrices(), reverse ? reversedX + 1.0f : this.getPosX() - 2.0f, this.getPosY() + (float)offset, 2.0f, 9.0f, this.mode.getValue() == Mode.ColorRect ? this.color4.getValue().getColorObject() : color1);
            }
            FontRenderers.modules.drawString(context.getMatrices(), module2.getDisplayName() + String.valueOf(Formatting.GRAY) + (String)(module2.getDisplayInfo() != null ? " [" + String.valueOf(Formatting.WHITE) + module2.getDisplayInfo() + String.valueOf(Formatting.GRAY) + "]" : ""), reverse ? (double)(reversedX - (float)stringWidth + 2.0f) : (double)(this.getPosX() + 3.0f), (double)(this.getPosY() + 3.0f + (float)offset), this.mode.getValue() == Mode.ColorRect ? -1 : color1.getRGB());
            offset += 9;
        }
        this.setBounds(this.getPosX(), this.getPosY(), (int)maxWidth * (reverse ? -1 : 1), offset);
    }

    private boolean shouldRender(Module m) {
        return !(!m.isDrawn() || this.hrender.getValue() != false && m.getCategory() == Module.Category.RENDER || this.hhud.getValue() != false && m.getCategory() == Module.Category.HUD);
    }

    private static enum Mode {
        ColorText,
        ColorRect;

    }
}

