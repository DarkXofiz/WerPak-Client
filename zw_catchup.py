p='src/main/java/thunder/hack/features/modules/combat/ElytraTarget.java'
s=open(p).read()
if 'catchUp' not in s:
    pairs=[
    ('    public static PlayerEntity target = null;\n',
     '    public final Setting<Float> catchUp = new Setting<Float>("Catch Up", Float.valueOf(1.2f), Float.valueOf(1.0f), Float.valueOf(2.0f));\n    private double lastTargetSpeed = 0.0;\n    public static PlayerEntity target = null;\n'),
    ('                    double totalDist =',
     '                    this.lastTargetSpeed = velocity.length();\n                    double totalDist ='),
    ('                    if (speed < 0.1) {\n                        speed = 1.0;\n                    }\n',
     '                    if (speed < 0.1) {\n                        speed = 1.0;\n                    }\n                    speed = Math.max(speed, this.lastTargetSpeed * this.catchUp.getValue().doubleValue());\n'),
    ('Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(25.0f));','Float.valueOf(4.0f), Float.valueOf(0.0f), Float.valueOf(25.0f));'),
    ]
    for a,b in pairs:
        assert s.count(a)==1,(a[:50],s.count(a))
        s=s.replace(a,b)
    open(p,'w').write(s)
print("TAMAM")
