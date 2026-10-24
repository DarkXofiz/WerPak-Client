/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.event.Event
 *  net.fabricmc.fabric.api.event.EventFactory
 *  net.minecraft.client.util.Window
 *  net.minecraft.client.MinecraftClient
 */
package thunder.hack.utility.render;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.util.Window;
import net.minecraft.client.MinecraftClient;

public interface WindowResizeCallback {
    public static final Event<WindowResizeCallback> EVENT = EventFactory.createArrayBacked(WindowResizeCallback.class, callbacks -> (client, window) -> {
        for (WindowResizeCallback callback : callbacks) {
            callback.onResized(client, window);
        }
    });

    public void onResized(MinecraftClient var1, Window var2);
}

