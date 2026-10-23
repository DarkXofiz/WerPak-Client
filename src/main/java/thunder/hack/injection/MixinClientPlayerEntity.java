/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.MovementType
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.core.Core;
import thunder.hack.events.impl.EventAfterRotate;
import thunder.hack.events.impl.EventMove;
import thunder.hack.events.impl.EventPostSync;
import thunder.hack.events.impl.EventSprint;
import thunder.hack.events.impl.EventSync;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.events.impl.PostPlayerUpdateEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.player.NoWeb;

@Mixin(value={ClientPlayerEntity.class}, priority=800)
public abstract class MixinClientPlayerEntity
extends AbstractClientPlayerEntity {
    @Unique
    boolean pre_sprint_state = false;
    @Unique
    private boolean updateLock = false;
    @Unique
    private Runnable postAction;

    @Shadow
    public abstract float getPitch(float var1);

    @Shadow
    protected abstract void sendMovementPackets();

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    public void tickHook(CallbackInfo info) {
        if (Module.fullNullCheck()) {
            return;
        }
        ThunderHack.EVENT_BUS.post(new PlayerUpdateEvent());
    }

    @Redirect(method={"tickMovement"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), require=0)
    private boolean tickMovementHook(ClientPlayerEntity player) {
        return player.isUsingItem();
    }

    @Inject(method={"shouldSlowDown"}, at={@At(value="HEAD")}, cancellable=true)
    public void shouldSlowDownHook(CallbackInfoReturnable<Boolean> cir) {
        NoWeb.Mode m;
        if (NoWeb.isNoWebEnabled() && ((m = NoWeb.getNoWebMode()) == NoWeb.Mode.Cancel || m == NoWeb.Mode.Spoof || m == NoWeb.Mode.WerPak)) {
            cir.setReturnValue((Object)false);
        }
    }

    @Inject(method={"move"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V")}, cancellable=true)
    public void onMoveHook(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        EventMove event = new EventMove(movement.x, movement.y, movement.z);
        ThunderHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            super.move(movementType, new Vec3d(event.getX(), event.getY(), event.getZ()));
            ci.cancel();
        }
    }

    @Inject(method={"sendMovementPackets"}, at={@At(value="HEAD")}, cancellable=true)
    private void sendMovementPacketsHook(CallbackInfo info) {
        if (Module.fullNullCheck()) {
            return;
        }
        EventSync event = new EventSync(this.getYaw(), this.getPitch());
        ThunderHack.EVENT_BUS.post(event);
        this.postAction = event.getPostAction();
        EventSprint e = new EventSprint(this.isSprinting());
        ThunderHack.EVENT_BUS.post(e);
        ThunderHack.EVENT_BUS.post(new EventAfterRotate());
        if (e.getSprintState() != Module.mc.player.lastSprinting) {
            if (e.getSprintState()) {
                Module.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this, ClientCommandC2SPacket.Mode.START_SPRINTING));
            } else {
                Module.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }
            Module.mc.player.lastSprinting = e.getSprintState();
        }
        this.pre_sprint_state = Module.mc.player.lastSprinting;
        Core.lockSprint = true;
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method={"sendMovementPackets"}, at={@At(value="RETURN")}, cancellable=true)
    private void sendMovementPacketsPostHook(CallbackInfo info) {
        if (Module.fullNullCheck()) {
            return;
        }
        Module.mc.player.lastSprinting = this.pre_sprint_state;
        Core.lockSprint = false;
        EventPostSync event = new EventPostSync();
        ThunderHack.EVENT_BUS.post(event);
        if (this.postAction != null) {
            this.postAction.run();
            this.postAction = null;
        }
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method={"tick"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V", ordinal=0, shift=At.Shift.AFTER)}, cancellable=true)
    private void PostUpdateHook(CallbackInfo info) {
        if (Module.fullNullCheck()) {
            return;
        }
        if (this.updateLock) {
            return;
        }
        PostPlayerUpdateEvent playerUpdateEvent = new PostPlayerUpdateEvent();
        ThunderHack.EVENT_BUS.post(playerUpdateEvent);
        if (playerUpdateEvent.isCancelled()) {
            info.cancel();
            if (playerUpdateEvent.getIterations() > 0) {
                for (int i = 0; i < playerUpdateEvent.getIterations(); ++i) {
                    this.updateLock = true;
                    this.tick();
                    this.updateLock = false;
                    this.sendMovementPackets();
                }
            }
        }
    }

    @Inject(method={"pushOutOfBlocks"}, at={@At(value="HEAD")}, cancellable=true)
    private void onPushOutOfBlocksHook(double x, double d, CallbackInfo info) {
    }

    @Inject(method={"tickNausea"}, at={@At(value="HEAD")}, cancellable=true)
    private void updateNauseaHook(CallbackInfo ci) {
    }
}

