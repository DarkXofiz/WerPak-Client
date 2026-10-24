/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.util.Formatting
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.PotionItem
 *  net.minecraft.component.type.PotionContentsComponent
 *  net.minecraft.command.CommandSource
 *  net.minecraft.component.DataComponentTypes
 */
package thunder.hack.features.cmd.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import net.minecraft.util.Formatting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.modules.client.ClientSettings;

public class KitCommand
extends Command {
    private static final String PATH = "ThunderHackRecode/misc/AutoGear.json";

    public KitCommand() {
        super("kit");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(KitCommand.literal("list").executes(context -> {
            this.listMessage();
            return 1;
        }));
        builder.then(KitCommand.literal("create").then(KitCommand.arg("name", StringArgumentType.word()).executes(context -> {
            this.save((String)context.getArgument("name", String.class));
            return 1;
        })));
        builder.then(KitCommand.literal("set").then(KitCommand.arg("name", StringArgumentType.word()).executes(context -> {
            this.set((String)context.getArgument("name", String.class));
            return 1;
        })));
        builder.then(KitCommand.literal("del").then(KitCommand.arg("name", StringArgumentType.word()).executes(context -> {
            this.delete((String)context.getArgument("name", String.class));
            return 1;
        })));
        builder.executes(context -> {
            KitCommand.sendMessage("kit <create/set/del/list> <name>");
            return 1;
        });
    }

    public static String getSelectedKit() {
        try {
            JsonObject json = new JsonParser().parse((Reader)new FileReader(PATH)).getAsJsonObject();
            if (!json.get("selected").getAsString().equals("none")) {
                return json.get("selected").getAsString();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        KitCommand.sendMessage(ClientSettings.isRu() ? "\u041a\u0438\u0442 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d" : "Kit not found");
        return "";
    }

    public static String getKitItems(String kit) {
        try {
            JsonObject json = new JsonParser().parse((Reader)new FileReader(PATH)).getAsJsonObject();
            return json.get(kit).getAsString();
        }
        catch (Exception exception) {
            KitCommand.sendMessage(ClientSettings.isRu() ? "\u041a\u0438\u0442 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d" : "Kit not found");
            return "";
        }
    }

    private void listMessage() {
        try {
            JsonObject json = new JsonParser().parse((Reader)new FileReader(PATH)).getAsJsonObject();
            KitCommand.sendMessage(ClientSettings.isRu() ? "\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u0435 \u043a\u0438\u0442\u044b:" : "Available kits:");
            for (int i = 0; i < json.entrySet().size(); ++i) {
                String item = json.entrySet().toArray()[i].toString().split("=")[0];
                KitCommand.sendMessage(String.valueOf(Formatting.GRAY) + "-> " + item + (item.equals("selected") ? (ClientSettings.isRu() ? "(\u0412\u044b\u0431\u0440\u0430\u043d)" : " (Selected)") : ""));
            }
        }
        catch (Exception e) {
            KitCommand.sendMessage(ClientSettings.isRu() ? "\u041f\u0440\u043e\u0431\u043b\u0435\u043c\u0430 \u0441 \u043a\u043e\u043d\u0444\u0438\u0433\u0443\u0440\u0430\u0446\u0438\u0435\u0439 \u043a\u0438\u0442\u043e\u0432!" : "Error with kit cfg!");
        }
    }

    private void delete(String name) {
        try {
            JsonObject json = new JsonParser().parse((Reader)new FileReader(PATH)).getAsJsonObject();
            if (json.get(name) != null && !name.equals("selected")) {
                json.remove(name);
                if (json.get("selected").getAsString().equals(name)) {
                    json.addProperty("selected", "none");
                }
                this.saveFile(json, name, ClientSettings.isRu() ? "\u0443\u0434\u0430\u043b\u0435\u043d" : "deleted");
            } else {
                KitCommand.sendMessage("Kit not found");
            }
        }
        catch (Exception e) {
            KitCommand.sendMessage(ClientSettings.isRu() ? "\u041a\u0438\u0442 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d" : "Kit not found");
        }
    }

    private void set(String name) {
        try {
            JsonObject json = new JsonParser().parse((Reader)new FileReader(PATH)).getAsJsonObject();
            if (json.get(name) != null && !name.equals("selected")) {
                json.addProperty("selected", name);
                this.saveFile(json, name, ClientSettings.isRu() ? "\u0432\u044b\u0431\u0440\u0430\u043d" : "selected");
            } else {
                KitCommand.sendMessage(ClientSettings.isRu() ? "\u041a\u0438\u0442 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d" : "Kit not found");
            }
        }
        catch (Exception e) {
            KitCommand.sendMessage(ClientSettings.isRu() ? "\u041a\u0438\u0442 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d" : "Kit not found");
        }
    }

    private void save(String name) {
        JsonObject json = new JsonObject();
        try {
            json = new JsonParser().parse((Reader)new FileReader(PATH)).getAsJsonObject();
            if (json.get(name) != null && !name.equals("selected")) {
                KitCommand.sendMessage(ClientSettings.isRu() ? "\u042d\u0442\u043e\u0442 \u043a\u0438\u0442 \u0443\u0436\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442" : "This kit arleady exist");
                return;
            }
        }
        catch (IOException e) {
            json.addProperty("selected", "none");
        }
        StringBuilder jsonInventory = new StringBuilder();
        for (ItemStack item : KitCommand.mc.player.getInventory().main) {
            jsonInventory.append((String)(item.getItem() instanceof PotionItem ? item.getItem().getTranslationKey() + ((PotionContentsComponent)item.getItem().getComponents().get(DataComponentTypes.POTION_CONTENTS)).getColor() : item.getItem().getTranslationKey())).append(" ");
        }
        json.addProperty(name, jsonInventory.toString());
        this.saveFile(json, name, ClientSettings.isRu() ? "\u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d" : "saved");
    }

    private void saveFile(@NotNull JsonObject completeJson, String name, String operation) {
        try {
            File file = new File(PATH);
            try {
                file.createNewFile();
            }
            catch (Exception exception) {
                // empty catch block
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(PATH));
            bw.write(completeJson.toString());
            bw.close();
            KitCommand.sendMessage((ClientSettings.isRu() ? "\u041a\u0438\u0442 " : "Kit ") + String.valueOf(Formatting.AQUA) + name + String.valueOf(Formatting.RESET) + " " + operation);
        }
        catch (IOException e) {
            KitCommand.sendMessage(ClientSettings.isRu() ? "\u041e\u0448\u0438\u0431\u043a\u0430 \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0438\u044f \u0444\u0430\u0439\u043b\u0430" : "Error saving the file");
        }
    }
}

