/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.scoreboard.ScoreboardObjective
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
 *  net.minecraft.text.MutableText
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.scoreboard.ScoreboardDisplaySlot
 *  net.minecraft.scoreboard.ReadableScoreboardScore
 *  net.minecraft.scoreboard.ScoreHolder
 *  net.minecraft.scoreboard.number.NumberFormat
 *  net.minecraft.scoreboard.number.StyledNumberFormat
 *  net.minecraft.client.render.BuiltBuffer
 *  org.joml.Matrix4f
 */
package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.scoreboard.ScoreboardObjective;
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
import net.minecraft.text.MutableText;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.client.render.BuiltBuffer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventMouse;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.features.modules.misc.NameProtect;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.hud.HudEditorGui;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;

public class ZeltrixWareZenoBest_CrackTargetHud
extends HudElement {
    public final Setting<ThemeStyle> themeStyle = new Setting<ThemeStyle>("Style", ThemeStyle.Neon);
    public final Setting<Float> scale = new Setting<Float>("Scale", Float.valueOf(1.0f), Float.valueOf(0.5f), Float.valueOf(2.0f));
    private final Setting<Boolean> customColor = new Setting<Boolean>("CustomColor", false);
    private final Setting<ColorSetting> color = new Setting<ColorSetting>("Color", new ColorSetting(-16718337), v -> this.customColor.getValue());
    private final Setting<Boolean> absorp = new Setting<Boolean>("Absorption", true);
    private final Setting<Boolean> funTimeHP = new Setting<Boolean>("FunTimeHP", false);
    private static final int MAX_DIGITS = 3;
    private final float[] digitOffsetY = new float[3];
    private final float[] digitAlpha = new float[3];
    private final long[] digitChangeTime = new long[3];
    private final char[] lastDigits = new char[3];
    private int lastRoundedHp = -1;
    private float alpha = 0.0f;
    private float hpAnimated = 20.0f;
    private float hpProgressAnimated = 1.0f;
    private float absorptionAnimated = 0.0f;
    private LivingEntity target;
    private boolean direction = false;

    public ZeltrixWareZenoBest_CrackTargetHud() {
        super("ZeltrixWareZenoBest_CrackTargetHud", 140, 36);
    }

    private void updateTarget() {
        Entity class_12972 = Aura.target;
        if (class_12972 instanceof LivingEntity) {
            LivingEntity living;
            this.target = living = (LivingEntity)class_12972;
            this.direction = true;
        } else if (ZeltrixWareZenoBest_CrackTargetHud.mc.currentScreen instanceof ChatScreen || ZeltrixWareZenoBest_CrackTargetHud.mc.currentScreen instanceof HudEditorGui) {
            this.target = ZeltrixWareZenoBest_CrackTargetHud.mc.player;
            this.direction = true;
        } else {
            this.direction = false;
            if (this.alpha < 0.02f) {
                this.target = null;
            }
        }
    }

    public float getTargetHealth() {
        if (this.target == null) {
            return 20.0f;
        }
        LivingEntity class_13092 = this.target;
        if (class_13092 instanceof PlayerEntity) {
            PlayerEntity ent = (PlayerEntity)class_13092;
            if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getServerInfo() != null && ZeltrixWareZenoBest_CrackTargetHud.mc.getNetworkHandler().getServerInfo().address.contains("funtime") || this.funTimeHP.getValue().booleanValue()) {
                ScoreboardObjective scoreBoard;
                String resolvedHp = "";
                if (ent.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME) != null && (scoreBoard = ent.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME)) != null) {
                    ReadableScoreboardScore readableScoreboardScore = ent.getScoreboard().getScore((ScoreHolder)ent, scoreBoard);
                    MutableText text2 = ReadableScoreboardScore.getFormattedScore((ReadableScoreboardScore)readableScoreboardScore, (NumberFormat)scoreBoard.getNumberFormatOr((NumberFormat)StyledNumberFormat.EMPTY));
                    resolvedHp = text2.getString();
                }
                float numValue = 0.0f;
                try {
                    numValue = Float.parseFloat(resolvedHp);
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
                return this.absorp.getValue() != false ? numValue + this.target.getAbsorptionAmount() : numValue;
            }
        }
        return this.absorp.getValue() != false ? this.target.getHealth() + this.target.getAbsorptionAmount() : this.target.getHealth();
    }

    private int getThemeColor() {
        if (this.customColor.getValue().booleanValue()) {
            return this.color.getValue().getColor();
        }
        return HudEditor.getColor(0).getRGB();
    }

    private float getDurabilityProgress(ItemStack stack) {
        if (stack.isEmpty() || !stack.isDamageable()) {
            return 1.0f;
        }
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) {
            return 1.0f;
        }
        return MathHelper.clamp((float)(1.0f - (float)stack.getDamage() / (float)maxDamage), (float)0.0f, (float)1.0f);
    }

    private int getDurabilityColor(float progress, int alphaVal) {
        int b;
        int g;
        int r;
        if (progress > 0.6f) {
            float t = (progress - 0.6f) / 0.4f;
            r = (int)(255.0f * (1.0f - t));
            g = 210;
            b = 60;
        } else if (progress > 0.3f) {
            float t = (progress - 0.3f) / 0.3f;
            r = 255;
            g = (int)(210.0f * t);
            b = 0;
        } else {
            r = 230;
            g = (int)(60.0f * (progress / 0.3f));
            b = 20;
        }
        return alphaVal << 24 | r << 16 | g << 8 | b;
    }

    public static void drawRingArc(MatrixStack matrices, float centerX, float centerY, float radius, float thickness, float startAngle, float endAngle, int color) {
        if (startAngle >= endAngle) {
            return;
        }
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        float innerRadius = Math.max(0.1f, radius - thickness * 0.5f);
        float outerRadius = radius + thickness * 0.5f;
        float step = 3.0f;
        for (float angle = startAngle; angle <= endAngle + 0.1f; angle += step) {
            float curAngle = Math.min(angle, endAngle);
            double rad = Math.toRadians(curAngle);
            float cos = (float)Math.cos(rad);
            float sin = (float)Math.sin(rad);
            float ox = centerX + cos * outerRadius;
            float oy = centerY + sin * outerRadius;
            float ix = centerX + cos * innerRadius;
            float iy = centerY + sin * innerRadius;
            buffer.vertex(matrix, ox, oy, 0.0f).color(color);
            buffer.vertex(matrix, ix, iy, 0.0f).color(color);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        RenderSystem.disableBlend();
    }

    @Override
    @EventHandler
    public void onMouse(@NotNull EventMouse event) {
        if (event.getAction() == 1 && event.getButton() == 1 && this.isHovering() && (ZeltrixWareZenoBest_CrackTargetHud.mc.currentScreen instanceof HudEditorGui || ZeltrixWareZenoBest_CrackTargetHud.mc.currentScreen instanceof ChatScreen)) {
            ThemeStyle[] styles = ThemeStyle.values();
            this.themeStyle.setValue(styles[(this.themeStyle.getValue().ordinal() + 1) % styles.length]);
        }
        super.onMouse(event);
    }

    @Override
    public void onRender2D(DrawContext context) {
        LivingEntity class_13092;
        int i;
        super.onRender2D(context);
        if (ZeltrixWareZenoBest_CrackTargetHud.mc.player == null) {
            return;
        }
        this.updateTarget();
        float targetAlpha = this.direction ? 1.0f : 0.0f;
        this.alpha = MathHelper.clamp((float)Render2DEngine.interpolateFloat(this.alpha, targetAlpha, 0.22f), (float)0.0f, (float)1.0f);
        if (this.target == null || this.alpha <= 0.01f) {
            this.setBounds(this.getPosX(), this.getPosY(), 0.0f, 0.0f);
            return;
        }
        float maxHealth = Math.max(1.0f, this.target.getMaxHealth());
        float curHealth = this.getTargetHealth();
        this.hpAnimated = Render2DEngine.interpolateFloat(this.hpAnimated, curHealth, 0.2f);
        float targetHpProgress = MathHelper.clamp((float)(curHealth / maxHealth), (float)0.0f, (float)1.0f);
        this.hpProgressAnimated = Render2DEngine.interpolateFloat(this.hpProgressAnimated, targetHpProgress, 0.2f);
        float absorpAmount = this.absorp.getValue() != false ? this.target.getAbsorptionAmount() : 0.0f;
        float targetAbsorpProgress = absorpAmount > 0.0f ? MathHelper.clamp((float)(absorpAmount / maxHealth), (float)0.0f, (float)1.0f) : 0.0f;
        this.absorptionAnimated = Render2DEngine.interpolateFloat(this.absorptionAnimated, targetAbsorpProgress, 0.2f);
        String name = ModuleManager.media.isEnabled() ? "Protected" : (ModuleManager.nameProtect.isEnabled() && this.target == ZeltrixWareZenoBest_CrackTargetHud.mc.player ? NameProtect.getCustomName() : this.target.getName().getString());
        int roundedHp = Math.round(this.hpAnimated);
        long now = System.currentTimeMillis();
        String hpText = String.valueOf(Math.max(0, roundedHp));
        char[] curDigits = new char[3];
        for (i = 0; i < 3; ++i) {
            curDigits[i] = 32;
        }
        for (i = 0; i < hpText.length(); ++i) {
            curDigits[3 - hpText.length() + i] = hpText.charAt(i);
        }
        if (this.lastRoundedHp == -1) {
            System.arraycopy(curDigits, 0, this.lastDigits, 0, 3);
            for (i = 0; i < 3; ++i) {
                this.digitOffsetY[i] = 0.0f;
                this.digitAlpha[i] = 1.0f;
                this.digitChangeTime[i] = now - 1000L;
            }
        } else if (roundedHp != this.lastRoundedHp) {
            boolean isDamage = roundedHp < this.lastRoundedHp;
            for (int i2 = 0; i2 < 3; ++i2) {
                if (curDigits[i2] == this.lastDigits[i2]) continue;
                this.digitOffsetY[i2] = isDamage ? 5.0f : -5.0f;
                this.digitAlpha[i2] = 0.0f;
                this.digitChangeTime[i2] = now;
            }
            System.arraycopy(curDigits, 0, this.lastDigits, 0, 3);
        }
        this.lastRoundedHp = roundedHp;
        for (i = 0; i < 3; ++i) {
            float el = (float)(now - this.digitChangeTime[i]) / 1000.0f;
            float t = Math.min(1.0f, el / 0.28f);
            float ease = 1.0f - (1.0f - t) * (1.0f - t) * (1.0f - t);
            float fadeT = Math.min(1.0f, el / 0.22f);
            this.digitOffsetY[i] = this.digitOffsetY[i] * (1.0f - ease);
            this.digitAlpha[i] = (float)Math.sin((double)fadeT * Math.PI * 0.5);
        }
        float x = this.getPosX();
        float y = this.getPosY();
        float padding = 5.0f;
        float headSize = 26.0f;
        float healthRingRadius = 9.5f;
        float healthRingSize = healthRingRadius * 2.0f;
        float textX = x + padding + headSize + 6.0f;
        float nameWidth = FontRenderers.sf_bold_mini.getStringWidth(name);
        float itemsWidth = 66.0f;
        float contentW = Math.max(Math.max(nameWidth, itemsWidth), 66.0f);
        float width = padding + headSize + 6.0f + contentW + 10.0f + healthRingSize + padding;
        float height = padding * 2.0f + headSize;
        float headX = x + padding;
        float headY = y + padding;
        float nameY = y + padding + 1.0f;
        float itemsY = nameY + 12.0f;
        float healthCenterX = x + width - padding - healthRingRadius;
        float healthCenterY = y + height * 0.5f;
        int drawAlpha = (int)(255.0f * this.alpha);
        int themeColor = this.customColor.getValue() != false ? this.color.getValue().getColorObject().getRGB() : HudEditor.getColor(0).getRGB();
        int themeColorWithAlpha = drawAlpha << 24 | themeColor & 0xFFFFFF;
        ThemeStyle style = this.themeStyle.getValue();
        Color bgOuter = style == ThemeStyle.Glass ? new Color(14, 14, 18, (int)(100.0f * this.alpha)) : new Color(14, 14, 18, (int)(230.0f * this.alpha));
        Color bgInner = style == ThemeStyle.Glass ? new Color(25, 25, 32, (int)(60.0f * this.alpha)) : new Color(25, 25, 32, (int)(120.0f * this.alpha));
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        float s = this.scale.getValue().floatValue();
        matrices.translate(x, y, 0.0f);
        matrices.scale(s, s, 1.0f);
        matrices.translate(-x, -y, 0.0f);
        if (style == ThemeStyle.Neon) {
            Render2DEngine.drawBlurredShadow(matrices, x - 1.0f, y - 1.0f, width + 2.0f, height + 2.0f, 8, new Color(themeColor & 0xFFFFFF | 0x50000000, true));
        }
        Render2DEngine.drawRound(matrices, x, y, width, height, 5.0f, bgOuter);
        Render2DEngine.drawRound(matrices, x + 0.5f, y + 0.5f, width - 1.0f, height - 1.0f, 4.5f, bgInner);
        float hurtPercent = 0.0f;
        if (this.target.hurtTime > 0) {
            hurtPercent = MathHelper.clamp((float)((float)this.target.hurtTime / 10.0f * 0.55f), (float)0.0f, (float)0.55f);
        }
        if ((class_13092 = this.target) instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)class_13092;
            RenderSystem.setShaderTexture((int)0, (Identifier)((AbstractClientPlayerEntity)player).getSkinTextures().texture());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor((float)1.0f, (float)(1.0f - hurtPercent), (float)(1.0f - hurtPercent), (float)this.alpha);
            Render2DEngine.renderTexture(matrices, headX, headY, headSize, headSize, 8.0f, 8.0f, 8.0, 8.0, 64.0, 64.0);
            Render2DEngine.renderTexture(matrices, headX, headY, headSize, headSize, 40.0f, 8.0f, 8.0, 8.0, 64.0, 64.0);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        } else {
            RenderSystem.setShaderTexture((int)0, (Identifier)mc.getEntityRenderDispatcher().getRenderer((Entity)this.target).getTexture((Entity)this.target));
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor((float)1.0f, (float)(1.0f - hurtPercent), (float)(1.0f - hurtPercent), (float)this.alpha);
            Render2DEngine.renderTexture(matrices, headX, headY, headSize, headSize, 0.0f, 0.0f, 1.0, 1.0, 1.0, 1.0);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        FontRenderers.sf_bold_mini.drawString(matrices, name, (double)textX, (double)nameY, drawAlpha << 24 | 0xFFFFFF);
        ItemStack[] items = new ItemStack[6];
        LivingEntity class_13093 = this.target;
        if (class_13093 instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)class_13093;
            DefaultedList armor = player.getInventory().armor;
            items[0] = (ItemStack)armor.get(3);
            items[1] = (ItemStack)armor.get(2);
            items[2] = (ItemStack)armor.get(1);
            items[3] = (ItemStack)armor.get(0);
            items[4] = player.getOffHandStack();
            items[5] = player.getMainHandStack();
        } else {
            items[0] = this.target.getEquippedStack(EquipmentSlot.HEAD);
            items[1] = this.target.getEquippedStack(EquipmentSlot.CHEST);
            items[2] = this.target.getEquippedStack(EquipmentSlot.LEGS);
            items[3] = this.target.getEquippedStack(EquipmentSlot.FEET);
            items[4] = this.target.getOffHandStack();
            items[5] = this.target.getMainHandStack();
        }
        float itemScale = 0.55f;
        for (int i3 = 0; i3 < 6; ++i3) {
            ItemStack stack = items[i3];
            float slotX = textX + (float)i3 * 11.0f;
            float slotY = itemsY;
            if (stack == null || stack.isEmpty()) {
                Render2DEngine.drawRound(matrices, slotX + 1.0f, slotY + 1.0f, 7.5f, 7.5f, 2.0f, new Color(36, 38, 48, (int)(140.0f * this.alpha)));
                continue;
            }
            matrices.push();
            matrices.translate(slotX - 1.0f, slotY - 1.0f, 0.0f);
            matrices.scale(itemScale, itemScale, 1.0f);
            context.drawItem(stack, 0, 0);
            matrices.pop();
            if (!stack.isDamageable() || stack.getDamage() <= 0) continue;
            float durProgress = this.getDurabilityProgress(stack);
            float durW = 8.5f;
            float durBarY = slotY + 9.5f;
            int durBg = Math.min(drawAlpha, 180) << 24 | 0x242630;
            int durFill = this.getDurabilityColor(durProgress, drawAlpha);
            Render2DEngine.drawRound(matrices, slotX, durBarY, durW, 1.4f, 0.5f, new Color(durBg, true));
            if (!(durProgress > 0.01f)) continue;
            Render2DEngine.drawRound(matrices, slotX, durBarY, durW * durProgress, 1.4f, 0.5f, new Color(durFill, true));
        }
        int tr = themeColor >> 16 & 0xFF;
        int tg = themeColor >> 8 & 0xFF;
        int tb = themeColor & 0xFF;
        int ringBgColor = drawAlpha << 24 | tr / 4 << 16 | tg / 4 << 8 | tb / 4;
        ZeltrixWareZenoBest_CrackTargetHud.drawRingArc(matrices, healthCenterX, healthCenterY, healthRingRadius, 2.2f, 0.0f, 360.0f, ringBgColor);
        float sweep = MathHelper.clamp((float)this.hpProgressAnimated, (float)0.0f, (float)1.0f) * 360.0f;
        if (sweep > 0.5f) {
            ZeltrixWareZenoBest_CrackTargetHud.drawRingArc(matrices, healthCenterX, healthCenterY, healthRingRadius, 2.2f, -90.0f, -90.0f + sweep, themeColorWithAlpha);
        }
        if (this.absorptionAnimated > 0.01f) {
            float absorbSweep = MathHelper.clamp((float)this.absorptionAnimated, (float)0.0f, (float)1.0f) * 360.0f;
            int goldColor = drawAlpha << 24 | 0xFFC800;
            ZeltrixWareZenoBest_CrackTargetHud.drawRingArc(matrices, healthCenterX, healthCenterY, healthRingRadius, 2.2f, -90.0f, -90.0f + absorbSweep, goldColor);
        }
        float totalW = FontRenderers.sf_bold_mini.getStringWidth(hpText);
        float startX = healthCenterX - totalW * 0.5f;
        float baseY = healthCenterY - 3.5f;
        float sx1 = x + (healthCenterX - healthRingRadius - 2.0f - x) * s;
        float sy1 = y + (healthCenterY - healthRingRadius - 2.0f - y) * s;
        float sx2 = x + (healthCenterX + healthRingRadius + 2.0f - x) * s;
        float sy2 = y + (healthCenterY + healthRingRadius + 2.0f - y) * s;
        Render2DEngine.beginScissor(sx1, sy1, sx2, sy2);
        for (int i4 = 0; i4 < hpText.length(); ++i4) {
            int slot = 3 - hpText.length() + i4;
            String ch = String.valueOf(hpText.charAt(i4));
            float charW = FontRenderers.sf_bold_mini.getStringWidth(ch);
            int charAlpha = (int)((float)drawAlpha * this.digitAlpha[slot]);
            int charColor = MathHelper.clamp((int)charAlpha, (int)0, (int)255) << 24 | 0xFFFFFF;
            FontRenderers.sf_bold_mini.drawString(matrices, ch, (double)startX, (double)(baseY + this.digitOffsetY[slot]), charColor);
            startX += charW;
        }
        Render2DEngine.endScissor();
        matrices.pop();
        this.setBounds(x, y, width * s, height * s);
    }

    public static enum ThemeStyle {
        Solid,
        Neon,
        Glass;

    }
}

