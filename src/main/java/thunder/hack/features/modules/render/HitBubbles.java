/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.RotationAxis
 */
package thunder.hack.features.modules.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventAttack;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.IClientPlayerEntity;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;
import thunder.hack.utility.render.Render2DEngine;

public class HitBubbles
extends Module {
    public final Setting<Integer> lifeTime = new Setting<Integer>("LifeTime", 30, 1, 150);
    private final ArrayList<HitBubble> bubbles = new ArrayList();

    public HitBubbles() {
        super("HitBubbles", Module.Category.RENDER);
    }

    @EventHandler
    public void onHit(EventAttack e) {
        Vec3d point = Managers.PLAYER.getRtxPoint(((IClientPlayerEntity)HitBubbles.mc.player).getLastYaw(), ((IClientPlayerEntity)HitBubbles.mc.player).getLastPitch(), ModuleManager.aura.attackRange.getValue().floatValue());
        if (point != null && !e.isPre()) {
            this.bubbles.add(new HitBubble((float)point.x, (float)point.y, (float)point.z, -((IClientPlayerEntity)HitBubbles.mc.player).getLastYaw(), ((IClientPlayerEntity)HitBubbles.mc.player).getLastPitch(), new Timer()));
        }
    }

    @Override
    public void onRender3D(MatrixStack matrixStack) {
        RenderSystem.disableDepthTest();
        ArrayList bubblesCopy = Lists.newArrayList(this.bubbles);
        bubblesCopy.forEach(b -> {
            matrixStack.push();
            matrixStack.translate((double)b.x - HitBubbles.mc.getEntityRenderDispatcher().camera.getPos().getX(), (double)b.y - HitBubbles.mc.getEntityRenderDispatcher().camera.getPos().getY(), (double)b.z - HitBubbles.mc.getEntityRenderDispatcher().camera.getPos().getZ());
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(b.yaw));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(b.pitch));
            Render2DEngine.drawBubble(matrixStack, (float)(-b.life.getPassedTimeMs()) / 4.0f, (float)b.life.getPassedTimeMs() / 1500.0f);
            matrixStack.pop();
        });
        RenderSystem.enableDepthTest();
        this.bubbles.removeIf(b -> b.life.passedMs(this.lifeTime.getValue() * 50));
    }

    public record HitBubble(float x, float y, float z, float yaw, float pitch, Timer life) {
    }
}

