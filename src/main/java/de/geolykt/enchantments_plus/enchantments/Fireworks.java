package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.FireworkArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Fireworks extends CustomEnchantment {

    public static final int ID = 15;

    @Override
    public Builder<Fireworks> defaults() {
        return new Builder<>(Fireworks::new, ID)
            .all("Shoots arrows that burst into fireworks upon impact",
                    new Tool[]{Tool.BOW},
                    "Fireworks",
                    4, // MAX LVL
                    Hand.RIGHT);
    }

    private Fireworks() {
        super(BaseEnchantments.FIREWORKS);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        FireworkArrow arrow = new FireworkArrow((AbstractArrow) evt.getProjectile(), level);
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
