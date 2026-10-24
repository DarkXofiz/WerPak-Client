/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.FireworkRocketEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientPlayerEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package thunder.hack.injection.accesors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.features.modules.movement.ElytraBooster;

@Mixin(value={FireworkRocketEntity.class})
public class MixinFireworkRocketEntity {
    @Inject(method={"tick"}, at={@At(value="HEAD")})
    private void onTickHead(CallbackInfo ci) {
        ClientPlayerEntity player;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) {
            return;
        }
        FireworkRocketEntity self = (FireworkRocketEntity)(Object)this;
        Entity class_12972 = self.getOwner();
        if (!(class_12972 instanceof ClientPlayerEntity) || (player = (ClientPlayerEntity)class_12972) != mc.player) {
            return;
        }
        ElytraBooster.getInstance().captureVelocity(player);
    }

    @Inject(method={"tick"}, at={@At(value="TAIL")})
    private void onTickTail(CallbackInfo ci) {
        ClientPlayerEntity player;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) {
            return;
        }
        FireworkRocketEntity self = (FireworkRocketEntity)(Object)this;
        Entity class_12972 = self.getOwner();
        if (!(class_12972 instanceof ClientPlayerEntity) || (player = (ClientPlayerEntity)class_12972) != mc.player) {
            return;
        }
        ElytraBooster.getInstance().amplifyBoost(player);
    }
}

