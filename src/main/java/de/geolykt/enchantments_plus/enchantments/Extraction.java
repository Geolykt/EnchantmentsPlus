/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2022 Geolykt and EnchantmentsPlus contributors
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
package de.geolykt.enchantments_plus.enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.RecipeUtil;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

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

    // TODO make use of BlockDropItemEvent instead
    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, final int level, boolean usedHand) {
        if (ADAPTER.ores().contains(evt.getBlock().getType())) {
            CompatibilityAdapter.damageTool(evt.getPlayer(), 1, usedHand);

            List<ItemStack> newDrops = new ArrayList<>();
            boolean same = true;
            for (ItemStack is: evt.getBlock().getDrops(Utilities.usedStack(evt.getPlayer(), usedHand))) {
                if (is.getType().isAir() || is.getAmount() <= 0) {
                    continue;
                }
                ItemStack ns = RecipeUtil.getSmeltedVariantCached(is);
                if (ns.getType() != is.getType()) {
                    same = false;
                }
                int oldAmount = ns.getAmount();
                if (ns.getMaxStackSize() == -1) {
                    newDrops.add(ns);
                    continue;
                }
                if (ns.getMaxStackSize() < 1) {
                    continue; // Would lead to an OOM -> to be discarded
                }
                int amount = ns.getAmount();
                while (amount >= ns.getMaxStackSize()) {
                    ns.setAmount(ns.getMaxStackSize());
                    newDrops.add(ns);
                    amount -= ns.getMaxStackSize();
                }
                ns.setAmount(oldAmount % ns.getMaxStackSize());
                newDrops.add(ns);
            }
            if (!same && newDrops.size() != 0) {
                CompatibilityAdapter.display(Utilities.getCenter(evt.getBlock()), Particle.FLAME, 10, .1f, .5f, .5f, .5f);
                Location location = evt.getBlock().getLocation();
                World world = location.getWorld();
                for (ItemStack is : newDrops) {
                    if (is != null && !is.getType().isAir() && is.getAmount() > 0) {
                        world.dropItemNaturally(location, is);
                    }
                }
                evt.getBlock().setType(Material.AIR);
                ExperienceOrb o = (ExperienceOrb) world.spawnEntity(location, EntityType.EXPERIENCE_ORB);
                // TODO scale with recipe XP
                o.setExperience(ThreadLocalRandom.current().nextInt(5) + 1 + evt.getExpToDrop());
                return true;
            }
        }
        return false;
    }
}
