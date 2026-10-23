/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.Identifier
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
import net.minecraft.util.Identifier;
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

public class YakupTVModel {
    private final ModelPart head;
    public static final Identifier YAKUP_TV_TEXTURE = Identifier.of((String)"thunderhack", (String)"textures/yakuptv.png");

    public YakupTVModel() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        root.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0f, -10.0f, -5.0f, 10.0f, 10.0f, 10.0f), ModelTransform.pivot((float)0.0f, (float)0.0f, (float)0.0f));
        ModelPart modelRoot = TexturedModelData.of((ModelData)modelData, (int)64, (int)64).createModel();
        this.head = modelRoot.getChild("head");
    }

    public void setAngles(PlayerEntity player, BipedEntityModel<PlayerEntity> baseModel, float tickDelta) {
        boolean reversed = ModuleManager.customModel.changeZ.getValue();
        float headYaw = reversed ? -baseModel.head.yaw : baseModel.head.yaw;
        this.head.pitch = baseModel.head.pitch;
        this.head.yaw = headYaw;
        this.head.roll = baseModel.head.roll;
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light) {
        this.head.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
    }
}

