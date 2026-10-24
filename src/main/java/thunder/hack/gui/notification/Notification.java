/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.gui.notification;

import java.awt.Color;
import net.minecraft.util.Formatting;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.core.manager.client.NotificationManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.Timer;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.EaseOutBack;

public class Notification {
    private final String message;
    private final String title;
    private final String icon;
    private final int lifeTime;
    public final EaseOutBack animation;
    private float y;
    private float width;
    private float animationX;
    private float height = 25.0f;
    private boolean direction = false;
    private final Timer timer = new Timer();

    public Notification(String title, String message, Type type, int time) {
        this.lifeTime = time;
        this.title = title;
        this.message = message;
        switch (type.ordinal()) {
            case 1: {
                this.icon = "J";
                break;
            }
            case 4: {
                this.icon = "K";
                break;
            }
            case 3: 
            case 5: {
                this.icon = "I";
                break;
            }
            case 2: {
                this.icon = "L";
                break;
            }
            default: {
                this.icon = "H";
            }
        }
        this.width = NotificationManager.isDefault() ? FontRenderers.sf_bold_mini.getStringWidth(message) + 38.0f : FontRenderers.sf_bold_micro.getStringWidth(title + " " + message) + 20.0f;
        this.height = NotificationManager.isDefault() ? 25.0f : 13.0f;
        this.animation = new EaseOutBack(NotificationManager.isDefault() ? 10 : 20);
        this.animationX = this.width;
        this.y = NotificationManager.isDefault() ? (float)Module.mc.getWindow().getScaledHeight() - this.height : (float)Module.mc.getWindow().getScaledHeight() / 2.0f + 10.0f;
    }

    public void render(MatrixStack matrix, float getY) {
        int animatedAlpha = (int)MathUtility.clamp((1.0 - this.animation.getAnimationd()) * 255.0, 0.0, 255.0);
        Color color = new Color(170, 170, 170, animatedAlpha);
        if (NotificationManager.isDefault()) {
            this.direction = this.isFinished();
            this.animationX = (float)((double)this.width * this.animation.getAnimationd());
            this.y = this.animate(this.y, getY);
            float x = (float)(Module.mc.getWindow().getScaledWidth() - 6) - this.width + this.animationX;
            if (HudEditor.hudStyle.is(HudEditor.HudStyle.Glowing)) {
                Render2DEngine.verticalGradient(matrix, x + 25.0f, this.y + 1.0f, x + 25.5f, this.y + 12.0f, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
                Render2DEngine.verticalGradient(matrix, x + 25.0f, this.y + 11.0f, x + 25.5f, this.y + 22.0f, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
            } else {
                Render2DEngine.drawRect(matrix, x + 25.0f, this.y + 2.0f, 0.5f, 20.0f, Render2DEngine.injectAlpha(new Color(0x44FFFFFF, true), (int)((float)animatedAlpha * 0.1f)));
            }
            FontRenderers.sf_bold_mini.drawString(matrix, this.title, (double)(x + 30.0f), (double)(this.y + 6.0f), HudEditor.textColor.getValue().getColor());
            FontRenderers.sf_bold_mini.drawString(matrix, this.message, (double)(x + 30.0f), (double)(this.y + 15.0f), color.getRGB());
            FontRenderers.mid_icons.drawString(matrix, this.icon, (double)(x + 5.0f), (double)(this.y + 7.0f), color.getRGB());
        } else {
            this.direction = this.isFinished();
            this.animationX = (float)((double)this.width * this.animation.getAnimationd());
            this.y = this.animate(this.y, getY);
            float x = (float)Module.mc.getWindow().getScaledWidth() / 2.0f - this.width / 2.0f;
            if (HudEditor.hudStyle.is(HudEditor.HudStyle.Glowing)) {
                Render2DEngine.verticalGradient(matrix, x + 13.0f, this.y + 1.0f, x + 13.5f, this.y + 6.0f, Render2DEngine.injectAlpha(color, 0), Render2DEngine.injectAlpha(color, animatedAlpha));
                Render2DEngine.verticalGradient(matrix, x + 13.0f, this.y + 6.0f, x + 13.5f, this.y + 11.0f, Render2DEngine.injectAlpha(color, animatedAlpha), Render2DEngine.injectAlpha(color, 0));
            } else {
                Render2DEngine.drawRect(matrix, x + 13.0f, this.y + 1.0f, 0.5f, 10.0f, Render2DEngine.injectAlpha(new Color(0x44FFFFFF, true), (int)((float)animatedAlpha * 0.1f)));
            }
            FontRenderers.sf_bold_micro.drawString(matrix, this.title + " " + this.message, (double)(x + 16.0f), (double)(this.y + 5.0f), color.getRGB());
            FontRenderers.icons.drawString(matrix, this.icon, (double)(x + 3.0f), (double)(this.y + 5.5f), color.getRGB());
        }
    }

    public void onUpdate() {
        this.animation.update(this.direction);
    }

    public void renderShaders(MatrixStack matrix, float getY) {
        this.direction = this.isFinished();
        this.animationX = (float)((double)this.width * this.animation.getAnimationd());
        this.y = this.animate(this.y, getY);
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawHudBase2(matrix, NotificationManager.isDefault() ? (float)(Module.mc.getWindow().getScaledWidth() - 6) - this.width + this.animationX : (float)Module.mc.getWindow().getScaledWidth() / 2.0f - this.width / 2.0f, this.y, this.width, this.height, NotificationManager.isDefault() ? 5.0f : 3.0f, HudEditor.blurStrength.getValue().floatValue(), HudEditor.blurOpacity.getValue().floatValue(), (float)MathUtility.clamp(1.0 - this.animation.getAnimationd(), 0.0, 1.0));
        } else {
            Render2DEngine.drawHudBase(matrix, NotificationManager.isDefault() ? (float)(Module.mc.getWindow().getScaledWidth() - 6) - this.width + this.animationX : (float)Module.mc.getWindow().getScaledWidth() / 2.0f - this.width / 2.0f, this.y, this.width, this.height, NotificationManager.isDefault() ? 5.0f : 3.0f, (float)MathUtility.clamp(1.0 - this.animation.getAnimationd(), 0.0, 1.0));
        }
    }

    private boolean isFinished() {
        return this.timer.passedMs(this.lifeTime);
    }

    public double getHeight() {
        return this.height;
    }

    public boolean shouldDelete() {
        return this.isFinished() && this.animationX >= this.width - 5.0f;
    }

    public float animate(float value, float target) {
        return value + (target - value) / 8.0f;
    }

    public static enum Type {
        SUCCESS("Success", Formatting.GREEN),
        INFO("Information", Formatting.AQUA),
        WARNING("Warning", Formatting.GOLD),
        ERROR("Error", Formatting.RED),
        ENABLED("Module enabled", Formatting.DARK_GREEN),
        DISABLED("Module disabled", Formatting.DARK_RED);

        final String name;
        final Formatting color;

        private Type(String name, Formatting color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public Formatting getColor() {
            return this.color;
        }
    }
}

