/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.scoreboard.Scoreboard
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package thunder.hack.injection;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Scoreboard.class})
public abstract class MixinScoreBoard {
    @Final
    @Shadow
    private Object2ObjectMap<String, Team> teamsByScoreHolder;

    @Inject(method={"removeScoreHolderFromTeam"}, at={@At(value="HEAD")}, cancellable=true)
    public void removeScoreHolderFromTeamHook(String scoreHolderName, Team team, CallbackInfo ci) {
        ci.cancel();
        if (this.teamsByScoreHolder.get((Object)scoreHolderName) != team) {
            return;
        }
        this.teamsByScoreHolder.remove((Object)scoreHolderName);
        team.getPlayerList().remove(scoreHolderName);
    }
}

