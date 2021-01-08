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

import org.bukkit.permissions.Permissible;

public enum PermissionTypes {

    USE("enchplus.enchant.use"), GET("enchplus.enchant.get"), GIVE("enchplus.command.give"),
    ENCHANT("enchplus.command.enchant"), LIST("enchplus.command.list"), INFO("enchplus.command.info"),
    ONOFF("enchplus.command.onoff"), RELOAD("enchplus.command.reload"), LASERCOL("enchplus.command.lasercol");

    private final String permission;

    PermissionTypes(String permission) {
        this.permission = permission;
    }

    /**
     * Obtains the permission string used by the permission string.
     * 
     * @return The permission String used by the {@link #hasPermission(Permissible)} method.
     * @since 1.1.2
     */
    @Deprecated(forRemoval = false, since = "3.0.1")
    public String getPermissionNames() {
        return this.permission;
    }

    public boolean hasPermission(Permissible permissible) {
        return permissible.hasPermission(permission);
    }
}
