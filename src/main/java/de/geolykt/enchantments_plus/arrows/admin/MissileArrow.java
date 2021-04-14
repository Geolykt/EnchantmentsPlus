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
package de.geolykt.enchantments_plus.arrows.admin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.List;

import static org.bukkit.Material.AIR;

public class MissileArrow extends EnchantedArrow {

    public MissileArrow(AbstractArrow entity) {
        super(entity);
    }

    @Override
    public void onLaunch(LivingEntity player, List<String> lore) {
        final Config config = Config.get(player.getWorld());
        Location playLoc = player.getLocation();
        final Location target = Utilities.getCenter(player.getTargetBlock(null, 220));
        target.setY(target.getY() + .5);
        final Location c = playLoc;
        c.setY(c.getY() + 1.1);
        final double d = target.distance(c);
        for (int i = 9; i <= ((int) (d * 5) + 9); i++) {
            final int i1 = i;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                Location loc = target.clone();
                loc.setX(c.getX() + (i1 * ((target.getX() - c.getX()) / (d * 5))));
                loc.setY(c.getY() + (i1 * ((target.getY() - c.getY()) / (d * 5))));
                loc.setZ(c.getZ() + (i1 * ((target.getZ() - c.getZ()) / (d * 5))));
                Location loc2 = target.clone();
                loc2.setX(c.getX() + ((i1 + 10) * ((target.getX() - c.getX()) / (d * 5))));
                loc2.setY(c.getY() + ((i1 + 10) * ((target.getY() - c.getY()) / (d * 5))));
                loc2.setZ(c.getZ() + ((i1 + 10) * ((target.getZ() - c.getZ()) / (d * 5))));
                CompatibilityAdapter.display(loc, Particle.FLAME, 10, .001f, 0, 0, 0);
                CompatibilityAdapter.display(loc, Particle.FLAME, 1, .1f, 0, 0, 0);
                if (i1 % 50 == 0) {
                    target.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 10f, .1f);
                }
                if (i1 >= ((int) (d * 5) + 9) || loc2.getBlock().getType() != AIR) {
                    CompatibilityAdapter.display(loc2, Particle.EXPLOSION_HUGE, 10, 0.1f, 0, 0, 0);
                    CompatibilityAdapter.display(loc, Particle.FLAME, 175, 1f, 0, 0, 0);
                    loc2.setY(loc2.getY() + 5);
                    loc2.getWorld().createExplosion(loc2.getX(), loc2.getY(), loc2.getZ(), 10,
                        config.explosionBlockBreak(), config.explosionBlockBreak());
                }
            }, i / 7);
        }
    }
}
