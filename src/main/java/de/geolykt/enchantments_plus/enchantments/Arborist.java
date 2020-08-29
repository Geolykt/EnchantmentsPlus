package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

import static de.geolykt.enchantments_plus.enums.Tool.AXE;
import static org.bukkit.Material.*;

import java.util.ArrayList;

public class Arborist extends CustomEnchantment {

    public static final int ID = 2;
    public static boolean doGoldenAppleDrop = true;

    @Override
    public Builder<Arborist> defaults() {
        return new Builder<>(Arborist::new, ID)
            .maxLevel(3)
            .loreName("Arborist")
            .probability(0)
            .enchantable(new Tool[]{AXE})
            .conflicting()
            .description("Drops more apples, sticks, and saplings when used on leaves")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.LEFT);
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
                    (Storage.rnd.nextInt(10000) <= (15 * (level+1) * power))) {
                drops.add(new ItemStack(GOLDEN_APPLE, 1));
            }
            boolean bol = false;
            for (ItemStack drop : drops) {
                bol = true;
                blk.getWorld().dropItemNaturally(blk.getLocation(), drop);
            }
            return bol;
        }
        return false;
    }
}

