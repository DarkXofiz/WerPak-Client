/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.GameMode
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.scoreboard.Scoreboard
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.PlayerListHud
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.util.Nullables
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package thunder.hack.injection;

import java.util.Comparator;
import java.util.List;
import net.minecraft.world.GameMode;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Nullables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.utility.render.animation.AnimationUtility;

@Mixin(value={PlayerListHud.class})
public class MixinPlayerListHud {
    private static final Comparator<PlayerListEntry> ENTRY_ORDERING = Comparator.<PlayerListEntry>comparingInt(entry -> entry.getGameMode() == GameMode.SPECTATOR ? 1 : 0).thenComparing(entry -> Nullables.mapOrElse(entry.getScoreboardTeam(), Team::getName, "")).thenComparing(entry -> entry.getProfile().getName(), String::compareToIgnoreCase);
    private float animation = 0.0f;

    @Inject(method={"collectPlayerEntries"}, at={@At(value="HEAD")}, cancellable=true)
    private void collectPlayerEntriesHook(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
        if (ClientSettings.futureCompatibility.getValue().booleanValue()) {
            return;
        }
        if (ThunderHack.isFuturePresent()) {
            return;
        }
        cir.setReturnValue(Module.mc.player.networkHandler.getListedPlayerListEntries().stream().sorted(ENTRY_ORDERING).limit(80L).toList());
    }

    @Inject(method={"render"}, at={@At(value="HEAD")})
    public void renderHead(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        if (ModuleManager.smoothTab.isEnabled()) {
            this.animation = AnimationUtility.fast(this.animation, 1.0f, ModuleManager.smoothTab.animationSpeed.getValue().floatValue());
            context.getMatrices().push();
            context.getMatrices().translate(0.0f, -1000.0f * (1.0f - this.animation), 0.0f);
        }
    }

    @Inject(method={"render"}, at={@At(value="RETURN")})
    public void renderReturn(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        if (ModuleManager.smoothTab.isEnabled()) {
            context.getMatrices().pop();
        } else {
            this.animation = 0.0f;
        }
    }
}

