/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket
 *  net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.features.modules.client;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;
import thunder.hack.utility.math.MathUtility;

public final class FastLatency
extends Module {
    private final Setting<Integer> delay = new Setting<Integer>("Delay", 80, 0, 1000);
    private final Timer timer = new Timer();
    private final Timer limitTimer = new Timer();
    private long ping;
    public int resolvedPing;

    public FastLatency() {
        super("FastLatency", Module.Category.CLIENT);
    }

    @Override
    public void onRender3D(MatrixStack stack) {
        if (this.timer.passedMs(5000L) && this.limitTimer.every(this.delay.getValue().intValue())) {
            this.sendPacket((Packet<?>)new RequestCommandCompletionsC2SPacket(1337, "w "));
            this.ping = System.currentTimeMillis();
            this.timer.reset();
        }
    }

    @EventHandler
    public void onPacketReceive(@NotNull PacketEvent.Receive e) {
        CommandSuggestionsS2CPacket c;
        Object t = e.getPacket();
        if (t instanceof CommandSuggestionsS2CPacket && (c = (CommandSuggestionsS2CPacket)t).completionId() == 1337) {
            this.resolvedPing = (int)MathUtility.clamp(System.currentTimeMillis() - this.ping, 0.0f, 1000.0f);
            this.timer.setMs(5000L);
        }
    }
}

