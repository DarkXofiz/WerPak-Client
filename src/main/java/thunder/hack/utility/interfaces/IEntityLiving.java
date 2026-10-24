/*
 * Decompiled with CFR 0.152.
 */
package thunder.hack.utility.interfaces;

import java.util.List;
import thunder.hack.features.modules.combat.Aura;

public interface IEntityLiving {
    public double getPrevServerX();

    public double getPrevServerY();

    public double getPrevServerZ();

    public List<Aura.Position> getPositionHistory();
}

