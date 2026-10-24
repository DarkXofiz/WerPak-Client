/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.texture.NativeImageBackedTexture
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  org.joml.Matrix4f
 */
package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sun.jna.Library;
import com.sun.jna.Native;
import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaInfo;
import dev.redstones.mediaplayerinfo.MediaPlayerInfo;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.BuiltBuffer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import thunder.hack.events.impl.EventMouse;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.hud.HudEditorGui;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;

public class SpotifyHud
extends HudElement {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private MediaInfo mediaInfo = new MediaInfo("Waiting for media...", "Unknown Artist", new byte[0], 0L, 100L, false);
    private final Identifier artwork = Identifier.of((String)"thunderhack", (String)"textures/music_artwork.png");
    private boolean artworkRegistered = false;
    private IMediaSession session;
    public final Setting<ThemeStyle> themeStyle = new Setting<ThemeStyle>("Style", ThemeStyle.Neon);
    public final Setting<Boolean> alwaysShow = new Setting<Boolean>("AlwaysShow", true);
    public final Setting<Boolean> glow = new Setting<Boolean>("Glow", true);
    public final Setting<LogoColorMode> logoColorMode = new Setting<LogoColorMode>("LogoColor", LogoColorMode.SpotifyGreen);
    public final Setting<ColorSetting> logoCustomColor = new Setting<ColorSetting>("CustomLogo", new ColorSetting(-14829228), v -> this.logoColorMode.getValue() == LogoColorMode.Custom);
    public final Setting<Boolean> whiteWaves = new Setting<Boolean>("WhiteWaves", false);
    public final Setting<Boolean> logoGlow = new Setting<Boolean>("LogoGlow", true);
    public final Setting<Boolean> customColor = new Setting<Boolean>("CustomColor", false);
    public final Setting<ColorSetting> color = new Setting<ColorSetting>("Color", new ColorSetting(-14829228), v -> this.customColor.getValue());
    public final Setting<Boolean> soundWaves = new Setting<Boolean>("SoundWaves", true);
    public final Setting<Boolean> controls = new Setting<Boolean>("Controls", true);
    private boolean isPlaying = false;
    private long trackDurationSeconds = 180L;
    private long currentPositionMs = 0L;
    private long lastFrameTime = System.currentTimeMillis();
    private float alpha = 0.0f;
    private float progressAnimated = 0.0f;
    private float animatedWidth = 168.0f;
    private final float[] waveHeights = new float[]{3.0f, 3.0f, 3.0f, 3.0f};
    private float prevHover = 0.0f;
    private float playHover = 0.0f;
    private float nextHover = 0.0f;
    private float titleScrollOffset = 0.0f;
    private float titleScrollDir = 1.0f;
    private long titleScrollTimer = 0L;
    private float prevBtnX;
    private float prevBtnY;
    private float playBtnX;
    private float playBtnY;
    private float nextBtnX;
    private float nextBtnY;
    private static final byte VK_MEDIA_NEXT_TRACK = -80;
    private static final byte VK_MEDIA_PREV_TRACK = -79;
    private static final byte VK_MEDIA_PLAY_PAUSE = -77;

    public SpotifyHud() {
        super("Spotify", 168, 48);
    }

    public Color getLogoColor() {
        switch (this.logoColorMode.getValue().ordinal()) {
            case 0: {
                return new Color(1947988);
            }
            case 1: {
                return HudEditor.getColor(0);
            }
            case 2: {
                return this.logoCustomColor.getValue().getColorObject();
            }
            case 3: {
                return Render2DEngine.rainbow(HudEditor.colorSpeed.getValue(), 1, 0.85f, 1.0f, 1.0f);
            }
        }
        return new Color(1947988);
    }

    private int getThemeColor() {
        if (this.customColor.getValue().booleanValue()) {
            return this.color.getValue().getColor();
        }
        return this.getLogoColor().getRGB();
    }

    public static String formatTime(long totalSeconds) {
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format("%d:%02d", minutes, seconds);
    }

    private boolean isHover(float mouseX, float mouseY, float x, float y, float w, float h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    private int getButtonColor(float hoverAnim, int baseAlpha) {
        int r = (int)(160.0f + 95.0f * hoverAnim);
        int g = (int)(160.0f + 95.0f * hoverAnim);
        int b = (int)(160.0f + 95.0f * hoverAnim);
        return baseAlpha << 24 | r << 16 | g << 8 | b;
    }

    private int getButtonColor(float hoverAnim, int baseAlpha, int themeColor) {
        int tr = themeColor >> 16 & 0xFF;
        int tg = themeColor >> 8 & 0xFF;
        int tb = themeColor & 0xFF;
        int r = (int)((float)tr + (float)(255 - tr) * hoverAnim * 0.6f);
        int g = (int)((float)tg + (float)(255 - tg) * hoverAnim * 0.6f);
        int b = (int)((float)tb + (float)(255 - tb) * hoverAnim * 0.6f);
        return baseAlpha << 24 | r << 16 | g << 8 | b;
    }

    public static void drawPlayIcon(MatrixStack matrices, float x, float y, float size, int color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x + 0.5f, y, 0.0f).color(color);
        buffer.vertex(matrix, x + size * 0.9f, y + size * 0.5f, 0.0f).color(color);
        buffer.vertex(matrix, x + 0.5f, y + size, 0.0f).color(color);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        Render2DEngine.endRender();
    }

    public static void drawPauseIcon(MatrixStack matrices, float x, float y, float w, float h, int color) {
        float barW = 2.2f;
        float gap = 2.4f;
        Render2DEngine.drawRound(matrices, x + 0.5f, y, barW, h, 0.6f, new Color(color, true));
        Render2DEngine.drawRound(matrices, x + 0.5f + barW + gap, y, barW, h, 0.6f, new Color(color, true));
    }

    public static void drawNextIcon(MatrixStack matrices, float x, float y, float size, int color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x, y, 0.0f).color(color);
        buffer.vertex(matrix, x + size - 2.0f, y + size * 0.5f, 0.0f).color(color);
        buffer.vertex(matrix, x + size, y + size, 0.0f).color(color);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        Render2DEngine.endRender();
        Render2DEngine.drawRound(matrices, x + size - 1.2f, y, 1.2f, size, 0.4f, new Color(color, true));
    }

    public static void drawPrevIcon(MatrixStack matrices, float x, float y, float size, int color) {
        Render2DEngine.drawRound(matrices, x, y, 1.2f, size, 0.4f, new Color(color, true));
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x + size, y, 0.0f).color(color);
        buffer.vertex(matrix, x + 2.0f, y + size * 0.5f, 0.0f).color(color);
        buffer.vertex(matrix, x + size, y + size, 0.0f).color(color);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        Render2DEngine.endRender();
    }

    private static void sendMediaKey(byte vkCode) {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                User32.INSTANCE.keybd_event(vkCode, (byte)0, 0, 0);
                User32.INSTANCE.keybd_event(vkCode, (byte)0, 2, 0);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private long getMs(long value) {
        if (value > 1000000000L) {
            return value / 10000L;
        }
        if (value < 10000L && value > 0L) {
            return value * 1000L;
        }
        return value;
    }

    @Override
    @EventHandler
    public void onMouse(@NotNull EventMouse event) {
        if (event.getAction() == 1) {
            float mouseY;
            float mouseX;
            if (event.getButton() == 1 && this.isHovering() && (SpotifyHud.mc.currentScreen instanceof HudEditorGui || SpotifyHud.mc.currentScreen instanceof ChatScreen)) {
                ThemeStyle[] styles = ThemeStyle.values();
                this.themeStyle.setValue(styles[(this.themeStyle.getValue().ordinal() + 1) % styles.length]);
            }
            if (this.controls.getValue().booleanValue() && event.getButton() == 0 && (SpotifyHud.mc.currentScreen instanceof ChatScreen || SpotifyHud.mc.currentScreen instanceof HudEditorGui) && this.handleClick(mouseX = (float)this.normaliseX(), mouseY = (float)this.normaliseY())) {
                return;
            }
        }
        super.onMouse(event);
    }

    private boolean handleClick(float mouseX, float mouseY) {
        if (!this.isPlaying) {
            return false;
        }
        if (this.isHover(mouseX, mouseY, this.prevBtnX - 3.0f, this.prevBtnY - 3.0f, 16.0f, 16.0f)) {
            SpotifyHud.sendMediaKey((byte)-79);
            return true;
        }
        if (this.isHover(mouseX, mouseY, this.playBtnX - 3.0f, this.playBtnY - 3.0f, 16.0f, 16.0f)) {
            SpotifyHud.sendMediaKey((byte)-77);
            return true;
        }
        if (this.isHover(mouseX, mouseY, this.nextBtnX - 3.0f, this.nextBtnY - 3.0f, 16.0f, 16.0f)) {
            SpotifyHud.sendMediaKey((byte)-80);
            return true;
        }
        return false;
    }

    @Override
    public void onRender2D(DrawContext context) {
        Color waveColor;
        Color bgInner;
        long elapsedSeconds;
        super.onRender2D(context);
        if (SpotifyHud.mc.player == null) {
            return;
        }
        long now = System.currentTimeMillis();
        long delta = now - this.lastFrameTime;
        this.lastFrameTime = now;
        if (SpotifyHud.mc.player.age % 10 == 0) {
            this.executorService.execute(() -> {
                try {
                    List<IMediaSession> sessions = MediaPlayerInfo.Instance.getMediaSessions();
                    if (sessions == null || sessions.isEmpty()) {
                        return;
                    }
                    IMediaSession currentSession = sessions.stream().filter(s -> s != null && s.getMedia() != null).max(Comparator.comparing(s -> {
                        try {
                            return s.getMedia().getPlaying();
                        }
                        catch (Exception e) {
                            return false;
                        }
                    })).orElse(null);
                    if (currentSession != null) {
                        MediaInfo info = currentSession.getMedia();
                        if (info == null) {
                            return;
                        }
                        if (!info.getTitle().isEmpty() || !info.getArtist().isEmpty() || info.getPlaying()) {
                            boolean needsUpdate;
                            boolean bl = needsUpdate = this.mediaInfo == null || !Arrays.equals(this.mediaInfo.getArtworkPng(), info.getArtworkPng());
                            if (needsUpdate && info.getArtworkPng() != null && info.getArtworkPng().length > 0) {
                                mc.execute(() -> {
                                    try {
                                        NativeImage image = NativeImage.read((InputStream)new ByteArrayInputStream(info.getArtworkPng()));
                                        NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
                                        mc.getTextureManager().registerTexture(this.artwork, (AbstractTexture)texture);
                                        this.artworkRegistered = true;
                                    }
                                    catch (Exception e) {
                                        this.artworkRegistered = false;
                                    }
                                });
                            }
                            long reportedMs = this.getMs(info.getPosition());
                            if (this.mediaInfo == null || !info.getTitle().equals(this.mediaInfo.getTitle())) {
                                this.titleScrollOffset = 0.0f;
                                this.titleScrollDir = 1.0f;
                                this.titleScrollTimer = System.currentTimeMillis();
                                this.currentPositionMs = reportedMs;
                            } else if (reportedMs > 0L && Math.abs(reportedMs - this.currentPositionMs) > 3000L) {
                                this.currentPositionMs = reportedMs;
                            }
                            this.mediaInfo = info;
                            this.session = currentSession;
                        }
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            });
        }
        boolean bl = this.isPlaying = this.mediaInfo != null && this.mediaInfo.getPlaying();
        if (this.isPlaying) {
            this.currentPositionMs += delta;
        }
        boolean active = this.alwaysShow.getValue() != false || this.isPlaying || SpotifyHud.mc.currentScreen instanceof ChatScreen || SpotifyHud.mc.currentScreen instanceof HudEditorGui;
        float targetAlpha = active ? 1.0f : 0.0f;
        this.alpha = MathHelper.clamp((float)Render2DEngine.interpolateFloat(this.alpha, targetAlpha, 0.2f), (float)0.0f, (float)1.0f);
        if (this.alpha <= 0.01f) {
            this.setBounds(this.getPosX(), this.getPosY(), 0.0f, 0.0f);
            return;
        }
        int drawAlpha = (int)(255.0f * this.alpha);
        Color logoColor = this.getLogoColor();
        int themeColor = this.getThemeColor();
        int accentColorWithAlpha = drawAlpha << 24 | themeColor & 0xFFFFFF;
        long l = this.trackDurationSeconds = this.mediaInfo != null ? this.mediaInfo.getDuration() / 10000000L : 180L;
        if (this.trackDurationSeconds == 0L) {
            this.trackDurationSeconds = 180L;
        }
        if ((elapsedSeconds = this.currentPositionMs / 1000L) > this.trackDurationSeconds) {
            elapsedSeconds = this.trackDurationSeconds;
        }
        String displayTitle = this.mediaInfo != null ? this.mediaInfo.getTitle() : "Waiting for media...";
        String displayArtist = this.mediaInfo != null ? this.mediaInfo.getArtist() : "Unknown Artist";
        float titleWidth = FontRenderers.sf_bold.getStringWidth(displayTitle);
        float artistWidth = FontRenderers.sf_bold_mini.getStringWidth(displayArtist);
        float contentW = Math.max(titleWidth, artistWidth);
        float targetWidth = Math.max(168.0f, Math.min(235.0f, 54.0f + contentW + (this.soundWaves.getValue() != false ? 32.0f : 14.0f)));
        this.animatedWidth = Render2DEngine.interpolateFloat(this.animatedWidth, targetWidth, 0.18f);
        float x = this.getPosX();
        float y = this.getPosY();
        float w = this.animatedWidth;
        float h = 48.0f;
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        ThemeStyle style = this.themeStyle.getValue();
        Color bgOuter = style == ThemeStyle.Glass ? new Color(14, 14, 18, (int)(100.0f * this.alpha)) : new Color(14, 14, 18, (int)(235.0f * this.alpha));
        Color color = bgInner = style == ThemeStyle.Glass ? new Color(28, 28, 38, (int)(60.0f * this.alpha)) : new Color(28, 28, 38, (int)(120.0f * this.alpha));
        if (style == ThemeStyle.Neon || this.glow.getValue().booleanValue()) {
            Render2DEngine.drawBlurredShadow(matrices, x - 2.0f, y - 2.0f, w + 4.0f, h + 4.0f, 10, new Color(themeColor & 0xFFFFFF | (int)(45.0f * this.alpha) << 24, true));
        }
        Render2DEngine.drawRound(matrices, x, y, w, h, 6.0f, bgOuter);
        Render2DEngine.drawRound(matrices, x + 0.5f, y + 0.5f, w - 1.0f, h - 1.0f, 5.5f, bgInner);
        float artX = x + 6.0f;
        float artY = y + 6.0f;
        float artSize = 36.0f;
        float radius = artSize / 2.0f;
        Color color2 = waveColor = this.whiteWaves.getValue() != false ? Color.WHITE : new Color(14, 14, 18);
        if (this.logoGlow.getValue().booleanValue() || this.glow.getValue().booleanValue()) {
            Render2DEngine.drawBlurredShadow(matrices, artX - 1.5f, artY - 1.5f, artSize + 3.0f, artSize + 3.0f, 12, Render2DEngine.applyOpacity(logoColor, 0.45f * this.alpha));
        }
        Render2DEngine.drawRound(matrices, artX, artY, artSize, artSize, 5.0f, Render2DEngine.applyOpacity(new Color(30, 30, 30), this.alpha));
        if (this.artworkRegistered && this.mediaInfo != null && this.mediaInfo.getArtworkPng() != null && this.mediaInfo.getArtworkPng().length > 0) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture((int)0, (Identifier)this.artwork);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)this.alpha);
            Render2DEngine.renderTexture(matrices, artX, artY, artSize, artSize, 0.0f, 0.0f, artSize, artSize, artSize, artSize);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        } else {
            FontRenderers.sf_bold.drawCenteredString(matrices, "?", (double)(artX + artSize / 2.0f), (double)(artY + artSize / 2.0f - 4.0f), drawAlpha << 24 | 0xFFFFFF);
        }
        float textX = x + 48.0f;
        float maxTitleW = w - 48.0f - (this.soundWaves.getValue() != false ? 30.0f : 10.0f);
        long nowMs = System.currentTimeMillis();
        if (titleWidth > maxTitleW) {
            if (nowMs - this.titleScrollTimer > 1200L) {
                this.titleScrollDir *= -1.0f;
                this.titleScrollTimer = nowMs;
            }
            this.titleScrollOffset = Render2DEngine.interpolateFloat(this.titleScrollOffset, this.titleScrollDir > 0.0f ? 0.0f : -(titleWidth - maxTitleW + 4.0f), 0.04f);
        } else {
            this.titleScrollOffset = 0.0f;
        }
        Render2DEngine.beginScissor(textX, y, textX + maxTitleW, y + 17.0f);
        FontRenderers.sf_bold.drawString(matrices, displayTitle, (double)(textX + this.titleScrollOffset), (double)(y + 6.5f), drawAlpha << 24 | 0xFFFFFF);
        Render2DEngine.endScissor();
        Render2DEngine.beginScissor(textX, y + 16.0f, textX + maxTitleW, y + 27.0f);
        FontRenderers.sf_bold_mini.drawString(matrices, displayArtist, (double)textX, (double)(y + 17.5f), drawAlpha << 24 | 0x9E9E9E);
        Render2DEngine.endScissor();
        if (this.soundWaves.getValue().booleanValue()) {
            float waveBaseX = x + w - 24.0f;
            for (int i = 0; i < 4; ++i) {
                float targetSize = this.isPlaying ? 2.5f + (float)(Math.abs(Math.sin((double)now / 160.0 + (double)i * 0.85)) * 8.5) : 2.5f;
                this.waveHeights[i] = Render2DEngine.interpolateFloat(this.waveHeights[i], targetSize, 0.25);
                float barX = waveBaseX + (float)i * 4.2f;
                float barY = y + 8.5f + (11.0f - this.waveHeights[i]) / 2.0f;
                Render2DEngine.drawRound(matrices, barX, barY, 2.0f, this.waveHeights[i], 0.8f, new Color(accentColorWithAlpha, true));
            }
        }
        float barX = textX;
        float barY = y + 28.5f;
        float barW = w - 54.0f;
        float barH = 2.2f;
        Render2DEngine.drawRound(matrices, barX, barY, barW, barH, 1.1f, new Color(255, 255, 255, (int)(35.0f * this.alpha)));
        float targetProgress = this.trackDurationSeconds > 0L ? MathHelper.clamp((float)((float)elapsedSeconds / (float)this.trackDurationSeconds), (float)0.0f, (float)1.0f) : 0.0f;
        this.progressAnimated = Render2DEngine.interpolateFloat(this.progressAnimated, targetProgress, 0.2f);
        if (this.progressAnimated > 0.005f) {
            Render2DEngine.drawRound(matrices, barX, barY, Math.max(2.2f, barW * this.progressAnimated), barH, 1.1f, new Color(accentColorWithAlpha, true));
        }
        float bottomY = y + 34.5f;
        String timeStr = SpotifyHud.formatTime(elapsedSeconds) + " / " + SpotifyHud.formatTime(this.trackDurationSeconds);
        FontRenderers.sf_bold_micro.drawString(matrices, timeStr, (double)barX, (double)(bottomY + 2.0f), drawAlpha << 24 | 0x8A8A8E);
        if (this.controls.getValue().booleanValue()) {
            float mouseX = this.normaliseX();
            float mouseY = this.normaliseY();
            this.prevBtnX = x + w - 44.0f;
            this.prevBtnY = bottomY;
            this.playBtnX = x + w - 30.0f;
            this.playBtnY = bottomY;
            this.nextBtnX = x + w - 16.0f;
            this.nextBtnY = bottomY;
            boolean canInteract = SpotifyHud.mc.currentScreen instanceof ChatScreen || SpotifyHud.mc.currentScreen instanceof HudEditorGui;
            boolean prevHov = canInteract && this.isHover(mouseX, mouseY, this.prevBtnX - 2.0f, this.prevBtnY - 2.0f, 12.0f, 12.0f);
            boolean playHov = canInteract && this.isHover(mouseX, mouseY, this.playBtnX - 2.0f, this.playBtnY - 2.0f, 12.0f, 12.0f);
            boolean nextHov = canInteract && this.isHover(mouseX, mouseY, this.nextBtnX - 2.0f, this.nextBtnY - 2.0f, 12.0f, 12.0f);
            this.prevHover = Render2DEngine.interpolateFloat(this.prevHover, prevHov ? 1.0f : 0.0f, 0.25);
            this.playHover = Render2DEngine.interpolateFloat(this.playHover, playHov ? 1.0f : 0.0f, 0.25);
            this.nextHover = Render2DEngine.interpolateFloat(this.nextHover, nextHov ? 1.0f : 0.0f, 0.25);
            int pCol = this.getButtonColor(this.prevHover, drawAlpha);
            int plCol = this.getButtonColor(this.playHover, drawAlpha, themeColor);
            int nCol = this.getButtonColor(this.nextHover, drawAlpha);
            SpotifyHud.drawPrevIcon(matrices, this.prevBtnX, this.prevBtnY, 8.0f, pCol);
            if (this.isPlaying) {
                SpotifyHud.drawPauseIcon(matrices, this.playBtnX, this.playBtnY, 8.0f, 8.0f, plCol);
            } else {
                SpotifyHud.drawPlayIcon(matrices, this.playBtnX, this.playBtnY, 8.0f, plCol);
            }
            SpotifyHud.drawNextIcon(matrices, this.nextBtnX, this.nextBtnY, 8.0f, nCol);
        }
        matrices.pop();
        this.setBounds(x, y, w, h);
    }

    @Override
    public void onEnable() {
        this.lastFrameTime = System.currentTimeMillis();
        this.currentPositionMs = 0L;
    }

    @Override
    public void onDisable() {
    }

    public static enum ThemeStyle {
        Solid,
        Neon,
        Glass;

    }

    public static enum LogoColorMode {
        SpotifyGreen,
        Theme,
        Custom,
        Rainbow;

    }

    private static interface User32
    extends Library {
        public static final User32 INSTANCE = (User32)Native.load((String)"user32", User32.class);

        public void keybd_event(byte var1, byte var2, int var3, int var4);
    }
}

