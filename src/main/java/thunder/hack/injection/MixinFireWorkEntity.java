/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.projectile.FireworkRocketEntity
 *  net.minecraft.util.math.Vec3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package thunder.hack.injection;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.IManager;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.combat.Aura;

@Mixin(value={FireworkRocketEntity.class})
public class MixinFireWorkEntity {
    @Shadow
    private LivingEntity shooter;

    @Redirect(method={"tick"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d tickHook(LivingEntity instance) {
        if (ModuleManager.aura.isEnabled() && ModuleManager.aura.rotationMode.not(Aura.Mode.None)) {
            if (Aura.target != null && this.shooter == IManager.mc.player && ModuleManager.aura.elytraTarget.getValue().booleanValue()) {
                return Managers.PLAYER.getRotationVector(ModuleManager.aura.rotationPitch, ModuleManager.aura.rotationYaw);
            }
        }
        return this.shooter.getRotationVector();
    }
}

