/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2021 Geolykt and EnchantmentsPlus contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.geolykt.enchantments_plus.evt;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.util.Tool;

/**
 * The second generation of the Anvil merger, the class that handles merging of enchantments on two Items within the Anvil.
 * Reason for the need of a second generation is due to an increase of bug reports and next to no understanding of how the code works.
 * 
 * @since 2.1.5
 */
public class NewAnvilMerger implements Listener {

    private static EnumSet<Material> supportedMaterials = null;

    /**
     * Checks if an ItemStack can be enchanted via the plugin's enchantments.
     *
     * @param is The input itemstack
     * @since 3.1.5
     */
    public static boolean isEnchantable(@Nullable ItemStack is) {
        if (supportedMaterials == null) {
            // Populate the list
            supportedMaterials = EnumSet.noneOf(Material.class);
            for (Tool tool : Tool.values()) {
                tool.getMaterials().forEach(supportedMaterials::add);
            }
            supportedMaterials.add(Material.ENCHANTED_BOOK);
        }
        return is != null && supportedMaterials.contains(is.getType());
    }

    /**
     * The remapping function used by {@link #mergeEnchantments(Map, Map)} to merge the levels of the same enchantments.
     *  Default behaviour (v2.1.5) results in the following: if Integer A and Integer B are the same and non-0, then the smaller of A+1 or the maximum level
     *   of the enchantment is returned.
     *  Otherwise the bigger of the two input levels is returned.
     * @param ench The enchantment that needs to be remapped, used to retrieve the maximum enchantment level
     * @param inputA The primary input level
     * @param inputB The secondary input level
     * @return Returns the new level of the Enchantment.
     * @since 2.1.5
     */
    public static int enchantmentLevelRemappingFunction(@NotNull CustomEnchantment ench, Integer inputA, Integer inputB) {
        if (inputA != 0 && inputB != 0 && inputA == inputB) {
            return Math.min(inputA + 1, ench.getMaxLevel());
        } else {
            return Math.min(Math.max(inputA, inputB), ench.getMaxLevel());
        }
    }

    /**
     * Merges two CustomEnchantment to Enchantment Level maps into each other to result a new Map that contains the keys of both maps.
     * It is guaranteed that the Keys on the output map are unique, in case a clash occurs, the highest value is picked or the value of one
     * entry is added by one and put on the list, provided that it's maximum level isn't surpassed.
     * The primary input map is shuffled so when the secondary input map contains enchantments that are incompatible with each other
     * both enchantments should have a roughly equal chance. Note that the method does not correct any mistakes made on that topic with
     * both input maps and if two incompatible enchantments are on the same input map then chances are that they will both be present in the
     * output map. The secondary input map is not shuffled.
     * @param inA Primary input enchantments
     * @param inB Secondary input enchantments
     * @return Output enchantments
     * @since 2.1.5
     */
    public static Map<CustomEnchantment, Integer> mergeEnchantments(Map<CustomEnchantment, Integer> inA, Map<CustomEnchantment, Integer> inB) {
        // TODO revisit this code in the v4.0.0 refractor (great things can be changed when we use Enums instead of CE instances)
        LinkedHashMap<CustomEnchantment, Integer> out = new LinkedHashMap<>(inA);

        // pretty sure I'm overcomplicating things again
        out.keySet().forEach((ench) -> {
            inB.keySet().removeIf((newEnch) -> {
                return ench.getConflicts().contains(newEnch.asEnum());
            });
        });
        for (Map.Entry<CustomEnchantment, Integer> entry : inB.entrySet()) {
            CustomEnchantment ench = entry.getKey();
            out.put(ench, enchantmentLevelRemappingFunction(ench, entry.getValue(), out.getOrDefault(entry.getKey(), 0)));
        }
        return out;
    }

    /**
     * This Event prepares the merge and under certain circumstances forces it to allow to happen.
     * Under normal circumstances the enchantments on both input items are extracted and put on the output item,
     * however if the merge doesn't happen under vanilla circumstances (for example both input items are not broken),
     * then the method tries a best-case guess to merge the right item on the left item and sets the amount of required levels for it
     * @param evt The event
     * @since 2.1.5
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void prepareEvent(PrepareAnvilEvent evt) {
        AnvilInventory inv = evt.getInventory();
        if (inv.getSize() < 3 || evt.getViewers().size() == 0 || !isEnchantable(inv.getItem(0))) {
            return; // Don't allow for obviously misaligned merging
        }
        List<String> nleftLore = new ArrayList<String>();
        if (evt.getResult() == null || evt.getResult().getType() == Material.AIR) {
            // Best guess merge
            ItemStack stackA = inv.getItem(0).clone();
            ItemStack stackB = inv.getItem(1);
            if (stackB != null && (stackA.getType() == stackB.getType() && CompatibilityAdapter.getDamage(stackA) == 0 || stackB.getType() == Material.ENCHANTED_BOOK) && CompatibilityAdapter.getDamage(stackB) == 0) {
                // Undamaged conversion
                World world = evt.getViewers().get(0).getWorld();
                Map<CustomEnchantment, Integer> out = mergeEnchantments(
                        CustomEnchantment.Enchantment_Adapter.getEnchants(stackA, true, world, nleftLore),
                        CustomEnchantment.Enchantment_Adapter.getEnchants(stackB, true, world, null));
                if (out.size() > 0) {
                    for (Map.Entry<CustomEnchantment, Integer> ench : out.entrySet()) {
                        if (ench.getKey().asEnum() == BaseEnchantments.UNREPAIRABLE && ench.getValue() != 0) {
                            evt.setResult(null);
                            return;
                        }
                    }
                    int appliedEnchants = 0;
                    int maxEnchants = Config.get(world).getMaxEnchants();
                    // Apply enchantments
                    for (Map.Entry<CustomEnchantment, Integer> ench : out.entrySet()) {
                        if ((ench.getKey().validMaterial(stackA) || stackA.getType() == Material.ENCHANTED_BOOK) && appliedEnchants++ < maxEnchants) {
                            CustomEnchantment.setEnchantment(stackA, ench.getKey(), ench.getValue(), world);
                        } else {
                            // Remove enchantment, don't go into risks, HashMaps aren't sorted in a particular order
                            CustomEnchantment.setEnchantment(stackA, ench.getKey(), 0, world);
                        }
                    }
                    inv.setRepairCost(out.size()*4+inv.getRepairCost());
                    evt.setResult(stackA);
                }
            }
        } else {
            final ItemStack result = evt.getResult().clone();
            World world = evt.getViewers().get(0).getWorld();
            Map<CustomEnchantment, Integer> out = mergeEnchantments(
                    CustomEnchantment.Enchantment_Adapter.getEnchants(inv.getItem(0), true, world, nleftLore),
                    CustomEnchantment.Enchantment_Adapter.getEnchants(inv.getItem(1), true, world, null));
            if (out.size() > 0) {
                for (Map.Entry<CustomEnchantment, Integer> ench : out.entrySet()) {
                    if (ench.getKey().asEnum() == BaseEnchantments.UNREPAIRABLE && ench.getValue() != 0) {
                        evt.setResult(null); // Abort merge
                        return;
                    }
                }
                int appliedEnchants = 0;
                int maxEnchants = Config.get(world).getMaxEnchants();
                for (Map.Entry<CustomEnchantment, Integer> ench : out.entrySet()) {
                    if ((ench.getKey().validMaterial(result) || result.getType() == Material.ENCHANTED_BOOK) && appliedEnchants++ < maxEnchants) {
                        CustomEnchantment.setEnchantment(result, ench.getKey(),  ench.getValue(), world);
                    } else {
                        // Remove enchantment, don't go into risks, HashMaps aren't sorted in a particular order
                        CustomEnchantment.setEnchantment(result, ench.getKey(), 0, world);
                    }
                }
                inv.setRepairCost(out.size()*7+inv.getRepairCost());
                evt.setResult(result);
            }
        }
    }
}
