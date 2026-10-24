/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.features.modules.render;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;

public class ChatFucker
extends Module {
    public Setting<String> text = new Setting<String>("Text", "ChatFucker!");
    public Setting<Float> delay = new Setting<Float>("Delay", Float.valueOf(3.0f), Float.valueOf(0.1f), Float.valueOf(20.0f));
    private final Timer timer = new Timer();

    public ChatFucker() {
        super("ChatFucker", Module.Category.RENDER);
    }

    @Override
    public void onUpdate() {
        if (this.timer.passedMs((long)(this.delay.getValue().floatValue() * 1000.0f))) {
            String msg = this.text.getValue();
            if (msg != null && !msg.isEmpty()) {
                if (msg.startsWith("/")) {
                    ChatFucker.mc.player.networkHandler.sendCommand(msg.substring(1));
                } else {
                    ChatFucker.mc.player.networkHandler.sendChatMessage(msg);
                }
            }
            this.timer.reset();
        }
    }
}

