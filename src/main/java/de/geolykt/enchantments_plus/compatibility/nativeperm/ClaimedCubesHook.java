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
package de.geolykt.enchantments_plus.compatibility.nativeperm;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import io.github.bycubed7.claimedcubes.managers.PlotManager;
import io.github.bycubed7.claimedcubes.plot.Plot;
import io.github.bycubed7.claimedcubes.plot.PlotPermission;

/**
 * A native permission hook targeting the
 * <a href="https://github.com/ByCubed7/Minecraft-ClaimedCubes">ClaimedCubes</a> plugin.
 * Do we think that this hook is useless? Yes. Do we implement it anyways? Hell yeah.
 *
 * @since 4.0.4
 */
public class ClaimedCubesHook implements NativePermissionHook {

    @Override
    public boolean request(@NotNull Player source, @NotNull Block target) {
        Plot plot = PlotManager.instance.getPlot(target.getChunk());
        if (plot == null) {
            return true;
        }
        if (plot.hasBan(source.getUniqueId())) {
            return false;
        }
        return plot.hasPermission(source.getUniqueId(), PlotPermission.PLACE) && plot.hasPermission(source.getUniqueId(), PlotPermission.BREAK);
    }
}
