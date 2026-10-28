p='src/main/java/thunder/hack/features/modules/misc/HeadSpin.java'
s=open(p).read()
a='Float.valueOf(35.0f), Float.valueOf(5.0f), Float.valueOf(90.0f));'
assert s.count(a)==1
s=s.replace(a,'Float.valueOf(20.0f), Float.valueOf(5.0f), Float.valueOf(90.0f));')
a='    private float angle = 0.0f;\n'
assert s.count(a)==1
s=s.replace(a,'    private final Setting<Boolean> spinWhileMoving = new Setting<Boolean>("Spin While Moving", false);\n'+a)
a='        final float realYaw = HeadSpin.mc.player.getYaw();\n'
assert s.count(a)==1
s=s.replace(a,'''        boolean moving = HeadSpin.mc.player.input.movementForward != 0.0f || HeadSpin.mc.player.input.movementSideways != 0.0f || HeadSpin.mc.player.input.jumping || HeadSpin.mc.player.isFallFlying();
        if (moving && !this.spinWhileMoving.getValue().booleanValue()) {
            return;
        }
'''+a)
open(p,'w').write(s)
print("TAMAM")
