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
package de.geolykt.enchantments_plus.compatibility;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.geolykt.enchantments_plus.enchantments.Reveal;
import uk.antiperson.stackmob.events.StackSpawnEvent;

/**
 * Prevents the merging of mobs that should not be merged with the MobStack5 plugin.
 *
 * @since 3.1.3
 */
public final class Stackmob5MergePreventer implements Listener {

    @EventHandler
    public void onMobStackSpawn(StackSpawnEvent event) {
        // Prevent merging of entities spawned by the reveal enchantment
        if (Reveal.GLOWING_BLOCKS.containsKey(event.getLivingEntity().getLocation())) {
            event.setCancelled(true);
        }
    }
}
