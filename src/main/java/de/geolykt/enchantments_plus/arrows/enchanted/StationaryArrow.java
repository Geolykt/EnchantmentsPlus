package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;

public class StationaryArrow extends EnchantedArrow {

    public StationaryArrow(AbstractArrow entity) {
        super(entity);
    }

    public boolean onImpact(EntityDamageByEntityEvent evt) {
        if (Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) arrow.getShooter(),
                0, false)) {
            LivingEntity ent = (LivingEntity) evt.getEntity();
            if (evt.getDamage() < ent.getHealth()) {
                evt.setCancelled(true);

                // Imitate Flame arrows after cancelling the original event
                if (arrow.getFireTicks() > 0) {
                    EntityCombustByEntityEvent ecbee = new EntityCombustByEntityEvent(arrow, ent, 5);
                    Bukkit.getPluginManager().callEvent(ecbee);
                    if (!ecbee.isCancelled()) {
                        // For some fucking reason I can't set the entity on fire in the same tick. So I'm delaying it by one tick and now it works
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                            Storage.COMPATIBILITY_ADAPTER.igniteEntity(ent, (Player) arrow.getShooter(), 300);
                        }, 1);
                    }
                }

                ent.damage(evt.getDamage());
                if (evt.getDamager().getType() == EntityType.ARROW) {
                    evt.getDamager().remove();
                }
            }

        }
        die();
        return true;
    }
}
