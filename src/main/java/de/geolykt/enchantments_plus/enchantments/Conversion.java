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

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

// TODO rework the enchantment to use XP points, not levels
public class Conversion extends CustomEnchantment {

    public static final int ID = 10;

    @Override
    public Builder<Conversion> defaults() {
        return new Builder<>(Conversion::new, ID)
            .all("Converts XP to health when right clicking and sneaking",
                new Tool[]{Tool.SWORD},
                "Conversion",
                4, // MAX LVL
                Hand.RIGHT);
    }

    public Conversion() {
        super(BaseEnchantments.CONVERSION);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Player player = evt.getPlayer();
            if (player.isSneaking()) {
                if (player.getLevel() > 1) {
                    if (player.getHealth() < 20) {
                        player.setLevel((player.getLevel() - 1));
                        player.setHealth(Math.min(20, player.getHealth() + 2 * power * level));
                        for (int i = 0; i < 3; i++) {
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                                CompatibilityAdapter.display(Utilities.getCenter(player.getLocation()), Particle.HEART, 10, .1f, .5f, .5f, .5f);
                            }, i * 5 + 1);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
