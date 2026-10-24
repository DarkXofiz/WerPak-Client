/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity.PositionUpdater
 *  net.minecraft.entity.vehicle.BoatEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package thunder.hack.injection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BoatEntity.class})
public class MixinBoatEntity {
    @Unique
    private float prevYaw;
    @Unique
    private float prevHeadYaw;

    @Inject(method={"updatePassengerPosition"}, at={@At(value="HEAD")})
    protected void updatePassengerPositionHookPre(Entity passenger, Entity.PositionUpdater positionUpdater, CallbackInfo ci) {
    }

    @Inject(method={"updatePassengerPosition"}, at={@At(value="RETURN")})
    protected void updatePassengerPositionHookPost(Entity passenger, Entity.PositionUpdater positionUpdater, CallbackInfo ci) {
    }
}

