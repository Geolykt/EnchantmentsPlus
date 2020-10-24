package de.geolykt.enchantments_plus.evt;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enchantments.Unrepairable;

import java.util.*;
import java.util.Map.Entry;

import static org.bukkit.Material.ENCHANTED_BOOK;
import static org.bukkit.event.EventPriority.MONITOR;

/**
 * This class manages the combination of enchantments in an anvil. It takes into account conflicting enchantments, 
 *  the max number of enchantments per tool, and the enchantment's max level. It shuffles the results every time
 *  so that the player can find the combination they desire when there are conflicting or too many enchantment
 * @deprecated The second generation of the class is now used and this generation will be removed in the v3.0.0 refractor.
 * @since 1.0.0
 */
@Deprecated
public class AnvilMerge implements Listener {

    /**
     * Simulates an anvil merge which runs similar to {@link #doMerge(ItemStack, ItemStack, ItemStack, Config)} but can run without an output
     * item, at the expense of lesser compatibility.
     * @param leftItem The left input item
     * @param rightItem The right input item
     * @param config The WorldConfiguration that should be used
     * @return The expected output item
     * @since 2.0.0
     */
    public ItemStack simulateMerge(ItemStack leftItem, ItemStack rightItem, Config config) {
        if (leftItem == null || rightItem == null) {
            return null;
        }
        if (leftItem.getType() == Material.AIR || rightItem.getType() == Material.AIR) {
            return null;
        }

        List<String> normalLeftLore = new ArrayList<>();
        Map<CustomEnchantment, Integer> leftEnchantments =
            CustomEnchantment.getEnchants(leftItem, true, config.getWorld(), normalLeftLore);
        Map<CustomEnchantment, Integer> rightEnchantments =
            CustomEnchantment.getEnchants(rightItem, true, config.getWorld());

        for (CustomEnchantment e : leftEnchantments.keySet()) {
            if (e.getId() == Unrepairable.ID) {
                return new ItemStack(Material.AIR);
            }
        }
        for (CustomEnchantment e : rightEnchantments.keySet()) {
            if (e.getId() == Unrepairable.ID) {
                return new ItemStack(Material.AIR);
            }
        }
        if(leftEnchantments.isEmpty() && rightEnchantments.isEmpty()) {
            return null;
        }
        
        EnchantmentPool pool = new EnchantmentPool(leftItem, config.getMaxEnchants());
        pool.addAll(leftEnchantments);
        List<Entry<CustomEnchantment, Integer>> rightEnchantmentList = new ArrayList<>(rightEnchantments.entrySet());
        Collections.shuffle(rightEnchantmentList);
        pool.addAll(rightEnchantmentList);
        HashMap<CustomEnchantment, Integer> outEnchantments = pool.getEnchantmentMap();

        ItemStack newOutItem = new ItemStack(leftItem);
        ItemMeta meta = leftItem.getItemMeta();
        meta.setLore(new ArrayList<>());
        newOutItem.setItemMeta(meta);

        for (Entry<CustomEnchantment, Integer> enchantEntry : outEnchantments.entrySet()) {
            enchantEntry.getKey().setEnchantment(newOutItem, enchantEntry.getValue(), config.getWorld());
        }

        ItemMeta newOutMeta = newOutItem.getItemMeta();
        List<String> outLore = newOutMeta.hasLore() ? newOutMeta.getLore() : new ArrayList<>();
        outLore.addAll(normalLeftLore);

        newOutMeta.setLore(outLore);
        newOutItem.setItemMeta(newOutMeta);

        CustomEnchantment.setGlow(newOutItem, !outEnchantments.isEmpty(), config.getWorld());

        return newOutItem;
        
    }

    /**
     * Applies the enchantment of the input Items on the output Item.
     * @param leftItem The left input item
     * @param rightItem The right input item
     * @param oldOutItem The item that should be the base of the output item
     * @param config The WorldConfiguration that should be used
     * @return The expected output item
     * @since 1.0
     */
    public ItemStack doMerge(ItemStack leftItem, ItemStack rightItem, ItemStack oldOutItem, Config config) {
        if (leftItem == null || rightItem == null || oldOutItem == null) {
            return null;
        }
        if (leftItem.getType() == Material.AIR || rightItem.getType() == Material.AIR
            || oldOutItem.getType() == Material.AIR) {
            return null;
        }
        if (!oldOutItem.hasItemMeta()) {
            return null;
        }

        List<String> normalLeftLore = new ArrayList<>();
        Map<CustomEnchantment, Integer> leftEnchantments =
            CustomEnchantment.getEnchants(leftItem, true, config.getWorld(), normalLeftLore);
        Map<CustomEnchantment, Integer> rightEnchantments =
            CustomEnchantment.getEnchants(rightItem, true, config.getWorld());

        for (CustomEnchantment e : leftEnchantments.keySet()) {
            if (e.getId() == Unrepairable.ID) {
                return new ItemStack(Material.AIR);
            }
        }
        for (CustomEnchantment e : rightEnchantments.keySet()) {
            if (e.getId() == Unrepairable.ID) {
                return new ItemStack(Material.AIR);
            }
        }
        if(leftEnchantments.isEmpty() && rightEnchantments.isEmpty()) {
            return oldOutItem;
        }
        
        EnchantmentPool pool = new EnchantmentPool(oldOutItem, config.getMaxEnchants());
        pool.addAll(leftEnchantments);
        List<Entry<CustomEnchantment, Integer>> rightEnchantmentList = new ArrayList<>(rightEnchantments.entrySet());
        Collections.shuffle(rightEnchantmentList);
        pool.addAll(rightEnchantmentList);
        HashMap<CustomEnchantment, Integer> outEnchantments = pool.getEnchantmentMap();

        ItemStack newOutItem = new ItemStack(oldOutItem);
        ItemMeta meta = oldOutItem.getItemMeta();
        meta.setLore(new ArrayList<>());
        newOutItem.setItemMeta(meta);

        for (Entry<CustomEnchantment, Integer> enchantEntry : outEnchantments.entrySet()) {
            enchantEntry.getKey().setEnchantment(newOutItem, enchantEntry.getValue(), config.getWorld());
        }

        ItemMeta newOutMeta = newOutItem.getItemMeta();
        List<String> outLore = newOutMeta.hasLore() ? newOutMeta.getLore() : new ArrayList<>();
        outLore.addAll(normalLeftLore);
        newOutMeta.setLore(outLore);
        newOutItem.setItemMeta(newOutMeta);

        CustomEnchantment.setGlow(newOutItem, !outEnchantments.isEmpty(), config.getWorld());

        return newOutItem;
    }

    @EventHandler(priority = MONITOR)
    public void onClicks(final InventoryClickEvent evt) {
        if (evt.getInventory().getType() != InventoryType.ANVIL || !evt.getClick().isLeftClick()) {
            return;
        }
        if (evt.getCurrentItem() != null && evt.getCurrentItem().getType() == ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) evt.getCurrentItem().getItemMeta();
            if (bookMeta.getStoredEnchants().containsKey(org.bukkit.enchantments.Enchantment.DURABILITY)
                && bookMeta.getStoredEnchants().get(org.bukkit.enchantments.Enchantment.DURABILITY) == 0) {
                bookMeta.removeStoredEnchant(org.bukkit.enchantments.Enchantment.DURABILITY);
                evt.getCurrentItem().setItemMeta(bookMeta);
            }
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClicks(final PrepareAnvilEvent evt) {
        if (evt.getViewers().size() < 1) {
            return;
        }

        final Config config = Config.get(evt.getViewers().get(0).getWorld());
        final AnvilInventory anvilInv = evt.getInventory();

        ItemStack outItem = anvilInv.getItem(2);
        ItemStack stack;
        if (outItem == null) {
            stack = simulateMerge(anvilInv.getItem(0),  anvilInv.getItem(1), config);
            evt.getInventory().setRepairCost(CustomEnchantment.getEnchants(stack, config.getWorld()).size() * 3);
        } else {
            stack = doMerge(anvilInv.getItem(0),  anvilInv.getItem(1), outItem, config);
        }

        if (stack != null) {
            evt.setResult(stack);
        }
    }

    private class EnchantmentPool {

        private final HashMap<CustomEnchantment, Integer> enchantPool = new HashMap<>();
        private final ItemStack                           is;
        private final int                                 maxCapacity;

        public EnchantmentPool(ItemStack base, int maxCapacity) {
            this.is = base;
            this.maxCapacity = maxCapacity;
        }

        public void addAll(Map<CustomEnchantment, Integer> enchantsToAdd) {
            addAll(enchantsToAdd.entrySet());
        }

        public void addAll(Collection<Entry<CustomEnchantment, Integer>> enchantsToAdd) {
            for (Entry<CustomEnchantment, Integer> enchantEntry : enchantsToAdd) {
                addEnchant(enchantEntry);
            }
        }

        private void addEnchant(Entry<CustomEnchantment, Integer> enchantEntry) {
            CustomEnchantment ench = enchantEntry.getKey();
            if (is.getType() != Material.ENCHANTED_BOOK && !ench.validMaterial(is)) {
                return;
            }
            for (CustomEnchantment e : enchantPool.keySet()) {
                if (ench.getConflicting().contains(e.getClass())) {
                    return;
                }
            }
            if (enchantPool.containsKey(ench)) {
                int leftLevel = enchantPool.get(ench);
                int rightLevel = enchantEntry.getValue();
                if (leftLevel == rightLevel && leftLevel < ench.getMaxLevel()) {
                    enchantPool.put(ench, leftLevel + 1);
                } else if (rightLevel > leftLevel) {
                    enchantPool.put(ench, rightLevel);
                }
            } else if (enchantPool.size() < maxCapacity) {
                enchantPool.put(ench, enchantEntry.getValue());
            }
        }

        public HashMap<CustomEnchantment, Integer> getEnchantmentMap() {
            return enchantPool;
        }
    }
}
