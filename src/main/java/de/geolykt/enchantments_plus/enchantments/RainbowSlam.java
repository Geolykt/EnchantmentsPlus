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
package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class RainbowSlam extends CustomEnchantment {

    // Entities affected by Rainbow Slam, protected against fall damage in order to deal damage as the attacker
    public static final Set<Entity> rainbowSlamNoFallEntities = new HashSet<>();
    public static final int         ID                        = 48;

    @Override
    public Builder<RainbowSlam> defaults() {
        return new Builder<>(RainbowSlam::new, ID)
            .all("Attacks enemy mobs with a powerful swirling slam",
                    new Tool[]{Tool.SWORD},
                    "Rainbow Slam",
                    4,
                    Hand.RIGHT,
                    BaseEnchantments.GUST, BaseEnchantments.FORCE);
    }

    public RainbowSlam() {
        super(BaseEnchantments.RAINBOW_SLAM);
    }

    @Override
    public boolean onEntityInteract(final PlayerInteractEntityEvent evt, final int level, boolean usedHand) {
        if (!(evt.getRightClicked() instanceof LivingEntity) ||
            !ADAPTER.attackEntity((LivingEntity) evt.getRightClicked(), evt.getPlayer(), 0, false)) {
            return false;
        }
        final LivingEntity ent = (LivingEntity) evt.getRightClicked();
        final Location l = ent.getLocation().clone();
        ent.teleport(l);
        for (int i = 0; i < 30; i++) {
            final int fI = i;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                for (int j = 0; j < 40; j++) {
                    if (ent.isDead()) {
                        return;
                    }
                    Location loc = l.clone();
                    float t = 30 * fI + j;
                    loc.setY(loc.getY() + (t / 100));
                    loc.setX(loc.getX() + Math.sin(Math.toRadians(t)) * t / 330);
                    loc.setZ(loc.getZ() + Math.cos(Math.toRadians(t)) * t / 330);

                    ThreadLocalRandom rand = ThreadLocalRandom.current();
                    ent.getWorld().spawnParticle(Particle.REDSTONE, loc, 1,
                        new Particle.DustOptions(Color.fromRGB(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)), 1.0f));
                    loc.setY(loc.getY() + 1.3);
                    ent.setVelocity(loc.toVector().subtract(ent.getLocation().toVector()));
                }
            }, i);
        }
        AtomicBoolean applied = new AtomicBoolean(false);
        rainbowSlamNoFallEntities.add(ent);
        for (int i = 0; i < 3; i++) {
            CompatibilityAdapter.damageTool(evt.getPlayer(), 3, usedHand);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                ent.setVelocity(l.toVector().subtract(ent.getLocation().toVector()).multiply(.3));
                ent.setFallDistance(0);
                if (ent.isOnGround() && !applied.get()) {
                    applied.set(true);
                    rainbowSlamNoFallEntities.remove(ent);
                    ADAPTER.attackEntity(ent, evt.getPlayer(), level * power, false);
                    ent.getWorld().spawnParticle(Particle.BLOCK_DUST, Utilities.getCenter(l), 20, evt.getPlayer().getLocation().getBlock().getBlockData());
                }
            }, 35 + (i * 5));
        }
        return true;
    }
}
