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

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;

import de.geolykt.enchantments_plus.arrows.EnchantedArrow;

import java.util.List;

public class QuickArrow extends EnchantedArrow {

    public QuickArrow(AbstractArrow entity) {
        super(entity);
    }

    public void onLaunch(LivingEntity player, List<String> lore) {
        arrow.setVelocity(arrow.getVelocity().normalize().multiply(3.5f));
    }

    @Override
    public void onImpact() {} // This is done knowingly
}
