/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Util
 *  net.minecraft.command.CommandSource
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.io.File;
import java.util.Objects;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.cmd.args.CategoryArgumentType;
import thunder.hack.features.cmd.args.CfgArgumentType;
import thunder.hack.features.cmd.args.ModuleArgumentType;
import thunder.hack.features.modules.Module;

public class CfgCommand
extends Command {
    public CfgCommand() {
        super("cfg", "config");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            StringBuilder configs = new StringBuilder("Configs: ");
            for (String str : Objects.requireNonNull(Managers.CONFIG.getConfigList())) {
                configs.append("\n- " + String.valueOf(str.equals(Managers.CONFIG.getCurrentConfig().getName().replace(".th", "")) ? Formatting.GREEN : "")).append(str).append(Formatting.RESET);
            }
            CfgCommand.sendMessage(configs.toString());
            return 1;
        });
        builder.then(CfgCommand.literal("list").executes(context -> {
            StringBuilder configs = new StringBuilder("Configs: ");
            for (String str : Objects.requireNonNull(Managers.CONFIG.getConfigList())) {
                configs.append("\n- " + String.valueOf(str.equals(Managers.CONFIG.getCurrentConfig().getName().replace(".th", "")) ? Formatting.GREEN : "")).append(str).append(Formatting.RESET);
            }
            CfgCommand.sendMessage(configs.toString());
            return 1;
        }));
        builder.then(CfgCommand.literal("dir").executes(context -> {
            try {
                Util.getOperatingSystem().open(new File("ThunderHackRecode/configs/").toURI());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return 1;
        }));
        builder.then(CfgCommand.literal("save").then(CfgCommand.arg("name", StringArgumentType.word()).executes(context -> {
            Managers.CONFIG.save((String)context.getArgument("name", String.class));
            return 1;
        })));
        builder.then(CfgCommand.literal("loadcloud").then(CfgCommand.arg("name", StringArgumentType.word()).executes(context -> {
            Managers.CONFIG.loadCloud((String)context.getArgument("name", String.class));
            return 1;
        })));
        builder.then(CfgCommand.literal("cloudlist").executes(context -> {
            StringBuilder configs = new StringBuilder("Cloud Configs: \n");
            for (String str : Objects.requireNonNull(Managers.CONFIG.getCloudConfigs())) {
                String[] split = str.split(";");
                configs.append("\n- " + String.valueOf(Formatting.BOLD) + split[0] + String.valueOf(Formatting.RESET) + String.valueOf(Formatting.GRAY) + " author: " + String.valueOf(Formatting.RESET) + split[1] + String.valueOf(Formatting.GRAY) + " last updated: " + String.valueOf(Formatting.RESET) + split[2]);
            }
            CfgCommand.sendMessage(configs.toString());
            return 1;
        }));
        builder.then(CfgCommand.literal("cloud").then(CfgCommand.arg("name", StringArgumentType.word()).executes(context -> {
            Managers.CONFIG.loadCloud((String)context.getArgument("name", String.class));
            return 1;
        })));
        builder.then(CfgCommand.literal("set").then(((RequiredArgumentBuilder)CfgCommand.arg("name", CfgArgumentType.create()).then(CfgCommand.arg("module", ModuleArgumentType.create()).executes(context -> {
            Managers.CONFIG.loadModuleOnly((String)context.getArgument("name", String.class), (Module)context.getArgument("module", Module.class));
            return 1;
        }))).executes(context -> {
            Managers.CONFIG.load((String)context.getArgument("name", String.class));
            return 1;
        })));
        builder.then(CfgCommand.literal("load").then(((RequiredArgumentBuilder)CfgCommand.arg("name", CfgArgumentType.create()).then(CfgCommand.arg("module", ModuleArgumentType.create()).executes(context -> {
            Managers.CONFIG.loadModuleOnly((String)context.getArgument("name", String.class), (Module)context.getArgument("module", Module.class));
            return 1;
        }))).executes(context -> {
            Managers.CONFIG.load((String)context.getArgument("name", String.class));
            return 1;
        })));
        builder.then(CfgCommand.literal("loadCategory").then(CfgCommand.arg("name", CfgArgumentType.create()).then(CfgCommand.arg("category", CategoryArgumentType.create()).executes(context -> {
            Managers.CONFIG.load((String)context.getArgument("name", String.class), (String)context.getArgument("category", String.class));
            return 1;
        }))));
        builder.then(CfgCommand.literal("loadBinds").then(CfgCommand.arg("name", CfgArgumentType.create()).executes(context -> {
            Managers.CONFIG.loadBinds((String)context.getArgument("name", String.class));
            return 1;
        })));
    }
}

