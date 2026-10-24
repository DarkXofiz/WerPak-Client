/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.scoreboard.ScoreboardEntry
 *  org.spongepowered.asm.mixin.Mixin
 */
package thunder.hack.injection;

import net.minecraft.scoreboard.ScoreboardEntry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={ScoreboardEntry.class})
public class MixinScoreboardPlayerScore {
}

