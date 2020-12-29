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
package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class Grab extends CustomEnchantment {

    // Locations where Grab has been used on a block and are waiting for the Watcher to handle their teleportation
    public static final Map<Location, Player> grabLocs     = new HashMap<>();
    public static final int                  ID           = 23;

    @Override
    public Builder<Grab> defaults() {
        return new Builder<>(Grab::new, ID)
            .all("Teleports mined items and XP directly to the player",
                    new Tool[]{Tool.PICKAXE, Tool.AXE, Tool.SHOVEL},
                    "Grab",
                    1, // MAX LVL
                    Hand.LEFT);
    }

    public Grab() {
        super(BaseEnchantments.GRAB);
    }

    @Override
    public boolean onBlockBreak(final BlockBreakEvent evt, int level, boolean usedHand) {
        grabLocs.put(evt.getBlock().getLocation(), evt.getPlayer());
        final Location loc = evt.getBlock().getLocation();
        //ADAPTER.breakBlockNMS(evt.getBlock(), evt.getPlayer());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> grabLocs.remove(loc), 3);
        return true;
    }
}
