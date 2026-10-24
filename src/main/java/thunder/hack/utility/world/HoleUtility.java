/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.Vec3d
 */
package thunder.hack.utility.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thunder.hack.features.modules.Module;

public final class HoleUtility {
    public static final Vec3i[] VECTOR_PATTERN = new Vec3i[]{new Vec3i(0, 0, 1), new Vec3i(0, 0, -1), new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0)};

    @NotNull
    public static List<BlockPos> getHolePoses(@NotNull Vec3d from) {
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        double decimalX = from.getX() - Math.floor(from.getX());
        double decimalZ = from.getZ() - Math.floor(from.getZ());
        int offX = HoleUtility.calcOffset(decimalX);
        int offZ = HoleUtility.calcOffset(decimalZ);
        positions.add(HoleUtility.getPos(from));
        for (int x = 0; x <= Math.abs(offX); ++x) {
            for (int z = 0; z <= Math.abs(offZ); ++z) {
                int properX = x * offX;
                int properZ = z * offZ;
                positions.add(Objects.requireNonNull(HoleUtility.getPos(from)).add(properX, 0, properZ));
            }
        }
        return positions;
    }

    @NotNull
    public static List<BlockPos> getSurroundPoses(@NotNull Vec3d from) {
        int z;
        int x;
        BlockPos fromPos = BlockPos.ofFloored((Position)from);
        ArrayList<BlockPos> tempOffsets = new ArrayList<BlockPos>();
        double decimalX = Math.abs(from.getX()) - Math.floor(Math.abs(from.getX()));
        double decimalZ = Math.abs(from.getZ()) - Math.floor(Math.abs(from.getZ()));
        int lengthXPos = HoleUtility.calcLength(decimalX, false);
        int lengthXNeg = HoleUtility.calcLength(decimalX, true);
        int lengthZPos = HoleUtility.calcLength(decimalZ, false);
        int lengthZNeg = HoleUtility.calcLength(decimalZ, true);
        for (x = 1; x < lengthXPos + 1; ++x) {
            tempOffsets.add(HoleUtility.addToPlayer(fromPos, x, 0.0, 1 + lengthZPos));
            tempOffsets.add(HoleUtility.addToPlayer(fromPos, x, 0.0, -(1 + lengthZNeg)));
        }
        for (x = 0; x <= lengthXNeg; ++x) {
            tempOffsets.add(HoleUtility.addToPlayer(fromPos, -x, 0.0, 1 + lengthZPos));
            tempOffsets.add(HoleUtility.addToPlayer(fromPos, -x, 0.0, -(1 + lengthZNeg)));
        }
        for (z = 1; z < lengthZPos + 1; ++z) {
            tempOffsets.add(HoleUtility.addToPlayer(fromPos, 1 + lengthXPos, 0.0, z));
            tempOffsets.add(HoleUtility.addToPlayer(fromPos, -(1 + lengthXNeg), 0.0, z));
        }
        for (z = 0; z <= lengthZNeg; ++z) {
            tempOffsets.add(HoleUtility.addToPlayer(fromPos, 1 + lengthXPos, 0.0, -z));
            tempOffsets.add(HoleUtility.addToPlayer(fromPos, -(1 + lengthXNeg), 0.0, -z));
        }
        return tempOffsets;
    }

    @NotNull
    private static BlockPos getPos(@NotNull Vec3d from) {
        return BlockPos.ofFloored((double)from.getX(), (double)(from.getY() - Math.floor(from.getY()) > 0.8 ? Math.floor(from.getY()) + 1.0 : Math.floor(from.getY())), (double)from.getZ());
    }

    public static int calcOffset(double dec) {
        return dec >= 0.7 ? 1 : (dec <= 0.3 ? -1 : 0);
    }

    public static int calcLength(double decimal, boolean negative) {
        if (negative) {
            return decimal <= 0.3 ? 1 : 0;
        }
        return decimal >= 0.7 ? 1 : 0;
    }

    public static BlockPos addToPlayer(@NotNull BlockPos playerPos, double x, double y, double z) {
        if (playerPos.getX() < 0) {
            x = -x;
        }
        if (playerPos.getY() < 0) {
            y = -y;
        }
        if (playerPos.getZ() < 0) {
            z = -z;
        }
        return playerPos.add((Vec3i)BlockPos.ofFloored((double)x, (double)y, (double)z));
    }

    public static boolean isHole(BlockPos pos) {
        return HoleUtility.isSingleHole(pos) || HoleUtility.validTwoBlockIndestructible(pos) || HoleUtility.validTwoBlockBedrock(pos) || HoleUtility.validQuadIndestructible(pos) || HoleUtility.validQuadBedrock(pos);
    }

    public static boolean isSingleHole(BlockPos pos) {
        return HoleUtility.validIndestructible(pos) || HoleUtility.validBedrock(pos);
    }

    public static boolean validIndestructible(@NotNull BlockPos pos) {
        return !(HoleUtility.validBedrock(pos) || !HoleUtility.isIndestructible(pos.add(0, -1, 0)) && !HoleUtility.isBedrock(pos.add(0, -1, 0)) || !HoleUtility.isIndestructible(pos.add(1, 0, 0)) && !HoleUtility.isBedrock(pos.add(1, 0, 0)) || !HoleUtility.isIndestructible(pos.add(-1, 0, 0)) && !HoleUtility.isBedrock(pos.add(-1, 0, 0)) || !HoleUtility.isIndestructible(pos.add(0, 0, 1)) && !HoleUtility.isBedrock(pos.add(0, 0, 1)) || !HoleUtility.isIndestructible(pos.add(0, 0, -1)) && !HoleUtility.isBedrock(pos.add(0, 0, -1)) || !HoleUtility.isReplaceable(pos) || !HoleUtility.isReplaceable(pos.add(0, 1, 0)) || !HoleUtility.isReplaceable(pos.add(0, 2, 0)));
    }

    public static boolean validBedrock(@NotNull BlockPos pos) {
        return HoleUtility.isBedrock(pos.add(0, -1, 0)) && HoleUtility.isBedrock(pos.add(1, 0, 0)) && HoleUtility.isBedrock(pos.add(-1, 0, 0)) && HoleUtility.isBedrock(pos.add(0, 0, 1)) && HoleUtility.isBedrock(pos.add(0, 0, -1)) && HoleUtility.isReplaceable(pos) && HoleUtility.isReplaceable(pos.add(0, 1, 0)) && HoleUtility.isReplaceable(pos.add(0, 2, 0));
    }

    public static boolean validTwoBlockBedrock(@NotNull BlockPos pos) {
        BlockPos[] checkPoses;
        if (!HoleUtility.isReplaceable(pos)) {
            return false;
        }
        Vec3i addVec = HoleUtility.getTwoBlocksDirection(pos);
        if (addVec == null) {
            return false;
        }
        for (BlockPos checkPos : checkPoses = new BlockPos[]{pos, pos.add(addVec)}) {
            if (!HoleUtility.isReplaceable(checkPos.add(0, 1, 0)) || !HoleUtility.isReplaceable(checkPos.add(0, 2, 0))) {
                return false;
            }
            BlockPos downPos = checkPos.down();
            if (!HoleUtility.isBedrock(downPos)) {
                return false;
            }
            for (Vec3i vec : VECTOR_PATTERN) {
                BlockPos reducedPos = checkPos.add(vec);
                if (HoleUtility.isBedrock(reducedPos) || reducedPos.equals((Object)pos) || reducedPos.equals((Object)pos.add(addVec))) continue;
                return false;
            }
        }
        return true;
    }

    public static boolean validTwoBlockIndestructible(@NotNull BlockPos pos) {
        if (!HoleUtility.isReplaceable(pos)) {
            return false;
        }
        Vec3i addVec = HoleUtility.getTwoBlocksDirection(pos);
        if (addVec == null) {
            return false;
        }
        BlockPos[] checkPoses = new BlockPos[]{pos, pos.add(addVec)};
        boolean wasIndestrictible = false;
        for (BlockPos checkPos : checkPoses) {
            BlockPos downPos = checkPos.down();
            if (HoleUtility.isIndestructible(downPos)) {
                wasIndestrictible = true;
            } else if (!HoleUtility.isBedrock(downPos)) {
                return false;
            }
            if (!HoleUtility.isReplaceable(checkPos.add(0, 1, 0)) || !HoleUtility.isReplaceable(checkPos.add(0, 2, 0))) {
                return false;
            }
            for (Vec3i vec : VECTOR_PATTERN) {
                BlockPos reducedPos = checkPos.add(vec);
                if (HoleUtility.isIndestructible(reducedPos)) {
                    wasIndestrictible = true;
                    continue;
                }
                if (HoleUtility.isBedrock(reducedPos) || reducedPos.equals((Object)pos) || reducedPos.equals((Object)pos.add(addVec))) continue;
                return false;
            }
        }
        return wasIndestrictible;
    }

    @Nullable
    private static Vec3i getTwoBlocksDirection(BlockPos pos) {
        for (Vec3i vec : VECTOR_PATTERN) {
            if (!HoleUtility.isReplaceable(pos.add(vec))) continue;
            return vec;
        }
        return null;
    }

    public static boolean validQuadIndestructible(@NotNull BlockPos pos) {
        List<BlockPos> checkPoses = HoleUtility.getQuadDirection(pos);
        if (checkPoses == null) {
            return false;
        }
        boolean wasIndestrictible = false;
        for (BlockPos checkPos : checkPoses) {
            BlockPos downPos = checkPos.down();
            if (HoleUtility.isIndestructible(downPos)) {
                wasIndestrictible = true;
            } else if (!HoleUtility.isBedrock(downPos)) {
                return false;
            }
            if (!HoleUtility.isReplaceable(checkPos.add(0, 1, 0)) || !HoleUtility.isReplaceable(checkPos.add(0, 2, 0))) {
                return false;
            }
            for (Vec3i vec : VECTOR_PATTERN) {
                BlockPos reducedPos = checkPos.add(vec);
                if (HoleUtility.isIndestructible(reducedPos)) {
                    wasIndestrictible = true;
                    continue;
                }
                if (HoleUtility.isBedrock(reducedPos) || checkPoses.contains(reducedPos)) continue;
                return false;
            }
        }
        return wasIndestrictible;
    }

    public static boolean validQuadBedrock(@NotNull BlockPos pos) {
        List<BlockPos> checkPoses = HoleUtility.getQuadDirection(pos);
        if (checkPoses == null) {
            return false;
        }
        for (BlockPos checkPos : checkPoses) {
            BlockPos downPos = checkPos.down();
            if (!HoleUtility.isBedrock(downPos)) {
                return false;
            }
            if (!HoleUtility.isReplaceable(checkPos.add(0, 1, 0)) || !HoleUtility.isReplaceable(checkPos.add(0, 2, 0))) {
                return false;
            }
            for (Vec3i vec : VECTOR_PATTERN) {
                BlockPos reducedPos = checkPos.add(vec);
                if (HoleUtility.isBedrock(reducedPos) || checkPoses.contains(reducedPos)) continue;
                return false;
            }
        }
        return true;
    }

    @Nullable
    private static List<BlockPos> getQuadDirection(@NotNull BlockPos pos) {
        ArrayList<BlockPos> dirList = new ArrayList<BlockPos>();
        dirList.add(pos);
        if (!HoleUtility.isReplaceable(pos)) {
            return null;
        }
        if (HoleUtility.isReplaceable(pos.add(1, 0, 0)) && HoleUtility.isReplaceable(pos.add(0, 0, 1)) && HoleUtility.isReplaceable(pos.add(1, 0, 1))) {
            dirList.add(pos.add(1, 0, 0));
            dirList.add(pos.add(0, 0, 1));
            dirList.add(pos.add(1, 0, 1));
        }
        if (HoleUtility.isReplaceable(pos.add(-1, 0, 0)) && HoleUtility.isReplaceable(pos.add(0, 0, -1)) && HoleUtility.isReplaceable(pos.add(-1, 0, -1))) {
            dirList.add(pos.add(-1, 0, 0));
            dirList.add(pos.add(0, 0, -1));
            dirList.add(pos.add(-1, 0, -1));
        }
        if (HoleUtility.isReplaceable(pos.add(1, 0, 0)) && HoleUtility.isReplaceable(pos.add(0, 0, -1)) && HoleUtility.isReplaceable(pos.add(1, 0, -1))) {
            dirList.add(pos.add(1, 0, 0));
            dirList.add(pos.add(0, 0, -1));
            dirList.add(pos.add(1, 0, -1));
        }
        if (HoleUtility.isReplaceable(pos.add(-1, 0, 0)) && HoleUtility.isReplaceable(pos.add(0, 0, 1)) && HoleUtility.isReplaceable(pos.add(-1, 0, 1))) {
            dirList.add(pos.add(-1, 0, 0));
            dirList.add(pos.add(0, 0, 1));
            dirList.add(pos.add(-1, 0, 1));
        }
        if (dirList.size() != 4) {
            return null;
        }
        return dirList;
    }

    private static boolean isIndestructible(BlockPos bp) {
        if (Module.mc.world == null) {
            return false;
        }
        Block block = Module.mc.world.getBlockState(bp).getBlock();
        return block == Blocks.OBSIDIAN || block == Blocks.NETHERITE_BLOCK || block == Blocks.CRYING_OBSIDIAN || block == Blocks.RESPAWN_ANCHOR;
    }

    private static boolean isBedrock(BlockPos bp) {
        if (Module.mc.world == null) {
            return false;
        }
        return Module.mc.world.getBlockState(bp).getBlock() == Blocks.BEDROCK;
    }

    private static boolean isReplaceable(BlockPos bp) {
        if (Module.mc.world == null) {
            return false;
        }
        return Module.mc.world.getBlockState(bp).isReplaceable();
    }
}

