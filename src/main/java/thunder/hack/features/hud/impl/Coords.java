/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.player.PlayerUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

public class Coords
extends HudElement {
    private final Setting<NetherCoords> netherCoords = new Setting<NetherCoords>("NetherCoords", NetherCoords.On);

    public Coords() {
        super("Coords", 100, 10);
    }

    @Override
    public void onRender2D(DrawContext context) {
        float pX;
        super.onRender2D(context);
        int posX = (int)Coords.mc.player.getX();
        int posY = (int)Coords.mc.player.getY();
        int posZ = (int)Coords.mc.player.getZ();
        float nether = !PlayerUtility.isInHell() ? 0.125f : 8.0f;
        int hposX = (int)(Coords.mc.player.getX() * (double)nether);
        int hposZ = (int)(Coords.mc.player.getZ() * (double)nether);
        String coordinates = "XYZ " + String.valueOf(Formatting.WHITE) + posX + " " + posY + " " + posZ + String.valueOf(Formatting.WHITE) + (String)(this.netherCoords.is(NetherCoords.On) || this.netherCoords.is(NetherCoords.OnlyNether) && !PlayerUtility.isInHell() ? " [" + String.valueOf(Formatting.RESET) + hposX + " " + hposZ + String.valueOf(Formatting.WHITE) + "]" : "");
        float f = pX = this.getPosX() > (float)mc.getWindow().getScaledWidth() / 2.0f ? this.getPosX() - FontRenderers.getModulesRenderer().getStringWidth(coordinates) : this.getPosX();
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawRoundedBlur(context.getMatrices(), pX, this.getPosY(), FontRenderers.getModulesRenderer().getStringWidth(coordinates) + 21.0f, 13.0f, 3.0f, HudEditor.blurColor.getValue().getColorObject());
            Render2DEngine.drawRect(context.getMatrices(), pX + 14.0f, this.getPosY() + 2.0f, 0.5f, 8.0f, new Color(0x44FFFFFF, true));
            Render2DEngine.setupRender();
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
            RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.coordsIcon);
            Render2DEngine.renderGradientTexture(context.getMatrices(), pX + 2.0f, this.getPosY() + 1.0f, 10.0, 10.0, 0.0f, 0.0f, 512.0, 512.0, 512.0, 512.0, HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));
            Render2DEngine.endRender();
        }
        FontRenderers.getModulesRenderer().drawString(context.getMatrices(), coordinates, (double)(pX + 18.0f), (double)(this.getPosY() + 5.0f), HudEditor.getColor(1).getRGB());
        this.setBounds(pX, this.getPosY(), FontRenderers.getModulesRenderer().getStringWidth(coordinates) + 21.0f, 13.0f);
    }

    private static enum NetherCoords {
        Off,
        On,
        OnlyNether;

    }
}

