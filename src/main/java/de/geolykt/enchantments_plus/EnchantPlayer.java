package de.geolykt.enchantments_plus;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// This is used to manage players on the server. It allows for easy access in enabling/disabling enchantments
//      and for adding cooldowns for different enchantments as they are used
public class EnchantPlayer {

    public static final Set<EnchantPlayer> PLAYERS = new HashSet<>();   // Collection of all players on the server

    private final Player                player;                          // Reference to the actual player object
    private final Map<Integer, Integer> enchantCooldown;   // Enchantment names mapped to their remaining cooldown

    // Creates a new enchant player objects and reads the player config file for their information
    public EnchantPlayer(Player player) {
        this.player = player;
        enchantCooldown = new HashMap<>();
        PLAYERS.add(this);
    }

    // Decrements the players cooldowns by one tick
    public void tick() {
        for (int enchantmentID : enchantCooldown.keySet()) {
            enchantCooldown.put(enchantmentID, Math.max(enchantCooldown.get(enchantmentID) - 1, 0));
        }
    }

    // Returns true if the given enchantment name is disabled for the player, otherwise false
    public boolean isDisabled(int enchantmentID) {
        if (player.hasMetadata("ze." + enchantmentID)) {
            return player.getMetadata("ze." + enchantmentID).get(0).asBoolean();
        } else {
            player.setMetadata("ze." + enchantmentID, new FixedMetadataValue(Storage.enchantments_plus, false));
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
    public void disable(int enchantmentID) {
        player.setMetadata("ze." + enchantmentID, new FixedMetadataValue(Storage.enchantments_plus, true));
    }

    // Enables the given enchantment for the player
    public void enable(int enchantmentID) {
        player.setMetadata("ze." + enchantmentID, new FixedMetadataValue(Storage.enchantments_plus, false));
    }

    // Disables all enchantments for the player
    public void disableAll() {
        for (CustomEnchantment enchant : Config.get(player.getWorld()).getEnchants()) {
            player.setMetadata("ze." + enchant.getId(), new FixedMetadataValue(Storage.enchantments_plus, true));
        }
    }

    // Enables all enchantments for the player
    public void enableAll() {
        for (CustomEnchantment enchant : Config.get(player.getWorld()).getEnchants()) {
            player.setMetadata("ze." + enchant.getId(), new FixedMetadataValue(Storage.enchantments_plus, false));
        }
    }

    // Returns the Player object associated with the EnchantPlayer
    public Player getPlayer() {
        return player;
    }

    // Returns the EnchantPlayer object associated with the given Player
    public static EnchantPlayer matchPlayer(Player player) {
        for (EnchantPlayer p : PLAYERS) {
            if (p.player.equals(player)) {
                return p;
            }
        }
        return new EnchantPlayer(player);
    }

    // Sends the EnchantPlayer the given message
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    // Returns true if the EnchantPlayer has the given permission, otherwise false
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

}
