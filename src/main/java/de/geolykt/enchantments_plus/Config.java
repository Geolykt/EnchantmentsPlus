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

import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.AdvancedLoreGetter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.BasicLoreGetter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.LeightweightPDCGetter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.PersistentDataGetter;
import de.geolykt.enchantments_plus.enchantments.*;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.evt.WatcherEnchant;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

// This class manages individual world configs, loading them each from the config file. It will start the process
//      to automatically update the config files if they are old
public class Config {

    public static final Map<World, Config> CONFIGS = new HashMap<>(3); // Map of all world configs on the current server
    public static final Set<CustomEnchantment> allEnchants = new HashSet<>(72, 1); // Set of all active Custom enchantments in form of instances.

    /**
     * This variable holds the classes of every registered enchantment in the plugin, please do not modify the variable, as it may have some
     * Unforeseen consequences.
     *
     * @since 1.2.2
     */
    public static final Set<Class<? extends CustomEnchantment>> REGISTERED_ENCHANTMENTS = new HashSet<>(72, 1);
    public static final FileConfiguration PATCH_CONFIGURATION;

    /**
     * True if reveal was registered, false otherwise, internally used to make sure that
     * the OreUncover event listener is not registered when not needed
     *
     * @since 3.0.0
     */
    private static final boolean registeredReveal = false;

    private final Set<CustomEnchantment> worldEnchants; // Set of active Custom Enchantments
    private final Map<String, CustomEnchantment> nameToEnch;
    private final Map<Short, CustomEnchantment> idToEnch; // Since 1.0.0, changed signature in 3.0.0
    private final Map<BaseEnchantments, CustomEnchantment> baseToEnch;
    private final double enchantRarity; // Overall rarity of obtaining enchantments
    private final int maxEnchants; // Max number of Custom Enchantments on a tool
    private final int shredDrops; // The setting (all, block, none) for shred drops
    private final boolean explosionBlockBreak; // Determines whether enchantment explosions cause world damage
    private final boolean enchantGlow;
    private final ChatColor enchantmentColor;
    private final ChatColor curseColor;

    /**
     * Constructs a new Config object
     *
     * @param worldEnchants        The enchantment supported on this world
     * @param enchantRarity        The global rarity of enchantments on the world
     * @param maxEnchants          The maximum enchantments on a item
     * @param shredDrops           0 = all; 1 = only blocks; 2 = none
     * @param explosionBlockBreak  True if explosions block breaking is enabled, only affects a few enchantments
     * @param enchantmentColor     The color of enchantments in the lore of an item
     * @param curseColor           The color of a curse enchantment in the lore of an item
     * @param enchantGlow          True if item glow should be enabled
     * @param plugin               The plugin that created the config
     * @since 3.0.0
     */
    public Config(@NotNull Set<CustomEnchantment> worldEnchants, double enchantRarity, int maxEnchants, int shredDrops,
                  boolean explosionBlockBreak, @NotNull ChatColor enchantmentColor, @NotNull ChatColor curseColor,
                  boolean enchantGlow, Plugin plugin) {
        this.worldEnchants = worldEnchants;
        this.enchantRarity = enchantRarity;
        this.maxEnchants = maxEnchants;
        this.shredDrops = shredDrops;
        this.explosionBlockBreak = explosionBlockBreak;

        this.nameToEnch = new HashMap<>(worldEnchants.size());
        this.idToEnch = new HashMap<>(worldEnchants.size());
        this.baseToEnch = new EnumMap<>(BaseEnchantments.class);

        for (CustomEnchantment ench : this.worldEnchants) {
            idToEnch.put(ench.asEnum().getLegacyID(), ench);
            nameToEnch.put(ChatColor.stripColor(ench.getLoreName().toLowerCase().replace(" ", "")), ench);
            baseToEnch.put(ench.asEnum(), ench);
        }

        this.enchantGlow = enchantGlow;
        this.enchantmentColor = enchantmentColor;
        this.curseColor = curseColor;

        if (!registeredReveal && baseToEnch.containsKey(BaseEnchantments.REVEAL)) {
            Bukkit.getPluginManager().registerEvents(new Listener() {
                // Makes glowing shulkers on an ore block disappear if it is uncovered
                @EventHandler
                public void onOreUncover(BlockBreakEvent evt) {
                    if (Reveal.GLOWING_BLOCKS.size() != 0) {
                        for (BlockFace face : Storage.CARDINAL_BLOCK_FACES) {
                            Entity blockToRemove = Reveal.GLOWING_BLOCKS.remove(evt.getBlock().getRelative(face).getLocation());
                            if (blockToRemove != null) {
                                blockToRemove.remove();
                            }
                        }
                    }
                }
            }, plugin);
        }
        allEnchants.addAll(worldEnchants);
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

    /**
     * Creates and returns the config of a world.
     *
     * @param world  The world the the configuration is valid for.
     * @param plugin The plugin requesting this operation (used internally for smart even registration)
     * @return The newly constructed configuration
     * @since 3.0.0
     */
    private static @NotNull Config getWorldConfig(@NotNull World world, Plugin plugin) {
        try {
            InputStream stream = Enchantments_plus.class.getResourceAsStream("/defaultconfig.yml");
            File file = new File(Storage.plugin.getDataFolder(), world.getName() + ".yml");
            if (!file.exists()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    stream.transferTo(fos);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            YamlConfiguration yamlConfig = new YamlConfiguration();
            yamlConfig.load(file);
            int[] version = new int[3];
            try {
                String[] versionString;
                if (yamlConfig.contains("ConfigVersion")) {
                    versionString = yamlConfig.getString("ConfigVersion").split("\\.");
                } else {
                    try {
                        versionString = yamlConfig.getString("ZenchantmentsConfigVersion").split("\\.");
                    } catch (NullPointerException ex) {
                        versionString = yamlConfig.getStringList("ZenchantmentsConfigVersion").get(0).split("\\.");
                    }
                }
                if (versionString.length == 3) {
                    for (int i = 0; i < 3; i++) {
                        version[i] = Integer.parseInt(versionString[i]);
                    }
                } else {
                    version = new int[]{0, 0, 0};
                }
            } catch (Exception expected) {
                version = new int[]{2, 1, 6};
            }
            // TODO do not hardcode this version
            if (version[0] != 2 && version[1] != 1 && version[2] != 6) {
                plugin.getLogger().warning("Config file for world " + world.getName() + " with UID " + world.getUID().toString()
                        + " might be potentially out of date! (does not match the latest revision version 2.1.6)");
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
                        ench.cooldownMillis(getCooldown(data));
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
                    ex.printStackTrace();
                    System.err.printf("Error parsing config for enchantment '%s'. Skipping.", cl.getName());
                }
            }
            return new Config(enchantments, enchantRarity, maxEnchants, shredDrops, explosionBlockBreak,
                    enchantColor, curseColor, enchantGlow, plugin);
        } catch (IOException | InvalidConfigurationException ex) {
            System.err.printf("Error parsing config for world '%s'.", world.getName());
            throw new RuntimeException("Error parsing config for a world", ex);
        }
    }

    /**
     * Must be specified
     */
    private static float getProbability(@NotNull LinkedHashMap<String, Object> data) {
        return ((Number) data.get(ConfigKeys.PROBABILITY.toString())).floatValue();
    }

    /**
     * Must be specified
     */
    private static String getLoreName(@NotNull LinkedHashMap<String, Object> data) {
        return data.get(ConfigKeys.NAME.toString()).toString();
    }

    /**
     * Must be specified.
     * The returned cooldown is in milliseconds starting from v3.0.0
     * and was in ticks until v3.0.0 (excluded)
     *
     * @since 1.0.0
     */
    private static int getCooldown(@NotNull LinkedHashMap<String, Object> data) {
        return ((Number) data.get(ConfigKeys.COOLDOWN.toString())).intValue();
    }

    /**
     * Must be specified.
     */
    private static int getMaxLevel(@NotNull LinkedHashMap<String, Object> data) {
        return ((Number) data.get(ConfigKeys.MAX_LEVEL.toString())).intValue();
    }

    /**
     * Must be specified
     */
    private static Tool[] getTools(@NotNull LinkedHashMap<String, Object> data) {
        Set<Tool> materials = new HashSet<>();
        for (String s : ((String) data.get(ConfigKeys.TOOLS.toString())).split(", |\\,")) {
            materials.add(Tool.fromString(s));
        }
        return materials.toArray(new Tool[0]);
    }

    /**
     * Defaulting to 1, as stated in CustomEnchantment
     */
    private static double getPower(@NotNull LinkedHashMap<String, Object> data) {
        return ((Number) data.getOrDefault(ConfigKeys.POWER.toString(), 1.0)).doubleValue();
    }

    /**
     * Gets the modifier for the Area of Effect.
     *
     * @param data The data
     * @return The aoe modifier
     * @since 2.1.6
     */
    private static double getAOEMod(@NotNull LinkedHashMap<String, Object> data) {
        return ((Number) data.getOrDefault(ConfigKeys.AREA_OF_EFFECT.toString(), 1.0)).doubleValue();
    }

    /**
     * Returns the Enchantments that are conflicting with the enchantment.
     * Unused until v3.0.0
     *
     * @param data      The data
     * @param defaults  The defaults to return if the key does not exist.
     * @return The conflicting enchantments, or defaults if it is unmapped.
     * @since 2.2.2
     */
    private static @NotNull BaseEnchantments[] getConflicts(@NotNull LinkedHashMap<String, Object> data, @NotNull BaseEnchantments[] defaults) {
        if (data.containsKey(ConfigKeys.CONFLICTS.toString())) {
            String[] s = data.get(ConfigKeys.CONFLICTS.toString()).toString().split(",");
            BaseEnchantments[] conflicts = new BaseEnchantments[s.length];
            for (int i = 0; i < s.length; i++) {
                conflicts[i] = BaseEnchantments.valueOf(s[i]);
            }
            return conflicts;
        } else {
            return defaults;
        }
    }

    // Returns the config object associated with the given world
    public static Config get(@NotNull World world) {
        if (CONFIGS.get(world) == null) {
            Config.CONFIGS.put(world, getWorldConfig(world, Storage.plugin));
        }
        return CONFIGS.get(world);
    }

    /**
     * Registers the configurations for the currently loaded worlds
     *
     * @param plugin The plugin requesting this operation
     * @since 3.0.0
     */
    protected static void registerWorldConfigurations(Plugin plugin) {
        for (World world : Bukkit.getWorlds()) {
            Config.CONFIGS.put(world, getWorldConfig(world, plugin));
        }
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

    /**
     * Obtains an enchantment from it's name
     *
     * @param enchName The name of the enchantment
     * @return The instance of the enchantment, or null if unmapped
     * @since 1.0.0
     */
    public @Nullable CustomEnchantment enchantFromString(@NotNull String enchName) {
        return nameToEnch.get(ChatColor.stripColor(enchName.toLowerCase()));
    }

    /**
     * Returns the set of enchantment names. Please note that the returned key set backs the actual registered enchantments,
     * so removal is not recommended.
     *
     * @return The names of the registered enchantments in this world
     * @since 3.0.0
     */
    public @NotNull Set<String> getEnchantNames() {
        return nameToEnch.keySet();
    }

    /**
     * Returns the Name-Enchantment instance mappings of the world.
     *
     * @return A set of the entries of the map of registered enchantments
     * @since 1.0.0
     */
    public @NotNull Set<Map.Entry<String, CustomEnchantment>> getSimpleMappings() {
        return nameToEnch.entrySet();
    }

    /**
     * Obtains an enchantment from it's ID
     *
     * @param id The ID of the enchantment
     * @return The enchantment mapped to the ID
     * @since 3.0.0
     */
    public @Nullable CustomEnchantment enchantFromID(short id) {
        return idToEnch.get(id);
    }

    /**
     * Obtains the enchantment that is backed by the BaseEnchantments enum.
     * Please note that the enchantment not be registered, which is why it may return null.
     *
     * @param ench The enchantment
     * @return The enchantment instance valid in the world, or null
     * @since 2.1.1
     */
    public @Nullable CustomEnchantment enchantFromEnum(@NotNull BaseEnchantments ench) {
        return baseToEnch.get(ench);
    }

    static {
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

} enum ConfigKeys {
    ENCHANTMENTS("enchantments"),
    NAME("Name"),
    PROBABILITY("Probability"),
    COOLDOWN("Cooldown"),
    POWER("Power"),
    MAX_LEVEL("Max Level"),
    TOOLS("Tools"),

    /**
     * Denotes the Area of effect of an achievement, only applicable for enchantment that implements {@link AreaOfEffectable}.
     * If the key does not exist, but should then a value of 1 should be implied.
     *
     * @since 2.1.6
     */
    AREA_OF_EFFECT("Effect area modifier"),

    /**
     * The key to get the conflicts of an enchantment.
     * Unused until v3.0.0
     * It's value should be a String separated with commas, the individual strings should all represent a {@link BaseEnchantments}
     *
     * @since 2.2.2
     */
    CONFLICTS("Conflicts");

    private final String key;

    ConfigKeys(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return this.key;
    }
}

