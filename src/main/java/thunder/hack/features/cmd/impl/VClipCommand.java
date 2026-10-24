/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.util.Formatting
 *  net.minecraft.command.CommandSource
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Position
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.util.Formatting;
import net.minecraft.command.CommandSource;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.modules.client.ClientSettings;

public class VClipCommand
extends Command {
    public VClipCommand() {
        super("vclip");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(VClipCommand.literal("down").executes(context -> {
            float y = 0.0f;
            for (int i = 1; i < 255; ++i) {
                if (VClipCommand.mc.world.getBlockState(BlockPos.ofFloored((Position)VClipCommand.mc.player.getPos()).add(0, -i, 0)) == Blocks.AIR.getDefaultState()) {
                    y = -i - 1;
                    break;
                }
                if (VClipCommand.mc.world.getBlockState(BlockPos.ofFloored((Position)VClipCommand.mc.player.getPos()).add(0, -i, 0)) != Blocks.BEDROCK.getDefaultState()) continue;
                VClipCommand.sendMessage(String.valueOf(Formatting.RED) + (ClientSettings.isRu() ? "\u041d\u0435\u043a\u0443\u0434\u0430 \u043a\u043b\u0438\u043f\u0430\u0442\u044c!" : "There's nowhere to clip!"));
                return 1;
            }
            this.clip(y);
            return 1;
        }));
        builder.then(VClipCommand.literal("up").executes(context -> {
            float y = 0.0f;
            for (int i = 4; i < 255; ++i) {
                if (VClipCommand.mc.world.getBlockState(BlockPos.ofFloored((Position)VClipCommand.mc.player.getPos()).add(0, i, 0)) != Blocks.AIR.getDefaultState()) continue;
                y = i + 1;
                break;
            }
            this.clip(y);
            return 1;
        }));
        builder.then(VClipCommand.arg("count", DoubleArgumentType.doubleArg()).executes(context -> {
            double count = (Double)context.getArgument("count", Double.class);
            try {
                VClipCommand.sendMessage(String.valueOf(Formatting.GREEN) + "\u041a\u043b\u0438\u043f\u0430\u0435\u043c\u0441\u044f \u043d\u0430 " + count + " \u0431\u043b\u043e\u043a\u043e\u0432");
                this.clip(count);
            }
            catch (Exception exception) {
                // empty catch block
            }
            return 1;
        }));
        builder.executes(context -> {
            VClipCommand.sendMessage("\u041f\u043e\u043f\u0440\u043e\u0431\u0443\u0439 .vclip <\u0447\u0438\u0441\u043b\u043e>");
            return 1;
        });
    }

    private void clip(double b) {
        if (ClientSettings.clipCommandMode.getValue() == ClientSettings.ClipCommandMode.Matrix) {
            int i;
            for (i = 0; i < 10; ++i) {
                VClipCommand.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(VClipCommand.mc.player.getX(), VClipCommand.mc.player.getY(), VClipCommand.mc.player.getZ(), false));
            }
            for (i = 0; i < 10; ++i) {
                VClipCommand.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(VClipCommand.mc.player.getX(), VClipCommand.mc.player.getY() + b, VClipCommand.mc.player.getZ(), false));
            }
        } else {
            VClipCommand.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(VClipCommand.mc.player.getX(), VClipCommand.mc.player.getY() + b, VClipCommand.mc.player.getZ(), false));
        }
        VClipCommand.mc.player.setPosition(VClipCommand.mc.player.getX(), VClipCommand.mc.player.getY() + b, VClipCommand.mc.player.getZ());
    }
}

