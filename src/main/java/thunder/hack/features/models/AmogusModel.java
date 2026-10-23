/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.model.ModelPart
 */
package thunder.hack.features.models;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.model.ModelPart;
import thunder.hack.core.manager.client.ModuleManager;

public class AmogusModel {
    private final ModelPart body;
    private final ModelPart eye;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private float limbSwing;
    private float limbSwingAmount;

    public AmogusModel() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        root.addChild("body", ModelPartBuilder.create().uv(34, 8).cuboid(-4.0f, 6.0f, -3.0f, 8.0f, 12.0f, 6.0f).uv(15, 10).cuboid(-3.0f, 9.0f, 3.0f, 6.0f, 8.0f, 3.0f).uv(26, 0).cuboid(-3.0f, 5.0f, -3.0f, 6.0f, 1.0f, 6.0f), ModelTransform.pivot((float)0.0f, (float)0.0f, (float)0.0f));
        root.addChild("eye", ModelPartBuilder.create().uv(0, 10).cuboid(-3.0f, 7.0f, -4.0f, 6.0f, 4.0f, 1.0f), ModelTransform.pivot((float)0.0f, (float)0.0f, (float)0.0f));
        root.addChild("leftLeg", ModelPartBuilder.create().uv(0, 0).cuboid(2.9f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f), ModelTransform.pivot((float)-2.0f, (float)18.0f, (float)0.0f));
        root.addChild("rightLeg", ModelPartBuilder.create().uv(13, 0).cuboid(-5.9f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f), ModelTransform.pivot((float)2.0f, (float)18.0f, (float)0.0f));
        ModelPart modelRoot = TexturedModelData.of((ModelData)modelData, (int)64, (int)64).createModel();
        this.body = modelRoot.getChild("body");
        this.eye = modelRoot.getChild("eye");
        this.leftLeg = modelRoot.getChild("leftLeg");
        this.rightLeg = modelRoot.getChild("rightLeg");
    }

    public void setAngles(PlayerEntity player, BipedEntityModel<PlayerEntity> baseModel, float tickDelta) {
        float legSwing;
        boolean reversed = ModuleManager.customModel.changeZ.getValue();
        this.limbSwing = player.limbAnimator.getPos(tickDelta);
        this.limbSwingAmount = player.limbAnimator.getSpeed(tickDelta);
        this.leftLeg.pitch = legSwing = (float)Math.cos(this.limbSwing * 0.6662f) * 1.4f * this.limbSwingAmount;
        this.rightLeg.pitch = -legSwing;
        float headYaw = reversed ? -baseModel.head.yaw : baseModel.head.yaw;
        float headPitch = baseModel.head.pitch;
        this.body.yaw = headYaw * 0.5f;
        this.eye.yaw = headYaw * 0.5f;
        this.body.pitch = headPitch * 0.5f;
        this.eye.pitch = headPitch * 0.5f;
    }

    public void renderBody(MatrixStack matrices, VertexConsumer vertices, int light) {
        this.body.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
    }

    public void renderEye(MatrixStack matrices, VertexConsumer vertices, int light) {
        this.eye.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
    }

    public void renderLegs(MatrixStack matrices, VertexConsumer vertices, int light) {
        this.leftLeg.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
        this.rightLeg.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
    }
}

