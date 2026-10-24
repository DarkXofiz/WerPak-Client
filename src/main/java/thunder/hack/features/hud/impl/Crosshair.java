/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.BuiltBuffer
 *  org.joml.Matrix4f
 */
package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.BuiltBuffer;
import org.joml.Matrix4f;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.TextureStorage;
import thunder.hack.utility.render.animation.AnimationUtility;

public class Crosshair
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Circle);
    private final Setting<Boolean> animated = new Setting<Boolean>("Animated", true, v -> this.mode.is(Mode.Default));
    private final Setting<Boolean> dot = new Setting<Boolean>("Dot", false, v -> this.mode.is(Mode.Default));
    private final Setting<Boolean> t = new Setting<Boolean>("T", false, v -> this.mode.is(Mode.Default));
    private final Setting<ColorMode> colorMode = new Setting<ColorMode>("ColorMode", ColorMode.Sync);
    public final Setting<ColorSetting> color = new Setting<ColorSetting>("Color", new ColorSetting(575714484));
    private final Setting<Boolean> dynamic = new Setting<Boolean>("Dynamic", true);
    private final Setting<Float> range = new Setting<Float>("Range", Float.valueOf(30.0f), Float.valueOf(0.1f), Float.valueOf(120.0f));
    private final Setting<Float> speed = new Setting<Float>("Speed", Float.valueOf(3.0f), Float.valueOf(0.1f), Float.valueOf(20.0f));
    private final Setting<Float> backSpeed = new Setting<Float>("BackSpeed", Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(20.0f));
    private float xAnim;
    private float yAnim;
    private float prevPitch;
    private float prevProgress;

    public Crosshair() {
        super("Crosshair", Module.Category.HUD);
    }

    @Override
    public void onRender2D(DrawContext context) {
        float progress;
        if (!Crosshair.mc.options.getPerspective().isFirstPerson()) {
            return;
        }
        float midX = (float)mc.getWindow().getScaledWidth() / 2.0f;
        float midY = (float)mc.getWindow().getScaledHeight() / 2.0f;
        float yawDelta = Crosshair.mc.player.prevHeadYaw - Crosshair.mc.player.getHeadYaw();
        float pitchDelta = this.prevPitch - Crosshair.mc.player.getPitch();
        this.xAnim = yawDelta > 0.0f ? AnimationUtility.fast(this.xAnim, midX - this.range.getValue().floatValue(), this.speed.getValue().floatValue()) : (yawDelta < 0.0f ? AnimationUtility.fast(this.xAnim, midX + this.range.getValue().floatValue(), this.speed.getValue().floatValue()) : AnimationUtility.fast(this.xAnim, midX, this.backSpeed.getValue().floatValue()));
        this.yAnim = pitchDelta > 0.0f ? AnimationUtility.fast(this.yAnim, midY - this.range.getValue().floatValue(), this.speed.getValue().floatValue()) : (pitchDelta < 0.0f ? AnimationUtility.fast(this.yAnim, midY + this.range.getValue().floatValue(), this.speed.getValue().floatValue()) : AnimationUtility.fast(this.yAnim, midY, this.backSpeed.getValue().floatValue()));
        this.prevPitch = Crosshair.mc.player.getPitch();
        if (!this.dynamic.getValue().booleanValue()) {
            this.xAnim = midX;
            this.yAnim = midY;
        }
        progress = (progress = 360.0f * Crosshair.mc.player.getAttackCooldownProgress(0.5f)) == 0.0f ? 360.0f : progress;
        switch (this.mode.getValue().ordinal()) {
            case 0: {
                Color c1 = this.colorMode.getValue() == ColorMode.Sync ? HudEditor.hcolor1.getValue().getColorObject() : this.color.getValue().getColorObject();
                Color c2 = this.colorMode.getValue() == ColorMode.Sync ? HudEditor.acolor.getValue().getColorObject() : this.color.getValue().getColorObject();
                Render2DEngine.drawArc(context.getMatrices(), this.xAnim - 25.0f, this.yAnim - 25.0f, 50.0f, 50.0f, 0.05f, 0.12f, 0.0f, Render2DEngine.interpolateFloat(this.prevProgress, progress, Render3DEngine.getTickDelta()), c1, c2);
                this.prevProgress = progress;
                break;
            }
            case 1: {
                Color color = this.color.getValue().getColorObject();
                context.getMatrices().push();
                context.getMatrices().translate(this.xAnim, this.yAnim, 0.0f);
                context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotation((float)(System.currentTimeMillis() % 70000L) / 70000.0f * 360.0f));
                context.getMatrices().translate(-this.xAnim, -this.yAnim, 0.0f);
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim - 0.75f, this.yAnim - 5.0f, 1.5f, 10.0f, color);
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim - 5.0f, this.yAnim - 0.75f, 10.0f, 1.5f, color);
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim, this.yAnim - 5.0f, 5.0f, 1.5f, color);
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim - 5.0f, this.yAnim + 4.0f, 5.25f, 1.5f, color);
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim - 5.0f, this.yAnim - 5.0f, 1.5f, 4.25f, color);
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim + 3.5f, this.yAnim, 1.5f, 5.5f, color);
                context.getMatrices().pop();
                break;
            }
            case 2: {
                context.getMatrices().push();
                context.getMatrices().translate(this.xAnim + 4.0f, this.yAnim + 4.0f, 0.0f);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
                RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
                BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.firefly);
                Color color1 = this.colorMode.getValue() == ColorMode.Sync ? HudEditor.getColor(1) : this.color.getValue().getColorObject();
                Matrix4f posMatrix = context.getMatrices().peek().getPositionMatrix();
                bufferBuilder.vertex(posMatrix, 0.0f, -8.0f, 0.0f).texture(0.0f, 1.0f).color(color1.getRGB());
                bufferBuilder.vertex(posMatrix, -8.0f, -8.0f, 0.0f).texture(1.0f, 1.0f).color(color1.getRGB());
                bufferBuilder.vertex(posMatrix, -8.0f, 0.0f, 0.0f).texture(1.0f, 0.0f).color(color1.getRGB());
                bufferBuilder.vertex(posMatrix, 0.0f, 0.0f, 0.0f).texture(0.0f, 0.0f).color(color1.getRGB());
                BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
                context.getMatrices().pop();
                break;
            }
            case 3: {
                Color color = this.color.getValue().getColorObject();
                float offset = this.animated.getValue() != false ? -3.0f + Render2DEngine.interpolateFloat(this.prevProgress, progress, Render3DEngine.getTickDelta()) / 100.0f : 0.0f;
                this.prevProgress = progress;
                if (!this.t.getValue().booleanValue()) {
                    Render2DEngine.drawRect(context.getMatrices(), this.xAnim - 1.0f, this.yAnim - 6.0f + offset, 2.0f, 4.0f, Color.BLACK);
                    Render2DEngine.drawRect(context.getMatrices(), this.xAnim - 0.5f, this.yAnim - 5.5f + offset, 1.0f, 3.0f, color);
                }
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim - 1.0f, this.yAnim + 2.0f - offset, 2.0f, 4.0f, Color.BLACK);
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim - 0.5f, this.yAnim + 2.5f - offset, 1.0f, 3.0f, color);
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim - 6.0f + offset, this.yAnim - 1.0f, 4.0f, 2.0f, Color.BLACK);
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim - 5.5f + offset, this.yAnim - 0.5f, 3.0f, 1.0f, color);
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim + 2.0f - offset, this.yAnim - 1.0f, 4.0f, 2.0f, Color.BLACK);
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim + 2.5f - offset, this.yAnim - 0.5f, 3.0f, 1.0f, color);
                if (!this.dot.getValue().booleanValue()) break;
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim - 1.0f, this.yAnim - 1.0f, 2.0f, 2.0f, Color.BLACK);
                Render2DEngine.drawRect(context.getMatrices(), this.xAnim - 0.5f, this.yAnim - 0.5f, 1.0f, 1.0f, color);
            }
        }
    }

    public float getAnimatedPosX() {
        if (this.xAnim == 0.0f) {
            return (float)mc.getWindow().getScaledWidth() / 2.0f;
        }
        return this.xAnim;
    }

    public float getAnimatedPosY() {
        if (this.yAnim == 0.0f) {
            return (float)mc.getWindow().getScaledHeight() / 2.0f;
        }
        return this.yAnim;
    }

    private static enum Mode {
        Circle,
        WiseTree,
        Dot,
        Default;

    }

    private static enum ColorMode {
        Custom,
        Sync;

    }
}

