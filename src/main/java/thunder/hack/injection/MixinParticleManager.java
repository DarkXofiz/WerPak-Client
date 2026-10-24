/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.CampfireSmokeParticle
 *  net.minecraft.client.particle.FireworksSparkParticle.Flash
 *  net.minecraft.client.particle.FireworksSparkParticle.FireworkParticle
 *  net.minecraft.client.particle.ExplosionLargeParticle
 *  net.minecraft.client.particle.ElderGuardianAppearanceParticle
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.BlockDustParticle
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package thunder.hack.injection;

import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.ElderGuardianAppearanceParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.BlockDustParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.render.NoRender;

@Mixin(value={ParticleManager.class})
public class MixinParticleManager {
    @Inject(at={@At(value="HEAD")}, method={"addParticle(Lnet/minecraft/client/particle/Particle;)V"}, cancellable=true)
    public void addParticleHook(Particle p, CallbackInfo e) {
        NoRender nR = ModuleManager.noRender;
        if (!nR.isEnabled()) {
            return;
        }
        if (nR.elderGuardian.getValue().booleanValue() && p instanceof ElderGuardianAppearanceParticle) {
            e.cancel();
        }
        if (nR.explosions.getValue().booleanValue() && p instanceof ExplosionLargeParticle) {
            e.cancel();
        }
        if (nR.campFire.getValue().booleanValue() && p instanceof CampfireSmokeParticle) {
            e.cancel();
        }
        if (nR.breakParticles.getValue().booleanValue() && p instanceof BlockDustParticle) {
            e.cancel();
        }
        if (nR.fireworks.getValue().booleanValue() && (p instanceof FireworksSparkParticle.FireworkParticle || p instanceof FireworksSparkParticle.Flash)) {
            e.cancel();
        }
    }
}

