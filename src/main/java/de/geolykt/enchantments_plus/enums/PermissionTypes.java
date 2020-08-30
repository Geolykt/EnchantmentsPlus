package de.geolykt.enchantments_plus.enums;

public enum PermissionTypes {

    USE("enchplus.enchant.use"), 
    GET("enchplus.enchant.get"),
    GIVE("enchplus.command.give"),
    ENCHANT("enchplus.command.enchant"),
    LIST("enchplus.command.list"),
    INFO("enchplus.command.info"),
    ONOFF("enchplus.command.onoff"),
    RELOAD("enchplus.command.reload");

    private String[] permissions;

    PermissionTypes(String ... permissions) {
        this.permissions = permissions;
    }
    public String[] getPermissionNames() {
        return this.permissions;
    }
}
