/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Matrix4f
 */
package thunder.hack.features.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.TextureStorage;

public class Particles
extends Module {
    private final Setting<BooleanSettingGroup> FireFlies = new Setting<BooleanSettingGroup>("FireFlies", new BooleanSettingGroup(true));
    private final Setting<Integer> ffcount = new Setting<Integer>("FFCount", 30, 20, 200).addToGroup(this.FireFlies);
    private final Setting<Float> ffsize = new Setting<Float>("FFSize", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(2.0f)).addToGroup(this.FireFlies);
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.SnowFlake);
    private final Setting<Integer> count = new Setting<Integer>("Count", 100, 20, 800);
    private final Setting<Float> size = new Setting<Float>("Size", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(6.0f));
    private final Setting<ColorMode> lmode = new Setting<ColorMode>("ColorMode", ColorMode.Sync);
    private final Setting<ColorSetting> color = new Setting<ColorSetting>("Color", new ColorSetting(3649978), v -> this.lmode.getValue() == ColorMode.Custom);
    private final Setting<Physics> physics = new Setting<Physics>("Physics", Physics.Fly, v -> this.mode.getValue() != Mode.Off);
    private final ArrayList<ParticleBase> fireFlies = new ArrayList();
    private final ArrayList<ParticleBase> particles = new ArrayList();

    public Particles() {
        super("Particles", Module.Category.RENDER);
    }

    @Override
    public void onUpdate() {
        this.fireFlies.removeIf(ParticleBase::tick);
        this.particles.removeIf(ParticleBase::tick);
        for (int i = this.fireFlies.size(); i < this.ffcount.getValue(); ++i) {
            if (!this.FireFlies.getValue().isEnabled()) continue;
            this.fireFlies.add(new FireFly(this, (float)(Particles.mc.player.getX() + (double)MathUtility.random(-25.0f, 25.0f)), (float)(Particles.mc.player.getY() + (double)MathUtility.random(2.0f, 15.0f)), (float)(Particles.mc.player.getZ() + (double)MathUtility.random(-25.0f, 25.0f)), MathUtility.random(-0.2f, 0.2f), MathUtility.random(-0.1f, 0.1f), MathUtility.random(-0.2f, 0.2f)));
        }
        for (int j = this.particles.size(); j < this.count.getValue(); ++j) {
            boolean drop;
            boolean bl = drop = this.physics.getValue() == Physics.Drop;
            if (this.mode.getValue() == Mode.Off) continue;
            this.particles.add(new ParticleBase((float)(Particles.mc.player.getX() + (double)MathUtility.random(-48.0f, 48.0f)), (float)(Particles.mc.player.getY() + (double)MathUtility.random(2.0f, 48.0f)), (float)(Particles.mc.player.getZ() + (double)MathUtility.random(-48.0f, 48.0f)), drop ? 0.0f : MathUtility.random(-0.4f, 0.4f), drop ? MathUtility.random(-0.2f, -0.05f) : MathUtility.random(-0.1f, 0.1f), drop ? 0.0f : MathUtility.random(-0.4f, 0.4f)));
        }
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        BufferBuilder bufferBuilder;
        if (this.FireFlies.getValue().isEnabled()) {
            stack.push();
            RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.firefly);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask((boolean)false);
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            final BufferBuilder bbF = bufferBuilder;
            this.fireFlies.forEach(p -> p.render(bbF));
            Render2DEngine.endBuilding(bufferBuilder);
            RenderSystem.depthMask((boolean)true);
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            stack.pop();
        }
        if (this.mode.getValue() != Mode.Off) {
            stack.push();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask((boolean)false);
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            final BufferBuilder bbP = bufferBuilder;
            this.particles.forEach(p -> p.render(bbP));
            Render2DEngine.endBuilding(bufferBuilder);
            RenderSystem.depthMask((boolean)true);
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            stack.pop();
        }
    }

    public static enum Mode {
        Off,
        SnowFlake,
        Stars,
        Hearts,
        Dollars,
        Bloom;

    }

    public static enum ColorMode {
        Custom,
        Sync;

    }

    public static enum Physics {
        Drop,
        Fly;

    }

    public class FireFly
    extends ParticleBase {
        public FireFly(Particles this$0, float posX, float posY, float posZ, float motionX, float motionY, float motionZ) {
            super(posX, posY, posZ, motionX, motionY, motionZ);
        }

        @Override
        public boolean tick() {
            this.age = Module.mc.player.squaredDistanceTo((double)this.posX, (double)this.posY, (double)this.posZ) > 100.0 ? (this.age -= 4) : (!Module.mc.world.getBlockState(new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ)).isAir() ? (this.age -= 8) : --this.age);
            if (this.age < 0) {
                return true;
            }
            this.prevposX = this.posX;
            this.prevposY = this.posY;
            this.prevposZ = this.posZ;
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.motionX *= 0.99f;
            this.motionY *= 0.99f;
            this.motionZ *= 0.99f;
            return false;
        }
    }

    public class ParticleBase {
        protected float prevposX;
        protected float prevposY;
        protected float prevposZ;
        protected float posX;
        protected float posY;
        protected float posZ;
        protected float motionX;
        protected float motionY;
        protected float motionZ;
        protected int age;
        protected int maxAge;

        public ParticleBase(float posX, float posY, float posZ, float motionX, float motionY, float motionZ) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.prevposX = posX;
            this.prevposY = posY;
            this.prevposZ = posZ;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.maxAge = this.age = (int)MathUtility.random(100.0f, 300.0f);
        }

        public boolean tick() {
            this.age = Module.mc.player.squaredDistanceTo((double)this.posX, (double)this.posY, (double)this.posZ) > 4096.0 ? (this.age -= 8) : --this.age;
            if (this.age < 0) {
                return true;
            }
            this.prevposX = this.posX;
            this.prevposY = this.posY;
            this.prevposZ = this.posZ;
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.motionX *= 0.9f;
            if (Particles.this.physics.getValue() == Physics.Fly) {
                this.motionY *= 0.9f;
            }
            this.motionZ *= 0.9f;
            this.motionY -= 0.001f;
            return false;
        }

        public void render(BufferBuilder bufferBuilder) {
            switch (Particles.this.mode.getValue().ordinal()) {
                case 5: {
                    RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.firefly);
                    break;
                }
                case 1: {
                    RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.snowflake);
                    break;
                }
                case 4: {
                    RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.dollar);
                    break;
                }
                case 3: {
                    RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.heart);
                    break;
                }
                case 2: {
                    RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.star);
                }
            }
            Camera camera = Module.mc.gameRenderer.getCamera();
            Color color1 = Particles.this.lmode.getValue() == ColorMode.Sync ? HudEditor.getColor(this.age * 2) : Particles.this.color.getValue().getColorObject();
            Vec3d pos = Render3DEngine.interpolatePos(this.prevposX, this.prevposY, this.prevposZ, this.posX, this.posY, this.posZ);
            MatrixStack matrices = new MatrixStack();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
            matrices.translate(pos.x, pos.y, pos.z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            Matrix4f matrix1 = matrices.peek().getPositionMatrix();
            bufferBuilder.vertex(matrix1, 0.0f, -Particles.this.size.getValue().floatValue(), 0.0f).texture(0.0f, 1.0f).color(Render2DEngine.injectAlpha(color1, (int)(255.0f * ((float)this.age / (float)this.maxAge))).getRGB());
            bufferBuilder.vertex(matrix1, -Particles.this.size.getValue().floatValue(), -Particles.this.size.getValue().floatValue(), 0.0f).texture(1.0f, 1.0f).color(Render2DEngine.injectAlpha(color1, (int)(255.0f * ((float)this.age / (float)this.maxAge))).getRGB());
            bufferBuilder.vertex(matrix1, -Particles.this.size.getValue().floatValue(), 0.0f, 0.0f).texture(1.0f, 0.0f).color(Render2DEngine.injectAlpha(color1, (int)(255.0f * ((float)this.age / (float)this.maxAge))).getRGB());
            bufferBuilder.vertex(matrix1, 0.0f, 0.0f, 0.0f).texture(0.0f, 0.0f).color(Render2DEngine.injectAlpha(color1, (int)(255.0f * ((float)this.age / (float)this.maxAge))).getRGB());
        }
    }
}

