/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.features.modules.render;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class AspectRatio
extends Module {
    public Setting<Float> ratio = new Setting<Float>("Ratio", Float.valueOf(1.78f), Float.valueOf(0.1f), Float.valueOf(5.0f));

    public AspectRatio() {
        super("AspectRatio", Module.Category.RENDER);
    }
}

