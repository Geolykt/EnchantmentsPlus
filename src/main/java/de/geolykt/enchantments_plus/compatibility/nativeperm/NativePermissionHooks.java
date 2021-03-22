package de.geolykt.enchantments_plus.compatibility.nativeperm;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class NativePermissionHooks {

    private final List<NativePermissionHook> hooks;

    public NativePermissionHooks(List<NativePermissionHook> hooks) {
        this.hooks = hooks;
    }

    /**
     * This method queries the correct Permission interfaces, which are plugins. 
     * If the plugin is not loaded the method will ignore it gracefully. <br>
     *
     * @param source The player, from where the Query originates from.
     * @param target The Block which should be tested whether the player may break/alter.
     * @return True if the player may break/alter the block, false otherwise
     * @since 3.1.4
     */
    public boolean nativeBlockPermissionQueryingSystem (@NotNull Player source, @NotNull Block target) {
        for (NativePermissionHook hook : hooks) {
            if (!hook.request(source, target)) {
                return false;
            }
        }
        return true;
    }
}
