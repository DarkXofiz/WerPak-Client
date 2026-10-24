/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.MinecraftClient
 *  org.lwjgl.opengl.GL30
 */
package thunder.hack.utility.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL30;

public class MSAAFramebuffer
extends Framebuffer {
    public static final int MAX_SAMPLES = GL30.glGetInteger((int)36183);
    private static final Map<Integer, MSAAFramebuffer> INSTANCES = new HashMap<Integer, MSAAFramebuffer>();
    private final int samples;
    private int rboColor;
    private int rboDepth;

    public MSAAFramebuffer(int samples) {
        super(true);
        this.samples = samples;
        this.setClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    public static MSAAFramebuffer getInstance(int samples) {
        return INSTANCES.computeIfAbsent(samples, x -> new MSAAFramebuffer(samples));
    }

    public static void use(boolean fancy, Runnable drawAction) {
        MSAAFramebuffer.use(Math.min(fancy ? 16 : 4, MAX_SAMPLES), MinecraftClient.getInstance().getFramebuffer(), drawAction);
    }

    public static void use(int samples, @NotNull Framebuffer mainBuffer, @NotNull Runnable drawAction) {
        RenderSystem.assertOnRenderThreadOrInit();
        MSAAFramebuffer msaaBuffer = MSAAFramebuffer.getInstance(samples);
        msaaBuffer.resize(mainBuffer.textureWidth, mainBuffer.textureHeight, false);
        GlStateManager._glBindFramebuffer((int)36008, (int)mainBuffer.fbo);
        GlStateManager._glBindFramebuffer((int)36009, (int)msaaBuffer.fbo);
        GlStateManager._glBlitFrameBuffer((int)0, (int)0, (int)msaaBuffer.textureWidth, (int)msaaBuffer.textureHeight, (int)0, (int)0, (int)msaaBuffer.textureWidth, (int)msaaBuffer.textureHeight, (int)16384, (int)9729);
        msaaBuffer.beginWrite(true);
        drawAction.run();
        msaaBuffer.endWrite();
        GlStateManager._glBindFramebuffer((int)36008, (int)msaaBuffer.fbo);
        GlStateManager._glBindFramebuffer((int)36009, (int)mainBuffer.fbo);
        GlStateManager._glBlitFrameBuffer((int)0, (int)0, (int)msaaBuffer.textureWidth, (int)msaaBuffer.textureHeight, (int)0, (int)0, (int)msaaBuffer.textureWidth, (int)msaaBuffer.textureHeight, (int)16384, (int)9729);
        msaaBuffer.clear(false);
        mainBuffer.beginWrite(false);
    }

    public void resize(int width, int height, boolean getError) {
        if (this.textureWidth != width || this.textureHeight != height) {
            super.resize(width, height, getError);
        }
    }

    public void initFbo(int width, int height, boolean getError) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.viewportWidth = width;
        this.viewportHeight = height;
        this.textureWidth = width;
        this.textureHeight = height;
        this.fbo = GlStateManager.glGenFramebuffers();
        GlStateManager._glBindFramebuffer((int)36160, (int)this.fbo);
        this.rboColor = GlStateManager.glGenRenderbuffers();
        GlStateManager._glBindRenderbuffer((int)36161, (int)this.rboColor);
        GL30.glRenderbufferStorageMultisample((int)36161, (int)this.samples, (int)32856, (int)width, (int)height);
        GlStateManager._glBindRenderbuffer((int)36161, (int)0);
        this.rboDepth = GlStateManager.glGenRenderbuffers();
        GlStateManager._glBindRenderbuffer((int)36161, (int)this.rboDepth);
        GL30.glRenderbufferStorageMultisample((int)36161, (int)this.samples, (int)6402, (int)width, (int)height);
        GlStateManager._glBindRenderbuffer((int)36161, (int)0);
        GL30.glFramebufferRenderbuffer((int)36160, (int)36064, (int)36161, (int)this.rboColor);
        GL30.glFramebufferRenderbuffer((int)36160, (int)36096, (int)36161, (int)this.rboDepth);
        this.colorAttachment = MinecraftClient.getInstance().getFramebuffer().getColorAttachment();
        this.depthAttachment = MinecraftClient.getInstance().getFramebuffer().getDepthAttachment();
        this.checkFramebufferStatus();
        this.clear(getError);
        this.endRead();
    }

    public void delete() {
        RenderSystem.assertOnRenderThreadOrInit();
        this.endRead();
        this.endWrite();
        if (this.fbo > -1) {
            GlStateManager._glBindFramebuffer((int)36160, (int)0);
            GlStateManager._glDeleteFramebuffers((int)this.fbo);
            this.fbo = -1;
        }
        if (this.rboColor > -1) {
            GlStateManager._glDeleteRenderbuffers((int)this.rboColor);
            this.rboColor = -1;
        }
        if (this.rboDepth > -1) {
            GlStateManager._glDeleteRenderbuffers((int)this.rboDepth);
            this.rboDepth = -1;
        }
        this.colorAttachment = -1;
        this.depthAttachment = -1;
        this.textureWidth = -1;
        this.textureHeight = -1;
    }
}

