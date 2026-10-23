/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.multiplayer.AddServerScreen
 *  net.minecraft.client.gui.screen.TitleScreen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 */
package thunder.hack.features.modules.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;
import thunder.hack.utility.discord.DiscordEventHandlers;
import thunder.hack.utility.discord.DiscordRPC;
import thunder.hack.utility.discord.DiscordRichPresence;

public final class RPC
extends Module {
    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;
    public static Setting<String> appId = new Setting<String>("AppID", "1541752948806254654");
    public static Setting<Mode> mode = new Setting<Mode>("Picture", Mode.Zeltix);
    public static Setting<dMode> dmode = new Setting<dMode>("DetailsMode", dMode.Server);
    public static Setting<String> detailsText = new Setting<String>("CustomDetails", "Playing ZeltixWare", v -> dmode.getValue() == dMode.Custom);
    public static Setting<sMode> smode = new Setting<sMode>("StateMode", sMode.CreatedBy);
    public static Setting<String> authorText = new Setting<String>("AuthorText", "Created By Zaltre", v -> smode.getValue() == sMode.CreatedBy);
    public static Setting<String> state = new Setting<String>("CustomState", "Beta? Recode? NextGen?", v -> smode.getValue() == sMode.Custom);
    public static Setting<Boolean> showIP = new Setting<Boolean>("ShowIP", true, v -> smode.getValue() == sMode.Server || dmode.getValue() == dMode.Server);
    public static Setting<Boolean> nickname = new Setting<Boolean>("Nickname", true);
    private static final String DOWNLOAD_LABEL = "Discord";
    private static final String DOWNLOAD_URL = "https://discord.gg/zenobest";
    public static Setting<Boolean> secondButton = new Setting<Boolean>("SecondButton", true);
    public static Setting<String> button2Label = new Setting<String>("Button2Label", "Discord", v -> secondButton.getValue());
    public static Setting<String> button2Url = new Setting<String>("Button2URL", "https://discord.gg/4SYP6U5Uct", v -> secondButton.getValue());
    public static DiscordRichPresence presence = new DiscordRichPresence();
    public static boolean started;
    static String String1;
    private final Timer timer_delay = new Timer();
    private static Thread thread;
    private static String lastInitializedAppId;

    public RPC() {
        super("DiscordRPC", Module.Category.CLIENT);
    }

    public static void readFile() {
        block9: {
            try {
                File file = new File("WerPak/misc/RPC.txt");
                if (!file.exists()) {
                    file = new File("ThunderHackRecode/misc/RPC.txt");
                }
                if (!file.exists()) break block9;
                try (BufferedReader reader = new BufferedReader(new FileReader(file));){
                    while (reader.ready()) {
                        String1 = reader.readLine();
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public static void WriteFile(String url1, String url2) {
        File folder = new File("WerPak/misc");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, "RPC.txt");
        try {
            file.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));){
                writer.write(url1 + "SEPARATOR" + url2 + "\n");
            }
            catch (Exception exception) {}
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void disable() {
        this.setEnabled(true);
        if (!started) {
            this.startRpc();
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public List<Setting<?>> getSettings() {
        return new ArrayList();
    }

    @Override
    public void onEnable() {
        this.startRpc();
    }

    @Override
    public void onUpdate() {
        if (!started) {
            this.startRpc();
        }
    }

    public synchronized void stopRpc() {
        started = false;
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
            thread = null;
        }
        if (rpc != null) {
            rpc.Discord_Shutdown();
        }
        lastInitializedAppId = "";
    }

    public synchronized void startRpc() {
        String currentAppId;
        if (this.isDisabled() || rpc == null) {
            return;
        }
        String string = currentAppId = appId.getValue() == null || appId.getValue().trim().isEmpty() ? "1541752948806254654" : appId.getValue().trim();
        if (started && !currentAppId.equals(lastInitializedAppId)) {
            this.stopRpc();
        }
        if (!started) {
            started = true;
            lastInitializedAppId = currentAppId;
            DiscordEventHandlers handlers = new DiscordEventHandlers();
            rpc.Discord_Initialize(currentAppId, handlers, true, "");
            RPC.presence.startTimestamp = System.currentTimeMillis() / 1000L;
            RPC.presence.largeImageText = "ZeltixWare v1.7b2407 [" + ThunderHack.GITHUB_HASH + "]";
            rpc.Discord_UpdatePresence(presence);
            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    rpc.Discord_RunCallbacks();
                    RPC.presence.details = this.getDetails();
                    RPC.presence.state = this.getState();
                    if (nickname.getValue().booleanValue() && mc != null && mc.getSession() != null) {
                        RPC.presence.smallImageText = "logged as - " + mc.getSession().getUsername();
                        RPC.presence.smallImageKey = "https://minotar.net/helm/" + mc.getSession().getUsername() + "/100.png";
                    } else {
                        RPC.presence.smallImageText = "";
                        RPC.presence.smallImageKey = "";
                    }
                    RPC.presence.button_label_1 = DOWNLOAD_LABEL;
                    RPC.presence.button_url_1 = DOWNLOAD_URL;
                    if (secondButton.getValue().booleanValue() && button2Url.getValue() != null && !button2Url.getValue().isEmpty()) {
                        RPC.presence.button_label_2 = button2Label.getValue();
                        RPC.presence.button_url_2 = button2Url.getValue();
                    } else {
                        RPC.presence.button_label_2 = "";
                        RPC.presence.button_url_2 = "";
                    }
                    RPC.presence.largeImageText = "ZeltixWare v1.7b2407 [" + ThunderHack.GITHUB_HASH + "]";
                    switch (mode.getValue().ordinal()) {
                        case 0: {
                            RPC.presence.largeImageKey = "https://i.imgur.com/yY0z2Uq.gif";
                            break;
                        }
                        case 1: {
                            RPC.presence.largeImageKey = "https://i.imgur.com/yY0z2Uq.gif";
                            break;
                        }
                        case 2: {
                            RPC.presence.largeImageKey = "https://media1.tenor.com/images/6bcbfcc0be97d029613b54f97845bc59/tenor.gif?itemid=26823781";
                            break;
                        }
                        case 3: {
                            RPC.readFile();
                            String[] parts = String1.split("SEPARATOR");
                            if (parts.length > 0) {
                                RPC.presence.largeImageKey = parts[0];
                            }
                            if (parts.length <= 1 || Objects.equals(parts[1], "none")) break;
                            RPC.presence.smallImageKey = parts[1];
                        }
                    }
                    rpc.Discord_UpdatePresence(presence);
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException ignored) {
                        break;
                    }
                }
            }, "Zeltix-RPC-Handler");
            thread.start();
        }
    }

    private String getDetails() {
        switch (dmode.getValue().ordinal()) {
            case 1: {
                return "Playing ZeltixWare";
            }
            case 2: {
                return detailsText.getValue();
            }
            case 0: {
                return this.getServerInfo();
            }
        }
        return "Playing ZeltixWare";
    }

    private String getState() {
        switch (smode.getValue().ordinal()) {
            case 0: {
                return authorText.getValue();
            }
            case 1: {
                return this.getServerInfo();
            }
            case 2: {
                return "Hacks: " + Managers.MODULE.getEnabledModules().size() + " / " + Managers.MODULE.modules.size();
            }
            case 4: {
                return state.getValue();
            }
            case 3: {
                return "ZeltixWare v1.7b2407 for mc 1.21";
            }
        }
        return authorText.getValue();
    }

    private String getServerInfo() {
        String ip;
        if (mc == null || RPC.mc.world == null || RPC.mc.player == null) {
            if (mc != null && RPC.mc.currentScreen != null) {
                if (RPC.mc.currentScreen instanceof TitleScreen) {
                    return "In Main Menu";
                }
                if (RPC.mc.currentScreen instanceof MultiplayerScreen) {
                    return "In Server List";
                }
                if (RPC.mc.currentScreen instanceof AddServerScreen) {
                    return "Adding Server";
                }
                return "In Menu";
            }
            return "In Main Menu";
        }
        if (mc.isInSingleplayer() || mc.getCurrentServerEntry() == null) {
            return "Singleplayer";
        }
        if (showIP.getValue().booleanValue() && (ip = RPC.mc.getCurrentServerEntry().address) != null && !ip.isEmpty()) {
            return "Playing on " + ip;
        }
        return "Playing Multiplayer";
    }

    static {
        String1 = "none";
        lastInitializedAppId = "";
    }

    public static enum dMode {
        Server,
        ZeltixWare,
        Custom;

    }

    public static enum sMode {
        CreatedBy,
        Server,
        Stats,
        Version,
        Custom;

    }

    public static enum Mode {
        Zeltix,
        Recode,
        MegaCute,
        Custom;

    }
}

