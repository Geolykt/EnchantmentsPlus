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
package de.geolykt.enchantments_plus.util;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

/**
 * This class has the goal of replacing the rigid CompatibilityAdapter with something more Soft-coded.<br>
 * While this leads to <strike>minimally</strike> <b>insanely</b> slower performance,
 *  is also leads to higher cross-version compatibility and flexibility with Datapacks. <br>
 *  As such, results are cached.
 */
public class RecipeUtil {

    /**
     * Returns the smelted Itemstack based on a given ItemStack.<br>
     * Does not account for the amount of the Itemstack.
     */
    @NotNull
    private static ItemStack getSmeltedVariant(ItemStack input) {
        Iterator<Recipe> recipes = Bukkit.recipeIterator();
        while (recipes.hasNext()) {
            if (recipes.next() instanceof FurnaceRecipe recipe) {
                RecipeChoice choice = recipe.getInputChoice();
                if (!choice.test(input)) {
                    continue;
                }
                ItemStack predone = recipe.getResult();
                return predone;
            }
        }
        return new ItemStack(Material.AIR);
    }

    /**
     * Recipe cache for smelting recipes
     */
    private static final HashMap<Material, ItemStack> smeltCache = new HashMap<>();

    /**
     * Returns the smelted Itemstack based on a given ItemStack.<br>
     * Does not account for stack capacity!
     * Returns air if the recipe was not found.
     * @param input The input itemstack
     * @param updateCache Whether or not the Cache should be updated
     * @return the smelted Itemstack based on a given ItemStack.
     */
    @NotNull
    public static ItemStack getSmeltedVariant(ItemStack input, boolean updateCache) {
        ItemStack out;
        if (updateCache) {
            out = getSmeltedVariant(input);
            smeltCache.put(input.getType(), out.clone());
        } else {
            out = smeltCache.getOrDefault(input.getType(), new ItemStack(Material.AIR)).clone();
        }
        out.setAmount(out.getAmount()*input.getAmount());
        return out;
    }

    private static final HashMap<Material, Long> cacheDuration = new HashMap<>();
    public static final int REFRESH_EVERY = 60000; // Refresh every minute

    /**
     * Returns the smelted Itemstack based on a given ItemStack.<br>
     * Does not account for stack capacity!
     * Returns air if the recipe was not found.
     * Also periodically refreshes the cache, in case recipes have been updated for one reason or another.
     * @param input The input itemstack
     * @return the smelted Itemstack based on a given ItemStack.
     */
    @NotNull
    public static ItemStack getSmeltedVariantCached(ItemStack input) {
        if (System.currentTimeMillis() - cacheDuration.getOrDefault(input.getType(), Instant.EPOCH.toEpochMilli()) > REFRESH_EVERY) {
            cacheDuration.put(input.getType(), System.currentTimeMillis());
            return getSmeltedVariant(input, true);
        } else {
            return getSmeltedVariant(input, false);
        }
    }
}
