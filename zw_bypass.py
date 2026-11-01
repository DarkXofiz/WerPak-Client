p='src/main/java/thunder/hack/features/modules/combat/ElytraTarget.java'
s=open(p).read()
if 'BypassMode' not in s:
    a='    public static PlayerEntity target = null;\n'
    assert s.count(a)==1,"hedef satiri yok"
    s=s.replace(a,'''    public final Setting<BypassMode> bypassMode = new Setting<BypassMode>("Bypass", BypassMode.Steer);
    public final Setting<Float> bypassTurn = new Setting<Float>("Bypass Turn", Float.valueOf(18.0f), Float.valueOf(2.0f), Float.valueOf(60.0f), v -> this.bypassMode.getValue() == BypassMode.Steer);
'''+a)
    a='                if (isFlying) {\n                    Vec3d dir = predictedPos.subtract(ElytraTarget.mc.player.getPos());'
    assert s.count(a)==1,"ucus blogu yok"
    s=s.replace(a,'''                if (isFlying && this.bypassMode.getValue() == BypassMode.Steer && predictedPos != null) {
                    Vec3d steer = predictedPos.subtract(ElytraTarget.mc.player.getEyePos());
                    double flat = Math.sqrt(steer.x * steer.x + steer.z * steer.z);
                    float wantYaw = (float)(Math.toDegrees(Math.atan2(steer.z, steer.x)) - 90.0);
                    float wantPitch = (float)(-Math.toDegrees(Math.atan2(steer.y, flat)));
                    float lim = this.bypassTurn.getValue().floatValue();
                    float dYaw = MathHelper.clamp((float)MathHelper.wrapDegrees((float)(wantYaw - ElytraTarget.mc.player.getYaw())), (float)(-lim), (float)lim);
                    float dPitch = MathHelper.clamp((float)(wantPitch - ElytraTarget.mc.player.getPitch()), (float)(-lim * 0.6f), (float)(lim * 0.6f));
                    dYaw += (float)((Math.random() - 0.5) * 0.6);
                    dPitch += (float)((Math.random() - 0.5) * 0.4);
                    ElytraTarget.mc.player.setYaw(ElytraTarget.mc.player.getYaw() + dYaw);
                    ElytraTarget.mc.player.setPitch(MathHelper.clamp((float)(ElytraTarget.mc.player.getPitch() + dPitch), (float)-89.0f, (float)89.0f));
                }
                if (isFlying && this.bypassMode.getValue() == BypassMode.Off) {
                    Vec3d dir = predictedPos.subtract(ElytraTarget.mc.player.getPos());''')
    i=s.rstrip().rfind('}')
    s=s[:i]+'\n    public enum BypassMode {\n        Off,\n        Steer\n    }\n'+s[i:]
    open(p,'w').write(s)
print("TAMAM")
