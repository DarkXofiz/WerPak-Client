/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.features.modules.render;

import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.IWorldRenderer;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public class BreakHighLight
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Shrink);
    private final Setting<ColorSetting> color = new Setting<ColorSetting>("Color", new ColorSetting(new Color(-1862467584, true)));
    private final Setting<ColorSetting> color2 = new Setting<ColorSetting>("Color2", new ColorSetting(new Color(-196608, true)));
    private final Setting<ColorSetting> ocolor = new Setting<ColorSetting>("OutlineColor", new ColorSetting(new Color(-1874854656, true)));
    private final Setting<ColorSetting> ocolor2 = new Setting<ColorSetting>("OutlineColor2", new ColorSetting(new Color(-13697792, true)));
    private final Setting<ColorSetting> textColor = new Setting<ColorSetting>("TextColor", new ColorSetting(-1));
    private final Setting<Float> lineWidth = new Setting<Float>("LineWidth", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(5.0f));
    private final Setting<Boolean> otherPlayer = new Setting<Boolean>("OtherPlayer", true);
    private float prevProgress;

    public BreakHighLight() {
        super("BreakHighLight", Module.Category.RENDER);
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        BlockHitResult bhr;
        HitResult ItemStackParticleEffect;
        if (BreakHighLight.mc.interactionManager.isBreakingBlock() && BreakHighLight.mc.crosshairTarget != null && (ItemStackParticleEffect = BreakHighLight.mc.crosshairTarget) instanceof BlockHitResult && !BreakHighLight.mc.world.isAir((bhr = (BlockHitResult)ItemStackParticleEffect).getBlockPos())) {
            Box shrunkMineBox = new Box((double)bhr.getBlockPos().getX(), (double)bhr.getBlockPos().getY(), (double)bhr.getBlockPos().getZ(), (double)bhr.getBlockPos().getX(), (double)bhr.getBlockPos().getY(), (double)bhr.getBlockPos().getZ());
            float noom = switch (this.mode.getValue().ordinal()) {
                case 0 -> Render2DEngine.interpolateFloat(this.prevProgress, MathUtility.clamp(BreakHighLight.mc.interactionManager.currentBreakingProgress, 0.0f, 1.0f), Render3DEngine.getTickDelta());
                case 1 -> 1.0f - Render2DEngine.interpolateFloat(this.prevProgress, BreakHighLight.mc.interactionManager.currentBreakingProgress, Render3DEngine.getTickDelta());
                default -> 1.0f;
            };
            Render3DEngine.drawFilledBox(stack, shrunkMineBox.shrink((double)noom, (double)noom, (double)noom).offset(0.5 + (double)noom * 0.5, 0.5 + (double)noom * 0.5, 0.5 + (double)noom * 0.5), Render2DEngine.interpolateColorC(this.color.getValue().getColorObject(), this.color2.getValue().getColorObject(), noom));
            Render3DEngine.drawBoxOutline(shrunkMineBox.shrink((double)noom, (double)noom, (double)noom).offset(0.5 + (double)noom * 0.5, 0.5 + (double)noom * 0.5, 0.5 + (double)noom * 0.5), Render2DEngine.interpolateColorC(this.ocolor.getValue().getColorObject(), this.ocolor2.getValue().getColorObject(), noom), this.lineWidth.getValue().floatValue());
            switch (this.mode.getValue().ordinal()) {
                case 0: {
                    this.prevProgress = noom;
                    break;
                }
                case 1: {
                    this.prevProgress = 1.0f - noom;
                    break;
                }
                default: {
                    this.prevProgress = 1.0f;
                }
            }
        }
        ((IWorldRenderer)BreakHighLight.mc.worldRenderer).getBlockBreakingInfos().forEach((integer, destroyBlockProgress) -> {
            Entity object = BreakHighLight.mc.world.getEntityById(integer.intValue());
            if (object != null && this.otherPlayer.getValue().booleanValue() && !object.getName().equals((Object)BreakHighLight.mc.player.getName())) {
                BlockPos pos = destroyBlockProgress.getPos();
                Render3DEngine.drawTextIn3D(String.valueOf(object.getName().getString()), pos.toCenterPos(), 0.0, 0.1, 0.0, this.textColor.getValue().getColorObject());
                Box shrunkMineBox = new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                float noom = switch (this.mode.getValue().ordinal()) {
                    case 0 -> MathUtility.clamp((float)destroyBlockProgress.getStage() / 10.0f, 0.0f, 1.0f);
                    case 1 -> 1.0f - (float)destroyBlockProgress.getStage() / 10.0f;
                    default -> 1.0f;
                };
                Render3DEngine.drawFilledBox(stack, shrunkMineBox.shrink((double)noom, (double)noom, (double)noom).offset(0.5 + (double)noom * 0.5, 0.5 + (double)noom * 0.5, 0.5 + (double)noom * 0.5), Render2DEngine.interpolateColorC(this.color.getValue().getColorObject(), this.color2.getValue().getColorObject(), noom));
                Render3DEngine.drawBoxOutline(shrunkMineBox.shrink((double)noom, (double)noom, (double)noom).offset(0.5 + (double)noom * 0.5, 0.5 + (double)noom * 0.5, 0.5 + (double)noom * 0.5), Render2DEngine.interpolateColorC(this.ocolor.getValue().getColorObject(), this.ocolor2.getValue().getColorObject(), noom), this.lineWidth.getValue().floatValue());
            }
        });
    }

    private static enum Mode {
        Grow,
        Shrink,
        Static;

    }
}

