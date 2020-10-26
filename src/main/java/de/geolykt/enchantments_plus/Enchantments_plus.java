package de.geolykt.enchantments_plus;
//For Bukkit & Spigot 1.16.X

import org.apache.commons.lang.time.StopWatch;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.enchantments.*;
import de.geolykt.enchantments_plus.evt.GrindstoneMerge;
import de.geolykt.enchantments_plus.evt.NewAnvilMerger;
import de.geolykt.enchantments_plus.evt.Watcher;
import de.geolykt.enchantments_plus.evt.WatcherArrow;
import de.geolykt.enchantments_plus.evt.WatcherEnchant;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import static org.bukkit.Material.*;
import static org.bukkit.potion.PotionEffectType.FAST_DIGGING;

public class Enchantments_plus extends JavaPlugin {

    // Creates a directory for the plugin and then loads configs
    public void loadConfigs() {
        File file = new File("plugins/Enchantments_plus/");
        boolean success = file.mkdir();
        if (success) {
            System.out.println("Created folder for Enchantments+ configuration.");
        }
        Config.loadConfigs();
    }

    // Sets blocks to their natural states at shutdown
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        for (Location l : FrozenStep.frozenLocs.keySet()) {
            l.getBlock().setType(WATER);
        }
        for (Location l : NetherStep.netherstepLocs.keySet()) {
            l.getBlock().setType(LAVA);
        }
        for (Entity e : Anthropomorphism.idleBlocks.keySet()) {
            e.remove();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasMetadata("ze.haste")) {
                player.removePotionEffect(FAST_DIGGING);
                player.removeMetadata("ze.haste", Storage.plugin);
            }
        }
    }

    // Sends commands over to the CommandProcessor for it to handle
    public boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        return CommandProcessor.onCommand(sender, command, commandlabel, args);
    }

    // Returns true if the given item stack has a custom enchantment
    public boolean hasEnchantment(ItemStack stack) {
        boolean has = false;
        for (Config c : Config.CONFIGS.values()) {
            if (!CustomEnchantment.getEnchants(stack, c.getWorld()).isEmpty()) {
                has = true;
            }
        }
        return has;
    }

    // Returns enchantment names mapped to their level from the given item stack
    public Map<String, Integer> getEnchantments(ItemStack stack) {
        Map<String, Integer> enchantments = new TreeMap<>();
        for (Map.Entry<CustomEnchantment, Integer> ench : 
            CustomEnchantment.getEnchants(stack, (World) Config.CONFIGS.keySet().toArray()[0]).entrySet()) {
            enchantments.put(ench.getKey().getLoreName(), ench.getValue());
        }
        return enchantments;
    }

    public Map<String, Integer> getEnchantments(ItemStack stack, World world) {
        Map<String, Integer> enchantments = new TreeMap<>();
        for (Map.Entry<CustomEnchantment, Integer> ench : CustomEnchantment.getEnchants(stack, world).entrySet()) {
            enchantments.put(ench.getKey().getLoreName(), ench.getValue());
        }
        return enchantments;
    }

    // Returns true if the enchantment (given by the string) can be applied to the given item stack
    public boolean isCompatible(String enchantmentName, ItemStack stack) {
        for (Config c : Config.CONFIGS.values()) {
            CustomEnchantment e;
            if ((e = c.enchantFromString(enchantmentName)) != null) {
                if (e.validMaterial(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Adds the enchantments (given by the string) of level 'level' to the given item stack, returning true if the
    //      action was successful
    public boolean addEnchantment(ItemStack stk, String enchantmentName, int lvl) {
        for (Config c : Config.CONFIGS.values()) {
            CustomEnchantment e;
            if ((e = c.enchantFromString(enchantmentName)) != null) {
                e.setEnchantment(stk, lvl, c.getWorld());
                return true;
            }
        }
        return false;
    }

    // Removes the enchantment (given by the string) from the given item stack, returning true if the action was
    //      successful
    public boolean removeEnchantment(ItemStack stk, String enchantmentName) {
        for (Config c : Config.CONFIGS.values()) {
            CustomEnchantment e;
            if ((e = c.enchantFromString(enchantmentName)) != null) {
                e.setEnchantment(stk, 0, c.getWorld());
                return true;
            }
        }
        return false;
    }

    /**
     * The metric object used by the plugin. Internal use only.
     * @since 2.1.6
     */
    private Metrics metric;

    // Loads configs and starts tasks
    public void onEnable() {
        StopWatch w = new StopWatch();
        w.start();
        Storage.plugin = this;
        File compatFile = new File(getDataFolder(), "magicCompat.yml");
        if (!compatFile.exists()) {
            saveResource("magicCompat.yml", false);
        }
        FileConfiguration compatConfig = YamlConfiguration.loadConfiguration(compatFile);
        Storage.COMPATIBILITY_ADAPTER.loadValues(compatConfig);

        Storage.version = this.getDescription().getVersion();
        loadConfigs();
        getCommand("ench").setTabCompleter(new CommandProcessor.TabCompletion());
        getServer().getPluginManager().registerEvents(new NewAnvilMerger(), this);
        getServer().getPluginManager().registerEvents(new GrindstoneMerge(), this);
        getServer().getPluginManager().registerEvents(new WatcherArrow(), this);
        getServer().getPluginManager().registerEvents(WatcherEnchant.instance(), this);
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

        Storage.ANTICHEAT_ADAPTER.onEnable();
        
        Laser.colorKey = new NamespacedKey(this, "laserCol");
        
        // Load runnables
        // High frequency runnable (every tick) -> Gotta run fast
        getServer().getScheduler().runTaskTimer(this, () -> {
            EnchantedArrow.doTick();
            Anthropomorphism.entityPhysics();
            MysteryFish.guardian(); // TODO is this even needed?
            WatcherEnchant.scanPlayers();
            Tracer.tracer();
            Singularity.blackholes();
        }, 1, 1);
        
        // medium-high frequency runnable (every five ticks)
        getServer().getScheduler().runTaskTimer(this, () -> {
            EnchantedArrow.scanAndReap();
            NetherStep.updateBlocks(); // TODO maybe allocate it to a PlayerMoveEvent executor?
        }, 5, 5);

        // medium-high asynchronous frequency runnable (every five ticks)
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> Anthropomorphism.removeCheck(), 5, 5);
        
        // BSTATS metrics init
        metric = new Metrics(this, 9211);
        metric.addCustomChart(new Metrics.SimplePie("distribution_method", () -> Storage.DISTRIBUTION));
        metric.addCustomChart(new Metrics.SimplePie("plugin_brand", () -> Storage.BRAND));
        metric.addCustomChart(new Metrics.SimplePie("enchantment_getter", () -> CustomEnchantment.Enchantment_Adapter.getClass().getSimpleName()));
        
        w.stop();
        getLogger().info(Storage.BRAND + " v" + Storage.version + " started up in " + w.getTime() + "ms");
    }
}
