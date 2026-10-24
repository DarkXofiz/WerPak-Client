/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 */
package thunder.hack.features.hud.impl;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import thunder.hack.core.Managers;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.MSAAFramebuffer;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public class RadarRewrite
extends HudElement {
    private final Setting<Boolean> glow = new Setting<Boolean>("Glow", false);
    private final Setting<Float> width = new Setting<Float>("Height", Float.valueOf(2.28f), Float.valueOf(0.1f), Float.valueOf(5.0f));
    private final Setting<Float> down = new Setting<Float>("Down", Float.valueOf(3.63f), Float.valueOf(0.1f), Float.valueOf(20.0f));
    private final Setting<Float> tracerWidth = new Setting<Float>("Width", Float.valueOf(0.44f), Float.valueOf(0.0f), Float.valueOf(8.0f));
    private final Setting<Integer> xOffset = new Setting<Integer>("TracerRadius", 68, 20, 100);
    private final Setting<Integer> pitchLock = new Setting<Integer>("PitchLock", 42, 0, 90);
    private final Setting<triangleModeEn> triangleMode = new Setting<triangleModeEn>("TracerCMode", triangleModeEn.Astolfo);
    private final Setting<Float> CRadius = new Setting<Float>("CompassRadius", Float.valueOf(47.0f), Float.valueOf(0.1f), Float.valueOf(70.0f));
    private final Setting<ColorSetting> ciColor = new Setting<ColorSetting>("Circle", new ColorSetting(new Color(0xFFFFFF)));
    private final Setting<ColorSetting> colorf = new Setting<ColorSetting>("Friend", new ColorSetting(new Color(59392)));
    private final Setting<ColorSetting> colors = new Setting<ColorSetting>("Tracer", new ColorSetting(new Color(0xFFFF00)));

    public RadarRewrite() {
        super("AkrienRadar", 50, 50);
    }

    public static float getRotations(Entity entity) {
        if (RadarRewrite.mc.player == null) {
            return 0.0f;
        }
        double x = RadarRewrite.interp(entity.getPos().x, entity.prevX) - RadarRewrite.interp(RadarRewrite.mc.player.getPos().x, RadarRewrite.mc.player.prevX);
        double z = RadarRewrite.interp(entity.getPos().z, entity.prevZ) - RadarRewrite.interp(RadarRewrite.mc.player.getPos().z, RadarRewrite.mc.player.prevZ);
        return (float)(-(Math.atan2(x, z) * 57.29577951308232));
    }

    public static double interp(double d, double d2) {
        return d2 + (d - d2) * (double)Render3DEngine.getTickDelta();
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        if (RadarRewrite.fullNullCheck()) {
            return;
        }
        float middleW = (float)mc.getWindow().getScaledWidth() * this.getX();
        float middleH = (float)mc.getWindow().getScaledHeight() * this.getY();
        MSAAFramebuffer.use(false, () -> {
            context.getMatrices().push();
            this.renderCompass(context.getMatrices(), middleW + this.CRadius.getValue().floatValue(), middleH + this.CRadius.getValue().floatValue());
            context.getMatrices().pop();
            int color = 0;
            context.getMatrices().push();
            context.getMatrices().translate(middleW + this.CRadius.getValue().floatValue(), middleH + this.CRadius.getValue().floatValue(), 0.0f);
            context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f / Math.abs(90.0f / MathUtility.clamp(RadarRewrite.mc.player.getPitch(), (float)this.pitchLock.getValue().intValue(), 90.0f)) - 102.0f));
            context.getMatrices().translate(-(middleW + this.CRadius.getValue().floatValue()), -(middleH + this.CRadius.getValue().floatValue()), 0.0f);
            for (PlayerEntity e : Lists.newArrayList((Iterable)RadarRewrite.mc.world.getPlayers())) {
                if (e == RadarRewrite.mc.player) continue;
                context.getMatrices().push();
                float yaw = RadarRewrite.getRotations((Entity)e) - RadarRewrite.mc.player.getYaw();
                context.getMatrices().translate(middleW + this.CRadius.getValue().floatValue(), middleH + this.CRadius.getValue().floatValue(), 0.0f);
                context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(yaw));
                context.getMatrices().translate(-(middleW + this.CRadius.getValue().floatValue()), -(middleH + this.CRadius.getValue().floatValue()), 0.0f);
                if (Managers.FRIEND.isFriend(e)) {
                    color = this.colorf.getValue().getColor();
                } else {
                    color = switch (this.triangleMode.getValue().ordinal()) {
                        default -> throw new MatchException(null, null);
                        case 0 -> this.colors.getValue().getColor();
                        case 1 -> Render2DEngine.astolfo(false, 1).getRGB();
                    };
                }
                Render2DEngine.drawTracerPointer(context.getMatrices(), middleW + this.CRadius.getValue().floatValue(), middleH - (float)this.xOffset.getValue().intValue() + this.CRadius.getValue().floatValue(), this.width.getValue().floatValue() * 5.0f, this.tracerWidth.getValue().floatValue(), this.down.getValue().floatValue(), true, this.glow.getValue(), color);
                context.getMatrices().translate(middleW + this.CRadius.getValue().floatValue(), middleH + this.CRadius.getValue().floatValue(), 0.0f);
                context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-yaw));
                context.getMatrices().translate(-(middleW + this.CRadius.getValue().floatValue()), -(middleH + this.CRadius.getValue().floatValue()), 0.0f);
                context.getMatrices().pop();
            }
            context.getMatrices().pop();
        });
        this.setBounds(this.getPosX(), this.getPosY(), (int)(this.CRadius.getValue().floatValue() * 2.0f), (int)(this.CRadius.getValue().floatValue() * 2.0f));
    }

    public void renderCompass(MatrixStack matrices, float x, float y) {
        float pitchFactor = Math.abs(90.0f / MathUtility.clamp(RadarRewrite.mc.player.getPitch(), (float)this.pitchLock.getValue().intValue(), 90.0f));
        this.drawEllipsCompas(matrices, -((int)RadarRewrite.mc.player.getYaw()), x, y, pitchFactor, 1.0f, -2.0f, 1.0f, this.ciColor.getValue().getColorObject(), false);
        this.drawEllipsCompas(matrices, -((int)RadarRewrite.mc.player.getYaw()), x, y, pitchFactor, 1.0f, 0.0f, 3.0f, Color.WHITE, true);
    }

    public void drawEllipsCompas(MatrixStack matrices, int yaw, float x, float y, float x2, float y2, float margin, float width, Color color, boolean Dir) {
        this.drawElipse(matrices, x, y, x2, y2, 15 + yaw, 75 + yaw, margin, width, color, Dir ? "W" : "");
        this.drawElipse(matrices, x, y, x2, y2, 105 + yaw, 165 + yaw, margin, width, color, Dir ? "N" : "");
        this.drawElipse(matrices, x, y, x2, y2, 195 + yaw, 255 + yaw, margin, width, color, Dir ? "E" : "");
        this.drawElipse(matrices, x, y, x2, y2, 285 + yaw, 345 + yaw, margin, width, color, Dir ? "S" : "");
    }

    public void drawElipse(MatrixStack matrices, float x, float y, float rx, float ry, float start, float end, float margin, float width, Color color, String direction) {
        if (start > end) {
            float endOffset = end;
            end = start;
            start = endOffset;
        }
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        float radius = this.CRadius.getValue().floatValue() - margin;
        for (float i = start; i <= end; i += 6.0f) {
            float stage = (i - start) / 360.0f;
            if (!Objects.equals(direction, "")) {
                color = HudEditor.getColor((int)(stage * 500.0f));
            }
            float cos = (float)Math.cos((double)i * Math.PI / 180.0);
            float sin = (float)Math.sin((double)i * Math.PI / 180.0);
            bufferBuilder.vertex(x + cos * (radius / ry), y + sin * (radius / rx), 0.0f).color(color.getRGB());
            bufferBuilder.vertex(x + cos * ((radius - width) / ry), y + sin * ((radius - width) / rx), 0.0f).color(color.getRGB());
        }
        Render2DEngine.endBuilding(bufferBuilder);
        RenderSystem.disableBlend();
        if (!Objects.equals(direction, "")) {
            FontRenderers.getModulesRenderer().drawString(matrices, direction, (double)(x - 2.0f) + Math.cos((double)(start - 15.0f) * Math.PI / 180.0) * (double)(radius / ry), (double)(y - 1.0f) + Math.sin((double)(start - 15.0f) * Math.PI / 180.0) * (double)(radius / rx), -1);
        }
    }

    public static enum triangleModeEn {
        Custom,
        Astolfo;

    }

    public static enum mode2 {
        Custom,
        Astolfo,
        TwoColor;

    }
}

