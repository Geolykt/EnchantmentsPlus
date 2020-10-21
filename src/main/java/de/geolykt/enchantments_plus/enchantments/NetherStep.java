package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static de.geolykt.enchantments_plus.util.Utilities.selfRemovingArea;
import static org.bukkit.Material.*;

public class NetherStep extends CustomEnchantment {

    // Blocks spawned from the NatherStep enchantment
    public static final Map<Location, Long> netherstepLocs = new HashMap<>();
    public static final int                 ID             = 39;

    @Override
    public Builder<NetherStep> defaults() {
        return new Builder<>(NetherStep::new, ID)
            .all(BaseEnchantments.NETHER_STEP,
                    0,
                    "Allows the player to slowly but safely walk on lava",
                    new Tool[]{Tool.BOOTS},
                    "Nether Step",
                    3, // MAX LVL
                    1.0,
                    Hand.NONE,
                    FrozenStep.class);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        if (player.isSneaking() && player.getLocation().getBlock().getType() == LAVA &&
            !player.isFlying()) {
            player.setVelocity(player.getVelocity().setY(.4));
        }
        Block block = player.getLocation().add(0, 0.2, 0).getBlock();
        int radius = (int) Math.round(power * level + 2);

        selfRemovingArea(SOUL_SAND, LAVA, radius, block, player, netherstepLocs);

        return true;
    }

    // Removes the blocks from NetherStep and FrozenStep after a period of time
    public static void updateBlocks() {
        Iterator<Location> it = FrozenStep.frozenLocs.keySet().iterator();
        while (it.hasNext()) {
            Location location = (Location) it.next();
            if (Math.abs(System.nanoTime() - FrozenStep.frozenLocs.get(location)) > 9E8) { // FIXME why in nanoseconds?
                location.getBlock().setType(WATER);
                it.remove();
            }
        }
        it = netherstepLocs.keySet().iterator();
        while (it.hasNext()) {
            Location location = (Location) it.next();
            if (Math.abs(System.nanoTime() - netherstepLocs.get(location)) > 9E8) {
                location.getBlock().setType(LAVA);
                it.remove();
            }
        }
    }
}
