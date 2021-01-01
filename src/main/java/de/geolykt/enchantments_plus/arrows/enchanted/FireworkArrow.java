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

import org.bukkit.*;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;

public class FireworkArrow extends EnchantedArrow {

    public FireworkArrow(AbstractArrow entity, int level) {
        super(entity, level);
    }

    public void onImpact() {
        Location l = arrow.getLocation();
        FireworkEffect.Type[] type = {FireworkEffect.Type.BALL, FireworkEffect.Type.BURST, FireworkEffect.Type.STAR,
            FireworkEffect.Type.BALL_LARGE};
        FireworkEffect.Builder b = FireworkEffect.builder();
        b = b.withColor(Color.LIME).withColor(Color.RED).withColor(Color.BLUE).withColor(Color.YELLOW).withColor(
            Color.fromRGB(0xFF00FF)).withColor(Color.ORANGE).withColor(Color.fromRGB(0x3E89FF));
        b = b.trail(true);
        b = b.with(type[(getLevel() > 4 ? 4 : getLevel()) - 1]);
        final Firework f = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
        FireworkMeta d = f.getFireworkMeta();
        d.setPower(0);
        d.addEffect(b.build());
        f.setFireworkMeta(d);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
            f.detonate();
        }, 1);
        die();
    }
}
