/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.projectile.ProjectileUtil
 *  net.minecraft.item.Items
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult.Type
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket
 *  net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext.FluidHandling
 *  net.minecraft.world.RaycastContext.ShapeType
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 */
package thunder.hack.core.manager.player;

import thunder.hack.events.impl.PacketEvent;
import java.util.ArrayDeque;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Items;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.manager.IManager;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventFixVelocity;
import thunder.hack.events.impl.EventKeyboardInput;
import thunder.hack.events.impl.EventPlayerJump;
import thunder.hack.events.impl.EventPlayerTravel;
import thunder.hack.events.impl.EventPostSync;
import thunder.hack.events.impl.EventSync;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.injection.accesors.IClientPlayerEntity;
import thunder.hack.utility.Timer;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.world.ExplosionUtility;

public class PlayerManager
implements IManager {
    public float yaw;
    public float pitch;
    public float lastYaw;
    public float lastPitch;
    public float currentPlayerSpeed;
    public float averagePlayerSpeed;
    public int ticksElytraFlying;
    public int serverSideSlot;
    public final Timer switchTimer = new Timer();
    private final ArrayDeque<Float> speedResult = new ArrayDeque(20);
    public float bodyYaw;
    public float prevBodyYaw;
    public boolean inInventory;

    @EventHandler(priority=200)
    public void onSync(EventSync event) {
        if (Module.fullNullCheck()) {
            return;
        }
        this.yaw = PlayerManager.mc.player.getYaw();
        this.pitch = PlayerManager.mc.player.getPitch();
        this.lastYaw = ((IClientPlayerEntity)PlayerManager.mc.player).getLastYaw();
        this.lastPitch = ((IClientPlayerEntity)PlayerManager.mc.player).getLastPitch();
        if (PlayerManager.mc.currentScreen == null) {
            this.inInventory = false;
        }
        this.ticksElytraFlying = PlayerManager.mc.player.isFallFlying() && PlayerManager.mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA ? ++this.ticksElytraFlying : 0;
    }

    @EventHandler
    public void onTick(EventTick e) {
        this.currentPlayerSpeed = (float)Math.hypot(PlayerManager.mc.player.getX() - PlayerManager.mc.player.prevX, PlayerManager.mc.player.getZ() - PlayerManager.mc.player.prevZ);
        if (this.speedResult.size() > 20) {
            this.speedResult.poll();
        }
        this.speedResult.add(Float.valueOf(this.currentPlayerSpeed));
        float average = 0.0f;
        for (Float value : this.speedResult) {
            average += MathUtility.clamp(value.floatValue(), 0.0f, 20.0f);
        }
        this.averagePlayerSpeed = average / (float)this.speedResult.size();
    }

    @EventHandler(priority=-200)
    public void postSync(EventPostSync event) {
        if (PlayerManager.mc.player == null) {
            return;
        }
        this.prevBodyYaw = this.bodyYaw;
        this.bodyYaw = this.getBodyYaw();
        if (!ModuleManager.rotations.clientLook.getValue().booleanValue()) {
            PlayerManager.mc.player.setYaw(this.yaw);
            PlayerManager.mc.player.setPitch(this.pitch);
        }
        ModuleManager.rotations.fixRotation = Float.NaN;
    }

    @EventHandler
    public void onJump(EventPlayerJump e) {
        ModuleManager.rotations.onJump(e);
    }

    @EventHandler
    public void onPlayerMove(EventFixVelocity e) {
        ModuleManager.rotations.onPlayerMove(e);
    }

    @EventHandler
    public void modifyVelocity(EventPlayerTravel e) {
        ModuleManager.rotations.modifyVelocity(e);
    }

    @EventHandler
    public void onKeyInput(EventKeyboardInput e) {
        ModuleManager.rotations.onKeyInput(e);
    }

    @EventHandler
    public void onSyncWithServer( @NotNull PacketEvent.Send event) {
        Object t;
        if (event.getPacket() instanceof ClickSlotC2SPacket) {
            this.inInventory = true;
        }
        if ((t = event.getPacket()) instanceof UpdateSelectedSlotC2SPacket) {
            UpdateSelectedSlotC2SPacket slot = (UpdateSelectedSlotC2SPacket)t;
            this.switchTimer.reset();
            this.serverSideSlot = slot.getSelectedSlot();
        }
        if (event.getPacket() instanceof CloseHandledScreenC2SPacket) {
            this.inInventory = false;
        }
    }

    @EventHandler
    public void onPacketReceive( @NotNull PacketEvent.Receive event) {
        Object t = event.getPacket();
        if (t instanceof UpdateSelectedSlotS2CPacket) {
            UpdateSelectedSlotS2CPacket slot = (UpdateSelectedSlotS2CPacket)t;
            this.switchTimer.reset();
            this.serverSideSlot = slot.getSlot();
        }
    }

    private float getBodyYaw() {
        float deltaBodyYaw;
        double x = PlayerManager.mc.player.getX() - PlayerManager.mc.player.prevX;
        double z = PlayerManager.mc.player.getZ() - PlayerManager.mc.player.prevZ;
        float offset = this.bodyYaw;
        if (x * x + z * z > 0.002500000176951289) {
            offset = (float)(MathHelper.atan2((double)z, (double)x) * 57.2957763671875 - 90.0);
        }
        if (PlayerManager.mc.player.handSwingProgress > 0.0f) {
            offset = ((IClientPlayerEntity)MinecraftClient.getInstance().player).getLastYaw();
        }
        return ((deltaBodyYaw = MathHelper.clamp((float)MathHelper.wrapDegrees((float)(((IClientPlayerEntity)MinecraftClient.getInstance().player).getLastYaw() - (this.bodyYaw + MathHelper.wrapDegrees((float)(offset - this.bodyYaw)) * 0.3f))), (float)-45.0f, (float)75.0f)) > 50.0f ? deltaBodyYaw * 0.2f : 0.0f) + ((IClientPlayerEntity)MinecraftClient.getInstance().player).getLastYaw() - deltaBodyYaw;
    }

    public boolean checkRtx(float yaw, float pitch, float distance, float wallDistance, Aura.RayTrace rt) {
        if (rt == Aura.RayTrace.OFF) {
            return true;
        }
        HitResult result = this.rayTrace(distance, yaw, pitch);
        Vec3d startPoint = PlayerManager.mc.player.getPos().add(0.0, (double)PlayerManager.mc.player.getEyeHeight(PlayerManager.mc.player.getPose()), 0.0);
        double distancePow2 = Math.pow(distance, 2.0);
        if (result != null) {
            distancePow2 = startPoint.squaredDistanceTo(result.getPos());
        }
        Vec3d rotationVector = this.getRotationVector(pitch, yaw).multiply((double)distance);
        Vec3d endPoint = startPoint.add(rotationVector);
        Box entityArea = PlayerManager.mc.player.getBoundingBox().stretch(rotationVector).expand(1.0, 1.0, 1.0);
        double maxDistance = Math.max(distancePow2, Math.pow(wallDistance, 2.0));
        EntityHitResult ehr = rt == Aura.RayTrace.OnlyTarget && Aura.target != null ? ProjectileUtil.raycast((Entity)PlayerManager.mc.player, (Vec3d)startPoint, (Vec3d)endPoint, (Box)entityArea, e -> !e.isSpectator() && e.canHit() && e == Aura.target, (double)maxDistance) : ProjectileUtil.raycast((Entity)PlayerManager.mc.player, (Vec3d)startPoint, (Vec3d)endPoint, (Box)entityArea, e -> !e.isSpectator() && e.canHit(), (double)maxDistance);
        if (ehr != null) {
            boolean allowWallHit;
            boolean allowedWallDistance = startPoint.squaredDistanceTo(ehr.getPos()) <= Math.pow(wallDistance, 2.0);
            boolean wallMissing = result == null;
            boolean wallBehindEntity = startPoint.squaredDistanceTo(ehr.getPos()) < distancePow2;
            boolean bl = allowWallHit = wallMissing || allowedWallDistance || wallBehindEntity;
            if (allowWallHit && startPoint.squaredDistanceTo(ehr.getPos()) <= Math.pow(distance, 2.0)) {
                return ehr.getEntity() == Aura.target || Aura.target == null || rt == Aura.RayTrace.OnlyTarget;
            }
        }
        return false;
    }

    public boolean checkRtx(float yaw, float pitch, float distance, float wallDistance, Entity entity) {
        double maxDistance;
        Box entityArea;
        Vec3d rotationVector;
        Vec3d endPoint;
        EntityHitResult ehr;
        HitResult result = this.rayTrace(distance, yaw, pitch);
        Vec3d startPoint = PlayerManager.mc.player.getPos().add(0.0, (double)PlayerManager.mc.player.getEyeHeight(PlayerManager.mc.player.getPose()), 0.0);
        double distancePow2 = Math.pow(distance, 2.0);
        if (result != null) {
            distancePow2 = startPoint.squaredDistanceTo(result.getPos());
        }
        if ((ehr = ProjectileUtil.raycast((Entity)PlayerManager.mc.player, (Vec3d)startPoint, (Vec3d)(endPoint = startPoint.add(rotationVector = this.getRotationVector(pitch, yaw).multiply((double)distance))), (Box)(entityArea = PlayerManager.mc.player.getBoundingBox().stretch(rotationVector).expand(1.0, 1.0, 1.0)), e -> !e.isSpectator() && e.canHit() && e == entity, (double)(maxDistance = Math.max(distancePow2, Math.pow(wallDistance, 2.0))))) != null) {
            boolean allowWallHit;
            boolean allowedWallDistance = startPoint.squaredDistanceTo(ehr.getPos()) <= Math.pow(wallDistance, 2.0);
            boolean wallMissing = result == null;
            boolean wallBehindEntity = startPoint.squaredDistanceTo(ehr.getPos()) < distancePow2;
            boolean bl = allowWallHit = wallMissing || allowedWallDistance || wallBehindEntity;
            if (allowWallHit && startPoint.squaredDistanceTo(ehr.getPos()) <= Math.pow(distance, 2.0)) {
                return ehr.getEntity() == entity;
            }
        }
        return false;
    }

    public Entity getRtxTarget(float yaw, float pitch, float distance, boolean ignoreWalls) {
        Box box;
        Entity targetedEntity = null;
        HitResult result = ignoreWalls ? null : this.rayTrace(distance, yaw, pitch);
        Vec3d vec3d = PlayerManager.mc.player.getPos().add(0.0, (double)PlayerManager.mc.player.getEyeHeight(PlayerManager.mc.player.getPose()), 0.0);
        double distancePow2 = Math.pow(distance, 2.0);
        if (result != null) {
            distancePow2 = result.getPos().squaredDistanceTo(vec3d);
        }
        Vec3d vec3d2 = this.getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * (double)distance, vec3d2.y * (double)distance, vec3d2.z * (double)distance);
        EntityHitResult entityHitResult = ProjectileUtil.raycast((Entity)PlayerManager.mc.player, (Vec3d)vec3d, (Vec3d)vec3d3, (Box)(box = PlayerManager.mc.player.getBoundingBox().stretch(vec3d2.multiply((double)distance)).expand(1.0, 1.0, 1.0)), entity -> !entity.isSpectator() && entity.canHit(), (double)distancePow2);
        if (entityHitResult != null) {
            Entity entity2 = entityHitResult.getEntity();
            Vec3d vec3d4 = entityHitResult.getPos();
            double g = vec3d.squaredDistanceTo(vec3d4);
            if ((g < distancePow2 || result == null) && entity2 instanceof LivingEntity) {
                targetedEntity = entity2;
                return targetedEntity;
            }
        }
        return targetedEntity;
    }

    public Vec3d getRtxPoint(float yaw, float pitch, float distance) {
        Box box;
        Vec3d vec3d = PlayerManager.mc.player.getPos().add(0.0, (double)PlayerManager.mc.player.getEyeHeight(PlayerManager.mc.player.getPose()), 0.0);
        double distancePow2 = Math.pow(distance, 2.0);
        Vec3d vec3d2 = this.getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * (double)distance, vec3d2.y * (double)distance, vec3d2.z * (double)distance);
        EntityHitResult entityHitResult = ProjectileUtil.raycast((Entity)PlayerManager.mc.player, (Vec3d)vec3d, (Vec3d)vec3d3, (Box)(box = PlayerManager.mc.player.getBoundingBox().stretch(vec3d2.multiply((double)distance)).expand(1.0, 1.0, 1.0)), entity -> !entity.isSpectator() && entity.canHit(), (double)distancePow2);
        if (entityHitResult != null) {
            Entity entity2 = entityHitResult.getEntity();
            Vec3d vec3d4 = entityHitResult.getPos();
            if (entity2 instanceof LivingEntity) {
                return vec3d4;
            }
        }
        return null;
    }

    public boolean isLookingAtBox(float yaw, float pitch, BlockPos blockPos) {
        Vec3d vec3d = PlayerManager.mc.player.getCameraPosVec(1.0f);
        Vec3d vec3d2 = this.getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * 7.0, vec3d2.y * 7.0, vec3d2.z * 7.0);
        BlockHitResult result = ExplosionUtility.rayCastBlock(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)PlayerManager.mc.player), blockPos);
        return result != null && result.getType() == HitResult.Type.BLOCK && result.getBlockPos().equals((Object)blockPos);
    }

    public HitResult rayTrace(double dst, float yaw, float pitch) {
        Vec3d vec3d = PlayerManager.mc.player.getCameraPosVec(1.0f);
        Vec3d vec3d2 = this.getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * dst, vec3d2.y * dst, vec3d2.z * dst);
        return PlayerManager.mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)PlayerManager.mc.player));
    }

    public HitResult getRtxTarget(float yaw, float pitch, double x, double y, double z) {
        Box box;
        HitResult result = this.rayTrace(5.0, yaw, pitch, x, y, z);
        Vec3d vec3d = new Vec3d(x, y, z).add(0.0, (double)PlayerManager.mc.player.getEyeHeight(PlayerManager.mc.player.getPose()), 0.0);
        double distancePow2 = 25.0;
        if (result != null) {
            distancePow2 = result.getPos().squaredDistanceTo(vec3d);
        }
        Vec3d vec3d2 = this.getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * 5.0, vec3d2.y * 5.0, vec3d2.z * 5.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast((Entity)PlayerManager.mc.player, (Vec3d)vec3d, (Vec3d)vec3d3, (Box)(box = new Box(x - 0.3, y, z - 0.3, x + 0.3, y + 1.8, z + 0.3).stretch(vec3d2.multiply(5.0)).expand(1.0, 1.0, 1.0)), entity -> !entity.isSpectator() && entity.canHit(), (double)distancePow2);
        if (entityHitResult != null) {
            Entity entity2 = entityHitResult.getEntity();
            Vec3d vec3d4 = entityHitResult.getPos();
            double g = vec3d.squaredDistanceTo(vec3d4);
            if ((g < distancePow2 || result == null) && entity2 instanceof LivingEntity) {
                return entityHitResult;
            }
        }
        return result;
    }

    public boolean isInWeb() {
        Box pBox = PlayerManager.mc.player.getBoundingBox();
        BlockPos pBlockPos = BlockPos.ofFloored(PlayerManager.mc.player.getPos());
        for (int x = pBlockPos.getX() - 2; x <= pBlockPos.getX() + 2; ++x) {
            for (int y = pBlockPos.getY() - 1; y <= pBlockPos.getY() + 4; ++y) {
                for (int z = pBlockPos.getZ() - 2; z <= pBlockPos.getZ() + 2; ++z) {
                    BlockPos bp = new BlockPos(x, y, z);
                    if (!pBox.intersects(new Box(bp)) || PlayerManager.mc.world.getBlockState(bp).getBlock() != Blocks.COBWEB) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public HitResult rayTrace(double dst, float yaw, float pitch, double x, double y, double z) {
        Vec3d vec3d = new Vec3d(x, y, z);
        Vec3d vec3d2 = this.getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * dst, vec3d2.y * dst, vec3d2.z * dst);
        return PlayerManager.mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)PlayerManager.mc.player));
    }

    public static float[] calcAngle(Vec3d to) {
        if (to == null) {
            return null;
        }
        double difX = to.x - PlayerManager.mc.player.getEyePos().x;
        double difY = (to.y - PlayerManager.mc.player.getEyePos().y) * -1.0;
        double difZ = to.z - PlayerManager.mc.player.getEyePos().z;
        double dist = MathHelper.sqrt((float)((float)(difX * difX + difZ * difZ)));
        return new float[]{(float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0)), (float)MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(difY, dist)))};
    }

    public static Vec2f calcAngleVec(Vec3d to) {
        if (to == null) {
            return null;
        }
        double difX = to.x - PlayerManager.mc.player.getEyePos().x;
        double difY = (to.y - PlayerManager.mc.player.getEyePos().y) * -1.0;
        double difZ = to.z - PlayerManager.mc.player.getEyePos().z;
        double dist = MathHelper.sqrt((float)((float)(difX * difX + difZ * difZ)));
        return new Vec2f((float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0)), (float)MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(difY, dist))));
    }

    @NotNull
    public Vec3d getRotationVector(float yaw, float pitch) {
        return new Vec3d((double)(MathHelper.sin((float)(-pitch * ((float)Math.PI / 180))) * MathHelper.cos((float)(yaw * ((float)Math.PI / 180)))), (double)(-MathHelper.sin((float)(yaw * ((float)Math.PI / 180)))), (double)(MathHelper.cos((float)(-pitch * ((float)Math.PI / 180))) * MathHelper.cos((float)(yaw * ((float)Math.PI / 180)))));
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        if (to == null) {
            return null;
        }
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt((float)((float)(difX * difX + difZ * difZ)));
        return new float[]{(float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0)), (float)MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(difY, dist)))};
    }
}

