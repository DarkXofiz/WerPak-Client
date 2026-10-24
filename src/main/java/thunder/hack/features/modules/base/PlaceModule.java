/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity.RemovalReason
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
 *  net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
 */
package thunder.hack.features.modules.base;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Hand;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.impl.SettingGroup;
import thunder.hack.utility.Timer;
import thunder.hack.utility.player.InteractionUtility;
import thunder.hack.utility.player.InventoryUtility;
import thunder.hack.utility.player.PlayerUtility;
import thunder.hack.utility.player.SearchInvResult;
import thunder.hack.utility.render.BlockAnimationUtility;

public abstract class PlaceModule
extends Module {
    protected final Setting<Float> range = new Setting<Float>("Range", Float.valueOf(5.0f), Float.valueOf(0.0f), Float.valueOf(7.0f));
    protected final Setting<InteractionUtility.Interact> interact = new Setting<InteractionUtility.Interact>("Interact", InteractionUtility.Interact.Strict);
    protected final Setting<InteractMode> placeMode = new Setting<InteractMode>("Place Mode", InteractMode.Normal);
    protected final Setting<InteractionUtility.Rotate> rotate = new Setting<InteractionUtility.Rotate>("Rotate", InteractionUtility.Rotate.None);
    protected final Setting<Boolean> swing = new Setting<Boolean>("Swing", false);
    protected final Setting<BooleanSettingGroup> crystalBreaker = new Setting<BooleanSettingGroup>("Crystal Breaker", new BooleanSettingGroup(false));
    protected final Setting<Integer> crystalAge = new Setting<Integer>("CrystalAge", 0, 0, 20).addToGroup(this.crystalBreaker);
    protected final Setting<Integer> breakDelay = new Setting<Integer>("Break Delay", 100, 1, 1000).addToGroup(this.crystalBreaker);
    protected final Setting<Boolean> remove = new Setting<Boolean>("Remove", false).addToGroup(this.crystalBreaker);
    protected final Setting<InteractMode> breakCrystalMode = new Setting<InteractMode>("Break Mode", InteractMode.Normal).addToGroup(this.crystalBreaker);
    protected final Setting<Boolean> antiWeakness = new Setting<Boolean>("Anti Weakness", false).addToGroup(this.crystalBreaker);
    protected final Setting<SettingGroup> blocks = new Setting<SettingGroup>("Blocks", new SettingGroup(false, 0));
    protected final Setting<Boolean> obsidian = new Setting<Boolean>("Obsidian", true).addToGroup(this.blocks);
    protected final Setting<Boolean> anchor = new Setting<Boolean>("Anchor", false).addToGroup(this.blocks);
    protected final Setting<Boolean> enderChest = new Setting<Boolean>("EnderChest", true).addToGroup(this.blocks);
    protected final Setting<Boolean> netherite = new Setting<Boolean>("Netherite", false).addToGroup(this.blocks);
    protected final Setting<Boolean> cryingObsidian = new Setting<Boolean>("Crying Obsidian", true).addToGroup(this.blocks);
    protected final Setting<Boolean> dirt = new Setting<Boolean>("Dirt", false).addToGroup(this.blocks);
    protected final Setting<Boolean> oakPlanks = new Setting<Boolean>("OakPlanks", false).addToGroup(this.blocks);
    protected final Setting<SettingGroup> pause = new Setting<SettingGroup>("Pause", new SettingGroup(false, 0));
    protected final Setting<Boolean> eatPause = new Setting<Boolean>("On Eat", false).addToGroup(this.pause);
    protected final Setting<Boolean> breakPause = new Setting<Boolean>("On Break", false).addToGroup(this.pause);
    protected final Setting<BooleanSettingGroup> render = new Setting<BooleanSettingGroup>("Render", new BooleanSettingGroup(true));
    protected final Setting<BlockAnimationUtility.BlockRenderMode> renderMode = new Setting<BlockAnimationUtility.BlockRenderMode>("Render Mode", BlockAnimationUtility.BlockRenderMode.All).addToGroup(this.render);
    protected final Setting<BlockAnimationUtility.BlockAnimationMode> animationMode = new Setting<BlockAnimationUtility.BlockAnimationMode>("Animation Mode", BlockAnimationUtility.BlockAnimationMode.Fade).addToGroup(this.render);
    protected final Setting<ColorSetting> renderFillColor = new Setting<ColorSetting>("Fill Color", new ColorSetting(HudEditor.getColor(0))).addToGroup(this.render);
    protected final Setting<ColorSetting> renderLineColor = new Setting<ColorSetting>("Line Color", new ColorSetting(HudEditor.getColor(0))).addToGroup(this.render);
    protected final Setting<Integer> renderLineWidth = new Setting<Integer>("Line Width", 2, 1, 5).addToGroup(this.render);
    public final Timer inactivityTimer = new Timer();
    public final Timer pauseTimer = new Timer();
    protected final Timer attackTimer = new Timer();

    public PlaceModule(@NotNull String name, @NotNull Module.Category category) {
        super(name, category);
    }

    protected boolean shouldPause() {
        return this.eatPause.getValue() != false && PlayerUtility.isEating() || this.breakPause.getValue() != false && PlayerUtility.isMining() || !this.pauseTimer.passedMs(350L);
    }

    protected boolean placeBlock(BlockPos pos) {
        return this.placeBlock(pos, this.crystalBreaker.getValue().isEnabled(), this.placeMode.getValue(), this.rotate.getValue());
    }

    protected boolean placeBlock(BlockPos pos, boolean ignoreEntities) {
        return this.placeBlock(pos, ignoreEntities, this.placeMode.getValue(), this.rotate.getValue());
    }

    protected boolean placeBlock(BlockPos pos, InteractionUtility.Rotate rotate) {
        return this.placeBlock(pos, this.crystalBreaker.getValue().isEnabled(), this.placeMode.getValue(), rotate);
    }

    protected boolean placeBlock(BlockPos pos, InteractMode mode) {
        return this.placeBlock(pos, this.crystalBreaker.getValue().isEnabled(), mode, this.rotate.getValue());
    }

    protected boolean placeBlock(BlockPos pos, boolean ignoreEntities, InteractMode mode, InteractionUtility.Rotate rotate) {
        if (this.shouldPause()) {
            return false;
        }
        boolean validInteraction = false;
        SearchInvResult result = this.getBlockResult();
        if (!result.found()) {
            return false;
        }
        if (this.crystalBreaker.getValue().isEnabled() && PlaceModule.mc.world != null && this.attackTimer.passedMs(this.breakDelay.getValue().intValue())) {
            PlaceModule.mc.world.getNonSpectatingEntities(EndCrystalEntity.class, new Box(pos)).stream().findFirst().ifPresent(this::breakCrystal);
        }
        if (mode == InteractMode.Packet) {
            validInteraction = InteractionUtility.placeBlock(pos, rotate, this.interact.getValue(), InteractionUtility.PlaceMode.Packet, result.slot(), true, ignoreEntities);
        }
        if (mode == InteractMode.Normal) {
            validInteraction = InteractionUtility.placeBlock(pos, rotate, this.interact.getValue(), InteractionUtility.PlaceMode.Normal, result.slot(), true, ignoreEntities);
        }
        if (validInteraction && PlaceModule.mc.player != null) {
            if (this.render.getValue().isEnabled()) {
                this.renderBlock(pos);
            }
            if (this.swing.getValue().booleanValue()) {
                PlaceModule.mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
        return validInteraction;
    }

    protected void breakCrystal(EndCrystalEntity entity) {
        if (PlaceModule.mc.player == null || PlaceModule.mc.world == null || PlaceModule.mc.interactionManager == null || this.shouldPause() || !this.attackTimer.passedMs(this.breakDelay.getValue().intValue()) || PlaceModule.mc.player.squaredDistanceTo((Entity)entity) > (double)this.range.getPow2Value() || entity.age < this.crystalAge.getValue()) {
            return;
        }
        int preSlot = PlaceModule.mc.player.getInventory().selectedSlot;
        if (this.antiWeakness.getValue().booleanValue() && PlaceModule.mc.player.hasStatusEffect(StatusEffects.WEAKNESS)) {
            SearchInvResult result = InventoryUtility.getAntiWeaknessItem();
            if (!result.found()) {
                return;
            }
            result.switchTo();
        }
        if (this.breakCrystalMode.getValue() == InteractMode.Packet) {
            this.sendPacket((Packet<?>)PlayerInteractEntityC2SPacket.attack((Entity)entity, (boolean)PlaceModule.mc.player.isSneaking()));
        }
        if (this.breakCrystalMode.getValue() == InteractMode.Normal) {
            PlaceModule.mc.interactionManager.attackEntity((PlayerEntity)PlaceModule.mc.player, (Entity)entity);
        }
        this.sendPacket((Packet<?>)new HandSwingC2SPacket(Hand.MAIN_HAND));
        this.attackTimer.reset();
        if (this.remove.getValue().booleanValue()) {
            entity.kill();
            entity.setRemoved(Entity.RemovalReason.KILLED);
            entity.onRemoved();
        }
        if (this.antiWeakness.getValue().booleanValue() && PlaceModule.mc.player.hasStatusEffect(StatusEffects.WEAKNESS)) {
            InventoryUtility.switchTo(preSlot);
        }
    }

    protected boolean canPlaceBlock(BlockPos pos, boolean ignoreEntities) {
        return InteractionUtility.canPlaceBlock(pos, this.interact.getValue(), ignoreEntities);
    }

    protected void renderBlock(BlockPos pos) {
        BlockAnimationUtility.renderBlock(pos, this.renderLineColor.getValue().getColorObject(), this.renderLineWidth.getValue(), this.renderFillColor.getValue().getColorObject(), this.animationMode.getValue(), this.renderMode.getValue());
    }

    protected SearchInvResult getBlockResult() {
        Block blockFromMainHandItem;
        ItemStack mainHandStack;
        ArrayList<Block> canUseBlocks = new ArrayList<Block>();
        if (PlaceModule.mc.player == null) {
            return SearchInvResult.notFound();
        }
        if (this.obsidian.getValue().booleanValue()) {
            canUseBlocks.add(Blocks.OBSIDIAN);
        }
        if (this.enderChest.getValue().booleanValue()) {
            canUseBlocks.add(Blocks.ENDER_CHEST);
        }
        if (this.cryingObsidian.getValue().booleanValue()) {
            canUseBlocks.add(Blocks.CRYING_OBSIDIAN);
        }
        if (this.netherite.getValue().booleanValue()) {
            canUseBlocks.add(Blocks.NETHERITE_BLOCK);
        }
        if (this.anchor.getValue().booleanValue()) {
            canUseBlocks.add(Blocks.RESPAWN_ANCHOR);
        }
        if (this.dirt.getValue().booleanValue()) {
            canUseBlocks.addAll(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL));
        }
        if (this.oakPlanks.getValue().booleanValue()) {
            canUseBlocks.addAll(List.of(Blocks.OAK_PLANKS, Blocks.BIRCH_PLANKS, Blocks.DARK_OAK_PLANKS));
        }
        if ((mainHandStack = PlaceModule.mc.player.getMainHandStack()) != ItemStack.EMPTY && mainHandStack.getItem() instanceof BlockItem && canUseBlocks.contains(blockFromMainHandItem = ((BlockItem)mainHandStack.getItem()).getBlock())) {
            return new SearchInvResult(PlaceModule.mc.player.getInventory().selectedSlot, true, mainHandStack);
        }
        return InventoryUtility.findBlockInHotBar(canUseBlocks);
    }

    public void pause() {
        this.pauseTimer.reset();
    }

    protected static enum InteractMode {
        Packet,
        Normal;

    }
}

