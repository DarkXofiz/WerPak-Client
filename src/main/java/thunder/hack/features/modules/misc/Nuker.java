/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.Entity
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.hit.HitResult.Type
 *  net.minecraft.block.FluidBlock
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.block.BlockState
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext.FluidHandling
 *  net.minecraft.world.RaycastContext.ShapeType
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.features.modules.misc;

import java.awt.Color;
import java.util.ArrayList;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.hit.HitResult;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventAttackBlock;
import thunder.hack.events.impl.EventSetBlockState;
import thunder.hack.events.impl.EventSync;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.features.modules.player.SpeedMine;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.impl.ItemSelectSetting;
import thunder.hack.utility.Timer;
import thunder.hack.utility.player.InteractionUtility;
import thunder.hack.utility.player.PlayerUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.world.ExplosionUtility;

public class Nuker
extends Module {
    public final Setting<ItemSelectSetting> selectedBlocks = new Setting<ItemSelectSetting>("SelectedBlocks", new ItemSelectSetting(new ArrayList<String>()));
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Default);
    private final Setting<Integer> delay = new Setting<Integer>("Delay", 25, 0, 1000);
    private final Setting<BlockSelection> blocks = new Setting<BlockSelection>("Blocks", BlockSelection.Select);
    private final Setting<Boolean> ignoreWalls = new Setting<Boolean>("IgnoreWalls", false);
    private final Setting<Boolean> flatten = new Setting<Boolean>("Flatten", false);
    private final Setting<Boolean> creative = new Setting<Boolean>("Creative", false);
    private final Setting<Boolean> avoidLava = new Setting<Boolean>("AvoidLava", false);
    private final Setting<Float> range = new Setting<Float>("Range", Float.valueOf(4.2f), Float.valueOf(1.5f), Float.valueOf(25.0f));
    private final Setting<ColorMode> colorMode = new Setting<ColorMode>("ColorMode", ColorMode.Sync);
    public final Setting<ColorSetting> color = new Setting<ColorSetting>("Color", new ColorSetting(575714484), v -> this.colorMode.getValue() == ColorMode.Custom);
    private Block targetBlockType;
    private BlockData blockData;
    private Timer breakTimer = new Timer();
    private NukerThread nukerThread = new NukerThread();
    private float rotationYaw;
    private float rotationPitch;

    public Nuker() {
        super("Nuker", Module.Category.MISC);
    }

    @Override
    public void onEnable() {
        this.nukerThread = new NukerThread();
        this.nukerThread.setName("ThunderHack-NukerThread");
        this.nukerThread.setDaemon(true);
        this.nukerThread.start();
    }

    @Override
    public void onDisable() {
        this.nukerThread.interrupt();
    }

    @Override
    public void onUpdate() {
        if (!this.nukerThread.isAlive()) {
            this.nukerThread = new NukerThread();
            this.nukerThread.setName("ThunderHack-NukerThread");
            this.nukerThread.setDaemon(true);
            this.nukerThread.start();
        }
    }

    @EventHandler
    public void onBlockInteract(EventAttackBlock e) {
        if (Nuker.mc.world.isAir(e.getBlockPos())) {
            return;
        }
        if (this.blocks.getValue().equals((Object)BlockSelection.Select) && this.targetBlockType != Nuker.mc.world.getBlockState(e.getBlockPos()).getBlock()) {
            this.targetBlockType = Nuker.mc.world.getBlockState(e.getBlockPos()).getBlock();
            this.sendMessage(ClientSettings.isRu() ? "\u0412\u044b\u0431\u0440\u0430\u043d \u0431\u043b\u043e\u043a: " + String.valueOf(Formatting.AQUA) + this.targetBlockType.getName().getString() : "Selected block: " + String.valueOf(Formatting.AQUA) + this.targetBlockType.getName().getString());
        }
    }

    @EventHandler
    public void onBlockDestruct(EventSetBlockState e) {
        if (this.blockData != null && e.getPos() == this.blockData.bp && e.getState().isAir()) {
            this.blockData = null;
            new Thread(() -> {
                if ((this.targetBlockType != null || this.blocks.getValue().equals((Object)BlockSelection.All)) && !Nuker.mc.options.attackKey.isPressed() && this.blockData == null) {
                    this.blockData = this.getNukerBlockPos();
                }
            }).start();
        }
    }

    @EventHandler
    public void onSync(EventSync e) {
        if (this.rotationYaw != -999.0f) {
            Nuker.mc.player.setYaw(this.rotationYaw);
            Nuker.mc.player.setPitch(this.rotationPitch);
            this.rotationYaw = -999.0f;
        }
    }

    @EventHandler
    public void onPlayerUpdate(PlayerUpdateEvent e) {
        if (this.blockData != null && (Nuker.mc.world.getBlockState(this.blockData.bp).getBlock() != this.targetBlockType && this.blocks.getValue().equals((Object)BlockSelection.Select) || PlayerUtility.squaredDistanceFromEyes(this.blockData.bp.toCenterPos()) > this.range.getPow2Value() || Nuker.mc.world.isAir(this.blockData.bp))) {
            this.blockData = null;
        }
        if (this.blockData == null || Nuker.mc.options.attackKey.isPressed()) {
            return;
        }
        float[] angle = InteractionUtility.calculateAngle(this.blockData.vec3d);
        this.rotationYaw = angle[0];
        this.rotationPitch = angle[1];
        ModuleManager.rotations.fixRotation = this.rotationYaw;
        if (this.mode.getValue() == Mode.Default) {
            this.breakBlock();
        }
        if (this.mode.getValue() == Mode.FastAF) {
            int intRange = (int)(Math.floor(this.range.getValue().floatValue()) + 1.0);
            var blocks_ = BlockPos.iterateOutwards((BlockPos)new BlockPos((Vec3i)BlockPos.ofFloored(Nuker.mc.player.getPos()).up()), (int)intRange, (int)intRange, (int)intRange);
            for (BlockPos b : blocks_) {
                if (this.flatten.getValue().booleanValue() && (double)b.getY() < Nuker.mc.player.getY() || this.avoidLava.getValue().booleanValue() && this.checkLava(b)) continue;
                BlockState state = Nuker.mc.world.getBlockState(b);
                if (!(PlayerUtility.squaredDistanceFromEyes(b.toCenterPos()) <= this.range.getPow2Value()) || !this.isAllowed(state.getBlock())) continue;
                try {
                    this.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, b, Direction.UP, id));
                    Nuker.mc.interactionManager.breakBlock(b);
                    Nuker.mc.player.swingHand(Hand.MAIN_HAND);
                }
                catch (Exception exception) {}
            }
        }
    }

    public synchronized void breakBlock() {
        if (this.blockData == null || Nuker.mc.options.attackKey.isPressed()) {
            return;
        }
        if (ModuleManager.speedMine.isEnabled() && ModuleManager.speedMine.mode.getValue() == SpeedMine.Mode.Packet) {
            if (!ModuleManager.speedMine.alreadyActing(this.blockData.bp)) {
                Nuker.mc.interactionManager.attackBlock(this.blockData.bp, this.blockData.dir);
                Nuker.mc.player.swingHand(Hand.MAIN_HAND);
            }
        } else {
            BlockPos cache = this.blockData.bp;
            Nuker.mc.interactionManager.updateBlockBreakingProgress(this.blockData.bp, this.blockData.dir);
            Nuker.mc.player.swingHand(Hand.MAIN_HAND);
            if (this.creative.getValue().booleanValue()) {
                Nuker.mc.interactionManager.breakBlock(cache);
            }
        }
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        BlockPos renderBp = null;
        if (this.blockData != null && this.blockData.bp != null) {
            renderBp = this.blockData.bp;
        }
        if (renderBp != null) {
            Color color1 = this.colorMode.getValue() == ColorMode.Sync ? HudEditor.getColor(1) : this.color.getValue().getColorObject();
            Render3DEngine.drawBoxOutline(new Box(this.blockData.bp), color1, 2.0f);
            Render3DEngine.drawFilledBox(stack, new Box(this.blockData.bp), Render2DEngine.injectAlpha(color1, 100));
        }
        if (this.mode.getValue() == Mode.Fast && this.breakTimer.passedMs(this.delay.getValue().intValue())) {
            this.breakBlock();
            this.breakTimer.reset();
        }
    }

    public BlockData getNukerBlockPos() {
        int intRange = (int)(Math.floor(this.range.getValue().floatValue()) + 1.0);
        var blocks_ = BlockPos.iterateOutwards((BlockPos)new BlockPos((Vec3i)BlockPos.ofFloored(Nuker.mc.player.getPos()).up()), (int)intRange, (int)intRange, (int)intRange);
        for (BlockPos b : blocks_) {
            BlockState state = Nuker.mc.world.getBlockState(b);
            if (this.flatten.getValue().booleanValue() && (double)b.getY() < Nuker.mc.player.getY() || !(PlayerUtility.squaredDistanceFromEyes(b.toCenterPos()) <= this.range.getPow2Value()) || this.avoidLava.getValue().booleanValue() && this.checkLava(b) || !this.isAllowed(state.getBlock())) continue;
            if (this.ignoreWalls.getValue().booleanValue()) {
                BlockHitResult result = ExplosionUtility.rayCastBlock(new RaycastContext(InteractionUtility.getEyesPos((Entity)Nuker.mc.player), b.toCenterPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)Nuker.mc.player), b);
                if (result == null) continue;
                return new BlockData(b, result.getPos(), result.getSide());
            }
            for (float x1 = 0.0f; x1 <= 1.0f; x1 += 0.2f) {
                for (float y1 = 0.0f; y1 <= 1.0f; y1 += 0.2f) {
                    for (float z1 = 0.0f; z1 <= 1.0f; z1 += 0.2f) {
                        Vec3d p = new Vec3d((double)((float)b.getX() + x1), (double)((float)b.getY() + y1), (double)((float)b.getZ() + z1));
                        BlockHitResult bhr = Nuker.mc.world.raycast(new RaycastContext(InteractionUtility.getEyesPos((Entity)Nuker.mc.player), p, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)Nuker.mc.player));
                        if (bhr == null || bhr.getType() != HitResult.Type.BLOCK || !bhr.getBlockPos().equals((Object)b)) continue;
                        return new BlockData(b, p, bhr.getSide());
                    }
                }
            }
        }
        return null;
    }

    private boolean checkLava(BlockPos base) {
        for (Direction dir : Direction.values()) {
            if (Nuker.mc.world.getBlockState(base.offset(dir)).getBlock() != Blocks.LAVA) continue;
            return true;
        }
        return false;
    }

    private boolean isAllowed(Block block) {
        boolean allowed = this.selectedBlocks.getValue().getItemsById().contains(block.getTranslationKey().replace("block.minecraft.", ""));
        return switch (this.blocks.getValue().ordinal()) {
            case 1 -> {
                if (block != Blocks.BEDROCK && block != Blocks.AIR && block != Blocks.CAVE_AIR && !(block instanceof FluidBlock)) {
                    yield true;
                }
                yield false;
            }
            case 0 -> {
                if (block == this.targetBlockType) {
                    yield true;
                }
                yield false;
            }
            case 3 -> allowed;
            default -> !allowed && block != Blocks.BEDROCK && block != Blocks.AIR && block != Blocks.CAVE_AIR && !(block instanceof FluidBlock);
        };
    }

    private static enum Mode {
        Default,
        Fast,
        FastAF;

    }

    private static enum BlockSelection {
        Select,
        All,
        BlackList,
        WhiteList;

    }

    private static enum ColorMode {
        Custom,
        Sync;

    }

    public class NukerThread
    extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (!Module.fullNullCheck()) {
                        while (Managers.ASYNC.ticking.get()) {
                        }
                        if (Nuker.this.targetBlockType == null && Nuker.this.blocks.getValue().equals((Object)BlockSelection.Select) || Module.mc.options.attackKey.isPressed() || Nuker.this.blockData != null) continue;
                        Nuker.this.blockData = Nuker.this.getNukerBlockPos();
                        continue;
                    }
                    Thread.yield();
                }
                catch (Exception exception) {}
            }
        }
    }

    public record BlockData(BlockPos bp, Vec3d vec3d, Direction dir) {
    }
}

