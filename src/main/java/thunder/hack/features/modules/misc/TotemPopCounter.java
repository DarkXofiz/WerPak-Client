/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.entity.player.PlayerEntity
 */
package thunder.hack.features.modules.misc;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.TotemPopEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.gui.notification.Notification;
import thunder.hack.setting.Setting;

public class TotemPopCounter
extends Module {
    public Setting<Boolean> notification = new Setting<Boolean>("Notification", true);

    public TotemPopCounter() {
        super("TotemPopCounter", Module.Category.MISC);
    }

    @EventHandler
    public void onTotemPop(@NotNull TotemPopEvent event) {
        if (event.getEntity() == TotemPopCounter.mc.player) {
            return;
        }
        String s = ClientSettings.isRu() ? String.valueOf(Formatting.GREEN) + event.getEntity().getName().getString() + String.valueOf(Formatting.WHITE) + " \u043f\u043e\u043f\u043d\u0443\u043b " + String.valueOf(Formatting.AQUA) + (event.getPops() > 1 ? event.getPops() + String.valueOf(Formatting.WHITE) + " \u0442\u043e\u0442\u0435\u043c\u043e\u0432!" : String.valueOf(Formatting.WHITE) + "\u0442\u043e\u0442\u0435\u043c!") : String.valueOf(Formatting.GREEN) + event.getEntity().getName().getString() + String.valueOf(Formatting.WHITE) + " popped " + String.valueOf(Formatting.AQUA) + (event.getPops() > 1 ? event.getPops() + String.valueOf(Formatting.WHITE) + " totems!" : String.valueOf(Formatting.WHITE) + " a totem!");
        this.sendMessage(s);
        if (this.notification.getValue().booleanValue()) {
            Managers.NOTIFICATION.publicity("TotemPopCounter", s, 2, Notification.Type.INFO);
        }
    }

    @Override
    public void onUpdate() {
        for (PlayerEntity player : TotemPopCounter.mc.world.getPlayers()) {
            if (player == TotemPopCounter.mc.player || player.getHealth() > 0.0f || !Managers.COMBAT.popList.containsKey(player.getName().getString())) continue;
            String s = ClientSettings.isRu() ? String.valueOf(Formatting.GREEN) + player.getName().getString() + String.valueOf(Formatting.WHITE) + " \u043f\u043e\u043f\u043d\u0443\u043b " + (Managers.COMBAT.popList.get(player.getName().getString()) > 1 ? String.valueOf(Managers.COMBAT.popList.get(player.getName().getString())) + String.valueOf(Formatting.WHITE) + " \u0442\u043e\u0442\u0435\u043c\u043e\u0432 \u0438 \u0441\u0434\u043e\u0445!" : String.valueOf(Formatting.WHITE) + "\u0442\u043e\u0442\u0435\u043c \u0438 \u0441\u0434\u043e\u0445!") : String.valueOf(Formatting.GREEN) + player.getName().getString() + String.valueOf(Formatting.WHITE) + " popped " + (Managers.COMBAT.popList.get(player.getName().getString()) > 1 ? String.valueOf(Managers.COMBAT.popList.get(player.getName().getString())) + String.valueOf(Formatting.WHITE) + " totems and died EZ LMAO!" : String.valueOf(Formatting.WHITE) + "totem and died EZ LMAO!");
            this.sendMessage(s);
            if (!this.notification.getValue().booleanValue()) continue;
            Managers.NOTIFICATION.publicity("TotemPopCounter", s, 2, Notification.Type.INFO);
        }
    }
}

