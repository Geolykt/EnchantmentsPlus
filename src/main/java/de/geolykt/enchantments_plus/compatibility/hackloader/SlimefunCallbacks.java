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
package de.geolykt.enchantments_plus.compatibility.hackloader;

import java.util.LinkedHashMap;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.geolykt.enchantments_plus.CustomEnchantment;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

/**
 * Class that contains callbacks that are called from transformed bytecode.
 * These callbacks are entirely focused around the slimefun integration.
 */
public class SlimefunCallbacks {

    public static final String INTERNAL_NAME = "de/geolykt/enchantments_plus/compatibility/hackloader/SlimefunCallbacks";

    public static int autoDisenchanter$vanillEnchs(@NotNull BlockMenu menu, @NotNull ItemStack disenchantedItem, @NotNull ItemStack enchantedBook) {
        World world = menu.getLocation().getWorld();

        LinkedHashMap<CustomEnchantment, Integer> enchs = CustomEnchantment.getEnchants(disenchantedItem, world, null);

        if (enchs.isEmpty()) {
            return 0; // Nothing to do
        }

        enchs.forEach((ench, level) -> {
            CustomEnchantment.setEnchantment(enchantedBook, ench, level, world);
            CustomEnchantment.setEnchantment(disenchantedItem, ench, 0, world);
        });

        return enchs.size();
    }

    public static @Nullable Object @NotNull[] autoDisenchanter$noVanillaEnchantments(@NotNull BlockMenu menu, @NotNull ItemStack original) {
        World world = menu.getLocation().getWorld();
        LinkedHashMap<CustomEnchantment, Integer> enchs = CustomEnchantment.getEnchants(original, world, null);

        if (enchs.isEmpty()) {
            return new Object[]{null, null, 0};
        }

        ItemStack disenchantedItem = original.clone();
        disenchantedItem.setAmount(1); // One item at a time
        ItemStack bookItem = new ItemStack(Material.ENCHANTED_BOOK);

        enchs.forEach((ench, level) -> {
            CustomEnchantment.setEnchantment(bookItem, ench, level, world);
            CustomEnchantment.setEnchantment(disenchantedItem, ench, 0, world);
        });

        return new Object[]{disenchantedItem, bookItem, enchs.size()};
    }
}
