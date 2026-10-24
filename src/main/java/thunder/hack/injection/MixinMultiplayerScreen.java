/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget.Builder
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package thunder.hack.injection;

import net.minecraft.text.Text;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.core.manager.IManager;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.gui.windows.WindowsScreen;
import thunder.hack.gui.windows.impl.ConfigWindow;
import thunder.hack.gui.windows.impl.FriendsWindow;
import thunder.hack.gui.windows.impl.MacroWindow;
import thunder.hack.gui.windows.impl.ProxyWindow;
import thunder.hack.gui.windows.impl.WaypointWindow;

@Mixin(value={MultiplayerScreen.class})
public abstract class MixinMultiplayerScreen
extends Screen {
    public MixinMultiplayerScreen(Text title) {
        super(title);
    }

    @Inject(method={"init"}, at={@At(value="RETURN")})
    private void initHook(CallbackInfo ci) {
        ButtonWidget.Builder builder = ButtonWidget.builder((Text)Text.literal((String)"\u26a1"), button -> IManager.mc.setScreen((Screen)new WindowsScreen(MacroWindow.get(ModuleManager.windows.macroPos.getValue().getX() * (float)IManager.mc.getWindow().getScaledWidth(), ModuleManager.windows.macroPos.getValue().getY() * (float)IManager.mc.getWindow().getScaledHeight(), ModuleManager.windows.macroPos), ConfigWindow.get(ModuleManager.windows.configPos.getValue().getX() * (float)IManager.mc.getWindow().getScaledWidth(), ModuleManager.windows.configPos.getValue().getY() * (float)IManager.mc.getWindow().getScaledHeight(), ModuleManager.windows.configPos), FriendsWindow.get(ModuleManager.windows.friendPos.getValue().getX() * (float)IManager.mc.getWindow().getScaledWidth(), ModuleManager.windows.friendPos.getValue().getY() * (float)IManager.mc.getWindow().getScaledHeight(), ModuleManager.windows.friendPos), WaypointWindow.get(ModuleManager.windows.waypointPos.getValue().getX() * (float)IManager.mc.getWindow().getScaledWidth(), ModuleManager.windows.waypointPos.getValue().getY() * (float)IManager.mc.getWindow().getScaledHeight(), ModuleManager.windows.waypointPos), ProxyWindow.get(ModuleManager.windows.proxyPos.getValue().getX() * (float)IManager.mc.getWindow().getScaledWidth(), ModuleManager.windows.proxyPos.getValue().getY() * (float)IManager.mc.getWindow().getScaledHeight(), ModuleManager.windows.proxyPos)))).size(60, 20);
        if (!ModuleManager.unHook.isEnabled()) {
            this.addDrawableChild(builder.position(this.width - 65, this.height - 25).build());
        }
    }
}

