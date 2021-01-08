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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Terraformer extends CustomEnchantment {

    public static int[][] SEARCH_FACES = new int[][]{new int[]{-1, 0, 0}, new int[]{1, 0, 0}, new int[]{0, -1, 0}, new int[]{0, 0, -1}, new int[]{0, 0, 1}};

    private static final int MAX_BLOCKS = 64;

    public static final int ID = 61;

    @Override
    public Builder<Terraformer> defaults() {
        return new Builder<>(Terraformer::new, ID)
            .all("Places the leftmost blocks in the players inventory within a 7 block radius",
                    new Tool[]{Tool.SHOVEL},
                    "Terraformer",
                    1,
                    Hand.RIGHT);
    }

    public Terraformer() {
        super(BaseEnchantments.TERRAFORMER);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getPlayer().isSneaking()) {
            if (evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Block start = evt.getClickedBlock().getRelative(evt.getBlockFace());
                if (start.getLocation().getBlockY() == evt.getPlayer().getEyeLocation().getBlockY()) {
                    return false;
                }
                Material mat = Material.AIR;

                for (int i = 0; i < 9; i++) {
                    ItemStack itemInHand = evt.getPlayer().getInventory().getItem(i);
                    if (itemInHand != null
                            && itemInHand.getType().isBlock()
                            && Storage.COMPATIBILITY_ADAPTER.terraformerMaterials().contains(itemInHand.getType())) {
                        mat = itemInHand.getType();
                        break;
                    }
                }
                if (mat == Material.AIR) {
                    return false;
                }

                for (Block b : Utilities.BFS(start, MAX_BLOCKS, false, 5.f, SEARCH_FACES,
                        Storage.COMPATIBILITY_ADAPTER.airs(), new HashSet<>(), false, true)) {
                    if (b.getType().equals(Material.AIR) 
                            && Utilities.hasItem(evt.getPlayer(), mat, 1)
                            && Storage.COMPATIBILITY_ADAPTER.placeBlock(b, evt.getPlayer(), mat, null)
                            && Utilities.removeItem(evt.getPlayer(), mat, 1)
                            && ThreadLocalRandom.current().nextInt(10) == 5) {
                        CompatibilityAdapter.damageTool(evt.getPlayer(), 1, usedHand);
                    }
                }
                return true;
            }
        }
        return false;
    }

}
