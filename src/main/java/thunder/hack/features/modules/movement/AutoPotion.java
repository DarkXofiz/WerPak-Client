/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.SplashPotionItem
 *  net.minecraft.component.type.PotionContentsComponent
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.component.DataComponentTypes
 */
package thunder.hack.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.component.DataComponentTypes;
import thunder.hack.events.impl.EventPostSync;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.utility.Timer;

public final class AutoPotion
extends Module {
    private final Setting<Boolean> strength = new Setting<Boolean>("Strength", true);
    private final Setting<Boolean> speed = new Setting<Boolean>("Speed", true);
    private final Setting<Boolean> fire = new Setting<Boolean>("FireResistance", true);
    private final Setting<BooleanSettingGroup> heal = new Setting<BooleanSettingGroup>("InstantHealing", new BooleanSettingGroup(true));
    private final Setting<Integer> healthH = new Setting<Integer>("Health", 8, 0, 20).addToGroup(this.heal);
    private final Setting<BooleanSettingGroup> regen = new Setting<BooleanSettingGroup>("Regeneration", new BooleanSettingGroup(true));
    private final Setting<TriggerOn> triggerOn = new Setting<TriggerOn>("Trigger", TriggerOn.LackOfRegen).addToGroup(this.regen);
    private final Setting<Integer> healthR = new Setting<Integer>("HP", 8, 0, 20, v -> this.triggerOn.is(TriggerOn.Health)).addToGroup(this.regen);
    public Timer timer = new Timer();

    public AutoPotion() {
        super("AutoPotion", Module.Category.COMBAT);
    }

    public static int getPotionSlot(Potions potion) {
        for (int i = 0; i < 9; ++i) {
            if (!AutoPotion.isStackPotion(AutoPotion.mc.player.getInventory().getStack(i), potion)) continue;
            return i;
        }
        return -1;
    }

    public static boolean isPotionOnHotBar(Potions potions) {
        return AutoPotion.getPotionSlot(potions) != -1;
    }

    public static boolean isStackPotion(ItemStack stack, Potions potion) {
        if (stack == null) {
            return false;
        }
        if (stack.getItem() instanceof SplashPotionItem) {
            PotionContentsComponent potionContentsComponent = (PotionContentsComponent)stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, (Object)PotionContentsComponent.DEFAULT);
            RegistryEntry id = null;
            switch (potion.ordinal()) {
                case 0: {
                    id = StatusEffects.STRENGTH;
                    break;
                }
                case 1: {
                    id = StatusEffects.SPEED;
                    break;
                }
                case 2: {
                    id = StatusEffects.FIRE_RESISTANCE;
                    break;
                }
                case 3: {
                    id = StatusEffects.INSTANT_HEALTH;
                    break;
                }
                case 4: {
                    id = StatusEffects.REGENERATION;
                }
            }
            for (StatusEffectInstance effect : potionContentsComponent.getEffects()) {
                if (effect.getEffectType() != id) continue;
                return true;
            }
        }
        return false;
    }

    private boolean shouldThrow() {
        return !AutoPotion.mc.player.hasStatusEffect(StatusEffects.SPEED) && AutoPotion.isPotionOnHotBar(Potions.SPEED) && this.speed.getValue() != false || !AutoPotion.mc.player.hasStatusEffect(StatusEffects.STRENGTH) && AutoPotion.isPotionOnHotBar(Potions.STRENGTH) && this.strength.getValue() != false || !AutoPotion.mc.player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE) && AutoPotion.isPotionOnHotBar(Potions.FIRERES) && this.fire.getValue() != false || AutoPotion.mc.player.getHealth() + AutoPotion.mc.player.getAbsorptionAmount() < (float)this.healthH.getValue().intValue() && AutoPotion.isPotionOnHotBar(Potions.HEAL) && this.heal.getValue().isEnabled() || !AutoPotion.mc.player.hasStatusEffect(StatusEffects.REGENERATION) && this.triggerOn.is(TriggerOn.LackOfRegen) && AutoPotion.isPotionOnHotBar(Potions.REGEN) && this.regen.getValue().isEnabled() || AutoPotion.mc.player.getHealth() + AutoPotion.mc.player.getAbsorptionAmount() < (float)this.healthR.getValue().intValue() && this.triggerOn.is(TriggerOn.Health) && AutoPotion.isPotionOnHotBar(Potions.REGEN) && this.regen.getValue().isEnabled();
    }

    @EventHandler
    public void onPostSync(EventPostSync e) {
        if (AutoPotion.mc.player.isOnGround() && AutoPotion.mc.player.age > 80 && this.shouldThrow() && this.timer.passedMs(250L)) {
            if (!AutoPotion.mc.player.hasStatusEffect(StatusEffects.SPEED) && AutoPotion.isPotionOnHotBar(Potions.SPEED) && this.speed.getValue().booleanValue()) {
                this.throwPotion(Potions.SPEED);
            }
            if (!AutoPotion.mc.player.hasStatusEffect(StatusEffects.STRENGTH) && AutoPotion.isPotionOnHotBar(Potions.STRENGTH) && this.strength.getValue().booleanValue()) {
                this.throwPotion(Potions.STRENGTH);
            }
            if (!AutoPotion.mc.player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE) && AutoPotion.isPotionOnHotBar(Potions.FIRERES) && this.fire.getValue().booleanValue()) {
                this.throwPotion(Potions.FIRERES);
            }
            if (AutoPotion.mc.player.getHealth() + AutoPotion.mc.player.getAbsorptionAmount() < (float)this.healthH.getValue().intValue() && this.heal.getValue().isEnabled() && AutoPotion.isPotionOnHotBar(Potions.HEAL)) {
                this.throwPotion(Potions.HEAL);
            }
            if ((!AutoPotion.mc.player.hasStatusEffect(StatusEffects.REGENERATION) && this.triggerOn.is(TriggerOn.LackOfRegen) || AutoPotion.mc.player.getHealth() + AutoPotion.mc.player.getAbsorptionAmount() < (float)this.healthR.getValue().intValue() && this.triggerOn.is(TriggerOn.Health)) && AutoPotion.isPotionOnHotBar(Potions.REGEN) && this.regen.getValue().isEnabled()) {
                this.throwPotion(Potions.REGEN);
            }
            this.sendPacket((Packet<?>)new UpdateSelectedSlotC2SPacket(AutoPotion.mc.player.getInventory().selectedSlot));
            this.timer.reset();
        }
    }

    public void throwPotion(Potions potion) {
        int slot = AutoPotion.getPotionSlot(potion);
        if (slot != -1) {
            this.sendPacket((Packet<?>)new UpdateSelectedSlotC2SPacket(slot));
            float yaw = AutoPotion.mc.player.getYaw() + 180.0f;
            float pitch = 50.0f;
            this.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id, yaw, pitch));
        }
    }

    public enum TriggerOn {
        LackOfRegen, Health;
    }


    public enum Potions {
        STRENGTH, SPEED, FIRERES, HEAL, REGEN;
    }

}

