/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.Vec3d
 */
package thunder.hack.events.impl;

import net.minecraft.util.math.Vec3d;
import thunder.hack.events.Event;

public class EventPlayerTravel
extends Event {
    private Vec3d mVec;
    private boolean pre;

    public EventPlayerTravel(Vec3d mVec, boolean pre) {
        this.mVec = mVec;
        this.pre = pre;
    }

    public Vec3d getmVec() {
        return this.mVec;
    }

    public boolean isPre() {
        return this.pre;
    }
}

