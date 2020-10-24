package de.geolykt.enchantments_plus;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// This is used to manage players on the server. It allows for easy access in enabling/disabling enchantments
//      and for adding cooldowns for different enchantments as they are used
public class EnchantPlayer {

    // FIXME this class is a memory leak in itself

    private static final Map<UUID, EnchantPlayer> PLAYERS = new HashMap<>();   // Collection of all players on the server

    private final Player                player;            // Reference to the actual player object
    private final Map<Integer, Integer> enchantCooldown;   // Enchantment names mapped to their remaining cooldown

    // Creates a new enchant player objects and reads the player config file for their information
    public EnchantPlayer(Player player) {
        this.player = player;
        enchantCooldown = new HashMap<>();
        PLAYERS.put(player.getUniqueId(), this);
    }

    // Decrements the players cooldowns by one tick
    public void tick() {
        for (int enchantmentID : enchantCooldown.keySet()) {
            enchantCooldown.put(enchantmentID, Math.max(enchantCooldown.get(enchantmentID) - 1, 0));
        }
    }

    // Returns true if the given enchantment name is disabled for the player, otherwise false
    @Deprecated
    public boolean isDisabled(int enchantmentID) {
        if (player.hasMetadata("ze." + enchantmentID)) {
            return player.getMetadata("ze." + enchantmentID).get(0).asBoolean();
        } else {
            player.setMetadata("ze." + enchantmentID, new FixedMetadataValue(Storage.plugin, false));
            return false;
        }
    }

    // Returns the cooldown remaining for the given enchantment name in ticks
    public int getCooldown(int enchantmentID) {
        return enchantCooldown.getOrDefault(enchantmentID, 0);
    }

    // Sets the given enchantment cooldown to the given amount of ticks
    public void setCooldown(int enchantmentID, int ticks) {
        enchantCooldown.put(enchantmentID, ticks);
    }

    // Disables the given enchantment for the player
    @Deprecated
    public void disable(int enchantmentID) {
        player.setMetadata("ze." + enchantmentID, new FixedMetadataValue(Storage.plugin, true));
    }

    // Enables the given enchantment for the player
    @Deprecated
    public void enable(int enchantmentID) {
        player.setMetadata("ze." + enchantmentID, new FixedMetadataValue(Storage.plugin, false));
    }

    // Disables all enchantments for the player
    @Deprecated
    public void disableAll() {
        for (CustomEnchantment enchant : Config.get(player.getWorld()).getEnchants()) {
            player.setMetadata("ze." + enchant.getId(), new FixedMetadataValue(Storage.plugin, true));
        }
    }

    // Enables all enchantments for the player
    @Deprecated
    public void enableAll() {
        for (CustomEnchantment enchant : Config.get(player.getWorld()).getEnchants()) {
            player.setMetadata("ze." + enchant.getId(), new FixedMetadataValue(Storage.plugin, false));
        }
    }

    // Returns the EnchantPlayer object associated with the given Player
    public static EnchantPlayer matchPlayer(Player player) {
        EnchantPlayer enchPlayer = PLAYERS.get(player.getUniqueId());
        if (enchPlayer == null) {
            return new EnchantPlayer(player);
        } else {
            return enchPlayer;
        }
    }

}
