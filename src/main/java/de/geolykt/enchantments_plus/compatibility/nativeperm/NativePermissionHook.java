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
    public boolean request(@NotNull Player source, @NotNull Block target);
}
