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
package de.geolykt.enchantments_plus.compatibility.nativeperm;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.geolykt.enchantments_plus.enums.BaseEnchantments;

public interface NativeLoggingHook {

    /**
     * Called when the logging hook is initialised. This is a bit different to the constructor because why not?
     *
     * @param logger The logger that is used to log errors to console or something. Don't use System.out at least
     * @since 3.1.6
     */
    public void onEnable(@NotNull Logger logger);

    /**
     * Logs an interaction caused by an enchantment.
     * Usually only considerably "destructive" enchantment actions are recorded,
     * such as those caused by Apocalypse or Switch.
     *
     * @param ench The enchantment that triggered the interaction
     * @param source The player UUID that caused this action
     * @param username The username (NOT the display name!) of the user causing the action
     * @param before The blockdata of the block that was modified before it was modified. May be null if not applicable (e. g. placing blocks)
     * @param blk The modified block
     * @since 3.1.6
     */
    public void logInteraction(@NotNull BaseEnchantments ench, @NotNull UUID source, @NotNull String username, @Nullable BlockState before, @NotNull Block blk);
}
