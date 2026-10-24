/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.util.Formatting
 *  net.minecraft.command.CommandSource
 *  net.minecraft.text.Text
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.util.Formatting;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.features.cmd.Command;

public class HelpCommand
extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            HelpCommand.sendMessage("Commands: \n");
            AtomicBoolean flip = new AtomicBoolean(false);
            Managers.COMMAND.getCommands().forEach(command -> {
                HelpCommand.mc.player.sendMessage(Text.of((String)(String.valueOf(flip.get() ? Formatting.LIGHT_PURPLE : Formatting.DARK_PURPLE) + Managers.COMMAND.getPrefix() + String.valueOf(flip.get() ? Formatting.AQUA : Formatting.DARK_AQUA) + command.getName() + (String)(command.getAliases().isEmpty() ? "" : " (" + command.getAliases() + ")") + String.valueOf(Formatting.DARK_GRAY) + " -> " + String.valueOf(flip.get() ? Formatting.WHITE : Formatting.GRAY) + command.getDescription())));
                flip.set(!flip.get());
            });
            return 1;
        });
    }
}

