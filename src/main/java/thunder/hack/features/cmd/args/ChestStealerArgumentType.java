/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.item.Item
 *  net.minecraft.command.CommandSource
 *  net.minecraft.block.Block
 *  net.minecraft.text.Text
 *  net.minecraft.registry.Registries
 */
package thunder.hack.features.cmd.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.item.Item;
import net.minecraft.command.CommandSource;
import net.minecraft.block.Block;
import net.minecraft.text.Text;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.modules.client.ClientSettings;

public class ChestStealerArgumentType
implements ArgumentType<String> {
    private static final List<String> EXAMPLES = ChestStealerArgumentType.getRegistered().stream().limit(5L).toList();

    public static ChestStealerArgumentType create() {
        return new ChestStealerArgumentType();
    }

    public String parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String blockName = reader.readString();
        if (!ChestStealerArgumentType.getRegistered().contains(blockName)) {
            throw new DynamicCommandExceptionType(name -> Text.literal((String)(ClientSettings.isRu() ? "\u0422\u0430\u043a\u043e\u0433\u043e \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430 \u043d\u0435\u0442!" : "There is no such item!"))).create((Object)blockName);
        }
        return blockName;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(ChestStealerArgumentType.getRegistered(), (SuggestionsBuilder)builder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @NotNull
    private static List<String> getRegistered() {
        ArrayList<String> result = new ArrayList<String>();
        for (Block block : Registries.BLOCK) {
            result.add(block.getTranslationKey().replace("block.minecraft.", ""));
        }
        for (Item item : Registries.ITEM) {
            result.add(item.getTranslationKey().replace("item.minecraft.", ""));
        }
        return result;
    }
}

