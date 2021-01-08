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
package de.geolykt.enchantments_plus.evt;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import de.geolykt.enchantments_plus.arrows.EnchantedArrow;

import java.util.Set;

// This is the watcher used by the EnchantArrow class. Each method checks for certain events
// and conditions and will call the relevant methods defined in the AdvancedArrow interface
//      
public class WatcherArrow implements Listener {

    // Called when an arrow hits a block
    @EventHandler
    public boolean impact(ProjectileHitEvent evt) {
        if (EnchantedArrow.advancedProjectiles.containsKey(evt.getEntity())) {
            Set<EnchantedArrow> ar = EnchantedArrow.advancedProjectiles.get(evt.getEntity());
            for (EnchantedArrow a : ar) {
                a.onImpact();
            }
        }
        return true;
    }

    // Called when an arrow hits an entity
    @EventHandler
    public boolean entityHit(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Arrow) {
            if (EnchantedArrow.advancedProjectiles.containsKey(evt.getDamager())) {
                Set<EnchantedArrow> arrows = EnchantedArrow.advancedProjectiles.get(evt.getDamager());
                for (EnchantedArrow arrow : arrows) {
                    if (evt.getEntity() instanceof LivingEntity) {
                        if (!arrow.onImpact(evt)) {
                            evt.setDamage(0);
                        }
                    }
                    EnchantedArrow.advancedProjectiles.remove(evt.getDamager());
                    if (evt.getEntity() instanceof LivingEntity
                            && evt.getDamage() >= ((LivingEntity) evt.getEntity()).getHealth()) {
                        EnchantedArrow.killedEntities.put(evt.getEntity(), arrow);
                    }
                }
            }
        }
        return true;
    }

    // Called when an arrow kills an entity; the advanced arrow is removed after this event
    @EventHandler
    public boolean entityDeath(EntityDeathEvent evt) {
        if (EnchantedArrow.killedEntities.containsKey(evt.getEntity())) {
            EnchantedArrow arrow = EnchantedArrow.killedEntities.get(evt.getEntity());
            arrow.onKill(evt);
            EnchantedArrow.killedEntities.remove(evt.getEntity());
        }
        return true;
    }

}
