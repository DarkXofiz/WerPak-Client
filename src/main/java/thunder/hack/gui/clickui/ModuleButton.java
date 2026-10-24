/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.util.math.RotationAxis
 */
package thunder.hack.gui.clickui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.hud.impl.TargetHud;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClickGui;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.clickui.AbstractButton;
import thunder.hack.gui.clickui.AbstractElement;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.clickui.impl.BindElement;
import thunder.hack.gui.clickui.impl.BooleanElement;
import thunder.hack.gui.clickui.impl.BooleanParentElement;
import thunder.hack.gui.clickui.impl.ColorPickerElement;
import thunder.hack.gui.clickui.impl.ItemSelectElement;
import thunder.hack.gui.clickui.impl.ModeElement;
import thunder.hack.gui.clickui.impl.ParentElement;
import thunder.hack.gui.clickui.impl.SliderElement;
import thunder.hack.gui.clickui.impl.StringElement;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.misc.DialogScreen;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.Bind;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.impl.ItemSelectSetting;
import thunder.hack.setting.impl.PositionSetting;
import thunder.hack.setting.impl.SettingGroup;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.TextureStorage;
import thunder.hack.utility.render.animation.AnimationUtility;
import thunder.hack.utility.render.animation.GearAnimation;

public class ModuleButton
extends AbstractButton {
    private final List<AbstractElement> elements;
    public final Module module;
    private boolean open;
    private boolean hovered;
    private boolean prevHovered;
    private float animation;
    private float animation2;
    float category_animation = 0.0f;
    int ticksOpened;
    private final GearAnimation gearAnimation = new GearAnimation();
    private boolean binding = false;
    private boolean holdbind = false;

    public ModuleButton(Module module) {
        this.module = module;
        this.elements = new ArrayList<AbstractElement>();
        for (Setting setting : module.getSettings()) {
            if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled") && !setting.getName().equals("Drawn")) {
                this.elements.add(new BooleanElement(setting));
                continue;
            }
            if (setting.getValue() instanceof ColorSetting) {
                this.elements.add(new ColorPickerElement(setting));
                continue;
            }
            if (setting.getValue() instanceof BooleanSettingGroup) {
                this.elements.add(new BooleanParentElement(setting));
                continue;
            }
            if (setting.isNumberSetting() && setting.hasRestriction()) {
                this.elements.add(new SliderElement(setting));
                continue;
            }
            if (setting.getValue() instanceof ItemSelectSetting) {
                this.elements.add(new ItemSelectElement(setting));
                continue;
            }
            if (setting.getValue() instanceof SettingGroup) {
                this.elements.add(new ParentElement(setting));
                continue;
            }
            if (setting.isEnumSetting() && !(setting.getValue() instanceof PositionSetting)) {
                this.elements.add(new ModeElement(setting));
                continue;
            }
            if (setting.getValue() instanceof Bind && !setting.getName().equals("Keybind")) {
                this.elements.add(new BindElement(setting));
                continue;
            }
            if (!(setting.getValue() instanceof String) && !(setting.getValue() instanceof Character) || setting.getName().equalsIgnoreCase("displayName")) continue;
            this.elements.add(new StringElement(setting));
        }
    }

    @Override
    public void init() {
        this.elements.forEach(AbstractElement::init);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.hovered = Render2DEngine.isHovered(mouseX, mouseY, this.x, this.y, this.width, this.height);
        this.animation = AnimationUtility.fast(this.animation, this.module.isEnabled() ? 1.0f : 0.0f, 8.0f);
        this.animation2 = AnimationUtility.fast(this.animation2, 1.0f, 10.0f);
        if (this.hovered) {
            if (!this.prevHovered) {
                Managers.SOUND.playScroll();
            }
            ClickGUI.currentDescription = I18n.translate((String)this.module.getDescription(), (Object[])new Object[0]);
        }
        this.prevHovered = this.hovered;
        float ix = this.x + 5.0f;
        float iy = this.y + this.height / 2.0f - 3.0f;
        this.offsetY = AnimationUtility.fast(this.offsetY, this.target_offset, 20.0f);
        float offsetY = 0.0f;
        if (this.isOpen()) {
            Render2DEngine.drawGuiBase(context.getMatrices(), this.x + 4.0f, this.y + 2.0f, this.width - 8.0f, this.height + (float)this.getElementsHeight(), 1.0f, 0.0f);
            Render2DEngine.addWindow(context.getMatrices(), new Render2DEngine.Rectangle(this.x + 1.0f, this.y + this.height - 2.0f, this.width + this.x - 2.0f, (float)((double)(this.height + this.y + 1.0f) + this.getElementsHeight())));
            if (Module.mc.player != null) {
                if (ClickGui.gear.getValue().isEnabled()) {
                    Render2DEngine.addWindow(context.getMatrices(), new Render2DEngine.Rectangle(this.x, this.y + this.height + 1.0f, this.width + this.x + 6.0f, (float)((double)(this.height + this.y + 1.0f) + this.getElementsHeight())));
                    float px = this.x + 4.0f + (this.width - 8.0f) / 2.0f;
                    float py = this.y + 12.0f + (this.height + (float)this.getElementsHeight()) / 2.0f;
                    int gScale = ModuleManager.clickGui.gearScale.getValue();
                    context.getMatrices().push();
                    context.getMatrices().translate(px, py, 0.0f);
                    context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.gearAnimation.getValue()));
                    context.getMatrices().translate(-px, -py, 0.0f);
                    RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.Gear);
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
                    Render2DEngine.renderGradientTexture(context.getMatrices(), px - (float)gScale / 2.0f, py - (float)gScale / 2.0f, gScale, gScale, 0.0f, 0.0f, gScale, gScale, gScale, gScale, Render2DEngine.injectAlpha(HudEditor.getColor(270).darker(), 110), Render2DEngine.injectAlpha(HudEditor.getColor(0).darker(), 110), Render2DEngine.injectAlpha(HudEditor.getColor(180).darker(), 110), Render2DEngine.injectAlpha(HudEditor.getColor(90).darker(), 110));
                    RenderSystem.disableBlend();
                    context.getMatrices().translate(px, py, 0.0f);
                    context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)Render2DEngine.interpolate(Module.mc.player.age - 1, Module.mc.player.age, Render3DEngine.getTickDelta()) * -4.0f));
                    context.getMatrices().translate(-px, -py, 0.0f);
                    context.getMatrices().pop();
                    Render2DEngine.popWindow();
                }
            }
            if (Render2DEngine.isHovered(mouseX, mouseY, this.x + 4.0f, this.y + this.height - 12.0f, this.width - 8.0f, this.height + (float)this.getElementsHeight())) {
                Render2DEngine.drawBlurredShadow(context.getMatrices(), mouseX - 10, mouseY - 10, 20.0f, 20.0f, 40, HudEditor.getColor(270));
            }
            for (AbstractElement element : this.elements) {
                if (!element.isVisible()) continue;
                element.setOffsetY(offsetY);
                element.setX(this.x);
                element.setY(this.y + this.height + 2.0f);
                element.setWidth(this.width);
                element.setHeight(13.0f);
                if (element instanceof ColorPickerElement) {
                    ColorPickerElement picker = (ColorPickerElement)element;
                    element.setHeight(picker.getHeight());
                } else if (element instanceof SliderElement) {
                    element.setHeight(18.0f);
                }
                if (element instanceof ModeElement) {
                    ModeElement combobox = (ModeElement)element;
                    combobox.setWHeight(13.0);
                    if (combobox.isOpen()) {
                        element.setHeight(13 + combobox.getSetting().getModes().length * 12);
                    } else {
                        element.setHeight(13.0f);
                    }
                }
                offsetY += element.getHeight();
            }
            context.getMatrices().push();
            TargetHud.sizeAnimation(context.getMatrices(), this.x + this.width / 2.0f + 6.0f, this.y + this.height / 2.0f - 12.0f, this.ticksOpened < 5 ? (double)Math.clamp(this.category_animation / offsetY, 0.0f, 1.0f) : 1.0);
            this.elements.forEach(e -> {
                if (e.isVisible()) {
                    e.render(context, mouseX, mouseY, delta);
                }
            });
            context.getMatrices().pop();
            Render2DEngine.drawBlurredShadow(context.getMatrices(), this.x + 3.0f, this.y + this.height, this.width - 6.0f, 3.0f, 13, HudEditor.getColor(1));
            if (!this.module.isEnabled()) {
                Render2DEngine.draw2DGradientRect(context.getMatrices(), this.x + 4.0f, this.y + this.height - 1.0f, this.x + 3.0f + this.width - 7.0f, 3.0f + this.y + this.height, Render2DEngine.applyOpacity(HudEditor.getColor(0), 0.0f), HudEditor.getColor(0), Render2DEngine.applyOpacity(HudEditor.getColor(90), 0.0f), HudEditor.getColor(90));
            }
            Render2DEngine.popWindow();
        } else if (this.hovered) {
            Render2DEngine.addWindow(context.getMatrices(), this.x + 1.0f, this.y, this.x + this.width - 2.0f, this.y + this.height, 1.0);
            Render2DEngine.drawBlurredShadow(context.getMatrices(), mouseX - 10, mouseY - 10, 20.0f, 20.0f, 35, HudEditor.getColor(270));
            Render2DEngine.popWindow();
        }
        this.category_animation = AnimationUtility.fast(this.category_animation, offsetY, 20.0f);
        if ((double)this.animation < 0.05) {
            Render2DEngine.drawRect(context.getMatrices(), this.x + 4.0f, this.y + 1.0f, this.width - 8.0f, this.height - 2.0f, Render2DEngine.applyOpacity(HudEditor.plateColor.getValue().getColorObject().darker(), 0.15f));
        } else {
            switch (ModuleManager.clickGui.gradientMode.getValue()) {
                case both: {
                    Render2DEngine.draw2DGradientRect(context.getMatrices(), this.x + 4.0f, this.y + 1.0f, this.x + 4.0f + this.width - 8.0f, this.y + 1.0f + this.height - 2.0f, Render2DEngine.applyOpacity(HudEditor.getColor(270), this.animation * 2.0f), Render2DEngine.applyOpacity(HudEditor.getColor(0), this.animation * 2.0f), Render2DEngine.applyOpacity(HudEditor.getColor(180), this.animation), Render2DEngine.applyOpacity(HudEditor.getColor(90), this.animation));
                    break;
                }
                case UpsideDown: {
                    Render2DEngine.draw2DGradientRect(context.getMatrices(), this.x + 4.0f, this.y + 1.0f, this.x + 4.0f + this.width - 8.0f, this.y + 1.0f + this.height - 2.0f, Render2DEngine.applyOpacity(HudEditor.getColor(270), this.animation * 2.0f), Render2DEngine.applyOpacity(HudEditor.getColor(0), this.animation * 2.0f), Render2DEngine.applyOpacity(HudEditor.getColor(270), this.animation), Render2DEngine.applyOpacity(HudEditor.getColor(0), this.animation));
                    break;
                }
                case LeftToRight: {
                    Render2DEngine.draw2DGradientRect(context.getMatrices(), this.x + 4.0f, this.y + 1.0f, this.x + 4.0f + this.width - 8.0f, this.y + 1.0f + this.height - 2.0f, Render2DEngine.applyOpacity(HudEditor.getColor(270), this.animation * 2.0f), Render2DEngine.applyOpacity(HudEditor.getColor(270), this.animation * 2.0f), Render2DEngine.applyOpacity(HudEditor.getColor(0), this.animation), Render2DEngine.applyOpacity(HudEditor.getColor(0), this.animation));
                }
            }
        }
        if (!this.module.getBind().getBind().equalsIgnoreCase("none") && !this.binding) {
            FontRenderers.sf_medium_modules.drawString(context.getMatrices(), this.getSbind(), (double)(this.x + this.width - 11.0f - FontRenderers.sf_medium_modules.getStringWidth(this.getSbind())), (double)(this.y + 6.0f), this.module.isEnabled() ? HudEditor.textColor2.getValue().getColor() : HudEditor.textColor.getValue().getColor());
        }
        if (this.binding) {
            FontRenderers.sf_medium_modules.drawString(context.getMatrices(), this.holdbind ? String.valueOf(Formatting.GRAY) + "Toggle / " + String.valueOf(Formatting.RESET) + "Hold" : String.valueOf(Formatting.RESET) + "Toggle " + String.valueOf(Formatting.GRAY) + "/ Hold", (double)(this.x + this.width - 11.0f - FontRenderers.sf_medium_modules.getStringWidth("Toggle/Hold")), (double)(iy + 2.0f), Render2DEngine.applyOpacity(Color.WHITE.getRGB(), this.animation2));
        }
        if (this.hovered && InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)340)) {
            FontRenderers.sf_medium_modules.drawString(context.getMatrices(), "Drawn " + (this.module.isDrawn() ? String.valueOf(Formatting.GREEN) + "TRUE" : String.valueOf(Formatting.RED) + "FALSE"), (double)(ix + 1.0f), (double)(iy + 2.0f), this.module.isEnabled() ? HudEditor.textColor2.getValue().getColor() : HudEditor.textColor.getValue().getColor());
        } else if (this.binding) {
            FontRenderers.sf_medium_modules.drawString(context.getMatrices(), "PressKey", (double)ix, (double)(iy + 2.0f), this.module.isEnabled() ? Render2DEngine.applyOpacity(HudEditor.textColor2.getValue().getColor(), this.animation2) : Render2DEngine.applyOpacity(HudEditor.textColor.getValue().getColor(), this.animation2));
        } else if (ModuleManager.clickGui.textSide.getValue() == ClickGui.TextSide.Left) {
            FontRenderers.sf_medium_modules.drawString(context.getMatrices(), this.module.getName(), (double)(ix + 2.0f), (double)(iy + 2.0f), this.module.isEnabled() ? HudEditor.textColor2.getValue().getColor() : HudEditor.textColor.getValue().getColor());
        } else {
            FontRenderers.sf_medium_modules.drawCenteredString(context.getMatrices(), this.module.getName(), (double)(ix + this.getWidth() / 2.0f - 4.0f), (double)(iy + 2.0f), this.module.isEnabled() ? HudEditor.textColor2.getValue().getColor() : HudEditor.textColor.getValue().getColor());
        }
    }

    @NotNull
    private String getSbind() {
        String sbind = this.module.getBind().getBind();
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

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.binding) {
            if ((float)mouseX > this.x + 56.0f && (float)mouseX < this.x + 67.0f && (float)mouseY > this.y && (float)mouseY < this.y + this.height) {
                this.holdbind = false;
                this.module.getBind().setHold(false);
                return;
            }
            if ((float)mouseX > this.x + 78.0f && (float)mouseX < this.x + 88.0f && (float)mouseY > this.y && (float)mouseY < this.y + this.height) {
                this.holdbind = true;
                this.module.getBind().setHold(true);
                return;
            }
            this.module.setBind(button, true, this.holdbind);
            this.binding = false;
        }
        if (this.hovered) {
            if (InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)340) && button == 0) {
                this.module.setDrawn(!this.module.isDrawn());
                return;
            }
            if (InputUtil.isKeyPressed((long)Module.mc.getWindow().getHandle(), (int)261) && button == 0) {
                DialogScreen dialogScreen = new DialogScreen(TextureStorage.questionPic, ClientSettings.isRu() ? "\u0421\u0431\u0440\u043e\u0441 \u043c\u043e\u0434\u0443\u043b\u044f" : "Reset module", ClientSettings.isRu() ? "\u0422\u044b \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0442\u0435\u043b\u044c\u043d\u043e \u0445\u043e\u0447\u0435\u0448\u044c \u0441\u0431\u0440\u043e\u0441\u0438\u0442\u044c " + this.module.getName() + "?" : "Are you sure you want to reset " + this.module.getName() + "?", ClientSettings.isRu() ? "\u0414\u0430" : "Yes", ClientSettings.isRu() ? "\u041d\u0435\u0442" : "No", () -> {
                    if (this.module.isEnabled()) {
                        this.module.disable("reseting");
                    }
                    for (Setting s : this.module.getSettings()) {
                        Object patt0$temp = s.getValue();
                        if (patt0$temp instanceof ColorSetting) {
                            ColorSetting cs = (ColorSetting)patt0$temp;
                            cs.setDefault();
                            continue;
                        }
                        s.setValue(s.getDefaultValue());
                    }
                    Module.mc.setScreen(null);
                }, () -> Module.mc.setScreen(null));
                Module.mc.setScreen((Screen)dialogScreen);
            }
            if (button == 0) {
                if (this.module.isToggleable()) {
                    this.module.toggle();
                }
            } else if (button == 1) {
                this.setOpen(!this.isOpen());
                if (this.open) {
                    Managers.SOUND.playSwipeIn();
                } else {
                    Managers.SOUND.playSwipeOut();
                }
                this.animation = 0.5f;
            } else if (button == 2) {
                this.animation2 = 0.0f;
                boolean bl = this.binding = !this.binding;
            }
        }
        if (this.open) {
            this.elements.forEach(element -> {
                if (element.isVisible()) {
                    element.mouseClicked(mouseX, mouseY, button);
                }
            });
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        if (this.isOpen()) {
            this.elements.forEach(element -> element.mouseReleased(mouseX, mouseY, button));
        }
    }

    @Override
    public void charTyped(char key, int keyCode) {
        if (this.isOpen()) {
            for (AbstractElement element : this.elements) {
                element.charTyped(key, keyCode);
            }
        }
    }

    @Override
    public void keyTyped(int keyCode) {
        if (this.isOpen()) {
            for (AbstractElement element : this.elements) {
                element.keyTyped(keyCode);
            }
        }
        if (this.binding) {
            if (keyCode == 256 || keyCode == 261) {
                this.module.setBind(-1, false, this.holdbind);
                Command.sendMessage((ClientSettings.isRu() ? "\u0423\u0434\u0430\u043b\u0435\u043d \u0431\u0438\u043d\u0434 \u0441 \u043c\u043e\u0434\u0443\u043b\u044f " : "Removed bind from ") + this.module.getName());
            } else {
                this.module.setBind(keyCode, false, this.holdbind);
                Command.sendMessage(this.module.getName() + (ClientSettings.isRu() ? " \u0431\u0438\u043d\u0434 \u0438\u0437\u043c\u0435\u043d\u0435\u043d \u043d\u0430 " : " bind changed to ") + this.module.getBind().getBind());
            }
            this.binding = false;
        }
    }

    @Override
    public void onGuiClosed() {
        this.elements.forEach(AbstractElement::onClose);
    }

    public List<AbstractElement> getElements() {
        return this.elements;
    }

    public double getElementsHeight() {
        return this.category_animation;
    }

    public double interp(double d, double d2, float d3) {
        return d2 + (d - d2) * (double)d3;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public void tick() {
        if (this.isOpen()) {
            this.gearAnimation.tick();
            ++this.ticksOpened;
        } else {
            this.ticksOpened = 0;
        }
    }
}

