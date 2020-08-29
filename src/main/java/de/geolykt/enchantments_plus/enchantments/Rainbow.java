package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Sheep;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.SHEAR;
import static org.bukkit.Material.*;
import static org.bukkit.block.BlockFace.DOWN;

public class Rainbow extends CustomEnchantment {

    public static final int ID = 47;

    @Override
    public Builder<Rainbow> defaults() {
        return new Builder<>(Rainbow::new, ID)
            .maxLevel(1)
            .loreName("Rainbow")
            .probability(0)
            .enchantable(new Tool[]{SHEAR})
            .conflicting()
            .description("Drops random flowers and wool colors when used")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.BOTH);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        Material dropMaterial;
        if (Tag.SMALL_FLOWERS.isTagged(evt.getBlock().getType())) {
            dropMaterial = Tag.SMALL_FLOWERS.getValues().toArray(new Material[0])[Storage.rnd.nextInt(Tag.SMALL_FLOWERS.getValues().size())];
        } else if (Tag.TALL_FLOWERS.isTagged(evt.getBlock().getType())) {
            dropMaterial = Tag.TALL_FLOWERS.getValues().toArray(new Material[0])[Storage.rnd.nextInt(Tag.TALL_FLOWERS.getValues().size())];
        } else {
            return false;
        }
        evt.setCancelled(true);
        if (Tag.TALL_FLOWERS.isTagged(evt.getBlock().getRelative(DOWN).getType())) {
            evt.getBlock().getRelative(DOWN).setType(AIR);
        }
        evt.getBlock().setType(AIR);
        Utilities.damageTool(evt.getPlayer(), 1, usedHand);
        evt.getPlayer().getWorld().dropItem(Utilities.getCenter(evt.getBlock()), new ItemStack(dropMaterial, 1));
        return true;
    }

    @Override
    public boolean onShear(PlayerShearEntityEvent evt, int level, boolean usedHand) {
        Sheep sheep = (Sheep) evt.getEntity();
        if (!sheep.isSheared()) {
            int count = Storage.rnd.nextInt(3) + 1;
            Utilities.damageTool(evt.getPlayer(), 1, usedHand);
            evt.setCancelled(true);
            sheep.setSheared(true);
            evt.getEntity().getWorld().dropItemNaturally(evt.getEntity().getLocation(),
                new ItemStack(Tag.WOOL.getValues().toArray(new Material[0])[Storage.rnd.nextInt(Tag.WOOL.getValues().size())], count));
        }
        return true;
    }
}
