/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ExperienceOrbEntity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.hit.HitResult.Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode
 *  net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext.FluidHandling
 *  net.minecraft.world.RaycastContext.ShapeType
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.client.network.PendingUpdateManager
 *  net.minecraft.client.network.SequencedPacketCreator
 */
package thunder.hack.utility.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.IClientWorldMixin;
import thunder.hack.utility.player.InventoryUtility;
import thunder.hack.utility.player.SearchInvResult;
import thunder.hack.utility.world.ExplosionUtility;

public final class InteractionUtility {
    private static final List<Block> SHIFT_BLOCKS = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.BIRCH_TRAPDOOR, Blocks.BAMBOO_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.CHERRY_TRAPDOOR, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.ACACIA_TRAPDOOR, Blocks.ENCHANTING_TABLE, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    public static Map<BlockPos, Long> awaiting = new HashMap<BlockPos, Long>();

    public static boolean canSee(Vec3d vec) {
        return InteractionUtility.canSee(vec, vec);
    }

    public static boolean canSee(Entity entity) {
        Vec3d entityEyes = InteractionUtility.getEyesPos(entity);
        Vec3d entityPos = entity.getPos();
        return InteractionUtility.canSee(entityEyes, entityPos);
    }

    public static boolean canSee(Vec3d entityEyes, Vec3d entityPos) {
        if (Module.mc.player == null || Module.mc.world == null) {
            return false;
        }
        Vec3d playerEyes = InteractionUtility.getEyesPos((Entity)Module.mc.player);
        if (ExplosionUtility.raycast(playerEyes, entityEyes, false) == HitResult.Type.MISS) {
            return true;
        }
        if (playerEyes.getY() > entityPos.getY()) {
            return ExplosionUtility.raycast(playerEyes, entityEyes, false) == HitResult.Type.MISS;
        }
        return false;
    }

    public static Vec3d getEyesPos(@NotNull Entity entity) {
        return entity.getPos().add(0.0, (double)entity.getEyeHeight(entity.getPose()), 0.0);
    }

    public static float[] calculateAngle(Vec3d to) {
        return InteractionUtility.calculateAngle(InteractionUtility.getEyesPos((Entity)Module.mc.player), to);
    }

    public static float[] calculateAngle(@NotNull Vec3d from, @NotNull Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt((float)((float)(difX * difX + difZ * difZ)));
        float yD = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0));
        float pD = (float)MathHelper.clamp((double)MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(difY, dist))), (double)-90.0, (double)90.0);
        return new float[]{yD, pD};
    }

    public static boolean placeBlock(BlockPos bp, Rotate rotate, Interact interact, PlaceMode mode, int slot, boolean returnSlot, boolean ignoreEntities) {
        int prevItem = Module.mc.player.getInventory().selectedSlot;
        if (slot == -1) {
            return false;
        }
        InventoryUtility.switchTo(slot);
        boolean result = InteractionUtility.placeBlock(bp, rotate, interact, mode, ignoreEntities);
        if (returnSlot) {
            InventoryUtility.switchTo(prevItem);
        }
        return result;
    }

    public static boolean placeBlock(BlockPos bp, Rotate rotate, Interact interact, PlaceMode mode, @NotNull SearchInvResult invResult, boolean returnSlot, boolean ignoreEntities) {
        int prevItem = Module.mc.player.getInventory().selectedSlot;
        invResult.switchTo();
        boolean result = InteractionUtility.placeBlock(bp, rotate, interact, mode, ignoreEntities);
        if (returnSlot) {
            InventoryUtility.switchTo(prevItem);
        }
        return result;
    }

    public static boolean placeBlock(BlockPos bp, Rotate rotate, Interact interact, PlaceMode mode, boolean ignoreEntities) {
        boolean sneak;
        BlockHitResult result = InteractionUtility.getPlaceResult(bp, interact, ignoreEntities);
        if (result == null || Module.mc.world == null || Module.mc.interactionManager == null || Module.mc.player == null) {
            return false;
        }
        boolean sprint = Module.mc.player.isSprinting();
        boolean bl = sneak = InteractionUtility.needSneak(Module.mc.world.getBlockState(result.getBlockPos()).getBlock()) && !Module.mc.player.isSneaking();
        if (sprint) {
            Module.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)Module.mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }
        if (sneak) {
            Module.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)Module.mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
        float[] angle = InteractionUtility.calculateAngle(result.getPos());
        switch (rotate.ordinal()) {
            case 0: {
                break;
            }
            case 1: {
                Module.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.LookAndOnGround(angle[0], angle[1], Module.mc.player.isOnGround()));
                break;
            }
            case 2: {
                Module.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.Full(Module.mc.player.getX(), Module.mc.player.getY(), Module.mc.player.getZ(), angle[0], angle[1], Module.mc.player.isOnGround()));
            }
        }
        if (mode == PlaceMode.Normal) {
            Module.mc.interactionManager.interactBlock(Module.mc.player, Hand.MAIN_HAND, result);
        }
        if (mode == PlaceMode.Packet) {
            InteractionUtility.sendSequencedPacket(id -> new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, id));
        }
        awaiting.put(bp, System.currentTimeMillis());
        if (rotate == Rotate.Grim) {
            Module.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.Full(Module.mc.player.getX(), Module.mc.player.getY(), Module.mc.player.getZ(), Module.mc.player.getYaw(), Module.mc.player.getPitch(), Module.mc.player.isOnGround()));
        }
        if (sneak) {
            Module.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)Module.mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
        if (sprint) {
            Module.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)Module.mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        }
        Module.mc.player.networkHandler.sendPacket((Packet)new HandSwingC2SPacket(Hand.MAIN_HAND));
        return true;
    }

    public static boolean canPlaceBlock(@NotNull BlockPos bp, Interact interact, boolean ignoreEntities) {
        if (awaiting.containsKey(bp)) {
            return false;
        }
        return InteractionUtility.getPlaceResult(bp, interact, ignoreEntities) != null;
    }

    public static float[] getPlaceAngle(@NotNull BlockPos bp, Interact interact, boolean ignoreEntities) {
        BlockHitResult result = InteractionUtility.getPlaceResult(bp, interact, ignoreEntities);
        if (result != null) {
            return InteractionUtility.calculateAngle(result.getPos());
        }
        return null;
    }

    public static void sendSequencedPacket(SequencedPacketCreator packetCreator) {
        if (Module.mc.getNetworkHandler() == null || Module.mc.world == null) {
            return;
        }
        try (PendingUpdateManager pendingUpdateManager = ((IClientWorldMixin)Module.mc.world).getPendingUpdateManager().incrementSequence();){
            int i = pendingUpdateManager.getSequence();
            Module.mc.getNetworkHandler().sendPacket(packetCreator.predict(i));
        }
    }

    @Nullable
    public static BlockHitResult getPlaceResult(@NotNull BlockPos bp, Interact interact, boolean ignoreEntities) {
        if (!ignoreEntities) {
            for (Entity entity : Module.mc.world.getNonSpectatingEntities(Entity.class, new Box(bp))) {
                if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity) continue;
                return null;
            }
        }
        if (!Module.mc.world.getBlockState(bp).isReplaceable()) {
            return null;
        }
        if (interact == Interact.AirPlace) {
            return ExplosionUtility.rayCastBlock(new RaycastContext(InteractionUtility.getEyesPos((Entity)Module.mc.player), bp.toCenterPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)Module.mc.player), bp);
        }
        ArrayList<BlockPosWithFacing> supports = InteractionUtility.getSupportBlocks(bp);
        for (BlockPosWithFacing support : supports) {
            if (interact != Interact.Vanilla) {
                @NotNull List<Direction> dirs = InteractionUtility.getStrictDirections(bp);
                if (dirs.isEmpty()) {
                    return null;
                }
                if (!dirs.contains(support.facing)) continue;
            }
            BlockHitResult result = null;
            if (interact == Interact.Legit) {
                Vec3d p = InteractionUtility.getVisibleDirectionPoint(support.facing, support.position, 0.0f, 6.0f);
                if (p != null) {
                    return new BlockHitResult(p, support.facing, support.position, false);
                }
            } else {
                Vec3d directionVec = new Vec3d((double)support.position.getX() + 0.5 + (double)support.facing.getVector().getX() * 0.5, (double)support.position.getY() + 0.5 + (double)support.facing.getVector().getY() * 0.5, (double)support.position.getZ() + 0.5 + (double)support.facing.getVector().getZ() * 0.5);
                result = new BlockHitResult(directionVec, support.facing, support.position, false);
            }
            return result;
        }
        return null;
    }

    @NotNull
    public static ArrayList<BlockPosWithFacing> getSupportBlocks(@NotNull BlockPos bp) {
        ArrayList<BlockPosWithFacing> list = new ArrayList<BlockPosWithFacing>();
        if (Module.mc.world.getBlockState(bp.add(0, -1, 0)).isSolid() || awaiting.containsKey(bp.add(0, -1, 0))) {
            list.add(new BlockPosWithFacing(bp.add(0, -1, 0), Direction.UP));
        }
        if (Module.mc.world.getBlockState(bp.add(0, 1, 0)).isSolid() || awaiting.containsKey(bp.add(0, 1, 0))) {
            list.add(new BlockPosWithFacing(bp.add(0, 1, 0), Direction.DOWN));
        }
        if (Module.mc.world.getBlockState(bp.add(-1, 0, 0)).isSolid() || awaiting.containsKey(bp.add(-1, 0, 0))) {
            list.add(new BlockPosWithFacing(bp.add(-1, 0, 0), Direction.EAST));
        }
        if (Module.mc.world.getBlockState(bp.add(1, 0, 0)).isSolid() || awaiting.containsKey(bp.add(1, 0, 0))) {
            list.add(new BlockPosWithFacing(bp.add(1, 0, 0), Direction.WEST));
        }
        if (Module.mc.world.getBlockState(bp.add(0, 0, 1)).isSolid() || awaiting.containsKey(bp.add(0, 0, 1))) {
            list.add(new BlockPosWithFacing(bp.add(0, 0, 1), Direction.NORTH));
        }
        if (Module.mc.world.getBlockState(bp.add(0, 0, -1)).isSolid() || awaiting.containsKey(bp.add(0, 0, -1))) {
            list.add(new BlockPosWithFacing(bp.add(0, 0, -1), Direction.SOUTH));
        }
        return list;
    }

    @Nullable
    public static BlockPosWithFacing checkNearBlocks(@NotNull BlockPos blockPos) {
        if (Module.mc.world.getBlockState(blockPos.add(0, -1, 0)).isSolid()) {
            return new BlockPosWithFacing(blockPos.add(0, -1, 0), Direction.UP);
        }
        if (Module.mc.world.getBlockState(blockPos.add(-1, 0, 0)).isSolid()) {
            return new BlockPosWithFacing(blockPos.add(-1, 0, 0), Direction.EAST);
        }
        if (Module.mc.world.getBlockState(blockPos.add(1, 0, 0)).isSolid()) {
            return new BlockPosWithFacing(blockPos.add(1, 0, 0), Direction.WEST);
        }
        if (Module.mc.world.getBlockState(blockPos.add(0, 0, 1)).isSolid()) {
            return new BlockPosWithFacing(blockPos.add(0, 0, 1), Direction.NORTH);
        }
        if (Module.mc.world.getBlockState(blockPos.add(0, 0, -1)).isSolid()) {
            return new BlockPosWithFacing(blockPos.add(0, 0, -1), Direction.SOUTH);
        }
        return null;
    }

    public static float squaredDistanceFromEyes(@NotNull Vec3d vec) {
        double d0 = vec.x - Module.mc.player.getX();
        double d1 = vec.z - Module.mc.player.getZ();
        double d2 = vec.y - (Module.mc.player.getY() + (double)Module.mc.player.getEyeHeight(Module.mc.player.getPose()));
        return (float)(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static float squaredDistanceFromEyes2d(@NotNull Vec3d vec) {
        double d0 = vec.x - Module.mc.player.getX();
        double d1 = vec.z - Module.mc.player.getZ();
        return (float)(d0 * d0 + d1 * d1);
    }

    @NotNull
    public static List<Direction> getStrictDirections(@NotNull BlockPos bp) {
        ArrayList<Direction> visibleSides = new ArrayList<Direction>();
        Vec3d positionVector = bp.toCenterPos();
        double westDelta = InteractionUtility.getEyesPos((Entity)Module.mc.player).x - positionVector.add((double)0.5, (double)0.0, (double)0.0).x;
        double eastDelta = InteractionUtility.getEyesPos((Entity)Module.mc.player).x - positionVector.add((double)-0.5, (double)0.0, (double)0.0).x;
        double northDelta = InteractionUtility.getEyesPos((Entity)Module.mc.player).z - positionVector.add((double)0.0, (double)0.0, (double)0.5).z;
        double southDelta = InteractionUtility.getEyesPos((Entity)Module.mc.player).z - positionVector.add((double)0.0, (double)0.0, (double)-0.5).z;
        double upDelta = InteractionUtility.getEyesPos((Entity)Module.mc.player).y - positionVector.add((double)0.0, (double)0.5, (double)0.0).y;
        double downDelta = InteractionUtility.getEyesPos((Entity)Module.mc.player).y - positionVector.add((double)0.0, (double)-0.5, (double)0.0).y;
        if (westDelta > 0.0 && InteractionUtility.isSolid(bp.west())) {
            visibleSides.add(Direction.EAST);
        }
        if (westDelta < 0.0 && InteractionUtility.isSolid(bp.east())) {
            visibleSides.add(Direction.WEST);
        }
        if (eastDelta < 0.0 && InteractionUtility.isSolid(bp.east())) {
            visibleSides.add(Direction.WEST);
        }
        if (eastDelta > 0.0 && InteractionUtility.isSolid(bp.west())) {
            visibleSides.add(Direction.EAST);
        }
        if (northDelta > 0.0 && InteractionUtility.isSolid(bp.north())) {
            visibleSides.add(Direction.SOUTH);
        }
        if (northDelta < 0.0 && InteractionUtility.isSolid(bp.south())) {
            visibleSides.add(Direction.NORTH);
        }
        if (southDelta < 0.0 && InteractionUtility.isSolid(bp.south())) {
            visibleSides.add(Direction.NORTH);
        }
        if (southDelta > 0.0 && InteractionUtility.isSolid(bp.north())) {
            visibleSides.add(Direction.SOUTH);
        }
        if (upDelta > 0.0 && InteractionUtility.isSolid(bp.down())) {
            visibleSides.add(Direction.UP);
        }
        if (upDelta < 0.0 && InteractionUtility.isSolid(bp.up())) {
            visibleSides.add(Direction.DOWN);
        }
        if (downDelta < 0.0 && InteractionUtility.isSolid(bp.up())) {
            visibleSides.add(Direction.DOWN);
        }
        if (downDelta > 0.0 && InteractionUtility.isSolid(bp.down())) {
            visibleSides.add(Direction.UP);
        }
        return visibleSides;
    }

    public static boolean isSolid(BlockPos bp) {
        return Module.mc.world.getBlockState(bp).isSolid() || awaiting.containsKey(bp);
    }

    @NotNull
    public static List<Direction> getStrictBlockDirections(@NotNull BlockPos bp) {
        ArrayList<Direction> visibleSides = new ArrayList<Direction>();
        Vec3d pV = bp.toCenterPos();
        double westDelta = InteractionUtility.getEyesPos((Entity)Module.mc.player).x - pV.add((double)0.5, (double)0.0, (double)0.0).x;
        double eastDelta = InteractionUtility.getEyesPos((Entity)Module.mc.player).x - pV.add((double)-0.5, (double)0.0, (double)0.0).x;
        double northDelta = InteractionUtility.getEyesPos((Entity)Module.mc.player).z - pV.add((double)0.0, (double)0.0, (double)0.5).z;
        double southDelta = InteractionUtility.getEyesPos((Entity)Module.mc.player).z - pV.add((double)0.0, (double)0.0, (double)-0.5).z;
        double upDelta = InteractionUtility.getEyesPos((Entity)Module.mc.player).y - pV.add((double)0.0, (double)0.5, (double)0.0).y;
        double downDelta = InteractionUtility.getEyesPos((Entity)Module.mc.player).y - pV.add((double)0.0, (double)-0.5, (double)0.0).y;
        if (westDelta > 0.0 && Module.mc.world.getBlockState(bp.east()).isReplaceable()) {
            visibleSides.add(Direction.EAST);
        }
        if (eastDelta < 0.0 && Module.mc.world.getBlockState(bp.west()).isReplaceable()) {
            visibleSides.add(Direction.WEST);
        }
        if (northDelta > 0.0 && Module.mc.world.getBlockState(bp.south()).isReplaceable()) {
            visibleSides.add(Direction.SOUTH);
        }
        if (southDelta < 0.0 && Module.mc.world.getBlockState(bp.north()).isReplaceable()) {
            visibleSides.add(Direction.NORTH);
        }
        if (upDelta > 0.0 && Module.mc.world.getBlockState(bp.up()).isReplaceable()) {
            visibleSides.add(Direction.UP);
        }
        if (downDelta < 0.0 && Module.mc.world.getBlockState(bp.down()).isReplaceable()) {
            visibleSides.add(Direction.DOWN);
        }
        return visibleSides;
    }

    @Nullable
    public static BreakData getBreakData(BlockPos bp, Interact interact) {
        if (interact == Interact.Vanilla) {
            return new BreakData(Direction.UP, bp.toCenterPos().add(0.0, 0.5, 0.0));
        }
        if (interact == Interact.Strict) {
            float bestDistance = 999.0f;
            Direction bestDirection = Direction.UP;
            Vec3d bestVector = null;
            for (Direction dir : Direction.values()) {
                Vec3d directionVec = new Vec3d((double)bp.getX() + 0.5 + (double)dir.getVector().getX() * 0.5, (double)bp.getY() + 0.5 + (double)dir.getVector().getY() * 0.5, (double)bp.getZ() + 0.5 + (double)dir.getVector().getZ() * 0.5);
                float distance = InteractionUtility.squaredDistanceFromEyes(directionVec);
                if (!(bestDistance > distance)) continue;
                bestDirection = dir;
                bestVector = directionVec;
                bestDistance = distance;
            }
            if (bestVector == null) {
                return null;
            }
            return new BreakData(bestDirection, bestVector);
        }
        if (interact == Interact.Legit) {
            float bestDistance = 999.0f;
            BreakData bestData = null;
            for (float x = 0.0f; x <= 1.0f; x += 0.2f) {
                for (float y = 0.0f; y <= 1.0f; y += 0.2f) {
                    for (float z = 0.0f; z <= 1.0f; z += 0.2f) {
                        Vec3d point = new Vec3d((double)((float)bp.getX() + x), (double)((float)bp.getY() + y), (double)((float)bp.getZ() + z));
                        BlockHitResult wallCheck = Module.mc.world.raycast(new RaycastContext(InteractionUtility.getEyesPos((Entity)Module.mc.player), point, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)Module.mc.player));
                        if (wallCheck != null && wallCheck.getType() == HitResult.Type.BLOCK && !wallCheck.getBlockPos().equals((Object)bp)) continue;
                        BlockHitResult result = ExplosionUtility.rayCastBlock(new RaycastContext(InteractionUtility.getEyesPos((Entity)Module.mc.player), point, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)Module.mc.player), bp);
                        if (!(InteractionUtility.squaredDistanceFromEyes(point) < bestDistance) || result == null || result.getType() != HitResult.Type.BLOCK) continue;
                        bestData = new BreakData(result.getSide(), result.getPos());
                    }
                }
            }
            if (bestData == null) {
                return null;
            }
            if (bestData.vector == null || bestData.dir == null) {
                return null;
            }
            return bestData;
        }
        return null;
    }

    @Nullable
    public static Vec3d getVisibleDirectionPoint(@NotNull Direction dir, @NotNull BlockPos bp, float wallRange, float range) {
        double x;
        Vec3d point;
        double z;
        Box brutBox = InteractionUtility.getDirectionBox(dir);
        if (brutBox.maxX - brutBox.minX == 0.0) {
            for (double y = brutBox.minY; y < brutBox.maxY; y += (double)0.1f) {
                for (z = brutBox.minZ; z < brutBox.maxZ; z += (double)0.1f) {
                    point = new Vec3d((double)bp.getX() + brutBox.minX, (double)bp.getY() + y, (double)bp.getZ() + z);
                    if (InteractionUtility.shouldSkipPoint(point, bp, dir, wallRange, range)) continue;
                    return point;
                }
            }
        }
        if (brutBox.maxY - brutBox.minY == 0.0) {
            for (x = brutBox.minX; x < brutBox.maxX; x += (double)0.1f) {
                for (z = brutBox.minZ; z < brutBox.maxZ; z += (double)0.1f) {
                    point = new Vec3d((double)bp.getX() + x, (double)bp.getY() + brutBox.minY, (double)bp.getZ() + z);
                    if (InteractionUtility.shouldSkipPoint(point, bp, dir, wallRange, range)) continue;
                    return point;
                }
            }
        }
        if (brutBox.maxZ - brutBox.minZ == 0.0) {
            for (x = brutBox.minX; x < brutBox.maxX; x += (double)0.1f) {
                for (double y = brutBox.minY; y < brutBox.maxY; y += (double)0.1f) {
                    point = new Vec3d((double)bp.getX() + x, (double)bp.getY() + y, (double)bp.getZ() + brutBox.minZ);
                    if (InteractionUtility.shouldSkipPoint(point, bp, dir, wallRange, range)) continue;
                    return point;
                }
            }
        }
        return null;
    }

    @NotNull
    private static Box getDirectionBox(Direction dir) {
        return switch (dir) {
            default -> throw new MatchException(null, null);
            case Direction.UP -> new Box((double)0.15f, 1.0, (double)0.15f, (double)0.85f, 1.0, (double)0.85f);
            case Direction.DOWN -> new Box((double)0.15f, 0.0, (double)0.15f, (double)0.85f, 0.0, (double)0.85f);
            case Direction.EAST -> new Box(1.0, (double)0.15f, (double)0.15f, 1.0, (double)0.85f, (double)0.85f);
            case Direction.WEST -> new Box(0.0, (double)0.15f, (double)0.15f, 0.0, (double)0.85f, (double)0.85f);
            case Direction.NORTH -> new Box((double)0.15f, (double)0.15f, 0.0, (double)0.85f, (double)0.85f, 0.0);
            case Direction.SOUTH -> new Box((double)0.15f, (double)0.15f, 1.0, (double)0.85f, (double)0.85f, 1.0);
        };
    }

    private static boolean shouldSkipPoint(Vec3d point, BlockPos bp, Direction dir, float wallRange, float range) {
        RaycastContext context = new RaycastContext(InteractionUtility.getEyesPos((Entity)Module.mc.player), point, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)Module.mc.player);
        BlockHitResult result = Module.mc.world.raycast(context);
        float dst = InteractionUtility.squaredDistanceFromEyes(point);
        if (result != null && result.getType() == HitResult.Type.BLOCK && !result.getBlockPos().equals((Object)bp) && dst > wallRange * wallRange) {
            return true;
        }
        return dst > range * range;
    }

    public static boolean needSneak(Block in) {
        return SHIFT_BLOCKS.contains(in);
    }

    public static void lookAt(BlockPos bp) {
        if (bp != null) {
            float[] angle = InteractionUtility.calculateAngle(bp.toCenterPos());
            Module.mc.player.setYaw(angle[0]);
            Module.mc.player.setPitch(angle[1]);
        }
    }

    public static boolean isVecInFOV(Vec3d pos, Integer fov) {
        double deltaX = pos.getX() - Module.mc.player.getX();
        double deltaZ = pos.getZ() - Module.mc.player.getZ();
        float yawDelta = MathHelper.wrapDegrees((float)((float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0)) - MathHelper.wrapDegrees((float)Module.mc.player.getYaw())));
        return Math.abs(yawDelta) <= (float)fov.intValue();
    }

    public static enum Rotate {
        None,
        Default,
        Grim;

    }

    public static enum Interact {
        Vanilla,
        Strict,
        Legit,
        AirPlace;

    }

    public static enum PlaceMode {
        Packet,
        Normal;

    }

    public record BlockPosWithFacing(BlockPos position, Direction facing) {
    }

    public record BreakData(Direction dir, Vec3d vector) {
    }
}

