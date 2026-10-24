/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.item.ItemStack
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.enchantment.Enchantments
 *  net.minecraft.world.BlockView
 *  net.minecraft.block.AirBlock
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Box
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.block.BlockState
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.registry.entry.RegistryEntry
 */
package thunder.hack.features.modules.player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.world.BlockView;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.network.packet.Packet;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.core.manager.player.PlayerManager;
import thunder.hack.events.impl.EventAttackBlock;
import thunder.hack.events.impl.EventSync;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.IInteractionManager;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.impl.SettingGroup;
import thunder.hack.utility.Timer;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.player.InteractionUtility;
import thunder.hack.utility.player.InventoryUtility;
import thunder.hack.utility.player.PlayerUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public final class SpeedMine
extends Module {
    public final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Packet);
    public final Setting<Boolean> doubleMine = new Setting<Boolean>("DoubleMine", false);
    private final Setting<StartMode> startMode = new Setting<StartMode>("StartMode", StartMode.StartAbort, v -> this.mode.is(Mode.Packet) && this.doubleMine.getValue() == false);
    private final Setting<SwitchMode> switchMode = new Setting<SwitchMode>("SwitchMode", SwitchMode.Alternative, v -> this.mode.not(Mode.Damage));
    private final Setting<Integer> swapDelay = new Setting<Integer>("SwapDelay", 50, 0, 1000, v -> this.mode.getValue() != Mode.Damage);
    private final Setting<Float> factor = new Setting<Float>("Factor", Float.valueOf(1.0f), Float.valueOf(0.5f), Float.valueOf(2.0f), v -> this.mode.getValue() != Mode.Damage);
    private final Setting<Float> speed = new Setting<Float>("Speed", Float.valueOf(0.5f), Float.valueOf(0.0f), Float.valueOf(1.0f), v -> this.mode.getValue() == Mode.Damage);
    public final Setting<Float> range = new Setting<Float>("Range", Float.valueOf(4.2f), Float.valueOf(3.0f), Float.valueOf(10.0f), v -> this.mode.getValue() != Mode.Damage);
    private final Setting<Boolean> rotate = new Setting<Boolean>("Rotate", false, v -> this.mode.getValue() != Mode.Damage);
    private final Setting<Boolean> resetOnSwitch = new Setting<Boolean>("ResetOnSwitch", true, v -> this.mode.getValue() != Mode.Damage);
    private final Setting<Integer> breakAttempts = new Setting<Integer>("BreakAttempts", 10, 1, 50, v -> this.mode.getValue() == Mode.Packet);
    private final Setting<Boolean> pauseEat = new Setting<Boolean>("Pause On Eat", false);
    private final Setting<Boolean> clientRemove = new Setting<Boolean>("ClientRemove", true);
    private final Setting<SettingGroup> packets = new Setting<SettingGroup>("Packets", new SettingGroup(false, 0), v -> this.mode.is(Mode.Packet) && this.doubleMine.getValue() == false);
    private final Setting<Boolean> stop = new Setting<Boolean>("Stop", true, v -> this.mode.is(Mode.Packet) && this.doubleMine.getValue() == false).addToGroup(this.packets);
    private final Setting<Boolean> abort = new Setting<Boolean>("Abort", true, v -> this.mode.is(Mode.Packet) && this.doubleMine.getValue() == false).addToGroup(this.packets);
    private final Setting<Boolean> start = new Setting<Boolean>("Start", true, v -> this.mode.is(Mode.Packet) && this.doubleMine.getValue() == false).addToGroup(this.packets);
    private final Setting<Boolean> stop2 = new Setting<Boolean>("Stop2", true, v -> this.mode.is(Mode.Packet) && this.doubleMine.getValue() == false).addToGroup(this.packets);
    private final Setting<BooleanSettingGroup> render = new Setting<BooleanSettingGroup>("Render", new BooleanSettingGroup(false), v -> this.mode.getValue() != Mode.Damage);
    private final Setting<Boolean> smooth = new Setting<Boolean>("Smooth", true, v -> this.mode.getValue() != Mode.Damage).addToGroup(this.render);
    private final Setting<RenderMode> renderMode = new Setting<RenderMode>("Render Mode", RenderMode.Shrink, v -> this.mode.getValue() != Mode.Damage).addToGroup(this.render);
    private final Setting<ColorSetting> startLineColor = new Setting<ColorSetting>("Start Line Color", new ColorSetting(new Color(255, 0, 0, 200)), v -> this.mode.getValue() != Mode.Damage).addToGroup(this.render);
    private final Setting<ColorSetting> endLineColor = new Setting<ColorSetting>("End Line Color", new ColorSetting(new Color(47, 255, 0, 200)), v -> this.mode.getValue() != Mode.Damage).addToGroup(this.render);
    private final Setting<Integer> lineWidth = new Setting<Integer>("Line Width", 2, 1, 10, v -> this.mode.getValue() != Mode.Damage).addToGroup(this.render);
    private final Setting<ColorSetting> startFillColor = new Setting<ColorSetting>("Start Fill Color", new ColorSetting(new Color(255, 0, 0, 120)), v -> this.mode.getValue() != Mode.Damage).addToGroup(this.render);
    private final Setting<ColorSetting> endFillColor = new Setting<ColorSetting>("End Fill Color", new ColorSetting(new Color(47, 255, 0, 120)), v -> this.mode.getValue() != Mode.Damage).addToGroup(this.render);
    public ArrayList<MineAction> actions = new ArrayList();

    public SpeedMine() {
        super("SpeedMine", Module.Category.PLAYER);
    }

    @Override
    public void onDisable() {
        this.actions.forEach(MineAction::reset);
        this.actions.clear();
    }

    @Override
    public void onEnable() {
        this.actions.forEach(MineAction::reset);
        this.actions.clear();
    }

    @Override
    public void onUpdate() {
        if (SpeedMine.fullNullCheck() || SpeedMine.mc.player.getAbilities().creativeMode) {
            return;
        }
        if (PlayerUtility.isEating() && this.pauseEat.getValue().booleanValue()) {
            return;
        }
        if (this.mode.getValue() == Mode.Damage && ((IInteractionManager)SpeedMine.mc.interactionManager).getCurBlockDamageMP() < this.speed.getValue().floatValue()) {
            ((IInteractionManager)SpeedMine.mc.interactionManager).setCurBlockDamageMP(this.speed.getValue().floatValue());
        }
        this.actions.removeIf(MineAction::update);
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        if (this.mode.is(Mode.Damage) || SpeedMine.mc.world == null) {
            return;
        }
        this.actions.forEach(a -> {
            if (!SpeedMine.mc.world.isAir(a.getPos())) {
                float noom = (float)MathUtility.clamp(Render2DEngine.interpolate(a.getPrevProgress(), a.getProgress(), Render3DEngine.getTickDelta()), 0.0, 1.0);
                Box renderBox = switch (this.renderMode.getValue().ordinal()) {
                    default -> throw new MatchException(null, null);
                    case 0 -> new Box(a.getPos());
                    case 2 -> new Box((double)a.getPos().getX(), (double)a.getPos().getY(), (double)a.getPos().getZ(), (double)(a.getPos().getX() + 1), (double)((float)a.getPos().getY() + noom), (double)(a.getPos().getZ() + 1));
                    case 1 -> new Box((double)a.getPos().getX(), (double)a.getPos().getY(), (double)a.getPos().getZ(), (double)a.getPos().getX(), (double)a.getPos().getY(), (double)a.getPos().getZ()).shrink((double)noom, (double)noom, (double)noom).offset(0.5 + (double)noom * 0.5, 0.5 + (double)noom * 0.5, 0.5 + (double)noom * 0.5);
                };
                Render3DEngine.FILLED_QUEUE.add(new Render3DEngine.FillAction(renderBox, Render2DEngine.getColor(this.startFillColor.getValue().getColorObject(), this.endFillColor.getValue().getColorObject(), a.getProgress(), this.smooth.getValue())));
                Render3DEngine.OUTLINE_QUEUE.add(new Render3DEngine.OutlineAction(renderBox, Render2DEngine.getColor(this.startLineColor.getValue().getColorObject(), this.endLineColor.getValue().getColorObject(), a.getProgress(), this.smooth.getValue()), this.lineWidth.getValue().intValue()));
            }
        });
    }

    @EventHandler
    public void onAttackBlock(@NotNull EventAttackBlock event) {
        if (SpeedMine.fullNullCheck() || !this.canBreak(event.getBlockPos()) || SpeedMine.mc.player.getAbilities().creativeMode || this.mode.is(Mode.Damage)) {
            return;
        }
        if (!this.alreadyActing(event.getBlockPos())) {
            if (!(this.doubleMine.getValue().booleanValue() && this.actions.size() < 2 || this.actions.isEmpty())) {
                this.actions.removeFirst().cancel();
            }
            this.actions.add(new MineAction(event.getBlockPos(), event.getEnumFacing()));
        }
        event.cancel();
    }

    public boolean alreadyActing(BlockPos blockPos) {
        return this.actions.stream().anyMatch(a -> a.pos.equals((Object)blockPos));
    }

    @EventHandler(priority=-100)
    private void onSync(EventSync event) {
        this.actions.forEach(MineAction::onSync);
    }

    @EventHandler
    private void onPacketSend(@NotNull PacketEvent.SendPost e) {
        if (e.getPacket() instanceof UpdateSelectedSlotC2SPacket && this.resetOnSwitch.getValue().booleanValue() && !this.switchMode.is(SwitchMode.Silent) && !this.mode.is(Mode.GrimInstant)) {
            this.actions.forEach(MineAction::reset);
        }
    }

    private void closeScreen() {
        if (SpeedMine.mc.player == null) {
            return;
        }
        this.sendPacket((Packet<?>)new CloseHandledScreenC2SPacket(SpeedMine.mc.player.currentScreenHandler.syncId));
    }

    public float getBlockStrength(@NotNull BlockState state, BlockPos position) {
        if (state == Blocks.AIR.getDefaultState()) {
            return 0.02f;
        }
        float hardness = state.getHardness((BlockView)SpeedMine.mc.world, position);
        if (hardness < 0.0f) {
            return 0.0f;
        }
        return this.getDigSpeed(state, position) / hardness / (this.canBreak(position) ? 30.0f : 100.0f);
    }

    private float getDestroySpeed(BlockPos position, BlockState state) {
        float destroySpeed = 1.0f;
        int slot = this.getTool(position);
        if (SpeedMine.mc.player == null) {
            return 0.0f;
        }
        if (slot != -1 && SpeedMine.mc.player.getInventory().getStack(slot) != null && !SpeedMine.mc.player.getInventory().getStack(slot).isEmpty()) {
            destroySpeed *= SpeedMine.mc.player.getInventory().getStack(slot).getMiningSpeedMultiplier(state);
        }
        return destroySpeed;
    }

    public float getDigSpeed(BlockState state, BlockPos position) {
        int slot;
        if (SpeedMine.mc.player == null) {
            return 0.0f;
        }
        float digSpeed = this.getDestroySpeed(position, state);
        if (digSpeed > 1.0f && (slot = this.getTool(position)) != -1) {
            ItemStack itemstack = SpeedMine.mc.player.getInventory().getStack(slot);
            int efficiencyModifier = EnchantmentHelper.getLevel((RegistryEntry)((RegistryEntry)SpeedMine.mc.world.getRegistryManager().get(Enchantments.EFFICIENCY.getRegistryRef()).getEntry(Enchantments.EFFICIENCY).get()), (ItemStack)itemstack);
            if (efficiencyModifier > 0 && !itemstack.isEmpty()) {
                digSpeed += (float)(StrictMath.pow(efficiencyModifier, 2.0) + 1.0);
            }
        }
        if (SpeedMine.mc.player.hasStatusEffect(StatusEffects.HASTE)) {
            digSpeed *= 1.0f + (float)(Objects.requireNonNull(SpeedMine.mc.player.getStatusEffect(StatusEffects.HASTE)).getAmplifier() + 1) * 0.2f;
        }
        if (SpeedMine.mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            digSpeed *= (float)Math.pow(0.3f, Objects.requireNonNull(SpeedMine.mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE)).getAmplifier() + 1);
        }
        if (SpeedMine.mc.player.isSubmergedInWater()) {
            digSpeed *= (float)SpeedMine.mc.player.getAttributeInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED).getValue();
        }
        if (!SpeedMine.mc.player.isOnGround() && ModuleManager.freeCam.isDisabled()) {
            digSpeed /= 5.0f;
        }
        return digSpeed < 0.0f ? 0.0f : digSpeed * this.factor.getValue().floatValue();
    }

    public int getTool(BlockPos pos) {
        int index = -1;
        float currentFastest = 1.0f;
        if (SpeedMine.mc.world == null || SpeedMine.mc.player == null || SpeedMine.mc.world.getBlockState(pos).getBlock() instanceof AirBlock) {
            return -1;
        }
        for (int i = 9; i < 45; ++i) {
            float destroySpeed;
            float digSpeed;
            ItemStack stack = SpeedMine.mc.player.getInventory().getStack(i >= 36 ? i - 36 : i);
            if (stack == ItemStack.EMPTY || stack.getMaxDamage() - stack.getDamage() <= 10 || !((digSpeed = (float)EnchantmentHelper.getLevel((RegistryEntry)((RegistryEntry)SpeedMine.mc.world.getRegistryManager().get(Enchantments.PROTECTION.getRegistryRef()).getEntry(Enchantments.EFFICIENCY).get()), (ItemStack)stack)) + (destroySpeed = stack.getMiningSpeedMultiplier(SpeedMine.mc.world.getBlockState(pos))) > currentFastest)) continue;
            currentFastest = digSpeed + destroySpeed;
            index = i;
        }
        return index >= 36 ? index - 36 : index;
    }

    private boolean canBreak(BlockPos pos) {
        if (SpeedMine.mc.world == null || PlayerUtility.squaredDistanceFromEyes(pos.toCenterPos()) > this.range.getPow2Value()) {
            return false;
        }
        BlockState blockState = SpeedMine.mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getHardness() != -1.0f;
    }

    public boolean isBlockDrop(Entity ent) {
        if (ent instanceof ItemEntity && this.isOn() && ent.age < 3) {
            for (MineAction a : this.actions) {
                if (!(a.getPos().toCenterPos().squaredDistanceTo(ent.getPos()) <= 1.0)) continue;
                return true;
            }
        }
        return false;
    }

    public static enum Mode {
        Packet,
        GrimInstant,
        Damage;

    }

    public static enum StartMode {
        StartAbort,
        StartStop;

    }

    public static enum SwitchMode {
        Silent,
        Normal,
        Alternative;

    }

    public static enum RenderMode {
        Block,
        Shrink,
        Grow;

    }

    public class MineAction {
        @NotNull
        private final BlockPos pos;
        private float progress;
        private float prevProgress;
        private int mineBreaks;
        private final Timer attackTimer = new Timer();

        public MineAction(BlockPos pos, Direction direction) {
            this.pos = pos;
            this.progress = 0.0f;
            this.mineBreaks = 0;
            this.start(direction);
        }

        public void start(Direction direction) {
            Direction startDirection;
            Direction class_23502 = startDirection = direction == null ? Module.mc.player.getHorizontalFacing() : direction;
            if (startDirection != null) {
                if (SpeedMine.this.doubleMine.getValue().booleanValue()) {
                    SpeedMine.this.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, this.pos, startDirection));
                    SpeedMine.this.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, this.pos, startDirection));
                    SpeedMine.this.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, this.pos, startDirection));
                } else {
                    SpeedMine.this.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, this.pos, startDirection));
                    SpeedMine.this.sendPacket((Packet)new PlayerActionC2SPacket(SpeedMine.this.startMode.getValue() == StartMode.StartAbort ? PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK : PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, this.pos, startDirection));
                }
            }
        }

        public boolean update() {
            boolean instant;
            Direction dir = InteractionUtility.getStrictDirections(this.pos).stream().findFirst().orElse(Module.mc.player.getHorizontalFacing());
            if (this.mineBreaks >= SpeedMine.this.breakAttempts.getValue() && SpeedMine.this.mode.not(Mode.GrimInstant)) {
                return true;
            }
            if (PlayerUtility.squaredDistanceFromEyes(this.pos.toCenterPos()) > SpeedMine.this.range.getPow2Value()) {
                this.cancel();
                return true;
            }
            if (Module.mc.world.isAir(this.pos)) {
                this.progress = 0.0f;
                this.prevProgress = -1.0f;
                return false;
            }
            if (this.progress == 0.0f && this.prevProgress == -1.0f && SpeedMine.this.mode.is(Mode.Packet) && this.attackTimer.every(800L)) {
                this.start(dir);
                Module.mc.player.swingHand(Hand.MAIN_HAND);
            }
            int pickSlot = SpeedMine.this.getTool(this.pos);
            int prevSlot = Module.mc.player.getInventory().selectedSlot;
            if (pickSlot == -1) {
                return false;
            }
            boolean bl = instant = this.mineBreaks > 0 && SpeedMine.this.mode.is(Mode.GrimInstant);
            if (this.progress >= 1.0f || instant) {
                int delay;
                this.switchTo(pickSlot, -1);
                if (SpeedMine.this.mode.getValue() == Mode.GrimInstant || SpeedMine.this.doubleMine.getValue().booleanValue()) {
                    SpeedMine.this.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, this.pos, dir));
                } else {
                    if (SpeedMine.this.stop.getValue().booleanValue()) {
                        SpeedMine.this.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, this.pos, dir));
                    }
                    if (SpeedMine.this.abort.getValue().booleanValue()) {
                        SpeedMine.this.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.pos, dir));
                    }
                    if (SpeedMine.this.start.getValue().booleanValue()) {
                        SpeedMine.this.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, this.pos, dir));
                    }
                    if (SpeedMine.this.stop2.getValue().booleanValue()) {
                        SpeedMine.this.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, this.pos, dir));
                    }
                }
                if (SpeedMine.this.clientRemove.getValue().booleanValue()) {
                    Module.mc.interactionManager.breakBlock(this.pos);
                }
                int n = delay = SpeedMine.this.doubleMine.getValue() != false ? 100 : SpeedMine.this.swapDelay.getValue();
                if (delay != 0) {
                    Managers.ASYNC.run(() -> this.switchTo(prevSlot, pickSlot), delay);
                } else {
                    this.switchTo(prevSlot, pickSlot);
                }
                ++this.mineBreaks;
                this.prevProgress = 0.0f;
                this.progress = 0.0f;
                if (SpeedMine.this.doubleMine.getValue().booleanValue() && SpeedMine.this.mode.is(Mode.GrimInstant) && SpeedMine.this.actions.size() >= 2) {
                    return true;
                }
            } else {
                this.prevProgress = this.progress;
                this.progress += SpeedMine.this.getBlockStrength(Module.mc.world.getBlockState(this.pos), this.pos);
            }
            this.fixMovement();
            return false;
        }

        private void switchTo(int slot, int from) {
            if (SpeedMine.this.switchMode.getValue() == SwitchMode.Alternative || slot >= 9) {
                if (from == -1) {
                    Module.clickSlot(slot < 9 ? slot + 36 : slot, Module.mc.player.getInventory().selectedSlot, SlotActionType.SWAP);
                } else {
                    Module.clickSlot(from < 9 ? from + 36 : from, Module.mc.player.getInventory().selectedSlot, SlotActionType.SWAP);
                }
                SpeedMine.this.closeScreen();
            } else if (SpeedMine.this.switchMode.is(SwitchMode.Silent)) {
                InventoryUtility.switchToSilent(slot);
            } else {
                InventoryUtility.switchTo(slot);
            }
        }

        public void fixMovement() {
            if (SpeedMine.this.rotate.getValue().booleanValue() && (double)this.progress > 0.95) {
                ModuleManager.rotations.fixRotation = PlayerManager.calcAngle(Module.mc.player.getEyePos(), this.pos.toCenterPos())[0];
            }
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public float getPrevProgress() {
            return this.prevProgress;
        }

        public float getProgress() {
            return this.progress;
        }

        public void onSync() {
            if (SpeedMine.this.rotate.getValue().booleanValue() && (double)this.progress > 0.95) {
                float[] angle = PlayerManager.calcAngle(Module.mc.player.getEyePos(), this.pos.toCenterPos().add(0.0, -0.25, 0.0));
                Module.mc.player.setYaw(angle[0]);
                Module.mc.player.setPitch(angle[1]);
            }
        }

        public void reset() {
            if (this.progress == 0.0f) {
                return;
            }
            this.progress = 0.0f;
            this.prevProgress = 0.0f;
            Direction dir = InteractionUtility.getStrictDirections(this.pos).stream().findFirst().orElse(Module.mc.player.getHorizontalFacing());
            SpeedMine.this.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.pos, dir));
            this.start(dir);
        }

        public void cancel() {
            if (this.progress != 0.0f) {
                SpeedMine.this.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.pos, Direction.DOWN));
            }
        }

        public boolean instantBreaking() {
            return this.mineBreaks > 0 && SpeedMine.this.mode.is(Mode.GrimInstant);
        }
    }
}

