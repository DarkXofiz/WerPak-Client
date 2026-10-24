/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.utility.interfaces;

import thunder.hack.features.modules.combat.Aura;

public interface IOtherClientPlayerEntity {
    public void resolve(Aura.Resolver var1);

    public void releaseResolver();
}

