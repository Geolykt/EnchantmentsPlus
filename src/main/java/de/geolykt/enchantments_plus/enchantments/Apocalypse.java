package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.admin.ApocalypseArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Apocalypse extends CustomEnchantment {

    public static final int ID = 69;

    @Override
    public Builder<Apocalypse> defaults() {
        return new Builder<>(Apocalypse::new, ID)
            .maxLevel(1)
            .loreName("Apocalypse")
            .probability(0)
            .enchantable(new Tool[]{Tool.BOW})
            .conflicting()
            .description("Unleashes hell")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.RIGHT)
            .base(BaseEnchantments.APOCALYPSE);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        ApocalypseArrow arrow = new ApocalypseArrow((Arrow) evt.getProjectile());
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
