/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.command.CommandSource
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.command.CommandRegistryAccess
 *  net.minecraft.registry.RegistryWrapper.WrapperLookup
 *  net.minecraft.registry.BuiltinRegistries
 */
package thunder.hack.features.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.command.CommandManager;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.BuiltinRegistries;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.manager.client.CommandManager;

public abstract class Command {
    protected static final CommandRegistryAccess REGISTRY_ACCESS = CommandManager.createRegistryAccess((RegistryWrapper.WrapperLookup)BuiltinRegistries.createWrapperLookup());
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    protected final List<String> names;
    private final String description;

    public Command(String ... names) {
        this.names = Arrays.asList(names);
        this.description = "descriptions.commands." + this.names.get(0);
    }

    public abstract void executeBuild(LiteralArgumentBuilder<CommandSource> var1);

    public static void sendMessage(String message) {
        if (Command.mc.player == null) {
            return;
        }
        Command.mc.player.sendMessage(Text.of((String)(CommandManager.getClientMessage() + " " + message)));
    }

    @NotNull
    protected static <T> RequiredArgumentBuilder<CommandSource, T> arg(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument((String)name, type);
    }

    @NotNull
    protected static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal((String)name);
    }

    public void register(CommandDispatcher<CommandSource> dispatcher) {
        for (String name : this.names) {
            LiteralArgumentBuilder builder = LiteralArgumentBuilder.literal((String)name);
            this.executeBuild((LiteralArgumentBuilder<CommandSource>)builder);
            dispatcher.register(builder);
        }
    }

    public String getName() {
        return this.names.get(0);
    }

    public String getAliases() {
        return String.join((CharSequence)", ", this.names.stream().filter(n -> !n.equals(this.names.get(0))).toList());
    }

    public String getDescription() {
        return I18n.translate((String)this.description, (Object[])new Object[0]);
    }
}

