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

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import de.diddiz.LogBlock.Actor;
import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;

public class LogBlockHook implements NativeLoggingHook {

    protected Consumer lbConsumer;

    @Override
    public void onEnable(@NotNull Logger logger) {
        for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
            if (pl instanceof LogBlock) {
                lbConsumer = ((LogBlock)pl).getConsumer();
                logger.fine("Using LogBlock implementation: " + pl.getClass().getName() + " from plugin " + pl.getName());
                return;
            }
        }
        throw new IllegalStateException("No plugin implements the LogBlock API even though it was declared!");
    }

    @Override
    public void logInteraction(@NotNull BaseEnchantments ench, @NotNull UUID source, @NotNull String username, @NotNull BlockState before, @NotNull Block blk) {
        int mode = 0;
        switch (ench) {
        case APOCALYPSE:
            mode = 0;
            break;
        case SPECTRAL:
        case SWITCH:
            mode = 1;
            break;
        case TERRAFORMER:
            mode = 2;
            break;
        default:
            return;
        }
        Actor a = new Actor(username, source, blk);
        if (mode == 2) {
            // Place
            lbConsumer.queueBlockPlace(a, blk.getLocation(), blk.getBlockData());
        } else if (mode == 1) {
            // alter
            lbConsumer.queueBlockReplace(a, before, blk.getState());
        } else if (mode == 0) {
            // break
            lbConsumer.queueBlockBreak(a, before);
        } else {
            throw new IllegalStateException("Congrats, you destroyed the fabric of reality!");
        }
    }

}
