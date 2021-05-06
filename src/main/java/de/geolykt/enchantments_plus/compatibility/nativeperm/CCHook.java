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

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.cjburkey.claimchunk.ClaimChunk;

public class CCHook implements NativePermissionHook {

    /**
     * The instance of the ChunkClaim plugin.
     *
     * @since 3.1.4
     */
    private JavaPlugin ccInstance = null;

    @Override
    public boolean request(@NotNull Player source, @NotNull Block target) {
        if (ccInstance == null) {
            if ((ccInstance = (ClaimChunk) Bukkit.getPluginManager().getPlugin("ClaimChunk")) == null) {
                // Failed to obtain the plugin in a recommended manner, try to get it via deprecated methods.
                @SuppressWarnings("deprecation")
                ClaimChunk claimChunkInstance = ClaimChunk.getInstance();
                ccInstance = claimChunkInstance; // To avoid deprecation warnings while not suppressing these for the whole method
            }
        }
        UUID owner = ((ClaimChunk)ccInstance).getChunkHandler().getOwner(target.getChunk());
        if (owner != null && !owner.equals(source.getUniqueId())) {
            return false;
        }
        return true;
    }

}
