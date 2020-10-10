package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
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
            .maxLevel(1)
            .loreName("Lumber")
            .probability(0)
            .enchantable(new Tool[]{Tool.AXE})
            .conflicting()
            .description("Breaks the entire tree at once")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.LEFT)
            .base(BaseEnchantments.LUMBER);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if (!evt.getPlayer().isSneaking()) {
            return false;
        }
        Block startBlock = evt.getBlock();

        if (!Storage.COMPATIBILITY_ADAPTER.lumberTrunk().contains(startBlock.getType())) {
            return false;
        }

        List<Block> blocks = Utilities.BFS(startBlock, MAX_BLOCKS, true, Float.MAX_VALUE, SEARCH_FACES, 
                Storage.COMPATIBILITY_ADAPTER.lumberTrunk(),  Storage.COMPATIBILITY_ADAPTER.lumberAllow(),
                true, false);
        for (Block b : blocks) {
            ADAPTER.breakBlockNMS(b, evt.getPlayer());
        }
        return !blocks.isEmpty();
    }
}
