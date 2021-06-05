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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.PierceMode;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

public class Pierce extends CustomEnchantment {

    private static final int MAX_BLOCKS = 128;

    public static int[][] SEARCH_FACES = new int[0][0];

    /**
     * The ID of this enchantment
     *
     * @since 1.0
     */
    public static final int ID = 42;

    @Override
    public Builder<Pierce> defaults() {
        return new Builder<>(Pierce::new, ID).all(
                "Lets the player mine in several modes which can be changed through shift clicking",
                new Tool[] { Tool.PICKAXE }, "Pierce", 1, Hand.BOTH, BaseEnchantments.ANTHROPOMORPHISM,
                BaseEnchantments.SWITCH, BaseEnchantments.SHRED, BaseEnchantments.REVEAL);
    }

    public Pierce() {
        super(BaseEnchantments.PIERCE);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        Player player = evt.getPlayer();
        if (!evt.getPlayer().hasMetadata("ze.pierce.mode")) {
            player.setMetadata("ze.pierce.mode", new FixedMetadataValue(Storage.plugin, 1));
        }
        if (player.isSneaking() && (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            int b = player.getMetadata("ze.pierce.mode").get(0).asInt();
            b = (b + 1) % ADAPTER.getActivePierceModes().length;
            player.setMetadata("ze.pierce.mode", new FixedMetadataValue(Storage.plugin, b));
            switch (ADAPTER.getActivePierceModes()[b]) {
            case NORMAL -> player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "1x Normal Mode");
            case WIDE -> player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "3x Wide Mode");
            case LONG -> player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "3x Long Mode");
            case TALL -> player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "3x Tall Mode");
            case VEIN -> player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Ore Mode");
            }
        }
        return false;
    }

    @Override
    public boolean onBlockBreak(final BlockBreakEvent evt, int level, boolean usedHand) {
        Player player = evt.getPlayer();
        if (!evt.getPlayer().hasMetadata("ze.pierce.mode")) {
            player.setMetadata("ze.pierce.mode", new FixedMetadataValue(Storage.plugin, 1));
        }
        final int mode = player.getMetadata("ze.pierce.mode").get(0).asInt();
        Set<Block> total = new HashSet<>();
        final Location blkLoc = evt.getBlock().getLocation();

        PierceMode modeEnum = ADAPTER.getActivePierceModes()[mode];
        switch (modeEnum) {
        case NORMAL:
            return false;
        case TALL:
        case LONG:
        case WIDE:
            int add = -1;
            boolean b = false;
            int[][] ints = { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };
            switch (Utilities.getCardinalDirection(evt.getPlayer().getLocation().getYaw(), 0)) {
            case SOUTH:
                ints = new int[][] { { 1, 0, 0 }, { 0, 0, 1 }, { 0, 1, 0 } };
                add = 1;
                b = true;
                break;
            case WEST:
                ints = new int[][] { { 0, 0, 1 }, { 1, 0, 0 }, { 0, 1, 0 } };
                break;
            case NORTH:
                ints = new int[][] { { 1, 0, 0 }, { 0, 0, 1 }, { 0, 1, 0 } };
                b = true;
                break;
            case EAST:
                ints = new int[][] { { 0, 0, 1 }, { 1, 0, 0 }, { 0, 1, 0 } };
                add = 1;
                break;
            default:
                break;
            }

            int[] rads = ints[switch (modeEnum) {
            case LONG -> 1;
            case TALL -> 2;
            case WIDE -> 0;
            default -> throw new IllegalArgumentException("Unexpected value: " + modeEnum);
            }];

            switch (modeEnum) {
            case LONG:
                if (b) {
                    blkLoc.setZ(blkLoc.getZ() + add);
                } else {
                    blkLoc.setX(blkLoc.getX() + add);
                }
                break;
            case TALL:
                if (evt.getPlayer().getLocation().getPitch() > 65) {
                    blkLoc.setY(blkLoc.getY() - 1);
                } else if (evt.getPlayer().getLocation().getPitch() < -65) {
                    blkLoc.setY(blkLoc.getY() + 1);
                }
                break;
            default:
                // Nothing
            }
            for (int x = -(rads[0]); x <= rads[0]; x++) {
                for (int y = -(rads[1]); y <= rads[1]; y++) {
                    for (int z = -(rads[2]); z <= rads[2]; z++) {
                        total.add(blkLoc.getBlock().getRelative(x, y, z));
                    }
                }
            }
            break;
        case VEIN:
            if (ADAPTER.ores().contains(evt.getBlock().getType())) {
                total.addAll(Utilities.BFS(evt.getBlock(), MAX_BLOCKS, false, Float.MAX_VALUE, SEARCH_FACES,
                        EnumSet.of(evt.getBlock().getType()), EnumSet.noneOf(Material.class), false, true));
            } else {
                return false;
            }
        }
        for (Block b : total) {
            if (ADAPTER.isBlockSafeToBreak(b)) {
                ADAPTER.breakBlockNMS(b, evt.getPlayer());
            }
        }
        return true;
    }

}
