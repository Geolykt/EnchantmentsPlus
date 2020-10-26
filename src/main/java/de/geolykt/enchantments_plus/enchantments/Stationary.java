package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.StationaryArrow;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Stationary extends CustomEnchantment {

    public static final int ID = 58;

    @Override
    public Builder<Stationary> defaults() {
        return new Builder<>(Stationary::new, ID)
                .all(BaseEnchantments.STATIONARY,
                        "Negates any knockback when attacking mobs, leaving them clueless as to who is attacking",
                        new Tool[]{Tool.BOW, Tool.SWORD},
                        "Stationary",
                        1,
                        Hand.BOTH);
    }

    @Override
    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if (!(evt.getEntity() instanceof LivingEntity)
                || ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0)) {
            LivingEntity ent = (LivingEntity) evt.getEntity();
            if (evt.getDamage() < ent.getHealth()) {
                evt.setCancelled(true);
                CompatibilityAdapter.damageTool(((Player) evt.getDamager()), 1, usedHand);
                ent.damage(evt.getDamage());
            }
        }
        return true;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        StationaryArrow arrow = new StationaryArrow((AbstractArrow) evt.getProjectile());
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
