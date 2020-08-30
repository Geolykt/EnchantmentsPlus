package de.geolykt.enchantments_plus.enchantments;

import static de.geolykt.enchantments_plus.enums.Tool.BOW;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.BlizzardArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

public class Blizzard extends CustomEnchantment {

    public static final int ID = 6;

    @Override
    public Builder<Blizzard> defaults() {
        return new Builder<>(Blizzard::new, ID)
            .maxLevel(3)
            .loreName("Blizzard")
            .probability(0)
            .enchantable(new Tool[]{BOW})
            .conflicting(Firestorm.class)
            .description("Spawns a blizzard where the arrow strikes freezing nearby entities")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.RIGHT)
            .base(BaseEnchantments.BLIZZARD);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        BlizzardArrow arrow = new BlizzardArrow((Arrow) evt.getProjectile(), level, power);
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
