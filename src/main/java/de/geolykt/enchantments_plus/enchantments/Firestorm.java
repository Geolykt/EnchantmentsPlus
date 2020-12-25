package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.FirestormArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

public class Firestorm extends CustomEnchantment implements AreaOfEffectable {

    public static final int ID = 14;

    @Override
    public Builder<Firestorm> defaults() {
        return new Builder<>(Firestorm::new, ID)
            .all("Spawns a firestorm where the arrow strikes burning nearby entities",
                    new Tool[]{Tool.BOW},
                    "Firestorm",
                    3, // MAX LVL
                    Hand.RIGHT,
                    BaseEnchantments.BLIZZARD);
    }

    public Firestorm() {
        super(BaseEnchantments.FIRE);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        FirestormArrow arrow = new FirestormArrow((AbstractArrow) evt.getProjectile(), level, power, getAOESize(level));
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
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
        return aoe + level;
    }

    @Override
    public double getAOEMultiplier() {
        return aoe;
    }

    /**
     * Sets the multiplier used for the area of effect size calculation, the multiplier should have in most cases a linear impact,
     * however it's not guaranteed that the AOE Size is linear to the multiplier as some other effects may play a role.<br>
     * <br>
     * Impact formula: <b>AOE + level</b>
     * @param newValue The new value of the multiplier
     * @since 2.1.6
     */
    @Override
    public void setAOEMultiplier(double newValue) {
        aoe = newValue;
    }

}
