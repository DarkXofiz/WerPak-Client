/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.texture.NativeImageBackedTexture
 *  net.minecraft.util.Util
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 */
package thunder.hack.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Util;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;
import thunder.hack.ThunderHack;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.mainmenu.MainMenuScreen;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.TextureStorage;

public class CreditsScreen
extends Screen {
    public ArrayList<Contributor> contributors = new ArrayList();
    private static int scroll;
    private static final int SCROLL_SPEED = 1;
    private static CreditsScreen INSTANCE;

    protected CreditsScreen() {
        super(Text.of((String)"CreditsScreen"));
        INSTANCE = this;
        for (String line : ThunderHack.contributors) {
            if (line == null) continue;
            String name = line.split(";")[0];
            String avatar = line.split(";")[1];
            String role = line.split(";")[2];
            String description = line.split(";")[3];
            String clickAction = line.split(";")[4];
            CreditsScreen.getInstance().contributors.add(new Contributor(name, CreditsScreen.getAvatar(avatar), role, description.replace('\u0442', '\n'), clickAction));
        }
    }

    public static CreditsScreen getInstance() {
        scroll = 150;
        if (INSTANCE == null) {
            INSTANCE = new CreditsScreen();
        }
        return INSTANCE;
    }

    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        float halfOfWidth = (float)Module.mc.getWindow().getScaledWidth() / 2.0f;
        float halfOfHeight = (float)Module.mc.getWindow().getScaledHeight() / 2.0f;
        float globalOffset = (float)(this.contributors.size() * 150) / 2.0f;
        this.renderBackground(context, mouseX, mouseY, delta);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int offset = 0;
        for (Contributor contributor : this.contributors) {
            float cX = halfOfWidth + (float)offset - globalOffset + (float)scroll;
            float cY = halfOfHeight - 120.0f;
            Render2DEngine.drawHudBase(context.getMatrices(), cX, cY, 140.0f, 240.0f, 20.0f, false);
            FontRenderers.sf_medium.drawGradientString(context.getMatrices(), contributor.name, cX + 70.0f - FontRenderers.sf_medium.getStringWidth(contributor.name) / 2.0f, halfOfHeight - 57.0f, 30);
            FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), contributor.role, (double)(cX + 70.0f), (double)(halfOfHeight - 48.0f), new Color(0x818181).getRGB());
            Render2DEngine.horizontalGradient(context.getMatrices(), cX + 2.0f, cY + 90.0f, cX + 70.0f, cY + 91.0f, Render2DEngine.injectAlpha(new Color(-1), 0), new Color(-1));
            Render2DEngine.horizontalGradient(context.getMatrices(), cX + 70.0f, cY + 90.0f, cX + 138.0f, cY + 91.0f, new Color(-1), Render2DEngine.injectAlpha(new Color(-1), 0));
            Render2DEngine.drawRound(context.getMatrices(), cX + 5.0f, cY + 100.0f, 130.0f, 130.0f, 8.0f, new Color(0x73000000, true));
            FontRenderers.sf_medium.drawString(context.getMatrices(), contributor.description, (double)(cX + 10.0f), (double)(cY + 108.0f), new Color(0x818182).getRGB());
            if (contributor.avatar != null) {
                context.drawTexture(contributor.avatar, (int)(cX + 70.0f - 24.0f), (int)(halfOfHeight - 110.0f), 48, 48, 0.0f, 0.0f, 96, 96, 96, 96);
            }
            if (Render2DEngine.isHovered(mouseX, mouseY, cX, cY, 140.0, 240.0) && !Objects.equals(contributor.clickAction, "")) {
                Render2DEngine.drawRound(context.getMatrices(), cX, cY, 140.0f, 240.0f, 8.0f, new Color(0x5FFFFFF, true));
            }
            offset += 150;
        }
        RenderSystem.disableBlend();
        Render2DEngine.drawHudBase(context.getMatrices(), (float)(Module.mc.getWindow().getScaledWidth() - 40), (float)(Module.mc.getWindow().getScaledHeight() - 40), 30.0f, 30.0f, 5.0f, Render2DEngine.isHovered(mouseX, mouseY, Module.mc.getWindow().getScaledWidth() - 60, Module.mc.getWindow().getScaledHeight() - 60, 40.0, 40.0) ? 0.7f : 1.0f);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)(Render2DEngine.isHovered(mouseX, mouseY, Module.mc.getWindow().getScaledWidth() - 40, Module.mc.getWindow().getScaledHeight() - 40, 30.0, 30.0) ? 0.7f : 1.0f));
        context.drawTexture(TextureStorage.thTeam, Module.mc.getWindow().getScaledWidth() - 40, Module.mc.getWindow().getScaledHeight() - 40, 30, 30, 0.0f, 0.0f, 30, 30, 30, 30);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float halfOfWidth = (float)Module.mc.getWindow().getScaledWidth() / 2.0f;
        float halfOfHeight = (float)Module.mc.getWindow().getScaledHeight() / 2.0f;
        float globalOffset = (float)(this.contributors.size() * 150) / 2.0f;
        int offset = 0;
        for (Contributor contributor : this.contributors) {
            float cY;
            float cX = (float)((double)(halfOfWidth + (float)offset - globalOffset) + Render2DEngine.interpolate(scroll, scroll + 1, Render3DEngine.getTickDelta()));
            if (Render2DEngine.isHovered(mouseX, mouseY, cX, cY = halfOfHeight - 120.0f, 140.0, 240.0) && !Objects.equals(contributor.clickAction, "none")) {
                Util.getOperatingSystem().open(URI.create(contributor.clickAction));
            }
            offset += 150;
        }
        if (Render2DEngine.isHovered(mouseX, mouseY, Module.mc.getWindow().getScaledWidth() - 40, Module.mc.getWindow().getScaledHeight() - 40, 40.0, 40.0)) {
            Module.mc.setScreen((Screen)MainMenuScreen.getInstance());
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public static Identifier getAvatar(String name) {
        try {
            NativeImageBackedTexture nIBT = CreditsScreen.getAvatarFromURL("https://cdn.discordapp.com/avatars/" + name + ".png?size=96");
            if (nIBT != null) {
                return MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("th-contributors-" + (int)MathUtility.random(0.0f, 1000000.0f), nIBT);
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static NativeImageBackedTexture getAvatarFromURL(String HeadStringURL) {
        try {
            return CreditsScreen.getAvatarFromStream(new URL(HeadStringURL).openStream());
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static NativeImageBackedTexture getAvatarFromStream(InputStream image) {
        NativeImage pic = null;
        try {
            pic = NativeImage.read((InputStream)image);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (pic != null) {
            return new NativeImageBackedTexture(CreditsScreen.parseAvatar(pic));
        }
        return null;
    }

    public static NativeImage parseAvatar(NativeImage image) {
        NativeImage imgNew = new NativeImage(96, 96, true);
        for (int x = 0; x < 96; ++x) {
            for (int y = 0; y < 96; ++y) {
                if (Math.hypot(x - 48, y - 48) > 45.0) {
                    imgNew.setColor(x, y, Render2DEngine.injectAlpha(new Color(image.getColor(x, y)), (int)((float)(48.0 - Math.hypot(x - 48, y - 48)) / 3.0f * 255.0f)).getRGB());
                    continue;
                }
                imgNew.setColor(x, y, image.getColor(x, y));
            }
        }
        image.close();
        return imgNew;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll += (int)(verticalAmount * 5.0);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public void tick() {
        if (--scroll <= -(this.contributors.size() * 150) + 100) {
            scroll = 0;
        }
    }

    static {
        INSTANCE = new CreditsScreen();
    }

    public record Contributor(String name, Identifier avatar, String role, String description, String clickAction) {
    }
}

