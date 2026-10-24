/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.utility.render.animation;

import thunder.hack.utility.math.FrameRateCounter;
import thunder.hack.utility.math.MathUtility;

public class AnimationUtility {
    public static float deltaTime() {
        return FrameRateCounter.INSTANCE.getFps() > 5 ? 1.0f / (float)FrameRateCounter.INSTANCE.getFps() : 0.016f;
    }

    public static float fast(float end, float start, float multiple) {
        float clampedDelta = MathUtility.clamp(AnimationUtility.deltaTime() * multiple, 0.0f, 1.0f);
        return (1.0f - clampedDelta) * end + clampedDelta * start;
    }
}

