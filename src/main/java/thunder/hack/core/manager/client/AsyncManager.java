/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.entity.Entity
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 */
package thunder.hack.core.manager.client;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.IManager;
import thunder.hack.events.impl.EventPostTick;
import thunder.hack.events.impl.EventSync;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.modules.Module;

public class AsyncManager
implements IManager {
    private ClientService clientService = new ClientService();
    public static ExecutorService executor = Executors.newCachedThreadPool();
    private volatile Iterable<Entity> threadSafeEntityList = Collections.emptyList();
    private volatile List<AbstractClientPlayerEntity> threadSafePlayersList = Collections.emptyList();
    public final AtomicBoolean ticking = new AtomicBoolean(false);

    public static void sleep(int delay) {
        try {
            Thread.sleep(delay);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @EventHandler(priority=-200)
    public void onPostTick(EventPostTick e) {
        if (AsyncManager.mc.world == null) {
            return;
        }
        this.threadSafeEntityList = Lists.newArrayList((Iterable)AsyncManager.mc.world.getEntities());
        this.threadSafePlayersList = Lists.newArrayList((Iterable)AsyncManager.mc.world.getPlayers());
        this.ticking.set(false);
    }

    public Iterable<Entity> getAsyncEntities() {
        return this.threadSafeEntityList;
    }

    public List<AbstractClientPlayerEntity> getAsyncPlayers() {
        return this.threadSafePlayersList;
    }

    public AsyncManager() {
        this.clientService.setName("ThunderHack-AsyncProcessor");
        this.clientService.setDaemon(true);
        this.clientService.start();
    }

    @EventHandler
    public void onSync(EventSync e) {
        if (!this.clientService.isAlive()) {
            this.clientService = new ClientService();
            this.clientService.setName("ThunderHack-AsyncProcessor");
            this.clientService.setDaemon(true);
            this.clientService.start();
        }
    }

    @EventHandler(priority=200)
    public void onTick(EventTick e) {
        this.ticking.set(true);
    }

    public void run(Runnable runnable, long delay) {
        executor.execute(() -> {
            try {
                Thread.sleep(delay);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            runnable.run();
        });
    }

    public void run(Runnable r) {
        executor.execute(r);
    }

    public static class ClientService
    extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Managers.TELEMETRY.onUpdate();
                    if (!Module.fullNullCheck()) {
                        Managers.MODULE.modules.forEach(m -> {
                            if (m.isEnabled()) {
                                m.onThread();
                            }
                        });
                        Thread.sleep(100L);
                        continue;
                    }
                    Thread.yield();
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                    Command.sendMessage(exception.getMessage());
                }
            }
        }
    }
}

