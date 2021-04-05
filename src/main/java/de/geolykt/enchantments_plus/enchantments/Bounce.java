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

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import static org.bukkit.Material.SLIME_BLOCK;

public class Bounce extends CustomEnchantment {

    public static final int ID = 7;

    @Override
    public Builder<Bounce> defaults() {
        return new Builder<>(Bounce::new, ID)
                .probability(0)
                .all("Shoots you in the air if you move on slime blocks. Sneaking on slime blocks negates this effect.",
                    new Tool[]{Tool.BOOTS},
                    "Bounce",
                    5, // MAX LVL
                    Hand.NONE);
    }

    public Bounce() {
        super(BaseEnchantments.BOUNCE);
    }

    @Override
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        if (player.getVelocity().getY() < 0 &&
            (player.getLocation().getBlock().getRelative(0, -1, 0).getType() == SLIME_BLOCK
                || player.getLocation().getBlock().getType() == SLIME_BLOCK
                || (player.getLocation().getBlock().getRelative(0, -2, 0).getType() == SLIME_BLOCK) &&
                (level * power) > 2.0)) {
            if (!player.isSneaking()) {
                player.setVelocity(player.getVelocity().setY(.56 * level * power));
                return true;
            }
            player.setFallDistance(0);
        }
        return false;
    }
}
