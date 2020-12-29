/*
 * 
 *  This file is part of EnchantmentsPlus, a bukkit plugin.
 *  Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 *  Copyright (c) 2020 Geolykt and EnchantmentsPlus contributors
 *   
 *  This program is free software: you can redistribute it and/or modify  
 *  it under the terms of the GNU General Public License as published by  
 *  the Free Software Foundation, version 3.
 *  
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License 
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.geolykt.enchantments_plus.evt.ench;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import de.geolykt.enchantments_plus.enchantments.Spectral;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;

/**
 * Pure permission querying Event, used by the {@link Spectral} class. After 1.2.1 it used to query permissions of block breaking. <br>
 * It should be noted that the targeted block can be changed after runtime via the {@link BlockSpectralChangeEvent#adjustBlock(Block)} method
 * which DOES not change the drops, as such the {@link BlockSpectralChangeEvent#getExpToDrop()} should not be used.
 * @since 1.0
 * @implNote Although the name implies that it is only used by the SpectralEnchantment, this is no longer true.
 *           To check which enchantment threw the event, use {@link BlockSpectralChangeEvent#getUse()}
 */
public class BlockSpectralChangeEvent extends BlockBreakEvent  {

    /**
     * This variable keeps track on which enchantment invoked the Event, which is useful for 3rd party APIs to check for which reason the query was used.<br>
     * By default it's {@link BaseEnchantments#SPECTRAL} and cannot be changed during the object's lifecycle.
     * @since 1.2.1
     */
    protected final BaseEnchantments invokingEnchantment;

    /**
     * This constructor constructs a BlockSpectralChangeEvent based on a {@link Block} and a {@link Player}. <br>
     * This specific Constructor does not throw an IllegalStateException and allows to set the {@link BaseEnchantments} ench.
     * @param theBlock The block that is designated to be queried
     * @param player The player that is targeted
     * @param ench The Enchantment the object is meant to be used for
     * @since 1.2.1
     */
    public BlockSpectralChangeEvent(@NotNull Block theBlock, @NotNull Player player, @NotNull BaseEnchantments ench) {
        super(theBlock, player);
        invokingEnchantment = ench;
    }

    /**
     * Changes the block the query is targeting.
     * @param newBlock The block to be set as the target
     * @since 1.1.5
     * @implNote This implementation does not reset nor recalculate block and XP drops.
     * @implNote This implementation resets the isCancelled() state of false, as thus it is recommended to take a snapshot if it's important to your use case
     */
    public void adjustBlock(@NotNull Block newBlock) {
        block = newBlock;
        setCancelled(false);
    }

    /**
     * This variable keeps track on which enchantment invoked the Event, which is useful for 3rd party APIs to check for which reason the query was used.<br>
     * By default it's {@link BaseEnchantments#SPECTRAL} and cannot be changed during the object's lifecycle.
     * @return The enchantment that has issued the event.
     * @since 1.2.1
     */
    public BaseEnchantments getUse() {
        return invokingEnchantment;
    }
}
