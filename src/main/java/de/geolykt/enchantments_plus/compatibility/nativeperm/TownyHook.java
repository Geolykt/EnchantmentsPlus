package de.geolykt.enchantments_plus.compatibility.nativeperm;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;

public class TownyHook implements NativePermissionHook {

    @Override
    public boolean request(@NotNull Player source, @NotNull Block target) {
        if (!(PlayerCacheUtil.getCachePermission(source, target.getLocation(), target.getType(), TownyPermission.ActionType.BUILD)
                || PlayerCacheUtil.getCachePermission(source, target.getLocation(), target.getType(), TownyPermission.ActionType.DESTROY))) {
            return false;
        }
        return true;
    }

}
