/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.StringHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.Screen;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.world.WayPointManager;
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

public class WaypointWindow
extends WindowBase {
    private static WaypointWindow instance;
    private ArrayList<WaypointPlate> waypointPlates = new ArrayList();
    private int listeningId = -1;
    private ListeningType listeningType;
    private String search = "Search";
    private String addName = "Name";
    private String addX = "X";
    private String addY = "Y";
    private String addZ = "Z";
    private String addServer = "2b2t.org";
    private String addDimension = "overworld";

    public WaypointWindow(float x, float y, float width, float height, Setting<PositionSetting> position) {
        super(x, y, width, height, "Waypoints", position, TextureStorage.waypointIcon);
        this.refresh();
    }

    public static WaypointWindow get(float x, float y, Setting<PositionSetting> position) {
        if (instance == null) {
            instance = new WaypointWindow(x, y, 340.0f, 180.0f, position);
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
        if (this.waypointPlates.isEmpty()) {
            FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), ClientSettings.isRu() ? "\u0422\u0443\u0442 \u043f\u043e\u043a\u0430 \u043f\u0443\u0441\u0442\u043e" : "It's empty here yet", (double)(this.getX() + this.getWidth() / 2.0f), (double)(this.getY() + this.getHeight() / 2.0f), new Color(0xBDBDBD).getRGB());
        }
        String blink = System.currentTimeMillis() / 240L % 2L == 0L ? "" : "l";
        float nameX = this.getX() + 11.0f;
        float nameWidth = this.getWidth() / 5.5f;
        float posXX = nameX + nameWidth + 2.0f;
        float posXWidth = this.getWidth() / 8.0f;
        float posYX = posXX + posXWidth + 2.0f;
        float posYWidth = this.getWidth() / 8.0f;
        float posZX = posYX + posYWidth + 2.0f;
        float posZWidth = this.getWidth() / 8.0f;
        float serverX = posZX + posZWidth + 2.0f;
        float serverWidth = this.getWidth() / 6.0f;
        float dimensionX = serverX + serverWidth + 2.0f;
        float dimensionWidth = this.getWidth() / 7.0f;
        boolean hover2 = Render2DEngine.isHovered(mouseX, mouseY, nameX, this.getY() + 19.0f, nameWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), nameX, this.getY() + 19.0f, nameWidth, 11.0f, hover2 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addName + (this.listeningId == -3 && this.listeningType == ListeningType.Name ? blink : ""), (double)(nameX + 2.0f), (double)(this.getY() + 23.0f), new Color(0xBDBDBD).getRGB());
        boolean hover3 = Render2DEngine.isHovered(mouseX, mouseY, posXX, this.getY() + 19.0f, posXWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), posXX, this.getY() + 19.0f, posXWidth, 11.0f, hover3 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addX + (this.listeningId == -3 && this.listeningType == ListeningType.X ? blink : ""), (double)(posXX + 2.0f), (double)(this.getY() + 23.0f), textColor);
        boolean hover4 = Render2DEngine.isHovered(mouseX, mouseY, posYX, this.getY() + 19.0f, posYWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), posYX, this.getY() + 19.0f, posYWidth, 11.0f, hover4 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addY + (this.listeningId == -3 && this.listeningType == ListeningType.Y ? blink : ""), (double)(posYX + 2.0f), (double)(this.getY() + 23.0f), new Color(0xBDBDBD).getRGB());
        boolean hover5 = Render2DEngine.isHovered(mouseX, mouseY, posZX, this.getY() + 19.0f, posZWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), posZX, this.getY() + 19.0f, posZWidth, 11.0f, hover5 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addZ + (this.listeningId == -3 && this.listeningType == ListeningType.Z ? blink : ""), (double)(posZX + 2.0f), (double)(this.getY() + 23.0f), new Color(0xBDBDBD).getRGB());
        boolean hover6 = Render2DEngine.isHovered(mouseX, mouseY, serverX, this.getY() + 19.0f, serverWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), serverX, this.getY() + 19.0f, serverWidth, 11.0f, hover6 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addServer + (this.listeningId == -3 && this.listeningType == ListeningType.Server ? blink : ""), (double)(serverX + 2.0f), (double)(this.getY() + 23.0f), new Color(0xBDBDBD).getRGB());
        boolean hover7 = Render2DEngine.isHovered(mouseX, mouseY, dimensionX, this.getY() + 19.0f, dimensionWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), dimensionX, this.getY() + 19.0f, dimensionWidth, 11.0f, hover7 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addDimension, (double)(dimensionX + 2.0f), (double)(this.getY() + 23.0f), new Color(0xBDBDBD).getRGB());
        boolean hover8 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 19.0f, 11.0, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + this.getWidth() - 15.0f, this.getY() + 19.0f, 11.0f, 11.0f, hover8 ? hoveredColor : color, color2);
        FontRenderers.categories.drawString(context.getMatrices(), "+", (double)(this.getX() + this.getWidth() - 12.0f), (double)(this.getY() + 23.0f), -1);
        Render2DEngine.horizontalGradient(context.getMatrices(), this.getX() + 2.0f, this.getY() + 33.0f, this.getX() + 2.0f + this.getWidth() / 2.0f - 2.0f, this.getY() + 33.5f, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
        Render2DEngine.horizontalGradient(context.getMatrices(), this.getX() + 2.0f + this.getWidth() / 2.0f - 2.0f, this.getY() + 33.0f, this.getX() + 2.0f + this.getWidth() - 4.0f, this.getY() + 33.5f, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "Name", (double)(nameX + nameWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "X", (double)(posXX + posXWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "Y", (double)(posYX + posYWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "Z", (double)(posZX + posZWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "Server", (double)(serverX + serverWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "Dimension", (double)(dimensionX + dimensionWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        Render2DEngine.addWindow(context.getMatrices(), this.getX(), this.getY() + 50.0f, this.getX() + this.getWidth(), this.getY() + this.getHeight() - 1.0f, 1.0);
        int id = 0;
        for (WaypointPlate waypointPlate : this.waypointPlates) {
            ++id;
            if ((float)((int)(waypointPlate.offset + this.getY() + 25.0f)) + this.getScrollOffset() > this.getY() + this.getHeight() || waypointPlate.offset + this.getScrollOffset() + this.getY() + 10.0f < this.getY()) continue;
            boolean hover22 = Render2DEngine.isHovered(mouseX, mouseY, nameX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, nameWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), nameX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, nameWidth, 11.0f, hover22 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), waypointPlate.waypoint.getName() + (this.listeningId == waypointPlate.id && this.listeningType == ListeningType.Name ? blink : ""), (double)(nameX + 2.0f), (double)(this.getY() + 40.0f + this.getScrollOffset() + waypointPlate.offset), new Color(0xBDBDBD).getRGB());
            boolean hover32 = Render2DEngine.isHovered(mouseX, mouseY, posXX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, posXWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), posXX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, posXWidth, 11.0f, hover32 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), waypointPlate.waypoint.getX() + (this.listeningId == waypointPlate.id && this.listeningType == ListeningType.X ? blink : ""), (double)(posXX + 2.0f), (double)(this.getY() + 40.0f + this.getScrollOffset() + waypointPlate.offset), textColor);
            boolean hover42 = Render2DEngine.isHovered(mouseX, mouseY, posYX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, posYWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), posYX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, posYWidth, 11.0f, hover42 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), waypointPlate.waypoint.getY() + (this.listeningId == waypointPlate.id && this.listeningType == ListeningType.Y ? blink : ""), (double)(posYX + 2.0f), (double)(this.getY() + 40.0f + this.getScrollOffset() + waypointPlate.offset), new Color(0xBDBDBD).getRGB());
            boolean hover52 = Render2DEngine.isHovered(mouseX, mouseY, posZX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, posZWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), posZX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, posZWidth, 11.0f, hover52 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), waypointPlate.waypoint.getZ() + (this.listeningId == waypointPlate.id && this.listeningType == ListeningType.Z ? blink : ""), (double)(posZX + 2.0f), (double)(this.getY() + 40.0f + this.getScrollOffset() + waypointPlate.offset), new Color(0xBDBDBD).getRGB());
            boolean hover62 = Render2DEngine.isHovered(mouseX, mouseY, serverX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, serverWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), serverX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, serverWidth, 11.0f, hover62 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), waypointPlate.waypoint.getServer() + (this.listeningId == waypointPlate.id && this.listeningType == ListeningType.Server ? blink : ""), (double)(serverX + 2.0f), (double)(this.getY() + 40.0f + this.getScrollOffset() + waypointPlate.offset), new Color(0xBDBDBD).getRGB());
            boolean hover72 = Render2DEngine.isHovered(mouseX, mouseY, dimensionX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, dimensionWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), dimensionX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, dimensionWidth, 11.0f, hover72 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), waypointPlate.waypoint.getDimension(), (double)(dimensionX + 2.0f), (double)(this.getY() + 40.0f + this.getScrollOffset() + waypointPlate.offset), new Color(0xBDBDBD).getRGB());
            boolean hover82 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, 11.0, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + this.getWidth() - 15.0f, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, 11.0f, 11.0f, hover82 ? hoveredColor : color, color2);
            FontRenderers.icons.drawString(context.getMatrices(), "w", (double)(this.getX() + this.getWidth() - 15.0f), (double)(waypointPlate.offset + this.getY() + 40.0f + this.getScrollOffset()), -1);
            FontRenderers.sf_medium_mini.drawString(context.getMatrices(), id + ".", (double)(this.getX() + 3.0f), (double)(this.getY() + 41.0f + this.getScrollOffset() + waypointPlate.offset), textColor);
        }
        this.setMaxElementsHeight(this.waypointPlates.size() * 20);
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
        float nameX = this.getX() + 11.0f;
        float nameWidth = this.getWidth() / 5.5f;
        float posXX = nameX + nameWidth + 2.0f;
        float posXWidth = this.getWidth() / 8.0f;
        float posYX = posXX + posXWidth + 2.0f;
        float posYWidth = this.getWidth() / 8.0f;
        float posZX = posYX + posYWidth + 2.0f;
        float posZWidth = this.getWidth() / 8.0f;
        float serverX = posZX + posZWidth + 2.0f;
        float serverWidth = this.getWidth() / 6.0f;
        float dimensionX = serverX + serverWidth + 2.0f;
        float dimensionWidth = this.getWidth() / 7.0f;
        boolean hoveringName = Render2DEngine.isHovered(mouseX, mouseY, nameX, this.getY() + 19.0f, nameWidth, 11.0);
        boolean hoveringX = Render2DEngine.isHovered(mouseX, mouseY, posXX, this.getY() + 19.0f, posXWidth, 11.0);
        boolean hoveringY = Render2DEngine.isHovered(mouseX, mouseY, posYX, this.getY() + 19.0f, posYWidth, 11.0);
        boolean hoveringZ = Render2DEngine.isHovered(mouseX, mouseY, posZX, this.getY() + 19.0f, posZWidth, 11.0);
        boolean hoveringServer = Render2DEngine.isHovered(mouseX, mouseY, serverX, this.getY() + 19.0f, serverWidth, 11.0);
        boolean hoveringDimension = Render2DEngine.isHovered(mouseX, mouseY, dimensionX, this.getY() + 19.0f, dimensionWidth, 11.0);
        boolean hoveringAdd = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 19.0f, 11.0, 11.0);
        if (hoveringName) {
            this.listeningType = ListeningType.Name;
            this.addName = "";
        }
        if (hoveringX) {
            this.listeningType = ListeningType.X;
            this.addX = "";
        }
        if (hoveringY) {
            this.listeningType = ListeningType.Y;
            this.addY = "";
        }
        if (hoveringZ) {
            this.listeningType = ListeningType.Z;
            this.addZ = "";
        }
        if (hoveringServer) {
            this.listeningType = ListeningType.Server;
            this.addServer = "";
        }
        if (hoveringDimension) {
            this.addDimension = this.switchType(this.addDimension);
        }
        if (hoveringName || hoveringX || hoveringY || hoveringZ || hoveringServer || hoveringDimension) {
            this.listeningId = -3;
        }
        if (hoveringAdd) {
            try {
                Managers.WAYPOINT.addWayPoint(new WayPointManager.WayPoint(Integer.parseInt(this.addX), Integer.parseInt(this.addY), Integer.parseInt(this.addZ), this.addName, this.addServer, this.addDimension));
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.refresh();
        }
        ArrayList copy = Lists.newArrayList(this.waypointPlates);
        for (WaypointPlate waypointPlate : copy) {
            if ((float)((int)(waypointPlate.offset + this.getY() + 50.0f)) + this.getScrollOffset() > this.getY() + this.getHeight()) continue;
            boolean hoveringName2 = Render2DEngine.isHovered(mouseX, mouseY, nameX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, nameWidth, 11.0);
            boolean hoveringX2 = Render2DEngine.isHovered(mouseX, mouseY, posXX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, posXWidth, 11.0);
            boolean hoveringY2 = Render2DEngine.isHovered(mouseX, mouseY, posYX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, posYWidth, 11.0);
            boolean hoveringZ2 = Render2DEngine.isHovered(mouseX, mouseY, posZX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, posZWidth, 11.0);
            boolean hoveringServer2 = Render2DEngine.isHovered(mouseX, mouseY, serverX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, serverWidth, 11.0);
            boolean hoveringDimension2 = Render2DEngine.isHovered(mouseX, mouseY, dimensionX, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, dimensionWidth, 11.0);
            boolean hoveringRemove = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 36.0f + this.getScrollOffset() + waypointPlate.offset, 11.0, 11.0);
            if (hoveringName2) {
                this.listeningType = ListeningType.Name;
            }
            if (hoveringX2) {
                this.listeningType = ListeningType.X;
            }
            if (hoveringY2) {
                this.listeningType = ListeningType.Y;
            }
            if (hoveringZ2) {
                this.listeningType = ListeningType.Z;
            }
            if (hoveringServer2) {
                this.listeningType = ListeningType.Server;
            }
            if (hoveringDimension2) {
                waypointPlate.waypoint.setDimension(this.switchType(waypointPlate.waypoint.getDimension()));
            }
            if (hoveringName2 || hoveringX2 || hoveringY2 || hoveringZ2 || hoveringServer2 || hoveringDimension2) {
                this.listeningId = waypointPlate.id;
            }
            if (!hoveringRemove) continue;
            Managers.WAYPOINT.removeWayPoint(waypointPlate.waypoint);
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
                    if (this.listeningId == -2) break;
                    this.listeningId = -1;
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
                    for (WaypointPlate plate : this.waypointPlates) {
                        if (this.listeningId != plate.id) continue;
                        switch (this.listeningType.ordinal()) {
                            case 0: {
                                plate.waypoint.setName(SliderElement.removeLastChar(plate.waypoint.getName()));
                                return;
                            }
                            case 4: {
                                plate.waypoint.setServer(SliderElement.removeLastChar(plate.waypoint.getServer()));
                                return;
                            }
                            case 1: {
                                String num = SliderElement.removeLastChar(String.valueOf(plate.waypoint.getX()));
                                if (num.isEmpty()) {
                                    num = "0";
                                }
                                plate.waypoint.setX(Integer.parseInt(num));
                                return;
                            }
                            case 2: {
                                String num = SliderElement.removeLastChar(String.valueOf(plate.waypoint.getY()));
                                if (num.isEmpty()) {
                                    num = "0";
                                }
                                plate.waypoint.setY(Integer.parseInt(num));
                                return;
                            }
                            case 3: {
                                String num = SliderElement.removeLastChar(String.valueOf(plate.waypoint.getZ()));
                                if (num.isEmpty()) {
                                    num = "0";
                                }
                                plate.waypoint.setZ(Integer.parseInt(num));
                                return;
                            }
                        }
                    }
                    if (this.listeningId != -3) break;
                    switch (this.listeningType.ordinal()) {
                        case 0: {
                            this.addName = SliderElement.removeLastChar(this.addName);
                            break;
                        }
                        case 4: {
                            this.addServer = SliderElement.removeLastChar(this.addServer);
                            break;
                        }
                        case 1: {
                            this.addX = SliderElement.removeLastChar(this.addX);
                            break;
                        }
                        case 2: {
                            this.addY = SliderElement.removeLastChar(this.addY);
                            break;
                        }
                        case 3: {
                            this.addZ = SliderElement.removeLastChar(this.addZ);
                        }
                    }
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
            for (WaypointPlate plate : this.waypointPlates) {
                if (this.listeningId != plate.id) continue;
                try {
                    switch (this.listeningType.ordinal()) {
                        case 0: {
                            plate.waypoint.setName(plate.waypoint.getName() + key);
                            break;
                        }
                        case 4: {
                            plate.waypoint.setServer(plate.waypoint.getServer() + key);
                            break;
                        }
                        case 1: {
                            plate.waypoint.setX(Integer.parseInt(String.valueOf(plate.waypoint.getX()) + key));
                            break;
                        }
                        case 2: {
                            plate.waypoint.setY(Integer.parseInt(String.valueOf(plate.waypoint.getY()) + key));
                            break;
                        }
                        case 3: {
                            plate.waypoint.setZ(Integer.parseInt(String.valueOf(plate.waypoint.getZ()) + key));
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if (this.listeningId == -3) {
                try {
                    switch (this.listeningType.ordinal()) {
                        case 0: {
                            this.addName = this.addName + key;
                            break;
                        }
                        case 4: {
                            this.addServer = this.addServer + key;
                            break;
                        }
                        case 1: {
                            this.addX = this.addX + key;
                            break;
                        }
                        case 2: {
                            this.addY = this.addY + key;
                            break;
                        }
                        case 3: {
                            this.addZ = this.addZ + key;
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.refresh();
        }
    }

    private void refresh() {
        this.resetScroll();
        this.waypointPlates.clear();
        int id1 = 0;
        for (WayPointManager.WayPoint w : Managers.WAYPOINT.getWayPoints()) {
            if (!this.search.equals("Search") && !this.search.isEmpty() && !w.getName().contains(this.search) && !w.getServer().contains(this.search)) continue;
            this.waypointPlates.add(new WaypointPlate(id1, id1 * 20 + 18, w));
            ++id1;
        }
        this.addServer = Module.mc.isInSingleplayer() || Module.fullNullCheck() ? "SinglePlayer" : Module.mc.getNetworkHandler().getServerInfo().address;
        this.addDimension = Module.fullNullCheck() ? "overworld" : Module.mc.world.getRegistryKey().getValue().getPath();
    }

    private String switchType(String t) {
        switch (t) {
            case "the_end": {
                return "overworld";
            }
            case "overworld": {
                return "the_nether";
            }
            case "the_nether": {
                return "the_end";
            }
        }
        return "overworld";
    }

    @Override
    public int getMinWidth() {
        return 340;
    }

    private static enum ListeningType {
        Name,
        X,
        Y,
        Z,
        Server;

    }

    private record WaypointPlate(int id, float offset, WayPointManager.WayPoint waypoint) {
    }
}

