package de.geolykt.enchantments_plus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enchantments.*;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.evt.ench.ZenchantmentUseEvent;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Material.BOOK;
import static org.bukkit.Material.ENCHANTED_BOOK;

// CustomEnchantment is the defualt structure for any enchantment. Each enchantment below it will extend this class
//      and will override any methods as neccecary in its behavior
// Why do we even have a comparable interface?
public abstract class CustomEnchantment implements Comparable<CustomEnchantment> {

    protected static final CompatibilityAdapter ADAPTER = Storage.COMPATIBILITY_ADAPTER;
    public static IEnchGatherer Enchantment_Adapter = new ProvisionalLoreGatherer();
    
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

    String getLoreName() {
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

    // Updates lore enchantments and descriptions to new format. This will be removed eventually
    @Deprecated
    public static ItemStack updateToNewFormat(ItemStack stk, World world) {
        if (stk != null) {
            if (stk.hasItemMeta()) {
                if (stk.getItemMeta().hasLore()) {
                    boolean hasEnch = false;
                    ItemMeta meta = stk.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    CustomEnchantment lastEnch = null;

                    List<String> tempLore = new LinkedList<>();
                    for (String str : meta.getLore()) {

                        CustomEnchantment ench = null;
                        int level = 0;
                        if (str.startsWith(ChatColor.GRAY + "")) {
                            String stripString = ChatColor.stripColor(str);

                            int splitIndex = stripString.lastIndexOf(" ");
                            if (splitIndex != -1) {
                                if (stripString.length() > 2) {
                                    String enchant;
                                    level = Utilities.getNumber(stripString.substring(splitIndex + 1));
                                    try {
                                        enchant = stripString.substring(0, splitIndex);
                                    } catch (Exception e) {
                                        enchant = "";
                                    }
                                    ench = Config.get(world).enchantFromString(enchant);
                                }
                            }
                        }

                        if (ench != null) {
                            lastEnch = ench;
                            hasEnch = true;
                            lore.add(ench.getShown(level, world));
                            lore.addAll(tempLore);
                            tempLore.clear();
                            continue;
                        }

                        if (lastEnch != null) {
                            tempLore.add(str);

                            StringBuilder bldr = new StringBuilder();
                            for (String ls : tempLore) {
                                bldr.append(ChatColor.stripColor(ls));
                            }
                            if (lastEnch.description.equals(bldr.toString())) {
                                lastEnch = null;
                                tempLore.clear();
                            }
                        } else {
                            lore.add(str);
                        }
                    }
                    lore.addAll(tempLore);

                    meta.setLore(lore);
                    stk.setItemMeta(meta);
                    if (hasEnch) {
                        setGlow(stk, true, world);
                    }
                    return stk;
                }
            }
        }
        return stk;
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

    public List<String> getDescription(World world) {
        List<String> desc = new LinkedList<>();
        if (Config.get(world).descriptionLore()) {
            String strStart = Utilities.toInvisibleString("ze.desc." + getId())
                    + Config.get(world).getDescriptionColor() + "" + ChatColor.ITALIC + " ";
            StringBuilder bldr = new StringBuilder();

            int i = 0;
            for (char c : description.toCharArray()) {
                if (i < 30) {
                    i++;
                    bldr.append(c);
                } else {
                    if (c == ' ') {
                        desc.add(strStart + bldr.toString());
                        bldr = new StringBuilder(" ");
                        i = 1;
                    } else {
                        bldr.append(c);
                    }
                }
            }
            if (i != 0) {
                desc.add(strStart + bldr.toString());
            }
        }
        return desc;
    }

    public static boolean isDescription(String str) {
        Map<String, Boolean> unescaped = Utilities.fromInvisibleString(str);
        for (Map.Entry<String, Boolean> entry : unescaped.entrySet()) {
            if (!entry.getValue()) {
                String[] vals = entry.getKey().split("\\.");
                if (vals.length == 3 && vals[0].equals("ze") && vals[1].equals("desc")) {
                    return true;
                }
            }
        }
        return false;
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

    protected static final class Builder<T extends CustomEnchantment> {

        private final T customEnchantment;

        public Builder(Supplier<T> sup, int id) {
            customEnchantment = sup.get();
            customEnchantment.setId(id);
            customEnchantment.key = new NamespacedKey(Storage.enchantments_plus, "ench." + id);
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
    public static interface IEnchGatherer {
    	// Returns a mapping of custom enchantments and their level on a given tool
        public abstract LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world,
                List<String> outExtraLore);

        // Returns a mapping of custom enchantments and their level on a given tool
        public abstract LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks,
                World world);

        // Returns a mapping of custom enchantments and their level on a given tool
        public abstract LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world);

        public abstract LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks,
                World world,
                List<String> outExtraLore);
        public abstract void setEnchantment(ItemStack stk, CustomEnchantment ench, int level, World world);
    }
    
    /**
     * The legacy Adapter for gathering Enchantments used up until 1.16
     */
    static class LegacyLoreGatherer implements IEnchGatherer {
    	// Returns a mapping of custom enchantments and their level on a given tool
        @Override
        public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world,
                List<String> outExtraLore) {
            return getEnchants(stk, false, world, outExtraLore);
        }

        // Returns a mapping of custom enchantments and their level on a given tool
        @Override
        public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks,
                World world) {
            return getEnchants(stk, acceptBooks, world, null);
        }

        // Returns a mapping of custom enchantments and their level on a given tool
        @Override
        public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world) {
            return getEnchants(stk, false, world, null);
        }

        @Override
        public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks,
                World world,
                List<String> outExtraLore) {
            Map<CustomEnchantment, Integer> map = new LinkedHashMap<>();
            if (stk != null && (acceptBooks || stk.getType() != Material.ENCHANTED_BOOK)) {
                if (stk.hasItemMeta()) {
                    if (stk.getItemMeta().hasLore()) {
                        List<String> lore = stk.getItemMeta().getLore();
                        for (String raw : lore) {
                            Map.Entry<CustomEnchantment, Integer> ench = getEnchant(raw, world);
                            if (ench != null) {
                                map.put(ench.getKey(), ench.getValue());
                            } else {
                                if (outExtraLore != null) {
                                    outExtraLore.add(raw);
                                }
                            }
                        }
                    }
                }
            }
            LinkedHashMap<CustomEnchantment, Integer> finalMap = new LinkedHashMap<>();
            for (int id : new int[]{Lumber.ID, Shred.ID, Mow.ID, Pierce.ID, Extraction.ID, Plough.ID}) {
                CustomEnchantment e = null;
                for (CustomEnchantment en : Config.allEnchants) {
                    if (en.getId() == id) {
                        e = en;
                    }
                }
                if (map.containsKey(e)) {
                    finalMap.put(e, map.get(e));
                    map.remove(e);
                }
            }
            finalMap.putAll(map);
            return finalMap;
        }

        // Returns the custom enchantment from the lore name
        private Map.Entry<CustomEnchantment, Integer> getEnchant(String raw, World world) {
            raw = raw.replaceAll("(" + ChatColor.COLOR_CHAR + ".)", "").trim();
            switch (raw.split(" ").length) {
            case 0:
                return null; // Invalid length, don't tell me otherwise
            case 1:
                CustomEnchantment enchant = Config.get(world).enchantFromString(raw);
                if (enchant == null) {
                    return null; // Not able to map enchantment
                } else {
                    return new SimpleEntry<CustomEnchantment, Integer>(enchant, 1);
                }
            case 2:
                CustomEnchantment ench = Config.get(world).enchantFromString(raw.split(" ")[0]);
                if (ench == null) {
                    ench = Config.get(world).enchantFromString(raw.replace(" ", "")); // In case of nightvision
                    if (ench == null)
                        return null; // Not able to map enchantment
                    else
                        return new SimpleEntry<>(ench, 1);
                }
                try {
                    return new AbstractMap.SimpleEntry<CustomEnchantment, Integer>(ench,
                            Utilities.getNumber(raw.split(" ")[1]));
                } catch (NumberFormatException expected){
                    return null; // Invalid roman numeral
                }
            case 3:
                CustomEnchantment ench2 = Config.get(world).enchantFromString(raw.split(" ")[0] +
                        raw.split(" ")[1]);
                if (ench2 == null) {
                    return null; // Not able to map enchantment
                }
                try {
                    return new AbstractMap.SimpleEntry<CustomEnchantment, Integer>(ench2,
                            Utilities.getNumber(raw.split(" ")[2]));
                } catch (NumberFormatException expected){
                    return null; // Invalid roman numeral
                }
            default:
                return null; // Invalid length
            }
        }
        
        @Override
        public void setEnchantment(ItemStack stk, CustomEnchantment ench, int level, World world) {
            if (stk == null) {
                return;
            }
            ItemMeta meta = stk.getItemMeta();
            List<String> lore = new LinkedList<>();
            List<String> normalLore = new LinkedList<>();
            boolean customEnch = false;
            if (meta.hasLore()) {
                for (String loreStr : meta.getLore()) {
                    Map.Entry<CustomEnchantment, Integer> enchEntry = getEnchant(loreStr, world);
                    if (enchEntry == null && !isDescription(loreStr)) {
                        normalLore.add(loreStr);
                    } else if (enchEntry != null && enchEntry.getKey() != ench) {
                        customEnch = true;
                        lore.add(enchEntry.getKey().getShown(enchEntry.getValue(), world));
                        lore.addAll(enchEntry.getKey().getDescription(world));
                    }
                }
            }

            if (ench != null && level > 0 && level <= ench.maxLevel) {
                lore.add(ench.getShown(level, world));
                lore.addAll(ench.getDescription(world));
                customEnch = true;
            }

            lore.addAll(normalLore);
            meta.setLore(lore);
            stk.setItemMeta(meta);

            if (customEnch && stk.getType() == BOOK) {
                stk.setType(ENCHANTED_BOOK);
            }

            setGlow(stk, customEnch, world);
        }
    }

    /**
     * The Enchantment gatherer used by <a href="https://github.com/Geolykt/NMSless-Enchantments_plus">
     *  Geolykt's NMSless-Enchantments_plus </a>, the implementation uses Persistent Data to store
     *  it's data. It is modified to be backwards compatible
     */
    static class PersistentDataGatherer implements IEnchGatherer {
        private LegacyLoreGatherer legacyGatherer = new LegacyLoreGatherer();
        private final boolean doCompat;
        private final Collection<Material> getterDenyList;
        private final boolean isGetterAllowlist; // Defines whether the above collection should be used as an allowlist instead

        /**
         * Used for enchantment conversion purposes
         */
        public final NamespacedKey ench_converted;

        public PersistentDataGatherer(Collection<Material> denylist, boolean allowlistToggle, boolean doCompat2) {
            ench_converted = new NamespacedKey(Storage.enchantments_plus, "e_convert");
            getterDenyList = denylist;
            isGetterAllowlist = allowlistToggle;
            doCompat = doCompat2;
        }

        @Override
        public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world,
                List<String> outExtraLore) {
            return getEnchants(stk, false, world, outExtraLore);
        }

        @Override
        public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks, World world) {
            return getEnchants(stk, acceptBooks, world, null);
        }

        @Override
        public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world) {
            return getEnchants(stk, false, world, null);
        }

        @Override
        public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks, World world,
                List<String> outExtraLore) {
            LinkedHashMap<CustomEnchantment, Integer> map = new LinkedHashMap<>();
            if ( (stk != null && stk.getType() != Material.AIR) && (acceptBooks || stk.getType() != Material.ENCHANTED_BOOK)) {
                if (stk.hasItemMeta()) {
                    //TODO what would be the best approach to remove the nesting in the two conditions? Ideally in a single if clause
                    if (isGetterAllowlist) {
                        if (!getterDenyList.contains(stk.getType())) {
                            return new LinkedHashMap<>();
                        }
                    } else {
                        if (getterDenyList.contains(stk.getType())) {
                            return new LinkedHashMap<>();
                        }
                    }
                    final PersistentDataContainer cont = stk.getItemMeta().getPersistentDataContainer();

                    if (doCompat && cont.getOrDefault(ench_converted, PersistentDataType.BYTE, (byte) 0) == 0) {
                        //Legacy conversion
                        map = legacyGatherer.getEnchants(stk, acceptBooks, world, outExtraLore);
                        for (Map.Entry<CustomEnchantment, Integer> ench : map.entrySet()) {
                            this.setEnchantment(stk, ench.getKey(), ench.getValue(), world);
                        }
                        ItemMeta itemMeta = stk.getItemMeta();
                        itemMeta.getPersistentDataContainer().set(ench_converted, PersistentDataType.BYTE, (byte) 1);
                        stk.setItemMeta(itemMeta);
                        return map;
                    }

                    Set<NamespacedKey> keys = cont.getKeys();

                    for (NamespacedKey key : keys) {
                        if (!key.getNamespace().toLowerCase(Locale.ROOT).equals("enchantments_plus")) {
                            continue;
                        }
                        if (!key.getKey().split("\\.")[0].equals("ench")) {
                            continue;
                        }

                        Integer level = (int) cont.getOrDefault(key, PersistentDataType.SHORT, (short) 0);
                        Short id = Short.decode(key.getKey().split("\\.")[1]);
                        CustomEnchantment ench = Config.get(world).enchantFromID(id);
                        if (ench == null) {
                            continue;
                        }
                        map.put(ench, level);
                    }
                }
            }

            LinkedHashMap<CustomEnchantment, Integer> finalMap = new LinkedHashMap<>();
            for (int id : new int[] { Lumber.ID, Shred.ID, Mow.ID, Pierce.ID, Extraction.ID, Plough.ID }) {
                CustomEnchantment e = null;
                for (CustomEnchantment en : Config.allEnchants) {
                    if (en.getId() == id) {
                        e = en;
                    }
                }
                if (map.containsKey(e)) {
                    finalMap.put(e, map.get(e));
                    map.remove(e);
                }
            }
            finalMap.putAll(map);
            return finalMap;
        }

        @Override
        public void setEnchantment(ItemStack stk, CustomEnchantment ench, int level, World world) {
            if (stk == null) {
                return;
            }
            ItemMeta meta = stk.getItemMeta();
            List<String> lore = new LinkedList<>();
            if (meta.hasLore()) {
                for (String loreStr : meta.getLore()) {
                    if (!loreStr.toLowerCase(Locale.ENGLISH).contains(ench.loreName.toLowerCase(Locale.ENGLISH))) {
                        lore.add(loreStr);
                    }
                }
            }

            if (ench != null && level > 0 && level <= ench.maxLevel) {
                meta.getPersistentDataContainer().set(ench.key, PersistentDataType.SHORT, (short) level);
                lore.add(ench.getShown(level, world));
            }
        
            //Disenchant item
            if (ench != null &&
                    level <= 0 &&
                    meta.getPersistentDataContainer().has(ench.key, PersistentDataType.SHORT)) {
                meta.getPersistentDataContainer().remove(ench.key);
            }
            
            meta.setLore(lore);
            stk.setItemMeta(meta);
        
            if (stk.getType() == BOOK) {
                stk.setType(ENCHANTED_BOOK);
            }

            setGlow(stk, true, world);
        }
    }
    
    
    /**
     * The upstream's implementation of handling enchantments post 1.16
     */
    static class ProvisionalLoreGatherer implements IEnchGatherer {

        private static final Pattern ENCH_LORE_PATTERN = Pattern.compile("§[a-fA-F0-9]([^§]+?)(?:$| $| (I|II|III|IV|V|VI|VII|VIII|IX|X)$)");
        
        // Returns a mapping of custom enchantments and their level on a given tool
        @Override
        public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world,
                List<String> outExtraLore) {
            return getEnchants(stk, false, world, outExtraLore);
        }

        // Returns a mapping of custom enchantments and their level on a given tool
        @Override
        public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks,
                World world) {
            return getEnchants(stk, acceptBooks, world, null);
        }

        // Returns a mapping of custom enchantments and their level on a given tool
        @Override
        public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world) {
            return getEnchants(stk, false, world, null);
        }

        @Override
        public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks,
                World world,
                List<String> outExtraLore) {
            Map<CustomEnchantment, Integer> map = new LinkedHashMap<>();
            if (stk != null && (acceptBooks || stk.getType() != Material.ENCHANTED_BOOK)) {
                if (stk.hasItemMeta()) {
                    if (stk.getItemMeta().hasLore()) {
                        List<String> lore = stk.getItemMeta().getLore();
                        for (String raw : lore) {
                            Map.Entry<CustomEnchantment, Integer> ench = getEnchant(raw, world);
                            if (ench != null) {
                                map.put(ench.getKey(), ench.getValue());
                            } else {
                                if (outExtraLore != null) {
                                    outExtraLore.add(raw);
                                }
                            }
                        }
                    }
                }
            }
            LinkedHashMap<CustomEnchantment, Integer> finalMap = new LinkedHashMap<>();
            for (int id : new int[]{Lumber.ID, Shred.ID, Mow.ID, Pierce.ID, Extraction.ID, Plough.ID}) {
                CustomEnchantment e = null;
                for (CustomEnchantment en : Config.allEnchants) {
                    if (en.getId() == id) {
                        e = en;
                    }
                }
                if (map.containsKey(e)) {
                    finalMap.put(e, map.get(e));
                    map.remove(e);
                }
            }
            finalMap.putAll(map);
            return finalMap;
        }

        // Returns the custom enchantment from the lore name
        private Map.Entry<CustomEnchantment, Integer> getEnchant(String raw, World world) {
            Matcher m = ENCH_LORE_PATTERN.matcher(raw);
            if (!m.find()) {
                return null;
            }

            String enchName = m.group(1);
            enchName = ChatColor.stripColor(enchName);
            int enchLvl = m.group(2) == null || m.group(2).equals("") ? 1 : Utilities.getNumber(m.group(2));

            CustomEnchantment ench = Config.get(world).enchantFromString(enchName);
            if (ench == null) {
                return null;
            }

            return new AbstractMap.SimpleEntry<>(ench, enchLvl);
        }
        
        @Override
        public void setEnchantment(ItemStack stk, CustomEnchantment ench, int level, World world) {
            if (stk == null) {
                return;
            }
            ItemMeta meta = stk.getItemMeta();
            List<String> lore = new LinkedList<>();
            List<String> normalLore = new LinkedList<>();
            boolean customEnch = false;
            if (meta.hasLore()) {
                for (String loreStr : meta.getLore()) {
                    Map.Entry<CustomEnchantment, Integer> enchEntry = getEnchant(loreStr, world);
                    if (enchEntry == null && !isDescription(loreStr)) {
                        normalLore.add(loreStr);
                    } else if (enchEntry != null && enchEntry.getKey() != ench) {
                        customEnch = true;
                        lore.add(enchEntry.getKey().getShown(enchEntry.getValue(), world));
                        lore.addAll(enchEntry.getKey().getDescription(world));
                    }
                }
            }

            if (ench != null && level > 0 && level <= ench.maxLevel) {
                lore.add(ench.getShown(level, world));
                lore.addAll(ench.getDescription(world));
                customEnch = true;
            }

            lore.addAll(normalLore);
            meta.setLore(lore);
            stk.setItemMeta(meta);

            if (customEnch && stk.getType() == BOOK) {
                stk.setType(ENCHANTED_BOOK);
            }

            setGlow(stk, customEnch, world);
        }
    }
}
