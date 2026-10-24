/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.entity.SignBlockEntity
 *  net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package thunder.hack.injection.accesors;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={AbstractSignEditScreen.class})
public interface ISignEditScreen {
    @Accessor(value="blockEntity")
    public SignBlockEntity getBlockEntity();

    @Accessor(value="front")
    public boolean isFront();
}

