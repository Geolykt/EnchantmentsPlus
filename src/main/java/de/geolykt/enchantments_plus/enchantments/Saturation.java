package de.geolykt.enchantments_plus.enchantments;

import static de.geolykt.enchantments_plus.enums.Tool.LEGGINGS;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

public class Saturation extends CustomEnchantment {

    public static final int ID = 50;

    @Override
    public Builder<Saturation> defaults() {
        return new Builder<>(Saturation::new, ID)
            .maxLevel(3)
            .loreName("Saturation")
            .probability(0)
            .enchantable(new Tool[]{LEGGINGS})
            .conflicting()
            .description("Uses less of the player's hunger")
            .cooldown(0)
            .power(1.0)

            .handUse(Hand.NONE);
    }

    @Override
    public boolean onHungerChange(FoodLevelChangeEvent evt, int level, boolean usedHand) {
        if (evt.getFoodLevel() < ((Player) evt.getEntity()).getFoodLevel() &&
            Storage.rnd.nextInt(10) > 10 - 2 * level * power) {
            evt.setCancelled(true);
        }
        return true;
    }
}
