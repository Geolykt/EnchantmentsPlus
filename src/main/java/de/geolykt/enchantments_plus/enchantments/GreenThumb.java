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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;

public class GreenThumb extends CustomEnchantment implements AreaOfEffectable {

    public static final int ID = 24;

    @Override
    public Builder<GreenThumb> defaults() {
        return new Builder<>(GreenThumb::new, ID)
            .all("Grows the foliage around the player",
                    new Tool[]{Tool.LEGGINGS},
                    "Green Thumb",
                    3, // MAX LVL
                    Hand.NONE);
    }

    public GreenThumb() {
        super(BaseEnchantments.GREEN_THUMB);
    }

    private static final EquipmentSlot[] SLOTS = 
            new EquipmentSlot[] {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        Location loc = player.getLocation();
        Block centerBlock = loc.getBlock();
        int radius = (int) getAOESize(level);
        for (int x = -(radius); x <= radius; x++) {
            for (int y = -(radius) - 1; y <= radius - 1; y++) {
                for (int z = -(radius); z <= radius; z++) {
                    Block relativeBlock = centerBlock.getRelative(x, y, z);
                    if (relativeBlock.getLocation().distanceSquared(loc) < radius * radius) {
                        if (ThreadLocalRandom.current().nextInt((int) (300 / (power * level / 2))) != 0) {
                            continue;
                        }
                        boolean applied = false;
                        if (relativeBlock.getType() == DIRT) {
                            if (ADAPTER.airs().contains(relativeBlock.getRelative(0, 1, 0).getType())) {
                                Material mat = ADAPTER.getDefaultSoilMaterial(centerBlock.getBiome());
                                switch (centerBlock.getBiome()) {
                                case MUSHROOM_FIELDS:
                                    mat = MYCELIUM;
                                    break;
                                case OLD_GROWTH_PINE_TAIGA:
                                case OLD_GROWTH_SPRUCE_TAIGA:
                                    mat = PODZOL;
                                    break;
                                default:
                                    mat = GRASS_BLOCK;
                                }
                                applied = ADAPTER.placeBlock(relativeBlock, player, mat, null);
                            }
                        } else {
                            applied = ADAPTER.grow(centerBlock.getRelative(x, y, z), player);
                        }
                        if (applied) { // Display particles and damage armor
                            CompatibilityAdapter.display(Utilities.getCenter(centerBlock.getRelative(x, y + 1, z)),
                                Particle.VILLAGER_HAPPY, 20, 1f, .3f, .3f, .3f);
                            if (ThreadLocalRandom.current().nextInt(50) > 42 && level != 10) {
                                for (EquipmentSlot slot : SLOTS) {
                                    final ItemStack s = player.getInventory().getItem(slot);
                                    if (CustomEnchantment.hasEnchantment(Config.get(player.getWorld()), s, BaseEnchantments.GREEN_THUMB) && CompatibilityAdapter.damageItem2(s, level)) {
                                        player.getInventory().setItem(slot, new ItemStack(Material.AIR));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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
