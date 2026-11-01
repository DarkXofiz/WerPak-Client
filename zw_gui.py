import re
# 1) jar adi: surum eki olmasin
p='build.gradle'
s=open(p).read()
if 'archiveVersion' not in s:
    s=s.rstrip('\n')+'\n\ntasks.withType(AbstractArchiveTask).configureEach {\n\tarchiveVersion = ""\n}\n'
    open(p,'w').write(s)
# 2) ClickGUI guzellestirme
p='src/main/java/thunder/hack/gui/clickui/ClickGUI.java'
s=open(p).read()
if 'zwGlass' not in s:
    pat=re.compile(r'        FontRenderers\.sf_bold\.drawCenteredString\(context\.getMatrices\(\), "[^"]*", 40\.0, 10\.0, ModuleManager\.clickGui\.categoryColor\.getValue\(\)\.getColor\(\)\);\n')
    assert len(pat.findall(s))==1,"baslik satiri bulunamadi"
    s=pat.sub('''        FontRenderers.sf_bold.drawString(context.getMatrices(), "ZeltrixWareZenoBest_Crack", 10.0, 9.0, ModuleManager.clickGui.categoryColor.getValue().getColor());
        Render2DEngine.drawRound(context.getMatrices(), 10.0f, 21.0f, 70.0f, 1.5f, 0.7f, HudEditor.getColor(0));
        Render2DEngine.drawRound(context.getMatrices(), 82.0f, 21.0f, 24.0f, 1.5f, 0.7f, HudEditor.getColor(90));
''',s)
    a='        FontRenderers.sf_medium.drawString(context.getMatrices(), "GUI Color", (double)(cx - 50.0f), (double)(cy + 4.0f), -1);\n'
    assert s.count(a)==1,"gui color satiri bulunamadi"
    s=s.replace(a,'''        float zwGlass = (float)colors.length * 20.0f + 62.0f;
        Render2DEngine.drawBlurredShadow(context.getMatrices(), cx - 56.0f, cy - 5.0f, zwGlass, 25.0f, 14, new Color(135, 80, 255, 55));
        Render2DEngine.drawRound(context.getMatrices(), cx - 56.0f, cy - 5.0f, zwGlass, 25.0f, 9.0f, new Color(12, 10, 18, 160));
'''+a)
    a='        anyHovered = false;\n        if (Module.fullNullCheck()) {\n            this.renderBackground(context, mouseX, mouseY, delta);\n        }\n'
    assert s.count(a)==1,"arka plan blogu bulunamadi"
    s=s.replace(a,a+'        Render2DEngine.drawRect(context.getMatrices(), 0.0f, 0.0f, (float)Module.mc.getWindow().getScaledWidth(), (float)Module.mc.getWindow().getScaledHeight(), new Color(8, 6, 14, 80));\n')
    open(p,'w').write(s)
print("TAMAM")
