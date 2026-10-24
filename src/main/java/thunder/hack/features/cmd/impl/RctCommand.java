/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.command.CommandSource
 *  net.minecraft.scoreboard.ScoreboardObjective
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.scoreboard.ScoreboardObjective;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.modules.client.ClientSettings;

public class RctCommand
extends Command {
    public RctCommand() {
        super("rct");
    }

    @Override
    public void executeBuild(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            String sName;
            String string = sName = RctCommand.mc.player.networkHandler.getServerInfo() == null ? "none" : RctCommand.mc.player.networkHandler.getServerInfo().address;
            if (!sName.contains("funtime") && !sName.contains("spookytime")) {
                RctCommand.sendMessage(ClientSettings.isRu() ? "Rct \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u043d\u0430 \u0444\u0430\u043d\u0438\u043a\u0435 \u0438 \u0441\u043f\u0443\u043a\u0438" : "Rct works only on funtime and spookytime");
                return 1;
            }
            String an = "an" + ((ScoreboardObjective)RctCommand.mc.player.getScoreboard().getObjectives().toArray()[0]).getDisplayName().getString().substring(10);
            Managers.ASYNC.run(() -> {
                RctCommand.mc.player.networkHandler.sendCommand("hub");
                long failSafe = System.currentTimeMillis();
                while (ThunderHack.core.getSetBackTime() > 600L && System.currentTimeMillis() - failSafe <= 1000L) {
                }
                RctCommand.mc.player.networkHandler.sendCommand(an);
            });
            return 1;
        });
    }
}

