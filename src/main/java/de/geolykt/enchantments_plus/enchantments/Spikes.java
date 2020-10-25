package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

public class Spikes extends CustomEnchantment implements AreaOfEffectable {

    public static final int ID = 56;

    @Override
    public Builder<Spikes> defaults() {
        return new Builder<>(Spikes::new, ID)
            .all(BaseEnchantments.SPIKES,
                    "Damages entities the player jumps onto",
                    new Tool[]{Tool.BOOTS},
                    "Spikes",
                    3,
                    Hand.NONE);
    }

    @Override
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        if (player.getVelocity().getY() < -0.45) {
            int radius = (int) getAOESize(level);
            for (Entity e : player.getNearbyEntities(radius, 2, radius)) {
                double fall = Math.min(player.getFallDistance(), 20.0);
                if (e instanceof LivingEntity) {
                    ADAPTER.attackEntity((LivingEntity) e, player, power * level * fall * 0.25, false);
                }
            }
        }
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
        return aoe;
    }

    @Override
    public double getAOEMultiplier() {
        return aoe;
    }

    /**
     * Sets the multiplier used for the area of effect size calculation, the multiplier should have in most cases a linear impact,
     * however it's not guaranteed that the AOE Size is linear to the multiplier as some other effects may play a role.<br>
     * <br>
     * Impact formula: <b>2 + AOE + level*2</b>
     * @param newValue The new value of the multiplier
     * @since 2.1.6
     */
    @Override
    public void setAOEMultiplier(double newValue) {
        aoe = newValue;
    }

}
