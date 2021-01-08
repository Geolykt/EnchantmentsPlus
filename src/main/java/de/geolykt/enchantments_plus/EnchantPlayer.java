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
package de.geolykt.enchantments_plus;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import de.geolykt.enchantments_plus.enums.BaseEnchantments;

/**
 * This is used to manage enchantment cooldowns of players on the server.
 *  The class might be removed in future development builds and integrated elsewhere
 * @since 1.0.0
 */
public class EnchantPlayer {

    /**
     * Internal data structure to store the cooldowns of the enchantment into.
     *  It should not be used directly
     * @since 3.0.0
     */
    private static final EnumMap<BaseEnchantments, HashMap<UUID, Long>> COOLDOWNS = new EnumMap<>(BaseEnchantments.class);

    static {
        for (BaseEnchantments ench : BaseEnchantments.values()) {
            COOLDOWNS.put(ench, new HashMap<>());
        }
    }

    /**
     * Returns true if the given enchantment name is disabled for the player,
     * otherwise false
     * @param player The player
     * @param ench The enchantment
     * @return True if the player was disabled
     * @since 3.0.0
     */
    public static boolean isDisabled(@NotNull Player player, @NotNull BaseEnchantments ench) {
        return COOLDOWNS.get(ench).getOrDefault(player.getUniqueId(), (long) 0.0) == Long.MAX_VALUE;
    }

    /**
     * Returns the remaining cooldown for the given enchantment in milliseconds that is left, may be negative.
     * The function does not take care of when an enchantment is disabled, use {@link #isDisabled(Player, CustomEnchantment)}
     * instead.
     * 
     * @param player the player the query is valid for
     * @param ench the enchantment
     * @return the cooldown remaining for the given enchantment in milliseconds
     * @since 3.0.0
     */
    public static long getCooldown(@NotNull Player player, @NotNull BaseEnchantments ench) {
        return COOLDOWNS.get(ench).getOrDefault(player.getUniqueId(), (long) 0.0) - System.currentTimeMillis();
    }

    /**
     * Returns the time when the cooldown has ended, this is presented by the amount of milliseconds that have passed since
     *  the epoch start, or Long.MAX_VALUE to represent that the enchantment is disabled
     * 
     * @param player the player the query is valid for
     * @param ench the enchantment
     * @return the time at which the cooldown ends, or Long.MAX_VALUE if it should never end.
     * @since 3.0.0
     */
    public static long getCooldownEnd(@NotNull Player player, @NotNull BaseEnchantments ench) {
        return COOLDOWNS.get(ench).getOrDefault(player.getUniqueId(), (long) 0.0);
    }

    /**
     * Sets the given enchantment cooldown to the given amount of milliseconds; silently fails if the enchantment
     * is disabled
     * 
     * @param player The player
     * @param enchantment The enchantment
     * @param millis The milliseconds for the cooldown
     * @since 3.0.0
     */
    public static void setCooldown(@NotNull Player player, @NotNull BaseEnchantments enchantment, int millis) {
        if (!isDisabled(player, enchantment)) {
            COOLDOWNS.get(enchantment).put(player.getUniqueId(), millis + System.currentTimeMillis());
        }
    }

    /**
     * Disables the given enchantment for the player
     * @param player The player that should be targeted in the operation
     * @param ench The targeted enchantment
     * @since 3.0.0
     */
    public static void disable(@NotNull Player player, @NotNull BaseEnchantments ench) {
        COOLDOWNS.get(ench).put(player.getUniqueId(), Long.MAX_VALUE);
    }

    /**
     * Enables the given enchantment for the player and silently fails if the enchantment is not disabled.
     * @param player The player that should be targeted in the operation
     * @param ench The enchantment to enable for the player
     * @since 3.0.0
     */
    public static void enable(@NotNull Player player, @NotNull BaseEnchantments ench) {
        if (isDisabled(player, ench)) {
            COOLDOWNS.get(ench).put(player.getUniqueId(), (long) 0);
        }
    }

    /**
     * Disables all enchantments for the player
     * @param player The player that is the target of the operation
     * @since 3.0.0 the cooldown remaining for the given enchantment in ticks
     */
    public static void disableAll(@NotNull Player player) {
        for (BaseEnchantments enchant : BaseEnchantments.values()) {
            disable(player, enchant);
        }
    }

    /**
     * Enables all enchantments for the player
     * @param player
     * @since 3.0.0
     */
    public static void enableAll(@NotNull Player player) {
        for (BaseEnchantments enchant : BaseEnchantments.values()) {
            enable(player, enchant);
        }
    }
}
