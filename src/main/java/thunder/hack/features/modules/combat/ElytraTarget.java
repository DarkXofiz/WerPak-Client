/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Items
 *  net.minecraft.world.World
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.features.modules.combat;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.interfaces.IEntityLiving;
import thunder.hack.utility.render.Render3DEngine;

public class ElytraTarget
extends Module {
    public final Setting<Boolean> drawBox = new Setting<Boolean>("Draw Box", true);
    public final Setting<ColorSetting> boxColor = new Setting<ColorSetting>("Box Color", new ColorSetting(new Color(0, 0, 255, 120).getRGB()));
    public final Setting<Float> overshoot = new Setting<Float>("Overshoot", Float.valueOf(2.5f), Float.valueOf(0.0f), Float.valueOf(6.0f));
    public final Setting<Float> distance = new Setting<Float>("Distance", Float.valueOf(100.0f), Float.valueOf(10.0f), Float.valueOf(400.0f));
    public final Setting<Boolean> predict = new Setting<Boolean>("Predict", true);
    public final Setting<Integer> predictTicks = new Setting<Integer>("Predict Ticks", 10, 1, 50);
    public final Setting<Integer> predictLength = new Setting<Integer>("Predict Length", 2, 1, 20);
    public final Setting<Float> driveBoost = new Setting<Float>("Drive Boost", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f));
    public final Setting<Boolean> velocityAssist = new Setting<Boolean>("Rotation Steer", true);
    public final Setting<Boolean> legacyVelocity = new Setting<Boolean>("Legacy Velocity", false);
    public final Setting<Boolean> antiCheat = new Setting<Boolean>("AntiCheat Bypass", true);
    public final Setting<Float> maxTurn = new Setting<Float>("Max Turn", Float.valueOf(18.0f), Float.valueOf(1.0f), Float.valueOf(90.0f), v -> this.antiCheat.getValue());
    public final Setting<Float> maxSpeed = new Setting<Float>("Max Speed", Float.valueOf(1.9f), Float.valueOf(0.5f), Float.valueOf(4.0f), v -> this.antiCheat.getValue());
    public final Setting<Float> jitter = new Setting<Float>("Jitter", Float.valueOf(0.02f), Float.valueOf(0.0f), Float.valueOf(0.1f), v -> this.antiCheat.getValue());
    public static PlayerEntity target = null;
    public static Vec3d predictedPos = null;
    private World lastWorld;
    private double oscillationAngle = 0.0;
    private boolean overshot = false;
    private int predictTimer = 0;
    private double currentOverrun = 0.0;

    public ElytraTarget() {
        super("ElytraTarget", Module.Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        target = null;
        predictedPos = null;
        this.overshot = false;
        this.oscillationAngle = 0.0;
        this.predictTimer = 0;
        this.currentOverrun = 0.0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        target = null;
        predictedPos = null;
        this.overshot = false;
        this.oscillationAngle = 0.0;
        this.predictTimer = 0;
        this.currentOverrun = 0.0;
    }

    @EventHandler
    public void onTick(EventTick event) {
        if (ElytraTarget.mc.player != null && ElytraTarget.mc.world != null) {
            if (ElytraTarget.mc.world != this.lastWorld) {
                target = null;
                this.lastWorld = ElytraTarget.mc.world;
            }
            if ((target = this.findTarget()) == null) {
                predictedPos = null;
                this.overshot = false;
                this.predictTimer = 0;
            } else if (this.predict.getValue().booleanValue()) {
                boolean isFlying;
                ++this.predictTimer;
                if (this.predictTimer >= this.predictLength.getValue() || predictedPos == null) {
                    this.predictTimer = 0;
                    this.currentOverrun = this.calculateOverrun(this.overshoot.getValue().floatValue(), this.driveBoost.getValue().floatValue());
                    Vec3d targetPos = target.getPos();
                    Vec3d velocity = Vec3d.ZERO;
                    PlayerEntity class_16572 = target;
                    if (class_16572 instanceof IEntityLiving) {
                        IEntityLiving el = (IEntityLiving)class_16572;
                        Vec3d from = new Vec3d(el.getPrevServerX(), el.getPrevServerY(), el.getPrevServerZ());
                        velocity = targetPos.subtract(from);
                    }
                    Vec3d targetFacing = new Vec3d(-Math.sin(Math.toRadians(target.getYaw())), 0.0, Math.cos(Math.toRadians(target.getYaw()))).normalize();
                    double totalDist = (double)this.predictTicks.getValue().intValue() + this.currentOverrun;
                    Vec3d movePred = targetPos.add(velocity.multiply(totalDist));
                    predictedPos = movePred.subtract(targetFacing.multiply(this.currentOverrun)).add(0.0, 0.2, 0.0);
                }
                boolean bl = isFlying = ElytraTarget.mc.player.isFallFlying() || ElytraTarget.mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA) && !ElytraTarget.mc.player.isOnGround();
                if (isFlying && this.velocityAssist.getValue().booleanValue() && predictedPos != null) {
                    Vec3d steer = predictedPos.subtract(ElytraTarget.mc.player.getEyePos());
                    double flat = Math.sqrt(steer.x * steer.x + steer.z * steer.z);
                    float wantYaw = (float)(Math.toDegrees(Math.atan2(steer.z, steer.x)) - 90.0);
                    float wantPitch = (float)(-Math.toDegrees(Math.atan2(steer.y, flat)));
                    float lim = this.maxTurn.getValue().floatValue();
                    float jit = this.jitter.getValue().floatValue() * 20.0f;
                    float dYaw = MathHelper.clamp((float)MathHelper.wrapDegrees((float)(wantYaw - ElytraTarget.mc.player.getYaw())), (float)(-lim), (float)lim);
                    float dPitch = MathHelper.clamp((float)(wantPitch - ElytraTarget.mc.player.getPitch()), (float)(-lim * 0.6f), (float)(lim * 0.6f));
                    dYaw += (float)((Math.random() - 0.5) * (double)jit);
                    dPitch += (float)((Math.random() - 0.5) * (double)jit);
                    ElytraTarget.mc.player.setYaw(ElytraTarget.mc.player.getYaw() + dYaw);
                    ElytraTarget.mc.player.setPitch(MathHelper.clamp((float)(ElytraTarget.mc.player.getPitch() + dPitch), (float)-89.0f, (float)89.0f));
                }
                if (isFlying && this.legacyVelocity.getValue().booleanValue()) {
                    Vec3d dir = predictedPos.subtract(ElytraTarget.mc.player.getPos());
                    Vec3d currentVel = ElytraTarget.mc.player.getVelocity();
                    double speed = currentVel.length();
                    if (speed < 0.1) {
                        speed = 1.0;
                    }
                    if (dir.lengthSquared() > 0.5) {
                        Vec3d dirNorm = dir.normalize();
                        double blend = Math.min(1.0, dir.length() / 3.0);
                        Vec3d blendedVel = currentVel.multiply(1.0 - blend).add(dirNorm.multiply(speed * blend));
                        if (this.antiCheat.getValue().booleanValue() && currentVel.lengthSquared() > 0.0025 && blendedVel.lengthSquared() > 1.0E-6) {
                            double curYaw = Math.atan2(currentVel.z, currentVel.x);
                            double newYaw = Math.atan2(blendedVel.z, blendedVel.x);
                            double diff = MathHelper.wrapDegrees((float)Math.toDegrees(newYaw - curYaw));
                            double lim = this.maxTurn.getValue().doubleValue();
                            double clamped = Math.max(-lim, Math.min(lim, diff));
                            double horiz = Math.sqrt(blendedVel.x * blendedVel.x + blendedVel.z * blendedVel.z);
                            double outYaw = curYaw + Math.toRadians(clamped);
                            double vy = blendedVel.y;
                            double jit = this.jitter.getValue().doubleValue();
                            double nx = Math.cos(outYaw) * horiz + (Math.random() - 0.5) * jit;
                            double nz = Math.sin(outYaw) * horiz + (Math.random() - 0.5) * jit;
                            double ny = vy + (Math.random() - 0.5) * jit;
                            Vec3d limited = new Vec3d(nx, ny, nz);
                            double cap = this.maxSpeed.getValue().doubleValue();
                            if (limited.length() > cap) {
                                limited = limited.normalize().multiply(cap);
                            }
                            blendedVel = limited;
                        }
                        ElytraTarget.mc.player.setVelocity(blendedVel);
                    }
                }
            } else {
                predictedPos = this.getFrontPosition((Entity)target, this.overshoot.getValue().doubleValue());
                this.oscillationAngle = 0.0;
                this.overshot = false;
            }
        } else {
            target = null;
            predictedPos = null;
        }
    }

    private PlayerEntity findTarget() {
        PlayerEntity bestTarget = null;
        float bestVal = -1.0f;
        for (PlayerEntity player : ElytraTarget.mc.world.getPlayers()) {
            float d;
            boolean isFlying;
            if (player == ElytraTarget.mc.player || !player.isAlive() || player.isSpectator() || player.getHealth() <= 0.0f || Managers.FRIEND.isFriend(player) || !(isFlying = player.isFallFlying() || player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA) && !player.isOnGround()) || (d = ElytraTarget.mc.player.distanceTo((Entity)player)) > this.distance.getValue().floatValue()) continue;
            float currentVal = 0.0f;
            Aura.Sort sortMode = ModuleManager.aura.sort.getValue();
            switch (sortMode) {
                case LowestDistance: {
                    currentVal = 1000.0f - d;
                    break;
                }
                case HighestDistance: {
                    currentVal = d;
                    break;
                }
                case LowestHealth: {
                    currentVal = 1000.0f - player.getHealth();
                    break;
                }
                case HighestHealth: {
                    currentVal = player.getHealth();
                    break;
                }
                case LowestDurability: {
                    currentVal = 1000.0f - (float)(player.getEquippedStack(EquipmentSlot.CHEST).getMaxDamage() - player.getEquippedStack(EquipmentSlot.CHEST).getDamage());
                    break;
                }
                case HighestDurability: {
                    currentVal = player.getEquippedStack(EquipmentSlot.CHEST).getMaxDamage() - player.getEquippedStack(EquipmentSlot.CHEST).getDamage();
                    break;
                }
                case FOV: {
                    double deltaX = player.getX() - ElytraTarget.mc.player.getX();
                    double deltaZ = player.getZ() - ElytraTarget.mc.player.getZ();
                    float yawDelta = Math.abs(MathHelper.wrapDegrees((float)((float)Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0f - ElytraTarget.mc.player.getYaw())));
                    currentVal = 180.0f - yawDelta;
                }
            }
            if (bestTarget != null && !(currentVal > bestVal)) continue;
            bestTarget = player;
            bestVal = currentVal;
        }
        return bestTarget;
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        if (target != null && this.drawBox.getValue().booleanValue()) {
            float tickDelta = mc.getRenderTickCounter().getTickDelta(false);
            double x = ElytraTarget.target.lastRenderX + (target.getX() - ElytraTarget.target.lastRenderX) * (double)tickDelta;
            double y = ElytraTarget.target.lastRenderY + (target.getY() - ElytraTarget.target.lastRenderY) * (double)tickDelta;
            double z = ElytraTarget.target.lastRenderZ + (target.getZ() - ElytraTarget.target.lastRenderZ) * (double)tickDelta;
            Vec3d interpolatedTargetPos = new Vec3d(x, y, z);
            if (this.predict.getValue().booleanValue()) {
                if (predictedPos != null) {
                    double s = 0.3;
                    Box box = new Box(ElytraTarget.predictedPos.x - s, ElytraTarget.predictedPos.y - s, ElytraTarget.predictedPos.z - s, ElytraTarget.predictedPos.x + s, ElytraTarget.predictedPos.y + s, ElytraTarget.predictedPos.z + s);
                    Color color = new Color(255, 255, 255, 120);
                    Render3DEngine.drawFilledBox(stack, box, color);
                    Render3DEngine.drawBoxOutline(box, new Color(255, 255, 255, 255), 2.0f);
                }
            } else {
                Vec3d renderPos;
                if (this.overshot) {
                    Vec3d toPlayer = ElytraTarget.mc.player.getCameraPosVec(tickDelta).subtract(interpolatedTargetPos).normalize();
                    double oscillationY = Math.sin(this.oscillationAngle) * 1.5;
                    renderPos = new Vec3d(interpolatedTargetPos.x + toPlayer.x * (double)this.overshoot.getValue().floatValue(), interpolatedTargetPos.y + 0.3 + oscillationY, interpolatedTargetPos.z + toPlayer.z * (double)this.overshoot.getValue().floatValue());
                } else {
                    renderPos = this.getFrontPosition((Entity)target, this.overshoot.getValue().doubleValue());
                }
                double s = 0.3;
                Box box = new Box(renderPos.x - s, renderPos.y - s, renderPos.z - s, renderPos.x + s, renderPos.y + s, renderPos.z + s);
                Color color = this.boxColor.getValue().getColorObject();
                Render3DEngine.drawFilledBox(stack, box, color);
                Render3DEngine.drawBoxOutline(box, color, 2.0f);
            }
        }
    }

    private Vec3d getFrontPosition(Entity target, double overshoot) {
        Vec3d targetPos = target.getPos();
        Vec3d targetFacing = new Vec3d(-Math.sin(Math.toRadians(target.getYaw())), 0.0, Math.cos(Math.toRadians(target.getYaw()))).normalize();
        if (target instanceof IEntityLiving) {
            IEntityLiving el = (IEntityLiving)target;
            Vec3d from = new Vec3d(el.getPrevServerX(), el.getPrevServerY(), el.getPrevServerZ());
            Vec3d to = new Vec3d(target.getX(), target.getY(), target.getZ());
            Vec3d velocity = to.subtract(from);
            if (velocity.lengthSquared() > 0.001) {
                Vec3d moveDir = velocity.normalize();
                Vec3d blended = moveDir.add(targetFacing.negate()).normalize();
                return to.add(blended.multiply(overshoot)).add(0.0, 0.2, 0.0);
            }
        }
        return targetPos.subtract(targetFacing.multiply(overshoot)).add(0.0, 0.2, 0.0);
    }

    private double calculateOverrun(float overshootVal, float driveBoostVal) {
        return overshootVal * driveBoostVal;
    }
}

