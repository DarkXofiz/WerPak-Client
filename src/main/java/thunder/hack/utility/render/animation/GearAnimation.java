/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.utility.render.animation;

import thunder.hack.features.modules.client.ClickGui;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public class GearAnimation {
    private float espValue = 1.0f;
    private float prevEspValue;
    private float espSpeed = 1.0f;
    private boolean flipSpeed;

    public float getValue() {
        return Render2DEngine.interpolateFloat(this.prevEspValue, this.espValue, Render3DEngine.getTickDelta());
    }

    public void tick() {
        this.prevEspValue = this.espValue;
        this.espValue += this.espSpeed;
        if (this.espSpeed > (float)ClickGui.gearStop.getValue().intValue()) {
            this.flipSpeed = true;
        }
        if (this.espSpeed < (float)(-ClickGui.gearStop.getValue().intValue())) {
            this.flipSpeed = false;
        }
        this.espSpeed = this.flipSpeed ? this.espSpeed - ClickGui.gearDuration.getValue().floatValue() : this.espSpeed + ClickGui.gearDuration.getValue().floatValue();
    }
}

