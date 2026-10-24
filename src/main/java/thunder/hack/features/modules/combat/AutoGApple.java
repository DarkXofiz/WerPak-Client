/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.item.Items
 */
package thunder.hack.features.modules.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.item.Items;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.PostPlayerUpdateEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.features.modules.combat.AutoTotem;
import thunder.hack.injection.accesors.IMinecraftClient;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;
import thunder.hack.utility.player.InventoryUtility;

public final class AutoGApple
extends Module {
    public final Setting<Integer> Delay = new Setting<Integer>("UseDelay", 0, 0, 2000);
    private final Setting<Float> health = new Setting<Float>("health", Float.valueOf(15.0f), Float.valueOf(1.0f), Float.valueOf(36.0f));
    public Setting<Boolean> absorption = new Setting<Boolean>("Absorption", false);
    public Setting<Boolean> autoTotemIntegration = new Setting<Boolean>("AutoTotemIntegration", true);
    private boolean isActive;
    private final Timer useDelay = new Timer();

    public AutoGApple() {
        super("AutoGApple", Module.Category.COMBAT);
    }

    @EventHandler
    public void onUpdate(PostPlayerUpdateEvent e) {
        if (AutoGApple.fullNullCheck()) {
            return;
        }
        if (this.GapInOffHand()) {
            float f = AutoGApple.mc.player.getHealth();
            float f2 = this.absorption.getValue() != false ? AutoGApple.mc.player.getAbsorptionAmount() : 0.0f;
            if (f + f2 <= this.health.getValue().floatValue() && this.useDelay.passedMs(this.Delay.getValue().intValue())) {
                this.isActive = true;
                if (AutoGApple.mc.currentScreen != null && !AutoGApple.mc.player.isUsingItem()) {
                    ((IMinecraftClient)mc).idoItemUse();
                } else {
                    AutoGApple.mc.options.useKey.setPressed(true);
                }
            } else if (this.isActive) {
                this.isActive = false;
                AutoGApple.mc.options.useKey.setPressed(false);
            }
        } else if (this.isActive) {
            this.isActive = false;
            AutoGApple.mc.options.useKey.setPressed(false);
        }
    }

    private boolean GapInOffHand() {
        if (this.autoTotemIntegration.getValue().booleanValue() && ModuleManager.autoTotem.isEnabled() && InventoryUtility.findItemInHotBar(Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE).found()) {
            if (!ModuleManager.autoTotem.rcGap.is(AutoTotem.RCGap.Off)) {
                return true;
            }
            this.sendMessage(String.valueOf(Formatting.RED) + (ClientSettings.isRu() ? "\u0412\u043a\u043b\u044e\u0447\u0438 RcGap \u0432 AutoTotem!" : "Enable RcGap in AutoTotem"));
        }
        return !AutoGApple.mc.player.getOffHandStack().isEmpty() && (AutoGApple.mc.player.getOffHandStack().getItem() == Items.GOLDEN_APPLE || AutoGApple.mc.player.getOffHandStack().getItem() == Items.ENCHANTED_GOLDEN_APPLE);
    }
}

