/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.util.Formatting
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.StringHelper
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.gui.screen.Screen
 */
package thunder.hack.gui.windows.impl;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.util.Formatting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.StringHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.Screen;
import thunder.hack.core.Managers;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.clickui.impl.SliderElement;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.windows.WindowBase;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.PositionSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

public class FriendsWindow
extends WindowBase {
    private static FriendsWindow instance;
    private ArrayList<FriendPlate> friendPlates = new ArrayList();
    private int listeningId = -1;
    private String search = "Search";
    private String addName = "Name";

    public FriendsWindow(float x, float y, float width, float height, Setting<PositionSetting> position) {
        super(x, y, width, height, "Friends", position, TextureStorage.playerIcon);
        this.refresh();
    }

    public static FriendsWindow get(float x, float y, Setting<PositionSetting> position) {
        if (instance == null) {
            instance = new FriendsWindow(x, y, 200.0f, 180.0f, position);
        }
        instance.refresh();
        return instance;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY) {
        super.render(context, mouseX, mouseY);
        Color color = new Color(-986500301, true);
        Color color2 = new Color(-983868581, true);
        Color hoveredColor = new Color(-985052855, true);
        int textColor = new Color(0xBDBDBD).getRGB();
        boolean hover1 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 90.0f, this.getY() + 3.0f, 70.0, 10.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + this.getWidth() - 90.0f, this.getY() + 3.0f, 70.0f, 10.0f, hover1 ? hoveredColor : color, color2);
        FontRenderers.sf_medium_mini.drawString(context.getMatrices(), this.search, (double)(this.getX() + this.getWidth() - 86.0f), (double)(this.getY() + 7.0f), new Color(0xD5D5D5).getRGB());
        if (this.friendPlates.isEmpty()) {
            FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), ClientSettings.isRu() ? "\u0422\u0443\u0442 \u043f\u043e\u043a\u0430 \u043f\u0443\u0441\u0442\u043e" : "It's empty here yet", (double)(this.getX() + this.getWidth() / 2.0f), (double)(this.getY() + this.getHeight() / 2.0f), new Color(0xBDBDBD).getRGB());
        }
        String blink2 = System.currentTimeMillis() / 240L % 2L == 0L ? "" : "l";
        boolean hover2 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + 11.0f, this.getY() + 19.0f, this.getWidth() - 28.0f, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + 11.0f, this.getY() + 19.0f, this.getWidth() - 28.0f, 11.0f, hover2 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addName + (this.listeningId == -3 ? blink2 : ""), (double)(this.getX() + 13.0f), (double)(this.getY() + 23.0f), new Color(0xBDBDBD).getRGB());
        boolean hover5 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 19.0f, 11.0, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + this.getWidth() - 15.0f, this.getY() + 19.0f, 11.0f, 11.0f, hover5 ? hoveredColor : color, color2);
        FontRenderers.categories.drawString(context.getMatrices(), "+", (double)(this.getX() + this.getWidth() - 12.0f), (double)(this.getY() + 23.0f), -1);
        Render2DEngine.horizontalGradient(context.getMatrices(), this.getX() + 2.0f, this.getY() + 33.0f, this.getX() + 2.0f + this.getWidth() / 2.0f - 2.0f, this.getY() + 33.5f, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
        Render2DEngine.horizontalGradient(context.getMatrices(), this.getX() + 2.0f + this.getWidth() / 2.0f - 2.0f, this.getY() + 33.0f, this.getX() + 2.0f + this.getWidth() - 4.0f, this.getY() + 33.5f, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
        Render2DEngine.addWindow(context.getMatrices(), this.getX(), this.getY() + 38.0f, this.getX() + this.getWidth(), this.getY() + this.getHeight() - 1.0f, 1.0);
        int id = 0;
        for (FriendPlate friendPlate : this.friendPlates) {
            ++id;
            if ((float)((int)(friendPlate.offset + this.getY() + 25.0f)) + this.getScrollOffset() > this.getY() + this.getHeight() || friendPlate.offset + this.getScrollOffset() + this.getY() + 10.0f < this.getY()) continue;
            boolean online = Module.mc.player != null && Module.mc.player.networkHandler.getPlayerList().stream().map(p -> p.getProfile().getName()).toList().contains(friendPlate.name()) || Managers.TELEMETRY.getOnlinePlayers().contains(friendPlate.name());
            Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + 11.0f, friendPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), this.getWidth() - 28.0f, 11.0f, color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), friendPlate.name() + (String)(online ? String.valueOf(Formatting.DARK_GRAY) + " l " + String.valueOf(Formatting.GREEN) + "Online" : ""), (double)(this.getX() + 13.0f), (double)(friendPlate.offset + this.getY() + 40.0f + this.getScrollOffset()), textColor);
            boolean hover52 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, friendPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), 11.0, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + this.getWidth() - 15.0f, friendPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), 11.0f, 11.0f, hover52 ? hoveredColor : color, color2);
            FontRenderers.icons.drawString(context.getMatrices(), "w", (double)(this.getX() + this.getWidth() - 15.0f), (double)(friendPlate.offset + this.getY() + 40.0f + this.getScrollOffset()), -1);
            FontRenderers.sf_medium_mini.drawString(context.getMatrices(), id + ".", (double)(this.getX() + 3.0f), (double)(friendPlate.offset + this.getY() + 41.0f + this.getScrollOffset()), textColor);
        }
        this.setMaxElementsHeight(this.friendPlates.size() * 20);
        Render2DEngine.popWindow();
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 90.0f, this.getY() + 3.0f, 70.0, 10.0)) {
            this.listeningId = -2;
            this.search = "";
        }
        if (Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 3.0f, 10.0, 10.0)) {
            Module.mc.setScreen((Screen)ClickGUI.getClickGui());
        }
        boolean hoveringName = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + 11.0f, this.getY() + 19.0f, this.getWidth() - 28.0f, 11.0);
        boolean hoveringAdd = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 19.0f, 11.0, 11.0);
        if (hoveringName) {
            this.addName = "";
            this.listeningId = -3;
        }
        if (hoveringAdd && !this.addName.isEmpty()) {
            Managers.FRIEND.addFriend(this.addName);
            this.addName = "";
            this.refresh();
        }
        var copy = Lists.newArrayList(this.friendPlates);
        for (FriendPlate friendPlate : copy) {
            boolean hoveringRemove;
            if ((float)((int)(friendPlate.offset + this.getY() + 50.0f)) + this.getScrollOffset() > this.getY() + this.getHeight() || !(hoveringRemove = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, friendPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), 11.0, 11.0))) continue;
            Managers.FRIEND.removeFriend(friendPlate.name());
            this.refresh();
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 70 && (InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)341) || InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)345))) {
            this.listeningId = -2;
            return;
        }
        if (this.listeningId != -1) {
            switch (keyCode) {
                case 257: {
                    if (!this.addName.isEmpty()) {
                        Managers.FRIEND.addFriend(this.addName);
                    }
                    if (this.listeningId != -2) {
                        this.listeningId = -1;
                    }
                    this.addName = "Name";
                    this.refresh();
                    break;
                }
                case 256: {
                    if (this.listeningId == -2) {
                        this.search = "Search";
                    }
                    this.listeningId = -1;
                    this.refresh();
                    break;
                }
                case 259: {
                    if (this.listeningId == -2) {
                        this.search = SliderElement.removeLastChar(this.search);
                        this.refresh();
                        if (Objects.equals(this.search, "")) {
                            this.listeningId = -1;
                            this.search = "Search";
                        }
                        return;
                    }
                    if (this.listeningId != -3) break;
                    this.addName = SliderElement.removeLastChar(this.addName);
                    break;
                }
                case 32: {
                    if (this.listeningId != -2) break;
                    this.search = this.search + " ";
                }
            }
        }
    }

    @Override
    public void charTyped(char key, int keyCode) {
        if (StringHelper.isValidChar((char)key) && this.listeningId != -1) {
            if (this.listeningId == -2) {
                this.search = this.search + key;
            }
            if (this.listeningId == -3) {
                this.addName = this.addName + key;
            }
            this.refresh();
        }
    }

    private void refresh() {
        this.resetScroll();
        this.friendPlates.clear();
        int id1 = 0;
        for (String f : Managers.FRIEND.getFriends()) {
            if (!this.search.equals("Search") && !this.search.isEmpty() && !f.contains(this.search)) continue;
            this.friendPlates.add(new FriendPlate(id1, id1 * 20 + 8, f));
            ++id1;
        }
    }

    private record FriendPlate(int id, float offset, String name) {
    }
}

