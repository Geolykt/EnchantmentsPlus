package de.geolykt.enchantments_plus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;

import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;

import java.util.*;

public class Storage {

    // Instance of the Enchantments_plus plugin to be used by the rest of the classes
    public static Enchantments_plus enchantments_plus;

    public static final String BRAND = "Enchantments+";
    public static final String DISTRIBUTION = "Git";
    
    // Absolute path to the plugin jar
    public static String pluginPath;

    // The plugin Logo to be used in chat commands
    public static final String LOGO = ChatColor.BLUE + "[" + ChatColor.DARK_AQUA + "Enchantments" + ChatColor.RED + "+"
            + ChatColor.BLUE + "] " + ChatColor.AQUA;
    public static final String MINILOGO = ChatColor.DARK_AQUA + "Enchantments" + ChatColor.RED + "+";

    // Current Enchantments_plus version
    public static String version = "";

    public static final CompatibilityAdapter COMPATIBILITY_ADAPTER;

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
                COMPATIBILITY_ADAPTER = de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter.getInstance();
                break;
        }
    }
}
