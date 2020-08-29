package de.geolykt.enchantments_plus.util;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * This class has the goal of replacing the rigid CompatibilityAdapter with something more Soft-coded.<br>
 * While this leads to minimally slower performance, is also leads to higher cross-version compatibility and flexibility with Datapacks.
 */
public class RecipeUtil {

    /**
     * Returns the smelted Itemstack based on a given ItemStack.<br>
     * Does not account for stack capacity!
     */
    public static ItemStack getSmeltedVariant(ItemStack input) {
        Iterator<Recipe> recipes = Bukkit.recipeIterator();
        while (recipes.hasNext()) {
            Recipe recipe = recipes.next();
            if (!(recipe instanceof FurnaceRecipe)) {
                continue;
            }
            if (((FurnaceRecipe) recipe).getInput().getType() != input.getType()) {
                continue;
            }
            ItemStack predone = recipe.getResult();
            predone.setAmount(predone.getAmount()*input.getAmount());
            return predone;
        }
        return new ItemStack(Material.AIR);
    }
}
