/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.features.modules.client;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class ClientSpoof
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Vanilla);
    private final Setting<String> custom = new Setting<String>("Client", "feather", v -> this.mode.getValue() == Mode.Custom);

    public ClientSpoof() {
        super("ClientSpoof", Module.Category.CLIENT);
    }

    public String getClientName() {
        return switch (this.mode.getValue().ordinal()) {
            case 0 -> "vanilla";
            case 1 -> "lunarclient:1.20.4";
            case 2 -> "lunarclient:1.20.1";
            case 3 -> this.custom.getValue().toString();
            default -> null;
        };
    }

    public static enum Mode {
        Vanilla,
        Lunar1_20_4,
        Lunar1_20_1,
        Custom,
        Null;

    }
}

