/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Difficulty
 *  net.minecraft.entity.DamageUtil
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.enchantment.Enchantments
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.explosion.Explosion
 *  net.minecraft.world.explosion.Explosion.DestructionType
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.hit.HitResult.Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.block.BlockState
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext.FluidHandling
 *  net.minecraft.world.RaycastContext.ShapeType
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.registry.entry.RegistryEntry
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package thunder.hack.utility.world;

import java.util.Objects;
import net.minecraft.world.Difficulty;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.commons.lang3.mutable.MutableInt;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.IExplosion;
import thunder.hack.utility.math.PredictUtility;

public final class ExplosionUtility {
    public static boolean terrainIgnore = false;
    public static Explosion explosion;

    public static float getAutoCrystalDamage(Vec3d crystalPos, PlayerEntity target, int predictTicks, boolean optimized) {
        if (predictTicks == 0) {
            return ExplosionUtility.getExplosionDamage(crystalPos, target, optimized);
        }
        return ExplosionUtility.getExplosionDamageWPredict(crystalPos, target, PredictUtility.predictBox(target, predictTicks), optimized);
    }

    public static float getSelfExplosionDamage(Vec3d explosionPos, int predictTicks, boolean optimized) {
        return ExplosionUtility.getAutoCrystalDamage(explosionPos, (PlayerEntity)Module.mc.player, predictTicks, optimized);
    }

    public static float getExplosionDamage(Vec3d explosionPos, PlayerEntity target, boolean optimized) {
        double distExposure;
        if (Module.mc.world.getDifficulty() == Difficulty.PEACEFUL || target == null) {
            return 0.0f;
        }
        if (explosion == null) {
            explosion = new Explosion((World)Module.mc.world, (Entity)Module.mc.player, 1.0, 33.0, 7.0, 6.0f, false, Explosion.DestructionType.DESTROY);
        }
        ((IExplosion)explosion).setX(explosionPos.x);
        ((IExplosion)explosion).setY(explosionPos.y);
        ((IExplosion)explosion).setZ(explosionPos.z);
        if (((IExplosion)explosion).getWorld() != Module.mc.world) {
            ((IExplosion)explosion).setWorld((World)Module.mc.world);
        }
        if (!new Box((double)MathHelper.floor((double)(explosionPos.x - 11.0)), (double)MathHelper.floor((double)(explosionPos.y - 11.0)), (double)MathHelper.floor((double)(explosionPos.z - 11.0)), (double)MathHelper.floor((double)(explosionPos.x + 13.0)), (double)MathHelper.floor((double)(explosionPos.y + 13.0)), (double)MathHelper.floor((double)(explosionPos.z + 13.0))).intersects(target.getBoundingBox())) {
            return 0.0f;
        }
        if (!target.isImmuneToExplosion(explosion) && !target.isInvulnerable() && (distExposure = (double)((float)target.squaredDistanceTo(explosionPos)) / 144.0) <= 1.0) {
            terrainIgnore = false;
            double exposure = ExplosionUtility.getExposure(explosionPos, target.getBoundingBox(), optimized);
            terrainIgnore = false;
            double finalExposure = (1.0 - distExposure) * exposure;
            float toDamage = (float)Math.floor((finalExposure * finalExposure + finalExposure) / 2.0 * 7.0 * 12.0 + 1.0);
            if (Module.mc.world.getDifficulty() == Difficulty.EASY) {
                toDamage = Math.min(toDamage / 2.0f + 1.0f, toDamage);
            } else if (Module.mc.world.getDifficulty() == Difficulty.HARD) {
                toDamage = toDamage * 3.0f / 2.0f;
            }
            toDamage = DamageUtil.getDamageLeft((LivingEntity)target, (float)toDamage, (DamageSource)((IExplosion)explosion).getDamageSource(), (float)target.getArmor(), (float)((float)target.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue()));
            if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
                int resistance = 25 - (target.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
                float resistance_1 = toDamage * (float)resistance;
                toDamage = Math.max(resistance_1 / 25.0f, 0.0f);
            }
            if (toDamage <= 0.0f) {
                toDamage = 0.0f;
            } else {
                float protAmount = 32.0f;
                if (protAmount > 0.0f) {
                    toDamage = DamageUtil.getInflictedDamage((float)toDamage, (float)protAmount);
                }
            }
            return toDamage;
        }
        return 0.0f;
    }

    public static float getExplosionDamageWPredict(Vec3d explosionPos, PlayerEntity target, Box predict, boolean optimized) {
        double distExposure;
        if (Module.mc.world.getDifficulty() == Difficulty.PEACEFUL) {
            return 0.0f;
        }
        if (target == null || predict == null) {
            return 0.0f;
        }
        if (explosion == null) {
            explosion = new Explosion((World)Module.mc.world, (Entity)Module.mc.player, 1.0, 33.0, 7.0, 6.0f, false, Explosion.DestructionType.DESTROY);
        }
        ((IExplosion)explosion).setX(explosionPos.x);
        ((IExplosion)explosion).setY(explosionPos.y);
        ((IExplosion)explosion).setZ(explosionPos.z);
        if (((IExplosion)explosion).getWorld() != Module.mc.world) {
            ((IExplosion)explosion).setWorld((World)Module.mc.world);
        }
        if (!new Box((double)MathHelper.floor((double)(explosionPos.x - 11.0)), (double)MathHelper.floor((double)(explosionPos.y - 11.0)), (double)MathHelper.floor((double)(explosionPos.z - 11.0)), (double)MathHelper.floor((double)(explosionPos.x + 13.0)), (double)MathHelper.floor((double)(explosionPos.y + 13.0)), (double)MathHelper.floor((double)(explosionPos.z + 13.0))).intersects(predict)) {
            return 0.0f;
        }
        if (!target.isImmuneToExplosion(explosion) && !target.isInvulnerable() && (distExposure = predict.getCenter().add(0.0, -0.9, 0.0).squaredDistanceTo(explosionPos) / 144.0) <= 1.0) {
            terrainIgnore = false;
            double exposure = ExplosionUtility.getExposure(explosionPos, predict, optimized);
            terrainIgnore = false;
            double finalExposure = (1.0 - distExposure) * exposure;
            float toDamage = (float)Math.floor((finalExposure * finalExposure + finalExposure) / 2.0 * 7.0 * 12.0 + 1.0);
            if (Module.mc.world.getDifficulty() == Difficulty.EASY) {
                toDamage = Math.min(toDamage / 2.0f + 1.0f, toDamage);
            } else if (Module.mc.world.getDifficulty() == Difficulty.HARD) {
                toDamage = toDamage * 3.0f / 2.0f;
            }
            toDamage = DamageUtil.getDamageLeft((LivingEntity)target, (float)toDamage, (DamageSource)((IExplosion)explosion).getDamageSource(), (float)target.getArmor(), (float)((float)Objects.requireNonNull(target.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).getValue()));
            if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
                int resistance = 25 - (Objects.requireNonNull(target.getStatusEffect(StatusEffects.RESISTANCE)).getAmplifier() + 1) * 5;
                float resistance_1 = toDamage * (float)resistance;
                toDamage = Math.max(resistance_1 / 25.0f, 0.0f);
            }
            if (toDamage <= 0.0f) {
                toDamage = 0.0f;
            } else {
                float protAmount = 32.0f;
                if (protAmount > 0.0f) {
                    toDamage = DamageUtil.getInflictedDamage((float)toDamage, (float)protAmount);
                }
            }
            return toDamage;
        }
        return 0.0f;
    }

    public static BlockHitResult rayCastBlock(RaycastContext context, BlockPos block) {
        return (BlockHitResult)BlockView.raycast((Vec3d)context.getStart(), (Vec3d)context.getEnd(), (Object)context, (raycastContext, blockPos) -> {
            BlockState blockState = !blockPos.equals((Object)block) ? Blocks.AIR.getDefaultState() : Blocks.OBSIDIAN.getDefaultState();
            Vec3d vec3d = raycastContext.getStart();
            Vec3d vec3d2 = raycastContext.getEnd();
            VoxelShape voxelShape = raycastContext.getBlockShape(blockState, (BlockView)Module.mc.world, blockPos);
            BlockHitResult blockHitResult = Module.mc.world.raycastBlock(vec3d, vec3d2, blockPos, voxelShape, blockState);
            VoxelShape voxelShape2 = VoxelShapes.empty();
            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, blockPos);
            double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult2.getPos());
            return d <= e ? blockHitResult : blockHitResult2;
        }, raycastContext -> {
            Vec3d vec3d = raycastContext.getStart().subtract(raycastContext.getEnd());
            return BlockHitResult.createMissed((Vec3d)raycastContext.getEnd(), (Direction)Direction.getFacing((double)vec3d.x, (double)vec3d.y, (double)vec3d.z), (BlockPos)BlockPos.ofFloored((Position)raycastContext.getEnd()));
        });
    }

    public static float getDamageOfGhostBlock(Vec3d explosionPos, PlayerEntity target, BlockPos bp) {
        double distExposure;
        double maxDist;
        if (Module.mc.world.getDifficulty() == Difficulty.PEACEFUL) {
            return 0.0f;
        }
        if (explosion == null) {
            explosion = new Explosion((World)Module.mc.world, (Entity)Module.mc.player, 1.0, 33.0, 7.0, 6.0f, false, Explosion.DestructionType.DESTROY);
        }
        ((IExplosion)explosion).setX(explosionPos.x);
        ((IExplosion)explosion).setY(explosionPos.y);
        ((IExplosion)explosion).setZ(explosionPos.z);
        if (((IExplosion)explosion).getWorld() != Module.mc.world) {
            ((IExplosion)explosion).setWorld((World)Module.mc.world);
        }
        if (!new Box((double)MathHelper.floor((double)(explosionPos.x - (maxDist = 12.0) - 1.0)), (double)MathHelper.floor((double)(explosionPos.y - maxDist - 1.0)), (double)MathHelper.floor((double)(explosionPos.z - maxDist - 1.0)), (double)MathHelper.floor((double)(explosionPos.x + maxDist + 1.0)), (double)MathHelper.floor((double)(explosionPos.y + maxDist + 1.0)), (double)MathHelper.floor((double)(explosionPos.z + maxDist + 1.0))).intersects(target.getBoundingBox())) {
            return 0.0f;
        }
        if (!target.isImmuneToExplosion(explosion) && !target.isInvulnerable() && (distExposure = target.squaredDistanceTo(explosionPos) / 144.0) <= 1.0) {
            terrainIgnore = false;
            double exposure = ExplosionUtility.getExposureGhost(explosionPos, (Entity)target, bp);
            terrainIgnore = false;
            double finalExposure = (1.0 - distExposure) * exposure;
            float toDamage = (float)Math.floor((finalExposure * finalExposure + finalExposure) / 2.0 * 7.0 * maxDist + 1.0);
            if (Module.mc.world.getDifficulty() == Difficulty.EASY) {
                toDamage = Math.min(toDamage / 2.0f + 1.0f, toDamage);
            } else if (Module.mc.world.getDifficulty() == Difficulty.HARD) {
                toDamage = toDamage * 3.0f / 2.0f;
            }
            toDamage = DamageUtil.getDamageLeft((LivingEntity)target, (float)toDamage, (DamageSource)((IExplosion)explosion).getDamageSource(), (float)target.getArmor(), (float)((float)target.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue()));
            if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
                int resistance = 25 - (target.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
                float resistance_1 = toDamage * (float)resistance;
                toDamage = Math.max(resistance_1 / 25.0f, 0.0f);
            }
            if (toDamage <= 0.0f) {
                toDamage = 0.0f;
            } else {
                float protAmount = 32.0f;
                if (protAmount > 0.0f) {
                    toDamage = DamageUtil.getInflictedDamage((float)toDamage, (float)protAmount);
                }
            }
            return toDamage;
        }
        return 0.0f;
    }

    private static float getExposureGhost(Vec3d source, Entity entity, BlockPos pos) {
        Box box = entity.getBoundingBox();
        double d = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double e = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double f = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
        double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
        if (d < 0.0 || e < 0.0 || f < 0.0) {
            return 0.0f;
        }
        int i = 0;
        int j = 0;
        for (double k = 0.0; k <= 1.0; k += d) {
            for (double l = 0.0; l <= 1.0; l += e) {
                for (double m = 0.0; m <= 1.0; m += f) {
                    double p;
                    double o;
                    double n = MathHelper.lerp((double)k, (double)box.minX, (double)box.maxX);
                    Vec3d vec3d = new Vec3d(n + g, o = MathHelper.lerp((double)l, (double)box.minY, (double)box.maxY), (p = MathHelper.lerp((double)m, (double)box.minZ, (double)box.maxZ)) + h);
                    if (ExplosionUtility.raycastGhost(new RaycastContext(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity), pos).getType() == HitResult.Type.MISS) {
                        ++i;
                    }
                    ++j;
                }
            }
        }
        return (float)i / (float)j;
    }

    public static float getExposure(Vec3d source, Box box, boolean optimized) {
        if (!optimized) {
            return ExplosionUtility.getExposure(source, box);
        }
        int miss = 0;
        int hit = 0;
        for (int k = 0; k <= 1; ++k) {
            for (int l = 0; l <= 1; ++l) {
                for (int m = 0; m <= 1; ++m) {
                    double p;
                    double o;
                    double n = MathHelper.lerp((double)k, (double)box.minX, (double)box.maxX);
                    Vec3d vec3d = new Vec3d(n, o = MathHelper.lerp((double)l, (double)box.minY, (double)box.maxY), p = MathHelper.lerp((double)m, (double)box.minZ, (double)box.maxZ));
                    if (ExplosionUtility.raycast(vec3d, source, false) == HitResult.Type.MISS) {
                        ++miss;
                    }
                    ++hit;
                }
            }
        }
        return (float)miss / (float)hit;
    }

    public static float getExposure(Vec3d source, Box box) {
        double d = 0.4545454446934474;
        double e = 0.21739130885479366;
        double f = 0.4545454446934474;
        int i = 0;
        int j = 0;
        for (double k = 0.0; k <= 1.0; k += d) {
            for (double l = 0.0; l <= 1.0; l += e) {
                for (double m = 0.0; m <= 1.0; m += f) {
                    double p;
                    double o;
                    double n = MathHelper.lerp((double)k, (double)box.minX, (double)box.maxX);
                    Vec3d vec3d = new Vec3d(n + 0.045454555306552624, o = MathHelper.lerp((double)l, (double)box.minY, (double)box.maxY), (p = MathHelper.lerp((double)m, (double)box.minZ, (double)box.maxZ)) + 0.045454555306552624);
                    if (ExplosionUtility.raycast(vec3d, source, false) == HitResult.Type.MISS) {
                        ++i;
                    }
                    ++j;
                }
            }
        }
        return (float)i / (float)j;
    }

    private static BlockHitResult raycastGhost(RaycastContext context, BlockPos bPos) {
        return (BlockHitResult)BlockView.raycast((Vec3d)context.getStart(), (Vec3d)context.getEnd(), (Object)context, (innerContext, pos) -> {
            Vec3d vec3d = innerContext.getStart();
            Vec3d vec3d2 = innerContext.getEnd();
            BlockState blockState = !pos.equals((Object)bPos) ? Module.mc.world.getBlockState(bPos) : Blocks.OBSIDIAN.getDefaultState();
            VoxelShape voxelShape = innerContext.getBlockShape(blockState, (BlockView)Module.mc.world, pos);
            BlockHitResult blockHitResult = Module.mc.world.raycastBlock(vec3d, vec3d2, pos, voxelShape, blockState);
            BlockHitResult blockHitResult2 = VoxelShapes.empty().raycast(vec3d, vec3d2, pos);
            double d = blockHitResult == null ? Double.MAX_VALUE : innerContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : innerContext.getStart().squaredDistanceTo(blockHitResult2.getPos());
            return d <= e ? blockHitResult : blockHitResult2;
        }, innerContext -> {
            Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
            return BlockHitResult.createMissed((Vec3d)innerContext.getEnd(), (Direction)Direction.getFacing((double)vec3d.x, (double)vec3d.y, (double)vec3d.z), (BlockPos)BlockPos.ofFloored((Position)innerContext.getEnd()));
        });
    }

    public static HitResult.Type raycast(Vec3d start, Vec3d end, boolean ignoreTerrain) {
        return (HitResult.Type)BlockView.raycast((Vec3d)start, (Vec3d)end, null, (innerContext, blockPos) -> {
            BlockState blockState = Module.mc.world.getBlockState(blockPos);
            if (blockState.getBlock().getBlastResistance() < 600.0f && ignoreTerrain) {
                return null;
            }
            BlockHitResult hitResult = blockState.getCollisionShape((BlockView)Module.mc.world, blockPos).raycast(start, end, blockPos);
            return hitResult == null ? null : hitResult.getType();
        }, innerContext -> HitResult.Type.MISS);
    }

    public static int getProtectionAmount(Iterable<ItemStack> equipment) {
        MutableInt mutableInt = new MutableInt();
        equipment.forEach(i -> mutableInt.add(ExplosionUtility.getProtectionAmount(i)));
        return mutableInt.intValue();
    }

    public static int getProtectionAmount(ItemStack stack) {
        int modifierBlast = EnchantmentHelper.getLevel((RegistryEntry)((RegistryEntry)Module.mc.world.getRegistryManager().get(Enchantments.BLAST_PROTECTION.getRegistryRef()).getEntry(Enchantments.BLAST_PROTECTION).get()), (ItemStack)stack);
        int modifier = EnchantmentHelper.getLevel((RegistryEntry)((RegistryEntry)Module.mc.world.getRegistryManager().get(Enchantments.PROTECTION.getRegistryRef()).getEntry(Enchantments.PROTECTION).get()), (ItemStack)stack);
        return modifierBlast * 2 + modifier;
    }
}

