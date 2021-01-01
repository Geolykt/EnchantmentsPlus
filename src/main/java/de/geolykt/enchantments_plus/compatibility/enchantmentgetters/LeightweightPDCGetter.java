/*
 *  This file is part of EnchantmentsPlus, a bukkit plugin.
 *  Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 *  Copyright (c) 2020 - 2021 Geolykt and EnchantmentsPlus contributors
 *   
 *  This program is free software: you can redistribute it and/or modify  
 *  it under the terms of the GNU General Public License as published by  
 *  the Free Software Foundation, version 3.
 *  
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License 
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
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
        if ((stk != null && stk.getType() != Material.AIR) && (acceptBooks || stk.getType() != Material.ENCHANTED_BOOK)) {
            ItemMeta itemMeta = stk.getItemMeta();
            if (itemMeta != null) {
                final PersistentDataContainer cont = itemMeta.getPersistentDataContainer();

                Set<NamespacedKey> keys = cont.getKeys();
                for (NamespacedKey key : keys) {
                    if (!key.getNamespace().toLowerCase(Locale.ROOT).equals(Storage.plugin.getName())) {
                        continue;
                    }
                    if (!key.getKey().split("\\.")[0].equals("ench")) {
                        continue;
                    }

                    Integer level = (int) cont.getOrDefault(key, PersistentDataType.SHORT, (short) 0);
                    Short id = Short.decode(key.getKey().split("\\.")[1]);
                    CustomEnchantment ench = Config.get(world).enchantFromID(id); // TODO use the ordinal ID instead of the legacy ID
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
        if (stk == null)
            return;
        ItemMeta meta = stk.getItemMeta();
        if (meta == null)
            return;

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

        CustomEnchantment.setGlow(stk, true, Config.get(world));
    }

    @Override
    public boolean hasEnchantment(@NotNull Config config, @Nullable ItemStack stk, @NotNull BaseEnchantments ench) {
        if (stk == null)
            return false;
        ItemMeta itemMeta = stk.getItemMeta();
        if (itemMeta != null) {
            return itemMeta.getPersistentDataContainer().has(config.enchantFromEnum(ench).getKey(), PersistentDataType.SHORT);
        }
        return false;
    }

    @Override
    public int getEnchantmentLevel(@NotNull Config config, @Nullable ItemStack stk, @NotNull BaseEnchantments ench) {
        if (stk == null)
            return 0;
        ItemMeta itemMeta = stk.getItemMeta();
        if (itemMeta != null) {
            Short level = itemMeta.getPersistentDataContainer().get(config.enchantFromEnum(ench).getKey(), PersistentDataType.SHORT);
            return level != null ? level : 0;
        }
        return 0;
    }

}
