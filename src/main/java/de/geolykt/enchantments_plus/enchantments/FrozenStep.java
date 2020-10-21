package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;
import java.util.Map;

import static de.geolykt.enchantments_plus.util.Utilities.selfRemovingArea;
import static org.bukkit.Material.*;

public class FrozenStep extends CustomEnchantment {

    // Blocks spawned from the Water Walker enchantment
    public static final Map<Location, Long> frozenLocs = new HashMap<>();
    public static final int                 ID         = 17;

    @Override
    public Builder<FrozenStep> defaults() {
        return new Builder<>(FrozenStep::new, ID)
            .all(BaseEnchantments.FROZEN_STEP,
                    "Allows the player to walk on water and safely emerge from it when sneaking",
                    new Tool[]{Tool.BOOTS},
                    "Frozen Step",
                    3, // MAX LVL
                    Hand.NONE,
                    NetherStep.class);
    }

    public boolean onScan(Player player, int level, boolean usedHand) {
        if (player.isSneaking() && player.getLocation().getBlock().getType() == WATER &&
            !player.isFlying()) {


            player.setVelocity(player.getVelocity().setY(.4));
        }
        Block block = player.getLocation().getBlock();
        int radius = (int) Math.round(power * level + 2);


        selfRemovingArea(PACKED_ICE, WATER, radius, block, player, frozenLocs);
        return true;
    }
}
