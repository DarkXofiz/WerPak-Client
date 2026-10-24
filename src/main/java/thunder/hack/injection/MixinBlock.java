/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.BlockView
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.block.CobwebBlock
 *  net.minecraft.block.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import net.minecraft.world.BlockView;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.block.CobwebBlock;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.features.modules.player.NoWeb;

@Mixin(value={Block.class})
public abstract class MixinBlock {
    @Inject(method={"shouldDrawSide"}, at={@At(value="HEAD")}, cancellable=true)
    private static void shouldDrawSideHook(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
    }

    @Inject(method={"getVelocityMultiplier"}, at={@At(value="HEAD")}, cancellable=true)
    public void getVelocityMultiplierHook(CallbackInfoReturnable<Float> cir) {
        if (!NoWeb.isNoWebEnabled()) {
            return;
        }
        if (!((Block)(Object)this instanceof CobwebBlock)) {
            return;
        }
        NoWeb.Mode mode = NoWeb.getNoWebMode();
        if (mode == NoWeb.Mode.Cancel || mode == NoWeb.Mode.Spoof || mode == NoWeb.Mode.WerPak) {
            cir.setReturnValue(Float.valueOf(1.0f));
        } else if (mode == NoWeb.Mode.Legit) {
            cir.setReturnValue(Float.valueOf(0.7f + NoWeb.getLegitFactor() * 0.3f));
        }
    }

    @Inject(method={"getSlipperiness"}, at={@At(value="HEAD")}, cancellable=true)
    public void getSlipperinessHook(CallbackInfoReturnable<Float> cir) {
    }
}

