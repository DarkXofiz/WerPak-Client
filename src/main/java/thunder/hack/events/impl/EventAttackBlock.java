/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 */
package thunder.hack.events.impl;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import thunder.hack.events.Event;

public class EventAttackBlock
extends Event {
    private BlockPos blockPos;
    private Direction enumFacing;

    public EventAttackBlock(BlockPos blockPos, Direction enumFacing) {
        this.blockPos = blockPos;
        this.enumFacing = enumFacing;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public Direction getEnumFacing() {
        return this.enumFacing;
    }

    public void setEnumFacing(Direction enumFacing) {
        this.enumFacing = enumFacing;
    }
}

