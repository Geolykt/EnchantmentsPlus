package de.geolykt.enchantments_plus;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.BasicLoreGetter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.IEnchGatherer;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import static org.bukkit.Material.BOOK;
import static org.bukkit.Material.ENCHANTED_BOOK;

// CustomEnchantment is the default structure for any enchantment. Each enchantment below it will extend this class
//      and will override any methods as neccecary in its behavior
// Why do we even have a comparable interface?
public abstract class CustomEnchantment implements Comparable<CustomEnchantment> {

    protected static final CompatibilityAdapter ADAPTER = Storage.COMPATIBILITY_ADAPTER;
    public static IEnchGatherer Enchantment_Adapter = new BasicLoreGetter();

    protected int id;

    protected int maxLevel;         // Max level the given enchant can naturally obtain
    protected String loreName;      // Name the given enchantment will appear as; with &7 (Gray) color
    protected float probability;    // Relative probability of obtaining the given enchantment
    protected Tool[] enchantable;   // Enums that represent tools that can receive and work with given enchantment

    /**
     * The enchantments this enchantment is incompatible with.
     *  The values are simply dummy values and don't mean anything as the keys will be used, not they values.
     * @since 3.0.0 (or 1.0.0 with another signature)
     */
    protected EnumMap<BaseEnchantments, Object> conflicting; // FIXME use an EnumSet dummy
    protected String description; // Description of what the enchantment does

    /**
     * The cooldown of the enchantment in milliseconds.
     * Default is 0.
     * @since 3.0.0
     */
    protected int cooldownMillis;

    protected double power;         // Power multiplier for the enchantment's effects; Default is 0; -1 means no
    // effect
    protected Hand handUse;
    // Which hands an enchantment has actions for; 0 = none, 1 = left, 2 = right, 3 = both
    private boolean used;
    // Indicates that an enchantment has already been applied to an event, avoiding infinite regress
    protected boolean isCursed;
    protected NamespacedKey key; // The NamespacedKey for this enchantment which can be used for storage

    /**
     * The base of the enchantment used for comparing two CustomEnchantment instances with each other.
     * @since 3.0.0
     */
    protected final BaseEnchantments baseEnum;

    /**
     * Constructor.
     * @param enumRepresentation The BaseEnchantments enum that is returned in the asEnum() operation
     * @since 3.0.0
     */
    protected CustomEnchantment(@NotNull BaseEnchantments enumRepresentation) {
        baseEnum = enumRepresentation;
    }

    public abstract Builder<? extends CustomEnchantment> defaults();

    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        return false;
    }

    /**
     * Fired on the PlayerInteractEvent if the block the player clicked on is either null (air)
     * OR is not interactive (so things such as Workbenches, Beds and so on).
     * Since this is only fired for enchantments that need the enchantment be in hand, the
     * method is not called for enchantment applied to non-hand items (this was different before v3.0.0
     * where it was called for everything)
     * @param evt The event that caused the method to be called
     * @param level The level of the enchantment
     * @param usedHand The hand used for the event, true if it is the main hand, false otherwise
     * @return True if an action was performed, false otherwise (used for cooldowns and other actions)
     * @since 1.0.0
     */
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        return false;
    }

    /**
     * Fired on the PlayerInteractEvent if the block the player clicked is not null
     * AND the block is interactive (so things such as Workbenches, Beds and so on).
     * Since the event is only listened by the Spectral enchantment, the event will not be fired
     * for any enchantments existing on non-hand items (this is a change in behaviour
     * was introduced in v3.0.0 to patch a serious TPS Drain)
     * @param evt The event that caused the method to be called
     * @param level The level of the enchantment
     * @param usedHand The hand used for the event, true if it is the main hand, false otherwise
     * @return True if an action was performed, false otherwise (used for cooldowns and other actions)
     * @since 1.0.0
     */
    public boolean onBlockInteractInteractable(PlayerInteractEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityInteract(PlayerInteractEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityKill(EntityDeathEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onBeingHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityDamage(EntityDamageEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onPlayerFish(PlayerFishEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onHungerChange(FoodLevelChangeEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onShear(PlayerShearEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    /**
     * Reports to the enchantment that the shooting entity was shooting with it's bow.
     * The caller must make sure that evt.getProjectile() returns an instance of AbstractArrow
     * @param evt The event that was fired
     * @param level The level of the enchantment
     * @param usedHand The hand used in the event (true = mainhand, false = offhand)
     * @return True if the event was processed, false otherwise
     * @since 1.0.0
     */
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onPotionSplash(PotionSplashEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onScan(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onScanHands(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onCombust(EntityCombustByEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onFastScan(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onFastScanHands(Player player, int level, boolean usedHand) {
        return false;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public String getLoreName() {
        return loreName;
    }

    void setLoreName(String loreName) {
        this.loreName = loreName;
    }

    public float getProbability() {
        return probability;
    }

    void setProbability(float probability) {
        this.probability = probability;
    }

    Tool[] getEnchantable() {
        return enchantable;
    }

    void setEnchantable(Tool[] enchantable) {
        this.enchantable = enchantable;
    }

    /**
     * Obtains the conflicting enchantments (as an enum)
     * @return A set of enchantments the enchantment conflicts with
     * @since 3.0.0
     */
    public @NotNull Set<BaseEnchantments> getConflicts() {
        return null; // FIXME obvious NPE
    }

    void addConflict(BaseEnchantments conflict) {
        if (conflicting == null) {
            conflicting = new EnumMap<>(BaseEnchantments.class);
        }
        conflicting.put(conflict, (byte) 0);
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    /**
     * Obtains the cooldown of the enchantment after use.
     *  The cooldown is now in milliseconds
     * @return The current cooldown of the enchantment in milliseconds
     * @since 3.0.0
     */
    public int getCooldownMillis() {
        return cooldownMillis;
    }

    /**
     * Sets the cooldown that the enchantment should have after use.
     *  The cooldown is now in milliseconds.
     * @param cooldown The new cooldown in milliseconds
     * @since 3.0.0
     */
    void setCooldownMillis(int cooldown) {
        this.cooldownMillis = cooldown;
    }

    public double getPower() {
        return power;
    }

    void setPower(double power) {
        this.power = power;
    }

    Hand getHandUse() {
        return handUse;
    }

    void setHandUse(Hand handUse) {
        this.handUse = handUse;
    }

    /**
     * @deprecated Obtaining the legacyID via the BaseEnchantment enum is preferred
     * Obtains the legacy ID of the enchantment.
     * It's the way of differentiating and storing enchantments in Zenchantments and
     *  one (albeit not liked) way of differentiating enchantments in Enchantments+ 1.0.0 to 3.0.0
     * @return The ID of the enchantment
     * @since 1.0.0
     */
    @Deprecated(forRemoval = true, since = "3.0.0")
    public int getId() {
        return id;
    }

    /**
     * @deprecated The ID of the enchantment should not be altered
     * Set the legacy ID of the enchantment.
     * It's the way of differentiating and storing enchantments in Zenchantments and
     *  one (albeit not liked) way of differentiating enchantments in Enchantments+ 1.0.0 to 3.0.0
     * @param id The ID of the enchantment
     * @since 1.0.0
     */
    @Deprecated(forRemoval = true, since = "3.0.0")
    void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the Enum representation of the enchantment used for comparing
     * @return The BaseEnchantments enum attached to the enchantment
     * @since 1.1.0
     */
    public @NotNull BaseEnchantments asEnum() {
        return baseEnum;
    }

    @Override
    public int compareTo(CustomEnchantment o) {
        return baseEnum.compareTo(o.baseEnum);
    }

    public static void applyForTool(Player player, ItemStack tool, BiPredicate<CustomEnchantment, Integer> action) {
        getEnchants(tool, player.getWorld(), null).forEach((CustomEnchantment ench, Integer level) -> {
            if (!ench.used && Utilities.canUse(player, ench.baseEnum)) {
                try {
                    ench.used = true;
                    if (action.test(ench, level)) {
                        EnchantPlayer.setCooldown(player, ench.asEnum(), ench.cooldownMillis);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ench.used = false;
            }
        });
    }

    /**
     * Returns a mapping of custom enchantments and their level on a given tool
     * @param stk The itemstack that the operation applies for
     * @param world The world where the itemstack is located, used for configuration obtaining
     * @param outExtraLore The output of any unused lore will be written to this list, or null if not needed
     * @return A map of enchantments mapped to their level
     * @since 3.0.0
     */
    public static LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk,
            @NotNull World world,
            @Nullable List<String> outExtraLore) {
        return Enchantment_Adapter.getEnchants(stk, world, outExtraLore);
    }

    public static boolean hasEnchantment(@NotNull Config config, ItemStack stk, BaseEnchantments ench) {
        return Enchantment_Adapter.hasEnchantment(config, stk, ench);
    }

    public static int getEnchantLevel(@NotNull Config config, ItemStack stk, BaseEnchantments ench) {
        return Enchantment_Adapter.getEnchantmentLevel(config, stk, ench);
    }

    /**
     * Determines if the material provided is enchantable with this enchantment.
     *
     * @param m The material to test.
     *
     * @return true iff the material can be enchanted with this enchantment.
     */
    // Returns true if the given material (tool) is compatible with the enchantment, otherwise false
    public boolean validMaterial(Material m) {
        for (Tool t : enchantable) {
            if (t.contains(m)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the stack of material provided is enchantable with this
     * enchantment.
     *
     * @param m The stack of material to test.
     *
     * @return true iff the stack of material can be enchanted with this
     * enchantment.
     */
    public boolean validMaterial(ItemStack m) {
        return validMaterial(m.getType());
    }

    public String getShown(int level, World world) {
        String levelStr = Utilities.getRomanString(level);
        return (isCursed ? Config.get(world).getCurseColor() : Config.get(world).getEnchantmentColor()) + loreName
                + (maxLevel == 1 ? " " : " " + levelStr);
    }

    public void setEnchantment(ItemStack stk, int level, World world) {
        Enchantment_Adapter.setEnchantment(stk, this, level, world);
    }

    public static void setEnchantment(ItemStack stk, CustomEnchantment ench, int level, World world) {
        Enchantment_Adapter.setEnchantment(stk, ench, level, world);
    }

    /**
     * Makes the item in the hand glow, provided that the enchantment glow was enabled in the configuration
     * @param stk The itemstack that the glow should be valid for
     * @param customEnch True if a hidden enchantment should be applied if all odds fail
     * @param conf the configuration that the beforementioned check is valid for
     * @since 3.0.0
     */
    public static void setGlow(@NotNull ItemStack stk, boolean customEnch, @NotNull Config conf) {
        if (!conf.enchantGlow()) {
            return;
        }
        ItemMeta itemMeta = stk.getItemMeta();
        EnchantmentStorageMeta bookMeta = null;

        boolean isBook = stk.getType() == BOOK || stk.getType() == ENCHANTED_BOOK;

        boolean containsNormal = false;
        boolean containsHidden = false;
        int duraLevel = 0;
        Map<Enchantment, Integer> enchs;

        if (stk.getType() == ENCHANTED_BOOK) {
            bookMeta = (EnchantmentStorageMeta) stk.getItemMeta();
            enchs = bookMeta.getStoredEnchants();
        } else {
            enchs = itemMeta.getEnchants();
        }

        for (Map.Entry<Enchantment, Integer> set : enchs.entrySet()) {
            if (!(set.getKey().equals(Enchantment.DURABILITY) && (duraLevel = set.getValue()) == 0)) {
                containsNormal = true;
            } else {
                containsHidden = true;
            }
        }
        if (containsNormal || (!customEnch && containsHidden)) {
            if (stk.getType() == ENCHANTED_BOOK) {
                if (duraLevel == 0) {
                    bookMeta.removeStoredEnchant(org.bukkit.enchantments.Enchantment.DURABILITY);
                }
                bookMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                if (duraLevel == 0) {
                    itemMeta.removeEnchant(Enchantment.DURABILITY);
                }
                itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        } else if (customEnch) {
            if (stk.getType() == BOOK) {
                stk.setType(ENCHANTED_BOOK);
                bookMeta = (EnchantmentStorageMeta) stk.getItemMeta();
                bookMeta.addStoredEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 0, true);
                bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                itemMeta.addEnchant(Enchantment.DURABILITY, 0, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }

        stk.setItemMeta(isBook ? bookMeta : itemMeta);
    }

    /**
     * Obtains the key of the enchantment
     * @return
     */
    public NamespacedKey getKey() {
        return key;
    }

    protected static final class Builder<T extends CustomEnchantment> {

        private final T customEnchantment;

        public Builder(Supplier<T> sup, int id) {
            customEnchantment = sup.get();
            customEnchantment.setId(id);
            customEnchantment.key = new NamespacedKey(Storage.plugin, "ench." + id);
            setConflicts();
        }

        public Builder<T> maxLevel(int maxLevel) {
            customEnchantment.setMaxLevel(maxLevel);
            return this;
        }

        public Builder<T> loreName(String loreName) {
            customEnchantment.setLoreName(loreName);
            return this;
        }

        public String loreName() {
            return customEnchantment.getLoreName();
        }

        public Builder<T> probability(float probability) {
            customEnchantment.setProbability(probability);
            return this;
        }

        public float probability() {
            return customEnchantment.getProbability();
        }

        public Builder<T> enchantable(Tool[] enchantable) {
            customEnchantment.setEnchantable(enchantable);
            return this;
        }

        /**
         * Sets the enchantments this enchantment instance is incompatible with
         * @param conflicts The conflicts
         * @return The instance of the builder
         * @since 3.0.0
         */
        public Builder<T> setConflicts(BaseEnchantments... conflicts) {
            for (BaseEnchantments conflict : conflicts) {
                customEnchantment.addConflict(conflict);
            }
            return this;
        }

        public Builder<T> description(String description) {
            customEnchantment.setDescription(description);
            return this;
        }

        /**
         * The inserted cooldown is in milliseconds
         * @param cooldown The amount of cooldown to use
         * @return The current build instance
         * @since 3.0.0
         */
        public Builder<T> cooldownMillis(int cooldown) {
            customEnchantment.setCooldownMillis(cooldown);
            return this;
        }

        /**
         * Sets the power of the enchantment, this is a world-sensitive option
         * @param power The new power of the enchantment, it should be at 1.0f by default
         * @return The instance of the builder (for chaining)
         * @since 1.0.0
         */
        public Builder<T> power(double power) {
            customEnchantment.setPower(power);
            return this;
        }

        /**
         * Calls all the setters with the supplied arguments, this method also implies the power to be 1, so the power should be set afterwards
         * if needed.
         * @param description The description of the enchantment
         * @param enchantable The tools on which the enchantment can be applied on
         * @param lore The lore string (Usually the name of the enchantment)
         * @param maxlevel The maximum level the enchantment can be levelled
         * @param handUse Which hands the enchantments can be applied on
         * @param conflicts The Conflicting enchantments
         * @return The builder instance
         * @since 3.0.0
         */
        @SafeVarargs
        public final Builder<T> all(
                String description,
                Tool[] enchantable,
                String lore,
                int maxlevel,
                Hand handUse,
                BaseEnchantments... conflicts) {
            customEnchantment.setHandUse(handUse);
            setConflicts(conflicts);
            description(description);
            enchantable(enchantable);
            loreName(lore);
            maxLevel(maxlevel);
            power(1.0);
            return this;
        }

        public T build() {
            return customEnchantment;
        }
    }
}
