/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.gl.Framebuffer
 */
package thunder.hack.utility.render.shaders.satin.api.managed.uniform;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.gl.Framebuffer;

public interface SamplerUniform {
    public void set(AbstractTexture var1);

    public void set(Framebuffer var1);

    public void set(int var1);
}

