/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.OrderedText
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import net.minecraft.text.Text;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.gui.font.FontRenderers;

@Mixin(value={DrawContext.class})
public class MixinDrawContext {
    @Shadow
    @Final
    private MatrixStack matrices;

    public void drawTextHook(TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow, CallbackInfoReturnable<Integer> cir) {
        MutableText text1 = Text.empty();
        text.accept((i, style, codePoint) -> {
            text1.append((Text)Text.literal((String)new String(Character.toChars(codePoint))).setStyle(style));
            return true;
        });
        FontRenderers.sf_medium.drawString(this.matrices, text1.getString(), (double)x, (double)y, color);
        cir.setReturnValue(((int)FontRenderers.sf_medium.getStringWidth(text.toString())));
    }
}

