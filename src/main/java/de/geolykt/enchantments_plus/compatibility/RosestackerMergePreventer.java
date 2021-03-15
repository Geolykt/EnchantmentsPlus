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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import de.geolykt.enchantments_plus.enchantments.Reveal;
import dev.rosewood.rosestacker.stack.StackedEntity;

public class RosestackerMergePreventer implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onStack(dev.rosewood.rosestacker.event.EntityStackEvent event) {
        for (StackedEntity sentity : event.getTargets()) {
            if (Reveal.GLOWING_BLOCKS.containsKey(sentity.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
