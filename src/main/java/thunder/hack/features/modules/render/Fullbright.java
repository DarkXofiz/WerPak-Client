/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.features.modules.render;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class Fullbright
extends Module {
    public static Setting<Float> minBright = new Setting<Float>("MinBright", Float.valueOf(0.5f), Float.valueOf(0.0f), Float.valueOf(1.0f));

    public Fullbright() {
        super("Fullbright", Module.Category.RENDER);
    }
}

