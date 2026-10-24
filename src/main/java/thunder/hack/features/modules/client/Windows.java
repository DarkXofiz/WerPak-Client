/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.Screen
 */
package thunder.hack.features.modules.client;

import net.minecraft.client.gui.screen.Screen;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.windows.WindowsScreen;
import thunder.hack.gui.windows.impl.ConfigWindow;
import thunder.hack.gui.windows.impl.FriendsWindow;
import thunder.hack.gui.windows.impl.MacroWindow;
import thunder.hack.gui.windows.impl.ProxyWindow;
import thunder.hack.gui.windows.impl.WaypointWindow;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.PositionSetting;

public class Windows
extends Module {
    public final Setting<PositionSetting> macroPos = new Setting<PositionSetting>("macroPos", new PositionSetting(0.3f, 0.3f), v -> false);
    public final Setting<PositionSetting> configPos = new Setting<PositionSetting>("configPos", new PositionSetting(0.35f, 0.35f), v -> false);
    public final Setting<PositionSetting> friendPos = new Setting<PositionSetting>("friendPos", new PositionSetting(0.4f, 0.4f), v -> false);
    public final Setting<PositionSetting> waypointPos = new Setting<PositionSetting>("waypointPos", new PositionSetting(0.45f, 0.45f), v -> false);
    public final Setting<PositionSetting> proxyPos = new Setting<PositionSetting>("proxyPos", new PositionSetting(0.5f, 0.5f), v -> false);

    public Windows() {
        super("Windows", Module.Category.CLIENT);
    }

    @Override
    public void onEnable() {
        mc.setScreen((Screen)new WindowsScreen(MacroWindow.get(this.macroPos.getValue().getX() * (float)mc.getWindow().getScaledWidth(), this.macroPos.getValue().getY() * (float)mc.getWindow().getScaledHeight(), this.macroPos), ConfigWindow.get(this.configPos.getValue().getX() * (float)mc.getWindow().getScaledWidth(), this.configPos.getValue().getY() * (float)mc.getWindow().getScaledHeight(), this.configPos), FriendsWindow.get(this.friendPos.getValue().getX() * (float)mc.getWindow().getScaledWidth(), this.friendPos.getValue().getY() * (float)mc.getWindow().getScaledHeight(), this.friendPos), WaypointWindow.get(this.waypointPos.getValue().getX() * (float)mc.getWindow().getScaledWidth(), this.waypointPos.getValue().getY() * (float)mc.getWindow().getScaledHeight(), this.waypointPos), ProxyWindow.get(this.proxyPos.getValue().getX() * (float)mc.getWindow().getScaledWidth(), this.proxyPos.getValue().getY() * (float)mc.getWindow().getScaledHeight(), this.proxyPos)));
        this.disable();
    }
}

