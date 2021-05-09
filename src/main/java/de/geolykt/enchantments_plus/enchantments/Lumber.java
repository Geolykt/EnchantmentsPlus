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

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.List;

public class Lumber extends CustomEnchantment {

    private static final int MAX_BLOCKS = 200;

    public static int[][] SEARCH_FACES = new int[][]{new int[]{}};

    public static final int ID = 34;

    @Override
    public Builder<Lumber> defaults() {
        return new Builder<>(Lumber::new, ID)
            .all("Breaks the entire tree at once",
                    new Tool[]{Tool.AXE},
                    "Lumber",
                    1,
                    Hand.LEFT);
    }

    public Lumber() {
        super(BaseEnchantments.LUMBER);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if (!evt.getPlayer().isSneaking()) {
            return false;
        }
        Block startBlock = evt.getBlock();

        if (!ADAPTER.lumberTrunk().contains(startBlock.getType())) {
            return false;
        }

        List<Block> blocks = Utilities.BFS(startBlock, MAX_BLOCKS, true, Float.MAX_VALUE, SEARCH_FACES, 
                ADAPTER.lumberTrunk(),  ADAPTER.lumberAllow(),
                true, false);
        for (Block b : blocks) {
            ADAPTER.breakBlockNMS(b, evt.getPlayer());
        }
        return !blocks.isEmpty();
    }
}
