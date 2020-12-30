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
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;
import java.util.Map;

import static de.geolykt.enchantments_plus.util.Utilities.selfRemovingArea;
import static org.bukkit.Material.*;

public class FrozenStep extends CustomEnchantment implements AreaOfEffectable {

    // Blocks spawned from the Water Walker enchantment
    public static final Map<Location, Long> frozenLocs = new HashMap<>();
    public static final int                 ID         = 17;

    @Override
    public Builder<FrozenStep> defaults() {
        return new Builder<>(FrozenStep::new, ID)
            .all("Allows the player to walk on water and safely emerge from it when sneaking",
                    new Tool[]{Tool.BOOTS},
                    "Frozen Step",
                    3, // MAX LVL
                    Hand.NONE,
                    BaseEnchantments.NETHER_STEP);
    }

    public FrozenStep() {
        super(BaseEnchantments.FROZEN_STEP);
    }

    public boolean onScan(Player player, int level, boolean usedHand) {
        if (player.isSneaking() && player.getLocation().getBlock().getType() == WATER &&
            !player.isFlying()) {
            player.setVelocity(player.getVelocity().setY(.4));
        }
        selfRemovingArea(PACKED_ICE, WATER, (int) getAOESize(level), player.getLocation().getBlock(), player, frozenLocs);
        return true;
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
