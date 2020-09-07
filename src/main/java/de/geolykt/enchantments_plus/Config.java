package de.geolykt.enchantments_plus;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.geolykt.enchantments_plus.enchantments.Arborist;
import de.geolykt.enchantments_plus.enchantments.Siphon;
import de.geolykt.enchantments_plus.enchantments.Spectral;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.evt.WatcherEnchant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

// This class manages indivudual world configs, loading them each from the config file. It will start the process
//      to automatically update the config files if they are old
public class Config {

    public static final Map<World, Config> CONFIGS = new HashMap<>(); // Map of all world configs on the current server
    public static final HashSet<CustomEnchantment> allEnchants = new HashSet<>();

    private final Set<CustomEnchantment> worldEnchants;     // Set of active Custom Enchantments
    private final Map<String, CustomEnchantment> nameToEnch;
    private final Map<Integer, CustomEnchantment> idToEnch;
    private final double enchantRarity;                        // Overall rarity of obtaining enchantments
    private final int maxEnchants;                             // Max number of Custom Enchantments on a tool
    private final int shredDrops;                              // The setting (all, block, none) for shred drops
    private final boolean explosionBlockBreak;                 // Determines whether enchantment explosions cause world damage
    private final boolean descriptionLore;                     // Determines if description lore appears on tools
    private final ChatColor descriptionColor;                  // The color of the description lore
    private final World world;                                 // The World associated with the config
    private final boolean enchantGlow;
    private final ChatColor enchantmentColor;
    private final ChatColor curseColor;

    public static final FileConfiguration PATCH_CONFIGURATION;
    
    // Constructs a new config object
    public Config(Set<CustomEnchantment> worldEnchants, double enchantRarity,
            int maxEnchants, int shredDrops, boolean explosionBlockBreak,
            boolean descriptionLore, ChatColor descriptionColor, ChatColor enchantmentColor,
            ChatColor curseColor, boolean enchantGlow, World world) {
        this.worldEnchants = worldEnchants;
        this.enchantRarity = enchantRarity;
        this.maxEnchants = maxEnchants;
        this.shredDrops = shredDrops;
        this.explosionBlockBreak = explosionBlockBreak;
        this.descriptionLore = descriptionLore;
        this.descriptionColor = descriptionColor;
        this.world = world;

        this.nameToEnch = new HashMap<>();
        for (CustomEnchantment ench : this.worldEnchants) {
            nameToEnch.put(ChatColor.stripColor(ench.getLoreName().toLowerCase().replace(" ", "")), ench);
        }

        this.idToEnch = new HashMap<>();
        for (CustomEnchantment ench : this.worldEnchants) {
            idToEnch.put(ench.getId(), ench);
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

    // Returns which block break setting is enabled for shred (0 = all; 1 = blocks; 2 = none)
    public int getShredDrops() {
        return shredDrops;
    }

    // Returns if certain enchantments can break blocks with the explosions they create
    public boolean explosionBlockBreak() {
        return explosionBlockBreak;
    }

    // Returns if description lore appears on tools
    public boolean descriptionLore() {
        return descriptionLore;
    }

    // Returns the color of description lore
    public ChatColor getDescriptionColor() {
        return descriptionColor;
    }

    // Returns whether enchant glow is enabled for custom enchantments
    public boolean enchantGlow() { return enchantGlow; }

    // Returns the color for enchantment lore
    public ChatColor getEnchantmentColor() { return enchantmentColor; }

    // Returns the color for curse lore
    public ChatColor getCurseColor() { return curseColor; }

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

    // Loads, parses, and auto updates the config file, creating a new config for each map 
    public static void loadConfigs() {
        CONFIGS.clear();
        WatcherEnchant.apply_patch_explosion = PATCH_CONFIGURATION.getBoolean("explosion.enable", true);
        WatcherEnchant.apply_patch_piston = PATCH_CONFIGURATION.getBoolean("piston.enable", true);
        WatcherEnchant.patch_cancel_explosion = !PATCH_CONFIGURATION.getBoolean("explosion.removeBlocksInsteadOfCancel", false);
        WatcherEnchant.patch_cancel_netherstep = !PATCH_CONFIGURATION.getBoolean("patch_ench_protect.netherstep_removeBlocksInsteadOfCancel", false);
        WatcherEnchant.patch_cancel_frozenstep = !PATCH_CONFIGURATION.getBoolean("patch_ench_protect.frozenstep_removeBlocksInsteadOfCancel", false);
        Spectral.performWorldProtection = PATCH_CONFIGURATION.getBoolean("worldProtection.spectral", true);
        Arborist.doGoldenAppleDrop = PATCH_CONFIGURATION.getBoolean("recipe.misc.arborist-doGoldenAppleDrop", true);
        Siphon.ratio = PATCH_CONFIGURATION.getDouble("nerfs.siphonRatio", 0.5);
        Siphon.calcAmour = PATCH_CONFIGURATION.getBoolean("nerfs.siphonsubstractAmour", true);

        if (PATCH_CONFIGURATION.getString("enchantmentGatherer", "NBT").equals("NBT")) {
            Collection<Material> col = EnumSet.noneOf(Material.class);
            boolean doCompat = Storage.enchantments_plus.getConfig().getBoolean("compatibility", false);
            for (String s : PATCH_CONFIGURATION.getStringList("getterDeny")) {
                col.add(Material.matchMaterial(s));
            }
            CustomEnchantment.Enchantment_Adapter = new CustomEnchantment.PersistentDataGatherer(col, doCompat);
        } else if (PATCH_CONFIGURATION.getString("enchantmentGatherer").equals("PR47-lore")) {
            CustomEnchantment.Enchantment_Adapter = new CustomEnchantment.LegacyLoreGatherer();
        } else {
            CustomEnchantment.Enchantment_Adapter = new CustomEnchantment.ProvisionalLoreGatherer();
        }
    }

    private static byte[] streamReadAllBytes (InputStream stream) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

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
    
    public static Config getWorldConfig (World world) {
        try {
            InputStream stream = Enchantments_plus.class.getResourceAsStream("/defaultconfig.yml");
            File file = new File(Storage.enchantments_plus.getDataFolder(), world.getName() + ".yml");
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
                    version = new int[]{0, 0, 0};
                }
            } catch (Exception ex) {
                version = new int[]{1, 5, 0};
            }
            //Init variables
            final int shredDrops;
            yamlConfig.save(file);
            //Load Variables
            double rarity = (double) (yamlConfig.get("enchant_rarity"));
            double enchantRarity = (rarity / 100.0);
            int maxEnchants = (int) yamlConfig.get("max_enchants");
            boolean explosionBlockBreak = (boolean) yamlConfig.get("explosion_block_break");
            boolean descriptionLore = (boolean) yamlConfig.get("description_lore");
            boolean enchantGlow = (boolean) yamlConfig.get("enchantment_glow");
            ChatColor descriptionColor = ChatColor.getByChar("" + yamlConfig.get("description_color"));
            ChatColor enchantColor = ChatColor.getByChar("" + yamlConfig.get("enchantment_color"));
            ChatColor curseColor = ChatColor.getByChar("" + yamlConfig.get("curse_color"));

            descriptionColor = (descriptionColor != null) ? descriptionColor : ChatColor.GREEN;
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
            //Load CustomEnchantment Classes
            Set<CustomEnchantment> enchantments = new HashSet<>();
            Map<String, LinkedHashMap<String, Object>> configInfo = new HashMap<>();
            for (Map<String, LinkedHashMap<String, Object>> part
                : (List<Map<String, LinkedHashMap<String, Object>>>) yamlConfig.get("enchantments")) {
                for (String name : part.keySet()) {
                    configInfo.put(name, part.get(name));
                }
            }

            List<Class<? extends CustomEnchantment>> customEnchantments = new ArrayList<>();

            new FastClasspathScanner(CustomEnchantment.class.getPackage().getName()).overrideClasspath(Storage.pluginPath)
                                                                                    .matchSubclassesOf(CustomEnchantment.class, customEnchantments::add).scan();

            for (Class<? extends CustomEnchantment> cl : customEnchantments) {
                try {

                    CustomEnchantment.Builder<? extends CustomEnchantment> ench = cl.newInstance().defaults();
                    if (configInfo.containsKey(ench.loreName())) {
                        LinkedHashMap<String, Object> data = configInfo.get(ench.loreName());
                        ench.probability((float) (double) data.get("Probability"));
                        ench.loreName((String) data.get("Name"));
                        ench.cooldown((int) data.get("Cooldown"));

                        if (data.get("Max Level") != null) {
                            ench.maxLevel((int) data.get("Max Level"));
                        }
                        if (data.get("Power") != null) {
                            ench.power((double) data.get("Power"));
                        }
                        Set<Tool> materials = new HashSet<>();
                        for (String s : ((String) data.get("Tools")).split(", |\\,")) {
                            materials.add(Tool.fromString(s));
                        }
                        ench.enchantable(materials.toArray(new Tool[0]));
                        if (ench.probability() != -1) {
                            enchantments.add(ench.build());
                        }
                    }
                } catch (IllegalAccessException | ClassCastException | InstantiationException ex) {
                    System.err.println("Error parsing config for enchantment " + cl.getName() + ", skipping");
                }
            }
            return new Config(enchantments, enchantRarity, maxEnchants, shredDrops, explosionBlockBreak,
                descriptionLore, descriptionColor, enchantColor, curseColor, enchantGlow, world);
        } catch (IOException | InvalidConfigurationException ex) {
            System.err.println("Error parsing config for world " + world.getName() + ", skipping");
        }
        return null;
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
        File patchFile = new File(Storage.enchantments_plus.getDataFolder(), "patches.yml");
        if (!patchFile.exists()) {
            Storage.enchantments_plus.saveResource("patches.yml", false);
        }
        PATCH_CONFIGURATION = YamlConfiguration.loadConfiguration(patchFile);
    }

}
