/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
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
 */
package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
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
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.hud.impl.Particles;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.features.modules.misc.NameProtect;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.hud.HudEditorGui;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.ThunderUtility;
import thunder.hack.utility.Timer;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.TextureStorage;
import thunder.hack.utility.render.animation.EaseOutBack;
import thunder.hack.utility.render.animation.EaseOutCirc;

public class TargetHud
extends HudElement {
    private final Setting<Integer> blurRadius = new Setting<Integer>("BallonBlur", 10, 1, 10);
    private final Setting<Integer> animX = new Setting<Integer>("AnimationX", 0, -2000, 2000);
    private final Setting<Integer> animY = new Setting<Integer>("AnimationY", 0, -2000, 2000);
    private final Setting<HPmodeEn> hpMode = new Setting<HPmodeEn>("HP Mode", HPmodeEn.HP);
    private final Setting<ImageModeEn> imageMode = new Setting<ImageModeEn>("Image", ImageModeEn.Anime);
    private final Setting<ModeEn> Mode = new Setting<ModeEn>("Mode", ModeEn.ThunderHack);
    private final Setting<ColorSetting> color = new Setting<ColorSetting>("Color1", new ColorSetting(-16492289), v -> this.Mode.getValue() == ModeEn.CelkaPasta);
    private final Setting<ColorSetting> color2 = new Setting<ColorSetting>("Color2", new ColorSetting(-16492289), v -> this.Mode.getValue() == ModeEn.CelkaPasta);
    private final Setting<Boolean> funTimeHP = new Setting<Boolean>("FunTimeHP", false);
    private final Setting<Boolean> mini = new Setting<Boolean>("Mini", false, v -> this.Mode.getValue() == ModeEn.NurikZapen);
    private final Setting<Boolean> absorp = new Setting<Boolean>("Absorption", true);
    private static Identifier custom;
    public EaseOutBack animation = new EaseOutBack();
    public static EaseOutCirc healthAnimation;
    public static EaseOutCirc headAnimation;
    private final ArrayList<Particles> particles = new ArrayList();
    private boolean sentParticles;
    private boolean direction = false;
    private LivingEntity target;
    float ticks;
    private final Timer timer = new Timer();

    public TargetHud() {
        super("TargetHud", 150, 50);
    }

    @Override
    public void onEnable() {
        try {
            custom = ThunderUtility.getCustomImg("thud");
        }
        catch (Exception e) {
            this.sendMessage(".minecraft -> ThunderHackRecode -> misc -> images -> thud.png");
        }
    }

    public String getDurationString(StatusEffectInstance pe) {
        if (pe.isInfinite()) {
            return "*:*";
        }
        return pe.getDuration() / 1200 + ":" + pe.getDuration() % 1200 / 20;
    }

    @Override
    public void onUpdate() {
        this.animation.update(this.direction);
        healthAnimation.update();
        headAnimation.update();
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        this.getTarget();
        if (this.target == null) {
            return;
        }
        float health = Math.min(this.target.getMaxHealth(), this.getHealth());
        context.getMatrices().push();
        if (!HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            if (this.Mode.is(ModeEn.NurikZapen) && this.mini.getValue().booleanValue()) {
                TargetHud.sizeAnimation(context.getMatrices(), this.getPosX() + 45.0f + (float)this.animX.getValue().intValue(), this.getPosY() + 15.0f + (float)this.animY.getValue().intValue(), this.animation.getAnimationd());
            } else {
                TargetHud.sizeAnimation(context.getMatrices(), this.getPosX() + 75.0f + (float)this.animX.getValue().intValue(), this.getPosY() + 25.0f + (float)this.animY.getValue().intValue(), this.animation.getAnimationd());
            }
        }
        if (this.animation.getAnimationd() > 0.0) {
            float animationFactor = (float)MathUtility.clamp(this.animation.getAnimationd(), 0.0, 1.0);
            switch (this.Mode.getValue().ordinal()) {
                case 0: {
                    this.renderThunderHack(context, health, animationFactor);
                    break;
                }
                case 2: {
                    this.renderCelkaPasta(context, health);
                    break;
                }
                case 1: {
                    if (this.mini.getValue().booleanValue()) {
                        this.renderMiniNurik(context, health, animationFactor);
                        break;
                    }
                    this.renderNurik(context, health, animationFactor);
                }
            }
        }
        context.getMatrices().pop();
    }

    private void getTarget() {
        if (Aura.target != null) {
            if (Aura.target instanceof LivingEntity) {
                this.target = (LivingEntity)Aura.target;
                this.direction = true;
            } else {
                this.target = null;
                this.direction = false;
            }
        } else if (TargetHud.mc.currentScreen instanceof ChatScreen || TargetHud.mc.currentScreen instanceof HudEditorGui) {
            this.target = TargetHud.mc.player;
            this.direction = true;
        } else {
            this.direction = false;
            if (this.animation.getAnimationd() < 0.02) {
                this.target = null;
            }
        }
    }

    private void renderCelkaPasta(DrawContext context, float health) {
        float hurtPercent = (float)this.target.hurtTime / 6.0f;
        Render2DEngine.drawBlurredShadow(context.getMatrices(), this.getPosX() - 2.0f, this.getPosY() - 2.0f, 164.0f, 51.0f, 5, this.color.getValue().getColorObject());
        Render2DEngine.drawRect(context.getMatrices(), this.getPosX(), this.getPosY(), 160.0f, 47.0f, new Color(0x66000000, true));
        this.setBounds(this.getPosX(), this.getPosY(), 160.0f, 47.0f);
        Render2DEngine.drawRect(context.getMatrices(), this.getPosX() + 117.0f, this.getPosY() + 4.0f, 18.0f, 18.0f, new Color(0x4D000000, true));
        Render2DEngine.drawRect(context.getMatrices(), this.getPosX() + 137.0f, this.getPosY() + 4.0f, 18.0f, 18.0f, new Color(0x4D000000, true));
        Render2DEngine.drawRect(context.getMatrices(), this.getPosX() + 117.0f, this.getPosY() + 25.0f, 18.0f, 18.0f, new Color(0x4D000000, true));
        Render2DEngine.drawRect(context.getMatrices(), this.getPosX() + 137.0f, this.getPosY() + 25.0f, 18.0f, 18.0f, new Color(0x4D000000, true));
        Render2DEngine.drawBlurredShadow(context.getMatrices(), this.getPosX() + 49.0f, this.getPosY() + 29.0f, 62.0f, 12.0f, 5, this.color.getValue().getColorObject().brighter().brighter().brighter());
        Render2DEngine.drawRect(context.getMatrices(), this.getPosX() + 50.0f, this.getPosY() + 30.0f, 60.0f, 10.0f, new Color(-1644167168, true));
        Render2DEngine.drawRect(context.getMatrices(), this.getPosX() + 50.0f, this.getPosY() + 30.0f, MathUtility.clamp((int)(60.0f * (health / this.target.getMaxHealth())), 0, 60), 10.0f, this.color.getValue().getColorObject().brighter().brighter().brighter());
        if (this.target instanceof PlayerEntity) {
            RenderSystem.setShaderTexture((int)0, (Identifier)((AbstractClientPlayerEntity)this.target).getSkinTextures().texture());
        } else {
            RenderSystem.setShaderTexture((int)0, (Identifier)mc.getEntityRenderDispatcher().getRenderer((Entity)this.target).getTexture((Entity)this.target));
        }
        RenderSystem.setShaderColor((float)1.0f, (float)(1.0f - hurtPercent), (float)(1.0f - hurtPercent), (float)1.0f);
        Render2DEngine.renderTexture(context.getMatrices(), this.getPosX() + 3.5f + hurtPercent, this.getPosY() + 3.5f + hurtPercent, 40.0f - hurtPercent * 2.0f, 40.0f - hurtPercent * 2.0f, 8.0f, 8.0f, 8.0, 8.0, 64.0, 64.0);
        Render2DEngine.renderTexture(context.getMatrices(), this.getPosX() + 3.5f + hurtPercent, this.getPosY() + 3.5f + hurtPercent, 40.0f - hurtPercent * 2.0f, 40.0f - hurtPercent * 2.0f, 40.0f, 8.0f, 8.0, 8.0, 64.0, 64.0);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        FontRenderers.modules.drawString(context.getMatrices(), ModuleManager.media.isEnabled() ? "Protected " : (ModuleManager.nameProtect.isEnabled() && this.target == TargetHud.mc.player ? NameProtect.getCustomName() : this.target.getName().getString()), (double)(this.getPosX() + 50.0f), (double)(this.getPosY() + 7.0f), -1);
        FontRenderers.modules.drawCenteredString(context.getMatrices(), (String)(this.hpMode.getValue() == HPmodeEn.HP ? String.valueOf((double)Math.round(10.0 * (double)this.getHealth()) / 10.0) : (double)Math.round(10.0 * (double)this.getHealth()) / 10.0 / 20.0 * 100.0 + "%"), (double)(this.getPosX() + 81.0f), (double)(this.getPosY() + 34.0f), -1);
        LivingEntity class_13092 = this.target;
        if (class_13092 instanceof PlayerEntity) {
            PlayerEntity pe = (PlayerEntity)class_13092;
            this.celestialArmor(context, pe, this.getPosX(), this.getPosY());
            this.celestialHands(context, pe, this.getPosX(), this.getPosY());
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private void renderNurik(DrawContext context, float health, float animationFactor) {
        float hurtPercent = Render2DEngine.interpolateFloat(MathUtility.clamp(this.target.hurtTime == 0 ? 0 : this.target.hurtTime + 1, 0, 10), this.target.hurtTime, Render3DEngine.getTickDelta()) / 8.0f;
        healthAnimation.setValue(health);
        health = (float)healthAnimation.getAnimationD();
        if (this.animation.getAnimationd() != 1.0 && !HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawGradientBlurredShadow1(context.getMatrices(), this.getPosX() + 4.0f, this.getPosY() + 4.0f, 131.0f, 40.0f, 14, HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));
            Render2DEngine.renderRoundedGradientRect(context.getMatrices(), HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90), this.getPosX(), this.getPosY(), 137.0f, 47.5f, 9.0f);
            Render2DEngine.drawRound(context.getMatrices(), this.getPosX() + 0.5f, this.getPosY() + 0.5f, 136.0f, 46.0f, 9.0f, Render2DEngine.injectAlpha(Color.BLACK, 220));
        } else {
            Render2DEngine.drawHudBase2(context.getMatrices(), this.getPosX(), this.getPosY(), 137.0f, 47.5f, 9.0f, HudEditor.blurStrength.getValue().floatValue(), HudEditor.blurOpacity.getValue().floatValue(), animationFactor);
        }
        this.setBounds(this.getPosX(), this.getPosY(), 137.0f, 47.5f);
        if (this.target instanceof PlayerEntity) {
            RenderSystem.setShaderTexture((int)0, (Identifier)((AbstractClientPlayerEntity)this.target).getSkinTextures().texture());
        } else {
            RenderSystem.setShaderTexture((int)0, (Identifier)mc.getEntityRenderDispatcher().getRenderer((Entity)this.target).getTexture((Entity)this.target));
        }
        context.getMatrices().push();
        context.getMatrices().translate(this.getPosX() + 3.5f + 20.0f, this.getPosY() + 3.5f + 20.0f, 0.0f);
        context.getMatrices().scale(1.0f - hurtPercent / 15.0f, 1.0f - hurtPercent / 15.0f, 1.0f);
        context.getMatrices().translate(-(this.getPosX() + 3.5f + 20.0f), -(this.getPosY() + 3.5f + 20.0f), 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.colorMask((boolean)false, (boolean)false, (boolean)false, (boolean)true);
        RenderSystem.clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        RenderSystem.clear((int)16384, (boolean)false);
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Render2DEngine.renderRoundedQuadInternal(context.getMatrices().peek().getPositionMatrix(), animationFactor, animationFactor, animationFactor, animationFactor, this.getPosX() + 3.5f, this.getPosY() + 3.5f, this.getPosX() + 3.5f + 40.0f, this.getPosY() + 3.5f + 40.0f, 7.0, 10.0);
        RenderSystem.blendFunc((int)772, (int)773);
        RenderSystem.setShaderColor((float)animationFactor, (float)(animationFactor - hurtPercent / 2.0f), (float)(animationFactor - hurtPercent / 2.0f), (float)((float)MathUtility.clamp(this.animation.getAnimationd(), 0.0, 1.0)));
        Render2DEngine.renderTexture(context.getMatrices(), this.getPosX() + 3.5f, this.getPosY() + 3.5f, 40.0, 40.0, 8.0f, 8.0f, 8.0, 8.0, 64.0, 64.0);
        Render2DEngine.renderTexture(context.getMatrices(), this.getPosX() + 3.5f, this.getPosY() + 3.5f, 40.0, 40.0, 40.0f, 8.0f, 8.0, 8.0, 64.0, 64.0);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.defaultBlendFunc();
        context.getMatrices().pop();
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawRect(context.getMatrices(), this.getPosX() + 48.0f, this.getPosY() + 32.0f, 85.0f, 11.0f, 4.0f, (float)((double)0.15f * this.animation.getAnimationd()));
            Render2DEngine.drawRect(context.getMatrices(), this.getPosX() + 48.0f, this.getPosY() + 32.0f, MathUtility.clamp(85.0f * (health / this.target.getMaxHealth()), 8.0f, 85.0f), 11.0f, 4.0f, (float)this.animation.getAnimationd());
        } else {
            Render2DEngine.drawGradientRound(context.getMatrices(), this.getPosX() + 48.0f, this.getPosY() + 32.0f, 85.0f, 11.0f, 4.0f, HudEditor.getColor(0).darker().darker(), HudEditor.getColor(0).darker().darker().darker().darker(), HudEditor.getColor(0).darker().darker().darker().darker(), HudEditor.getColor(0).darker().darker().darker().darker());
            Render2DEngine.renderRoundedGradientRect(context.getMatrices(), HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(0), HudEditor.getColor(270), this.getPosX() + 48.0f, this.getPosY() + 32.0f, (int)MathUtility.clamp(85.0f * (health / this.target.getMaxHealth()), 8.0f, 85.0f), 11.0f, 4.0f);
        }
        FontRenderers.sf_bold.drawCenteredString(context.getMatrices(), (String)(this.hpMode.getValue() == HPmodeEn.HP ? String.valueOf((double)Math.round(10.0 * (double)this.getHealth()) / 10.0) : (double)Math.round(10.0 * (double)this.getHealth()) / 10.0 / 20.0 * 100.0 + "%"), (double)(this.getPosX() + 92.0f), (double)(this.getPosY() + 35.0f), Render2DEngine.applyOpacity(-1, animationFactor));
        FontRenderers.sf_bold.drawString(context.getMatrices(), ModuleManager.media.isEnabled() ? "Protected " : (ModuleManager.nameProtect.isEnabled() && this.target == TargetHud.mc.player ? NameProtect.getCustomName() : this.target.getName().getString()), (double)(this.getPosX() + 48.0f), (double)(this.getPosY() + 7.0f), Render2DEngine.applyOpacity(-1, animationFactor));
        if (this.target instanceof PlayerEntity) {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)((float)MathUtility.clamp(this.animation.getAnimationd(), 0.0, 1.0)));
            DefaultedList armor = ((PlayerEntity)this.target).getInventory().armor;
            ItemStack[] items = new ItemStack[]{this.target.getMainHandStack(), (ItemStack)armor.get(3), (ItemStack)armor.get(2), (ItemStack)armor.get(1), (ItemStack)armor.get(0), this.target.getOffHandStack()};
            float xItemOffset = this.getPosX() + 48.0f;
            for (ItemStack itemStack : items) {
                context.getMatrices().push();
                context.getMatrices().translate(xItemOffset, this.getPosY() + 15.0f, 0.0f);
                context.getMatrices().scale(0.75f, 0.75f, 0.75f);
                context.drawItem(itemStack, 0, 0);
                context.drawItemInSlot(TargetHud.mc.textRenderer, itemStack, 0, 0);
                context.getMatrices().pop();
                xItemOffset += 12.0f;
            }
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }

    private void renderMiniNurik(DrawContext context, float health, float animationFactor) {
        float hurtPercent = Render2DEngine.interpolateFloat(MathUtility.clamp(this.target.hurtTime == 0 ? 0 : this.target.hurtTime + 1, 0, 10), this.target.hurtTime, Render3DEngine.getTickDelta()) / 8.0f;
        healthAnimation.setValue(health);
        health = (float)healthAnimation.getAnimationD();
        if (this.animation.getAnimationd() != 1.0 && !HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawGradientBlurredShadow1(context.getMatrices(), this.getPosX() + 2.0f, this.getPosY() + 2.0f, 91.0f, 31.0f, 12, HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));
            Render2DEngine.renderRoundedGradientRect(context.getMatrices(), HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90), this.getPosX(), this.getPosY(), 95.0f, 35.0f, 7.0f);
            Render2DEngine.drawRound(context.getMatrices(), this.getPosX() + 0.5f, this.getPosY() + 0.5f, 94.0f, 34.0f, 7.0f, Render2DEngine.injectAlpha(Color.BLACK, 220));
        } else {
            Render2DEngine.drawHudBase2(context.getMatrices(), this.getPosX(), this.getPosY(), 95.0f, 35.5f, 8.0f, HudEditor.blurStrength.getValue().floatValue(), HudEditor.blurOpacity.getValue().floatValue(), animationFactor);
        }
        this.setBounds(this.getPosX(), this.getPosY(), 95.0f, 35.5f);
        if (this.target instanceof PlayerEntity) {
            RenderSystem.setShaderTexture((int)0, (Identifier)((AbstractClientPlayerEntity)this.target).getSkinTextures().texture());
        } else {
            RenderSystem.setShaderTexture((int)0, (Identifier)mc.getEntityRenderDispatcher().getRenderer((Entity)this.target).getTexture((Entity)this.target));
        }
        context.getMatrices().push();
        context.getMatrices().translate((double)this.getPosX() + 2.5 + 15.0, (double)this.getPosY() + 2.5 + 15.0, 0.0);
        context.getMatrices().scale(1.0f - hurtPercent / 20.0f, 1.0f - hurtPercent / 20.0f, 1.0f);
        context.getMatrices().translate(-((double)this.getPosX() + 2.5 + 15.0), -((double)this.getPosY() + 2.5 + 15.0), 0.0);
        RenderSystem.enableBlend();
        RenderSystem.colorMask((boolean)false, (boolean)false, (boolean)false, (boolean)true);
        RenderSystem.clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        RenderSystem.clear((int)16384, (boolean)false);
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Render2DEngine.renderRoundedQuadInternal(context.getMatrices().peek().getPositionMatrix(), animationFactor, animationFactor, animationFactor, animationFactor, (double)this.getPosX() + 2.5, (double)this.getPosY() + 2.5, (double)this.getPosX() + 2.5 + 30.0, (double)this.getPosY() + 2.5 + 30.0, 5.0, 10.0);
        RenderSystem.blendFunc((int)772, (int)773);
        RenderSystem.setShaderColor((float)animationFactor, (float)(animationFactor - hurtPercent / 2.0f), (float)(animationFactor - hurtPercent / 2.0f), (float)animationFactor);
        Render2DEngine.renderTexture(context.getMatrices(), (double)this.getPosX() + 2.5, (double)this.getPosY() + 2.5, 30.0, 30.0, 8.0f, 8.0f, 8.0, 8.0, 64.0, 64.0);
        Render2DEngine.renderTexture(context.getMatrices(), (double)this.getPosX() + 2.5, (double)this.getPosY() + 2.5, 30.0, 30.0, 40.0f, 8.0f, 8.0, 8.0, 64.0, 64.0);
        RenderSystem.defaultBlendFunc();
        context.getMatrices().pop();
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawRect(context.getMatrices(), this.getPosX() + 38.0f, this.getPosY() + 25.0f, 52.0f, 7.0f, 2.0f, (float)((double)0.15f * this.animation.getAnimationd()));
            Render2DEngine.drawRect(context.getMatrices(), this.getPosX() + 38.0f, this.getPosY() + 25.0f, MathUtility.clamp(52.0f * (health / this.target.getMaxHealth()), 8.0f, 52.0f), 7.0f, 2.0f, (float)this.animation.getAnimationd());
        } else {
            Render2DEngine.drawGradientRound(context.getMatrices(), this.getPosX() + 38.0f, this.getPosY() + 25.0f, 52.0f, 7.0f, 2.0f, HudEditor.getColor(0).darker().darker(), HudEditor.getColor(0).darker().darker().darker().darker(), HudEditor.getColor(0).darker().darker().darker().darker(), HudEditor.getColor(0).darker().darker().darker().darker());
            Render2DEngine.renderRoundedGradientRect(context.getMatrices(), HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(0), HudEditor.getColor(270), this.getPosX() + 38.0f, this.getPosY() + 25.0f, (int)MathUtility.clamp(52.0f * (health / this.target.getMaxHealth()), 8.0f, 52.0f), 7.0f, 2.0f);
        }
        FontRenderers.sf_bold_mini.drawCenteredString(context.getMatrices(), (String)(this.hpMode.getValue() == HPmodeEn.HP ? String.valueOf((double)Math.round(10.0 * (double)this.getHealth()) / 10.0) : (double)Math.round(10.0 * (double)this.getHealth()) / 10.0 / 20.0 * 100.0 + "%"), (double)(this.getPosX() + 65.0f), (double)(this.getPosY() + 27.0f), Render2DEngine.applyOpacity(-1, animationFactor));
        FontRenderers.sf_bold_mini.drawString(context.getMatrices(), ModuleManager.media.isEnabled() ? "Protected " : (ModuleManager.nameProtect.isEnabled() && this.target == TargetHud.mc.player ? NameProtect.getCustomName() : this.target.getName().getString()), (double)(this.getPosX() + 38.0f), (double)(this.getPosY() + 5.0f), Render2DEngine.applyOpacity(-1, animationFactor));
        if (this.target instanceof PlayerEntity) {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)((float)MathUtility.clamp(this.animation.getAnimationd(), 0.0, 1.0)));
            DefaultedList armor = ((PlayerEntity)this.target).getInventory().armor;
            ItemStack[] items = new ItemStack[]{this.target.getMainHandStack(), (ItemStack)armor.get(3), (ItemStack)armor.get(2), (ItemStack)armor.get(1), (ItemStack)armor.get(0), this.target.getOffHandStack()};
            float xItemOffset = this.getPosX() + 38.0f;
            for (ItemStack itemStack : items) {
                context.getMatrices().push();
                context.getMatrices().translate(xItemOffset, this.getPosY() + 13.0f, 0.0f);
                context.getMatrices().scale(0.5f, 0.5f, 0.5f);
                context.drawItem(itemStack, 0, 0);
                context.drawItemInSlot(TargetHud.mc.textRenderer, itemStack, 0, 0);
                context.getMatrices().pop();
                xItemOffset += 9.0f;
            }
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }

    /*
     * WARNING - void declaration
     */
    private void renderThunderHack(DrawContext context, float health, float animationFactor) {
        float hurtPercent;
        block15: {
            block17: {
                block16: {
                    hurtPercent = (float)this.target.hurtTime / 6.0f;
                    Render2DEngine.drawRound(context.getMatrices(), this.getPosX(), this.getPosY(), 70.0f, 50.0f, 6.0f, new Color(0, 0, 0, 139));
                    Render2DEngine.drawRound(context.getMatrices(), this.getPosX() + 50.0f, this.getPosY(), 100.0f, 50.0f, 6.0f, new Color(0, 0, 0, 255));
                    this.setBounds(this.getPosX(), this.getPosY(), 150.0f, 50.0f);
                    if (this.imageMode.is(ImageModeEn.None)) break block15;
                    if (!this.imageMode.is(ImageModeEn.Anime)) break block16;
                    RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.thudPic);
                    break block17;
                }
                if (custom == null) break block15;
                RenderSystem.setShaderTexture((int)0, (Identifier)custom);
            }
            context.getMatrices().push();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Render2DEngine.drawRound(context.getMatrices(), this.getPosX() + 50.0f, this.getPosY(), 100.0f, 50.0f, 12.0f, new Color(0, 0, 0, 255));
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor((float)0.3f, (float)0.3f, (float)0.3f, (float)1.0f);
            Render2DEngine.renderTexture(context.getMatrices(), this.getPosX() + 50.0f, this.getPosY(), 95.0, 50.0, 0.0f, 0.0f, 100.0, 50.0, 100.0, 50.0);
            context.getMatrices().pop();
        }
        for (Particles particles : this.particles) {
            if (!(particles.opacity > 4.0)) continue;
            particles.render2D(context.getMatrices());
        }
        if (this.timer.passedMs(16L)) {
            this.ticks += 0.1f;
            for (Particles particles : this.particles) {
                particles.updatePosition();
                if (!(particles.opacity < 1.0)) continue;
                this.particles.remove(particles);
            }
            this.timer.reset();
        }
        ArrayList<Particles> removeList = new ArrayList<Particles>();
        for (Particles p : this.particles) {
            if (!(p.opacity <= 1.0)) continue;
            removeList.add(p);
        }
        for (Particles p : removeList) {
            this.particles.remove(p);
        }
        if (this.target.hurtTime == 9 && !this.sentParticles) {
            for (int var6_12 = 0; var6_12 <= 6; ++var6_12) {
                Particles p = new Particles();
                Color c = Particles.mixColors(this.color.getValue().getColorObject(), this.color2.getValue().getColorObject(), (Math.sin(this.ticks + this.getPosX() * 0.4f + (float)var6_12) + 1.0) * 0.5);
                p.init(this.getPosX(), this.getPosY(), MathUtility.random(-3.0f, 3.0f), MathUtility.random(-3.0f, 3.0f), 20.0, c);
                this.particles.add(p);
            }
            this.sentParticles = true;
        }
        if (this.target.hurtTime == 8) {
            this.sentParticles = false;
        }
        float f = hurtPercent;
        headAnimation.setValue(f);
        if (this.target instanceof PlayerEntity) {
            RenderSystem.setShaderTexture((int)0, (Identifier)((AbstractClientPlayerEntity)this.target).getSkinTextures().texture());
        } else {
            RenderSystem.setShaderTexture((int)0, (Identifier)mc.getEntityRenderDispatcher().getRenderer((Entity)this.target).getTexture((Entity)this.target));
        }
        context.getMatrices().push();
        context.getMatrices().translate((double)this.getPosX() + 2.5 + 15.0, (double)this.getPosY() + 2.5 + 15.0, 0.0);
        context.getMatrices().scale(1.0f - hurtPercent / 20.0f, 1.0f - hurtPercent / 20.0f, 1.0f);
        context.getMatrices().translate(-((double)this.getPosX() + 2.5 + 15.0), -((double)this.getPosY() + 2.5 + 15.0), 0.0);
        RenderSystem.enableBlend();
        RenderSystem.colorMask((boolean)false, (boolean)false, (boolean)false, (boolean)true);
        RenderSystem.clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        RenderSystem.clear((int)16384, (boolean)false);
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Render2DEngine.renderRoundedQuadInternal(context.getMatrices().peek().getPositionMatrix(), animationFactor, animationFactor, animationFactor, animationFactor, (double)this.getPosX() + 2.5, (double)this.getPosY() + 2.5, (double)this.getPosX() + 2.5 + 45.0, (double)this.getPosY() + 2.5 + 45.0, 5.0, 10.0);
        RenderSystem.blendFunc((int)772, (int)773);
        RenderSystem.setShaderColor((float)animationFactor, (float)(animationFactor - hurtPercent / 2.0f), (float)(animationFactor - hurtPercent / 2.0f), (float)animationFactor);
        Render2DEngine.renderTexture(context.getMatrices(), (double)this.getPosX() + 2.5, (double)this.getPosY() + 2.5, 45.0, 45.0, 8.0f, 8.0f, 8.0, 8.0, 64.0, 64.0);
        Render2DEngine.renderTexture(context.getMatrices(), (double)this.getPosX() + 2.5, (double)this.getPosY() + 2.5, 45.0, 45.0, 40.0f, 8.0f, 8.0, 8.0, 64.0, 64.0);
        RenderSystem.defaultBlendFunc();
        context.getMatrices().pop();
        healthAnimation.setValue(health);
        health = (float)healthAnimation.getAnimationD();
        Render2DEngine.drawBlurredShadow(context.getMatrices(), this.getPosX() + 55.0f, this.getPosY() + 22.0f, 90.0f, 8.0f, this.blurRadius.getValue(), HudEditor.getColor(0));
        Render2DEngine.drawGradientRound(context.getMatrices(), this.getPosX() + 55.0f, this.getPosY() + 35.0f - 14.0f, 90.0f, 10.0f, 2.0f, HudEditor.getColor(0).darker().darker(), HudEditor.getColor(0).darker().darker().darker().darker(), HudEditor.getColor(0).darker().darker().darker().darker(), HudEditor.getColor(0).darker().darker().darker().darker());
        Render2DEngine.renderRoundedGradientRect(context.getMatrices(), HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(0), HudEditor.getColor(270), this.getPosX() + 55.0f, this.getPosY() + 35.0f - 14.0f, (int)MathUtility.clamp(90.0f * (health / this.target.getMaxHealth()), 3.0f, 90.0f), 10.0f, 2.0f);
        FontRenderers.sf_bold.drawCenteredString(context.getMatrices(), (String)(this.hpMode.getValue() == HPmodeEn.HP ? String.valueOf((double)Math.round(10.0 * (double)this.getHealth()) / 10.0) : (int)((double)Math.round(10.0 * (double)health) / 10.0 / 20.0 * 100.0) + "%"), (double)(this.getPosX() + 102.0f), (double)(this.getPosY() + 24.0f), Render2DEngine.applyOpacity(-1, animationFactor));
        FontRenderers.sf_bold.drawString(context.getMatrices(), ModuleManager.media.isEnabled() ? "Protected " : (ModuleManager.nameProtect.isEnabled() && this.target == TargetHud.mc.player ? NameProtect.getCustomName() : this.target.getName().getString()), (double)(this.getPosX() + 55.0f), (double)(this.getPosY() + 5.0f), Render2DEngine.applyOpacity(-1, animationFactor));
        if (this.target instanceof PlayerEntity) {
            DefaultedList armor = ((PlayerEntity)this.target).getInventory().armor;
            ItemStack[] items = new ItemStack[]{this.target.getMainHandStack(), (ItemStack)armor.get(3), (ItemStack)armor.get(2), (ItemStack)armor.get(1), (ItemStack)armor.get(0), this.target.getOffHandStack()};
            float xItemOffset = this.getPosX() + 60.0f;
            for (ItemStack itemStack : items) {
                if (itemStack.isEmpty()) continue;
                context.getMatrices().push();
                context.getMatrices().translate(xItemOffset, this.getPosY() + 35.0f, 0.0f);
                context.getMatrices().scale(0.75f, 0.75f, 0.75f);
                context.drawItem(itemStack, 0, 0);
                context.drawItemInSlot(TargetHud.mc.textRenderer, itemStack, 0, 0);
                context.getMatrices().pop();
                xItemOffset += 14.0f;
            }
            this.drawPotionEffect(context.getMatrices(), (PlayerEntity)this.target);
        }
    }

    private void celestialArmor(DrawContext context, PlayerEntity target, float posX, float posY) {
        for (int i = 0; i < 4; ++i) {
            if (((ItemStack)target.getInventory().armor.get(3 - i)).isEmpty()) continue;
            context.getMatrices().push();
            context.getMatrices().translate(posX + (float)(i > 1 ? 138 : 118), posY + (float)(i % 2 == 0 ? 5 : 26), 0.0f);
            context.drawItem((ItemStack)target.getInventory().armor.get(3 - i), 0, 0);
            context.drawItemInSlot(TargetHud.mc.textRenderer, (ItemStack)target.getInventory().armor.get(3 - i), 0, 0);
            context.getMatrices().pop();
        }
    }

    private void celestialHands(DrawContext context, PlayerEntity target, float posX, float posY) {
        for (int i = 0; i < 2; ++i) {
            if ((i == 0 ? target.getMainHandStack() : target.getOffHandStack()).isEmpty()) continue;
            context.getMatrices().push();
            context.getMatrices().translate(posX + (float)(i == 0 ? 50 : 77), posY + 14.0f, 0.0f);
            context.getMatrices().scale(0.75f, 0.75f, 1.0f);
            context.drawItem(i == 0 ? target.getMainHandStack() : target.getOffHandStack(), 0, 0);
            context.getMatrices().pop();
            FontRenderers.settings.drawString(context.getMatrices(), "x" + (i == 0 ? target.getMainHandStack() : target.getOffHandStack()).getCount(), (double)(posX + (float)(i == 0 ? 50 : 77) + 12.0f), (double)(posY + 21.0f), -1);
        }
    }

    private void drawPotionEffect(MatrixStack ms, PlayerEntity entity) {
        StringBuilder finalString = new StringBuilder();
        for (StatusEffectInstance potionEffect : entity.getStatusEffects()) {
            boolean potRanOut;
            StatusEffect potion = (StatusEffect)potionEffect.getEffectType().value();
            if (potion != StatusEffects.REGENERATION.value() && potion != StatusEffects.SPEED.value() && potion != StatusEffects.STRENGTH.value() && potion != StatusEffects.WEAKNESS.value()) continue;
            boolean bl = potRanOut = (double)potionEffect.getDuration() != 0.0;
            if (!entity.hasStatusEffect(potionEffect.getEffectType()) || !potRanOut) continue;
            finalString.append(TargetHud.getPotionName(potion)).append(potionEffect.getAmplifier() < 1 ? "" : Integer.valueOf(potionEffect.getAmplifier() + 1)).append(" ").append(this.getDurationString(potionEffect)).append(" ");
        }
        FontRenderers.settings.drawString(ms, finalString.toString(), (double)(this.getPosX() + 55.0f), (double)(this.getPosY() + 15.0f), new Color(0x8D8D8D).getRGB());
    }

    public float getHealth() {
        LivingEntity class_13092 = this.target;
        if (class_13092 instanceof PlayerEntity) {
            PlayerEntity ent = (PlayerEntity)class_13092;
            if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getServerInfo() != null && TargetHud.mc.getNetworkHandler().getServerInfo().address.contains("funtime") || this.funTimeHP.getValue().booleanValue()) {
                ScoreboardObjective scoreBoard = null;
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

    public static void sizeAnimation(MatrixStack matrixStack, double width, double height, double animation) {
        matrixStack.translate(width, height, 0.0);
        matrixStack.scale((float)animation, (float)animation, 1.0f);
        matrixStack.translate(-width, -height, 0.0);
    }

    public static String getPotionName(StatusEffect p) {
        if (p == StatusEffects.REGENERATION.value()) {
            return "Reg";
        }
        if (p == StatusEffects.STRENGTH.value()) {
            return "Str";
        }
        if (p == StatusEffects.SPEED.value()) {
            return "Spd";
        }
        if (p == StatusEffects.HASTE.value()) {
            return "H";
        }
        if (p == StatusEffects.WEAKNESS.value()) {
            return "W";
        }
        if (p == StatusEffects.RESISTANCE.value()) {
            return "Res";
        }
        return "pon";
    }

    static {
        healthAnimation = new EaseOutCirc();
        headAnimation = new EaseOutCirc();
    }

    public static enum HPmodeEn {
        HP,
        Percentage;

    }

    public static enum ImageModeEn {
        None,
        Anime,
        Custom;

    }

    public static enum ModeEn {
        ThunderHack,
        NurikZapen,
        CelkaPasta;

    }
}

