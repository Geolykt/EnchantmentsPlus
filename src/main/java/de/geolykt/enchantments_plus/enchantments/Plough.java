package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class Plough extends CustomEnchantment {

    public static final int ID = 43;

    @Override
    public Builder<Plough> defaults() {
        return new Builder<>(Plough::new, ID)
            .all(BaseEnchantments.PLOUGH,
                    "Tills all soil within a radius",
                    new Tool[]{Tool.HOE},
                    "Plough",
                    3,
                    Hand.RIGHT);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() == RIGHT_CLICK_BLOCK) {
            Location loc = evt.getClickedBlock().getLocation();
            int radiusXZ = (int) Math.round(power * level + 2);
            int radiusY = 1;
            for (int x = -(radiusXZ); x <= radiusXZ; x++) {
                for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                    for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                        Block block = loc.getBlock();
                        if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {
                            if (((block.getRelative(x, y, z).getType() == DIRT
                                || block.getRelative(x, y, z).getType() == GRASS_BLOCK
                                || block.getRelative(x, y, z).getType() == MYCELIUM))
                                && Storage.COMPATIBILITY_ADAPTER.airs().contains(block.getRelative(x, y + 1, z).getType())) {
                                ADAPTER.placeBlock(block.getRelative(x, y, z), evt.getPlayer(), Material.FARMLAND,
                                    null);
                                if (Storage.rnd.nextBoolean()) {
                                    CompatibilityAdapter.damageTool(evt.getPlayer(), 1, usedHand);
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
