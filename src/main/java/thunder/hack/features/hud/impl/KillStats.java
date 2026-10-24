/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Formatting
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

public class KillStats
extends HudElement {
    int death = 0;
    int killstreak = 0;
    int kills = 0;

    public KillStats() {
        super("KillStats", 100, 35);
    }

    @Override
    public void onDisable() {
        this.death = 0;
        this.kills = 0;
        this.killstreak = 0;
    }

    @EventHandler
    private void death(PacketEvent.Receive event) {
        EntityStatusS2CPacket pac;
        Object t = event.getPacket();
        if (t instanceof EntityStatusS2CPacket && (pac = (EntityStatusS2CPacket)t).getStatus() == 3) {
            if (!(pac.getEntity((World)KillStats.mc.world) instanceof PlayerEntity)) {
                return;
            }
            if (pac.getEntity((World)KillStats.mc.world) == KillStats.mc.player) {
                ++this.death;
                this.killstreak = 0;
            } else if (Aura.target == pac.getEntity((World)KillStats.mc.world)) {
                ++this.killstreak;
                ++this.kills;
            }
        }
    }

    @Override
    public void onRender2D(DrawContext context) {
        float pX;
        super.onRender2D(context);
        String streak = "KillStreak: " + String.valueOf(Formatting.WHITE) + this.killstreak;
        String kd = " KD: " + String.valueOf(Formatting.WHITE) + MathUtility.round((float)this.kills / (float)(this.death > 0 ? this.death : 1));
        float f = pX = this.getPosX() > (float)mc.getWindow().getScaledWidth() / 2.0f ? this.getPosX() - FontRenderers.getModulesRenderer().getStringWidth(streak) - FontRenderers.getModulesRenderer().getStringWidth(kd) : this.getPosX();
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawRoundedBlur(context.getMatrices(), pX, this.getPosY(), FontRenderers.getModulesRenderer().getStringWidth(streak) + FontRenderers.getModulesRenderer().getStringWidth(kd) + 21.0f, 13.0f, 3.0f, HudEditor.blurColor.getValue().getColorObject());
            Render2DEngine.drawRect(context.getMatrices(), pX + 14.0f, this.getPosY() + 2.0f, 0.5f, 8.0f, new Color(0x44FFFFFF, true));
            Render2DEngine.setupRender();
            RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.swordIcon);
            Render2DEngine.renderGradientTexture(context.getMatrices(), pX + 2.0f, this.getPosY() + 1.0f, 10.0, 10.0, 0.0f, 0.0f, 16.0, 16.0, 16.0, 16.0, HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));
            Render2DEngine.endRender();
        }
        FontRenderers.getModulesRenderer().drawString(context.getMatrices(), streak, (double)(pX + 18.0f), (double)(this.getPosY() + 5.0f), HudEditor.getColor(1).getRGB());
        FontRenderers.getModulesRenderer().drawString(context.getMatrices(), kd, (double)(pX + 18.0f + FontRenderers.getModulesRenderer().getStringWidth(streak)), (double)(this.getPosY() + 5.0f), HudEditor.getColor(1).getRGB());
        this.setBounds(pX, this.getPosY(), FontRenderers.getModulesRenderer().getStringWidth(streak) + FontRenderers.getModulesRenderer().getStringWidth(kd) + 21.0f, 13.0f);
    }
}

