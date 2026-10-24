/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.hit.BlockHitResult
 */
package thunder.hack.features.modules.base;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thunder.hack.events.impl.EventPostSync;
import thunder.hack.events.impl.EventSync;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.base.PlaceModule;
import thunder.hack.setting.Setting;
import thunder.hack.utility.player.InteractionUtility;
import thunder.hack.utility.world.HoleUtility;

public abstract class TrapModule
extends PlaceModule {
    protected final Setting<PlaceTiming> placeTiming = new Setting<PlaceTiming>("Place Timing", PlaceTiming.Default);
    protected final Setting<Integer> blocksPerTick = new Setting<Integer>("Block/Tick", 8, 1, 12, v -> this.placeTiming.getValue() == PlaceTiming.Default);
    protected final Setting<Integer> placeDelay = new Setting<Integer>("Delay/Place", 3, 0, 10);
    protected final Setting<TrapMode> trapMode = new Setting<TrapMode>("Trap Mode", TrapMode.Full);
    private int delay;
    protected PlayerEntity target;
    private final ArrayList<BlockPos> sequentialBlocks = new ArrayList();

    public TrapModule(@NotNull String name, @NotNull Module.Category category) {
        super(name, category);
    }

    protected abstract boolean needNewTarget();

    @Nullable
    protected abstract PlayerEntity getTarget();

    @Override
    public void onDisable() {
        this.target = null;
        this.delay = 0;
        this.sequentialBlocks.clear();
    }

    @EventHandler
    private void onSync(EventSync event) {
        BlockHitResult result;
        BlockPos targetBlock;
        if (this.needNewTarget()) {
            this.target = this.getTarget();
            return;
        }
        if (this.placeTiming.getValue() == PlaceTiming.Vanilla && !this.rotate.is(InteractionUtility.Rotate.None) && (targetBlock = this.getBlockToPlace()) != null && TrapModule.mc.player != null && (result = InteractionUtility.getPlaceResult(targetBlock, (InteractionUtility.Interact)((Object)this.interact.getValue()), false)) != null) {
            float[] angle = InteractionUtility.calculateAngle(result.getPos());
            TrapModule.mc.player.setYaw(angle[0]);
            TrapModule.mc.player.setPitch(angle[1]);
        }
    }

    @EventHandler
    private void onPostSync(EventPostSync event) {
        BlockPos targetBlock;
        InteractionUtility.Rotate rotateMod;
        if (this.delay > 0) {
            --this.delay;
            return;
        }
        InteractionUtility.Rotate rotate = rotateMod = this.placeTiming.is(PlaceTiming.Vanilla) && !this.rotate.is(InteractionUtility.Rotate.None) ? InteractionUtility.Rotate.None : (InteractionUtility.Rotate)((Object)this.rotate.getValue());
        if (this.placeTiming.getValue() == PlaceTiming.Default) {
            BlockPos targetBlock2;
            for (int placed = 0; placed < this.blocksPerTick.getValue() && (targetBlock2 = this.getBlockToPlace()) != null && this.placeBlock(targetBlock2, rotateMod); ++placed) {
                this.delay = this.placeDelay.getValue();
                this.inactivityTimer.reset();
            }
        } else if (this.placeTiming.getValue() == PlaceTiming.Vanilla && (targetBlock = this.getBlockToPlace()) != null && this.placeBlock(targetBlock, rotateMod)) {
            this.sequentialBlocks.add(targetBlock);
            this.delay = this.placeDelay.getValue();
            this.inactivityTimer.reset();
        }
    }

    @Nullable
    protected BlockPos getBlockToPlace() {
        if (this.target == null || TrapModule.mc.player == null) {
            return null;
        }
        return this.getBlocks(this.target).stream().filter(pos -> pos.getSquaredDistance(TrapModule.mc.player.getPos()) < (double)this.range.getPow2Value()).filter(pos -> InteractionUtility.canPlaceBlock(pos, (InteractionUtility.Interact)((Object)((Object)this.interact.getValue())), true)).max(Comparator.comparing(pos -> TrapModule.mc.player.squaredDistanceTo(pos.toCenterPos()))).orElse(null);
    }

    protected List<BlockPos> getBlocks(@NotNull PlayerEntity player) {
        Vec3d playerPos = player.getPos();
        ArrayList<BlockPos> offsets = new ArrayList<BlockPos>();
        List<BlockPos> holePoses = HoleUtility.getHolePoses(playerPos);
        List<BlockPos> surroundPoses = HoleUtility.getSurroundPoses(playerPos);
        if (TrapModule.mc.player == null || TrapModule.mc.world == null) {
            return offsets;
        }
        switch (this.trapMode.getValue().ordinal()) {
            case 0: {
                offsets.addAll(holePoses.stream().map(BlockPos::down).toList());
                if (this.interact.getValue() != InteractionUtility.Interact.AirPlace) {
                    offsets.addAll(this.addHelpOffsets(surroundPoses));
                }
                offsets.addAll(surroundPoses);
                offsets.addAll(surroundPoses.stream().map(BlockPos::up).toList());
                if (this.interact.getValue() != InteractionUtility.Interact.AirPlace) {
                    surroundPoses.stream().map(pos -> pos.up(2)).filter(pos -> pos.getSquaredDistance(TrapModule.mc.player.getPos()) < (double)this.range.getPow2Value()).max(Comparator.comparing(pos -> TrapModule.mc.player.squaredDistanceTo(pos.toCenterPos()))).ifPresent(pos -> {
                        offsets.add((BlockPos)pos);
                        offsets.add(pos.down());
                    });
                }
                offsets.addAll(holePoses.stream().map(pos -> pos.up(2)).toList());
                break;
            }
            case 1: {
                offsets.addAll(holePoses.stream().map(BlockPos::down).toList());
                if (this.interact.getValue() != InteractionUtility.Interact.AirPlace) {
                    surroundPoses.stream().filter(pos -> pos.getSquaredDistance(TrapModule.mc.player.getPos()) < (double)this.range.getPow2Value()).max(Comparator.comparing(pos -> player.squaredDistanceTo(pos.toCenterPos()))).ifPresent(pos -> {
                        offsets.add((BlockPos)pos);
                        offsets.add(pos.up());
                        offsets.add(pos.up(2));
                    });
                    offsets.addAll(this.addHelpOffsets(surroundPoses));
                }
                offsets.addAll(surroundPoses);
                offsets.addAll(holePoses.stream().map(pos -> pos.up(2)).toList());
                break;
            }
            case 2: {
                offsets.addAll(holePoses.stream().map(BlockPos::down).toList());
                if (this.interact.getValue() != InteractionUtility.Interact.AirPlace) {
                    surroundPoses.stream().map(BlockPos::down).filter(pos -> pos.getSquaredDistance(TrapModule.mc.player.getPos()) < (double)this.range.getPow2Value()).max(Comparator.comparing(pos -> player.squaredDistanceTo(pos.toCenterPos()))).ifPresent(pos -> {
                        offsets.add((BlockPos)pos);
                        offsets.add(pos.up());
                        offsets.add(pos.up(3));
                    });
                }
                offsets.addAll(surroundPoses.stream().map(BlockPos::up).toList());
                offsets.addAll(holePoses.stream().map(pos -> pos.up(2)).toList());
            }
        }
        return offsets;
    }

    @NotNull
    private List<BlockPos> addHelpOffsets(@NotNull List<BlockPos> surroundPoses) {
        ArrayList<BlockPos> helpOffsets = new ArrayList<BlockPos>();
        if (TrapModule.mc.world == null || TrapModule.mc.player == null) {
            return helpOffsets;
        }
        surroundPoses.stream().map(BlockPos::down).filter(pos -> pos.getSquaredDistance(TrapModule.mc.player.getPos()) < (double)this.range.getPow2Value()).filter(pos -> {
            for (Direction dir : Direction.values()) {
                if (TrapModule.mc.world.getBlockState(pos.add(dir.getVector().up())).isReplaceable()) continue;
                return false;
            }
            return true;
        }).forEach(helpOffsets::add);
        return helpOffsets;
    }

    protected static enum PlaceTiming {
        Default,
        Vanilla;

    }

    protected static enum TrapMode {
        Full,
        Legs,
        Head;

    }
}

