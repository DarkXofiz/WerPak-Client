/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.block.CobwebBlock
 *  net.minecraft.block.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package thunder.hack.injection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.CobwebBlock;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.core.manager.IManager;
import thunder.hack.features.modules.player.NoWeb;

@Mixin(value={CobwebBlock.class})
public class MixinCobwebBlock {
    @Inject(method={"onEntityCollision"}, at={@At(value="HEAD")}, cancellable=true)
    public void onEntityCollisionHook(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (IManager.mc == null || IManager.mc.player == null) {
            return;
        }
        if (!(entity instanceof PlayerEntity) || entity != IManager.mc.player) {
            return;
        }
        if (!NoWeb.isNoWebEnabled()) {
            return;
        }
        NoWeb.Mode mode = NoWeb.getNoWebMode();
        if (mode == NoWeb.Mode.Cancel || mode == NoWeb.Mode.Spoof || mode == NoWeb.Mode.WerPak) {
            ci.cancel();
        } else if (mode == NoWeb.Mode.Legit) {
            ci.cancel();
            float factor = NoWeb.getLegitFactor();
            Vec3d vel = entity.getVelocity();
            entity.setVelocity(vel.x * (double)factor, vel.y * 0.05, vel.z * (double)factor);
        }
    }
}

