/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.entity.EntityPose
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArgs
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.invoke.arg.Args
 */
package thunder.hack.injection;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.entity.EntityPose;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.injection.accesors.IClientPlayerEntity;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

@Mixin(value={LivingEntityRenderer.class})
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    private LivingEntity lastEntity;
    private float originalHeadYaw;
    private float originalPrevHeadYaw;
    private float originalPrevHeadPitch;
    private float originalHeadPitch;
    @Shadow
    protected M model;
    @Shadow
    @Final
    protected List<FeatureRenderer<T, M>> features;

    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true)
    public void onRenderPre(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        Object direction;
        PlayerEntity pe;
        if (Module.fullNullCheck()) {
            return;
        }
        if (Module.mc.player != null && livingEntity == Module.mc.player && Module.mc.player.getControllingVehicle() == null && ClientSettings.renderRotations.getValue().booleanValue() && !ThunderHack.isFuturePresent()) {
            this.originalHeadYaw = ((LivingEntity)livingEntity).headYaw;
            this.originalPrevHeadYaw = ((LivingEntity)livingEntity).prevHeadYaw;
            this.originalPrevHeadPitch = ((LivingEntity)livingEntity).prevPitch;
            this.originalHeadPitch = livingEntity.getPitch();
            livingEntity.setPitch(((IClientPlayerEntity)MinecraftClient.getInstance().player).getLastPitch());
            ((LivingEntity)livingEntity).prevPitch = Managers.PLAYER.lastPitch;
            ((LivingEntity)livingEntity).headYaw = ((IClientPlayerEntity)MinecraftClient.getInstance().player).getLastYaw();
            ((LivingEntity)livingEntity).bodyYaw = Render2DEngine.interpolateFloat(Managers.PLAYER.prevBodyYaw, Managers.PLAYER.bodyYaw, Render3DEngine.getTickDelta());
            ((LivingEntity)livingEntity).prevHeadYaw = Managers.PLAYER.lastYaw;
            ((LivingEntity)livingEntity).prevBodyYaw = Render2DEngine.interpolateFloat(Managers.PLAYER.prevBodyYaw, Managers.PLAYER.bodyYaw, Render3DEngine.getTickDelta());
        }
        if (livingEntity != Module.mc.player && ModuleManager.freeCam.isEnabled() && ModuleManager.freeCam.track.getValue().booleanValue() && ModuleManager.freeCam.trackEntity != null && ModuleManager.freeCam.trackEntity == livingEntity) {
            ci.cancel();
            return;
        }
        this.lastEntity = livingEntity;
        if (livingEntity instanceof PlayerEntity) {
            pe = (PlayerEntity)livingEntity;
            if (ModuleManager.chams.isEnabled() && ModuleManager.chams.players.getValue().booleanValue()) {
                ModuleManager.chams.renderPlayer(pe, f, g, matrixStack, i, (EntityModel)this.model, ci, () -> this.postRender(livingEntity));
                if (!pe.isSpectator()) {
                    float n;
                    float l;
                    Entity entity;
                    matrixStack.push();
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
                    ModuleManager.chams.setupTransforms1(pe, matrixStack, l, h, g);
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
                    for (FeatureRenderer<T, M> featureRenderer : this.features) {
                        featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntity, o, n, g, l, k, m);
                    }
                    matrixStack.pop();
                }
            }
        }
        if (livingEntity instanceof PlayerEntity && (pe = (PlayerEntity)livingEntity) == Module.mc.player && ModuleManager.customModel.isEnabled() && (direction = this.model) instanceof BipedEntityModel) {
            Entity k;
            BipedEntityModel bipedModel = (BipedEntityModel)direction;
            matrixStack.push();
            float h = MathHelper.lerpAngleDegrees((float)g, (float)pe.prevBodyYaw, (float)pe.bodyYaw);
            float j = MathHelper.lerpAngleDegrees((float)g, (float)pe.prevHeadYaw, (float)pe.headYaw);
            float k2 = j - h;
            if (pe.hasVehicle() && (k = pe.getVehicle()) instanceof LivingEntity) {
                LivingEntity livingEntity2 = (LivingEntity)k;
                h = MathHelper.lerpAngleDegrees((float)g, (float)livingEntity2.prevBodyYaw, (float)livingEntity2.bodyYaw);
                k2 = j - h;
                float l = MathHelper.wrapDegrees((float)k2);
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
                k2 = j - h;
            }
            float l = (float)pe.age + g;
            ModuleManager.chams.setupTransforms1(pe, matrixStack, l, h, g);
            matrixStack.scale(-1.0f, -1.0f, 1.0f);
            matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
            matrixStack.translate(0.0f, -1.501f, 0.0f);
            float limbPos = pe.limbAnimator.getPos(g);
            float limbSpeed = pe.limbAnimator.getSpeed(g);
            if (pe.isBaby()) {
                limbPos *= 3.0f;
            }
            if (limbSpeed > 1.0f) {
                limbSpeed = 1.0f;
            }
            float m = MathHelper.lerp((float)g, (float)pe.prevPitch, (float)pe.getPitch());
            if (LivingEntityRenderer.shouldFlipUpsideDown((LivingEntity)pe)) {
                m *= -1.0f;
                k2 *= -1.0f;
            }
            bipedModel.method_17087((LivingEntity)pe, limbPos, limbSpeed, l, k2, m);
            ModuleManager.customModel.customModelRenderer.render(pe, f, g, matrixStack, vertexConsumerProvider, i, ModuleManager.customModel.mode.getValue(), (BipedEntityModel<PlayerEntity>)bipedModel);
            for (FeatureRenderer<T, M> featureRenderer : this.features) {
                featureRenderer.render(matrixStack, vertexConsumerProvider, i, (Entity)pe, limbPos, limbSpeed, g, l, k2, m);
            }
            matrixStack.pop();
            ci.cancel();
            this.postRender(livingEntity);
            return;
        }
    }

    @Unique
    public void postRender(T livingEntity) {
        if (Module.fullNullCheck()) {
            return;
        }
        if (Module.mc.player != null && livingEntity == Module.mc.player && Module.mc.player.getControllingVehicle() == null && ClientSettings.renderRotations.getValue().booleanValue() && !ThunderHack.isFuturePresent()) {
            ((LivingEntity)livingEntity).prevPitch = this.originalPrevHeadPitch;
            livingEntity.setPitch(this.originalHeadPitch);
            ((LivingEntity)livingEntity).headYaw = this.originalHeadYaw;
            ((LivingEntity)livingEntity).prevHeadYaw = this.originalPrevHeadYaw;
        }
    }

    @Inject(method={"render"}, at={@At(value="TAIL")})
    public void onRenderPost(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        this.postRender(livingEntity);
    }

    @ModifyArgs(method={"render"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
    private void renderHook(Args args2) {
        PlayerEntity pl;
        LivingEntity class_13092;
        if (Module.fullNullCheck()) {
            return;
        }
        float alpha = -1.0f;
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.antiPlayerCollision.getValue().booleanValue() && this.lastEntity != Module.mc.player && (class_13092 = this.lastEntity) instanceof PlayerEntity && !(pl = (PlayerEntity)class_13092).isInvisible()) {
            alpha = MathUtility.clamp((float)(Module.mc.player.squaredDistanceTo(this.lastEntity.getPos()) / 3.0) + 0.2f, 0.0f, 1.0f);
        }
        if (alpha != -1.0f) {
            args2.set(4, (Object)Render2DEngine.applyOpacity(0x26FFFFFF, alpha));
        }
    }
}

