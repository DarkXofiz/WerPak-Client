/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.util.Formatting
 *  net.minecraft.command.CommandSource
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Formatting;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.cmd.Command;

public class StaffCommand
extends Command {
    public static List<String> staffNames = new ArrayList<String>();

    public StaffCommand() {
        super("staff");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(StaffCommand.literal("reset").executes(context -> {
            staffNames.clear();
            StaffCommand.sendMessage("staff list got reset.");
            return 1;
        }));
        builder.then(StaffCommand.literal("add").then(StaffCommand.arg("name", StringArgumentType.word()).executes(context -> {
            String name = (String)context.getArgument("name", String.class);
            staffNames.add(name);
            StaffCommand.sendMessage(String.valueOf(Formatting.GREEN) + name + " added to staff list");
            return 1;
        })));
        builder.then(StaffCommand.literal("del").then(StaffCommand.arg("name", StringArgumentType.word()).executes(context -> {
            String name = (String)context.getArgument("name", String.class);
            staffNames.remove(name);
            StaffCommand.sendMessage(String.valueOf(Formatting.GREEN) + name + " removed from staff list");
            return 1;
        })));
        builder.executes(context -> {
            if (staffNames.isEmpty()) {
                StaffCommand.sendMessage("Staff list empty");
            } else {
                StringBuilder f = new StringBuilder("Staff: ");
                for (String staff : staffNames) {
                    try {
                        f.append(staff).append(", ");
                    }
                    catch (Exception exception) {}
                }
                StaffCommand.sendMessage(f.toString());
            }
            return 1;
        });
    }
}

