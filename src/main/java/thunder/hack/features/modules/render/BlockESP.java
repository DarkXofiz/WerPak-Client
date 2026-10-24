/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.util.Util
 *  net.minecraft.block.AirBlock
 *  net.minecraft.block.BarrierBlock
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.block.CommandBlock
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.features.modules.render;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.util.Util;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BarrierBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.CommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.impl.ItemSelectSetting;
import thunder.hack.utility.Timer;
import thunder.hack.utility.player.PlayerUtility;
import thunder.hack.utility.render.Render3DEngine;

public class BlockESP
extends Module {
    public final Setting<ItemSelectSetting> selectedBlocks = new Setting<ItemSelectSetting>("SelectedBlocks", new ItemSelectSetting(new ArrayList<String>()));
    public static ArrayList<BlockVec> blocks = new ArrayList();
    private final Setting<Integer> range = new Setting<Integer>("Range", 100, 1, 128);
    private final Setting<BooleanSettingGroup> limit = new Setting<BooleanSettingGroup>("Limit", new BooleanSettingGroup(true));
    private final Setting<Integer> limitCount = new Setting<Integer>("LimitCount", 50, 1, 2048).addToGroup(this.limit);
    private final Setting<ColorSetting> color = new Setting<ColorSetting>("Color", new ColorSetting(-16711681));
    private final Setting<Boolean> illegals = new Setting<Boolean>("Illegals", true);
    private final Setting<Boolean> tracers = new Setting<Boolean>("Tracers", false);
    private final Setting<Boolean> fill = new Setting<Boolean>("Fill", true);
    private final Setting<Boolean> outline = new Setting<Boolean>("Outline", true);
    private final ExecutorService searchThread = Executors.newSingleThreadExecutor();
    private final Timer searchTimer = new Timer();
    private long lastFrameTime;
    private boolean canContinue;

    public BlockESP() {
        super("BlockESP", Module.Category.RENDER);
    }

    @Override
    public void onEnable() {
        blocks.clear();
        this.lastFrameTime = System.currentTimeMillis();
        this.canContinue = true;
    }

    @Override
    public void onUpdate() {
        if (this.searchTimer.every(1000L) && this.canContinue) {
            CompletableFuture.supplyAsync(this::scan, this.searchThread).thenAcceptAsync(this::sync, (Executor)Util.getMainWorkerExecutor());
            this.canContinue = false;
        }
    }

    private ArrayList<BlockVec> scan() {
        ArrayList<BlockVec> blocks = new ArrayList<BlockVec>();
        int startX = (int)Math.floor(BlockESP.mc.player.getX() - (double)this.range.getValue().intValue());
        int endX = (int)Math.ceil(BlockESP.mc.player.getX() + (double)this.range.getValue().intValue());
        int startY = BlockESP.mc.world.getBottomY() + 1;
        int endY = BlockESP.mc.world.getTopY();
        int startZ = (int)Math.floor(BlockESP.mc.player.getZ() - (double)this.range.getValue().intValue());
        int endZ = (int)Math.ceil(BlockESP.mc.player.getZ() + (double)this.range.getValue().intValue());
        for (int x = startX; x <= endX; ++x) {
            for (int y = startY; y <= endY; ++y) {
                for (int z = startZ; z <= endZ; ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState bs = BlockESP.mc.world.getBlockState(pos);
                    if (!this.shouldAdd(bs.getBlock(), pos)) continue;
                    blocks.add(new BlockVec(pos.getX(), pos.getY(), pos.getZ()));
                }
            }
        }
        return blocks;
    }

    private void sync(ArrayList<BlockVec> b) {
        blocks = b;
        this.canContinue = true;
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        if (BlockESP.fullNullCheck() || blocks.isEmpty()) {
            return;
        }
        int count = 0;
        if (mc.getCurrentFps() < 8 && BlockESP.mc.player.age > 100) {
            this.disable(ClientSettings.isRu() ? "\u0421\u043f\u0430\u0441\u0430\u0435\u043c \u0442\u0432\u043e\u0439 \u041f\u041a :)" : "Saving ur pc :)");
            return;
        }
        if (this.fill.getValue().booleanValue() || this.outline.getValue().booleanValue()) {
            for (BlockVec vec : Lists.newArrayList(blocks)) {
                if (count > this.limitCount.getValue() && this.limit.getValue().isEnabled()) continue;
                if (vec.getDistance(BlockESP.mc.player.getPos()) > (double)this.range.getPow2Value()) {
                    blocks.remove(vec);
                    continue;
                }
                Box b = new Box(vec.x, vec.y, vec.z, vec.x + 1.0, vec.y + 1.0, vec.z + 1.0);
                if (this.fill.getValue().booleanValue()) {
                    Render3DEngine.FILLED_QUEUE.add(new Render3DEngine.FillAction(b, this.color.getValue().getColorObject()));
                }
                if (this.outline.getValue().booleanValue()) {
                    Render3DEngine.OUTLINE_QUEUE.add(new Render3DEngine.OutlineAction(b, this.color.getValue().getColorObject(), 2.0f));
                }
                if (this.tracers.getValue().booleanValue()) {
                    Vec3d vec2 = new Vec3d(0.0, 0.0, 75.0).rotateX(-((float)Math.toRadians(BlockESP.mc.gameRenderer.getCamera().getPitch()))).rotateY(-((float)Math.toRadians(BlockESP.mc.gameRenderer.getCamera().getYaw()))).add(BlockESP.mc.cameraEntity.getEyePos());
                    Render3DEngine.drawLineDebug(vec2, vec.getVector(), this.color.getValue().getColorObject());
                }
                ++count;
            }
        }
        this.lastFrameTime = System.currentTimeMillis();
    }

    private boolean shouldAdd(Block block, BlockPos pos) {
        if (block instanceof AirBlock) {
            return false;
        }
        if (this.selectedBlocks.getValue().contains(block)) {
            return true;
        }
        if (this.illegals.getValue().booleanValue()) {
            return this.isIllegal(block, pos);
        }
        return false;
    }

    private boolean isIllegal(Block block, BlockPos pos) {
        if (block instanceof CommandBlock || block instanceof BarrierBlock) {
            return true;
        }
        if (block == Blocks.BEDROCK) {
            if (!PlayerUtility.isInHell()) {
                return pos.getY() > 4;
            }
            return pos.getY() > 127 || pos.getY() < 123 && pos.getY() > 4;
        }
        return false;
    }

    public record BlockVec(double x, double y, double z) {
        public double getDistance(@NotNull Vec3d v) {
            double dx = this.x - v.x;
            double dy = this.y - v.y;
            double dz = this.z - v.z;
            return dx * dx + dy * dy + dz * dz;
        }

        public Vec3d getVector() {
            return new Vec3d(this.x + 0.5, this.y + 0.5, this.z + 0.5);
        }
    }
}

