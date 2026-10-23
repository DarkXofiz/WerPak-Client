/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Matrix4f
 */
package thunder.hack.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public class SantaHat
extends Module {
    public final Setting<ColorSetting> hatColor = new Setting<ColorSetting>("HatColor", new ColorSetting(new Color(200, 0, 0, 230).getRGB()));
    public final Setting<ColorSetting> fluffColor = new Setting<ColorSetting>("FluffColor", new ColorSetting(new Color(255, 255, 255, 230).getRGB()));

    public SantaHat() {
        super("SantaHat", Module.Category.RENDER);
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        if (SantaHat.mc.player == null) {
            return;
        }
        if (SantaHat.mc.options.getPerspective().isFirstPerson()) {
            return;
        }
        double x = SantaHat.mc.player.prevX + (SantaHat.mc.player.getX() - SantaHat.mc.player.prevX) * (double)Render3DEngine.getTickDelta() - SantaHat.mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = SantaHat.mc.player.prevY + (SantaHat.mc.player.getY() - SantaHat.mc.player.prevY) * (double)Render3DEngine.getTickDelta() - SantaHat.mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = SantaHat.mc.player.prevZ + (SantaHat.mc.player.getZ() - SantaHat.mc.player.prevZ) * (double)Render3DEngine.getTickDelta() - SantaHat.mc.getEntityRenderDispatcher().camera.getPos().getZ();
        stack.push();
        stack.translate(x, y + (double)SantaHat.mc.player.getStandingEyeHeight() + 0.1, z);
        float yaw = SantaHat.mc.player.prevHeadYaw + (SantaHat.mc.player.headYaw - SantaHat.mc.player.prevHeadYaw) * Render3DEngine.getTickDelta();
        float pitch = SantaHat.mc.player.prevPitch + (SantaHat.mc.player.getPitch() - SantaHat.mc.player.prevPitch) * Render3DEngine.getTickDelta();
        stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(yaw));
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
        if (SantaHat.mc.player.isSneaking()) {
            stack.translate(0.0, -0.2, 0.0);
        }
        Render3DEngine.setupRender();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator tessellator = Tessellator.getInstance();
        Matrix4f matrix = stack.peek().getPositionMatrix();
        Color hatAwt = new Color(this.hatColor.getValue().getColor(), true);
        float hR = (float)hatAwt.getRed() / 255.0f;
        float hG = (float)hatAwt.getGreen() / 255.0f;
        float hB = (float)hatAwt.getBlue() / 255.0f;
        float hA = (float)hatAwt.getAlpha() / 255.0f;
        float topR = Math.min(1.0f, hR + 0.1f);
        float topG = Math.min(1.0f, hG + 0.1f);
        float topB = Math.min(1.0f, hB + 0.1f);
        Color fluffAwt = new Color(this.fluffColor.getValue().getColor(), true);
        float fR = (float)fluffAwt.getRed() / 255.0f;
        float fG = (float)fluffAwt.getGreen() / 255.0f;
        float fB = (float)fluffAwt.getBlue() / 255.0f;
        float fA = (float)fluffAwt.getAlpha() / 255.0f;
        float height = 0.45f;
        float baseRadius = 0.26f;
        int segments = 36;
        int stacks = 10;
        for (int j = 0; j < stacks; ++j) {
            float p1 = (float)j / (float)stacks;
            float p2 = (float)(j + 1) / (float)stacks;
            float r1 = baseRadius * (1.0f - p1);
            float r2 = baseRadius * (1.0f - p2);
            float y1 = height * p1;
            float y2 = height * p2;
            float bZ1 = -0.25f * p1 * p1;
            float bZ2 = -0.25f * p2 * p2;
            float cR1 = hR + (topR - hR) * p1;
            float cG1 = hG + (topG - hG) * p1;
            float cB1 = hB + (topB - hB) * p1;
            float cR2 = hR + (topR - hR) * p2;
            float cG2 = hG + (topG - hG) * p2;
            float cB2 = hB + (topB - hB) * p2;
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            for (int i = 0; i <= segments; ++i) {
                float angle = (float)((double)(i * 2) * Math.PI / (double)segments);
                float sin = (float)Math.sin(angle);
                float cos = (float)Math.cos(angle);
                bufferBuilder.vertex(matrix, cos * r2, y2, sin * r2 + bZ2).color(cR2, cG2, cB2, hA);
                bufferBuilder.vertex(matrix, cos * r1, y1, sin * r1 + bZ1).color(cR1, cG1, cB1, hA);
            }
            Render2DEngine.endBuilding(bufferBuilder);
        }
        float trimHeight = 0.12f;
        float trimRadius = baseRadius + 0.08f;
        int trimStacks = 4;
        for (int j = 0; j < trimStacks; ++j) {
            float p1 = (float)j / (float)trimStacks;
            float p2 = (float)(j + 1) / (float)trimStacks;
            float y1 = -trimHeight * p1;
            float y2 = -trimHeight * p2;
            float tr1 = baseRadius + (trimRadius - baseRadius) * (float)Math.sin((double)p1 * Math.PI);
            float tr2 = baseRadius + (trimRadius - baseRadius) * (float)Math.sin((double)p2 * Math.PI);
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            for (int i = 0; i <= segments; ++i) {
                float angle = (float)((double)(i * 2) * Math.PI / (double)segments);
                float sin = (float)Math.sin(angle);
                float cos = (float)Math.cos(angle);
                bufferBuilder.vertex(matrix, cos * tr2, y2, sin * tr2).color(fR, fG, fB, fA);
                bufferBuilder.vertex(matrix, cos * tr1, y1, sin * tr1).color(fR, fG, fB, fA);
            }
            Render2DEngine.endBuilding(bufferBuilder);
        }
        stack.translate(0.0f, height, -0.25f);
        matrix = stack.peek().getPositionMatrix();
        float pomRadius = 0.1f;
        int sphereSegments = 32;
        for (int i = 0; i < sphereSegments; ++i) {
            float lat0 = (float)(Math.PI * (-0.5 + (double)i / (double)sphereSegments));
            float lat1 = (float)(Math.PI * (-0.5 + (double)(i + 1) / (double)sphereSegments));
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            for (int j = 0; j <= sphereSegments; ++j) {
                float lng = (float)(Math.PI * 2 * (double)(j - 1) / (double)sphereSegments);
                float px = (float)(Math.cos(lat0) * Math.cos(lng));
                float py = (float)Math.sin(lat0);
                float pz = (float)(Math.cos(lat0) * Math.sin(lng));
                bufferBuilder.vertex(matrix, px * pomRadius, py * pomRadius, pz * pomRadius).color(fR, fG, fB, fA);
                px = (float)(Math.cos(lat1) * Math.cos(lng));
                py = (float)Math.sin(lat1);
                pz = (float)(Math.cos(lat1) * Math.sin(lng));
                bufferBuilder.vertex(matrix, px * pomRadius, py * pomRadius, pz * pomRadius).color(fR, fG, fB, fA);
            }
            Render2DEngine.endBuilding(bufferBuilder);
        }
        RenderSystem.enableCull();
        Render3DEngine.endRender();
        stack.pop();
    }
}

