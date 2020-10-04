package de.geolykt.enchantments_plus.evt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enchantments.Unrepairable;

import java.util.*;
import java.util.Map.Entry;

import static org.bukkit.Material.ENCHANTED_BOOK;
import static org.bukkit.event.EventPriority.MONITOR;

// This class manages the combination of enchantments in an anvil. It takes into account conflicting enchantments, 
//      the max number of enchantments per tool, and the enchantment's max level. It shuffles the results every time
//      so that the player can find the combination they desire when there are conficting or too many enchantment
public class AnvilMerge implements Listener {

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

        boolean isBookL = leftItem.getType() == Material.ENCHANTED_BOOK;
        boolean isBookR = rightItem.getType() == Material.ENCHANTED_BOOK;


        Map<Enchantment, Integer> lEnch = isBookL ?
            ((EnchantmentStorageMeta) leftItem.getItemMeta()).getStoredEnchants()
            : leftItem.getEnchantments();
        Map<Enchantment, Integer> rEnch = isBookR ?
            ((EnchantmentStorageMeta) rightItem.getItemMeta()).getStoredEnchants()
            : rightItem.getEnchantments();

        int leftUnbLvl = lEnch.getOrDefault(Enchantment.DURABILITY, -1);
        int rightUnbLvl = rEnch.getOrDefault(Enchantment.DURABILITY, -1);

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

        if (leftUnbLvl * rightUnbLvl == 0 && leftUnbLvl < 1 && rightUnbLvl < 1) {
            if (oldOutItem.getType() == ENCHANTED_BOOK) {
                ((EnchantmentStorageMeta)newOutMeta).removeStoredEnchant(Enchantment.DURABILITY);
                newOutMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                newOutMeta.removeEnchant(Enchantment.DURABILITY);
                newOutMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }

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

    @EventHandler(priority = MONITOR)
    public void onClicks(final PrepareAnvilEvent evt) {
        if (evt.getViewers().size() < 1) {
            return;
        }

        final Config config = Config.get(evt.getViewers().get(0).getWorld());
        final AnvilInventory anvilInv = evt.getInventory();

        if (anvilInv.getItem(0) != null && anvilInv.getItem(0).getType() == ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) anvilInv.getItem(0).getItemMeta();
            if (!bookMeta.getStoredEnchants().containsKey(org.bukkit.enchantments.Enchantment.DURABILITY)) {
                bookMeta.addStoredEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 0, true);
                anvilInv.getItem(0).setItemMeta(bookMeta);
            }
        }
        if (anvilInv.getItem(1) != null && anvilInv.getItem(1).getType() == ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) anvilInv.getItem(1).getItemMeta();
            if (!bookMeta.getStoredEnchants().containsKey(org.bukkit.enchantments.Enchantment.DURABILITY)) {
                bookMeta.addStoredEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 0, true);
                anvilInv.getItem(1).setItemMeta(bookMeta);
            }
        }


        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {

            ItemStack leftItem = anvilInv.getItem(0);
            ItemStack rightItem = anvilInv.getItem(1);
            ItemStack outItem = anvilInv.getItem(2);
            ItemStack stack = doMerge(leftItem, rightItem, outItem, config);

            if (stack != null) {
                anvilInv.setItem(2, stack);
            }
        }, 0);
    }

    private class EnchantmentPool {

        private final HashMap<CustomEnchantment, Integer> enchantPool = new HashMap<>();
        private final ItemStack                           is;
        private final int                                 maxCapacity;

        public EnchantmentPool(ItemStack is, int maxCapacity) {
            this.is = is;
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
