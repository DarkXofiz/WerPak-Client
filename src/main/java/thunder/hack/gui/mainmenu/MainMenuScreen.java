/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Util
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.option.OptionsScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.TitleScreen
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 *  net.minecraft.client.gui.screen.world.SelectWorldScreen
 */
package thunder.hack.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.mainmenu.CreditsScreen;
import thunder.hack.gui.mainmenu.MainMenuButton;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

public class MainMenuScreen
extends Screen {
    private final List<MainMenuButton> buttons = new ArrayList<MainMenuButton>();
    private final List<Particle> particles = new ArrayList<Particle>();
    public boolean confirm = false;
    public static int ticksActive = 0;
    private static MainMenuScreen INSTANCE = new MainMenuScreen();

    protected MainMenuScreen() {
        super(Text.of((String)"ZeltrixWareZenoBest_CrackMainMenuScreen"));
        INSTANCE = this;
    }

    public static MainMenuScreen getInstance() {
        ticksActive = 0;
        if (INSTANCE == null) {
            INSTANCE = new MainMenuScreen();
        }
        return INSTANCE;
    }

    protected void init() {
        super.init();
        this.buttons.clear();
        if (this.particles.isEmpty()) {
            float sW = Module.mc.getWindow().getScaledWidth();
            float sH = Module.mc.getWindow().getScaledHeight();
            for (int i = 0; i < 55; ++i) {
                this.particles.add(new Particle(sW, sH, true));
            }
        }
        float screenW = Module.mc.getWindow().getScaledWidth();
        float screenH = Module.mc.getWindow().getScaledHeight();
        float totalW = 600.0f;
        float totalH = 340.0f;
        float startX = (screenW - totalW) / 2.0f;
        float startY = (screenH - totalH) / 2.0f;
        float rightX = startX + 265.0f;
        float btnW = 310.0f;
        float btnH = 37.0f;
        float btnGap = 7.0f;
        float btnStartY = startY + 54.0f;
        this.buttons.add(new MainMenuButton(rightX + 12.0f, btnStartY, btnW, btnH, ClientSettings.isRu() ? "\u041e\u0434\u0438\u043d\u043e\u0447\u043d\u0430\u044f \u0438\u0433\u0440\u0430" : "Singleplayer", ClientSettings.isRu() ? "\u041b\u043e\u043a\u0430\u043b\u044c\u043d\u044b\u0435 \u043c\u0438\u0440\u044b" : "Play on local worlds", "\u2756", () -> Module.mc.setScreen((Screen)new SelectWorldScreen((Screen)this))));
        this.buttons.add(new MainMenuButton(rightX + 12.0f, btnStartY + (btnH + btnGap), btnW, btnH, ClientSettings.isRu() ? "\u0421\u0435\u0442\u0435\u0432\u0430\u044f \u0438\u0433\u0440\u0430" : "Multiplayer", ClientSettings.isRu() ? "\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u043a \u0441\u0435\u0440\u0432\u0435\u0440\u0430\u043c" : "Join online servers", "\ud83c\udf10", () -> Module.mc.setScreen((Screen)new MultiplayerScreen((Screen)this))));
        this.buttons.add(new MainMenuButton(rightX + 12.0f, btnStartY + (btnH + btnGap) * 2.0f, btnW, btnH, ClientSettings.isRu() ? "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438" : "Options", ClientSettings.isRu() ? "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u0438\u0433\u0440\u044b \u0438 \u0433\u0440\u0430\u0444\u0438\u043a\u0438" : "Game & video settings", "\u2699", () -> Module.mc.setScreen((Screen)new OptionsScreen((Screen)this, Module.mc.options))));
        this.buttons.add(new MainMenuButton(rightX + 12.0f, btnStartY + (btnH + btnGap) * 3.0f, btnW, btnH, ClientSettings.isRu() ? "\u041c\u043e\u0434\u0443\u043b\u0438 \u043a\u043b\u0438\u0435\u043d\u0442\u0430" : "Client GUI", ClientSettings.isRu() ? "\u041c\u0435\u043d\u044e \u0444\u0443\u043d\u043a\u0446\u0438\u0439 ZeltrixWareZenoBest_Crack" : "Configure client features", "\u26a1", () -> ModuleManager.clickGui.setGui()));
        this.buttons.add(new MainMenuButton(rightX + 12.0f, btnStartY + (btnH + btnGap) * 4.0f, btnW, btnH, ClientSettings.isRu() ? "\u041e\u0431\u044b\u0447\u043d\u043e\u0435 \u043c\u0435\u043d\u044e" : "Vanilla Menu", ClientSettings.isRu() ? "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u043e\u0435 \u0433\u043b\u0430\u0432\u043d\u043e\u0435 \u043c\u0435\u043d\u044e" : "Return to vanilla title", "\u25c0", () -> {
            this.confirm = true;
            Module.mc.setScreen((Screen)new TitleScreen());
            this.confirm = false;
        }));
        this.buttons.add(new MainMenuButton(rightX + 12.0f, btnStartY + (btnH + btnGap) * 5.0f, btnW, btnH, ClientSettings.isRu() ? "\u0412\u044b\u0439\u0442\u0438 \u0438\u0437 \u0438\u0433\u0440\u044b" : "Quit Game", ClientSettings.isRu() ? "\u0417\u0430\u043a\u0440\u044b\u0442\u044c Minecraft" : "Exit to desktop", "\u2715", () -> ((MinecraftClient)Module.mc).scheduleStop()));
    }

    public void tick() {
        ++ticksActive;
        float sW = Module.mc.getWindow().getScaledWidth();
        float sH = Module.mc.getWindow().getScaledHeight();
        for (Particle p : this.particles) {
            p.update(sW, sH);
        }
    }

    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrices = context.getMatrices();
        float screenW = Module.mc.getWindow().getScaledWidth();
        float screenH = Module.mc.getWindow().getScaledHeight();
        this.renderBackground(context, mouseX, mouseY, delta);
        Render2DEngine.drawRound(matrices, 0.0f, 0.0f, screenW, screenH, 0.0f, new Color(8, 6, 14, 220));
        for (Particle p : this.particles) {
            p.render(matrices);
        }
        float totalW = 600.0f;
        float totalH = 340.0f;
        float startX = (screenW - totalW) / 2.0f;
        float startY = (screenH - totalH) / 2.0f;
        float leftX = startX;
        float leftY = startY;
        float leftW = 250.0f;
        float leftH = totalH;
        Render2DEngine.drawBlurredShadow(matrices, leftX, leftY, leftW, leftH, 18, new Color(0, 0, 0, 190));
        Render2DEngine.drawRound(matrices, leftX, leftY, leftW, leftH, 14.0f, new Color(13, 10, 22, 235));
        Render2DEngine.drawGradientRound(matrices, leftX + 10.0f, leftY + 2.0f, leftW - 20.0f, 2.0f, 1.0f, new Color(0, 210, 255, 180), new Color(155, 89, 182, 220), new Color(155, 89, 182, 100), new Color(0, 210, 255, 80));
        float emblemSize = 70.0f;
        float emblemX = leftX + (leftW - emblemSize) / 2.0f;
        float emblemY = leftY + 24.0f;
        Render2DEngine.drawBlurredShadow(matrices, emblemX, emblemY, emblemSize, emblemSize, 20, new Color(155, 89, 182, 130));
        Render2DEngine.drawBlurredShadow(matrices, emblemX + 10.0f, emblemY + 10.0f, emblemSize - 20.0f, emblemSize - 20.0f, 14, new Color(0, 210, 255, 100));
        Render2DEngine.drawGradientRound(matrices, emblemX, emblemY, emblemSize, emblemSize, 18.0f, new Color(0, 210, 255), new Color(155, 89, 182), new Color(74, 0, 224), new Color(0, 210, 255));
        Render2DEngine.drawRound(matrices, emblemX + 2.5f, emblemY + 2.5f, emblemSize - 5.0f, emblemSize - 5.0f, 16.0f, new Color(16, 12, 28, 250));
        FontRenderers.thglitchBig.drawCenteredString(matrices, "Z", (double)(emblemX + emblemSize / 2.0f), (double)(emblemY + 10.0f), Color.WHITE.getRGB());
        float brandY = leftY + 108.0f;
        FontRenderers.sf_bold.drawCenteredString(matrices, "ZeltrixWareZenoBest_Crack Ware", (double)(leftX + leftW / 2.0f), (double)brandY, Color.WHITE.getRGB());
        FontRenderers.sf_medium_mini.drawCenteredString(matrices, "PREMIUM UTILITY CLIENT", (double)(leftX + leftW / 2.0f), (double)(brandY + 16.0f), new Color(0, 210, 255, 210).getRGB());
        Render2DEngine.drawRound(matrices, leftX + 25.0f, brandY + 32.0f, leftW - 50.0f, 1.5f, 0.75f, new Color(255, 255, 255, 25));
        float infoY = brandY + 44.0f;
        float pulse = (float)((double)0.6f + Math.sin((double)System.currentTimeMillis() * 0.005) * (double)0.4f);
        Render2DEngine.drawRound(matrices, leftX + 28.0f, infoY + 2.0f, 6.0f, 6.0f, 3.0f, new Color(46, 204, 113, (int)(pulse * 255.0f)));
        FontRenderers.sf_bold_mini.drawString(matrices, "SYSTEM ONLINE", (double)(leftX + 38.0f), (double)(infoY + 1.5f), new Color(46, 204, 113).getRGB());
        String username = Module.mc.getSession() != null ? Module.mc.getSession().getUsername() : "Player";
        FontRenderers.sf_medium_mini.drawString(matrices, "User: " + String.valueOf(Formatting.WHITE) + username, (double)(leftX + 28.0f), (double)(infoY + 18.0f), new Color(175, 170, 195).getRGB());
        FontRenderers.sf_medium_mini.drawString(matrices, "Build: " + String.valueOf(Formatting.WHITE) + "V15 (Fabric 1.21)", (double)(leftX + 28.0f), (double)(infoY + 31.0f), new Color(175, 170, 195).getRGB());
        String onlineStr = "Online: " + String.valueOf(Formatting.GREEN) + Managers.TELEMETRY.getOnlinePlayers().size() + String.valueOf(Formatting.GRAY) + " users";
        FontRenderers.sf_medium_mini.drawString(matrices, onlineStr, (double)(leftX + 28.0f), (double)(infoY + 44.0f), new Color(175, 170, 195).getRGB());
        FontRenderers.sf_medium_mini.drawCenteredString(matrices, "\"Dominance by Design\"", (double)(leftX + leftW / 2.0f), (double)(leftY + leftH - 22.0f), new Color(130, 125, 150, 190).getRGB());
        float rightX = startX + 265.0f;
        float rightY = startY;
        float rightW = 335.0f;
        float rightH = totalH;
        Render2DEngine.drawBlurredShadow(matrices, rightX, rightY, rightW, rightH, 18, new Color(0, 0, 0, 190));
        Render2DEngine.drawRound(matrices, rightX, rightY, rightW, rightH, 14.0f, new Color(13, 10, 22, 235));
        Render2DEngine.drawGradientRound(matrices, rightX + 10.0f, rightY + 2.0f, rightW - 20.0f, 2.0f, 1.0f, new Color(155, 89, 182, 220), new Color(0, 210, 255, 180), new Color(0, 210, 255, 80), new Color(155, 89, 182, 100));
        FontRenderers.sf_bold.drawString(matrices, "NAVIGATION", (double)(rightX + 18.0f), (double)(rightY + 16.0f), Color.WHITE.getRGB());
        FontRenderers.sf_medium_mini.drawString(matrices, "Select a game mode or utility", (double)(rightX + 18.0f), (double)(rightY + 30.0f), new Color(135, 130, 155).getRGB());
        Render2DEngine.drawRound(matrices, rightX + 18.0f, rightY + 44.0f, rightW - 36.0f, 1.5f, 0.75f, new Color(255, 255, 255, 25));
        this.buttons.forEach(b -> b.onRender(context, mouseX, mouseY));
        int totalAddonsLoaded = Managers.ADDON.getTotalAddons();
        String addonsText = "Addons: " + String.valueOf(Formatting.AQUA) + totalAddonsLoaded;
        float textWidth = FontRenderers.sf_bold_mini.getStringWidth(addonsText);
        FontRenderers.sf_bold_mini.drawString(matrices, addonsText, (double)(screenW - textWidth - 12.0f), 10.0, Color.WHITE.getRGB());
        float btnIconY = screenH - 34.0f;
        boolean hoveredCredits = Render2DEngine.isHovered(mouseX, mouseY, screenW - 38.0f, btnIconY, 26.0, 26.0);
        Render2DEngine.drawRound(matrices, screenW - 38.0f, btnIconY, 26.0f, 26.0f, 6.0f, new Color(20, 16, 32, hoveredCredits ? 240 : 160));
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)(hoveredCredits ? 1.0f : 0.7f));
        context.drawTexture(TextureStorage.thTeam, (int)(screenW - 35.0f), (int)(btnIconY + 3.0f), 20, 20, 0.0f, 0.0f, 30, 30, 30, 30);
        boolean hoveredDonate = Render2DEngine.isHovered(mouseX, mouseY, screenW - 70.0f, btnIconY, 26.0, 26.0);
        Render2DEngine.drawRound(matrices, screenW - 70.0f, btnIconY, 26.0f, 26.0f, 6.0f, new Color(20, 16, 32, hoveredDonate ? 240 : 160));
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)(hoveredDonate ? 1.0f : 0.7f));
        context.drawTexture(TextureStorage.donation, (int)(screenW - 67.0f), (int)(btnIconY + 3.0f), 20, 20, 0.0f, 0.0f, 30, 30, 30, 30);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        FontRenderers.sf_medium_mini.drawString(matrices, "ZeltrixWareZenoBest_Crack \u2022 Powered by ThunderHack", 12.0, (double)(screenH - 16.0f), new Color(110, 105, 130, 180).getRGB());
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float screenH;
        float btnIconY;
        for (MainMenuButton b : this.buttons) {
            if (!b.onClick((int)mouseX, (int)mouseY)) continue;
            return true;
        }
        float screenW = Module.mc.getWindow().getScaledWidth();
        if (Render2DEngine.isHovered(mouseX, mouseY, screenW - 38.0f, btnIconY = (screenH = (float)Module.mc.getWindow().getScaledHeight()) - 34.0f, 26.0, 26.0)) {
            Module.mc.setScreen((Screen)CreditsScreen.getInstance());
            return true;
        }
        if (Render2DEngine.isHovered(mouseX, mouseY, screenW - 70.0f, btnIconY, 26.0, 26.0)) {
            Util.getOperatingSystem().open(URI.create("https://www.donationalerts.com/r/06ed/"));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public static class Particle {
        private float x;
        private float y;
        private float size;
        private float speedY;
        private float wobbleSpeed;
        private float wobbleAmount;
        private float seed;
        private float alpha;
        private Color color;

        public Particle(float screenW, float screenH, boolean initial) {
            this.reset(screenW, screenH, initial);
        }

        public void reset(float screenW, float screenH, boolean initial) {
            this.x = (float)(Math.random() * (double)Math.max(screenW, 100.0f));
            this.y = initial ? (float)(Math.random() * (double)Math.max(screenH, 100.0f)) : screenH + 10.0f;
            this.size = (float)(2.0 + Math.random() * 3.5);
            this.speedY = (float)(0.35 + Math.random() * 0.75);
            this.wobbleSpeed = (float)(0.001 + Math.random() * 0.002);
            this.wobbleAmount = (float)(8.0 + Math.random() * 16.0);
            this.seed = (float)(Math.random() * 1000.0);
            this.alpha = (float)(0.25 + Math.random() * 0.65);
            Color[] palette = new Color[]{new Color(0, 210, 255), new Color(155, 89, 182), new Color(110, 140, 255), new Color(255, 255, 255), new Color(74, 0, 224)};
            this.color = palette[(int)(Math.random() * (double)palette.length)];
        }

        public void update(float screenW, float screenH) {
            this.y -= this.speedY;
            if (this.y < -15.0f) {
                this.reset(screenW, screenH, false);
            }
        }

        public void render(MatrixStack matrices) {
            float curX = this.x + (float)Math.sin((float)System.currentTimeMillis() * this.wobbleSpeed + this.seed) * this.wobbleAmount;
            Color drawCol = Render2DEngine.applyOpacity(this.color, this.alpha);
            Render2DEngine.drawRound(matrices, curX, this.y, this.size, this.size, this.size / 2.0f, drawCol);
        }
    }
}

