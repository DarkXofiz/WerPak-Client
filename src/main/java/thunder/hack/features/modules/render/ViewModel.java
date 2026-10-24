/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.RotationAxis
 */
package thunder.hack.features.modules.render;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import thunder.hack.events.impl.EventHeldItemRenderer;
import thunder.hack.events.impl.EventSetting;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.SettingGroup;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public class ViewModel
extends Module {
    public final Setting<Boolean> syncHands = new Setting<Boolean>("SyncHands", true);
    public final Setting<SettingGroup> mainHand = new Setting<SettingGroup>("MainHand", new SettingGroup(false, 0));
    public final Setting<Float> positionMainX = new Setting<Float>("positionMainX", Float.valueOf(0.0f), Float.valueOf(-3.0f), Float.valueOf(3.0f)).addToGroup(this.mainHand);
    public final Setting<Float> positionMainY = new Setting<Float>("positionMainY", Float.valueOf(0.0f), Float.valueOf(-3.0f), Float.valueOf(3.0f)).addToGroup(this.mainHand);
    public final Setting<Float> positionMainZ = new Setting<Float>("positionMainZ", Float.valueOf(0.0f), Float.valueOf(-3.0f), Float.valueOf(3.0f)).addToGroup(this.mainHand);
    public final Setting<Float> scaleMain = new Setting<Float>("ScaleMain", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(1.5f)).addToGroup(this.mainHand);
    public final Setting<SettingGroup> rotationMain = new Setting<SettingGroup>("Rotation", new SettingGroup(false, 1)).addToGroup(this.mainHand);
    public final Setting<Float> rotationMainX = new Setting<Float>("rotationMainX", Float.valueOf(0.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f)).addToGroup(this.rotationMain);
    public final Setting<Float> rotationMainY = new Setting<Float>("rotationMainY", Float.valueOf(0.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f)).addToGroup(this.rotationMain);
    public final Setting<Float> rotationMainZ = new Setting<Float>("rotationMainZ", Float.valueOf(0.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f)).addToGroup(this.rotationMain);
    public final Setting<SettingGroup> animateMain = new Setting<SettingGroup>("Animate", new SettingGroup(false, 1)).addToGroup(this.mainHand);
    public final Setting<Boolean> animateMainX = new Setting<Boolean>("animateMainX", false).addToGroup(this.animateMain);
    public final Setting<Boolean> animateMainY = new Setting<Boolean>("animateMainY", false).addToGroup(this.animateMain);
    public final Setting<Boolean> animateMainZ = new Setting<Boolean>("animateMainZ", false).addToGroup(this.animateMain);
    public final Setting<Float> speedAnimateMain = new Setting<Float>("speedAnimateMain", Float.valueOf(1.0f), Float.valueOf(1.0f), Float.valueOf(5.0f)).addToGroup(this.rotationMain);
    public final Setting<SettingGroup> offHand = new Setting<SettingGroup>("OffHand", new SettingGroup(false, 0));
    public final Setting<Float> positionOffX = new Setting<Float>("positionOffX", Float.valueOf(0.0f), Float.valueOf(-3.0f), Float.valueOf(3.0f)).addToGroup(this.offHand);
    public final Setting<Float> positionOffY = new Setting<Float>("positionOffY", Float.valueOf(0.0f), Float.valueOf(-3.0f), Float.valueOf(3.0f)).addToGroup(this.offHand);
    public final Setting<Float> positionOffZ = new Setting<Float>("positionOffZ", Float.valueOf(0.0f), Float.valueOf(-3.0f), Float.valueOf(3.0f)).addToGroup(this.offHand);
    public final Setting<Float> scaleOff = new Setting<Float>("ScaleOff", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(1.5f)).addToGroup(this.offHand);
    public final Setting<SettingGroup> rotationOff = new Setting<SettingGroup>("RotationOff", new SettingGroup(false, 1)).addToGroup(this.offHand);
    public final Setting<Float> rotationOffX = new Setting<Float>("rotationOffX", Float.valueOf(0.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f)).addToGroup(this.rotationOff);
    public final Setting<Float> rotationOffY = new Setting<Float>("rotationOffY", Float.valueOf(0.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f)).addToGroup(this.rotationOff);
    public final Setting<Float> rotationOffZ = new Setting<Float>("rotationOffZ", Float.valueOf(0.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f)).addToGroup(this.rotationOff);
    public final Setting<SettingGroup> animateOff = new Setting<SettingGroup>("AnimateOff", new SettingGroup(false, 1)).addToGroup(this.offHand);
    public final Setting<Boolean> animateOffX = new Setting<Boolean>("animateOffX", false).addToGroup(this.animateOff);
    public final Setting<Boolean> animateOffY = new Setting<Boolean>("animateOffY", false).addToGroup(this.animateOff);
    public final Setting<Boolean> animateOffZ = new Setting<Boolean>("animateOffZ", false).addToGroup(this.animateOff);
    public final Setting<Float> speedAnimateOff = new Setting<Float>("speedAnimateOff", Float.valueOf(1.0f), Float.valueOf(1.0f), Float.valueOf(5.0f)).addToGroup(this.rotationOff);
    public final Setting<SettingGroup> eatMod = new Setting<SettingGroup>("Eat", new SettingGroup(false, 0));
    public final Setting<Float> eatX = new Setting<Float>("EatX", Float.valueOf(1.0f), Float.valueOf(-1.0f), Float.valueOf(2.0f)).addToGroup(this.eatMod);
    public final Setting<Float> eatY = new Setting<Float>("EatY", Float.valueOf(1.0f), Float.valueOf(-1.0f), Float.valueOf(2.0f)).addToGroup(this.eatMod);
    private float prevMainX;
    private float prevMainY;
    private float prevMainZ;
    private float prevOffX;
    private float prevOffY;
    private float prevOffZ;

    public ViewModel() {
        super("ViewModel", Module.Category.RENDER);
    }

    private float rotate(float value, float speed) {
        return value - speed <= 180.0f && value - speed > -180.0f ? value - speed : 180.0f;
    }

    @EventHandler
    public void onSettingChange(EventSetting e) {
        if (!this.syncHands.getValue().booleanValue()) {
            return;
        }
        if (e.getSetting() == this.positionMainX) {
            this.positionOffX.setValueSilent(this.positionMainX.getValue());
        }
        if (e.getSetting() == this.positionMainY) {
            this.positionOffY.setValueSilent(this.positionMainY.getValue());
        }
        if (e.getSetting() == this.positionMainZ) {
            this.positionOffZ.setValueSilent(this.positionMainZ.getValue());
        }
        if (e.getSetting() == this.positionOffX) {
            this.positionMainX.setValueSilent(this.positionOffX.getValue());
        }
        if (e.getSetting() == this.positionOffY) {
            this.positionMainY.setValueSilent(this.positionOffY.getValue());
        }
        if (e.getSetting() == this.positionOffZ) {
            this.positionMainZ.setValueSilent(this.positionOffZ.getValue());
        }
        if (e.getSetting() == this.scaleMain) {
            this.scaleOff.setValueSilent(this.scaleMain.getValue());
        }
        if (e.getSetting() == this.scaleOff) {
            this.scaleMain.setValueSilent(this.scaleOff.getValue());
        }
    }

    @Override
    public void onUpdate() {
        this.prevMainX = this.rotationMainX.getValue().floatValue();
        this.prevMainY = this.rotationMainY.getValue().floatValue();
        this.prevMainZ = this.rotationMainZ.getValue().floatValue();
        this.prevOffX = this.rotationOffX.getValue().floatValue();
        this.prevOffY = this.rotationOffY.getValue().floatValue();
        this.prevOffZ = this.rotationOffZ.getValue().floatValue();
        if (this.animateMainX.getValue().booleanValue()) {
            this.rotationMainX.setValue(Float.valueOf(this.rotate(this.rotationMainX.getValue().floatValue(), this.speedAnimateMain.getValue().floatValue())));
        }
        if (this.animateMainY.getValue().booleanValue()) {
            this.rotationMainY.setValue(Float.valueOf(this.rotate(this.rotationMainY.getValue().floatValue(), this.speedAnimateMain.getValue().floatValue())));
        }
        if (this.animateMainZ.getValue().booleanValue()) {
            this.rotationMainZ.setValue(Float.valueOf(this.rotate(this.rotationMainZ.getValue().floatValue(), this.speedAnimateMain.getValue().floatValue())));
        }
        if (this.animateOffX.getValue().booleanValue()) {
            this.rotationOffX.setValue(Float.valueOf(this.rotate(this.rotationOffX.getValue().floatValue(), this.speedAnimateOff.getValue().floatValue())));
        }
        if (this.animateOffY.getValue().booleanValue()) {
            this.rotationOffY.setValue(Float.valueOf(this.rotate(this.rotationOffY.getValue().floatValue(), this.speedAnimateOff.getValue().floatValue())));
        }
        if (this.animateOffZ.getValue().booleanValue()) {
            this.rotationOffZ.setValue(Float.valueOf(this.rotate(this.rotationOffZ.getValue().floatValue(), this.speedAnimateOff.getValue().floatValue())));
        }
    }

    @EventHandler
    private void onHeldItemRender(EventHeldItemRenderer event) {
        if (event.getHand() == Hand.MAIN_HAND) {
            event.getStack().translate(this.positionMainX.getValue().floatValue(), this.positionMainY.getValue().floatValue(), this.positionMainZ.getValue().floatValue());
            event.getStack().scale(this.scaleMain.getValue().floatValue(), this.scaleMain.getValue().floatValue(), this.scaleMain.getValue().floatValue());
            event.getStack().multiply(RotationAxis.POSITIVE_X.rotationDegrees(Render2DEngine.interpolateFloat(this.prevMainX, this.rotationMainX.getValue().floatValue(), Render3DEngine.getTickDelta())));
            event.getStack().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Render2DEngine.interpolateFloat(this.prevMainY, this.rotationMainY.getValue().floatValue(), Render3DEngine.getTickDelta())));
            event.getStack().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Render2DEngine.interpolateFloat(this.prevMainZ, this.rotationMainZ.getValue().floatValue(), Render3DEngine.getTickDelta())));
        } else {
            event.getStack().translate(-this.positionOffX.getValue().floatValue(), this.positionOffY.getValue().floatValue(), this.positionOffZ.getValue().floatValue());
            event.getStack().scale(this.scaleOff.getValue().floatValue(), this.scaleOff.getValue().floatValue(), this.scaleOff.getValue().floatValue());
            event.getStack().multiply(RotationAxis.POSITIVE_X.rotationDegrees(Render2DEngine.interpolateFloat(this.prevOffX, this.rotationOffX.getValue().floatValue(), Render3DEngine.getTickDelta())));
            event.getStack().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Render2DEngine.interpolateFloat(this.prevOffY, this.rotationOffY.getValue().floatValue(), Render3DEngine.getTickDelta())));
            event.getStack().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Render2DEngine.interpolateFloat(this.prevOffZ, this.rotationOffZ.getValue().floatValue(), Render3DEngine.getTickDelta())));
        }
    }
}

