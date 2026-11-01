import os, re
NEW = "zeltraxCrack"
PRE = ["ZeltrixWareZenoBest_Crack", "WerPak Client", "WerPakClient", "WerPak"]
HUD = "(Keybinds|TargetHud|WatermarkAlternative|WatermarkV2|Watermark)"
assert os.path.isdir("src/main/java"), "yanlis klasor"
def files():
    for top in ("src", ".github", "NOTES.txt", "gradle.properties", "build.gradle"):
        if os.path.isfile(top):
            yield top
        else:
            for r, _, fs in os.walk(top):
                for f in fs:
                    yield os.path.join(r, f)
def rd(p):
    try:
        return open(p, encoding="utf-8").read()
    except Exception:
        return None
# A) HUD sinif adlari orijinal Zeltrix* olsun
for p in list(files()):
    s = rd(p)
    if s is None: continue
    t = s
    for pre in PRE[:1] + PRE[3:]:
        t = re.sub(re.escape(pre) + HUD + r"\b", r"Zeltrix\1", t)
    if p.endswith(".java") and "/features/hud/impl/Zeltrix" not in p:
        pass
    if t != s: open(p, "w", encoding="utf-8").write(t)
hd = "src/main/java/thunder/hack/features/hud/impl/"
for n in ("Keybinds", "TargetHud", "Watermark", "WatermarkAlternative", "WatermarkV2"):
    for pre in (PRE[0], PRE[3]):
        old = hd + pre + n + ".java"
        if os.path.exists(old):
            os.rename(old, hd + "Zeltrix" + n + ".java")
    p = hd + "Zeltrix" + n + ".java"
    s = rd(p)
    if s is not None:
        for pre in PRE:
            s = s.replace(pre, "Zeltrix")
        open(p, "w", encoding="utf-8").write(s)
# B) geri kalan her yer: zeltraxCrack
for p in list(files()):
    s = rd(p)
    if s is None: continue
    t = s
    for pre in PRE:
        t = t.replace(pre, NEW)
    if t != s: open(p, "w", encoding="utf-8").write(t)
print("TAMAM")
