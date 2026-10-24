/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.block.AirBlock
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.RotationAxis
 */
package thunder.hack.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

public class HitParticles
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Stars);
    private final Setting<Physics> physics = new Setting<Physics>("Physics", Physics.Fall);
    private final Setting<ColorSetting> colorrr = new Setting<ColorSetting>("Color", new ColorSetting(-2013200640));
    private final Setting<Boolean> onlySelf = new Setting<Boolean>("Self", false);
    private final Setting<Integer> amount = new Setting<Integer>("Amount", 2, 1, 5);
    private final Setting<Integer> lifeTime = new Setting<Integer>("LifeTime", 2, 1, 10);
    private final Setting<Integer> speed = new Setting<Integer>("Speed", 2, 1, 20);
    private final Setting<Float> starsScale = new Setting<Float>("Scale", Float.valueOf(3.0f), Float.valueOf(1.0f), Float.valueOf(10.0f), v -> this.mode.getValue() != Mode.Orbiz);
    private final Setting<ColorMode> colorMode = new Setting<ColorMode>("ColorMode", ColorMode.Sync);
    private final Setting<ColorSetting> colorH = new Setting<ColorSetting>("HealColor", new ColorSetting(3142544), v -> this.mode.is(Mode.Text));
    private final Setting<ColorSetting> colorD = new Setting<ColorSetting>("DamageColor", new ColorSetting(15811379), v -> this.mode.is(Mode.Text));
    private final HashMap<Integer, Float> healthMap = new HashMap();
    private final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList();

    public HitParticles() {
        super("HitParticles", Module.Category.RENDER);
    }

    @Override
    public void onUpdate() {
        this.particles.removeIf(Particle::update);
        if (this.mode.is(Mode.Text)) {
            for (Entity entity : HitParticles.mc.world.getEntities()) {
                if (entity == null || HitParticles.mc.player.squaredDistanceTo(entity) > 256.0 || !entity.isAlive() || !(entity instanceof LivingEntity)) continue;
                LivingEntity lent = (LivingEntity)entity;
                Color c = this.colorMode.getValue() == ColorMode.Sync ? HudEditor.getColor((int)MathUtility.random(1.0f, 228.0f)) : this.colorrr.getValue().getColorObject();
                float health = lent.getHealth() + lent.getAbsorptionAmount();
                float lastHealth = this.healthMap.getOrDefault(entity.getId(), Float.valueOf(health)).floatValue();
                this.healthMap.put(entity.getId(), Float.valueOf(health));
                if (lastHealth == health) continue;
                this.particles.add(new Particle((float)lent.getX(), MathUtility.random((float)(lent.getY() + (double)lent.getHeight()), (float)lent.getY()), (float)lent.getZ(), c, MathUtility.random(0.0f, 180.0f), MathUtility.random(10.0f, 60.0f), health - lastHealth));
            }
            return;
        }
        for (PlayerEntity player : HitParticles.mc.world.getPlayers()) {
            if (this.onlySelf.getValue().booleanValue() && player != HitParticles.mc.player || player.hurtTime <= 0) continue;
            Color c = this.colorMode.getValue() == ColorMode.Sync ? HudEditor.getColor((int)MathUtility.random(1.0f, 228.0f)) : this.colorrr.getValue().getColorObject();
            for (int i = 0; i < this.amount.getValue(); ++i) {
                this.particles.add(new Particle((float)player.getX(), MathUtility.random((float)(player.getY() + (double)player.getHeight()), (float)player.getY()), (float)player.getZ(), c, MathUtility.random(0.0f, 180.0f), MathUtility.random(10.0f, 60.0f), 0.0f));
            }
        }
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        RenderSystem.disableDepthTest();
        if (HitParticles.mc.player != null && HitParticles.mc.world != null) {
            for (Particle particle : this.particles) {
                particle.render(stack);
            }
        }
        RenderSystem.enableDepthTest();
    }

    public static enum Mode {
        Orbiz,
        Stars,
        Hearts,
        Bloom,
        Text;

    }

    public static enum Physics {
        Fall,
        Fly;

    }

    public static enum ColorMode {
        Custom,
        Sync;

    }

    public class Particle {
        float x;
        float y;
        float z;
        float px;
        float py;
        float pz;
        float motionX;
        float motionY;
        float motionZ;
        float rotationAngle;
        float rotationSpeed;
        float health;
        long time;
        Color color;

        public Particle(float x, float y, float z, Color color, float rotationAngle, float rotationSpeed, float health) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.px = x;
            this.py = y;
            this.pz = z;
            this.motionX = MathUtility.random(-((float)HitParticles.this.speed.getValue().intValue()) / 50.0f, (float)HitParticles.this.speed.getValue().intValue() / 50.0f);
            this.motionY = MathUtility.random(-((float)HitParticles.this.speed.getValue().intValue()) / 50.0f, (float)HitParticles.this.speed.getValue().intValue() / 50.0f);
            this.motionZ = MathUtility.random(-((float)HitParticles.this.speed.getValue().intValue()) / 50.0f, (float)HitParticles.this.speed.getValue().intValue() / 50.0f);
            this.time = System.currentTimeMillis();
            this.color = color;
            this.rotationAngle = rotationAngle;
            this.rotationSpeed = rotationSpeed;
            this.health = health;
        }

        public long getTime() {
            return this.time;
        }

        public boolean update() {
            double sp = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.px = this.x;
            this.py = this.y;
            this.pz = this.z;
            this.x += this.motionX;
            this.y += this.motionY;
            this.z += this.motionZ;
            if (this.posBlock(this.x, this.y - HitParticles.this.starsScale.getValue().floatValue() / 10.0f, this.z)) {
                this.motionY = -this.motionY / 1.1f;
                this.motionX /= 1.1f;
                this.motionZ /= 1.1f;
            } else if (this.posBlock((double)this.x - sp, this.y, (double)this.z - sp) || this.posBlock((double)this.x + sp, this.y, (double)this.z + sp) || this.posBlock((double)this.x + sp, this.y, (double)this.z - sp) || this.posBlock((double)this.x - sp, this.y, (double)this.z + sp) || this.posBlock((double)this.x + sp, this.y, this.z) || this.posBlock((double)this.x - sp, this.y, this.z) || this.posBlock(this.x, this.y, (double)this.z + sp) || this.posBlock(this.x, this.y, (double)this.z - sp)) {
                this.motionX = -this.motionX;
                this.motionZ = -this.motionZ;
            }
            if (HitParticles.this.physics.getValue() == Physics.Fall) {
                this.motionY -= 0.035f;
            }
            this.motionX /= 1.005f;
            this.motionZ /= 1.005f;
            this.motionY /= 1.005f;
            return System.currentTimeMillis() - this.getTime() > (long)(HitParticles.this.lifeTime.getValue() * 1000);
        }

        public void render(MatrixStack matrixStack) {
            float size = HitParticles.this.starsScale.getValue().floatValue();
            float scale = HitParticles.this.mode.is(Mode.Text) ? 0.025f * size : 0.07f;
            double posX = Render2DEngine.interpolate(this.px, this.x, Render3DEngine.getTickDelta()) - Module.mc.getEntityRenderDispatcher().camera.getPos().getX();
            double posY = Render2DEngine.interpolate(this.py, this.y, Render3DEngine.getTickDelta()) + 0.1 - Module.mc.getEntityRenderDispatcher().camera.getPos().getY();
            double posZ = Render2DEngine.interpolate(this.pz, this.z, Render3DEngine.getTickDelta()) - Module.mc.getEntityRenderDispatcher().camera.getPos().getZ();
            matrixStack.push();
            matrixStack.translate(posX, posY, posZ);
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(size / 2.0f, size / 2.0f, size / 2.0f);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-Module.mc.gameRenderer.getCamera().getYaw()));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Module.mc.gameRenderer.getCamera().getPitch()));
            if (HitParticles.this.mode.is(Mode.Text)) {
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            } else {
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.rotationAngle += AnimationUtility.deltaTime() * this.rotationSpeed));
            }
            matrixStack.translate(-size / 2.0f, -size / 2.0f, -size / 2.0f);
            switch (HitParticles.this.mode.getValue().ordinal()) {
                case 0: {
                    Render2DEngine.drawOrbiz(matrixStack, 0.0f, 0.3, this.color);
                    Render2DEngine.drawOrbiz(matrixStack, -0.1f, 0.5, this.color);
                    Render2DEngine.drawOrbiz(matrixStack, -0.2f, 0.7, this.color);
                    break;
                }
                case 1: {
                    Render2DEngine.drawStar(matrixStack, this.color, size);
                    break;
                }
                case 2: {
                    Render2DEngine.drawHeart(matrixStack, this.color, size);
                    break;
                }
                case 3: {
                    Render2DEngine.drawBloom(matrixStack, this.color, size);
                    break;
                }
                case 4: {
                    FontRenderers.sf_medium.drawCenteredString(matrixStack, MathUtility.round2(this.health) + " ", 0.0, 0.0, (this.health > 0.0f ? HitParticles.this.colorH.getValue() : HitParticles.this.colorD.getValue()).getColorObject());
                }
            }
            matrixStack.scale(0.8f, 0.8f, 0.8f);
            matrixStack.pop();
        }

        private boolean posBlock(double x, double y, double z) {
            Block b = Module.mc.world.getBlockState(BlockPos.ofFloored((double)x, (double)y, (double)z)).getBlock();
            return !(b instanceof AirBlock) && b != Blocks.WATER && b != Blocks.LAVA;
        }
    }
}

