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
package de.geolykt.enchantments_plus;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author wicden
 * 
 * Helper class for efficient execution of fast continuous item effects.
 * This class scans the list of online players for effects to be performed and keeps them
 * in a cache where the executions are repeated without re-scanning player inventory.
 * Inventory scans are also staggered across the configured time period, smoothing out lag spikes.
 */
public class HighFrequencyRunnableCache implements Runnable {

    private final int refreshPeriodTicks;
    private final ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Supplier<Boolean>> cache0 = new ArrayList<>(), cache1 = new ArrayList<>();
    private final BiConsumer<Player, Consumer<Supplier<Boolean>>> cacheFeeder;

    private int feedFraction = 0;

    /**
     * Create a cache for continuous player-based effects
     * @param cacheFeeder a function that determines the set of tasks to be executed for each player and inserts them into the provided Collection
     * @param refreshPeriodTicks the number of ticks over which spread out the player sweep
     */
    public HighFrequencyRunnableCache(BiConsumer<Player, Consumer<Supplier<Boolean>>> cacheFeeder, int refreshPeriodTicks) {
        this.cacheFeeder = cacheFeeder;
        this.refreshPeriodTicks = refreshPeriodTicks;
    }

    @Override
    public void run() {
        cache0.removeIf(booleanSupplier -> !booleanSupplier.get());

        if (feedFraction == 0) {
            players.clear();
            players.addAll(Bukkit.getOnlinePlayers());
            ArrayList<Supplier<Boolean>> cache2 = cache0;
            cache0 = cache1;
            cache1 = cache2;
            cache1.clear();
        }

        int listStart = players.size() * feedFraction / refreshPeriodTicks;
        int listEnd = players.size() * (feedFraction + 1) / refreshPeriodTicks;

        //System.out.println("Tick " + feedFraction + ": Scanning " + (listEnd - listStart) + " players");

        for (int i = listStart; i < listEnd; i++) {
            Player p = players.get(i);
            if (!p.isOnline()) {
                continue;
            }
            cacheFeeder.accept(p, cache1::add);
        }

        feedFraction = (feedFraction + 1) % refreshPeriodTicks;
    }
}
