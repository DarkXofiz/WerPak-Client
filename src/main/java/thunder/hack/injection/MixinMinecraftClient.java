/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.util.Window
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.resource.ResourcePack
 *  net.minecraft.client.gui.screen.Overlay
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 *  net.minecraft.client.RunArgs
 *  net.minecraft.client.util.MacWindowUtil
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.resource.InputSupplier
 *  net.minecraft.client.util.Icons
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWImage
 *  org.lwjgl.glfw.GLFWImage$Buffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Window;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePack;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.RunArgs;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.resource.InputSupplier;
import net.minecraft.client.util.Icons;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventAttack;
import thunder.hack.events.impl.EventHandleBlockBreaking;
import thunder.hack.events.impl.EventPostTick;
import thunder.hack.events.impl.EventScreen;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.WindowResizeCallback;

@Mixin(value={MinecraftClient.class})
public abstract class MixinMinecraftClient {
    @Shadow
    @Final
    private Window window;
    @Unique
    private String[] shittyServers = new String[]{"mineblaze", "musteryworld", "dexland", "masedworld", "vimeworld", "hypemc", "vimemc"};

    @Shadow
    public abstract void setScreen(@Nullable Screen var1);

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    void postWindowInit(RunArgs args2, CallbackInfo ci) {
        try {
            FontRenderers.settings = FontRenderers.create(12.0f, "comfortaa");
            FontRenderers.modules = FontRenderers.create(15.0f, "comfortaa");
            FontRenderers.categories = FontRenderers.create(18.0f, "comfortaa");
            FontRenderers.thglitch = FontRenderers.create(36.0f, "glitched");
            FontRenderers.thglitchBig = FontRenderers.create(72.0f, "glitched");
            FontRenderers.monsterrat = FontRenderers.create(18.0f, "monsterrat");
            FontRenderers.sf_bold = FontRenderers.create(16.0f, "sf_bold");
            FontRenderers.sf_medium = FontRenderers.create(16.0f, "sf_medium");
            FontRenderers.sf_medium_mini = FontRenderers.create(12.0f, "sf_medium");
            FontRenderers.sf_medium_modules = FontRenderers.create(14.0f, "sf_medium");
            FontRenderers.sf_bold_mini = FontRenderers.create(14.0f, "sf_bold");
            FontRenderers.sf_bold_micro = FontRenderers.create(12.0f, "sf_bold");
            FontRenderers.profont = FontRenderers.create(16.0f, "profont");
            FontRenderers.icons = FontRenderers.create(20.0f, "icons");
            FontRenderers.mid_icons = FontRenderers.create(46.0f, "icons");
            FontRenderers.big_icons = FontRenderers.create(72.0f, "icons");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    void preTickHook(CallbackInfo ci) {
        if (!Module.fullNullCheck()) {
            ThunderHack.EVENT_BUS.post(new EventTick());
        }
    }

    @Inject(method={"tick"}, at={@At(value="RETURN")})
    void postTickHook(CallbackInfo ci) {
        if (!Module.fullNullCheck()) {
            ThunderHack.EVENT_BUS.post(new EventPostTick());
        }
    }

    @Inject(method={"onResolutionChanged"}, at={@At(value="TAIL")})
    private void captureResize(CallbackInfo ci) {
        ((WindowResizeCallback)WindowResizeCallback.EVENT.invoker()).onResized((MinecraftClient)(Object)this, this.window);
    }

    @Inject(method={"doItemPick"}, at={@At(value="HEAD")}, cancellable=true)
    private void doItemPickHook(CallbackInfo ci) {
    }

    @Inject(method={"setOverlay"}, at={@At(value="HEAD")})
    public void setOverlay(Overlay overlay, CallbackInfo ci) {
    }

    @Inject(method={"setScreen"}, at={@At(value="HEAD")}, cancellable=true)
    public void setScreenHookPre(Screen screen, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        EventScreen event = new EventScreen(screen);
        ThunderHack.EVENT_BUS.post(event);
        if (event.isCancelled() || ClickGUI.close && screen == null) {
            ci.cancel();
        }
    }

    @Inject(method={"setScreen"}, at={@At(value="RETURN")})
    public void setScreenHookPost(Screen screen, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        if (screen instanceof MultiplayerScreen) {
            MultiplayerScreen mScreen = (MultiplayerScreen)screen;
            if (ModuleManager.antiServerAdd.isEnabled() && mScreen.getServerList() != null) {
                block0: for (int i = 0; i < mScreen.getServerList().size(); ++i) {
                    ServerInfo info = mScreen.getServerList().get(i);
                    for (String server : this.shittyServers) {
                        if (info == null || info.address == null || !info.address.toLowerCase().contains(server.toLowerCase())) continue;
                        mScreen.getServerList().remove(info);
                        mScreen.getServerList().saveFile();
                        this.setScreen(screen);
                        continue block0;
                    }
                }
            }
        }
    }

    @Redirect(method={"<init>"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/util/Window;setIcon(Lnet/minecraft/resource/ResourcePack;Lnet/minecraft/client/util/Icons;)V"))
    private void onChangeIcon(Window instance, ResourcePack resourcePack, Icons icons) throws IOException {
        if (GLFW.glfwGetPlatform() == 393218) {
            MacWindowUtil.setApplicationIconImage((InputSupplier)icons.getMacIcon(resourcePack));
            return;
        }
        this.setWindowIcon(ThunderHack.class.getResourceAsStream("/icon.png"), ThunderHack.class.getResourceAsStream("/icon.png"));
    }

    public void setWindowIcon(InputStream img16x16, InputStream img32x32) {
        try (MemoryStack memorystack = MemoryStack.stackPush();){
            GLFWImage.Buffer buffer = GLFWImage.malloc((int)2, (MemoryStack)memorystack);
            List<InputStream> imgList = List.of(img16x16, img32x32);
            ArrayList<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
            for (int i = 0; i < imgList.size(); ++i) {
                NativeImage nativeImage = NativeImage.read((InputStream)imgList.get(i));
                ByteBuffer bytebuffer = MemoryUtil.memAlloc((int)(nativeImage.getWidth() * nativeImage.getHeight() * 4));
                bytebuffer.asIntBuffer().put(nativeImage.copyPixelsRgba());
                buffer.position(i);
                buffer.width(nativeImage.getWidth());
                buffer.height(nativeImage.getHeight());
                buffer.pixels(bytebuffer);
                buffers.add(bytebuffer);
            }
            try {
                if (GLFW.glfwGetPlatform() != 393219) {
                    GLFW.glfwSetWindowIcon((long)Module.mc.getWindow().getHandle(), (GLFWImage.Buffer)buffer);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            buffers.forEach(MemoryUtil::memFree);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Inject(method={"doAttack"}, at={@At(value="HEAD")}, cancellable=true)
    private void doAttackHook(CallbackInfoReturnable<Boolean> cir) {
        EventAttack event = new EventAttack(null, true);
        ThunderHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method={"handleBlockBreaking"}, at={@At(value="HEAD")}, cancellable=true)
    private void handleBlockBreakingHook(boolean breaking, CallbackInfo ci) {
        EventHandleBlockBreaking event = new EventHandleBlockBreaking();
        ThunderHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}

