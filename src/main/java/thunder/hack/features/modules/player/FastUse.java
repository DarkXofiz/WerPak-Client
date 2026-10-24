/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.Items
 */
package thunder.hack.features.modules.player;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.IMinecraftClient;
import thunder.hack.setting.Setting;

public class FastUse
extends Module {
    private final Setting<Integer> delay = new Setting<Integer>("Delay", 0, 0, 5);
    public Setting<Boolean> blocks = new Setting<Boolean>("Blocks", false);
    public Setting<Boolean> crystals = new Setting<Boolean>("Crystals", false);
    public Setting<Boolean> xp = new Setting<Boolean>("XP", false);
    public Setting<Boolean> all = new Setting<Boolean>("All", true);

    public FastUse() {
        super("FastUse", Module.Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        if (this.check(FastUse.mc.player.getMainHandStack().getItem()) && ((IMinecraftClient)mc).getUseCooldown() > this.delay.getValue()) {
            ((IMinecraftClient)mc).setUseCooldown(this.delay.getValue());
        }
    }

    public boolean check(Item item) {
        return item instanceof BlockItem && this.blocks.getValue() != false || item == Items.END_CRYSTAL && this.crystals.getValue() != false || item == Items.EXPERIENCE_BOTTLE && this.xp.getValue() != false || this.all.getValue() != false;
    }
}

