package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import static org.bukkit.Material.GOLD_NUGGET;
import static org.bukkit.Material.SAND;

public class GoldRush extends CustomEnchantment {

    public static final int ID = 22;

    @Override
    public Builder<GoldRush> defaults() {
        return new Builder<>(GoldRush::new, ID)
            .maxLevel(3)
            .loreName("Gold Rush")
            .probability(0)
            .enchantable(new Tool[]{Tool.SHOVEL})
            .conflicting()
            .description("Randomly drops gold nuggets when mining sand")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.LEFT)
            .base(BaseEnchantments.GOLD_RUSH);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if (evt.getBlock().getType() == SAND && Storage.rnd.nextInt(100) >= (100 - (level * power * 3))) {
            evt.getBlock().getWorld()
               .dropItemNaturally(evt.getBlock().getLocation(), new ItemStack(GOLD_NUGGET));
            return true;
        }
        return false;
    }
}
