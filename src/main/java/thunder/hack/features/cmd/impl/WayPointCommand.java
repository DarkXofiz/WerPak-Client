/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  net.minecraft.command.CommandSource
 *  net.minecraft.util.math.BlockPos
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.world.WayPointManager;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.cmd.args.WayPointArgumentType;
import thunder.hack.features.modules.client.ClientSettings;

public class WayPointCommand
extends Command {
    public WayPointCommand() {
        super("waypoint", "waypoints");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(WayPointCommand.literal("list").executes(context -> {
            WayPointCommand.sendMessage(ClientSettings.isRu() ? "\u041c\u0435\u0442\u043a\u0438:" : "Waypoints:");
            WayPointCommand.sendMessage(" ");
            Managers.WAYPOINT.getWayPoints().forEach(wp -> WayPointCommand.sendMessage(wp.getName() + " X: " + wp.getX() + " Y: " + wp.getY() + " Z: " + wp.getZ() + " Server: " + wp.getServer() + " Dimension: " + wp.getDimension()));
            return 1;
        }));
        builder.then(WayPointCommand.literal("remove").then(WayPointCommand.arg("name", WayPointArgumentType.create()).executes(context -> {
            WayPointManager.WayPoint wp = (WayPointManager.WayPoint)context.getArgument("name", WayPointManager.WayPoint.class);
            Managers.WAYPOINT.removeWayPoint(wp);
            WayPointCommand.sendMessage((String)(ClientSettings.isRu() ? "\u0423\u0434\u0430\u043b\u0435\u043d\u0430 \u043c\u0435\u0442\u043a\u0430 " : "Deleted waypoint " + wp.getName()));
            return 1;
        })));
        builder.then(WayPointCommand.literal("add").then(((RequiredArgumentBuilder)WayPointCommand.arg("name", StringArgumentType.word()).executes(context -> {
            String name = (String)context.getArgument("name", String.class);
            WayPointManager.WayPoint wp = new WayPointManager.WayPoint((int)WayPointCommand.mc.player.getX(), (int)WayPointCommand.mc.player.getY(), (int)WayPointCommand.mc.player.getZ(), name, mc.isInSingleplayer() ? "SinglePlayer" : WayPointCommand.mc.getNetworkHandler().getServerInfo().address, WayPointCommand.mc.world.getRegistryKey().getValue().getPath());
            Managers.WAYPOINT.addWayPoint(wp);
            WayPointCommand.sendMessage((ClientSettings.isRu() ? "\u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u0430 \u043c\u0435\u0442\u043a\u0430 " + name + " \u0441 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0430\u043c\u0438" : "Added waypoint " + name + " with coords") + " X: " + (int)WayPointCommand.mc.player.getX() + " Y: " + (int)WayPointCommand.mc.player.getY() + " Z: " + (int)WayPointCommand.mc.player.getZ());
            return 1;
        })).then(WayPointCommand.arg("x", IntegerArgumentType.integer()).then(WayPointCommand.arg("y", IntegerArgumentType.integer()).then(WayPointCommand.arg("z", IntegerArgumentType.integer()).executes(context -> {
            String name = (String)context.getArgument("name", String.class);
            BlockPos pos = new BlockPos(((Integer)context.getArgument("x", Integer.class)).intValue(), ((Integer)context.getArgument("y", Integer.class)).intValue(), ((Integer)context.getArgument("z", Integer.class)).intValue());
            WayPointManager.WayPoint wp = new WayPointManager.WayPoint(pos.getX(), pos.getY(), pos.getZ(), name, mc.isInSingleplayer() ? "SinglePlayer" : WayPointCommand.mc.getNetworkHandler().getServerInfo().address, WayPointCommand.mc.world.getRegistryKey().getValue().getPath());
            Managers.WAYPOINT.addWayPoint(wp);
            WayPointCommand.sendMessage((ClientSettings.isRu() ? "\u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u0430 \u043c\u0435\u0442\u043a\u0430 " + name + " \u0441 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0430\u043c\u0438 X: " : "Added waypoint " + name + " with coords") + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ());
            return 1;
        }))))));
        builder.executes(context -> {
            WayPointCommand.sendMessage(this.usage());
            return 1;
        });
    }

    String usage() {
        return "waypoint add/remove/list (waypoint add x y z name), (waypoint remove name)";
    }
}

