/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.features.modules.render;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class InventoryAnimation
extends Module {
    public static InventoryAnimation INSTANCE;
    public final Setting<AnimationType> animationType = new Setting<AnimationType>("Type", AnimationType.Scale);
    public final Setting<Float> speed = new Setting<Float>("Speed", Float.valueOf(10.0f), Float.valueOf(1.0f), Float.valueOf(30.0f));
    public final Setting<Boolean> inventoryOnly = new Setting<Boolean>("InventoryOnly", false);

    public InventoryAnimation() {
        super("InventoryAnimation", Module.Category.RENDER);
        INSTANCE = this;
    }

    public static enum AnimationType {
        Scale,
        Bounce,
        SlideUp,
        SlideDown,
        SlideLeft,
        SlideRight,
        Flip,
        Warp,
        Glitch;

    }
}

