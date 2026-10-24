/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.MovementType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.text.Text
 *  net.minecraft.component.type.FoodComponent
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import net.minecraft.component.type.FoodComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventAttack;
import thunder.hack.events.impl.EventEatFood;
import thunder.hack.events.impl.EventPlayerJump;
import thunder.hack.events.impl.EventPlayerTravel;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.Media;
import thunder.hack.features.modules.combat.Aura;

@Mixin(value={PlayerEntity.class}, priority=800)
public class MixinPlayerEntity {
    @Inject(method={"getAttackCooldownProgressPerTick"}, at={@At(value="HEAD")}, cancellable=true)
    public void getAttackCooldownProgressPerTickHook(CallbackInfoReturnable<Float> cir) {
        if (ModuleManager.aura.isEnabled() && ModuleManager.aura.switchMode.getValue() == Aura.Switch.Silent) {
            cir.setReturnValue((Object)Float.valueOf(12.5f));
        }
    }

    @Inject(method={"getDisplayName"}, at={@At(value="HEAD")}, cancellable=true)
    public void getDisplayNameHook(CallbackInfoReturnable<Text> cir) {
        if (ModuleManager.media.isEnabled() && Media.nickProtect.getValue().booleanValue()) {
            cir.setReturnValue((Object)Text.of((String)"Protected"));
        }
    }

    @Inject(method={"attack"}, at={@At(value="INVOKE", target="Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V", shift=At.Shift.AFTER)})
    public void attackAHook(CallbackInfo callbackInfo) {
    }

    @Inject(method={"getMovementSpeed"}, at={@At(value="HEAD")}, cancellable=true)
    public void getMovementSpeedHook(CallbackInfoReturnable<Float> cir) {
    }

    @Inject(method={"attack"}, at={@At(value="HEAD")}, cancellable=true)
    private void attackAHook2(Entity target, CallbackInfo ci) {
        EventAttack event = new EventAttack(target, false);
        ThunderHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method={"travel"}, at={@At(value="HEAD")}, cancellable=true)
    private void onTravelhookPre(Vec3d movementInput, CallbackInfo ci) {
        if (Module.mc.player == null) {
            return;
        }
        EventPlayerTravel event = new EventPlayerTravel(movementInput, true);
        ThunderHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            Module.mc.player.move(MovementType.SELF, Module.mc.player.getVelocity());
            ci.cancel();
        }
    }

    @Inject(method={"travel"}, at={@At(value="RETURN")}, cancellable=true)
    private void onTravelhookPost(Vec3d movementInput, CallbackInfo ci) {
        if (Module.mc.player == null) {
            return;
        }
        EventPlayerTravel event = new EventPlayerTravel(movementInput, false);
        ThunderHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            Module.mc.player.move(MovementType.SELF, Module.mc.player.getVelocity());
            ci.cancel();
        }
    }

    @Inject(method={"jump"}, at={@At(value="HEAD")})
    private void onJumpPre(CallbackInfo ci) {
        ThunderHack.EVENT_BUS.post(new EventPlayerJump(true));
    }

    @Inject(method={"jump"}, at={@At(value="RETURN")})
    private void onJumpPost(CallbackInfo ci) {
        ThunderHack.EVENT_BUS.post(new EventPlayerJump(false));
    }

    @Inject(method={"eatFood"}, at={@At(value="RETURN")})
    public void eatFoodHook(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        ThunderHack.EVENT_BUS.post(new EventEatFood((ItemStack)cir.getReturnValue()));
    }

    @Inject(method={"shouldDismount"}, at={@At(value="HEAD")}, cancellable=true)
    protected void shouldDismountHook(CallbackInfoReturnable<Boolean> cir) {
    }

    @Inject(method={"getBlockInteractionRange"}, at={@At(value="HEAD")}, cancellable=true)
    public void getBlockInteractionRangeHook(CallbackInfoReturnable<Double> cir) {
    }

    @Inject(method={"getEntityInteractionRange"}, at={@At(value="HEAD")}, cancellable=true)
    public void getEntityInteractionRangeHook(CallbackInfoReturnable<Double> cir) {
    }
}

