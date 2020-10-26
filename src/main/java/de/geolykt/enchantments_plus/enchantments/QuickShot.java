package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.QuickArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class QuickShot extends CustomEnchantment {

    public static final int ID = 46;

    @Override
    public Builder<QuickShot> defaults() {
        return new Builder<>(QuickShot::new, ID)
            .all(BaseEnchantments.QUICK_SHOT,
                    "Shoots arrows at full speed, instantly",
                    new Tool[]{Tool.BOW},
                    "Quick Shot",
                    1, // MAX LVL
                    Hand.RIGHT);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        QuickArrow arrow = new QuickArrow((AbstractArrow) evt.getProjectile());
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
