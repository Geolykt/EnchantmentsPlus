package de.geolykt.enchantments_plus.compatibility.nativeperm;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.cjburkey.claimchunk.ClaimChunk;

public class CCHook implements NativePermissionHook {

    /**
     * The instance of the ChunkClaim plugin.
     *
     * @since 3.1.4
     */
    private JavaPlugin ccInstance = null;

    @Override
    public boolean request(@NotNull Player source, @NotNull Block target) {
        if (ccInstance == null) {
            if ((ccInstance = (ClaimChunk) Bukkit.getPluginManager().getPlugin("ClaimChunk")) == null) {
                // Failed to obtain the plugin in a recommended manner, try to get it via deprecated methods.
                @SuppressWarnings("deprecation")
                ClaimChunk claimChunkInstance = ClaimChunk.getInstance();
                ccInstance = claimChunkInstance; // To avoid deprecation warnings while not suppressing these for the whole method
            }
        }
        UUID owner = ((ClaimChunk)ccInstance).getChunkHandler().getOwner(target.getChunk());
        if (owner != null && !owner.equals(source.getUniqueId())) {
            return false;
        }
        return true;
    }

}
