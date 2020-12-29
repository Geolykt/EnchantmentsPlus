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

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import static org.bukkit.Material.*;

import java.util.concurrent.ThreadLocalRandom;

public class Variety extends CustomEnchantment {

    public static final int ID = 65;

    @Override
    public Builder<Variety> defaults() {
        return new Builder<>(Variety::new, ID)
            .all("Drops random types of wood or leaves",
                    new Tool[]{Tool.AXE},
                    "Variety",
                    1,
                    Hand.LEFT,
                    BaseEnchantments.FIRE);
    }

    public Variety() {
        super(BaseEnchantments.VARIETY);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        Material mat = evt.getBlock().getType();
        if (Tag.LOGS.isTagged(mat)) {
            evt.getBlock().setType(AIR);
            evt.getBlock().getWorld()
               .dropItemNaturally(evt.getBlock().getLocation(),
                   new ItemStack(Tag.LOGS.getValues().toArray(new Material[0])[ThreadLocalRandom.current().nextInt(Tag.LOGS.getValues().size())]));
            CompatibilityAdapter.damageTool(evt.getPlayer(), 1, usedHand);
        } else if (Tag.LEAVES.isTagged(mat)) {
            evt.getBlock().setType(AIR);
            evt.getBlock().getWorld()
               .dropItemNaturally(evt.getBlock().getLocation(),
                   new ItemStack(Tag.LEAVES.getValues().toArray(new Material[0])[ThreadLocalRandom.current().nextInt(Tag.LEAVES.getValues().size())]));
            CompatibilityAdapter.damageTool(evt.getPlayer(), 1, usedHand);
        }
        return true;
    }
}
