/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.MathHelper
 */
package thunder.hack.features.modules.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class NoWeb
extends Module {
    public static final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.WerPak);
    private final Setting<Float> legitFactor = new Setting<Float>("LegitFactor", Float.valueOf(0.55f), Float.valueOf(0.1f), Float.valueOf(0.99f), v -> mode.getValue() == Mode.Legit);
    private final Setting<Float> speed = new Setting<Float>("Speed", Float.valueOf(0.633f), Float.valueOf(0.1f), Float.valueOf(2.0f), v -> mode.getValue() == Mode.WerPak);
    private final Setting<Float> motionY = new Setting<Float>("MotionY", Float.valueOf(1.3f), Float.valueOf(0.1f), Float.valueOf(3.0f), v -> mode.getValue() == Mode.WerPak);

    public static boolean isNoWebEnabled() {
        return ModuleManager.noWeb != null && ModuleManager.noWeb.isEnabled();
    }

    public static Mode getNoWebMode() {
        if (ModuleManager.noWeb == null) {
            return Mode.Cancel;
        }
        return mode.getValue();
    }

    public static float getLegitFactor() {
        if (ModuleManager.noWeb == null) {
            return 0.55f;
        }
        return ModuleManager.noWeb.legitFactor.getValue().floatValue();
    }

    public NoWeb() {
        super("NoWeb", Module.Category.PLAYER);
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (NoWeb.mc.player == null || NoWeb.mc.world == null) {
            return;
        }
        if (mode.getValue() == Mode.Spoof) {
            if (this.isInWebLocal()) {
                Vec3d vel = NoWeb.mc.player.getVelocity();
                double newX = vel.x * 1.85;
                double newZ = vel.z * 1.85;
                NoWeb.mc.player.setVelocity(newX, vel.y, newZ);
            }
        } else if (mode.getValue() == Mode.WerPak && this.isInWebLocal()) {
            double mY = NoWeb.mc.options.jumpKey.isPressed() ? (double)this.motionY.getValue().floatValue() : (NoWeb.mc.options.sneakKey.isPressed() ? (double)(-this.motionY.getValue().floatValue()) : 0.0);
            float yaw = NoWeb.mc.player.getYaw() * ((float)Math.PI / 180);
            float moveForward = NoWeb.mc.player.input != null ? NoWeb.mc.player.input.movementForward : NoWeb.mc.player.forwardSpeed;
            float moveSideways = NoWeb.mc.player.input != null ? NoWeb.mc.player.input.movementSideways : NoWeb.mc.player.sidewaysSpeed;
            float f = moveForward * this.speed.getValue().floatValue();
            float s = moveSideways * this.speed.getValue().floatValue();
            if (f != 0.0f || s != 0.0f) {
                NoWeb.mc.player.setVelocity((double)(-MathHelper.sin((float)yaw) * f + MathHelper.cos((float)yaw) * s), mY, (double)(MathHelper.cos((float)yaw) * f + MathHelper.sin((float)yaw) * s));
            } else {
                NoWeb.mc.player.setVelocity(0.0, mY, 0.0);
            }
        }
    }

    public boolean isInWebLocal() {
        if (NoWeb.mc.player == null || NoWeb.mc.world == null) {
            return false;
        }
        Box pBox = NoWeb.mc.player.getBoundingBox();
        BlockPos pBlockPos = BlockPos.ofFloored(NoWeb.mc.player.getPos());
        for (int x = pBlockPos.getX() - 1; x <= pBlockPos.getX() + 1; ++x) {
            for (int y = pBlockPos.getY() - 1; y <= pBlockPos.getY() + 2; ++y) {
                for (int z = pBlockPos.getZ() - 1; z <= pBlockPos.getZ() + 1; ++z) {
                    BlockPos bp = new BlockPos(x, y, z);
                    if (!pBox.intersects(new Box(bp)) || NoWeb.mc.world.getBlockState(bp).getBlock() != Blocks.COBWEB) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public static enum Mode {
        Cancel,
        Spoof,
        Legit,
        WerPak;

    }
}

