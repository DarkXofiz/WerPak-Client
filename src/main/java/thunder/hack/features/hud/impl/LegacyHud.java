/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.Pair
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.registry.RegistryKeys
 *  org.apache.commons.lang3.StringUtils
 */
package thunder.hack.features.hud.impl;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.util.Formatting;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemConvertible;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Pair;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.registry.RegistryKeys;
import org.apache.commons.lang3.StringUtils;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.hud.impl.PotionHud;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.font.FontRenderer;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.math.FrameRateCounter;
import thunder.hack.utility.math.MathUtility;

public class LegacyHud
extends Module {
    private static final ItemStack totem = new ItemStack((ItemConvertible)Items.TOTEM_OF_UNDYING);
    private final Setting<Font> customFont = new Setting<Font>("Font", Font.Minecraft);
    private final Setting<ColorSetting> colorSetting = new Setting<ColorSetting>("Color", new ColorSetting(new Color(30719)));
    private final Setting<Boolean> renderingUp = new Setting<Boolean>("RenderingUp", false);
    private final Setting<Boolean> waterMark = new Setting<Boolean>("Watermark", false);
    private final Setting<Boolean> arrayList = new Setting<Boolean>("ActiveModules", false);
    private final Setting<Boolean> coords = new Setting<Boolean>("Coords", false);
    private final Setting<Boolean> direction = new Setting<Boolean>("Direction", false);
    private final Setting<Boolean> armor = new Setting<Boolean>("Armor", false);
    private final Setting<Boolean> totems = new Setting<Boolean>("Totems", false);
    private final Setting<Boolean> greeter = new Setting<Boolean>("Welcomer", false);
    private final Setting<Boolean> speed = new Setting<Boolean>("Speed", false);
    private final Setting<Boolean> bps = new Setting<Boolean>("BPS", false, v -> this.speed.getValue());
    public final Setting<Boolean> potions = new Setting<Boolean>("Potions", false);
    private final Setting<Boolean> ping = new Setting<Boolean>("Ping", false);
    private final Setting<Boolean> tps = new Setting<Boolean>("TPS", false);
    private final Setting<Boolean> extraTps = new Setting<Boolean>("ExtraTPS", true, v -> this.tps.getValue());
    private final Setting<Boolean> offhandDurability = new Setting<Boolean>("OffhandDurability", false);
    private final Setting<Boolean> mainhandDurability = new Setting<Boolean>("MainhandDurability", false);
    private final Setting<Boolean> fps = new Setting<Boolean>("FPS", false);
    private final Setting<Boolean> chests = new Setting<Boolean>("Chests", false);
    private final Setting<Boolean> worldTime = new Setting<Boolean>("WorldTime", false);
    private final Setting<Boolean> biome = new Setting<Boolean>("Biome", false);
    public Setting<Boolean> time = new Setting<Boolean>("Time", false);
    public Setting<Integer> waterMarkY = new Setting<Integer>("WatermarkPosY", 2, 0, 20, v -> this.waterMark.getValue());
    private int color;

    public LegacyHud() {
        super("LegacyHud", Module.Category.HUD);
    }

    @Override
    public void onRender2D(DrawContext context) {
        String str;
        int i;
        Object str2;
        int j;
        if (LegacyHud.fullNullCheck()) {
            return;
        }
        int width = mc.getWindow().getScaledWidth();
        int height = mc.getWindow().getScaledHeight();
        int offset = switch (this.customFont.getValue().ordinal()) {
            case 0 -> 10;
            case 2 -> 9;
            default -> 8;
        };
        this.color = this.colorSetting.getValue().getColor();
        if (this.waterMark.getValue().booleanValue()) {
            this.drawText(context, "thunderhack V15", 2, this.waterMarkY.getValue());
        }
        int n = j = LegacyHud.mc.currentScreen instanceof ChatScreen && this.renderingUp.getValue() == false ? 14 : 0;
        if (this.arrayList.getValue().booleanValue()) {
            for (Module module2 : Managers.MODULE.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> this.getStringWidth(module.getFullArrayString()) * -1)).toList()) {
                if (!module2.isDrawn()) continue;
                str2 = module2.getDisplayName() + String.valueOf(Formatting.GRAY) + (String)(module2.getDisplayInfo() != null ? " [" + String.valueOf(Formatting.WHITE) + module2.getDisplayInfo() + String.valueOf(Formatting.GRAY) + "]" : "");
                if (this.renderingUp.getValue().booleanValue()) {
                    this.drawText(context, (String)str2, width - 2 - this.getStringWidth((String)str2), 2 + j * offset);
                    ++j;
                    continue;
                }
                this.drawText(context, (String)str2, width - 2 - this.getStringWidth((String)str2), height - (j += offset));
            }
        }
        int n2 = LegacyHud.mc.currentScreen instanceof ChatScreen && this.renderingUp.getValue() != false ? 13 : (i = this.renderingUp.getValue() != false ? -2 : 0);
        if (this.potions.getValue().booleanValue()) {
            var effects = new ArrayList<>(LegacyHud.mc.player.getStatusEffects());
            for (StatusEffectInstance potionEffect : effects) {
                StatusEffect potion = (StatusEffect)potionEffect.getEffectType().value();
                String power = "";
                switch (potionEffect.getAmplifier()) {
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
                String s = potion.getName().getString() + " " + power;
                String s2 = PotionHud.getDuration(potionEffect);
                Color c = new Color(((StatusEffect)potionEffect.getEffectType().value()).getColor());
                if (this.renderingUp.getValue().booleanValue()) {
                    this.drawText(context, s + " " + s2, width - this.getStringWidth(s + " " + s2) - 2, height - 2 - (i += offset), c.getRGB());
                    continue;
                }
                this.drawText(context, s + " " + s2, width - this.getStringWidth(s + " " + s2) - 2, 2 + i++ * offset, c.getRGB());
            }
        }
        if (this.worldTime.getValue().booleanValue()) {
            String str22 = "WorldTime: " + String.valueOf(Formatting.WHITE) + LegacyHud.mc.world.getTimeOfDay() % 24000L;
            this.drawText(context, str22, width - this.getStringWidth(str22) - 2, this.renderingUp.getValue() != false ? height - 2 - (i += offset) : 2 + i++ * offset);
        }
        if (this.mainhandDurability.getValue().booleanValue()) {
            str = "MainHand" + String.valueOf(Formatting.WHITE) + " [" + (LegacyHud.mc.player.getMainHandStack().getMaxDamage() - LegacyHud.mc.player.getMainHandStack().getDamage()) + "]";
            this.drawText(context, str, width - this.getStringWidth(str) - 2, this.renderingUp.getValue() != false ? height - 2 - (i += offset) : 2 + i++ * offset);
        }
        if (this.tps.getValue().booleanValue()) {
            str = "TPS " + String.valueOf(Formatting.WHITE) + Managers.SERVER.getTPS() + (String)(this.extraTps.getValue() != false ? " [" + Managers.SERVER.getTPS2() + "]" : "");
            this.drawText(context, str, width - this.getStringWidth(str) - 2, this.renderingUp.getValue() != false ? height - 2 - (i += offset) : 2 + i++ * offset);
        }
        if (this.speed.getValue().booleanValue()) {
            str = "Speed " + String.valueOf(Formatting.WHITE) + MathUtility.round(Managers.PLAYER.currentPlayerSpeed * (this.bps.getValue() != false ? 20.0f : 72.0f) * ThunderHack.TICK_TIMER) + (this.bps.getValue() != false ? " b/s" : " km/h");
            this.drawText(context, str, width - this.getStringWidth(str) - 2, this.renderingUp.getValue() != false ? height - 2 - (i += offset) : 2 + i++ * offset);
        }
        if (this.chests.getValue().booleanValue()) {
            Pair<Integer, Integer> chests = ModuleManager.chestCounter.getChestCount();
            str2 = "Chests: " + String.valueOf(Formatting.WHITE) + "S:" + String.valueOf(chests.getLeft()) + " D:" + String.valueOf(chests.getRight());
            this.drawText(context, (String)str2, width - this.getStringWidth((String)str2) - 2, this.renderingUp.getValue() != false ? height - 2 - (i += offset) : 2 + i++ * offset);
        }
        if (this.biome.getValue().booleanValue()) {
            String str3 = "Biome: " + String.valueOf(Formatting.WHITE) + LegacyHud.biome();
            this.drawText(context, str3, width - this.getStringWidth(str3) - 2, this.renderingUp.getValue() != false ? height - 2 - (i += offset) : 2 + i++ * offset);
        }
        if (this.time.getValue().booleanValue()) {
            str = "Time " + String.valueOf(Formatting.WHITE) + new SimpleDateFormat("h:mm a").format(new Date());
            this.drawText(context, str, width - this.getStringWidth(str) - 2, this.renderingUp.getValue() != false ? height - 2 - (i += offset) : 2 + i++ * offset);
        }
        if (this.offhandDurability.getValue().booleanValue()) {
            str = "OffHand" + String.valueOf(Formatting.WHITE) + " [" + (LegacyHud.mc.player.getOffHandStack().getMaxDamage() - LegacyHud.mc.player.getOffHandStack().getDamage()) + "]";
            this.drawText(context, str, width - this.getStringWidth(str) - 2, this.renderingUp.getValue() != false ? height - 2 - (i += offset) : 2 + i++ * offset);
        }
        if (this.ping.getValue().booleanValue()) {
            String str1 = "Ping " + String.valueOf(Formatting.WHITE) + Managers.SERVER.getPing();
            this.drawText(context, str1, width - this.getStringWidth(str1) - 2, this.renderingUp.getValue() != false ? height - 2 - (i += offset) : 2 + i++ * offset);
        }
        if (this.fps.getValue().booleanValue()) {
            String fpsText = "FPS " + String.valueOf(Formatting.WHITE) + FrameRateCounter.INSTANCE.getFps();
            this.drawText(context, fpsText, width - this.getStringWidth(fpsText) - 2, this.renderingUp.getValue() != false ? height - 2 - (i += offset) : 2 + i++ * offset);
        }
        boolean inHell = Objects.equals(LegacyHud.mc.world.getRegistryKey().getValue().getPath(), "the_nether");
        int posX = (int)LegacyHud.mc.player.getX();
        int posY = (int)LegacyHud.mc.player.getY();
        int posZ = (int)LegacyHud.mc.player.getZ();
        float nether = !inHell ? 0.125f : 8.0f;
        int hposX = (int)(LegacyHud.mc.player.getX() * (double)nether);
        int hposZ = (int)(LegacyHud.mc.player.getZ() * (double)nether);
        i = LegacyHud.mc.currentScreen instanceof ChatScreen ? 14 : 0;
        String coordinates = String.valueOf(Formatting.WHITE) + "XYZ " + String.valueOf(Formatting.RESET) + (inHell ? posX + ", " + posY + ", " + posZ + String.valueOf(Formatting.WHITE) + " [" + String.valueOf(Formatting.RESET) + hposX + ", " + hposZ + String.valueOf(Formatting.WHITE) + "]" + String.valueOf(Formatting.RESET) : posX + ", " + posY + ", " + posZ + String.valueOf(Formatting.WHITE) + " [" + String.valueOf(Formatting.RESET) + hposX + ", " + hposZ + String.valueOf(Formatting.WHITE) + "]");
        Object direction1 = "";
        i += offset;
        if (this.direction.getValue().booleanValue()) {
            switch (LegacyHud.mc.player.getHorizontalFacing()) {
                case EAST: {
                    direction1 = "East" + String.valueOf(Formatting.WHITE) + " [+X]";
                    break;
                }
                case WEST: {
                    direction1 = "West" + String.valueOf(Formatting.WHITE) + " [-X]";
                    break;
                }
                case NORTH: {
                    direction1 = "North" + String.valueOf(Formatting.WHITE) + " [-Z]";
                    break;
                }
                case SOUTH: {
                    direction1 = "South" + String.valueOf(Formatting.WHITE) + " [+Z]";
                }
            }
            this.drawText(context, (String)direction1, 2, height - i - 11);
        }
        if (this.coords.getValue().booleanValue()) {
            this.drawText(context, coordinates, 2, height - i);
        }
        if (this.armor.getValue().booleanValue()) {
            this.renderArmorHUD(true, context);
        }
        if (this.totems.getValue().booleanValue()) {
            this.renderTotemHUD(context);
        }
        if (this.greeter.getValue().booleanValue()) {
            this.renderGreeter(context);
        }
    }

    private void drawText(DrawContext context, String str, int x, int y, int color) {
        if (!this.customFont.getValue().equals((Object)Font.Minecraft)) {
            FontRenderer adapter = switch (this.customFont.getValue().ordinal()) {
                case 2 -> FontRenderers.monsterrat;
                case 3 -> FontRenderers.sf_medium;
                default -> FontRenderers.modules;
            };
            adapter.drawString(context.getMatrices(), str.replace(String.valueOf(Formatting.WHITE), ""), (double)x + 0.5, (double)y + 0.5, Color.BLACK.getRGB());
            adapter.drawString(context.getMatrices(), str, (double)x, (double)y, color);
            return;
        }
        context.drawText(LegacyHud.mc.textRenderer, str, x, y, color, true);
    }

    private void drawText(DrawContext context, String str, int x, int y) {
        if (!this.customFont.getValue().equals((Object)Font.Minecraft)) {
            FontRenderer adapter = switch (this.customFont.getValue().ordinal()) {
                case 2 -> FontRenderers.monsterrat;
                case 3 -> FontRenderers.sf_medium;
                default -> FontRenderers.modules;
            };
            adapter.drawString(context.getMatrices(), str.replace(String.valueOf(Formatting.WHITE), ""), (double)x + 0.5, (double)y + 0.5, Color.BLACK.getRGB());
            adapter.drawString(context.getMatrices(), str, (double)x, (double)y, this.color);
            return;
        }
        context.drawText(LegacyHud.mc.textRenderer, str, x, y, this.color, true);
    }

    private int getStringWidth(String str) {
        switch (this.customFont.getValue().ordinal()) {
            case 2: {
                return (int)FontRenderers.monsterrat.getStringWidth(str);
            }
            case 3: {
                return (int)FontRenderers.sf_medium.getStringWidth(str);
            }
            case 0: {
                return LegacyHud.mc.textRenderer.getWidth(str);
            }
        }
        return (int)FontRenderers.modules.getStringWidth(str);
    }

    public void renderGreeter(DrawContext context) {
        String text = "Good " + LegacyHud.getTimeOfDay() + LegacyHud.mc.player.getName().getString();
        this.drawText(context, text, (int)((float)mc.getWindow().getScaledWidth() / 2.0f - (float)this.getStringWidth(text) / 2.0f + 2.0f), 2);
    }

    public static String getTimeOfDay() {
        int timeOfDay = Calendar.getInstance().get(11);
        if (timeOfDay < 12) {
            return "Morning ";
        }
        if (timeOfDay < 16) {
            return "Afternoon ";
        }
        if (timeOfDay < 21) {
            return "Evening ";
        }
        return "Night ";
    }

    public void renderTotemHUD(DrawContext context) {
        int width = mc.getWindow().getScaledWidth();
        int height = mc.getWindow().getScaledHeight();
        int totems = LegacyHud.mc.player.getInventory().main.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        int u = LegacyHud.mc.player.getMaxAir();
        int v = Math.min(LegacyHud.mc.player.getAir(), u);
        if (LegacyHud.mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += LegacyHud.mc.player.getOffHandStack().getCount();
        }
        if (totems > 0) {
            int i = width / 2;
            int y = height - 55 - (LegacyHud.mc.player.isSubmergedInWater() || v < u ? 10 : 0);
            int x = i - 189 + 180 + 2;
            context.drawItem(totem, x, y);
            context.drawItemInSlot(LegacyHud.mc.textRenderer, totem, x, y);
            this.drawText(context, "" + totems, 8 + (int)((float)x - (float)this.getStringWidth("" + totems) / 2.0f), y - 7, 0xFFFFFF);
        }
    }

    private static String biome() {
        if (LegacyHud.mc.player == null || LegacyHud.mc.world == null) {
            return null;
        }
        Identifier id = LegacyHud.mc.world.getRegistryManager().get(RegistryKeys.BIOME).getId((Object)((Biome)LegacyHud.mc.world.getBiome(LegacyHud.mc.player.getBlockPos()).value()));
        if (id == null) {
            return "Unknown";
        }
        return Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    public void renderArmorHUD(boolean percent, DrawContext context) {
        int i = 0;
        int u = LegacyHud.mc.player.getMaxAir();
        int v = Math.min(LegacyHud.mc.player.getAir(), u);
        int y = mc.getWindow().getScaledHeight() - 55 - (LegacyHud.mc.player.isSubmergedInWater() || v < u ? 10 : 0);
        for (ItemStack is : LegacyHud.mc.player.getInventory().armor) {
            ++i;
            if (is.isEmpty()) continue;
            int x = mc.getWindow().getScaledWidth() / 2 - 90 + (9 - i) * 20 + 2;
            context.drawItem(is, x, y);
            context.drawItemInSlot(LegacyHud.mc.textRenderer, is, x, y);
            String s = is.getCount() > 1 ? "" + is.getCount() : "";
            this.drawText(context, s, x + 19 - 2 - this.getStringWidth(s), y + 9, 0xFFFFFF);
            if (!percent) continue;
            float green = (float)(is.getMaxDamage() - is.getDamage()) / (float)is.getMaxDamage();
            float red = 1.0f - green;
            int dmg = 100 - (int)(red * 100.0f);
            this.drawText(context, "" + dmg, x + 8 - this.getStringWidth("" + dmg) / 2, y - 11, new Color((int)MathUtility.clamp(red * 255.0f, 0.0f, 255.0f), (int)MathUtility.clamp(green * 255.0f, 0.0f, 255.0f), 0).getRGB());
        }
    }

    private static enum Font {
        Minecraft,
        Comfortaa,
        Monsterrat,
        SF;

    }
}

