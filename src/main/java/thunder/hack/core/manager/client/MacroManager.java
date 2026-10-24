/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.core.manager.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.CopyOnWriteArrayList;
import thunder.hack.core.manager.IManager;

public class MacroManager
implements IManager {
    private static CopyOnWriteArrayList<Macro> macros = new CopyOnWriteArrayList();

    public static void addMacro(Macro macro) {
        if (!macros.contains(macro)) {
            macros.add(macro);
        }
    }

    public void onLoad() {
        block8: {
            macros = new CopyOnWriteArrayList();
            try {
                File file = new File("ThunderHackRecode/misc/macro.txt");
                if (!file.exists()) break block8;
                try (BufferedReader reader = new BufferedReader(new FileReader(file));){
                    while (reader.ready()) {
                        String[] nameKey = reader.readLine().split(":");
                        String name = nameKey[0];
                        String key = nameKey[1];
                        String command = nameKey[2];
                        MacroManager.addMacro(new Macro(name, command, Integer.parseInt(key)));
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public void saveMacro() {
        File file = new File("ThunderHackRecode/misc/macro.txt");
        try {
            if (new File("ThunderHackRecode").mkdirs()) {
                file.createNewFile();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));){
            for (Macro macro : macros) {
                writer.write(macro.name + ":" + macro.bind + ":" + macro.text + "\n");
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void removeMacro(Macro macro) {
        macros.remove(macro);
    }

    public CopyOnWriteArrayList<Macro> getMacros() {
        return macros;
    }

    public Macro getMacroByName(String n) {
        for (Macro m : this.getMacros()) {
            if (!m.name.equalsIgnoreCase(n)) continue;
            return m;
        }
        return null;
    }

    public static class Macro {
        private String name;
        private String text;
        private int bind;

        public Macro(String name, String text, int bind) {
            this.name = name;
            this.text = text;
            this.bind = bind;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getBind() {
            return this.bind;
        }

        public void setBind(int bind) {
            this.bind = bind;
        }

        public void runMacro() {
            if (IManager.mc.player == null) {
                return;
            }
            if (this.text.contains("/")) {
                IManager.mc.player.networkHandler.sendChatCommand(this.text.replace("/", ""));
            } else {
                IManager.mc.player.networkHandler.sendChatMessage(this.text);
            }
        }
    }
}

