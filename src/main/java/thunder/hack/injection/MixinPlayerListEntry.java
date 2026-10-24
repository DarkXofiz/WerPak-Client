/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.util.Util
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.client.util.SkinTextures
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import com.mojang.authlib.GameProfile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;
import net.minecraft.util.Util;
import net.minecraft.util.Identifier;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.utility.OptifineCapes;
import thunder.hack.utility.ThunderUtility;

@Mixin(value={PlayerListEntry.class})
public class MixinPlayerListEntry {
    @Unique
    private boolean loadedCapeTexture;
    @Unique
    private Identifier customCapeTexture;

    @Inject(method={"<init>(Lcom/mojang/authlib/GameProfile;Z)V"}, at={@At(value="TAIL")})
    private void initHook(GameProfile profile, boolean secureChatEnforced, CallbackInfo ci) {
        this.getTexture(profile);
    }

    @Inject(method={"getSkinTextures"}, at={@At(value="TAIL")}, cancellable=true)
    private void getCapeTexture(CallbackInfoReturnable<SkinTextures> cir) {
        if (this.customCapeTexture != null) {
            SkinTextures prev = (SkinTextures)cir.getReturnValue();
            SkinTextures newTextures = new SkinTextures(prev.texture(), prev.textureUrl(), this.customCapeTexture, this.customCapeTexture, prev.model(), prev.secure());
            cir.setReturnValue(newTextures);
        }
    }

    @Unique
    private void getTexture(GameProfile profile) {
        if (this.loadedCapeTexture) {
            return;
        }
        this.loadedCapeTexture = true;
        Util.getMainWorkerExecutor().execute(() -> {
            try {
                String inputLine;
                URL capesList = new URL("https://raw.githubusercontent.com/Pan4ur/THRecodeUtil/main/capes/capeBase.txt");
                BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
                while ((inputLine = in.readLine()) != null) {
                    String colune = inputLine.trim();
                    String name = colune.split(":")[0];
                    String cape = colune.split(":")[1];
                    if (!Objects.equals(profile.getName(), name)) continue;
                    this.customCapeTexture = Identifier.of((String)"thunderhack", (String)("textures/capes/" + cape + ".png"));
                    return;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            for (String str : ThunderUtility.starGazer) {
                if (!profile.getName().toLowerCase().equals(str.toLowerCase())) continue;
                this.customCapeTexture = Identifier.of((String)"thunderhack", (String)"textures/capes/starcape.png");
            }
            if (ModuleManager.optifineCapes.isEnabled()) {
                OptifineCapes.loadPlayerCape(profile, id -> {
                    this.customCapeTexture = id;
                });
            }
        });
    }
}

