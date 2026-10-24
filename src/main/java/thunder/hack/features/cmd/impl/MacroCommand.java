/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.command.CommandSource
 *  net.minecraft.client.util.InputUtil
 *  org.lwjgl.glfw.GLFW
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.lang.reflect.Field;
import net.minecraft.command.CommandSource;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.MacroManager;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.cmd.args.MacroArgumentType;
import thunder.hack.features.modules.client.ClientSettings;

public class MacroCommand
extends Command {
    public MacroCommand() {
        super("macro", "macros");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(MacroCommand.literal("list").executes(context -> {
            MacroCommand.sendMessage(ClientSettings.isRu() ? "\u041c\u0430\u043a\u0440\u043e\u0441\u044b:" : "Macros:");
            MacroCommand.sendMessage(" ");
            Managers.MACRO.getMacros().forEach(macro -> MacroCommand.sendMessage(macro.getName() + (String)(macro.getBind() != -1 ? " [" + this.toString(macro.getBind()) + "]" : "") + " {" + macro.getText() + "}"));
            return 1;
        }));
        builder.then(MacroCommand.literal("remove").then(MacroCommand.arg("macro", MacroArgumentType.create()).executes(context -> {
            MacroManager.Macro macro = (MacroManager.Macro)context.getArgument("macro", MacroManager.Macro.class);
            if (macro == null) {
                MacroCommand.sendMessage(ClientSettings.isRu() ? "\u041d\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442 \u0442\u0430\u043a\u043e\u0433\u043e \u043c\u0430\u043a\u0440\u043e\u0441\u0430!" : "Wrong macro name!");
                return 1;
            }
            Managers.MACRO.removeMacro(macro);
            MacroCommand.sendMessage((ClientSettings.isRu() ? "\u0423\u0434\u0430\u043b\u0435\u043d \u043c\u0430\u043a\u0440\u043e\u0441 " : "Removed macro ") + macro.getName());
            return 1;
        })));
        builder.then(MacroCommand.literal("add").then(MacroCommand.arg("name", StringArgumentType.word()).then(MacroCommand.arg("bind", StringArgumentType.word()).then(MacroCommand.arg("args", StringArgumentType.greedyString()).executes(context -> {
            String name = (String)context.getArgument("name", String.class);
            String bind = ((String)context.getArgument("bind", String.class)).toUpperCase();
            String args2 = (String)context.getArgument("args", String.class);
            if (InputUtil.fromTranslationKey((String)("key.keyboard." + bind.toLowerCase())).getCode() == -1) {
                MacroCommand.sendMessage(ClientSettings.isRu() ? "\u041d\u0435\u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u044b\u0439 \u0431\u0438\u043d\u0434!" : "Wrong bind!");
                return 1;
            }
            MacroManager.Macro macro = new MacroManager.Macro(name, args2, InputUtil.fromTranslationKey((String)("key.keyboard." + bind.toLowerCase())).getCode());
            MacroManager.addMacro(macro);
            MacroCommand.sendMessage(ClientSettings.isRu() ? "\u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d \u043c\u0430\u043a\u0440\u043e\u0441 " + name + " \u043d\u0430 \u043a\u043d\u043e\u043f\u043a\u0443 " + this.toString(macro.getBind()) : "Added macro " + name + " to " + this.toString(macro.getBind()));
            return 1;
        })))));
        builder.executes(context -> {
            MacroCommand.sendMessage(this.usage());
            return 1;
        });
    }

    public String toString(int key) {
        Object kn;
        Object object = kn = key > 0 ? GLFW.glfwGetKeyName((int)key, (int)GLFW.glfwGetKeyScancode((int)key)) : "None";
        if (kn == null) {
            try {
                for (Field declaredField : GLFW.class.getDeclaredFields()) {
                    int a;
                    if (!declaredField.getName().startsWith("GLFW_KEY_") || (a = ((Integer)declaredField.get(null)).intValue()) != key) continue;
                    String nb = declaredField.getName().substring("GLFW_KEY_".length());
                    kn = nb.substring(0, 1).toUpperCase() + nb.substring(1).toLowerCase();
                }
            }
            catch (Exception ignored) {
                kn = "unknown." + key;
            }
        }
        return key == -1 ? "None" : ((String)kn).toUpperCase();
    }

    String usage() {
        return "macro add/remove/list (macro add name key text), (macro remove name)";
    }
}

