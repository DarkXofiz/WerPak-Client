/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity.RemovalReason
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.EntityRendererFactory.Context
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Vector3f
 */
package thunder.hack.features.modules.render;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.concurrent.CopyOnWriteArrayList;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import thunder.hack.events.impl.TotemPopEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.IEntity;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public final class TotemPop
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Textured);
    private final Setting<Boolean> secondLayer = new Setting<Boolean>("SecondLayer", true);
    private final Setting<ColorSetting> color = new Setting<ColorSetting>("Color", new ColorSetting(-2013200640));
    private final Setting<Integer> ySpeed = new Setting<Integer>("YSpeed", 3, -10, 10);
    private final Setting<Integer> aSpeed = new Setting<Integer>("AlphaSpeed", 5, 1, 100);
    private final Setting<Float> rotSpeed = new Setting<Float>("RotationSpeed", Float.valueOf(0.25f), Float.valueOf(0.0f), Float.valueOf(6.0f));
    private final CopyOnWriteArrayList<Person> popList = new CopyOnWriteArrayList();

    public TotemPop() {
        super("TotemPop", Module.Category.RENDER);
    }

    @Override
    public void onUpdate() {
        this.popList.forEach(person -> person.update(this.popList));
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        if (this.mode.is(Mode.Simple)) {
            RenderSystem.defaultBlendFunc();
        } else {
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        }
        this.popList.forEach(person -> this.renderEntity(stack, (LivingEntity)person.player, person.modelPlayer, person.getTexture(), person.getAlpha()));
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    @EventHandler
    private void onTotemPop(@NotNull TotemPopEvent e) {
        if (e.getEntity().equals((Object)TotemPop.mc.player) || TotemPop.mc.world == null) {
            return;
        }
        PlayerEntity entity = new PlayerEntity((World)TotemPop.mc.world, BlockPos.ORIGIN, e.getEntity().bodyYaw, new GameProfile(e.getEntity().getUuid(), e.getEntity().getName().getString())){

            public boolean isSpectator() {
                return false;
            }

            public boolean isCreative() {
                return false;
            }
        };
        entity.copyPositionAndRotation((Entity)e.getEntity());
        entity.bodyYaw = e.getEntity().bodyYaw;
        entity.headYaw = e.getEntity().headYaw;
        entity.handSwingProgress = e.getEntity().handSwingProgress;
        entity.handSwingTicks = e.getEntity().handSwingTicks;
        entity.setSneaking(e.getEntity().isSneaking());
        entity.limbAnimator.setSpeed(e.getEntity().limbAnimator.getSpeed());
        entity.limbAnimator.pos = e.getEntity().limbAnimator.getPos();
        this.popList.add(new Person(entity, ((AbstractClientPlayerEntity)e.getEntity()).getSkinTextures().texture()));
    }

    private void renderEntity(@NotNull MatrixStack matrices, @NotNull LivingEntity entity, @NotNull PlayerEntityModel<PlayerEntity> modelBase, Identifier texture, int alpha) {
        BufferBuilder buffer;
        modelBase.leftPants.visible = this.secondLayer.getValue();
        modelBase.rightPants.visible = this.secondLayer.getValue();
        modelBase.leftSleeve.visible = this.secondLayer.getValue();
        modelBase.rightSleeve.visible = this.secondLayer.getValue();
        modelBase.jacket.visible = this.secondLayer.getValue();
        modelBase.hat.visible = this.secondLayer.getValue();
        double x = entity.getX() - TotemPop.mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = entity.getY() - TotemPop.mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = entity.getZ() - TotemPop.mc.getEntityRenderDispatcher().camera.getPos().getZ();
        matrices.push();
        matrices.translate((float)x, (float)y, (float)z);
        float yRotYaw = (float)alpha / 255.0f * 360.0f * this.rotSpeed.getValue().floatValue();
        yRotYaw = yRotYaw == 0.0f ? 0.0f : Render2DEngine.interpolateFloat(yRotYaw, yRotYaw - (float)this.aSpeed.getValue().intValue() / 255.0f * 360.0f * this.rotSpeed.getValue().floatValue(), Render3DEngine.getTickDelta());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(MathUtility.rad(180.0f - entity.bodyYaw + yRotYaw)));
        TotemPop.prepareScale(matrices);
        modelBase.animateModel((PlayerEntity)entity, entity.limbAnimator.getPos(), entity.limbAnimator.getSpeed(), Render3DEngine.getTickDelta());
        float limbSpeed = Math.min(entity.limbAnimator.getSpeed(), 1.0f);
        modelBase.setAngles((PlayerEntity)entity, entity.limbAnimator.getPos(), limbSpeed, (float)entity.age, entity.headYaw - entity.bodyYaw, entity.getPitch());
        if (this.mode.is(Mode.Textured)) {
            RenderSystem.setShaderTexture((int)0, (Identifier)texture);
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionProgram);
            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        }
        RenderSystem.setShaderColor((float)this.color.getValue().getGlRed(), (float)this.color.getValue().getGlGreen(), (float)this.color.getValue().getGlBlue(), (float)((float)alpha / 255.0f));
        modelBase.render(matrices, (VertexConsumer)buffer, 10, 0);
        Render2DEngine.endBuilding(buffer);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        matrices.pop();
    }

    private static void prepareScale(@NotNull MatrixStack matrixStack) {
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.scale(1.6f, 1.8f, 1.6f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
    }

    private static enum Mode {
        Simple,
        Textured;

    }

    private class Person {
        private final PlayerEntity player;
        private final PlayerEntityModel<PlayerEntity> modelPlayer;
        private Identifier texture;
        private int alpha;

        public Person(PlayerEntity player, Identifier texture) {
            this.player = player;
            this.modelPlayer = new PlayerEntityModel(new EntityRendererFactory.Context(Module.mc.getEntityRenderDispatcher(), Module.mc.getItemRenderer(), Module.mc.getBlockRenderManager(), Module.mc.getEntityRenderDispatcher().getHeldItemRenderer(), Module.mc.getResourceManager(), Module.mc.getEntityModelLoader(), Module.mc.textRenderer).getPart(EntityModelLayers.PLAYER), false);
            this.modelPlayer.getHead().scale(new Vector3f(-0.3f, -0.3f, -0.3f));
            this.alpha = TotemPop.this.color.getValue().getAlpha();
            this.texture = texture;
        }

        public void update(CopyOnWriteArrayList<Person> arrayList) {
            if (this.alpha <= 0) {
                arrayList.remove(this);
                this.player.kill();
                this.player.remove(Entity.RemovalReason.KILLED);
                this.player.onRemoved();
                return;
            }
            this.alpha -= TotemPop.this.aSpeed.getValue().intValue();
            ((IEntity)this.player).setPos(this.player.getPos().add(0.0, (double)TotemPop.this.ySpeed.getValue().intValue() / 20.0, 0.0));
        }

        public int getAlpha() {
            return MathUtility.clamp(this.alpha, 0, 255);
        }

        public Identifier getTexture() {
            return this.texture;
        }
    }
}

