/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full
 */
package thunder.hack.features.modules.combat;

import io.netty.buffer.Unpooled;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.jetbrains.annotations.NotNull;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.IClientPlayerEntity;
import thunder.hack.setting.Setting;

public final class Criticals
extends Module {
    public final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.UpdatedNCP);
    public static boolean cancelCrit;

    public Criticals() {
        super("Criticals", Module.Category.COMBAT);
    }

    @EventHandler
    public void onPacketSend(@NotNull PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket && Criticals.getInteractType((PlayerInteractEntityC2SPacket)event.getPacket()) == InteractType.ATTACK) {
            Entity ent = Criticals.getEntity((PlayerInteractEntityC2SPacket)event.getPacket());
            if (ent == null || ent instanceof EndCrystalEntity || cancelCrit) {
                return;
            }
            this.doCrit();
        }
    }

    public void doCrit() {
        if (this.isDisabled() || Criticals.mc.player == null || Criticals.mc.world == null) {
            return;
        }
        if ((Criticals.mc.player.isOnGround() || Criticals.mc.player.getAbilities().flying || this.mode.is(Mode.Grim)) && !Criticals.mc.player.isInLava() && !Criticals.mc.player.isSubmergedInWater()) {
            switch (this.mode.getValue().ordinal()) {
                case 2: {
                    this.critPacket(1.058293536E-5, false);
                    this.critPacket(9.16580235E-6, false);
                    this.critPacket(1.0371854E-7, false);
                    break;
                }
                case 0: {
                    this.critPacket(0.0625, false);
                    this.critPacket(0.0, false);
                    break;
                }
                case 3: {
                    this.critPacket(2.71875E-7, false);
                    this.critPacket(0.0, false);
                    break;
                }
                case 1: {
                    this.critPacket(0.062600301692775, false);
                    this.critPacket(0.07260029960661, false);
                    this.critPacket(0.0, false);
                    this.critPacket(0.0, false);
                    break;
                }
                case 4: {
                    if (Criticals.mc.player.isOnGround()) break;
                    this.critPacket(-1.0E-6, true);
                }
            }
        }
    }

    private void critPacket(double yDelta, boolean full) {
        if (!full) {
            this.sendPacket((Packet<?>)new PlayerMoveC2SPacket.PositionAndOnGround(Criticals.mc.player.getX(), Criticals.mc.player.getY() + yDelta, Criticals.mc.player.getZ(), false));
        } else {
            this.sendPacket((Packet<?>)new PlayerMoveC2SPacket.Full(Criticals.mc.player.getX(), Criticals.mc.player.getY() + yDelta, Criticals.mc.player.getZ(), ((IClientPlayerEntity)Criticals.mc.player).getLastYaw(), ((IClientPlayerEntity)Criticals.mc.player).getLastPitch(), false));
        }
    }

    public static Entity getEntity(@NotNull PlayerInteractEntityC2SPacket packet) {
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        packet.write(packetBuf);
        return Criticals.mc.world.getEntityById(packetBuf.readVarInt());
    }

    public static InteractType getInteractType(@NotNull PlayerInteractEntityC2SPacket packet) {
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        packet.write(packetBuf);
        packetBuf.readVarInt();
        return (InteractType)packetBuf.readEnumConstant(InteractType.class);
    }

    public static enum Mode {
        Ncp,
        Strict,
        OldNCP,
        UpdatedNCP,
        Grim;

    }

    public static enum InteractType {
        INTERACT,
        ATTACK,
        INTERACT_AT;

    }
}

