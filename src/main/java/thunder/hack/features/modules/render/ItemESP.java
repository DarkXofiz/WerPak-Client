/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 *  org.joml.Matrix4f
 *  org.joml.Vector4d
 */
package thunder.hack.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4d;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public class ItemESP
extends Module {
    private final Setting<Boolean> shadow = new Setting<Boolean>("Shadow", true);
    private final Setting<ColorSetting> scolor = new Setting<ColorSetting>("ShadowColor", new ColorSetting(new Color(0).getRGB()));
    private final Setting<ColorSetting> tcolor = new Setting<ColorSetting>("TextColor", new ColorSetting(new Color(-1).getRGB()));
    private final Setting<ESPMode> espMode = new Setting<ESPMode>("Mode", ESPMode.Rect);
    private final Setting<Float> radius = new Setting<Float>("Radius", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.espMode.getValue() == ESPMode.Circle);
    private final Setting<Boolean> useHudColor = new Setting<Boolean>("UseHudColor", true, v -> this.espMode.getValue() == ESPMode.Circle);
    private final Setting<Integer> cOffset = new Setting<Integer>("ColorOffset", 2, 1, 50, v -> this.espMode.getValue() == ESPMode.Circle && this.useHudColor.getValue() != false);
    private final Setting<ColorSetting> circleColor = new Setting<ColorSetting>("CircleColor", new ColorSetting(new Color(-1).getRGB()), v -> this.espMode.getValue() == ESPMode.Circle && this.useHudColor.getValue() == false);
    private final Setting<Integer> cPoints = new Setting<Integer>("CirclePoints", 12, 3, 32, v -> this.espMode.getValue() == ESPMode.Circle);

    public ItemESP() {
        super("ItemESP", Module.Category.RENDER);
    }

    @Override
    public void onRender2D(DrawContext context) {
        for (Object ent : ItemESP.mc.world.getEntities()) {
            if (!(ent instanceof ItemEntity)) continue;
            Vec3d[] vectors = ItemESP.getPoints((Entity)ent);
            Vector4d position = null;
            for (Vec3d vector : vectors) {
                vector = Render3DEngine.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
                if (!(vector.z > 0.0) || !(vector.z < 1.0)) continue;
                if (position == null) {
                    position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                }
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
                position.w = Math.max(vector.y, position.w);
            }
            if (position == null) continue;
            float posX = (float)position.x;
            float posY = (float)position.y;
            float endPosX = (float)position.z;
            float diff = (endPosX - posX) / 2.0f;
            float textWidth = FontRenderers.sf_bold_mini.getStringWidth(ent.getDisplayName().getString()) * 1.0f;
            float tagX = (posX + diff - textWidth / 2.0f) * 1.0f;
            if (this.shadow.getValue().booleanValue()) {
                Render2DEngine.drawBlurredShadow(context.getMatrices(), tagX - 2.0f, posY - 13.0f, FontRenderers.sf_bold_mini.getStringWidth(ent.getDisplayName().getString()) + 4.0f, 10.0f, 14, this.scolor.getValue().getColorObject());
            }
            FontRenderers.sf_bold_mini.drawString(context.getMatrices(), ent.getDisplayName().getString(), (double)tagX, (double)(posY - 10.0f), this.tcolor.getValue().getColor());
        }
        if (this.espMode.getValue() == ESPMode.Rect) {
            boolean any = false;
            for (Entity ent : ItemESP.mc.world.getEntities()) {
                if (!(ent instanceof ItemEntity)) continue;
                Vec3d[] vectors = ItemESP.getPoints(ent);
                Vector4d position = null;
                for (Vec3d vector : vectors) {
                    vector = Render3DEngine.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
                    if (!(vector.z > 0.0) || !(vector.z < 1.0)) continue;
                    if (position == null) {
                        position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                    }
                    position.x = Math.min(vector.x, position.x);
                    position.y = Math.min(vector.y, position.y);
                    position.z = Math.max(vector.x, position.z);
                    position.w = Math.max(vector.y, position.w);
                }
                if (position == null) continue;
                any = true;
            }
            if (!any) {
                return;
            }
            Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
            Render2DEngine.setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            for (Entity ent : ItemESP.mc.world.getEntities()) {
                if (!(ent instanceof ItemEntity)) continue;
                Vec3d[] vectors = ItemESP.getPoints(ent);
                Vector4d position = null;
                for (Vec3d vector : vectors) {
                    vector = Render3DEngine.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
                    if (!(vector.z > 0.0) || !(vector.z < 1.0)) continue;
                    if (position == null) {
                        position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                    }
                    position.x = Math.min(vector.x, position.x);
                    position.y = Math.min(vector.y, position.y);
                    position.z = Math.max(vector.x, position.z);
                    position.w = Math.max(vector.y, position.w);
                }
                if (position == null) continue;
                float posX = (float)position.x;
                float posY = (float)position.y;
                float endPosX = (float)position.z;
                float endPosY = (float)position.w;
                this.drawRect(bufferBuilder, matrix, posX, posY, endPosX, endPosY);
            }
            Render2DEngine.endBuilding(bufferBuilder);
            Render2DEngine.endRender();
        }
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        if (this.espMode.getValue() == ESPMode.Circle) {
            for (Entity ent : ItemESP.mc.world.getEntities()) {
                if (!(ent instanceof ItemEntity)) continue;
                Render3DEngine.drawCircle3D(stack, ent, this.radius.getValue().floatValue(), this.circleColor.getValue().getColor(), this.cPoints.getValue(), this.useHudColor.getValue(), this.cOffset.getValue());
            }
        }
    }

    private void drawRect(BufferBuilder bufferBuilder, Matrix4f stack, float posX, float posY, float endPosX, float endPosY) {
        Color black = Color.BLACK;
        Render2DEngine.setRectPoints(bufferBuilder, stack, posX - 1.0f, posY, posX + 0.5f, endPosY + 0.5f, black, black, black, black);
        Render2DEngine.setRectPoints(bufferBuilder, stack, posX - 1.0f, posY - 0.5f, endPosX + 0.5f, posY + 1.0f, black, black, black, black);
        Render2DEngine.setRectPoints(bufferBuilder, stack, endPosX - 1.0f, posY, endPosX + 0.5f, endPosY + 0.5f, black, black, black, black);
        Render2DEngine.setRectPoints(bufferBuilder, stack, posX - 1.0f, endPosY - 1.0f, endPosX + 0.5f, endPosY + 0.5f, black, black, black, black);
        Render2DEngine.setRectPoints(bufferBuilder, stack, posX - 0.5f, posY, posX, endPosY, HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(0), HudEditor.getColor(270));
        Render2DEngine.setRectPoints(bufferBuilder, stack, posX, endPosY - 0.5f, endPosX, endPosY, HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(180), HudEditor.getColor(0));
        Render2DEngine.setRectPoints(bufferBuilder, stack, posX - 0.5f, posY, endPosX, posY + 0.5f, HudEditor.getColor(180), HudEditor.getColor(90), HudEditor.getColor(90), HudEditor.getColor(180));
        Render2DEngine.setRectPoints(bufferBuilder, stack, endPosX - 0.5f, posY, endPosX, endPosY, HudEditor.getColor(90), HudEditor.getColor(270), HudEditor.getColor(270), HudEditor.getColor(90));
    }

    @NotNull
    private static Vec3d[] getPoints(Entity ent) {
        Box axisAlignedBB = ItemESP.getBox(ent);
        Vec3d[] vectors = new Vec3d[]{new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)};
        return vectors;
    }

    @NotNull
    private static Box getBox(Entity ent) {
        double x = ent.prevX + (ent.getX() - ent.prevX) * (double)Render3DEngine.getTickDelta();
        double y = ent.prevY + (ent.getY() - ent.prevY) * (double)Render3DEngine.getTickDelta();
        double z = ent.prevZ + (ent.getZ() - ent.prevZ) * (double)Render3DEngine.getTickDelta();
        Box axisAlignedBB2 = ent.getBoundingBox();
        Box axisAlignedBB = new Box(axisAlignedBB2.minX - ent.getX() + x - 0.05, axisAlignedBB2.minY - ent.getY() + y, axisAlignedBB2.minZ - ent.getZ() + z - 0.05, axisAlignedBB2.maxX - ent.getX() + x + 0.05, axisAlignedBB2.maxY - ent.getY() + y + 0.15, axisAlignedBB2.maxZ - ent.getZ() + z + 0.05);
        return axisAlignedBB;
    }

    private static enum ESPMode {
        Rect,
        Circle,
        None;

    }
}

