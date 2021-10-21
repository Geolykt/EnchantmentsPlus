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
package de.geolykt.enchantments_plus.arrows;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import de.geolykt.enchantments_plus.Storage;

/**
 * Proxy for {@link AbstractArrow} which allows subclasses to define custom behaviour
 * triggered on certain events.
 */
public class EnchantedArrow {
    // Entities an enchanted arrow has damaged or killed

    // TODO v5.0.0: rename the maps to conventional names
    public static final Map<Entity, EnchantedArrow> killedEntities = new HashMap<>();

    // Not actually for removal, but I want to lower the visibility of the map
    // Arrows mapped to different advanced arrow effects, to be used by the Arrow Watcher to perform these effects
    @Deprecated(forRemoval = true, since = "4.0.2")
    public static final Map<AbstractArrow, Set<EnchantedArrow>> advancedProjectiles = new HashMap<>();
    protected final AbstractArrow arrow;
    protected final int level;
    protected final double power;
    private int tick;

    /**
     * Creates a new EnchantedArrow from the given arrow with the specified
     * level and power.
     *
     * @param arrow Arrow entity from which to make the EnchantedArrow.
     * @param level Level of enchantment on arrow.
     * @param power Power level of enchantment.
     */
    public EnchantedArrow(AbstractArrow arrow, int level, double power) {
        this.arrow = arrow;
        this.level = level;
        this.power = power;
    }

    /**
     * Creates a new EnchantedArrow from the given arrow with the specified
     * level.
     *
     * @param arrow Arrow entity from which to make the EnchantedArrow.
     * @param level Level of enchantment on arrow.
     */
    public EnchantedArrow(AbstractArrow arrow, int level) {
        this(arrow, level, 1);
    }

    /**
     * Creates a new EnchantedArrow from the given arrow.
     *
     * @param arrow Arrow entity from which to make the EnchantedArrow.
     */
    public EnchantedArrow(AbstractArrow arrow) {
        this(arrow, 0);
    }

    // Adds an arrow entity into the arrow storage variable calls its launch method
    public static void putArrow(AbstractArrow e, EnchantedArrow a, Player p) {
        Set<EnchantedArrow> ars = null;
        if (advancedProjectiles.containsKey(e)) {
            ars = advancedProjectiles.get(e);
        }
        if (ars == null) {
            ars = new HashSet<>();
            advancedProjectiles.put(e, ars);
        }
        ars.add(a);
        a.onLaunch(p, null);
    }

    protected void die() {
        die(true);
    }
        // Called when the arrow has finished any functionality
    protected void die(boolean removeArrow) {
        onDie();
        if (removeArrow) {
            arrow.remove();
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
            if (advancedProjectiles.containsKey(arrow)) {
                advancedProjectiles.get(arrow).remove(this);
                if (advancedProjectiles.get(arrow).isEmpty()) {
                    advancedProjectiles.remove(arrow);
                }
            }
        }, 1);
    }

    /**
     * Increments internal tick counter
     */
    private void tick() {
        tick++;
        onTick();
    }

    //region Getters
    /**
     * @return Ticks since arrow was created.
     */
    public int getTick() {
        return this.tick;
    }

    /**
     * @return Level of enchantment on this arrow.
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * @return Power of enchantment on this arrow.
     */
    public double getPower() {
        return power;
    }

    //endregion
    //region Events
    // Called when the player shoots an arrow of this type
    /**
     * Called when an arrow of this type is launched.
     *
     * @param player Player launching the arrow.
     * @param lore List of lore attached to this arrow.
     */
    public void onLaunch(LivingEntity player, List<String> lore) {
    }

    /**
     * Called on every 'tick' this arrow experiences.
     */
    protected void onTick() {
    }

    /**
     * Called when this arrow impacts a block.
     */
    public void onImpact() {
        die(true);
    }

    /**
     * Called when this arrow kills another entity.
     *
     * @param evt Event concerning entity killed.
     */
    public void onKill(EntityDeathEvent evt) {
    }

    /**
     * Called when this arrow impacts another entity.
     *
     * @param evt Event concerning entity impacted.
     *
     * @return true iff impact successful and should deal damage.
     */
    public boolean onImpact(EntityDamageByEntityEvent evt) {
        onImpact();
        return true;
    }

    /**
     * Called when this arrow dies.
     */
    protected void onDie() {
    }

    //endregion
    public static void scanAndReap() {
        // advancedProjectiles had an extra thread safety layer ever since the code was added in the original repository,
        // this does not make much sense to me at this time, so if we get any CMEs you know why
        for (AbstractArrow a : advancedProjectiles.keySet()) {
            if (a.isDead()) {
                Set<EnchantedArrow> children = advancedProjectiles.get(a);
                if (children == null) {
                    // why this can null is beyond me, but it can be:
                    // https://github.com/Geolykt/EnchantmentsPlus/issues/99
                    continue;
                }
                children.forEach(EnchantedArrow::die);
            } else {
                for (EnchantedArrow ea : advancedProjectiles.get(a)) {
                    if (ea.getTick() > 600) {
                        ea.die();
                    }
                }
            }
        }
    }

    public static void doTick() {
        advancedProjectiles.values().forEach((set) -> {
            if (set != null) {
                set.forEach(EnchantedArrow::tick);
            }
        });
    }
}
