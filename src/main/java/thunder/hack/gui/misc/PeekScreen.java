/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.SimpleInventory
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.ShulkerBoxScreenHandler
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.block.Block
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen
 *  net.minecraft.component.type.ContainerComponent
 *  net.minecraft.component.DataComponentTypes
 */
package thunder.hack.gui.misc;

import java.util.Arrays;
import java.util.List;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.text.Text;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.DataComponentTypes;

public class PeekScreen
extends ShulkerBoxScreen {
    private static final ItemStack[] ITEMS = new ItemStack[27];

    public PeekScreen(ShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title, Block block) {
        super(handler, inventory, title);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ItemStack itemStack;
        if (button == 2 && this.focusedSlot != null && !this.focusedSlot.getStack().isEmpty() && this.client.player.playerScreenHandler.getCursorStack().isEmpty() && (itemStack = this.focusedSlot.getStack()).contains(DataComponentTypes.CONTAINER)) {
            Arrays.fill(ITEMS, ItemStack.EMPTY);
            ContainerComponent nbt = (ContainerComponent)itemStack.get(DataComponentTypes.CONTAINER);
            if (nbt != null) {
                var list = nbt.stream().toList();
                for (int i = 0; i < list.size(); ++i) {
                    PeekScreen.ITEMS[i] = (ItemStack)list.get(i);
                }
            }
            this.client.setScreen((Screen)new PeekScreen(new ShulkerBoxScreenHandler(0, this.client.player.getInventory(), (Inventory)new SimpleInventory(ITEMS)), this.client.player.getInventory(), this.focusedSlot.getStack().getName(), ((BlockItem)this.focusedSlot.getStack().getItem()).getBlock()));
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
}

