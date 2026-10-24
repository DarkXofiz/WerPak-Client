/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 */
package thunder.hack.gui.misc;

import java.awt.Color;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.Render2DEngine;

public class DialogScreen
extends Screen {
    private final Identifier pic;
    private final String header;
    private final String description;
    private final String yesText;
    private final String noText;
    private final Runnable yesAction;
    private final Runnable noAction;

    public DialogScreen(Identifier pic, String header, String description, String yesText, String noText, Runnable yesAction, Runnable noAction) {
        super(Text.of((String)"ThDialogScreen"));
        this.pic = pic;
        this.header = header;
        this.description = description;
        this.yesText = yesText;
        this.noText = noText;
        this.yesAction = yesAction;
        this.noAction = noAction;
    }

    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        float halfOfWidth = (float)Module.mc.getWindow().getScaledWidth() / 2.0f;
        float halfOfHeight = (float)Module.mc.getWindow().getScaledHeight() / 2.0f;
        float mainX = halfOfWidth - 120.0f;
        float mainY = halfOfHeight - 80.0f;
        float mainWidth = 240.0f;
        float mainHeight = 140.0f;
        Render2DEngine.drawHudBase(context.getMatrices(), mainX, mainY, mainWidth, mainHeight, 20.0f, false);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), this.header, (double)(mainX + mainWidth / 2.0f), (double)(mainY + 5.0f), -1);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), this.description, (double)(mainX + mainWidth / 2.0f), (double)(mainY + 12.0f), new Color(-1409286145, true).getRGB());
        Render2DEngine.drawHudBase(context.getMatrices(), mainX + 5.0f, mainY + 95.0f, 110.0f, 40.0f, 15.0f, false);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), this.yesText, (double)(mainX + 60.0f), (double)(mainY + 112.0f), this.yesHovered(mouseX, mouseY) ? -1 : new Color(-1409286145, true).getRGB());
        Render2DEngine.drawHudBase(context.getMatrices(), mainX + 125.0f, mainY + 95.0f, 110.0f, 40.0f, 15.0f, false);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), this.noText, (double)(mainX + 180.0f), (double)(mainY + 112.0f), this.noHovered(mouseX, mouseY) ? -1 : new Color(-1409286145, true).getRGB());
        context.drawTexture(this.pic, (int)(mainX + mainWidth / 2.0f - 35.0f), (int)mainY + 25, 0.0f, 0.0f, 70, 65, 70, 65);
    }

    private boolean isHovered(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    private boolean yesHovered(int mX, int mY) {
        float mainX = (float)Module.mc.getWindow().getScaledWidth() / 2.0f - 120.0f;
        float mainY = (float)Module.mc.getWindow().getScaledHeight() / 2.0f - 80.0f;
        return this.isHovered(mX, mY, (int)mainX + 5, (int)mainY + 95, 110, 40);
    }

    private boolean noHovered(int mX, int mY) {
        float mainX = (float)Module.mc.getWindow().getScaledWidth() / 2.0f - 120.0f;
        float mainY = (float)Module.mc.getWindow().getScaledHeight() / 2.0f - 80.0f;
        return this.isHovered(mX, mY, (int)mainX + 125, (int)mainY + 95, 110, 40);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.yesHovered((int)mouseX, (int)mouseY)) {
            this.yesAction.run();
        } else if (this.noHovered((int)mouseX, (int)mouseY)) {
            this.noAction.run();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}

