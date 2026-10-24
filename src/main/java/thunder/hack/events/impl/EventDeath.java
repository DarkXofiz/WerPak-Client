/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 */
package thunder.hack.events.impl;

import net.minecraft.entity.player.PlayerEntity;
import thunder.hack.events.Event;

public class EventDeath
extends Event {
    private final PlayerEntity player;

    public EventDeath(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }
}

