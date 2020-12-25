package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Sheep;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.Material.*;
import static org.bukkit.block.BlockFace.DOWN;

import java.util.concurrent.ThreadLocalRandom;

public class Rainbow extends CustomEnchantment {

    public static final int ID = 47;

    @Override
    public Builder<Rainbow> defaults() {
        return new Builder<>(Rainbow::new, ID)
            .all("Drops random flowers and wool colors when used",
                    new Tool[]{Tool.SHEARS},
                    "Rainbow",
                    3,
                    Hand.BOTH);
    }

    private Rainbow() {
        super(BaseEnchantments.RAINBOW);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        Material dropMaterial;
        if (Tag.SMALL_FLOWERS.isTagged(evt.getBlock().getType())) {
            dropMaterial = Tag.SMALL_FLOWERS.getValues().toArray(new Material[0])[ThreadLocalRandom.current().nextInt(Tag.SMALL_FLOWERS.getValues().size())];
        } else if (Tag.TALL_FLOWERS.isTagged(evt.getBlock().getType())) {
            dropMaterial = Tag.TALL_FLOWERS.getValues().toArray(new Material[0])[ThreadLocalRandom.current().nextInt(Tag.TALL_FLOWERS.getValues().size())];
        } else {
            return false;
        }
        evt.setCancelled(true);
        if (Tag.TALL_FLOWERS.isTagged(evt.getBlock().getRelative(DOWN).getType())) {
            evt.getBlock().getRelative(DOWN).setType(AIR);
        }
        evt.getBlock().setType(AIR);
        CompatibilityAdapter.damageTool(evt.getPlayer(), 1, usedHand);
        evt.getPlayer().getWorld().dropItem(Utilities.getCenter(evt.getBlock()), new ItemStack(dropMaterial, 1));
        return true;
    }

    @Override
    public boolean onShear(PlayerShearEntityEvent evt, int level, boolean usedHand) {
        Sheep sheep = (Sheep) evt.getEntity();
        if (!sheep.isSheared()) {
            int count = ThreadLocalRandom.current().nextInt(3) + 1;
            CompatibilityAdapter.damageTool(evt.getPlayer(), 1, usedHand);
            evt.setCancelled(true);
            sheep.setSheared(true);
            evt.getEntity().getWorld().dropItemNaturally(evt.getEntity().getLocation(),
                new ItemStack(Tag.WOOL.getValues().toArray(new Material[0])[ThreadLocalRandom.current().nextInt(Tag.WOOL.getValues().size())], count));
        }
        return true;
    }
}
