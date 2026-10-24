/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Identifier
 */
package thunder.hack.gui.font;

import net.minecraft.util.Identifier;

public class Texture {
    final Identifier id;

    public Texture(String path) {
        this.id = Identifier.of((String)"thunderhack", (String)this.validatePath(path));
    }

    public Texture(Identifier i) {
        this.id = Identifier.of((String)i.getNamespace(), (String)i.getPath());
    }

    String validatePath(String path) {
        if (Identifier.isPathValid((String)path)) {
            return path;
        }
        StringBuilder ret = new StringBuilder();
        for (char c : path.toLowerCase().toCharArray()) {
            if (!Identifier.isPathCharacterValid((char)c)) continue;
            ret.append(c);
        }
        return ret.toString();
    }

    public Identifier getId() {
        return this.id;
    }
}

