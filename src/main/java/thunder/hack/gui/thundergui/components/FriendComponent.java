/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.texture.NativeImageBackedTexture
 *  net.minecraft.util.Util
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.gui.thundergui.components;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Util;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.core.Core;
import thunder.hack.core.Managers;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.thundergui.ThunderGui;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;
import thunder.hack.utility.render.animation.AnimationUtility;

public class FriendComponent {
    float scroll_animation = 0.0f;
    private Identifier head = null;
    private final String name;
    private int posX;
    private int posY;
    private int progress;
    private int fade;
    private final int index;
    private boolean first_open = true;
    private float scrollPosY;
    private float prevPosY;

    public FriendComponent(String name, int posX, int posY, int index) {
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.fade = 0;
        this.index = index * 5;
        this.loadHead(name);
        this.scrollPosY = posY;
        this.scroll_animation = 0.0f;
    }

    public void loadHead(String name) {
        if (Core.HEADS.containsKey(name)) {
            this.head = Core.HEADS.get(name);
        }
        Util.getMainWorkerExecutor().execute(() -> {
            try {
                NativeImageBackedTexture nIBT = FriendComponent.getHeadFromURL("https://minotar.net/helm/" + name + "/22.png");
                this.head = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("th-heads-" + name, nIBT);
                Core.HEADS.put(name, this.head);
            }
            catch (Exception e) {
                this.head = null;
            }
        });
    }

    public static NativeImageBackedTexture getHeadFromURL(String HeadStringURL) {
        try {
            URL capeURL = new URL(HeadStringURL);
            return FriendComponent.getHeadFromStream(capeURL.openStream());
        }
        catch (IOException e) {
            return null;
        }
    }

    public static NativeImageBackedTexture getHeadFromStream(InputStream image) {
        NativeImage Head = null;
        try {
            Head = NativeImage.read((InputStream)image);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (Head != null) {
            NativeImageBackedTexture nIBT = new NativeImageBackedTexture(FriendComponent.parseHead(Head));
            return nIBT;
        }
        return null;
    }

    public static NativeImage parseHead(NativeImage image) {
        int imageHeight;
        int imageWidth = 22;
        int imageSrcWidth = image.getWidth();
        int srcHeight = image.getHeight();
        int imageSrcHeight = image.getHeight();
        for (imageHeight = 22; imageWidth < imageSrcWidth || imageHeight < imageSrcHeight; imageWidth *= 2, imageHeight *= 2) {
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

    public void render(DrawContext context, int MouseX, int MouseY) {
        if (this.scrollPosY != (float)this.posY) {
            this.scroll_animation = AnimationUtility.fast(this.scroll_animation, 1.0f, 15.0f);
            this.posY = (int)Render2DEngine.interpolate(this.prevPosY, this.scrollPosY, this.scroll_animation);
        }
        if (this.posY > ThunderGui.getInstance().main_posY + ThunderGui.getInstance().height || this.posY < ThunderGui.getInstance().main_posY) {
            return;
        }
        Render2DEngine.drawRound(context.getMatrices(), this.posX + 5, this.posY, 285.0f, 30.0f, 4.0f, Render2DEngine.applyOpacity(new Color(44, 35, 52, 255), this.getFadeFactor()));
        if (this.first_open) {
            Render2DEngine.addWindow(context.getMatrices(), this.posX + 5, this.posY, this.posX + 5 + 285, this.posY + 30, 1.0);
            Render2DEngine.drawBlurredShadow(context.getMatrices(), MouseX - 20, MouseY - 20, 40.0f, 40.0f, 60, Render2DEngine.applyOpacity(new Color(-1017816450, true), this.getFadeFactor()));
            Render2DEngine.popWindow();
            this.first_open = false;
        }
        if (this.isHovered(MouseX, MouseY)) {
            Render2DEngine.addWindow(context.getMatrices(), this.posX + 5, this.posY, this.posX + 5 + 285, this.posY + 30, 1.0);
            Render2DEngine.drawBlurredShadow(context.getMatrices(), MouseX - 20, MouseY - 20, 40.0f, 40.0f, 60, Render2DEngine.applyOpacity(new Color(-1017816450, true), this.getFadeFactor()));
            Render2DEngine.popWindow();
        }
        Render2DEngine.drawRound(context.getMatrices(), this.posX + 266, this.posY + 8, 14.0f, 14.0f, 2.0f, Render2DEngine.applyOpacity(new Color(25, 20, 30, 255), this.getFadeFactor()));
        if (Render2DEngine.isHovered(MouseX, MouseY, this.posX + 268, this.posY + 10, 10.0, 10.0)) {
            Render2DEngine.drawRound(context.getMatrices(), this.posX + 268, this.posY + 10, 10.0f, 10.0f, 2.0f, Render2DEngine.applyOpacity(new Color(65, 1, 13, 255), this.getFadeFactor()));
        } else {
            Render2DEngine.drawRound(context.getMatrices(), this.posX + 268, this.posY + 10, 10.0f, 10.0f, 2.0f, Render2DEngine.applyOpacity(new Color(94, 1, 18, 255), this.getFadeFactor()));
        }
        FontRenderers.icons.drawString(context.getMatrices(), "w", (double)(this.posX + 268), (double)(this.posY + 13), Render2DEngine.applyOpacity(-1, this.getFadeFactor()));
        context.drawTexture(Objects.requireNonNullElse(this.head, TextureStorage.crackedSkin), this.posX + 10, this.posY + 3, 0.0f, 0.0f, 22, 22, 22, 22);
        FontRenderers.modules.drawString(context.getMatrices(), this.name, (double)(this.posX + 37), (double)(this.posY + 6), Render2DEngine.applyOpacity(-1, this.getFadeFactor()));
        boolean online = Module.mc.player.networkHandler.getPlayerList().stream().map(p -> p.getProfile().getName()).toList().contains(this.name);
        FontRenderers.settings.drawString(context.getMatrices(), online ? "online" : "offline", (double)(this.posX + 37), (double)(this.posY + 17), online ? Render2DEngine.applyOpacity(new Color(-16025088, true).getRGB(), this.getFadeFactor()) : Render2DEngine.applyOpacity(new Color(-4342339, true).getRGB(), this.getFadeFactor()));
    }

    private float getFadeFactor() {
        return (float)this.fade / (5.0f + (float)this.index);
    }

    public void onTick() {
        if (this.progress > 4) {
            this.progress = 0;
        }
        ++this.progress;
        if (this.fade < 10 + this.index) {
            ++this.fade;
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX > this.posX && mouseX < this.posX + 295 && mouseY > this.posY && mouseY < this.posY + 30;
    }

    public void movePosition(float deltaX, float deltaY) {
        this.posY = (int)((float)this.posY + deltaY);
        this.posX = (int)((float)this.posX + deltaX);
        this.scrollPosY = this.posY;
    }

    public void mouseClicked(int MouseX, int MouseY, int clickedButton) {
        if (this.posY > ThunderGui.getInstance().main_posY + ThunderGui.getInstance().height || this.posY < ThunderGui.getInstance().main_posY) {
            return;
        }
        if (Render2DEngine.isHovered(MouseX, MouseY, this.posX + 268, this.posY + 10, 10.0, 10.0)) {
            Managers.FRIEND.removeFriend(this.name);
            ThunderGui.getInstance().loadFriends();
        }
    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public void scrollElement(float deltaY) {
        this.scroll_animation = 0.0f;
        this.prevPosY = this.posY;
        this.scrollPosY += deltaY;
    }
}

