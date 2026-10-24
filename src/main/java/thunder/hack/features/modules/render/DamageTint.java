/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.features.modules.render;

import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.features.modules.Module;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;

public class DamageTint
extends Module {
    public DamageTint() {
        super("DamageTint", Module.Category.RENDER);
    }

    @Override
    public void onRender2D(DrawContext context) {
        float factor = 1.0f - MathUtility.clamp(DamageTint.mc.player.getHealth(), 0.0f, 12.0f) / 12.0f;
        Color red = new Color(0xFF0000, true);
        if (factor < 1.0f) {
            Render2DEngine.draw2DGradientRect(context.getMatrices(), 0.0f, 0.0f, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), Render2DEngine.injectAlpha(red, (int)(factor * 170.0f)), red, Render2DEngine.injectAlpha(red, (int)(factor * 170.0f)), red);
        }
    }
}

