/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.AreaEffectCloudEntity
 *  net.minecraft.component.type.PotionContentsComponent
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package thunder.hack.injection.accesors;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.component.type.PotionContentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={AreaEffectCloudEntity.class})
public interface IAreaEffectCloudEntity {
    @Accessor(value="potionContentsComponent")
    public PotionContentsComponent getPotionContentsComponent();
}

