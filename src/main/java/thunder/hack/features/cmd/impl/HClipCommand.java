/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.util.Formatting
 *  net.minecraft.command.CommandSource
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround
 *  net.minecraft.util.math.MathHelper
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.util.Formatting;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.cmd.Command;

public class HClipCommand
extends Command {
    public HClipCommand() {
        super("hclip");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(HClipCommand.literal("s").executes(context -> {
            double x = -((double)MathHelper.sin((float)(HClipCommand.mc.player.getYaw() * ((float)Math.PI / 180))) * 0.8);
            double z = (double)MathHelper.cos((float)(HClipCommand.mc.player.getYaw() * ((float)Math.PI / 180))) * 0.8;
            for (int i = 0; i < 10; ++i) {
                HClipCommand.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(HClipCommand.mc.player.getX() + x, HClipCommand.mc.player.getY(), HClipCommand.mc.player.getZ() + z, false));
            }
            HClipCommand.mc.player.setPosition(HClipCommand.mc.player.getX() + x, HClipCommand.mc.player.getY(), HClipCommand.mc.player.getZ() + z);
            return 1;
        }));
        builder.then(HClipCommand.arg("count", DoubleArgumentType.doubleArg()).executes(context -> {
            double speed = (Double)context.getArgument("count", Double.class);
            try {
                HClipCommand.sendMessage(String.valueOf(Formatting.GREEN) + "\u043a\u043b\u0438\u043f\u0430\u0435\u043c\u0441\u044f \u043d\u0430  " + speed + " \u0431\u043b\u043e\u043a\u043e\u0432.");
                HClipCommand.mc.player.setPosition(HClipCommand.mc.player.getX() - (double)MathHelper.sin((float)(HClipCommand.mc.player.getYaw() * ((float)Math.PI / 180))) * speed, HClipCommand.mc.player.getY(), HClipCommand.mc.player.getZ() + (double)MathHelper.cos((float)(HClipCommand.mc.player.getYaw() * ((float)Math.PI / 180))) * speed);
            }
            catch (Exception exception) {
                // empty catch block
            }
            return 1;
        }));
        builder.executes(context -> {
            HClipCommand.sendMessage("\u041f\u043e\u043f\u0440\u043e\u0431\u0443\u0439 .hclip <\u0447\u0438\u0441\u043b\u043e>, .hclip s");
            return 1;
        });
    }
}

