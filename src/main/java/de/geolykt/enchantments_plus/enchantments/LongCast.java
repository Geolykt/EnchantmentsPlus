package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class LongCast extends CustomEnchantment {

    public static final int ID = 33;

    @Override
    public Builder<LongCast> defaults() {
        return new Builder<>(LongCast::new, ID)
            .all(BaseEnchantments.LONG_CAST,
                    "Launches fishing hooks farther out when casting",
                    new Tool[]{Tool.ROD},
                    "Long Cast",
                    2, // MAX LVL
                    Hand.RIGHT,
                    ShortCast.class);
    }

    @Override
    public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level, boolean usedHand) {
        if (evt.getEntity().getType() == EntityType.FISHING_HOOK) {
            evt.getEntity().setVelocity(
                evt.getEntity().getVelocity().normalize().multiply(Math.min(1.9 + (power * level - 1.2), 2.7)));
        }
        return true;
    }
}
