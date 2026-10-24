/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.Screen
 */
package thunder.hack.features.modules.client;

import java.awt.Color;
import net.minecraft.client.gui.screen.Screen;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClickGui;
import thunder.hack.gui.hud.HudEditorGui;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;

public final class HudEditor
extends Module {
    public static final Setting<Boolean> sticky = new Setting<Boolean>("Sticky", true);
    public static final Setting<HudStyle> hudStyle = new Setting<HudStyle>("HudStyle", HudStyle.Blurry);
    public static final Setting<ArrowsStyle> arrowsStyle = new Setting<ArrowsStyle>("ArrowsStyle", ArrowsStyle.Default);
    public static final Setting<ClickGui.colorModeEn> colorMode = new Setting<ClickGui.colorModeEn>("ColorMode", ClickGui.colorModeEn.Static);
    public static final Setting<Integer> colorSpeed = new Setting<Integer>("ColorSpeed", 18, 2, 54);
    public static final Setting<Boolean> glow = new Setting<Boolean>("Light", true);
    public static final Setting<ColorSetting> hcolor1 = new Setting<ColorSetting>("Color", new ColorSetting(-6974059));
    public static final Setting<ColorSetting> acolor = new Setting<ColorSetting>("Color2", new ColorSetting(-8365735));
    public static final Setting<ColorSetting> plateColor = new Setting<ColorSetting>("PlateColor", new ColorSetting(new Color(-419430400, true).getRGB()));
    public static final Setting<ColorSetting> textColor = new Setting<ColorSetting>("TextColor", new ColorSetting(new Color(-1, true).getRGB()));
    public static final Setting<ColorSetting> textColor2 = new Setting<ColorSetting>("TextColor2", new ColorSetting(new Color(-1, true).getRGB()));
    public static final Setting<ColorSetting> blurColor = new Setting<ColorSetting>("BlurColor", new ColorSetting(new Color(-16773595, true).getRGB()));
    public static final Setting<Float> hudRound = new Setting<Float>("HudRound", Float.valueOf(4.0f), Float.valueOf(1.0f), Float.valueOf(7.0f));
    public static final Setting<Float> alpha = new Setting<Float>("Alpha", Float.valueOf(0.9f), Float.valueOf(0.0f), Float.valueOf(1.0f));
    public static final Setting<Float> blend = new Setting<Float>("Blend", Float.valueOf(10.0f), Float.valueOf(1.0f), Float.valueOf(15.0f));
    public static final Setting<Float> outline = new Setting<Float>("Outline", Float.valueOf(0.5f), Float.valueOf(0.0f), Float.valueOf(2.5f));
    public static final Setting<Float> glow1 = new Setting<Float>("Glow", Float.valueOf(0.5f), Float.valueOf(0.0f), Float.valueOf(1.0f));
    public static final Setting<Float> blurOpacity = new Setting<Float>("BlurOpacity", Float.valueOf(0.55f), Float.valueOf(0.0f), Float.valueOf(1.0f));
    public static final Setting<Float> blurStrength = new Setting<Float>("BlurStrength", Float.valueOf(20.0f), Float.valueOf(5.0f), Float.valueOf(50.0f));

    public HudEditor() {
        super("HudEditor", Module.Category.CLIENT);
    }

    public static Color getColor(int count) {
        return switch (colorMode.getValue()) {
            case ClickGui.colorModeEn.Sky -> Render2DEngine.skyRainbow(colorSpeed.getValue(), count);
            case ClickGui.colorModeEn.LightRainbow -> Render2DEngine.rainbow(colorSpeed.getValue(), count, 0.6f, 1.0f, 1.0f);
            case ClickGui.colorModeEn.Rainbow -> Render2DEngine.rainbow(colorSpeed.getValue(), count, 1.0f, 1.0f, 1.0f);
            case ClickGui.colorModeEn.Fade -> Render2DEngine.fade(colorSpeed.getValue(), count, hcolor1.getValue().getColorObject(), 1.0f);
            case ClickGui.colorModeEn.DoubleColor -> Render2DEngine.TwoColoreffect(hcolor1.getValue().getColorObject(), acolor.getValue().getColorObject(), colorSpeed.getValue().intValue(), count);
            case ClickGui.colorModeEn.Analogous -> Render2DEngine.interpolateColorsBackAndForth(colorSpeed.getValue(), count, hcolor1.getValue().getColorObject(), Render2DEngine.getAnalogousColor(acolor.getValue().getColorObject()), true);
            default -> hcolor1.getValue().getColorObject();
        };
    }

    @Override
    public void onEnable() {
        mc.setScreen((Screen)HudEditorGui.getHudGui());
        this.disable();
    }

    public static enum HudStyle {
        Blurry,
        Glowing;

    }

    public static enum ArrowsStyle {
        Default,
        New;

    }
}

