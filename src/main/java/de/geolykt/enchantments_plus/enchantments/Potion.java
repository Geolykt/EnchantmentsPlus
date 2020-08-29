package de.geolykt.enchantments_plus.enchantments;

import static de.geolykt.enchantments_plus.enums.Tool.BOW;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.PotionArrow;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

public class Potion extends CustomEnchantment {

    public static final int ID = 44;
    PotionEffectType[] potions;

    @Override
    public Builder<Potion> defaults() {
        return new Builder<>(Potion::new, ID)
            .maxLevel(3)
            .loreName("Potion")
            .probability(0)
            .enchantable(new Tool[]{BOW})
            .conflicting()
            .description("Gives the shooter random positive potion effects when attacking")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        PotionArrow arrow = new PotionArrow((Arrow) evt.getProjectile(), level, power);
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
