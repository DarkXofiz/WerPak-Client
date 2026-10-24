/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.features.modules.client;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public final class Notifications
extends Module {
    public final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.CrossHair);

    public Notifications() {
        super("Notifications", Module.Category.CLIENT);
    }

    public static enum Mode {
        Default,
        CrossHair,
        Text;

    }
}

