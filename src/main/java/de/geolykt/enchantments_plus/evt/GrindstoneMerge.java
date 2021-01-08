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
        ItemStack stk;
        if (gi.getItem(2) != null && gi.getItem(2).getType() != Material.AIR) { // Get the output item
            stk = gi.getItem(2);
        } else if (gi.getItem(0) != null && gi.getItem(0).getType() != Material.AIR) {
            stk = gi.getItem(0).clone();
        } else {
            return;
        }
        // TODO can be done more efficiently (maybe a dedicated method for that?)
        Map<CustomEnchantment, Integer> enchants = CustomEnchantment.getEnchants(stk, world, null);
        for (CustomEnchantment ench : enchants.keySet()) {
            CustomEnchantment.setEnchantment(stk, ench, 0, world);
        }
        gi.setItem(2, stk);
    }
}
