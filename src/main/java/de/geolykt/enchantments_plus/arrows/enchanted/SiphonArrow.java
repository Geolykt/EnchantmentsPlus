package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.enchantments.Siphon;

public class SiphonArrow extends EnchantedArrow {

    public SiphonArrow(AbstractArrow entity, int level, double power) {
        super(entity, level, power);
    }

    public boolean onImpact(EntityDamageByEntityEvent evt) {
        if (evt.getEntity() instanceof LivingEntity && Storage.COMPATIBILITY_ADAPTER.attackEntity(
                (LivingEntity) evt.getEntity(),
                (Player) arrow.getShooter(), 0, false)) {
            Player player = (Player) ((Projectile) evt.getDamager()).getShooter();
            double difference = 0;
            if (Siphon.calcAmour) {
                difference = 0.17 * level * power * evt.getFinalDamage();
            } else {
                difference = 0.17 * level * power * evt.getDamage();
            }
            player.setHealth(player.getHealth() + 
                    Math.min(difference, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() - player.getHealth()));
        }
        die();
        return true;
    }
}
