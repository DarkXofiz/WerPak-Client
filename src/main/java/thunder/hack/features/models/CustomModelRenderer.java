/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 */
package thunder.hack.features.models;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.models.AmogusModel;
import thunder.hack.features.models.RabbitModel;
import thunder.hack.features.models.YakupTVModel;
import thunder.hack.features.modules.movement.CustomModel;

public class CustomModelRenderer {
    private final AmogusModel amogusModel = new AmogusModel();
    private final RabbitModel rabbitModel = new RabbitModel();
    private final YakupTVModel yakupTVModel = new YakupTVModel();

    public void render(PlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CustomModel.Mode mode, BipedEntityModel<PlayerEntity> baseModel) {
        switch (mode) {
            case Amogus: {
                this.renderAmogus(player, yaw, tickDelta, matrices, vertexConsumers, light, baseModel);
                break;
            }
            case Rabbit: {
                this.renderRabbit(player, yaw, tickDelta, matrices, vertexConsumers, light, baseModel);
                break;
            }
            case YakupTV: {
                this.renderYakupTV(player, yaw, tickDelta, matrices, vertexConsumers, light, baseModel);
                break;
            }
            case Tavsan: {
                this.renderRabbit(player, yaw, tickDelta, matrices, vertexConsumers, light, baseModel);
                break;
            }
            case Boykisser: {
                this.renderRabbit(player, yaw, tickDelta, matrices, vertexConsumers, light, baseModel);
                break;
            }
            case Paimon: {
                this.renderYakupTV(player, yaw, tickDelta, matrices, vertexConsumers, light, baseModel);
                break;
            }
            case Cutie: {
                this.renderYakupTV(player, yaw, tickDelta, matrices, vertexConsumers, light, baseModel);
            }
        }
    }

    private void renderAmogus(PlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, BipedEntityModel<PlayerEntity> baseModel) {
        matrices.push();
        if (player.isSneaking()) {
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.translate(0.0f, 1.5f, 0.0f);
        }
        Color bodyColor = ModuleManager.customModel.bodyColor.getValue().getColorObject();
        Color eyeColor = ModuleManager.customModel.eyeColor.getValue().getColorObject();
        Color legsColor = ModuleManager.customModel.legsColor.getValue().getColorObject();
        if (ModuleManager.customModel.friendHighlight.getValue().booleanValue() && Managers.FRIEND.isFriend(player.getName().getString())) {
            bodyColor = Color.GREEN;
            eyeColor = Color.WHITE;
            legsColor = Color.GREEN;
        }
        this.amogusModel.setAngles(player, baseModel, tickDelta);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySmoothCutout((Identifier)Identifier.of((String)"thunderhack", (String)"textures/amogus.png")));
        RenderSystem.setShaderColor((float)((float)bodyColor.getRed() / 255.0f), (float)((float)bodyColor.getGreen() / 255.0f), (float)((float)bodyColor.getBlue() / 255.0f), (float)((float)bodyColor.getAlpha() / 255.0f));
        this.amogusModel.renderBody(matrices, vertexConsumer, light);
        RenderSystem.setShaderColor((float)((float)eyeColor.getRed() / 255.0f), (float)((float)eyeColor.getGreen() / 255.0f), (float)((float)eyeColor.getBlue() / 255.0f), (float)((float)eyeColor.getAlpha() / 255.0f));
        this.amogusModel.renderEye(matrices, vertexConsumer, light);
        RenderSystem.setShaderColor((float)((float)legsColor.getRed() / 255.0f), (float)((float)legsColor.getGreen() / 255.0f), (float)((float)legsColor.getBlue() / 255.0f), (float)((float)legsColor.getAlpha() / 255.0f));
        this.amogusModel.renderLegs(matrices, vertexConsumer, light);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        matrices.pop();
    }

    private void renderRabbit(PlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, BipedEntityModel<PlayerEntity> baseModel) {
        matrices.push();
        if (player.isSneaking()) {
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.translate(0.0f, 1.5f, 0.0f);
        }
        this.rabbitModel.setAngles(player, baseModel, tickDelta);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySmoothCutout((Identifier)Identifier.of((String)"thunderhack", (String)"textures/rabbit.png")));
        this.rabbitModel.render(matrices, vertexConsumer, light);
        matrices.pop();
    }

    private void renderYakupTV(PlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, BipedEntityModel<PlayerEntity> baseModel) {
        matrices.push();
        if (player.isSneaking()) {
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.translate(0.0f, 1.5f, 0.0f);
        }
        this.yakupTVModel.setAngles(player, baseModel, tickDelta);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySmoothCutout((Identifier)YakupTVModel.YAKUP_TV_TEXTURE));
        this.yakupTVModel.render(matrices, vertexConsumer, light);
        matrices.pop();
    }
}

