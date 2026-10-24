/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gl.ShaderProgram
 */
package thunder.hack.utility.render.shaders.satin.api.managed;

import net.minecraft.client.gl.ShaderProgram;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.UniformFinder;

public interface ManagedCoreShader
extends UniformFinder {
    public ShaderProgram getProgram();

    public void release();
}

