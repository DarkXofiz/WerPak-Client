/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Ordering
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Formatting
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.passive.TameableEntity
 *  net.minecraft.entity.passive.HorseEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.enchantment.Enchantments
 *  net.minecraft.world.GameMode
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.block.ShulkerBoxBlock
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.render.DiffuseLighting
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.text.MutableText
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.scoreboard.ScoreboardDisplaySlot
 *  net.minecraft.scoreboard.ReadableScoreboardScore
 *  net.minecraft.scoreboard.ScoreHolder
 *  net.minecraft.scoreboard.number.NumberFormat
 *  net.minecraft.scoreboard.number.StyledNumberFormat
 *  net.minecraft.component.type.ContainerComponent
 *  net.minecraft.component.type.ItemEnchantmentsComponent
 *  net.minecraft.component.DataComponentTypes
 *  org.joml.Vector4d
 *  org.lwjgl.opengl.GL11
 */
package thunder.hack.features.modules.render;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.Formatting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.world.GameMode;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.MutableText;
import net.minecraft.registry.RegistryKey;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.DataComponentTypes;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL11;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.core.manager.player.FriendManager;
import thunder.hack.features.hud.impl.PotionHud;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.features.modules.misc.NameProtect;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.TextureStorage;

public class NameTags
extends Module {
    private final Map<RegistryKey<Enchantment>, String> encMap = new HashMap<RegistryKey<Enchantment>, String>();
    private final Setting<Boolean> self = new Setting<Boolean>("Self", false);
    private final Setting<Float> scale = new Setting<Float>("Scale", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(10.0f));
    private final Setting<Boolean> resize = new Setting<Boolean>("Resize", false);
    private final Setting<Float> height = new Setting<Float>("Height", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(10.0f));
    private final Setting<Boolean> gamemode = new Setting<Boolean>("Gamemode", false);
    private final Setting<Boolean> spawners = new Setting<Boolean>("SpawnerNameTag", false);
    private final Setting<Boolean> entityOwner = new Setting<Boolean>("EntityOwner", false);
    private final Setting<Boolean> ping = new Setting<Boolean>("Ping", false);
    private final Setting<Boolean> hp = new Setting<Boolean>("HP", true);
    private final Setting<Boolean> distance = new Setting<Boolean>("Distance", true);
    private final Setting<Boolean> pops = new Setting<Boolean>("TotemPops", true);
    private final Setting<OutlineColor> outline = new Setting<OutlineColor>("OutlineType", OutlineColor.New);
    private final Setting<OutlineColor> friendOutline = new Setting<OutlineColor>("FriendOutline", OutlineColor.None);
    private final Setting<ColorSetting> outlineColor = new Setting<ColorSetting>("OutlineColor", new ColorSetting(Integer.MIN_VALUE));
    private final Setting<ColorSetting> friendOutlineColor = new Setting<ColorSetting>("FriendOutlineColor", new ColorSetting(Integer.MIN_VALUE));
    private final Setting<Boolean> enchantss = new Setting<Boolean>("Enchants", true);
    private final Setting<Boolean> onlyHands = new Setting<Boolean>("OnlyHands", false, v -> this.enchantss.getValue());
    private final Setting<Boolean> funtimeHp = new Setting<Boolean>("FunTimeHp", false);
    private final Setting<Boolean> ignoreBots = new Setting<Boolean>("IgnoreBots", false);
    private final Setting<Boolean> potions = new Setting<Boolean>("Potions", true);
    private final Setting<Boolean> shulkers = new Setting<Boolean>("Shulkers", true);
    private final Setting<ColorSetting> fillColorA = new Setting<ColorSetting>("Fill", new ColorSetting(Integer.MIN_VALUE));
    private final Setting<ColorSetting> fillColorF = new Setting<ColorSetting>("FriendFill", new ColorSetting(Integer.MIN_VALUE));
    private final Setting<Font> font = new Setting<Font>("FontMode", Font.Fancy);
    private final Setting<Armor> armorMode = new Setting<Armor>("ArmorMode", Armor.Full);
    private final Setting<Health> health = new Setting<Health>("Health", Health.Number);

    public NameTags() {
        super("NameTags", Module.Category.RENDER);
        this.encMap.put((RegistryKey<Enchantment>)Enchantments.BLAST_PROTECTION, "B");
        this.encMap.put((RegistryKey<Enchantment>)Enchantments.PROTECTION, "P");
        this.encMap.put((RegistryKey<Enchantment>)Enchantments.SHARPNESS, "S");
        this.encMap.put((RegistryKey<Enchantment>)Enchantments.EFFICIENCY, "E");
        this.encMap.put((RegistryKey<Enchantment>)Enchantments.UNBREAKING, "U");
        this.encMap.put((RegistryKey<Enchantment>)Enchantments.POWER, "PO");
        this.encMap.put((RegistryKey<Enchantment>)Enchantments.THORNS, "T");
    }

    @Override
    public void onRender2D(DrawContext context) {
        if (NameTags.mc.options.hudHidden) {
            return;
        }
        for (PlayerEntity ent : NameTags.mc.world.getPlayers()) {
            OutlineColor cl;
            if (ent == NameTags.mc.player && (NameTags.mc.options.getPerspective().isFirstPerson() || !this.self.getValue().booleanValue()) || NameTags.getEntityPing(ent) <= 0 && this.ignoreBots.getValue().booleanValue()) continue;
            double x = ent.prevX + (ent.getX() - ent.prevX) * (double)Render3DEngine.getTickDelta();
            double y = ent.prevY + (ent.getY() - ent.prevY) * (double)Render3DEngine.getTickDelta();
            double z = ent.prevZ + (ent.getZ() - ent.prevZ) * (double)Render3DEngine.getTickDelta();
            float scale = this.resize.getValue() != false ? this.scale.getValue().floatValue() / NameTags.mc.player.distanceTo((Entity)ent) : this.scale.getValue().floatValue();
            Vec3d vector = new Vec3d(x, y + (double)this.height.getValue().floatValue(), z);
            Vector4d position = null;
            vector = Render3DEngine.worldSpaceToScreenSpace(vector);
            if (vector.z > 0.0 && vector.z < 1.0) {
                position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
            }
            Object final_string = "";
            if (this.ping.getValue().booleanValue()) {
                final_string = (String)final_string + this.getPingColor(NameTags.getEntityPing(ent)) + NameTags.getEntityPing(ent) + "ms " + String.valueOf(Formatting.WHITE);
            }
            if (this.gamemode.getValue().booleanValue()) {
                final_string = (String)final_string + this.translateGamemode(NameTags.getEntityGamemode(ent)) + " ";
            }
            final_string = FriendManager.friends.stream().anyMatch(i -> i.contains(ent.getDisplayName().getString())) && NameProtect.hideFriends.getValue() != false && ModuleManager.nameProtect.isEnabled() ? (String)final_string + NameProtect.getCustomName() + " " : (String)final_string + ent.getDisplayName().getString() + " ";
            if (this.hp.getValue().booleanValue() && this.health.is(Health.Number)) {
                final_string = (String)final_string + this.getHealthColor(this.getHealth(ent)) + NameTags.round2(this.getHealth(ent)) + " ";
            }
            if (this.distance.getValue().booleanValue()) {
                final_string = (String)final_string + String.format("%.1f", Float.valueOf(NameTags.mc.player.distanceTo((Entity)ent))) + "m ";
            }
            if (this.pops.getValue().booleanValue() && Managers.COMBAT.getPops(ent) != 0) {
                final_string = (String)final_string + String.valueOf(Formatting.RESET) + Managers.COMBAT.getPops(ent);
            }
            if (position == null) continue;
            double posX = position.x;
            double posY = position.y;
            double endPosX = position.z;
            double maxEnchantY = 0.0;
            float diff = (float)(endPosX - posX) / 2.0f;
            float textWidth = this.font.getValue() == Font.Fancy ? FontRenderers.sf_bold.getStringWidth((String)final_string) * 1.0f : (float)NameTags.mc.textRenderer.getWidth((String)final_string);
            float tagX = (float)((posX + (double)diff - (double)(textWidth / 2.0f)) * 1.0);
            ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
            if (this.armorMode.getValue() != Armor.Durability) {
                stacks.add(ent.getOffHandStack());
            }
            stacks.add((ItemStack)ent.getInventory().armor.get(0));
            stacks.add((ItemStack)ent.getInventory().armor.get(1));
            stacks.add((ItemStack)ent.getInventory().armor.get(2));
            stacks.add((ItemStack)ent.getInventory().armor.get(3));
            if (this.armorMode.getValue() != Armor.Durability) {
                stacks.add(ent.getMainHandStack());
            }
            context.getMatrices().push();
            context.getMatrices().translate(tagX - 2.0f + (textWidth + 4.0f) / 2.0f, (float)(posY - 13.0) + 6.5f, 0.0f);
            context.getMatrices().scale(scale, scale, 1.0f);
            context.getMatrices().translate(-(tagX - 2.0f + (textWidth + 4.0f) / 2.0f), -((float)(posY - 13.0 + 6.5)), 0.0f);
            float item_offset = 0.0f;
            if (this.armorMode.getValue() != Armor.None) {
                for (ItemStack armorComponent : stacks) {
                    if (!armorComponent.isEmpty()) {
                        if (this.armorMode.getValue() == Armor.Full) {
                            context.getMatrices().push();
                            context.getMatrices().translate(posX - 55.0 + (double)item_offset, (double)((float)(posY - 33.0)), 0.0);
                            context.getMatrices().scale(1.1f, 1.1f, 1.1f);
                            DiffuseLighting.disableGuiDepthLighting();
                            context.drawItem(armorComponent, 0, 0);
                            context.drawItemInSlot(NameTags.mc.textRenderer, armorComponent, 0, 0);
                            context.getMatrices().pop();
                        } else {
                            context.getMatrices().push();
                            context.getMatrices().translate(posX - 35.0 + (double)item_offset, (double)((float)(posY - 20.0)), 0.0);
                            context.getMatrices().scale(0.7f, 0.7f, 0.7f);
                            float durability = armorComponent.getMaxDamage() - armorComponent.getDamage();
                            int percent = (int)(durability / (float)armorComponent.getMaxDamage() * 100.0f);
                            Color color = percent < 33 ? Color.RED : (percent > 33 && percent < 66 ? Color.YELLOW : Color.GREEN);
                            context.drawText(NameTags.mc.textRenderer, percent + "%", 0, 0, color.getRGB(), false);
                            context.getMatrices().pop();
                        }
                        float enchantmentY = 0.0f;
                        ItemEnchantmentsComponent enchants = EnchantmentHelper.getEnchantments((ItemStack)armorComponent);
                        if (this.enchantss.getValue().booleanValue() && (!this.onlyHands.getValue().booleanValue() || armorComponent == ent.getOffHandStack() || armorComponent == ent.getMainHandStack())) {
                            for (RegistryKey class_53212 : this.encMap.keySet()) {
                                if (!enchants.getEnchantments().contains(NameTags.mc.world.getRegistryManager().get(Enchantments.PROTECTION.getRegistryRef()).getEntry(class_53212).get())) continue;
                                String id = this.encMap.get(class_53212);
                                int level = enchants.getLevel((RegistryEntry)NameTags.mc.world.getRegistryManager().get(Enchantments.PROTECTION.getRegistryRef()).getEntry(class_53212).get());
                                String encName = id + level;
                                if (this.font.getValue() == Font.Fancy) {
                                    FontRenderers.sf_bold.drawString(context.getMatrices(), encName, posX - 50.0 + (double)item_offset, (double)((float)posY - 45.0f + enchantmentY), -1);
                                } else {
                                    context.getMatrices().push();
                                    context.getMatrices().translate(posX - 50.0 + (double)item_offset, posY - 45.0 + (double)enchantmentY, 0.0);
                                    context.drawText(NameTags.mc.textRenderer, encName, 0, 0, -1, false);
                                    context.getMatrices().pop();
                                }
                                if (!(maxEnchantY > (double)(enchantmentY -= 8.0f))) continue;
                                maxEnchantY = enchantmentY;
                            }
                        }
                    }
                    item_offset += 18.0f;
                }
            }
            Color color = Managers.FRIEND.isFriend(ent) ? this.fillColorF.getValue().getColorObject() : this.fillColorA.getValue().getColorObject();
            OutlineColor outlineColor = cl = Managers.FRIEND.isFriend(ent) ? this.friendOutline.getValue() : this.outline.getValue();
            if (cl == OutlineColor.New) {
                Render2DEngine.drawRectWithOutline(context.getMatrices(), tagX - 2.0f, (float)(posY - 13.0), textWidth + 4.0f, 11.0f, color, this.outlineColor.getValue().getColorObject());
            } else {
                Render2DEngine.drawRect(context.getMatrices(), tagX - 2.0f, (float)(posY - 13.0), textWidth + 4.0f, 11.0f, color);
            }
            if (Managers.TELEMETRY.getOnlinePlayers().contains(ent.getGameProfile().getName())) {
                Render2DEngine.drawRect(context.getMatrices(), tagX - 14.0f, (float)(posY - 13.0), 12.0f, 11.0f, color.brighter().brighter());
                RenderSystem.enableBlend();
                RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
                Color lColor = HudEditor.getColor(0);
                RenderSystem.setShaderColor((float)((float)lColor.getRed() / 255.0f), (float)((float)lColor.getGreen() / 255.0f), (float)((float)lColor.getBlue() / 255.0f), (float)1.0f);
                RenderSystem.setShaderTexture((int)0, (Identifier)TextureStorage.miniLogo);
                Render2DEngine.renderTexture(context.getMatrices(), tagX - 13.0f, (float)(posY - 12.5), 10.0, 10.0, 0.0f, 0.0f, 256.0, 256.0, 256.0, 256.0);
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                RenderSystem.disableBlend();
            }
            switch (cl.ordinal()) {
                case 2: 
                case 3: {
                    break;
                }
                case 0: {
                    Render2DEngine.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 14.0), textWidth + 6.0f, 1.0f, HudEditor.getColor(270));
                    Render2DEngine.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 3.0), textWidth + 6.0f, 1.0f, HudEditor.getColor(0));
                    Render2DEngine.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 14.0), 1.0f, 11.0f, HudEditor.getColor(180));
                    Render2DEngine.drawRect(context.getMatrices(), tagX + textWidth + 2.0f, (float)(posY - 14.0), 1.0f, 11.0f, HudEditor.getColor(90));
                    break;
                }
                case 1: {
                    Render2DEngine.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 14.0), textWidth + 6.0f, 1.0f, Managers.FRIEND.isFriend(ent) ? this.friendOutlineColor.getValue().getColorObject() : this.outlineColor.getValue().getColorObject());
                    Render2DEngine.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 3.0), textWidth + 6.0f, 1.0f, Managers.FRIEND.isFriend(ent) ? this.friendOutlineColor.getValue().getColorObject() : this.outlineColor.getValue().getColorObject());
                    Render2DEngine.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 14.0), 1.0f, 11.0f, Managers.FRIEND.isFriend(ent) ? this.friendOutlineColor.getValue().getColorObject() : this.outlineColor.getValue().getColorObject());
                    Render2DEngine.drawRect(context.getMatrices(), tagX + textWidth + 2.0f, (float)(posY - 14.0), 1.0f, 11.0f, Managers.FRIEND.isFriend(ent) ? this.friendOutlineColor.getValue().getColorObject() : this.outlineColor.getValue().getColorObject());
                }
            }
            if (this.font.getValue() == Font.Fancy) {
                FontRenderers.sf_bold.drawString(context.getMatrices(), (String)final_string, (double)tagX, (double)((float)posY - 10.0f), -1);
            } else {
                context.getMatrices().push();
                context.getMatrices().translate(tagX, (float)posY - 11.0f, 0.0f);
                context.drawText(NameTags.mc.textRenderer, (String)final_string, 0, 0, -1, false);
                context.getMatrices().pop();
            }
            if (!this.health.is(Health.Number)) {
                int i2 = MathHelper.ceil((float)ent.getHealth());
                float f = (float)ent.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
                int p = MathHelper.ceil((float)ent.getAbsorptionAmount());
                context.getMatrices().push();
                context.getMatrices().translate(posX - 44.0, posY, 0.0);
                context.getMatrices().scale(1.1f, 1.1f, 1.0f);
                this.renderHealthBar(context, ent, f, i2, p);
                context.getMatrices().pop();
            }
            if (this.potions.getValue().booleanValue()) {
                this.renderStatusEffectOverlay(context, (float)posX, (float)(posY + maxEnchantY - 60.0), ent);
            }
            Item handItem = ent.getMainHandStack().getItem();
            if (this.shulkers.getValue().booleanValue()) {
                BlockItem bi;
                this.renderShulkerToolTip(context, (int)posX - 90, (int)posY - 120, handItem instanceof BlockItem && (bi = (BlockItem)handItem).getBlock() instanceof ShulkerBoxBlock ? ent.getMainHandStack() : ent.getOffHandStack());
            }
            context.getMatrices().pop();
        }
        if (this.spawners.getValue().booleanValue()) {
            this.drawSpawnerNameTag(context);
        }
        if (this.entityOwner.getValue().booleanValue()) {
            this.drawEntityOwner(context);
        }
    }

    private void drawSpawnerNameTag(DrawContext context) {
    }

    public void drawEntityOwner(DrawContext context) {
        for (Entity ent : NameTags.mc.world.getEntities()) {
            String ownerName = "";
            if (ent instanceof ProjectileEntity) {
                ProjectileEntity pe = (ProjectileEntity)ent;
                if (pe.getOwner() != null) {
                    ownerName = pe.getOwner().getDisplayName().getString();
                }
            } else if (ent instanceof HorseEntity) {
                HorseEntity he = (HorseEntity)ent;
                if (he.getOwnerUuid() != null) {
                    ownerName = he.getOwnerUuid().toString();
                }
            } else {
                TameableEntity te;
                if (!(ent instanceof TameableEntity) || !(te = (TameableEntity)ent).isTamed() || te.getOwner() == null) continue;
                ownerName = te.getOwner().getDisplayName().getString();
            }
            String final_string = "Owned by " + ownerName;
            double x = ent.prevX + (ent.getX() - ent.prevX) * (double)Render3DEngine.getTickDelta();
            double y = ent.prevY + (ent.getY() - ent.prevY) * (double)Render3DEngine.getTickDelta();
            double z = ent.prevZ + (ent.getZ() - ent.prevZ) * (double)Render3DEngine.getTickDelta();
            Vec3d vector = new Vec3d(x, y + 2.0, z);
            Vector4d position = null;
            vector = Render3DEngine.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
            if (vector.z > 0.0 && vector.z < 1.0) {
                position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
            }
            if (position == null) continue;
            double posX = position.x;
            double posY = position.y;
            double endPosX = position.z;
            float diff = (float)(endPosX - posX) / 2.0f;
            float textWidth = FontRenderers.sf_bold.getStringWidth(final_string) * 1.0f;
            float tagX = (float)((posX + (double)diff - (double)(textWidth / 2.0f)) * 1.0);
            Render2DEngine.drawRect(context.getMatrices(), tagX - 2.0f, (float)(posY - 13.0), textWidth + 4.0f, 11.0f, this.fillColorA.getValue().getColorObject());
            switch (this.outline.getValue().ordinal()) {
                case 2: {
                    break;
                }
                case 0: {
                    Render2DEngine.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 14.0), textWidth + 6.0f, 1.0f, HudEditor.getColor(270));
                    Render2DEngine.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 3.0), textWidth + 6.0f, 1.0f, HudEditor.getColor(0));
                    Render2DEngine.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 14.0), 1.0f, 11.0f, HudEditor.getColor(180));
                    Render2DEngine.drawRect(context.getMatrices(), tagX + textWidth + 2.0f, (float)(posY - 14.0), 1.0f, 11.0f, HudEditor.getColor(90));
                    break;
                }
                case 1: {
                    Render2DEngine.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 14.0), textWidth + 6.0f, 1.0f, this.outlineColor.getValue().getColorObject());
                    Render2DEngine.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 3.0), textWidth + 6.0f, 1.0f, this.outlineColor.getValue().getColorObject());
                    Render2DEngine.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 14.0), 1.0f, 11.0f, this.outlineColor.getValue().getColorObject());
                    Render2DEngine.drawRect(context.getMatrices(), tagX + textWidth + 2.0f, (float)(posY - 14.0), 1.0f, 11.0f, this.outlineColor.getValue().getColorObject());
                }
            }
            FontRenderers.sf_bold.drawString(context.getMatrices(), final_string, (double)tagX, (double)((float)posY - 10.0f), -1);
        }
    }

    public static int getEntityPing(PlayerEntity entity) {
        if (mc.getNetworkHandler() == null) {
            return 0;
        }
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        if (playerListEntry == null) {
            return 0;
        }
        return playerListEntry.getLatency();
    }

    public static GameMode getEntityGamemode(PlayerEntity entity) {
        if (entity == null) {
            return null;
        }
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        return playerListEntry == null ? null : playerListEntry.getGameMode();
    }

    private String translateGamemode(GameMode gamemode) {
        if (gamemode == null) {
            return "[BOT]";
        }
        return switch (gamemode) {
            default -> throw new MatchException(null, null);
            case GameMode.SURVIVAL -> "[S]";
            case GameMode.CREATIVE -> "[C]";
            case GameMode.SPECTATOR -> "[SP]";
            case GameMode.ADVENTURE -> "[A]";
        };
    }

    public float getHealth(PlayerEntity ent) {
        if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getServerInfo() != null && NameTags.mc.getNetworkHandler().getServerInfo().address.contains("funtime") || this.funtimeHp.getValue().booleanValue()) {
            ScoreboardObjective scoreBoard = null;
            String resolvedHp = "";
            if (ent.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME) != null && (scoreBoard = ent.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME)) != null) {
                ReadableScoreboardScore readableScoreboardScore = ent.getScoreboard().getScore((ScoreHolder)ent, scoreBoard);
                MutableText text2 = ReadableScoreboardScore.getFormattedScore((ReadableScoreboardScore)readableScoreboardScore, (NumberFormat)scoreBoard.getNumberFormatOr((NumberFormat)StyledNumberFormat.EMPTY));
                resolvedHp = text2.getString();
            }
            float numValue = 0.0f;
            try {
                numValue = Float.parseFloat(resolvedHp);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            return numValue;
        }
        return ent.getHealth() + ent.getAbsorptionAmount();
    }

    private void renderHealthBar(DrawContext context, PlayerEntity player, float maxHealth, int lastHealth, int absorption) {
        int i = MathHelper.ceil((double)((double)maxHealth / 2.0));
        int j = MathHelper.ceil((double)((double)absorption / 2.0));
        int k = i * 2;
        int cont = 0;
        for (int l = i + j - 1; l >= 0; --l) {
            int q;
            int n = l % 10;
            int o = n * 8;
            if (cont < 10) {
                this.drawHeart(context, HeartType.CONTAINER, o, false, player);
                ++cont;
            }
            if ((q = l * 2) < lastHealth) {
                this.drawHeart(context, HeartType.NORMAL, o, q + 1 == lastHealth, player);
            }
            if (l < i) continue;
            int r = q - k;
            if (q - k >= absorption) continue;
            context.getMatrices().push();
            context.getMatrices().translate(0.0f, 0.0f, 0.001f);
            this.drawHeart(context, HeartType.ABSORBING, o, r + 1 == absorption, player);
            context.getMatrices().pop();
        }
    }

    private void drawHeart(DrawContext context, HeartType type, int x, boolean half, PlayerEntity player) {
        if (this.health.is(Health.Dots)) {
            Color color;
            Color color2 = color = Managers.FRIEND.isFriend(player) ? this.fillColorF.getValue().getColorObject() : this.fillColorA.getValue().getColorObject();
            if (type == HeartType.CONTAINER) {
                Render2DEngine.drawRect(context.getMatrices(), x, 0.0f, 7.0f, 3.0f, color);
            } else if (type == HeartType.NORMAL) {
                if (half) {
                    Render2DEngine.drawRect(context.getMatrices(), x, 0.0f, 3.0f, 3.0f, this.getHealthColor2(player.getHealth() + player.getAbsorptionAmount()));
                    Render2DEngine.drawRect(context.getMatrices(), x + 3, 0.0f, 4.0f, 3.0f, color);
                } else {
                    Render2DEngine.drawRect(context.getMatrices(), x, 0.0f, 7.0f, 3.0f, this.getHealthColor2(player.getHealth() + player.getAbsorptionAmount()));
                }
            }
        } else {
            context.drawGuiTexture(type.getTexture(half), x, 0, 9, 9);
        }
    }

    @NotNull
    public String getHealthColor(float health) {
        if (health <= 15.0f && health > 7.0f) {
            return String.valueOf(Formatting.YELLOW);
        }
        if (health > 15.0f) {
            return String.valueOf(Formatting.GREEN);
        }
        return String.valueOf(Formatting.RED);
    }

    @NotNull
    public String getPingColor(int ping) {
        if (ping <= 60) {
            return String.valueOf(Formatting.GREEN);
        }
        if (ping > 60 && ping < 120) {
            return String.valueOf(Formatting.YELLOW);
        }
        return String.valueOf(Formatting.RED);
    }

    @NotNull
    private Color getHealthColor2(float health) {
        if (health <= 15.0f && health > 7.0f) {
            return Color.YELLOW;
        }
        if (health > 15.0f) {
            return Color.GREEN;
        }
        return Color.RED;
    }

    public static float round2(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return 1.0f;
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    private void renderStatusEffectOverlay(DrawContext context, float x, float y, PlayerEntity player) {
        ArrayList effects = new ArrayList(player.getStatusEffects());
        if (effects.isEmpty()) {
            return;
        }
        x += (float)effects.size() * 12.5f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(effects)) {
            x -= 25.0f;
            String power = "";
            switch (statusEffectInstance.getAmplifier()) {
                case 0: {
                    power = "I";
                    break;
                }
                case 1: {
                    power = "II";
                    break;
                }
                case 2: {
                    power = "III";
                    break;
                }
                case 3: {
                    power = "IV";
                    break;
                }
                case 4: {
                    power = "V";
                }
            }
            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0.0f);
            context.drawSprite(0, 0, 0, 18, 18, mc.getStatusEffectSpriteManager().getSprite(statusEffectInstance.getEffectType()));
            FontRenderers.sf_bold_mini.drawCenteredString(context.getMatrices(), PotionHud.getDuration(statusEffectInstance), 9.0, -8.0, -1);
            FontRenderers.categories.drawCenteredString(context.getMatrices(), power, 9.0, -16.0, -1);
            context.getMatrices().pop();
        }
        RenderSystem.disableBlend();
    }

    public boolean renderShulkerToolTip(DrawContext context, int offsetX, int offsetY, ItemStack stack) {
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
            } else {
                return false;
            }
            this.draw(context, compoundTag.stream().toList(), offsetX, offsetY, colors);
        }
        catch (Exception ignore) {
            return false;
        }
        return true;
    }

    private void draw(DrawContext context, List<ItemStack> itemStacks, int offsetX, int offsetY, float[] colors) {
        RenderSystem.disableDepthTest();
        GL11.glClear((int)256);
        this.drawBackground(context, offsetX += 8, offsetY -= 82, colors);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        DiffuseLighting.enableGuiDepthLighting();
        int row = 0;
        int i = 0;
        for (ItemStack itemStack : itemStacks) {
            context.drawItem(itemStack, offsetX + 8 + i * 18, offsetY + 7 + row * 18);
            context.drawItemInSlot(NameTags.mc.textRenderer, itemStack, offsetX + 8 + i * 18, offsetY + 7 + row * 18);
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

    private static enum OutlineColor {
        Sync,
        Custom,
        None,
        New;

    }

    public static enum Font {
        Fancy,
        Fast;

    }

    public static enum Armor {
        None,
        Full,
        Durability;

    }

    public static enum Health {
        Number,
        Hearts,
        Dots;

    }

    private static enum HeartType {
        CONTAINER(Identifier.of((String)"hud/heart/container"), Identifier.of((String)"hud/heart/container")),
        NORMAL(Identifier.of((String)"hud/heart/full"), Identifier.of((String)"hud/heart/half")),
        ABSORBING(Identifier.of((String)"hud/heart/absorbing_full"), Identifier.of((String)"hud/heart/absorbing_half"));

        private final Identifier fullTexture;
        private final Identifier halfTexture;

        private HeartType(Identifier fullTexture, Identifier halfTexture) {
            this.fullTexture = fullTexture;
            this.halfTexture = halfTexture;
        }

        public Identifier getTexture(boolean half) {
            return half ? this.halfTexture : this.fullTexture;
        }
    }
}

