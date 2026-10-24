/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.command.CommandSource
 *  net.minecraft.client.network.PlayerListEntry
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.client.network.PlayerListEntry;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.cmd.args.FriendArgumentType;
import thunder.hack.features.cmd.args.PlayerArgumentType;

public class FriendCommand
extends Command {
    public FriendCommand() {
        super("friend", "friends");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(FriendCommand.literal("reset").executes(context -> {
            Managers.FRIEND.clear();
            FriendCommand.sendMessage("Friends got reset.");
            return 1;
        }));
        builder.then(FriendCommand.literal("add").then(FriendCommand.arg("player", PlayerArgumentType.create()).executes(context -> {
            PlayerListEntry player = (PlayerListEntry)context.getArgument("player", PlayerListEntry.class);
            Managers.FRIEND.addFriend(player.getProfile().getName());
            FriendCommand.sendMessage(player.getProfile().getName() + " has been friended");
            return 1;
        })));
        builder.then(FriendCommand.literal("remove").then(FriendCommand.arg("player", FriendArgumentType.create()).executes(context -> {
            String nickname = (String)context.getArgument("player", String.class);
            Managers.FRIEND.removeFriend(nickname);
            FriendCommand.sendMessage(nickname + " has been unfriended");
            return 1;
        })));
        builder.then(FriendCommand.literal("is").then(FriendCommand.arg("player", PlayerArgumentType.create()).executes(context -> {
            PlayerListEntry player = (PlayerListEntry)context.getArgument("player", PlayerListEntry.class);
            FriendCommand.sendMessage(player.getProfile().getName() + (Managers.FRIEND.isFriend(player.getProfile().getName()) ? " is friended." : " isn't friended."));
            return 1;
        })));
        builder.executes(context -> {
            if (Managers.FRIEND.getFriends().isEmpty()) {
                FriendCommand.sendMessage("Friend list empty D:");
            } else {
                StringBuilder f = new StringBuilder("Friends: ");
                for (String friend : Managers.FRIEND.getFriends()) {
                    try {
                        f.append(friend).append(", ");
                    }
                    catch (Exception exception) {}
                }
                FriendCommand.sendMessage(f.toString());
            }
            return 1;
        });
    }
}

