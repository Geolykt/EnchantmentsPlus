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
