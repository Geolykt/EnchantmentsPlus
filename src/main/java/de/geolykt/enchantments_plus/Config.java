package de.geolykt.enchantments_plus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.AdvancedLoreGetter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.BasicLoreGetter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.LeightweightPDCGetter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.PersistentDataGetter;
import de.geolykt.enchantments_plus.enchantments.*;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.evt.WatcherEnchant;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;

// This class manages individual world configs, loading them each from the config file. It will start the process
//      to automatically update the config files if they are old
public class Config {

    public static final Map<World, Config> CONFIGS = new HashMap<>(3, 1); // Map of all world configs on the current server
    public static final Set<CustomEnchantment> allEnchants = new HashSet<>(72, 1); // Set of all active Custom enchantments in form of instances.

    /**
     * This variable holds the classes of every registered enchantment in the plugin, please do not modify the variable, as it may have some
     * Unforeseen consequences.
     * @since 1.2.2
     */
    public static final Set<Class<? extends CustomEnchantment>> REGISTERED_ENCHANTMENTS = new HashSet<>(72, 1);

    private static final int CONFIG_BUFFER_SIZE = 16 * 1024;
    private final Set<CustomEnchantment> worldEnchants; // Set of active Custom Enchantments
    private final Map<String, CustomEnchantment> nameToEnch;
    private final Map<Integer, CustomEnchantment> idToEnch;
    private final Map<BaseEnchantments, CustomEnchantment> baseToEnch;
    private final double enchantRarity; // Overall rarity of obtaining enchantments
    private final int maxEnchants; // Max number of Custom Enchantments on a tool
    private final int shredDrops; // The setting (all, block, none) for shred drops
    private final boolean explosionBlockBreak; // Determines whether enchantment explosions cause world damage
    private final World world; // The World associated with the config
    private final boolean enchantGlow;
    private final ChatColor enchantmentColor;
    private final ChatColor curseColor;

    public static final FileConfiguration PATCH_CONFIGURATION;
    // Constructs a new config object
    public Config(Set<CustomEnchantment> worldEnchants, double enchantRarity, int maxEnchants, int shredDrops,
            boolean explosionBlockBreak,
            ChatColor enchantmentColor, ChatColor curseColor, boolean enchantGlow, World world) {
        this.worldEnchants = worldEnchants;
        this.enchantRarity = enchantRarity;
        this.maxEnchants = maxEnchants;
        this.shredDrops = shredDrops;
        this.explosionBlockBreak = explosionBlockBreak;
        this.world = world;

        this.nameToEnch = new HashMap<>(worldEnchants.size());
        this.idToEnch = new HashMap<>(worldEnchants.size());
        this.baseToEnch = new EnumMap<>(BaseEnchantments.class);

        for (CustomEnchantment ench : this.worldEnchants) {
            idToEnch.put(ench.getId(), ench);
            nameToEnch.put(ChatColor.stripColor(ench.getLoreName().toLowerCase().replace(" ", "")), ench);
            baseToEnch.put(ench.asEnum(), ench);
        }

        this.enchantGlow = enchantGlow;
        this.enchantmentColor = enchantmentColor;
        this.curseColor = curseColor;

        allEnchants.addAll(worldEnchants);
    }

    // Returns a mapping of enchantment names to custom enchantment objects
    public Set<CustomEnchantment> getEnchants() {
        return worldEnchants;
    }

    // Returns the overall rarity of obtaining an enchantment
    public double getEnchantRarity() {
        return enchantRarity;
    }

    // Returns the max number of enchantments applicable on a tool
    public int getMaxEnchants() {
        return maxEnchants;
    }

    // Returns which block break setting is enabled for shred (0 = all; 1 = blocks;
    // 2 = none)
    public int getShredDrops() {
        return shredDrops;
    }

    // Returns if certain enchantments can break blocks with the explosions they
    // create - only used by enchanted arrows as of yet (as they are the only ones to create explosions)
    public boolean explosionBlockBreak() {
        return explosionBlockBreak;
    }

    // Returns whether enchant glow is enabled for custom enchantments
    public boolean enchantGlow() {
        return enchantGlow;
    }

    // Returns the color for enchantment lore
    public ChatColor getEnchantmentColor() {
        return enchantmentColor;
    }

    // Returns the color for curse lore
    public ChatColor getCurseColor() {
        return curseColor;
    }

    // Returns the world associated with the config
    public World getWorld() {
        return world;
    }

    public CustomEnchantment enchantFromString(String enchName) {
        return nameToEnch.get(ChatColor.stripColor(enchName.toLowerCase()));
    }

    public List<String> getEnchantNames() {
        return new ArrayList<>(nameToEnch.keySet());
    }

    public Set<Map.Entry<String, CustomEnchantment>> getSimpleMappings() {
        return nameToEnch.entrySet();
    }

    public CustomEnchantment enchantFromID(int id) {
        return idToEnch.get(id);
    }

    public CustomEnchantment enchantFromEnum(BaseEnchantments ench) {
        return baseToEnch.get(ench);
    }

    // Loads, parses, and auto updates the config file, creating a new config for
    // each map
    public static void loadConfigs() {
        CONFIGS.clear();
        WatcherEnchant.apply_patch_explosion = PATCH_CONFIGURATION.getBoolean("explosion.enable", true);
        WatcherEnchant.apply_patch_piston = PATCH_CONFIGURATION.getBoolean("piston.enable", true);
        WatcherEnchant.patch_cancel_explosion = !PATCH_CONFIGURATION.getBoolean("explosion.removeBlocksInsteadOfCancel",
                false);
        WatcherEnchant.patch_cancel_netherstep = !PATCH_CONFIGURATION
                .getBoolean("patch_ench_protect.netherstep_removeBlocksInsteadOfCancel", false);
        WatcherEnchant.patch_cancel_frozenstep = !PATCH_CONFIGURATION
                .getBoolean("patch_ench_protect.frozenstep_removeBlocksInsteadOfCancel", false);
        Spectral.performWorldProtection = PATCH_CONFIGURATION.getBoolean("worldProtection.spectral", true);
        Spectral.useNativeProtection = PATCH_CONFIGURATION.getBoolean("worldProtection.native", true);
        Arborist.doGoldenAppleDrop = PATCH_CONFIGURATION.getBoolean("recipe.misc.arborist-doGoldenAppleDrop", true);
        Siphon.ratio = PATCH_CONFIGURATION.getDouble("nerfs.siphonRatio", 0.5);
        Siphon.calcAmour = PATCH_CONFIGURATION.getBoolean("nerfs.siphonsubstractAmour", true);
        Laser.doShredCooldown = PATCH_CONFIGURATION.getBoolean("nerfs.shredCoolDownOnLaser", true);
        
        boolean isAllowlist = PATCH_CONFIGURATION.getBoolean("isAllowlist", true);
        EnumSet<Material> allowlist = EnumSet.noneOf(Material.class);
        if (PATCH_CONFIGURATION.getBoolean("denyPartial", false)) {
            for (String s : PATCH_CONFIGURATION.getStringList("getterAllow")) {
                for (Material m : Material.values()) {
                    if (m.toString().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
                        allowlist.add(m);
                    }
                }
            }
        } else {
            for (String s : PATCH_CONFIGURATION.getStringList("getterAllow")) {
                allowlist.add(Material.matchMaterial(s));
            }
        }
        
        switch (PATCH_CONFIGURATION.getString("enchantmentGatherer", "advLore")) {
        case "advLore":
            CustomEnchantment.Enchantment_Adapter = new AdvancedLoreGetter(allowlist, !isAllowlist);
            break;
        case "lwNBT":
            CustomEnchantment.Enchantment_Adapter = new LeightweightPDCGetter();
            break;
        case "NBT":
            CustomEnchantment.Enchantment_Adapter = new PersistentDataGetter(allowlist, !isAllowlist);
            break;
        case "PR47-lore":
            CustomEnchantment.Enchantment_Adapter = new BasicLoreGetter();
            break;
        default:
            Bukkit.getLogger().severe(Storage.MINILOGO + ChatColor.RED + "No (or invalid) enchantment gatherer specified, fallback to default.");
        }
    }

    private static byte[] streamReadAllBytes(InputStream stream) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[CONFIG_BUFFER_SIZE];

        try {
            while ((nRead = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return buffer.toByteArray();
        }

        return buffer.toByteArray();
    }

    public static Config getWorldConfig(World world) {
        try {
            InputStream stream = Enchantments_plus.class.getResourceAsStream("/defaultconfig.yml");
            File file = new File(Storage.plugin.getDataFolder(), world.getName() + ".yml");
            if (!file.exists()) {
                try {
                    String raw = new String(streamReadAllBytes(stream), StandardCharsets.UTF_8);
                    byte[] b = raw.getBytes();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(b, 0, b.length);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    System.err.println("Error loading config");
                }
            }
            YamlConfiguration yamlConfig = new YamlConfiguration();
            yamlConfig.load(file);
            int[] version = new int[3];
            try {
                String[] versionString;
                try {
                    versionString = yamlConfig.getString("ZenchantmentsConfigVersion").split("\\.");
                } catch (NullPointerException ex) {
                    versionString = ((String) yamlConfig.getList("ZenchantmentsConfigVersion").get(0)).split("\\.");
                }
                if (versionString.length == 3) {
                    for (int i = 0; i < 3; i++) {
                        version[i] = Integer.parseInt(versionString[i]);
                    }
                } else {
                    version = new int[] { 0, 0, 0 };
                }
            } catch (Exception ex) {
                version = new int[] { 1, 5, 0 };
            }
            // Init variables
            final int shredDrops;
            yamlConfig.save(file);
            // Load Variables
            double enchantRarity = yamlConfig.getDouble("enchant_rarity", 25.0) / 100;
            int maxEnchants = yamlConfig.getInt("max_enchants", 4);
            boolean explosionBlockBreak = yamlConfig.getBoolean("explosion_block_break", true);
            boolean enchantGlow = yamlConfig.getBoolean("enchantment_glow", false);
            ChatColor enchantColor = ChatColor.getByChar(yamlConfig.getString("enchantment_color", "7"));
            ChatColor curseColor = ChatColor.getByChar(yamlConfig.getString("curse_color", "c"));

            enchantColor = enchantColor != null ? enchantColor : ChatColor.GRAY;
            curseColor = curseColor != null ? curseColor : ChatColor.RED;

            switch ((String) yamlConfig.get("shred_drops")) {
                case "all":
                    shredDrops = 0;
                    break;
                case "block":
                    shredDrops = 1;
                    break;
                case "none":
                    shredDrops = 2;
                    break;
                default:
                    shredDrops = 0;
            }
            Map<String, LinkedHashMap<String, Object>> configInfo = new HashMap<>();
            for (Map<String, LinkedHashMap<String, Object>> definition : (List<Map<String, LinkedHashMap<String, Object>>>) yamlConfig
                    .get(ConfigKeys.ENCHANTMENTS.toString())) {
                for (String enchantmentName : definition.keySet()) {
                    configInfo.put(enchantmentName, definition.get(enchantmentName));
                }
            }
            // Load CustomEnchantment Classes
            Set<CustomEnchantment> enchantments = new HashSet<>();
            for (Class<? extends CustomEnchantment> cl : REGISTERED_ENCHANTMENTS) {
                try {
                    CustomEnchantment.Builder<? extends CustomEnchantment> ench = cl.getDeclaredConstructor().newInstance().defaults();
                    if (configInfo.containsKey(ench.loreName())) {
                        LinkedHashMap<String, Object> data = configInfo.get(ench.loreName());
                        ench.probability(getProbability(data));
                        ench.loreName(getLoreName(data));
                        ench.cooldown(getCooldown(data));
                        ench.maxLevel(getMaxLevel(data));
                        ench.power(getPower(data));
                        ench.enchantable(getTools(data));
                        final CustomEnchantment builtEnch = ench.build();
                        if (builtEnch instanceof AreaOfEffectable) {
                            ((AreaOfEffectable) builtEnch).setAOEMultiplier(getAOEMod(data));
                        }
                        if (ench.probability() != -1) {
                            enchantments.add(builtEnch);
                        }
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                        NoSuchMethodException | SecurityException ex) {
                    System.err.printf("Error parsing config for enchantment '%s'. Skipping.", cl.getName());
                }
            }
            return new Config(enchantments, enchantRarity, maxEnchants, shredDrops, explosionBlockBreak,
                    enchantColor, curseColor, enchantGlow, world);
        } catch (IOException | InvalidConfigurationException ex) {
            System.err.printf("Error parsing config for world '%s'. Skipping", world.getName());
        }
        return null;
    }

    /**
     * Must be specified
     */
    private static float getProbability(LinkedHashMap<String, Object> data) {
        return (float) (double) data.get(ConfigKeys.PROBABILITY.toString());
    }

    /**
     * Must be specified
     */
    private static String getLoreName(LinkedHashMap<String, Object> data) {
        return (String) data.get(ConfigKeys.NAME.toString());
    }

    /**
     * Must be specified
     */
    private static int getCooldown(LinkedHashMap<String, Object> data) {
        return (int) data.get(ConfigKeys.COOLDOWN.toString());
    }

    /**
     * Must be specified.
     */
    private static int getMaxLevel(LinkedHashMap<String, Object> data) {
        return (int) data.get(ConfigKeys.MAX_LEVEL.toString());
    }

    /**
     * Must be specified
     */
    private static Tool[] getTools(LinkedHashMap<String, Object> data) {
        Set<Tool> materials = new HashSet<>();
        for (String s : ((String) data.get(ConfigKeys.TOOLS.toString())).split(", |\\,")) {
            materials.add(Tool.fromString(s));
        }
        return materials.toArray(new Tool[0]);
    }

    /**
     * Defaulting to 1, as stated in CustomEnchantment
     */
    private static double getPower(LinkedHashMap<String, Object> data) {
        return (double) data.getOrDefault(ConfigKeys.POWER.toString(), 1.0);
    }
    
    /**
     * Gets the modifier for the Area of Effect.
     * @param data The data
     * @return The aoe modifier
     * @since 2.1.6
     */
    private static double getAOEMod(LinkedHashMap<String, Object> data) {
        return (double) data.getOrDefault(ConfigKeys.AREA_OF_EFFECT.toString(), 1.0);
    }

    /**
     * Returns the Enchantments that are conflicting with the enchantment.
     * Unused until v3.0.0
     * @param data The data
     * @param defaults The defaults to return if the key does not exist.
     * @return The conflicting enchantments, or defaults if it is unmapped.
     * @since 2.2.2
     */
    @SuppressWarnings("unused")
	private static BaseEnchantments[] getConflicts(LinkedHashMap<String, Object> data, BaseEnchantments[] defaults) {
    	if (data.containsKey(ConfigKeys.CONFLICTS.toString())) {
    		String[] s = data.get(ConfigKeys.CONFLICTS.toString()).toString().split(",");
    		BaseEnchantments [] conflicts = new BaseEnchantments[s.length];
    		for (int i = 0; i < s.length; i++) {
    			conflicts[i] = BaseEnchantments.valueOf(s[i]);
    		}
    		return conflicts;
    	} else {
    		return defaults;
    	}
    }

    // Returns the config object associated with the given world
    public static Config get(World world) {
        if (CONFIGS.get(world) == null) {
            Config.CONFIGS.put(world, getWorldConfig(world));
        }
        return CONFIGS.get(world);
    }

    static {
        for (World world : Bukkit.getWorlds()) {
            Config.CONFIGS.put(world, getWorldConfig(world));
        }
        File patchFile = new File(Storage.plugin.getDataFolder(), "patches.yml");
        if (!patchFile.exists()) {
            Storage.plugin.saveResource("patches.yml", false);
        }
        PATCH_CONFIGURATION = YamlConfiguration.loadConfiguration(patchFile);
        
        REGISTERED_ENCHANTMENTS.add(Anthropomorphism.class);
        REGISTERED_ENCHANTMENTS.add(Apocalypse.class);
        REGISTERED_ENCHANTMENTS.add(Arborist.class);
        REGISTERED_ENCHANTMENTS.add(Bind.class);
        REGISTERED_ENCHANTMENTS.add(BlazesCurse.class);
        REGISTERED_ENCHANTMENTS.add(Blizzard.class);
        REGISTERED_ENCHANTMENTS.add(Bounce.class);
        REGISTERED_ENCHANTMENTS.add(Burst.class);
        REGISTERED_ENCHANTMENTS.add(Combustion.class);
        REGISTERED_ENCHANTMENTS.add(Conversion.class);
        REGISTERED_ENCHANTMENTS.add(Decapitation.class);
        REGISTERED_ENCHANTMENTS.add(Ethereal.class);
        REGISTERED_ENCHANTMENTS.add(Extraction.class);
        REGISTERED_ENCHANTMENTS.add(Fire.class);
        REGISTERED_ENCHANTMENTS.add(Firestorm.class);
        REGISTERED_ENCHANTMENTS.add(Fireworks.class);
        REGISTERED_ENCHANTMENTS.add(Force.class);
        REGISTERED_ENCHANTMENTS.add(FrozenStep.class);
        REGISTERED_ENCHANTMENTS.add(Fuse.class);
        REGISTERED_ENCHANTMENTS.add(Germination.class);
        REGISTERED_ENCHANTMENTS.add(Glide.class);
        REGISTERED_ENCHANTMENTS.add(Gluttony.class);
        REGISTERED_ENCHANTMENTS.add(GoldRush.class);
        REGISTERED_ENCHANTMENTS.add(Grab.class);
        REGISTERED_ENCHANTMENTS.add(GreenThumb.class);
        REGISTERED_ENCHANTMENTS.add(Gust.class);
        REGISTERED_ENCHANTMENTS.add(Harvest.class);
        REGISTERED_ENCHANTMENTS.add(Haste.class);
        REGISTERED_ENCHANTMENTS.add(IceAspect.class);
        REGISTERED_ENCHANTMENTS.add(Jump.class);
        REGISTERED_ENCHANTMENTS.add(Laser.class);
        REGISTERED_ENCHANTMENTS.add(Level.class);
        REGISTERED_ENCHANTMENTS.add(LongCast.class);
        REGISTERED_ENCHANTMENTS.add(Lumber.class);
        REGISTERED_ENCHANTMENTS.add(Magnetism.class);
        REGISTERED_ENCHANTMENTS.add(Meador.class);
        REGISTERED_ENCHANTMENTS.add(Missile.class);
        REGISTERED_ENCHANTMENTS.add(Mow.class);
        REGISTERED_ENCHANTMENTS.add(MysteryFish.class);
        REGISTERED_ENCHANTMENTS.add(NetherStep.class);
        REGISTERED_ENCHANTMENTS.add(NightVision.class);
        REGISTERED_ENCHANTMENTS.add(Persephone.class);
        REGISTERED_ENCHANTMENTS.add(Pierce.class);
        REGISTERED_ENCHANTMENTS.add(Plough.class);
        REGISTERED_ENCHANTMENTS.add(Potion.class);
        REGISTERED_ENCHANTMENTS.add(PotionResistance.class);
        REGISTERED_ENCHANTMENTS.add(QuickShot.class);
        REGISTERED_ENCHANTMENTS.add(Rainbow.class);
        REGISTERED_ENCHANTMENTS.add(RainbowSlam.class);
        REGISTERED_ENCHANTMENTS.add(Reaper.class);
        REGISTERED_ENCHANTMENTS.add(Reveal.class);
        REGISTERED_ENCHANTMENTS.add(Saturation.class);
        REGISTERED_ENCHANTMENTS.add(ShortCast.class);
        REGISTERED_ENCHANTMENTS.add(Shred.class);
        REGISTERED_ENCHANTMENTS.add(Singularity.class);
        REGISTERED_ENCHANTMENTS.add(Siphon.class);
        REGISTERED_ENCHANTMENTS.add(SonicShock.class);
        REGISTERED_ENCHANTMENTS.add(Spectral.class);
        REGISTERED_ENCHANTMENTS.add(Speed.class);
        REGISTERED_ENCHANTMENTS.add(Spikes.class);
        REGISTERED_ENCHANTMENTS.add(Spread.class);
        REGISTERED_ENCHANTMENTS.add(Stationary.class);
        REGISTERED_ENCHANTMENTS.add(Stock.class);
        REGISTERED_ENCHANTMENTS.add(Stream.class);
        REGISTERED_ENCHANTMENTS.add(Switch.class);
        REGISTERED_ENCHANTMENTS.add(Terraformer.class);
        REGISTERED_ENCHANTMENTS.add(Toxic.class);
        REGISTERED_ENCHANTMENTS.add(Tracer.class);
        REGISTERED_ENCHANTMENTS.add(Transformation.class);
        REGISTERED_ENCHANTMENTS.add(Unrepairable.class);
        REGISTERED_ENCHANTMENTS.add(Variety.class);
        REGISTERED_ENCHANTMENTS.add(Vortex.class);
        REGISTERED_ENCHANTMENTS.add(Weight.class);
    }

}

enum ConfigKeys {
    ENCHANTMENTS("enchantments"),
    NAME("Name"),
    PROBABILITY("Probability"),
    COOLDOWN("Cooldown"),
    POWER("Power"),
    MAX_LEVEL("Max Level"),
    TOOLS("Tools"),
    
    /**
     * Denotes the Area of effect of an achievement, only applicable for enchantment that implements {@link AreaOfEffectable}.
     *  If the key does not exist, but should then a value of 1 should be implied.
     * @since 2.1.6
     */
    AREA_OF_EFFECT("Effect area modifier"),

    /**
     *  The key to get the conflicts of an enchantment. 
     *   Unused until v3.0.0
     *   It's value should be a String separated with commas, the individual strings should all represent a {@link BaseEnchantments}
     *  @since 2.2.2
     */
    CONFLICTS("Conflicts");

    private String key;

    ConfigKeys(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return this.key;
    }
}
