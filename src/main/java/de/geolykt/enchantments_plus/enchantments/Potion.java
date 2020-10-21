package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.PotionArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Potion extends CustomEnchantment {

    public static final int ID = 44;
    PotionEffectType[] potions;

    @Override
    public Builder<Potion> defaults() {
        return new Builder<>(Potion::new, ID)
            .all(BaseEnchantments.POTION,
                    0,
                    "Gives the shooter random positive potion effects when attacking",
                    new Tool[]{Tool.BOW},
                    "Potion",
                    3, // MAX LVL
                    1.0,
                    Hand.RIGHT);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        PotionArrow arrow = new PotionArrow((Arrow) evt.getProjectile(), level, power);
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
