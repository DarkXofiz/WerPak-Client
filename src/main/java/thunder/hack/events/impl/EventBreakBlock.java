/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 */
package thunder.hack.events.impl;

import net.minecraft.util.math.BlockPos;
import thunder.hack.events.Event;

public class EventBreakBlock
extends Event {
    private BlockPos bp;

    public EventBreakBlock(BlockPos bp) {
        this.bp = bp;
    }

    public BlockPos getPos() {
        return this.bp;
    }
}

