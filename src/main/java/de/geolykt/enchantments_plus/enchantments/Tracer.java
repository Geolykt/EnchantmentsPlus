/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2022 Geolykt and EnchantmentsPlus contributors
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

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.TracerArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;
import java.util.Map;

// FIXME refractor this shit in 5.0.0 - also implement AreaOfEffectable
public class Tracer extends CustomEnchantment {

    public static final Map<AbstractArrow, LivingEntity> ARROW_TARGETS = new HashMap<>();

    public static final int ID = 63;

    // Map of tracer arrows to their expected accuracy
    public static final Map<AbstractArrow, Integer> tracer = new HashMap<>();

    public Tracer() {
        super(BaseEnchantments.TRACER);
    }

    @Override
    public Builder<Tracer> defaults() {
        return new Builder<>(Tracer::new, ID)
            .all("Guides the arrow to targets and then attacks",
                    new Tool[]{Tool.BOW, Tool.CROSSBOW},
                    "Tracer",
                    4,
                    Hand.RIGHT);
    }

    /**
     * Obtains the entity that is targeted by this tracer arrow.
     *
     * @param source The arrow that is the source of this operation
     * @return The entity that is targeted by this operation
     * @throws IllegalArgumentException if the arrow is not a tracer arrow (or it was removed from the tracer arrow pool)
     * @since 4.0.1
     */
    public static @Nullable LivingEntity getArrowTarget(@NotNull AbstractArrow source) {
        LivingEntity closestEntity = ARROW_TARGETS.get(source);
        if (closestEntity != null && closestEntity.isValid() && !closestEntity.isDead()
                && closestEntity.getWorld() == source.getWorld()) {
            // isValid and !isDead should do the same, but better be safe than sorry
            return closestEntity;
        }
        ProjectileSource shooter = source.getShooter();
        if (!(shooter instanceof Entity)) {
            return null; // we need to know the location of the shooter in order to be able to do the required math
        }
        Entity shooterEntity = (Entity) shooter;
        if (!source.getLocation().getWorld().equals(shooterEntity.getLocation().getWorld())) {
            return null; // the shooter switched worlds in the meantime
        }
        if (source.getLocation().distanceSquared(shooterEntity.getLocation()) < 225) {
            return null; // Arrow has not yet travelled far enough
        }
        closestEntity = null; // in case it was not null, but otherwise invalid

        Integer level = tracer.get(source);
        if (level == null) {
            throw new IllegalArgumentException("Arrow not in tracer arrow pool.");
        }
        level += 2;

        double minDistanceSq = Double.POSITIVE_INFINITY;

        for (Entity target : source.getNearbyEntities(level, level, level)) {
            if (!(target instanceof LivingEntity) || target == shooterEntity) {
                continue;
            }
            double distanceSq = target.getLocation().distanceSquared(source.getLocation());
            if (distanceSq < minDistanceSq) {
                minDistanceSq = distanceSq;
                closestEntity = (LivingEntity) target;
            }
        }
        if (closestEntity != null) {
            ARROW_TARGETS.put(source, closestEntity);
        }
        return closestEntity;
    }

    // Moves Tracer arrows towards a target
    public static void tracer() {
        for (AbstractArrow e : tracer.keySet()) {
            if (!(e.getShooter() instanceof Entity)) {
                continue; // Unlikely, but possible - so better be safe than sorry
            }
            Entity close = getArrowTarget(e);
            if (close != null) {
                Location location = close.getLocation();
                Location pos = e.getLocation();
                double its = location.distance(pos);
                if (its == 0) {
                    its = 1;
                }
                org.bukkit.util.Vector v = new org.bukkit.util.Vector((location.getX() - pos.getX()) / its,
                        (location.getY() - pos.getY()) / its, (location.getZ() - pos.getZ()) / its);
                v.add(e.getLocation().getDirection().multiply(.1));
                e.setVelocity(v.multiply(2));
            }
        }
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        TracerArrow arrow = new TracerArrow((AbstractArrow) evt.getProjectile(), level, power);
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
