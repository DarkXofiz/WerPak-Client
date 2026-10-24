/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.option.Perspective
 *  net.minecraft.resource.ResourceFactory
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.RenderTickCounter
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.option.Perspective;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.utility.math.FrameRateCounter;
import thunder.hack.utility.render.BlockAnimationUtility;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.shaders.satin.impl.ReloadableShaderEffectManager;

@Mixin(value={GameRenderer.class})
public abstract class MixinGameRenderer {
    @Shadow
    private float zoom;
    @Shadow
    private float zoomX;
    @Shadow
    private float zoomY;
    @Shadow
    private float viewDistance;

    @Shadow
    public abstract void tick();

    @Inject(at={@At(value="INVOKE", target="Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal=1, shift=At.Shift.BEFORE)}, method={"render"})
    void postHudRenderHook(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        FrameRateCounter.INSTANCE.recordFrame();
    }

    @Inject(at={@At(value="FIELD", target="Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode=180, ordinal=0)}, method={"renderWorld"})
    void render3dHook(RenderTickCounter tickCounter, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        Camera camera = Module.mc.gameRenderer.getCamera();
        MatrixStack matrixStack = new MatrixStack();
        RenderSystem.getModelViewStack().pushMatrix().mul((Matrix4fc)matrixStack.peek().getPositionMatrix());
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        RenderSystem.applyModelViewMatrix();
        Render3DEngine.lastProjMat.set((Matrix4fc)RenderSystem.getProjectionMatrix());
        Render3DEngine.lastModMat.set((Matrix4fc)RenderSystem.getModelViewMatrix());
        Render3DEngine.lastWorldSpaceMatrix.set((Matrix4fc)matrixStack.peek().getPositionMatrix());
        Managers.MODULE.onRender3D(matrixStack);
        BlockAnimationUtility.onRender(matrixStack);
        Render3DEngine.onRender3D(matrixStack);
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.applyModelViewMatrix();
    }

    @Inject(method={"renderWorld"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/render/Camera;FLorg/joml/Matrix4f;)V", shift=At.Shift.AFTER)})
    public void postRender3dHook(RenderTickCounter tickCounter, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        Managers.SHADER.renderShaders();
    }

    @Redirect(method={"renderWorld"}, at=@At(value="INVOKE", target="Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
    private float renderWorldHook(float delta, float first, float second) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.nausea.getValue().booleanValue()) {
            return 0.0f;
        }
        return MathHelper.lerp((float)delta, (float)first, (float)second);
    }

    @Inject(method={"loadPrograms"}, at={@At(value="RETURN")})
    private void loadSatinPrograms(ResourceFactory factory, CallbackInfo ci) {
        ReloadableShaderEffectManager.INSTANCE.reload(factory);
    }

    @Inject(method={"updateCrosshairTarget"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/render/GameRenderer;findCrosshairTarget(Lnet/minecraft/entity/Entity;DDF)Lnet/minecraft/util/hit/HitResult;")}, cancellable=true)
    private void onUpdateTargetedEntity(float tickDelta, CallbackInfo info) {
        if (Module.fullNullCheck()) {
            return;
        }
        if (ModuleManager.freeCam.isEnabled()) {
            Module.mc.getProfiler().pop();
            info.cancel();
            Module.mc.crosshairTarget = Managers.PLAYER.getRtxTarget(ModuleManager.freeCam.getFakeYaw(), ModuleManager.freeCam.getFakePitch(), ModuleManager.freeCam.getFakeX(), ModuleManager.freeCam.getFakeY(), ModuleManager.freeCam.getFakeZ());
        }
    }

    @Inject(method={"findCrosshairTarget"}, at={@At(value="HEAD")}, cancellable=true)
    private void findCrosshairTargetHook(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta, CallbackInfoReturnable<HitResult> cir) {
    }

    @Inject(method={"getBasicProjectionMatrix"}, at={@At(value="TAIL")}, cancellable=true)
    public void getBasicProjectionMatrixHook(double fov, CallbackInfoReturnable<Matrix4f> cir) {
        if (ModuleManager.aspectRatio.isEnabled()) {
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.peek().getPositionMatrix().identity();
            if (this.zoom != 1.0f) {
                matrixStack.translate(this.zoomX, -this.zoomY, 0.0f);
                matrixStack.scale(this.zoom, this.zoom, 1.0f);
            }
            matrixStack.peek().getPositionMatrix().mul((Matrix4fc)new Matrix4f().setPerspective((float)(fov * 0.01745329238474369), ModuleManager.aspectRatio.ratio.getValue().floatValue(), 0.05f, this.viewDistance * 4.0f));
            cir.setReturnValue((Object)matrixStack.peek().getPositionMatrix());
        }
    }

    @Inject(method={"getFov(Lnet/minecraft/client/render/Camera;FZ)D"}, at={@At(value="TAIL")}, cancellable=true)
    public void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cb) {
        if (ModuleManager.fov.isEnabled()) {
            if ((Double)cb.getReturnValue() == 70.0 && !ModuleManager.fov.itemFov.getValue().booleanValue() && Module.mc.options.getPerspective() != Perspective.FIRST_PERSON) {
                return;
            }
            if (ModuleManager.fov.itemFov.getValue().booleanValue() && (Double)cb.getReturnValue() == 70.0) {
                cb.setReturnValue((Object)ModuleManager.fov.itemFovModifier.getValue().doubleValue());
                return;
            }
            if (Module.mc.player.isSubmergedInWater()) {
                return;
            }
            cb.setReturnValue((Object)ModuleManager.fov.fovModifier.getValue().doubleValue());
        }
    }

    @Inject(method={"bobView"}, at={@At(value="HEAD")}, cancellable=true)
    private void bobViewHook(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        if (ClientSettings.customBob.getValue().booleanValue()) {
            ThunderHack.core.bobView(matrices, tickDelta);
            ci.cancel();
        }
    }

    @Unique
    private HitResult ensureTargetInRangeCustom(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        Vec3d vec3d = hitResult.getPos();
        if (!vec3d.isInRange(cameraPos, interactionRange)) {
            Vec3d vec3d2 = hitResult.getPos();
            Direction direction = Direction.getFacing((double)(vec3d2.x - cameraPos.x), (double)(vec3d2.y - cameraPos.y), (double)(vec3d2.z - cameraPos.z));
            return BlockHitResult.createMissed((Vec3d)vec3d2, (Direction)direction, (BlockPos)BlockPos.ofFloored(vec3d2));
        }
        return hitResult;
    }

    @Inject(method={"showFloatingItem"}, at={@At(value="HEAD")}, cancellable=true)
    private void showFloatingItemHook(ItemStack floatingItem, CallbackInfo info) {
        if (ModuleManager.totemAnimation.isEnabled()) {
            ModuleManager.totemAnimation.showFloatingItem(floatingItem);
            info.cancel();
        }
    }

    @Inject(method={"renderFloatingItem"}, at={@At(value="HEAD")}, cancellable=true)
    private void renderFloatingItemHook(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (ModuleManager.totemAnimation.isEnabled()) {
            ModuleManager.totemAnimation.renderFloatingItem(tickDelta);
            ci.cancel();
        }
    }

    @Inject(method={"tiltViewWhenHurt"}, at={@At(value="HEAD")}, cancellable=true)
    private void tiltViewWhenHurtHook(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.hurtCam.getValue().booleanValue()) {
            ci.cancel();
        }
    }
}

