package de.geolykt.enchantments_plus.enchantments;

import static de.geolykt.enchantments_plus.enums.Tool.BOW;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.FireworkArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

public class Fireworks extends CustomEnchantment {

    public static final int ID = 15;

    @Override
    public Builder<Fireworks> defaults() {
        return new Builder<>(Fireworks::new, ID)
            .maxLevel(4)
            .loreName("Fireworks")
            .probability(0)
            .enchantable(new Tool[]{BOW})
            .conflicting()
            .description("Shoots arrows that burst into fireworks upon impact")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.RIGHT)
            .base(BaseEnchantments.FIREWORKS);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        FireworkArrow arrow = new FireworkArrow((Arrow) evt.getProjectile(), level);
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
