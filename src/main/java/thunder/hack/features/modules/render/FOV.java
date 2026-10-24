/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.features.modules.render;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class FOV
extends Module {
    public final Setting<Integer> fovModifier = new Setting<Integer>("FOV modifier", 120, 0, 358);
    public final Setting<Boolean> itemFov = new Setting<Boolean>("Item Fov", false);
    public final Setting<Integer> itemFovModifier = new Setting<Integer>("Item FOV modifier", 120, 0, 358);

    public FOV() {
        super("FOV", Module.Category.RENDER);
    }
}

