/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.util.Identifier
 */
package thunder.hack.utility.render.shaders;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import thunder.hack.features.modules.Module;
import thunder.hack.utility.render.shaders.satin.api.managed.ManagedCoreShader;
import thunder.hack.utility.render.shaders.satin.api.managed.ShaderEffectManager;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform1f;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform2f;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform4f;

public class ArcShader {
    private Uniform2f uLocation;
    private Uniform2f uSize;
    private Uniform1f radius;
    private Uniform1f thickness;
    private Uniform1f time;
    private Uniform4f color1;
    private Uniform4f color2;
    private Uniform1f start;
    private Uniform1f end;
    public static final ManagedCoreShader ARC = ShaderEffectManager.getInstance().manageCoreShader(Identifier.of((String)"thunderhack", (String)"arc"), VertexFormats.POSITION);

    public ArcShader() {
        this.setup();
    }

    public void setParameters(float x, float y, float width, float height, float r, float thickness, float start, float end, Color c1, Color c2) {
        if (Module.mc.player == null) {
            return;
        }
        float i = (float)Module.mc.getWindow().getScaleFactor();
        this.radius.set(r * i);
        this.uLocation.set(x * i, -y * i + (float)Module.mc.getWindow().getScaledHeight() * i - height * i);
        this.uSize.set(width * i, height * i);
        this.color1.set((float)c1.getRed() / 255.0f, (float)c1.getGreen() / 255.0f, (float)c1.getBlue() / 255.0f, 1.0f);
        this.color2.set((float)c2.getRed() / 255.0f, (float)c2.getGreen() / 255.0f, (float)c2.getBlue() / 255.0f, 1.0f);
        this.time.set((float)Module.mc.player.age * 4.0f);
        this.thickness.set(thickness);
        this.start.set(start);
        this.end.set(end);
    }

    public void use() {
        RenderSystem.setShader(ARC::getProgram);
    }

    protected void setup() {
        this.uSize = ARC.findUniform2f("uSize");
        this.uLocation = ARC.findUniform2f("uLocation");
        this.radius = ARC.findUniform1f("radius");
        this.thickness = ARC.findUniform1f("thickness");
        this.start = ARC.findUniform1f("start");
        this.end = ARC.findUniform1f("end");
        this.time = ARC.findUniform1f("time");
        this.color1 = ARC.findUniform4f("color1");
        this.color2 = ARC.findUniform4f("color2");
    }
}

