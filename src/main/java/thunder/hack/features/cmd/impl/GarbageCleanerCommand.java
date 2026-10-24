/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.command.CommandSource
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.cmd.Command;

public class GarbageCleanerCommand
extends Command {
    public GarbageCleanerCommand() {
        super("gc", "garbagecleaner", "clearram");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            GarbageCleanerCommand.sendMessage("Cleaning RAM..");
            System.gc();
            GarbageCleanerCommand.sendMessage("Successfully cleaned RAM!");
            return 1;
        });
    }
}

