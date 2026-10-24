/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.features.hud.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventMouse;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.hud.HudEditorGui;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;

public class KeyBinds
extends HudElement {
    public final Setting<Boolean> customColor = new Setting<Boolean>("CustomColor", false);
    public final Setting<ColorSetting> color = new Setting<ColorSetting>("Color", new ColorSetting(-16718337), v -> this.customColor.getValue());
    public final Setting<Boolean> onlyEnabled = new Setting<Boolean>("OnlyEnabled", true);
    public final Setting<Boolean> glow = new Setting<Boolean>("Glow", true);
    public final Setting<ThemeStyle> themeStyle = new Setting<ThemeStyle>("Style", ThemeStyle.Neon);
    private final Map<String, Float> animationMap = new HashMap<String, Float>();
    private float animatedHeight = 0.0f;
    private float alpha = 0.0f;

    public KeyBinds() {
        super("KeyBinds", 95, 30);
    }

    private int getThemeColor() {
        if (this.customColor.getValue().booleanValue()) {
            return this.color.getValue().getColor();
        }
        return HudEditor.getColor(0).getRGB();
    }

    @Override
    @EventHandler
    public void onMouse(@NotNull EventMouse event) {
        if (event.getAction() == 1 && event.getButton() == 1 && this.isHovering()) {
            ThemeStyle[] styles = ThemeStyle.values();
            this.themeStyle.setValue(styles[(this.themeStyle.getValue().ordinal() + 1) % styles.length]);
        }
        super.onMouse(event);
    }

    @Override
    public void onRender2D(DrawContext context) {
        Color bgInner;
        float contentHeight;
        super.onRender2D(context);
        if (KeyBinds.mc.player == null) {
            return;
        }
        boolean isGui = KeyBinds.mc.currentScreen instanceof ChatScreen || KeyBinds.mc.currentScreen instanceof HudEditorGui;
        ArrayList<ModuleBind> activeBinds = new ArrayList<ModuleBind>();
        for (Module m : Managers.MODULE.modules) {
            float targetAnim;
            if (m == ModuleManager.clickGui || Objects.equals(m.getBind().getBind(), "None")) continue;
            boolean active = this.onlyEnabled.getValue() == false || m.isEnabled();
            float currentAnim = this.animationMap.getOrDefault(m.getName(), Float.valueOf(0.0f)).floatValue();
            float newAnim = Render2DEngine.interpolateFloat(currentAnim, targetAnim = active ? 1.0f : 0.0f, 0.18f);
            if (Math.abs(newAnim - targetAnim) < 0.01f) {
                newAnim = targetAnim;
            }
            if (newAnim > 0.001f) {
                this.animationMap.put(m.getName(), Float.valueOf(newAnim));
                activeBinds.add(new ModuleBind(m.getName(), KeyBinds.getShortKeyName(m), newAnim));
                continue;
            }
            this.animationMap.remove(m.getName());
        }
        if (activeBinds.isEmpty() && isGui) {
            activeBinds.add(new ModuleBind("Aura", "R", 1.0f));
            activeBinds.add(new ModuleBind("Velocity", "V", 1.0f));
            activeBinds.add(new ModuleBind("Flight", "F", 1.0f));
        }
        boolean hasAnyVisible = !activeBinds.isEmpty() || isGui;
        float targetAlpha = hasAnyVisible ? 1.0f : 0.0f;
        this.alpha = MathHelper.clamp((float)Render2DEngine.interpolateFloat(this.alpha, targetAlpha, 0.22f), (float)0.0f, (float)1.0f);
        if (this.alpha <= 0.01f) {
            this.setBounds(this.getPosX(), this.getPosY(), 0.0f, 0.0f);
            return;
        }
        float x = this.getPosX();
        float y = this.getPosY();
        float panelWidth = 95.0f;
        float titleHeight = 24.0f;
        float rowHeight = 12.5f;
        float statusPanelWidth = 16.0f;
        float mainPanelWidth = panelWidth - statusPanelWidth - 11.0f;
        float totalAnimHeight = 0.0f;
        for (ModuleBind bind : activeBinds) {
            totalAnimHeight += rowHeight * bind.anim();
        }
        float totalHeight = titleHeight + 5.0f + contentHeight + ((contentHeight = totalAnimHeight) > 0.5f ? 4.5f : 0.0f);
        this.animatedHeight = Render2DEngine.interpolateFloat(this.animatedHeight, totalHeight, 0.18f);
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        int themeColor = this.getThemeColor();
        ThemeStyle style = this.themeStyle.getValue();
        Color bgOuter = style == ThemeStyle.Glass ? new Color(14, 14, 18, (int)(100.0f * this.alpha)) : new Color(14, 14, 18, (int)(235.0f * this.alpha));
        Color color = bgInner = style == ThemeStyle.Glass ? new Color(28, 28, 38, (int)(60.0f * this.alpha)) : new Color(28, 28, 38, (int)(120.0f * this.alpha));
        if (style == ThemeStyle.Neon || this.glow.getValue().booleanValue()) {
            Render2DEngine.drawBlurredShadow(matrices, x - 2.0f, y - 2.0f, panelWidth + 4.0f, this.animatedHeight + 4.0f, 10, new Color(themeColor & 0xFFFFFF | (int)(45.0f * this.alpha) << 24, true));
        }
        Render2DEngine.drawRound(matrices, x, y, panelWidth, this.animatedHeight, 7.0f, bgOuter);
        Render2DEngine.drawRound(matrices, x + 4.5f, y + 4.5f, panelWidth - 9.0f, titleHeight - 9.0f, 3.5f, bgInner);
        int drawAlpha = (int)(255.0f * this.alpha);
        FontRenderers.sf_bold.drawString(matrices, "Binds", (double)(x + 10.0f), (double)(y + 9.0f), drawAlpha << 24 | 0xFFFFFF);
        FontRenderers.sf_bold.drawString(matrices, "D", (double)(x + panelWidth - 16.0f), (double)(y + 9.0f), drawAlpha << 24 | 0x9E9E9E);
        float currentY = y + titleHeight + 2.0f;
        if (contentHeight > 1.0f) {
            Render2DEngine.drawRound(matrices, x + 4.5f, currentY, mainPanelWidth, contentHeight, 3.5f, bgInner);
            float statusPanelX = x + 4.5f + mainPanelWidth + 2.5f;
            Render2DEngine.drawRound(matrices, statusPanelX, currentY, statusPanelWidth, contentHeight, 3.5f, bgInner);
            Render2DEngine.beginScissor(x, currentY, x + panelWidth, currentY + contentHeight);
            float itemY = currentY;
            for (ModuleBind bind : activeBinds) {
                float anim = bind.anim();
                float itemH = rowHeight * anim;
                if (itemH < 0.5f) continue;
                float effectiveAlpha = anim * this.alpha;
                int textAlpha = (int)(255.0f * effectiveAlpha);
                float barWidth = 1.3f;
                float barHeight = 7.0f * anim;
                float barX = x + 4.5f + 2.8f;
                float barY = itemY + (rowHeight - barHeight) / 2.0f;
                Render2DEngine.drawRound(matrices, barX, barY, barWidth, barHeight, 0.9f, new Color(themeColor & 0xFFFFFF | textAlpha << 24, true));
                float nameX = barX + barWidth + 3.5f;
                float nameY = itemY + 4.0f;
                Render2DEngine.beginScissor(x + 4.5f, currentY, x + 4.5f + mainPanelWidth, currentY + contentHeight);
                FontRenderers.sf_bold_mini.drawString(matrices, bind.name(), (double)nameX, (double)nameY, textAlpha << 24 | 0xFFFFFF);
                Render2DEngine.endScissor();
                String keyName = bind.key();
                float keyTextWidth = FontRenderers.sf_bold_mini.getStringWidth(keyName);
                float maxWidth = statusPanelWidth - 2.0f;
                if (keyTextWidth > maxWidth) {
                    float scale = maxWidth / keyTextWidth;
                    matrices.push();
                    float keyX = statusPanelX + 1.0f;
                    float keyY = itemY + 4.0f;
                    matrices.translate(keyX, keyY, 0.0f);
                    matrices.scale(scale, scale, 1.0f);
                    FontRenderers.sf_bold_mini.drawString(matrices, keyName, 0.0, 0.0, textAlpha << 24 | themeColor & 0xFFFFFF);
                    matrices.pop();
                } else {
                    float keyX = statusPanelX + (statusPanelWidth - keyTextWidth) / 2.0f;
                    float keyY = itemY + 4.0f;
                    FontRenderers.sf_bold_mini.drawString(matrices, keyName, (double)keyX, (double)keyY, textAlpha << 24 | themeColor & 0xFFFFFF);
                }
                itemY += itemH;
            }
            Render2DEngine.endScissor();
        }
        matrices.pop();
        this.setBounds(x, y, panelWidth, this.animatedHeight);
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
            case "MOUSE_BUTTON_4" -> "M4";
            case "MOUSE_BUTTON_5" -> "M5";
            case "BACKSPACE" -> "Back";
            case "DELETE" -> "Del";
            case "INSERT" -> "Ins";
            case "RETURN" -> "Enter";
            default -> sbind.startsWith("NUMPAD") ? "N" + sbind.replace("NUMPAD", "") : sbind.toUpperCase();
        };
    }

    public static enum ThemeStyle {
        Solid,
        Neon,
        Glass;

    }

    private record ModuleBind(String name, String key, float anim) {
    }
}

