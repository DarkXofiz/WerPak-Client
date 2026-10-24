/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Util
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  org.lwjgl.glfw.GLFW
 */
package thunder.hack.gui.thundergui;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.util.Util;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ConfigManager;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ThunderHackGui;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.thundergui.components.BooleanComponent;
import thunder.hack.gui.thundergui.components.BooleanParentComponent;
import thunder.hack.gui.thundergui.components.CategoryPlate;
import thunder.hack.gui.thundergui.components.ColorPickerComponent;
import thunder.hack.gui.thundergui.components.ConfigComponent;
import thunder.hack.gui.thundergui.components.FriendComponent;
import thunder.hack.gui.thundergui.components.ModeComponent;
import thunder.hack.gui.thundergui.components.ModulePlate;
import thunder.hack.gui.thundergui.components.ParentComponent;
import thunder.hack.gui.thundergui.components.SettingElement;
import thunder.hack.gui.thundergui.components.SliderComponent;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.impl.SettingGroup;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;
import thunder.hack.utility.render.animation.EaseOutBack;

public class ThunderGui
extends Screen {
    public static CurrentMode currentMode = CurrentMode.Modules;
    public static boolean scroll_lock = false;
    public static ModulePlate selected_plate;
    public static ModulePlate prev_selected_plate;
    public static EaseOutBack open_animation;
    public static boolean open_direction;
    private static ThunderGui INSTANCE;
    public final ArrayList<ModulePlate> components = new ArrayList();
    public final CopyOnWriteArrayList<CategoryPlate> categories = new CopyOnWriteArrayList();
    public final ArrayList<SettingElement> settings = new ArrayList();
    public final CopyOnWriteArrayList<ConfigComponent> configs = new CopyOnWriteArrayList();
    public final CopyOnWriteArrayList<FriendComponent> friends = new CopyOnWriteArrayList();
    private final int main_width = 400;
    public int main_posX = 100;
    public int main_posY = 100;
    public Module.Category current_category = Module.Category.COMBAT;
    public Module.Category new_category = Module.Category.COMBAT;
    float category_animation = 1.0f;
    float settings_animation = 1.0f;
    float manager_animation = 1.0f;
    int prevCategoryY;
    int CategoryY;
    int slider_y;
    int slider_x;
    private int main_height = 250;
    private boolean dragging = false;
    private boolean rescale = false;
    private int drag_x = 0;
    private int drag_y = 0;
    private int rescale_y = 0;
    private float scroll = 0.0f;
    private boolean first_open = true;
    private boolean searching = false;
    private boolean listening_friend = false;
    private boolean listening_config = false;
    private String search_string = "Search";
    private String config_string = "Save config";
    private String friend_string = "Add friend";
    private CurrentMode prevMode = CurrentMode.Modules;
    public static boolean mouse_state;
    public static int mouse_x;
    public static int mouse_y;
    private boolean middleDragging = false;
    private double middleDragStartY = 0.0;
    private int middleDragStartSliderY = 0;

    public ThunderGui() {
        super((Text)Text.literal((String)"ThunderGui2"));
        this.setInstance();
        this.load();
        this.CategoryY = this.getCategoryY(this.new_category);
    }

    public boolean shouldPause() {
        return false;
    }

    public static ThunderGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThunderGui();
        }
        return INSTANCE;
    }

    public static ThunderGui getThunderGui() {
        open_animation = new EaseOutBack();
        open_direction = true;
        return ThunderGui.getInstance();
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void load() {
        this.categories.clear();
        this.components.clear();
        this.configs.clear();
        this.friends.clear();
        int module_y = 0;
        for (Module module : Managers.MODULE.getModulesByCategory(this.current_category)) {
            this.components.add(new ModulePlate(module, this.main_posX + 100, this.main_posY + 40 + module_y, module_y / 35));
            module_y += 35;
        }
        int category_y = 0;
        for (Module.Category category : Managers.MODULE.getCategories()) {
            this.categories.add(new CategoryPlate(category, this.main_posX + 8, this.main_posY + 43 + category_y));
            category_y += 17;
        }
    }

    public void loadConfigs() {
        this.friends.clear();
        this.configs.clear();
        new Thread(() -> {
            int config_y = 3;
            for (String file1 : Objects.requireNonNull(Managers.CONFIG.getConfigList())) {
                this.configs.add(new ConfigComponent(file1, ConfigManager.getConfigDate(file1), this.main_posX + 100, this.main_posY + 40 + config_y, config_y / 35));
                config_y += 35;
            }
        }).start();
    }

    public void loadFriends() {
        this.configs.clear();
        this.friends.clear();
        int friend_y = 3;
        for (String friend : Managers.FRIEND.getFriends()) {
            this.friends.add(new FriendComponent(friend, this.main_posX + 100, this.main_posY + 40 + friend_y, friend_y / 35));
            friend_y += 35;
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (Module.fullNullCheck()) {
            this.render(context, mouseX, mouseY, delta);
        }
        context.getMatrices().push();
        mouse_x = mouseX;
        mouse_y = mouseY;
        if (open_animation.getAnimationd() > 0.0) {
            this.renderGui(context, mouseX, mouseY, delta);
        }
        if (open_animation.getAnimationd() <= 0.01 && !open_direction) {
            open_animation = new EaseOutBack();
            Module.mc.currentScreen = null;
            Module.mc.setScreen(null);
        }
        context.getMatrices().pop();
    }

    public void renderGui(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        if (this.dragging) {
            float deltaX = mouseX - this.drag_x - this.main_posX;
            float deltaY2 = mouseY - this.drag_y - this.main_posY;
            this.main_posX = mouseX - this.drag_x;
            this.main_posY = mouseY - this.drag_y;
            this.slider_y += (int)deltaY2;
            this.slider_x += (int)deltaX;
            this.configs.forEach(configComponent -> configComponent.movePosition(deltaX, deltaY2));
            this.friends.forEach(friendComponent -> friendComponent.movePosition(deltaX, deltaY2));
            this.components.forEach(component -> component.movePosition(deltaX, deltaY2));
            this.categories.forEach(category -> category.movePosition(deltaX, deltaY2));
        }
        if (this.middleDragging) {
            float middleDragDeltaY = (float)((double)mouseY - this.middleDragStartY);
            int newSliderY = (int)((float)this.middleDragStartSliderY + middleDragDeltaY);
            int sliderDelta = newSliderY - this.slider_y;
            this.slider_y = newSliderY;
            this.categories.forEach(category -> category.movePosition(0.0f, sliderDelta));
        }
        if (this.rescale) {
            float f;
            float deltaY = mouseY - this.rescale_y - this.main_height;
            if ((float)this.main_height + f > 250.0f) {
                this.main_height = (int)((float)this.main_height + deltaY);
            }
        }
        if (this.current_category != null && this.current_category != this.new_category) {
            this.prevCategoryY = this.getCategoryY(this.current_category);
            this.CategoryY = this.getCategoryY(this.new_category);
            this.current_category = this.new_category;
            this.category_animation = 1.0f;
            this.slider_y = 0;
            this.search_string = "Search";
            this.config_string = "Save config";
            this.friend_string = "Add friend";
            currentMode = CurrentMode.Modules;
            this.load();
        }
        this.manager_animation = AnimationUtility.fast(this.manager_animation, 0.0f, 15.0f);
        this.category_animation = AnimationUtility.fast(this.category_animation, 0.0f, 15.0f);
        Render2DEngine.drawRound(context.getMatrices(), this.main_posX, this.main_posY, 400.0f, this.main_height, 9.0f, ThunderHackGui.getColorByTheme(0));
        Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 5, this.main_posY + 5, 90.0f, 30.0f, 7.0f, ThunderHackGui.getColorByTheme(1));
        context.getMatrices().push();
        context.getMatrices().scale(0.85f, 0.85f, 1.0f);
        context.getMatrices().translate((double)(this.main_posX + 10) / 0.85, (double)(this.main_posY + 15) / 0.85, 0.0);
        FontRenderers.thglitch.drawString(context.getMatrices(), "THUNDERHACK", 0.0, 0.0, ThunderHackGui.getColorByTheme(2).getRGB());
        context.getMatrices().translate((double)(-(this.main_posX + 10)) / 0.85, (double)(-(this.main_posY + 15)) / 0.85, 0.0);
        context.getMatrices().scale(1.0f, 1.0f, 1.0f);
        context.getMatrices().pop();
        FontRenderers.settings.drawString(context.getMatrices(), "recode V15", (double)((float)(this.main_posX + 91) - FontRenderers.settings.getStringWidth("recode V15")), (double)(this.main_posY + 30), ThunderHackGui.getColorByTheme(3).getRGB());
        Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 5, this.main_posY + 40, 90.0f, 120.0f, 7.0f, ThunderHackGui.getColorByTheme(4));
        if (currentMode == CurrentMode.Modules) {
            Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 20, this.main_posY + 195, 60.0f, 20.0f, 4.0f, ThunderHackGui.getColorByTheme(4));
        } else if (currentMode == CurrentMode.CfgManager) {
            Render2DEngine.drawGradientRound(context.getMatrices(), this.main_posX + 20, this.main_posY + 195, 60.0f, 20.0f, 4.0f, ThunderHackGui.getColorByTheme(4), ThunderHackGui.getColorByTheme(4), ThunderHackGui.getColorByTheme(5), ThunderHackGui.getColorByTheme(5));
        } else {
            Render2DEngine.drawGradientRound(context.getMatrices(), this.main_posX + 20, this.main_posY + 195, 60.0f, 20.0f, 4.0f, ThunderHackGui.getColorByTheme(5), ThunderHackGui.getColorByTheme(5), ThunderHackGui.getColorByTheme(4), ThunderHackGui.getColorByTheme(4));
        }
        Render2DEngine.drawRound(context.getMatrices(), (float)this.main_posX + 49.5f, this.main_posY + 197, 1.0f, 16.0f, 0.5f, ThunderHackGui.getColorByTheme(6));
        FontRenderers.mid_icons.drawString(context.getMatrices(), "u", (double)(this.main_posX + 20), (double)(this.main_posY + 196), currentMode == CurrentMode.CfgManager ? ThunderHackGui.getColorByTheme(2).getRGB() : new Color(0x8D8D8D).getRGB());
        FontRenderers.mid_icons.drawString(context.getMatrices(), "v", (double)(this.main_posX + 54), (double)(this.main_posY + 197), currentMode == CurrentMode.FriendManager ? ThunderHackGui.getColorByTheme(2).getRGB() : new Color(0x8D8D8D).getRGB());
        if (this.isHoveringItem(this.main_posX + 20, this.main_posY + 195, 60.0f, 20.0f, mouseX, mouseY)) {
            Render2DEngine.addWindow(context.getMatrices(), this.main_posX + 20, this.main_posY + 195, this.main_posX + 20 + 60, this.main_posY + 195 + 20, 1.0);
            Render2DEngine.drawBlurredShadow(context.getMatrices(), mouseX - 20, mouseY - 20, 40.0f, 40.0f, 60, new Color(-1017816450, true));
            Render2DEngine.popWindow();
        }
        if (this.first_open) {
            this.category_animation = 1.0f;
            Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 8, (float)this.CategoryY + (float)this.slider_y, 84.0f, 15.0f, 2.0f, ThunderHackGui.getColorByTheme(7));
            this.first_open = false;
        } else if (currentMode == CurrentMode.Modules) {
            Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 8, (float)Render2DEngine.interpolate(this.CategoryY, this.prevCategoryY, this.category_animation) + (float)this.slider_y, 84.0f, 15.0f, 2.0f, ThunderHackGui.getColorByTheme(7));
        }
        if (selected_plate != prev_selected_plate) {
            prev_selected_plate = selected_plate;
            this.settings_animation = 1.0f;
            this.settings.clear();
            this.scroll = 0.0f;
            if (selected_plate != null) {
                for (Setting setting : selected_plate.getModule().getSettings()) {
                    if (setting.getValue() instanceof SettingGroup) {
                        this.settings.add(new ParentComponent(setting));
                    }
                    if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled") && !setting.getName().equals("Drawn")) {
                        this.settings.add(new BooleanComponent(setting));
                    }
                    if (setting.getValue() instanceof BooleanSettingGroup) {
                        this.settings.add(new BooleanParentComponent(setting));
                    }
                    if (setting.getValue().getClass().isEnum()) {
                        this.settings.add(new ModeComponent(setting));
                    }
                    if (setting.getValue() instanceof ColorSetting) {
                        this.settings.add(new ColorPickerComponent(setting));
                    }
                    if (!setting.isNumberSetting() || !setting.hasRestriction()) continue;
                    this.settings.add(new SliderComponent(setting));
                }
            }
        }
        this.settings_animation = AnimationUtility.fast(this.settings_animation, 0.0f, 15.0f);
        if (currentMode != this.prevMode) {
            if (this.prevMode != CurrentMode.CfgManager) {
                this.manager_animation = 1.0f;
                if (currentMode == CurrentMode.CfgManager) {
                    this.loadConfigs();
                }
            }
            if (this.prevMode != CurrentMode.FriendManager) {
                this.manager_animation = 1.0f;
                if (currentMode == CurrentMode.FriendManager) {
                    this.loadFriends();
                }
            }
            this.prevMode = currentMode;
        }
        if (selected_plate != null && currentMode == CurrentMode.Modules) {
            Render2DEngine.drawRound(context.getMatrices(), (float)Render2DEngine.interpolate(this.main_posX + 200, selected_plate.getPosX(), this.settings_animation), (float)Render2DEngine.interpolate(this.main_posY + 40, selected_plate.getPosY(), this.settings_animation), (float)Render2DEngine.interpolate(195.0, 90.0, this.settings_animation), (float)Render2DEngine.interpolate(this.main_height - 45, 30.0, this.settings_animation), 4.0f, ThunderHackGui.getColorByTheme(7));
        }
        if (currentMode != CurrentMode.Modules) {
            this.searching = false;
            Render2DEngine.addWindow(context.getMatrices(), (float)Render2DEngine.interpolate(this.main_posX + 80, this.main_posX + 200, this.manager_animation), this.main_posY + 39, (float)Render2DEngine.interpolate(399.0, 195.0, this.manager_animation) + (float)this.main_posX + 36.0f, (float)this.main_height + (float)this.main_posY - 3.0f, 1.0);
            Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 100, (float)this.main_posY + 40.0f, 295.0f, (float)this.main_height - 44.0f, 4.0f, ThunderHackGui.getColorByTheme(7));
            this.configs.forEach(components -> components.render(context, mouseX, mouseY));
            this.friends.forEach(components -> components.render(context, mouseX, mouseY));
            Render2DEngine.draw2DGradientRect(context.getMatrices(), this.main_posX + 102, this.main_posY + 34, this.main_posX + 393, this.main_posY + 60, new Color(25, 20, 30, 0), ThunderHackGui.getColorByTheme(7), new Color(25, 20, 30, 0), new Color(37, 27, 41, 245));
            Render2DEngine.draw2DGradientRect(context.getMatrices(), this.main_posX + 102, this.main_posY + this.main_height - 35, this.main_posX + 393, this.main_posY + this.main_height, ThunderHackGui.getColorByTheme(7), new Color(25, 20, 30, 0), ThunderHackGui.getColorByTheme(7), new Color(37, 27, 41, 0));
            Render2DEngine.popWindow();
        }
        Render2DEngine.addWindow(context.getMatrices(), this.main_posX + 79, this.main_posY + 35, this.main_posX + 396 + 40, this.main_posY + this.main_height, 1.0);
        this.components.forEach(components -> components.render(context.getMatrices(), mouseX, mouseY));
        Render2DEngine.popWindow();
        this.categories.forEach(category -> category.render(context.getMatrices(), mouseX, mouseY));
        if (currentMode == CurrentMode.Modules) {
            Render2DEngine.draw2DGradientRect(context.getMatrices(), this.main_posX + 98, this.main_posY + 34, this.main_posX + 191, this.main_posY + 50, new Color(37, 27, 41, 0), new Color(37, 27, 41, 245), new Color(37, 27, 41, 0), new Color(37, 27, 41, 245));
            Render2DEngine.draw2DGradientRect(context.getMatrices(), this.main_posX + 98, this.main_posY + this.main_height - 15, this.main_posX + 191, this.main_posY + this.main_height, new Color(37, 27, 41, 245), new Color(37, 27, 41, 0), new Color(37, 27, 41, 245), new Color(37, 27, 41, 0));
        }
        Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 100, this.main_posY + 5, 295.0f, 30.0f, 7.0f, new Color(25, 20, 30, 250));
        if (this.isHoveringItem(this.main_posX + 105, this.main_posY + 14, 11.0f, 11.0f, mouseX, mouseY)) {
            Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 105, this.main_posY + 14, 11.0f, 11.0f, 3.0f, new Color(68, 49, 75, 250));
        } else {
            Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 105, this.main_posY + 14, 11.0f, 11.0f, 3.0f, new Color(52, 38, 58, 250));
        }
        FontRenderers.modules.drawString(context.getMatrices(), "current cfg: " + Managers.CONFIG.currentConfig.getName(), (double)(this.main_posX + 120), (double)(this.main_posY + 18), new Color(-838860801, true).getRGB());
        FontRenderers.icons.drawString(context.getMatrices(), "t", (double)(this.main_posX + 106), (double)(this.main_posY + 17), new Color(-1023410177, true).getRGB());
        Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 250, this.main_posY + 15, 140.0f, 10.0f, 3.0f, new Color(52, 38, 58, 250));
        if (currentMode == CurrentMode.Modules) {
            FontRenderers.icons.drawString(context.getMatrices(), "s", (double)(this.main_posX + 378), (double)(this.main_posY + 18), this.searching ? new Color(-872415233, true).getRGB() : new Color(-2080374785, true).getRGB());
        }
        if (this.isHoveringItem(this.main_posX + 250, this.main_posY + 15, 140.0f, 20.0f, mouseX, mouseY)) {
            Render2DEngine.addWindow(context.getMatrices(), this.main_posX + 250, this.main_posY + 15, this.main_posX + 250 + 140, this.main_posY + 15 + 10, 1.0);
            Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 250, this.main_posY + 15, 140.0f, 10.0f, 3.0f, new Color(84, 63, 94, 36));
            Render2DEngine.drawBlurredShadow(context.getMatrices(), mouseX - 20, mouseY - 20, 40.0f, 40.0f, 60, new Color(-1017816450, true));
            Render2DEngine.popWindow();
        }
        if (currentMode == CurrentMode.Modules) {
            FontRenderers.modules.drawString(context.getMatrices(), this.search_string, (double)(this.main_posX + 252), (double)(this.main_posY + 19), this.searching ? new Color(-872415233, true).getRGB() : new Color(-2080374785, true).getRGB());
        }
        if (currentMode == CurrentMode.CfgManager) {
            FontRenderers.modules.drawString(context.getMatrices(), this.config_string, (double)(this.main_posX + 252), (double)(this.main_posY + 19), this.listening_config ? new Color(-872415233, true).getRGB() : new Color(-2080374785, true).getRGB());
            Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 368, this.main_posY + 17, 20.0f, 6.0f, 1.0f, this.isHoveringItem(this.main_posX + 368, this.main_posY + 17, 20.0f, 6.0f, mouseX, mouseY) ? new Color(59, 42, 63, 194) : new Color(33, 23, 35, 194));
            FontRenderers.modules.drawCenteredString(context.getMatrices(), "+", (double)(this.main_posX + 378), (double)(this.main_posY + 16), ThunderHackGui.getColorByTheme(2).getRGB());
        }
        if (currentMode == CurrentMode.FriendManager) {
            FontRenderers.modules.drawString(context.getMatrices(), this.friend_string, (double)(this.main_posX + 252), (double)(this.main_posY + 19), this.listening_friend ? new Color(-872415233, true).getRGB() : new Color(-2080374785, true).getRGB());
            Render2DEngine.drawRound(context.getMatrices(), this.main_posX + 368, this.main_posY + 17, 20.0f, 6.0f, 1.0f, this.isHoveringItem(this.main_posX + 368, this.main_posY + 17, 20.0f, 6.0f, mouseX, mouseY) ? new Color(59, 42, 63, 194) : new Color(33, 23, 35, 194));
            FontRenderers.modules.drawCenteredString(context.getMatrices(), "+", (double)(this.main_posX + 378), (double)(this.main_posY + 16), ThunderHackGui.getColorByTheme(2).getRGB());
        }
        if (selected_plate == null) {
            return;
        }
        float scissorX1 = (float)Render2DEngine.interpolate(this.main_posX + 200, selected_plate.getPosX(), this.settings_animation) - 20.0f;
        float scissorY1 = (float)Render2DEngine.interpolate(this.main_posY + 40, selected_plate.getPosY(), this.settings_animation);
        float scissorX2 = Math.max((float)Render2DEngine.interpolate(395.0, 90.0, this.settings_animation) + (float)this.main_posX, (float)(this.main_posX + 205)) + 40.0f;
        float scissorY2 = Math.max((float)Render2DEngine.interpolate(this.main_height - 5, 30.0, this.settings_animation) + (float)this.main_posY, (float)(this.main_posY + 45));
        if (scissorX2 < scissorX1) {
            scissorX2 = scissorX1;
        }
        if (scissorY2 < scissorY1) {
            scissorY2 = scissorY1;
        }
        Render2DEngine.addWindow(context.getMatrices(), scissorX1, scissorY1, scissorX2, scissorY2, 1.0);
        if (!this.settings.isEmpty()) {
            float offsetY = 0.0f;
            for (SettingElement element : this.settings) {
                if (!element.isVisible()) continue;
                element.setOffsetY(offsetY);
                element.setX(this.main_posX + 210);
                element.setY((float)(this.main_posY + 45) + this.scroll);
                element.setWidth(175.0f);
                element.setHeight(15.0f);
                if (element instanceof ColorPickerComponent && ((ColorPickerComponent)element).isOpen()) {
                    element.setHeight(56.0f);
                }
                if (element instanceof ModeComponent) {
                    ModeComponent component2 = (ModeComponent)element;
                    component2.setWHeight(15.0);
                    if (component2.isOpen()) {
                        offsetY += (float)(component2.getSetting().getModes().length * 6);
                        element.setHeight(element.getHeight() + (float)(component2.getSetting().getModes().length * 6) + 3.0f);
                    } else {
                        element.setHeight(15.0f);
                    }
                }
                element.render(context.getMatrices(), mouseX, mouseY, partialTicks);
                offsetY += element.getHeight() + 3.0f;
            }
        }
        if (selected_plate == null || (double)this.settings_animation < 0.99) {
            // empty if block
        }
        Render2DEngine.popWindow();
    }

    private int getCategoryY(Module.Category category) {
        for (CategoryPlate categoryPlate : this.categories) {
            if (categoryPlate.getCategory() != category) continue;
            return categoryPlate.getPosY();
        }
        return 0;
    }

    public void onTick() {
        open_animation.update(open_direction);
        this.components.forEach(ModulePlate::onTick);
        this.settings.forEach(SettingElement::onTick);
        this.configs.forEach(ConfigComponent::onTick);
        this.friends.forEach(FriendComponent::onTick);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int clickedButton) {
        mouse_state = true;
        if (this.isHoveringItem(this.main_posX + 368, this.main_posY + 17, 20.0f, 6.0f, (float)mouseX, (float)mouseY)) {
            if (this.listening_config) {
                Managers.CONFIG.save(this.config_string);
                this.config_string = "Save config";
                this.listening_config = false;
                this.loadConfigs();
                return super.mouseClicked(mouseX, mouseY, clickedButton);
            }
            if (this.listening_friend) {
                Managers.FRIEND.addFriend(this.friend_string);
                this.friend_string = "Add friend";
                this.listening_friend = false;
                this.loadFriends();
                return super.mouseClicked(mouseX, mouseY, clickedButton);
            }
        }
        if (this.isHoveringItem(this.main_posX + 105, this.main_posY + 14, 11.0f, 11.0f, (float)mouseX, (float)mouseY)) {
            try {
                Util.getOperatingSystem().open(new File("ThunderHackRecode/configs/").toURI());
            }
            catch (Exception e) {
                Command.sendMessage("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0440\u043e\u0432\u043e\u0434\u043d\u0438\u043a!");
            }
        }
        if (this.isHoveringItem(this.main_posX + 20, this.main_posY + 195, 28.0f, 20.0f, (float)mouseX, (float)mouseY)) {
            this.current_category = null;
            currentMode = CurrentMode.CfgManager;
            this.settings.clear();
            this.components.clear();
        }
        if (this.isHoveringItem(this.main_posX + 50, this.main_posY + 195, 28.0f, 20.0f, (float)mouseX, (float)mouseY)) {
            this.current_category = null;
            currentMode = CurrentMode.FriendManager;
            this.settings.clear();
            this.components.clear();
        }
        if (this.isHoveringItem(this.main_posX, this.main_posY, 400.0f, 30.0f, (float)mouseX, (float)mouseY)) {
            this.drag_x = (int)(mouseX - (double)this.main_posX);
            this.drag_y = (int)(mouseY - (double)this.main_posY);
            this.dragging = true;
        }
        if (this.isHoveringItem(this.main_posX + 250, this.main_posY + 15, 140.0f, 10.0f, (float)mouseX, (float)mouseY) && currentMode == CurrentMode.Modules) {
            this.searching = true;
            ThunderHack.currentKeyListener = ThunderHack.KeyListening.ThunderGui;
        }
        if (this.isHoveringItem(this.main_posX + 250, this.main_posY + 15, 110.0f, 10.0f, (float)mouseX, (float)mouseY) && currentMode == CurrentMode.CfgManager) {
            this.listening_config = true;
            ThunderHack.currentKeyListener = ThunderHack.KeyListening.ThunderGui;
        }
        if (this.isHoveringItem(this.main_posX + 250, this.main_posY + 15, 110.0f, 10.0f, (float)mouseX, (float)mouseY) && currentMode == CurrentMode.FriendManager) {
            this.listening_friend = true;
            ThunderHack.currentKeyListener = ThunderHack.KeyListening.ThunderGui;
        }
        if (this.isHoveringItem(this.main_posX, this.main_posY + this.main_height - 6, 400.0f, 12.0f, (float)mouseX, (float)mouseY)) {
            this.rescale_y = (int)mouseY - this.main_height;
            this.rescale = true;
        }
        if (clickedButton == 2) {
            this.middleDragging = true;
            this.middleDragStartY = mouseY;
            this.middleDragStartSliderY = this.slider_y;
        }
        this.settings.forEach(component -> component.mouseClicked((int)mouseX, (int)mouseY, clickedButton));
        this.components.forEach(components -> components.mouseClicked((int)mouseX, (int)mouseY, clickedButton));
        this.categories.forEach(category -> category.mouseClicked((int)mouseX, (int)mouseY, 0));
        this.configs.forEach(component -> component.mouseClicked((int)mouseX, (int)mouseY, clickedButton));
        this.friends.forEach(component -> component.mouseClicked((int)mouseX, (int)mouseY, clickedButton));
        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouse_state = false;
        this.dragging = false;
        this.rescale = false;
        if (button == 2) {
            this.middleDragging = false;
        }
        this.settings.forEach(settingElement -> settingElement.mouseReleased((int)mouseX, (int)mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        try {
            this.keyTyped(GLFW.glfwGetKeyName((int)keyCode, (int)scanCode), keyCode);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (keyCode == 256) {
            super.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return false;
    }

    public void keyTyped(String typedChar, int keyCode) throws IOException {
        if (ThunderHack.currentKeyListener != ThunderHack.KeyListening.Sliders && ThunderHack.currentKeyListener != ThunderHack.KeyListening.ThunderGui) {
            return;
        }
        if (keyCode == 1) {
            open_direction = false;
            this.searching = false;
        }
        this.settings.forEach(settingElement -> settingElement.keyTyped(typedChar, keyCode));
        this.components.forEach(component -> component.keyTyped(typedChar, keyCode));
        if (this.searching) {
            if (keyCode == 340 || keyCode == 344) {
                return;
            }
            this.components.clear();
            if (this.search_string.equalsIgnoreCase("search")) {
                this.search_string = "";
            }
            int module_y = 0;
            for (Module module : Managers.MODULE.getModulesSearch(this.search_string)) {
                ModulePlate mPlate = new ModulePlate(module, this.main_posX + 100, this.main_posY + 40 + module_y, module_y / 35);
                if (!this.components.contains(mPlate)) {
                    this.components.add(mPlate);
                }
                module_y += 35;
            }
            if (keyCode == 257 || keyCode == 335) {
                this.search_string = "Search";
                this.searching = false;
                return;
            }
            if (keyCode == 259) {
                this.search_string = ThunderGui.removeLastChar(this.search_string);
                return;
            }
            if (keyCode >= 65 && keyCode <= 90 || keyCode >= 48 && keyCode <= 57) {
                this.search_string = this.search_string + typedChar;
            }
        }
        if (this.listening_config) {
            if (this.config_string.equalsIgnoreCase("Save config")) {
                this.config_string = "";
            }
            switch (keyCode) {
                case 256: {
                    this.config_string = "Save config";
                    this.listening_config = false;
                    return;
                }
                case 259: {
                    this.config_string = ThunderGui.removeLastChar(this.config_string);
                    return;
                }
                case 257: {
                    if (!this.config_string.equals("Save config") && !this.config_string.equals("")) {
                        Managers.CONFIG.save(this.config_string);
                        this.config_string = "Save config";
                        this.listening_config = false;
                        this.loadConfigs();
                    }
                    return;
                }
            }
            this.config_string = this.config_string + typedChar;
        }
        if (this.listening_friend) {
            if (this.friend_string.equalsIgnoreCase("Add friend")) {
                this.friend_string = "";
            }
            switch (keyCode) {
                case 256: {
                    this.friend_string = "Add friend";
                    this.listening_friend = false;
                    return;
                }
                case 259: {
                    this.friend_string = ThunderGui.removeLastChar(this.friend_string);
                    return;
                }
                case 257: {
                    if (!this.friend_string.equals("Add friend") && !this.config_string.equals("")) {
                        Managers.FRIEND.addFriend(this.friend_string);
                        this.friend_string = "Add friend";
                        this.listening_friend = false;
                        this.loadFriends();
                    }
                    return;
                }
            }
            this.friend_string = this.friend_string + typedChar;
        }
    }

    public boolean isHoveringItem(float x, float y, float x1, float y1, float mouseX, float mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x1 + x && mouseY <= y1 + y;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float dWheel = (int)(verticalAmount * 10.0);
        this.settings.forEach(component -> component.checkMouseWheel(dWheel));
        if (scroll_lock) {
            scroll_lock = false;
        } else {
            float scaledDWheel = dWheel * ThunderHackGui.scrollSpeed.getValue().floatValue();
            if (currentMode == CurrentMode.Modules) {
                if (selected_plate != null) {
                    this.scroll += scaledDWheel;
                } else if (this.isHoveringItem(this.main_posX + 4, this.main_posY + 38, 90.0f, (float)this.main_height - 80.0f, (float)mouseX, (float)mouseY)) {
                    this.slider_y += (int)scaledDWheel;
                    this.categories.forEach(category -> category.movePosition(0.0f, (int)scaledDWheel));
                } else {
                    this.components.forEach(component -> component.scrollElement(scaledDWheel));
                }
            }
            this.configs.forEach(component -> component.scrollElement(scaledDWheel));
            this.friends.forEach(component -> component.scrollElement(scaledDWheel));
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    static {
        open_animation = new EaseOutBack(5);
        open_direction = false;
        INSTANCE = new ThunderGui();
    }

    public static enum CurrentMode {
        Modules,
        CfgManager,
        FriendManager,
        WayPointManager,
        MacroManager;

    }
}

