/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.decoration.ArmorStandEntity
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.BuiltBuffer
 *  org.joml.Matrix4f
 */
package thunder.hack.features.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.BuiltBuffer;
import org.joml.Matrix4f;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.TextureStorage;

public class TargetESP
extends Module {
    public final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Ghost);
    public final Setting<Float> ghostSpeed = new Setting<Float>("GhostSpeed", Float.valueOf(1.0f), Float.valueOf(0.5f), Float.valueOf(2.0f), v -> this.mode.getValue() == Mode.Ghost || this.mode.getValue() == Mode.Prizraki);
    public final Setting<Integer> ghostV2Trails = new Setting<Integer>("V2Trails", 3, 1, 5, v -> this.mode.getValue() == Mode.GhostV2);
    public final Setting<Integer> ghostV2Length = new Setting<Integer>("V2Length", 32, 10, 60, v -> this.mode.getValue() == Mode.GhostV2);
    public final Setting<Float> ghostV2Speed = new Setting<Float>("V2Speed", Float.valueOf(2.0f), Float.valueOf(0.5f), Float.valueOf(6.0f), v -> this.mode.getValue() == Mode.GhostV2);
    public final Setting<Float> ghostV2Radius = new Setting<Float>("V2Radius", Float.valueOf(0.75f), Float.valueOf(0.2f), Float.valueOf(2.0f), v -> this.mode.getValue() == Mode.GhostV2);
    public final Setting<Float> ghostV2Spread = new Setting<Float>("V2Spread", Float.valueOf(0.85f), Float.valueOf(0.1f), Float.valueOf(2.0f), v -> this.mode.getValue() == Mode.GhostV2);
    public final Setting<Float> ghostV2Wobble = new Setting<Float>("V2Wobble", Float.valueOf(0.35f), Float.valueOf(0.0f), Float.valueOf(1.0f), v -> this.mode.getValue() == Mode.GhostV2);
    public final Setting<Float> ghostV2Size = new Setting<Float>("V2Size", Float.valueOf(0.38f), Float.valueOf(0.1f), Float.valueOf(0.8f), v -> this.mode.getValue() == Mode.GhostV2);
    public final Setting<Boolean> ghostV2Rainbow = new Setting<Boolean>("V2Rainbow", false, v -> this.mode.getValue() == Mode.GhostV2);
    public final Setting<Float> prizrakiSize = new Setting<Float>("PrizrakiSize", Float.valueOf(0.4f), Float.valueOf(0.1f), Float.valueOf(1.0f), v -> this.mode.getValue() == Mode.Prizraki);
    public final Setting<Float> kolcoSpeed = new Setting<Float>("KolcoSpeed", Float.valueOf(0.01f), Float.valueOf(0.05f), Float.valueOf(0.3f), v -> this.mode.getValue() == Mode.Kolco);
    public final Setting<Boolean> kolcoRainbow = new Setting<Boolean>("KolcoRainbow", true, v -> this.mode.getValue() == Mode.Kolco);
    public final Setting<Boolean> kolcoDamageRed = new Setting<Boolean>("DamageRed", true, v -> this.mode.getValue() == Mode.Kolco);
    public final Setting<Boolean> kolcoThroughWalls = new Setting<Boolean>("ThroughWalls", false, v -> this.mode.getValue() == Mode.Kolco);
    public final Setting<ColorSetting> color = new Setting<ColorSetting>("Color", new ColorSetting(new Color(135, 80, 255).getRGB()));
    public final Setting<Boolean> drawBox = new Setting<Boolean>("DrawBox", true);
    public final Setting<ColorSetting> boxColor = new Setting<ColorSetting>("BoxColor", new ColorSetting(new Color(255, 255, 255).getRGB()), v -> this.drawBox.getValue());
    public final Setting<Boolean> predict = new Setting<Boolean>("Predict", false);
    public final Setting<Float> predictTicks = new Setting<Float>("PredictTicks", Float.valueOf(10.0f), Float.valueOf(1.0f), Float.valueOf(40.0f), v -> this.predict.getValue());
    public final Setting<ColorSetting> predictColor = new Setting<ColorSetting>("PredictColor", new ColorSetting(new Color(255, 0, 0).getRGB()), v -> this.predict.getValue());
    private final SimpleAnimation animation = new SimpleAnimation(500L);
    private Entity lastTarget = null;
    private float ghostAnimTime = 0.0f;
    private long lastGhostTimestamp = 0L;
    private final SimpleAnimation ghostV2Anim = new SimpleAnimation(350L);
    private Entity ghostV2LastTarget = null;
    private float ghostV2AnimTime = 0.0f;
    private long lastGhostV2Timestamp = 0L;
    private static long lastPrizrakTime = System.currentTimeMillis();
    private long lastKolcoUpdateTime = 0L;
    private double kolcoStep = 0.0;
    private float kolcoAnimProgress = 0.0f;
    private long lastDamageTime = 0L;
    private float damageFlashIntensity = 0.0f;
    private final ArrayList<Particle> particles = new ArrayList();
    private float spawnAccumulator = 0.0f;
    private long lastCubeTime = 0L;
    private final SimpleAnimation crystalsAnim = new SimpleAnimation(350L);
    private Entity crystalsLastTarget = null;
    private final List<RiderParticle> riderParticles = new ArrayList<RiderParticle>();
    private final SimpleAnimation riderAnim = new SimpleAnimation(300L);
    private float riderMovingAngle = 0.0f;

    public TargetESP() {
        super("TargetESP", Module.Category.RENDER);
    }

    @Override
    public void onRender3D(MatrixStack matrices) {
        Camera camera = TargetESP.mc.gameRenderer.getCamera();
        float tickDelta = Render3DEngine.getTickDelta();
        Entity target = Aura.target;
        if (target != null && target != TargetESP.mc.player && !(target instanceof ArmorStandEntity)) {
            this.lastTarget = target;
            this.animation.run(1.0f);
        } else {
            this.animation.run(0.0f);
            if (this.animation.getValue() == 0.0f) {
                this.lastTarget = null;
            }
        }
        if (target != null && this.lastTarget != null) {
            switch (this.mode.getValue().ordinal()) {
                case 0: {
                    this.renderGhost(matrices, camera, (LivingEntity)this.lastTarget, tickDelta);
                    break;
                }
                case 1: {
                    this.ghostV2LastTarget = this.lastTarget;
                    this.ghostV2Anim.run(1.0f);
                    float v2Anim = this.ghostV2Anim.getValue();
                    if (!(v2Anim > 0.001f)) break;
                    this.renderGhostV2(matrices, camera, (LivingEntity)this.ghostV2LastTarget, v2Anim, tickDelta);
                    break;
                }
                case 2: {
                    this.crystalsLastTarget = this.lastTarget;
                    this.crystalsAnim.run(1.0f);
                    float anim = this.crystalsAnim.getValue();
                    if (!(anim > 0.001f)) break;
                    this.renderCrystals(matrices, camera, (LivingEntity)this.crystalsLastTarget, false, anim, tickDelta);
                    break;
                }
                case 3: {
                    this.crystalsLastTarget = this.lastTarget;
                    this.crystalsAnim.run(1.0f);
                    float anim2 = this.crystalsAnim.getValue();
                    if (!(anim2 > 0.001f)) break;
                    this.renderCrystals(matrices, camera, (LivingEntity)this.crystalsLastTarget, true, anim2, tickDelta);
                    break;
                }
                case 4: {
                    this.renderCubes(matrices, camera, (LivingEntity)this.lastTarget, tickDelta);
                    break;
                }
                case 5: {
                    this.renderPrizraki(matrices, camera, (LivingEntity)this.lastTarget, tickDelta);
                    break;
                }
                case 6: {
                    this.kolcoAnimProgress = Math.min(1.0f, this.kolcoAnimProgress + 0.1f);
                    Entity tgtEntity = this.lastTarget;
                    if (tgtEntity instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity)tgtEntity;
                        if (living.hurtTime > 0) {
                            this.lastDamageTime = System.currentTimeMillis();
                        }
                    }
                    long now = System.currentTimeMillis();
                    if (this.lastKolcoUpdateTime == 0L) {
                        this.lastKolcoUpdateTime = now;
                    }
                    float dt = Math.min((float)(now - this.lastKolcoUpdateTime) / 1000.0f, 0.1f);
                    this.lastKolcoUpdateTime = now;
                    this.kolcoStep += (double)(this.kolcoSpeed.getValue().floatValue() * dt) * 60.0;
                    this.renderKolco(matrices, camera, (LivingEntity)this.lastTarget, tickDelta, this.kolcoAnimProgress);
                    break;
                }
                case 7: {
                    this.renderGhostRider(matrices, camera, (LivingEntity)this.lastTarget, tickDelta);
                }
            }
            if (this.predict.getValue().booleanValue()) {
                this.renderPredictBox(matrices, camera, (LivingEntity)this.lastTarget, tickDelta);
            }
            if (this.drawBox.getValue().booleanValue() && this.lastTarget instanceof LivingEntity) {
                Box box = this.lastTarget.getBoundingBox();
                Render3DEngine.drawFilledBox(matrices, box, Render2DEngine.injectAlpha(this.boxColor.getValue().getColorObject(), 50));
                Render3DEngine.drawBoxOutline(box, this.boxColor.getValue().getColorObject(), 2.0f);
            }
        } else {
            float anim;
            this.lastGhostTimestamp = 0L;
            this.ghostAnimTime = 0.0f;
            this.lastCubeTime = 0L;
            this.spawnAccumulator = 0.0f;
            this.riderParticles.clear();
            if (this.mode.getValue() == Mode.GhostV2) {
                this.ghostV2Anim.run(0.0f);
                anim = this.ghostV2Anim.getValue();
                if (anim > 0.001f && this.ghostV2LastTarget != null) {
                    this.renderGhostV2(matrices, camera, (LivingEntity)this.ghostV2LastTarget, anim, tickDelta);
                } else if (anim <= 0.001f) {
                    this.ghostV2LastTarget = null;
                    this.lastGhostV2Timestamp = 0L;
                }
            }
            if (this.mode.getValue() == Mode.Crystals || this.mode.getValue() == Mode.Crystals2) {
                this.crystalsAnim.run(0.0f);
                anim = this.crystalsAnim.getValue();
                if (anim > 0.001f && this.crystalsLastTarget != null) {
                    this.renderCrystals(matrices, camera, (LivingEntity)this.crystalsLastTarget, this.mode.getValue() == Mode.Crystals2, anim, tickDelta);
                } else if (anim <= 0.001f) {
                    this.crystalsLastTarget = null;
                }
            }
            if (this.mode.getValue() == Mode.Kolco) {
                this.kolcoAnimProgress = Math.max(0.0f, this.kolcoAnimProgress - 0.05f);
                if (this.kolcoAnimProgress > 0.0f && this.lastTarget != null) {
                    long now = System.currentTimeMillis();
                    if (this.lastKolcoUpdateTime == 0L) {
                        this.lastKolcoUpdateTime = now;
                    }
                    float dt = Math.min((float)(now - this.lastKolcoUpdateTime) / 1000.0f, 0.1f);
                    this.lastKolcoUpdateTime = now;
                    this.kolcoStep += (double)(this.kolcoSpeed.getValue().floatValue() * dt) * 60.0;
                    this.renderKolco(matrices, camera, (LivingEntity)this.lastTarget, tickDelta, this.kolcoAnimProgress);
                }
            } else {
                this.lastKolcoUpdateTime = 0L;
            }
            if (this.mode.getValue() == Mode.Cubes && !this.particles.isEmpty()) {
                this.renderCubes(matrices, camera, null, tickDelta);
            }
        }
    }

    private void renderGhost(MatrixStack matrices, Camera camera, LivingEntity target, float partialTicks) {
        Vec3d camPos = camera.getPos();
        float camYaw = camera.getYaw();
        float camPitch = camera.getPitch();
        double tx = MathHelper.lerp((double)partialTicks, (double)target.lastRenderX, (double)target.getX());
        double ty = MathHelper.lerp((double)partialTicks, (double)target.lastRenderY, (double)target.getY());
        double tz = MathHelper.lerp((double)partialTicks, (double)target.lastRenderZ, (double)target.getZ());
        double x = tx - camPos.x;
        double y = ty - camPos.y;
        double z = tz - camPos.z;
        long now = System.currentTimeMillis();
        if (this.lastGhostTimestamp == 0L) {
            this.lastGhostTimestamp = now;
        }
        this.ghostAnimTime += 4.0f * (float)(now - this.lastGhostTimestamp) / 600.0f * this.ghostSpeed.getValue().floatValue();
        this.lastGhostTimestamp = now;
        Color c = this.color.getValue().getColorObject();
        float r = (float)c.getRed() / 255.0f;
        float g = (float)c.getGreen() / 255.0f;
        float b = (float)c.getBlue() / 255.0f;
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        boolean hasGhost = false;
        float radiusMod = 1.3f;
        float spriteSize = 0.5f;
        int totalSprites = 25;
        for (int ring = 0; ring < 9; ring += 3) {
            for (int si = 0; si < totalSprites; ++si) {
                float ao = (float)si * 0.1f;
                float ro = (float)Math.pow(ring, 2.0);
                double sx = x + (double)(radiusMod * 0.6f * MathHelper.sin((float)(this.ghostAnimTime + ao + ro)));
                double sy = y + 0.4 + (double)(radiusMod * 0.3f * MathHelper.sin((float)(this.ghostAnimTime + (float)si * 0.2f))) + (double)(0.2f * (float)ring);
                double sz = z + (double)(radiusMod * 0.6f * MathHelper.cos((float)(this.ghostAnimTime + ao - ro)));
                float progress = (float)si / (float)(totalSprites - 1);
                float scale = spriteSize * (0.2f + progress * 0.8f);
                float alpha = 1.0f - progress * 0.9f;
                if (scale < 0.01f || alpha < 0.01f) continue;
                float cr = Math.min(1.0f, r * 1.5f);
                float cg = Math.min(1.0f, g * 1.5f);
                float cb = Math.min(1.0f, b * 1.5f);
                this.putBloomQuad(builder, matrices, sx, sy, sz, scale * 4.0f, r, g, b, alpha * 0.15f, camYaw, camPitch);
                this.putBloomQuad(builder, matrices, sx, sy, sz, scale * 2.0f, r, g, b, alpha * 0.35f, camYaw, camPitch);
                this.putBloomQuad(builder, matrices, sx, sy, sz, scale * 0.8f, cr, cg, cb, alpha, camYaw, camPitch);
                hasGhost = true;
            }
        }
        if (hasGhost) {
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)builder.end());
        }
        this.restoreRenderState();
    }

    private void renderGhostV2(MatrixStack matrices, Camera camera, LivingEntity target, float animProgress, float partialTicks) {
        if (target == null || animProgress <= 0.001f) {
            return;
        }
        Vec3d camPos = camera.getPos();
        float camYaw = camera.getYaw();
        float camPitch = camera.getPitch();
        double tx = MathHelper.lerp((double)partialTicks, (double)target.lastRenderX, (double)target.getX()) - camPos.x;
        double ty = MathHelper.lerp((double)partialTicks, (double)target.lastRenderY, (double)target.getY()) - camPos.y;
        double tz = MathHelper.lerp((double)partialTicks, (double)target.lastRenderZ, (double)target.getZ()) - camPos.z;
        long now = System.currentTimeMillis();
        if (this.lastGhostV2Timestamp == 0L) {
            this.lastGhostV2Timestamp = now;
        }
        float dt = Math.min((float)(now - this.lastGhostV2Timestamp) / 1000.0f, 0.1f);
        this.lastGhostV2Timestamp = now;
        this.ghostV2AnimTime += dt * this.ghostV2Speed.getValue().floatValue() * 120.0f;
        int trails = this.ghostV2Trails.getValue();
        int length = this.ghostV2Length.getValue();
        float radiusBase = (target.getWidth() * 0.5f + this.ghostV2Radius.getValue().floatValue()) * animProgress;
        float baseSize = this.ghostV2Size.getValue().floatValue();
        float wobble = this.ghostV2Wobble.getValue().floatValue();
        float spread = this.ghostV2Spread.getValue().floatValue();
        float entityHeight = target.getHeight();
        Color baseColor = this.color.getValue().getColorObject();
        boolean isRainbow = this.ghostV2Rainbow.getValue();
        float hurtBoost = target.hurtTime > 0 ? (float)target.hurtTime / 10.0f : 0.0f;
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.firefly);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        for (int j = 0; j < trails; ++j) {
            float trailProgress = trails > 1 ? (float)j / (float)(trails - 1) : 0.5f;
            double trailBaseY = ty + (double)entityHeight * 0.12 + (double)(entityHeight * spread * trailProgress);
            float baseAngle = this.ghostV2AnimTime + (float)j * (360.0f / (float)trails);
            for (int i = 0; i <= length; ++i) {
                float b;
                float g;
                float r;
                float t = (float)i / (float)length;
                float arcAngle = baseAngle - (1.0f - t) * 115.0f;
                double radAngle = Math.toRadians(arcAngle);
                double wave = Math.sin(Math.toRadians(this.ghostV2AnimTime * 2.2f + (float)i * 5.0f + (float)j * 120.0f)) * ((double)wobble * 0.22);
                double rad = (double)radiusBase + Math.cos(Math.toRadians(this.ghostV2AnimTime * 1.5f + (float)j * 90.0f)) * ((double)wobble * 0.08);
                double px = tx + Math.cos(radAngle) * rad;
                double py = trailBaseY + wave;
                double pz = tz + Math.sin(radAngle) * rad;
                float quadScale = baseSize * (0.22f + 0.78f * t) * animProgress;
                float quadAlpha = (0.1f + 0.9f * t) * animProgress;
                if (isRainbow) {
                    float hue = ((float)(now % 3000L) / 3000.0f + (float)j * 0.25f + t * 0.1f) % 1.0f;
                    int rgb = Color.HSBtoRGB(hue, 0.75f, 1.0f);
                    r = (float)(rgb >> 16 & 0xFF) / 255.0f;
                    g = (float)(rgb >> 8 & 0xFF) / 255.0f;
                    b = (float)(rgb & 0xFF) / 255.0f;
                } else {
                    r = (float)baseColor.getRed() / 255.0f;
                    g = (float)baseColor.getGreen() / 255.0f;
                    b = (float)baseColor.getBlue() / 255.0f;
                }
                if (hurtBoost > 0.05f) {
                    r = MathHelper.lerp((float)(hurtBoost * 0.7f), (float)r, (float)1.0f);
                    g = MathHelper.lerp((float)(hurtBoost * 0.7f), (float)g, (float)0.2f);
                    b = MathHelper.lerp((float)(hurtBoost * 0.7f), (float)b, (float)0.3f);
                }
                this.putTexBloomQuad(builder, matrices, px, py, pz, quadScale * 2.2f, r, g, b, quadAlpha * 0.32f, camYaw, camPitch);
                this.putTexBloomQuad(builder, matrices, px, py, pz, quadScale * 1.2f, r, g, b, quadAlpha * 0.75f, camYaw, camPitch);
                float coreBrightness = t * t * 0.9f;
                float cr = Math.min(1.0f, r + coreBrightness * 0.65f);
                float cg = Math.min(1.0f, g + coreBrightness * 0.65f);
                float cb = Math.min(1.0f, b + coreBrightness * 0.65f);
                this.putTexBloomQuad(builder, matrices, px, py, pz, quadScale * 0.6f, cr, cg, cb, quadAlpha * (0.85f + 0.15f * t), camYaw, camPitch);
            }
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)builder.end());
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void putTexBloomQuad(BufferBuilder builder, MatrixStack matrices, double x, double y, double z, float size, float r, float g, float b, float a, float yaw, float pitch) {
        matrices.push();
        matrices.translate(x, y, z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float half = size / 2.0f;
        builder.vertex(matrix, -half, half, 0.0f).texture(0.0f, 1.0f).color(r, g, b, a);
        builder.vertex(matrix, half, half, 0.0f).texture(1.0f, 1.0f).color(r, g, b, a);
        builder.vertex(matrix, half, -half, 0.0f).texture(1.0f, 0.0f).color(r, g, b, a);
        builder.vertex(matrix, -half, -half, 0.0f).texture(0.0f, 0.0f).color(r, g, b, a);
        matrices.pop();
    }

    private void renderPrizraki(MatrixStack matrices, Camera camera, LivingEntity target, float partialTicks) {
        Vec3d camPos = camera.getPos();
        float camYaw = camera.getYaw();
        float camPitch = camera.getPitch();
        double tx = MathHelper.lerp((double)partialTicks, (double)target.lastRenderX, (double)target.getX());
        double ty = MathHelper.lerp((double)partialTicks, (double)target.lastRenderY, (double)target.getY()) + 0.38 + (double)target.getHeight() / 2.0;
        double tz = MathHelper.lerp((double)partialTicks, (double)target.lastRenderZ, (double)target.getZ());
        double rx = tx - camPos.x + 0.2;
        double ry = ty - camPos.y;
        double rz = tz - camPos.z;
        double radius = 0.4 + (double)target.getWidth() / 2.0;
        float speed = 30.0f / this.ghostSpeed.getValue().floatValue();
        float size = this.prizrakiSize.getValue().floatValue();
        double distance = 6.0;
        int length = 34;
        long now = System.currentTimeMillis();
        Color c = this.color.getValue().getColorObject();
        float r = (float)c.getRed() / 255.0f;
        float g = (float)c.getGreen() / 255.0f;
        float b = (float)c.getBlue() / 255.0f;
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        for (int trail = 0; trail < 3; ++trail) {
            for (int i = 0; i < length; ++i) {
                double angle = 0.05 * ((double)(now - lastPrizrakTime) - (double)i * distance) / (double)speed;
                double s = Math.sin(angle * Math.PI) * radius;
                double cos = Math.cos(angle * Math.PI) * radius;
                double o = trail == 0 ? Math.cos(angle * Math.PI) * radius : Math.sin(angle * Math.PI) * radius;
                float t = (float)i / (float)(length - 1);
                float curSize = size * (1.0f - t * 0.5f);
                float alpha = 1.0f - t * 0.9f;
                double px = rx + (trail == 1 ? -s : s);
                double py = ry + o;
                double pz = rz + (trail == 2 ? cos : -cos);
                float cr = Math.min(1.0f, r * 1.5f);
                float cg = Math.min(1.0f, g * 1.5f);
                float cb = Math.min(1.0f, b * 1.5f);
                this.putBloomQuad(buffer, matrices, px, py, pz, curSize * 0.6f, r, g, b, alpha * 0.15f, camYaw, camPitch);
                this.putBloomQuad(buffer, matrices, px, py, pz, curSize * 0.35f, r, g, b, alpha * 0.35f, camYaw, camPitch);
                this.putBloomQuad(buffer, matrices, px, py, pz, curSize * 0.15f, cr, cg, cb, alpha, camYaw, camPitch);
            }
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buffer.end());
        this.restoreRenderState();
    }

    private void renderKolco(MatrixStack matrices, Camera camera, LivingEntity target, float partialTicks, float animProgress) {
        if (!(animProgress <= 0.0f) && target != null) {
            boolean canSee;
            Vec3d camPos = camera.getPos();
            float camYaw = camera.getYaw();
            float camPitch = camera.getPitch();
            double tx = MathHelper.lerp((double)partialTicks, (double)target.lastRenderX, (double)target.getX());
            double ty = MathHelper.lerp((double)partialTicks, (double)target.lastRenderY, (double)target.getY());
            double tz = MathHelper.lerp((double)partialTicks, (double)target.lastRenderZ, (double)target.getZ());
            double rx = tx - camPos.x;
            double ry = ty - camPos.y;
            double rz = tz - camPos.z;
            float entityWidth = target.getWidth() * 0.9f;
            float entityHeight = target.getHeight();
            float eased = TargetESP.easeOutCubic(animProgress);
            boolean bl = canSee = TargetESP.mc.player != null && TargetESP.mc.player.canSee((Entity)target);
            if (this.kolcoThroughWalls.getValue().booleanValue() || canSee) {
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableCull();
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask((boolean)false);
                BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                boolean hasKolco = false;
                double golovkaY = this.absSin(this.kolcoStep) * (double)entityHeight;
                float spriteSize = 0.12f;
                int totalPoints = 72;
                long now = System.currentTimeMillis();
                for (int i = 0; i < totalPoints; ++i) {
                    double angle = Math.PI * 2 * (double)i / (double)totalPoints;
                    float xOff = (float)(Math.cos(angle) * (double)entityWidth);
                    float zOff = (float)(Math.sin(angle) * (double)entityWidth);
                    int baseColor = this.getKolcoColor(i * (360 / totalPoints), now);
                    float cr = (float)(baseColor >> 16 & 0xFF) / 255.0f;
                    float cg = (float)(baseColor >> 8 & 0xFF) / 255.0f;
                    float cb = (float)(baseColor & 0xFF) / 255.0f;
                    float ss = eased * spriteSize;
                    if (ss < 0.01f) continue;
                    this.putBloomQuad(builder, matrices, rx + (double)xOff, ry + golovkaY, rz + (double)zOff, ss * 4.0f, cr, cg, cb, eased * 0.15f, camYaw, camPitch);
                    this.putBloomQuad(builder, matrices, rx + (double)xOff, ry + golovkaY, rz + (double)zOff, ss * 2.0f, cr, cg, cb, eased * 0.35f, camYaw, camPitch);
                    this.putBloomQuad(builder, matrices, rx + (double)xOff, ry + golovkaY, rz + (double)zOff, ss * 0.8f, Math.min(1.0f, cr * 1.5f), Math.min(1.0f, cg * 1.5f), Math.min(1.0f, cb * 1.5f), eased, camYaw, camPitch);
                    hasKolco = true;
                }
                if (hasKolco) {
                    BufferRenderer.drawWithGlobalProgram((BuiltBuffer)builder.end());
                }
                this.restoreRenderState();
            }
        }
    }

    private double absSin(double input) {
        return Math.abs(1.0 + Math.sin(input)) / 2.0;
    }

    private int getKolcoColor(int offsetAngle, long now) {
        int c;
        if (this.kolcoRainbow.getValue().booleanValue()) {
            float timeFactor = (float)(now % 3000L) / 3000.0f;
            int hue = (int)(timeFactor * 360.0f) + offsetAngle;
            c = Color.HSBtoRGB((float)(hue % 360) / 360.0f, 0.7f, 1.0f);
            c = 0xFF000000 | c & 0xFFFFFF;
        } else {
            c = this.color.getValue().getColor();
            if ((c >> 24 & 0xFF) == 0) {
                c = 0xFF000000 | c & 0xFFFFFF;
            }
        }
        return this.applyDamageFlash(c);
    }

    private int applyDamageFlash(int c) {
        if (!this.kolcoDamageRed.getValue().booleanValue()) {
            return c;
        }
        long timeSince = System.currentTimeMillis() - this.lastDamageTime;
        float target = 0.0f;
        if (timeSince < 500L) {
            target = 1.0f - TargetESP.easeOutCubic((float)timeSince / 500.0f);
        }
        this.damageFlashIntensity = MathHelper.lerp((float)0.1f, (float)this.damageFlashIntensity, (float)target);
        if (this.damageFlashIntensity < 0.05f) {
            return c;
        }
        int a = c >> 24 & 0xFF;
        int r = c >> 16 & 0xFF;
        int g = c >> 8 & 0xFF;
        int b = c & 0xFF;
        return a << 24 | (int)(MathHelper.lerp((float)this.damageFlashIntensity, (float)((float)r / 255.0f), (float)1.0f) * 255.0f) << 16 | (int)(MathHelper.lerp((float)this.damageFlashIntensity, (float)((float)g / 255.0f), (float)0.2f) * 255.0f) << 8 | (int)(MathHelper.lerp((float)this.damageFlashIntensity, (float)((float)b / 255.0f), (float)0.2f) * 255.0f);
    }

    private void renderCrystals(MatrixStack matrices, Camera camera, LivingEntity target, boolean sharp, float anim, float partialTicks) {
        if (target != null && TargetESP.mc.player != null) {
            float eased = TargetESP.easeOutCubic(anim);
            float time = ((float)TargetESP.mc.player.age + partialTicks) * 6.0f;
            Vec3d camPos = camera.getPos();
            double tx = MathHelper.lerp((double)partialTicks, (double)target.lastRenderX, (double)target.getX());
            double ty = MathHelper.lerp((double)partialTicks, (double)target.lastRenderY, (double)target.getY());
            double tz = MathHelper.lerp((double)partialTicks, (double)target.lastRenderZ, (double)target.getZ());
            float camYaw = camera.getYaw();
            float camPitch = camera.getPitch();
            float entityHeight = target.getHeight();
            float halfWidth = target.getWidth() * 0.5f;
            Color baseColor = this.color.getValue().getColorObject();
            float r = Math.min(1.0f, (float)baseColor.getRed() / 255.0f * 1.3f);
            float g = Math.min(1.0f, (float)baseColor.getGreen() / 255.0f * 1.3f);
            float b = Math.min(1.0f, (float)baseColor.getBlue() / 255.0f * 1.3f);
            matrices.push();
            matrices.translate(tx - camPos.x, ty - camPos.y, tz - camPos.z);
            int crystalCount = 18;
            float pelvisY = entityHeight * 0.35f;
            float torsoY = entityHeight * 0.55f;
            float neckY = entityHeight * 0.74f;
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask((boolean)false);
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
            boolean hasCrystals = false;
            for (int i = 0; i < crystalCount; ++i) {
                float s1 = (float)Math.sin((float)i * 1.7f + 0.3f) * 0.5f + 0.5f;
                float s2 = (float)Math.cos((float)i * 2.3f + 0.7f) * 0.5f + 0.5f;
                float s3 = (float)Math.sin((float)i * 3.1f + 1.1f) * 0.5f + 0.5f;
                float angle = time + (float)i * (360.0f / (float)crystalCount) + s1 * 12.0f;
                float radius = halfWidth + 0.25f + s3 * 0.15f;
                float cx = radius * (float)Math.cos(Math.toRadians(angle));
                float cz = radius * (float)Math.sin(Math.toRadians(angle));
                float cy = s2 * entityHeight;
                float scale = 0.18f * eased;
                if (scale < 0.001f) continue;
                float lookY = this.getCrystalLookY(cy, entityHeight, pelvisY, torsoY, neckY);
                float dx = -cx;
                float dy = lookY - cy;
                float dz = -cz;
                float yaw = (float)Math.toDegrees(Math.atan2(dz, dx));
                float pitch = (float)Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
                this.drawCrystalShape(buf, matrices, cx, cy, cz, scale, yaw, pitch, (int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f), (int)(200.0f * anim), sharp);
                hasCrystals = true;
            }
            if (hasCrystals) {
                BufferRenderer.drawWithGlobalProgram((BuiltBuffer)buf.end());
            }
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
            matrices.pop();
            RenderSystem.depthMask((boolean)true);
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }
    }

    private float getCrystalLookY(float cy, float h, float pelvis, float torso, float neck) {
        float n = cy / h;
        if (n < 0.33f) {
            return pelvis;
        }
        return n < 0.6f ? torso : neck;
    }

    private void drawCrystalShape(BufferBuilder buf, MatrixStack ms, float x, float y, float z, float scale, float yaw, float pitch, int r, int g, int b, int a, boolean sharp) {
        ms.push();
        ms.translate(x, y, z);
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
        ms.scale(scale, scale, scale);
        Matrix4f m = ms.peek().getPositionMatrix();
        float w = sharp ? 0.35f : 0.7f;
        float h = sharp ? 1.2f : 1.0f;
        this.tri(buf, m, h, 0.0f, 0.0f, 0.0f, w, 0.0f, 0.0f, 0.0f, w, r, g, b, a);
        this.tri(buf, m, h, 0.0f, 0.0f, 0.0f, 0.0f, w, 0.0f, -w, 0.0f, r, g, b, a);
        this.tri(buf, m, h, 0.0f, 0.0f, 0.0f, -w, 0.0f, 0.0f, 0.0f, -w, r, g, b, a);
        this.tri(buf, m, h, 0.0f, 0.0f, 0.0f, 0.0f, -w, 0.0f, w, 0.0f, r, g, b, a);
        this.tri(buf, m, -h, 0.0f, 0.0f, 0.0f, w, 0.0f, 0.0f, 0.0f, w, r, g, b, a);
        this.tri(buf, m, -h, 0.0f, 0.0f, 0.0f, 0.0f, w, 0.0f, -w, 0.0f, r, g, b, a);
        this.tri(buf, m, -h, 0.0f, 0.0f, 0.0f, -w, 0.0f, 0.0f, 0.0f, -w, r, g, b, a);
        this.tri(buf, m, -h, 0.0f, 0.0f, 0.0f, 0.0f, -w, 0.0f, w, 0.0f, r, g, b, a);
        ms.pop();
    }

    private void tri(BufferBuilder buf, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int r, int g, int b, int a) {
        buf.vertex(m, x1, y1, z1).color(r, g, b, a);
        buf.vertex(m, x2, y2, z2).color(r, g, b, a);
        buf.vertex(m, x3, y3, z3).color(r, g, b, a);
    }

    private void renderCubes(MatrixStack matrices, Camera camera, LivingEntity target, float partialTicks) {
        long now = System.currentTimeMillis();
        if (this.lastCubeTime == 0L) {
            this.lastCubeTime = now;
        }
        float dt = Math.min((float)(now - this.lastCubeTime) / 1000.0f, 0.1f);
        this.lastCubeTime = now;
        if (target != null) {
            this.spawnAccumulator += dt;
            while (this.spawnAccumulator >= 0.017f) {
                this.spawnAccumulator -= 0.017f;
                for (int i = 0; i < 1; ++i) {
                    double rand = Math.random() * 360.0;
                    double px = Math.cos(Math.toRadians(rand)) * 0.7;
                    double py = 0.04 + Math.random() * 0.16;
                    double pz = Math.sin(Math.toRadians(rand)) * 0.7;
                    this.particles.add(new Particle((Entity)target, px, py, pz));
                }
            }
        }
        float camYaw = camera.getYaw();
        float camPitch = camera.getPitch();
        Iterator<Particle> it = this.particles.iterator();
        ArrayList<Particle> toRender = new ArrayList<Particle>();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update(dt);
            if (now - p.time > 1000L) {
                it.remove();
                continue;
            }
            toRender.add(p);
        }
        Color c = this.color.getValue().getColorObject();
        for (Particle p : toRender) {
            p.renderCube(matrices, camera, c.getRGB(), partialTicks);
        }
        if (!toRender.isEmpty()) {
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
            RenderSystem.disableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask((boolean)false);
            BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            boolean hasBloom = false;
            for (Particle p : toRender) {
                if (!p.renderBloom(builder, matrices, camera, c.getRGB(), camYaw, camPitch, partialTicks)) continue;
                hasBloom = true;
            }
            if (hasBloom) {
                BufferRenderer.drawWithGlobalProgram((BuiltBuffer)builder.end());
            }
            RenderSystem.depthMask((boolean)true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
        }
    }

    private void renderPredictBox(MatrixStack matrices, Camera camera, LivingEntity target, float partialTicks) {
        double velZ;
        double velY;
        double velX = target.getX() - target.lastRenderX;
        double speedSq = velX * velX + (velY = target.getY() - target.lastRenderY) * velY + (velZ = target.getZ() - target.lastRenderZ) * velZ;
        if (speedSq < 0.001) {
            return;
        }
        double tx = MathHelper.lerp((double)partialTicks, (double)target.lastRenderX, (double)target.getX());
        double ty = MathHelper.lerp((double)partialTicks, (double)target.lastRenderY, (double)target.getY());
        double tz = MathHelper.lerp((double)partialTicks, (double)target.lastRenderZ, (double)target.getZ());
        double predX = tx + velX * (double)this.predictTicks.getValue().floatValue();
        double predY = ty + velY * (double)this.predictTicks.getValue().floatValue() + (double)target.getHeight() / 2.0;
        double predZ = tz + velZ * (double)this.predictTicks.getValue().floatValue();
        Vec3d camPos = camera.getPos();
        double x = predX - camPos.x;
        double y = predY - camPos.y;
        double z = predZ - camPos.z;
        float camYaw = camera.getYaw();
        float camPitch = camera.getPitch();
        Color c = this.predictColor.getValue().getColorObject();
        float r = (float)c.getRed() / 255.0f;
        float g = (float)c.getGreen() / 255.0f;
        float b = (float)c.getBlue() / 255.0f;
        float a = 0.6f;
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        float size = 0.5f;
        this.putBloomQuad(builder, matrices, x, y, z, size, r, g, b, a, camYaw, camPitch);
        this.putBloomQuad(builder, matrices, x, y, z, size * 0.8f, r, g, b, 1.0f, camYaw, camPitch);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)builder.end());
        this.restoreRenderState();
    }

    private void renderGhostRider(MatrixStack matrices, Camera camera, LivingEntity target, float partialTicks) {
        while (this.riderParticles.size() < 3) {
            this.riderParticles.add(new RiderParticle(new Vec3d(target.getX(), target.getY() + (double)target.getHeight() / 2.0, target.getZ())));
        }
        this.riderMovingAngle = (this.riderMovingAngle + 20.0f) % 36000.0f;
        this.riderAnim.run(target.hurtTime > 7 ? 1.0f : 0.0f);
        Vec3d camPos = camera.getPos();
        float camYaw = camera.getYaw();
        float camPitch = camera.getPitch();
        double tx = MathHelper.lerp((double)partialTicks, (double)target.lastRenderX, (double)target.getX());
        double ty = MathHelper.lerp((double)partialTicks, (double)target.lastRenderY, (double)target.getY());
        double tz = MathHelper.lerp((double)partialTicks, (double)target.lastRenderZ, (double)target.getZ());
        Color c = this.color.getValue().getColorObject();
        float r = (float)c.getRed() / 255.0f;
        float g = (float)c.getGreen() / 255.0f;
        float b = (float)c.getBlue() / 255.0f;
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        for (RiderParticle rider : this.riderParticles) {
            rider.update((Entity)target);
            double px = rider.pos.x - camPos.x;
            double py = rider.pos.y - camPos.y;
            double pz = rider.pos.z - camPos.z;
            this.putBloomQuad(builder, matrices, px, py, pz, 0.4f, r, g, b, 0.25f, camYaw, camPitch);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)builder.end());
        this.restoreRenderState();
    }

    private void putBloomQuad(BufferBuilder builder, MatrixStack matrices, double x, double y, double z, float size, float r, float g, float b, float a, float yaw, float pitch) {
        matrices.push();
        matrices.translate(x, y, z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float half = size / 2.0f;
        builder.vertex(matrix, -half, -half, 0.0f).color(r, g, b, a);
        builder.vertex(matrix, -half, half, 0.0f).color(r, g, b, a);
        builder.vertex(matrix, half, half, 0.0f).color(r, g, b, a);
        builder.vertex(matrix, half, -half, 0.0f).color(r, g, b, a);
        matrices.pop();
    }

    private void restoreRenderState() {
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private static float easeOutCubic(float x) {
        return (float)(1.0 - Math.pow(1.0 - (double)x, 3.0));
    }

    public static enum Mode {
        Ghost,
        GhostV2,
        Crystals,
        Crystals2,
        Cubes,
        Prizraki,
        Kolco,
        GhostRider;

    }

    private static class SimpleAnimation {
        private final long duration;
        private long startTime;
        private float targetValue;
        private float startValue;

        public SimpleAnimation(long duration) {
            this.duration = duration;
            this.startTime = System.currentTimeMillis();
        }

        public void run(float target) {
            if (this.targetValue != target) {
                this.startValue = this.getValue();
                this.targetValue = target;
                this.startTime = System.currentTimeMillis();
            }
        }

        public float getValue() {
            long elapsed = System.currentTimeMillis() - this.startTime;
            if (elapsed >= this.duration) {
                return this.targetValue;
            }
            float t = (float)elapsed / (float)this.duration;
            float eased = TargetESP.easeOutCubic(t);
            return this.startValue + (this.targetValue - this.startValue) * eased;
        }
    }

    private static class Particle {
        public long time = System.currentTimeMillis();
        public double x;
        public double y;
        public double z;
        public Entity target;

        public Particle(Entity target, double x, double y, double z) {
            this.target = target;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void update(float dt) {
            this.y += (double)dt * 0.2;
        }

        public void renderCube(MatrixStack matrices, Camera camera, int color, float partialTicks) {
        }

        public boolean renderBloom(BufferBuilder builder, MatrixStack matrices, Camera camera, int color, float yaw, float pitch, float partialTicks) {
            return false;
        }
    }

    private static class RiderParticle {
        public Vec3d pos;

        public RiderParticle(Vec3d pos) {
            this.pos = pos;
        }

        public void update(Entity target) {
            this.pos = new Vec3d(target.getX(), target.getY() + (double)target.getHeight() / 2.0, target.getZ());
        }
    }
}

