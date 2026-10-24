/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.gui.windows;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.core.manager.IManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.windows.WindowBase;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

public class WindowsScreen
extends Screen {
    private List<WindowBase> windows = new ArrayList<WindowBase>();
    public static WindowBase lastClickedWindow;
    public static WindowBase draggingWindow;
    private static final Identifier clickGuiIcon;

    public WindowsScreen(WindowBase ... windows) {
        super(Text.of((String)"THWindows"));
        this.windows.clear();
        lastClickedWindow = null;
        this.windows = Arrays.stream(windows).toList();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (Module.fullNullCheck()) {
            this.renderBackground(context, mouseX, mouseY, delta);
        }
        MatrixStack matrices = context.getMatrices();
        int i = IManager.mc.getWindow().getScaledWidth() / 2;
        float offset = (float)this.windows.size() * 20.0f / -2.0f - 23.0f;
        Render2DEngine.drawHudBase(matrices, (float)i + offset - 1.5f, IManager.mc.getWindow().getScaledHeight() - 25, (float)this.windows.size() * 20.0f + 23.0f, 19.0f, HudEditor.hudRound.getValue().floatValue());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)(Render2DEngine.isHovered(mouseX, mouseY, (float)i + offset + 1.0f, IManager.mc.getWindow().getScaledHeight() - 23, 15.0, 15.0) ? 0.95f : 0.7f));
        context.drawTexture(clickGuiIcon, (int)((float)i + offset) + 1, IManager.mc.getWindow().getScaledHeight() - 23, 15, 15, 0.0f, 0.0f, 15, 15, 15, 15);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.disableBlend();
        Render2DEngine.drawLine((float)i + offset + 20.0f, IManager.mc.getWindow().getScaledHeight() - 23, (float)i + offset + 20.0f, IManager.mc.getWindow().getScaledHeight() - 9, Color.GRAY.getRGB());
        offset += 23.0f;
        for (WindowBase w2 : this.windows) {
            Color c = Render2DEngine.isHovered(mouseX, mouseY, (float)i + offset, IManager.mc.getWindow().getScaledHeight() - 24, 17.0, 17.0) ? new Color(2083467055, true) : (!w2.isVisible() ? new Color(2082348574, true) : new Color(2084256571, true));
            Render2DEngine.drawRect(matrices, (float)i + offset, IManager.mc.getWindow().getScaledHeight() - 24, 17.0f, 17.0f, HudEditor.hudRound.getValue().floatValue(), 0.7f, c, c, c, c);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)(Render2DEngine.isHovered(mouseX, mouseY, (float)i + offset + 1.0f, IManager.mc.getWindow().getScaledHeight() - 23, 15.0, 15.0) ? 0.95f : 0.7f));
            context.drawTexture(w2.getIcon() != null ? w2.getIcon() : TextureStorage.configIcon, (int)((float)i + offset) + 3, IManager.mc.getWindow().getScaledHeight() - 21, 11, 11, 0.0f, 0.0f, 11, 11, 11, 11);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderSystem.disableBlend();
            offset += 20.0f;
        }
        this.windows.stream().filter(WindowBase::isVisible).forEach(w -> {
            if (w != lastClickedWindow) {
                w.render(context, mouseX, mouseY);
            }
        });
        if (lastClickedWindow != null && lastClickedWindow.isVisible()) {
            lastClickedWindow.render(context, mouseX, mouseY);
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.windows.forEach(w -> w.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.windows.stream().filter(WindowBase::isVisible).forEach(w -> w.mouseClicked(mouseX, mouseY, button));
        int i = IManager.mc.getWindow().getScaledWidth() / 2;
        float offset = (float)this.windows.size() * 20.0f / -2.0f - 23.0f;
        if (Render2DEngine.isHovered(mouseX, mouseY, (float)i + offset + 1.0f, IManager.mc.getWindow().getScaledHeight() - 23, 15.0, 15.0)) {
            IManager.mc.setScreen((Screen)ClickGUI.getClickGui());
        }
        offset += 23.0f;
        for (WindowBase w2 : this.windows) {
            if (Render2DEngine.isHovered(mouseX, mouseY, (float)i + offset, IManager.mc.getWindow().getScaledHeight() - 24, 17.0, 17.0)) {
                w2.setVisible(!w2.isVisible());
            }
            offset += 20.0f;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.windows.stream().filter(WindowBase::isVisible).forEach(w -> w.keyPressed(keyCode, scanCode, modifiers));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char key, int keyCode) {
        this.windows.stream().filter(WindowBase::isVisible).forEach(w -> w.charTyped(key, keyCode));
        return super.charTyped(key, keyCode);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.windows.stream().filter(WindowBase::isVisible).forEach(w -> w.mouseScrolled((int)(verticalAmount * 5.0)));
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    static {
        clickGuiIcon = Identifier.of((String)"thunderhack", (String)"textures/gui/elements/clickgui.png");
    }
}

