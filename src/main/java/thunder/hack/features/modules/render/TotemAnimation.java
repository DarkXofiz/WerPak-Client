/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider.Immediate
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.model.json.ModelTransformationMode
 */
package thunder.hack.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class TotemAnimation
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.FadeOut);
    private final Setting<Integer> speed = new Setting<Integer>("Speed", 40, 1, 100);
    private ItemStack floatingItem = null;
    private int floatingItemTimeLeft;

    public TotemAnimation() {
        super("TotemAnimation", Module.Category.RENDER);
    }

    public void showFloatingItem(ItemStack floatingItem) {
        this.floatingItem = floatingItem;
        this.floatingItemTimeLeft = this.getTime();
    }

    @Override
    public void onUpdate() {
        if (this.floatingItemTimeLeft > 0) {
            --this.floatingItemTimeLeft;
            if (this.floatingItemTimeLeft == 0) {
                this.floatingItem = null;
            }
        }
    }

    public void renderFloatingItem(float tickDelta) {
        if (this.floatingItem != null && this.floatingItemTimeLeft > 0 && !this.mode.is(Mode.Off)) {
            int scaledWidth = mc.getWindow().getScaledWidth();
            int scaledHeight = mc.getWindow().getScaledHeight();
            int elapsedTime = this.getTime() - this.floatingItemTimeLeft;
            float animationProgress = ((float)elapsedTime + tickDelta) / (float)this.getTime();
            float progressSquared = animationProgress * animationProgress;
            float progressCubed = animationProgress * progressSquared;
            float oscillationFactor = 10.25f * progressCubed * progressSquared - 24.95f * progressSquared * progressSquared + 25.5f * progressCubed - 13.8f * progressSquared + 4.0f * animationProgress;
            float oscillationRadians = oscillationFactor * (float)Math.PI;
            RenderSystem.enableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.push();
            float adjustedProgress = (float)elapsedTime + tickDelta;
            float scale = 50.0f + 175.0f * MathHelper.sin((float)oscillationRadians);
            switch (this.mode.getValue().ordinal()) {
                case 0: {
                    float x2 = (float)(Math.sin(adjustedProgress * 112.0f / 180.0f) * 100.0);
                    float y2 = (float)(Math.cos(adjustedProgress * 112.0f / 180.0f) * 50.0);
                    matrixStack.translate((float)(scaledWidth / 2) + x2, (float)(scaledHeight / 2) + y2, -50.0f);
                    matrixStack.scale(scale, -scale, scale);
                    break;
                }
                case 1: {
                    matrixStack.translate((float)(scaledWidth / 2), (float)(scaledHeight / 2), -50.0f);
                    matrixStack.scale(scale, -scale, scale);
                    break;
                }
                case 2: {
                    matrixStack.translate((float)(scaledWidth / 2), (float)(scaledHeight / 2), -50.0f);
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(adjustedProgress * 2.0f));
                    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(adjustedProgress * 2.0f));
                    matrixStack.scale(200.0f - adjustedProgress * 1.5f, -200.0f + adjustedProgress * 1.5f, 200.0f - adjustedProgress * 1.5f);
                    break;
                }
                case 3: {
                    matrixStack.translate((float)(scaledWidth / 2), (float)(scaledHeight / 2), -50.0f);
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(adjustedProgress * 3.0f));
                    matrixStack.scale(200.0f - adjustedProgress * 1.5f, -200.0f + adjustedProgress * 1.5f, 200.0f - adjustedProgress * 1.5f);
                    break;
                }
                case 4: {
                    float downFactor = (float)(Math.pow(adjustedProgress, 3.0) * (double)0.2f);
                    matrixStack.translate((float)(scaledWidth / 2), (float)(scaledHeight / 2) + downFactor, -50.0f);
                    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(adjustedProgress * 5.0f));
                    matrixStack.scale(200.0f - adjustedProgress * 1.5f, -200.0f + adjustedProgress * 1.5f, 200.0f - adjustedProgress * 1.5f);
                    break;
                }
                case 5: {
                    float downFactor = (float)(Math.pow(adjustedProgress, 3.0) * (double)0.2f) - 20.0f;
                    matrixStack.translate((float)(scaledWidth / 2), (float)(scaledHeight / 2) - downFactor, -50.0f);
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(adjustedProgress * (float)this.floatingItemTimeLeft * 2.0f));
                    matrixStack.scale(200.0f - adjustedProgress * 1.5f, -200.0f + adjustedProgress * 1.5f, 200.0f - adjustedProgress * 1.5f);
                    break;
                }
                case 6: {
                    float rightFactor = (float)(Math.pow(adjustedProgress, 2.0) * 4.5);
                    matrixStack.translate((float)(scaledWidth / 2) + rightFactor, (float)(scaledHeight / 2), -50.0f);
                    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(adjustedProgress * 40.0f));
                    matrixStack.scale(200.0f - adjustedProgress * 1.5f, -200.0f + adjustedProgress * 1.5f, 200.0f - adjustedProgress * 1.5f);
                }
            }
            VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)(1.0f - animationProgress));
            mc.getItemRenderer().renderItem(this.floatingItem, ModelTransformationMode.FIXED, 0xF000F0, OverlayTexture.DEFAULT_UV, matrixStack, (VertexConsumerProvider)immediate, (World)TotemAnimation.mc.world, 0);
            matrixStack.pop();
            immediate.draw();
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.disableDepthTest();
        }
    }

    private int getTime() {
        int invertedSpeed = 101 - this.speed.getValue();
        if (this.mode.is(Mode.FadeOut)) {
            return invertedSpeed / 4;
        }
        if (this.mode.is(Mode.Insert)) {
            return invertedSpeed / 2;
        }
        return invertedSpeed;
    }

    private static enum Mode {
        FadeOut,
        Size,
        Otkisuli,
        Insert,
        Fall,
        Rocket,
        Roll,
        Off;

    }
}

