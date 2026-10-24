/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.command.CommandSource
 *  net.minecraft.util.math.BlockPos
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import thunder.hack.ThunderHack;
import thunder.hack.features.cmd.Command;

public class GpsCommand
extends Command {
    public GpsCommand() {
        super("gps");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(GpsCommand.literal("off").executes(context -> {
            ThunderHack.gps_position = null;
            return 1;
        }));
        builder.then(GpsCommand.arg("x", IntegerArgumentType.integer()).then(GpsCommand.arg("z", IntegerArgumentType.integer()).executes(context -> {
            int x = (Integer)context.getArgument("x", Integer.class);
            int z = (Integer)context.getArgument("z", Integer.class);
            ThunderHack.gps_position = new BlockPos(x, 0, z);
            GpsCommand.sendMessage("GPS \u043d\u0430\u0441\u0442\u0440\u043e\u0435\u043d \u043d\u0430 X: " + ThunderHack.gps_position.getX() + " Z: " + ThunderHack.gps_position.getZ());
            return 1;
        })));
        builder.executes(context -> {
            GpsCommand.sendMessage("\u041f\u043e\u043f\u0440\u043e\u0431\u0443\u0439 .gps off / .gps x z");
            return 1;
        });
    }
}

