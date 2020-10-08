package de.geolykt.enchantments_plus.util;

import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

import de.geolykt.enchantments_plus.enums.PermissionTypes;

public class PermissionHandler {

    public static boolean hasPermission(Player player, PermissionTypes permissionType) {
        return hasPermission((CommandSender) player, permissionType);
    }

    public static boolean hasPermission(CommandSender player, PermissionTypes permissionType) {
        String[] permissions = permissionType.getPermissionNames();

        for (String permissionString : permissions) {
            if (player.hasPermission(permissionString)) {
                return true;
            }
        }
        return false;
    }
}
