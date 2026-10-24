/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.Arm
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.CrossbowItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.model.json.ModelTransformationMode
 */
package thunder.hack.features.modules.render;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.Arm;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import org.jetbrains.annotations.NotNull;
import thunder.hack.ThunderHack;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventHeldItemRenderer;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.injection.accesors.IHeldItemRenderer;
import thunder.hack.setting.Setting;

public class Animations
extends Module {
    private final Setting<Boolean> onlyaura = new Setting<Boolean>("OnlyAura", false);
    public Setting<Boolean> oldAnimationsM = new Setting<Boolean>("DisableSwapMain", true);
    public Setting<Boolean> oldAnimationsOff = new Setting<Boolean>("DisableSwapOff", true);
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.Default);
    public static Setting<Boolean> slowAnimation = new Setting<Boolean>("SlowAnimation", true);
    public static Setting<Integer> slowAnimationVal = new Setting<Integer>("SlowValue", 12, 1, 50);
    public boolean flip;

    public Animations() {
        super("Animations", Module.Category.RENDER);
    }

    public boolean shouldAnimate() {
        return this.isEnabled() && (this.onlyaura.getValue() == false || ModuleManager.aura.isEnabled() && Aura.target != null) && this.mode.getValue() != Mode.Normal;
    }

    public boolean shouldChangeAnimationDuration() {
        return this.isEnabled() && (this.onlyaura.getValue() == false || ModuleManager.aura.isEnabled() && Aura.target != null);
    }

    @Override
    public void onUpdate() {
        if (Animations.fullNullCheck()) {
            return;
        }
        if (this.oldAnimationsM.getValue().booleanValue() && ((IHeldItemRenderer)mc.getEntityRenderDispatcher().getHeldItemRenderer()).getEquippedProgressMainHand() <= 1.0f) {
            ((IHeldItemRenderer)mc.getEntityRenderDispatcher().getHeldItemRenderer()).setEquippedProgressMainHand(1.0f);
            ((IHeldItemRenderer)mc.getEntityRenderDispatcher().getHeldItemRenderer()).setItemStackMainHand(Animations.mc.player.getMainHandStack());
        }
        if (this.oldAnimationsOff.getValue().booleanValue() && ((IHeldItemRenderer)mc.getEntityRenderDispatcher().getHeldItemRenderer()).getEquippedProgressOffHand() <= 1.0f) {
            ((IHeldItemRenderer)mc.getEntityRenderDispatcher().getHeldItemRenderer()).setEquippedProgressOffHand(1.0f);
            ((IHeldItemRenderer)mc.getEntityRenderDispatcher().getHeldItemRenderer()).setItemStackOffHand(Animations.mc.player.getOffHandStack());
        }
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof HandSwingC2SPacket) {
            this.flip = !this.flip;
        }
    }

    private void renderSwordAnimation(MatrixStack matrices, float f, float swingProgress, float equipProgress, Arm arm) {
        if (arm == Arm.LEFT && (this.mode.getValue() == Mode.Eleven || this.mode.getValue() == Mode.Ten || this.mode.getValue() == Mode.Nine || this.mode.getValue() == Mode.Three || this.mode.getValue() == Mode.Thirteen || this.mode.getValue() == Mode.Fourteen)) {
            this.applyEquipOffset(matrices, arm, equipProgress);
            matrices.translate(-ModuleManager.viewModel.positionMainX.getValue().floatValue(), ModuleManager.viewModel.positionMainY.getValue().floatValue(), ModuleManager.viewModel.positionMainZ.getValue().floatValue());
            this.applySwingOffset(matrices, arm, swingProgress);
            matrices.translate(ModuleManager.viewModel.positionMainX.getValue().floatValue(), -ModuleManager.viewModel.positionMainY.getValue().floatValue(), -ModuleManager.viewModel.positionMainZ.getValue().floatValue());
            return;
        }
        switch (this.mode.getValue().ordinal()) {
            case 1: {
                this.applyEquipOffset(matrices, arm, equipProgress);
                this.translateToViewModelOff(matrices);
                this.applySwingOffset(matrices, arm, swingProgress);
                this.translateBacklOff(matrices);
                break;
            }
            case 2: {
                float n = -0.4f * MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                this.applyEquipOffset(matrices, arm, n);
                int i = arm == Arm.RIGHT ? 1 : -1;
                this.translateToViewModel(matrices);
                float f1 = MathHelper.sin((float)(swingProgress * swingProgress * (float)Math.PI));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * (45.0f + f1 * -20.0f)));
                float g = MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * g * -20.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * 0.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * -45.0f));
                this.translateBack(matrices);
                break;
            }
            case 3: {
                this.applyEquipOffset(matrices, arm, 0.2f * MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * ((float)Math.PI * 2))));
                break;
            }
            case 4: {
                float n = -0.4f * MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                float g = MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                this.applyEquipOffset(matrices, arm, n);
                int i = arm == Arm.RIGHT ? 1 : -1;
                this.translateToViewModel(matrices);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * (45.0f + f * -20.0f)));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * g * -70.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-70.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * -45.0f));
                this.translateBack(matrices);
                break;
            }
            case 5: {
                this.applyEquipOffset(matrices, arm, 0.0f);
                this.translateToViewModel(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(swingProgress > 0.0f ? -MathHelper.sin((float)(swingProgress * 13.0f)) * 37.0f : 0.0f));
                this.translateBack(matrices);
                break;
            }
            case 6: {
                this.applyEquipOffset(matrices, arm, 0.0f);
                int i = arm == Arm.RIGHT ? 1 : -1;
                float g = MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                this.translateToViewModel(matrices);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * g * -20.0f));
                this.translateBack(matrices);
                break;
            }
            case 7: {
                this.applyEquipOffset(matrices, arm, equipProgress);
                this.translateToViewModel(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(swingProgress * (this.flip ? 360.0f : -360.0f)));
                this.translateBack(matrices);
                break;
            }
            case 9: {
                this.applyEquipOffset(matrices, arm, equipProgress);
                this.translateToViewModel(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(swingProgress * -360.0f));
                this.translateBack(matrices);
                break;
            }
            case 8: {
                this.applyEquipOffset(matrices, arm, equipProgress);
                float a = -MathHelper.sin((float)(swingProgress * 3.0f)) / 2.0f + 1.0f;
                matrices.scale(a, a, a);
                break;
            }
            case 10: {
                float g = MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                this.applyEquipOffset(matrices, arm, 0.0f);
                this.translateToViewModel(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(50.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-30.0f * (1.0f - g) - 30.0f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(110.0f));
                this.translateBack(matrices);
                break;
            }
            case 11: {
                float g = MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                matrices.translate(0.0f, 0.0f, 0.0f);
                this.applyEquipOffset(matrices, arm, 0.0f);
                this.translateToViewModel(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(50.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-60.0f * g - 50.0f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(110.0f));
                this.translateBack(matrices);
                break;
            }
            case 12: {
                float g = MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                this.applyEquipOffset(matrices, arm, 0.0f);
                this.translateToViewModel(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(50.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-60.0f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(110.0f + 20.0f * g));
                this.translateBack(matrices);
                break;
            }
            case 13: {
                float g = MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                this.applyEquipOffset(matrices, arm, 0.0f);
                matrices.translate(0.0f, 0.0f, -g / 4.0f);
                this.translateToViewModel(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-120.0f));
                this.translateBack(matrices);
                break;
            }
            case 14: {
                float g = MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                this.applyEquipOffset(matrices, arm, 0.0f);
                this.translateToViewModel(matrices);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-MathHelper.sin((float)(swingProgress * 3.0f)) * 60.0f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-60.0f * g));
                this.translateBack(matrices);
                break;
            }
            case 15: {
                if (swingProgress > 0.0f) {
                    float g = MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                    matrices.translate(0.56f, equipProgress * -0.2f - 0.5f, -0.7f);
                    this.translateToViewModel(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0f));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * -85.0f));
                    if (ModuleManager.viewModel.isEnabled()) {
                        matrices.translate(-0.1f * ModuleManager.viewModel.scaleMain.getValue().floatValue(), 0.28f * ModuleManager.viewModel.scaleMain.getValue().floatValue(), 0.2f * ModuleManager.viewModel.scaleMain.getValue().floatValue());
                    } else {
                        matrices.translate(-0.1f, 0.28f, 0.2f);
                    }
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-85.0f));
                    this.translateBack(matrices);
                    break;
                }
                float n = -0.4f * MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                float m = 0.2f * MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * ((float)Math.PI * 2)));
                float f1 = -0.2f * MathHelper.sin((float)(swingProgress * (float)Math.PI));
                matrices.translate(n, m, f1);
                this.applyEquipOffset(matrices, arm, equipProgress);
                this.applySwingOffset(matrices, arm, swingProgress);
            }
        }
    }

    public void renderFirstPersonItemCustom(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!player.isUsingSpyglass()) {
            boolean bl = hand == Hand.MAIN_HAND;
            Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
            matrices.push();
            float f = 0.0f;
            if (item.isOf(Items.CROSSBOW)) {
                int i;
                boolean bl2 = CrossbowItem.isCharged((ItemStack)item);
                boolean bl3 = arm == Arm.RIGHT;
                int n = i = bl3 ? 1 : -1;
                if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    matrices.translate((float)i * -0.4785682f, -0.094387f, 0.05731531f);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-11.935f));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * 65.3f));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * -9.785f));
                    f = (float)item.getMaxUseTime((LivingEntity)Animations.mc.player) - ((float)Animations.mc.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                    float g = f / (float)CrossbowItem.getPullTime((ItemStack)item, (LivingEntity)Animations.mc.player);
                    if (g > 1.0f) {
                        g = 1.0f;
                    }
                    if (g > 0.1f) {
                        float h = MathHelper.sin((float)((f - 0.1f) * 1.3f));
                        float j = g - 0.1f;
                        float k = h * j;
                        matrices.translate(k * 0.0f, k * 0.004f, k * 0.0f);
                    }
                    matrices.translate(g * 0.0f, g * 0.0f, g * 0.04f);
                    matrices.scale(1.0f, 1.0f, 1.0f + g * 0.2f);
                    matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)i * 45.0f));
                } else {
                    f = -0.4f * MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
                    float g = 0.2f * MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * ((float)Math.PI * 2)));
                    float h = -0.2f * MathHelper.sin((float)(swingProgress * (float)Math.PI));
                    matrices.translate((float)i * f, g, h);
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    this.applySwingOffset(matrices, arm, swingProgress);
                    if (bl2 && swingProgress < 0.001f && bl) {
                        matrices.translate((float)i * -0.641864f, 0.0f, 0.0f);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * 10.0f));
                    }
                }
                EventHeldItemRenderer event = new EventHeldItemRenderer(hand, item, equipProgress, matrices);
                ThunderHack.EVENT_BUS.post(event);
                this.renderItem((LivingEntity)player, item, bl3 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl3, matrices, vertexConsumers, light);
            } else {
                boolean bl2 = arm == Arm.RIGHT;
                float m = 0.0f;
                if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                    int l = bl2 ? 1 : -1;
                    switch (item.getUseAction()) {
                        case NONE: 
                        case BLOCK: {
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            break;
                        }
                        case EAT: 
                        case DRINK: {
                            this.applyEatOrDrinkTransformationCustom(matrices, tickDelta, arm, item);
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            break;
                        }
                        case BOW: {
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            matrices.translate((float)l * -0.2785682f, 0.18344387f, 0.15731531f);
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935f));
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 35.3f));
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -9.785f));
                            m = (float)item.getMaxUseTime((LivingEntity)Animations.mc.player) - ((float)Animations.mc.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                            f = m / 20.0f;
                            f = (f * f + f * 2.0f) / 3.0f;
                            if (f > 1.0f) {
                                f = 1.0f;
                            }
                            if (f > 0.1f) {
                                float g = MathHelper.sin((float)((m - 0.1f) * 1.3f));
                                float h = f - 0.1f;
                                float j = g * h;
                                matrices.translate(j * 0.0f, j * 0.004f, j * 0.0f);
                            }
                            matrices.translate(f * 0.0f, f * 0.0f, f * 0.04f);
                            matrices.scale(1.0f, 1.0f, 1.0f + f * 0.2f);
                            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)l * 45.0f));
                            break;
                        }
                        case SPEAR: {
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            matrices.translate((float)l * -0.5f, 0.7f, 0.1f);
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-55.0f));
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 35.3f));
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -9.785f));
                            m = (float)item.getMaxUseTime((LivingEntity)Animations.mc.player) - ((float)Animations.mc.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                            f = m / 10.0f;
                            if (f > 1.0f) {
                                f = 1.0f;
                            }
                            if (f > 0.1f) {
                                float g = MathHelper.sin((float)((m - 0.1f) * 1.3f));
                                float h = f - 0.1f;
                                float j = g * h;
                                matrices.translate(j * 0.0f, j * 0.004f, j * 0.0f);
                            }
                            matrices.translate(0.0f, 0.0f, f * 0.2f);
                            matrices.scale(1.0f, 1.0f, 1.0f + f * 0.2f);
                            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)l * 45.0f));
                            break;
                        }
                        case BRUSH: {
                            this.applyBrushTransformation(matrices, tickDelta, arm, item, equipProgress);
                        }
                    }
                } else if (player.isUsingRiptide()) {
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    int l = bl2 ? 1 : -1;
                    matrices.translate((float)l * -0.4f, 0.8f, 0.3f);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 65.0f));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -85.0f));
                } else {
                    this.renderSwordAnimation(matrices, f, swingProgress, equipProgress, arm);
                }
                EventHeldItemRenderer event = new EventHeldItemRenderer(hand, item, equipProgress, matrices);
                ThunderHack.EVENT_BUS.post(event);
                this.renderItem((LivingEntity)player, item, bl2 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl2, matrices, vertexConsumers, light);
            }
            matrices.pop();
        }
    }

    private void applyBrushTransformation(MatrixStack matrices, float tickDelta, Arm arm, @NotNull ItemStack stack, float equipProgress) {
        this.applyEquipOffset(matrices, arm, equipProgress);
        float f = (float)Animations.mc.player.getItemUseTimeLeft() - tickDelta + 1.0f;
        float g = 1.0f - f / (float)stack.getMaxUseTime((LivingEntity)Animations.mc.player);
        float m = -15.0f + 75.0f * MathHelper.cos((float)(g * 45.0f * (float)Math.PI));
        if (arm != Arm.RIGHT) {
            matrices.translate(0.1, 0.83, 0.35);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(m));
            matrices.translate(-0.3, 0.22, 0.35);
        } else {
            matrices.translate(-0.25, 0.22, 0.35);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(m));
        }
    }

    private void applyEquipOffset(@NotNull MatrixStack matrices, Arm arm, float equipProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate((float)i * 0.56f, -0.52f + equipProgress * -0.6f, -0.72f);
    }

    private void applySwingOffset(@NotNull MatrixStack matrices, Arm arm, float swingProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        float f = MathHelper.sin((float)(swingProgress * swingProgress * (float)Math.PI));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * (45.0f + f * -20.0f)));
        float g = MathHelper.sin((float)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * g * -20.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * -80.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * -45.0f));
    }

    public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (stack.isEmpty()) {
            return;
        }
        mc.getItemRenderer().renderItem(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, entity.getWorld(), light, OverlayTexture.DEFAULT_UV, entity.getId() + renderMode.ordinal());
    }

    private void applyEatOrDrinkTransformationCustom(MatrixStack matrices, float tickDelta, Arm arm, @NotNull ItemStack stack) {
        float h;
        float f = (float)Animations.mc.player.getItemUseTimeLeft() - tickDelta + 1.0f;
        float g = f / (float)stack.getMaxUseTime((LivingEntity)Animations.mc.player);
        if (g < 0.8f) {
            h = MathHelper.abs((float)(MathHelper.cos((float)(f / 4.0f * (float)Math.PI)) * 0.005f));
            matrices.translate(0.0f, h, 0.0f);
        }
        h = 1.0f - (float)Math.pow(g, 27.0);
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate(h * 0.6f * (float)i * ModuleManager.viewModel.eatX.getValue().floatValue(), h * -0.5f * ModuleManager.viewModel.eatY.getValue().floatValue(), h * 0.0f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * h * 90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(h * 10.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * h * 30.0f));
    }

    private void translateToViewModel(MatrixStack matrices) {
        if (ModuleManager.viewModel.isEnabled()) {
            matrices.translate(ModuleManager.viewModel.positionMainX.getValue().floatValue(), ModuleManager.viewModel.positionMainY.getValue().floatValue(), ModuleManager.viewModel.positionMainZ.getValue().floatValue());
        }
    }

    private void translateToViewModelOff(MatrixStack matrices) {
        if (ModuleManager.viewModel.isEnabled()) {
            matrices.translate(-ModuleManager.viewModel.positionMainX.getValue().floatValue(), ModuleManager.viewModel.positionMainY.getValue().floatValue(), ModuleManager.viewModel.positionMainZ.getValue().floatValue());
        }
    }

    private void translateBack(MatrixStack matrices) {
        if (ModuleManager.viewModel.isEnabled()) {
            matrices.translate(-ModuleManager.viewModel.positionMainX.getValue().floatValue(), -ModuleManager.viewModel.positionMainY.getValue().floatValue(), -ModuleManager.viewModel.positionMainZ.getValue().floatValue());
        }
    }

    private void translateBacklOff(MatrixStack matrices) {
        if (ModuleManager.viewModel.isEnabled()) {
            matrices.translate(ModuleManager.viewModel.positionMainX.getValue().floatValue(), -ModuleManager.viewModel.positionMainY.getValue().floatValue(), -ModuleManager.viewModel.positionMainZ.getValue().floatValue());
        }
    }

    private static enum Mode {
        Normal,
        Default,
        One,
        Two,
        Three,
        Four,
        Five,
        Six,
        Seven,
        Eight,
        Nine,
        Ten,
        Eleven,
        Twelve,
        Thirteen,
        Fourteen;

    }
}

