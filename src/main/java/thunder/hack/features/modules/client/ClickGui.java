/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baritone.api.BaritoneAPI
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.screen.Screen
 */
package thunder.hack.features.modules.client;

import baritone.api.BaritoneAPI;
import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.screen.Screen;
import thunder.hack.ThunderHack;
import thunder.hack.events.impl.EventSetting;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.setting.impl.ColorSetting;

public class ClickGui
extends Module {
    public final Setting<Gradient> gradientMode = new Setting<Gradient>("Gradient", Gradient.LeftToRight);
    public final Setting<ColorSetting> categoryColor = new Setting<ColorSetting>("BgColor", new ColorSetting(new Color(25, 20, 30, 100).getRGB()));
    public final Setting<TextSide> textSide = new Setting<TextSide>("TextSide", TextSide.Left);
    public final Setting<scrollModeEn> scrollMode = new Setting<scrollModeEn>("ScrollMode", scrollModeEn.Old);
    public final Setting<Integer> catHeight = new Setting<Integer>("CategoryHeight", 300, 100, 720, v -> this.scrollMode.is(scrollModeEn.New));
    public final Setting<Boolean> descriptions = new Setting<Boolean>("Descriptions", true);
    public final Setting<Boolean> blur = new Setting<Boolean>("Blur", true);
    public final Setting<Boolean> tips = new Setting<Boolean>("Tips", true);
    public final Setting<Boolean> pauseBaritone = new Setting<Boolean>("PauseBaritone", false);
    public final Setting<Image> image = new Setting<Image>("Image", Image.None);
    public final Setting<Integer> moduleWidth = new Setting<Integer>("ModuleWidth", 100, 50, 200);
    public final Setting<Integer> moduleHeight = new Setting<Integer>("ModuleHeight", 14, 8, 25);
    public final Setting<Integer> settingFontScale = new Setting<Integer>("SettingFontScale", 12, 6, 24);
    public final Setting<Integer> modulesFontScale = new Setting<Integer>("ModulesFontScale", 14, 6, 24);
    public static final Setting<BooleanSettingGroup> gear = new Setting<BooleanSettingGroup>("Gear", new BooleanSettingGroup(true));
    public final Setting<Integer> gearScale = new Setting<Integer>("GearScale", 60, 6, 300).addToGroup(gear);
    public static Setting<Float> gearDuration = new Setting<Float>("GearDuration", Float.valueOf(0.5f), Float.valueOf(0.1f), Float.valueOf(2.0f)).addToGroup(gear);
    public static Setting<Integer> gearStop = new Setting<Integer>("GearStop", 25, 10, 45).addToGroup(gear);
    public final Setting<Boolean> closeAnimation = new Setting<Boolean>("CloseAnimation", true);

    public ClickGui() {
        super("ClickGui", Module.Category.CLIENT);
        this.setBind(80, false, false);
    }

    @Override
    public void onEnable() {
        if (this.pauseBaritone.getValue().booleanValue() && !ClickGui.fullNullCheck() && ThunderHack.baritone) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("pause");
        }
        this.setGui();
    }

    @Override
    public void onDisable() {
        if (this.pauseBaritone.getValue().booleanValue() && !ClickGui.fullNullCheck() && ThunderHack.baritone) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("resume");
        }
    }

    public void setGui() {
        mc.setScreen((Screen)ClickGUI.getClickGui());
    }

    @Override
    public void onUpdate() {
        if (!(ClickGui.mc.currentScreen instanceof ClickGUI)) {
            this.disable();
        }
    }

    @EventHandler
    public void onSetting(EventSetting e) {
        try {
            if (e.getSetting() == this.settingFontScale) {
                FontRenderers.sf_medium_mini = FontRenderers.create(this.settingFontScale.getValue().intValue(), "sf_medium");
            }
            if (e.getSetting() == this.modulesFontScale) {
                FontRenderers.sf_medium_modules = FontRenderers.create(this.modulesFontScale.getValue().intValue(), "sf_medium");
            }
            if (e.getSetting() == this.image) {
                ClickGUI.getClickGui().imageAnimation.reset();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static enum Gradient {
        UpsideDown,
        LeftToRight,
        both;

    }

    public static enum TextSide {
        Left,
        Center;

    }

    public static enum scrollModeEn {
        New,
        Old;

    }

    public static enum Image {
        None("", 0, 0, new int[]{0, 0}, 0),
        Bdsm("bdsm.png", 1414, 823, new int[]{-100, 90}, 900),
        Cutie("cutie.png", 476, 490, new int[]{500, 60}, 500),
        Cutie2("cutie2.png", 540, 540, new int[]{500, 60}, 500),
        Nya("nya.png", 246, 238, new int[]{500, 60}, 500),
        Smile("smile.png", 324, 406, new int[]{500, 40}, 500),
        Bat("bat.png", 352, 503, new int[]{500, 40}, 500),
        Gacha("gacha.png", 592, 592, new int[]{500, 60}, 500),
        NFT("nft.png", 562, 494, new int[]{400, 110}, 500),
        ThousandSeven("thousand_seven.png", 380, 336, new int[]{400, 130}, 500);

        public final int fileWidth;
        public final int fileHeight;
        public final Identifier file;
        public final int[] pos;
        public final int size;

        private Image(String file, int fileWidth, int fileHeight, int[] pos, int size) {
            this.fileHeight = fileHeight;
            this.fileWidth = fileWidth;
            this.file = Identifier.of((String)"thunderhack", (String)("textures/gui/images/" + file));
            this.pos = pos;
            this.size = size;
        }
    }

    public static enum colorModeEn {
        Static,
        Sky,
        LightRainbow,
        Rainbow,
        Fade,
        DoubleColor,
        Analogous;

    }
}

