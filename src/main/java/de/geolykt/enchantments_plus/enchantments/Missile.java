package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.admin.MissileArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

public class Missile extends CustomEnchantment {

    public static final int ID = 71;

    @Override
    public Builder<Missile> defaults() {
        return new Builder<>(Missile::new, ID)
            .maxLevel(1)
            .loreName("Missile")
            .probability(0)
            .enchantable(new Tool[]{Tool.BOW})
            .conflicting()
            .description("Shoots a missile from the bow")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.RIGHT)
            .base(BaseEnchantments.MISSILE);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        MissileArrow arrow = new MissileArrow((Arrow) evt.getProjectile());
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        evt.setCancelled(true);
        Utilities.damageTool((Player) evt.getEntity(), 1, usedHand);
        Utilities.removeItem(((Player) evt.getEntity()), Material.ARROW, 1);
        return true;
    }
}
