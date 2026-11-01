B='src/main/java/thunder/hack/gui/clickui/'
def ed(f,pairs,mark):
    s=open(B+f).read()
    if mark in s:
        return
    for a,b in pairs:
        assert s.count(a)==1,(f,a[:60],s.count(a))
        s=s.replace(a,b)
    open(B+f,'w').write(s)
ed('Category.java',[
('        Color m4 = HudEditor.getColor(90);\n',
 '        Color m4 = HudEditor.getColor(90);\n        Render2DEngine.drawBlurredShadow(context.getMatrices(), this.getX() - 2.0f, this.getY() - 2.0f, this.width + 4.0f, 24.0f + (this.isOpen() ? this.catHeight : 0.0f), 16, new Color(m2.getRed(), m2.getGreen(), m2.getBlue(), 75)); // zwStyle\n'),
('        Render2DEngine.drawRound(context.getMatrices(), this.getX(), this.getY(), this.width, 20.0f, 4.0f, HudEditor.getColor(270));\n',
 '        Render2DEngine.drawGradientRound(context.getMatrices(), this.getX(), this.getY(), this.width, 20.0f, 7.0f, m1, m2, m3, m4);\n'),
('        if (this.isOpen()) {\n            Render2DEngine.drawRound(context.getMatrices(), this.getX(), this.getY() + 20.0f, this.width, this.catHeight, 4.0f, ModuleManager.clickGui.categoryColor.getValue().getColorObject());\n        }\n',
 '        if (this.isOpen()) {\n            Render2DEngine.drawRound(context.getMatrices(), this.getX(), this.getY() + 20.0f, this.width, this.catHeight, 7.0f, ModuleManager.clickGui.categoryColor.getValue().getColorObject());\n            Render2DEngine.drawRound(context.getMatrices(), this.getX(), this.getY() + 20.0f, this.width, this.catHeight, 7.0f, new Color(10, 8, 16, 70));\n            Render2DEngine.drawRound(context.getMatrices(), this.getX() + 8.0f, this.getY() + 19.0f, this.width - 16.0f, 1.5f, 0.7f, HudEditor.getColor(0));\n        }\n'),
],'zwStyle')
ed('ModuleButton.java',[
('        if (!this.module.getBind().getBind().equalsIgnoreCase("none") && !this.binding) {\n',
 '        if (this.hovered) {\n            Render2DEngine.drawRound(context.getMatrices(), this.x + 4.0f, this.y + 1.0f, this.width - 8.0f, this.height - 2.0f, 3.0f, new Color(255, 255, 255, 16)); // zwStyle\n        }\n        if (this.animation > 0.05f) {\n            Render2DEngine.drawRound(context.getMatrices(), this.x + 4.0f, this.y + 3.0f + (this.height - 6.0f) * (1.0f - this.animation) / 2.0f, 2.0f, (this.height - 6.0f) * this.animation, 1.0f, HudEditor.getColor(0));\n        }\n        if (!this.module.getBind().getBind().equalsIgnoreCase("none") && !this.binding) {\n'),
],'zwStyle')
print("TAMAM")
