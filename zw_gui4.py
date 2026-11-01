B='src/main/java/thunder/hack/gui/clickui/'
def ed(f,pairs,mark):
    s=open(B+f).read()
    if mark in s: return
    for a,b in pairs:
        assert s.count(a)==1,(f,a[:60],s.count(a))
        s=s.replace(a,b)
    open(B+f,'w').write(s)
ed('ClickGUI.java',[(
'            Render2DEngine.drawHudBase(context.getMatrices(), (float)(mouseX + 7), (float)(mouseY + 5), FontRenderers.sf_medium.getStringWidth(currentDescription) + 6.0f, 11.0f, 1.0f, false);\n',
'''            Render2DEngine.drawBlurredShadow(context.getMatrices(), (float)(mouseX + 7), (float)(mouseY + 5), FontRenderers.sf_medium.getStringWidth(currentDescription) + 10.0f, 13.0f, 10, new Color(135, 80, 255, 50)); // zwTip
            Render2DEngine.drawRound(context.getMatrices(), (float)(mouseX + 7), (float)(mouseY + 5), FontRenderers.sf_medium.getStringWidth(currentDescription) + 10.0f, 13.0f, 5.0f, new Color(12, 10, 18, 215));
            Render2DEngine.drawRound(context.getMatrices(), (float)(mouseX + 7), (float)(mouseY + 7), 1.5f, 9.0f, 0.7f, HudEditor.getColor(0));
'''),
('            FontRenderers.sf_medium.drawString(context.getMatrices(), currentDescription, (double)(mouseX + 10), (double)(mouseY + 8), HudEditor.getColor(0).getRGB());\n',
 '            FontRenderers.sf_medium.drawString(context.getMatrices(), currentDescription, (double)(mouseX + 13), (double)(mouseY + 8), -1);\n'),
('        float zwGlass =',
'''        String zwHint = "Sag tik: kategori ac/kapat   |   Shift: Drawn   |   Scroll: kaydir";
        FontRenderers.sf_medium.drawString(context.getMatrices(), zwHint, (double)((float)Module.mc.getWindow().getScaledWidth() - FontRenderers.sf_medium.getStringWidth(zwHint) - 10.0f), (double)((float)Module.mc.getWindow().getScaledHeight() - 14.0f), new Color(255, 255, 255, 110).getRGB());
        float zwGlass ='''),
],'zwTip')
print("TAMAM")
