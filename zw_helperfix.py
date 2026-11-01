p='src/main/java/thunder/hack/features/modules/player/ElytraHelper.java'
s=open(p).read()
if 'ghostGcd' not in s:
    a='''        mc.player.setYaw(ghostYaw + (float)((Math.random() - 0.5) * 0.24));
        mc.player.setPitch(net.minecraft.util.math.MathHelper.clamp(ghostPitch + (float)((Math.random() - 0.5) * 0.2), -90.0f, 90.0f));
'''
    assert s.count(a)==1,"ghostSync bulunamadi"
    s=s.replace(a,'''        double ghostSens = (Double)mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
        float ghostGcd = (float)(ghostSens * ghostSens * ghostSens * 1.2);
        mc.player.setYaw(ghostYaw + ghostGcd * (float)(Math.floor(Math.random() * 3.0) - 1.0));
        mc.player.setPitch(net.minecraft.util.math.MathHelper.clamp(ghostPitch + ghostGcd * (float)(Math.floor(Math.random() * 3.0) - 1.0), -90.0f, 90.0f));
''')
    open(p,'w').write(s)
print("TAMAM")
