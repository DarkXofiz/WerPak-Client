/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package thunder.hack.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventMove;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.setting.Setting;

public class ElytraMotion
extends Module {
    public final Setting<Float> attackDistance = new Setting<Float>("Distance", Float.valueOf(3.0f), Float.valueOf(0.1f), Float.valueOf(5.0f));
    public boolean freeze;

    public ElytraMotion() {
        super("ElytraMotion", Module.Category.MOVEMENT);
    }

    @EventHandler
    public void onUpdate(PlayerUpdateEvent event) {
        if (!ElytraMotion.mc.player.isFallFlying()) {
            this.freeze = false;
        } else if (this.check()) {
            ElytraMotion.mc.options.jumpKey.setPressed(false);
            this.freeze = true;
        } else {
            ElytraMotion.mc.options.jumpKey.setPressed(true);
            this.freeze = false;
        }
    }

    @EventHandler
    public void onMotion(EventMove eventMotion) {
        if (this.freeze) {
            eventMotion.setX(0.0);
            eventMotion.setY(0.0);
            eventMotion.setZ(0.0);
            eventMotion.cancel();
        }
    }

    public boolean check() {
        if (!ModuleManager.aura.isEnabled()) {
            return false;
        }
        Entity target = Aura.target;
        if (target == null) {
            return false;
        }
        return ElytraMotion.mc.player.squaredDistanceTo(target.getPos()) < (double)(this.attackDistance.getValue().floatValue() * this.attackDistance.getValue().floatValue()) && ElytraMotion.mc.player.isFallFlying();
    }

    @Override
    public void onDisable() {
        this.freeze = false;
        super.onDisable();
    }
}

