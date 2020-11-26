package de.geolykt.enchantments_plus.compatibility.enchantmentgetters;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;

/**
 * The LeightweightPDCGetter, which is a more lightweight variant of the usual PDC getter, however lacks some important features 
 *  such as denylists or automatic conversions / compatibility mode. <br>
 *  Only functional for 1.16+
 *  @see PersistentDataGetter
 *  @since 2.0.0
 */
public class LeightweightPDCGetter implements IEnchGatherer {

    @Override
    public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks, World world,
            List<String> outExtraLore) {
        LinkedHashMap<CustomEnchantment, Integer> map = new LinkedHashMap<>();
        if ( (stk != null && stk.getType() != Material.AIR) && (acceptBooks || stk.getType() != Material.ENCHANTED_BOOK)) {
            if (stk.getItemMeta() != null) {
                
                final PersistentDataContainer cont = stk.getItemMeta().getPersistentDataContainer();

                Set<NamespacedKey> keys = cont.getKeys();
                if (keys == null) {
                    return map;
                }

                for (NamespacedKey key : keys) {
                    if (!key.getNamespace().toLowerCase(Locale.ROOT).equals("enchantments_plus")) { // FIXME hardcoded string!
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
        return map;
    }

    @Override
    public void setEnchantment(ItemStack stk, CustomEnchantment ench, int level, World world) {
        if (stk == null || stk.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = stk.getItemMeta();
        List<String> lore = new LinkedList<>();
        if (meta.getLore() != null) {
            for (String loreStr : meta.getLore()) {
                if (!loreStr.toLowerCase(Locale.ENGLISH).contains(ench.getLoreName().toLowerCase(Locale.ENGLISH))) {
                    lore.add(loreStr);
                }
            }
        }

        if (ench != null && level > 0 && level <= ench.getMaxLevel()) {
            meta.getPersistentDataContainer().set(ench.getKey(), PersistentDataType.SHORT, (short) level);
            lore.add(ench.getShown(level, world));
        }
    
        //Disenchant item
        if (ench != null &&
                level <= 0 &&
                meta.getPersistentDataContainer().has(ench.getKey(), PersistentDataType.SHORT)) {
            meta.getPersistentDataContainer().remove(ench.getKey());
        }
        
        meta.setLore(lore);
        stk.setItemMeta(meta);
    
        if (stk.getType() == Material.BOOK) {
            stk.setType(Material.ENCHANTED_BOOK);
        }

        CustomEnchantment.setGlow(stk, true, world);
    }

    @Override
    public boolean hasEnchantment(Config config, ItemStack stk, BaseEnchantments ench) {
        if (stk != null && stk.getItemMeta() != null) {
            return stk.getItemMeta().getPersistentDataContainer().has(config.enchantFromEnum(ench).getKey(), PersistentDataType.SHORT);
        }
        return false;
    }

    @Override
    public int getEnchantmentLevel(Config config, ItemStack stk, BaseEnchantments ench) {
        if (stk != null && stk.getItemMeta() != null) {
            Short level = stk.getItemMeta().getPersistentDataContainer().get(config.enchantFromEnum(ench).getKey(), PersistentDataType.SHORT);
            return level != null ? level : 0;
        }
        return 0;
    }

}
