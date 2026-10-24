/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.sound.SoundCategory
 */
package thunder.hack.features.modules.misc;

import java.util.ArrayList;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventEntityRemoved;
import thunder.hack.events.impl.EventEntitySpawn;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.misc.NameProtect;
import thunder.hack.gui.notification.Notification;
import thunder.hack.setting.Setting;

public class VisualRange
extends Module {
    private static final ArrayList<String> entities = new ArrayList();
    private final Setting<Boolean> leave = new Setting<Boolean>("Leave", true);
    private final Setting<Boolean> enter = new Setting<Boolean>("Enter", true);
    private final Setting<Boolean> friends = new Setting<Boolean>("Friends", true);
    private final Setting<Boolean> soundpl = new Setting<Boolean>("Sound", true);
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Notification);

    public VisualRange() {
        super("VisualRange", Module.Category.MISC);
    }

    @EventHandler
    public void onEntityAdded(EventEntitySpawn event) {
        if (!this.isValid(event.getEntity())) {
            return;
        }
        if (entities.contains(event.getEntity().getName().getString())) {
            return;
        }
        entities.add(event.getEntity().getName().getString());
        if (this.enter.getValue().booleanValue()) {
            this.notify(event.getEntity(), true);
        }
    }

    @EventHandler
    public void onEntityRemoved(EventEntityRemoved event) {
        if (!this.isValid(event.entity)) {
            return;
        }
        if (!entities.contains(event.entity.getName().getString())) {
            return;
        }
        entities.remove(event.entity.getName().getString());
        if (this.leave.getValue().booleanValue()) {
            this.notify(event.entity, false);
        }
    }

    public void notify(Entity entity, boolean enter) {
        Object message = "";
        if (ModuleManager.nameProtect.isEnabled() && NameProtect.hideFriends.getValue().booleanValue()) {
            message = String.valueOf(Formatting.AQUA) + NameProtect.newName.getValue();
        }
        message = Managers.FRIEND.isFriend(entity.getName().getString()) ? String.valueOf(Formatting.AQUA) + entity.getName().getString() : String.valueOf(Formatting.GRAY) + entity.getName().getString();
        message = enter ? (String)message + String.valueOf(Formatting.GREEN) + " was found!" : (String)message + String.valueOf(Formatting.RED) + " left to X:" + (int)entity.getX() + " Z:" + (int)entity.getZ();
        if (this.mode.is(Mode.Chat) || this.mode.is(Mode.Both)) {
            this.sendMessage((String)message);
        }
        if (this.mode.is(Mode.Notification) || this.mode.is(Mode.Both)) {
            Managers.NOTIFICATION.publicity("VisualRange", (String)message, 2, Notification.Type.WARNING);
        }
        if (this.soundpl.getValue().booleanValue()) {
            try {
                if (enter) {
                    VisualRange.mc.world.playSound((PlayerEntity)VisualRange.mc.player, VisualRange.mc.player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0f, 1.0f);
                } else {
                    VisualRange.mc.world.playSound((PlayerEntity)VisualRange.mc.player, VisualRange.mc.player.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public boolean isValid(Entity entity) {
        if (!(entity instanceof PlayerEntity)) {
            return false;
        }
        return entity != VisualRange.mc.player && (!Managers.FRIEND.isFriend(entity.getName().getString()) || this.friends.getValue() != false);
    }

    public static enum Mode {
        Chat,
        Notification,
        Both;

    }
}

