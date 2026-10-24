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
import net.minecraft.util.Formatting;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.modules.client.ClientSettings;

public class PrefixCommand
extends Command {
    public PrefixCommand() {
        super("prefix");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(PrefixCommand.literal("set").then(PrefixCommand.arg("prefix", StringArgumentType.greedyString()).executes(context -> {
            String prefix = (String)context.getArgument("prefix", String.class);
            Managers.COMMAND.setPrefix(prefix);
            PrefixCommand.sendMessage(String.valueOf(Formatting.GREEN) + (ClientSettings.isRu() ? "\u041f\u0440\u0435\u0444\u0438\u043a\u0441 \u0438\u0437\u043c\u0435\u043d\u0435\u043d \u043d\u0430 " : "Changed prefix to ") + prefix);
            ClientSettings.prefix.setValue(prefix);
            return 1;
        })));
        builder.executes(context -> {
            PrefixCommand.sendMessage(String.valueOf(Formatting.GREEN) + (ClientSettings.isRu() ? "\u0422\u0435\u043a\u0443\u0449\u0438\u0439 \u043f\u0440\u0435\u0444\u0438\u043a\u0441: " : "Current prefix: ") + Managers.COMMAND.getPrefix());
            return 1;
        });
    }
}

