/*
 *  This file is part of EnchantmentsPlus, a bukkit plugin.
 *  Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 *  Copyright (c) 2020 - 2021 Geolykt and EnchantmentsPlus contributors
 *   
 *  This program is free software: you can redistribute it and/or modify  
 *  it under the terms of the GNU General Public License as published by  
 *  the Free Software Foundation, version 3.
 *  
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License 
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
