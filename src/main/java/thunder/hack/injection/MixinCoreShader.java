/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gl.ShaderProgram
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package thunder.hack.injection;

import java.util.List;
import java.util.Map;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import thunder.hack.utility.render.shaders.satin.impl.SamplerAccess;

@Mixin(value={ShaderProgram.class})
public abstract class MixinCoreShader
implements SamplerAccess {
    @Shadow
    @Final
    private Map<String, Object> samplers;

    @Override
    public boolean hasSampler(String name) {
        return this.samplers.containsKey(name);
    }

    @Override
    @Accessor(value="samplerNames")
    public abstract List<String> getSamplerNames();

    @Override
    @Accessor(value="loadedSamplerIds")
    public abstract List<Integer> getSamplerShaderLocs();
}

