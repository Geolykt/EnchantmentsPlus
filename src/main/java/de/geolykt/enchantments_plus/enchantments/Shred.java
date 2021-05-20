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
package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import de.geolykt.enchantments_plus.*;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.evt.WatcherEnchant;
import de.geolykt.enchantments_plus.evt.ench.BlockShredEvent;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.Material.*;

public class Shred extends CustomEnchantment implements AreaOfEffectable {

    public static final int ID = 52;

    @Override
    public Builder<Shred> defaults() {
        return new Builder<>(Shred::new, ID)
                .all("Breaks the blocks within a radius of the original block mined",
                        new Tool[]{Tool.PICKAXE, Tool.SHOVEL},
                        "Shred",
                        5,
                        Hand.LEFT,
                        BaseEnchantments.PIERCE, BaseEnchantments.SWITCH);
    }

    public Shred() {
        super(BaseEnchantments.SHRED);
    }

    // FIXME these methods may require a recode as they are not maintainable

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if (!Storage.COMPATIBILITY_ADAPTER.shredPicks().contains(evt.getBlock().getType())
                && !Storage.COMPATIBILITY_ADAPTER.shredShovels().contains(evt.getBlock().getType())) {
            return false;
        }
        blocks(evt.getBlock(), evt.getBlock(), new int[]{level + 3, level + 3, level + 3}, 0,
                getAOESize(level), new HashSet<>(), evt.getPlayer(), Config.get(evt.getBlock().getWorld()),
                Utilities.usedStack(evt.getPlayer(), usedHand).getType(), usedHand);
        return true;
    }

    public void blocks(Block centerBlock, final Block relativeBlock, int[] coords, int time, double size,
            Set<Block> used,
            final Player player, final Config config, final Material itemType, boolean usedHand) {

        if (!Storage.COMPATIBILITY_ADAPTER.airs().contains(relativeBlock.getType()) && !used.contains(relativeBlock)) {
            final Material originalType = relativeBlock.getType();
            if ((Tool.PICKAXE.contains(itemType) && !Storage.COMPATIBILITY_ADAPTER.shredPicks().contains(relativeBlock.getType()))
                    || (Tool.SHOVEL.contains(itemType) && !Storage.COMPATIBILITY_ADAPTER.shredShovels().contains(relativeBlock.getType()))) {
                return;
            }
            if (config.getShredDrops() == 0) {
                ADAPTER.breakBlockNMS(relativeBlock, player);
            } else {
                BlockShredEvent relativeEvent = new BlockShredEvent(relativeBlock, player);
                Bukkit.getServer().getPluginManager().callEvent(relativeEvent);
                if (relativeEvent.isCancelled()) {
                    return;
                }
                if (config.getShredDrops() == 1) {
                    if (relativeBlock.getType().equals(NETHER_QUARTZ_ORE)) {
                        relativeBlock.setType(NETHERRACK);
                    } else if (Storage.COMPATIBILITY_ADAPTER.ores().contains(relativeBlock.getType())) {
                        relativeBlock.setType(STONE);
                    }
                    WatcherEnchant.getInstance().onBlockShred(relativeEvent); // Run this again so enchantment can modify the loot
                    if (relativeEvent.isCancelled()) {
                        return;
                    }
                    relativeBlock.breakNaturally();
                } else {
                    relativeBlock.setType(AIR);
                }
            }
            Sound sound = null;
            switch (originalType) {
                case GRASS_BLOCK:
                    sound = Sound.BLOCK_GRASS_BREAK;
                    break;
                case DIRT:
                case GRAVEL:
                case CLAY:
                    sound = Sound.BLOCK_GRAVEL_BREAK;
                    break;
                case SAND:
                    sound = Sound.BLOCK_SAND_BREAK;
                    break;
                case AIR:
                    break;
                default:
                    sound = Sound.BLOCK_STONE_BREAK;
                    break;
            }
            if (sound != null) {
                relativeBlock.getLocation().getWorld().playSound(relativeBlock.getLocation(), sound, 1, 1);
            }

            CompatibilityAdapter.damageTool(player, 1, usedHand);
            used.add(relativeBlock);
            for (int i = 0; i < 3; i++) {

                if (coords[i] > 0) {

                    coords[i] -= 1;
                    Block blk1 = relativeBlock.getRelative(i == 0 ? -1 : 0, i == 1 ? -1 : 0, i == 2 ? -1 : 0);
                    Block blk2 = relativeBlock.getRelative(i == 0 ? 1 : 0, i == 1 ? 1 : 0, i == 2 ? 1 : 0);

                    if (blk1.getLocation().distanceSquared(centerBlock.getLocation()) < size + (-1
                            + 2 * Math.random())) {
                        blocks(centerBlock, blk1, coords, time + 2, size, used, player, config, itemType, usedHand);
                    }
                    if (blk2.getLocation().distanceSquared(centerBlock.getLocation()) < size + (-1
                            + 2 * Math.random())) {
                        blocks(centerBlock, blk2, coords, time + 2, size, used, player, config, itemType, usedHand);
                    }
                    coords[i] += 1;
                }
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
        return 4.4 + (level * .22) + (aoe * .22);
    }

    @Override
    public double getAOEMultiplier() {
        return aoe;
    }

    /**
     * Sets the multiplier used for the area of effect size calculation, the multiplier should have in most cases a linear impact,
     * however it's not guaranteed that the AOE Size is linear to the multiplier as some other effects may play a role.<br>
     * <br>
     * Impact formula: <b>4.4 + (level * .22) + (AOE * .22)</b>
     * @param newValue The new value of the multiplier
     * @since 2.1.6
     */
    @Override
    public void setAOEMultiplier(double newValue) {
        aoe = newValue;
    }
}
