package de.geolykt.enchantments_plus.util;

import de.geolykt.enchantments_plus.EnchantPlayer;
import de.geolykt.enchantments_plus.enums.PermissionTypes;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionHandler {

    public static boolean hasPermission(EnchantPlayer player, PermissionTypes permissionType) {
        return PermissionHandler.hasPermission((CommandSender) player.getPlayer(), permissionType);
    }

    public static boolean hasPermission(Player player, PermissionTypes permissionType) {
        return PermissionHandler.hasPermission((CommandSender) player.getPlayer(), permissionType);
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
