/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.util.SkinTextures
 *  net.minecraft.client.util.SkinTextures.Model
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.movement.CustomModel;
import thunder.hack.utility.render.TextureStorage;

@Mixin(value={AbstractClientPlayerEntity.class})
public class MixinAbstractClientPlayerEntity {
    @Inject(method={"getSkinTextures"}, at={@At(value="RETURN")}, cancellable=true)
    public void onGetSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        if (this == MinecraftClient.getInstance().player && ModuleManager.customModel != null && ModuleManager.customModel.isEnabled()) {
            Identifier texture = switch (ModuleManager.customModel.mode.getValue()) {
                case CustomModel.Mode.Tavsan -> Identifier.of((String)"thunderhack", (String)"textures/rabbit.png");
                case CustomModel.Mode.Boykisser -> TextureStorage.boykisser;
                case CustomModel.Mode.Paimon -> TextureStorage.paimon;
                case CustomModel.Mode.Cutie -> TextureStorage.cutie;
                default -> Identifier.of((String)"thunderhack", (String)"textures/yakuptv.png");
            };
            SkinTextures current = (SkinTextures)cir.getReturnValue();
            if (current == null) {
                cir.setReturnValue((Object)new SkinTextures(texture, null, null, null, SkinTextures.Model.WIDE, false));
            } else {
                cir.setReturnValue((Object)new SkinTextures(texture, null, current.capeTexture(), current.elytraTexture(), SkinTextures.Model.WIDE, current.secure()));
            }
        }
    }
}

