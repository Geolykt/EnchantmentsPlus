package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;
import java.util.Map;

import static de.geolykt.enchantments_plus.util.Utilities.selfRemovingArea;
import static org.bukkit.Material.*;

public class FrozenStep extends CustomEnchantment implements AreaOfEffectable {

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
        int radius = (int) getAOESize(level);


        selfRemovingArea(PACKED_ICE, WATER, radius, block, player, frozenLocs);
        return true;
    }

    /**
     * The Area of effect multiplier used by this enchantment.
     * @since 2.1.6
     * @see AreaOfEffectable
     */
    private double aoe = 1.0;
    
    @Override
    public double getAOESize(int level) {
        return 2 + aoe + level;
    }

    @Override
    public double getAOEMultiplier() {
        return aoe;
    }

    /**
     * Sets the multiplier used for the area of effect size calculation, the multiplier should have in most cases a linear impact,
     * however it's not guaranteed that the AOE Size is linear to the multiplier as some other effects may play a role.<br>
     * <br>
     * Impact formula: <b>2 + AOE + level</b>
     * @param newValue The new value of the multiplier
     * @since 2.1.6
     */
    @Override
    public void setAOEMultiplier(double newValue) {
        aoe = newValue;
    }

}
