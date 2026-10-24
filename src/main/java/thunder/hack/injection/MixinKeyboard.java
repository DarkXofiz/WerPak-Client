/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Keyboard
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package thunder.hack.injection;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.EventKeyPress;
import thunder.hack.events.impl.EventKeyRelease;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.hud.HudEditorGui;

@Mixin(value={Keyboard.class})
public class MixinKeyboard {
    @Inject(method={"onKey"}, at={@At(value="HEAD")}, cancellable=true)
    private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        boolean whitelist;
        if (Module.fullNullCheck()) {
            return;
        }
        boolean bl = whitelist = Module.mc.currentScreen == null || Module.mc.currentScreen instanceof ClickGUI || Module.mc.currentScreen instanceof HudEditorGui;
        if (!whitelist) {
            return;
        }
        if (action == 0) {
            Managers.MODULE.onKeyReleased(key);
        }
        if (action == 1) {
            Managers.MODULE.onKeyPressed(key);
        }
        if (action == 2) {
            action = 1;
        }
        switch (action) {
            case 0: {
                EventKeyRelease event = new EventKeyRelease(key, scanCode);
                ThunderHack.EVENT_BUS.post(event);
                if (!event.isCancelled()) break;
                ci.cancel();
                break;
            }
            case 1: {
                EventKeyPress event = new EventKeyPress(key, scanCode);
                ThunderHack.EVENT_BUS.post(event);
                if (!event.isCancelled()) break;
                ci.cancel();
            }
        }
    }
}

