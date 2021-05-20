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

//For Bukkit & Spigot 1.16.X
import org.apache.commons.lang.time.StopWatch;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.compatibility.nativeperm.WGHook;
import de.geolykt.enchantments_plus.enchantments.*;
import de.geolykt.enchantments_plus.evt.AnvilMerge;
import de.geolykt.enchantments_plus.evt.GrindstoneMerge;
import de.geolykt.enchantments_plus.evt.NewAnvilMerger;
import de.geolykt.enchantments_plus.evt.Watcher;
import de.geolykt.enchantments_plus.evt.WatcherArrow;
import de.geolykt.enchantments_plus.evt.WatcherEnchant;

import java.io.File;

import static org.bukkit.potion.PotionEffectType.FAST_DIGGING;

public class Enchantments_plus extends JavaPlugin {

    /**
     * Internal value that should not be used outside of the plugin.
     * It's main use is to initialise tertiary classes without the need of larger hacks.
     * Even though this is a pretty large hack in itself.
     *
     * @since 2.2.1
     */
    protected static Enchantments_plus internal_instance;

    public Enchantments_plus() {
        internal_instance = this;
    }

    // Creates a directory for the plugin and then loads configs
    public void loadConfigs() {
        File file = new File("plugins/Enchantments_plus/");
        boolean success = file.mkdir();
        if (success) {
            System.out.println("Created folder for Enchantments+ configuration.");
        }
        File compatFile = new File(getDataFolder(), "magicCompat.yml");
        if (!compatFile.exists()) {
            saveResource("magicCompat.yml", false);
        }
        FileConfiguration compatConfig = YamlConfiguration.loadConfiguration(compatFile);
        Storage.COMPATIBILITY_ADAPTER.loadValues(compatConfig);
        Config.loadConfigs();
        Config.registerWorldConfigurations(this);
    }

    // Sets blocks to their natural states at shutdown
    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        FrozenStep.frozenLocs.keySet().forEach((location) -> location.getBlock().setType(Material.WATER));
        NetherStep.netherstepLocs.keySet().forEach((location) -> location.getBlock().setType(Material.LAVA));
        Anthropomorphism.idleBlocks.keySet().forEach(Entity::remove);
        Reveal.GLOWING_BLOCKS.forEach((loc, ent) -> ent.remove());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasMetadata("ze.haste")) {
                player.removePotionEffect(FAST_DIGGING);
                player.removeMetadata("ze.haste", Storage.plugin);
            }
        }
    }

    // Sends commands over to the CommandProcessor for it to handle
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        return CommandProcessor.onCommand(sender, command, commandlabel, args);
    }

    /**
     * The metric object used by the plugin. Internal use only.
     * @since 2.1.6
     */
    private Metrics metric;

    // Loads configs and starts tasks
    @Override
    public void onEnable() {
        Storage.plugin = this;
        StopWatch w = new StopWatch();
        w.start();

        Storage.version = this.getDescription().getVersion();
        loadConfigs();
        if (Config.PATCH_CONFIGURATION.getBoolean("1xx-anvil-merger", false)) {
            getServer().getPluginManager().registerEvents(new AnvilMerge(), this);
        } else if (!Config.PATCH_CONFIGURATION.getBoolean("disable-anvil-merging", false)) {
            getServer().getPluginManager().registerEvents(new NewAnvilMerger(), this);
        }
        getCommand("ench").setTabCompleter(new CommandProcessor.TabCompletion());
        getServer().getPluginManager().registerEvents(new GrindstoneMerge(), this);
        getServer().getPluginManager().registerEvents(new WatcherArrow(), this);
        getServer().getPluginManager().registerEvents(WatcherEnchant.getInstance(), this);
        getServer().getPluginManager().registerEvents(new Watcher(), this);

        int[][] ALL_SEARCH_FACES = new int[27][3];
        int i = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    ALL_SEARCH_FACES[i++] = new int[]{x, y, z};
                }
            }
        }
        Lumber.SEARCH_FACES = ALL_SEARCH_FACES;
        Spectral.SEARCH_FACES = ALL_SEARCH_FACES;
        Pierce.SEARCH_FACES = ALL_SEARCH_FACES;

        Laser.colorKey = new NamespacedKey(this, "laserCol");

        // Load runnables
        // High frequency runnable (every tick) -> Gotta run fast
        getServer().getScheduler().runTaskTimer(this, () -> {
            EnchantedArrow.doTick();
            Anthropomorphism.entityPhysics();
            MysteryFish.guardian(); // TODO is this even needed?
            WatcherEnchant.runCache();
            Tracer.tracer();
            Singularity.blackholes();
        }, 1, 1);

        // medium-high frequency runnable (every five ticks)
        getServer().getScheduler().runTaskTimer(this, () -> {
            EnchantedArrow.scanAndReap();
            NetherStep.updateBlocks(); // TODO maybe allocate it to a PlayerMoveEvent executor?
        }, 5, 5);

        // medium-high asynchronous frequency runnable (every five ticks)
        getServer().getScheduler().runTaskTimerAsynchronously(this, Anthropomorphism::removeCheck, 5, 5);

        // BSTATS metrics init
        metric = new Metrics(this, 9211);

        metric.addCustomChart(new SimplePie("distribution_method", () -> Storage.DISTRIBUTION));
        metric.addCustomChart(new SimplePie("plugin_brand", () -> Storage.BRAND));
        metric.addCustomChart(new SimplePie("enchantment_getter", () -> CustomEnchantment.Enchantment_Adapter.getClass().getSimpleName()));

        w.stop();
        getLogger().info(Storage.BRAND + " v" + Storage.version + " started up in " + w.getTime() + "ms");
    }
   
    @Override
    public void onLoad() {
        // Let's play the classloader!
        try {
            WGHook.dummy();
        } catch (Throwable ignored) { }
    }
}
