/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.util.math.MathHelper
 */
package thunder.hack.utility.player;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.EventMove;
import thunder.hack.features.modules.Module;

public final class MovementUtility {
    public static boolean isMoving() {
        return Module.mc.player != null && Module.mc.world != null && Module.mc.player.input != null && ((double)Module.mc.player.input.movementForward != 0.0 || (double)Module.mc.player.input.movementSideways != 0.0);
    }

    public static double getSpeed() {
        return Math.hypot(Module.mc.player.getVelocity().x, Module.mc.player.getVelocity().z);
    }

    public static double[] forward(double d) {
        float f = Module.mc.player.input.movementForward;
        float f2 = Module.mc.player.input.movementSideways;
        float f3 = Module.mc.player.getYaw();
        if (f != 0.0f) {
            if (f2 > 0.0f) {
                f3 += (float)(f > 0.0f ? -45 : 45);
            } else if (f2 < 0.0f) {
                f3 += (float)(f > 0.0f ? 45 : -45);
            }
            f2 = 0.0f;
            if (f > 0.0f) {
                f = 1.0f;
            } else if (f < 0.0f) {
                f = -1.0f;
            }
        }
        double d2 = Math.sin(Math.toRadians(f3 + 90.0f));
        double d3 = Math.cos(Math.toRadians(f3 + 90.0f));
        double d4 = (double)f * d * d3 + (double)f2 * d * d2;
        double d5 = (double)f * d * d2 - (double)f2 * d * d3;
        return new double[]{d4, d5};
    }

    public static void setMotion(double speed) {
        double forward = Module.mc.player.input.movementForward;
        double strafe = Module.mc.player.input.movementSideways;
        float yaw = Module.mc.player.getYaw();
        if (forward == 0.0 && strafe == 0.0) {
            Module.mc.player.setVelocity(0.0, Module.mc.player.getVelocity().y, 0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            double sin = MathHelper.sin((float)((float)Math.toRadians(yaw + 90.0f)));
            double cos = MathHelper.cos((float)((float)Math.toRadians(yaw + 90.0f)));
            Module.mc.player.setVelocity(forward * speed * cos + strafe * speed * sin, Module.mc.player.getVelocity().y, forward * speed * sin - strafe * speed * cos);
        }
    }

    public static float getMoveDirection() {
        double forward = Module.mc.player.input.movementForward;
        double strafe = Module.mc.player.input.movementSideways;
        if (strafe > 0.0) {
            strafe = 1.0;
        } else if (strafe < 0.0) {
            strafe = -1.0;
        }
        float yaw = Module.mc.player.getYaw();
        if (forward == 0.0 && strafe == 0.0) {
            return yaw;
        }
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += forward > 0.0 ? -45.0f : -135.0f;
            } else if (strafe < 0.0) {
                yaw += forward > 0.0 ? 45.0f : 135.0f;
            } else if (forward < 0.0) {
                yaw += 180.0f;
            }
        }
        if (forward == 0.0) {
            if (strafe > 0.0) {
                yaw -= 90.0f;
            } else if (strafe < 0.0) {
                yaw += 90.0f;
            }
        }
        return yaw;
    }

    public static double[] forwardWithoutStrafe(double d) {
        float f3 = Module.mc.player.getYaw();
        double d4 = d * Math.cos(Math.toRadians(f3 + 90.0f));
        double d5 = d * Math.sin(Math.toRadians(f3 + 90.0f));
        return new double[]{d4, d5};
    }

    public static double getJumpSpeed() {
        double jumpSpeed = 0.3999999463558197;
        if (Module.mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            double amplifier = Module.mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
            jumpSpeed += (amplifier + 1.0) * 0.1;
        }
        return jumpSpeed;
    }

    public static void modifyEventSpeed(EventMove event, double d) {
        double d2 = Module.mc.player.input.movementForward;
        double d3 = Module.mc.player.input.movementSideways;
        float f = Module.mc.player.getYaw();
        if (d2 == 0.0 && d3 == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (d2 != 0.0) {
                if (d3 > 0.0) {
                    f += (float)(d2 > 0.0 ? -45 : 45);
                } else if (d3 < 0.0) {
                    f += (float)(d2 > 0.0 ? 45 : -45);
                }
                d3 = 0.0;
                if (d2 > 0.0) {
                    d2 = 1.0;
                } else if (d2 < 0.0) {
                    d2 = -1.0;
                }
            }
            double sin = Math.sin(Math.toRadians(f + 90.0f));
            double cos = Math.cos(Math.toRadians(f + 90.0f));
            event.setX(d2 * d * cos + d3 * d * sin);
            event.setZ(d2 * d * sin - d3 * d * cos);
        }
    }

    public static double getBaseMoveSpeed() {
        int n;
        double d = 0.2873;
        if (Module.fullNullCheck()) {
            return d;
        }
        if (Module.mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            n = Module.mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
            d *= 1.0 + 0.2 * (double)(n + 1);
        }
        if (Module.mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            n = Module.mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
            d /= 1.0 + 0.2 * (double)(n + 1);
        }
        if (Module.mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
            n = Module.mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
            d /= 1.0 + 0.2 * (double)(n + 1);
        }
        return d;
    }

    public static boolean sprintIsLegit(float yaw) {
        return Math.abs(Math.abs(MathHelper.wrapDegrees((float)yaw)) - Math.abs(MathHelper.wrapDegrees((float)Managers.PLAYER.yaw))) < 40.0f;
    }
}

