/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.util.SkinTextures
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import net.minecraft.util.Identifier;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.client.Media;
import thunder.hack.utility.render.TextureStorage;

@Mixin(value={SkinTextures.class})
public class MixinSkinTextures {
    @Inject(method={"texture"}, at={@At(value="HEAD")}, cancellable=true)
    public void getSkinTextureHook(CallbackInfoReturnable<Identifier> cir) {
        if (ModuleManager.media.isEnabled() && Media.skinProtect.getValue().booleanValue()) {
            cir.setReturnValue(TextureStorage.sunRiseSkin);
        }
    }
}

