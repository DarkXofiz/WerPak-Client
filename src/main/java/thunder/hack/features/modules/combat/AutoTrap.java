/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 */
package thunder.hack.features.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.player.CombatManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.base.TrapModule;
import thunder.hack.setting.Setting;

public final class AutoTrap
extends TrapModule {
    private final Setting<CombatManager.TargetBy> targetBy = new Setting<CombatManager.TargetBy>("Target By", CombatManager.TargetBy.Distance);
    private final Setting<Boolean> targetMovingPlayers = new Setting<Boolean>("MovingPlayers", false);

    public AutoTrap() {
        super("AutoTrap", Module.Category.COMBAT);
    }

    @Override
    protected boolean needNewTarget() {
        return this.target == null || this.target.distanceTo((Entity)AutoTrap.mc.player) > ((Float)this.range.getValue()).floatValue() || this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0f || this.target.isDead();
    }

    @Override
    @Nullable
    protected PlayerEntity getTarget() {
        return Managers.COMBAT.getTarget(((Float)this.range.getValue()).floatValue(), this.targetBy.getValue(), p -> p.getVelocity().lengthSquared() < 0.08 || this.targetMovingPlayers.getValue() != false);
    }
}

