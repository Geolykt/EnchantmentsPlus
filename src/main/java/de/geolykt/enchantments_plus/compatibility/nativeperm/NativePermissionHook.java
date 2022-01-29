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

/**
 * A native permission hook
 *
 * @since 3.1.4
 */
@FunctionalInterface
public interface NativePermissionHook {

    /**
     * Checks if the target block can be broken or replaced by the source player according to the hook.
     * This means that the hook for plugin X can (and likely will) return true even if the hook for plugin Y
     * may return false and vice versa. As such to test for native permissions it is best to iterate over all
     * active hooks.
     *
     * @param source The player that needs to be tested
     * @param target The target player
     * @return True if the block can be broken or replaced, false if not.
     * @since 3.1.4
     */
    public boolean request(@NotNull Player source, @NotNull Block target);
}
