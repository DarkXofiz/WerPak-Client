/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.network.ClientPlayerEntity
 */
package thunder.hack.features.modules.movement;

import net.minecraft.util.math.Vec3d;
import net.minecraft.client.network.ClientPlayerEntity;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class ElytraBooster
extends Module {
    private static ElytraBooster INSTANCE = new ElytraBooster();
    public final Setting<Float> boost = new Setting<Float>("Boost", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(2.0f));
    private Vec3d capturedVelocity = null;

    public ElytraBooster() {
        super("ElytraBooster", Module.Category.MOVEMENT);
        INSTANCE = this;
    }

    public static ElytraBooster getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ElytraBooster();
        }
        return INSTANCE;
    }

    public void captureVelocity(ClientPlayerEntity player) {
        if (!this.isEnabled()) {
            return;
        }
        this.capturedVelocity = player.getVelocity();
    }

    public void amplifyBoost(ClientPlayerEntity player) {
        if (!this.isEnabled()) {
            return;
        }
        if (this.capturedVelocity == null) {
            return;
        }
        float b = this.boost.getValue().floatValue();
        if (b <= 0.01f) {
            player.setVelocity(this.capturedVelocity);
            this.capturedVelocity = null;
            return;
        }
        Vec3d velAfter = player.getVelocity();
        Vec3d delta = velAfter.subtract(this.capturedVelocity);
        player.setVelocity(this.capturedVelocity.x + delta.x * (double)b, this.capturedVelocity.y + delta.y * (double)b, this.capturedVelocity.z + delta.z * (double)b);
        this.capturedVelocity = null;
    }

    @Override
    public void onUpdate() {
        double multiplier;
        if (ElytraBooster.mc.player == null || !ElytraBooster.mc.player.isFallFlying()) {
            return;
        }
        float b = this.boost.getValue().floatValue();
        if (Math.abs(b - 1.0f) < 0.01f) {
            return;
        }
        Vec3d vel = ElytraBooster.mc.player.getVelocity();
        double hSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (hSpeed < 0.05) {
            return;
        }
        if (b > 1.0f) {
            double factor = b - 1.0f;
            multiplier = 1.0 + factor * 0.005;
        } else {
            double factor = 1.0f - b;
            multiplier = 1.0 - factor * 0.06;
        }
        ElytraBooster.mc.player.setVelocity(vel.x * multiplier, vel.y, vel.z * multiplier);
    }
}

