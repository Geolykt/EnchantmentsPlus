/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2021 Geolykt and EnchantmentsPlus contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.Particle;
import org.bukkit.entity.*;

import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.potion.PotionEffectType.SLOW;

public class BlizzardArrow extends EnchantedArrow {

    /**
     * The Area of effect used by this arrow.
     * @since 2.1.6
     * @see AreaOfEffectable
     */
    private double aoe;
    
    public BlizzardArrow(AbstractArrow entity, int level, double power, double aoeSize) {
        super(entity, level, power);
        aoe = aoeSize;
    }

    @SuppressWarnings("unlikely-arg-type")
    public void onImpact() {
        CompatibilityAdapter.display(Utilities.getCenter(arrow.getLocation()), Particle.CLOUD,
                100 * getLevel(), .1f, getLevel(), 1.5f, getLevel());
        for (Entity e : arrow.getNearbyEntities(aoe, aoe, aoe)) {
            if (e instanceof LivingEntity && !e.equals(arrow.getShooter())
                    && Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) e, (Player) arrow.getShooter(), 0, false)) {
                Utilities.addPotion((LivingEntity) e, SLOW, 50 + (int) (getLevel() * getPower() * 50),
                        (int) Math.round(getLevel() * getPower() * 2));
            }
        }
        die();
    }
}
