package de.geolykt.enchantments_plus;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;

import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.compatibility.anticheat.AbstractAnticheatAdapter;
import de.geolykt.enchantments_plus.compatibility.anticheat.None;

public class Storage {

    /**
     * Instance of the Enchantments_plus plugin to be used by the rest of the classes
     * @since 2.0.0
     */
    public static Enchantments_plus plugin;

    /**
     * Represents the Brand of the plugin, please change it in case you fork the plugin to mark that you have forked it. <br>
     * It's currently only used by the `/ench version` command.
     * @since 1.0.0
     */
    public static final String BRAND = "FastAsyncZenchantments";

    /**
     * Represents the way the plugin was obtained, the reason behind is purely for analytical purposes. <br>
     * It's currently only used by the `/ench version` command.
     * @since 1.0.0
     */
    public static final String DISTRIBUTION = "Git";

    /**
     * A coled text-based logo for the plugin, used mainly for command responses, but can be used for other stuff. <br>
     * Note: The ChatColor being used after this string will be "reset" to ChatColor.AQUA.
     */
    public static final String LOGO = ChatColor.BLUE + "[" + ChatColor.DARK_AQUA + "Enchantments" + ChatColor.RED + "+"
            + ChatColor.BLUE + "] " + ChatColor.AQUA;
    
    /**
     * A coled text-based logo for the plugin, used mainly for the command line, but can be used for other stuff. <br>
     * Note: Due to the nature of this String, it is recommended to use it before a ChatColor.RESET or similar.
     */
    public static final String MINILOGO = ChatColor.DARK_AQUA + "Enchantments" + ChatColor.RED + "+";

    // Current Enchantments_plus version
    public static String version = "";

    public static final CompatibilityAdapter COMPATIBILITY_ADAPTER;
    public static AbstractAnticheatAdapter ANTICHEAT_ADAPTER = new None();

    // Random object
    public static final Random rnd = new Random();

    public static final BlockFace[] CARDINAL_BLOCK_FACES = {
        BlockFace.UP,
        BlockFace.DOWN,
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST
    };

    static {
        String versionString = Bukkit.getServer().getClass().getPackage().getName();
        String nmsVersionString = versionString.substring(versionString.lastIndexOf('.') + 1);
        System.out.println(BRAND + ": Detected NMS version \"" + nmsVersionString + "\"");
        switch (nmsVersionString) {
            default:
                COMPATIBILITY_ADAPTER = new CompatibilityAdapter();
                break;
        }
    }
}
