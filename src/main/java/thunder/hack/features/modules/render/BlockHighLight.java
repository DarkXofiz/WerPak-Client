/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult.Type
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.features.modules.render;

import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public class BlockHighLight
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Outline);
    private final Setting<ColorSetting> color = new Setting<ColorSetting>("Color", new ColorSetting(-1));
    private final Setting<Float> lineWidth = new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f));

    public BlockHighLight() {
        super("BlockHighLight", Module.Category.RENDER);
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        if (BlockHighLight.mc.crosshairTarget == null) {
            return;
        }
        if (BlockHighLight.mc.crosshairTarget.getType() != HitResult.Type.BLOCK) {
            return;
        }
        HitResult ItemStackParticleEffect = BlockHighLight.mc.crosshairTarget;
        if (!(ItemStackParticleEffect instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult bhr = (BlockHitResult)ItemStackParticleEffect;
        switch (this.mode.getValue().ordinal()) {
            case 0: {
                Render3DEngine.drawBoxOutline(new Box(bhr.getBlockPos()), Render2DEngine.injectAlpha(this.color.getValue().getColorObject(), 255), this.lineWidth.getValue().floatValue());
                Render3DEngine.drawFilledBox(stack, new Box(bhr.getBlockPos()), this.color.getValue().getColorObject());
                break;
            }
            case 1: {
                Render3DEngine.drawSideOutline(new Box(bhr.getBlockPos()), Render2DEngine.injectAlpha(this.color.getValue().getColorObject(), 255), this.lineWidth.getValue().floatValue(), bhr.getSide());
                Render3DEngine.drawFilledSide(stack, new Box(bhr.getBlockPos()), this.color.getValue().getColorObject(), bhr.getSide());
                break;
            }
            case 2: {
                Render3DEngine.drawFilledBox(stack, new Box(bhr.getBlockPos()), this.color.getValue().getColorObject());
                break;
            }
            case 3: {
                Render3DEngine.drawFilledSide(stack, new Box(bhr.getBlockPos()), this.color.getValue().getColorObject(), bhr.getSide());
                break;
            }
            case 4: {
                Render3DEngine.drawBoxOutline(new Box(bhr.getBlockPos()), Render2DEngine.injectAlpha(this.color.getValue().getColorObject(), 255), this.lineWidth.getValue().floatValue());
                break;
            }
            case 5: {
                Render3DEngine.drawSideOutline(new Box(bhr.getBlockPos()), Render2DEngine.injectAlpha(this.color.getValue().getColorObject(), 255), this.lineWidth.getValue().floatValue(), bhr.getSide());
            }
        }
    }

    private static enum Mode {
        Both,
        BothSide,
        Fill,
        FilledSide,
        Outline,
        OutlinedSide;

    }
}

