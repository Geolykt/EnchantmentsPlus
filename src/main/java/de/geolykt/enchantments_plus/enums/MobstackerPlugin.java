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
package de.geolykt.enchantments_plus.enums;

/**
 * Enumeration of Mobstacker plugins that this plugin can integrate with.
 * Used to prevent mobstacking where it doesn't belong (i. e. stacking reveal shulkers).
 *
 * @since 3.1.3
 */
public enum MobstackerPlugin {

    /**
     * No or unknown mobstacker plugin found or the integration is disabled.
     *
     * @since 3.1.3
     */
    NONE,

    /**
     * The mobstacker plugin is uk.antiperson.stackmob:StackMob
     * Source at https://github.com/Nathat23/StackMob-5
     *
     * @since 3.1.3
     */
    STACKMOB_5;
}
