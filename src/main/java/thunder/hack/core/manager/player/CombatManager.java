/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
 *  net.minecraft.util.math.MathHelper
 */
package thunder.hack.core.manager.player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.IManager;
import thunder.hack.events.impl.EventPostTick;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.events.impl.TotemPopEvent;
import thunder.hack.features.modules.Module;

public class CombatManager
implements IManager {
    public HashMap<String, Integer> popList = new HashMap();

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        EntityStatusS2CPacket pac;
        if (Module.fullNullCheck()) {
            return;
        }
        Object t = event.getPacket();
        if (t instanceof EntityStatusS2CPacket && (pac = (EntityStatusS2CPacket)t).getStatus() == 35) {
            Entity ent = pac.getEntity((World)CombatManager.mc.world);
            if (!(ent instanceof PlayerEntity)) {
                return;
            }
            if (this.popList == null) {
                this.popList = new HashMap();
            }
            if (this.popList.get(ent.getName().getString()) == null) {
                this.popList.put(ent.getName().getString(), 1);
            } else if (this.popList.get(ent.getName().getString()) != null) {
                this.popList.put(ent.getName().getString(), this.popList.get(ent.getName().getString()) + 1);
            }
            ThunderHack.EVENT_BUS.post(new TotemPopEvent((PlayerEntity)ent, this.popList.get(ent.getName().getString())));
        }
    }

    @EventHandler
    public void onPostTick(EventPostTick event) {
        if (Module.fullNullCheck()) {
            return;
        }
        for (PlayerEntity player : CombatManager.mc.world.getPlayers()) {
            if (!(player.getHealth() <= 0.0f) || !this.popList.containsKey(player.getName().getString())) continue;
            this.popList.remove(player.getName().getString(), this.popList.get(player.getName().getString()));
        }
    }

    public int getPops(@NotNull PlayerEntity entity) {
        if (this.popList.get(entity.getName().getString()) == null) {
            return 0;
        }
        return this.popList.get(entity.getName().getString());
    }

    public List<PlayerEntity> getTargets(float range) {
        return CombatManager.mc.world.getPlayers().stream().filter(e -> !e.isDead()).filter(entityPlayer -> !Managers.FRIEND.isFriend(entityPlayer.getName().getString())).filter(entityPlayer -> entityPlayer != CombatManager.mc.player).filter(entityPlayer -> CombatManager.mc.player.squaredDistanceTo((Entity)entityPlayer) < (double)(range * range)).sorted(Comparator.comparing(e -> CombatManager.mc.player.squaredDistanceTo((Entity)e))).collect(Collectors.toList());
    }

    @Nullable
    public PlayerEntity getTarget(float range, @NotNull TargetBy targetBy) {
        PlayerEntity target = null;
        switch (targetBy.ordinal()) {
            case 1: {
                target = this.getTargetByFOV(range);
                break;
            }
            case 2: {
                target = this.getTargetByHealth(range);
                break;
            }
            case 0: {
                target = this.getNearestTarget(range);
            }
        }
        return target;
    }

    @Nullable
    public PlayerEntity getNearestTarget(float range) {
        return this.getTargets(range).stream().min(Comparator.comparing(t -> Float.valueOf(CombatManager.mc.player.distanceTo((Entity)t)))).orElse(null);
    }

    public PlayerEntity getTargetByHealth(float range) {
        return this.getTargets(range).stream().min(Comparator.comparing(t -> Float.valueOf(t.getHealth() + t.getAbsorptionAmount()))).orElse(null);
    }

    public PlayerEntity getTargetByFOV(float range) {
        return this.getTargets(range).stream().min(Comparator.comparing(this::getFOVAngle)).orElse(null);
    }

    public PlayerEntity getTargetByFOV(float range, float fov) {
        return this.getTargets(range).stream().filter(entityPlayer -> this.getFOVAngle((LivingEntity)entityPlayer) < fov).min(Comparator.comparing(this::getFOVAngle)).orElse(null);
    }

    @Nullable
    public PlayerEntity getTarget(float range, @NotNull TargetBy targetBy, @NotNull Predicate<PlayerEntity> predicate) {
        PlayerEntity target = null;
        switch (targetBy.ordinal()) {
            case 1: {
                target = this.getTargetByFOV(range, predicate);
                break;
            }
            case 2: {
                target = this.getTargetByHealth(range, predicate);
                break;
            }
            case 0: {
                target = this.getNearestTarget(range, predicate);
            }
        }
        return target;
    }

    @Nullable
    public PlayerEntity getNearestTarget(float range, Predicate<PlayerEntity> predicate) {
        return this.getTargets(range).stream().filter(predicate).min(Comparator.comparing(t -> Float.valueOf(CombatManager.mc.player.distanceTo((Entity)t)))).orElse(null);
    }

    public PlayerEntity getTargetByHealth(float range, Predicate<PlayerEntity> predicate) {
        return this.getTargets(range).stream().filter(predicate).min(Comparator.comparing(t -> Float.valueOf(t.getHealth() + t.getAbsorptionAmount()))).orElse(null);
    }

    public PlayerEntity getTargetByFOV(float range, Predicate<PlayerEntity> predicate) {
        return this.getTargets(range).stream().filter(predicate).min(Comparator.comparing(this::getFOVAngle)).orElse(null);
    }

    private float getFOVAngle(@NotNull LivingEntity e) {
        float yaw = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(e.getZ() - CombatManager.mc.player.getZ(), e.getX() - CombatManager.mc.player.getX())) - 90.0));
        return Math.abs(yaw - MathHelper.wrapDegrees((float)CombatManager.mc.player.getYaw()));
    }

    public static enum TargetBy {
        Distance,
        FOV,
        Health;

    }
}

