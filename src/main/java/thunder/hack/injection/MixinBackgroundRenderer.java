/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.BackgroundRenderer
 *  net.minecraft.client.render.BackgroundRenderer.FogType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.render.WorldTweaks;

@Mixin(value={BackgroundRenderer.class})
public class MixinBackgroundRenderer {
    @Inject(method={"applyFog"}, at={@At(value="TAIL")})
    private static void onApplyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.fog.getValue().booleanValue() && fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
            RenderSystem.setShaderFogStart((float)(viewDistance * 4.0f));
            RenderSystem.setShaderFogEnd((float)(viewDistance * 4.25f));
        }
        if (ModuleManager.worldTweaks.isEnabled() && WorldTweaks.fogModify.getValue().isEnabled()) {
            RenderSystem.setShaderFogStart((float)WorldTweaks.fogStart.getValue().intValue());
            RenderSystem.setShaderFogEnd((float)WorldTweaks.fogEnd.getValue().intValue());
            RenderSystem.setShaderFogColor((float)WorldTweaks.fogColor.getValue().getGlRed(), (float)WorldTweaks.fogColor.getValue().getGlGreen(), (float)WorldTweaks.fogColor.getValue().getGlBlue());
        } else if (ModuleManager.ambience.isEnabled()) {
            RenderSystem.setShaderFogStart((float)0.0f);
            RenderSystem.setShaderFogEnd((float)100.0f);
            RenderSystem.setShaderFogColor((float)ModuleManager.ambience.color.getValue().getGlRed(), (float)ModuleManager.ambience.color.getValue().getGlGreen(), (float)ModuleManager.ambience.color.getValue().getGlBlue());
        }
    }

    @Inject(method={"getFogModifier(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/BackgroundRenderer$StatusEffectFogModifier;"}, at={@At(value="HEAD")}, cancellable=true)
    private static void onGetFogModifier(Entity entity, float tickDelta, CallbackInfoReturnable<Object> info) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.blindness.getValue().booleanValue()) {
            info.setReturnValue(null);
        }
    }
}

