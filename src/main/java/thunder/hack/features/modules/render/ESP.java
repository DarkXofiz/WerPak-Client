/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.AreaEffectCloudEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.SpawnGroup
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.TntEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.thrown.EnderPearlEntity
 *  net.minecraft.item.Items
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Box
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.particle.EntityEffectParticleEffect
 *  org.joml.Matrix4f
 *  org.joml.Vector4d
 */
package thunder.hack.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Items;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Box;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.particle.EntityEffectParticleEffect;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4d;
import thunder.hack.core.Managers;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.injection.accesors.IAreaEffectCloudEntity;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.impl.SettingGroup;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

public class ESP
extends Module {
    private final Setting<Boolean> lingeringPotions = new Setting<Boolean>("LingeringPotions", false);
    private final Setting<Boolean> tntFuse = new Setting<Boolean>("TNTFuse", false);
    private final Setting<Float> tntrange = new Setting<Float>("TNTRange", Float.valueOf(8.0f), Float.valueOf(0.0f), Float.valueOf(8.0f));
    private final Setting<ColorSetting> tntFuseText = new Setting<ColorSetting>("TNTFuseText", new ColorSetting(new Color(-1)), v -> this.tntFuse.getValue());
    private final Setting<Boolean> tntRadius = new Setting<Boolean>("TNTRadius", false);
    private final Setting<ColorSetting> tntRadiusColor = new Setting<ColorSetting>("TNTSphereColor", new ColorSetting(new Color(-1)), v -> this.tntRadius.getValue());
    private final Setting<Boolean> beaconRadius = new Setting<Boolean>("BeaconRadius", false);
    private final Setting<Boolean> keepY = new Setting<Boolean>("KeepY", false, v -> this.beaconRadius.getValue());
    private final Setting<ColorSetting> sphereColor = new Setting<ColorSetting>("SphereColor", new ColorSetting(new Color(-1)), v -> this.beaconRadius.getValue());
    private final Setting<ColorSetting> beakonColor = new Setting<ColorSetting>("BeakonColor", new ColorSetting(new Color(-1)), v -> this.beaconRadius.getValue());
    private final Setting<Boolean> burrow = new Setting<Boolean>("Burrow", false);
    private final Setting<ColorSetting> burrowTextColor = new Setting<ColorSetting>("BurrowTextColor", new ColorSetting(new Color(-1)), v -> this.burrow.getValue());
    private final Setting<ColorSetting> burrowColor = new Setting<ColorSetting>("BurrowColor", new ColorSetting(new Color(-1)), v -> this.burrow.getValue());
    private final Setting<Boolean> pearls = new Setting<Boolean>("Pearls", false);
    private final Setting<Boolean> dizorentRadius = new Setting<Boolean>("DizorentRadius", true);
    private final Setting<ColorSetting> dizorentColor = new Setting<ColorSetting>("DizorentColor", new ColorSetting(new Color(-1291783732, true)), v -> this.dizorentRadius.getValue());
    private final Setting<SettingGroup> boxEsp = new Setting<SettingGroup>("Box", new SettingGroup(false, 0));
    private final Setting<Boolean> players = new Setting<Boolean>("Players", true).addToGroup(this.boxEsp);
    private final Setting<Boolean> friends = new Setting<Boolean>("Friends", true).addToGroup(this.boxEsp);
    private final Setting<Boolean> crystals = new Setting<Boolean>("Crystals", true).addToGroup(this.boxEsp);
    private final Setting<Boolean> creatures = new Setting<Boolean>("Creatures", false).addToGroup(this.boxEsp);
    private final Setting<Boolean> monsters = new Setting<Boolean>("Monsters", false).addToGroup(this.boxEsp);
    private final Setting<Boolean> ambients = new Setting<Boolean>("Ambients", false).addToGroup(this.boxEsp);
    private final Setting<Boolean> others = new Setting<Boolean>("Others", false).addToGroup(this.boxEsp);
    private final Setting<Boolean> outline = new Setting<Boolean>("Outline", true).addToGroup(this.boxEsp);
    private final Setting<Colors> colorMode = new Setting<Colors>("ColorMode", Colors.SyncColor).addToGroup(this.boxEsp);
    private final Setting<Boolean> renderHealth = new Setting<Boolean>("renderHealth", true).addToGroup(this.boxEsp);
    private final Setting<SettingGroup> boxColors = new Setting<SettingGroup>("BoxColors", new SettingGroup(false, 0));
    private final Setting<ColorSetting> playersC = new Setting<ColorSetting>("PlayersC", new ColorSetting(new Color(16749056))).addToGroup(this.boxColors);
    private final Setting<ColorSetting> friendsC = new Setting<ColorSetting>("FriendsC", new ColorSetting(new Color(0x30FF00))).addToGroup(this.boxColors);
    private final Setting<ColorSetting> crystalsC = new Setting<ColorSetting>("CrystalsC", new ColorSetting(new Color(48127))).addToGroup(this.boxColors);
    private final Setting<ColorSetting> creaturesC = new Setting<ColorSetting>("CreaturesC", new ColorSetting(new Color(10527910))).addToGroup(this.boxColors);
    private final Setting<ColorSetting> monstersC = new Setting<ColorSetting>("MonstersC", new ColorSetting(new Color(0xFF0000))).addToGroup(this.boxColors);
    private final Setting<ColorSetting> ambientsC = new Setting<ColorSetting>("AmbientsC", new ColorSetting(new Color(8061183))).addToGroup(this.boxColors);
    private final Setting<ColorSetting> othersC = new Setting<ColorSetting>("OthersC", new ColorSetting(new Color(16711778))).addToGroup(this.boxColors);
    public final Setting<ColorSetting> healthB = new Setting<ColorSetting>("healthB", new ColorSetting(new Color(0xFF1100))).addToGroup(this.boxColors);
    public final Setting<ColorSetting> healthU = new Setting<ColorSetting>("healthU", new ColorSetting(new Color(0x2FFF00))).addToGroup(this.boxColors);
    private float dizorentAnimation = 0.0f;

    public ESP() {
        super("ESP", Module.Category.RENDER);
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        double y;
        if (ESP.mc.options.hudHidden) {
            return;
        }
        if (this.lingeringPotions.getValue().booleanValue()) {
            for (Entity ent : ESP.mc.world.getEntities()) {
                double u;
                double v;
                int i;
                if (!(ent instanceof AreaEffectCloudEntity)) continue;
                AreaEffectCloudEntity aece = (AreaEffectCloudEntity)ent;
                double x = aece.getX() - ESP.mc.getEntityRenderDispatcher().camera.getPos().getX();
                y = aece.getY() - ESP.mc.getEntityRenderDispatcher().camera.getPos().getY();
                double z = aece.getZ() - ESP.mc.getEntityRenderDispatcher().camera.getPos().getZ();
                float middle = aece.getRadius();
                stack.push();
                stack.translate(x, y, z);
                Render3DEngine.setupRender();
                RenderSystem.disableDepthTest();
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                for (i = 0; i <= 360; i += 6) {
                    v = Math.sin(Math.toRadians(i));
                    u = Math.cos(Math.toRadians(i));
                    bufferBuilder.vertex(stack.peek().getPositionMatrix(), (float)u * middle, 0.0f, (float)v * middle).color(Render2DEngine.injectAlpha(new Color(this.getAreaCloudColor(aece)), 100).getRGB());
                    bufferBuilder.vertex(stack.peek().getPositionMatrix(), 0.0f, 0.0f, 0.0f).color(Render2DEngine.injectAlpha(new Color(this.getAreaCloudColor(aece)), 0).getRGB());
                }
                Render2DEngine.endBuilding(bufferBuilder);
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                for (i = 0; i <= 360; i += 6) {
                    v = Math.sin(Math.toRadians(i));
                    u = Math.cos(Math.toRadians(i));
                    bufferBuilder.vertex(stack.peek().getPositionMatrix(), (float)u * middle, 0.0f, (float)v * middle).color(Render2DEngine.injectAlpha(new Color(this.getAreaCloudColor(aece)), 255).getRGB());
                    bufferBuilder.vertex(stack.peek().getPositionMatrix(), (float)u * (middle - 0.04f), 0.0f, (float)v * (middle - 0.04f)).color(Render2DEngine.injectAlpha(new Color(this.getAreaCloudColor(aece)), 255).getRGB());
                }
                Render2DEngine.endBuilding(bufferBuilder);
                Render3DEngine.endRender();
                RenderSystem.enableDepthTest();
                stack.translate(-x, -y, -z);
                stack.pop();
                RenderSystem.disableDepthTest();
                MatrixStack matrices = new MatrixStack();
                Camera camera = ESP.mc.gameRenderer.getCamera();
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
                matrices.translate(x, y, z);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                matrices.translate(0.0f, 0.0f, 0.0f);
                matrices.scale(-0.05f, -0.05f, 0.0f);
                FontRenderers.modules.drawCenteredString(matrices, String.format("%.1f", Float.valueOf(aece.getRadius() * 10.0f - 5.0f)), 0.0, -10.0, -1);
                RenderSystem.disableBlend();
                RenderSystem.enableDepthTest();
            }
        }
        if (this.dizorentRadius.getValue().booleanValue()) {
            this.dizorentAnimation = AnimationUtility.fast(this.dizorentAnimation, ESP.mc.player.getMainHandStack().getItem() == Items.ENDER_EYE ? 10.0f : 0.0f, 15.0f);
            if (ESP.mc.player.getMainHandStack().getItem() == Items.ENDER_EYE) {
                double d;
                int i;
                double x = Render2DEngine.interpolate(ESP.mc.player.prevX, ESP.mc.player.getX(), Render3DEngine.getTickDelta()) - ESP.mc.getEntityRenderDispatcher().camera.getPos().getX();
                double y2 = Render2DEngine.interpolate(ESP.mc.player.prevY, ESP.mc.player.getY(), Render3DEngine.getTickDelta()) - ESP.mc.getEntityRenderDispatcher().camera.getPos().getY();
                double z = Render2DEngine.interpolate(ESP.mc.player.prevZ, ESP.mc.player.getZ(), Render3DEngine.getTickDelta()) - ESP.mc.getEntityRenderDispatcher().camera.getPos().getZ();
                stack.push();
                stack.translate(x, y2, z);
                Render3DEngine.setupRender();
                RenderSystem.disableDepthTest();
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                for (i = 0; i <= 360; i += 6) {
                    d = Math.sin(Math.toRadians(i));
                    double u = Math.cos(Math.toRadians(i));
                    bufferBuilder.vertex(stack.peek().getPositionMatrix(), (float)u * this.dizorentAnimation, 0.0f, (float)d * this.dizorentAnimation).color(Render2DEngine.injectAlpha(new Color(this.dizorentColor.getValue().getColor()), 100).getRGB());
                    bufferBuilder.vertex(stack.peek().getPositionMatrix(), 0.0f, 0.0f, 0.0f).color(Render2DEngine.injectAlpha(new Color(this.dizorentColor.getValue().getColor()), 0).getRGB());
                }
                Render2DEngine.endBuilding(bufferBuilder);
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                for (i = 0; i <= 360; i += 6) {
                    d = Math.sin(Math.toRadians(i));
                    double u = Math.cos(Math.toRadians(i));
                    bufferBuilder.vertex(stack.peek().getPositionMatrix(), (float)u * this.dizorentAnimation, 0.0f, (float)d * this.dizorentAnimation).color(Render2DEngine.injectAlpha(new Color(this.dizorentColor.getValue().getColor()), 255).getRGB());
                    bufferBuilder.vertex(stack.peek().getPositionMatrix(), (float)u * (this.dizorentAnimation - 0.04f), 0.0f, (float)d * (this.dizorentAnimation - 0.04f)).color(Render2DEngine.injectAlpha(new Color(this.dizorentColor.getValue().getColor()), 255).getRGB());
                }
                Render2DEngine.endBuilding(bufferBuilder);
                Render3DEngine.endRender();
                RenderSystem.enableDepthTest();
                stack.translate(-x, -y2, -z);
                stack.pop();
                for (PlayerEntity class_16572 : Managers.ASYNC.getAsyncPlayers()) {
                    if (ESP.mc.player.squaredDistanceTo(class_16572.getPos()) > 100.0 || class_16572 == ESP.mc.player) continue;
                    Render3DEngine.drawTargetEsp(stack, (Entity)class_16572);
                }
            }
        }
        if (this.beaconRadius.getValue().booleanValue()) {
            // empty if block
        }
        if (this.burrow.getValue().booleanValue()) {
            for (PlayerEntity pl : ESP.mc.world.getPlayers()) {
                BlockPos blockPos = BlockPos.ofFloored((Position)pl.getPos().add(0.0, (double)0.15f, 0.0));
                Block block = ESP.mc.world.getBlockState(blockPos).getBlock();
                double x = (double)blockPos.getX() - ESP.mc.getEntityRenderDispatcher().camera.getPos().getX();
                double y3 = (double)blockPos.getY() - ESP.mc.getEntityRenderDispatcher().camera.getPos().getY();
                double d = (double)blockPos.getZ() - ESP.mc.getEntityRenderDispatcher().camera.getPos().getZ();
                if (block != Blocks.OBSIDIAN && block != Blocks.CRYING_OBSIDIAN && block != Blocks.ANVIL && block != Blocks.PLAYER_HEAD && block != Blocks.SKELETON_SKULL && block != Blocks.WITHER_SKELETON_SKULL) continue;
                Render3DEngine.drawBoxOutline(new Box(blockPos), this.burrowColor.getValue().getColorObject(), 2.0f);
                RenderSystem.disableDepthTest();
                MatrixStack matrices = new MatrixStack();
                Camera camera = ESP.mc.gameRenderer.getCamera();
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
                matrices.translate(x + 0.5, y3 + 0.5, d + 0.5);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                matrices.translate(0.0f, 0.0f, 0.0f);
                matrices.scale(-0.025f, -0.025f, 0.0f);
                FontRenderers.modules.drawCenteredString(matrices, "BURROW", 0.0, -5.0, this.burrowTextColor.getValue().getColor());
                RenderSystem.disableBlend();
                RenderSystem.enableDepthTest();
            }
        }
        if (this.tntFuse.getValue().booleanValue() || this.tntRadius.getValue().booleanValue()) {
            for (Entity ent : ESP.mc.world.getEntities()) {
                if (!(ent instanceof TntEntity)) continue;
                TntEntity tnt = (TntEntity)ent;
                double x = tnt.prevX + (tnt.getPos().getX() - tnt.prevX) * (double)Render3DEngine.getTickDelta() - ESP.mc.getEntityRenderDispatcher().camera.getPos().getX();
                y = tnt.prevY + (tnt.getPos().getY() - tnt.prevY) * (double)Render3DEngine.getTickDelta() - ESP.mc.getEntityRenderDispatcher().camera.getPos().getY();
                double z = tnt.prevZ + (tnt.getPos().getZ() - tnt.prevZ) * (double)Render3DEngine.getTickDelta() - ESP.mc.getEntityRenderDispatcher().camera.getPos().getZ();
                if (this.tntFuse.getValue().booleanValue()) {
                    RenderSystem.disableDepthTest();
                    MatrixStack matrices = new MatrixStack();
                    Camera camera = ESP.mc.gameRenderer.getCamera();
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
                    matrices.translate(x, y + 0.5, z);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    matrices.translate(0.0f, 0.0f, 0.0f);
                    matrices.scale(-0.025f, -0.025f, 0.0f);
                    FontRenderers.modules.drawCenteredString(matrices, String.format("%.1f", Float.valueOf((float)tnt.getFuse() / 20.0f)) + "s", 0.0, -5.0, this.tntFuseText.getValue().getColor());
                    RenderSystem.disableBlend();
                    RenderSystem.enableDepthTest();
                }
                if (!this.tntRadius.getValue().booleanValue()) continue;
                stack.push();
                stack.translate(x, y, z);
                Render3DEngine.drawSphere(stack, this.tntrange.getValue().floatValue(), 20, 20, this.tntRadiusColor.getValue().getColor());
                stack.translate(-x, -y, -z);
                stack.pop();
            }
        }
    }

    @Override
    public void onRender2D(DrawContext context) {
        if (ESP.mc.options.hudHidden) {
            return;
        }
        if (this.pearls.getValue().booleanValue()) {
            for (Entity ent : ESP.mc.world.getEntities()) {
                if (!(ent instanceof EnderPearlEntity)) continue;
                EnderPearlEntity pearl = (EnderPearlEntity)ent;
                float xOffset = (float)mc.getWindow().getScaledWidth() / 2.0f;
                float yOffset = (float)mc.getWindow().getScaledHeight() / 2.0f;
                float xPos = (float)(pearl.prevX + (pearl.getPos().getX() - pearl.prevX) * (double)Render3DEngine.getTickDelta());
                float zPos = (float)(pearl.prevZ + (pearl.getPos().getZ() - pearl.prevZ) * (double)Render3DEngine.getTickDelta());
                float yaw = ESP.getRotations(new Vec2f(xPos, zPos)) - ESP.mc.player.getYaw();
                context.getMatrices().translate(xOffset, yOffset, 0.0f);
                context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(yaw));
                context.getMatrices().translate(-xOffset, -yOffset, 0.0f);
                Render2DEngine.drawTracerPointer(context.getMatrices(), xOffset, yOffset - 50.0f, 12.5f, 0.5f, 3.63f, true, true, HudEditor.getColor(1).getRGB());
                context.getMatrices().translate(xOffset, yOffset, 0.0f);
                context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-yaw));
                context.getMatrices().translate(-xOffset, -yOffset, 0.0f);
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                FontRenderers.modules.drawCenteredString(context.getMatrices(), String.format("%.1f", Float.valueOf(ESP.mc.player.distanceTo((Entity)pearl))) + "m", (double)((float)(Math.sin(Math.toRadians(yaw)) * 50.0) + xOffset), (double)((float)((double)yOffset - Math.cos(Math.toRadians(yaw)) * 50.0) - 20.0f), -1);
            }
        }
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        Render2DEngine.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        for (Entity ent : ESP.mc.world.getEntities()) {
            if (!this.shouldRender(ent)) continue;
            this.drawBox(bufferBuilder, ent, matrix);
        }
        Render2DEngine.endBuilding(bufferBuilder);
        Render2DEngine.endRender();
    }

    public boolean shouldRender(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (ESP.mc.player == null) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            if (entity == ESP.mc.player) {
                return false;
            }
            if (Managers.FRIEND.isFriend((PlayerEntity)entity)) {
                return this.friends.getValue();
            }
            return this.players.getValue();
        }
        if (entity instanceof EndCrystalEntity) {
            return this.crystals.getValue();
        }
        return switch (entity.getType().getSpawnGroup()) {
            case SpawnGroup.CREATURE, SpawnGroup.WATER_CREATURE -> this.creatures.getValue();
            case SpawnGroup.MONSTER -> this.monsters.getValue();
            case SpawnGroup.AMBIENT, SpawnGroup.WATER_AMBIENT -> this.ambients.getValue();
            default -> this.others.getValue();
        };
    }

    public Color getEntityColor(Entity entity) {
        if (entity == null) {
            return new Color(-1);
        }
        if (entity instanceof PlayerEntity) {
            if (Managers.FRIEND.isFriend((PlayerEntity)entity)) {
                return this.friendsC.getValue().getColorObject();
            }
            return this.playersC.getValue().getColorObject();
        }
        if (entity instanceof EndCrystalEntity) {
            return this.crystalsC.getValue().getColorObject();
        }
        return switch (entity.getType().getSpawnGroup()) {
            case SpawnGroup.CREATURE, SpawnGroup.WATER_CREATURE -> this.creaturesC.getValue().getColorObject();
            case SpawnGroup.MONSTER -> this.monstersC.getValue().getColorObject();
            case SpawnGroup.AMBIENT, SpawnGroup.WATER_AMBIENT -> this.ambientsC.getValue().getColorObject();
            default -> this.othersC.getValue().getColorObject();
        };
    }

    public void drawBox(BufferBuilder bufferBuilder, @NotNull Entity ent, Matrix4f matrix) {
        Vec3d[] vectors = ESP.getVectors(ent);
        Color col = this.getEntityColor(ent);
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
        if (position != null) {
            LivingEntity lent;
            double posX = position.x;
            double posY = position.y;
            double endPosX = position.z;
            double endPosY = position.w;
            if (this.outline.getValue().booleanValue()) {
                Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(posX - 1.0), (float)posY, (float)(posX + 0.5), (float)(endPosY + 0.5), Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
                Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(posX - 1.0), (float)(posY - 0.5), (float)(endPosX + 0.5), (float)(posY + 0.5 + 0.5), Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
                Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(endPosX - 0.5 - 0.5), (float)posY, (float)(endPosX + 0.5), (float)(endPosY + 0.5), Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
                Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(posX - 1.0), (float)(endPosY - 0.5 - 0.5), (float)(endPosX + 0.5), (float)(endPosY + 0.5), Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
            }
            switch (this.colorMode.getValue().ordinal()) {
                case 1: {
                    Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(posX - 0.5), (float)posY, (float)(posX + 0.5 - 0.5), (float)endPosY, col, col, col, col);
                    Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)posX, (float)(endPosY - 0.5), (float)endPosX, (float)endPosY, col, col, col, col);
                    Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(posX - 0.5), (float)posY, (float)endPosX, (float)(posY + 0.5), col, col, col, col);
                    Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(endPosX - 0.5), (float)posY, (float)endPosX, (float)endPosY, col, col, col, col);
                    break;
                }
                case 0: {
                    Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(posX - 0.5), (float)posY, (float)(posX + 0.5 - 0.5), (float)endPosY, HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(0), HudEditor.getColor(270));
                    Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)posX, (float)(endPosY - 0.5), (float)endPosX, (float)endPosY, HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(180), HudEditor.getColor(0));
                    Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(posX - 0.5), (float)posY, (float)endPosX, (float)(posY + 0.5), HudEditor.getColor(180), HudEditor.getColor(90), HudEditor.getColor(90), HudEditor.getColor(180));
                    Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(endPosX - 0.5), (float)posY, (float)endPosX, (float)endPosY, HudEditor.getColor(90), HudEditor.getColor(270), HudEditor.getColor(270), HudEditor.getColor(90));
                }
            }
            if (ent instanceof LivingEntity && (lent = (LivingEntity)ent).getHealth() != 0.0f && this.renderHealth.getValue().booleanValue()) {
                Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(posX - 5.0), (float)posY, (float)posX - 3.0f, (float)endPosY, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
                switch (this.colorMode.getValue().ordinal()) {
                    case 1: {
                        Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(posX - 5.0), (float)(endPosY + (posY - endPosY) * (double)lent.getHealth() / (double)lent.getMaxHealth()), (float)posX - 3.0f, (float)endPosY, this.healthB.getValue().getColorObject(), this.healthB.getValue().getColorObject(), this.healthU.getValue().getColorObject(), this.healthU.getValue().getColorObject());
                        break;
                    }
                    case 0: {
                        Render2DEngine.setRectPoints(bufferBuilder, matrix, (float)(posX - 5.0), (float)(endPosY + (posY - endPosY) * (double)lent.getHealth() / (double)lent.getMaxHealth()), (float)posX - 3.0f, (float)endPosY, HudEditor.getColor(90), HudEditor.getColor(90), HudEditor.getColor(270), HudEditor.getColor(270));
                    }
                }
            }
        }
    }

    @NotNull
    private static Vec3d[] getVectors(@NotNull Entity ent) {
        double x = ent.prevX + (ent.getX() - ent.prevX) * (double)Render3DEngine.getTickDelta();
        double y = ent.prevY + (ent.getY() - ent.prevY) * (double)Render3DEngine.getTickDelta();
        double z = ent.prevZ + (ent.getZ() - ent.prevZ) * (double)Render3DEngine.getTickDelta();
        Box axisAlignedBB2 = ent.getBoundingBox();
        Box axisAlignedBB = new Box(axisAlignedBB2.minX - ent.getX() + x - 0.05, axisAlignedBB2.minY - ent.getY() + y, axisAlignedBB2.minZ - ent.getZ() + z - 0.05, axisAlignedBB2.maxX - ent.getX() + x + 0.05, axisAlignedBB2.maxY - ent.getY() + y + 0.15, axisAlignedBB2.maxZ - ent.getZ() + z + 0.05);
        return new Vec3d[]{new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)};
    }

    private int getAreaCloudColor(AreaEffectCloudEntity ent) {
        ParticleEffect particleEffect = ent.getParticleType();
        if (particleEffect instanceof EntityEffectParticleEffect) {
            EntityEffectParticleEffect effect = (EntityEffectParticleEffect)particleEffect;
            return ((IAreaEffectCloudEntity)ent).getPotionContentsComponent().getColor();
        }
        return -1;
    }

    public static float getRotations(Vec2f vec) {
        if (ESP.mc.player == null) {
            return 0.0f;
        }
        double x = (double)vec.x - ESP.mc.player.getPos().x;
        double z = (double)vec.y - ESP.mc.player.getPos().z;
        return (float)(-(Math.atan2(x, z) * 57.29577951308232));
    }

    public static enum Colors {
        SyncColor,
        Custom;

    }
}

