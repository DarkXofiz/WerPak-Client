/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  org.joml.Vector4d
 */
package thunder.hack.features.modules.client;

import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4d;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.world.WayPointManager;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.TextureStorage;

public final class WayPoints
extends Module {
    public WayPoints() {
        super("WayPoints", Module.Category.CLIENT);
    }

    @Override
    public void onEnable() {
        this.sendMessage(Managers.COMMAND.getPrefix() + "waypoint add x y z name");
    }

    @Override
    public void onRender2D(DrawContext context) {
        if (!Managers.WAYPOINT.getWayPoints().isEmpty() && !WayPoints.fullNullCheck()) {
            for (WayPointManager.WayPoint wp : Managers.WAYPOINT.getWayPoints()) {
                double plYaw;
                if (wp.getName() == null || mc.isInSingleplayer() && wp.getServer().equals("SinglePlayer") || mc.getNetworkHandler().getServerInfo() != null && !WayPoints.mc.getNetworkHandler().getServerInfo().address.contains(wp.getServer()) || !WayPoints.mc.world.getRegistryKey().getValue().getPath().equals(wp.getDimension())) continue;
                double difX = (double)wp.getX() - WayPoints.mc.player.getPos().x;
                double difZ = (double)wp.getZ() - WayPoints.mc.player.getPos().z;
                float yaw = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0));
                if (Math.abs((double)yaw - (plYaw = (double)MathHelper.wrapDegrees((float)WayPoints.mc.player.getYaw()))) > 90.0) continue;
                Vec3d vector = new Vec3d((double)wp.getX(), (double)wp.getY(), (double)wp.getZ());
                Vector4d position = null;
                vector = Render3DEngine.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
                position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
                double posX = position.x;
                double posY = position.y;
                double endPosX = position.z;
                float diff = (float)(endPosX - posX) / 2.0f;
                float tagX = (float)((posX + (double)diff - (double)(FontRenderers.sf_bold_mini.getStringWidth(wp.getName()) / 2.0f)) * 1.0);
                String coords = wp.getX() + " " + wp.getZ();
                float tagX2 = (float)((posX + (double)diff - (double)(FontRenderers.sf_bold_mini.getStringWidth(coords) / 2.0f)) * 1.0);
                String distance = String.format("%.0f", Math.sqrt(WayPoints.mc.player.squaredDistanceTo((double)wp.getX(), (double)wp.getY(), (double)wp.getZ()))) + "m";
                float tagX3 = (float)((posX + (double)diff - (double)(FontRenderers.sf_bold_mini.getStringWidth(distance) / 2.0f)) * 1.0);
                context.getMatrices().push();
                context.getMatrices().translate(posX - 10.0, posY - 35.0, 0.0);
                context.drawTexture(TextureStorage.waypoint, 0, 0, 20, 20, 0.0f, 0.0f, 20, 20, 20, 20);
                context.getMatrices().pop();
                FontRenderers.sf_bold_mini.drawString(context.getMatrices(), wp.getName(), (double)tagX, (double)((float)posY - 10.0f), -1);
                FontRenderers.sf_bold_mini.drawString(context.getMatrices(), String.valueOf(Formatting.GRAY) + coords, (double)tagX2, (double)((float)posY - 2.0f), -1);
                FontRenderers.sf_bold_mini.drawString(context.getMatrices(), String.valueOf(Formatting.GRAY) + distance, (double)tagX3, (double)((float)posY + 6.0f), -1);
            }
        }
    }
}

