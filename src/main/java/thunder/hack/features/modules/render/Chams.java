/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.entity.EntityPose
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.entity.EndCrystalEntityRenderer
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  org.joml.Quaternionf
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package thunder.hack.features.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityPose;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.EventHeldItemRenderer;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

public class Chams
extends Module {
    public final Setting<Boolean> handItems = new Setting<Boolean>("HandItems", false);
    private final Setting<ColorSetting> handItemsColor = new Setting<ColorSetting>("HandItemsColor", new ColorSetting(new Color(-1827152291, true)), v -> this.handItems.getValue());
    public final Setting<Boolean> crystals = new Setting<Boolean>("Crystals", false);
    private final Setting<ColorSetting> crystalColor = new Setting<ColorSetting>("CrystalColor", new ColorSetting(new Color(-1825711896, true)), v -> this.crystals.getValue());
    private final Setting<Boolean> staticCrystal = new Setting<Boolean>("StaticCrystal", true, v -> this.crystals.getValue());
    private final Setting<CMode> crystalMode = new Setting<CMode>("CrystalMode", CMode.One, v -> this.crystals.getValue());
    public final Setting<Boolean> players = new Setting<Boolean>("Players", false);
    private final Setting<ColorSetting> playerColor = new Setting<ColorSetting>("PlayerColor", new ColorSetting(new Color(-1825711896, true)), v -> this.players.getValue());
    private final Setting<ColorSetting> friendColor = new Setting<ColorSetting>("FriendColor", new ColorSetting(new Color(-1825707984, true)), v -> this.players.getValue());
    private final Setting<Boolean> playerTexture = new Setting<Boolean>("PlayerTexture", true, v -> this.players.getValue());
    private final Setting<Boolean> simple = new Setting<Boolean>("Simple", false, v -> this.players.getValue());
    private final Setting<Boolean> alternativeBlending = new Setting<Boolean>("AlternativeBlending", true);
    private final Identifier crystalTexture = Identifier.of((String)"textures/entity/end_crystal/end_crystal.png");
    private static final float SINE_45_DEGREES = (float)Math.sin(0.7853981633974483);

    public Chams() {
        super("Chams", Module.Category.RENDER);
    }

    public void renderCrystal(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, int i, ModelPart core, ModelPart frame) {
        BufferBuilder buffer;
        RenderSystem.enableBlend();
        if (this.alternativeBlending.getValue().booleanValue()) {
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        } else {
            RenderSystem.defaultBlendFunc();
        }
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        if (this.crystalMode.getValue() != CMode.One) {
            if (this.crystalMode.getValue() == CMode.Three) {
                RenderSystem.setShaderTexture((int)0, (Identifier)this.crystalTexture);
            } else {
                RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.crystalTexture2);
            }
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionProgram);
            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        }
        matrixStack.push();
        float h = this.staticCrystal.getValue() != false ? -1.4f : EndCrystalEntityRenderer.getYOffset((EndCrystalEntity)endCrystalEntity, (float)g);
        float j = ((float)endCrystalEntity.endCrystalAge + g) * 3.0f;
        matrixStack.push();
        RenderSystem.setShaderColor((float)this.crystalColor.getValue().getGlRed(), (float)this.crystalColor.getValue().getGlGreen(), (float)this.crystalColor.getValue().getGlBlue(), (float)this.crystalColor.getValue().getGlAlpha());
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        matrixStack.translate(0.0f, -0.5f, 0.0f);
        int k = OverlayTexture.DEFAULT_UV;
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        matrixStack.translate(0.0f, 1.5f + h / 2.0f, 0.0f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        frame.render(matrixStack, (VertexConsumer)buffer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        frame.render(matrixStack, (VertexConsumer)buffer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        core.render(matrixStack, (VertexConsumer)buffer, i, k);
        matrixStack.pop();
        matrixStack.pop();
        Render2DEngine.endBuilding(buffer);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
    }

    public void renderPlayer(PlayerEntity pe, float f, float g, MatrixStack matrixStack, int i, EntityModel model, CallbackInfo ci, Runnable post) {
        float n;
        Direction direction;
        float l;
        Entity entity;
        BufferBuilder buffer;
        RenderSystem.enableBlend();
        if (this.alternativeBlending.getValue().booleanValue()) {
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        } else {
            RenderSystem.defaultBlendFunc();
        }
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
        if (!this.simple.getValue().booleanValue()) {
            RenderSystem.setShaderTexture((int)0, (Identifier)((AbstractClientPlayerEntity)pe).getSkinTextures().texture());
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionProgram);
            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        }
        matrixStack.push();
        if (Managers.FRIEND.isFriend(pe)) {
            RenderSystem.setShaderColor((float)this.friendColor.getValue().getGlRed(), (float)this.friendColor.getValue().getGlGreen(), (float)this.friendColor.getValue().getGlBlue(), (float)this.friendColor.getValue().getGlAlpha());
        } else {
            RenderSystem.setShaderColor((float)this.playerColor.getValue().getGlRed(), (float)this.playerColor.getValue().getGlGreen(), (float)this.playerColor.getValue().getGlBlue(), (float)this.playerColor.getValue().getGlAlpha());
        }
        model.handSwingProgress = pe.getHandSwingProgress(g);
        model.riding = pe.hasVehicle();
        model.child = false;
        float h = MathHelper.lerpAngleDegrees((float)g, (float)pe.prevBodyYaw, (float)pe.bodyYaw);
        float j = MathHelper.lerpAngleDegrees((float)g, (float)pe.prevHeadYaw, (float)pe.headYaw);
        float k = j - h;
        if (pe.hasVehicle() && (entity = pe.getVehicle()) instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)entity;
            h = MathHelper.lerpAngleDegrees((float)g, (float)livingEntity2.prevBodyYaw, (float)livingEntity2.bodyYaw);
            k = j - h;
            l = MathHelper.wrapDegrees((float)k);
            if (l < -85.0f) {
                l = -85.0f;
            }
            if (l >= 85.0f) {
                l = 85.0f;
            }
            h = j - l;
            if (l * l > 2500.0f) {
                h += l * 0.2f;
            }
            k = j - h;
        }
        float m = MathHelper.lerp((float)g, (float)pe.prevPitch, (float)pe.getPitch());
        if (LivingEntityRenderer.shouldFlipUpsideDown((LivingEntity)pe)) {
            m *= -1.0f;
            k *= -1.0f;
        }
        if (pe.isInPose(EntityPose.SLEEPING) && (direction = pe.getSleepingDirection()) != null) {
            n = pe.getEyeHeight(EntityPose.STANDING) - 0.1f;
            matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0f, (float)(-direction.getOffsetZ()) * n);
        }
        l = (float)pe.age + g;
        this.setupTransforms1(pe, matrixStack, l, h, g);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
        n = 0.0f;
        float o = 0.0f;
        if (!pe.hasVehicle() && pe.isAlive()) {
            n = pe.limbAnimator.getSpeed(g);
            o = pe.limbAnimator.getPos(g);
            if (pe.isBaby()) {
                o *= 3.0f;
            }
            if (n > 1.0f) {
                n = 1.0f;
            }
        }
        model.animateModel((Entity)pe, o, n, g);
        model.setAngles((Entity)pe, o, n, l, k, m);
        int p = LivingEntityRenderer.getOverlay((LivingEntity)pe, (float)0.0f);
        model.render(matrixStack, (VertexConsumer)buffer, i, p);
        Render2DEngine.endBuilding(buffer);
        RenderSystem.disableBlend();
        RenderSystem.disableCull();
        matrixStack.pop();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.enableDepthTest();
        if (!this.playerTexture.getValue().booleanValue()) {
            ci.cancel();
            post.run();
        }
    }

    public void setupTransforms1(PlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h) {
        float j = abstractClientPlayerEntity.getLeaningPitch(h);
        float k = abstractClientPlayerEntity.getPitch(h);
        if (abstractClientPlayerEntity.isFallFlying()) {
            this.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            float l = (float)abstractClientPlayerEntity.getFallFlyingTicks() + h;
            float m = MathHelper.clamp((float)(l * l / 100.0f), (float)0.0f, (float)1.0f);
            if (!abstractClientPlayerEntity.isUsingRiptide()) {
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(m * (-90.0f - k)));
            }
            Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
            Vec3d vec3d2 = abstractClientPlayerEntity.getVelocity();
            double d = vec3d2.horizontalLengthSquared();
            double e = vec3d.horizontalLengthSquared();
            if (d > 0.0 && e > 0.0) {
                double n = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                double o = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation((float)(Math.signum(o) * Math.acos(n))));
            }
        } else if (j > 0.0f) {
            this.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            float l = abstractClientPlayerEntity.isTouchingWater() ? -90.0f - k : -90.0f;
            float m = MathHelper.lerp((float)j, (float)0.0f, (float)l);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(m));
            if (abstractClientPlayerEntity.isInSwimmingPose()) {
                matrixStack.translate(0.0f, -1.0f, 0.3f);
            }
        } else {
            this.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
        }
    }

    private void setupTransforms(PlayerEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        if (!entity.isInPose(EntityPose.SLEEPING)) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - bodyYaw));
        }
        if (entity.deathTime > 0) {
            float f = ((float)entity.deathTime + tickDelta - 1.0f) / 20.0f * 1.6f;
            if ((f = MathHelper.sqrt((float)f)) > 1.0f) {
                f = 1.0f;
            }
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 90.0f));
        } else if (entity.isUsingRiptide()) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - entity.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((float)entity.age + tickDelta) * -75.0f));
        } else if (entity.isInPose(EntityPose.SLEEPING)) {
            Direction direction = entity.getSleepingDirection();
            float g = direction != null ? Chams.getYaw(direction) : bodyYaw;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
        }
    }

    private static float getYaw(Direction direction) {
        return switch (direction) {
            case Direction.NORTH -> 270.0f;
            case Direction.SOUTH -> 90.0f;
            case Direction.EAST -> 180.0f;
            default -> 0.0f;
        };
    }

    @EventHandler
    public void onRenderHands(EventHeldItemRenderer e) {
        if (this.handItems.getValue().booleanValue()) {
            RenderSystem.setShaderColor((float)((float)this.handItemsColor.getValue().getRed() / 255.0f), (float)((float)this.handItemsColor.getValue().getGreen() / 255.0f), (float)((float)this.handItemsColor.getValue().getBlue() / 255.0f), (float)((float)this.handItemsColor.getValue().getAlpha() / 255.0f));
        }
    }

    private static enum CMode {
        One,
        Two,
        Three;

    }
}

