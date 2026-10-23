/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.features.modules.render;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class SmoothTab
extends Module {
    public final Setting<Float> animationSpeed = new Setting<Float>("Speed", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(20.0f));

    public SmoothTab() {
        super("SmoothTab", Module.Category.RENDER);
    }
}

