/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 */
package thunder.hack.core.manager.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.manager.IManager;

public class FriendManager
implements IManager {
    public static List<String> friends = new ArrayList<String>();

    public boolean isFriend(String name) {
        return friends.stream().anyMatch(friend -> friend.equalsIgnoreCase(name));
    }

    public boolean isFriend(@NotNull PlayerEntity player) {
        return this.isFriend(player.getName().getString());
    }

    public void removeFriend(String name) {
        friends.remove(name);
    }

    public void addFriend(String friend) {
        friends.add(friend);
    }

    public List<String> getFriends() {
        return friends;
    }

    public void clear() {
        friends.clear();
    }

    public List<AbstractClientPlayerEntity> getNearFriends() {
        if (FriendManager.mc.world == null) {
            return new ArrayList<AbstractClientPlayerEntity>();
        }
        return FriendManager.mc.world.getPlayers().stream().filter(player -> friends.contains(player.getName().getString())).toList();
    }

    public void saveFriends() {
        File file = new File("ThunderHackRecode/misc/friends.txt");
        try {
            file.createNewFile();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));){
            for (String friend : friends) {
                writer.write(friend + "\n");
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void loadFriends() {
        block8: {
            try {
                File file = new File("ThunderHackRecode/misc/friends.txt");
                if (!file.exists()) break block8;
                try (BufferedReader reader = new BufferedReader(new FileReader(file));){
                    while (reader.ready()) {
                        friends.add(reader.readLine());
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

