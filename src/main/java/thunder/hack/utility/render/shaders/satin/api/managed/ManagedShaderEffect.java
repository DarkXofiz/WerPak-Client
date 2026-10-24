/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gl.PostEffectProcessor
 */
package thunder.hack.utility.render.shaders.satin.api.managed;

import net.minecraft.client.gl.PostEffectProcessor;
import thunder.hack.utility.render.shaders.satin.api.managed.ManagedFramebuffer;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.SamplerUniformV2;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.UniformFinder;

public interface ManagedShaderEffect
extends UniformFinder {
    public PostEffectProcessor getShaderEffect();

    public void release();

    public void render(float var1);

    public ManagedFramebuffer getTarget(String var1);

    public void setUniformValue(String var1, int var2);

    public void setUniformValue(String var1, float var2);

    public void setUniformValue(String var1, float var2, float var3);

    public void setUniformValue(String var1, float var2, float var3, float var4);

    public void setUniformValue(String var1, float var2, float var3, float var4, float var5);

    @Override
    public SamplerUniformV2 findSampler(String var1);
}

