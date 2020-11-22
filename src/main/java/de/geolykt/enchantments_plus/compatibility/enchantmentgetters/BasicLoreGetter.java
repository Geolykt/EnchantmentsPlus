package de.geolykt.enchantments_plus.compatibility.enchantmentgetters;

import static org.bukkit.Material.BOOK;
import static org.bukkit.Material.ENCHANTED_BOOK;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.util.Utilities;

/**
 * An more basic getter that stores the Enchantments within Lore.
 * @since 2.0.0
 */
public class BasicLoreGetter implements IEnchGatherer {

    @Override
    public LinkedHashMap<CustomEnchantment, Integer> getEnchants(@Nullable ItemStack stk, boolean acceptBooks, World world,
            List<String> outExtraLore) {
        LinkedHashMap<CustomEnchantment, Integer> map = new LinkedHashMap<>();
        if (stk != null && (acceptBooks || stk.getType() != Material.ENCHANTED_BOOK)) {
            if (stk.getItemMeta() != null && stk.getItemMeta().hasLore() && stk.getItemMeta().getLore() != null) {
                Config cfg = Config.get(world);
                List<String> lore = stk.getItemMeta().getLore();
                for (String raw : lore) {
                    Map.Entry<CustomEnchantment, Integer> ench = getEnchant(raw, cfg);
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
        return map;
    }

    @Nullable
    private Map.Entry<CustomEnchantment, Integer> getEnchant(String raw, Config cfg) {
        raw = raw.replaceAll("(" + ChatColor.COLOR_CHAR + ".)", "").trim();
        switch (raw.split(" ").length) {
        case 0:
            return null; // Invalid length, don't tell me otherwise
        case 1:
            CustomEnchantment enchant =cfg.enchantFromString(raw);
            if (enchant == null) {
                return null; // Not able to map enchantment
            } else {
                return new SimpleEntry<CustomEnchantment, Integer>(enchant, 1);
            }
        case 2:
            CustomEnchantment ench = cfg.enchantFromString(raw.split(" ")[0]);
            if (ench == null) {
                ench = cfg.enchantFromString(raw.replace(" ", "")); // In case of nightvision
                if (ench == null)
                    return null; // Not able to map enchantment
                else
                    return new SimpleEntry<>(ench, 1);
            }
            try {
                return new AbstractMap.SimpleEntry<CustomEnchantment, Integer>(ench,
                        Utilities.getNumber(raw.split(" ")[1]));
            } catch (NumberFormatException expected) {
                return null; // Invalid roman numeral
            }
        case 3:
            CustomEnchantment ench2 = cfg.enchantFromString(raw.split(" ")[0] + raw.split(" ")[1]);
            if (ench2 == null) {
                return null; // Not able to map enchantment
            }
            try {
                return new AbstractMap.SimpleEntry<CustomEnchantment, Integer>(ench2,
                        Utilities.getNumber(raw.split(" ")[2]));
            } catch (NumberFormatException expected) {
                return null; // Invalid roman numeral
            }
        default:
            return null; // Invalid length
        }
    }

    @Override
    public void setEnchantment(ItemStack stk, CustomEnchantment ench, int level, World world) {
        if (stk == null || stk.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = stk.getItemMeta();
        List<String> lore = new LinkedList<>();
        List<String> normalLore = new LinkedList<>();
        boolean customEnch = false;
        if (meta.hasLore()) {
            Config cfg = Config.get(world);
            for (String loreStr : meta.getLore()) {
                Map.Entry<CustomEnchantment, Integer> enchEntry = getEnchant(loreStr, cfg);
                if (enchEntry == null) {
                    normalLore.add(loreStr);
                } else if (enchEntry != null && enchEntry.getKey() != ench) {
                    customEnch = true;
                    lore.add(enchEntry.getKey().getShown(enchEntry.getValue(), world));
                }
            }
        }

        if (ench != null && level > 0 && level <= ench.getMaxLevel()) {
            lore.add(ench.getShown(level, world));
            customEnch = true;
        }

        lore.addAll(normalLore);
        meta.setLore(lore);
        stk.setItemMeta(meta);

        if (customEnch && stk.getType() == BOOK) {
            stk.setType(ENCHANTED_BOOK);
        }

        CustomEnchantment.setGlow(stk, customEnch, world);
    }

    @Override
    public int getEnchantmentLevel(Config config, ItemStack stk, BaseEnchantments enchantment) {
        if (stk != null && stk.hasItemMeta() && stk.getItemMeta().getLore() != null) {
            for (String raw : stk.getItemMeta().getLore()) {
                Map.Entry<CustomEnchantment, Integer> ench = getEnchant(raw, config);
                if (ench != null && ench.getKey().asEnum() == enchantment) {
                    return ench.getValue();
                }
            }
        }
        return 0;
    }

}
