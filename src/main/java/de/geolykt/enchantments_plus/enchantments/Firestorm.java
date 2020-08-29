package de.geolykt.enchantments_plus.enchantments;

import static de.geolykt.enchantments_plus.enums.Tool.BOW;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.FirestormArrow;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

public class Firestorm extends CustomEnchantment {

    public static final int ID = 14;

    @Override
    public Builder<Firestorm> defaults() {
        return new Builder<>(Firestorm::new, ID)
            .maxLevel(3)
            .loreName("Firestorm")
            .probability(0)
            .enchantable(new Tool[]{BOW})
            .conflicting(Blizzard.class)
            .description("Spawns a firestorm where the arrow strikes burning nearby entities")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        FirestormArrow arrow = new FirestormArrow((Arrow) evt.getProjectile(), level, power);
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
