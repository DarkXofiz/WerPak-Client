/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.SimpleInventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.screen.ShulkerBoxScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.FilledMapItem
 *  net.minecraft.world.World
 *  net.minecraft.item.map.MapState
 *  net.minecraft.block.ShulkerBoxBlock
 *  net.minecraft.text.Text
 *  net.minecraft.client.render.DiffuseLighting
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider.Immediate
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.component.type.MapIdComponent
 *  net.minecraft.component.type.ContainerComponent
 *  net.minecraft.component.DataComponentTypes
 *  org.lwjgl.opengl.GL11
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.FilledMapItem;
import net.minecraft.world.World;
import net.minecraft.item.map.MapState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.text.Text;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.DataComponentTypes;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.core.Core;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.misc.PeekScreen;
import thunder.hack.utility.Timer;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;
import thunder.hack.utility.render.animation.AnimationUtility;

@Mixin(value={HandledScreen.class})
public abstract class MixinHandledScreen<T extends ScreenHandler>
extends Screen
implements ScreenHandlerProvider<T> {
    @Unique
    private final Timer delayTimer = new Timer();
    @Unique
    private float animation = 0.0f;
    @Unique
    private Runnable postRender;
    @Shadow
    @Nullable
    protected Slot focusedSlot;
    @Shadow
    protected int x;
    @Shadow
    protected int y;
    private static final ItemStack[] ITEMS = new ItemStack[27];
    private Map<Render2DEngine.Rectangle, Integer> clickableRects = new HashMap<Render2DEngine.Rectangle, Integer>();

    protected MixinHandledScreen(Text title) {
        super(title);
    }

    @Shadow
    protected abstract boolean isPointOverSlot(Slot var1, double var2, double var4);

    @Shadow
    protected abstract void onMouseClick(Slot var1, int var2, int var3, SlotActionType var4);

    @Inject(method={"render"}, at={@At(value="HEAD")})
    private void drawScreenHook(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (ModuleManager.inventoryAnimation.isEnabled()) {
            this.animation = AnimationUtility.fast(this.animation, 1.0f, ModuleManager.inventoryAnimation.speed.getValue().floatValue());
            context.getMatrices().push();
            float scale = 0.5f + 0.5f * this.animation;
            context.getMatrices().translate((float)this.width / 2.0f * (1.0f - scale), (float)this.height / 2.0f * (1.0f - scale), 0.0f);
            context.getMatrices().scale(scale, scale, 1.0f);
        }
    }

    private boolean shit() {
        return InputUtil.isKeyPressed((long)MinecraftClient.getInstance().getWindow().getHandle(), (int)340) || InputUtil.isKeyPressed((long)MinecraftClient.getInstance().getWindow().getHandle(), (int)344);
    }

    private boolean attack() {
        return Core.hold_mouse0;
    }

    @Inject(method={"render"}, at={@At(value="TAIL")})
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (ModuleManager.inventoryAnimation.isEnabled()) {
            context.getMatrices().pop();
        } else {
            this.animation = 0.0f;
        }
        if (Module.fullNullCheck()) {
            return;
        }
        if (this.focusedSlot != null && !this.focusedSlot.getStack().isEmpty() && this.client.player.playerScreenHandler.getCursorStack().isEmpty()) {
            if (this.focusedSlot.getStack().contains(DataComponentTypes.CONTAINER)) {
                this.renderShulkerToolTip(context, mouseX, mouseY, 0, 0, this.focusedSlot.getStack());
            } else if (this.focusedSlot.getStack().getItem() == Items.FILLED_MAP) {
                this.drawMapPreview(context, this.focusedSlot.getStack(), mouseX, mouseY);
            }
        }
        boolean xOffset = false;
        int yOffset = 20;
        boolean stage = false;
    }

    @Inject(method={"drawSlot(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/screen/slot/Slot;)V"}, at={@At(value="TAIL")})
    protected void drawSlotHook(DrawContext context, Slot slot, CallbackInfo ci) {
    }

    public boolean renderShulkerToolTip(DrawContext context, int offsetX, int offsetY, int mouseX, int mouseY, ItemStack stack) {
        try {
            BlockItem bi;
            ContainerComponent compoundTag = (ContainerComponent)stack.get(DataComponentTypes.CONTAINER);
            if (compoundTag == null) {
                return false;
            }
            float[] colors = new float[]{1.0f, 1.0f, 1.0f};
            Item focusedItem = stack.getItem();
            if (focusedItem instanceof BlockItem && (bi = (BlockItem)focusedItem).getBlock() instanceof ShulkerBoxBlock) {
                try {
                    Color c = new Color(Objects.requireNonNull(ShulkerBoxBlock.getColor((Item)stack.getItem())).getEntityColor());
                    colors = new float[]{(float)c.getRed() / 255.0f, (float)c.getGreen() / 255.0f, (float)c.getRed() / 255.0f, (float)c.getAlpha() / 255.0f};
                }
                catch (NullPointerException npe) {
                    colors = new float[]{1.0f, 1.0f, 1.0f};
                }
            }
            this.draw(context, compoundTag.stream().toList(), offsetX, offsetY, mouseX, mouseY, colors);
        }
        catch (Exception ignore) {
            return false;
        }
        return true;
    }

    @Inject(method={"drawMouseoverTooltip"}, at={@At(value="HEAD")}, cancellable=true)
    private void onDrawMouseoverTooltip(DrawContext context, int x, int y, CallbackInfo ci) {
        if (Module.fullNullCheck()) {
            return;
        }
        if (this.focusedSlot != null && !this.focusedSlot.getStack().isEmpty() && this.client.player.playerScreenHandler.getCursorStack().isEmpty() && this.focusedSlot.getStack().getItem() == Items.FILLED_MAP) {
            ci.cancel();
        }
    }

    @Unique
    private void draw(DrawContext context, List<ItemStack> itemStacks, int offsetX, int offsetY, int mouseX, int mouseY, float[] colors) {
        RenderSystem.disableDepthTest();
        GL11.glClear((int)256);
        this.drawBackground(context, offsetX += 8, offsetY -= 82, colors);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        DiffuseLighting.enableGuiDepthLighting();
        int row = 0;
        int i = 0;
        for (ItemStack itemStack : itemStacks) {
            context.drawItem(itemStack, offsetX + 8 + i * 18, offsetY + 7 + row * 18);
            context.drawItemInSlot(Module.mc.textRenderer, itemStack, offsetX + 8 + i * 18, offsetY + 7 + row * 18);
            if (mouseX > offsetX + 8 + i * 18 && mouseX < offsetX + 28 + i * 18 && mouseY > offsetY + 7 + row * 18 && mouseY < offsetY + 27 + row * 18) {
                this.postRender = () -> context.drawTooltip(this.textRenderer, MixinHandledScreen.getTooltipFromItem((MinecraftClient)Module.mc, (ItemStack)itemStack), itemStack.getTooltipData(), mouseX, mouseY);
            }
            if (++i < 9) continue;
            i = 0;
            ++row;
        }
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.enableDepthTest();
    }

    private void drawBackground(DrawContext context, int x, int y, float[] colors) {
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor((float)colors[0], (float)colors[1], (float)colors[2], (float)1.0f);
        RenderSystem.texParameter((int)3553, (int)10240, (int)9729);
        RenderSystem.texParameter((int)3553, (int)10241, (int)9987);
        context.drawTexture(TextureStorage.container, x, y, 0.0f, 0.0f, 176, 67, 176, 67);
        RenderSystem.enableBlend();
    }

    private void drawMapPreview(DrawContext context, ItemStack stack, int x, int y) {
        RenderSystem.enableBlend();
        context.getMatrices().push();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int y1 = y - 12;
        int x1 = x + 8;
        int z = 300;
        MapState mapState = FilledMapItem.getMapState((ItemStack)stack, (World)(Object)this.client.world);
        if (mapState != null) {
            mapState.getPlayerSyncData((PlayerEntity)(Object)this.client.player);
            z = 310;
            double scale = 0.65625;
            context.getMatrices().translate((float)(x1 += 8), (float)(y1 += 8), (float)z);
            context.getMatrices().scale((float)scale, (float)scale, 0.0f);
            VertexConsumerProvider.Immediate consumer = this.client.getBufferBuilders().getEntityVertexConsumers();
            this.client.gameRenderer.getMapRenderer().draw(context.getMatrices(), (VertexConsumerProvider)consumer, (MapIdComponent)stack.get(DataComponentTypes.MAP_ID), mapState, false, 0xF000F0);
        }
        context.getMatrices().pop();
    }

    @Inject(method={"mouseClicked"}, at={@At(value="HEAD")}, cancellable=true)
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack;
        if (Module.fullNullCheck()) {
            return;
        }
        if (button == 2 && this.focusedSlot != null && !this.focusedSlot.getStack().isEmpty() && this.client.player.playerScreenHandler.getCursorStack().isEmpty() && (itemStack = this.focusedSlot.getStack()).contains(DataComponentTypes.CONTAINER)) {
            Arrays.fill(ITEMS, ItemStack.EMPTY);
            ContainerComponent nbt = (ContainerComponent)itemStack.get(DataComponentTypes.CONTAINER);
            if (nbt != null) {
                var list = nbt.stream().toList();
                for (int i = 0; i < list.size(); ++i) {
                    MixinHandledScreen.ITEMS[i] = (ItemStack)list.get(i);
                }
            }
            this.client.setScreen((Screen)new PeekScreen(new ShulkerBoxScreenHandler(0, this.client.player.getInventory(), (Inventory)new SimpleInventory(ITEMS)), this.client.player.getInventory(), this.focusedSlot.getStack().getName(), ((BlockItem)(Object)this.focusedSlot.getStack().getItem()).getBlock()));
            cir.setReturnValue(true);
        }
        for (Render2DEngine.Rectangle rect : this.clickableRects.keySet()) {
            if (!rect.contains(mouseX, mouseY)) continue;
            Module.mc.interactionManager.clickSlot(Module.mc.player.currentScreenHandler.syncId, this.clickableRects.get(rect).intValue(), 0, SlotActionType.PICKUP, (PlayerEntity)Module.mc.player);
        }
    }
}

