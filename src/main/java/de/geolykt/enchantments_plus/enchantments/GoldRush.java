package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import static org.bukkit.Material.GOLD_NUGGET;
import static org.bukkit.Material.SAND;

import java.util.concurrent.ThreadLocalRandom;

public class GoldRush extends CustomEnchantment {

    public static final int ID = 22;

    @Override
    public Builder<GoldRush> defaults() {
        return new Builder<>(GoldRush::new, ID)
            .all("Randomly drops gold nuggets when mining sand",
                    new Tool[]{Tool.SHOVEL},
                    "Gold Rush",
                    3, // MAX LVL
                    Hand.LEFT);
    }

    private GoldRush() {
        super(BaseEnchantments.GOLD_RUSH);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if (evt.getBlock().getType() == SAND && ThreadLocalRandom.current().nextInt(100) >= (100 - (level * power * 3))) {
            evt.getBlock().getWorld()
               .dropItemNaturally(evt.getBlock().getLocation(), new ItemStack(GOLD_NUGGET));
            return true;
        }
        return false;
    }
}
