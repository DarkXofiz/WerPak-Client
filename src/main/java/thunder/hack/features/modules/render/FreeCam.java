/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 */
package thunder.hack.features.modules.render;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import thunder.hack.events.impl.EventAttack;
import thunder.hack.events.impl.EventKeyboardInput;
import thunder.hack.events.impl.EventMouse;
import thunder.hack.events.impl.EventMove;
import thunder.hack.events.impl.EventSync;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.player.MovementUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public class FreeCam
extends Module {
    private final Setting<Float> speed = new Setting<Float>("HSpeed", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f));
    private final Setting<Float> hspeed = new Setting<Float>("VSpeed", Float.valueOf(0.42f), Float.valueOf(0.1f), Float.valueOf(3.0f));
    private final Setting<Boolean> freeze = new Setting<Boolean>("Freeze", false);
    public final Setting<Boolean> track = new Setting<Boolean>("Track", false);
    private float fakeYaw;
    private float fakePitch;
    private float prevFakeYaw;
    private float prevFakePitch;
    private float prevScroll;
    private double fakeX;
    private double fakeY;
    private double fakeZ;
    private double prevFakeX;
    private double prevFakeY;
    private double prevFakeZ;
    public LivingEntity trackEntity;

    public FreeCam() {
        super("FreeCam", Module.Category.RENDER);
    }

    @Override
    public void onEnable() {
        FreeCam.mc.chunkCullingEnabled = false;
        this.trackEntity = null;
        this.fakePitch = FreeCam.mc.player.getPitch();
        this.fakeYaw = FreeCam.mc.player.getYaw();
        this.prevFakePitch = this.fakePitch;
        this.prevFakeYaw = this.fakeYaw;
        this.fakeX = FreeCam.mc.player.getX();
        this.fakeY = FreeCam.mc.player.getY() + (double)FreeCam.mc.player.getEyeHeight(FreeCam.mc.player.getPose());
        this.fakeZ = FreeCam.mc.player.getZ();
        this.prevFakeX = FreeCam.mc.player.getX();
        this.prevFakeY = FreeCam.mc.player.getY();
        this.prevFakeZ = FreeCam.mc.player.getZ();
    }

    @EventHandler
    public void onAttack(EventAttack e) {
        Entity class_12972;
        if (!e.isPre() && (class_12972 = e.getEntity()) instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity)class_12972;
            if (this.track.getValue().booleanValue()) {
                this.trackEntity = entity;
            }
        }
    }

    @Override
    public void onDisable() {
        if (FreeCam.fullNullCheck()) {
            return;
        }
        FreeCam.mc.chunkCullingEnabled = true;
    }

    @EventHandler(priority=100)
    public void onSync(EventSync e) {
        this.prevFakeYaw = this.fakeYaw;
        this.prevFakePitch = this.fakePitch;
        if (this.isKeyPressed(256) || this.isKeyPressed(340) || this.isKeyPressed(344)) {
            this.trackEntity = null;
        }
        if (this.trackEntity != null) {
            this.fakeYaw = this.trackEntity.getYaw();
            this.fakePitch = this.trackEntity.getPitch();
            this.prevFakeX = this.fakeX;
            this.prevFakeY = this.fakeY;
            this.prevFakeZ = this.fakeZ;
            this.fakeX = this.trackEntity.getX();
            this.fakeY = this.trackEntity.getY() + (double)this.trackEntity.getEyeHeight(this.trackEntity.getPose());
            this.fakeZ = this.trackEntity.getZ();
        } else {
            this.fakeYaw = FreeCam.mc.player.getYaw();
            this.fakePitch = FreeCam.mc.player.getPitch();
        }
    }

    @EventHandler
    public void onKeyboardInput(EventKeyboardInput e) {
        if (FreeCam.mc.player == null) {
            return;
        }
        if (this.trackEntity == null) {
            double[] motion = MovementUtility.forward(this.speed.getValue().floatValue());
            this.prevFakeX = this.fakeX;
            this.prevFakeY = this.fakeY;
            this.prevFakeZ = this.fakeZ;
            this.fakeX += motion[0];
            this.fakeZ += motion[1];
            if (FreeCam.mc.options.jumpKey.isPressed()) {
                this.fakeY += (double)this.hspeed.getValue().floatValue();
            }
            if (FreeCam.mc.options.sneakKey.isPressed()) {
                this.fakeY -= (double)this.hspeed.getValue().floatValue();
            }
        }
        FreeCam.mc.player.input.movementForward = 0.0f;
        FreeCam.mc.player.input.movementSideways = 0.0f;
        FreeCam.mc.player.input.jumping = false;
        FreeCam.mc.player.input.sneaking = false;
    }

    @EventHandler(priority=-100)
    public void onMove(EventMove e) {
        if (this.freeze.getValue().booleanValue()) {
            e.setX(0.0);
            e.setY(0.0);
            e.setZ(0.0);
            e.cancel();
        }
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send e) {
        if (this.freeze.getValue().booleanValue() && e.getPacket() instanceof PlayerMoveC2SPacket) {
            e.cancel();
        }
    }

    @EventHandler
    public void onScroll(EventMouse e) {
        if (e.getAction() == 2) {
            if (e.getButton() > 0) {
                this.speed.setValue(Float.valueOf(this.speed.getValue().floatValue() + 0.05f));
            } else {
                this.speed.setValue(Float.valueOf(this.speed.getValue().floatValue() - 0.05f));
            }
            this.prevScroll = e.getButton();
        }
    }

    public float getFakeYaw() {
        return (float)Render2DEngine.interpolate(this.prevFakeYaw, this.fakeYaw, Render3DEngine.getTickDelta());
    }

    public float getFakePitch() {
        return (float)Render2DEngine.interpolate(this.prevFakePitch, this.fakePitch, Render3DEngine.getTickDelta());
    }

    public double getFakeX() {
        return Render2DEngine.interpolate(this.prevFakeX, this.fakeX, Render3DEngine.getTickDelta());
    }

    public double getFakeY() {
        return Render2DEngine.interpolate(this.prevFakeY, this.fakeY, Render3DEngine.getTickDelta());
    }

    public double getFakeZ() {
        return Render2DEngine.interpolate(this.prevFakeZ, this.fakeZ, Render3DEngine.getTickDelta());
    }
}

