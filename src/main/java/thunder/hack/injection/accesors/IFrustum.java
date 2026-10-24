/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.Frustum
 *  org.joml.FrustumIntersection
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package thunder.hack.injection.accesors;

import net.minecraft.client.render.Frustum;
import org.joml.FrustumIntersection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={Frustum.class})
public interface IFrustum {
    @Accessor
    public FrustumIntersection getFrustumIntersection();

    @Accessor(value="x")
    public double getX();

    @Accessor(value="x")
    public void setX(double var1);

    @Accessor(value="y")
    public double getY();

    @Accessor(value="y")
    public void setY(double var1);

    @Accessor(value="z")
    public double getZ();

    @Accessor(value="z")
    public void setZ(double var1);
}

