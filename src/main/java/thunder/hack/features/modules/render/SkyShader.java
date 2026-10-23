/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.features.modules.render;

import java.awt.Color;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.shaders.SkyShaderProgram;

public class SkyShader
extends Module {
    public final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Galaxy);
    public final Setting<ColorSetting> color1 = new Setting<ColorSetting>("Color1", new ColorSetting(new Color(7536895)));
    public final Setting<ColorSetting> color2 = new Setting<ColorSetting>("Color2", new ColorSetting(new Color(65493)));
    public SkyShaderProgram shaderProgram;

    public SkyShader() {
        super("SkyShader", Module.Category.RENDER);
    }

    @Override
    public void onEnable() {
        if (this.shaderProgram == null) {
            this.shaderProgram = new SkyShaderProgram();
        }
    }

    @Override
    public void onDisable() {
        this.shaderProgram = null;
    }

    public static enum Mode {
        Galaxy;

    }
}

