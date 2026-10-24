/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.MathHelper
 */
package thunder.hack.features.modules.client;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventFixVelocity;
import thunder.hack.events.impl.EventKeyboardInput;
import thunder.hack.events.impl.EventPlayerJump;
import thunder.hack.events.impl.EventPlayerTravel;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.setting.Setting;

public class Rotations
extends Module {
    private final Setting<MoveFix> moveFix = new Setting<MoveFix>("MoveFix", MoveFix.Off);
    public final Setting<Boolean> clientLook = new Setting<Boolean>("ClientLook", false);
    public float fixRotation;
    private float jumpPrevYaw;
    private float travelPrevYaw;
    private float travelPrevPitch;

    public Rotations() {
        super("Rotations", Module.Category.CLIENT);
    }

    public void onJump(EventPlayerJump e) {
        if (Float.isNaN(this.fixRotation) || this.moveFix.getValue() == MoveFix.Off || Rotations.mc.player.isRiding()) {
            return;
        }
        if (e.isPre()) {
            this.jumpPrevYaw = Rotations.mc.player.getYaw();
            Rotations.mc.player.setYaw(this.fixRotation);
        } else {
            Rotations.mc.player.setYaw(this.jumpPrevYaw);
        }
    }

    public void onPlayerMove(EventFixVelocity event) {
        if (this.moveFix.getValue() == MoveFix.Free) {
            if (Float.isNaN(this.fixRotation) || Rotations.mc.player.isRiding()) {
                return;
            }
            event.setVelocity(this.fix(this.fixRotation, event.getMovementInput(), event.getSpeed()));
        }
    }

    public void modifyVelocity(EventPlayerTravel e) {
        if (ModuleManager.aura.isEnabled()) {
            if (Aura.target != null && ModuleManager.aura.rotationMode.not(Aura.Mode.None) && ModuleManager.aura.elytraTarget.getValue().booleanValue() && Managers.PLAYER.ticksElytraFlying > 5) {
                if (e.isPre()) {
                    this.travelPrevYaw = Rotations.mc.player.getYaw();
                    this.travelPrevPitch = Rotations.mc.player.getPitch();
                    Rotations.mc.player.setYaw(this.fixRotation);
                    Rotations.mc.player.setPitch(ModuleManager.aura.rotationPitch);
                } else {
                    Rotations.mc.player.setYaw(this.travelPrevYaw);
                    Rotations.mc.player.setPitch(this.travelPrevPitch);
                }
                return;
            }
        }
        if (this.moveFix.getValue() == MoveFix.Focused && !Float.isNaN(this.fixRotation) && !Rotations.mc.player.isRiding()) {
            if (e.isPre()) {
                this.travelPrevYaw = Rotations.mc.player.getYaw();
                Rotations.mc.player.setYaw(this.fixRotation);
            } else {
                Rotations.mc.player.setYaw(this.travelPrevYaw);
            }
        }
    }

    public void onKeyInput(EventKeyboardInput e) {
        if (this.moveFix.getValue() == MoveFix.Free) {
            if (Float.isNaN(this.fixRotation) || Rotations.mc.player.isRiding()) {
                return;
            }
            float mF = Rotations.mc.player.input.movementForward;
            float mS = Rotations.mc.player.input.movementSideways;
            float delta = (Rotations.mc.player.getYaw() - this.fixRotation) * ((float)Math.PI / 180);
            float cos = MathHelper.cos((float)delta);
            float sin = MathHelper.sin((float)delta);
            Rotations.mc.player.input.movementSideways = Math.round(mS * cos - mF * sin);
            Rotations.mc.player.input.movementForward = Math.round(mF * cos + mS * sin);
        }
    }

    private Vec3d fix(float yaw, Vec3d movementInput, float speed) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        }
        Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply((double)speed);
        float f = MathHelper.sin((float)(yaw * ((float)Math.PI / 180)));
        float g = MathHelper.cos((float)(yaw * ((float)Math.PI / 180)));
        return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
    }

    @Override
    public boolean isToggleable() {
        return false;
    }

    private static enum MoveFix {
        Off,
        Focused,
        Free;

    }
}

