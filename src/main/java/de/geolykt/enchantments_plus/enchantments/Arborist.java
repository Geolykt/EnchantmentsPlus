package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Arborist extends CustomEnchantment {

    public static final int ID = 2;
    public static boolean doGoldenAppleDrop = true;

    @Override
    public Builder<Arborist> defaults() {
        return new Builder<>(Arborist::new, ID)
                .all("Drops more apples, sticks, and saplings when used on leaves", // DESCRIPTION
                    new Tool[]{Tool.AXE}, // APPLICABLE TOOLS
                    "Arborist", // NAME
                    3, // MAX LEVEL
                    Hand.LEFT); // APPLICABLE HANDS
    }

    public Arborist() {
        super(BaseEnchantments.ARBORIST);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        Block blk = evt.getBlock();
        if (Tag.LEAVES.isTagged(blk.getType())) {
            ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
            for (int i = 0; i < level + 3; i++) {
                drops.addAll(blk.getDrops());
            }
            if (doGoldenAppleDrop && 
                    (ThreadLocalRandom.current().nextInt(10000) <= (15 * (level+1) * power))) {
                drops.add(new ItemStack(Material.GOLDEN_APPLE, 1));
            }
            boolean bol = false;
            for (ItemStack drop : drops) {
                if (drop.getType() != Material.AIR) {
                    bol = true;
                    blk.getWorld().dropItemNaturally(blk.getLocation(), drop);
                }
            }
            return bol;
        }
        return false;
    }
}

