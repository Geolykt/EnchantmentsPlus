package de.geolykt.enchantments_plus.enums;

import org.bukkit.permissions.Permissible;

public enum PermissionTypes {

    USE("enchplus.enchant.use"), GET("enchplus.enchant.get"), GIVE("enchplus.command.give"),
    ENCHANT("enchplus.command.enchant"), LIST("enchplus.command.list"), INFO("enchplus.command.info"),
    ONOFF("enchplus.command.onoff"), RELOAD("enchplus.command.reload"), LASERCOL("enchplus.command.lasercol");

    private String permission;

    PermissionTypes(String permission) {
        this.permission = permission;
    }

    public String getPermissionNames() {
        return this.permission;
    }
    
    public boolean hasPermission(Permissible permissible) {
        return permissible.hasPermission(permission);
    }
}
