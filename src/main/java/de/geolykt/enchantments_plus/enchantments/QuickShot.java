package de.geolykt.enchantments_plus.enchantments;

import static de.geolykt.enchantments_plus.enums.Tool.BOW;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.QuickArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

public class QuickShot extends CustomEnchantment {

    public static final int ID = 46;

    @Override
    public Builder<QuickShot> defaults() {
        return new Builder<>(QuickShot::new, ID)
            .maxLevel(1)
            .loreName("Quick Shot")
            .probability(0)
            .enchantable(new Tool[]{BOW})
            .conflicting()
            .description("Shoots arrows at full speed, instantly")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.RIGHT)
            .base(BaseEnchantments.QUICK_SHOT);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        QuickArrow arrow = new QuickArrow((Arrow) evt.getProjectile());
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
