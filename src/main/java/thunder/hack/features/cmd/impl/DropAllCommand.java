/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.command.CommandSource
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.features.cmd.Command;

public class DropAllCommand
extends Command {
    public DropAllCommand() {
        super("dropall", "drop");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(DropAllCommand.literal("legit").executes(context -> {
            Managers.ASYNC.run(() -> {
                for (int i = 5; i <= 45; ++i) {
                    DropAllCommand.mc.interactionManager.clickSlot(DropAllCommand.mc.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, (PlayerEntity)DropAllCommand.mc.player);
                    try {
                        Thread.sleep(70L);
                        continue;
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                DropAllCommand.mc.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(DropAllCommand.mc.player.currentScreenHandler.syncId));
            }, 1L);
            DropAllCommand.sendMessage("ok");
            return 1;
        }));
        builder.executes(context -> {
            for (int i = 5; i <= 45; ++i) {
                DropAllCommand.mc.interactionManager.clickSlot(DropAllCommand.mc.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, (PlayerEntity)DropAllCommand.mc.player);
            }
            DropAllCommand.mc.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(DropAllCommand.mc.player.currentScreenHandler.syncId));
            DropAllCommand.sendMessage("ok");
            return 1;
        });
    }
}

