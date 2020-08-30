package de.geolykt.enchantments_plus;

import org.bukkit.command.CommandSender;

public class PermissionHandler {


    public static boolean hasPermission(EnchantPlayer player, PermissionTypes permissionType) {
        return PermissionHandler.hasPermission((CommandSender)player, permissionType);
    }
    public static boolean hasPermission(CommandSender player, PermissionTypes permissionType) {
        String[] permissions =
            permissionType.getPermissionNames();

        for (String permissionString : permissions) {
            if (player.hasPermission(permissionString)) {
                return true;
            }
        }
        return false;
            }
}
