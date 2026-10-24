/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package thunder.hack.injection.accesors;

import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ExplosionS2CPacket.class})
public interface IExplosionS2CPacket {
    @Mutable
    @Accessor(value="playerVelocityX")
    public void setMotionX(float var1);

    @Mutable
    @Accessor(value="playerVelocityY")
    public void setMotionY(float var1);

    @Mutable
    @Accessor(value="playerVelocityZ")
    public void setMotionZ(float var1);

    @Accessor(value="playerVelocityX")
    public float getMotionX();

    @Accessor(value="playerVelocityY")
    public float getMotionY();

    @Accessor(value="playerVelocityZ")
    public float getMotionZ();
}

