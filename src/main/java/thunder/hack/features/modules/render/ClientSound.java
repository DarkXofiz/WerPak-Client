/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.features.modules.render;

import thunder.hack.core.Managers;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class ClientSound
extends Module {
    private static ClientSound INSTANCE = new ClientSound();
    public final Setting<EnableSnd> enableSound = new Setting<EnableSnd>("EnableSound", EnableSnd.Ding);
    public final Setting<DisableSnd> disableSound = new Setting<DisableSnd>("DisableSound", DisableSnd.Swoosh);
    public final Setting<ClickSnd> clickSound = new Setting<ClickSnd>("ClickSound", ClickSnd.Boolean);

    public ClientSound() {
        super("ClientSound", Module.Category.RENDER);
        INSTANCE = this;
    }

    public static ClientSound getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientSound();
        }
        return INSTANCE;
    }

    public void onModuleEnable() {
        if (!this.isEnabled()) {
            return;
        }
        switch (this.enableSound.getValue().ordinal()) {
            case 0: {
                Managers.SOUND.playSound(Managers.SOUND.ENABLE_SOUNDEVENT);
                break;
            }
            case 1: {
                Managers.SOUND.playSound(Managers.SOUND.ENABLE_SOUNDEVENT);
                break;
            }
            case 2: {
                Managers.SOUND.playSwipeIn();
                break;
            }
            case 3: {
                Managers.SOUND.playSound(Managers.SOUND.ALERT_SOUNDEVENT);
                break;
            }
            case 4: {
                Managers.SOUND.playSound("enable");
                break;
            }
        }
    }

    public void onModuleDisable() {
        if (!this.isEnabled()) {
            return;
        }
        switch (this.disableSound.getValue().ordinal()) {
            case 0: {
                Managers.SOUND.playSwipeOut();
                break;
            }
            case 1: {
                Managers.SOUND.playSound(Managers.SOUND.DISABLE_SOUNDEVENT);
                break;
            }
            case 2: {
                Managers.SOUND.playSound(Managers.SOUND.ALERT_SOUNDEVENT);
                break;
            }
            case 3: {
                Managers.SOUND.playSound("disable");
                break;
            }
        }
    }

    public void onRightClick() {
        if (!this.isEnabled()) {
            return;
        }
        switch (this.clickSound.getValue().ordinal()) {
            case 0: {
                Managers.SOUND.playBoolean();
                break;
            }
            case 1: {
                Managers.SOUND.playSound(Managers.SOUND.KEYPRESS_SOUNDEVENT);
                break;
            }
            case 2: {
                Managers.SOUND.playSlider();
                break;
            }
            case 3: {
                Managers.SOUND.playSound(Managers.SOUND.ORTHODOX_SOUNDEVENT);
                break;
            }
            case 4: {
                Managers.SOUND.playSound("click");
                break;
            }
        }
    }

    public static enum EnableSnd {
        Ding,
        Inertia,
        Swipe,
        Alert,
        Custom,
        OFF;

    }

    public static enum DisableSnd {
        Swoosh,
        Inertia,
        Alert,
        Custom,
        OFF;

    }

    public static enum ClickSnd {
        Boolean,
        KeyBoard,
        Scroll,
        Orthodox,
        Custom,
        OFF;

    }
}

