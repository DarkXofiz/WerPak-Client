/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.input.KeyboardInput
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package thunder.hack.injection;

import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.ThunderHack;
import thunder.hack.events.impl.EventKeyboardInput;
import thunder.hack.features.modules.Module;

@Mixin(value={KeyboardInput.class})
public class MixinKeyboardInput {
    @Inject(method={"tick"}, at={@At(value="FIELD", target="Lnet/minecraft/client/input/KeyboardInput;sneaking:Z", shift=At.Shift.BEFORE)}, cancellable=true)
    private void onSneak(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        EventKeyboardInput event = new EventKeyboardInput();
        ThunderHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}

