package de.geolykt.enchantments_plus.enums;

import de.geolykt.enchantments_plus.enchantments.Pierce;

/**
 * The modes used by the {@link Pierce} enchantment.
 * The individual modes can be enabled and disabled via the patches.yml file
 *
 * @since 4.0.0
 */
public enum PierceMode {

    /**
     * When a block is broken the two blocks behind the source block is broken.
     *
     * @since 4.0.0
     */
    LONG,

    /**
     * The normal vanilla behaviour that occurs when breaking a block. No further blocks are broken.
     *
     * @since 4.0.0
     */
    NORMAL,

    /**
     * When a block is broken the block above and under the block is broken.
     *
     * @since 4.0.0
     */
    TALL,

    /**
     * When a block is broken that is an ore within an ore vein, then the entire vein is broken.
     * If the block is not an ore, then {@link #NORMAL} behaviour is used.
     *
     * @since 4.0.0
     */
    VEIN,

    /**
     * When a block is broken the two blocks next to the block are broken that are neither behind, above or under
     * the source block.
     *
     * @since 4.0.0
     */
    WIDE;
}
