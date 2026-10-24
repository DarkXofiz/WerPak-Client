/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package thunder.hack.injection.accesors;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={PlayerInteractEntityC2SPacket.class})
public interface IPlayerInteractEntityC2SPacket {
    @Invoker(value="write")
    public void write(PacketByteBuf var1);
}

