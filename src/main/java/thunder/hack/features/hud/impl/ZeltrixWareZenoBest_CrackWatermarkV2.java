/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.network.PlayerListEntry
 */
package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.network.PlayerListEntry;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.EventMouse;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.features.modules.misc.NameProtect;
import thunder.hack.gui.font.FontRenderer;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

public class ZeltrixWareZenoBest_CrackWatermarkV2
extends HudElement {
    private static final int[] PALETTE = new int[]{new Color(10181046).getRGB(), new Color(3447003).getRGB(), new Color(1752220).getRGB(), new Color(15158332).getRGB(), new Color(15965202).getRGB(), new Color(3066993).getRGB(), new Color(15277667).getRGB(), new Color(16733986).getRGB()};
    private int paletteIndex = 0;
    public final Setting<ThemeStyle> themeStyle = new Setting<ThemeStyle>("Style", ThemeStyle.Neon);
    public final Setting<Boolean> showServer = new Setting<Boolean>("Show Server", true);
    public final Setting<Boolean> showTps = new Setting<Boolean>("Show TPS", true);
    public final Setting<Boolean> useTheme = new Setting<Boolean>("Use Theme Color", true);
    private static final float PADDING_X = 6.0f;
    private static final float GAP = 4.0f;
    private static final float LAYER_GAP = 2.0f;
    private static final float ROW_HEIGHT = 20.0f;
    private static final float CORNER_R = 4.0f;

    public ZeltrixWareZenoBest_CrackWatermarkV2() {
        super("ZeltrixWareZenoBest_CrackWatermarkV2", 160, 42);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        if (ZeltrixWareZenoBest_CrackWatermarkV2.mc.player == null || ZeltrixWareZenoBest_CrackWatermarkV2.mc.world == null) {
            return;
        }
        MatrixStack matrices = context.getMatrices();
        float x = this.getPosX();
        float y = this.getPosY();
        Color accentColor = this.useTheme.getValue() != false ? HudEditor.getColor(0) : new Color(PALETTE[this.paletteIndex], true);
        int accent = accentColor.getRGB();
        Color bgColor = HudEditor.plateColor.getValue().getColorObject();
        int textNormal = HudEditor.textColor.getValue().getColorObject().getRGB();
        String username = NameProtect.getCustomName();
        String fpsValue = String.valueOf(mc.getCurrentFps());
        String fpsSuffix = " FPS";
        String pingValue = String.valueOf(this.getPing());
        String pingSuffix = " ms";
        String serverText = this.getServerAddress();
        int accentIdx = this.getServerAccentIndex(serverText);
        String serverPrefix = accentIdx >= 0 ? serverText.substring(0, accentIdx + 1) : serverText;
        String serverAccent = accentIdx >= 0 && accentIdx + 1 < serverText.length() ? serverText.substring(accentIdx + 1) : "";
        String tpsValue = this.formatOneDecimal(Managers.SERVER.getTPS());
        String tpsSuffix = " TPS";
        String slash = " / ";
        int slashColor = new Color(100, 100, 100).getRGB();
        FontRenderer font = FontRenderers.sf_bold;
        FontRenderer iconFont = FontRenderers.icons;
        float iconGap = 3.0f;
        float slashW = font.getStringWidth(" / ");
        float itemZW = font.getStringWidth("Z");
        float itemZeltrixWareZenoBest_CrackW = font.getStringWidth("ZeltrixWareZenoBest_Crack");
        float headW = iconFont.getStringWidth("p");
        float item3W = headW + iconGap + font.getStringWidth(username);
        float item4W = iconFont.getStringWidth("t") + iconGap + font.getStringWidth(serverPrefix) + (serverAccent.isEmpty() ? 0.0f : font.getStringWidth(serverAccent));
        float row1W = 12.0f + itemZW + slashW + itemZeltrixWareZenoBest_CrackW + slashW + item3W + slashW + item4W;
        float item5W = iconFont.getStringWidth("w") + iconGap + font.getStringWidth(fpsValue) + font.getStringWidth(" FPS");
        float item6W = iconFont.getStringWidth("i") + iconGap + font.getStringWidth(pingValue) + font.getStringWidth(" ms");
        float item7W = this.showTps.getValue() != false ? iconFont.getStringWidth("u") + iconGap + font.getStringWidth(tpsValue) + font.getStringWidth(" TPS") : 0.0f;
        float row2W = 12.0f + item5W + slashW + item6W;
        if (this.showTps.getValue().booleanValue()) {
            row2W += slashW + item7W;
        }
        float row2Y = y + 20.0f + 2.0f;
        this.drawRect(matrices, x, y, row1W, 20.0f, bgColor, accentColor);
        this.drawRect(matrices, x, row2Y, row2W, 20.0f, bgColor, accentColor);
        float dx = x + 6.0f;
        font.drawString(matrices, "Z", (double)dx, (double)(y + 7.0f), textNormal);
        font.drawString(matrices, " / ", (double)(dx += itemZW), (double)(y + 7.0f), slashColor);
        font.drawString(matrices, "ZeltrixWareZenoBest_Crack", (double)(dx += slashW), (double)(y + 7.0f), accent);
        font.drawString(matrices, " / ", (double)(dx += itemZeltrixWareZenoBest_CrackW), (double)(y + 7.0f), slashColor);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((int)770, (int)771);
        RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.playerIcon);
        RenderSystem.setShaderColor((float)((float)(accent >> 16 & 0xFF) / 255.0f), (float)((float)(accent >> 8 & 0xFF) / 255.0f), (float)((float)(accent & 0xFF) / 255.0f), (float)1.0f);
        Render2DEngine.renderTexture(matrices, dx += slashW, y + (20.0f - headW) / 2.0f, headW, headW, 0.0f, 0.0f, headW, headW, headW, headW);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        font.drawString(matrices, username, (double)(dx += headW + iconGap), (double)(y + 7.0f), textNormal);
        font.drawString(matrices, " / ", (double)(dx += font.getStringWidth(username)), (double)(y + 7.0f), slashColor);
        iconFont.drawString(matrices, "t", (double)(dx += slashW), (double)(y + 8.0f), accent);
        font.drawString(matrices, serverPrefix, (double)(dx += iconFont.getStringWidth("t") + iconGap), (double)(y + 7.0f), textNormal);
        dx += font.getStringWidth(serverPrefix);
        if (!serverAccent.isEmpty()) {
            font.drawString(matrices, serverAccent, (double)dx, (double)(y + 7.0f), accent);
            dx += font.getStringWidth(serverAccent);
        }
        dx = x + 6.0f;
        iconFont.drawString(matrices, "w", (double)dx, (double)(row2Y + 8.0f), accent);
        font.drawString(matrices, fpsValue, (double)(dx += iconFont.getStringWidth("w") + iconGap), (double)(row2Y + 7.0f), textNormal);
        font.drawString(matrices, " FPS", (double)(dx += font.getStringWidth(fpsValue)), (double)(row2Y + 7.0f), accent);
        font.drawString(matrices, " / ", (double)(dx += font.getStringWidth(" FPS")), (double)(row2Y + 7.0f), slashColor);
        iconFont.drawString(matrices, "i", (double)(dx += slashW), (double)(row2Y + 8.0f), accent);
        font.drawString(matrices, pingValue, (double)(dx += iconFont.getStringWidth("i") + iconGap), (double)(row2Y + 7.0f), textNormal);
        font.drawString(matrices, " ms", (double)(dx += font.getStringWidth(pingValue)), (double)(row2Y + 7.0f), accent);
        dx += font.getStringWidth(" ms");
        if (this.showTps.getValue().booleanValue()) {
            font.drawString(matrices, " / ", (double)dx, (double)(row2Y + 7.0f), slashColor);
            iconFont.drawString(matrices, "u", (double)(dx += slashW), (double)(row2Y + 8.0f), accent);
            font.drawString(matrices, tpsValue, (double)(dx += iconFont.getStringWidth("u") + iconGap), (double)(row2Y + 7.0f), textNormal);
            font.drawString(matrices, " TPS", (double)(dx += font.getStringWidth(tpsValue)), (double)(row2Y + 7.0f), accent);
        }
        float totalW = Math.max(row1W, row2W);
        this.setBounds(x, y, totalW, 42.0f);
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

    private void drawRect(MatrixStack matrices, float x, float y, float w, float h, Color bg, Color outlineColor) {
        ThemeStyle style = this.themeStyle.getValue();
        if (style == ThemeStyle.Neon) {
            Render2DEngine.drawBlurredShadow(matrices, x - 2.0f, y - 2.0f, w + 4.0f, h + 4.0f, 10, new Color(outlineColor.getRGB() & 0xFFFFFF | 0x50000000, true));
            Render2DEngine.drawRound(matrices, x, y, w, h, 4.0f, bg);
        } else if (style == ThemeStyle.Glass) {
            Render2DEngine.drawRound(matrices, x, y, w, h, 4.0f, new Color(0, 0, 0, 100));
        } else {
            Render2DEngine.drawRound(matrices, x, y, w, h, 4.0f, bg);
        }
    }

    private int getPing() {
        PlayerListEntry entry;
        if (mc.getNetworkHandler() == null) {
            return 0;
        }
        if (ZeltrixWareZenoBest_CrackWatermarkV2.mc.player != null && (entry = mc.getNetworkHandler().getPlayerListEntry(ZeltrixWareZenoBest_CrackWatermarkV2.mc.player.getUuid())) != null && entry.getLatency() > 0) {
            return entry.getLatency();
        }
        String username = mc.getSession().getUsername();
        for (PlayerListEntry entry2 : mc.getNetworkHandler().getPlayerList()) {
            if (!entry2.getProfile().getName().equalsIgnoreCase(username)) continue;
            return entry2.getLatency();
        }
        return 0;
    }

    private String getServerAddress() {
        if (mc.getCurrentServerEntry() != null && ZeltrixWareZenoBest_CrackWatermarkV2.mc.getCurrentServerEntry().address != null && !ZeltrixWareZenoBest_CrackWatermarkV2.mc.getCurrentServerEntry().address.isEmpty()) {
            return ZeltrixWareZenoBest_CrackWatermarkV2.mc.getCurrentServerEntry().address;
        }
        return "localhost";
    }

    private int getServerAccentIndex(String serverText) {
        int firstDot = serverText.indexOf(46);
        if (firstDot < 0) {
            return -1;
        }
        int secondDot = serverText.indexOf(46, firstDot + 1);
        return secondDot >= 0 ? secondDot : firstDot;
    }

    private String formatOneDecimal(float value) {
        int scaled = Math.round(value * 10.0f);
        return scaled / 10 + "." + Math.abs(scaled % 10);
    }

    public static enum ThemeStyle {
        Solid,
        Neon,
        Glass;

    }
}

