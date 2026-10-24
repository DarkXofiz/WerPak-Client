/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Matrix4f
 */
package thunder.hack.features.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.setting.Setting;
import thunder.hack.utility.ThunderUtility;
import thunder.hack.utility.Timer;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

public class JumpCircle
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Default);
    private final Setting<Boolean> easeOut = new Setting<Boolean>("EaseOut", true);
    private final Setting<Float> rotateSpeed = new Setting<Float>("RotateSpeed", Float.valueOf(2.0f), Float.valueOf(0.5f), Float.valueOf(5.0f));
    private final Setting<Float> circleScale = new Setting<Float>("CircleScale", Float.valueOf(1.0f), Float.valueOf(0.5f), Float.valueOf(5.0f));
    private final Setting<Boolean> onlySelf = new Setting<Boolean>("OnlySelf", false);
    private final List<Circle> circles = new ArrayList<Circle>();
    private final List<PlayerEntity> cache = new CopyOnWriteArrayList<PlayerEntity>();
    private Identifier custom;

    public JumpCircle() {
        super("JumpCircle", Module.Category.RENDER);
    }

    @Override
    public void onEnable() {
        try {
            this.custom = ThunderUtility.getCustomImg("circle");
        }
        catch (Exception e) {
            this.sendMessage(e.getMessage());
        }
    }

    @Override
    public void onUpdate() {
        if (this.mode.is(Mode.Custom) && this.custom == null) {
            try {
                this.custom = ThunderUtility.getCustomImg("circle");
            }
            catch (Exception e) {
                this.sendMessage(".minecraft -> ThunderHackRecode -> misc -> images -> circle.png");
            }
        }
        for (PlayerEntity pl2 : JumpCircle.mc.world.getPlayers()) {
            if (this.cache.contains(pl2) || !pl2.isOnGround() || JumpCircle.mc.player != pl2 && this.onlySelf.getValue().booleanValue()) continue;
            this.cache.add(pl2);
        }
        this.cache.forEach(pl -> {
            if (pl != null && !pl.isOnGround()) {
                this.circles.add(new Circle(new Vec3d(pl.getX(), (double)((float)((int)Math.floor(pl.getY())) + 0.001f), pl.getZ()), new Timer()));
                this.cache.remove(pl);
            }
        });
        this.circles.removeIf(c -> c.timer.passedMs(this.easeOut.getValue() != false ? 5000L : 6000L));
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        Collections.reverse(this.circles);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        switch (this.mode.getValue().ordinal()) {
            case 1: {
                RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.bubble);
                break;
            }
            case 0: {
                RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.default_circle);
                break;
            }
            case 2: {
                RenderSystem.setShaderTexture((int)0, (Identifier)Objects.requireNonNullElse(this.custom, TextureStorage.default_circle));
            }
        }
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        for (Circle c : this.circles) {
            float colorAnim = (float)c.timer.getPassedTimeMs() / 6000.0f;
            float sizeAnim = this.circleScale.getValue().floatValue() - (float)Math.pow(1.0f - (float)c.timer.getPassedTimeMs() * (this.easeOut.getValue() != false ? 2.0f : 1.0f) / 5000.0f, 4.0);
            stack.push();
            stack.translate(c.pos().x - JumpCircle.mc.getEntityRenderDispatcher().camera.getPos().getX(), c.pos().y - JumpCircle.mc.getEntityRenderDispatcher().camera.getPos().getY(), c.pos().z - JumpCircle.mc.getEntityRenderDispatcher().camera.getPos().getZ());
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sizeAnim * this.rotateSpeed.getValue().floatValue() * 1000.0f));
            float scale = sizeAnim * 2.0f;
            Matrix4f matrix = stack.peek().getPositionMatrix();
            buffer.vertex(matrix, -sizeAnim, -sizeAnim + scale, 0.0f).texture(0.0f, 1.0f).color(Render2DEngine.applyOpacity(HudEditor.getColor(270), 1.0f - colorAnim).getRGB());
            buffer.vertex(matrix, -sizeAnim + scale, -sizeAnim + scale, 0.0f).texture(1.0f, 1.0f).color(Render2DEngine.applyOpacity(HudEditor.getColor(0), 1.0f - colorAnim).getRGB());
            buffer.vertex(matrix, -sizeAnim + scale, -sizeAnim, 0.0f).texture(1.0f, 0.0f).color(Render2DEngine.applyOpacity(HudEditor.getColor(180), 1.0f - colorAnim).getRGB());
            buffer.vertex(matrix, -sizeAnim, -sizeAnim, 0.0f).texture(0.0f, 0.0f).color(Render2DEngine.applyOpacity(HudEditor.getColor(90), 1.0f - colorAnim).getRGB());
            stack.pop();
        }
        Render2DEngine.endBuilding(buffer);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.enableDepthTest();
        Collections.reverse(this.circles);
    }

    public static enum Mode {
        Default,
        Portal,
        Custom;

    }

    public record Circle(Vec3d pos, Timer timer) {
    }
}

