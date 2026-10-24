/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderTickCounter.Dynamic
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;

@Mixin(value={RenderTickCounter.Dynamic.class})
public class MixinDynamic {
    @Shadow
    private float lastFrameDuration;
    @Shadow
    private float tickDelta;
    @Shadow
    private long prevTimeMillis;
    @Final
    @Shadow
    private float tickTime;

    @Inject(method={"Lnet/minecraft/client/render/RenderTickCounter$Dynamic;beginRenderTick(J)I"}, at={@At(value="HEAD")}, cancellable=true)
    private void beginRenderTickHook(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        if (ThunderHack.TICK_TIMER == 1.0f) {
            return;
        }
        this.lastFrameDuration = (float)(timeMillis - this.prevTimeMillis) / this.tickTime * ThunderHack.TICK_TIMER;
        this.prevTimeMillis = timeMillis;
        this.tickDelta += this.lastFrameDuration;
        int i = (int)this.tickDelta;
        this.tickDelta -= (float)i;
        cir.setReturnValue(i);
    }
}

