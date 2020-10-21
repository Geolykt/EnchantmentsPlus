package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.google.common.collect.Sets;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.*;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class Pierce extends CustomEnchantment {

    private static final int MAX_BLOCKS = 128;

    public static int[][] SEARCH_FACES = new int[][]{new int[]{}};

    // Map of players who use pierce and what mode they are currently using
    public static final Map<Player, Integer> pierceModes = new HashMap<>();
    public static final int                  ID          = 42;

    @Override
    public Builder<Pierce> defaults() {
        return new Builder<>(Pierce::new, ID)
            .all(BaseEnchantments.PIERCE,
                    "Lets the player mine in several modes which can be changed through shift clicking",
                    new Tool[]{Tool.PICKAXE},
                    "Pierce",
                    1,
                    Hand.BOTH,
                    Anthropomorphism.class, Switch.class, Shred.class, Reveal.class);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        Player player = evt.getPlayer();
        if (!evt.getPlayer().hasMetadata("ze.pierce.mode")) {
            player.setMetadata("ze.pierce.mode", new FixedMetadataValue(Storage.plugin, 1));
        }
        if (player.isSneaking() && (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK)) {
            int b = player.getMetadata("ze.pierce.mode").get(0).asInt();
            b = b == 5 ? 1 : b + 1;
            player.setMetadata("ze.pierce.mode", new FixedMetadataValue(Storage.plugin, b));
            switch (b) {
                case 1:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "1x Normal Mode");
                    break;
                case 2:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "3x Wide Mode");
                    break;
                case 3:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "3x Long Mode");
                    break;
                case 4:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "3x Tall Mode");
                    break;
                case 5:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Ore Mode");
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean onBlockBreak(final BlockBreakEvent evt, int level, boolean usedHand) {
        //1 = normal; 2 = wide; 3 = deep; 4 = tall; 5 = ore
        Player player = evt.getPlayer();
        if (!evt.getPlayer().hasMetadata("ze.pierce.mode")) {
            player.setMetadata("ze.pierce.mode", new FixedMetadataValue(Storage.plugin, 1));
        }
        final int mode = player.getMetadata("ze.pierce.mode").get(0).asInt();
        Set<Block> total = new HashSet<>();
        final Location blkLoc = evt.getBlock().getLocation();
        if (mode != 1 && mode != 5) {
            int add = -1;
            boolean b = false;
            int[][] ints = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
            switch (Utilities.getCardinalDirection(evt.getPlayer().getLocation().getYaw(), 0)) {
                case SOUTH:
                    ints = new int[][]{{1, 0, 0}, {0, 0, 1}, {0, 1, 0}};
                    add = 1;
                    b = true;
                    break;
                case WEST:
                    ints = new int[][]{{0, 0, 1}, {1, 0, 0}, {0, 1, 0}};
                    break;
                case NORTH:
                    ints = new int[][]{{1, 0, 0}, {0, 0, 1}, {0, 1, 0}};
                    b = true;
                    break;
                case EAST:
                    ints = new int[][]{{0, 0, 1}, {1, 0, 0}, {0, 1, 0}};
                    add = 1;
                    break;
                default:
                    break;
            }
            int[] rads = ints[mode - 2];
            if (mode == 3) {
                if (b) {
                    blkLoc.setZ(blkLoc.getZ() + add);
                } else {
                    blkLoc.setX(blkLoc.getX() + add);
                }
            }
            if (mode == 4) {
                if (evt.getPlayer().getLocation().getPitch() > 65) {
                    blkLoc.setY(blkLoc.getY() - 1);
                } else if (evt.getPlayer().getLocation().getPitch() < -65) {
                    blkLoc.setY(blkLoc.getY() + 1);
                }
            }
            for (int x = -(rads[0]); x <= rads[0]; x++) {
                for (int y = -(rads[1]); y <= rads[1]; y++) {
                    for (int z = -(rads[2]); z <= rads[2]; z++) {
                        total.add(blkLoc.getBlock().getRelative(x, y, z));
                    }
                }
            }
        } else if (mode == 5) {
            if (Storage.COMPATIBILITY_ADAPTER.ores().contains(evt.getBlock().getType())) {
                total.addAll(Utilities.BFS(evt.getBlock(), MAX_BLOCKS, false, Float.MAX_VALUE, SEARCH_FACES,
                    Sets.immutableEnumSet(evt.getBlock().getType()),
                    new HashSet<Material>(), false, true));

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
