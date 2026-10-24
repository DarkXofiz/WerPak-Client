/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.util.Formatting
 *  net.minecraft.command.CommandSource
 */
package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.util.Formatting;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.cmd.args.ModuleArgumentType;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;

public class DrawCommand
extends Command {
    public DrawCommand() {
        super("draw");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(DrawCommand.arg("module", ModuleArgumentType.create()).executes(context -> {
            Module module;
            module.setDrawn(!(module = (Module)context.getArgument("module", Module.class)).isDrawn());
            if (ClientSettings.isRu()) {
                DrawCommand.sendMessage("\u041c\u043e\u0434\u0443\u043b\u044c " + String.valueOf(Formatting.GREEN) + module.getName() + String.valueOf(Formatting.WHITE) + " \u0442\u0435\u043f\u0435\u0440\u044c " + (module.isDrawn() ? "\u0432\u0438\u0434\u0435\u043d \u0432 ArrayList" : "\u043d\u0435 \u0432\u0438\u0434\u0435\u043d \u0432 ArrayList"));
            } else {
                DrawCommand.sendMessage(String.valueOf(Formatting.GREEN) + module.getName() + String.valueOf(Formatting.WHITE) + " is now " + (module.isDrawn() ? "visible in ArrayList" : "invisible in ArrayList"));
            }
            return 1;
        }));
    }
}

