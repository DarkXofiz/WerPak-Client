/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.BlockState
 *  net.minecraft.world.chunk.WorldChunk
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.features.modules.Module;
import thunder.hack.utility.world.ExplosionUtility;

@Mixin(value={World.class})
public abstract class MixinWorld {
    @Inject(method={"getBlockState"}, at={@At(value="HEAD")}, cancellable=true)
    public void blockStateHook(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if (ExplosionUtility.terrainIgnore && Module.mc.world != null && !Module.mc.world.isInBuildLimit(pos)) {
            WorldChunk worldChunk = Module.mc.world.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
            BlockState tempState = worldChunk.getBlockState(pos);
            if (tempState.getBlock() == Blocks.OBSIDIAN || tempState.getBlock() == Blocks.BEDROCK || tempState.getBlock() == Blocks.ENDER_CHEST || tempState.getBlock() == Blocks.RESPAWN_ANCHOR) {
                return;
            }
            cir.setReturnValue(Blocks.AIR.getDefaultState());
        }
    }
}

