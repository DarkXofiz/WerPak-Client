/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.texture.NativeImageBackedTexture
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.BuiltBuffer
 *  org.apache.commons.lang3.RandomStringUtils
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 *  org.lwjgl.BufferUtils
 */
package thunder.hack.utility.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Stack;
import javax.imageio.ImageIO;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.BuiltBuffer;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.Texture;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.GaussianFilter;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.TextureStorage;
import thunder.hack.utility.render.shaders.ArcShader;
import thunder.hack.utility.render.shaders.BlurProgram;
import thunder.hack.utility.render.shaders.HudShader;
import thunder.hack.utility.render.shaders.MainMenuProgram;
import thunder.hack.utility.render.shaders.RectangleShader;
import thunder.hack.utility.render.shaders.TextureColorProgram;

public class Render2DEngine {
    public static TextureColorProgram TEXTURE_COLOR_PROGRAM;
    public static HudShader HUD_SHADER;
    public static RectangleShader RECTANGLE_SHADER;
    public static MainMenuProgram MAIN_MENU_PROGRAM;
    public static ArcShader ARC_PROGRAM;
    public static BlurProgram BLUR_PROGRAM;
    public static HashMap<Integer, BlurredShadow> shadowCache;
    public static HashMap<Integer, BlurredShadow> shadowCache1;
    static final Stack<Rectangle> clipStack;

    public static void addWindow(MatrixStack stack, Rectangle r1) {
        Matrix4f matrix = stack.peek().getPositionMatrix();
        Vector4f coord = new Vector4f(r1.x, r1.y, 0.0f, 1.0f);
        Vector4f end = new Vector4f(r1.x1, r1.y1, 0.0f, 1.0f);
        coord.mulTranspose((Matrix4fc)matrix);
        end.mulTranspose((Matrix4fc)matrix);
        float x = coord.x();
        float y = coord.y();
        float endX = end.x();
        float endY = end.y();
        Rectangle r = new Rectangle(x, y, endX, endY);
        if (clipStack.empty()) {
            clipStack.push(r);
            Render2DEngine.beginScissor(r.x, r.y, r.x1, r.y1);
        } else {
            Rectangle lastClip = clipStack.peek();
            float lsx = lastClip.x;
            float lsy = lastClip.y;
            float lstx = lastClip.x1;
            float lsty = lastClip.y1;
            float nsx = MathHelper.clamp((float)r.x, (float)lsx, (float)lstx);
            float nsy = MathHelper.clamp((float)r.y, (float)lsy, (float)lsty);
            float nstx = MathHelper.clamp((float)r.x1, (float)nsx, (float)lstx);
            float nsty = MathHelper.clamp((float)r.y1, (float)nsy, (float)lsty);
            clipStack.push(new Rectangle(nsx, nsy, nstx, nsty));
            Render2DEngine.beginScissor(nsx, nsy, nstx, nsty);
        }
    }

    public static void popWindow() {
        clipStack.pop();
        if (clipStack.empty()) {
            Render2DEngine.endScissor();
        } else {
            Rectangle r = clipStack.peek();
            Render2DEngine.beginScissor(r.x, r.y, r.x1, r.y1);
        }
    }

    public static void beginScissor(double x, double y, double endX, double endY) {
        double width = endX - x;
        double height = endY - y;
        width = Math.max(0.0, width);
        height = Math.max(0.0, height);
        float d = (float)Render3DEngine.getScaleFactor();
        int ay = (int)(((double)Module.mc.getWindow().getScaledHeight() - (y + height)) * (double)d);
        RenderSystem.enableScissor((int)((int)(x * (double)d)), (int)ay, (int)((int)(width * (double)d)), (int)((int)(height * (double)d)));
    }

    public static void endScissor() {
        RenderSystem.disableScissor();
    }

    public static void addWindow(MatrixStack stack, float x, float y, float x1, float y1, double animation_factor) {
        float h = y + y1;
        float h2 = (float)((double)h * (1.0 - MathUtility.clamp(animation_factor, 0.0, (double)1.0025f)));
        float x3 = x;
        float y3 = y + h2;
        float x4 = x1;
        float y4 = y1 - h2;
        if (x4 < x3) {
            x4 = x3;
        }
        if (y4 < y3) {
            y4 = y3;
        }
        Render2DEngine.addWindow(stack, new Rectangle(x3, y3, x4, y4));
    }

    public static void horizontalGradient(MatrixStack matrices, float x1, float y1, float x2, float y2, Color startColor, Color endColor) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x1, y1, 0.0f).color(startColor.getRGB());
        buffer.vertex(matrix, x1, y2, 0.0f).color(startColor.getRGB());
        buffer.vertex(matrix, x2, y2, 0.0f).color(endColor.getRGB());
        buffer.vertex(matrix, x2, y1, 0.0f).color(endColor.getRGB());
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        Render2DEngine.endRender();
    }

    public static void verticalGradient(MatrixStack matrices, float left, float top, float right, float bottom, Color startColor, Color endColor) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, left, top, 0.0f).color(startColor.getRGB());
        buffer.vertex(matrix, left, bottom, 0.0f).color(endColor.getRGB());
        buffer.vertex(matrix, right, bottom, 0.0f).color(endColor.getRGB());
        buffer.vertex(matrix, right, top, 0.0f).color(startColor.getRGB());
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        Render2DEngine.endRender();
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, Color c) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x, y + height, 0.0f).color(c.getRGB());
        buffer.vertex(matrix, x + width, y + height, 0.0f).color(c.getRGB());
        buffer.vertex(matrix, x + width, y, 0.0f).color(c.getRGB());
        buffer.vertex(matrix, x, y, 0.0f).color(c.getRGB());
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        Render2DEngine.endRender();
    }

    public static void drawRectWithOutline(MatrixStack matrices, float x, float y, float width, float height, Color c, Color c2) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x, y + height, 0.0f).color(c.getRGB());
        buffer.vertex(matrix, x + width, y + height, 0.0f).color(c.getRGB());
        buffer.vertex(matrix, x + width, y, 0.0f).color(c.getRGB());
        buffer.vertex(matrix, x, y, 0.0f).color(c.getRGB());
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x, y + height, 0.0f).color(c2.getRGB());
        buffer.vertex(matrix, x + width, y + height, 0.0f).color(c2.getRGB());
        buffer.vertex(matrix, x + width, y, 0.0f).color(c2.getRGB());
        buffer.vertex(matrix, x, y, 0.0f).color(c2.getRGB());
        buffer.vertex(matrix, x, y + height, 0.0f).color(c2.getRGB());
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        Render2DEngine.endRender();
    }

    public static void drawRectDumbWay(MatrixStack matrices, float x, float y, float x1, float y1, Color c1) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x, y1, 0.0f).color(c1.getRGB());
        buffer.vertex(matrix, x1, y1, 0.0f).color(c1.getRGB());
        buffer.vertex(matrix, x1, y, 0.0f).color(c1.getRGB());
        buffer.vertex(matrix, x, y, 0.0f).color(c1.getRGB());
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        Render2DEngine.endRender();
    }

    public static void setRectPoints(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float x1, float y1, Color c1, Color c2, Color c3, Color c4) {
        bufferBuilder.vertex(matrix, x, y1, 0.0f).color(c1.getRGB());
        bufferBuilder.vertex(matrix, x1, y1, 0.0f).color(c2.getRGB());
        bufferBuilder.vertex(matrix, x1, y, 0.0f).color(c3.getRGB());
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(c4.getRGB());
    }

    public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX - width <= x && mouseY >= y && mouseY - height <= y;
    }

    public static void drawBlurredShadow(MatrixStack matrices, float x, float y, float width, float height, int blurRadius, Color color) {
        int identifier;
        if (!HudEditor.glow.getValue().booleanValue()) {
            return;
        }
        x -= (float)blurRadius;
        y -= (float)blurRadius;
        if (!shadowCache.containsKey(identifier = (int)((width += (float)(blurRadius * 2)) * (height += (float)(blurRadius * 2)) + width * (float)blurRadius))) {
            BufferedImage original = new BufferedImage((int)width, (int)height, 2);
            Graphics g = original.getGraphics();
            g.setColor(new Color(-1));
            g.fillRect(blurRadius, blurRadius, (int)(width - (float)(blurRadius * 2)), (int)(height - (float)(blurRadius * 2)));
            g.dispose();
            GaussianFilter op = new GaussianFilter(blurRadius);
            BufferedImage blurred = op.filter(original, null);
            shadowCache.put(identifier, new BlurredShadow(blurred));
            return;
        }
        shadowCache.get(identifier).bind();
        Render2DEngine.setupRender();
        RenderSystem.setShaderColor((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
        Render2DEngine.renderTexture(matrices, x, y, width, height, 0.0f, 0.0f, width, height, width, height);
        Render2DEngine.endRender();
    }

    public static void drawGradientBlurredShadow(MatrixStack matrices, float x, float y, float width, float height, int blurRadius, Color color1, Color color2, Color color3, Color color4) {
        int identifier;
        if (!HudEditor.glow.getValue().booleanValue()) {
            return;
        }
        x -= (float)blurRadius;
        y -= (float)blurRadius;
        if (!shadowCache.containsKey(identifier = (int)((width += (float)(blurRadius * 2)) * (height += (float)(blurRadius * 2)) + width * (float)blurRadius))) {
            BufferedImage original = new BufferedImage((int)width, (int)height, 2);
            Graphics g = original.getGraphics();
            g.setColor(new Color(-1));
            g.fillRect(blurRadius, blurRadius, (int)(width - (float)(blurRadius * 2)), (int)(height - (float)(blurRadius * 2)));
            g.dispose();
            GaussianFilter op = new GaussianFilter(blurRadius);
            BufferedImage blurred = op.filter(original, null);
            shadowCache.put(identifier, new BlurredShadow(blurred));
            return;
        }
        shadowCache.get(identifier).bind();
        Render2DEngine.setupRender();
        Render2DEngine.renderGradientTexture(matrices, x, y, width, height, 0.0f, 0.0f, width, height, width, height, color1, color2, color3, color4);
        Render2DEngine.endRender();
    }

    public static void drawGradientBlurredShadow1(MatrixStack matrices, float x, float y, float width, float height, int blurRadius, Color color1, Color color2, Color color3, Color color4) {
        int identifier;
        if (!HudEditor.glow.getValue().booleanValue()) {
            return;
        }
        x -= (float)blurRadius;
        y -= (float)blurRadius;
        if (!shadowCache1.containsKey(identifier = (int)((width += (float)(blurRadius * 2)) * (height += (float)(blurRadius * 2)) + width * (float)blurRadius))) {
            BufferedImage original = new BufferedImage((int)width, (int)height, 2);
            Graphics g = original.getGraphics();
            g.setColor(new Color(-1));
            g.fillRect(blurRadius, blurRadius, (int)(width - (float)(blurRadius * 2)), (int)(height - (float)(blurRadius * 2)));
            g.dispose();
            BufferedImage blurred = new GaussianFilter(blurRadius).filter(original, null);
            BufferedImage black = new BufferedImage((int)width + blurRadius * 2, (int)height + blurRadius * 2, 2);
            Graphics g2 = black.getGraphics();
            g2.setColor(new Color(0));
            g2.fillRect(0, 0, (int)width + blurRadius * 2, (int)height + blurRadius * 2);
            g2.dispose();
            BufferedImage combined = new BufferedImage((int)width, (int)height, 2);
            Graphics g1 = combined.getGraphics();
            g1.drawImage(black, -blurRadius, -blurRadius, null);
            g1.drawImage(blurred, 0, 0, null);
            g1.dispose();
            shadowCache1.put(identifier, new BlurredShadow(combined));
            return;
        }
        shadowCache1.get(identifier).bind();
        Render2DEngine.setupRender();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        Render2DEngine.renderGradientTexture(matrices, x, y, width, height, 0.0f, 0.0f, width, height, width, height, color1, color2, color3, color4);
        Render2DEngine.endRender();
    }

    public static void registerBufferedImageTexture(Texture i, BufferedImage bi) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage)bi, "png", baos);
            byte[] bytes = baos.toByteArray();
            Render2DEngine.registerTexture(i, bytes);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static void registerTexture(Texture i, byte[] content) {
        try {
            ByteBuffer data = BufferUtils.createByteBuffer((int)content.length).put(content);
            data.flip();
            NativeImageBackedTexture tex = new NativeImageBackedTexture(NativeImage.read((ByteBuffer)data));
            Module.mc.execute(() -> Module.mc.getTextureManager().registerTexture(i.getId(), (AbstractTexture)tex));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void renderTexture(MatrixStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight) {
        double x1 = x0 + width;
        double y1 = y0 + height;
        double z = 0.0;
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(matrix, (float)x0, (float)y1, (float)z).texture(u / (float)textureWidth, (v + (float)regionHeight) / (float)textureHeight);
        buffer.vertex(matrix, (float)x1, (float)y1, (float)z).texture((u + (float)regionWidth) / (float)textureWidth, (v + (float)regionHeight) / (float)textureHeight);
        buffer.vertex(matrix, (float)x1, (float)y0, (float)z).texture((u + (float)regionWidth) / (float)textureWidth, v / (float)textureHeight);
        buffer.vertex(matrix, (float)x0, (float)y0, (float)z).texture(u / (float)textureWidth, (v + 0.0f) / (float)textureHeight);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
    }

    public static void renderGradientTexture(MatrixStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight, Color c1, Color c2, Color c3, Color c4) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        Render2DEngine.renderGradientTextureInternal(buffer, matrices, x0, y0, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight, c1, c2, c3, c4);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
    }

    public static void renderGradientTextureInternal(BufferBuilder buff, MatrixStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight, Color c1, Color c2, Color c3, Color c4) {
        double x1 = x0 + width;
        double y1 = y0 + height;
        double z = 0.0;
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        buff.vertex(matrix, (float)x0, (float)y1, (float)z).texture(u / (float)textureWidth, (v + (float)regionHeight) / (float)textureHeight).color(c1.getRGB());
        buff.vertex(matrix, (float)x1, (float)y1, (float)z).texture((u + (float)regionWidth) / (float)textureWidth, (v + (float)regionHeight) / (float)textureHeight).color(c2.getRGB());
        buff.vertex(matrix, (float)x1, (float)y0, (float)z).texture((u + (float)regionWidth) / (float)textureWidth, v / (float)textureHeight).color(c3.getRGB());
        buff.vertex(matrix, (float)x0, (float)y0, (float)z).texture(u / (float)textureWidth, (v + 0.0f) / (float)textureHeight).color(c4.getRGB());
    }

    public static void renderRoundedGradientRect(MatrixStack matrices, Color color1, Color color2, Color color3, Color color4, float x, float y, float width, float height, float Radius) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderSystem.colorMask((boolean)false, (boolean)false, (boolean)false, (boolean)true);
        RenderSystem.clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        RenderSystem.clear((int)16384, (boolean)false);
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        Render2DEngine.drawRound(matrices, x, y, width, height, Radius, color1);
        Render2DEngine.setupRender();
        RenderSystem.blendFunc((int)772, (int)773);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x, y + height, 0.0f).color(color1.getRGB());
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0f).color(color2.getRGB());
        bufferBuilder.vertex(matrix, x + width, y, 0.0f).color(color3.getRGB());
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(color4.getRGB());
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
        Render2DEngine.endRender();
    }

    public static void drawRound(MatrixStack matrices, float x, float y, float width, float height, float radius, Color color) {
        Render2DEngine.renderRoundedQuad(matrices, color, x, y, width + x, height + y, radius, 4.0);
    }

    public static void renderRoundedQuad(MatrixStack matrices, Color c, double fromX, double fromY, double toX, double toY, double radius, double samples) {
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Render2DEngine.renderRoundedQuadInternal(matrices.peek().getPositionMatrix(), (float)c.getRed() / 255.0f, (float)c.getGreen() / 255.0f, (float)c.getBlue() / 255.0f, (float)c.getAlpha() / 255.0f, fromX, fromY, toX, toY, radius, samples);
        Render2DEngine.endRender();
    }

    public static void renderRoundedQuad2(MatrixStack matrices, Color c, Color c2, Color c3, Color c4, double fromX, double fromY, double toX, double toY, double radius) {
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Render2DEngine.renderRoundedQuadInternal2(matrices.peek().getPositionMatrix(), (float)c.getRed() / 255.0f, (float)c.getGreen() / 255.0f, (float)c.getBlue() / 255.0f, (float)c.getAlpha() / 255.0f, (float)c2.getRed() / 255.0f, (float)c2.getGreen() / 255.0f, (float)c2.getBlue() / 255.0f, (float)c2.getAlpha() / 255.0f, (float)c3.getRed() / 255.0f, (float)c3.getGreen() / 255.0f, (float)c3.getBlue() / 255.0f, (float)c3.getAlpha() / 255.0f, (float)c4.getRed() / 255.0f, (float)c4.getGreen() / 255.0f, (float)c4.getBlue() / 255.0f, (float)c4.getAlpha() / 255.0f, fromX, fromY, toX, toY, radius);
        Render2DEngine.endRender();
    }

    public static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double radius, double samples) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        double[][] map = new double[][]{{toX - radius, toY - radius, radius}, {toX - radius, fromY + radius, radius}, {fromX + radius, fromY + radius, radius}, {fromX + radius, toY - radius, radius}};
        for (int i = 0; i < 4; ++i) {
            double[] current = map[i];
            double rad = current[2];
            for (double r = (double)i * 90.0; r < 90.0 + (double)i * 90.0; r += 90.0 / samples) {
                float rad1 = (float)Math.toRadians(r);
                float sin = (float)(Math.sin(rad1) * rad);
                float cos = (float)(Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float)current[0] + sin, (float)current[1] + cos, 0.0f).color(cr, cg, cb, ca);
            }
            float rad1 = (float)Math.toRadians(90.0 + (double)i * 90.0);
            float sin = (float)(Math.sin(rad1) * rad);
            float cos = (float)(Math.cos(rad1) * rad);
            bufferBuilder.vertex(matrix, (float)current[0] + sin, (float)current[1] + cos, 0.0f).color(cr, cg, cb, ca);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
    }

    public static void renderRoundedQuadInternal2(Matrix4f matrix, float cr, float cg, float cb, float ca, float cr1, float cg1, float cb1, float ca1, float cr2, float cg2, float cb2, float ca2, float cr3, float cg3, float cb3, float ca3, double fromX, double fromY, double toX, double toY, double radC1) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        double[][] map = new double[][]{{toX - radC1, toY - radC1, radC1}, {toX - radC1, fromY + radC1, radC1}, {fromX + radC1, fromY + radC1, radC1}, {fromX + radC1, toY - radC1, radC1}};
        for (int i = 0; i < 4; ++i) {
            double[] current = map[i];
            double rad = current[2];
            block6: for (double r = (double)(i * 90); r < (double)(90 + i * 90); r += 10.0) {
                float rad1 = (float)Math.toRadians(r);
                float sin = (float)(Math.sin(rad1) * rad);
                float cos = (float)(Math.cos(rad1) * rad);
                switch (i) {
                    case 0: {
                        bufferBuilder.vertex(matrix, (float)current[0] + sin, (float)current[1] + cos, 0.0f).color(cr1, cg1, cb1, ca1);
                        continue block6;
                    }
                    case 1: {
                        bufferBuilder.vertex(matrix, (float)current[0] + sin, (float)current[1] + cos, 0.0f).color(cr, cg, cb, ca);
                        continue block6;
                    }
                    case 2: {
                        bufferBuilder.vertex(matrix, (float)current[0] + sin, (float)current[1] + cos, 0.0f).color(cr2, cg2, cb2, ca2);
                        continue block6;
                    }
                    default: {
                        bufferBuilder.vertex(matrix, (float)current[0] + sin, (float)current[1] + cos, 0.0f).color(cr3, cg3, cb3, ca3);
                    }
                }
            }
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
    }

    public static void draw2DGradientRect(MatrixStack matrices, float left, float top, float right, float bottom, Color leftBottomColor, Color leftTopColor, Color rightBottomColor, Color rightTopColor) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, right, top, 0.0f).color(rightTopColor.getRGB());
        bufferBuilder.vertex(matrix, left, top, 0.0f).color(leftTopColor.getRGB());
        bufferBuilder.vertex(matrix, left, bottom, 0.0f).color(leftBottomColor.getRGB());
        bufferBuilder.vertex(matrix, right, bottom, 0.0f).color(rightBottomColor.getRGB());
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
        Render2DEngine.endRender();
    }

    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static void drawTracerPointer(MatrixStack matrices, float x, float y, float size, float tracerWidth, float downHeight, boolean down, boolean glow, int color) {
        switch (HudEditor.arrowsStyle.getValue()) {
            case Default: {
                Render2DEngine.drawDefaultArrow(matrices, x, y, size, tracerWidth, downHeight, down, glow, color);
                break;
            }
            case New: {
                Render2DEngine.drawNewArrow(matrices, x, y, size + 8.0f, new Color(color));
            }
        }
    }

    public static void drawNewArrow(MatrixStack matrices, float x, float y, float size, Color color) {
        RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.arrow);
        Render2DEngine.setupRender();
        RenderSystem.setShaderColor((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
        RenderSystem.disableDepthTest();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x - size / 2.0f, y + size, 0.0f).texture(0.0f, 1.0f);
        bufferBuilder.vertex(matrix, x + size / 2.0f, y + size, 0.0f).texture(1.0f, 1.0f);
        bufferBuilder.vertex(matrix, x + size / 2.0f, y, 0.0f).texture(1.0f, 0.0f);
        bufferBuilder.vertex(matrix, x - size / 2.0f, y, 0.0f).texture(0.0f, 0.0f);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        Render2DEngine.endRender();
    }

    public static void drawDefaultArrow(MatrixStack matrices, float x, float y, float size, float tracerWidth, float downHeight, boolean down, boolean glow, int color) {
        if (glow) {
            Render2DEngine.drawBlurredShadow(matrices, x - size * tracerWidth, y, x + size * tracerWidth - (x - size * tracerWidth), size, 10, Render2DEngine.injectAlpha(new Color(color), 140));
        }
        matrices.push();
        Render2DEngine.setupRender();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(color);
        bufferBuilder.vertex(matrix, x - size * tracerWidth, y + size, 0.0f).color(color);
        bufferBuilder.vertex(matrix, x, y + size - downHeight, 0.0f).color(color);
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(color);
        color = Render2DEngine.darker(new Color(color), 0.8f).getRGB();
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(color);
        bufferBuilder.vertex(matrix, x, y + size - downHeight, 0.0f).color(color);
        bufferBuilder.vertex(matrix, x + size * tracerWidth, y + size, 0.0f).color(color);
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(color);
        if (down) {
            color = Render2DEngine.darker(new Color(color), 0.6f).getRGB();
            bufferBuilder.vertex(matrix, x - size * tracerWidth, y + size, 0.0f).color(color);
            bufferBuilder.vertex(matrix, x + size * tracerWidth, y + size, 0.0f).color(color);
            bufferBuilder.vertex(matrix, x, y + size - downHeight, 0.0f).color(color);
            bufferBuilder.vertex(matrix, x - size * tracerWidth, y + size, 0.0f).color(color);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
        Render2DEngine.endRender();
        matrices.pop();
    }

    public static void endRender() {
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static void drawGradientRound(MatrixStack ms, float v, float v1, float i, float i1, float v2, Color darker, Color darker1, Color darker2, Color darker3) {
        Render2DEngine.renderRoundedQuad2(ms, darker, darker1, darker2, darker3, v, v1, v + i, v1 + i1, v2);
    }

    public static float scrollAnimate(float endPoint, float current, float speed) {
        boolean shouldContinueAnimation;
        boolean bl = shouldContinueAnimation = endPoint > current;
        if (speed < 0.0f) {
            speed = 0.0f;
        } else if (speed > 1.0f) {
            speed = 1.0f;
        }
        float dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        float factor = dif * speed;
        return current + (shouldContinueAnimation ? factor : -factor);
    }

    public static Color injectAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp((int)alpha, (int)0, (int)255));
    }

    public static Color TwoColoreffect(Color cl1, Color cl2, double speed, double count) {
        int angle = (int)(((double)System.currentTimeMillis() / speed + count) % 360.0);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return Render2DEngine.interpolateColorC(cl1, cl2, (float)angle / 360.0f);
    }

    public static Color astolfo(boolean clickgui, int yOffset) {
        float speed = clickgui ? 3500.0f : 3000.0f;
        float hue = System.currentTimeMillis() % (long)((int)speed) + (long)yOffset;
        if (hue > speed) {
            hue -= speed;
        }
        if ((hue /= speed) > 0.5f) {
            hue = 0.5f - (hue - 0.5f);
        }
        return Color.getHSBColor(hue += 0.5f, 0.4f, 1.0f);
    }

    public static Color rainbow(int delay, float saturation, float brightness) {
        double rainbow = Math.ceil((float)(System.currentTimeMillis() + (long)delay) / 16.0f);
        return Color.getHSBColor((float)((rainbow %= 360.0) / 360.0), saturation, brightness);
    }

    public static Color skyRainbow(int speed, int index) {
        int n;
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        return Color.getHSBColor((double)((float)((double)n / 360.0)) < 0.5 ? -((float)((double)angle / 360.0)) : (float)((double)(angle %= 360) / 360.0), 0.5f, 1.0f);
    }

    public static Color fade(int speed, int index, Color color, float alpha) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        Color colorHSB = new Color(Color.HSBtoRGB(hsb[0], hsb[1], (float)angle / 360.0f));
        return new Color(colorHSB.getRed(), colorHSB.getGreen(), colorHSB.getBlue(), Math.max(0, Math.min(255, (int)(alpha * 255.0f))));
    }

    public static Color getAnalogousColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float degree = 0.84f;
        float newHueSubtracted = hsb[0] - degree;
        return new Color(Color.HSBtoRGB(newHueSubtracted, hsb[1], hsb[2]));
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1.0f, Math.max(0.0f, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((float)color.getAlpha() * opacity));
    }

    public static int applyOpacity(int color_int, float opacity) {
        opacity = Math.min(1.0f, Math.max(0.0f, opacity));
        Color color = new Color(color_int);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((float)color.getAlpha() * opacity)).getRGB();
    }

    public static Color darker(Color color, float factor) {
        return new Color(Math.max((int)((float)color.getRed() * factor), 0), Math.max((int)((float)color.getGreen() * factor), 0), Math.max((int)((float)color.getBlue() * factor), 0), color.getAlpha());
    }

    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        float hue = (float)angle / 360.0f;
        Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(opacity * 255.0f))));
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? Render2DEngine.interpolateColorHue(start, end, (float)angle / 360.0f) : Render2DEngine.interpolateColorC(start, end, (float)angle / 360.0f);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        return new Color(Render2DEngine.interpolateInt(color1.getRed(), color2.getRed(), amount), Render2DEngine.interpolateInt(color1.getGreen(), color2.getGreen(), amount), Render2DEngine.interpolateInt(color1.getBlue(), color2.getBlue(), amount), Render2DEngine.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
        Color resultColor = Color.getHSBColor(Render2DEngine.interpolateFloat(color1HSB[0], color2HSB[0], amount), Render2DEngine.interpolateFloat(color1HSB[1], color2HSB[1], amount), Render2DEngine.interpolateFloat(color1HSB[2], color2HSB[2], amount));
        return new Color(resultColor.getRed(), resultColor.getGreen(), resultColor.getBlue(), Render2DEngine.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static double interpolate(double oldValue, double newValue, double interpolationValue) {
        return oldValue + (newValue - oldValue) * interpolationValue;
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return (float)Render2DEngine.interpolate(oldValue, newValue, (float)interpolationValue);
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return (int)Render2DEngine.interpolate(oldValue, newValue, (float)interpolationValue);
    }

    public static void drawArc(MatrixStack matrices, float x, float y, float width, float height, float radius, float thickness, float start, float end, Color c1, Color c2) {
        BufferBuilder bb = Render2DEngine.preShaderDraw(matrices, x - width / 2.0f, y - height / 2.0f, x + width / 2.0f, y + height / 2.0f);
        ARC_PROGRAM.setParameters(x, y, width, height, radius, thickness, start, end, c1, c2);
        ARC_PROGRAM.use();
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bb.end());
        Render2DEngine.endRender();
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, float radius, float alpha) {
        BufferBuilder bb = Render2DEngine.preShaderDraw(matrices, x - 10.0f, y - 10.0f, width + 20.0f, height + 20.0f);
        RECTANGLE_SHADER.setParameters(x, y, width, height, radius, alpha);
        RECTANGLE_SHADER.use();
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bb.end());
        Render2DEngine.endRender();
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, float radius, float alpha, Color c1, Color c2, Color c3, Color c4) {
        BufferBuilder bb = Render2DEngine.preShaderDraw(matrices, x - 10.0f, y - 10.0f, width + 20.0f, height + 20.0f);
        RECTANGLE_SHADER.setParameters(x, y, width, height, radius, alpha, c1, c2, c3, c4);
        RECTANGLE_SHADER.use();
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bb.end());
        Render2DEngine.endRender();
    }

    public static void drawHudBase(MatrixStack matrices, float x, float y, float width, float height, float radius) {
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawRoundedBlur(matrices, x, y, width, height, radius, HudEditor.blurColor.getValue().getColorObject());
        } else {
            BufferBuilder bb = Render2DEngine.preShaderDraw(matrices, x - 10.0f, y - 10.0f, width + 20.0f, height + 20.0f);
            HUD_SHADER.setParameters(x, y, width, height, radius, HudEditor.alpha.getValue().floatValue(), HudEditor.alpha.getValue().floatValue());
            HUD_SHADER.use();
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bb.end());
            Render2DEngine.endRender();
        }
    }

    public static void drawHudBase2(MatrixStack matrices, float x, float y, float width, float height, float radius, float blurStrenth, float blurOpacity, float animationFactor) {
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            blurOpacity = (float)Render2DEngine.interpolate(1.0, blurOpacity, animationFactor);
            Color c = Render2DEngine.interpolateColorC(Color.BLACK, HudEditor.blurColor.getValue().getColorObject(), animationFactor);
            Render2DEngine.drawRoundedBlur(matrices, x, y, width, height, radius, c, blurStrenth *= animationFactor, blurOpacity);
        } else {
            BufferBuilder bb = Render2DEngine.preShaderDraw(matrices, x - 10.0f, y - 10.0f, width + 20.0f, height + 20.0f);
            HUD_SHADER.setParameters(x, y, width, height, radius, HudEditor.alpha.getValue().floatValue(), HudEditor.alpha.getValue().floatValue());
            HUD_SHADER.use();
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bb.end());
            Render2DEngine.endRender();
        }
    }

    public static void drawHudBase(MatrixStack matrices, float x, float y, float width, float height, float radius, boolean hud) {
        BufferBuilder bb = Render2DEngine.preShaderDraw(matrices, x - 10.0f, y - 10.0f, width + 20.0f, height + 20.0f);
        HUD_SHADER.setParameters(x, y, width, height, radius, HudEditor.alpha.getValue().floatValue(), HudEditor.alpha.getValue().floatValue());
        HUD_SHADER.use();
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bb.end());
        Render2DEngine.endRender();
    }

    public static void drawRoundedBlur(MatrixStack matrices, float x, float y, float width, float height, float radius, Color c1) {
        Render2DEngine.drawRoundedBlur(matrices, x, y, width, height, radius, c1, HudEditor.blurStrength.getValue().floatValue(), HudEditor.blurOpacity.getValue().floatValue());
    }

    public static void drawRoundedBlur(MatrixStack matrices, float x, float y, float width, float height, float radius, Color c1, float blurStrenth, float blurOpacity) {
        BufferBuilder bb = Render2DEngine.preShaderDraw(matrices, x - 10.0f, y - 10.0f, width + 20.0f, height + 20.0f);
        BLUR_PROGRAM.setParameters(x, y, width, height, radius, c1, blurStrenth, blurOpacity);
        BLUR_PROGRAM.use();
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bb.end());
        Render2DEngine.endRender();
    }

    public static void drawHudBase(MatrixStack matrices, float x, float y, float width, float height, float radius, float alpha) {
        BufferBuilder bb = Render2DEngine.preShaderDraw(matrices, x - 10.0f, y - 10.0f, width + 20.0f, height + 20.0f);
        HUD_SHADER.setParameters(x, y, width, height, radius, alpha, HudEditor.alpha.getValue().floatValue());
        HUD_SHADER.use();
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bb.end());
        Render2DEngine.endRender();
    }

    public static void drawGuiBase(MatrixStack matrices, float x, float y, float width, float height, float radius, float opacity) {
        BufferBuilder bb = Render2DEngine.preShaderDraw(matrices, x - 10.0f, y - 10.0f, width + 20.0f, height + 20.0f);
        HUD_SHADER.setParameters(x, y, width, height, radius, 1.0f, opacity);
        HUD_SHADER.use();
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bb.end());
        Render2DEngine.endRender();
    }

    public static void drawMainMenuShader(MatrixStack matrices, float x, float y, float width, float height) {
        BufferBuilder bb = Render2DEngine.preShaderDraw(matrices, x, y, width, height);
        MAIN_MENU_PROGRAM.setParameters(x, y, width, height);
        MAIN_MENU_PROGRAM.use();
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bb.end());
        Render2DEngine.endRender();
    }

    public static BufferBuilder preShaderDraw(MatrixStack matrices, float x, float y, float width, float height) {
        Render2DEngine.setupRender();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        Render2DEngine.setRectanglePoints(buffer, matrix, x, y, x + width, y + height);
        return buffer;
    }

    public static void setRectanglePoints(BufferBuilder buffer, Matrix4f matrix, float x, float y, float x1, float y1) {
        buffer.vertex(matrix, x, y, 0.0f);
        buffer.vertex(matrix, x, y1, 0.0f);
        buffer.vertex(matrix, x1, y1, 0.0f);
        buffer.vertex(matrix, x1, y, 0.0f);
    }

    public static void drawOrbiz(MatrixStack matrices, float z, double r, Color c) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 20; ++i) {
            float x2 = (float)(Math.sin((float)i * 56.548656f / 180.0f) * r);
            float y2 = (float)(Math.cos((float)i * 56.548656f / 180.0f) * r);
            bufferBuilder.vertex(matrix, x2, y2, z).color((float)c.getRed() / 255.0f, (float)c.getGreen() / 255.0f, (float)c.getBlue() / 255.0f, 0.4f);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
        Render2DEngine.endRender();
    }

    public static void drawStar(MatrixStack matrices, Color c, float scale) {
        Render2DEngine.setupRender();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.star);
        RenderSystem.setShaderColor((float)((float)c.getRed() / 255.0f), (float)((float)c.getGreen() / 255.0f), (float)((float)c.getBlue() / 255.0f), (float)((float)c.getAlpha() / 255.0f));
        Render2DEngine.renderGradientTexture(matrices, 0.0, 0.0, scale, scale, 0.0f, 0.0f, 128.0, 128.0, 128.0, 128.0, c, c, c, c);
        Render2DEngine.endRender();
    }

    public static void drawHeart(MatrixStack matrices, Color c, float scale) {
        Render2DEngine.setupRender();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.heart);
        RenderSystem.setShaderColor((float)((float)c.getRed() / 255.0f), (float)((float)c.getGreen() / 255.0f), (float)((float)c.getBlue() / 255.0f), (float)((float)c.getAlpha() / 255.0f));
        Render2DEngine.renderGradientTexture(matrices, 0.0, 0.0, scale, scale, 0.0f, 0.0f, 128.0, 128.0, 128.0, 128.0, c, c, c, c);
        Render2DEngine.endRender();
    }

    public static void drawBloom(MatrixStack matrices, Color c, float scale) {
        Render2DEngine.setupRender();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.firefly);
        RenderSystem.setShaderColor((float)((float)c.getRed() / 255.0f), (float)((float)c.getGreen() / 255.0f), (float)((float)c.getBlue() / 255.0f), (float)((float)c.getAlpha() / 255.0f));
        Render2DEngine.renderGradientTexture(matrices, 0.0, 0.0, scale, scale, 0.0f, 0.0f, 128.0, 128.0, 128.0, 128.0, c, c, c, c);
        Render2DEngine.endRender();
    }

    public static void drawBubble(MatrixStack matrices, float angle, float factor) {
        Render2DEngine.setupRender();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.bubble);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));
        float scale = factor * 2.0f;
        Render2DEngine.renderGradientTexture(matrices, -scale / 2.0f, -scale / 2.0f, scale, scale, 0.0f, 0.0f, 128.0, 128.0, 128.0, 128.0, Render2DEngine.applyOpacity(HudEditor.getColor(270), 1.0f - factor), Render2DEngine.applyOpacity(HudEditor.getColor(0), 1.0f - factor), Render2DEngine.applyOpacity(HudEditor.getColor(180), 1.0f - factor), Render2DEngine.applyOpacity(HudEditor.getColor(90), 1.0f - factor));
        Render2DEngine.endRender();
    }

    public static void drawLine(float x, float y, float x1, float y1, int color) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(x, y, 0.0f).color(color);
        bufferBuilder.vertex(x1, y1, 0.0f).color(color);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
    }

    public static boolean isDark(Color color) {
        return Render2DEngine.isDark((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f);
    }

    public static boolean isDark(float r, float g, float b) {
        return Render2DEngine.colorDistance(r, g, b, 0.0f, 0.0f, 0.0f) < Render2DEngine.colorDistance(r, g, b, 1.0f, 1.0f, 1.0f);
    }

    public static float colorDistance(float r1, float g1, float b1, float r2, float g2, float b2) {
        float a = r2 - r1;
        float b = g2 - g1;
        float c = b2 - b1;
        return (float)Math.sqrt(a * a + b * b + c * c);
    }

    public static void initShaders() {
        HUD_SHADER = new HudShader();
        MAIN_MENU_PROGRAM = new MainMenuProgram();
        TEXTURE_COLOR_PROGRAM = new TextureColorProgram();
        ARC_PROGRAM = new ArcShader();
        RECTANGLE_SHADER = new RectangleShader();
        BLUR_PROGRAM = new BlurProgram();
    }

    @NotNull
    public static Color getColor(@NotNull Color start, @NotNull Color end, float progress, boolean smooth) {
        if (!smooth) {
            return (double)progress >= 0.95 ? end : start;
        }
        int rDiff = end.getRed() - start.getRed();
        int gDiff = end.getGreen() - start.getGreen();
        int bDiff = end.getBlue() - start.getBlue();
        int aDiff = end.getAlpha() - start.getAlpha();
        return new Color(Render2DEngine.fixColorValue(start.getRed() + (int)((float)rDiff * progress)), Render2DEngine.fixColorValue(start.getGreen() + (int)((float)gDiff * progress)), Render2DEngine.fixColorValue(start.getBlue() + (int)((float)bDiff * progress)), Render2DEngine.fixColorValue(start.getAlpha() + (int)((float)aDiff * progress)));
    }

    private static int fixColorValue(int colorVal) {
        return colorVal > 255 ? 255 : Math.max(colorVal, 0);
    }

    public static void endBuilding(BufferBuilder bb) {
        BuiltBuffer builtBuffer = bb.endNullable();
        if (builtBuffer != null) {
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)builtBuffer);
        }
    }

    static {
        shadowCache = new HashMap();
        shadowCache1 = new HashMap();
        clipStack = new Stack();
    }

    public record Rectangle(float x, float y, float x1, float y1) {
        public boolean contains(double x, double y) {
            return x >= (double)this.x && x <= (double)this.x1 && y >= (double)this.y && y <= (double)this.y1;
        }
    }

    public static class BlurredShadow {
        Texture id = new Texture("texture/remote/" + RandomStringUtils.randomAlphanumeric((int)16));

        public BlurredShadow(BufferedImage bufferedImage) {
            Render2DEngine.registerBufferedImageTexture(this.id, bufferedImage);
        }

        public void bind() {
            RenderSystem.setShaderTexture((int)0, (Identifier)this.id.getId());
        }
    }
}

