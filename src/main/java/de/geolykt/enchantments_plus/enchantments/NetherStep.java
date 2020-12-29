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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static de.geolykt.enchantments_plus.util.Utilities.selfRemovingArea;

public class NetherStep extends CustomEnchantment implements AreaOfEffectable {

    /**
     * The location of the blocks spawned by the NetherStep enchantment mapped to when they were spawned in.
     * In Zenchantments and 1.0.0 to 2.x.x the resolution of the time is in nanoseconds, in v3.0.0 onwards
     * it is in millisecond resolution.
     * @since 1.0.0
     */
    public static final Map<Location, Long> netherstepLocs = new HashMap<>();
    public static final int                 ID             = 39;

    @Override
    public Builder<NetherStep> defaults() {
        return new Builder<>(NetherStep::new, ID)
            .all("Allows the player to slowly but safely walk on lava",
                    new Tool[]{Tool.BOOTS},
                    "Nether Step",
                    3, // MAX LVL
                    Hand.NONE,
                    BaseEnchantments.FROZEN_STEP);
    }

    public NetherStep() {
        super(BaseEnchantments.NETHER_STEP);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        if (player.isSneaking() && player.getLocation().getBlock().getType() == Material.LAVA &&
            !player.isFlying()) {
            player.setVelocity(player.getVelocity().setY(.4));
        }

        selfRemovingArea(Material.SOUL_SAND,
                Material.LAVA,
                (int) getAOESize(level),
                player.getLocation().add(0, 0.2, 0).getBlock(),
                player,
                netherstepLocs);

        return true;
    }

    // Removes the blocks from NetherStep and FrozenStep after a period of time
    public static void updateBlocks() {
        Iterator<Location> it = FrozenStep.frozenLocs.keySet().iterator();
        while (it.hasNext()) {
            Location location = it.next();
            if (Math.abs(System.currentTimeMillis() - FrozenStep.frozenLocs.get(location)) > 900) {
                location.getBlock().setType(Material.WATER);
                it.remove();
            }
        }
        it = netherstepLocs.keySet().iterator();
        while (it.hasNext()) {
            Location location = it.next();
            if (Math.abs(System.nanoTime() - netherstepLocs.get(location)) > 900) {
                location.getBlock().setType(Material.LAVA);
                it.remove();
            }
        }
    }

    /**
     * The Area of effect multiplier used by this enchantment.
     * @since 2.1.6
     * @see AreaOfEffectable
     */
    private double aoe = 1.0;
    
    @Override
    public double getAOESize(int level) {
        return 2 + aoe + level;
    }

    @Override
    public double getAOEMultiplier() {
        return aoe;
    }

    /**
     * Sets the multiplier used for the area of effect size calculation, the multiplier should have in most cases a linear impact,
     * however it's not guaranteed that the AOE Size is linear to the multiplier as some other effects may play a role.<br>
     * <br>
     * Impact formula: <b>2 + AOE + level</b>
     * @param newValue The new value of the multiplier
     * @since 2.1.6
     */
    @Override
    public void setAOEMultiplier(double newValue) {
        aoe = newValue;
    }

}
