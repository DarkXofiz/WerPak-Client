/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package thunder.hack.events.impl;

import net.minecraft.item.ItemStack;
import thunder.hack.events.Event;

public class EventEatFood
extends Event {
    private final ItemStack stack;

    public EventEatFood(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getFood() {
        return this.stack;
    }
}

