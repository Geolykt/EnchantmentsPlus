package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;

public class MultiArrow extends EnchantedArrow {

    public MultiArrow(AbstractArrow entity) {
        super(entity);
    }

    public boolean onImpact(EntityDamageByEntityEvent evt) {
        final LivingEntity e = (LivingEntity) evt.getEntity();
        int temp = e.getMaximumNoDamageTicks();
        e.setMaximumNoDamageTicks(0);
        e.setNoDamageTicks(0);
        e.setMaximumNoDamageTicks(temp);
        die();
        return true;
    }

    public void onImpact() {
        Arrow p = arrow.getWorld().spawnArrow(arrow.getLocation(), arrow.getVelocity(),
            (float) (arrow.getVelocity().length() / 10), 0);
        p.setFireTicks(arrow.getFireTicks());
        p.getLocation().setDirection(arrow.getLocation().getDirection());
        p.setMetadata("ze.arrow", new FixedMetadataValue(Storage.plugin, null));
        this.arrow.remove();
    }
}
