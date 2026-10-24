/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 */
package thunder.hack.features.modules.misc;

import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.IMinecraftClient;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.utility.Timer;
import thunder.hack.utility.math.MathUtility;

public class TapeMouse
extends Module {
    private final Setting<Integer> delay = new Setting<Integer>("Delay", 600, 0, 10000);
    private final Setting<BooleanSettingGroup> randomize = new Setting<BooleanSettingGroup>("Randomize", new BooleanSettingGroup(false));
    private final Setting<Integer> randomizeValue = new Setting<Integer>("Value", 600, 0, 10000).addToGroup(this.randomize);
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Left);
    private final Setting<Boolean> legit = new Setting<Boolean>("Legit", false, v -> this.mode.getValue() == Mode.Left);
    private final Timer timer = new Timer();

    public TapeMouse() {
        super("TapeMouse", Module.Category.MISC);
    }

    @Override
    public void onUpdate() {
        if (this.timer.every((long)((float)this.delay.getValue().intValue() + (this.randomize.getValue().isEnabled() ? MathUtility.random(0.0f, this.randomizeValue.getValue().intValue()) : 0.0f)))) {
            if (this.mode.getValue() == Mode.Left) {
                if (!this.legit.getValue().booleanValue()) {
                    HitResult hr = TapeMouse.mc.crosshairTarget;
                    if (hr != null) {
                        BlockHitResult bhr;
                        EntityHitResult ehr;
                        if (hr instanceof EntityHitResult && (ehr = (EntityHitResult)hr).getEntity() != null) {
                            TapeMouse.mc.interactionManager.attackEntity((PlayerEntity)TapeMouse.mc.player, ehr.getEntity());
                            TapeMouse.mc.player.swingHand(Hand.MAIN_HAND);
                        } else if (hr instanceof BlockHitResult && (bhr = (BlockHitResult)hr).getBlockPos() != null && bhr.getSide() != null && !TapeMouse.mc.world.isAir(bhr.getBlockPos())) {
                            TapeMouse.mc.interactionManager.attackBlock(bhr.getBlockPos(), bhr.getSide());
                            TapeMouse.mc.player.swingHand(Hand.MAIN_HAND);
                        }
                    }
                } else {
                    ((IMinecraftClient)mc).idoAttack();
                }
            } else {
                ((IMinecraftClient)mc).idoItemUse();
            }
        }
    }

    private static enum Mode {
        Right,
        Left;

    }
}

