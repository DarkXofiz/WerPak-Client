p='src/main/java/thunder/hack/features/modules/combat/ElytraTarget.java'
s=open(p).read()
if 'Max\n' not in s.split('enum BypassMode')[-1]:
    pairs=[
    ('        Off,\n        Steer\n','        Off,\n        Steer,\n        Max\n'),
    ('new Setting<BypassMode>("Bypass", BypassMode.Steer)','new Setting<BypassMode>("Bypass", BypassMode.Max)'),
    ('v -> this.bypassMode.getValue() == BypassMode.Steer);','v -> this.bypassMode.getValue() != BypassMode.Off);'),
    ('isFlying && this.bypassMode.getValue() == BypassMode.Steer && predictedPos != null','isFlying && this.bypassMode.getValue() != BypassMode.Off && predictedPos != null'),
    ('                    ElytraTarget.mc.player.setYaw(ElytraTarget.mc.player.getYaw() + dYaw);\n',
'''                    if (this.bypassMode.getValue() == BypassMode.Max) {
                        double sens = (Double)ElytraTarget.mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
                        double gcd = sens * sens * sens * 8.0 * 0.15;
                        float ease = 0.55f + (float)(Math.random() * 0.25);
                        dYaw *= ease;
                        dPitch *= ease;
                        dYaw = (float)((double)Math.round((double)dYaw / gcd) * gcd);
                        dPitch = (float)((double)Math.round((double)dPitch / gcd) * gcd);
                    }
                    ElytraTarget.mc.player.setYaw(ElytraTarget.mc.player.getYaw() + dYaw);
'''),
    ]
    for a,b in pairs:
        assert s.count(a)==1,a[:60]
        s=s.replace(a,b)
    open(p,'w').write(s)
print("TAMAM")
