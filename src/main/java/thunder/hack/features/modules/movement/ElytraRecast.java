/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.item.ElytraItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode
 *  net.minecraft.client.util.InputUtil
 */
package thunder.hack.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.client.util.InputUtil;
import thunder.hack.ThunderHack;
import thunder.hack.events.impl.EventSync;
import thunder.hack.events.impl.EventTravel;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.ILivingEntity;
import thunder.hack.setting.Setting;
import thunder.hack.utility.math.MathUtility;

public class ElytraRecast
extends Module {
    public Setting<Exploit> exploit = new Setting<Exploit>("Exploit", Exploit.None);
    public Setting<Boolean> changePitch = new Setting<Boolean>("ChangePitch", true);
    public Setting<Float> pitchValue = new Setting<Float>("PitchValue", Float.valueOf(55.0f), Float.valueOf(-90.0f), Float.valueOf(90.0f), v -> this.changePitch.getValue());
    public Setting<Boolean> autoWalk = new Setting<Boolean>("AutoWalk", true);
    public Setting<Boolean> autoJump = new Setting<Boolean>("AutoJump", true);
    public Setting<Boolean> allowBroken = new Setting<Boolean>("AllowBroken", true);
    private float prevClientPitch;
    private float prevClientYaw;
    private float jitter;

    public ElytraRecast() {
        super("ElytraRecast", Module.Category.MOVEMENT);
    }

    @EventHandler
    public void onSync(EventSync e) {
        if (this.changePitch.getValue().booleanValue()) {
            ElytraRecast.mc.player.setPitch(this.pitchValue.getValue().floatValue());
        }
        switch (this.exploit.getValue().ordinal()) {
            case 0: {
                break;
            }
            case 1: {
                ElytraRecast.mc.player.setYaw(ElytraRecast.mc.player.getYaw() + this.jitter);
                break;
            }
            case 2: {
                ElytraRecast.mc.player.setPitch(this.pitchValue.getValue().floatValue() - Math.abs(this.jitter / 2.0f));
            }
        }
    }

    @EventHandler
    public void modifyVelocity(EventTravel e) {
        if (this.changePitch.getValue().booleanValue()) {
            if (e.isPre()) {
                this.prevClientPitch = ElytraRecast.mc.player.getPitch();
                this.prevClientYaw = ElytraRecast.mc.player.getYaw();
                ElytraRecast.mc.player.setPitch(this.pitchValue.getValue().floatValue());
                switch (this.exploit.getValue().ordinal()) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        ElytraRecast.mc.player.setYaw(ElytraRecast.mc.player.getYaw() + this.jitter);
                        break;
                    }
                    case 2: {
                        ElytraRecast.mc.player.setPitch(this.pitchValue.getValue().floatValue() - Math.abs(this.jitter / 2.0f));
                    }
                }
            } else {
                ElytraRecast.mc.player.setPitch(this.prevClientPitch);
                if (this.exploit.getValue() == Exploit.Strict) {
                    ElytraRecast.mc.player.setYaw(this.prevClientYaw);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (!InputUtil.isKeyPressed((long)mc.getWindow().getHandle(), (int)ElytraRecast.mc.options.forwardKey.getDefaultKey().getCode())) {
            ElytraRecast.mc.options.forwardKey.setPressed(false);
        }
        if (!InputUtil.isKeyPressed((long)mc.getWindow().getHandle(), (int)ElytraRecast.mc.options.jumpKey.getDefaultKey().getCode())) {
            ElytraRecast.mc.options.jumpKey.setPressed(false);
        }
    }

    @Override
    public void onUpdate() {
        if (this.autoJump.getValue().booleanValue()) {
            ElytraRecast.mc.options.jumpKey.setPressed(true);
        }
        if (this.autoWalk.getValue().booleanValue()) {
            ElytraRecast.mc.options.forwardKey.setPressed(true);
        }
        if (!ElytraRecast.mc.player.isFallFlying() && ElytraRecast.mc.player.fallDistance > 0.0f && this.checkElytra() && !ElytraRecast.mc.player.isFallFlying()) {
            this.castElytra();
        }
        this.jitter = 20.0f * MathUtility.sin((float)(System.currentTimeMillis() - ThunderHack.initTime) / 50.0f);
        ((ILivingEntity)ElytraRecast.mc.player).setLastJumpCooldown(0);
    }

    public boolean castElytra() {
        if (this.checkElytra() && this.check()) {
            this.sendPacket((Packet<?>)new ClientCommandC2SPacket((Entity)ElytraRecast.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            return true;
        }
        return false;
    }

    private boolean checkElytra() {
        if (ElytraRecast.mc.player.input.jumping && !ElytraRecast.mc.player.getAbilities().flying && !ElytraRecast.mc.player.hasVehicle() && !ElytraRecast.mc.player.isClimbing()) {
            ItemStack is = ElytraRecast.mc.player.getEquippedStack(EquipmentSlot.CHEST);
            return is.isOf(Items.ELYTRA) && (ElytraItem.isUsable((ItemStack)is) || this.allowBroken.getValue() != false);
        }
        return false;
    }

    private boolean check() {
        ItemStack is;
        if (!ElytraRecast.mc.player.isTouchingWater() && !ElytraRecast.mc.player.hasStatusEffect(StatusEffects.LEVITATION) && (is = ElytraRecast.mc.player.getEquippedStack(EquipmentSlot.CHEST)).isOf(Items.ELYTRA) && (ElytraItem.isUsable((ItemStack)is) || this.allowBroken.getValue().booleanValue())) {
            ElytraRecast.mc.player.startFallFlying();
            return true;
        }
        return false;
    }

    private static enum Exploit {
        None,
        Strict,
        Strong;

    }
}

