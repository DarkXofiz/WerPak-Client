/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.dimension.DimensionType
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.render.LightmapTextureManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import net.minecraft.world.dimension.DimensionType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.render.Fullbright;

@Mixin(value={LightmapTextureManager.class})
public class MixinLightmapTextureManager {
    @Inject(method={"getDarknessFactor(F)F"}, at={@At(value="HEAD")}, cancellable=true)
    private void getDarknessFactor(float tickDelta, CallbackInfoReturnable<Float> info) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.darkness.getValue().booleanValue()) {
            info.setReturnValue((Object)Float.valueOf(0.0f));
        }
    }

    @Inject(method={"getBrightness"}, at={@At(value="HEAD")}, cancellable=true)
    private static void getBrightnessHook(DimensionType type, int lightLevel, CallbackInfoReturnable<Float> cir) {
        if (ModuleManager.fullbright.isEnabled()) {
            float f = (float)lightLevel / 15.0f;
            float g = f / (4.0f - 3.0f * f);
            cir.setReturnValue((Object)Float.valueOf(Math.max(MathHelper.lerp((float)type.ambientLight(), (float)g, (float)1.0f), Fullbright.minBright.getValue().floatValue())));
        }
    }
}

