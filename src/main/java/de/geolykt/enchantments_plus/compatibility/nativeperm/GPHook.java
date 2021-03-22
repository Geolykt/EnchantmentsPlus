package de.geolykt.enchantments_plus.compatibility.nativeperm;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class GPHook implements NativePermissionHook {

    /**
     * The last claim to have been accessed
     *
     * @since 3.1.4
     */
    private Claim gpCache = null;

    @Override
    public boolean request(@NotNull Player source, @NotNull Block target) {
        if (GriefPrevention.instance.claimsEnabledForWorld(target.getWorld())) {
            gpCache = GriefPrevention.instance.dataStore.getClaimAt(target.getLocation(), false, gpCache);
            if (gpCache != null && gpCache.allowEdit(source) != null) {
                return false;
            }
        }
        return true;
    }

}
