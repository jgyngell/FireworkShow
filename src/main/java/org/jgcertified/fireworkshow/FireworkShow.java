package org.jgcertified.fireworkshow;

import org.redstonechips.RCPrefs;
import org.redstonechips.RedstoneChips;
import org.redstonechips.circuit.CircuitLibrary;

/**
 *
 * @author Tal Eisenberg
 */
public class FireworkShow extends CircuitLibrary {
    @Override
    public Class[] getCircuitClasses() {
        return new Class[] { firework.class };
    }

    @Override
    public void onRedstoneChipsEnable(RedstoneChips rc) {
    }
}
