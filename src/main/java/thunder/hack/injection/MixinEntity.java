/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.MathHelper
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArgs
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  org.spongepowered.asm.mixin.injection.invoke.arg.Args
 */
package thunder.hack.injection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import thunder.hack.ThunderHack;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventFixVelocity;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.combat.HitBox;
import thunder.hack.utility.interfaces.IEntity;

@Mixin(value={Entity.class})
public abstract class MixinEntity
implements IEntity {
    @Shadow
    private Box boundingBox;

    @Shadow
    protected abstract BlockPos getVelocityAffectingPos();

    @Override
    public BlockPos thunderHack_Recode$getVelocityBP() {
        return this.getVelocityAffectingPos();
    }

    @ModifyArgs(method={"pushAwayFrom"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void pushAwayFromHook(Args args2) {
    }

    @Inject(method={"updateVelocity"}, at={@At(value="HEAD")}, cancellable=true)
    public void updateVelocityHook(float speed, Vec3d movementInput, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        if ((Object)this == Module.mc.player) {
            ci.cancel();
            EventFixVelocity event = new EventFixVelocity(movementInput, speed, Module.mc.player.getYaw(), MixinEntity.movementInputToVelocityC(movementInput, speed, Module.mc.player.getYaw()));
            ThunderHack.EVENT_BUS.post(event);
            Module.mc.player.setVelocity(Module.mc.player.getVelocity().add(event.getVelocity()));
        }
    }

    @Unique
    private static Vec3d movementInputToVelocityC(Vec3d movementInput, float speed, float yaw) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        }
        Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply((double)speed);
        float f = MathHelper.sin((float)(yaw * ((float)Math.PI / 180)));
        float g = MathHelper.cos((float)(yaw * ((float)Math.PI / 180)));
        return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
    }

    @Inject(method={"getBoundingBox"}, at={@At(value="HEAD")}, cancellable=true)
    public final void getBoundingBox(CallbackInfoReturnable<Box> cir) {
        if (ModuleManager.hitBox.isEnabled() && Module.mc != null && Module.mc.player != null && ((Entity)(Object)this).getId() != Module.mc.player.getId() && (ModuleManager.aura.isDisabled() || HitBox.affectToAura.getValue().booleanValue())) {
            cir.setReturnValue(new Box(this.boundingBox.minX - (double)(HitBox.XZExpand.getValue().floatValue() / 2.0f), this.boundingBox.minY - (double)(HitBox.YExpand.getValue().floatValue() / 2.0f), this.boundingBox.minZ - (double)(HitBox.XZExpand.getValue().floatValue() / 2.0f), this.boundingBox.maxX + (double)(HitBox.XZExpand.getValue().floatValue() / 2.0f), this.boundingBox.maxY + (double)(HitBox.YExpand.getValue().floatValue() / 2.0f), this.boundingBox.maxZ + (double)(HitBox.XZExpand.getValue().floatValue() / 2.0f)));
        }
    }

    @Inject(method={"isGlowing"}, at={@At(value="HEAD")}, cancellable=true)
    public void isGlowingHook(CallbackInfoReturnable<Boolean> cir) {
    }

    @Inject(method={"isOnFire"}, at={@At(value="HEAD")}, cancellable=true)
    public void isOnFireHook(CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.fireEntity.getValue().booleanValue()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method={"isInvisibleTo"}, at={@At(value="HEAD")}, cancellable=true)
    public void isInvisibleToHook(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
    }

    @Inject(method={"isInLava"}, at={@At(value="HEAD")}, cancellable=true)
    public void isInLavaHook(CallbackInfoReturnable<Boolean> cir) {
    }

    @Inject(method={"isTouchingWater"}, at={@At(value="HEAD")}, cancellable=true)
    public void isTouchingWaterHook(CallbackInfoReturnable<Boolean> cir) {
    }

    @Inject(method={"setSwimming"}, at={@At(value="HEAD")}, cancellable=true)
    public void setSwimmingHook(boolean swimming, CallbackInfo ci) {
    }

    @ModifyVariable(method={"changeLookDirection"}, at=@At(value="HEAD"), ordinal=0, argsOnly=true)
    private double changeLookDirectionHook0(double value) {
        return value;
    }

    @ModifyVariable(method={"changeLookDirection"}, at=@At(value="HEAD"), ordinal=1, argsOnly=true)
    private double changeLookDirectionHook1(double value) {
        return value;
    }
}

