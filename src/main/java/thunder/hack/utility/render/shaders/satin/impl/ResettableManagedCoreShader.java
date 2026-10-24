/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram
 *  net.minecraft.client.render.VertexFormat
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.resource.ResourceFactory
 *  net.minecraft.client.gl.ShaderProgram
 */
package thunder.hack.utility.render.shaders.satin.impl;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.client.gl.ShaderProgram;
import thunder.hack.utility.render.shaders.satin.api.managed.ManagedCoreShader;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.SamplerUniform;
import thunder.hack.utility.render.shaders.satin.impl.ManagedSamplerUniformV1;
import thunder.hack.utility.render.shaders.satin.impl.ManagedUniformBase;
import thunder.hack.utility.render.shaders.satin.impl.ResettableManagedShaderBase;

public final class ResettableManagedCoreShader
extends ResettableManagedShaderBase<ShaderProgram>
implements ManagedCoreShader {
    private final Consumer<ManagedCoreShader> initCallback;
    private final VertexFormat vertexFormat;
    private final Map<String, ManagedSamplerUniformV1> managedSamplers = new HashMap<String, ManagedSamplerUniformV1>();

    public ResettableManagedCoreShader(Identifier location, VertexFormat vertexFormat, Consumer<ManagedCoreShader> initCallback) {
        super(location);
        this.vertexFormat = vertexFormat;
        this.initCallback = initCallback;
    }

    @Override
    protected ShaderProgram parseShader(ResourceFactory resourceManager, MinecraftClient mc, Identifier location) throws IOException {
        return new FabricShaderProgram(resourceManager, this.getLocation(), this.vertexFormat);
    }

    @Override
    public void setup(int newWidth, int newHeight) {
        Preconditions.checkNotNull((Object)((ShaderProgram)this.shader));
        for (ManagedUniformBase uniform : this.getManagedUniforms()) {
            this.setupUniform(uniform, (ShaderProgram)this.shader);
        }
        this.initCallback.accept(this);
    }

    @Override
    public ShaderProgram getProgram() {
        return (ShaderProgram)this.shader;
    }

    @Override
    protected boolean setupUniform(ManagedUniformBase uniform, ShaderProgram shader) {
        return uniform.findUniformTarget(shader);
    }

    @Override
    public SamplerUniform findSampler(String samplerName) {
        return this.manageUniform(this.managedSamplers, ManagedSamplerUniformV1::new, samplerName, "sampler");
    }

    @Override
    protected void logInitError(IOException e) {
        LogUtils.getLogger().error("Could not create shader program {}", (Object)this.getLocation(), (Object)e);
    }
}

