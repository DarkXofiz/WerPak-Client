/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
 *  net.minecraft.client.network.PlayerListEntry
 */
package thunder.hack.core.manager.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.client.network.PlayerListEntry;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.manager.IManager;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.utility.math.MathUtility;

public class ServerManager
implements IManager {
    private final ArrayDeque<Float> tpsResult = new ArrayDeque(20);
    private long time;
    private long tickTime;
    private float tps;

    public float getTPS() {
        return ServerManager.round2(this.tps);
    }

    public float getTPS2() {
        return ServerManager.round2(20.0f * ((float)this.tickTime / 1000.0f));
    }

    public float getTPSFactor() {
        return (float)this.tickTime / 1000.0f;
    }

    public static float round2(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    @EventHandler
    public void onPacketReceive( @NotNull PacketEvent.Receive event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            if (this.time != 0L) {
                this.tickTime = System.currentTimeMillis() - this.time;
                if (this.tpsResult.size() > 20) {
                    this.tpsResult.poll();
                }
                this.tpsResult.add(Float.valueOf(20.0f * (1000.0f / (float)this.tickTime)));
                float average = 0.0f;
                for (Float value : this.tpsResult) {
                    average += MathUtility.clamp(value.floatValue(), 0.0f, 20.0f);
                }
                this.tps = average / (float)this.tpsResult.size();
            }
            this.time = System.currentTimeMillis();
        }
    }

    public int getPing() {
        if (mc.getNetworkHandler() == null || ServerManager.mc.player == null) {
            return 0;
        }
        if (ModuleManager.fastLatency.isEnabled()) {
            return ModuleManager.fastLatency.resolvedPing;
        }
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(ServerManager.mc.player.getUuid());
        if (playerListEntry == null) {
            return 0;
        }
        return playerListEntry.getLatency();
    }
}

