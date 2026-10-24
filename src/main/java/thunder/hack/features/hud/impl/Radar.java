/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.hud.impl;

import java.awt.Color;
import net.minecraft.util.Formatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.features.modules.render.NameTags;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public class Radar
extends HudElement {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Rect);
    private final Setting<ColorMode> colorMode = new Setting<ColorMode>("ColorMode", ColorMode.Sync);
    private final Setting<Integer> size = new Setting<Integer>("Size", 80, 20, 300);
    private final Setting<ColorSetting> color2 = new Setting<ColorSetting>("Color", new ColorSetting(-15724528));
    private final Setting<ColorSetting> color3 = new Setting<ColorSetting>("PlayerColor", new ColorSetting(-979657829));
    private final Setting<Component> c1 = new Setting<Component>("Component1", Component.Name, v -> this.mode.is(Mode.Text));
    private final Setting<Component> c2 = new Setting<Component>("Component2", Component.Hp, v -> this.mode.is(Mode.Text));
    private final Setting<Component> c3 = new Setting<Component>("Component3", Component.Ping, v -> this.mode.is(Mode.Text));
    private final Setting<Component> c4 = new Setting<Component>("Component4", Component.None, v -> this.mode.is(Mode.Text));
    private final Setting<Component> c5 = new Setting<Component>("Component5", Component.None, v -> this.mode.is(Mode.Text));
    private final Setting<Formatting> c12 = new Setting<Formatting>("Color1", Formatting.WHITE, v -> this.mode.is(Mode.Text));
    private final Setting<Formatting> c22 = new Setting<Formatting>("Color2", Formatting.WHITE, v -> this.mode.is(Mode.Text));
    private final Setting<Formatting> c32 = new Setting<Formatting>("Color3", Formatting.WHITE, v -> this.mode.is(Mode.Text));
    private final Setting<Formatting> c42 = new Setting<Formatting>("Color4", Formatting.WHITE, v -> this.mode.is(Mode.Text));

    public Radar() {
        super("Radar", 100, 100);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        if (this.mode.getValue() == Mode.Rect) {
            Render2DEngine.drawHudBase(context.getMatrices(), this.getPosX(), this.getPosY(), this.size.getValue().intValue(), this.size.getValue().intValue(), HudEditor.hudRound.getValue().floatValue());
            if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
                Render2DEngine.drawRectDumbWay(context.getMatrices(), this.getPosX(), this.getPosY() + ((float)this.size.getValue().intValue() / 2.0f + 0.25f), this.getPosX() + (float)this.size.getValue().intValue(), this.getPosY() + (float)this.size.getValue().intValue() / 2.0f - 0.25f, new Color(0x54FFFFFF, true));
                Render2DEngine.drawRectDumbWay(context.getMatrices(), this.getPosX() + ((float)this.size.getValue().intValue() / 2.0f - 0.25f), this.getPosY() - 0.5f, this.getPosX() + (float)this.size.getValue().intValue() / 2.0f + 0.25f, this.getPosY() + (float)this.size.getValue().intValue() - 1.0f, new Color(0x54FFFFFF, true));
            } else {
                Render2DEngine.draw2DGradientRect(context.getMatrices(), (float)((double)this.getPosX() + ((double)((float)this.size.getValue().intValue() / 2.0f) - 0.5)), (float)((double)this.getPosY() + 3.5), (float)((double)this.getPosX() + ((double)((float)this.size.getValue().intValue() / 2.0f) + 0.2)), (float)((double)(this.getPosY() + (float)this.size.getValue().intValue()) - 3.5), this.color2.getValue().getColorObject(), this.color2.getValue().getColorObject(), this.color2.getValue().getColorObject(), this.color2.getValue().getColorObject());
                Render2DEngine.draw2DGradientRect(context.getMatrices(), this.getPosX() + 3.5f, this.getPosY() + ((float)this.size.getValue().intValue() / 2.0f - 0.2f), this.getPosX() + (float)this.size.getValue().intValue() - 3.5f, this.getPosY() + ((float)this.size.getValue().intValue() / 2.0f + 0.5f), this.color2.getValue().getColorObject(), this.color2.getValue().getColorObject(), this.color2.getValue().getColorObject(), this.color2.getValue().getColorObject());
            }
            for (PlayerEntity class_16572 : Managers.ASYNC.getAsyncPlayers()) {
                if (class_16572 == Radar.mc.player) continue;
                float f = (float)(class_16572.prevX + (class_16572.prevX - class_16572.getX()) * (double)Render3DEngine.getTickDelta() - Radar.mc.player.getX()) * 2.0f;
                float posZ = (float)(class_16572.prevZ + (class_16572.prevZ - class_16572.getZ()) * (double)Render3DEngine.getTickDelta() - Radar.mc.player.getZ()) * 2.0f;
                float cos = (float)Math.cos((double)Radar.mc.player.getYaw(Render3DEngine.getTickDelta()) * 0.017453292);
                float sin = (float)Math.sin((double)Radar.mc.player.getYaw(Render3DEngine.getTickDelta()) * 0.017453292);
                float rotY = -(posZ * cos - f * sin);
                float rotX = -(f * cos + posZ * sin);
                if (rotY > (float)this.size.getValue().intValue() / 2.0f - 6.0f) {
                    rotY = (float)this.size.getValue().intValue() / 2.0f - 6.0f;
                } else if (rotY < -((float)this.size.getValue().intValue() / 2.0f - 8.0f)) {
                    rotY = -((float)this.size.getValue().intValue() / 2.0f - 8.0f);
                }
                if (rotX > (float)this.size.getValue().intValue() / 2.0f - 5.0f) {
                    rotX = (float)this.size.getValue().intValue() / 2.0f - 5.0f;
                } else if (rotX < -((float)this.size.getValue().intValue() / 2.0f - 5.0f)) {
                    rotX = -((float)this.size.getValue().intValue() / 2.0f - 5.0f);
                }
                Render2DEngine.drawRound(context.getMatrices(), this.getPosX() + (float)this.size.getValue().intValue() / 2.0f + rotX - 2.0f, this.getPosY() + (float)this.size.getValue().intValue() / 2.0f + rotY - 2.0f, 4.0f, 4.0f, 2.0f, this.color3.getValue().getColorObject());
            }
        }
        if (this.mode.getValue() == Mode.Text) {
            float offset_y = 0.0f;
            for (PlayerEntity class_16573 : Managers.ASYNC.getAsyncPlayers()) {
                if (class_16573 == Radar.mc.player) continue;
                String str = String.format("%s %s %s %s %s", this.getText(this.c1, class_16573), this.getText(this.c2, class_16573), this.getText(this.c3, class_16573), this.getText(this.c4, class_16573), this.getText(this.c5, class_16573));
                if (this.colorMode.getValue() == ColorMode.Sync) {
                    FontRenderers.sf_bold.drawString(context.getMatrices(), str, (double)this.getPosX(), (double)(this.getPosY() + offset_y), HudEditor.getColor((int)(offset_y * 2.0f)).getRGB());
                } else {
                    FontRenderers.sf_bold.drawString(context.getMatrices(), str, (double)this.getPosX(), (double)(this.getPosY() + offset_y), this.color2.getValue().getColor());
                }
                offset_y += FontRenderers.sf_bold.getFontHeight(str);
            }
        }
        this.setBounds(this.getPosX(), this.getPosY(), this.size.getValue().intValue(), this.size.getValue().intValue());
    }

    public String getText(Setting<Component> c, PlayerEntity player) {
        switch (c.getValue().ordinal()) {
            default: {
                return "";
            }
            case 1: {
                int health = (int)Math.ceil(player.getHealth() + player.getAbsorptionAmount());
                return ModuleManager.nameTags.getHealthColor(health) + health + String.valueOf(Formatting.RESET);
            }
            case 0: {
                return String.valueOf(this.c12.getValue()) + player.getName().getString() + String.valueOf(Formatting.RESET);
            }
            case 3: {
                return String.valueOf(this.c22.getValue()) + NameTags.getEntityPing(player) + "ms" + String.valueOf(Formatting.RESET);
            }
            case 2: {
                return String.valueOf(this.c32.getValue()) + (int)Math.ceil(Radar.mc.player.distanceTo((Entity)player)) + "m" + String.valueOf(Formatting.RESET);
            }
            case 4: 
        }
        return String.valueOf(this.c42.getValue()) + Managers.COMBAT.getPops(player) + String.valueOf(Formatting.RESET);
    }

    private static enum Mode {
        Rect,
        Text;

    }

    public static enum ColorMode {
        Sync,
        Custom;

    }

    public static enum Component {
        Name,
        Hp,
        Distance,
        Ping,
        TotemPops,
        None;

    }
}

