package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.event.entity.EntityDeathEvent;

import de.geolykt.enchantments_plus.arrows.EnchantedArrow;

public class VortexArrow extends EnchantedArrow {

    public VortexArrow(AbstractArrow entity) {
        super(entity);
    }

    public void onKill(final EntityDeathEvent evt) {
        die();
    }
}
