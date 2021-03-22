package de.geolykt.enchantments_plus.compatibility.nativeperm;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class WGHook implements NativePermissionHook {

    @Override
    public boolean request(@NotNull Player source, @NotNull Block target) {
        return WorldGuardPlugin.inst().createProtectionQuery().testBlockBreak(source, target) ||
            WorldGuardPlugin.inst().createProtectionQuery().testBlockInteract(source, target);
    }

}
