/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ClientPlayerEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package thunder.hack.injection.accesors;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={ClientPlayerEntity.class})
public interface IClientPlayerEntity {
    @Invoker(value="sendMovementPackets")
    public void iSendMovementPackets();

    @Accessor(value="lastYaw")
    public float getLastYaw();

    @Accessor(value="lastPitch")
    public float getLastPitch();

    @Accessor(value="lastYaw")
    public void setLastYaw(float var1);

    @Accessor(value="lastPitch")
    public void setLastPitch(float var1);

    @Accessor(value="mountJumpStrength")
    public void setMountJumpStrength(float var1);
}

