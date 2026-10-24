/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.BuiltBuffer
 *  org.joml.Matrix4f
 */
package thunder.hack.utility.render.animation;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.BuiltBuffer;
import org.joml.Matrix4f;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.TextureStorage;

public class CaptureMark {
    private static float espValue = 1.0f;
    private static float prevEspValue;
    private static float espSpeed;
    private static boolean flipSpeed;

    public static void render(Entity target) {
        Camera camera = Module.mc.gameRenderer.getCamera();
        double tPosX = Render2DEngine.interpolate(target.prevX, target.getX(), Render3DEngine.getTickDelta()) - camera.getPos().x;
        double tPosY = Render2DEngine.interpolate(target.prevY, target.getY(), Render3DEngine.getTickDelta()) - camera.getPos().y;
        double tPosZ = Render2DEngine.interpolate(target.prevZ, target.getZ(), Render3DEngine.getTickDelta()) - camera.getPos().z;
        MatrixStack matrices = new MatrixStack();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        matrices.translate(tPosX, tPosY + (double)(target.getEyeHeight(target.getPose()) / 2.0f), tPosZ);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Render2DEngine.interpolateFloat(prevEspValue, espValue, Render3DEngine.getTickDelta())));
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.capture);
        matrices.translate(-0.75, -0.75, -0.01);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, 0.0f, 1.5f, 0.0f).texture(0.0f, 1.0f).color(HudEditor.getColor(90).getRGB());
        buffer.vertex(matrix, 1.5f, 1.5f, 0.0f).texture(1.0f, 1.0f).color(HudEditor.getColor(0).getRGB());
        buffer.vertex(matrix, 1.5f, 0.0f, 0.0f).texture(1.0f, 0.0f).color(HudEditor.getColor(180).getRGB());
        buffer.vertex(matrix, 0.0f, 0.0f, 0.0f).texture(0.0f, 0.0f).color(HudEditor.getColor(270).getRGB());
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static void tick() {
        prevEspValue = espValue;
        espValue += espSpeed;
        if (espSpeed > 25.0f) {
            flipSpeed = true;
        }
        if (espSpeed < -25.0f) {
            flipSpeed = false;
        }
        espSpeed = flipSpeed ? espSpeed - 0.5f : espSpeed + 0.5f;
    }

    static {
        espSpeed = 1.0f;
    }
}

