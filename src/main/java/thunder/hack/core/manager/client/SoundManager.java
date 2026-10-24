/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.registry.Registry
 *  net.minecraft.util.Identifier
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.registry.Registries
 */
package thunder.hack.core.manager.client;

import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.registry.Registries;
import thunder.hack.core.manager.IManager;
import thunder.hack.core.manager.client.ConfigManager;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.features.modules.client.SoundFX;
import thunder.hack.utility.Timer;
import thunder.hack.utility.math.MathUtility;

public class SoundManager
implements IManager {
    public final Identifier KEYPRESS_SOUND = Identifier.of((String)"thunderhack:keypress");
    public SoundEvent KEYPRESS_SOUNDEVENT = SoundEvent.of((Identifier)this.KEYPRESS_SOUND);
    public final Identifier KEYRELEASE_SOUND = Identifier.of((String)"thunderhack:keyrelease");
    public SoundEvent KEYRELEASE_SOUNDEVENT = SoundEvent.of((Identifier)this.KEYRELEASE_SOUND);
    public final Identifier UWU_SOUND = Identifier.of((String)"thunderhack:uwu");
    public SoundEvent UWU_SOUNDEVENT = SoundEvent.of((Identifier)this.UWU_SOUND);
    public final Identifier ENABLE_SOUND = Identifier.of((String)"thunderhack:enable");
    public SoundEvent ENABLE_SOUNDEVENT = SoundEvent.of((Identifier)this.ENABLE_SOUND);
    public final Identifier DISABLE_SOUND = Identifier.of((String)"thunderhack:disable");
    public SoundEvent DISABLE_SOUNDEVENT = SoundEvent.of((Identifier)this.DISABLE_SOUND);
    public final Identifier MOAN1_SOUND = Identifier.of((String)"thunderhack:moan1");
    public SoundEvent MOAN1_SOUNDEVENT = SoundEvent.of((Identifier)this.MOAN1_SOUND);
    public final Identifier MOAN2_SOUND = Identifier.of((String)"thunderhack:moan2");
    public SoundEvent MOAN2_SOUNDEVENT = SoundEvent.of((Identifier)this.MOAN2_SOUND);
    public final Identifier MOAN3_SOUND = Identifier.of((String)"thunderhack:moan3");
    public SoundEvent MOAN3_SOUNDEVENT = SoundEvent.of((Identifier)this.MOAN3_SOUND);
    public final Identifier MOAN4_SOUND = Identifier.of((String)"thunderhack:moan4");
    public SoundEvent MOAN4_SOUNDEVENT = SoundEvent.of((Identifier)this.MOAN4_SOUND);
    public final Identifier SKEET_SOUND = Identifier.of((String)"thunderhack:skeet");
    public SoundEvent SKEET_SOUNDEVENT = SoundEvent.of((Identifier)this.SKEET_SOUND);
    public final Identifier ORTHODOX_SOUND = Identifier.of((String)"thunderhack:orthodox");
    public SoundEvent ORTHODOX_SOUNDEVENT = SoundEvent.of((Identifier)this.ORTHODOX_SOUND);
    public final Identifier BOOLEAN_SOUND = Identifier.of((String)"thunderhack:boolean");
    public SoundEvent BOOLEAN_SOUNDEVENT = SoundEvent.of((Identifier)this.BOOLEAN_SOUND);
    public final Identifier SCROLL_SOUND = Identifier.of((String)"thunderhack:scroll");
    public SoundEvent SCROLL_SOUNDEVENT = SoundEvent.of((Identifier)this.SCROLL_SOUND);
    public final Identifier SWIPEIN_SOUND = Identifier.of((String)"thunderhack:swipein");
    public SoundEvent SWIPEIN_SOUNDEVENT = SoundEvent.of((Identifier)this.SWIPEIN_SOUND);
    public final Identifier SWIPEOUT_SOUND = Identifier.of((String)"thunderhack:swipeout");
    public SoundEvent SWIPEOUT_SOUNDEVENT = SoundEvent.of((Identifier)this.SWIPEOUT_SOUND);
    public final Identifier ALERT_SOUND = Identifier.of((String)"thunderhack:alert");
    public SoundEvent ALERT_SOUNDEVENT = SoundEvent.of((Identifier)this.ALERT_SOUND);
    public final Identifier PM_SOUND = Identifier.of((String)"thunderhack:pmsound");
    public SoundEvent PM_SOUNDEVENT = SoundEvent.of((Identifier)this.PM_SOUND);
    public final Identifier RIFK_SOUND = Identifier.of((String)"thunderhack:rifk");
    public SoundEvent RIFK_SOUNDEVENT = SoundEvent.of((Identifier)this.RIFK_SOUND);
    public final Identifier CUTIE_SOUND = Identifier.of((String)"thunderhack:cutie");
    public SoundEvent CUTIE_SOUNDEVENT = SoundEvent.of((Identifier)this.CUTIE_SOUND);
    private final Timer scrollTimer = new Timer();

    public void registerSounds() {
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.KEYPRESS_SOUND, (Object)this.KEYPRESS_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.KEYRELEASE_SOUND, (Object)this.KEYRELEASE_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.ENABLE_SOUND, (Object)this.ENABLE_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.DISABLE_SOUND, (Object)this.DISABLE_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.MOAN1_SOUND, (Object)this.MOAN1_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.MOAN2_SOUND, (Object)this.MOAN2_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.MOAN3_SOUND, (Object)this.MOAN3_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.MOAN4_SOUND, (Object)this.MOAN4_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.UWU_SOUND, (Object)this.UWU_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.SKEET_SOUND, (Object)this.SKEET_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.ORTHODOX_SOUND, (Object)this.ORTHODOX_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.SCROLL_SOUND, (Object)this.SCROLL_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.BOOLEAN_SOUND, (Object)this.BOOLEAN_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.SWIPEIN_SOUND, (Object)this.SWIPEIN_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.SWIPEOUT_SOUND, (Object)this.SWIPEOUT_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.ALERT_SOUND, (Object)this.ALERT_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.PM_SOUND, (Object)this.PM_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.RIFK_SOUND, (Object)this.RIFK_SOUNDEVENT);
        Registry.register((Registry)Registries.SOUND_EVENT, (Identifier)this.CUTIE_SOUND, (Object)this.CUTIE_SOUNDEVENT);
    }

    public void playHitSound(SoundFX.HitSound value) {
        switch (value) {
            case UWU: {
                this.playSound(this.UWU_SOUNDEVENT);
                break;
            }
            case SKEET: {
                this.playSound(this.SKEET_SOUNDEVENT);
                break;
            }
            case KEYBOARD: {
                this.playSound(this.KEYPRESS_SOUNDEVENT);
                break;
            }
            case CUTIE: {
                this.playSound(this.CUTIE_SOUNDEVENT);
                break;
            }
            case MOAN: {
                SoundEvent sound = switch ((int)MathUtility.random(0.0f, 3.0f)) {
                    case 0 -> this.MOAN1_SOUNDEVENT;
                    case 1 -> this.MOAN2_SOUNDEVENT;
                    case 2 -> this.MOAN3_SOUNDEVENT;
                    default -> this.MOAN4_SOUNDEVENT;
                };
                this.playSound(sound);
                break;
            }
            case RIFK: {
                this.playSound(this.RIFK_SOUNDEVENT);
                break;
            }
            case CUSTOM: {
                this.playSound("hit");
            }
        }
    }

    public void playEnable() {
        if (ModuleManager.soundFX.enableMode.getValue() == SoundFX.OnOffSound.Inertia) {
            this.playSound(this.ENABLE_SOUNDEVENT);
        } else if (ModuleManager.soundFX.enableMode.getValue() == SoundFX.OnOffSound.Custom) {
            this.playSound("enable");
        }
    }

    public void playDisable() {
        if (ModuleManager.soundFX.disableMode.getValue() == SoundFX.OnOffSound.Inertia) {
            this.playSound(this.DISABLE_SOUNDEVENT);
        } else if (ModuleManager.soundFX.disableMode.getValue() == SoundFX.OnOffSound.Custom) {
            this.playSound("disable");
        }
    }

    public void playScroll() {
        if (this.scrollTimer.every(50L)) {
            if (ModuleManager.soundFX.scrollSound.getValue() == SoundFX.ScrollSound.KeyBoard) {
                this.playSound(this.KEYPRESS_SOUNDEVENT);
            } else if (ModuleManager.soundFX.scrollSound.getValue() == SoundFX.ScrollSound.Custom) {
                this.playSound("scroll");
            }
        }
    }

    public void playSound(SoundEvent sound) {
        if (SoundManager.mc.player != null && SoundManager.mc.world != null) {
            SoundManager.mc.world.playSound((PlayerEntity)SoundManager.mc.player, SoundManager.mc.player.getBlockPos(), sound, SoundCategory.BLOCKS, (float)ModuleManager.soundFX.volume.getValue().intValue() / 100.0f, 1.0f);
        }
    }

    public void playSound(String name) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(ConfigManager.SOUNDS_FOLDER, name + ".wav").getAbsoluteFile()));
            FloatControl floatControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            floatControl.setValue(floatControl.getMaximum() - floatControl.getMinimum() * ((float)ModuleManager.soundFX.volume.getValue().intValue() / 100.0f) + floatControl.getMinimum());
            clip.start();
        }
        catch (Exception e) {
            Command.sendMessage((ClientSettings.isRu() ? "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u043e\u0441\u043f\u0440\u043e\u0438\u0437\u0432\u0435\u0434\u0435\u043d\u0438\u044f \u0437\u0432\u0443\u043a\u0430! \u041f\u0440\u043e\u0432\u0435\u0440\u044c " : "Error with playing sound! Check ") + new File(ConfigManager.SOUNDS_FOLDER, name + ".wav").getAbsolutePath());
        }
    }

    public void playSlider() {
        this.playSound(this.SCROLL_SOUNDEVENT);
    }

    public void playBoolean() {
        this.playSound(this.BOOLEAN_SOUNDEVENT);
    }

    public void playSwipeIn() {
        this.playSound(this.SWIPEIN_SOUNDEVENT);
    }

    public void playSwipeOut() {
        this.playSound(this.SWIPEOUT_SOUNDEVENT);
    }

    public void playPmSound() {
        this.playSound(this.PM_SOUNDEVENT);
    }
}

