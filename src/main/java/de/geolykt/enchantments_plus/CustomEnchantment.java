package de.geolykt.enchantments_plus;

import org.bukkit.Bukkit;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import de.geolykt.enchantments_plus.annotations.AsyncSafe;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.BasicLoreGetter;
import de.geolykt.enchantments_plus.compatibility.enchantmentgetters.IEnchGatherer;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.evt.ench.AsyncZenchantmentUseEvent;
import de.geolykt.enchantments_plus.evt.ench.ZenchantmentUseEvent;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import static org.bukkit.Material.BOOK;
import static org.bukkit.Material.ENCHANTED_BOOK;

// CustomEnchantment is the defualt structure for any enchantment. Each enchantment below it will extend this class
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
    protected Set<Class<? extends CustomEnchantment>> conflicting; // Classes of enchantments that don't work with given enchantment
    protected String description;   // Description of what the enchantment does
    protected int cooldown;         // Cooldown for given enchantment given in ticks; Default is 0
    protected double power;         // Power multiplier for the enchantment's effects; Default is 0; -1 means no
    // effect
    protected Hand handUse;
    // Which hands an enchantment has actions for; 0 = none, 1 = left, 2 = right, 3 = both
    private boolean used;
    // Indicates that an enchantment has already been applied to an event, avoiding infinite regress
    protected boolean isCursed;
    protected NamespacedKey key; // The NamespacedKey for this enchantment which can be used for storage
    protected BaseEnchantments base; // The base of the enchantment

    public abstract Builder<? extends CustomEnchantment> defaults();

    //region Enchanment Events
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        return false;
    }

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

    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onPotionSplash(PotionSplashEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onPlayerDeath(PlayerDeathEvent evt, int level, boolean usedHand) {
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

    @AsyncSafe
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onFastScanHands(Player player, int level, boolean usedHand) {
        return false;
    }

    //endregion
    //region Getters and Setters
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

    public Set<Class<? extends CustomEnchantment>> getConflicting() {
        return conflicting;
    }

    void setConflicting(Set<Class<? extends CustomEnchantment>> conflicts) {
        this.conflicting = conflicts;
    }
    
    void addConflicting(Class<? extends CustomEnchantment> conflict) {
        if (conflicting == null) {
            conflicting = new HashSet<Class<? extends CustomEnchantment>>();
        }
        conflicting.add(conflict);
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public int getCooldown() {
        return cooldown;
    }

    void setCooldown(int cooldown) {
        this.cooldown = cooldown;
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

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public BaseEnchantments asEnum() {
        return base;
    }
    
    void setBase(BaseEnchantments baseEnchant) {
        base = baseEnchant;
    }
    
    @Override
    public int compareTo(CustomEnchantment o) {
        return this.getLoreName().compareTo(o.getLoreName());
    }

    //endregion
    public static void applyForTool(Player player, ItemStack tool, BiPredicate<CustomEnchantment, Integer> action) {    
        getEnchants(tool, player.getWorld()).forEach((CustomEnchantment ench, Integer level) -> {
            if (!ench.used && Utilities.canUse(player, ench.id)) {
                try {
                    ench.used = true;
                    if (action.test(ench, level)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.id, ench.cooldown);
                        final ZenchantmentUseEvent evt = new ZenchantmentUseEvent(player, EquipmentSlot.HAND, ench, level);
                        Bukkit.getPluginManager().callEvent(evt);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ench.used = false;
            }
        });
    }

    /**
     * Asynchronously applies the enchantments on a tool, please beware that the action should be also asynchronously, otherwise
     * the asyncSafety is not provided
     * @param player The player that is the target of the application of enchantment
     * @param tool The item that is the target of the application of the enchantment
     * @param action The action that should be performed
     * @since 2.1.1@fast-async
     */
    @AsyncSafe
    public static void applyForToolAsync(Player player, ItemStack tool, BiPredicate<CustomEnchantment, Integer> action) {    
        getEnchants(tool, player.getWorld()).forEach((CustomEnchantment ench, Integer level) -> {
            if (!ench.used && Utilities.canUse(player, ench.id)) {
                try {
                    ench.used = true;
                    if (action.test(ench, level)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.id, ench.cooldown);
                        final AsyncZenchantmentUseEvent evt = new AsyncZenchantmentUseEvent(player, EquipmentSlot.HAND, ench, level);
                        Bukkit.getPluginManager().callEvent(evt);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ench.used = false;
            }
        });
    }

    // Returns a mapping of custom enchantments and their level on a given tool
    public static LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world,
            List<String> outExtraLore) {
    	return Enchantment_Adapter.getEnchants(stk, world, outExtraLore);
    }

    // Returns a mapping of custom enchantments and their level on a given tool
    public static LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks,
            World world) {
        return Enchantment_Adapter.getEnchants(stk, acceptBooks, world, null);
    }

    // Returns a mapping of custom enchantments and their level on a given tool
    public static LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world) {
        return Enchantment_Adapter.getEnchants(stk, false, world, null);
    }

    public static LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks,
            World world,
            List<String> outExtraLore) {
        return Enchantment_Adapter.getEnchants(stk, acceptBooks, world, outExtraLore);
    }

    public static boolean hasEnchantment(Config config, ItemStack stk, BaseEnchantments ench) {
        return Enchantment_Adapter.hasEnchantment(config, stk, ench);
    }

    public static int getEnchantLevel(Config config, ItemStack stk, BaseEnchantments ench) {
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

    public static void setGlow(ItemStack stk, boolean customEnch, World world) {
        if (Config.get(world) == null || !Config.get(world).enchantGlow()) {
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

    public NamespacedKey getKey() {
        return key;
    }

    protected static final class Builder<T extends CustomEnchantment> {

        private final T customEnchantment;

        public Builder(Supplier<T> sup, int id) {
            customEnchantment = sup.get();
            customEnchantment.setId(id);
            customEnchantment.key = new NamespacedKey(Storage.plugin, "ench." + id);
        }

        public Builder<T> maxLevel(int maxLevel) {
            customEnchantment.setMaxLevel(maxLevel);
            return this;
        }

        public int maxLevel() {
            return customEnchantment.getMaxLevel();
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

        public Tool[] enchantable() {
            return customEnchantment.getEnchantable();
        }

        public Builder<T> conflicting(Set<Class<? extends CustomEnchantment>> conflicts) {
            customEnchantment.setConflicting(conflicts);
            return this;
        }

        //I hope that the final modifier doesn't end up making any issues.
        @SafeVarargs //The vararg in this method can be generally be considered safe
        public final Builder<T> conflicting(Class<? extends CustomEnchantment>... conflicts) {
            for (Class<? extends CustomEnchantment> ce : conflicts) {
                customEnchantment.addConflicting(ce);
            }
            return this;
        }
        
        public Set<Class<? extends CustomEnchantment>> getConflicting() {
            return customEnchantment.getConflicting();
        }
        
        public Builder<T> conflicting() {
            customEnchantment.setConflicting(new HashSet<Class<? extends CustomEnchantment>>());
            return this;
        }
        
        public Builder<T> description(String description) {
            customEnchantment.setDescription(description);
            return this;
        }

        public String description() {
            return customEnchantment.getDescription();
        }

        public Builder<T> cooldown(int cooldown) {
            customEnchantment.setCooldown(cooldown);
            return this;
        }

        public int cooldown() {
            return customEnchantment.getCooldown();
        }

        public Builder<T> power(double power) {
            customEnchantment.setPower(power);
            return this;
        }

        public double power() {
            return customEnchantment.getPower();
        }

        public Builder<T> handUse(Hand handUse) {
            customEnchantment.setHandUse(handUse);
            return this;
        }

        public Hand handUse() {
            return customEnchantment.getHandUse();
        }

        public Builder<T> base(BaseEnchantments base) {
            customEnchantment.setBase(base);
            return this;
        }
        
        public T build() {
            return customEnchantment;
        }
    }
}
