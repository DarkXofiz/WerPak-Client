/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.util.Formatting
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.hud.impl;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

public class KillFeed
extends HudElement {
    private Setting<Boolean> resetOnDeath = new Setting<Boolean>("ResetOnDeath", true);
    private final List<KillComponent> players = new ArrayList<KillComponent>();
    private float vAnimation;
    private float hAnimation;

    public KillFeed() {
        super("KillFeed", 50, 50);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        int y_offset1 = 0;
        float scale_x = 30.0f;
        for (KillComponent kc : Lists.newArrayList(this.players)) {
            if (FontRenderers.modules.getStringWidth(kc.getString()) > scale_x) {
                scale_x = FontRenderers.modules.getStringWidth(kc.getString());
            }
            y_offset1 += 15;
        }
        this.vAnimation = AnimationUtility.fast(this.vAnimation, 14 + y_offset1, 15.0f);
        this.hAnimation = AnimationUtility.fast(this.hAnimation, scale_x + 10.0f, 15.0f);
        Render2DEngine.drawHudBase(context.getMatrices(), this.getPosX(), this.getPosY(), this.hAnimation, this.vAnimation, HudEditor.hudRound.getValue().floatValue());
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Glowing)) {
            FontRenderers.sf_bold.drawCenteredString(context.getMatrices(), "KillFeed", (double)(this.getPosX() + this.hAnimation / 2.0f), (double)(this.getPosY() + 4.0f), HudEditor.textColor.getValue().getColorObject());
        } else {
            FontRenderers.sf_bold.drawGradientCenteredString(context.getMatrices(), "KillFeed", this.getPosX() + this.hAnimation / 2.0f, this.getPosY() + 4.0f, 10);
        }
        if (y_offset1 > 0) {
            if (HudEditor.hudStyle.is(HudEditor.HudStyle.Glowing)) {
                Render2DEngine.horizontalGradient(context.getMatrices(), this.getPosX() + 2.0f, this.getPosY() + 13.7f, this.getPosX() + 2.0f + this.hAnimation / 2.0f - 2.0f, this.getPosY() + 14.0f, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
                Render2DEngine.horizontalGradient(context.getMatrices(), this.getPosX() + 2.0f + this.hAnimation / 2.0f - 2.0f, this.getPosY() + 13.7f, this.getPosX() + 2.0f + this.hAnimation - 4.0f, this.getPosY() + 14.0f, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
            } else {
                Render2DEngine.drawRectDumbWay(context.getMatrices(), this.getPosX() + 4.0f, this.getPosY() + 13.0f, this.getPosX() + this.getWidth() - 4.0f, this.getPosY() + 13.5f, new Color(0x54FFFFFF, true));
            }
        }
        Render2DEngine.addWindow(context.getMatrices(), this.getPosX(), this.getPosY(), this.getPosX() + this.hAnimation, this.getPosY() + this.vAnimation, 1.0);
        int y_offset = 3;
        for (KillComponent kc : Lists.newArrayList(this.players)) {
            FontRenderers.modules.drawString(context.getMatrices(), kc.getString(), (double)(this.getPosX() + 5.0f), (double)(this.getPosY() + 18.0f + (float)y_offset), -1);
            y_offset += 10;
        }
        Render2DEngine.popWindow();
        this.setBounds(this.getPosX(), this.getPosY(), this.hAnimation, this.vAnimation);
    }

    @EventHandler
    public void onPacket(@NotNull PacketEvent.Receive e) {
        Object object;
        Object t = e.getPacket();
        if (!(t instanceof EntityStatusS2CPacket)) {
            return;
        }
        EntityStatusS2CPacket pac = (EntityStatusS2CPacket)t;
        if (pac.getStatus() == 3 && (object = pac.getEntity((World)KillFeed.mc.world)) instanceof PlayerEntity) {
            PlayerEntity pl = (PlayerEntity)object;
            if (pl == KillFeed.mc.player && this.resetOnDeath.getValue().booleanValue()) {
                this.players.clear();
                return;
            }
            if (Aura.target != null && Aura.target == pac.getEntity((World)KillFeed.mc.world)) {
                for (KillComponent kc : Lists.newArrayList(this.players)) {
                    if (!Objects.equals(kc.getName(), pl.getName().getString())) continue;
                    kc.increase();
                    return;
                }
                this.players.add(new KillComponent(this, pl.getName().getString()));
            }
        }
    }

    @Override
    public void onDisable() {
        this.players.clear();
    }

    private class KillComponent {
        private String name;
        private int count;

        public KillComponent(KillFeed killFeed, String name) {
            this.name = name;
            this.count = 1;
        }

        public void increase() {
            ++this.count;
        }

        public String getName() {
            return this.name;
        }

        public String getString() {
            return String.valueOf(Formatting.RED) + "EZ - " + String.valueOf(Formatting.RESET) + this.name + (String)(this.count > 1 ? " [" + String.valueOf(Formatting.GRAY) + "x" + this.count + String.valueOf(Formatting.RESET) + "]" : "");
        }
    }
}

