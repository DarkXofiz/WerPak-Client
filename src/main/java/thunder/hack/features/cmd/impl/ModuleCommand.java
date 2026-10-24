/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.util.Formatting
 *  net.minecraft.command.CommandSource
 */
package thunder.hack.features.cmd.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Objects;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.cmd.args.ModuleArgumentType;
import thunder.hack.features.cmd.args.SettingArgumentType;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.impl.EnumConverter;
import thunder.hack.setting.impl.PositionSetting;

public class ModuleCommand
extends Command {
    public ModuleCommand() {
        super("module", "modules");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(ModuleCommand.arg("module", ModuleArgumentType.create()).executes(context -> {
            Module module = (Module)context.getArgument("module", Module.class);
            ModuleCommand.sendMessage(module.getDisplayName() + " : " + I18n.translate((String)module.getDescription(), (Object[])new Object[0]));
            for (Setting setting2 : module.getSettings()) {
                ModuleCommand.sendMessage(setting2.getName() + " : " + String.valueOf(setting2.getValue()));
            }
            return 1;
        }));
        builder.then(((RequiredArgumentBuilder)ModuleCommand.arg("module", ModuleArgumentType.create()).then(ModuleCommand.literal("reset").executes(context -> {
            Module module = (Module)context.getArgument("module", Module.class);
            for (Setting s : module.getSettings()) {
                Object patt0$temp = s.getValue();
                if (patt0$temp instanceof ColorSetting) {
                    ColorSetting cs = (ColorSetting)patt0$temp;
                    cs.setDefault();
                    continue;
                }
                s.setValue(s.getDefaultValue());
            }
            return 1;
        }))).then(ModuleCommand.arg("setting", SettingArgumentType.create()).then(ModuleCommand.arg("settingValue", StringArgumentType.greedyString()).executes(context -> {
            Module module = (Module)context.getArgument("module", Module.class);
            String settingName = (String)context.getArgument("setting", String.class);
            String settingValue = (String)context.getArgument("settingValue", String.class);
            Setting<?> setting = null;
            for (Setting set : module.getSettings()) {
                if (!Objects.equals(set.getName(), settingName)) continue;
                setting = set;
            }
            if (setting == null) {
                ModuleCommand.sendMessage("No such setting");
                return 1;
            }
            JsonParser jp = new JsonParser();
            if (setting.getValue().getClass().getSimpleName().equalsIgnoreCase("String")) {
                setting.setValue(settingValue);
                ModuleCommand.sendMessage(String.valueOf(Formatting.DARK_GRAY) + module.getName() + " " + setting.getName() + (ClientSettings.isRu() ? " \u0431\u044b\u043b \u0432\u044b\u0441\u0442\u0430\u0432\u043b\u0435\u043d " : " has been set to ") + settingValue);
                return 1;
            }
            try {
                if (setting.getName().equalsIgnoreCase("Enabled")) {
                    if (settingValue.equalsIgnoreCase("true")) {
                        module.enable();
                    }
                    if (settingValue.equalsIgnoreCase("false")) {
                        module.disable();
                    }
                }
                ModuleCommand.setCommandValue(module, setting, jp.parse(settingValue));
            }
            catch (Exception e) {
                ModuleCommand.sendMessage((ClientSettings.isRu() ? "\u041d\u0435\u0432\u0435\u0440\u043d\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435! \u042d\u0442\u0430 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u0442\u0440\u0435\u0431\u0443\u0435\u0442 \u0442\u0438\u043f: " : "Bad Value! This setting requires a: ") + setting.getValue().getClass().getSimpleName());
                return 1;
            }
            if (settingValue.contains("toggle")) {
                ModuleCommand.sendMessage(String.valueOf(Formatting.GRAY) + module.getName() + " " + setting.getName() + (ClientSettings.isRu() ? " \u0431\u044b\u043b \u043f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0435\u043d" : " has been toggled"));
            } else {
                ModuleCommand.sendMessage(String.valueOf(Formatting.GRAY) + module.getName() + " " + setting.getName() + (ClientSettings.isRu() ? " \u0431\u044b\u043b \u0432\u044b\u0441\u0442\u0430\u0432\u043b\u0435\u043d " : " has been set to ") + settingValue);
            }
            return 1;
        }))));
        builder.executes(context -> {
            ModuleCommand.sendMessage("Modules: ");
            for (Module.Category category : Managers.MODULE.getCategories()) {
                StringBuilder modules = new StringBuilder(category.getName() + ": ");
                for (Module module1 : Managers.MODULE.getModulesByCategory(category)) {
                    modules.append(module1.isEnabled() ? Formatting.GREEN : Formatting.RED).append(module1.getName()).append(Formatting.WHITE).append(", ");
                }
                ModuleCommand.sendMessage(modules.toString());
            }
            return 1;
        });
    }

    public static void setCommandValue(@NotNull Module feature, Setting setting, JsonElement element) {
        block25: for (Setting checkSetting : feature.getSettings()) {
            if (!Objects.equals(setting.getName(), checkSetting.getName())) continue;
            switch (checkSetting.getValue().getClass().getSimpleName()) {
                case "SettingGroup": 
                case "Bind": {
                    return;
                }
                case "Boolean": {
                    if (element.getAsString().equals("toggle")) {
                        checkSetting.setValue((Boolean)checkSetting.getValue() == false);
                        return;
                    }
                    checkSetting.setValue(element.getAsBoolean());
                    return;
                }
                case "BooleanSettingGroup": {
                    ((BooleanSettingGroup)checkSetting.getValue()).setEnabled(element.getAsBoolean());
                    continue block25;
                }
                case "Double": {
                    checkSetting.setValue(element.getAsDouble());
                    return;
                }
                case "Float": {
                    checkSetting.setValue(Float.valueOf(element.getAsFloat()));
                    return;
                }
                case "Integer": {
                    checkSetting.setValue(element.getAsInt());
                    return;
                }
                case "String": {
                    String str = element.getAsString();
                    checkSetting.setValue(str.replace("_", " "));
                    return;
                }
                case "ColorSetting": {
                    JsonArray array = element.getAsJsonArray();
                    ((ColorSetting)checkSetting.getValue()).setColor(array.get(0).getAsInt());
                    ((ColorSetting)checkSetting.getValue()).setRainbow(array.get(1).getAsBoolean());
                    return;
                }
                case "PositionSetting": {
                    JsonArray array3 = element.getAsJsonArray();
                    ((PositionSetting)checkSetting.getValue()).setX(array3.get(0).getAsFloat());
                    ((PositionSetting)checkSetting.getValue()).setY(array3.get(1).getAsFloat());
                    return;
                }
            }
            try {
                EnumConverter converter = new EnumConverter(((Enum)checkSetting.getValue()).getClass());
                Enum value = converter.doBackward(element);
                checkSetting.setValue(value == null ? checkSetting.getDefaultValue() : value);
            }
            catch (Exception exception) {}
        }
    }
}

