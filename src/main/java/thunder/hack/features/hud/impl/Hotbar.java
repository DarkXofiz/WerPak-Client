/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.network.ClientPlayerEntity
 */
package thunder.hack.features.hud.impl;

import java.awt.Color;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.network.ClientPlayerEntity;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.windows.WindowsScreen;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;

public class Hotbar
extends HudElement {
    public static final Setting<Mode> lmode = new Setting<Mode>("LeftHandMode", Mode.Merged);

    public Hotbar() {
        super("Hotbar", 0, 0);
    }

    @Override
    public void onRender2D(DrawContext context) {
        if (Hotbar.mc.currentScreen instanceof WindowsScreen) {
            return;
        }
        ClientPlayerEntity playerEntity = Hotbar.mc.player;
        if (playerEntity != null) {
            MatrixStack matrices = context.getMatrices();
            int i = mc.getWindow().getScaledWidth() / 2;
            if (Hotbar.mc.player.getOffHandStack().isEmpty()) {
                Render2DEngine.drawHudBase(matrices, i - 90, mc.getWindow().getScaledHeight() - 25, 180.0f, 20.0f, HudEditor.hudRound.getValue().floatValue());
            } else if (lmode.getValue() == Mode.Merged) {
                Render2DEngine.drawHudBase(matrices, i - 111, mc.getWindow().getScaledHeight() - 25, 201.0f, 20.0f, HudEditor.hudRound.getValue().floatValue());
                if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
                    Render2DEngine.drawRect(context.getMatrices(), i - 109 + 18, mc.getWindow().getScaledHeight() - 23, 0.5f, 15.0f, new Color(0x44FFFFFF, true));
                } else {
                    Render2DEngine.verticalGradient(matrices, i - 109 + 18, mc.getWindow().getScaledHeight() - 22 + 1 - 4, (float)(i - 108 + 18) - 0.5f, mc.getWindow().getScaledHeight() - 11 + 1 - 4, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
                    Render2DEngine.verticalGradient(matrices, i - 109 + 18, mc.getWindow().getScaledHeight() - 11 - 4, (float)(i - 108 + 18) - 0.5f, mc.getWindow().getScaledHeight() - 5, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
                }
            } else {
                Render2DEngine.drawHudBase(matrices, i - 90, mc.getWindow().getScaledHeight() - 25, 180.0f, 20.0f, HudEditor.hudRound.getValue().floatValue());
                Render2DEngine.drawHudBase(matrices, (float)i - 112.5f, mc.getWindow().getScaledHeight() - 25, 20.0f, 20.0f, HudEditor.hudRound.getValue().floatValue());
            }
            Color c = HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry) ? new Color(2081756437, true) : new Color(2083467055, true);
            Render2DEngine.drawRect(matrices, (float)(i - 88) + (float)playerEntity.getInventory().selectedSlot * 19.8f, mc.getWindow().getScaledHeight() - 24, 17.0f, 17.0f, HudEditor.hudRound.getValue().floatValue(), 0.7f, c, c, c, c);
        }
    }

    public static void renderHotBarItems(float tickDelta, DrawContext context) {
        if (Hotbar.mc.currentScreen instanceof WindowsScreen) {
            return;
        }
        ClientPlayerEntity playerEntity = Hotbar.mc.player;
        if (playerEntity != null) {
            MatrixStack matrices = context.getMatrices();
            int i = mc.getWindow().getScaledWidth() / 2;
            int o = mc.getWindow().getScaledHeight() - 16 - 3;
            if (!Hotbar.mc.player.getOffHandStack().isEmpty()) {
                if (lmode.getValue() == Mode.Merged) {
                    Hotbar.renderHotbarItem(context, i - 109, o - 5, playerEntity.getOffHandStack());
                } else {
                    Hotbar.renderHotbarItem(context, i - 111, o - 5, playerEntity.getOffHandStack());
                }
            }
            for (int m = 0; m < 9; ++m) {
                int n = i - 90 + m * 20 + 2;
                if (m == Hotbar.mc.player.getInventory().selectedSlot) {
                    Hotbar.renderHotbarItem(context, n, o - 7, (ItemStack)playerEntity.getInventory().main.get(m));
                    continue;
                }
                Hotbar.renderHotbarItem(context, n, o - 5, (ItemStack)playerEntity.getInventory().main.get(m));
            }
        }
    }

    private static void renderHotbarItem(DrawContext context, int i, int j, ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            context.getMatrices().push();
            context.getMatrices().translate((float)(i + 8), (float)(j + 12), 0.0f);
            context.getMatrices().scale(0.9f, 0.9f, 1.0f);
            context.getMatrices().translate((float)(-(i + 8)), (float)(-(j + 12)), 0.0f);
            context.drawItem(itemStack, i, j);
            context.drawItemInSlot(Hotbar.mc.textRenderer, itemStack, i, j);
            context.getMatrices().pop();
        }
    }

    public static void renderXpBar(int x, MatrixStack matrices) {
        mc.getProfiler().push("expBar");
        mc.getProfiler().pop();
        if (Hotbar.mc.player.experienceLevel > 0) {
            mc.getProfiler().push("expLevel");
            String string = "" + Hotbar.mc.player.experienceLevel;
            int k = (int)(((float)mc.getWindow().getScaledWidth() - FontRenderers.sf_bold_mini.getStringWidth(string)) / 2.0f);
            int l = mc.getWindow().getScaledHeight() - 31 - 4;
            FontRenderers.sf_bold_mini.drawString(matrices, string, (double)k, (double)l, 8453920);
            mc.getProfiler().pop();
        }
    }

    public static enum Mode {
        Merged,
        Separately;

    }
}

