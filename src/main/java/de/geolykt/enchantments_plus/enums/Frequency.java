package de.geolykt.enchantments_plus.enums;

import de.geolykt.enchantments_plus.TaskRunner;
import de.geolykt.enchantments_plus.annotations.EffectTask;

/**
 * Frequencies for {@link EffectTask}
 *
 */
public enum Frequency {
    HIGH(1), MEDIUM_HIGH(5), /*MEDIUM_LOW(10), LOW(20),*/ SLOW(200);

    public final int period;

    /**
     * Constructs a Frequency object which is run every {@code i} seconds.
     *
     * @param period Period of execution for annotation method, in seconds.
     *
     * @see TaskRunner
     */
    Frequency(int period) {
        this.period = period;
    }
}
