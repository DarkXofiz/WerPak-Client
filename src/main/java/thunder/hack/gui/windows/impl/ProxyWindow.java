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
 *  org.lwjgl.glfw.GLFW
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
import org.lwjgl.glfw.GLFW;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ProxyManager;
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

public class ProxyWindow
extends WindowBase {
    private static ProxyWindow instance;
    private ArrayList<ProxyPlate> proxyPlates = new ArrayList();
    private int listeningId = -1;
    private ListeningType listeningType;
    private String search = "Search";
    private String addIp = "Ip";
    private String addPort = "Port";
    private String addPassword = "Password";
    private String addLogin = "Login";
    private String addName = "Name";
    private String[] pinging = new String[]{".", "..", "..."};

    public ProxyWindow(float x, float y, float width, float height, Setting<PositionSetting> position) {
        super(x, y, width, height, "Proxies", position, TextureStorage.proxyIcon);
        this.refresh();
    }

    public static ProxyWindow get(float x, float y, Setting<PositionSetting> position) {
        if (instance == null) {
            instance = new ProxyWindow(x, y, 340.0f, 180.0f, position);
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
        if (this.proxyPlates.isEmpty()) {
            FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), ClientSettings.isRu() ? "\u0422\u0443\u0442 \u043f\u043e\u043a\u0430 \u043f\u0443\u0441\u0442\u043e" : "It's empty here yet", (double)(this.getX() + this.getWidth() / 2.0f), (double)(this.getY() + this.getHeight() / 2.0f), new Color(0xBDBDBD).getRGB());
        }
        String blink = System.currentTimeMillis() / 240L % 2L == 0L ? "" : "l";
        float nameX = this.getX() + 11.0f;
        float nameWidth = this.getWidth() / 5.5f;
        float ipX = nameX + nameWidth + 2.0f;
        float posXWidth = this.getWidth() / 5.0f;
        float portX = ipX + posXWidth + 2.0f;
        float posYWidth = this.getWidth() / 12.0f;
        float posZX = portX + posYWidth + 2.0f;
        float posZWidth = this.getWidth() / 6.5f;
        float serverX = posZX + posZWidth + 2.0f;
        float serverWidth = this.getWidth() / 6.0f;
        boolean hover2 = Render2DEngine.isHovered(mouseX, mouseY, nameX, this.getY() + 19.0f, nameWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), nameX, this.getY() + 19.0f, nameWidth, 11.0f, hover2 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addName + (this.listeningId == -3 && this.listeningType == ListeningType.Name ? blink : ""), (double)(nameX + 2.0f), (double)(this.getY() + 23.0f), new Color(0xBDBDBD).getRGB());
        boolean hover3 = Render2DEngine.isHovered(mouseX, mouseY, ipX, this.getY() + 19.0f, posXWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), ipX, this.getY() + 19.0f, posXWidth, 11.0f, hover3 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addIp + (this.listeningId == -3 && this.listeningType == ListeningType.Ip ? blink : ""), (double)(ipX + 2.0f), (double)(this.getY() + 23.0f), textColor);
        boolean hover4 = Render2DEngine.isHovered(mouseX, mouseY, portX, this.getY() + 19.0f, posYWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), portX, this.getY() + 19.0f, posYWidth, 11.0f, hover4 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addPort + (this.listeningId == -3 && this.listeningType == ListeningType.Port ? blink : ""), (double)(portX + 2.0f), (double)(this.getY() + 23.0f), new Color(0xBDBDBD).getRGB());
        boolean hover5 = Render2DEngine.isHovered(mouseX, mouseY, posZX, this.getY() + 19.0f, posZWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), posZX, this.getY() + 19.0f, posZWidth, 11.0f, hover5 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addLogin + (this.listeningId == -3 && this.listeningType == ListeningType.Login ? blink : ""), (double)(posZX + 2.0f), (double)(this.getY() + 23.0f), new Color(0xBDBDBD).getRGB());
        boolean hover6 = Render2DEngine.isHovered(mouseX, mouseY, serverX, this.getY() + 19.0f, serverWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), serverX, this.getY() + 19.0f, serverWidth, 11.0f, hover6 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addPassword + (this.listeningId == -3 && this.listeningType == ListeningType.Password ? blink : ""), (double)(serverX + 2.0f), (double)(this.getY() + 23.0f), new Color(0xBDBDBD).getRGB());
        boolean hover8 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 19.0f, 11.0, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + this.getWidth() - 15.0f, this.getY() + 19.0f, 11.0f, 11.0f, hover8 ? hoveredColor : color, color2);
        FontRenderers.categories.drawString(context.getMatrices(), "+", (double)(this.getX() + this.getWidth() - 12.0f), (double)(this.getY() + 23.0f), -1);
        Render2DEngine.horizontalGradient(context.getMatrices(), this.getX() + 2.0f, this.getY() + 33.0f, this.getX() + 2.0f + this.getWidth() / 2.0f - 2.0f, this.getY() + 33.5f, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
        Render2DEngine.horizontalGradient(context.getMatrices(), this.getX() + 2.0f + this.getWidth() / 2.0f - 2.0f, this.getY() + 33.0f, this.getX() + 2.0f + this.getWidth() - 4.0f, this.getY() + 33.5f, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "Name", (double)(nameX + nameWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "IP", (double)(ipX + posXWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "Port", (double)(portX + posYWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "Login", (double)(posZX + posZWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "Password", (double)(serverX + serverWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        Render2DEngine.addWindow(context.getMatrices(), this.getX(), this.getY() + 50.0f, this.getX() + this.getWidth(), this.getY() + this.getHeight() - 1.0f, 1.0);
        int id = 0;
        for (ProxyPlate proxyPlate : this.proxyPlates) {
            ++id;
            if ((float)((int)(proxyPlate.offset + this.getY() + 25.0f)) + this.getScrollOffset() > this.getY() + this.getHeight() || proxyPlate.offset + this.getScrollOffset() + this.getY() + 10.0f < this.getY()) continue;
            boolean hover22 = Render2DEngine.isHovered(mouseX, mouseY, nameX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, nameWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), nameX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, nameWidth, 11.0f, hover22 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), proxyPlate.proxy.getName() + (this.listeningId == proxyPlate.id && this.listeningType == ListeningType.Name ? blink : ""), (double)(nameX + 2.0f), (double)(this.getY() + 40.0f + this.getScrollOffset() + proxyPlate.offset), new Color(0xBDBDBD).getRGB());
            boolean hover32 = Render2DEngine.isHovered(mouseX, mouseY, ipX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, posXWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), ipX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, posXWidth, 11.0f, hover32 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), proxyPlate.proxy.getIp() + (this.listeningId == proxyPlate.id && this.listeningType == ListeningType.Ip ? blink : ""), (double)(ipX + 2.0f), (double)(this.getY() + 40.0f + this.getScrollOffset() + proxyPlate.offset), textColor);
            boolean hover42 = Render2DEngine.isHovered(mouseX, mouseY, portX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, posYWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), portX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, posYWidth, 11.0f, hover42 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), proxyPlate.proxy.getPort() + (this.listeningId == proxyPlate.id && this.listeningType == ListeningType.Port ? blink : ""), (double)(portX + 2.0f), (double)(this.getY() + 40.0f + this.getScrollOffset() + proxyPlate.offset), new Color(0xBDBDBD).getRGB());
            boolean hover52 = Render2DEngine.isHovered(mouseX, mouseY, posZX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, posZWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), posZX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, posZWidth, 11.0f, hover52 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), (String)(hover52 ? proxyPlate.proxy.getL() + (this.listeningId == proxyPlate.id && this.listeningType == ListeningType.Login ? blink : "") : "*******"), (double)(posZX + 2.0f), (double)(this.getY() + 40.0f + this.getScrollOffset() + proxyPlate.offset), new Color(0xBDBDBD).getRGB());
            boolean hover62 = Render2DEngine.isHovered(mouseX, mouseY, serverX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, serverWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), serverX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, serverWidth, 11.0f, hover62 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), (String)(hover62 ? proxyPlate.proxy.getP() + (this.listeningId == proxyPlate.id && this.listeningType == ListeningType.Password ? blink : "") : "*******"), (double)(serverX + 2.0f), (double)(this.getY() + 40.0f + this.getScrollOffset() + proxyPlate.offset), new Color(0xBDBDBD).getRGB());
            boolean hover82 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, 11.0, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + this.getWidth() - 15.0f, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, 11.0f, 11.0f, hover82 ? hoveredColor : color, color2);
            FontRenderers.icons.drawString(context.getMatrices(), "w", (double)(this.getX() + this.getWidth() - 15.0f), (double)(proxyPlate.offset + this.getY() + 40.0f + this.getScrollOffset()), -1);
            FontRenderers.sf_medium_mini.drawString(context.getMatrices(), id + ".", (double)(this.getX() + 3.0f), (double)(this.getY() + 41.0f + this.getScrollOffset() + proxyPlate.offset), textColor);
            boolean selected = Managers.PROXY.getActiveProxy() == proxyPlate.proxy;
            boolean hover9 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 28.0f, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, 11.0, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + this.getWidth() - 28.0f, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, 11.0f, 11.0f, hover9 ? hoveredColor : color, color2);
            FontRenderers.icons.drawString(context.getMatrices(), selected ? "i" : "k", (double)(this.getX() + this.getWidth() - 26.0f), (double)(proxyPlate.offset + this.getY() + 41.0f + this.getScrollOffset()), selected ? -1 : Color.GRAY.getRGB());
            boolean hover10 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 51.5f, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, 21.5, 11.0);
            String ping = proxyPlate.proxy.getPing() <= 0 ? (proxyPlate.proxy.getPing() == -1 ? String.valueOf(Formatting.RED) + "shit" : (proxyPlate.proxy.getPing() == -2 ? this.pinging[(int)(System.currentTimeMillis() / 200L % 3L)] : "check")) : proxyPlate.proxy.getPing() + "ms";
            Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + this.getWidth() - 51.5f, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, 21.5f, 11.0f, hover10 ? hoveredColor : color, color2);
            FontRenderers.sf_medium_mini.drawCenteredString(context.getMatrices(), ping, (double)(this.getX() + this.getWidth() - 41.0f), (double)(proxyPlate.offset + this.getY() + 40.0f + this.getScrollOffset()), hover10 ? -1 : Color.GRAY.getRGB());
        }
        this.setMaxElementsHeight(this.proxyPlates.size() * 20);
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
        float posXWidth = this.getWidth() / 5.0f;
        float posYX = posXX + posXWidth + 2.0f;
        float posYWidth = this.getWidth() / 12.0f;
        float posZX = posYX + posYWidth + 2.0f;
        float posZWidth = this.getWidth() / 6.5f;
        float serverX = posZX + posZWidth + 2.0f;
        float serverWidth = this.getWidth() / 6.0f;
        boolean hoveringName = Render2DEngine.isHovered(mouseX, mouseY, nameX, this.getY() + 19.0f, nameWidth, 11.0);
        boolean hoveringX = Render2DEngine.isHovered(mouseX, mouseY, posXX, this.getY() + 19.0f, posXWidth, 11.0);
        boolean hoveringY = Render2DEngine.isHovered(mouseX, mouseY, posYX, this.getY() + 19.0f, posYWidth, 11.0);
        boolean hoveringZ = Render2DEngine.isHovered(mouseX, mouseY, posZX, this.getY() + 19.0f, posZWidth, 11.0);
        boolean hoveringServer = Render2DEngine.isHovered(mouseX, mouseY, serverX, this.getY() + 19.0f, serverWidth, 11.0);
        boolean hoveringAdd = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 19.0f, 11.0, 11.0);
        if (hoveringName) {
            this.listeningType = ListeningType.Name;
            this.addName = "";
        }
        if (hoveringX) {
            this.listeningType = ListeningType.Ip;
            this.addIp = "";
        }
        if (hoveringY) {
            this.listeningType = ListeningType.Port;
            this.addPort = "";
        }
        if (hoveringZ) {
            this.listeningType = ListeningType.Login;
            this.addLogin = "";
        }
        if (hoveringServer) {
            this.listeningType = ListeningType.Password;
            this.addPassword = "";
        }
        if (hoveringName || hoveringX || hoveringY || hoveringZ || hoveringServer) {
            this.listeningId = -3;
        }
        if (hoveringAdd) {
            try {
                Managers.PROXY.addProxy(new ProxyManager.ThProxy(this.addName, this.addIp, Integer.parseInt(this.addPort), this.addLogin, this.addPassword));
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.refresh();
        }
        ArrayList copy = Lists.newArrayList(this.proxyPlates);
        for (ProxyPlate proxyPlate : copy) {
            if ((float)((int)(proxyPlate.offset + this.getY() + 50.0f)) + this.getScrollOffset() > this.getY() + this.getHeight()) continue;
            boolean hoveringName2 = Render2DEngine.isHovered(mouseX, mouseY, nameX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, nameWidth, 11.0);
            boolean hoveringIp = Render2DEngine.isHovered(mouseX, mouseY, posXX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, posXWidth, 11.0);
            boolean hoveringPort = Render2DEngine.isHovered(mouseX, mouseY, posYX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, posYWidth, 11.0);
            boolean hoveringLogin = Render2DEngine.isHovered(mouseX, mouseY, posZX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, posZWidth, 11.0);
            boolean hoveringPassword = Render2DEngine.isHovered(mouseX, mouseY, serverX, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, serverWidth, 11.0);
            boolean hoveringRemove = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, 11.0, 11.0);
            boolean hoveringSelect = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 28.0f, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, 11.0, 11.0);
            boolean hoveringCheck = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 51.5f, this.getY() + 36.0f + this.getScrollOffset() + proxyPlate.offset, 21.5, 11.0);
            if (hoveringName2) {
                this.listeningType = ListeningType.Name;
            }
            if (hoveringIp) {
                this.listeningType = ListeningType.Ip;
            }
            if (hoveringPort) {
                this.listeningType = ListeningType.Port;
            }
            if (hoveringLogin) {
                this.listeningType = ListeningType.Login;
            }
            if (hoveringPassword) {
                this.listeningType = ListeningType.Password;
            }
            if (hoveringCheck) {
                Managers.PROXY.checkPing(proxyPlate.proxy());
            }
            if (hoveringName2 || hoveringIp || hoveringPort || hoveringLogin || hoveringPassword) {
                this.listeningId = proxyPlate.id;
            }
            if (hoveringSelect) {
                if (Managers.PROXY.getActiveProxy() == proxyPlate.proxy) {
                    Managers.PROXY.setActiveProxy(null);
                } else {
                    Managers.PROXY.setActiveProxy(proxyPlate.proxy);
                }
            }
            if (!hoveringRemove) continue;
            Managers.PROXY.removeProxy(proxyPlate.proxy);
            this.refresh();
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 70 && (InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)341) || InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)345))) {
            this.listeningId = -2;
            return;
        }
        if (keyCode == 86 && (InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)341) || InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)345))) {
            String paste = GLFW.glfwGetClipboardString((long)Module.mc.getWindow().getHandle());
            if (paste == null) {
                return;
            }
            for (ProxyPlate plate : this.proxyPlates) {
                if (this.listeningId != plate.id) continue;
                switch (this.listeningType.ordinal()) {
                    case 0: {
                        plate.proxy.setName(paste);
                        return;
                    }
                    case 1: {
                        plate.proxy.setIp(paste);
                        return;
                    }
                    case 2: {
                        try {
                            plate.proxy.setPort(Integer.parseInt(paste));
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    case 3: {
                        plate.proxy.setL(paste);
                        return;
                    }
                    case 4: {
                        plate.proxy.setP(paste);
                        return;
                    }
                }
            }
            if (this.listeningId == -3) {
                switch (this.listeningType.ordinal()) {
                    case 0: {
                        this.addName = paste;
                        break;
                    }
                    case 1: {
                        this.addIp = paste;
                        break;
                    }
                    case 2: {
                        this.addPort = paste;
                        break;
                    }
                    case 3: {
                        this.addLogin = paste;
                        break;
                    }
                    case 4: {
                        this.addPassword = paste;
                    }
                }
            }
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
                    for (ProxyPlate plate : this.proxyPlates) {
                        if (this.listeningId != plate.id) continue;
                        switch (this.listeningType.ordinal()) {
                            case 0: {
                                plate.proxy.setName(SliderElement.removeLastChar(plate.proxy.getName()));
                                return;
                            }
                            case 1: {
                                plate.proxy.setIp(SliderElement.removeLastChar(plate.proxy.getIp()));
                                return;
                            }
                            case 2: {
                                String num = SliderElement.removeLastChar(String.valueOf(plate.proxy.getPort()));
                                if (num.isEmpty()) {
                                    num = "0";
                                }
                                plate.proxy.setPort(Integer.parseInt(num));
                                return;
                            }
                            case 3: {
                                plate.proxy.setL(SliderElement.removeLastChar(plate.proxy.getL()));
                                return;
                            }
                            case 4: {
                                plate.proxy.setP(SliderElement.removeLastChar(plate.proxy.getP()));
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
                        case 1: {
                            this.addIp = SliderElement.removeLastChar(this.addIp);
                            break;
                        }
                        case 2: {
                            this.addPort = SliderElement.removeLastChar(this.addPort);
                            break;
                        }
                        case 3: {
                            this.addLogin = SliderElement.removeLastChar(this.addLogin);
                            break;
                        }
                        case 4: {
                            this.addPassword = SliderElement.removeLastChar(this.addPassword);
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
            for (ProxyPlate plate : this.proxyPlates) {
                if (this.listeningId != plate.id) continue;
                try {
                    switch (this.listeningType.ordinal()) {
                        case 0: {
                            plate.proxy.setName(plate.proxy.getName() + key);
                            break;
                        }
                        case 1: {
                            plate.proxy.setIp(plate.proxy.getIp() + key);
                            break;
                        }
                        case 2: {
                            plate.proxy.setPort(Integer.parseInt(String.valueOf(plate.proxy.getPort()) + key));
                            break;
                        }
                        case 3: {
                            plate.proxy.setL(plate.proxy.getL() + key);
                            break;
                        }
                        case 4: {
                            plate.proxy.setP(plate.proxy.getP() + key);
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
                        case 1: {
                            this.addIp = this.addIp + key;
                            break;
                        }
                        case 2: {
                            this.addPort = this.addPort + key;
                            break;
                        }
                        case 3: {
                            this.addLogin = this.addLogin + key;
                            break;
                        }
                        case 4: {
                            this.addPassword = this.addPassword + key;
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
        this.proxyPlates.clear();
        int id1 = 0;
        for (ProxyManager.ThProxy p : Managers.PROXY.getProxies()) {
            if (!this.search.equals("Search") && !this.search.isEmpty() && !p.getName().contains(this.search)) continue;
            this.proxyPlates.add(new ProxyPlate(id1, id1 * 20 + 18, p));
            ++id1;
        }
    }

    @Override
    public int getMinWidth() {
        return 340;
    }

    private static enum ListeningType {
        Name,
        Ip,
        Port,
        Login,
        Password;

    }

    private record ProxyPlate(int id, float offset, ProxyManager.ThProxy proxy) {
    }
}

