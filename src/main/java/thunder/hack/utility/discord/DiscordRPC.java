/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 */
package thunder.hack.utility.discord;

import com.sun.jna.Library;
import com.sun.jna.Native;
import thunder.hack.utility.discord.DiscordEventHandlers;
import thunder.hack.utility.discord.DiscordRichPresence;

public interface DiscordRPC
extends Library {
    public static final DiscordRPC INSTANCE = Initializer.load();

    public void Discord_UpdateHandlers(DiscordEventHandlers var1);

    public void Discord_UpdatePresence(DiscordRichPresence var1);

    public void Discord_Respond(String var1, int var2);

    public void Discord_Register(String var1, String var2);

    public void Discord_Shutdown();

    public void Discord_UpdateConnection();

    public void Discord_RegisterSteamGame(String var1, String var2);

    public void Discord_RunCallbacks();

    public void Discord_Initialize(String var1, DiscordEventHandlers var2, boolean var3, String var4);

    public void Discord_ClearPresence();

    public static class Initializer {
        static DiscordRPC load() {
            try {
                return (DiscordRPC)Native.load((String)"discord-rpc", DiscordRPC.class);
            }
            catch (Throwable e) {
                System.out.println("[ThunderHack] Failed to load discord-rpc native library (Expected on Android/PojavLauncher): " + e.getMessage());
                return null;
            }
        }
    }
}

