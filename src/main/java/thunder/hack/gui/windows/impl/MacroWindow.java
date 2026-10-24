/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.StringHelper
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.gui.screen.Screen
 *  org.lwjgl.glfw.GLFW
 */
package thunder.hack.gui.windows.impl;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.StringHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.MacroManager;
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

public class MacroWindow
extends WindowBase {
    private static MacroWindow instance;
    private ArrayList<MacroPlate> macroPlates = new ArrayList();
    private int listeningId = -1;
    private int addBindKeyCode = -1;
    private ListeningType listeningType;
    private String search = "Search";
    private String addName = "Name";
    private String addBind = "Bind";
    private String addText = "Text";

    public MacroWindow(float x, float y, float width, float height, Setting<PositionSetting> position) {
        super(x, y, width, height, "Macros", position, TextureStorage.macrosIcon);
        this.refresh();
    }

    public static MacroWindow get(float x, float y, Setting<PositionSetting> position) {
        if (instance == null) {
            instance = new MacroWindow(x, y, 200.0f, 180.0f, position);
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
        if (this.macroPlates.isEmpty()) {
            FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), ClientSettings.isRu() ? "\u0422\u0443\u0442 \u043f\u043e\u043a\u0430 \u043f\u0443\u0441\u0442\u043e" : "It's empty here yet", (double)(this.getX() + this.getWidth() / 2.0f), (double)(this.getY() + this.getHeight() / 2.0f), new Color(0xBDBDBD).getRGB());
        }
        String blink = System.currentTimeMillis() / 240L % 2L == 0L ? "" : "l";
        float nameX = this.getX() + 11.0f;
        float nameWidth = this.getWidth() / 4.5f;
        float bindX = nameX + nameWidth + 2.0f;
        float bindWidth = 27.0f;
        float textX = bindX + bindWidth + 2.0f;
        float textWidth = this.getWidth() - (textX - this.getX()) - 17.0f;
        boolean hover2 = Render2DEngine.isHovered(mouseX, mouseY, nameX, this.getY() + 19.0f, nameWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), nameX, this.getY() + 19.0f, nameWidth, 11.0f, hover2 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addName + (this.listeningId == -3 && this.listeningType == ListeningType.Name ? blink : ""), (double)(nameX + 2.0f), (double)(this.getY() + 23.0f), new Color(0xBDBDBD).getRGB());
        boolean hover3 = Render2DEngine.isHovered(mouseX, mouseY, bindX, this.getY() + 19.0f, bindWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), bindX, this.getY() + 19.0f, bindWidth, 11.0f, hover3 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), this.getSbind(this.addBind) + (this.listeningId == -3 && this.listeningType == ListeningType.Bind ? blink : ""), (double)(bindX + 13.5f), (double)(this.getY() + 23.0f), textColor);
        boolean hover4 = Render2DEngine.isHovered(mouseX, mouseY, textX, this.getY() + 19.0f, textWidth, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), textX, this.getY() + 19.0f, textWidth, 11.0f, hover4 ? hoveredColor : color, color2);
        FontRenderers.sf_medium.drawString(context.getMatrices(), this.addText + (this.listeningId == -3 && this.listeningType == ListeningType.Text ? blink : ""), (double)(textX + 2.0f), (double)(this.getY() + 23.0f), new Color(0xBDBDBD).getRGB());
        boolean hover5 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 19.0f, 11.0, 11.0);
        Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + this.getWidth() - 15.0f, this.getY() + 19.0f, 11.0f, 11.0f, hover5 ? hoveredColor : color, color2);
        FontRenderers.categories.drawString(context.getMatrices(), "+", (double)(this.getX() + this.getWidth() - 12.0f), (double)(this.getY() + 23.0f), -1);
        Render2DEngine.horizontalGradient(context.getMatrices(), this.getX() + 2.0f, this.getY() + 33.0f, this.getX() + 2.0f + this.getWidth() / 2.0f - 2.0f, this.getY() + 33.5f, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
        Render2DEngine.horizontalGradient(context.getMatrices(), this.getX() + 2.0f + this.getWidth() / 2.0f - 2.0f, this.getY() + 33.0f, this.getX() + 2.0f + this.getWidth() - 4.0f, this.getY() + 33.5f, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "Name", (double)(nameX + nameWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "l   Bind   l", (double)(bindX + bindWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), "Text", (double)(textX + textWidth / 2.0f), (double)(this.getY() + 40.0f), textColor);
        Render2DEngine.addWindow(context.getMatrices(), this.getX(), this.getY() + 50.0f, this.getX() + this.getWidth(), this.getY() + this.getHeight() - 1.0f, 1.0);
        int id = 0;
        for (MacroPlate macroPlate : this.macroPlates) {
            ++id;
            if ((float)((int)(macroPlate.offset + this.getY() + 25.0f)) + this.getScrollOffset() > this.getY() + this.getHeight() || macroPlate.offset + this.getScrollOffset() + this.getY() + 10.0f < this.getY()) continue;
            boolean hover22 = Render2DEngine.isHovered(mouseX, mouseY, nameX, macroPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), nameWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), nameX, macroPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), nameWidth, 11.0f, hover22 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), macroPlate.macro().getName() + (macroPlate.id() == this.listeningId && this.listeningType == ListeningType.Name ? blink : ""), (double)(nameX + 2.0f), (double)(macroPlate.offset + this.getY() + 40.0f + this.getScrollOffset()), textColor);
            boolean hover32 = Render2DEngine.isHovered(mouseX, mouseY, bindX, macroPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), bindWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), bindX, macroPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), bindWidth, 11.0f, hover32 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), this.getSbind(this.toString(macroPlate.macro().getBind())) + (macroPlate.id() == this.listeningId && this.listeningType == ListeningType.Bind ? blink : ""), (double)(bindX + 13.5f), (double)(macroPlate.offset + this.getY() + 40.0f + this.getScrollOffset()), textColor);
            boolean hover42 = Render2DEngine.isHovered(mouseX, mouseY, textX, macroPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), textWidth, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), textX, macroPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), textWidth, 11.0f, hover42 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), macroPlate.macro().getText() + (macroPlate.id() == this.listeningId && this.listeningType == ListeningType.Text ? blink : ""), (double)(textX + 2.0f), (double)(macroPlate.offset + this.getY() + 40.0f + this.getScrollOffset()), textColor);
            boolean hover52 = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, macroPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), 11.0, 11.0);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), this.getX() + this.getWidth() - 15.0f, macroPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), 11.0f, 11.0f, hover52 ? hoveredColor : color, color2);
            FontRenderers.icons.drawString(context.getMatrices(), "w", (double)(this.getX() + this.getWidth() - 15.0f), (double)(macroPlate.offset + this.getY() + 40.0f + this.getScrollOffset()), -1);
            FontRenderers.sf_medium_mini.drawString(context.getMatrices(), id + ".", (double)(this.getX() + 3.0f), (double)(macroPlate.offset + this.getY() + 41.0f + this.getScrollOffset()), textColor);
        }
        this.setMaxElementsHeight(this.macroPlates.size() * 20);
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
        float nameWidth = this.getWidth() / 4.5f;
        float bindX = nameX + nameWidth + 2.0f;
        float bindWidth = 27.0f;
        float textX = bindX + bindWidth + 2.0f;
        float textWidth = this.getWidth() - (textX - this.getX()) - 17.0f;
        boolean hoveringName = Render2DEngine.isHovered(mouseX, mouseY, nameX, this.getY() + 19.0f, nameWidth, 11.0);
        boolean hoveringBind = Render2DEngine.isHovered(mouseX, mouseY, bindX, this.getY() + 19.0f, bindWidth, 11.0);
        boolean hoveringText = Render2DEngine.isHovered(mouseX, mouseY, textX, this.getY() + 19.0f, textWidth, 11.0);
        boolean hoveringAdd = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, this.getY() + 19.0f, 11.0, 11.0);
        if (hoveringName) {
            this.listeningType = ListeningType.Name;
            this.addName = "";
        }
        if (hoveringBind) {
            this.listeningType = ListeningType.Bind;
            this.addBind = "";
            this.addBindKeyCode = -1;
        }
        if (hoveringText) {
            this.listeningType = ListeningType.Text;
            this.addText = "";
        }
        if (hoveringText || hoveringName || hoveringBind) {
            this.listeningId = -3;
        }
        if (hoveringAdd && this.addBindKeyCode != -1) {
            MacroManager.addMacro(new MacroManager.Macro(this.addName, this.addText, this.addBindKeyCode));
            this.refresh();
        }
        var copy = Lists.newArrayList(this.macroPlates);
        for (MacroPlate macroPlate : copy) {
            if ((float)((int)(macroPlate.offset + this.getY() + 50.0f)) + this.getScrollOffset() > this.getY() + this.getHeight()) continue;
            boolean hoveringName1 = Render2DEngine.isHovered(mouseX, mouseY, nameX, macroPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), nameWidth, 11.0);
            boolean hoveringBind1 = Render2DEngine.isHovered(mouseX, mouseY, bindX, macroPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), bindWidth, 11.0);
            boolean hoveringText1 = Render2DEngine.isHovered(mouseX, mouseY, textX, macroPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), textWidth, 11.0);
            boolean hoveringRemove = Render2DEngine.isHovered(mouseX, mouseY, this.getX() + this.getWidth() - 15.0f, macroPlate.offset + this.getY() + 36.0f + this.getScrollOffset(), 11.0, 11.0);
            if (hoveringName1) {
                this.listeningType = ListeningType.Name;
            }
            if (hoveringBind1) {
                this.listeningType = ListeningType.Bind;
            }
            if (hoveringText1) {
                this.listeningType = ListeningType.Text;
            }
            if (hoveringName1 || hoveringBind1 || hoveringText1) {
                this.listeningId = macroPlate.id;
            }
            if (!hoveringRemove) continue;
            Managers.MACRO.removeMacro(macroPlate.macro());
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
            for (MacroPlate plate : this.macroPlates) {
                if (this.listeningId != plate.id) continue;
                switch (this.listeningType.ordinal()) {
                    case 1: {
                        plate.macro.setText(paste);
                        return;
                    }
                    case 0: {
                        plate.macro.setName(paste);
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
                        this.addText = paste;
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
                    for (MacroPlate plate : this.macroPlates) {
                        if (this.listeningId != plate.id) continue;
                        switch (this.listeningType.ordinal()) {
                            case 0: {
                                plate.macro.setName(SliderElement.removeLastChar(plate.macro.getName()));
                                return;
                            }
                            case 1: {
                                plate.macro.setText(SliderElement.removeLastChar(plate.macro.getText()));
                                return;
                            }
                            case 2: {
                                return;
                            }
                        }
                    }
                    if (this.listeningId != -3) break;
                    switch (this.listeningType.ordinal()) {
                        case 0: {
                            this.addName = SliderElement.removeLastChar(this.addName);
                            return;
                        }
                        case 1: {
                            this.addText = SliderElement.removeLastChar(this.addText);
                            return;
                        }
                        case 2: {
                            return;
                        }
                    }
                    break;
                }
                case 32: {
                    if (this.listeningId != -2) break;
                    this.search = this.search + " ";
                    return;
                }
            }
            for (MacroPlate plate : this.macroPlates) {
                if (keyCode == -1 || this.listeningId != plate.id || this.listeningType != ListeningType.Bind) continue;
                plate.macro.setBind(keyCode);
                this.listeningId = -1;
            }
            if (this.listeningId == -3 && keyCode != -1 && this.listeningType == ListeningType.Bind) {
                this.addBind = this.toString(keyCode);
                this.addBindKeyCode = keyCode;
                this.listeningId = -1;
            }
        }
    }

    @Override
    public void charTyped(char key, int keyCode) {
        if (StringHelper.isValidChar((char)key) && this.listeningId != -1) {
            if (this.listeningId == -2) {
                this.search = this.search + key;
            }
            for (MacroPlate plate : this.macroPlates) {
                if (this.listeningId != plate.id) continue;
                switch (this.listeningType.ordinal()) {
                    case 0: {
                        plate.macro.setName(plate.macro.getName() + key);
                        break;
                    }
                    case 1: {
                        plate.macro.setText(plate.macro.getText() + key);
                        break;
                    }
                }
                return;
            }
            if (this.listeningId == -3) {
                switch (this.listeningType.ordinal()) {
                    case 0: {
                        this.addName = this.addName + key;
                        break;
                    }
                    case 1: {
                        this.addText = this.addText + key;
                        break;
                    }
                }
            }
            this.refresh();
        }
    }

    private void refresh() {
        this.resetScroll();
        this.macroPlates.clear();
        int id1 = 0;
        for (MacroManager.Macro m : Managers.MACRO.getMacros()) {
            if (!this.search.equals("Search") && !this.search.isEmpty() && !m.getName().contains(this.search) && !m.getText().contains(this.search)) continue;
            this.macroPlates.add(new MacroPlate(id1, id1 * 20 + 18, m));
            ++id1;
        }
    }

    public String toString(int key) {
        Object kn;
        Object object = kn = key > 0 ? GLFW.glfwGetKeyName((int)key, (int)GLFW.glfwGetKeyScancode((int)key)) : "None";
        if (kn == null) {
            try {
                for (Field declaredField : GLFW.class.getDeclaredFields()) {
                    int a;
                    if (!declaredField.getName().startsWith("GLFW_KEY_") || (a = ((Integer)declaredField.get(null)).intValue()) != key) continue;
                    String nb = declaredField.getName().substring("GLFW_KEY_".length());
                    kn = nb.substring(0, 1).toUpperCase() + nb.substring(1).toLowerCase();
                }
            }
            catch (Exception ignored) {
                kn = "unknown." + key;
            }
        }
        return key == -1 ? "None" : ((String)kn).toUpperCase();
    }

    private String getSbind(String sbind) {
        if (sbind.equals("LEFT_CONTROL")) {
            sbind = "LCtrl";
        }
        if (sbind.equals("RIGHT_CONTROL")) {
            sbind = "RCtrl";
        }
        if (sbind.equals("LEFT_SHIFT")) {
            sbind = "LShift";
        }
        if (sbind.equals("RIGHT_SHIFT")) {
            sbind = "RShift";
        }
        if (sbind.equals("LEFT_ALT")) {
            sbind = "LAlt";
        }
        if (sbind.equals("RIGHT_ALT")) {
            sbind = "RAlt";
        }
        return sbind;
    }

    private static enum ListeningType {
        Name,
        Text,
        Bind;

    }

    private record MacroPlate(int id, float offset, MacroManager.Macro macro) {
    }
}

