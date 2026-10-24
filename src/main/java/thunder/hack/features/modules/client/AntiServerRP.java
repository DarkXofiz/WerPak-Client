/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket
 *  net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket
 *  net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status
 */
package thunder.hack.features.modules.client;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import org.jetbrains.annotations.NotNull;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.utility.math.MathUtility;

public final class AntiServerRP
extends Module {
    private boolean confirm;
    private boolean accepted;
    private int delay;

    public AntiServerRP() {
        super("AntiServerRP", Module.Category.CLIENT);
    }

    @EventHandler
    public void onPacketReceive(@NotNull PacketEvent.Receive e) {
        if (e.getPacket() instanceof ResourcePackSendS2CPacket) {
            this.confirm = true;
            this.accepted = false;
            this.delay = 0;
            e.cancel();
        }
    }

    @Override
    public void onUpdate() {
        if (this.confirm) {
            ++this.delay;
            if ((float)this.delay > MathUtility.random(15.0f, 30.0f) && !this.accepted) {
                this.sendPacket((Packet<?>)new ResourcePackStatusC2SPacket(AntiServerRP.mc.player.getUuid(), ResourcePackStatusC2SPacket.Status.ACCEPTED));
                this.accepted = true;
            }
            if ((float)this.delay > MathUtility.random(40.0f, 60.0f) && this.accepted) {
                this.sendPacket((Packet<?>)new ResourcePackStatusC2SPacket(AntiServerRP.mc.player.getUuid(), ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
                this.confirm = false;
            }
        }
    }
}

