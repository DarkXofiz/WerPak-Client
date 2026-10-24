/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.apache.commons.io.IOUtils
 */
package thunder.hack.features.modules.misc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.IOUtils;
import thunder.hack.core.Managers;
import thunder.hack.features.hud.impl.StaffBoard;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;

public class Spammer
extends Module {
    public static ArrayList<String> SpamList = new ArrayList();
    public Setting<Mode> mode = new Setting<Mode>("mode", Mode.Chat);
    public Setting<Messages> messages = new Setting<Messages>("messages", Messages.File);
    public Setting<WhisperPrefix> whisper_prefix = new Setting<WhisperPrefix>("prefix", WhisperPrefix.W, v -> this.mode.getValue() == Mode.Whispers);
    public Setting<Boolean> global = new Setting<Boolean>("global", true, v -> this.mode.getValue() == Mode.Chat);
    public Setting<Boolean> antiSpam = new Setting<Boolean>("AntiSpam", false);
    public Setting<Float> delay = new Setting<Float>("delay", Float.valueOf(5.0f), Float.valueOf(0.0f), Float.valueOf(30.0f));
    private final Timer timer_delay = new Timer();
    private final Random random = new Random();
    private String fact;

    public Spammer() {
        super("Spammer", Module.Category.MISC);
    }

    public static void loadSpammer() {
        try {
            File file = new File("ThunderHackRecode/misc/spammer.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            new Thread(() -> {
                try {
                    String line;
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader((InputStream)fis, StandardCharsets.UTF_8);
                    BufferedReader reader = new BufferedReader(isr);
                    ArrayList<String> lines = new ArrayList<String>();
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                    boolean newline = false;
                    for (String l : lines) {
                        if (!l.equals("")) continue;
                        newline = true;
                        break;
                    }
                    SpamList.clear();
                    ArrayList<String> spamList = new ArrayList<String>();
                    if (newline) {
                        StringBuilder spamChunk = new StringBuilder();
                        for (String l : lines) {
                            if (l.equals("")) {
                                if (spamChunk.isEmpty()) continue;
                                spamList.add(spamChunk.toString());
                                spamChunk = new StringBuilder();
                                continue;
                            }
                            spamChunk.append(l).append(" ");
                        }
                        spamList.add(spamChunk.toString());
                    } else {
                        spamList.addAll(lines);
                    }
                    SpamList = spamList;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }).start();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public String getPlayerName() {
        try {
            List<String> list = StaffBoard.getOnlinePlayer();
            if (list.isEmpty()) {
                return "";
            }
            return list.get(this.random.nextInt(0, list.size() - 1));
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    private void changeFact() {
        Managers.ASYNC.run(() -> {
            try {
                String jsonResponse = IOUtils.toString((URL)new URL("https://catfact.ninja/fact?max_length=200"), (Charset)StandardCharsets.UTF_8);
                JsonObject jsonObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                this.fact = jsonObject.get("fact").getAsString();
            }
            catch (IOException e) {
                this.disable(ClientSettings.isRu() ? "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u0444\u0430\u043a\u0442, \u043c\u043e\u0436\u0435\u0442 \u0442\u044b \u0432\u043a\u043b\u044e\u0447\u0438\u0448\u044c \u0438\u043d\u0442\u0435\u0440\u043d\u0435\u0442?" : "Failed to load the fact, can you turn on the Internet?");
            }
        });
    }

    public static String generateRandomSymbol() {
        Random random = new Random();
        String randomSymbol = "[";
        randomSymbol = (String)randomSymbol + (char)(random.nextInt(26) + 97);
        randomSymbol = (String)randomSymbol + random.nextInt(10);
        randomSymbol = (String)randomSymbol + (char)(random.nextInt(26) + 97);
        randomSymbol = (String)randomSymbol + "]";
        return randomSymbol;
    }

    @Override
    public void onEnable() {
        Spammer.loadSpammer();
        this.changeFact();
    }

    @Override
    public void onUpdate() {
        if (this.timer_delay.passedMs((long)(this.delay.getValue().floatValue() * 1000.0f))) {
            Object c;
            if (this.messages.getValue() == Messages.File) {
                if (SpamList.isEmpty()) {
                    this.disable(ClientSettings.isRu() ? "\u0424\u0430\u0439\u043b spammer \u043f\u0443\u0441\u0442\u043e\u0439!" : "The spammer file is empty!");
                    return;
                }
                c = SpamList.get(new Random().nextInt(SpamList.size()));
            } else {
                if (this.fact == null) {
                    return;
                }
                c = this.fact;
                this.changeFact();
            }
            if (this.antiSpam.getValue().booleanValue()) {
                c = (String)c + Spammer.generateRandomSymbol();
            }
            if (this.mode.getValue() == Mode.Chat) {
                if (((String)c).charAt(0) == '/') {
                    c = ((String)c).replace("/", "");
                    Spammer.mc.player.networkHandler.sendCommand((String)c);
                } else {
                    Spammer.mc.player.networkHandler.sendChatMessage((String)(this.global.getValue() != false ? "!" + (String)c : c));
                }
            } else {
                try {
                    String prefix = this.whisper_prefix.getValue().prefix;
                    Spammer.mc.player.networkHandler.sendCommand(prefix + this.getPlayerName() + " " + (String)c);
                }
                catch (NullPointerException nullPointerException) {
                    // empty catch block
                }
            }
            this.timer_delay.reset();
        }
    }

    private static enum Mode {
        Chat,
        Whispers;

    }

    private static enum Messages {
        File,
        CatFacts;

    }

    private static enum WhisperPrefix {
        W("w "),
        Msg("msg "),
        Tell("tell ");

        final String prefix;

        private WhisperPrefix(String p) {
            this.prefix = p;
        }
    }
}

