/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFW
 */
package thunder.hack.setting.impl;

import java.lang.reflect.Field;
import org.lwjgl.glfw.GLFW;

public class Bind {
    private final int key;
    private boolean hold;
    private final boolean mouse;

    public Bind(int key, boolean mouse, boolean hold) {
        this.key = key;
        this.mouse = mouse;
        this.hold = hold;
    }

    public int getKey() {
        return this.key;
    }

    public String getBind() {
        Object kn;
        if (this.mouse) {
            return "M" + this.key;
        }
        Object object = kn = this.key > 0 ? GLFW.glfwGetKeyName((int)this.key, (int)0) : "None";
        if (kn == null) {
            try {
                for (Field declaredField : GLFW.class.getDeclaredFields()) {
                    int a;
                    if (!declaredField.getName().startsWith("GLFW_KEY_") || (a = ((Integer)declaredField.get(null)).intValue()) != this.key) continue;
                    String nb = declaredField.getName().substring("GLFW_KEY_".length());
                    kn = nb.substring(0, 1).toUpperCase() + nb.substring(1).toLowerCase();
                }
            }
            catch (Exception ignore) {
                kn = "unknown." + this.key;
            }
        }
        return this.key == -1 ? "None" : ((String)kn).toUpperCase();
    }

    public boolean isHold() {
        return this.hold;
    }

    public boolean isMouse() {
        return this.mouse;
    }

    public void setHold(boolean hold) {
        this.hold = hold;
    }
}

