/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.geolykt.enchantments_plus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.annotations.AsyncSafe;


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
    @AsyncSafe
    public void run() {
        Iterator<Supplier<Boolean>> it = cache0.iterator();
        while (it.hasNext()) {
            if (!it.next().get()) {
                it.remove();
            }
        }

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
            cacheFeeder.accept(p, (tick) -> {
                cache1.add(tick);
            });
        }

        feedFraction = (feedFraction + 1) % refreshPeriodTicks;
    }
}
