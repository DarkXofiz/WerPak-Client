/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Util
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.resource.ResourceReload
 *  net.minecraft.client.gui.screen.SplashOverlay
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package thunder.hack.injection;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.resource.ResourceReload;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

@Mixin(value={SplashOverlay.class})
public abstract class MixinSplashOverlay {
    @Final
    @Shadow
    private boolean reloading;
    @Shadow
    private float progress;
    @Shadow
    private long reloadCompleteTime = -1L;
    @Shadow
    private long reloadStartTime = -1L;
    @Final
    @Shadow
    private ResourceReload reload;
    @Final
    @Shadow
    private Consumer<Optional<Throwable>> exceptionHandler;

    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true)
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (ModuleManager.unHook.isEnabled() || !ClientSettings.customLoadingScreen.getValue().booleanValue()) {
            return;
        }
        ci.cancel();
        this.renderCustom(context, mouseX, mouseY, delta);
    }

    public void renderCustom(DrawContext context, int mouseX, int mouseY, float delta) {
        float h;
        int k;
        float g;
        int i = Module.mc.getWindow().getScaledWidth();
        int j = Module.mc.getWindow().getScaledHeight();
        long l = Util.getMeasuringTimeMs();
        if (this.reloading && this.reloadStartTime == -1L) {
            this.reloadStartTime = l;
        }
        float f = this.reloadCompleteTime > -1L ? (float)(l - this.reloadCompleteTime) / 1000.0f : -1.0f;
        float f2 = g = this.reloadStartTime > -1L ? (float)(l - this.reloadStartTime) / 500.0f : -1.0f;
        if (f >= 1.0f) {
            if (Module.mc.currentScreen != null) {
                Module.mc.currentScreen.render(context, 0, 0, delta);
            }
            k = MathHelper.ceil((float)((1.0f - MathHelper.clamp((float)(f - 1.0f), (float)0.0f, (float)1.0f)) * 255.0f));
            context.fill(0, 0, i, j, MixinSplashOverlay.withAlpha(new Color(458773).getRGB(), k));
            h = 1.0f - MathHelper.clamp((float)(f - 1.0f), (float)0.0f, (float)1.0f);
        } else if (this.reloading) {
            if (Module.mc.currentScreen != null && g < 1.0f) {
                Module.mc.currentScreen.render(context, mouseX, mouseY, delta);
            }
            k = MathHelper.ceil((double)(MathHelper.clamp((double)g, (double)0.15, (double)1.0) * 255.0));
            context.fill(0, 0, i, j, MixinSplashOverlay.withAlpha(new Color(458773).getRGB(), k));
            h = MathHelper.clamp((float)g, (float)0.0f, (float)1.0f);
        } else {
            k = new Color(458773).getRGB();
            float m = (float)(k >> 16 & 0xFF) / 255.0f;
            float n = (float)(k >> 8 & 0xFF) / 255.0f;
            float o = (float)(k & 0xFF) / 255.0f;
            GlStateManager._clearColor((float)m, (float)n, (float)o, (float)1.0f);
            GlStateManager._clear((int)16384, (boolean)MinecraftClient.IS_SYSTEM_MAC);
            h = 1.0f;
        }
        k = (int)((double)context.getScaledWindowWidth() * 0.5);
        int p = (int)((double)context.getScaledWindowHeight() * 0.5);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((int)770, (int)1);
        RenderSystem.setShaderColor((float)0.1f, (float)0.1f, (float)0.1f, (float)h);
        context.drawTexture(TextureStorage.thLogo, k - 150, p - 35, 0.0f, 0.0f, 300, 70, 300, 70);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)h);
        Render2DEngine.addWindow(context.getMatrices(), k - 150, p - 35, (float)(k - 150) + 300.0f * this.progress, p + 35, 1.0);
        context.drawTexture(TextureStorage.thLogo, k - 150, p - 35, 0.0f, 0.0f, 300, 70, 300, 70);
        Render2DEngine.popWindow();
        float t = this.reload.getProgress();
        this.progress = MathHelper.clamp((float)(this.progress * 0.95f + t * 0.050000012f), (float)0.0f, (float)1.0f);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        if (f >= 2.0f) {
            Module.mc.setOverlay(null);
        }
        if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || g >= 2.0f)) {
            try {
                this.reload.throwException();
                this.exceptionHandler.accept(Optional.empty());
            }
            catch (Throwable var23) {
                this.exceptionHandler.accept(Optional.of(var23));
            }
            this.reloadCompleteTime = Util.getMeasuringTimeMs();
            if (Module.mc.currentScreen != null) {
                Module.mc.currentScreen.init(Module.mc, Module.mc.getWindow().getScaledWidth(), Module.mc.getWindow().getScaledHeight());
            }
        }
    }

    private static int withAlpha(int color, int alpha) {
        return color & 0xFFFFFF | alpha << 24;
    }
}

