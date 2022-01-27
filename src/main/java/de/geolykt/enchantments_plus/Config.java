/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2022 Geolykt and EnchantmentsPlus contributors
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

import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.compatibility.RosestackerMergePreventer;
import de.geolykt.enchantments_plus.compatibility.Stackmob5MergePreventer;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.AdvancedLoreGetter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.BasicLoreGetter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.LeightweightPDCGetter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.PersistentDataGetter;
import de.geolykt.enchantments_plus.compatibility.nativeperm.NativePermissionHooks;
import de.geolykt.enchantments_plus.enchantments.*;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.MobstackerPlugin;
import de.geolykt.enchantments_plus.enums.PierceMode;
import de.geolykt.enchantments_plus.evt.WatcherEnchant;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// This class manages individual world configs, loading them each from the config file. It will start the process
//      to automatically update the config files if they are old
public class Config {

    /**
     * A class that stores information about some configuration data.
     * It mostly stores the patches.yml file in memory without having to use large amounts of static abuse
     *
     * @since 4.0.0
     */
    public static class EnchantmentConfiguration {
        /**
         * Whether a golden apple should be dropped from leaves from time to time when using the arborist enchantment.
         *
         * @since 4.0.0
         */
        protected boolean arboristGoldenAppleDrop = true;

        /**
         * Whether to cancel the event if an explosion has been found that could cause exploits.
         *
         * @since 4.0.0
         */
        protected boolean cancelExplosions = true;

        /**
         * Whether to cancel the event if an explosion has been found that could cause exploits.
         * This variable is valid only for the Frozenstep enchantment
         *
         * @since 4.0.0
         */
        protected boolean cancelExplosionsFrozenstep = true;

        /**
         * Whether to cancel the event if an explosion has been found that could cause exploits.
         * This variable is valid only for the Netherstep enchantment
         *
         * @since 4.0.0
         */
        protected boolean cancelExplosionsNetherstep = true;

        /**
         * Whether the plugin should actively listen to explosions and mitigate effects done by this.
         * Disabling this can result in exploits!
         *
         * @since 4.0.0
         */
        protected boolean enableExplosionsProtection = true;

        /**
         * Whether to use the NativePermissionHooks at all.
         * This setting applies to all enchantments except for fire.
         * Note that the value is independent of {@link #spectralNPQ}.
         *
         * @since 4.0.0
         */
        protected boolean enableNPQ = true;

        /**
         * Whether the plugin should actively listen to piston events and mitigate effects done by this.
         * Disabling this can result in exploits!
         *
         * @since 4.0.0
         */
        protected boolean enablePistonsProtection = true;

        /**
         * Whether to use the {@link NativePermissionHooks}/native permission query for the fire enchantment
         *
         * @since 4.0.0
         */
        protected boolean fireNPQ = true;

        /**
         * Whether the laser enchantment prevents use of the shred enchantment temporarily
         *
         * @since 4.0.0
         */
        protected boolean laserBlockShred = true;

        /**
         * Obtains the {@link NamespacedKey} that is used by the {@link PersistentDataContainer} of Laser-enchanted items
         * to mark the color of the laser.
         *
         * @since 4.0.0
         */
        protected NamespacedKey laserColorKey = null;

        /**
         * The mobstacking plugin to use
         *
         * @since 4.0.0
         */
        protected MobstackerPlugin mobStackerPlugin = MobstackerPlugin.NONE;

        /**
         * Whether to subtract armour for the siphon enchantment, turning that to false can result in almost infinite fights
         * as there is a net gain in HP
         *
         * @since 4.0.0
         */
        protected boolean siphonArmor = true;

        /**
         * Whether to use the NativePermissionHooks for the spectral enchantment
         *
         * @since 4.0.0
         */
        protected boolean spectralNPQ = true;

        /**
         * Whether the {@link Vortex} enchantment should use
         * {@link CompatibilityAdapter#givePlayerXP(int, org.bukkit.entity.Player)} instead of a
         * raw {@link Player#giveExp(int)}. Disabling this feature can be useful as it reduces the
         * power of the enchantment and gives the old Zenchantments feeling as that was the default
         * prior to 4.0.4
         *
         * @since 4.0.4
         */
        protected boolean vortexApplyMending = true;

        /**
         * Whether to integrate to worldguard regions by disallowing the usage of enchantments when the "eplus" flag
         * is set to DENY
         *
         * @since 4.0.0
         */
        protected boolean wgRegionIntegration = false;

        /**
         * Whether the laser enchantment prevents use of the shred enchantment temporarily
         *
         * @return See above
         * @since 4.0.0
         */
        public boolean blockShredWithLaser() {
            return laserBlockShred;
        }

        /**
         * Whether to cancel the event if an explosion has been found that could cause exploits.
         * If false, then the given blocks are removed silently
         *
         * @return See above
         * @since 4.0.0
         */
        public boolean cancelExploitableExplosions() {
            return cancelExplosions;
        }

        /**
         * Whether to cancel the event if an explosion has been found that could cause exploits.
         * This method is valid only for the Frozenstep enchantment
         * If false, then the given blocks are removed silently
         *
         * @return See above
         * @since 4.0.0
         */
        public boolean cancelExploitableFrozenstepExplosions() {
            return cancelExplosionsFrozenstep;
        }

        /**
         * Whether to cancel the event if an explosion has been found that could cause exploits.
         * This method is valid only for the Netherstep enchantment
         * If false, then the given blocks are removed silently
         *
         * @return See above
         * @since 4.0.0
         */
        public boolean cancelExploitableNetherstepExplosions() {
            return cancelExplosionsNetherstep;
        }

        /**
         * Whether a golden apple should be dropped from leaves from time to time when using the arborist enchantment.
         *
         * @return See above
         * @since 4.0.0
         */
        public boolean enableGoldenAppleDrop() {
            return arboristGoldenAppleDrop;
        }
        /**
         * Whether to use the NativePermissionHooks at all.
         * This setting applies to all enchantments except for fire.
         * Note that the value is independent of {@link #enableSpectralNativePermissionQuery()}
         * so if this method yields false enableSpectralNativePermissionQuery can yield true either way.
         * Nevertheless, the plugin should not disregard this return value.
         *
         * @return See above
         * @since 4.0.0
         */
        public boolean enableNativepermissionQuery() {
            return enableNPQ;
        }

        /**
         * Whether to use the NativePermissionHooks for the spectral enchantment.
         * If {@link #enableNativepermissionQuery()} is false, then this method can be
         * disregarded
         *
         * @return See above
         * @since 4.0.0
         */
        public boolean enableSpectralNativePermissionQuery() {
            return spectralNPQ;
        }

        /**
         * Obtains the {@link NamespacedKey} that is used by the {@link PersistentDataContainer} of Laser-enchanted items
         * to mark the color of the laser.
         *
         * @return See above
         * @since 4.0.0
         */
        public @NotNull NamespacedKey getLaserColorKey() {
            if (laserColorKey == null) {
                throw new IllegalStateException("The laser color namespaced key has not yet been defined!");
            }
            return laserColorKey;
        }

        /**
         * Whether to prevent exploitable explosions. The exact strategy is defined with {@link #cancelExplosions},
         * {@link #cancelExplosionsFrozenstep} and {@link #cancelExplosionsNetherstep}.
         *
         * @return See above
         * @since 4.0.0
         */
        public boolean preventExploitableExplosions() {
            return enableExplosionsProtection;
        }

        /**
         * Whether to prevent exploitable piston movements concerning the netherstep and frozenstep blocks.
         * The exact strategy is defined with
         * {@link #cancelExplosionsFrozenstep} and {@link #cancelExplosionsNetherstep}.
         *
         * @return See above
         * @since 4.0.0
         */
        public boolean preventExploitablePistonMovements() {
            return enableExplosionsProtection;
        }

        /**
         * Whether the Siphon enchantment excludes damage that was mitigated by the armour.
         *
         * @return See above
         * @since 4.0.0
         */
        public boolean siphonUseFinalDamage() {
            return siphonArmor;
        }

        /**
         * Whether the {@link Vortex} enchantment should use
         * {@link CompatibilityAdapter#givePlayerXP(int, org.bukkit.entity.Player)} instead of a
         * raw {@link Player#giveExp(int)}. Disabling this feature can be useful as it reduces the
         * power of the enchantment and gives the old Zenchantments feeling as that was the default
         * prior to 4.0.4
         *
         * @return A boolean stating whether the Vortex enchantment should apply mending
         * @since 4.0.4
         */
        public boolean vortexApplyMending() {
            return vortexApplyMending;
        }
    }

    public static final Set<CustomEnchantment> allEnchants = new HashSet<>(72, 1); // Set of all active Custom enchantments in form of instances.
    public static final Map<World, Config> CONFIGS = new HashMap<>(3); // Map of all world configs on the current server

    /**
     * The enchantment configuration used in the current session.
     * Do not access this field unless there is no other logical way of accessing this field to prevent static abuse
     * and other spaghetti code (which is why that class was created in the first place)
     *
     * @since 4.0.0
     */
    static final EnchantmentConfiguration ENCH_CONFIG = new EnchantmentConfiguration();

    static final FileConfiguration PATCH_CONFIGURATION;

    /**
     * This variable holds the classes of every registered enchantment in the plugin, please do not modify the variable, as it may have some
     * Unforeseen consequences.
     *
     * @since 1.2.2
     */
    public static final Set<Class<? extends CustomEnchantment>> REGISTERED_ENCHANTMENTS = new HashSet<>(72, 1);

    /**
     * True if reveal was registered, false otherwise, internally used to make sure that
     * the OreUncover event listener is not registered when not needed
     * Actually existed since 3.0.0, but it was always false and final back then, ups.
     *
     * @since 3.1.3
     */
    private static boolean registeredReveal = false;

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

    private static <T> @NotNull T asNotNull(@Nullable T value) {
        if (value == null) {
            throw new NullPointerException("This should not have happened. Consider reporting this bug.");
        }
        return value;
    }

    // Returns the config object associated with the given world
    public static Config get(@NotNull World world) {
        if (CONFIGS.get(world) == null) {
            Config.CONFIGS.put(world, getWorldConfig(world, Storage.plugin));
        }
        return CONFIGS.get(world);
    }

    /**
     * Gets the modifier for the Area of Effect.
     *
     * @param data The data
     * @return The AOE modifier
     * @since 4.0.0
     */
    private static double getAOEModifier(@NotNull Map<String, Object> data) {
        return ((Number) data.getOrDefault(ConfigKeys.AREA_OF_EFFECT.toString(), 1.0)).doubleValue();
    }

    /**
     * Returns the Enchantments that are conflicting with the enchantment.
     *
     * @param data      The data
     * @param defaults  The defaults to return if the key does not exist.
     * @return The conflicting enchantments, or defaults if it is unmapped.
     * @since 4.0.0
     */
    private static @NotNull BaseEnchantments[] getEnchantmentConflicts(@NotNull Map<String, Object> data, @NotNull BaseEnchantments[] defaults) {
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

    /**
     * Must be specified.
     * The returned cooldown is in milliseconds starting from v3.0.0
     * and was in ticks until v3.0.0 (excluded)
     *
     * @since 4.0.0
     */
    private static int getEnchantmentCooldown(@NotNull Map<String, Object> data) {
        return ((Number) data.get(ConfigKeys.COOLDOWN.toString())).intValue();
    }

    /**
     * Must be specified
     */
    private static String getLoreName(@NotNull Map<String, Object> data) {
        return data.get(ConfigKeys.NAME.toString()).toString();
    }

    /**
     * Must be specified.
     */
    private static int getMaxLevel(@NotNull Map<String, Object> data) {
        return ((Number) data.get(ConfigKeys.MAX_LEVEL.toString())).intValue();
    }
    /**
     * Defaulting to 1, as stated in CustomEnchantment
     */
    private static double getPower(@NotNull Map<String, Object> data) {
        return ((Number) data.getOrDefault(ConfigKeys.POWER.toString(), 1.0)).doubleValue();
    }
    /**
     * Must be specified
     */
    private static float getProbability(@NotNull Map<String, Object> data) {
        return ((Number) data.get(ConfigKeys.PROBABILITY.toString())).floatValue();
    }
    /**
     * Must be specified
     */
    private static Tool[] getTools(@NotNull Map<String, Object> data) {
        Set<Tool> materials = new HashSet<>();
        for (String s : ((String) data.get(ConfigKeys.TOOLS.toString())).split(", |\\,")) {
            materials.add(Tool.fromString(s));
        }
        return materials.toArray(new Tool[0]);
    }

    /**
     * Creates and returns the config of a world.
     *
     * @param world  The world the the configuration is valid for.
     * @param plugin The plugin requesting this operation (used internally for smart event registration)
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
                    try {
                        stream.close();
                    } catch (IOException e2) {
                        new RuntimeException(e).printStackTrace();
                    }
                    throw new RuntimeException(e);
                }
            }
            YamlConfiguration yamlConfig = new YamlConfiguration();
            yamlConfig.load(file);
            int[] version = new int[3];
            try {
                String[] versionString;
                if (yamlConfig.contains("ConfigVersion")) {
                    versionString = asNotNull(yamlConfig.getString("ConfigVersion")).split("\\.");
                } else {
                    try {
                        versionString = asNotNull(yamlConfig.getString("ZenchantmentsConfigVersion")).split("\\.");
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
            ChatColor enchantColor = ChatColor.getByChar(asNotNull(yamlConfig.getString("enchantment_color", "7")));
            ChatColor curseColor = ChatColor.getByChar(asNotNull(yamlConfig.getString("curse_color", "c")));

            enchantColor = enchantColor != null ? enchantColor : ChatColor.GRAY;
            curseColor = curseColor != null ? curseColor : ChatColor.RED;

            switch (asNotNull(yamlConfig.getString("shred_drops"))) {
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
            Map<String, Map<String, Object>> configInfo = new HashMap<>();
            @SuppressWarnings({ "unchecked", "rawtypes" })
            List<Map<String, Map<String, Object>>> rawData = (List) yamlConfig.getMapList(ConfigKeys.ENCHANTMENTS.toString());
            for (Map<String, Map<String, Object>> definition : rawData) {
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
                        Map<String, Object> data = configInfo.get(ench.loreName());
                        ench.probability(getProbability(data));
                        ench.loreName(getLoreName(data));
                        ench.cooldownMillis(getEnchantmentCooldown(data));
                        ench.maxLevel(getMaxLevel(data));
                        ench.power(getPower(data));
                        ench.enchantable(getTools(data));
                        ench.enchConfig(ENCH_CONFIG);
                        ench.setConflicts(getEnchantmentConflicts(data, ench.getCurrentConflicts()));
                        final CustomEnchantment builtEnch = ench.build();
                        if (builtEnch instanceof AreaOfEffectable) {
                            ((AreaOfEffectable) builtEnch).setAOEMultiplier(getAOEModifier(data));
                        }
                        if (ench.probability() != -1) {
                            enchantments.add(builtEnch);
                        }
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                        NoSuchMethodException | SecurityException ex) {
                    plugin.getLogger().severe("Error parsing config for enchantment '" + cl.getName() + "'. Skipping.");
                    ex.printStackTrace();
                }
            }
            try {
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Config(enchantments, enchantRarity, maxEnchants, shredDrops, explosionBlockBreak,
                    enchantColor, curseColor, enchantGlow, plugin);
        } catch (IOException | InvalidConfigurationException ex) {
            plugin.getLogger().severe("Error parsing config for world '" + world.getName() + "'.");
            throw new RuntimeException("Error parsing config for a world", ex);
        }
    }

    // Loads, parses, and auto updates the config file, creating a new config for
    // each map
    public static void loadConfigs() {
        CONFIGS.clear();
        WatcherEnchant.getInstance().setConfiguration(ENCH_CONFIG);
        ENCH_CONFIG.enableExplosionsProtection = PATCH_CONFIGURATION.getBoolean("explosion.enable", true);
        ENCH_CONFIG.enablePistonsProtection = PATCH_CONFIGURATION.getBoolean("piston.enable", true);
        ENCH_CONFIG.cancelExplosions = !PATCH_CONFIGURATION.getBoolean("explosion.removeBlocksInsteadOfCancel", false);
        ENCH_CONFIG.cancelExplosionsNetherstep = !PATCH_CONFIGURATION.getBoolean("patch_ench_protect.netherstep_removeBlocksInsteadOfCancel", false);
        ENCH_CONFIG.cancelExplosionsFrozenstep = !PATCH_CONFIGURATION.getBoolean("patch_ench_protect.frozenstep_removeBlocksInsteadOfCancel", false);
        ENCH_CONFIG.spectralNPQ = PATCH_CONFIGURATION.getBoolean("worldProtection.spectral", true);
        ENCH_CONFIG.enableNPQ = PATCH_CONFIGURATION.getBoolean("worldProtection.native", true);
        ENCH_CONFIG.wgRegionIntegration = PATCH_CONFIGURATION.getBoolean("worldProtection.wg", false);
        ENCH_CONFIG.fireNPQ = ENCH_CONFIG.enableNPQ && PATCH_CONFIGURATION.getBoolean("worldprotection.fire", true);
        ENCH_CONFIG.arboristGoldenAppleDrop = PATCH_CONFIGURATION.getBoolean("recipe.misc.arborist-doGoldenAppleDrop", true);
        ENCH_CONFIG.siphonArmor = PATCH_CONFIGURATION.getBoolean("nerfs.siphonsubstractAmour", true);
        ENCH_CONFIG.laserBlockShred = PATCH_CONFIGURATION.getBoolean("nerfs.shredCoolDownOnLaser", true);
        ENCH_CONFIG.vortexApplyMending = PATCH_CONFIGURATION.getBoolean("buffs.vortexApplyMending", true);

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

        switch (asNotNull(PATCH_CONFIGURATION.getString("enchantmentGatherer", "advLore"))) {
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
            Storage.plugin.getLogger().severe("No (or invalid) enchantment gatherer specified, fallback to default.");
        }

        if (PATCH_CONFIGURATION.getBoolean("pluginCompat.mobstacker", true)) {
            try {
                Class.forName("dev.rosewood.rosestacker.event.EntityStackEvent");
                ENCH_CONFIG.mobStackerPlugin = MobstackerPlugin.ROSESTACKER;
            } catch (ClassNotFoundException excepted) {}
            try {
                Class.forName("uk.antiperson.stackmob.events.StackSpawnEvent");
                ENCH_CONFIG.mobStackerPlugin = MobstackerPlugin.STACKMOB_5;
            } catch (ClassNotFoundException excepted) {}
        }

        Collection<String> pierceModes = PATCH_CONFIGURATION.getStringList("pierce-modes");
        if (!pierceModes.isEmpty()) {
            Collection<PierceMode> modes = new ArrayList<>(pierceModes.size());
            for (String s : pierceModes) {
                try {
                    modes.add(PierceMode.valueOf(s));
                } catch (RuntimeException e) {
                    Storage.plugin.getLogger().warning("Unable to match pierce mode " + s + " to a valid value. Skipping it.");
                    e.printStackTrace(); // Just in case I messed up
                }
            }
            Storage.COMPATIBILITY_ADAPTER.setActivePierceModes(modes.toArray(new PierceMode[0]));
        }
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

    private final Map<BaseEnchantments, CustomEnchantment> baseToEnch;

    private final ChatColor curseColor;

    private final boolean enchantGlow;

    private final ChatColor enchantmentColor;

    private final double enchantRarity; // Overall rarity of obtaining enchantments

    private final boolean explosionBlockBreak; // Determines whether enchantment explosions cause world damage

    private final Map<Short, CustomEnchantment> idToEnch; // Since 1.0.0, changed signature in 3.0.0

    private final int maxEnchants; // Max number of Custom Enchantments on a tool

    private final Map<String, CustomEnchantment> nameToEnch;

    private final int shredDrops; // The setting (all, block, none) for shred drops

    private final Set<CustomEnchantment> worldEnchants; // Set of active Custom Enchantments

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
            switch (ENCH_CONFIG.mobStackerPlugin) {
            case STACKMOB_5:
                Bukkit.getPluginManager().registerEvents(new Stackmob5MergePreventer(), plugin);
                break;
            case ROSESTACKER:
                Bukkit.getPluginManager().registerEvents(new RosestackerMergePreventer(), plugin);
                break;
            case NONE:
            default:
                break;
            }
            registeredReveal = true;
        }
        allEnchants.addAll(worldEnchants);
    }

    /**
     * Obtains the enchantment that is backed by the BaseEnchantments enum.
     * Please note that the enchantment not be registered, which is why it may return null.
     * However this is not traditional behaviour, which is why it is annotated as not null.
     *
     * @param ench The enchantment
     * @return The enchantment instance valid in the world, or null
     * @since 2.1.1
     */
    public @NotNull CustomEnchantment enchantFromEnum(@NotNull BaseEnchantments ench) {
        return baseToEnch.get(ench);
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
     * Obtains an enchantment from it's name
     *
     * @param enchName The name of the enchantment
     * @return The instance of the enchantment, or null if unmapped
     * @since 1.0.0
     */
    public @Nullable CustomEnchantment enchantFromString(@NotNull String enchName) {
        return nameToEnch.get(ChatColor.stripColor(enchName.toLowerCase()));
    }

    // Returns whether enchant glow is enabled for custom enchantments
    public boolean enchantGlow() {
        return enchantGlow;
    }

    // Returns if certain enchantments can break blocks with the explosions they
    // create - only used by enchanted arrows as of yet (as they are the only ones to create explosions)
    public boolean explosionBlockBreak() {
        return explosionBlockBreak;
    }

    // Returns the color for curse lore
    public ChatColor getCurseColor() {
        return curseColor;
    }

    // Returns the color for enchantment lore
    public ChatColor getEnchantmentColor() {
        return enchantmentColor;
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

    // Returns the overall rarity of obtaining an enchantment
    public double getEnchantRarity() {
        return enchantRarity;
    }

    // Returns a mapping of enchantment names to custom enchantment objects
    public Set<CustomEnchantment> getEnchants() {
        return worldEnchants;
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

    /**
     * Returns the Name-Enchantment instance mappings of the world.
     *
     * @return A set of the entries of the map of registered enchantments
     * @since 1.0.0
     */
    public @NotNull Set<Map.Entry<String, CustomEnchantment>> getSimpleMappings() {
        return nameToEnch.entrySet();
    }

} enum ConfigKeys {
    /**
     * Denotes the Area of effect of an achievement, only applicable for enchantment that implements {@link AreaOfEffectable}.
     * If the key does not exist, but should then a value of 1 should be implied.
     *
     * @since 2.1.6
     */
    AREA_OF_EFFECT("Effect area modifier"),

    /**
     * The key to get the conflicts of an enchantment.
     * Unused until v4.0.0
     * It's value should be a String separated with commas, the individual strings should all represent a {@link BaseEnchantments}
     *
     * @since 2.2.2
     */
    CONFLICTS("Conflicts"),
    COOLDOWN("Cooldown"),
    ENCHANTMENTS("enchantments"),
    MAX_LEVEL("Max Level"),
    NAME("Name"),
    POWER("Power"),
    PROBABILITY("Probability"),
    TOOLS("Tools");

    private final String key;

    ConfigKeys(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return this.key;
    }
}

