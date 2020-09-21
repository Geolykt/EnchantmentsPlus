package de.geolykt.enchantments_plus.evt.ench;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import de.geolykt.enchantments_plus.enchantments.Spectral;

/**
 * Pure permission querying Event, used by the {@link Spectral} class. <br>
 * It should be noted that the targeted block can be changed after runtime via the {@link BlockSpectralChangeEvent#adjustBlock(Block)} method
 * which DOES not change the drops, as such the {@link BlockSpectralChangeEvent#getExpToDrop()} should not be used.
 * @since 1.0
 */
public class BlockSpectralChangeEvent extends BlockBreakEvent  {

    /**
     * This constructor constructs a {@link BlockSpectralChangeEvent} based on a {@link Block} {@link Player}.
     * @param theBlock The block that is designated to be queried
     * @param player The player that is targeted
     * @since 1.0
     * @throws IllegalStateException If the {@link Spectral#performWorldProtection} is false, at which the constructor (and as such the class in itself) is disabled.
     */
    public BlockSpectralChangeEvent(@NotNull Block theBlock, @NotNull Player player) {
        super(theBlock, player);
        if (!Spectral.performWorldProtection) {
            throw new IllegalStateException("A BlockSpectralChangeEvent was constructed but World protection for Spectral is turned off");
        }
    }
    
    /**
     * Changes the block the query is targeting.
     * @param newBlock The block to be set as the target
     * @since 1.1.5
     * @implNote This implementation does not reset or recalculate block and XP drops.
     * @implNote This implementation resets the isCancelled() state of false, as thus it is recommended to take a snapshot if it's important to your use case
     */
    public void adjustBlock(@NotNull Block newBlock) {
        block = newBlock;
        setCancelled(false);
    }
}
