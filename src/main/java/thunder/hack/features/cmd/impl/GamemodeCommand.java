/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.world.GameMode
 *  net.minecraft.command.CommandSource
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.world.GameMode;
import net.minecraft.command.CommandSource;
import thunder.hack.features.cmd.Command;

public class GamemodeCommand
extends Command {
    public GamemodeCommand() {
        super("gamemode", "gm");
    }

    @Override
    public void executeBuild(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(GamemodeCommand.arg("mode", StringArgumentType.greedyString()).executes(context -> {
            String mode;
            switch (mode = (String)context.getArgument("mode", String.class)) {
                case "survival": 
                case "0": {
                    GamemodeCommand.mc.interactionManager.setGameMode(GameMode.SURVIVAL);
                }
                case "creative": 
                case "1": {
                    GamemodeCommand.mc.interactionManager.setGameMode(GameMode.CREATIVE);
                }
                case "spectator": 
                case "2": {
                    GamemodeCommand.mc.interactionManager.setGameMode(GameMode.SPECTATOR);
                }
                case "adventure": 
                case "3": {
                    GamemodeCommand.mc.interactionManager.setGameMode(GameMode.ADVENTURE);
                }
            }
            return 1;
        }));
    }
}

