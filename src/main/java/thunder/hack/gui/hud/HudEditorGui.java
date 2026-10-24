/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.gui.screen.Screen
 */
package thunder.hack.gui.hud;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.Screen;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClickGui;
import thunder.hack.gui.clickui.AbstractCategory;
import thunder.hack.gui.clickui.Category;
import thunder.hack.gui.clickui.ClickGUI;

public class HudEditorGui
extends Screen {
    public static HudElement currentlyDragging;
    private final List<AbstractCategory> windows = Lists.newArrayList();
    private static HudEditorGui instance;
    private boolean firstOpen = true;
    private double dWheel;

    public HudEditorGui() {
        super(Text.of((String)"HudEditorGui"));
        this.setInstance();
    }

    protected void init() {
        if (this.firstOpen) {
            Category window = new Category(Module.Category.HUD, Managers.MODULE.getModulesByCategory(Module.Category.HUD), (float)Module.mc.getWindow().getScaledWidth() / 2.0f - 50.0f, 20.0f, 100.0f, 18.0f);
            window.setOpen(true);
            this.windows.add(window);
            this.firstOpen = false;
        }
        this.windows.forEach(AbstractCategory::init);
    }

    public boolean shouldPause() {
        return false;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ClickGUI.anyHovered = false;
        if (ModuleManager.clickGui.scrollMode.getValue() == ClickGui.scrollModeEn.Old) {
            for (AbstractCategory window : this.windows) {
                if (InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)264)) {
                    window.setY(window.getY() + 2.0f);
                }
                if (InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)265)) {
                    window.setY(window.getY() - 2.0f);
                }
                if (InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)262)) {
                    window.setX(window.getX() + 2.0f);
                }
                if (InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)263)) {
                    window.setX(window.getX() - 2.0f);
                }
                if (this.dWheel == 0.0) continue;
                window.setY((float)((double)window.getY() + this.dWheel));
            }
        } else {
            for (AbstractCategory window : this.windows) {
                if (this.dWheel == 0.0) continue;
                window.setModuleOffset((float)this.dWheel, mouseX, mouseY);
            }
        }
        this.dWheel = 0.0;
        for (AbstractCategory window : this.windows) {
            window.render(context, mouseX, mouseY, delta);
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.dWheel = (int)(verticalAmount * 5.0);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.windows.forEach(w -> {
            w.mouseClicked((int)mouseX, (int)mouseY, button);
            this.windows.forEach(w1 -> {
                if (w.dragging && w != w1) {
                    w1.dragging = false;
                }
            });
        });
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.windows.forEach(w -> w.mouseReleased((int)mouseX, (int)mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.windows.forEach(w -> w.keyTyped(keyCode));
        if (keyCode == 256) {
            super.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return false;
    }

    public void removed() {
        ThunderHack.EVENT_BUS.unsubscribe((Object)this);
    }

    public void hudClicked(Module module) {
        for (AbstractCategory window : this.windows) {
            window.hudClicked(module);
        }
    }

    public static HudEditorGui getInstance() {
        if (instance == null) {
            instance = new HudEditorGui();
        }
        return instance;
    }

    public static HudEditorGui getHudGui() {
        return HudEditorGui.getInstance();
    }

    private void setInstance() {
        instance = this;
    }

    static {
        instance = new HudEditorGui();
    }
}

