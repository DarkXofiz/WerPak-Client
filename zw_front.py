p='src/main/java/thunder/hack/features/modules/combat/ElytraTarget.java'
s=open(p).read()
if 'frontOffset' not in s:
    a='    public static PlayerEntity target = null;\n'
    assert s.count(a)==1,"hedef satiri yok"
    s=s.replace(a,'    public final Setting<Float> frontOffset = new Setting<Float>("Front Offset", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(25.0f));\n'+a)
    a='predictedPos = movePred.subtract(targetFacing.multiply(this.currentOverrun)).add(0.0, 0.2, 0.0);'
    assert s.count(a)==1,"tahmin satiri yok"
    s=s.replace(a,a+'\n                    predictedPos = predictedPos.add(targetFacing.multiply(this.frontOffset.getValue().doubleValue()));')
    a='predictedPos = this.getFrontPosition((Entity)target, this.overshoot.getValue().doubleValue());'
    assert s.count(a)==1,"on konum satiri yok"
    s=s.replace(a,a+'\n                predictedPos = predictedPos.add(new Vec3d(-Math.sin(Math.toRadians(target.getYaw())), 0.0, Math.cos(Math.toRadians(target.getYaw()))).multiply(this.frontOffset.getValue().doubleValue()));')
    open(p,'w').write(s)
print("TAMAM")
