p='src/main/java/thunder/hack/features/modules/combat/Aura.java'
s=open(p).read()
if 'COBWEB' not in s:
    a='        if (ModuleManager.criticals.isEnabled() && ModuleManager.criticals.mode.is(Criticals.Mode.Grim)) {\n            return true;\n        }\n'
    assert s.count(a)==1,"autoCrit bolumu bulunamadi"
    s=s.replace(a,'''        if (Aura.mc.world != null && (Aura.mc.world.getBlockState(Aura.mc.player.getBlockPos()).isOf(net.minecraft.block.Blocks.COBWEB) || Aura.mc.world.getBlockState(Aura.mc.player.getBlockPos().up()).isOf(net.minecraft.block.Blocks.COBWEB))) {
            return true;
        }
'''+a)
    open(p,'w').write(s)
print("TAMAM")
