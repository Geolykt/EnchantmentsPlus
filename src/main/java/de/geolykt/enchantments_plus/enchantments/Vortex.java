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
package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.VortexArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;
import java.util.Map;

public class Vortex extends CustomEnchantment {

    // Locations where Vortex has been used on a block and are waiting for the Watcher to handle their teleportation
    public static final Map<Location, Player> vortexLocs = new HashMap<>();
    public static final int ID = 66;

    @Override
    public Builder<Vortex> defaults() {
        return new Builder<>(Vortex::new, ID)
                .all("Teleports mob loot and XP directly to the player",
                        new Tool[]{Tool.BOW, Tool.SWORD, Tool.AXE},
                        "Vortex",
                        1,
                        Hand.BOTH,
                        BaseEnchantments.GRAB);
    }

    public Vortex() {
        super(BaseEnchantments.VORTEX);
    }

    @Override
    public boolean onEntityKill(final EntityDeathEvent evt, int level, boolean usedHand) {
        final Location deathBlock = evt.getEntity().getLocation();
        vortexLocs.put(deathBlock, evt.getEntity().getKiller());
        
        evt.getEntity().getKiller().giveExp(evt.getDroppedExp());
        evt.setDroppedExp(0);
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
            vortexLocs.remove(deathBlock);
        }, 3);
        return true;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        VortexArrow arrow = new VortexArrow((AbstractArrow) evt.getProjectile());
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
