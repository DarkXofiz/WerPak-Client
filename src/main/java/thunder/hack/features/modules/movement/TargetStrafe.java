/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 */
package thunder.hack.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import thunder.hack.events.impl.EventMove;
import thunder.hack.events.impl.EventSync;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.player.MovementUtility;

public class TargetStrafe
extends Module {
    public final Setting<Float> range = new Setting<Float>("Range", Float.valueOf(3.0f), Float.valueOf(0.1f), Float.valueOf(10.0f));
    public final Setting<Boolean> autoJump = new Setting<Boolean>("AutoJump", true);
    public final Setting<Boolean> autoTarget = new Setting<Boolean>("AutoTarget", true);
    private PlayerEntity target = null;
    private int direction = 1;

    public TargetStrafe() {
        super("TargetStrafe", Module.Category.MOVEMENT);
    }

    @Override
    public void onDisable() {
        this.target = null;
    }

    @EventHandler
    public void onSync(EventSync e) {
        if (TargetStrafe.mc.player == null || TargetStrafe.mc.world == null) {
            return;
        }
        this.target = null;
        for (PlayerEntity player : TargetStrafe.mc.world.getPlayers()) {
            if (player == TargetStrafe.mc.player || !player.isAlive() || TargetStrafe.mc.player.distanceTo((Entity)player) > 15.0f || this.target != null && !(TargetStrafe.mc.player.distanceTo((Entity)player) < TargetStrafe.mc.player.distanceTo((Entity)this.target))) continue;
            this.target = player;
        }
    }

    @EventHandler
    public void onUpdate(PlayerUpdateEvent e) {
        if (TargetStrafe.mc.player == null || this.target == null) {
            return;
        }
        if (TargetStrafe.mc.player.horizontalCollision) {
            this.direction = -this.direction;
        }
        if (this.autoJump.getValue().booleanValue() && TargetStrafe.mc.player.isOnGround() && TargetStrafe.mc.player.distanceTo((Entity)this.target) <= this.range.getValue().floatValue()) {
            TargetStrafe.mc.player.jump();
        }
    }

    @EventHandler
    public void onMove(EventMove e) {
        if (TargetStrafe.mc.player == null || this.target == null) {
            return;
        }
        double distance = TargetStrafe.mc.player.distanceTo((Entity)this.target);
        if (this.autoTarget.getValue().booleanValue() || distance <= (double)this.range.getValue().floatValue()) {
            float moveForward = 1.0f;
            float moveStrafe = this.direction;
            if (distance <= (double)this.range.getValue().floatValue()) {
                moveForward = 0.0f;
            }
            float yaw = this.getRotations((Entity)this.target)[0];
            double currentSpeed = Math.max(MovementUtility.getSpeed(), MovementUtility.getBaseMoveSpeed());
            double[] dir = this.getDirection(yaw, currentSpeed, moveForward, moveStrafe);
            e.setX(dir[0]);
            e.setZ(dir[1]);
            e.cancel();
            TargetStrafe.mc.player.setVelocity(e.getX(), TargetStrafe.mc.player.getVelocity().y, e.getZ());
        }
    }

    private double[] getDirection(float yaw, double speed, float moveForward, float moveStrafe) {
        if (moveForward == 0.0f && moveStrafe == 0.0f) {
            return new double[]{0.0, 0.0};
        }
        if (moveForward != 0.0f) {
            if (moveStrafe >= 1.0f) {
                yaw += (float)(moveForward > 0.0f ? -45 : 45);
                moveStrafe = 0.0f;
            } else if (moveStrafe <= -1.0f) {
                yaw += (float)(moveForward > 0.0f ? 45 : -45);
                moveStrafe = 0.0f;
            }
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        return new double[]{(double)moveForward * speed * cos + (double)moveStrafe * speed * sin, (double)moveForward * speed * sin - (double)moveStrafe * speed * cos};
    }

    private float[] getRotations(Entity entity) {
        double dx = entity.getX() - TargetStrafe.mc.player.getX();
        double dz = entity.getZ() - TargetStrafe.mc.player.getZ();
        double dy = entity.getY() + (double)entity.getStandingEyeHeight() - (TargetStrafe.mc.player.getY() + (double)TargetStrafe.mc.player.getStandingEyeHeight());
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(dy, dist)));
        return new float[]{yaw, pitch};
    }
}

