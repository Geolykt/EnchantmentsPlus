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

import org.bukkit.Particle;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;

import java.util.concurrent.ThreadLocalRandom;

public class Extraction extends CustomEnchantment {

    public static final int ID = 12;

    @Override
    public Builder<Extraction> defaults() {
        return new Builder<>(Extraction::new, ID)
            .all("Smelts and yields more product from ores",
                new Tool[]{Tool.PICKAXE},
                "Extraction",
                3, // MAX LVL
                Hand.LEFT,
                BaseEnchantments.SWITCH);
    }

    public Extraction() {
        super(BaseEnchantments.EXTRACTION);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, final int level, boolean usedHand) {
        if (evt.getBlock().getType() == GOLD_ORE || evt.getBlock().getType() == IRON_ORE) {
            CompatibilityAdapter.damageTool(evt.getPlayer(), 1, usedHand);
            for (int x = 0; x < ThreadLocalRandom.current().nextInt((int) Math.round(power * level + 1)) + 1; x++) {
                evt.getBlock().getWorld().dropItemNaturally(evt.getBlock().getLocation(),
                    new ItemStack(evt.getBlock().getType() == GOLD_ORE ?
                        GOLD_INGOT : IRON_INGOT));
            }
            ExperienceOrb o = (ExperienceOrb) evt.getBlock().getWorld()
                                                 .spawnEntity(evt.getBlock().getLocation(), EXPERIENCE_ORB);
            o.setExperience(
                evt.getBlock().getType() == IRON_ORE ? ThreadLocalRandom.current().nextInt(5) + 1 : ThreadLocalRandom.current().nextInt(5) + 3);
            evt.getBlock().setType(AIR);
            CompatibilityAdapter.display(evt.getBlock().getLocation(), Particle.FLAME, 10, .1f, .5f, .5f, .5f);
            return true;
        }
        return false;
    }
}
