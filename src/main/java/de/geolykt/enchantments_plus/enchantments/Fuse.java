package de.geolykt.enchantments_plus.enchantments;

import static de.geolykt.enchantments_plus.enums.Tool.BOW;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.FuseArrow;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

public class Fuse extends CustomEnchantment {

    public static final int ID = 18;

    @Override
    public Builder<Fuse> defaults() {
        return new Builder<>(Fuse::new, ID)
            .maxLevel(1)
            .loreName("Fuse")
            .probability(0)
            .enchantable(new Tool[]{BOW})
            .conflicting()
            .description("Instantly ignites anything explosive")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        FuseArrow arrow = new FuseArrow((Arrow) evt.getProjectile());
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
