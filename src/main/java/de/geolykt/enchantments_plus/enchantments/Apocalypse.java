package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.entity.AbstractArrow;
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
            .probability(0)
            .all("Unleashes hell",
                new Tool[]{Tool.BOW},
                "Apocalypse",
                1,
                Hand.RIGHT);
    }

    public Apocalypse() {
        super(BaseEnchantments.APOCALYPSE);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        ApocalypseArrow arrow = new ApocalypseArrow((AbstractArrow) evt.getProjectile());
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
