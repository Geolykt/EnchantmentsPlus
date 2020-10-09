/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.geolykt.enchantments_plus.evt;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import static org.bukkit.event.EventPriority.MONITOR;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;

/**
 *
 * @author Dennis
 */
public class GrindstoneMerge implements Listener {

    @EventHandler(priority = MONITOR)
    public void onClicks(final InventoryClickEvent evt) {
        if (evt.getInventory().getType() == InventoryType.GRINDSTONE) {
            GrindstoneInventory gi = (GrindstoneInventory) evt.getInventory();
            World world = (World) evt.getViewers().get(0).getWorld();
            if (evt.getSlot() == 2) {
                removeOutputEnchants(gi, world);
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                    removeOutputEnchants(gi, world);
                }, 0);
            }
        }
    }

    private void removeOutputEnchants(GrindstoneInventory gi, World world) {
        if (gi.getItem(2) != null && gi.getItem(2).getType() != Material.AIR) { // Get the output item
            ItemStack stk = gi.getItem(2);
            Map<CustomEnchantment, Integer> enchants = CustomEnchantment.getEnchants(stk, world);
            for (CustomEnchantment ench : enchants.keySet()) {
                CustomEnchantment.setEnchantment(stk, ench, 0, world);
            }
            gi.setItem(2, stk);
        }
    }
}
