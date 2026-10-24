/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Mouse
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package thunder.hack.injection;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.EventMouse;
import thunder.hack.features.modules.Module;

@Mixin(value={Mouse.class})
public class MixinMouse {
    @Inject(method={"onMouseButton"}, at={@At(value="HEAD")})
    public void onMouseButtonHook(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window == Module.mc.getWindow().getHandle()) {
            if (action == 0) {
                Managers.MODULE.onMoseKeyReleased(button);
            }
            if (action == 1) {
                Managers.MODULE.onMoseKeyPressed(button);
            }
            ThunderHack.EVENT_BUS.post(new EventMouse(button, action));
        }
    }

    @Inject(method={"onMouseScroll"}, at={@At(value="HEAD")})
    private void onMouseScrollHook(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (window == Module.mc.getWindow().getHandle()) {
            ThunderHack.EVENT_BUS.post(new EventMouse((int)vertical, 2));
        }
    }
}

