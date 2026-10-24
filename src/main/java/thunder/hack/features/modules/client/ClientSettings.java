/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.features.modules.client;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public final class ClientSettings
extends Module {
    public static Setting<Boolean> futureCompatibility = new Setting<Boolean>("FutureCompatibility", false);
    public static Setting<Boolean> customMainMenu = new Setting<Boolean>("CustomMainMenu", true);
    public static Setting<Boolean> customPanorama = new Setting<Boolean>("CustomPanorama", true);
    public static Setting<Boolean> customLoadingScreen = new Setting<Boolean>("CustomLoadingScreen", true);
    public static Setting<Boolean> scaleFactorFix = new Setting<Boolean>("ScaleFactorFix", false);
    public static Setting<Float> scaleFactorFixValue = new Setting<Float>("ScaleFactorFixValue", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(4.0f));
    public static Setting<Boolean> renderRotations = new Setting<Boolean>("RenderRotations", true);
    public static Setting<Boolean> skullEmoji = new Setting<Boolean>("SkullEmoji", true);
    public static Setting<Boolean> clientMessages = new Setting<Boolean>("ClientMessages", true);
    public static Setting<Boolean> debug = new Setting<Boolean>("Debug", false);
    public static Setting<Boolean> customBob = new Setting<Boolean>("CustomBob", true);
    public static Setting<Boolean> telemetry = new Setting<Boolean>("Telemetry", true);
    public static Setting<Language> language = new Setting<Language>("Language", Language.ENG);
    public static Setting<String> prefix = new Setting<String>("Prefix", "@");
    public static Setting<ClipCommandMode> clipCommandMode = new Setting<ClipCommandMode>("ClipCommandMode", ClipCommandMode.Matrix);

    public ClientSettings() {
        super("ClientSettings", Module.Category.CLIENT);
    }

    public static boolean isRu() {
        return language.is(Language.RU);
    }

    @Override
    public boolean isToggleable() {
        return false;
    }

    public static enum Language {
        RU,
        ENG;

    }

    public static enum ClipCommandMode {
        Default,
        Matrix;

    }
}

