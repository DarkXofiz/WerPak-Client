/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.features.modules.combat;

import thunder.hack.core.manager.client.ServerManager;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public final class HitBox
extends Module {
    public static final Setting<Float> XZExpand = new Setting<Float>("XZExpand", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f));
    public static final Setting<Float> YExpand = new Setting<Float>("YExpand", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(5.0f));
    public static final Setting<Boolean> affectToAura = new Setting<Boolean>("AffectToAura", false);

    public HitBox() {
        super("HitBoxes", Module.Category.COMBAT);
    }

    @Override
    public String getDisplayInfo() {
        return "H: " + ServerManager.round2(XZExpand.getValue().floatValue()) + " V: " + ServerManager.round2(YExpand.getValue().floatValue());
    }
}

