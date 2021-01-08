/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2021 Geolykt and EnchantmentsPlus contributors
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
package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.Material.*;

public class Gluttony extends CustomEnchantment {

    public static final int ID = 21;

    @Override
    public Builder<Gluttony> defaults() {
        return new Builder<>(Gluttony::new, ID)
            .all("Automatically eats for the player",
                    new Tool[]{Tool.HELMET},
                    "Gluttony",
                    1, // MAX LVL
                    Hand.NONE);
    }

    public Gluttony() {
        super(BaseEnchantments.GLUTTONY);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        for (int i = 0; i < Storage.COMPATIBILITY_ADAPTER.gluttonyFoodItems().length; i++) {
            if (player.getInventory().containsAtLeast(
                new ItemStack(Storage.COMPATIBILITY_ADAPTER.gluttonyFoodItems()[i]), 1)
                && player.getFoodLevel() <= 20 - Storage.COMPATIBILITY_ADAPTER.gluttonyFoodLevels()[i]) {
                Utilities.removeItem(player, Storage.COMPATIBILITY_ADAPTER.gluttonyFoodItems()[i], 1);
                player.setFoodLevel(player.getFoodLevel() + Storage.COMPATIBILITY_ADAPTER.gluttonyFoodLevels()[i]);
                player.setSaturation(
                    (float) (player.getSaturation() + Storage.COMPATIBILITY_ADAPTER.gluttonySaturations()[i]));
                if (Storage.COMPATIBILITY_ADAPTER.gluttonyFoodItems()[i] == RABBIT_STEW
                    || Storage.COMPATIBILITY_ADAPTER.gluttonyFoodItems()[i] == MUSHROOM_STEW
                    || Storage.COMPATIBILITY_ADAPTER.gluttonyFoodItems()[i] == BEETROOT_SOUP) {
                    player.getInventory().addItem(new ItemStack(BOWL));
                }
            }
        }
        return true;
    }
}
