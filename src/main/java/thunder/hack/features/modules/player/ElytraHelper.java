/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.item.Item
 *  net.minecraft.item.Items
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
 */
package thunder.hack.features.modules.player;

import net.minecraft.util.Hand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.Bind;
import thunder.hack.utility.Timer;
import thunder.hack.utility.player.InteractionUtility;
import thunder.hack.utility.player.InventoryUtility;
import thunder.hack.utility.player.SearchInvResult;

public class ElytraHelper
extends Module {
    private static ElytraHelper INSTANCE = new ElytraHelper();
    private final Setting<Bind> switchButton = new Setting<Bind>("SwitchButton", new Bind(-1, false, false));
    private final Setting<Bind> fireWorkButton = new Setting<Bind>("FireWorkButton", new Bind(-1, false, false));
    private final Setting<FireWorkMode> fireWorkMode = new Setting<FireWorkMode>("FireWorkMode", FireWorkMode.Normal);
    private final Setting<Boolean> autoFirework = new Setting<Boolean>("AutoFirework", false);
    private final Setting<Bind> legitFireworkKey = new Setting<Bind>("Legit Key", new Bind(-1, false, false), v -> this.fireWorkMode.getValue() == FireWorkMode.Legit);
    private final Setting<Boolean> slotReturn = new Setting<Boolean>("SlotReturn", true, v -> this.fireWorkMode.getValue() == FireWorkMode.Normal);
    private final Setting<Integer> delay = new Setting<Integer>("Delay", 100, 1, 1000, v -> this.fireWorkMode.getValue() == FireWorkMode.Normal && this.slotReturn.getValue() != false || this.autoFirework.getValue() != false);
    private final Setting<Boolean> delayBypass = new Setting<Boolean>("Delay-Bypass", false, v -> this.fireWorkMode.getValue() == FireWorkMode.Normal && this.slotReturn.getValue() != false || this.autoFirework.getValue() != false);
    private final Setting<Float> overshoot = new Setting<Float>("Overshoot", Float.valueOf(3.5f), Float.valueOf(3.0f), Float.valueOf(15.0f), v -> this.autoFirework.getValue());
    private final Setting<Boolean> swing = new Setting<Boolean>("Swing", true);
    private final Setting<Boolean> fakeRotation = new Setting<Boolean>("Fake Rotation", false);
    private final Setting<Boolean> predict = new Setting<Boolean>("Predict", false, v -> this.autoFirework.getValue());
    private final Setting<Integer> predictTicks = new Setting<Integer>("Predict Ticks", 2, 1, 5, v -> this.autoFirework.getValue() != false && this.predict.getValue() != false);
    private final Setting<Integer> predictLength = new Setting<Integer>("Predict Length", 2, 1, 20, v -> this.autoFirework.getValue() != false && this.predict.getValue() != false);
    private final Setting<Float> driveBoost = new Setting<Float>("Drive Boost", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.autoFirework.getValue() != false && this.predict.getValue() != false);
    private final Timer switchTimer = new Timer();
    private final Timer fireworkTimer = new Timer();
    private final Timer returnTimer = new Timer();
    private int returnSlot = -1;
    private int returnInvSlot = -1;
    public static boolean swapping = false;
    public static boolean hasUsedFirework = false;
    public static long lastElytraAttackTime = 0L;
    private int bypassDelayOffset = 0;
    private int fakeRotState = 0;
    private int predictTimer = 0;
    private double currentOverrun = 0.0;

    public static ElytraHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ElytraHelper();
        }
        return INSTANCE;
    }

    public ElytraHelper() {
        super("ElytraHelper", Module.Category.PLAYER);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (ElytraHelper.mc.player != null && ElytraHelper.mc.world != null) {
            if (ElytraHelper.mc.currentScreen == null) {
                if (this.switchButton.getValue().getKey() != -1 && this.isKeyPressed(this.switchButton.getValue().getKey())) {
                    this.swapChest(false);
                }
                if (this.fireWorkButton.getValue().getKey() != -1 && this.isKeyPressed(this.fireWorkButton.getValue().getKey()) && ElytraHelper.mc.player.isFallFlying()) {
                    this.useFireWork();
                }
                if (this.legitFireworkKey.getValue().getKey() != -1 && this.isKeyPressed(this.legitFireworkKey.getValue().getKey()) && ElytraHelper.mc.player.isFallFlying()) {
                    this.useFireWork();
                }
            }
            if (!ElytraHelper.mc.player.isFallFlying()) {
                hasUsedFirework = false;
            }
            if (this.autoFirework.getValue().booleanValue() && ElytraHelper.mc.player.isFallFlying() && ElytraHelper.mc.currentScreen == null) {
                float tps = Managers.SERVER.getTPS();
                float tpsFactor = 20.0f / (tps <= 0.0f ? 20.0f : tps);
                long baseDelay = (long)((float)this.delay.getValue().intValue() * tpsFactor);
                long syncedDelay = this.delayBypass.getValue() != false ? baseDelay + (long)this.bypassDelayOffset : baseDelay;
                boolean attackCooldownOk = true;
                Vec3d vel = ElytraHelper.mc.player.getVelocity();
                double horizSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
                if (this.predict.getValue().booleanValue()) {
                    double prevSpeed;
                    double accel;
                    ++this.predictTimer;
                    if (this.predictTimer >= this.predictLength.getValue()) {
                        this.predictTimer = 0;
                        this.currentOverrun = this.calculateOverrun(this.overshoot.getValue().floatValue(), this.driveBoost.getValue().floatValue());
                    }
                    if ((accel = horizSpeed - (prevSpeed = Math.sqrt(Math.pow(ElytraHelper.mc.player.getX() - ElytraHelper.mc.player.prevX, 2.0) + Math.pow(ElytraHelper.mc.player.getZ() - ElytraHelper.mc.player.prevZ, 2.0)))) < 0.0) {
                        horizSpeed += accel * (double)this.predictTicks.getValue().intValue();
                    }
                } else {
                    this.predictTimer = 0;
                    this.currentOverrun = this.overshoot.getValue().floatValue();
                }
                boolean overshootOk = horizSpeed <= this.currentOverrun;
                boolean skipFirework = false;
                if (attackCooldownOk && overshootOk && !skipFirework && this.fireworkTimer.passedMs(syncedDelay)) {
                    this.useFireWork();
                    this.fireworkTimer.reset();
                }
            }
            if (this.returnSlot != -1 && this.returnTimer.passedMs((long)this.delay.getValue().intValue() + (long)(this.delayBypass.getValue() != false ? this.bypassDelayOffset : 0))) {
                if (this.returnInvSlot != -1) {
                    ElytraHelper.mc.interactionManager.clickSlot(ElytraHelper.mc.player.playerScreenHandler.syncId, this.returnInvSlot, ElytraHelper.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)ElytraHelper.mc.player);
                    this.sendPacket((Packet<?>)new CloseHandledScreenC2SPacket(ElytraHelper.mc.player.playerScreenHandler.syncId));
                    this.returnInvSlot = -1;
                }
                this.returnSlot = -1;
            }
        }
    }

    private void advanceBypassState() {
        if (this.delayBypass.getValue().booleanValue()) {
            ++this.bypassDelayOffset;
            if (this.bypassDelayOffset > 100) {
                this.bypassDelayOffset = 0;
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        hasUsedFirework = false;
        this.predictTimer = 0;
        this.currentOverrun = 0.0;
    }

    public void useFireWork() {
        boolean applyFakeRot;
        hasUsedFirework = true;
        SearchInvResult fireWorkResult = InventoryUtility.findItemInInventory(Items.ELYTRA);
        if (!fireWorkResult.found()) {
            this.sendMessage(ClientSettings.isRu() ? "\u0423 \u0442\u0435\u0431\u044f \u043d\u0435\u0442 \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u043e\u0432!" : "You've got no fireworks!");
            return;
        }
        float oldYaw = ElytraHelper.mc.player.getYaw();
        float oldPitch = ElytraHelper.mc.player.getPitch();
        boolean bl = applyFakeRot = this.fakeRotation.getValue() != false && this.swing.getValue() == false;
        if (applyFakeRot) {
            float fireYaw = oldYaw;
            float firePitch = oldPitch;
            switch (this.fakeRotState) {
                case 0: {
                    fireYaw = oldYaw - 25.0f;
                    break;
                }
                case 1: {
                    fireYaw = oldYaw + 25.0f;
                    break;
                }
                case 2: {
                    fireYaw = oldYaw;
                    break;
                }
                case 3: {
                    fireYaw = oldYaw - 25.0f;
                    firePitch = oldPitch - 20.0f;
                    break;
                }
                case 4: {
                    fireYaw = oldYaw + 25.0f;
                    firePitch = oldPitch - 20.0f;
                }
            }
            this.fakeRotState = (this.fakeRotState + 1) % 5;
            ElytraHelper.mc.player.setYaw(fireYaw);
            ElytraHelper.mc.player.setPitch(firePitch);
        }
        if (this.fireWorkMode.getValue() == FireWorkMode.HvH) {
            if (ElytraHelper.mc.player.getOffHandStack().getItem() == Items.ELYTRA) {
                InteractionUtility.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(Hand.OFF_HAND, id, ElytraHelper.mc.player.getYaw(), ElytraHelper.mc.player.getPitch()));
                if (this.swing.getValue().booleanValue()) {
                    ElytraHelper.mc.player.swingHand(Hand.OFF_HAND);
                }
            } else {
                SearchInvResult hotbarFireWorkResult = InventoryUtility.findItemInHotBar(Items.ELYTRA);
                if (hotbarFireWorkResult.found()) {
                    int currentSlot = ElytraHelper.mc.player.getInventory().selectedSlot;
                    int fireworkSlot = hotbarFireWorkResult.slot();
                    mc.getNetworkHandler().sendPacket((Packet)new UpdateSelectedSlotC2SPacket(fireworkSlot));
                    InteractionUtility.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id, ElytraHelper.mc.player.getYaw(), ElytraHelper.mc.player.getPitch()));
                    if (this.swing.getValue().booleanValue()) {
                        ElytraHelper.mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    mc.getNetworkHandler().sendPacket((Packet)new UpdateSelectedSlotC2SPacket(currentSlot));
                } else {
                    int epSlot = fireWorkResult.slot();
                    ElytraHelper.mc.interactionManager.clickSlot(ElytraHelper.mc.player.playerScreenHandler.syncId, epSlot, 40, SlotActionType.SWAP, (PlayerEntity)ElytraHelper.mc.player);
                    InteractionUtility.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(Hand.OFF_HAND, id, ElytraHelper.mc.player.getYaw(), ElytraHelper.mc.player.getPitch()));
                    if (this.swing.getValue().booleanValue()) {
                        ElytraHelper.mc.player.swingHand(Hand.OFF_HAND);
                    }
                    ElytraHelper.mc.interactionManager.clickSlot(ElytraHelper.mc.player.playerScreenHandler.syncId, epSlot, 40, SlotActionType.SWAP, (PlayerEntity)ElytraHelper.mc.player);
                }
            }
        } else if (this.fireWorkMode.getValue() == FireWorkMode.Legit) {
            int currentSlot = ElytraHelper.mc.player.getInventory().selectedSlot;
            int fireworkSlot = fireWorkResult.slot();
            if (fireworkSlot == currentSlot + 36) {
                ElytraHelper.mc.interactionManager.interactItem((PlayerEntity)ElytraHelper.mc.player, Hand.MAIN_HAND);
                if (this.swing.getValue().booleanValue()) {
                    ElytraHelper.mc.player.swingHand(Hand.MAIN_HAND);
                }
            } else {
                ElytraHelper.mc.interactionManager.clickSlot(ElytraHelper.mc.player.playerScreenHandler.syncId, fireworkSlot, currentSlot, SlotActionType.SWAP, (PlayerEntity)ElytraHelper.mc.player);
                InteractionUtility.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id, ElytraHelper.mc.player.getYaw(), ElytraHelper.mc.player.getPitch()));
                if (this.swing.getValue().booleanValue()) {
                    ElytraHelper.mc.player.swingHand(Hand.MAIN_HAND);
                }
                ElytraHelper.mc.interactionManager.clickSlot(ElytraHelper.mc.player.playerScreenHandler.syncId, fireworkSlot, currentSlot, SlotActionType.SWAP, (PlayerEntity)ElytraHelper.mc.player);
            }
        } else {
            SearchInvResult hotbarFireWorkResult = InventoryUtility.findItemInHotBar(Items.ELYTRA);
            if (this.fireWorkMode.getValue() == FireWorkMode.Normal && this.slotReturn.getValue().booleanValue()) {
                this.returnSlot = ElytraHelper.mc.player.getInventory().selectedSlot;
                this.returnInvSlot = hotbarFireWorkResult.found() ? -1 : (fireWorkResult.found() ? fireWorkResult.slot() : -1);
                this.returnTimer.reset();
            }
            InventoryUtility.saveSlot();
            if (hotbarFireWorkResult.found()) {
                hotbarFireWorkResult.switchTo();
            } else {
                ElytraHelper.mc.interactionManager.clickSlot(ElytraHelper.mc.player.playerScreenHandler.syncId, fireWorkResult.slot(), ElytraHelper.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)ElytraHelper.mc.player);
                this.sendPacket((Packet<?>)new CloseHandledScreenC2SPacket(ElytraHelper.mc.player.playerScreenHandler.syncId));
            }
            ElytraHelper.mc.interactionManager.interactItem((PlayerEntity)ElytraHelper.mc.player, Hand.MAIN_HAND);
            if (this.swing.getValue().booleanValue()) {
                ElytraHelper.mc.player.swingHand(Hand.MAIN_HAND);
            }
            if (this.fireWorkMode.getValue() == FireWorkMode.Silent) {
                InventoryUtility.returnSlot();
                if (!hotbarFireWorkResult.found()) {
                    ElytraHelper.mc.interactionManager.clickSlot(ElytraHelper.mc.player.playerScreenHandler.syncId, fireWorkResult.slot(), ElytraHelper.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)ElytraHelper.mc.player);
                    this.sendPacket((Packet<?>)new CloseHandledScreenC2SPacket(ElytraHelper.mc.player.playerScreenHandler.syncId));
                }
            }
        }
        if (applyFakeRot) {
            ElytraHelper.mc.player.setYaw(oldYaw);
            ElytraHelper.mc.player.setPitch(oldPitch);
        }
        if (this.swing.getValue().booleanValue()) {
            this.tryAuraAttackOnFirework();
        }
        this.advanceBypassState();
    }

    private void tryAuraAttackOnFirework() {
        try {
            float range;
            Aura aura = ModuleManager.aura;
            if (aura == null || !aura.isEnabled()) {
                return;
            }
            if (Aura.target == null) {
                return;
            }
            if (!(Aura.target instanceof LivingEntity)) {
                return;
            }
            float dist = ElytraHelper.mc.player.distanceTo(Aura.target);
            if (dist > (range = (aura.elytra.getValue() != false ? aura.elytraAttackRange.getValue() : aura.attackRange.getValue()).floatValue())) {
                return;
            }
            boolean[] state = aura.preAttack();
            aura.attack();
            aura.postAttack(state[0], state[1]);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private double calculateOverrun(float overshootVal, float driveBoostVal) {
        return overshootVal * driveBoostVal;
    }

    public static int getChestPlateSlot() {
        Item[] items = new Item[]{Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.LEATHER_CHESTPLATE};
        for (Item item : items) {
            SearchInvResult slot = InventoryUtility.findItemInInventory(item);
            if (!slot.found()) continue;
            return slot.slot();
        }
        return -1;
    }

    private void swapChest(boolean disable) {
        SearchInvResult result = InventoryUtility.findItemInInventory(Items.FIREWORK_ROCKET);
        if (ElytraHelper.mc.player.getInventory().getStack(38).getItem() == Items.FIREWORK_ROCKET) {
            int slot = ElytraHelper.getChestPlateSlot();
            if (slot == -1) {
                if (disable) {
                    this.disable(ClientSettings.isRu() ? "\u0423 \u0442\u0435\u0431\u044f \u043d\u0435\u0442 \u043d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a\u0430!" : "You don't have a chestplate!");
                } else {
                    this.sendMessage(ClientSettings.isRu() ? "\u0423 \u0442\u0435\u0431\u044f \u043d\u0435\u0442 \u043d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a\u0430!" : "You don't have a chestplate!");
                }
                return;
            }
            ElytraHelper.clickSlot(slot);
            ElytraHelper.clickSlot(6);
            ElytraHelper.clickSlot(slot);
            this.sendPacket((Packet<?>)new CloseHandledScreenC2SPacket(ElytraHelper.mc.player.playerScreenHandler.syncId));
        } else {
            if (!result.found()) {
                if (disable) {
                    this.disable(ClientSettings.isRu() ? "\u0423 \u0442\u0435\u0431\u044f \u043d\u0435\u0442 \u044d\u043b\u0438\u0442\u0440\u044b!" : "You don't have an elytra!");
                } else {
                    this.sendMessage(ClientSettings.isRu() ? "\u0423 \u0442\u0435\u0431\u044f \u043d\u0435\u0442 \u044d\u043b\u0438\u0442\u0440\u044b!" : "You don't have an elytra!");
                }
                return;
            }
            ElytraHelper.clickSlot(result.slot());
            ElytraHelper.clickSlot(6);
            ElytraHelper.clickSlot(result.slot());
            this.sendPacket((Packet<?>)new CloseHandledScreenC2SPacket(ElytraHelper.mc.player.playerScreenHandler.syncId));
        }
        if (disable) {
            this.disable(ClientSettings.isRu() ? "\u0421\u0432\u0430\u043f\u043d\u0443\u043b! \u041e\u0442\u043a\u043b\u044e\u0447\u0430\u044e.." : "Swapped! Disabling..");
        }
    }

    public String getFireWorkMode() {
        return this.fireWorkMode.getValue().name();
    }

    public boolean isSwingEnabled() {
        return this.swing.getValue();
    }

    public enum FireWorkMode {
        Normal, Silent, Legit, HvH;
    }

}

