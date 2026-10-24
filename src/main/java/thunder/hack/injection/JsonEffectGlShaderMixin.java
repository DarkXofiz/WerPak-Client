/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.gl.JsonEffectShaderProgram
 *  net.minecraft.client.gl.ShaderStage.Type
 *  net.minecraft.util.Identifier
 *  net.minecraft.resource.ResourceFactory
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package thunder.hack.injection;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import thunder.hack.utility.render.shaders.satin.impl.SamplerAccess;

@Mixin(value={JsonEffectShaderProgram.class})
public abstract class JsonEffectGlShaderMixin
implements SamplerAccess {
    @WrapOperation(at={@At(value="INVOKE", target="net/minecraft/util/Identifier.ofVanilla (Ljava/lang/String;)Lnet/minecraft/util/Identifier;", ordinal=0)}, method={"<init>"})
    Identifier constructProgramIdentifier(String arg, Operation<Identifier> original, ResourceFactory unused, String id) {
        if (!id.contains(":")) {
            return (Identifier)original.call(new Object[]{arg});
        }
        Identifier split = Identifier.of((String)id);
        return Identifier.of((String)split.getNamespace(), (String)("shaders/program/" + split.getPath() + ".json"));
    }

    @WrapOperation(at={@At(value="INVOKE", target="net/minecraft/util/Identifier.ofVanilla (Ljava/lang/String;)Lnet/minecraft/util/Identifier;", ordinal=0)}, method={"loadEffect"})
    private static Identifier constructProgramIdentifier(String arg, Operation<Identifier> original, ResourceFactory unused, ShaderStage.Type shaderType, String id) {
        if (!arg.contains(":")) {
            return (Identifier)original.call(new Object[]{arg});
        }
        Identifier split = Identifier.of((String)id);
        return Identifier.of((String)split.getNamespace(), (String)("shaders/program/" + split.getPath() + shaderType.getFileExtension()));
    }
}

