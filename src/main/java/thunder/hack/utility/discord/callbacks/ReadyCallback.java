/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Callback
 */
package thunder.hack.utility.discord.callbacks;

import com.sun.jna.Callback;
import thunder.hack.utility.discord.DiscordUser;

public interface ReadyCallback
extends Callback {
    public void apply(DiscordUser var1);
}

