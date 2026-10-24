/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.item.Item
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode
 *  net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
 */
package thunder.hack.features.modules.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.Bind;
import thunder.hack.utility.Timer;
import thunder.hack.utility.player.InventoryUtility;
import thunder.hack.utility.player.SearchInvResult;

public class ElytraSwap
extends Module {
    private final Setting<Boolean> delay = new Setting<Boolean>("Delay", false);
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Enable);
    private final Setting<Bind> switchButton = new Setting<Bind>("SwitchButton", new Bind(-1, false, false), v -> this.mode.getValue() == Mode.Bind);
    private final Setting<Bind> fireWorkButton = new Setting<Bind>("FireWorkButton", new Bind(-1, false, false), v -> this.mode.getValue() == Mode.Bind);
    private final Setting<Boolean> startFireWork = new Setting<Boolean>("StartFireWork", true, v -> this.mode.getValue() == Mode.Bind);
    private final Setting<FireWorkMode> fireWorkMode = new Setting<FireWorkMode>("FireWorkMode", FireWorkMode.Normal, v -> this.mode.getValue() == Mode.Bind);
    private final Timer switchTimer = new Timer();
    private final Timer fireworkTimer = new Timer();
    public static boolean swapping = false;

    public ElytraSwap() {
        super("ElytraSwap", Module.Category.PLAYER);
    }

    @Override
    public void onEnable() {
        if (this.mode.getValue() == Mode.Enable) {
            this.swapChest(true);
        }
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == Mode.Bind && ElytraSwap.mc.currentScreen == null) {
            if (this.switchButton.getValue().getKey() != -1 && this.isKeyPressed(this.switchButton.getValue().getKey()) && this.switchTimer.every(500L)) {
                this.swapChest(false);
            }
            if (this.fireWorkButton.getValue().getKey() != -1 && this.isKeyPressed(this.fireWorkButton.getValue().getKey()) && this.fireworkTimer.every(500L) && ElytraSwap.mc.player.isFallFlying()) {
                this.useFireWork();
            }
        }
    }

    @EventHandler
    public void onPacketSend(PacketEvent.SendPost e) {
        ClientCommandC2SPacket command;
        Object t = e.getPacket();
        if (t instanceof ClientCommandC2SPacket && (command = (ClientCommandC2SPacket)t).getMode() == ClientCommandC2SPacket.Mode.START_FALL_FLYING && this.mode.getValue() == Mode.Bind && this.startFireWork.getValue().booleanValue()) {
            this.useFireWork();
        }
    }

    public void useFireWork() {
        SearchInvResult hotbarFireWorkResult = InventoryUtility.findItemInHotBar(Items.ELYTRA);
        SearchInvResult fireWorkResult = InventoryUtility.findItemInInventory(Items.ELYTRA);
        InventoryUtility.saveSlot();
        if (hotbarFireWorkResult.found()) {
            hotbarFireWorkResult.switchTo();
        } else if (fireWorkResult.found()) {
            ElytraSwap.mc.interactionManager.clickSlot(ElytraSwap.mc.player.currentScreenHandler.syncId, fireWorkResult.slot(), ElytraSwap.mc.player.getInventory().selectedSlot, SlotActionType.PICKUP, (PlayerEntity)ElytraSwap.mc.player);
            this.sendPacket((Packet<?>)new CloseHandledScreenC2SPacket(ElytraSwap.mc.player.currentScreenHandler.syncId));
        } else {
            this.sendMessage(ClientSettings.isRu() ? "\u0423 \u0442\u0435\u0431\u044f \u043d\u0435\u0442 \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u043e\u0432!" : "You've got no fireworks!");
            return;
        }
        this.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id, ElytraSwap.mc.player.getYaw(), ElytraSwap.mc.player.getPitch()));
        this.sendPacket((Packet<?>)new HandSwingC2SPacket(Hand.MAIN_HAND));
        if (this.fireWorkMode.getValue() == FireWorkMode.Silent) {
            InventoryUtility.returnSlot();
            if (!hotbarFireWorkResult.found()) {
                ElytraSwap.mc.interactionManager.clickSlot(ElytraSwap.mc.player.currentScreenHandler.syncId, fireWorkResult.slot(), ElytraSwap.mc.player.getInventory().selectedSlot, SlotActionType.PICKUP, (PlayerEntity)ElytraSwap.mc.player);
                this.sendPacket((Packet<?>)new CloseHandledScreenC2SPacket(ElytraSwap.mc.player.currentScreenHandler.syncId));
            }
        }
    }

    public static int getChestPlateSlot() {
        Item[] items;
        for (Item item : items = new Item[]{Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.IRON_CHESTPLATE, Items.LEATHER_CHESTPLATE}) {
            SearchInvResult slot = InventoryUtility.findItemInInventory(item);
            if (!slot.found()) continue;
            return slot.slot();
        }
        return -1;
    }

    /*
     * Enabled aggressive block sorting
     */
    private void swapChest(boolean disable) {
        block9: {
            block11: {
                SearchInvResult result;
                block10: {
                    result = InventoryUtility.findItemInInventory(Items.FIREWORK_ROCKET);
                    if (ElytraSwap.mc.player.getInventory().getStack(38).getItem() != Items.FIREWORK_ROCKET) break block10;
                    int slot = ElytraSwap.getChestPlateSlot();
                    if (slot != -1) {
                        if (this.delay.getValue().booleanValue()) {
                            Managers.ASYNC.run(() -> {
                                swapping = true;
                                ElytraSwap.clickSlot(slot);
                                try {
                                    Thread.sleep(200L);
                                }
                                catch (Exception exception) {
                                    // empty catch block
                                }
                                ElytraSwap.clickSlot(6);
                                try {
                                    Thread.sleep(200L);
                                }
                                catch (Exception exception) {
                                    // empty catch block
                                }
                                ElytraSwap.clickSlot(slot);
                                this.sendPacket((Packet<?>)new CloseHandledScreenC2SPacket(ElytraSwap.mc.player.currentScreenHandler.syncId));
                                swapping = false;
                            });
                            break block9;
                        } else {
                            ElytraSwap.clickSlot(slot);
                            ElytraSwap.clickSlot(6);
                            ElytraSwap.clickSlot(slot);
                            this.sendPacket((Packet<?>)new CloseHandledScreenC2SPacket(ElytraSwap.mc.player.currentScreenHandler.syncId));
                        }
                        break block9;
                    } else {
                        if (disable) {
                            this.disable(ClientSettings.isRu() ? "\u0423 \u0442\u0435\u0431\u044f \u043d\u0435\u0442 \u043d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a\u0430!" : "You don't have a chestplate!");
                            return;
                        }
                        this.sendMessage(ClientSettings.isRu() ? "\u0423 \u0442\u0435\u0431\u044f \u043d\u0435\u0442 \u043d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a\u0430!" : "You don't have a chestplate!");
                        return;
                    }
                }
                if (!result.found()) break block11;
                if (this.delay.getValue().booleanValue()) {
                    new Thread(() -> {
                        swapping = true;
                        ElytraSwap.clickSlot(result.slot());
                        try {
                            Thread.sleep(200L);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        ElytraSwap.clickSlot(6);
                        try {
                            Thread.sleep(200L);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        ElytraSwap.clickSlot(result.slot());
                        this.sendPacket((Packet<?>)new CloseHandledScreenC2SPacket(ElytraSwap.mc.player.currentScreenHandler.syncId));
                        if (this.startFireWork.getValue().booleanValue() && ElytraSwap.mc.player.fallDistance > 0.0f) {
                            this.sendPacket((Packet<?>)new ClientCommandC2SPacket((Entity)ElytraSwap.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                        }
                        swapping = false;
                    }).start();
                    break block9;
                } else {
                    ElytraSwap.clickSlot(result.slot());
                    ElytraSwap.clickSlot(6);
                    ElytraSwap.clickSlot(result.slot());
                    this.sendPacket((Packet<?>)new CloseHandledScreenC2SPacket(ElytraSwap.mc.player.currentScreenHandler.syncId));
                    if (this.startFireWork.getValue().booleanValue() && ElytraSwap.mc.player.fallDistance > 0.0f) {
                        this.sendPacket((Packet<?>)new ClientCommandC2SPacket((Entity)ElytraSwap.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                    }
                }
                break block9;
            }
            if (disable) {
                this.disable(ClientSettings.isRu() ? "\u0423 \u0442\u0435\u0431\u044f \u043d\u0435\u0442 \u044d\u043b\u0438\u0442\u0440\u044b!" : "You don't have an elytra!");
                return;
            }
            this.sendMessage(ClientSettings.isRu() ? "\u0423 \u0442\u0435\u0431\u044f \u043d\u0435\u0442 \u044d\u043b\u0438\u0442\u0440\u044b!" : "You don't have an elytra!");
            return;
        }
        if (!disable) return;
        this.disable(ClientSettings.isRu() ? "\u0421\u0432\u0430\u043f\u043d\u0443\u043b! \u041e\u0442\u043a\u043b\u044e\u0447\u0430\u044e.." : "Swapped! Disabling..");
    }

    private static enum Mode {
        Enable,
        Bind;

    }

    private static enum FireWorkMode {
        Silent,
        Normal;

    }
}

