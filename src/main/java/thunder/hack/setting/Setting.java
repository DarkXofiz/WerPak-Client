/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.setting;

import java.util.function.Predicate;
import thunder.hack.ThunderHack;
import thunder.hack.events.impl.EventSetting;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.impl.Bind;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.setting.impl.EnumConverter;
import thunder.hack.setting.impl.ItemSelectSetting;
import thunder.hack.setting.impl.PositionSetting;
import thunder.hack.setting.impl.SettingGroup;

public class Setting<T> {
    private final String name;
    private final T defaultValue;
    private T value;
    private T plannedValue;
    private T min;
    private T max;
    public Setting<?> group = null;
    private boolean hasRestriction;
    private Predicate<T> visibility;
    private Module module;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.plannedValue = defaultValue;
    }

    public Setting(String name, T defaultValue, T min, T max) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.plannedValue = defaultValue;
        this.hasRestriction = true;
    }

    public Setting(String name, T defaultValue, T min, T max, Predicate<T> visibility) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.plannedValue = defaultValue;
        this.visibility = visibility;
        this.hasRestriction = true;
    }

    public Setting(String name, T defaultValue, Predicate<T> visibility) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.visibility = visibility;
        this.plannedValue = defaultValue;
    }

    public static Enum get(Enum clazz) {
        int index = EnumConverter.currentEnum(clazz);
        for (int i = 0; i < ((Enum[])clazz.getClass().getEnumConstants()).length; ++i) {
            Enum e = ((Enum[])clazz.getClass().getEnumConstants())[i];
            if (i != index + 1) continue;
            return e;
        }
        return ((Enum[])clazz.getClass().getEnumConstants())[0];
    }

    public String getName() {
        return this.name;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.setValueSilent(value);
        ThunderHack.EVENT_BUS.post(new EventSetting(this));
    }

    public void setValueSilent(T value) {
        this.setPlannedValue(value);
        if (this.hasRestriction) {
            if (((Number)this.min).floatValue() > ((Number)value).floatValue()) {
                this.setPlannedValue(this.min);
            }
            if (((Number)this.max).floatValue() < ((Number)value).floatValue()) {
                this.setPlannedValue(this.max);
            }
        }
        this.value = this.plannedValue;
    }

    public float getPow2Value() {
        if (this.value instanceof Float) {
            return ((Float)this.value).floatValue() * ((Float)this.value).floatValue();
        }
        if (this.value instanceof Integer) {
            return (Integer)this.value * (Integer)this.value;
        }
        return 0.0f;
    }

    public void setPlannedValue(T value) {
        this.plannedValue = value;
    }

    public T getMin() {
        return this.min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return this.max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public Module getModule() {
        return this.module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String currentEnumName() {
        return EnumConverter.getProperName((Enum)this.value);
    }

    public String[] getModes() {
        return EnumConverter.getNames((Enum)this.value);
    }

    public void setEnum(Enum mod) {
        this.plannedValue = mod;
    }

    public void increaseEnum() {
        this.plannedValue = EnumConverter.increaseEnum((Enum)this.value);
        this.value = this.plannedValue;
        ThunderHack.EVENT_BUS.post(new EventSetting(this));
    }

    public void setEnumByNumber(int id) {
        this.plannedValue = EnumConverter.setEnumInt((Enum)this.value, id);
        this.value = this.plannedValue;
        ThunderHack.EVENT_BUS.post(new EventSetting(this));
    }

    public boolean isNumberSetting() {
        return this.value instanceof Double || this.value instanceof Integer || this.value instanceof Short || this.value instanceof Long || this.value instanceof Float;
    }

    public boolean isInteger() {
        return this.value instanceof Integer;
    }

    public boolean isFloat() {
        return this.value instanceof Float;
    }

    public boolean isEnumSetting() {
        return this.value.getClass().isEnum();
    }

    public boolean isBindSetting() {
        return this.value instanceof Bind;
    }

    public boolean isStringSetting() {
        return this.value instanceof String;
    }

    public boolean isItemSelectSetting() {
        return this.value instanceof ItemSelectSetting;
    }

    public boolean isPositionSetting() {
        return this.value instanceof PositionSetting;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public boolean hasRestriction() {
        return this.hasRestriction;
    }

    public Setting<T> addToGroup(Setting<?> group) {
        this.group = group;
        return this;
    }

    public boolean isVisible() {
        if (this.group != null) {
            SettingGroup p;
            BooleanSettingGroup bp;
            Object obj = this.group.getValue();
            if (obj instanceof BooleanSettingGroup && !(bp = (BooleanSettingGroup)obj).isExtended()) {
                return false;
            }
            obj = this.group.getValue();
            if (obj instanceof SettingGroup && !(p = (SettingGroup)obj).isExtended()) {
                return false;
            }
        }
        if (this.visibility == null) {
            return true;
        }
        return this.visibility.test(this.getValue());
    }

    public boolean is(T v) {
        return this.value == v;
    }

    public boolean not(T v) {
        return this.value != v;
    }
}

