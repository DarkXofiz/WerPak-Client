/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ChatScreen
 */
package thunder.hack.features.hud.impl;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.hud.HudEditorGui;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

public class WerPakKeybinds
extends HudElement {
    public final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Neon);
    private float hAnimation = 95.0f;
    private float vAnimation = 50.0f;
    private float visibleValue = 0.0f;

    public WerPakKeybinds() {
        super("WerPakKeybinds", 100, 50);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        var activeModules = Managers.MODULE.modules.stream().filter(modx -> modx.isOn() && !Objects.equals(modx.getBind().getBind(), "None")).collect(Collectors.toList());
        boolean isEditing = WerPakKeybinds.mc.currentScreen instanceof ChatScreen || WerPakKeybinds.mc.currentScreen instanceof HudEditorGui;
        boolean shouldShow = !activeModules.isEmpty() || isEditing;
        this.visibleValue = AnimationUtility.fast(this.visibleValue, shouldShow ? 1.0f : 0.0f, 15.0f);
        if (!(this.visibleValue <= 0.01f)) {
            Color outlineColor;
            float x = this.getPosX();
            float y = this.getPosY();
            float panelWidth = 90.0f;
            float titleHeight = 20.0f;
            float itemHeight = 13.0f;
            float spacing = -2.5f;
            float statusPanelWidth = 14.0f;
            float mainPanelWidth = panelWidth - statusPanelWidth - 11.5f;
            int moduleCount = activeModules.isEmpty() && isEditing ? 3 : activeModules.size();
            float contentHeight = Math.max(16.0f, (float)moduleCount * (itemHeight + spacing) - spacing);
            float totalHeight = titleHeight + 7.0f + contentHeight;
            this.hAnimation = AnimationUtility.fast(this.hAnimation, panelWidth, 15.0f);
            this.vAnimation = AnimationUtility.fast(this.vAnimation, totalHeight, 15.0f);
            context.getMatrices().push();
            if (this.visibleValue < 1.0f) {
                context.getMatrices().translate(x + panelWidth / 2.0f, y + totalHeight / 2.0f, 0.0f);
                context.getMatrices().scale(this.visibleValue, this.visibleValue, 1.0f);
                context.getMatrices().translate(-(x + panelWidth / 2.0f), -(y + totalHeight / 2.0f), 0.0f);
            }
            Color bgColor = this.mode.getValue() == Mode.Neon ? new Color(10, 10, 10, 200) : new Color(30, 30, 30, 90);
            Color color = outlineColor = this.mode.getValue() == Mode.Neon ? HudEditor.textColor.getValue().getColorObject() : new Color(255, 255, 255, 40);
            if (this.mode.getValue() == Mode.Neon) {
                Render2DEngine.drawRect(context.getMatrices(), x - 1.0f, y - 1.0f, this.hAnimation + 2.0f, this.vAnimation + 2.0f, 10.0f, this.visibleValue, outlineColor, outlineColor, outlineColor, outlineColor);
            } else {
                Render2DEngine.drawRect(context.getMatrices(), x - 0.5f, y - 0.5f, this.hAnimation + 1.0f, this.vAnimation + 1.0f, 10.0f, this.visibleValue, outlineColor, outlineColor, outlineColor, outlineColor);
            }
            Render2DEngine.drawRect(context.getMatrices(), x, y, this.hAnimation, this.vAnimation, 10.0f, this.visibleValue, bgColor, bgColor, bgColor, bgColor);
            Color innerBg = this.mode.getValue() == Mode.Neon ? new Color(0, 0, 0, 100) : new Color(255, 255, 255, 20);
            Render2DEngine.drawRect(context.getMatrices(), x + 4.5f, y + 4.5f, this.hAnimation - 9.0f, titleHeight - 9.0f, 3.5f, this.visibleValue, innerBg, innerBg, innerBg, innerBg);
            FontRenderers.icons.drawString(context.getMatrices(), "C", (double)(x + 9.0f), (double)(y + 8.5f), HudEditor.textColor.getValue().getColor());
            FontRenderers.sf_bold.drawString(context.getMatrices(), "Keybinds", (double)(x + 23.0f), (double)(y + 8.0f), new Color(255, 255, 255, (int)(255.0f * this.visibleValue)).getRGB());
            float currentY = y + titleHeight - 2.0f;
            Render2DEngine.drawRect(context.getMatrices(), x + 4.5f, currentY, mainPanelWidth, contentHeight, 3.5f, this.visibleValue, innerBg, innerBg, innerBg, innerBg);
            float statusPanelX = x + 4.5f + mainPanelWidth + 2.5f;
            Render2DEngine.drawRect(context.getMatrices(), statusPanelX, currentY, statusPanelWidth, contentHeight, 3.5f, this.visibleValue, innerBg, innerBg, innerBg, innerBg);
            if (activeModules.isEmpty() && isEditing) {
                String[] examples = new String[]{"Aura", "Velocity", "Flight"};
                String[] keys2 = new String[]{"R", "V", "F"};
                for (int i = 0; i < 3; ++i) {
                    this.drawModuleBind(context, currentY, x, statusPanelX, statusPanelWidth, itemHeight, examples[i], keys2[i], this.visibleValue);
                    currentY += itemHeight + spacing;
                }
            } else {
                for (Module mod : activeModules) {
                    this.drawModuleBind(context, currentY, x, statusPanelX, statusPanelWidth, itemHeight, mod.getName(), WerPakKeybinds.getShortKeyName(mod), this.visibleValue);
                    currentY += itemHeight + spacing;
                }
            }
            context.getMatrices().pop();
            this.setBounds(x, y, this.hAnimation, this.vAnimation);
        }
    }

    private void drawModuleBind(DrawContext context, float currentY, float x, float statusPanelX, float statusPanelWidth, float itemHeight, String moduleName, String keyName, float alpha) {
        float barWidth = 1.3f;
        float barHeight = 7.0f;
        float barX = x + 4.5f + 2.8f;
        float barY = currentY + 3.0f;
        Color sideBarColor = this.mode.getValue() == Mode.Neon ? HudEditor.textColor.getValue().getColorObject() : Color.WHITE;
        Render2DEngine.drawRect(context.getMatrices(), barX, barY, barWidth, barHeight, 0.5f, alpha, sideBarColor, sideBarColor, sideBarColor, sideBarColor);
        float nameX = barX + barWidth + 3.5f;
        FontRenderers.sf_bold_mini.drawString(context.getMatrices(), moduleName, (double)nameX, (double)(currentY + 4.5f), new Color(255, 255, 255, (int)(255.0f * alpha)).getRGB());
        float keyTextWidth = FontRenderers.sf_bold_mini.getStringWidth(keyName);
        float maxWidth = statusPanelWidth - 2.0f;
        if (keyTextWidth > maxWidth) {
            float scale = maxWidth / keyTextWidth;
            context.getMatrices().push();
            context.getMatrices().translate(statusPanelX + 1.0f, currentY + 4.5f, 0.0f);
            context.getMatrices().scale(scale, scale, 1.0f);
            FontRenderers.sf_bold_mini.drawString(context.getMatrices(), keyName, 0.0, 0.0, new Color(255, 255, 255, (int)(255.0f * alpha)).getRGB());
            context.getMatrices().pop();
        } else {
            float keyX = statusPanelX + (statusPanelWidth - keyTextWidth) / 2.0f;
            FontRenderers.sf_bold_mini.drawString(context.getMatrices(), keyName, (double)keyX, (double)(currentY + 4.5f), new Color(255, 255, 255, (int)(255.0f * alpha)).getRGB());
        }
    }

    @NotNull
    public static String getShortKeyName(Module feature) {
        String sbind;
        return switch (sbind = feature.getBind().getBind()) {
            case "LEFT_CONTROL" -> "LCtrl";
            case "RIGHT_CONTROL" -> "RCtrl";
            case "LEFT_SHIFT" -> "LShift";
            case "RIGHT_SHIFT" -> "RShift";
            case "LEFT_ALT" -> "LAlt";
            case "RIGHT_ALT" -> "RAlt";
            default -> sbind.toUpperCase();
        };
    }

    public static enum Mode {
        Neon,
        Glass;

    }
}

