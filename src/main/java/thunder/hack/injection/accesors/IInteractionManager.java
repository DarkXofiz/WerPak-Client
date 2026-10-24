/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package thunder.hack.injection.accesors;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={ClientPlayerInteractionManager.class})
public interface IInteractionManager {
    @Accessor(value="currentBreakingProgress")
    public float getCurBlockDamageMP();

    @Accessor(value="currentBreakingProgress")
    public void setCurBlockDamageMP(float var1);

    @Invoker(value="syncSelectedSlot")
    public void syncSlot();
}

