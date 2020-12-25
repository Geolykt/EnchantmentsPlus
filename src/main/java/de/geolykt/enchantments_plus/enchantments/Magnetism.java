package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

public class Magnetism extends CustomEnchantment implements AreaOfEffectable {

    public static final int ID = 35;

    @Override
    public Builder<Magnetism> defaults() {
        return new Builder<>(Magnetism::new, ID)
            .all("Slowly attracts nearby items to the players inventory",
                    new Tool[]{Tool.LEGGINGS},
                    "Magnetism",
                    3,
                    Hand.NONE);
    }

    private Magnetism() {
        super(BaseEnchantments.MAGNETISM);
    }

    @Override
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        int radius = (int) getAOESize(level);
        for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if (e.getType().equals(EntityType.DROPPED_ITEM) && e.getTicksLived() > 160) {
                e.setVelocity(player.getLocation().toVector().subtract(e.getLocation().toVector()).multiply(.05));
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
        return 3 + aoe + level * 2;
    }

    @Override
    public double getAOEMultiplier() {
        return aoe;
    }

    /**
     * Sets the multiplier used for the area of effect size calculation, the multiplier should have in most cases a linear impact,
     * however it's not guaranteed that the AOE Size is linear to the multiplier as some other effects may play a role.<br>
     * <br>
     * Impact formula: <b>3 + AOE + (2 * level)</b>
     * @param newValue The new value of the multiplier
     * @since 2.1.6
     */
    @Override
    public void setAOEMultiplier(double newValue) {
        aoe = newValue;
    }

}
