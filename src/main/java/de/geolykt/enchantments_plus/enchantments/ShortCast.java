package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class ShortCast extends CustomEnchantment {

    public static final int ID = 51;

    @Override
    public Builder<ShortCast> defaults() {
        return new Builder<>(ShortCast::new, ID)
            .all(BaseEnchantments.SHORT_CAST,
                    0,
                    "Launches fishing hooks closer in when casting",
                    new Tool[]{Tool.ROD},
                    "Short Cast",
                    2, // MAX LVL
                    1.0,
                    Hand.RIGHT,
                    LongCast.class);
    }

    @Override
    public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level, boolean usedHand) {
        if (evt.getEntity().getType() == EntityType.FISHING_HOOK) {
            evt.getEntity()
               .setVelocity(evt.getEntity().getVelocity().normalize().multiply((.8f / (level * power))));
        }
        return true;
    }
}
