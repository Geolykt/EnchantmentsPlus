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
/**
 * There are instances where supporting foreign plugins as-is is impossible as they lack proper API.
 * While there are plugins that accept PRs from outsiders, there are various plugins where it either
 * is outright not impossible or where it takes a very long time until a PR gets accepted.
 * To remedy this issue enchantmentsPlus uses the nuclear option of cross-plugin compatibility:
 * The Hackloader.
 */
package de.geolykt.enchantments_plus.compatibility.hackloader;
