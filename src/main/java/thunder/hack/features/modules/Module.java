/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.util.Formatting
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.text.Text
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.network.PendingUpdateManager
 *  net.minecraft.client.network.SequencedPacketCreator
 */
package thunder.hack.features.modules;

import com.mojang.logging.LogUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.util.Formatting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.network.packet.Packet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.CommandManager;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.features.modules.client.UnHook;
import thunder.hack.features.modules.client.Windows;
import thunder.hack.gui.notification.Notification;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.Bind;

public abstract class Module {
    private final Setting<Bind> bind = new Setting<Bind>("Keybind", new Bind(-1, false, false));
    private final Setting<Boolean> drawn = new Setting<Boolean>("Drawn", true);
    private final Setting<Boolean> enabled = new Setting<Boolean>("Enabled", false);
    private final String description;
    private final Category category;
    private final String displayName;
    private final List<String> ignoreSoundList = Arrays.asList("ClickGui", "ThunderGui", "HudEditor");
    public static final MinecraftClient mc = MinecraftClient.getInstance();

    public Module(@NotNull String name, @NotNull Category category) {
        this.displayName = name;
        this.description = "descriptions." + category.getName().toLowerCase() + "." + name.toLowerCase();
        this.category = category;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onLogin() {
    }

    public void onLogout() {
    }

    public void onUpdate() {
    }

    public void onRender2D(DrawContext event) {
    }

    public void onRender3D(MatrixStack event) {
    }

    public void onUnload() {
    }

    public boolean isToggleable() {
        return true;
    }

    protected void sendPacket(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) {
            return;
        }
        mc.getNetworkHandler().sendPacket(packet);
    }

    protected void sendPacketSilent(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) {
            return;
        }
        ThunderHack.core.silentPackets.add(packet);
        mc.getNetworkHandler().sendPacket(packet);
    }

    protected void sendSequencedPacket(SequencedPacketCreator packetCreator) {
        if (mc.getNetworkHandler() == null || Module.mc.world == null) {
            return;
        }
        try (PendingUpdateManager pendingUpdateManager = Module.mc.world.getPendingUpdateManager().incrementSequence();){
            int i = pendingUpdateManager.getSequence();
            mc.getNetworkHandler().sendPacket(packetCreator.predict(i));
        }
    }

    public String getDisplayInfo() {
        return null;
    }

    public boolean isOn() {
        return this.enabled.getValue();
    }

    public boolean isOff() {
        return this.enabled.getValue() == false;
    }

    public void setEnabled(boolean enabled) {
        this.enabled.setValue(enabled);
    }

    public void onThread() {
    }

    public void enable() {
        if (!(this instanceof UnHook)) {
            this.enabled.setValue(true);
        }
        if (!Module.fullNullCheck() || this instanceof UnHook || this instanceof Windows) {
            this.onEnable();
        }
        if (this.isOn()) {
            ThunderHack.EVENT_BUS.subscribe(this);
        }
        if (Module.fullNullCheck()) {
            return;
        }
        LogUtils.getLogger().info("[ThunderHack] enabled " + this.getName());
        Managers.MODULE.sortModules();
        if (!this.ignoreSoundList.contains(this.getDisplayName())) {
            Managers.NOTIFICATION.publicity(this.getDisplayName(), ClientSettings.isRu() ? "\u041c\u043e\u0434\u0443\u043b\u044c \u0432\u043a\u043b\u044e\u0447\u0435\u043d!" : "Was Enabled!", 2, Notification.Type.ENABLED);
            Managers.SOUND.playEnable();
            ModuleManager.clientSound.onModuleEnable();
        }
    }

    public void disable(String reason) {
        this.sendMessage(reason);
        this.disable();
    }

    public void disable() {
        try {
            ThunderHack.EVENT_BUS.unsubscribe(this);
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.enabled.setValue(false);
        Managers.MODULE.sortModules();
        if (Module.fullNullCheck()) {
            return;
        }
        this.onDisable();
        ThunderHack.LOGGER.info("[ThunderHack] disabled {}", (Object)this.getName());
        if (!this.ignoreSoundList.contains(this.getDisplayName())) {
            Managers.NOTIFICATION.publicity(this.getDisplayName(), ClientSettings.isRu() ? "\u041c\u043e\u0434\u0443\u043b\u044c \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d!" : "Was Disabled!", 2, Notification.Type.DISABLED);
            Managers.SOUND.playDisable();
            ModuleManager.clientSound.onModuleDisable();
        }
    }

    public void toggle() {
        if (this.enabled.getValue().booleanValue()) {
            this.disable();
        } else {
            this.enable();
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isDrawn() {
        return this.drawn.getValue();
    }

    public void setDrawn(boolean d) {
        this.drawn.setValue(d);
    }

    public Category getCategory() {
        return this.category;
    }

    public Bind getBind() {
        return this.bind.getValue();
    }

    public void setBind(int key, boolean mouse, boolean hold) {
        this.setBind(new Bind(key, mouse, hold));
    }

    public void setBind(Bind b) {
        this.bind.setValue(b);
    }

    public boolean listening() {
        return this.isOn();
    }

    public String getFullArrayString() {
        return this.getDisplayName() + String.valueOf(Formatting.GRAY) + (String)(this.getDisplayInfo() != null ? " [" + String.valueOf(Formatting.WHITE) + this.getDisplayInfo() + String.valueOf(Formatting.GRAY) + "]" : "");
    }

    public static boolean fullNullCheck() {
        return Module.mc.player == null || Module.mc.world == null || ModuleManager.unHook.isEnabled();
    }

    public String getName() {
        return this.getDisplayName();
    }

    public List<Setting<?>> getSettings() {
        ArrayList settingList = new ArrayList();
        for (Class<?> currentSuperclass = this.getClass(); currentSuperclass != null; currentSuperclass = currentSuperclass.getSuperclass()) {
            for (Field field : currentSuperclass.getDeclaredFields()) {
                if (!Setting.class.isAssignableFrom(field.getType())) continue;
                try {
                    field.setAccessible(true);
                    settingList.add((Setting)field.get(this));
                }
                catch (IllegalAccessException error) {
                    ThunderHack.LOGGER.warn(error.getMessage());
                }
            }
        }
        settingList.forEach((Consumer<Setting<?>>)((Consumer<Setting>)s -> s.setModule(this)));
        return settingList;
    }

    public boolean isEnabled() {
        return this.isOn();
    }

    public boolean isDisabled() {
        return !this.isEnabled();
    }

    public static void clickSlot(int id) {
        if (id == -1 || Module.mc.interactionManager == null || Module.mc.player == null) {
            return;
        }
        Module.mc.interactionManager.clickSlot(Module.mc.player.currentScreenHandler.syncId, id, 0, SlotActionType.PICKUP, (PlayerEntity)Module.mc.player);
    }

    public static void clickSlot(int id, SlotActionType type) {
        if (id == -1 || Module.mc.interactionManager == null || Module.mc.player == null) {
            return;
        }
        Module.mc.interactionManager.clickSlot(Module.mc.player.currentScreenHandler.syncId, id, 0, type, (PlayerEntity)Module.mc.player);
    }

    public static void clickSlot(int id, int button, SlotActionType type) {
        if (id == -1 || Module.mc.interactionManager == null || Module.mc.player == null) {
            return;
        }
        Module.mc.interactionManager.clickSlot(Module.mc.player.currentScreenHandler.syncId, id, button, type, (PlayerEntity)Module.mc.player);
    }

    public void sendMessage(String message) {
        if (Module.fullNullCheck() || !ClientSettings.clientMessages.getValue().booleanValue() || ModuleManager.unHook.isEnabled()) {
            return;
        }
        if (mc.isOnThread()) {
            Module.mc.player.sendMessage(Text.of((String)(CommandManager.getClientMessage() + " " + String.valueOf(Formatting.GRAY) + "[" + String.valueOf(Formatting.DARK_PURPLE) + this.getDisplayName() + String.valueOf(Formatting.GRAY) + "] " + message)));
        } else {
            mc.executeSync(() -> Module.mc.player.sendMessage(Text.of((String)(CommandManager.getClientMessage() + " " + String.valueOf(Formatting.GRAY) + "[" + String.valueOf(Formatting.DARK_PURPLE) + this.getDisplayName() + String.valueOf(Formatting.GRAY) + "] " + message))));
        }
    }

    public void sendChatMessage(String message) {
        if (Module.fullNullCheck()) {
            return;
        }
        mc.getNetworkHandler().sendChatMessage(message);
    }

    public void sendChatCommand(String command) {
        if (Module.fullNullCheck()) {
            return;
        }
        mc.getNetworkHandler().sendChatCommand(command);
    }

    public void debug(String message) {
        if (Module.fullNullCheck() || !ClientSettings.debug.getValue().booleanValue()) {
            return;
        }
        if (mc.isOnThread()) {
            Module.mc.player.sendMessage(Text.of((String)(CommandManager.getClientMessage() + " " + String.valueOf(Formatting.GRAY) + "[" + String.valueOf(Formatting.DARK_PURPLE) + this.getDisplayName() + String.valueOf(Formatting.GRAY) + "] [\ud83d\udd27] " + message)));
        } else {
            mc.executeSync(() -> Module.mc.player.sendMessage(Text.of((String)(CommandManager.getClientMessage() + " " + String.valueOf(Formatting.GRAY) + "[" + String.valueOf(Formatting.DARK_PURPLE) + this.getDisplayName() + String.valueOf(Formatting.GRAY) + "] [\ud83d\udd27] " + message))));
        }
    }

    public boolean isKeyPressed(int button) {
        if (button == -1 || ModuleManager.unHook.isEnabled()) {
            return false;
        }
        if (Managers.MODULE.activeMouseKeys.contains(button)) {
            Managers.MODULE.activeMouseKeys.clear();
            return true;
        }
        if (button < 10) {
            return false;
        }
        return InputUtil.isKeyPressed((long)mc.getWindow().getHandle(), (int)button);
    }

    public boolean isKeyPressed(Setting<Bind> bind) {
        if (bind.getValue().getKey() == -1 || ModuleManager.unHook.isEnabled()) {
            return false;
        }
        return this.isKeyPressed(bind.getValue().getKey());
    }

    @Nullable
    public Setting<?> getSettingByName(String name) {
        for (Setting<?> setting : this.getSettings()) {
            if (!setting.getName().equalsIgnoreCase(name)) continue;
            return setting;
        }
        return null;
    }

    public static class Category {
        private final String name;
        private static final Map<String, Category> CATEGORIES = new LinkedHashMap<String, Category>();
        public static final Category COMBAT = new Category("Combat");
        public static final Category MISC = new Category("Misc");
        public static final Category RENDER = new Category("Render");
        public static final Category MOVEMENT = new Category("Movement");
        public static final Category PLAYER = new Category("Player");
        public static final Category CLIENT = new Category("Client");
        public static final Category HUD = new Category("HUD");

        private Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static Category getCategory(String name) {
            return CATEGORIES.computeIfAbsent(name, Category::new);
        }

        public static Collection<Category> values() {
            return CATEGORIES.values();
        }

        public static boolean isCustomCategory(Category category) {
            Set<String> predefinedCategoryNames = Set.of("Combat", "Misc", "Render", "Movement", "Player", "Client", "HUD");
            return !predefinedCategoryNames.contains(category.getName());
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Category category = (Category)o;
            return Objects.equals(this.name, category.name);
        }

        public int hashCode() {
            return Objects.hash(this.name);
        }

        static {
            CATEGORIES.put("Combat", COMBAT);
            CATEGORIES.put("Misc", MISC);
            CATEGORIES.put("Render", RENDER);
            CATEGORIES.put("Movement", MOVEMENT);
            CATEGORIES.put("Player", PLAYER);
            CATEGORIES.put("Client", CLIENT);
            CATEGORIES.put("HUD", HUD);
        }
    }
}

