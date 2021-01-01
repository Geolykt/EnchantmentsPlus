/*
 *  This file is part of EnchantmentsPlus, a bukkit plugin.
 *  Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 *  Copyright (c) 2020 - 2021 Geolykt and EnchantmentsPlus contributors
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
package de.geolykt.enchantments_plus.compatibility.enchantmentgetters;

import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;

public interface IEnchGatherer {

    /**
     * This method gets the Enchantments on an ItemStack, how the Enchantments are stored and read depends on the implementation
     * @param stk The Itemstack
     * @param world The World that is used for the Configuration
     * @param outExtraLore Puts non-zenchantments lore in this list
     * @return A map of the Enchantments mapped to their level
     * @since 2.0.0
     */
    public default LinkedHashMap<CustomEnchantment, Integer> getEnchants(@Nullable ItemStack stk, World world, final List<String> outExtraLore) {
        return getEnchants(stk, false, world, outExtraLore);
    }

    /**
     * This method gets the Enchantments on an ItemStack, how the Enchantments are stored and read depends on the implementation
     * @param stk The Itemstack
     * @param acceptBooks Whether books should be accepted
     * @param world The World that is used for the Configuration
     * @return A map of the Enchantments mapped to their level
     * @since 2.0.0
     */
    public default LinkedHashMap<CustomEnchantment, Integer> getEnchants(@Nullable ItemStack stk, boolean acceptBooks, World world) {
        return getEnchants(stk, acceptBooks, world, null);
    }

    /**
     * This method gets the Enchantments on an ItemStack, how the Enchantments are stored and read depends on the implementation
     * @param stk The Itemstack
     * @param world The World that is used for the Configuration
     * @return A map of the Enchantments mapped to their level
     * @since 2.0.0
     */
    public default LinkedHashMap<CustomEnchantment, Integer> getEnchants(@Nullable ItemStack stk, World world) {
        return getEnchants(stk, false, world, null);
    }

    /**
     * This method gets the Enchantments on an ItemStack, how the Enchantments are stored and read depends on the implementation
     * @param stk The Itemstack
     * @param acceptBooks Whether books should be accepted
     * @param world The World that is used for the Configuration
     * @param outExtraLore Puts non-zenchantments lore in this list
     * @return Returns a mapping of custom enchantments and their level on a given tool
     * @since 2.0.0
     */
    public abstract LinkedHashMap<CustomEnchantment, Integer> getEnchants(@Nullable ItemStack stk,
            boolean acceptBooks,
            World world,
            final List<String> outExtraLore);

    /**
     * Applies an Enchantment on an ItemStack, how the Enchantment are written and stored depends on the implementation
     * @param stk The itemstack
     * @param ench The Enchantment that should be applied
     * @param level The Level of the Enchantment
     * @param world The World where the Itemstack is located. Used for Configurations and similar
     * @since 2.0.0
     */
    public abstract void setEnchantment(@Nullable ItemStack stk, CustomEnchantment ench, int level, World world);

    /**
     * Checks whether an Item has a given Enchantment. This should be used instead of a normal map.contains() as it can be
     * Optimised by some getters to result in significantly better performance.
     * @param config The configuration that should be used
     * @param stk The stack that should be read
     * @param ench The Enchantment that should be searched for.
     * @return True if the stack contained the given Enchantment, false otherwise
     * @since 2.1.1
     */
    public default boolean hasEnchantment(@NotNull Config config, @Nullable ItemStack stk, @NotNull BaseEnchantments ench) {
        return getEnchantmentLevel(config, stk, ench) != 0;
    }

    /**
     * Returns the level of a given enchantment. This should be used instead of a normal map.get() as it can be
     * Optimised by some getters to result in significantly better performance. <br>
     * In case the enchantment is not found, 0 should be returned.
     * @param config The configuration that should be used
     * @param stk The stack that should be read
     * @param ench The Enchantment that should be searched for.
     * @return The level of the given enchantment on a given stack.
     * @since 2.1.1
     */
    public abstract int getEnchantmentLevel(@NotNull Config config, @Nullable ItemStack stk, @NotNull BaseEnchantments ench);
}
