/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.features.hud.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.Render2DEngine;

public class WorldLoadAnim
extends HudElement {
    private static final long FADE_IN = 450L;
    private static final long HOLD = 1400L;
    private static final long GLITCH_AT = 1000L;
    private static final long FADE_OUT = 700L;
    private static final long TOTAL = 2550L;
    private long lastJoinTime = 0L;
    private boolean wasNull = true;
    private final List<Particle> particles = new ArrayList<Particle>();
    private boolean particlesSpawned = false;
    private float eltrixSlide = 0.0f;
    private float barProgress = 0.0f;

    public WorldLoadAnim() {
        super("WorldLoadAnim", 0, 0);
    }

    @Override
    public void onUpdate() {
        if (WorldLoadAnim.mc.world == null) {
            this.wasNull = true;
            this.particles.clear();
            this.particlesSpawned = false;
            this.eltrixSlide = 0.0f;
            this.barProgress = 0.0f;
        } else if (this.wasNull) {
            this.wasNull = false;
            this.lastJoinTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onRender2D(DrawContext context) {
        float alpha;
        if (WorldLoadAnim.mc.world == null || WorldLoadAnim.mc.player == null) {
            return;
        }
        long elapsed = System.currentTimeMillis() - this.lastJoinTime;
        if (elapsed > 2550L) {
            this.particles.clear();
            return;
        }
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        float cx = (float)sw * 0.5f;
        float cy = (float)sh * 0.5f;
        if (elapsed < 450L) {
            alpha = (float)elapsed / 450.0f;
        } else if (elapsed < 1850L) {
            alpha = 1.0f;
        } else {
            float t = (float)(elapsed - 450L - 1400L) / 700.0f;
            alpha = 1.0f - t;
        }
        alpha = MathHelper.clamp((float)alpha, (float)0.0f, (float)1.0f);
        float exitScale = 1.0f;
        if (elapsed > 1850L) {
            float t = (float)(elapsed - 450L - 1400L) / 700.0f;
            exitScale = 1.0f + t * 0.18f;
        }
        float popScale = 1.0f;
        if (elapsed < 450L) {
            float t = (float)elapsed / 450.0f;
            popScale = WorldLoadAnim.easeOutBack(t);
        }
        float logoScale = popScale * exitScale;
        float holdProgress = (float)Math.max(0L, elapsed - 450L) / 1400.0f;
        this.eltrixSlide = MathHelper.clamp((float)Render2DEngine.interpolateFloat(this.eltrixSlide, alpha > 0.05f ? 1.0f : 0.0f, 0.25), (float)0.0f, (float)1.0f);
        this.barProgress = MathHelper.clamp((float)(holdProgress * 1.05f), (float)0.0f, (float)1.0f);
        if (!this.particlesSpawned && (double)elapsed > 270.0) {
            this.spawnParticles(cx, cy);
            this.particlesSpawned = true;
        }
        MatrixStack ms = context.getMatrices();
        ms.push();
        int bgA = (int)(255.0f * alpha);
        this.drawFullBackground(ms, sw, sh, bgA);
        this.updateAndDrawParticles(ms, alpha);
        ms.push();
        ms.translate(cx, cy, 0.0f);
        ms.scale(logoScale, logoScale, 1.0f);
        ms.translate(-cx, -cy, 0.0f);
        this.drawZGlow(ms, cx, cy, alpha);
        int themeC = HudEditor.getColor(0).getRGB();
        String zStr = "Z";
        float zW = FontRenderers.sf_bold.getStringWidth(zStr) * 2.2f;
        float zH = 39.600002f;
        String eltrixStr = "eltrix";
        float eltrixW = FontRenderers.sf_bold.getStringWidth(eltrixStr) * 2.2f;
        float totalW = zW + eltrixW + 4.0f;
        float logoX = cx - totalW * 0.5f;
        float logoY = cy - zH * 0.5f - 4.0f;
        Render2DEngine.drawBlurredShadow(ms, logoX - 6.0f, logoY - 4.0f, zW + 12.0f, zH + 8.0f, 12, new Color(themeC >> 16 & 0xFF, themeC >> 8 & 0xFF, themeC & 0xFF, (int)(160.0f * alpha)));
        ms.push();
        ms.translate(logoX + zW * 0.5f, logoY + zH * 0.5f, 0.0f);
        ms.scale(2.2f, 2.2f, 1.0f);
        ms.translate(-(logoX + zW * 0.5f), -(logoY + zH * 0.5f), 0.0f);
        FontRenderers.sf_bold.drawString(ms, zStr, (double)logoX, (double)logoY, WorldLoadAnim.withAlpha(themeC, (int)(255.0f * alpha)));
        ms.pop();
        float slideOff = (1.0f - this.eltrixSlide) * 35.0f;
        float eltrixX = logoX + zW + 4.0f + slideOff;
        float eltrixAlpha = this.eltrixSlide * alpha;
        ms.push();
        ms.translate(eltrixX + eltrixW * 0.5f, logoY + zH * 0.5f, 0.0f);
        ms.scale(2.2f, 2.2f, 1.0f);
        ms.translate(-(eltrixX + eltrixW * 0.5f), -(logoY + zH * 0.5f), 0.0f);
        FontRenderers.sf_bold.drawString(ms, eltrixStr, (double)eltrixX, (double)logoY, WorldLoadAnim.withAlpha(0xFFFFFF, (int)(255.0f * eltrixAlpha)));
        ms.pop();
        float lineY = logoY + zH + 6.0f;
        float lineHalf = totalW * 0.5f * this.eltrixSlide;
        this.drawGradientLine(ms, cx - lineHalf, lineY, cx + lineHalf, lineY + 0.9f, themeC, alpha);
        ms.pop();
        float barW = Math.min((float)sw * 0.35f, 220.0f);
        float barH = 2.5f;
        float barX = cx - barW * 0.5f;
        float barY = cy + 40.0f;
        this.drawProgressBar(ms, barX, barY, barW, barH, this.barProgress, themeC, alpha);
        String sub = this.barProgress < 0.99f ? "Loading world..." : "Ready!";
        float subW = FontRenderers.sf_bold_mini.getStringWidth(sub);
        FontRenderers.sf_bold_mini.drawString(ms, sub, (double)(cx - subW * 0.5f), (double)(barY + 6.0f), WorldLoadAnim.withAlpha(0xBBBBBB, (int)(200.0f * alpha * this.eltrixSlide)));
        if (elapsed > 1000L && elapsed < 1080L) {
            float gT = (float)(elapsed - 1000L) / 80.0f;
            float gA = (float)Math.sin((double)gT * Math.PI) * 0.18f * alpha;
            Render2DEngine.drawRect(ms, 0.0f, 0.0f, sw, sh, new Color(themeC >> 16 & 0xFF, themeC >> 8 & 0xFF, themeC & 0xFF, (int)(255.0f * gA)));
        }
        ms.pop();
    }

    private void drawFullBackground(MatrixStack ms, int sw, int sh, int bgA) {
        Render2DEngine.drawRect(ms, 0.0f, 0.0f, sw, sh, new Color(8, 8, 14, bgA));
        int vigA = (int)((float)bgA * 0.45f);
        Render2DEngine.drawBlurredShadow(ms, (float)sw * 0.1f, (float)sh * 0.1f, (float)sw * 0.8f, (float)sh * 0.8f, 40, new Color(0, 0, 0, 0));
    }

    private void drawZGlow(MatrixStack ms, float cx, float cy, float alpha) {
        double pulse = 0.5 + 0.5 * Math.sin((double)System.currentTimeMillis() / 380.0);
        int themeC = HudEditor.getColor(0).getRGB();
        int r = themeC >> 16 & 0xFF;
        int g = themeC >> 8 & 0xFF;
        int b = themeC & 0xFF;
        for (int i = 5; i >= 1; --i) {
            float size = 55.0f + (float)i * 18.0f;
            float ga = (float)(0.012 * (double)(6 - i) * (double)alpha * (0.7 + 0.3 * pulse));
            Render2DEngine.drawBlurredShadow(ms, cx - size, cy - size, size * 2.0f, size * 2.0f, 22 - i * 2, new Color(r, g, b, (int)(255.0f * ga)));
        }
    }

    private void drawGradientLine(MatrixStack ms, float x1, float y, float x2, float yb, int themeC, float alpha) {
        int r = themeC >> 16 & 0xFF;
        int g = themeC >> 8 & 0xFF;
        int b = themeC & 0xFF;
        float mid = (x1 + x2) * 0.5f;
        Color transparent = new Color(r, g, b, 0);
        Color solid = new Color(r, g, b, (int)(255.0f * alpha));
        Render2DEngine.horizontalGradient(ms, x1, y, mid, yb, transparent, solid);
        Render2DEngine.horizontalGradient(ms, mid, y, x2, yb, solid, transparent);
    }

    private void drawProgressBar(MatrixStack ms, float x, float y, float w, float h, float progress, int themeC, float alpha) {
        int a = (int)(255.0f * alpha);
        Render2DEngine.drawRound(ms, x, y, w, h, h * 0.5f, new Color(30, 30, 40, (int)(180.0f * alpha)));
        if (progress > 0.01f) {
            float filled = w * progress;
            int r = themeC >> 16 & 0xFF;
            int g = themeC >> 8 & 0xFF;
            int b = themeC & 0xFF;
            Render2DEngine.drawRound(ms, x, y, filled, h, h * 0.5f, new Color(r, g, b, a));
            Render2DEngine.drawBlurredShadow(ms, x + filled - 3.0f, y - 2.0f, 6.0f, h + 4.0f, 6, new Color(r, g, b, (int)(180.0f * alpha)));
        }
    }

    private void spawnParticles(float cx, float cy) {
        Random rng = new Random();
        int themeC = HudEditor.getColor(0).getRGB();
        for (int i = 0; i < 28; ++i) {
            double angle = Math.toRadians((double)i * 12.857142857142858 + (double)(rng.nextFloat() * 12.0f));
            float speed = 0.4f + rng.nextFloat() * 1.6f;
            float vx = (float)(Math.cos(angle) * (double)speed);
            float vy = (float)(Math.sin(angle) * (double)speed);
            float radius = 1.2f + rng.nextFloat() * 2.2f;
            float life = 0.5f + rng.nextFloat() * 0.5f;
            Color pc = rng.nextBoolean() ? new Color(themeC >> 16 & 0xFF, themeC >> 8 & 0xFF, themeC & 0xFF, 255) : new Color(200, 180, 255, 255);
            this.particles.add(new Particle(cx, cy, vx, vy, radius, life, pc));
        }
    }

    private void updateAndDrawParticles(MatrixStack ms, float globalAlpha) {
        long now = System.currentTimeMillis();
        this.particles.removeIf(p -> p.isDead(now));
        for (Particle p2 : this.particles) {
            p2.update(now);
            float pa = p2.getAlpha(now) * globalAlpha;
            if (pa < 0.01f) continue;
            Color c = p2.color;
            Render2DEngine.drawRound(ms, p2.x - p2.radius, p2.y - p2.radius, p2.radius * 2.0f, p2.radius * 2.0f, p2.radius, new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(255.0f * pa)));
        }
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return 1.0f + c3 * (float)Math.pow(t - 1.0f, 3.0) + c1 * (float)Math.pow(t - 1.0f, 2.0);
    }

    private static int withAlpha(int rgb, int alpha) {
        alpha = MathHelper.clamp((int)alpha, (int)0, (int)255);
        return alpha << 24 | rgb & 0xFFFFFF;
    }

    private static class Particle {
        float x;
        float y;
        final float vx;
        final float vy;
        final float radius;
        final float lifetime;
        final Color color;
        final long born;

        Particle(float x, float y, float vx, float vy, float radius, float lifetime, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = radius;
            this.lifetime = lifetime;
            this.color = color;
            this.born = System.currentTimeMillis();
        }

        boolean isDead(long now) {
            return now - this.born >= (long)(this.lifetime * 1000.0f);
        }

        void update(long now) {
            float t = (float)(now - this.born) / 1000.0f;
            this.x += this.vx;
            this.y = this.y + this.vy - t * 0.12f;
        }

        float getAlpha(long now) {
            float t = (float)(now - this.born) / (this.lifetime * 1000.0f);
            if (t < 0.15f) {
                return t / 0.15f;
            }
            return 1.0f - (t - 0.15f) / 0.85f;
        }
    }
}

