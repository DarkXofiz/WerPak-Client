/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ChatScreen
 *  org.lwjgl.glfw.GLFW
 */
package thunder.hack.features.hud;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.EventMouse;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.hud.HudEditorGui;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.PositionSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public class HudElement
extends Module {
    private final Setting<PositionSetting> pos = new Setting<PositionSetting>("Position", new PositionSetting(0.5f, 0.5f));
    private boolean mouseState = false;
    private boolean mouseButton = false;
    private float x;
    private float y;
    private float dragX;
    private float dragY;
    private float hitX;
    private float hitY;
    private float height;
    private float width;
    public static boolean anyHovered = false;

    public HudElement(String name, int width, int height) {
        super(name, Module.Category.HUD);
        this.height = height;
        this.width = width;
    }

    @Override
    public void onRender2D(DrawContext context) {
        this.y = (float)mc.getWindow().getScaledHeight() * this.pos.getValue().getY();
        this.x = (float)mc.getWindow().getScaledWidth() * this.pos.getValue().getX();
        if ((HudElement.mc.currentScreen instanceof ChatScreen || HudElement.mc.currentScreen instanceof HudEditorGui) && this.mouseButton && this.mouseState) {
            this.pos.getValue().setX(Math.clamp(Render2DEngine.scrollAnimate(((float)this.normaliseX() - this.dragX) / (float)mc.getWindow().getScaledWidth(), this.pos.getValue().getX(), 0.1f), 0.0f, 1.0f));
            this.pos.getValue().setY(Math.clamp(Render2DEngine.scrollAnimate(((float)this.normaliseY() - this.dragY) / (float)mc.getWindow().getScaledHeight(), this.pos.getValue().getY(), 0.1f), 0.0f, 1.0f - this.height / (float)mc.getWindow().getScaledHeight()));
            float finalX = 0.0f;
            float finalY = 0.0f;
            if (HudEditor.sticky.getValue().booleanValue()) {
                for (Module m : Managers.MODULE.getEnabledModules()) {
                    HudElement hudElement;
                    if (!(m instanceof HudElement) || (hudElement = (HudElement)m) == this || hudElement.getPosX() == 0.0f && hudElement.getPosY() == 0.0f) continue;
                    if (this.getPosX() > (float)mc.getWindow().getScaledWidth() / 2.0f) {
                        if (this.isNear(hudElement.getHitX() + hudElement.getWidth(), this.getHitX() + this.getWidth())) {
                            finalX = hudElement.getHitX() + hudElement.getWidth() - this.getWidth();
                        }
                    } else if (this.isNear(hudElement.getHitX(), this.getHitX())) {
                        finalX = hudElement.getHitX();
                    }
                    if (!this.isNear(hudElement.getHitY(), this.getHitY())) continue;
                    finalY = hudElement.getHitY();
                }
            }
            if (finalX != 0.0f || finalY != 0.0f) {
                Render2DEngine.drawRound(context.getMatrices(), finalX == 0.0f ? this.getHitX() : finalX, finalY == 0.0f ? this.getHitY() : finalY, this.width, this.height, 3.0f, new Color(2066689839, true));
            }
            if (finalX != 0.0f) {
                Render2DEngine.drawLine(finalX, 0.0f, finalX, mc.getWindow().getScaledHeight(), -1);
            }
            if (finalY != 0.0f) {
                Render2DEngine.drawLine(0.0f, finalY, mc.getWindow().getScaledWidth(), finalY, -1);
            }
        }
        if (this.mouseButton) {
            if (!this.mouseState && this.isHovering()) {
                this.dragX = (int)((float)this.normaliseX() - this.pos.getValue().getX() * (float)mc.getWindow().getScaledWidth());
                this.dragY = (int)((float)this.normaliseY() - this.pos.getValue().getY() * (float)mc.getWindow().getScaledHeight());
                this.mouseState = true;
            }
        } else {
            this.mouseState = false;
        }
        if (this.isHovering() && (HudElement.mc.currentScreen instanceof ChatScreen || HudElement.mc.currentScreen instanceof HudEditorGui)) {
            if (GLFW.glfwGetPlatform() != 393219) {
                GLFW.glfwSetCursor((long)mc.getWindow().getHandle(), (long)(this.mouseState ? GLFW.glfwCreateStandardCursor((int)221187) : GLFW.glfwCreateStandardCursor((int)221188)));
            }
            anyHovered = true;
        }
    }

    @EventHandler
    public void onMouse(@NotNull EventMouse event) {
        if (event.getAction() == 0 && event.getButton() == 1 && this.isHovering() && HudElement.mc.currentScreen instanceof HudEditorGui) {
            HudEditorGui.getHudGui().hudClicked(this);
        }
        if (event.getAction() == 0) {
            if (this.mouseButton && this.mouseState && HudEditor.sticky.getValue().booleanValue()) {
                for (Module m : Managers.MODULE.getEnabledModules()) {
                    HudElement hudElement;
                    if (!(m instanceof HudElement) || (hudElement = (HudElement)m) == this || hudElement.getHitX() == 0.0f && hudElement.getHitY() == 0.0f) continue;
                    float hitDifX = this.getPosX() - this.getHitX();
                    float hitDifY = this.getPosY() - this.getHitY();
                    if (this.getPosX() > (float)mc.getWindow().getScaledWidth() / 2.0f) {
                        if (this.isNear(hudElement.getHitX() + hudElement.getWidth(), this.getHitX() + this.getWidth())) {
                            this.pos.getValue().setX((hudElement.getHitX() + hitDifX + hudElement.getWidth() - this.getWidth()) / (float)mc.getWindow().getScaledWidth());
                        }
                    } else if (this.isNear(hudElement.getHitX(), this.getHitX())) {
                        this.pos.getValue().setX((hudElement.getHitX() + hitDifX) / (float)mc.getWindow().getScaledWidth());
                    }
                    if (!this.isNear(hudElement.getHitY(), this.getHitY())) continue;
                    this.pos.getValue().setY((hudElement.getHitY() + hitDifY) / (float)mc.getWindow().getScaledHeight());
                }
            }
            HudEditorGui.currentlyDragging = null;
            this.mouseButton = false;
        }
        if (event.getAction() == 1 && this.isHovering() && HudEditorGui.currentlyDragging == null) {
            HudEditorGui.currentlyDragging = this;
            this.mouseButton = true;
        }
    }

    public int normaliseX() {
        return (int)(HudElement.mc.mouse.getX() / Render3DEngine.getScaleFactor());
    }

    public int normaliseY() {
        return (int)(HudElement.mc.mouse.getY() / Render3DEngine.getScaleFactor());
    }

    public boolean isHovering() {
        return (float)this.normaliseX() > Math.min(this.hitX, this.hitX + this.width) && (float)this.normaliseX() < Math.max(this.hitX, this.hitX + this.width) && (float)this.normaliseY() > Math.min(this.hitY, this.hitY + this.height) && (float)this.normaliseY() < Math.max(this.hitY, this.hitY + this.height);
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setHitX(float hitX) {
        this.hitX = hitX;
    }

    public void setHitY(float hitY) {
        this.hitY = hitY;
    }

    public void setBounds(float x, float y, float w, float h) {
        this.setHitX(x);
        this.setHitY(y);
        this.setWidth(w);
        this.setHeight(h);
    }

    public float getPosX() {
        return this.x;
    }

    public float getHitX() {
        return this.hitX;
    }

    public float getHitY() {
        return this.hitY;
    }

    public float getPosY() {
        return this.y;
    }

    public float getX() {
        return this.pos.getValue().x;
    }

    public float getY() {
        return this.pos.getValue().y;
    }

    public float getHeight() {
        return this.height;
    }

    public float getWidth() {
        return this.width;
    }

    private boolean isNear(float n1, float n2) {
        return Math.abs(n1 - n2) < 10.0f;
    }
}

