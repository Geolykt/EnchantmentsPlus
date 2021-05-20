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

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WGHook implements NativePermissionHook {

    private static final StateFlag EPLUS_FLAG;

    static {
        EPLUS_FLAG = new StateFlag("eplus", true);
        WorldGuard.getInstance().getFlagRegistry().register(EPLUS_FLAG);
    }

    /**
     * Dummy method, literally does nothing. NOT OFFICIAL API IN ANY WAY
     * Main use for this method is to force java to load this class and initialise the clinit block.
     */
    public static void dummy() {}

    /**
     * Checks whether the region that is within the given block has the enchantment use flag for the given player.
     *
     * @param p The affected player
     * @param loc The location
     * @return False if the permission does not exist, false otherwise
     * @since 3.1.6
     */
    public static boolean hasPermission(@NotNull Player p, @NotNull Location loc) {
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
        if (regionManager == null) {
            return true;
        }

        // If any regions in the given chunk deny chunk claiming, false is returned
        for (ProtectedRegion regionIn : regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(loc))) {
            State flagState = regionIn.getFlag(EPLUS_FLAG);
            if (flagState == State.DENY) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean request(@NotNull Player source, @NotNull Block target) {
        return WorldGuardPlugin.inst().createProtectionQuery().testBlockBreak(source, target) ||
            WorldGuardPlugin.inst().createProtectionQuery().testBlockInteract(source, target);
    }
}
