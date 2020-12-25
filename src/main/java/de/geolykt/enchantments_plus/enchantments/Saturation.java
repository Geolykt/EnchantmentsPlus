package de.geolykt.enchantments_plus.enchantments;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Saturation extends CustomEnchantment {

    public static final int ID = 50;

    @Override
    public Builder<Saturation> defaults() {
        return new Builder<>(Saturation::new, ID)
            .all("Uses less of the player's hunger",
                    new Tool[]{Tool.LEGGINGS},
                    "Saturation",
                    3, // MAX LVL
                    Hand.NONE);
    }

    private Saturation() {
        super(BaseEnchantments.SATURATION);
    }

    @Override
    public boolean onHungerChange(FoodLevelChangeEvent evt, int level, boolean usedHand) {
        if (evt.getFoodLevel() < ((Player) evt.getEntity()).getFoodLevel() &&
            ThreadLocalRandom.current().nextInt(10) > 10 - 2 * level * power) {
            evt.setCancelled(true);
        }
        return true;
    }
}
