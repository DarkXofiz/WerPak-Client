/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.hit.HitResult.Type
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext.FluidHandling
 *  net.minecraft.world.RaycastContext.ShapeType
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.features.modules.combat;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.impl.SettingGroup;
import thunder.hack.utility.Timer;
import thunder.hack.utility.player.InteractionUtility;
import thunder.hack.utility.player.InventoryUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public final class AutoWeb
extends Module {
    private final Setting<Integer> range = new Setting<Integer>("Range", 5, 1, 7);
    private final Setting<Integer> placeWallRange = new Setting<Integer>("WallRange", 5, 1, 7);
    private final Setting<PlaceTiming> placeTiming = new Setting<PlaceTiming>("PlaceTiming", PlaceTiming.Default);
    private final Setting<Integer> blocksPerTick = new Setting<Integer>("Block/Tick", 8, 1, 12, v -> this.placeTiming.getValue() == PlaceTiming.Default);
    private final Setting<Integer> placeDelay = new Setting<Integer>("Delay/Place", 3, 0, 10);
    private final Setting<InteractionUtility.Interact> interact = new Setting<InteractionUtility.Interact>("Interact", InteractionUtility.Interact.Strict);
    private final Setting<InteractionUtility.PlaceMode> placeMode = new Setting<InteractionUtility.PlaceMode>("PlaceMode", InteractionUtility.PlaceMode.Normal);
    private final Setting<InteractionUtility.Rotate> rotate = new Setting<InteractionUtility.Rotate>("Rotate", InteractionUtility.Rotate.None);
    private final Setting<SettingGroup> selection = new Setting<SettingGroup>("Selection", new SettingGroup(false, 0));
    private final Setting<Boolean> head = new Setting<Boolean>("Head", true).addToGroup(this.selection);
    private final Setting<Boolean> leggs = new Setting<Boolean>("Leggs", true).addToGroup(this.selection);
    private final Setting<Boolean> surround = new Setting<Boolean>("Surround", true).addToGroup(this.selection);
    private final Setting<Boolean> upperSurround = new Setting<Boolean>("UpperSurround", false).addToGroup(this.selection);
    private final Setting<SettingGroup> renderCategory = new Setting<SettingGroup>("Render", new SettingGroup(false, 0));
    private final Setting<RenderMode> renderMode = new Setting<RenderMode>("Render Mode", RenderMode.Fade).addToGroup(this.renderCategory);
    private final Setting<ColorSetting> renderFillColor = new Setting<ColorSetting>("Render Fill Color", new ColorSetting(HudEditor.getColor(0))).addToGroup(this.renderCategory);
    private final Setting<ColorSetting> renderLineColor = new Setting<ColorSetting>("Render Line Color", new ColorSetting(HudEditor.getColor(0))).addToGroup(this.renderCategory);
    private final Setting<Integer> renderLineWidth = new Setting<Integer>("Render Line Width", 2, 1, 5).addToGroup(this.renderCategory);
    private final Setting<Integer> effectDurationMs = new Setting<Integer>("Effect Duration (MS)", 500, 0, 10000).addToGroup(this.renderCategory);
    private final ArrayList<BlockPos> sequentialBlocks = new ArrayList();
    public static Timer inactivityTimer = new Timer();
    private final Map<BlockPos, Long> renderPoses = new ConcurrentHashMap<BlockPos, Long>();
    private int delay = 0;

    public AutoWeb() {
        super("AutoWeb", Module.Category.COMBAT);
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        this.renderPoses.forEach((pos, time) -> {
            if (System.currentTimeMillis() - time > (long)this.effectDurationMs.getValue().intValue()) {
                this.renderPoses.remove(pos);
            } else {
                switch (this.renderMode.getValue().ordinal()) {
                    case 0: {
                        Render3DEngine.drawFilledBox(stack, new Box(pos), Render2DEngine.injectAlpha(this.renderFillColor.getValue().getColorObject(), (int)(100.0f * (1.0f - (float)(System.currentTimeMillis() - time) / 500.0f))));
                        Render3DEngine.drawBoxOutline(new Box(pos), Render2DEngine.injectAlpha(this.renderLineColor.getValue().getColorObject(), (int)(100.0f * (1.0f - (float)(System.currentTimeMillis() - time) / 500.0f))), this.renderLineWidth.getValue().intValue());
                        break;
                    }
                    case 1: {
                        float scale = 1.0f - (float)(System.currentTimeMillis() - time) / 500.0f;
                        Box box = new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                        Render3DEngine.drawFilledBox(stack, box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), Render2DEngine.injectAlpha(this.renderFillColor.getValue().getColorObject(), (int)(100.0f * (1.0f - (float)(System.currentTimeMillis() - time) / 500.0f))));
                        Render3DEngine.drawBoxOutline(box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), this.renderLineColor.getValue().getColorObject(), this.renderLineWidth.getValue().intValue());
                    }
                }
            }
        });
    }

    @Override
    public void onEnable() {
        this.sequentialBlocks.clear();
        this.renderPoses.clear();
    }

    @EventHandler
    public void onTick(EventTick e) {
        BlockPos targetBlock1 = this.getSequentialPos();
        if (targetBlock1 == null) {
            return;
        }
        if (this.delay > 0) {
            --this.delay;
            return;
        }
        InventoryUtility.saveSlot();
        if (this.placeTiming.getValue() == PlaceTiming.Default) {
            BlockPos targetBlock;
            for (int placed = 0; placed < this.blocksPerTick.getValue() && (targetBlock = this.getSequentialPos()) != null && InteractionUtility.placeBlock(targetBlock, this.rotate.getValue(), this.interact.getValue(), this.placeMode.getValue(), this.getSlot(), false, true); ++placed) {
                this.renderPoses.put(targetBlock, System.currentTimeMillis());
                this.delay = this.placeDelay.getValue();
                inactivityTimer.reset();
            }
        } else if (this.placeTiming.getValue() == PlaceTiming.Vanilla) {
            BlockPos targetBlock = this.getSequentialPos();
            if (targetBlock == null) {
                return;
            }
            if (InteractionUtility.placeBlock(targetBlock, this.rotate.getValue(), this.interact.getValue(), this.placeMode.getValue(), this.getSlot(), false, true)) {
                this.sequentialBlocks.add(targetBlock);
                this.renderPoses.put(targetBlock, System.currentTimeMillis());
                this.delay = this.placeDelay.getValue();
                inactivityTimer.reset();
            }
        }
        InventoryUtility.returnSlot();
    }

    private BlockPos getSequentialPos() {
        PlayerEntity target = Managers.COMBAT.getNearestTarget(this.range.getValue().intValue());
        if (target != null) {
            BlockPos targetBp = BlockPos.ofFloored(target.getPos());
            ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
            if (this.leggs.getValue().booleanValue()) {
                positions.add(targetBp);
            }
            if (this.head.getValue().booleanValue()) {
                positions.add(targetBp.up());
            }
            if (this.surround.getValue().booleanValue()) {
                positions.add(targetBp.east());
                positions.add(targetBp.west());
                positions.add(targetBp.south());
                positions.add(targetBp.north());
            }
            if (this.upperSurround.getValue().booleanValue()) {
                positions.add(targetBp.east().up());
                positions.add(targetBp.west().up());
                positions.add(targetBp.south().up());
                positions.add(targetBp.north().up());
            }
            for (BlockPos bp : positions) {
                BlockHitResult wallCheck = AutoWeb.mc.world.raycast(new RaycastContext(InteractionUtility.getEyesPos((Entity)AutoWeb.mc.player), bp.toCenterPos().offset(Direction.UP, 0.5), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)AutoWeb.mc.player));
                if (wallCheck != null && wallCheck.getType() == HitResult.Type.BLOCK && wallCheck.getBlockPos() != bp && InteractionUtility.squaredDistanceFromEyes(bp.toCenterPos()) > this.placeWallRange.getPow2Value() || !InteractionUtility.canPlaceBlock(bp, this.interact.getValue(), true) || !AutoWeb.mc.world.getBlockState(bp).isReplaceable()) continue;
                return bp;
            }
        }
        return null;
    }

    private int getSlot() {
        Block blockFromMainhandItem;
        ArrayList<Block> canUseBlocks = new ArrayList<Block>();
        canUseBlocks.add(Blocks.COBWEB);
        int slot = -1;
        ItemStack mainhandStack = AutoWeb.mc.player.getMainHandStack();
        if (mainhandStack != ItemStack.EMPTY && mainhandStack.getItem() instanceof BlockItem && canUseBlocks.contains(blockFromMainhandItem = ((BlockItem)mainhandStack.getItem()).getBlock())) {
            slot = AutoWeb.mc.player.getInventory().selectedSlot;
        }
        if (slot == -1) {
            for (int i = 0; i < 9; ++i) {
                Block blockFromItem;
                ItemStack stack = AutoWeb.mc.player.getInventory().getStack(i);
                if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof BlockItem) || !canUseBlocks.contains(blockFromItem = ((BlockItem)stack.getItem()).getBlock())) continue;
                slot = i;
                break;
            }
        }
        return slot;
    }

    private static enum PlaceTiming {
        Default,
        Vanilla;

    }

    private static enum RenderMode {
        Fade,
        Decrease;

    }
}

