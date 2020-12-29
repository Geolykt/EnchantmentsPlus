/*
 * 
 *  This file is part of EnchantmentsPlus, a bukkit plugin.
 *  Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 *  Copyright (c) 2020 Geolykt and EnchantmentsPlus contributors
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
