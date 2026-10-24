/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.VertexFormat
 *  net.minecraft.util.Identifier
 */
package thunder.hack.utility.render.shaders.satin.api.managed;

import java.util.function.Consumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import thunder.hack.utility.render.shaders.satin.api.managed.ManagedCoreShader;
import thunder.hack.utility.render.shaders.satin.api.managed.ManagedShaderEffect;
import thunder.hack.utility.render.shaders.satin.impl.ReloadableShaderEffectManager;

public interface ShaderEffectManager {
    public static ShaderEffectManager getInstance() {
        return ReloadableShaderEffectManager.INSTANCE;
    }

    public ManagedShaderEffect manage(Identifier var1);

    public ManagedShaderEffect manage(Identifier var1, Consumer<ManagedShaderEffect> var2);

    public ManagedCoreShader manageCoreShader(Identifier var1);

    public ManagedCoreShader manageCoreShader(Identifier var1, VertexFormat var2);

    public ManagedCoreShader manageCoreShader(Identifier var1, VertexFormat var2, Consumer<ManagedCoreShader> var3);
}

