/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.core.Managers;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

public class WerPakWatermark
extends HudElement {
    public final Setting<Boolean> showIp = new Setting<Boolean>("Show IP", true);
    public final Setting<Boolean> showTime = new Setting<Boolean>("Show Time", true);

    public WerPakWatermark() {
        super("WerPakWatermark", 120, 24);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        String title = "WerPak";
        String fps = mc.getCurrentFps() + " FPS";
        String ping = Managers.SERVER.getPing() + " ms";
        String ip = mc.getCurrentServerEntry() != null ? WerPakWatermark.mc.getCurrentServerEntry().address : "Singleplayer";
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        float logoSize = 13.0f;
        float logoSpacing = 5.0f;
        float titleWidth = FontRenderers.sf_bold.getStringWidth(title);
        float fpsWidth = FontRenderers.sf_bold.getStringWidth(fps);
        float pingWidth = FontRenderers.sf_bold.getStringWidth(ping);
        float ipWidth = this.showIp.getValue() != false ? FontRenderers.sf_bold.getStringWidth(ip) : 0.0f;
        float timeWidth = this.showTime.getValue() != false ? FontRenderers.sf_bold.getStringWidth(time) : 0.0f;
        float totalWidth = 7.0f + logoSize + logoSpacing + titleWidth + 10.0f + fpsWidth + 10.0f + pingWidth + 10.0f;
        if (this.showIp.getValue().booleanValue()) {
            totalWidth += ipWidth + 10.0f;
        }
        if (this.showTime.getValue().booleanValue()) {
            totalWidth += timeWidth + 10.0f;
        }
        Render2DEngine.drawRound(context.getMatrices(), this.getPosX(), this.getPosY(), totalWidth, 24.0f, 4.0f, new Color(5, 5, 5, 240));
        int normalColor = HudEditor.textColor.getValue().getColorObject().getRGB();
        int secondaryColor = new Color(170, 170, 170).getRGB();
        float currentX = this.getPosX() + 7.0f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.miniLogo);
        Render2DEngine.renderTexture(context.getMatrices(), currentX, this.getPosY() + 5.5f, logoSize, logoSize, 0.0f, 0.0f, 128.0, 128.0, 128.0, 128.0);
        FontRenderers.sf_bold.drawString(context.getMatrices(), title, (double)(currentX += logoSize + logoSpacing), (double)(this.getPosY() + 8.5f), normalColor);
        FontRenderers.sf_bold.drawString(context.getMatrices(), fps, (double)(currentX += titleWidth + 10.0f), (double)(this.getPosY() + 8.5f), secondaryColor);
        FontRenderers.sf_bold.drawString(context.getMatrices(), ping, (double)(currentX += fpsWidth + 10.0f), (double)(this.getPosY() + 8.5f), secondaryColor);
        currentX += pingWidth + 10.0f;
        if (this.showIp.getValue().booleanValue()) {
            FontRenderers.sf_bold.drawString(context.getMatrices(), ip, (double)currentX, (double)(this.getPosY() + 8.5f), secondaryColor);
            currentX += ipWidth + 10.0f;
        }
        if (this.showTime.getValue().booleanValue()) {
            FontRenderers.sf_bold.drawString(context.getMatrices(), time, (double)currentX, (double)(this.getPosY() + 8.5f), secondaryColor);
        }
        this.setBounds(this.getPosX(), this.getPosY(), totalWidth, 24.0f);
    }
}

