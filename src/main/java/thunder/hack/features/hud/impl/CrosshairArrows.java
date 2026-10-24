/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.RotationAxis
 */
package thunder.hack.features.hud.impl;

import com.google.common.collect.Lists;
import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.RotationAxis;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.hud.impl.RadarRewrite;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

public class CrosshairArrows
extends HudElement {
    public static Setting<Boolean> glow = new Setting<Boolean>("Glow", false);
    private final Setting<Float> width = new Setting<Float>("Height", Float.valueOf(2.28f), Float.valueOf(0.1f), Float.valueOf(5.0f));
    private final Setting<BooleanSettingGroup> down = new Setting<BooleanSettingGroup>("Down", new BooleanSettingGroup(false));
    private final Setting<Float> downHeight = new Setting<Float>("DownHeight", Float.valueOf(3.63f), Float.valueOf(0.1f), Float.valueOf(20.0f)).addToGroup(this.down);
    private final Setting<Float> tracerWidth = new Setting<Float>("Width", Float.valueOf(0.44f), Float.valueOf(0.0f), Float.valueOf(8.0f));
    private final Setting<Integer> xOffset = new Setting<Integer>("TracerRadius", 68, 20, 100);
    private final Setting<Integer> pitchLock = new Setting<Integer>("PitchLock", 42, 0, 90);
    private final Setting<triangleModeEn> triangleMode = new Setting<triangleModeEn>("TracerCMode", triangleModeEn.Astolfo);
    private final Setting<ColorSetting> colorf = new Setting<ColorSetting>("Friend", new ColorSetting(new Color(59392)));
    private final Setting<ColorSetting> colors = new Setting<ColorSetting>("Tracer", new ColorSetting(new Color(0xFFFF00)));
    private float smoothYaw = 0.0f;

    public CrosshairArrows() {
        super("CrosshairArrows", 0, 0);
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        if (CrosshairArrows.fullNullCheck()) {
            return;
        }
        float middleW = ModuleManager.crosshair.getAnimatedPosX();
        float middleH = ModuleManager.crosshair.getAnimatedPosY();
        int color = 0;
        context.getMatrices().push();
        context.getMatrices().translate(middleW, middleH, 0.0f);
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f / Math.abs(90.0f / MathUtility.clamp(CrosshairArrows.mc.player.getPitch(), (float)this.pitchLock.getValue().intValue(), 90.0f)) - 102.0f));
        context.getMatrices().translate(-middleW, -middleH, 0.0f);
        this.smoothYaw = AnimationUtility.fast(this.smoothYaw, CrosshairArrows.mc.player.getYaw(), 13.0f);
        for (PlayerEntity e : Lists.newArrayList(CrosshairArrows.mc.world.getPlayers())) {
            if (e == CrosshairArrows.mc.player) continue;
            context.getMatrices().push();
            float yaw = RadarRewrite.getRotations((Entity)e) - this.smoothYaw;
            context.getMatrices().translate(middleW, middleH, 0.0f);
            context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(yaw));
            context.getMatrices().translate(-middleW, -middleH, 0.0f);
            if (Managers.FRIEND.isFriend(e)) {
                color = this.colorf.getValue().getColor();
            } else {
                color = switch (this.triangleMode.getValue().ordinal()) {
                    default -> throw new MatchException(null, null);
                    case 0 -> this.colors.getValue().getColor();
                    case 1 -> Render2DEngine.astolfo(false, 1).getRGB();
                };
            }
            Render2DEngine.drawTracerPointer(context.getMatrices(), middleW, middleH - (float)this.xOffset.getValue().intValue(), this.width.getValue().floatValue() * 5.0f, this.tracerWidth.getValue().floatValue(), this.downHeight.getValue().floatValue(), this.down.getValue().isEnabled(), glow.getValue(), color);
            context.getMatrices().translate(middleW, middleH, 0.0f);
            context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-yaw));
            context.getMatrices().translate(-middleW, -middleH, 0.0f);
            context.getMatrices().pop();
        }
        context.getMatrices().pop();
    }

    public static enum triangleModeEn {
        Custom,
        Astolfo;

    }
}

