package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;

public class SiphonArrow extends EnchantedArrow {

    public SiphonArrow(Arrow entity, int level, double power) {
        super(entity, level, power);
    }

    public boolean onImpact(EntityDamageByEntityEvent evt) {
        if (evt.getEntity() instanceof LivingEntity && Storage.COMPATIBILITY_ADAPTER.attackEntity(
                (LivingEntity) evt.getEntity(),
                (Player) arrow.getShooter(), 0)) {
            Player player = (Player) ((Projectile) evt.getDamager()).getShooter();
            int difference = (int) Math.round(.17 * getLevel() * getPower() * evt.getDamage());
            while (difference > 0) {
                if (player.getHealth() <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                    player.setHealth(player.getHealth() + 1);
                }
                difference--;
            }
        }
        die();
        return true;
    }
}
