import sys
B='src/main/java/thunder/hack/'
open(B+'features/modules/misc/HeadSpin.java','w').write('''package thunder.hack.features.modules.misc;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.MathHelper;
import thunder.hack.events.impl.EventSync;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class HeadSpin
extends Module {
    private final Setting<Float> speed = new Setting<Float>("Speed", Float.valueOf(35.0f), Float.valueOf(5.0f), Float.valueOf(90.0f));
    private final Setting<Boolean> pitchSpin = new Setting<Boolean>("Pitch Wobble", false);
    private float angle = 0.0f;

    public HeadSpin() {
        super("HeadSpin", Module.Category.MISC);
    }

    @Override
    public void onEnable() {
        this.angle = HeadSpin.mc.player != null ? HeadSpin.mc.player.getYaw() : 0.0f;
    }

    @EventHandler
    public void onSync(EventSync e) {
        if (HeadSpin.mc.player == null) {
            return;
        }
        final float realYaw = HeadSpin.mc.player.getYaw();
        final float realPitch = HeadSpin.mc.player.getPitch();
        this.angle = MathHelper.wrapDegrees((float)(this.angle + this.speed.getValue().floatValue()));
        float fakePitch = realPitch;
        if (this.pitchSpin.getValue().booleanValue()) {
            fakePitch = MathHelper.clamp((float)(realPitch + (float)Math.sin(Math.toRadians(this.angle * 2.0f)) * 40.0f), (float)-89.0f, (float)89.0f);
        }
        HeadSpin.mc.player.setYaw(this.angle);
        HeadSpin.mc.player.setPitch(fakePitch);
        e.addPostAction(() -> {
            HeadSpin.mc.player.setYaw(realYaw);
            HeadSpin.mc.player.setPitch(realPitch);
        });
    }
}
''')
p=B+'core/manager/client/ModuleManager.java'
s=open(p).read()
a='    public static Ghost ghost = new Ghost();\n'
assert s.count(a)==1
s=s.replace(a,a+'    public static thunder.hack.features.modules.misc.HeadSpin headSpin = new thunder.hack.features.modules.misc.HeadSpin();\n')
open(p,'w').write(s)
print("TAMAM")
