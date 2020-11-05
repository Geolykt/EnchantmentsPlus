package de.geolykt.enchantments_plus.evt;

import java.util.ArrayList;
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

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;

/**
 * The second generation of the Anvil merger, the class that handles merging of enchantments on two Items within the Anvil.
 * Reason for the need of a second generation is due to an increase of bug reports and next to no understanding of how the code works.
 * 
 * @since 2.1.5
 */
public class NewAnvilMerger implements Listener {

    /**
     * The remapping function used by {@link #mergeEnchantments(Map, Map)} to merge the levels of the same enchantments.
     *  Default behavior (v2.1.5) results in the following: if Integer A and Integer B are the same and non-0, then A+1 is returned.
     *  Otherwise the bigger of the two is returned.
     * @param ench The enchantment that needs to be remapped, ignored per default
     * @param inputA The primary input level
     * @param inputB The secondary input level
     * @return Returns the new level of the Enchantment.
     * @since 2.1.5
     */
    public static Integer enchantmentLevelRemappingFunction(CustomEnchantment ench, Integer inputA, Integer inputB) {
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
        // TODO revisit this code in the v3.0.0 refractor (great things can be changed when we use Enums instead of CE instances)
        LinkedHashMap<CustomEnchantment, Integer> out = new LinkedHashMap<>();
        out.putAll(inA);

        // pretty sure I'm overcomplicating things again
        out.keySet().forEach((ench) -> {
            inB.keySet().removeIf((newEnch) -> {
                for (Class<? extends CustomEnchantment> conflict : ench.getConflicting()) {
                    if (newEnch.getClass().equals(conflict)) {
                        return true;
                    }
                }
                return false;
            });
        });
        inB.forEach((ench, newLevel) -> out.merge(ench, newLevel, (oench, olevel) -> enchantmentLevelRemappingFunction(ench, olevel, newLevel)));
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
        if (inv.getSize() < 3 || evt.getViewers().size() == 0 || inv.getItem(0) == null)
            return;
        List<String> nleftLore = new ArrayList<String>();
        if (evt.getResult() == null) {
            // Best guess merge
            ItemStack stackA = inv.getItem(0).clone();
            ItemStack stackB = inv.getItem(1);
            if (stackA != null
                    && stackB != null
                    && (stackA.getType() == stackB.getType() || stackB.getType() == Material.ENCHANTED_BOOK) 
                    && CompatibilityAdapter.getDamage(stackA) == 0
                    && CompatibilityAdapter.getDamage(stackB) == 0) {
                // Undamaged conversion
                World world = evt.getViewers().get(0).getWorld();
                Map<CustomEnchantment, Integer> out = mergeEnchantments(
                        CustomEnchantment.getEnchants(stackA, true, world, nleftLore),
                        CustomEnchantment.getEnchants(stackB, true, world));
                if (out.size() > 0) {
                    boolean unrepairable = false;
                    for (Map.Entry<CustomEnchantment, Integer> ench : out.entrySet()) {
                        if (ench.getKey().asEnum() == BaseEnchantments.UNREPAIRABLE && ench.getValue() != 0) {
                            unrepairable = true;
                            break;
                        }
                    }
                    if (unrepairable) {
                        evt.setResult(null);
                    } else {
                        out.forEach((ench, level) -> {
                            CustomEnchantment.setEnchantment(stackA, ench, ench.validMaterial(stackA) ? level : 0, world);
                        });
                        inv.setRepairCost(out.size()*7+inv.getRepairCost());
                        evt.setResult(stackA);
                    }
                }
            }
        } else {
            final ItemStack result = evt.getResult().clone();
            World world = evt.getViewers().get(0).getWorld();
            Map<CustomEnchantment, Integer> out = mergeEnchantments(
                    CustomEnchantment.getEnchants(inv.getItem(0), true, world, nleftLore),
                    CustomEnchantment.getEnchants(inv.getItem(1), true, world));
            if (out.size() > 0) {
                boolean unrepairable = false;
                for (Map.Entry<CustomEnchantment, Integer> ench : out.entrySet()) {
                    if (ench.getKey().asEnum() == BaseEnchantments.UNREPAIRABLE && ench.getValue() != 0) {
                        unrepairable = true;
                    }
                }
                if (unrepairable) {
                    evt.setResult(null);
                } else {
                    out.forEach((ench, level) -> CustomEnchantment.setEnchantment(result, ench, ench.validMaterial(result) ? level : 0, world));
                    inv.setRepairCost(out.size()*7+inv.getRepairCost());
                    evt.setResult(result);
                }
            }
        }
    }
}
