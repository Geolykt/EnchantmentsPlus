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

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.util.Utilities;

public class PotionArrow extends EnchantedArrow {

    public PotionArrow(AbstractArrow entity, int level, double power) {
        super(entity, level, power);
    }

    public boolean onImpact(EntityDamageByEntityEvent evt) {
        if (ThreadLocalRandom.current().nextInt((int) Math.round(10 / (getLevel() * getPower() + 1))) == 1) {
            Utilities.addPotion((LivingEntity) arrow.getShooter(),
                Storage.COMPATIBILITY_ADAPTER.potionPotions().get(ThreadLocalRandom.current().nextInt(12)),
                150 + (int) Math.round(getLevel() * getPower() * 50), (int) Math.round(getLevel() * getPower()));
        }
        die();
        return true;
    }
}
