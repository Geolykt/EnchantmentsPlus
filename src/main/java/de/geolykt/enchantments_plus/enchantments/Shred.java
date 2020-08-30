package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.*;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.evt.BlockShredEvent;
import de.geolykt.enchantments_plus.evt.WatcherEnchant;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.HashSet;
import java.util.Set;

import static de.geolykt.enchantments_plus.enums.Tool.PICKAXE;
import static de.geolykt.enchantments_plus.enums.Tool.SHOVEL;
import static org.bukkit.Material.*;

public class Shred extends CustomEnchantment {

    public static final int ID = 52;

    @Override
    public Builder<Shred> defaults() {
        return new Builder<>(Shred::new, ID)
                .maxLevel(5)
                .loreName("Shred")
                .probability(0)
                .enchantable(new Tool[]{SHOVEL, PICKAXE})
                .conflicting(Pierce.class, Switch.class)
                .description("Breaks the blocks within a radius of the original block mined")
                .cooldown(0)
                .power(-1.0)
                .handUse(Hand.LEFT)
                .base(BaseEnchantments.SHRED);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if (!Storage.COMPATIBILITY_ADAPTER.shredPicks().contains(evt.getBlock().getType())
                && !Storage.COMPATIBILITY_ADAPTER.shredShovels().contains(evt.getBlock().getType())) {
            return false;
        }
        ItemStack hand = Utilities.usedStack(evt.getPlayer(), usedHand);
        blocks(evt.getBlock(), evt.getBlock(), new int[]{level + 3, level + 3, level + 3}, 0,
                4.6 + (level * .22), new HashSet<>(), evt.getPlayer(), Config.get(evt.getBlock().getWorld()),
                hand.getType(), usedHand);
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
                    //TODO Why run this twice?
                    WatcherEnchant.instance().onBlockShred(relativeEvent);
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

            Utilities.damageTool(player, 1, usedHand);
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
}
