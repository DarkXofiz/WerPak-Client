/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.MovementType
 *  net.minecraft.util.math.Vec3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventTravel;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.features.modules.render.Animations;
import thunder.hack.utility.interfaces.IEntityLiving;

@Mixin(value={LivingEntity.class})
public class MixinEntityLiving
implements IEntityLiving {
    @Shadow
    protected double serverX;
    @Shadow
    protected double serverY;
    @Shadow
    protected double serverZ;
    @Unique
    double prevServerX;
    @Unique
    double prevServerY;
    @Unique
    double prevServerZ;
    @Unique
    public List<Aura.Position> positonHistory = new ArrayList<Aura.Position>();
    @Unique
    private boolean prevFlying = false;

    @Override
    public List<Aura.Position> getPositionHistory() {
        return this.positonHistory;
    }

    @Inject(method={"getHandSwingDuration"}, at={@At(value="HEAD")}, cancellable=true)
    private void getArmSwingAnimationEnd(CallbackInfoReturnable<Integer> info) {
        if (!ModuleManager.noRender.noSwing.getValue().booleanValue() && ModuleManager.animations.shouldChangeAnimationDuration() && Animations.slowAnimation.getValue().booleanValue()) {
            info.setReturnValue((Object)Animations.slowAnimationVal.getValue());
        }
    }

    @Inject(method={"updateTrackedPositionAndAngles"}, at={@At(value="HEAD")})
    private void updateTrackedPositionAndAnglesHook(double x, double y, double z, float yaw, float pitch, int interpolationSteps, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        this.prevServerX = this.serverX;
        this.prevServerY = this.serverY;
        this.prevServerZ = this.serverZ;
        this.positonHistory.add(new Aura.Position(this.serverX, this.serverY, this.serverZ));
        this.positonHistory.removeIf(Aura.Position::shouldRemove);
    }

    @Override
    public double getPrevServerX() {
        return this.prevServerX;
    }

    @Override
    public double getPrevServerY() {
        return this.prevServerY;
    }

    @Override
    public double getPrevServerZ() {
        return this.prevServerZ;
    }

    @Inject(method={"isFallFlying"}, at={@At(value="TAIL")}, cancellable=true)
    public void isFallFlyingHook(CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.elytraRecast.isEnabled()) {
            boolean elytra = (Boolean)cir.getReturnValue();
            if (this.prevFlying && !((Boolean)cir.getReturnValue()).booleanValue()) {
                cir.setReturnValue((Object)ModuleManager.elytraRecast.castElytra());
            }
            this.prevFlying = elytra;
        }
    }

    @Inject(method={"travel"}, at={@At(value="HEAD")}, cancellable=true)
    public void travelHook(Vec3d movementInput, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        if ((LivingEntity)this != Module.mc.player) {
            return;
        }
        EventTravel event = new EventTravel(Module.mc.player.getVelocity(), true);
        ThunderHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            Module.mc.player.move(MovementType.SELF, event.getmVec());
            ci.cancel();
        }
    }

    @Inject(method={"travel"}, at={@At(value="RETURN")}, cancellable=true)
    public void travelPostHook(Vec3d movementInput, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        if ((LivingEntity)this != Module.mc.player) {
            return;
        }
        EventTravel event = new EventTravel(movementInput, false);
        ThunderHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            Module.mc.player.move(MovementType.SELF, Module.mc.player.getVelocity());
            ci.cancel();
        }
    }

    @ModifyVariable(method={"setSprinting"}, at=@At(value="HEAD"), ordinal=0, argsOnly=true)
    private boolean setSprintingHook(boolean sprinting) {
        return sprinting;
    }

    @Inject(method={"getHandSwingDuration"}, at={@At(value="HEAD")}, cancellable=true)
    private void onGetHandSwingDuration(CallbackInfoReturnable<Integer> cir) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.noSwing.getValue().booleanValue()) {
            cir.setReturnValue((Object)0);
            cir.cancel();
        }
    }
}

