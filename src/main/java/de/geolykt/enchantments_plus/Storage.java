package de.geolykt.enchantments_plus;

import java.util.Random;

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
    public static final String BRAND = "Enchantments+";

    /**
     * Represents the way the plugin was obtained, the reason behind is purely for analytical purposes. <br>
     * It's currently only used by the `/ench version` command.
     * @since 1.0.0
     */
    public static final String DISTRIBUTION = "Git";

    /**
     * A coled text-based logo for the plugin, used mainly for command responses, but can be used for other stuff. <br>
     * Note: The ChatColor being used after this string will be "reset" to ChatColor.AQUA.
     * Has a space afterwards.
     * @since 1.0.0
     */
    public static final String LOGO = ChatColor.BLUE + "[" + ChatColor.DARK_AQUA + "Enchantments" + ChatColor.RED + "+"
            + ChatColor.BLUE + "] " + ChatColor.AQUA;
    
    /**
     * A coled text-based logo for the plugin, used mainly for the command line, but can be used for other stuff. <br>
     * Note: Due to the nature of this String, it is recommended to use it before a ChatColor.RESET or similar. (end of the character is red).
     * It also doesn't have a space afterwards
     * @since 1.0.0
     */
    public static final String MINILOGO = ChatColor.DARK_AQUA + "Enchantments" + ChatColor.RED + "+";

    /**
     * Marks the used version of the plugin. The version is gathered during the onEnable() function at runtime and is implicitly set via
     * the plugin.yml where it's collected from.
     * The usual format is MAJOR.MINOR.PATCH, however it may be annotated with a single character to mark reuploads.
     * @since 1.0.0
     */
    public static String version = "";

    public static final CompatibilityAdapter COMPATIBILITY_ADAPTER = new CompatibilityAdapter();
    public static AbstractAnticheatAdapter ANTICHEAT_ADAPTER = new None();

    /**
     * @deprecated Will be removed in the future to make way for proper thread safety
     * Shared random object
     * @since 1.0.0
     */
    @Deprecated
    public static final Random rnd = new Random();

    /**
     * Container for the cardinal block faces, i. e. block faces that are directly touch the current block.
     * @since 1.0.0
     */
    public static final BlockFace[] CARDINAL_BLOCK_FACES = {
        BlockFace.UP,
        BlockFace.DOWN,
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST,
    };
}
