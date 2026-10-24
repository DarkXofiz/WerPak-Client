/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 */
package thunder.hack.gui.clickui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import thunder.hack.ThunderHack;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.BaritoneSettings;
import thunder.hack.features.modules.client.ClickGui;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.clickui.AbstractButton;
import thunder.hack.gui.clickui.AbstractCategory;
import thunder.hack.gui.clickui.AbstractElement;
import thunder.hack.gui.clickui.ModuleButton;
import thunder.hack.gui.clickui.impl.SearchBar;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

public class Category
extends AbstractCategory {
    private final Identifier ICON;
    private boolean scrollHover;
    private final List<AbstractButton> buttons = new ArrayList<AbstractButton>();
    public float catHeight;

    public Category(Module.Category category, ArrayList<Module> features, float x, float y, float width, float height) {
        super(category.getName(), x, y, width, height);
        this.ICON = Identifier.of((String)"thunderhack", (String)("textures/gui/headers/" + (Module.Category.isCustomCategory(category) ? "stock" : category.getName().toLowerCase()) + ".png"));
        if (category.getName().equals("Client")) {
            this.buttons.add(new SearchBar());
        }
        features.forEach(feature -> {
            if (!(feature instanceof BaritoneSettings) || ThunderHack.baritone) {
                this.buttons.add(new ModuleButton((Module)feature));
            }
        });
    }

    @Override
    public void init() {
        this.buttons.forEach(AbstractButton::init);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.setWidth(ModuleManager.clickGui.moduleWidth.getValue().intValue());
        this.scrollHover = Render2DEngine.isHovered(mouseX, mouseY, this.getX(), this.getY() + this.height, this.width, this.catHeight + 20.0f);
        context.getMatrices().push();
        boolean popStack = false;
        float height1 = ModuleManager.clickGui.scrollMode.getValue() == ClickGui.scrollModeEn.Old || this.getButtonsHeight() < (double)ModuleManager.clickGui.catHeight.getValue().intValue() ? (float)this.getButtonsHeight() : (float)ModuleManager.clickGui.catHeight.getValue().intValue();
        this.catHeight = AnimationUtility.fast(this.catHeight, height1, 30.0f);
        Color m1 = HudEditor.getColor(270);
        Color m2 = HudEditor.getColor(0);
        Color m3 = HudEditor.getColor(180);
        Color m4 = HudEditor.getColor(90);
        if (this.isOpen()) {
            if (ModuleManager.clickGui.scrollMode.getValue() != ClickGui.scrollModeEn.Old && !(this.getButtonsHeight() < (double)ModuleManager.clickGui.catHeight.getValue().intValue())) {
                Render2DEngine.addWindow(context.getMatrices(), this.getX() + 3.0f, this.getY() + this.height - 6.0f, this.getX() + 3.0f + this.width - 6.0f, this.getY() + this.height - 6.0f + (float)ModuleManager.clickGui.catHeight.getValue().intValue(), 1.0);
                popStack = true;
            }
            if (popStack) {
                double maxScroll = this.getButtonsHeight() - (double)ModuleManager.clickGui.catHeight.getValue().intValue() + 20.0;
                if (maxScroll > 0.0) {
                    if ((double)(-this.moduleOffset) > maxScroll) {
                        this.moduleOffset = -((float)maxScroll);
                    } else if (this.moduleOffset > 0.0f) {
                        this.moduleOffset = 0.0f;
                    }
                } else {
                    this.moduleOffset = 0.0f;
                }
            } else {
                this.moduleOffset = 0.0f;
            }
            for (AbstractButton button : this.buttons) {
                if (button instanceof ModuleButton) {
                    ModuleButton mb = (ModuleButton)button;
                    if (SearchBar.listening && !mb.module.getName().toLowerCase().contains(SearchBar.moduleName.toLowerCase())) continue;
                }
                button.setY(this.getY() + this.height + this.moduleOffset);
                button.setX(this.getX() + 2.0f);
                button.setWidth(this.width - 4.0f);
                button.setHeight(ModuleManager.clickGui.moduleHeight.getValue().intValue());
                button.render(context, mouseX, mouseY, delta);
            }
        }
        if (popStack) {
            Render2DEngine.popWindow();
        }
        Render2DEngine.drawRound(context.getMatrices(), this.getX(), this.getY(), this.width, 20.0f, 4.0f, HudEditor.getColor(270));
        if (this.isOpen()) {
            Render2DEngine.drawRound(context.getMatrices(), this.getX(), this.getY() + 20.0f, this.width, this.catHeight, 4.0f, ModuleManager.clickGui.categoryColor.getValue().getColorObject());
        }
        FontRenderers.sf_bold.drawString(context.getMatrices(), this.getName(), (double)(this.getX() + 20.0f), (double)(this.getY() + 6.5f), HudEditor.textColor.getValue().getColor());
        context.getMatrices().push();
        RenderSystem.setShaderTexture((int)0, (Identifier)this.ICON);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Color c = HudEditor.textColor.getValue().getColorObject();
        RenderSystem.setShaderColor((float)((float)c.getRed() / 255.0f), (float)((float)c.getGreen() / 255.0f), (float)((float)c.getBlue() / 255.0f), (float)((float)c.getAlpha() / 255.0f));
        context.drawTexture(this.ICON, (int)this.getX() + 3, (int)this.getY() + 3, 0.0f, 0.0f, 14, 14, 14, 14);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        context.getMatrices().pop();
        context.getMatrices().pop();
        this.updatePosition();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 1 && this.hovered) {
            this.setOpen(!this.isOpen());
        }
        super.mouseClicked(mouseX, mouseY, button);
        if (this.isOpen() && this.scrollHover) {
            this.buttons.forEach(b -> b.mouseClicked(mouseX, mouseY, button));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        if (this.isOpen()) {
            this.buttons.forEach(b -> b.mouseReleased(mouseX, mouseY, button));
        }
    }

    @Override
    public boolean keyTyped(int keyCode) {
        if (this.isOpen()) {
            for (AbstractButton button : this.buttons) {
                button.keyTyped(keyCode);
            }
        }
        return false;
    }

    @Override
    public void charTyped(char key, int keyCode) {
        if (this.isOpen()) {
            for (AbstractButton button : this.buttons) {
                button.charTyped(key, keyCode);
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        this.buttons.forEach(AbstractButton::onGuiClosed);
    }

    @Override
    public void tick() {
        this.buttons.forEach(AbstractButton::tick);
    }

    private void updatePosition() {
        float offsetY = 0.0f;
        float openY = 0.0f;
        for (AbstractButton button : this.buttons) {
            ModuleButton mbutton;
            if (button instanceof ModuleButton) {
                ModuleButton mb = (ModuleButton)button;
                if (SearchBar.listening && !mb.module.getName().toLowerCase().contains(SearchBar.moduleName.toLowerCase())) continue;
            }
            button.setTargetOffset(offsetY);
            if (button instanceof ModuleButton && (mbutton = (ModuleButton)button).isOpen()) {
                for (AbstractElement element : mbutton.getElements()) {
                    if (!element.isVisible()) continue;
                    offsetY += element.getHeight();
                }
                offsetY += 2.0f;
            }
            offsetY += button.getHeight() + openY;
        }
    }

    @Override
    public void hudClicked(Module module) {
        for (AbstractButton button : this.buttons) {
            if (!(button instanceof ModuleButton)) continue;
            ModuleButton mbutton = (ModuleButton)button;
            if (mbutton.module != module) continue;
            mbutton.setOpen(true);
        }
    }

    public double getButtonsHeight() {
        double height = 8.0;
        for (AbstractButton button : this.buttons) {
            if (button instanceof ModuleButton) {
                ModuleButton mb = (ModuleButton)button;
                if (SearchBar.listening && !mb.module.getName().toLowerCase().contains(SearchBar.moduleName.toLowerCase())) continue;
            }
            if (button instanceof ModuleButton) {
                ModuleButton mbutton = (ModuleButton)button;
                if (mbutton.isOpen()) {
                    height += 2.0;
                }
                height += mbutton.getElementsHeight();
            }
            height += (double)button.getHeight();
        }
        return height;
    }
}

