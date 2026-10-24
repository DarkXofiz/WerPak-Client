/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.suggestion.Suggestions
 *  net.minecraft.command.CommandSource
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.gui.screen.ChatInputSuggestor
 *  net.minecraft.client.gui.screen.ChatInputSuggestor.SuggestionWindow
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package thunder.hack.injection;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import thunder.hack.core.Managers;
import thunder.hack.features.modules.Module;

@Mixin(value={ChatInputSuggestor.class})
public abstract class MixinChatInputSuggestor {
    @Final
    @Shadow
    TextFieldWidget textField;
    @Shadow
    boolean completingSuggestions;
    @Shadow
    private ParseResults<CommandSource> parse;
    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;
    @Shadow
    private ChatInputSuggestor.SuggestionWindow window;

    @Shadow
    protected abstract void showCommandSuggestions();

    @Inject(method={"refresh"}, at={@At(value="INVOKE", target="Lcom/mojang/brigadier/StringReader;canRead()Z", remap=false)}, cancellable=true, locals=LocalCapture.CAPTURE_FAILHARD)
    public void refreshHook(CallbackInfo ci, String string, StringReader reader) {
        if (Module.fullNullCheck()) {
            return;
        }
        if (reader.canRead(Managers.COMMAND.getPrefix().length()) && reader.getString().startsWith(Managers.COMMAND.getPrefix(), reader.getCursor())) {
            int cursor;
            reader.setCursor(reader.getCursor() + 1);
            if (this.parse == null) {
                this.parse = Managers.COMMAND.getDispatcher().parse(reader, (Object)Managers.COMMAND.getSource());
            }
            if (!((cursor = this.textField.getCursor()) < 1 || this.window != null && this.completingSuggestions)) {
                this.pendingSuggestions = Managers.COMMAND.getDispatcher().getCompletionSuggestions(this.parse, cursor);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) {
                        this.showCommandSuggestions();
                    }
                });
            }
            ci.cancel();
        }
    }
}

