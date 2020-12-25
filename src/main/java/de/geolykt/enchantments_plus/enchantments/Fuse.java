package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.FuseArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Fuse extends CustomEnchantment {

    public static final int ID = 18;

    @Override
    public Builder<Fuse> defaults() {
        return new Builder<>(Fuse::new, ID)
            .all("Instantly ignites anything explosive",
                    new Tool[]{Tool.BOW},
                    "Fuse",
                    1, // MAX LVL
                    Hand.RIGHT);
    }

    public Fuse() {
        super(BaseEnchantments.FUSE);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        FuseArrow arrow = new FuseArrow((AbstractArrow) evt.getProjectile());
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
