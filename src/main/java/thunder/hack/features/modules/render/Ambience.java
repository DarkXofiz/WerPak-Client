/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
 */
package thunder.hack.features.modules.render;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.setting.impl.ColorSetting;

public class Ambience
extends Module {
    public static final Setting<BooleanSettingGroup> fogModify = new Setting<BooleanSettingGroup>("FogModify", new BooleanSettingGroup(true));
    public static final Setting<Integer> fogStart = new Setting<Integer>("FogStart", 0, 0, 256).addToGroup(fogModify);
    public static final Setting<Integer> fogEnd = new Setting<Integer>("FogEnd", 64, 10, 256).addToGroup(fogModify);
    public static final Setting<ColorSetting> fogColor = new Setting<ColorSetting>("FogColor", new ColorSetting(new Color(11075839))).addToGroup(fogModify);
    public final Setting<Boolean> ctime = new Setting<Boolean>("ChangeTime", false);
    public final Setting<Integer> ctimeVal = new Setting<Integer>("Time", 21, 0, 23);
    public final Setting<ColorSetting> color = new Setting<ColorSetting>("Color", new ColorSetting(new Color(255, 255, 255).getRGB()));
    long oldTime;

    public Ambience() {
        super("Ambience", Module.Category.RENDER);
    }

    @Override
    public void onEnable() {
        if (Ambience.mc.world != null) {
            this.oldTime = Ambience.mc.world.getTimeOfDay();
        }
    }

    @Override
    public void onDisable() {
        if (Ambience.mc.world != null) {
            Ambience.mc.world.setTimeOfDay(this.oldTime);
        }
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket && this.ctime.getValue().booleanValue()) {
            this.oldTime = ((WorldTimeUpdateS2CPacket)event.getPacket()).getTimeOfDay();
            event.cancel();
        }
    }

    @Override
    public void onUpdate() {
        if (Ambience.mc.world != null && this.ctime.getValue().booleanValue()) {
            Ambience.mc.world.setTimeOfDay((long)(this.ctimeVal.getValue() * 1000));
        }
    }
}

