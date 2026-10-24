/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack.Entry
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.BuiltBuffer
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 *  org.lwjgl.opengl.GL11
 */
package thunder.hack.utility.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.BuiltBuffer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import thunder.hack.ThunderHack;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

public class Render3DEngine {
    public static List<FillAction> FILLED_QUEUE = new ArrayList<FillAction>();
    public static List<OutlineAction> OUTLINE_QUEUE = new ArrayList<OutlineAction>();
    public static List<FadeAction> FADE_QUEUE = new ArrayList<FadeAction>();
    public static List<FillSideAction> FILLED_SIDE_QUEUE = new ArrayList<FillSideAction>();
    public static List<OutlineSideAction> OUTLINE_SIDE_QUEUE = new ArrayList<OutlineSideAction>();
    public static List<DebugLineAction> DEBUG_LINE_QUEUE = new ArrayList<DebugLineAction>();
    public static List<LineAction> LINE_QUEUE = new ArrayList<LineAction>();
    public static final Matrix4f lastProjMat = new Matrix4f();
    public static final Matrix4f lastModMat = new Matrix4f();
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();
    private static float prevCircleStep;
    private static float circleStep;

    public static void onRender3D(MatrixStack stack) {
        BufferBuilder buffer;
        Tessellator tessellator;
        if (!(FILLED_QUEUE.isEmpty() && FADE_QUEUE.isEmpty() && FILLED_SIDE_QUEUE.isEmpty())) {
            tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            RenderSystem.disableDepthTest();
            Render3DEngine.setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            FILLED_QUEUE.forEach(action -> Render3DEngine.setFilledBoxVertexes(bufferBuilder, stack.peek().getPositionMatrix(), action.box(), action.color()));
            FADE_QUEUE.forEach(action -> Render3DEngine.setFilledFadePoints(action.box(), bufferBuilder, stack.peek().getPositionMatrix(), action.color(), action.color2()));
            FILLED_SIDE_QUEUE.forEach(action -> Render3DEngine.setFilledSidePoints(bufferBuilder, stack.peek().getPositionMatrix(), action.box, action.color(), action.side()));
            Render2DEngine.endBuilding(bufferBuilder);
            Render3DEngine.endRender();
            RenderSystem.enableDepthTest();
            FADE_QUEUE.clear();
            FILLED_SIDE_QUEUE.clear();
            FILLED_QUEUE.clear();
        }
        if (!OUTLINE_QUEUE.isEmpty() || !OUTLINE_SIDE_QUEUE.isEmpty()) {
            Render3DEngine.setupRender();
            tessellator = Tessellator.getInstance();
            buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            RenderSystem.lineWidth((float)2.0f);
            final BufferBuilder fbOutline = buffer;
            OUTLINE_QUEUE.forEach(action -> Render3DEngine.setOutlinePoints(action.box(), Render3DEngine.matrixFrom(action.box().minX, action.box().minY, action.box().minZ), fbOutline, action.color()));
            OUTLINE_SIDE_QUEUE.forEach(action -> Render3DEngine.setSideOutlinePoints(action.box, Render3DEngine.matrixFrom(action.box().minX, action.box().minY, action.box().minZ), fbOutline, action.color(), action.side()));
            Render2DEngine.endBuilding(buffer);
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
            Render3DEngine.endRender();
            OUTLINE_QUEUE.clear();
            OUTLINE_SIDE_QUEUE.clear();
        }
        if (!DEBUG_LINE_QUEUE.isEmpty()) {
            Render3DEngine.setupRender();
            RenderSystem.disableDepthTest();
            tessellator = Tessellator.getInstance();
            buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.LINES);
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            final BufferBuilder fbLine1 = buffer;
            DEBUG_LINE_QUEUE.forEach(action -> {
                MatrixStack matrices = Render3DEngine.matrixFrom(action.start.getX(), action.start.getY(), action.start.getZ());
                Render3DEngine.vertexLine(matrices, (VertexConsumer)fbLine1, 0.0f, 0.0f, 0.0f, (float)(action.end.getX() - action.start.getX()), (float)(action.end.getY() - action.start.getY()), (float)(action.end.getZ() - action.start.getZ()), action.color);
            });
            Render2DEngine.endBuilding(buffer);
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
            Render3DEngine.endRender();
            DEBUG_LINE_QUEUE.clear();
        }
        if (!LINE_QUEUE.isEmpty()) {
            Render3DEngine.setupRender();
            tessellator = Tessellator.getInstance();
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            RenderSystem.lineWidth((float)2.0f);
            RenderSystem.disableDepthTest();
            buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
            final BufferBuilder fbLine2 = buffer;
            LINE_QUEUE.forEach(action -> {
                MatrixStack matrices = Render3DEngine.matrixFrom(action.start.getX(), action.start.getY(), action.start.getZ());
                Render3DEngine.vertexLine(matrices, (VertexConsumer)fbLine2, 0.0f, 0.0f, 0.0f, (float)(action.end.getX() - action.start.getX()), (float)(action.end.getY() - action.start.getY()), (float)(action.end.getZ() - action.start.getZ()), action.color);
            });
            Render2DEngine.endBuilding(buffer);
            RenderSystem.enableCull();
            RenderSystem.lineWidth((float)1.0f);
            RenderSystem.enableDepthTest();
            Render3DEngine.endRender();
            LINE_QUEUE.clear();
        }
    }

    @Deprecated
    public static void drawFilledBox(MatrixStack stack, Box box, Color c) {
        FILLED_QUEUE.add(new FillAction(box, c));
    }

    public static void setFilledBoxVertexes(@NotNull BufferBuilder bufferBuilder, Matrix4f m, @NotNull Box box, @NotNull Color c) {
        float minX = (float)(box.minX - Module.mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float)(box.minY - Module.mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float)(box.minZ - Module.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float)(box.maxX - Module.mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float)(box.maxY - Module.mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float)(box.maxZ - Module.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        bufferBuilder.vertex(m, minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(m, maxX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(m, maxX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(m, minX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(m, minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(m, minX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(m, maxX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(m, maxX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(m, maxX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(m, maxX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(m, maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(m, maxX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(m, minX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(m, maxX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(m, maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(m, minX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(m, minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(m, minX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(m, minX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(m, minX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(m, minX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(m, minX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(m, maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(m, maxX, maxY, minZ).color(c.getRGB());
    }

    @NotNull
    public static Box interpolateBox(@NotNull Box from, @NotNull Box to, float delta) {
        double X = Render2DEngine.interpolate(from.maxX, to.maxX, delta);
        double Y = Render2DEngine.interpolate(from.maxY, to.maxY, delta);
        double Z = Render2DEngine.interpolate(from.maxZ, to.maxZ, delta);
        double X1 = Render2DEngine.interpolate(from.minX, to.minX, delta);
        double Y1 = Render2DEngine.interpolate(from.minY, to.minY, delta);
        double Z1 = Render2DEngine.interpolate(from.minZ, to.minZ, delta);
        return new Box(X1, Y1, Z1, X, Y, Z);
    }

    @Deprecated
    public static void drawFilledSide(MatrixStack stack, @NotNull Box box, Color c, Direction dir) {
        FILLED_SIDE_QUEUE.add(new FillSideAction(box, c, dir));
    }

    public static void setFilledSidePoints(BufferBuilder buffer, Matrix4f matrix, Box box, Color c, Direction dir) {
        float minX = (float)(box.minX - Module.mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float)(box.minY - Module.mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float)(box.minZ - Module.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float)(box.maxX - Module.mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float)(box.maxY - Module.mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float)(box.maxZ - Module.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        if (dir == Direction.DOWN) {
            buffer.vertex(matrix, minX, minY, minZ).color(c.getRGB());
            buffer.vertex(matrix, maxX, minY, minZ).color(c.getRGB());
            buffer.vertex(matrix, maxX, minY, maxZ).color(c.getRGB());
            buffer.vertex(matrix, minX, minY, maxZ).color(c.getRGB());
        }
        if (dir == Direction.NORTH) {
            buffer.vertex(matrix, minX, minY, minZ).color(c.getRGB());
            buffer.vertex(matrix, minX, maxY, minZ).color(c.getRGB());
            buffer.vertex(matrix, maxX, maxY, minZ).color(c.getRGB());
            buffer.vertex(matrix, maxX, minY, minZ).color(c.getRGB());
        }
        if (dir == Direction.EAST) {
            buffer.vertex(matrix, maxX, minY, minZ).color(c.getRGB());
            buffer.vertex(matrix, maxX, maxY, minZ).color(c.getRGB());
            buffer.vertex(matrix, maxX, maxY, maxZ).color(c.getRGB());
            buffer.vertex(matrix, maxX, minY, maxZ).color(c.getRGB());
        }
        if (dir == Direction.SOUTH) {
            buffer.vertex(matrix, minX, minY, maxZ).color(c.getRGB());
            buffer.vertex(matrix, maxX, minY, maxZ).color(c.getRGB());
            buffer.vertex(matrix, maxX, maxY, maxZ).color(c.getRGB());
            buffer.vertex(matrix, minX, maxY, maxZ).color(c.getRGB());
        }
        if (dir == Direction.WEST) {
            buffer.vertex(matrix, minX, minY, minZ).color(c.getRGB());
            buffer.vertex(matrix, minX, minY, maxZ).color(c.getRGB());
            buffer.vertex(matrix, minX, maxY, maxZ).color(c.getRGB());
            buffer.vertex(matrix, minX, maxY, minZ).color(c.getRGB());
        }
        if (dir == Direction.UP) {
            buffer.vertex(matrix, minX, maxY, minZ).color(c.getRGB());
            buffer.vertex(matrix, minX, maxY, maxZ).color(c.getRGB());
            buffer.vertex(matrix, maxX, maxY, maxZ).color(c.getRGB());
            buffer.vertex(matrix, maxX, maxY, minZ).color(c.getRGB());
        }
    }

    public static void drawTextIn3D(String text, @NotNull Vec3d pos, double offX, double offY, double textOffset, @NotNull Color color) {
        MatrixStack matrices = new MatrixStack();
        Camera camera = Module.mc.gameRenderer.getCamera();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        matrices.translate(pos.getX() - camera.getPos().x, pos.getY() - camera.getPos().y, pos.getZ() - camera.getPos().z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        Render3DEngine.setupRender();
        matrices.translate(offX, offY - 0.1, -0.01);
        matrices.scale(-0.025f, -0.025f, 0.0f);
        FontRenderers.sf_medium.drawCenteredString(matrices, text, textOffset, 0.0, color.getRGB());
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        Render3DEngine.endRender();
    }

    @NotNull
    public static Vec3d worldSpaceToScreenSpace(@NotNull Vec3d pos) {
        Camera camera = Module.mc.getEntityRenderDispatcher().camera;
        int displayHeight = Module.mc.getWindow().getHeight();
        int[] viewport = new int[4];
        GL11.glGetIntegerv((int)2978, (int[])viewport);
        Vector3f target = new Vector3f();
        double deltaX = pos.x - camera.getPos().x;
        double deltaY = pos.y - camera.getPos().y;
        double deltaZ = pos.z - camera.getPos().z;
        Vector4f transformedCoordinates = new Vector4f((float)deltaX, (float)deltaY, (float)deltaZ, 1.0f).mul((Matrix4fc)lastWorldSpaceMatrix);
        Matrix4f matrixProj = new Matrix4f((Matrix4fc)lastProjMat);
        Matrix4f matrixModel = new Matrix4f((Matrix4fc)lastModMat);
        matrixProj.mul((Matrix4fc)matrixModel).project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport, target);
        return new Vec3d((double)target.x / Render3DEngine.getScaleFactor(), (double)((float)displayHeight - target.y) / Render3DEngine.getScaleFactor(), (double)target.z);
    }

    public static double getScaleFactor() {
        return ClientSettings.scaleFactorFix.getValue() != false ? (double)ClientSettings.scaleFactorFixValue.getValue().floatValue() : Module.mc.getWindow().getScaleFactor();
    }

    @Deprecated
    public static void drawFilledFadeBox(@NotNull MatrixStack stack, @NotNull Box box, @NotNull Color c, @NotNull Color c1) {
        FADE_QUEUE.add(new FadeAction(box, c, c1));
    }

    public static void setFilledFadePoints(Box box, BufferBuilder buffer, Matrix4f posMatrix, Color c, Color c1) {
        float minX = (float)(box.minX - Module.mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float)(box.minY - Module.mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float)(box.minZ - Module.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float)(box.maxX - Module.mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float)(box.maxY - Module.mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float)(box.maxZ - Module.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        buffer.vertex(posMatrix, minX, minY, minZ).color(c.getRGB());
        buffer.vertex(posMatrix, minX, maxY, minZ).color(c1.getRGB());
        buffer.vertex(posMatrix, maxX, maxY, minZ).color(c1.getRGB());
        buffer.vertex(posMatrix, maxX, minY, minZ).color(c.getRGB());
        buffer.vertex(posMatrix, maxX, minY, minZ).color(c.getRGB());
        buffer.vertex(posMatrix, maxX, maxY, minZ).color(c1.getRGB());
        buffer.vertex(posMatrix, maxX, maxY, maxZ).color(c1.getRGB());
        buffer.vertex(posMatrix, maxX, minY, maxZ).color(c.getRGB());
        buffer.vertex(posMatrix, minX, minY, maxZ).color(c.getRGB());
        buffer.vertex(posMatrix, maxX, minY, maxZ).color(c.getRGB());
        buffer.vertex(posMatrix, maxX, maxY, maxZ).color(c1.getRGB());
        buffer.vertex(posMatrix, minX, maxY, maxZ).color(c1.getRGB());
        buffer.vertex(posMatrix, minX, minY, minZ).color(c.getRGB());
        buffer.vertex(posMatrix, minX, minY, maxZ).color(c.getRGB());
        buffer.vertex(posMatrix, minX, maxY, maxZ).color(c1.getRGB());
        buffer.vertex(posMatrix, minX, maxY, minZ).color(c1.getRGB());
        buffer.vertex(posMatrix, minX, maxY, minZ).color(c1.getRGB());
        buffer.vertex(posMatrix, minX, maxY, maxZ).color(c1.getRGB());
        buffer.vertex(posMatrix, maxX, maxY, maxZ).color(c1.getRGB());
        buffer.vertex(posMatrix, maxX, maxY, minZ).color(c1.getRGB());
    }

    public static void drawLine(@NotNull Vec3d start, @NotNull Vec3d end, @NotNull Color color) {
        LINE_QUEUE.add(new LineAction(start, end, color));
    }

    @Deprecated
    public static void drawBoxOutline(@NotNull Box box, Color color, float lineWidth) {
        OUTLINE_QUEUE.add(new OutlineAction(box, color, lineWidth));
    }

    public static void setOutlinePoints(Box box, MatrixStack matrices, BufferBuilder buffer, Color color) {
        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());
        float x1 = (float)box.minX;
        float y1 = (float)box.minY;
        float z1 = (float)box.minZ;
        float x2 = (float)box.maxX;
        float y2 = (float)box.maxY;
        float z2 = (float)box.maxZ;
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z1, x2, y1, z1, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z1, x2, y1, z2, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z2, x1, y1, z2, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z2, x1, y1, z1, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z2, x1, y2, z2, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z1, x1, y2, z1, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z2, x2, y2, z2, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z1, x2, y2, z1, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y2, z1, x2, y2, z1, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y2, z1, x2, y2, z2, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y2, z2, x1, y2, z2, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y2, z2, x1, y2, z1, color);
    }

    @Deprecated
    public static void drawSideOutline(@NotNull Box box, Color color, float lineWidth, Direction dir) {
        OUTLINE_SIDE_QUEUE.add(new OutlineSideAction(box, color, lineWidth, dir));
    }

    public static void setSideOutlinePoints(Box box, MatrixStack matrices, BufferBuilder buffer, Color color, Direction dir) {
        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());
        float x1 = (float)box.minX;
        float y1 = (float)box.minY;
        float z1 = (float)box.minZ;
        float x2 = (float)box.maxX;
        float y2 = (float)box.maxY;
        float z2 = (float)box.maxZ;
        switch (dir) {
            case UP: {
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y2, z1, x2, y2, z1, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y2, z1, x2, y2, z2, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y2, z2, x1, y2, z2, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y2, z2, x1, y2, z1, color);
                break;
            }
            case DOWN: {
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z1, x2, y1, z1, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z1, x2, y1, z2, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z2, x1, y1, z2, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z2, x1, y1, z1, color);
                break;
            }
            case EAST: {
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z1, x2, y2, z1, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z2, x2, y2, z2, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y2, z2, x2, y2, z1, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z2, x2, y1, z1, color);
                break;
            }
            case WEST: {
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z1, x1, y2, z1, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z2, x1, y2, z2, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y2, z2, x1, y2, z1, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z2, x1, y1, z1, color);
                break;
            }
            case NORTH: {
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z1, x2, y2, z1, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z1, x1, y2, z1, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z1, x1, y1, z1, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y2, z1, x1, y2, z1, color);
                break;
            }
            case SOUTH: {
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z2, x1, y2, z2, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z2, x2, y2, z2, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z2, x2, y1, z2, color);
                Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y2, z2, x2, y2, z2, color);
            }
        }
    }

    public static void drawHoleOutline(@NotNull Box box, Color color, float lineWidth) {
        Render3DEngine.setupRender();
        MatrixStack matrices = Render3DEngine.matrixFrom(box.minX, box.minY, box.minZ);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth((float)lineWidth);
        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());
        float x1 = (float)box.minX;
        float y1 = (float)box.minY;
        float y2 = (float)box.maxY;
        float z1 = (float)box.minZ;
        float x2 = (float)box.maxX;
        float z2 = (float)box.maxZ;
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z1, x2, y1, z1, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z1, x2, y1, z2, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z2, x1, y1, z2, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z2, x1, y1, z1, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z1, x1, y2, z1, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z2, x2, y2, z2, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x1, y1, z2, x1, y2, z2, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, x2, y1, z1, x2, y2, z1, color);
        Render2DEngine.endBuilding(buffer);
        RenderSystem.enableCull();
        Render3DEngine.endRender();
    }

    public static void vertexLine(@NotNull MatrixStack matrices, @NotNull VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, @NotNull Color lineColor) {
        Matrix4f model = matrices.peek().getPositionMatrix();
        MatrixStack.Entry entry = matrices.peek();
        Vector3f normalVec = Render3DEngine.getNormal(x1, y1, z1, x2, y2, z2);
        buffer.vertex(model, x1, y1, z1).color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()).normal(entry, normalVec.x(), normalVec.y(), normalVec.z());
        buffer.vertex(model, x2, y2, z2).color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()).normal(entry, normalVec.x(), normalVec.y(), normalVec.z());
    }

    @NotNull
    public static Vector3f getNormal(float x1, float y1, float z1, float x2, float y2, float z2) {
        float xNormal = x2 - x1;
        float yNormal = y2 - y1;
        float zNormal = z2 - z1;
        float normalSqrt = MathHelper.sqrt((float)(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal));
        return new Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
    }

    @NotNull
    public static MatrixStack matrixFrom(double x, double y, double z) {
        MatrixStack matrices = new MatrixStack();
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);
        return matrices;
    }

    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void endRender() {
        RenderSystem.disableBlend();
    }

    public static void drawTargetEsp(MatrixStack stack, @NotNull Entity target) {
        int j;
        ArrayList<Vec3d> vecs = new ArrayList<Vec3d>();
        ArrayList<Vec3d> vecs1 = new ArrayList<Vec3d>();
        ArrayList<Vec3d> vecs2 = new ArrayList<Vec3d>();
        double x = target.prevX + (target.getX() - target.prevX) * (double)Render3DEngine.getTickDelta() - Module.mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = target.prevY + (target.getY() - target.prevY) * (double)Render3DEngine.getTickDelta() - Module.mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = target.prevZ + (target.getZ() - target.prevZ) * (double)Render3DEngine.getTickDelta() - Module.mc.getEntityRenderDispatcher().camera.getPos().getZ();
        double height = target.getHeight();
        for (int i = 0; i <= 361; ++i) {
            double v = Math.sin(Math.toRadians(i));
            double u = Math.cos(Math.toRadians(i));
            Vec3d vec = new Vec3d((double)((float)(u * 0.5)), height, (double)((float)(v * 0.5)));
            vecs.add(vec);
            double v1 = Math.sin(Math.toRadians((i + 120) % 360));
            double u1 = Math.cos(Math.toRadians(i + 120) % 360.0);
            Vec3d vec1 = new Vec3d((double)((float)(u1 * 0.5)), height, (double)((float)(v1 * 0.5)));
            vecs1.add(vec1);
            double v2 = Math.sin(Math.toRadians((i + 240) % 360));
            double u2 = Math.cos(Math.toRadians((i + 240) % 360));
            Vec3d vec2 = new Vec3d((double)((float)(u2 * 0.5)), height, (double)((float)(v2 * 0.5)));
            vecs2.add(vec2);
            height -= (double)0.004f;
        }
        stack.push();
        stack.translate(x, y, z);
        Render3DEngine.setupRender();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = stack.peek().getPositionMatrix();
        for (j = 0; j < vecs.size() - 1; ++j) {
            float alpha = 1.0f - ((float)j + (float)(System.currentTimeMillis() - ThunderHack.initTime) / 5.0f) % 360.0f / 60.0f;
            bufferBuilder.vertex(matrix, (float)((Vec3d)vecs.get((int)j)).x, (float)((Vec3d)vecs.get((int)j)).y, (float)((Vec3d)vecs.get((int)j)).z).color(Render2DEngine.injectAlpha(HudEditor.getColor((int)((float)j / 20.0f)), (int)(alpha * 255.0f)).getRGB());
            bufferBuilder.vertex(matrix, (float)((Vec3d)vecs.get((int)(j + 1))).x, (float)((Vec3d)vecs.get((int)(j + 1))).y + 0.1f, (float)((Vec3d)vecs.get((int)(j + 1))).z).color(Render2DEngine.injectAlpha(HudEditor.getColor((int)((float)j / 20.0f)), (int)(alpha * 255.0f)).getRGB());
        }
        Render2DEngine.endBuilding(bufferBuilder);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (j = 0; j < vecs1.size() - 1; ++j) {
            float alpha = 1.0f - ((float)j + (float)(System.currentTimeMillis() - ThunderHack.initTime) / 5.0f) % 360.0f / 60.0f;
            bufferBuilder.vertex(matrix, (float)((Vec3d)vecs1.get((int)j)).x, (float)((Vec3d)vecs1.get((int)j)).y, (float)((Vec3d)vecs1.get((int)j)).z).color(Render2DEngine.injectAlpha(HudEditor.getColor((int)((float)j / 20.0f)), (int)(alpha * 255.0f)).getRGB());
            bufferBuilder.vertex(matrix, (float)((Vec3d)vecs1.get((int)(j + 1))).x, (float)((Vec3d)vecs1.get((int)(j + 1))).y + 0.1f, (float)((Vec3d)vecs1.get((int)(j + 1))).z).color(Render2DEngine.injectAlpha(HudEditor.getColor((int)((float)j / 20.0f)), (int)(alpha * 255.0f)).getRGB());
        }
        Render2DEngine.endBuilding(bufferBuilder);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (j = 0; j < vecs2.size() - 1; ++j) {
            float alpha = 1.0f - ((float)j + (float)(System.currentTimeMillis() - ThunderHack.initTime) / 5.0f) % 360.0f / 60.0f;
            bufferBuilder.vertex(matrix, (float)((Vec3d)vecs2.get((int)j)).x, (float)((Vec3d)vecs2.get((int)j)).y, (float)((Vec3d)vecs2.get((int)j)).z).color(Render2DEngine.injectAlpha(HudEditor.getColor((int)((float)j / 20.0f)), (int)(alpha * 255.0f)).getRGB());
            bufferBuilder.vertex(matrix, (float)((Vec3d)vecs2.get((int)(j + 1))).x, (float)((Vec3d)vecs2.get((int)(j + 1))).y + 0.1f, (float)((Vec3d)vecs2.get((int)(j + 1))).z).color(Render2DEngine.injectAlpha(HudEditor.getColor((int)((float)j / 20.0f)), (int)(alpha * 255.0f)).getRGB());
        }
        Render2DEngine.endBuilding(bufferBuilder);
        RenderSystem.enableCull();
        stack.translate(-x, -y, -z);
        Render3DEngine.endRender();
        RenderSystem.enableDepthTest();
        stack.pop();
    }

    public static void renderCrosses(@NotNull Box box, Color color, float lineWidth) {
        Render3DEngine.setupRender();
        MatrixStack matrices = Render3DEngine.matrixFrom(box.minX, box.minY, box.minZ);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth((float)lineWidth);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, (float)box.maxX, (float)box.minY, (float)box.minZ, (float)box.minX, (float)box.minY, (float)box.maxZ, color);
        Render3DEngine.vertexLine(matrices, (VertexConsumer)buffer, (float)box.minX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.minY, (float)box.maxZ, color);
        Render2DEngine.endBuilding(buffer);
        RenderSystem.enableCull();
        Render3DEngine.endRender();
    }

    public static void drawSphere(MatrixStack matrix, float radius, int slices, int stacks, int color) {
        float z;
        float y;
        float x;
        float theta;
        int j;
        BufferBuilder buffer;
        float rho;
        int i;
        float drho = (float)Math.PI / (float)stacks;
        float dtheta = (float)Math.PI * 2 / ((float)slices - 1.0f);
        Render3DEngine.setupRender();
        for (i = 1; i < stacks; ++i) {
            rho = (float)i * drho;
            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            for (j = 0; j < slices; ++j) {
                theta = (float)j * dtheta;
                x = (float)(Math.cos(theta) * Math.sin(rho));
                y = (float)(Math.sin(theta) * Math.sin(rho));
                z = (float)Math.cos(rho);
                buffer.vertex(matrix.peek().getPositionMatrix(), x * radius, y * radius, z * radius).color(color);
            }
            Render2DEngine.endBuilding(buffer);
        }
        for (j = 0; j < slices; ++j) {
            theta = (float)j * dtheta;
            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            for (i = 0; i <= stacks; ++i) {
                rho = (float)i * drho;
                x = (float)(Math.cos(theta) * Math.sin(rho));
                y = (float)(Math.sin(theta) * Math.sin(rho));
                z = (float)Math.cos(rho);
                buffer.vertex(matrix.peek().getPositionMatrix(), x * radius, y * radius, z * radius).color(color);
            }
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        }
        Render3DEngine.endRender();
    }

    public static void drawCylinder(MatrixStack stack, float radius, float height, int slices, int stacks, int color) {
        float da = (float)(Math.PI * 2 / (double)slices);
        float dz = height / (float)stacks;
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        float y = 0.0f;
        for (int j = 0; j <= stacks; ++j) {
            for (int i = 0; i <= slices; ++i) {
                float x = (float)Math.cos((float)i * da);
                float z = (float)Math.sin((float)i * da);
                buffer.vertex(stack.peek().getPositionMatrix(), x * radius, y, z * radius).color(color);
            }
            y += dz;
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        for (int i = 0; i <= slices; ++i) {
            float x = (float)Math.cos((float)i * da);
            float z = (float)Math.sin((float)i * da);
            buffer.vertex(stack.peek().getPositionMatrix(), x * radius, 0.0f, z * radius).color(color);
            buffer.vertex(stack.peek().getPositionMatrix(), x * radius, height, z * radius).color(color);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
    }

    public static void drawCircle3D(MatrixStack stack, Entity ent, float radius, int color, int points, boolean hudColor, int colorOffset) {
        Render3DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        double x = ent.prevX + (ent.getX() - ent.prevX) * (double)Render3DEngine.getTickDelta() - Module.mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = ent.prevY + (ent.getY() - ent.prevY) * (double)Render3DEngine.getTickDelta() - Module.mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = ent.prevZ + (ent.getZ() - ent.prevZ) * (double)Render3DEngine.getTickDelta() - Module.mc.getEntityRenderDispatcher().camera.getPos().getZ();
        stack.push();
        stack.translate(x, y, z);
        Matrix4f matrix = stack.peek().getPositionMatrix();
        for (int i = 0; i <= points; ++i) {
            if (hudColor) {
                color = HudEditor.getColor(i * colorOffset).getRGB();
            }
            bufferBuilder.vertex(matrix, (float)((double)radius * Math.cos((double)i * 6.28 / (double)points)), 0.0f, (float)((double)radius * Math.sin((double)i * 6.28 / (double)points))).color(color);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
        Render3DEngine.endRender();
        stack.translate(-x, -y, -z);
        stack.pop();
    }

    public static void drawOldTargetEsp(MatrixStack stack, Entity target) {
        double cs = prevCircleStep + (circleStep - prevCircleStep) * Render3DEngine.getTickDelta();
        double prevSinAnim = Render3DEngine.absSinAnimation(cs - (double)0.45f);
        double sinAnim = Render3DEngine.absSinAnimation(cs);
        double x = target.prevX + (target.getX() - target.prevX) * (double)Render3DEngine.getTickDelta() - Module.mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = target.prevY + (target.getY() - target.prevY) * (double)Render3DEngine.getTickDelta() - Module.mc.getEntityRenderDispatcher().camera.getPos().getY() + prevSinAnim * (double)target.getHeight();
        double z = target.prevZ + (target.getZ() - target.prevZ) * (double)Render3DEngine.getTickDelta() - Module.mc.getEntityRenderDispatcher().camera.getPos().getZ();
        double nextY = target.prevY + (target.getY() - target.prevY) * (double)Render3DEngine.getTickDelta() - Module.mc.getEntityRenderDispatcher().camera.getPos().getY() + sinAnim * (double)target.getHeight();
        stack.push();
        Render3DEngine.setupRender();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        for (int i = 0; i <= 30; ++i) {
            float cos = (float)(x + Math.cos((double)i * 6.28 / 30.0) * (double)target.getWidth() * 0.8);
            float sin = (float)(z + Math.sin((double)i * 6.28 / 30.0) * (double)target.getWidth() * 0.8);
            bufferBuilder.vertex(stack.peek().getPositionMatrix(), cos, (float)nextY, sin).color(Render2DEngine.injectAlpha(HudEditor.getColor(i), 170).getRGB());
            bufferBuilder.vertex(stack.peek().getPositionMatrix(), cos, (float)y, sin).color(Render2DEngine.injectAlpha(HudEditor.getColor(i), 0).getRGB());
        }
        Render2DEngine.endBuilding(bufferBuilder);
        RenderSystem.enableCull();
        Render3DEngine.endRender();
        RenderSystem.enableDepthTest();
        stack.pop();
    }

    public static void renderGhosts(int espLength, int factor, float shaking, float amplitude, Entity target) {
        Camera camera = Module.mc.gameRenderer.getCamera();
        double tPosX = Render2DEngine.interpolate(target.prevX, target.getX(), Render3DEngine.getTickDelta()) - camera.getPos().x;
        double tPosY = Render2DEngine.interpolate(target.prevY, target.getY(), Render3DEngine.getTickDelta()) - camera.getPos().y;
        double tPosZ = Render2DEngine.interpolate(target.prevZ, target.getZ(), Render3DEngine.getTickDelta()) - camera.getPos().z;
        float iAge = (float)Render2DEngine.interpolate(target.age - 1, target.age, Render3DEngine.getTickDelta());
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.firefly);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        boolean canSee = Module.mc.player.canSee(target);
        if (canSee) {
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask((boolean)false);
        } else {
            RenderSystem.disableDepthTest();
        }
        for (int j = 0; j < 3; ++j) {
            for (int i = 0; i <= espLength; ++i) {
                double radians = Math.toRadians((((float)i / 1.5f + iAge) * (float)factor + (float)(j * 120)) % (float)(factor * 360));
                double sinQuad = Math.sin(Math.toRadians(iAge * 2.5f + (float)(i * (j + 1))) * (double)amplitude) / (double)shaking;
                float offset = (float)i / (float)espLength;
                MatrixStack matrices = new MatrixStack();
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
                matrices.translate(tPosX + Math.cos(radians) * (double)target.getWidth(), tPosY + 1.0 + sinQuad, tPosZ + Math.sin(radians) * (double)target.getWidth());
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                Matrix4f matrix = matrices.peek().getPositionMatrix();
                int color = Render2DEngine.applyOpacity(HudEditor.getColor((int)(180.0f * offset)), offset).getRGB();
                float scale = Math.max(0.24f * offset, 0.2f);
                buffer.vertex(matrix, -scale, scale, 0.0f).texture(0.0f, 1.0f).color(color);
                buffer.vertex(matrix, scale, scale, 0.0f).texture(1.0f, 1.0f).color(color);
                buffer.vertex(matrix, scale, -scale, 0.0f).texture(1.0f, 0.0f).color(color);
                buffer.vertex(matrix, -scale, -scale, 0.0f).texture(0.0f, 0.0f).color(color);
            }
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        if (canSee) {
            RenderSystem.depthMask((boolean)true);
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.disableBlend();
    }

    public static void updateTargetESP() {
        prevCircleStep = circleStep;
        circleStep += 0.15f;
    }

    public static double absSinAnimation(double input) {
        return Math.abs(1.0 + Math.sin(input)) / 2.0;
    }

    public static Vec3d interpolatePos(float prevposX, float prevposY, float prevposZ, float posX, float posY, float posZ) {
        double x = (double)(prevposX + (posX - prevposX) * Render3DEngine.getTickDelta()) - Module.mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = (double)(prevposY + (posY - prevposY) * Render3DEngine.getTickDelta()) - Module.mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = (double)(prevposZ + (posZ - prevposZ) * Render3DEngine.getTickDelta()) - Module.mc.getEntityRenderDispatcher().camera.getPos().getZ();
        return new Vec3d(x, y, z);
    }

    public static void drawLineDebug(Vec3d start, Vec3d end, Color color) {
        DEBUG_LINE_QUEUE.add(new DebugLineAction(start, end, color));
    }

    public static float getTickDelta() {
        return Module.mc.getRenderTickCounter().getTickDelta(true);
    }

    public record FillAction(Box box, Color color) {
    }

    public record FillSideAction(Box box, Color color, Direction side) {
    }

    public record FadeAction(Box box, Color color, Color color2) {
    }

    public record LineAction(Vec3d start, Vec3d end, Color color) {
    }

    public record OutlineAction(Box box, Color color, float lineWidth) {
    }

    public record OutlineSideAction(Box box, Color color, float lineWidth, Direction side) {
    }

    public record DebugLineAction(Vec3d start, Vec3d end, Color color) {
    }
}

