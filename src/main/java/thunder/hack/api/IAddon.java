/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package thunder.hack.api;

import com.mojang.logging.LogUtils;
import java.util.List;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.Module;

public interface IAddon {
    public void onInitialize();

    public List<Module> getModules();

    public List<Command> getCommands();

    public List<HudElement> getHudElements();

    public String getPackage();

    public String getName();

    public String getAuthor();

    public String getRepo();

    public String getVersion();

    default public String getDescription() {
        return "";
    }

    default public void onShutdown() {
        LogUtils.getLogger().info("Shutting down addon: " + this.getName());
    }
}

