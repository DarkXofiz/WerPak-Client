/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.TypedActionResult
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.TridentItem
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.world.World
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.events.impl.UseTridentEvent;

@Mixin(value={TridentItem.class})
public abstract class MixinTridentItem {
    @Inject(method={"onStoppedUsing"}, at={@At(value="HEAD")}, cancellable=true)
    public void onStoppedUsingHook(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (user == ThunderHack.mc.player && EnchantmentHelper.getTridentSpinAttackStrength((ItemStack)stack, (LivingEntity)ThunderHack.mc.player) > 0.0f) {
            UseTridentEvent e = new UseTridentEvent();
            ThunderHack.EVENT_BUS.post(e);
            if (e.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method={"use"}, at={@At(value="HEAD")}, cancellable=true)
    public void useHook(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
    }
}

