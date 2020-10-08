package de.geolykt.enchantments_plus.compatibility.enchantmentgetters;

import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;

public interface IEnchGatherer {

    /**
     * This method gets the Enchantments on an ItemStack, how the Enchantments are stored and read depends on the implementation
     * @param stk The Itemstack
     * @param world The World that is used for the Configuration
     * @param outExtraLore Puts non-zenchantments lore in this list
     * @return A map of the Enchantments mapped to their level
     * @since 2.0.0
     */
    public default LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world, final List<String> outExtraLore) {
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
    public default LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks, World world) {
        return getEnchants(stk, acceptBooks, world, null);
    }

    /**
     * This method gets the Enchantments on an ItemStack, how the Enchantments are stored and read depends on the implementation
     * @param stk The Itemstack
     * @param world The World that is used for the Configuration
     * @return A map of the Enchantments mapped to their level
     * @since 2.0.0
     */
    public default LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world) {
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
    public abstract LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk,
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
    public abstract void setEnchantment(ItemStack stk, CustomEnchantment ench, int level, World world);
}
