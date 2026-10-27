/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.item.ElytraItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Position
 *  net.minecraft.block.FluidBlock
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
 *  net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.entity.data.DataTracker.SerializedEntry
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.network.packet.s2c.common.CommonPingS2CPacket
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.client.render.GameRenderer
 */
package thunder.hack.features.modules.movement;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.client.render.GameRenderer;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.EventMove;
import thunder.hack.events.impl.EventSync;
import thunder.hack.events.impl.EventTravel;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.features.modules.combat.Criticals;
import thunder.hack.features.modules.player.ElytraSwap;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.notification.Notification;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.Bind;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.utility.Timer;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.player.InventoryUtility;
import thunder.hack.utility.player.MovementUtility;
import thunder.hack.utility.player.PlayerUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public class ElytraPlus
extends Module {
    public final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.FireWork);
    private final Setting<Integer> disablerDelay = new Setting<Integer>("DisablerDelay", 1, 0, 10, v -> this.mode.is(Mode.SunriseOld));
    private final Setting<Boolean> twoBee = new Setting<Boolean>("2b2t", false, v -> this.mode.is(Mode.Boost));
    private final Setting<Boolean> onlySpace = new Setting<Boolean>("OnlySpace", true, v -> this.mode.is(Mode.Boost) && this.twoBee.getValue() != false);
    private final Setting<Boolean> stopOnGround = new Setting<Boolean>("StopOnGround", false, v -> this.mode.is(Mode.Packet));
    private final Setting<Boolean> infDurability = new Setting<Boolean>("InfDurability", true, v -> this.mode.is(Mode.Packet));
    private final Setting<Boolean> vertical = new Setting<Boolean>("Vertical", false, v -> this.mode.is(Mode.Packet));
    private final Setting<NCPStrict> ncpStrict = new Setting<NCPStrict>("NCPStrict", NCPStrict.Off, v -> this.mode.is(Mode.Packet));
    private final Setting<AntiKick> antiKick = new Setting<AntiKick>("AntiKick", AntiKick.Jitter, v -> this.mode.is(Mode.FireWork) || this.mode.is(Mode.SunriseOld));
    private final Setting<Float> xzSpeed = new Setting<Float>("XZSpeed", Float.valueOf(1.55f), Float.valueOf(0.1f), Float.valueOf(10.0f), v -> !this.mode.is(Mode.Boost) && this.mode.getValue() != Mode.Pitch40Infinite);
    private final Setting<Float> ySpeed = new Setting<Float>("YSpeed", Float.valueOf(0.47f), Float.valueOf(0.0f), Float.valueOf(2.0f), v -> this.mode.is(Mode.FireWork) || this.mode.getValue() == Mode.SunriseOld || this.mode.is(Mode.Packet) && this.vertical.getValue() != false);
    private final Setting<Integer> fireSlot = new Setting<Integer>("FireSlot", 1, 1, 9, v -> this.mode.is(Mode.FireWork));
    private final Setting<BooleanSettingGroup> accelerate = new Setting<BooleanSettingGroup>("Acceleration", new BooleanSettingGroup(false), v -> this.mode.is(Mode.Control) || this.mode.is(Mode.Packet));
    private final Setting<Float> accelerateFactor = new Setting<Float>("AccelerateFactor", Float.valueOf(9.0f), Float.valueOf(0.0f), Float.valueOf(100.0f), v -> this.mode.is(Mode.Control) || this.mode.is(Mode.Packet)).addToGroup(this.accelerate);
    private final Setting<Float> fireDelay = new Setting<Float>("FireDelay", Float.valueOf(1.5f), Float.valueOf(0.0f), Float.valueOf(1.5f), v -> this.mode.is(Mode.FireWork));
    private final Setting<BooleanSettingGroup> grim = new Setting<BooleanSettingGroup>("Grim", new BooleanSettingGroup(false), v -> this.mode.is(Mode.FireWork));
    private final Setting<Boolean> rotate = new Setting<Boolean>("Rotate", true, v -> this.mode.is(Mode.FireWork)).addToGroup(this.grim);
    private final Setting<Boolean> fireWorkExtender = new Setting<Boolean>("FireWorkExtender", true, v -> this.mode.is(Mode.FireWork)).addToGroup(this.grim);
    private final Setting<Boolean> stayMad = new Setting<Boolean>("GroundSafe", false, v -> this.mode.is(Mode.FireWork));
    private final Setting<Boolean> keepFlying = new Setting<Boolean>("KeepFlying", false, v -> this.mode.is(Mode.FireWork));
    private final Setting<Boolean> disableOnFlag = new Setting<Boolean>("DisableOnFlag", false, v -> this.mode.is(Mode.FireWork));
    private final Setting<Boolean> allowFireSwap = new Setting<Boolean>("AllowFireSwap", false, v -> this.mode.is(Mode.FireWork));
    private final Setting<Boolean> bowBomb = new Setting<Boolean>("BowBomb", false, v -> this.mode.is(Mode.FireWork) || this.mode.getValue() == Mode.SunriseOld);
    private final Setting<Bind> bombKey = new Setting<Bind>("BombKey", new Bind(-1, false, false), v -> this.mode.getValue() == Mode.SunriseOld);
    private final Setting<Boolean> instantFly = new Setting<Boolean>("InstantFly", true, v -> this.mode.is(Mode.Boost) && this.twoBee.getValue() == false || this.mode.is(Mode.Control));
    private final Setting<Boolean> cruiseControl = new Setting<Boolean>("CruiseControl", false, v -> this.mode.is(Mode.Boost));
    private final Setting<Float> factor = new Setting<Float>("Factor", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(50.0f), v -> this.mode.is(Mode.Boost));
    private final Setting<Float> upSpeed = new Setting<Float>("UpSpeed", Float.valueOf(1.0f), Float.valueOf(0.01f), Float.valueOf(5.0f), v -> this.mode.is(Mode.Boost) && this.twoBee.getValue() == false || this.mode.is(Mode.Control));
    private final Setting<Float> downFactor = new Setting<Float>("Glide", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(2.0f), v -> this.mode.is(Mode.Boost) && this.twoBee.getValue() == false || this.mode.is(Mode.Control));
    private final Setting<Boolean> stopMotion = new Setting<Boolean>("StopMotion", true, v -> this.mode.is(Mode.Boost) && this.twoBee.getValue() == false);
    private final Setting<Float> minUpSpeed = new Setting<Float>("MinUpSpeed", Float.valueOf(0.5f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.mode.is(Mode.Boost) && this.cruiseControl.getValue() != false);
    private final Setting<Boolean> forceHeight = new Setting<Boolean>("ForceHeight", false, v -> this.mode.is(Mode.Boost) && this.cruiseControl.getValue() != false);
    private final Setting<Integer> manualHeight = new Setting<Integer>("Height", 121, 1, 256, v -> this.mode.is(Mode.Boost) && this.forceHeight.getValue() != false);
    private final Setting<Float> sneakDownSpeed = new Setting<Float>("DownSpeed", Float.valueOf(1.0f), Float.valueOf(0.01f), Float.valueOf(5.0f), v -> this.mode.is(Mode.Control));
    private final Setting<Boolean> speedLimit = new Setting<Boolean>("SpeedLimit", true, v -> this.mode.is(Mode.Boost));
    private final Setting<Float> maxSpeed = new Setting<Float>("MaxSpeed", Float.valueOf(2.5f), Float.valueOf(0.1f), Float.valueOf(10.0f), v -> this.mode.is(Mode.Boost));
    private final Setting<Float> redeployInterval = new Setting<Float>("RedeployInterval", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.mode.is(Mode.Boost) && this.twoBee.getValue() == false);
    private final Setting<Float> redeployTimeOut = new Setting<Float>("RedeployTimeout", Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(20.0f), v -> this.mode.is(Mode.Boost) && this.twoBee.getValue() == false);
    private final Setting<Float> redeployDelay = new Setting<Float>("RedeployDelay", Float.valueOf(0.5f), Float.valueOf(0.1f), Float.valueOf(1.0f), v -> this.mode.is(Mode.Boost) && this.twoBee.getValue() == false);
    private final Setting<Float> infiniteMaxSpeed = new Setting<Float>("InfiniteMaxSpeed", Float.valueOf(150.0f), Float.valueOf(50.0f), Float.valueOf(170.0f), v -> this.mode.getValue() == Mode.Pitch40Infinite);
    private final Setting<Float> infiniteMinSpeed = new Setting<Float>("InfiniteMinSpeed", Float.valueOf(25.0f), Float.valueOf(10.0f), Float.valueOf(70.0f), v -> this.mode.getValue() == Mode.Pitch40Infinite);
    private final Setting<Integer> infiniteMaxHeight = new Setting<Integer>("InfiniteMaxHeight", 200, 50, 360, v -> this.mode.getValue() == Mode.Pitch40Infinite);
    private final Timer startTimer = new Timer();
    private final Timer redeployTimer = new Timer();
    private final Timer strictTimer = new Timer();
    private final Timer pingTimer = new Timer();
    private boolean infiniteFlag;
    private boolean hasTouchedGround;
    private boolean elytraEquiped;
    private boolean flying;
    private boolean started;
    private float acceleration;
    private float accelerationY;
    private float height;
    private float prevClientPitch;
    private float infinitePitch;
    private float lastInfinitePitch;
    private ItemStack prevArmorItemCopy;
    private ItemStack getStackInSlotCopy;
    private Item prevArmorItem = Items.AIR;
    private Item prevItemInHand = Items.AIR;
    private Vec3d flightZonePos;
    private int prevElytraSlot = -1;
    private int disablerTicks;
    private int slotWithFireWorks = -1;
    private long lastFireworkTime;

    public ElytraPlus() {
        super("Elytra+", Module.Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (ElytraPlus.mc.player.getY() < (double)this.infiniteMaxHeight.getValue().intValue() && this.mode.getValue() == Mode.Pitch40Infinite) {
            this.disable(ClientSettings.isRu() ? "\u041f\u043e\u0434\u043d\u0438\u043c\u0438\u0441\u044c \u0432\u044b\u0448\u0435 " + String.valueOf(Formatting.AQUA) + String.valueOf(this.infiniteMaxHeight.getValue()) + String.valueOf(Formatting.GRAY) + " \u0432\u044b\u0441\u043e\u0442\u044b!" : "Go above " + String.valueOf(Formatting.AQUA) + String.valueOf(this.infiniteMaxHeight.getValue()) + String.valueOf(Formatting.GRAY) + " height!");
        }
        this.flying = false;
        this.reset();
        this.infiniteFlag = false;
        this.acceleration = 0.0f;
        this.accelerationY = 0.0f;
        if (ElytraPlus.mc.player != null) {
            this.height = (float)ElytraPlus.mc.player.getY();
        }
        this.pingTimer.reset();
        if (this.mode.is(Mode.FireWork)) {
            this.fireworkOnEnable();
        }
    }

    @EventHandler
    public void modifyVelocity(EventTravel e) {
        if (this.mode.getValue() == Mode.Pitch40Infinite) {
            if (e.isPre()) {
                this.prevClientPitch = ElytraPlus.mc.player.getPitch();
                ElytraPlus.mc.player.setPitch(this.lastInfinitePitch);
            } else {
                ElytraPlus.mc.player.setPitch(this.prevClientPitch);
            }
        }
        if (this.mode.is(Mode.FireWork) && Managers.PLAYER.ticksElytraFlying < 4) {
            if (e.isPre()) {
                this.prevClientPitch = ElytraPlus.mc.player.getPitch();
                ElytraPlus.mc.player.setPitch(-45.0f);
            } else {
                ElytraPlus.mc.player.setPitch(this.prevClientPitch);
            }
        }
        if (this.mode.getValue() == Mode.SunriseNew) {
            if (ElytraPlus.mc.options.jumpKey.isPressed()) {
                if (e.isPre()) {
                    this.prevClientPitch = ElytraPlus.mc.player.getPitch();
                    ElytraPlus.mc.player.setPitch(-45.0f);
                } else {
                    ElytraPlus.mc.player.setPitch(this.prevClientPitch);
                }
            } else if (ElytraPlus.mc.options.sneakKey.isPressed()) {
                if (e.isPre()) {
                    this.prevClientPitch = ElytraPlus.mc.player.getPitch();
                    ElytraPlus.mc.player.setPitch(45.0f);
                } else {
                    ElytraPlus.mc.player.setPitch(this.prevClientPitch);
                }
            }
        }
    }

    @EventHandler
    public void onSync(EventSync e) {
        switch (this.mode.getValue().ordinal()) {
            case 1: {
                this.doSunrise();
                break;
            }
            case 5: {
                this.doSunriseNew();
                break;
            }
            case 2: 
            case 3: {
                this.doPreLegacy();
                break;
            }
            case 0: {
                this.fireworkOnSync();
                break;
            }
            case 4: {
                this.doPitch40Infinite();
                break;
            }
            case 6: {
                this.doPacket(e);
            }
        }
    }

    private void doPacket(EventSync e) {
        if (!(this.isBoxCollidingGround() && this.stopOnGround.getValue().booleanValue() || ElytraPlus.mc.player.getInventory().getStack(38).getItem() != Items.ELYTRA)) {
            if (this.infDurability.getValue().booleanValue() || !ElytraPlus.mc.player.isFallFlying()) {
                this.sendPacket((Packet<?>)new ClientCommandC2SPacket((Entity)ElytraPlus.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
            if (ElytraPlus.mc.player.age % 3 != 0 && this.ncpStrict.is(NCPStrict.Motion)) {
                e.cancel();
            }
        }
    }

    private void doPitch40Infinite() {
        ItemStack is = ElytraPlus.mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (is.isOf(Items.ELYTRA)) {
            ElytraPlus.mc.player.setPitch(this.lastInfinitePitch);
            if (is.getDamage() > 380 && ElytraPlus.mc.player.age % 100 == 0) {
                Managers.NOTIFICATION.publicity("Elytra+", ClientSettings.isRu() ? "\u042d\u043b\u0438\u0442\u0440\u0430 \u0441\u043a\u043e\u0440\u043e \u0441\u043b\u043e\u043c\u0430\u0435\u0442\u0441\u044f!" : "Elytra's about to break!", 2, Notification.Type.WARNING);
                ElytraPlus.mc.world.playSound((PlayerEntity)ElytraPlus.mc.player, ElytraPlus.mc.player.getX(), ElytraPlus.mc.player.getY(), ElytraPlus.mc.player.getZ(), (RegistryEntry)SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT, 10.0f, 1.0f, 0L);
            }
        }
    }

    private void doSunriseNew() {
        int elytra;
        if (ElytraPlus.mc.player.horizontalCollision) {
            this.acceleration = 0.0f;
        }
        if ((elytra = InventoryUtility.getElytra()) == -1) {
            return;
        }
        if (ElytraPlus.mc.player.isOnGround()) {
            ElytraPlus.mc.player.jump();
            this.acceleration = 0.0f;
            return;
        }
        if (ElytraPlus.mc.player.fallDistance <= 0.0f) {
            return;
        }
        if (ElytraPlus.mc.options.jumpKey.isPressed() || ElytraPlus.mc.options.sneakKey.isPressed()) {
            this.acceleration = 0.0f;
            this.takeOnElytra();
        } else {
            this.takeOnChestPlate();
            if (ElytraPlus.mc.player.age % 8 == 0) {
                this.matrixDisabler(elytra);
            }
            MovementUtility.setMotion(Math.min((this.acceleration += 8.0f / this.xzSpeed.getValue().floatValue()) / 100.0f, this.xzSpeed.getValue().floatValue()));
            if (!MovementUtility.isMoving()) {
                this.acceleration = 0.0f;
            }
            ElytraPlus.mc.player.setVelocity(ElytraPlus.mc.player.getVelocity().getX(), (double)-0.005f, ElytraPlus.mc.player.getVelocity().getZ());
        }
    }

    private void takeOnElytra() {
        int elytra = InventoryUtility.getElytra();
        if (elytra == -1) {
            return;
        }
        int n = elytra = elytra >= 0 && elytra < 9 ? elytra + 36 : elytra;
        if (elytra != -2) {
            ElytraPlus.clickSlot(elytra);
            ElytraPlus.clickSlot(6);
            ElytraPlus.clickSlot(elytra);
            ElytraPlus.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)ElytraPlus.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
    }

    private void takeOnChestPlate() {
        int slot = ElytraSwap.getChestPlateSlot();
        if (slot == -1) {
            return;
        }
        if (slot != -2) {
            ElytraPlus.clickSlot(slot);
            ElytraPlus.clickSlot(6);
            ElytraPlus.clickSlot(slot);
        }
    }

    private float getInfinitePitch() {
        if (ElytraPlus.mc.player.getY() < (double)this.infiniteMaxHeight.getValue().intValue()) {
            if (Managers.PLAYER.currentPlayerSpeed * 72.0f < this.infiniteMinSpeed.getValue().floatValue() && !this.infiniteFlag) {
                this.infiniteFlag = true;
            }
            if (Managers.PLAYER.currentPlayerSpeed * 72.0f > this.infiniteMaxSpeed.getValue().floatValue() && this.infiniteFlag) {
                this.infiniteFlag = false;
            }
        } else {
            this.infiniteFlag = true;
        }
        this.infinitePitch = this.infiniteFlag ? (this.infinitePitch += 3.0f) : (this.infinitePitch -= 3.0f);
        this.infinitePitch = MathUtility.clamp(this.infinitePitch, -40.0f, 40.0f);
        return this.infinitePitch;
    }

    @Override
    public void onDisable() {
        ThunderHack.TICK_TIMER = 1.0f;
        ElytraPlus.mc.player.getAbilities().flying = false;
        ElytraPlus.mc.player.getAbilities().setFlySpeed(0.05f);
        if (this.mode.is(Mode.FireWork)) {
            this.fireworkOnDisable();
        }
    }

    @EventHandler(priority=-200)
    public void onMove(EventMove e) {
        switch (this.mode.getValue().ordinal()) {
            case 2: {
                this.doBoost(e);
                break;
            }
            case 3: {
                this.doControl(e);
                break;
            }
            case 0: {
                this.fireworkOnMove(e);
                break;
            }
            case 6: {
                this.doMotionPacket(e);
            }
        }
    }

    @EventHandler
    public void onPacketSend(PacketEvent.SendPost event) {
        if (ElytraPlus.fullNullCheck()) {
            return;
        }
        Object t = event.getPacket();
        if (t instanceof ClientCommandC2SPacket) {
            ClientCommandC2SPacket command = (ClientCommandC2SPacket)t;
            if (this.mode.is(Mode.FireWork) && command.getMode() == ClientCommandC2SPacket.Mode.START_FALL_FLYING) {
                this.doFireWork(false);
            }
        }
        if ((t = event.getPacket()) instanceof PlayerInteractEntityC2SPacket) {
            PlayerInteractEntityC2SPacket p = (PlayerInteractEntityC2SPacket)t;
            if (this.mode.is(Mode.FireWork) && this.grim.getValue().isEnabled() && this.fireWorkExtender.getValue().booleanValue() && this.flying && this.flightZonePos != null && (float)Criticals.getEntity((PlayerInteractEntityC2SPacket)p).age < (float)this.pingTimer.getPassedTimeMs() / 50.0f) {
                this.sendMessage(String.valueOf(Formatting.RED) + (ClientSettings.isRu() ? "\u0412 \u044d\u0442\u043e\u043c \u0440\u0435\u0436\u0438\u043c\u0435 \u043d\u0435\u043b\u044c\u0437\u044f \u0431\u0438\u0442\u044c \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439 \u043a\u043e\u0442\u043e\u0440\u044b\u0435 \u043f\u043e\u044f\u0432\u0438\u043b\u0438\u0441\u044c \u043f\u043e\u0441\u043b\u0435 \u0432\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u044f \u043c\u043e\u0434\u0443\u043b\u044f!" : "In this mode, you cannot hit entities that spawned after the module was turned on!"));
            }
        }
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive e) {
        EntityTrackerUpdateS2CPacket pac;
        Object t = e.getPacket();
        if (t instanceof EntityTrackerUpdateS2CPacket && (pac = (EntityTrackerUpdateS2CPacket)t).id() == ElytraPlus.mc.player.getId() && (this.mode.is(Mode.Packet) || this.mode.is(Mode.SunriseOld))) {
            var values2 = pac.trackedValues();
            if (values2.isEmpty()) {
                return;
            }
            for (DataTracker.SerializedEntry value : values2) {
                if (!value.value().toString().equals("FALL_FLYING") && (value.id() != 0 || !value.value().toString().equals("-120") && !value.value().toString().equals("-128") && !value.value().toString().equals("-126"))) continue;
                e.cancel();
            }
        }
        if (e.getPacket() instanceof PlayerPositionLookS2CPacket) {
            this.acceleration = 0.0f;
            this.accelerationY = 0.0f;
            this.pingTimer.reset();
            if (this.disableOnFlag.getValue().booleanValue() && this.mode.is(Mode.FireWork)) {
                this.disable(ClientSettings.isRu() ? "\u0412\u044b\u043a\u043b\u044e\u0447\u0435\u043d \u0438\u0437-\u0437\u0430 \u0444\u043b\u0430\u0433\u0430!" : "Disabled due to flag!");
            }
        }
        if (e.getPacket() instanceof CommonPingS2CPacket && this.mode.is(Mode.FireWork) && this.grim.getValue().isEnabled() && this.fireWorkExtender.getValue().booleanValue() && this.flying) {
            if (!this.pingTimer.passedMs(50000L)) {
                if (this.pingTimer.passedMs(1000L) && PlayerUtility.getSquaredDistance2D(this.flightZonePos) < 7000.0f) {
                    e.cancel();
                }
            } else {
                this.pingTimer.reset();
            }
        }
    }

    @EventHandler
    public void onPlayerUpdate(PlayerUpdateEvent e) {
        switch (this.mode.getValue().ordinal()) {
            case 0: {
                this.fireWorkOnPlayerUpdate();
                break;
            }
            case 4: {
                this.lastInfinitePitch = PlayerUtility.fixAngle(this.getInfinitePitch());
            }
        }
    }

    private void doMotionPacket(EventMove e) {
        ElytraPlus.mc.player.getAbilities().flying = false;
        ElytraPlus.mc.player.getAbilities().setFlySpeed(0.05f);
        if (this.isBoxCollidingGround() && this.stopOnGround.getValue().booleanValue() || ElytraPlus.mc.player.getInventory().getStack(38).getItem() != Items.ELYTRA) {
            return;
        }
        ElytraPlus.mc.player.getAbilities().flying = true;
        ElytraPlus.mc.player.getAbilities().setFlySpeed(this.xzSpeed.getValue().floatValue() / 15.0f * (this.accelerate.getValue().isEnabled() ? Math.min((this.acceleration += this.accelerateFactor.getValue().floatValue()) / 100.0f, 1.0f) : 1.0f));
        e.cancel();
        if (ElytraPlus.mc.player.age % 3 == 0 && this.ncpStrict.is(NCPStrict.Motion)) {
            e.setY(0.0);
            e.setX(0.0);
            e.setZ(0.0);
            return;
        }
        if (Math.abs(e.getX()) < 0.05) {
            e.setX(0.0);
        }
        if (Math.abs(e.getZ()) < 0.05) {
            e.setZ(0.0);
        }
        e.setY(this.vertical.getValue().booleanValue() ? (ElytraPlus.mc.options.jumpKey.isPressed() ? (double)this.ySpeed.getValue().floatValue() : (ElytraPlus.mc.options.sneakKey.isPressed() ? (double)(-this.ySpeed.getValue().floatValue()) : 0.0)) : 0.0);
        switch (this.ncpStrict.getValue().ordinal()) {
            case 2: {
                e.setY(-1.000088900582341E-12);
                break;
            }
            case 3: {
                e.setY(-4.000355602329364E-12);
                break;
            }
            case 1: {
                e.setY(2.0E-4 - (ElytraPlus.mc.player.age % 2 == 0 ? 0.0 : 1.0E-6) + MathUtility.random(0.0, 9.0E-7));
            }
        }
        if (ElytraPlus.mc.player.horizontalCollision && (this.ncpStrict.is(NCPStrict.New) || this.ncpStrict.is(NCPStrict.Motion)) && ElytraPlus.mc.player.age % 2 == 0) {
            e.setY(-0.07840000152587923);
        }
        if ((this.infDurability.getValue().booleanValue() || this.ncpStrict.is(NCPStrict.Motion)) && !MovementUtility.isMoving() && Math.abs(e.getX()) < 0.121) {
            float angleToRad = (float)Math.toRadians(4.5 * (double)(ElytraPlus.mc.player.age % 80));
            e.setX(Math.sin(angleToRad) * 0.12);
            e.setZ(Math.cos(angleToRad) * 0.12);
        }
    }

    private void doPreLegacy() {
        if (this.twoBee.getValue().booleanValue() && this.mode.is(Mode.Boost)) {
            return;
        }
        if (ElytraPlus.mc.player.isOnGround()) {
            this.hasTouchedGround = true;
        }
        if (!this.cruiseControl.getValue().booleanValue()) {
            this.height = (float)ElytraPlus.mc.player.getY();
        }
        if (this.strictTimer.passedMs(1500L) && !this.strictTimer.passedMs(2000L)) {
            ThunderHack.TICK_TIMER = 1.0f;
        }
        if (!ElytraPlus.mc.player.isFallFlying()) {
            if (this.hasTouchedGround && !ElytraPlus.mc.player.isOnGround() && ElytraPlus.mc.player.fallDistance > 0.0f && this.instantFly.getValue().booleanValue()) {
                ThunderHack.TICK_TIMER = 0.3f;
            }
            if (!ElytraPlus.mc.player.isOnGround() && this.instantFly.getValue().booleanValue() && ElytraPlus.mc.player.getVelocity().getY() < 0.0) {
                if (!this.startTimer.passedMs((long)(1000.0f * this.redeployDelay.getValue().floatValue()))) {
                    return;
                }
                this.startTimer.reset();
                this.sendPacket((Packet<?>)new ClientCommandC2SPacket((Entity)ElytraPlus.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                this.hasTouchedGround = false;
                this.strictTimer.reset();
            }
        }
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        if (this.mode.is(Mode.FireWork) && this.grim.getValue().isEnabled() && this.fireWorkExtender.getValue().booleanValue() && this.flying && this.flightZonePos != null) {
            stack.push();
            Render3DEngine.setupRender();
            RenderSystem.disableCull();
            Tessellator tessellator = Tessellator.getInstance();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            for (int i = 0; i <= 30; ++i) {
                float cos = (float)(this.flightZonePos.getX() - ElytraPlus.mc.getEntityRenderDispatcher().camera.getPos().getX() + Math.cos((double)i * (Math.PI * 2) / 30.0) * 95.0);
                float sin = (float)(this.flightZonePos.getZ() - ElytraPlus.mc.getEntityRenderDispatcher().camera.getPos().getZ() + Math.sin((double)i * (Math.PI * 2) / 30.0) * 95.0);
                bufferBuilder.vertex(stack.peek().getPositionMatrix(), cos, (float)(-ElytraPlus.mc.getEntityRenderDispatcher().camera.getPos().getY()), sin).color(Render2DEngine.injectAlpha(HudEditor.getColor(i), 255).getRGB());
                bufferBuilder.vertex(stack.peek().getPositionMatrix(), cos, (float)(128.0 - ElytraPlus.mc.getEntityRenderDispatcher().camera.getPos().getY()), sin).color(Render2DEngine.injectAlpha(HudEditor.getColor(i), 0).getRGB());
            }
            Render2DEngine.endBuilding(bufferBuilder);
            RenderSystem.enableCull();
            Render3DEngine.endRender();
            stack.pop();
        }
    }

    @Override
    public void onRender2D(DrawContext context) {
        if (this.mode.is(Mode.FireWork) && this.grim.getValue().isEnabled() && this.fireWorkExtender.getValue().booleanValue() && this.flying && !this.pingTimer.passedMs(50000L) && this.pingTimer.passedMs(1000L)) {
            int timeS = (int)MathUtility.round2((float)(50000L - this.pingTimer.getPassedTimeMs()) / 1000.0f);
            int dist = (int)(83.0 - Math.sqrt(PlayerUtility.getSquaredDistance2D(this.flightZonePos)));
            FontRenderers.sf_bold.drawCenteredString(context.getMatrices(), ClientSettings.isRu() ? "\u041e\u0441\u0442\u0430\u043b\u043e\u0441\u044c " + timeS + " \u0441\u0435\u043a\u0443\u043d\u0434 \u0438 " + dist + " \u043c\u0435\u0442\u0440\u043e\u0432" : timeS + " seconds and " + dist + " meters left", (double)((float)mc.getWindow().getScaledWidth() / 2.0f), (double)((float)mc.getWindow().getScaledHeight() / 2.0f + 30.0f), -1);
        }
    }

    private void doSunrise() {
        int elytra;
        if (ElytraPlus.mc.player.horizontalCollision) {
            this.acceleration = 0.0f;
        }
        if (ElytraPlus.mc.player.verticalCollision) {
            this.acceleration = 0.0f;
            ElytraPlus.mc.player.setVelocity(ElytraPlus.mc.player.getVelocity().getX(), (double)0.42f, ElytraPlus.mc.player.getVelocity().getZ());
        }
        if ((elytra = InventoryUtility.getElytra()) == -1) {
            return;
        }
        if (ElytraPlus.mc.player.isOnGround()) {
            ElytraPlus.mc.player.jump();
        }
        if (this.disablerTicks-- <= 0) {
            this.matrixDisabler(elytra);
        }
        if (ElytraPlus.mc.player.fallDistance > 0.25f) {
            MovementUtility.setMotion(Math.min((this.acceleration += 11.0f / this.xzSpeed.getValue().floatValue()) / 100.0f, this.xzSpeed.getValue().floatValue()));
            if (!MovementUtility.isMoving()) {
                this.acceleration = 0.0f;
            }
            if (InputUtil.isKeyPressed((long)mc.getWindow().getHandle(), (int)this.bombKey.getValue().getKey())) {
                MovementUtility.setMotion(0.8f);
                ElytraPlus.mc.player.setVelocity(ElytraPlus.mc.player.getVelocity().getX(), ElytraPlus.mc.player.age % 2 == 0 ? (double)0.42f : (double)-0.42f, ElytraPlus.mc.player.getVelocity().getZ());
                this.acceleration = 70.0f;
            } else {
                switch (this.antiKick.getValue().ordinal()) {
                    case 1: {
                        ElytraPlus.mc.player.setVelocity(ElytraPlus.mc.player.getVelocity().getX(), ElytraPlus.mc.player.age % 2 == 0 ? 0.08 : -0.08, ElytraPlus.mc.player.getVelocity().getZ());
                        break;
                    }
                    case 2: {
                        ElytraPlus.mc.player.setVelocity(ElytraPlus.mc.player.getVelocity().getX(), (double)(-0.01f - (ElytraPlus.mc.player.age % 2 == 0 ? 1.0E-4f : 0.006f)), ElytraPlus.mc.player.getVelocity().getZ());
                        break;
                    }
                    case 0: {
                        ElytraPlus.mc.player.setVelocity(ElytraPlus.mc.player.getVelocity().getX(), 0.0, ElytraPlus.mc.player.getVelocity().getZ());
                    }
                }
            }
            if (!ElytraPlus.mc.player.isSneaking() && ElytraPlus.mc.options.jumpKey.isPressed()) {
                ElytraPlus.mc.player.setVelocity(ElytraPlus.mc.player.getVelocity().getX(), (double)this.ySpeed.getValue().floatValue(), ElytraPlus.mc.player.getVelocity().getZ());
            }
            if (ElytraPlus.mc.options.sneakKey.isPressed()) {
                ElytraPlus.mc.player.setVelocity(ElytraPlus.mc.player.getVelocity().getX(), (double)(-this.ySpeed.getValue().floatValue()), ElytraPlus.mc.player.getVelocity().getZ());
            }
        }
    }

    private void doBoost(EventMove e) {
        if (ElytraPlus.mc.player.getInventory().getStack(38).getItem() != Items.ELYTRA || !ElytraPlus.mc.player.isFallFlying() || ElytraPlus.mc.player.isTouchingWater() || ElytraPlus.mc.player.isInLava() || !ElytraPlus.mc.player.isFallFlying()) {
            return;
        }
        float moveForward = ElytraPlus.mc.player.input.movementForward;
        if (this.cruiseControl.getValue().booleanValue()) {
            if (ElytraPlus.mc.options.jumpKey.isPressed()) {
                this.height += 1.0f;
            } else if (ElytraPlus.mc.options.sneakKey.isPressed()) {
                this.height -= 1.0f;
            }
            if (this.forceHeight.getValue().booleanValue()) {
                this.height = this.manualHeight.getValue().intValue();
            }
            if (this.twoBee.getValue().booleanValue()) {
                if (Managers.PLAYER.currentPlayerSpeed >= this.minUpSpeed.getValue().floatValue()) {
                    ElytraPlus.mc.player.setPitch((float)MathHelper.clamp((double)MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(((double)this.height - ElytraPlus.mc.player.getY()) * -1.0, 10.0))), (double)-50.0, (double)50.0));
                } else {
                    ElytraPlus.mc.player.setPitch(0.25f);
                }
            } else {
                double heightPct = 1.0 - Math.sqrt(MathHelper.clamp((double)((double)Managers.PLAYER.currentPlayerSpeed / 1.7), (double)0.0, (double)1.0));
                if (Managers.PLAYER.currentPlayerSpeed >= this.minUpSpeed.getValue().floatValue() && this.startTimer.passedMs((long)(2000.0f * this.redeployInterval.getValue().floatValue()))) {
                    double pitch = -(44.4 * heightPct + 0.6);
                    double diff = ((double)(this.height + 1.0f) - ElytraPlus.mc.player.getY()) * 2.0;
                    double pDist = -Math.toDegrees(Math.atan2(Math.abs(diff), (double)Managers.PLAYER.currentPlayerSpeed * 30.0)) * Math.signum(diff);
                    ElytraPlus.mc.player.setPitch((float)(pitch + (pDist - pitch) * MathHelper.clamp((double)Math.abs(diff), (double)0.0, (double)1.0)));
                } else {
                    ElytraPlus.mc.player.setPitch(0.25f);
                    moveForward = 1.0f;
                }
            }
        }
        if (this.twoBee.getValue().booleanValue()) {
            if (ElytraPlus.mc.options.jumpKey.isPressed() || !this.onlySpace.getValue().booleanValue() || this.cruiseControl.getValue().booleanValue()) {
                double[] m = MovementUtility.forwardWithoutStrafe(this.factor.getValue().floatValue() / 10.0f);
                e.setX(e.getX() + m[0]);
                e.setZ(e.getZ() + m[1]);
            }
        } else {
            double ySpeed;
            Vec3d rotationVec = ElytraPlus.mc.player.getRotationVec(Render3DEngine.getTickDelta());
            double d6 = Math.hypot(rotationVec.x, rotationVec.z);
            double currentSpeed = Math.hypot(e.getX(), e.getZ());
            float f4 = (float)(Math.pow(Math.cos(Math.toRadians(ElytraPlus.mc.player.getPitch())), 2.0) * Math.min(1.0, rotationVec.length() / 0.4));
            e.setY(e.getY() + (-0.08 + (double)f4 * 0.06));
            if (e.getY() < 0.0 && d6 > 0.0) {
                ySpeed = e.getY() * -0.1 * (double)f4;
                e.setY(e.getY() + ySpeed);
                e.setX(e.getX() + rotationVec.x * ySpeed / d6);
                e.setZ(e.getZ() + rotationVec.z * ySpeed / d6);
            }
            if (ElytraPlus.mc.player.getPitch() < 0.0f) {
                ySpeed = currentSpeed * -Math.sin(Math.toRadians(ElytraPlus.mc.player.getPitch())) * 0.04;
                e.setY(e.getY() + ySpeed * 3.2);
                e.setX(e.getX() - rotationVec.x * ySpeed / d6);
                e.setZ(e.getZ() - rotationVec.z * ySpeed / d6);
            }
            if (d6 > 0.0) {
                e.setX(e.getX() + (rotationVec.x / d6 * currentSpeed - e.getX()) * 0.1);
                e.setZ(e.getZ() + (rotationVec.z / d6 * currentSpeed - e.getZ()) * 0.1);
            }
            if (ElytraPlus.mc.player.getPitch() > 0.0f && e.getY() < 0.0) {
                if (moveForward != 0.0f && this.startTimer.passedMs((long)(2000.0f * this.redeployInterval.getValue().floatValue())) && this.redeployTimer.passedMs((long)(1000.0f * this.redeployTimeOut.getValue().floatValue()))) {
                    if (this.stopMotion.getValue().booleanValue()) {
                        e.setX(0.0);
                        e.setZ(0.0);
                    }
                    this.startTimer.reset();
                    this.sendPacket((Packet<?>)new ClientCommandC2SPacket((Entity)ElytraPlus.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                } else if (!this.startTimer.passedMs((long)(2000.0f * this.redeployInterval.getValue().floatValue()))) {
                    e.setX(e.getX() - (double)moveForward * Math.sin(Math.toRadians(ElytraPlus.mc.player.getYaw())) * (double)this.factor.getValue().floatValue() / 20.0);
                    e.setZ(e.getZ() + (double)moveForward * Math.cos(Math.toRadians(ElytraPlus.mc.player.getYaw())) * (double)this.factor.getValue().floatValue() / 20.0);
                    this.redeployTimer.reset();
                }
            }
        }
        double speed = Math.hypot(e.getX(), e.getZ());
        if (this.speedLimit.getValue().booleanValue() && speed > (double)this.maxSpeed.getValue().floatValue()) {
            e.setX(e.getX() * (double)this.maxSpeed.getValue().floatValue() / speed);
            e.setZ(e.getZ() * (double)this.maxSpeed.getValue().floatValue() / speed);
        }
        ElytraPlus.mc.player.setVelocity(e.getX(), e.getY(), e.getZ());
        e.cancel();
    }

    private void doControl(EventMove e) {
        if (ElytraPlus.mc.player.getInventory().getStack(38).getItem() != Items.ELYTRA || !ElytraPlus.mc.player.isFallFlying()) {
            return;
        }
        double[] dir = MovementUtility.forward(this.xzSpeed.getValue().floatValue() * (this.accelerate.getValue().isEnabled() ? Math.min((this.acceleration += this.accelerateFactor.getValue().floatValue()) / 100.0f, 1.0f) : 1.0f));
        e.setX(dir[0]);
        e.setY(ElytraPlus.mc.options.jumpKey.isPressed() ? (double)this.upSpeed.getValue().floatValue() : (ElytraPlus.mc.options.sneakKey.isPressed() ? (double)(-this.sneakDownSpeed.getValue().floatValue()) : -0.08 * (double)this.downFactor.getValue().floatValue()));
        e.setZ(dir[1]);
        if (!MovementUtility.isMoving()) {
            this.acceleration = 0.0f;
        }
        ElytraPlus.mc.player.setVelocity(e.getX(), e.getY(), e.getZ());
        e.cancel();
    }

    public void matrixDisabler(int elytra) {
        int n = elytra = elytra >= 0 && elytra < 9 ? elytra + 36 : elytra;
        if (elytra != -2) {
            ElytraPlus.mc.interactionManager.clickSlot(ElytraPlus.mc.player.currentScreenHandler.syncId, elytra, 1, SlotActionType.PICKUP, (PlayerEntity)ElytraPlus.mc.player);
            ElytraPlus.mc.interactionManager.clickSlot(ElytraPlus.mc.player.currentScreenHandler.syncId, 6, 1, SlotActionType.PICKUP, (PlayerEntity)ElytraPlus.mc.player);
        }
        ElytraPlus.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)ElytraPlus.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        if (elytra != -2) {
            ElytraPlus.mc.interactionManager.clickSlot(ElytraPlus.mc.player.currentScreenHandler.syncId, 6, 1, SlotActionType.PICKUP, (PlayerEntity)ElytraPlus.mc.player);
            ElytraPlus.mc.interactionManager.clickSlot(ElytraPlus.mc.player.currentScreenHandler.syncId, elytra, 1, SlotActionType.PICKUP, (PlayerEntity)ElytraPlus.mc.player);
        }
        this.disablerTicks = this.disablerDelay.getValue();
    }

    private int getFireWorks(boolean hotbar) {
        if (hotbar) {
            return InventoryUtility.findItemInHotBar(Items.FIREWORK_ROCKET).slot();
        }
        return InventoryUtility.findItemInInventory(Items.FIREWORK_ROCKET).slot();
    }

    private void noFireworks() {
        this.disable(ClientSettings.isRu() ? "\u041d\u0435\u0442\u0443 \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u043e\u0432 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435!" : "No fireworks in the hotbar!");
        this.flying = false;
    }

    private void noElytra() {
        this.disable(ClientSettings.isRu() ? "\u041d\u0435\u0442\u0443 \u044d\u043b\u0438\u0442\u0440 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435!" : "No elytras found in the inventory!");
        this.flying = false;
    }

    private void reset() {
        this.slotWithFireWorks = -1;
        this.prevItemInHand = Items.AIR;
        this.getStackInSlotCopy = null;
    }

    private void resetPrevItems() {
        this.prevElytraSlot = -1;
        this.prevArmorItem = Items.AIR;
        this.prevArmorItemCopy = null;
    }

    private void moveFireworksToHotbar(int n2) {
        ElytraPlus.clickSlot(n2);
        ElytraPlus.clickSlot(this.fireSlot.getValue() - 1 + 36);
        ElytraPlus.clickSlot(n2);
    }

    private void returnItem() {
        if (this.slotWithFireWorks == -1 || this.getStackInSlotCopy == null || this.prevItemInHand == Items.FIREWORK_ROCKET || this.prevItemInHand == Items.AIR) {
            return;
        }
        int n2 = ElytraPlus.findInInventory(this.getStackInSlotCopy, this.prevItemInHand);
        n2 = n2 < 9 && n2 != -1 ? n2 + 36 : n2;
        ElytraPlus.clickSlot(n2);
        ElytraPlus.clickSlot(this.fireSlot.getValue() - 1 + 36);
        ElytraPlus.clickSlot(n2);
    }

    public static int findInInventory(ItemStack stack, Item item) {
        if (stack == null) {
            return -1;
        }
        for (int i2 = 0; i2 < 45; ++i2) {
            ItemStack is = ElytraPlus.mc.player.getInventory().getStack(i2);
            if (!ItemStack.areItemsEqual((ItemStack)is, (ItemStack)stack) || is.getItem() != item) continue;
            return i2;
        }
        return -1;
    }

    private int getFireworks() {
        if (ElytraPlus.mc.player.getOffHandStack().getItem() == Items.FIREWORK_ROCKET) {
            return -2;
        }
        int firesInHotbar = this.getFireWorks(true);
        int firesInInventory = this.getFireWorks(false);
        if (firesInInventory == -1) {
            this.noFireworks();
            return -1;
        }
        if (firesInHotbar == -1) {
            if (!this.allowFireSwap.getValue().booleanValue()) {
                this.disable(ClientSettings.isRu() ? "\u041d\u0435\u0442 \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u043e\u0432!" : "No fireworks!");
                return this.fireSlot.getValue() - 1;
            }
            this.moveFireworksToHotbar(firesInInventory);
            return this.fireSlot.getValue() - 1;
        }
        return firesInHotbar;
    }

    private boolean canFly() {
        if (this.shouldSwapToElytra()) {
            return false;
        }
        return this.getFireworks() != -1;
    }

    private boolean shouldSwapToElytra() {
        ItemStack is = ElytraPlus.mc.player.getEquippedStack(EquipmentSlot.CHEST);
        return is.getItem() != Items.ELYTRA || !ElytraItem.isUsable((ItemStack)is);
    }

    private void doFireWork(boolean started) {
        if (started && (float)(System.currentTimeMillis() - this.lastFireworkTime) < this.fireDelay.getValue().floatValue() * 1000.0f) {
            return;
        }
        if (this.grim.getValue().isEnabled() && this.fireWorkExtender.getValue().booleanValue() && started && this.pingTimer.passedMs(200L) && this.flightZonePos != null && PlayerUtility.getSquaredDistance2D(this.flightZonePos) < 7000.0f) {
            return;
        }
        if (started && !ElytraPlus.mc.player.isFallFlying()) {
            return;
        }
        if (!started && Managers.PLAYER.ticksElytraFlying > 1) {
            return;
        }
        int slot = this.getFireworks();
        if (slot == -1) {
            this.slotWithFireWorks = -1;
            return;
        }
        this.slotWithFireWorks = slot;
        boolean inOffhand = ElytraPlus.mc.player.getOffHandStack().getItem() == Items.FIREWORK_ROCKET;
        int prevSlot = ElytraPlus.mc.player.getInventory().selectedSlot;
        if (!inOffhand && prevSlot != slot) {
            this.sendPacket((Packet<?>)new UpdateSelectedSlotC2SPacket(slot));
        }
        this.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(inOffhand ? Hand.OFF_HAND : Hand.MAIN_HAND, id, ElytraPlus.mc.player.getYaw(), ElytraPlus.mc.player.getPitch()));
        if (!inOffhand && prevSlot != ElytraPlus.mc.player.getInventory().selectedSlot) {
            this.sendPacket((Packet<?>)new UpdateSelectedSlotC2SPacket(prevSlot));
        }
        this.flying = true;
        this.lastFireworkTime = System.currentTimeMillis();
        this.pingTimer.reset();
        this.flightZonePos = ElytraPlus.mc.player.getPos();
    }

    private void equipElytra() {
        int elytraSlot = InventoryUtility.getElytra();
        if (elytraSlot == -1 && ElytraPlus.mc.player.currentScreenHandler.getCursorStack().getItem() != Items.ELYTRA) {
            this.noElytra();
            return;
        }
        if (!this.shouldSwapToElytra()) {
            return;
        }
        if (this.prevElytraSlot == -1) {
            ItemStack is = ElytraPlus.mc.player.getEquippedStack(EquipmentSlot.CHEST);
            this.prevElytraSlot = elytraSlot;
            this.prevArmorItem = is.getItem();
            this.prevArmorItemCopy = is.copy();
        }
        ElytraPlus.clickSlot(elytraSlot);
        ElytraPlus.clickSlot(6);
        if (this.prevElytraSlot != -1) {
            ElytraPlus.clickSlot(this.prevElytraSlot);
        }
        this.elytraEquiped = true;
    }

    private void returnChestPlate() {
        if (this.prevElytraSlot != -1 && this.prevArmorItem != Items.AIR) {
            if (!this.elytraEquiped) {
                return;
            }
            ItemStack is = ElytraPlus.mc.player.getInventory().getStack(this.prevElytraSlot);
            boolean bl2 = is != ItemStack.EMPTY && !ItemStack.areItemsEqual((ItemStack)is, (ItemStack)this.prevArmorItemCopy);
            int n2 = ElytraPlus.findInInventory(this.prevArmorItemCopy, this.prevArmorItem);
            int n = n2 = n2 < 9 && n2 != -1 ? n2 + 36 : n2;
            if (ElytraPlus.mc.player.currentScreenHandler.getCursorStack().getItem() != Items.AIR) {
                ElytraPlus.clickSlot(6);
                if (this.prevElytraSlot != -1) {
                    ElytraPlus.clickSlot(this.prevElytraSlot);
                }
                return;
            }
            if (n2 == -1) {
                return;
            }
            ElytraPlus.clickSlot(n2);
            ElytraPlus.clickSlot(6);
            if (!bl2) {
                ElytraPlus.clickSlot(n2);
            } else {
                int n4 = ElytraPlus.findEmpty(false);
                if (n4 != -1) {
                    ElytraPlus.clickSlot(n4);
                }
            }
        }
        this.resetPrevItems();
    }

    public static int findEmpty(boolean hotbar) {
        for (int i2 = hotbar ? 0 : 9; i2 < (hotbar ? 9 : 45); ++i2) {
            if (!ElytraPlus.mc.player.getInventory().getStack(i2).isEmpty()) continue;
            return i2;
        }
        return -1;
    }

    public void fireWorkOnPlayerUpdate() {
        boolean aboveLiquid;
        boolean inAir = ElytraPlus.mc.world.isAir(BlockPos.ofFloored(ElytraPlus.mc.player.getPos()));
        boolean bl = aboveLiquid = ElytraPlus.isAboveLiquid(0.1f) && inAir && ElytraPlus.mc.player.getVelocity().getY() < 0.0;
        if (ElytraPlus.mc.player.fallDistance > 0.0f && inAir || aboveLiquid) {
            this.equipElytra();
        } else if (ElytraPlus.mc.player.isOnGround()) {
            this.started = false;
            return;
        }
        if (!MovementUtility.isMoving()) {
            this.acceleration = 0.0f;
        }
        if (!this.canFly()) {
            return;
        }
        if (!ElytraPlus.mc.player.isFallFlying() && !this.started && ElytraPlus.mc.player.getVelocity().getY() < 0.0) {
            this.sendPacket((Packet<?>)new ClientCommandC2SPacket((Entity)ElytraPlus.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            this.started = true;
        }
        if (Managers.PLAYER.ticksElytraFlying < 4) {
            ElytraPlus.mc.options.jumpKey.setPressed(false);
        }
        this.doFireWork(true);
    }

    public void fireworkOnSync() {
        if (this.grim.getValue().isEnabled() && this.rotate.getValue().booleanValue()) {
            if (ElytraPlus.mc.options.jumpKey.isPressed() && ElytraPlus.mc.player.isFallFlying() && this.flying) {
                ElytraPlus.mc.player.setPitch(-45.0f);
            }
            if (ElytraPlus.mc.options.sneakKey.isPressed() && ElytraPlus.mc.player.isFallFlying() && this.flying) {
                ElytraPlus.mc.player.setPitch(45.0f);
            }
            ElytraPlus.mc.player.setYaw(MovementUtility.getMoveDirection());
        }
        if (!MovementUtility.isMoving() && ElytraPlus.mc.options.jumpKey.isPressed() && ElytraPlus.mc.player.isFallFlying() && this.flying) {
            ElytraPlus.mc.player.setPitch(-90.0f);
        }
        if (Managers.PLAYER.ticksElytraFlying < 5 && !ElytraPlus.mc.player.isOnGround()) {
            ElytraPlus.mc.player.setPitch(-45.0f);
        }
    }

    public void fireworkOnMove(EventMove e) {
        if (ElytraPlus.mc.player.isFallFlying() && this.flying) {
            if (ElytraPlus.mc.player.horizontalCollision || ElytraPlus.mc.player.verticalCollision) {
                this.acceleration = 0.0f;
                this.accelerationY = 0.0f;
            }
            if (Managers.PLAYER.ticksElytraFlying < 4) {
                e.setY(0.2f);
                e.cancel();
                return;
            }
            if (ElytraPlus.mc.options.jumpKey.isPressed()) {
                e.setY(this.ySpeed.getValue().floatValue() * Math.min((this.accelerationY += 9.0f) / 100.0f, 1.0f));
            } else if (ElytraPlus.mc.options.sneakKey.isPressed()) {
                e.setY(-this.ySpeed.getValue().floatValue() * Math.min((this.accelerationY += 9.0f) / 100.0f, 1.0f));
            } else if (this.bowBomb.getValue().booleanValue() && ElytraPlus.checkGround(2.0f)) {
                e.setY(ElytraPlus.mc.player.age % 2 == 0 ? (double)0.42f : (double)-0.42f);
            } else {
                switch (this.antiKick.getValue().ordinal()) {
                    case 1: {
                        e.setY(ElytraPlus.mc.player.age % 2 == 0 ? (double)0.08f : (double)-0.08f);
                        break;
                    }
                    case 2: {
                        e.setY(-0.08f);
                        break;
                    }
                    case 0: {
                        e.setY(0.0);
                    }
                }
            }
            if (!MovementUtility.isMoving()) {
                this.acceleration = 0.0f;
            }
            if (ElytraPlus.mc.player.input.movementSideways > 0.0f) {
                ElytraPlus.mc.player.input.movementSideways = 1.0f;
            } else if (ElytraPlus.mc.player.input.movementSideways < 0.0f) {
                ElytraPlus.mc.player.input.movementSideways = -1.0f;
            }
            MovementUtility.modifyEventSpeed(e, this.xzSpeed.getValue().floatValue() * Math.min((this.acceleration += 9.0f) / 100.0f, 1.0f));
            if (this.stayMad.getValue().booleanValue() && !ElytraPlus.checkGround(3.0f) && Managers.PLAYER.ticksElytraFlying > 10) {
                e.setY(0.42f);
            }
            e.cancel();
        }
    }

    public static boolean checkGround(float f2) {
        if (ElytraPlus.mc.player.getY() < 0.0) {
            return false;
        }
        return !ElytraPlus.mc.world.getBlockCollisions((Entity)ElytraPlus.mc.player, ElytraPlus.mc.player.getBoundingBox().offset(0.0, (double)(-f2), 0.0)).iterator().hasNext();
    }

    public static boolean isAboveLiquid(float offset) {
        if (ElytraPlus.mc.player == null) {
            return false;
        }
        return ElytraPlus.mc.world.getBlockState(BlockPos.ofFloored((double)ElytraPlus.mc.player.getX(), (double)(ElytraPlus.mc.player.getY() - (double)offset), (double)ElytraPlus.mc.player.getZ())).getBlock() instanceof FluidBlock;
    }

    public void fireworkOnEnable() {
        if (ElytraPlus.mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA && ElytraPlus.mc.player.currentScreenHandler.getCursorStack().getItem() != Items.ELYTRA && InventoryUtility.getElytra() == -1) {
            this.noElytra();
            return;
        }
        if (this.getFireWorks(false) == -1) {
            this.noFireworks();
            return;
        }
        if (this.getFireWorks(true) != -1) {
            return;
        }
        this.getStackInSlotCopy = ElytraPlus.mc.player.getInventory().getStack(this.fireSlot.getValue() - 1).copy();
        this.prevItemInHand = ElytraPlus.mc.player.getInventory().getStack(this.fireSlot.getValue() - 1).getItem();
    }

    public void fireworkOnDisable() {
        this.started = false;
        if (this.keepFlying.getValue().booleanValue()) {
            return;
        }
        ElytraPlus.mc.player.setVelocity(0.0, ElytraPlus.mc.player.getVelocity().getY(), 0.0);
        new Thread(() -> {
            this.sendPacket((Packet<?>)new ClientCommandC2SPacket((Entity)ElytraPlus.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            ThunderHack.TICK_TIMER = 0.1f;
            this.returnItem();
            this.reset();
            try {
                Thread.sleep(200L);
            }
            catch (InterruptedException interruptedException) {
                ThunderHack.TICK_TIMER = 1.0f;
                interruptedException.printStackTrace();
            }
            this.returnChestPlate();
            this.resetPrevItems();
            ThunderHack.TICK_TIMER = 1.0f;
        }).start();
    }

    private boolean isBoxCollidingGround() {
        return ElytraPlus.mc.world.getBlockCollisions((Entity)ElytraPlus.mc.player, ElytraPlus.mc.player.getBoundingBox().expand(-0.25, 0.0, -0.25).offset(0.0, -0.3, 0.0)).iterator().hasNext();
    }

    public static enum Mode {
        FireWork,
        SunriseOld,
        Boost,
        Control,
        Pitch40Infinite,
        SunriseNew,
        Packet;

    }

    public static enum NCPStrict {
        Off,
        Old,
        New,
        Motion;

    }

    public static enum AntiKick {
        Off,
        Jitter,
        Glide;

    }
}

