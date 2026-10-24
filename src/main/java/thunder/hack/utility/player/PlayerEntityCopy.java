/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity.RemovalReason
 *  net.minecraft.client.network.OtherClientPlayerEntity
 */
package thunder.hack.utility.player;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import thunder.hack.features.modules.Module;

public class PlayerEntityCopy
extends OtherClientPlayerEntity {
    public PlayerEntityCopy() {
        super(Objects.requireNonNull(Module.mc.world), Objects.requireNonNull(Module.mc.player).getGameProfile());
        this.copyFrom((Entity)Module.mc.player);
        this.getPlayerListEntry();
        this.dataTracker.set(PLAYER_MODEL_PARTS, Module.mc.player.getDataTracker().get(PLAYER_MODEL_PARTS));
        this.setUuid(UUID.randomUUID());
    }

    public void spawn() {
        if (Module.mc.world == null) {
            return;
        }
        this.unsetRemoved();
        Module.mc.world.addEntity((Entity)this);
    }

    public void deSpawn() {
        if (Module.mc.world == null) {
            return;
        }
        Module.mc.world.removeEntity(this.getId(), Entity.RemovalReason.DISCARDED);
    }
}

