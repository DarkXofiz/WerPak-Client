/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.features.hud.impl;

import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.core.Managers;
import thunder.hack.features.hud.HudElement;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;

public class WerPakWatermarkAlternative
extends HudElement {
    private static final int BG_COLOR = new Color(20, 20, 20, 240).getRGB();
    private static final int WHITE_COLOR = new Color(255, 255, 255, 255).getRGB();
    private static final float BAR_H = 17.0f;
    private static final float BAR_RADIUS = 5.0f;
    private static final float H_PAD = 5.0f;
    private static final float H_PAD_RIGHT = 5.0f;
    private static final float ELEMENT_GAP = 8.0f;
    private static final float PILL_GAP = 4.0f;
    private static final float ROW_GAP = 3.0f;
    public final Setting<Boolean> showFps = new Setting<Boolean>("Show FPS", true);
    public final Setting<Boolean> showMs = new Setting<Boolean>("Show MS", true);
    public final Setting<Boolean> showServer = new Setting<Boolean>("Show Server", true);
    public final Setting<Boolean> showTps = new Setting<Boolean>("Show TPS", true);
    public final Setting<Boolean> showCoords = new Setting<Boolean>("Show Coords", true);
    public final Setting<Boolean> showBps = new Setting<Boolean>("Show BPS", true);
    public final Setting<Boolean> showTime = new Setting<Boolean>("Show Time", true);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public WerPakWatermarkAlternative() {
        super("WerPakWatermarkAlt", 100, 30);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        if (WerPakWatermarkAlternative.mc.player == null || WerPakWatermarkAlternative.mc.world == null) {
            return;
        }
        MatrixStack matrices = context.getMatrices();
        float x = this.getPosX();
        float y = this.getPosY();
        String brandText = "WerPak";
        String username = WerPakWatermarkAlternative.mc.player.getName().getString();
        int fps = mc.getCurrentFps();
        String fpsText = fps + "fps";
        int ping = Managers.SERVER.getPing();
        String pingText = ping + "ms";
        float tps = Managers.SERVER.getTPS();
        String tpsText = this.formatOneDecimal(tps) + "tps";
        String timeText = LocalTime.now().format(TIME_FMT);
        int px = MathHelper.floor((double)WerPakWatermarkAlternative.mc.player.getX());
        int py = MathHelper.floor((double)WerPakWatermarkAlternative.mc.player.getY());
        int pz = MathHelper.floor((double)WerPakWatermarkAlternative.mc.player.getZ());
        String coordsText = "x" + px + " y" + py + " z" + pz;
        double bps = (double)Managers.PLAYER.currentPlayerSpeed * 20.0;
        String bpsText = this.formatOneDecimal((float)bps) + "bps";
        float brandPillW = 10.0f + FontRenderers.sf_bold.getStringWidth(brandText);
        float mainPillW = 10.0f;
        boolean firstMain = true;
        if (this.showServer.getValue().booleanValue() && !username.isEmpty()) {
            mainPillW += (firstMain ? 0.0f : 8.0f) + FontRenderers.sf_bold.getStringWidth(username);
            firstMain = false;
        }
        if (this.showFps.getValue().booleanValue()) {
            mainPillW += (firstMain ? 0.0f : 8.0f) + FontRenderers.sf_bold.getStringWidth(fpsText);
            firstMain = false;
        }
        if (this.showMs.getValue().booleanValue()) {
            mainPillW += (firstMain ? 0.0f : 8.0f) + FontRenderers.sf_bold.getStringWidth(pingText);
            firstMain = false;
        }
        if (this.showTime.getValue().booleanValue()) {
            mainPillW += (firstMain ? 0.0f : 8.0f) + FontRenderers.sf_bold.getStringWidth(timeText);
        }
        boolean hasRow2 = this.showCoords.getValue() != false || this.showBps.getValue() != false || this.showTps.getValue() != false;
        float row2W = 0.0f;
        if (hasRow2) {
            row2W = 10.0f;
            boolean first2 = true;
            if (this.showCoords.getValue().booleanValue()) {
                row2W += (first2 ? 0.0f : 8.0f) + FontRenderers.sf_bold.getStringWidth(coordsText);
                first2 = false;
            }
            if (this.showBps.getValue().booleanValue()) {
                row2W += (first2 ? 0.0f : 8.0f) + FontRenderers.sf_bold.getStringWidth(bpsText);
                first2 = false;
            }
            if (this.showTps.getValue().booleanValue()) {
                row2W += (first2 ? 0.0f : 8.0f) + FontRenderers.sf_bold.getStringWidth(tpsText);
            }
        }
        float row1TotalW = brandPillW + 4.0f + mainPillW;
        float totalW = Math.max(row1TotalW, row2W);
        float totalH = 17.0f + (hasRow2 ? 20.0f : 0.0f);
        float textY1 = y + 8.5f - 4.0f;
        this.drawBar(matrices, x, y, brandPillW);
        float brandCx = x + 5.0f;
        FontRenderers.sf_bold.drawString(matrices, brandText, (double)brandCx, (double)textY1, WHITE_COLOR);
        float x1b = x + brandPillW + 4.0f;
        this.drawBar(matrices, x1b, y, mainPillW);
        float cx = x1b + 5.0f;
        boolean drawnMain = false;
        if (this.showServer.getValue().booleanValue() && !username.isEmpty()) {
            if (drawnMain) {
                cx += 8.0f;
            }
            cx = this.drawTextItem(matrices, username, cx, textY1);
            drawnMain = true;
        }
        if (this.showFps.getValue().booleanValue()) {
            if (drawnMain) {
                cx += 8.0f;
            }
            cx = this.drawTextItem(matrices, fpsText, cx, textY1);
            drawnMain = true;
        }
        if (this.showMs.getValue().booleanValue()) {
            if (drawnMain) {
                cx += 8.0f;
            }
            cx = this.drawTextItem(matrices, pingText, cx, textY1);
            drawnMain = true;
        }
        if (this.showTime.getValue().booleanValue()) {
            if (drawnMain) {
                cx += 8.0f;
            }
            cx = this.drawTextItem(matrices, timeText, cx, textY1);
        }
        if (hasRow2) {
            float y2 = y + 17.0f + 3.0f;
            float textY2 = y2 + 8.5f - 4.0f;
            this.drawBar(matrices, x, y2, row2W);
            float cx2 = x + 5.0f;
            boolean drawn2 = false;
            if (this.showCoords.getValue().booleanValue()) {
                cx2 = this.drawTextItem(matrices, coordsText, cx2, textY2);
                drawn2 = true;
            }
            if (this.showBps.getValue().booleanValue()) {
                if (drawn2) {
                    cx2 += 8.0f;
                }
                cx2 = this.drawTextItem(matrices, bpsText, cx2, textY2);
                drawn2 = true;
            }
            if (this.showTps.getValue().booleanValue()) {
                if (drawn2) {
                    cx2 += 8.0f;
                }
                cx2 = this.drawTextItem(matrices, tpsText, cx2, textY2);
            }
        }
        this.setBounds(x, y, totalW, totalH);
    }

    private void drawBar(MatrixStack matrices, float x, float y, float w) {
        Render2DEngine.drawRound(matrices, x, y, w, 17.0f, 5.0f, new Color(BG_COLOR, true));
    }

    private float drawTextItem(MatrixStack matrices, String text, float cx, float textY) {
        FontRenderers.sf_bold.drawString(matrices, text, (double)cx, (double)textY, WHITE_COLOR);
        return cx + FontRenderers.sf_bold.getStringWidth(text);
    }

    private String formatOneDecimal(float value) {
        int scaled = Math.round(value * 10.0f);
        return scaled / 10 + "." + Math.abs(scaled % 10);
    }
}

