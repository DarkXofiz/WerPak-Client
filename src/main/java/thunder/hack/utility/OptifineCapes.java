/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.texture.NativeImageBackedTexture
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 */
package thunder.hack.utility;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;

public final class OptifineCapes {
    public static void loadPlayerCape(GameProfile player, ReturnCapeTexture response) {
        try {
            String uuid = player.getId().toString();
            NativeImageBackedTexture nIBT = OptifineCapes.getCapeFromURL(String.format("http://s.optifine.net/capes/%s.png", player.getName()));
            Identifier capeTexture = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("th-cape-" + uuid, nIBT);
            response.response(capeTexture);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static NativeImageBackedTexture getCapeFromURL(String capeStringURL) {
        try {
            URL capeURL = new URL(capeStringURL);
            return OptifineCapes.getCapeFromStream(capeURL.openStream());
        }
        catch (IOException e) {
            return null;
        }
    }

    public static NativeImageBackedTexture getCapeFromStream(InputStream image) {
        NativeImage cape = null;
        try {
            cape = NativeImage.read((InputStream)image);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (cape != null) {
            return new NativeImageBackedTexture(OptifineCapes.parseCape(cape));
        }
        return null;
    }

    public static NativeImage parseCape(NativeImage image) {
        int imageHeight;
        int imageWidth = 64;
        int imageSrcWidth = image.getWidth();
        int srcHeight = image.getHeight();
        int imageSrcHeight = image.getHeight();
        for (imageHeight = 32; imageWidth < imageSrcWidth || imageHeight < imageSrcHeight; imageWidth *= 2, imageHeight *= 2) {
        }
        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < imageSrcWidth; ++x) {
            for (int y = 0; y < srcHeight; ++y) {
                imgNew.setColor(x, y, image.getColor(x, y));
            }
        }
        image.close();
        return imgNew;
    }

    public static interface ReturnCapeTexture {
        public void response(Identifier var1);
    }
}

