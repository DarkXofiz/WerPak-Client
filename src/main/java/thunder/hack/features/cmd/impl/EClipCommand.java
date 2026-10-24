/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.util.Formatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.item.Items
 *  net.minecraft.command.CommandSource
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Position
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.util.Formatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.Items;
import net.minecraft.command.CommandSource;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.cmd.Command;
import thunder.hack.utility.player.InventoryUtility;

public class EClipCommand
extends Command {
    public EClipCommand() {
        super("eclip");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(((LiteralArgumentBuilder)EClipCommand.literal("bedrock").executes(context -> {
            this.execute(-((float)EClipCommand.mc.player.getY()) - 3.0f);
            return 1;
        })).then(EClipCommand.arg("number", FloatArgumentType.floatArg()).executes(context -> {
            float y = -((float)EClipCommand.mc.player.getY()) - 3.0f;
            if (y == 0.0f) {
                y = ((Float)context.getArgument("number", Float.class)).floatValue();
            }
            this.execute(y);
            return 1;
        })));
        builder.then(((LiteralArgumentBuilder)EClipCommand.literal("down").executes(context -> {
            float y = 0.0f;
            for (int i = 1; i < 255; ++i) {
                if (EClipCommand.mc.world.getBlockState(BlockPos.ofFloored((Position)EClipCommand.mc.player.getPos()).add(0, -i, 0)) == Blocks.AIR.getDefaultState()) {
                    y = -i - 1;
                    break;
                }
                if (EClipCommand.mc.world.getBlockState(BlockPos.ofFloored((Position)EClipCommand.mc.player.getPos()).add(0, -i, 0)) != Blocks.BEDROCK.getDefaultState()) continue;
                EClipCommand.sendMessage(String.valueOf(Formatting.RED) + " \u043c\u043e\u0436\u043d\u043e \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u043e\u0434 \u0431\u0435\u0434\u0440\u043e\u043a");
                EClipCommand.sendMessage(String.valueOf(Formatting.RED) + " eclip bedrock");
                return 1;
            }
            this.execute(y);
            return 1;
        })).then(EClipCommand.arg("number", FloatArgumentType.floatArg()).executes(context -> {
            float y = 0.0f;
            for (int i = 1; i < 255; ++i) {
                if (EClipCommand.mc.world.getBlockState(BlockPos.ofFloored((Position)EClipCommand.mc.player.getPos()).add(0, -i, 0)) == Blocks.AIR.getDefaultState()) {
                    y = -i - 1;
                    break;
                }
                if (EClipCommand.mc.world.getBlockState(BlockPos.ofFloored((Position)EClipCommand.mc.player.getPos()).add(0, -i, 0)) != Blocks.BEDROCK.getDefaultState()) continue;
                EClipCommand.sendMessage(String.valueOf(Formatting.RED) + " \u043c\u043e\u0436\u043d\u043e \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u043e\u0434 \u0431\u0435\u0434\u0440\u043e\u043a");
                EClipCommand.sendMessage(String.valueOf(Formatting.RED) + " eclip bedrock");
                return 1;
            }
            if (y == 0.0f) {
                y = ((Float)context.getArgument("number", Float.class)).floatValue();
            }
            this.execute(y);
            return 1;
        })));
        builder.then(((LiteralArgumentBuilder)EClipCommand.literal("up").executes(context -> {
            float y = 0.0f;
            for (int i = 4; i < 255; ++i) {
                if (EClipCommand.mc.world.getBlockState(BlockPos.ofFloored((Position)EClipCommand.mc.player.getPos()).add(0, i, 0)) != Blocks.AIR.getDefaultState()) continue;
                y = i + 1;
                break;
            }
            this.execute(y);
            return 1;
        })).then(EClipCommand.arg("number", FloatArgumentType.floatArg()).executes(context -> {
            float y = 0.0f;
            for (int i = 4; i < 255; ++i) {
                if (EClipCommand.mc.world.getBlockState(BlockPos.ofFloored((Position)EClipCommand.mc.player.getPos()).add(0, i, 0)) != Blocks.AIR.getDefaultState()) continue;
                y = i + 1;
                break;
            }
            if (y == 0.0f) {
                y = ((Float)context.getArgument("number", Float.class)).floatValue();
            }
            this.execute(y);
            return 1;
        })));
    }

    private void execute(float y) {
        int elytra = InventoryUtility.findItemInInventory(Items.FIREWORK_ROCKET).slot();
        if (elytra == -1) {
            EClipCommand.sendMessage(String.valueOf(Formatting.RED) + "\u0432\u0430\u043c \u043d\u0443\u0436\u043d\u044b \u044d\u043b\u0438\u0442\u0440\u044b \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435");
            return;
        }
        if (elytra != -2) {
            EClipCommand.mc.interactionManager.clickSlot(0, elytra, 1, SlotActionType.PICKUP, (PlayerEntity)EClipCommand.mc.player);
            EClipCommand.mc.interactionManager.clickSlot(0, 6, 1, SlotActionType.PICKUP, (PlayerEntity)EClipCommand.mc.player);
        }
        EClipCommand.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(EClipCommand.mc.player.getX(), EClipCommand.mc.player.getY(), EClipCommand.mc.player.getZ(), false));
        EClipCommand.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(EClipCommand.mc.player.getX(), EClipCommand.mc.player.getY(), EClipCommand.mc.player.getZ(), false));
        EClipCommand.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)EClipCommand.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        EClipCommand.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(EClipCommand.mc.player.getX(), EClipCommand.mc.player.getY() + (double)y, EClipCommand.mc.player.getZ(), false));
        EClipCommand.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)EClipCommand.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        if (elytra != -2) {
            EClipCommand.mc.interactionManager.clickSlot(0, 6, 1, SlotActionType.PICKUP, (PlayerEntity)EClipCommand.mc.player);
            EClipCommand.mc.interactionManager.clickSlot(0, elytra, 1, SlotActionType.PICKUP, (PlayerEntity)EClipCommand.mc.player);
        }
        EClipCommand.mc.player.setPosition(EClipCommand.mc.player.getX(), EClipCommand.mc.player.getY() + (double)y, EClipCommand.mc.player.getZ());
    }
}

