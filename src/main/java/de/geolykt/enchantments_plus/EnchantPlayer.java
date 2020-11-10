package de.geolykt.enchantments_plus;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// This is used to manage players on the server. It allows for easy access in enabling/disabling enchantments
//      and for adding cooldowns for different enchantments as they are used
public class EnchantPlayer {

    private static final Map<UUID, EnchantPlayer> PLAYERS = new HashMap<>();   // Collection of all players on the server

    private final Map<Integer, Integer> enchantCooldown;   // Enchantment names mapped to their remaining cooldown

    /**
     * If the cooldown is above this value it will be regarded as if the enchantment is disabled.
     *  This value should not be too near to 0 or Integer.MAX_VALUE. This is pretty much a magic constant.
     * @since 2.2.2
     */
    private static final int DISABLE_THRESHOLD = 0x5FFFFFFF;
    
    // Creates a new enchant player objects and reads the player config file for their information
    public EnchantPlayer(@NotNull Player player) {
        enchantCooldown = new HashMap<>();
        PLAYERS.put(player.getUniqueId(), this);
    }

    // Decrements the players cooldowns by one tick
    public void tick() {
    	enchantCooldown.replaceAll((key, value) -> Math.max(--value, 0));
    }

    // Returns true if the given enchantment name is disabled for the player, otherwise false
    public boolean isDisabled(int enchantmentID) {
		return enchantCooldown.getOrDefault(enchantmentID, 0) > DISABLE_THRESHOLD;
    }

    /**
     * Returns the cooldown remaining for the given enchantment in ticks
     *  may return very high numbers if the enchantment is disabled.
     * @param enchantmentID the enchantment ID
     * @return the cooldown remaining for the given enchantment in ticks
     */
    public int getCooldown(int enchantmentID) {
        return enchantCooldown.getOrDefault(enchantmentID, 0);
    }

    /**
     * Sets the given enchantment cooldown to the given amount of ticks
     *  has no effect if the enchantment either is or could be disabled.
     * @param enchantmentID The enchantment
     * @param ticks The ticks for the cooldown
     * @since 1.0
     */
    public void setCooldown(int enchantmentID, int ticks) {
        enchantCooldown.put(enchantmentID, ticks);
    }

    // Disables the given enchantment for the player
    public void disable(int enchantmentID) {
    	enchantCooldown.put(enchantmentID, Integer.MAX_VALUE);
    }

    // Enables the given enchantment for the player
    public void enable(int enchantmentID) {
    	if (isDisabled(enchantmentID)) {
        	enchantCooldown.put(enchantmentID, 0);
    	}
    }

    // Disables all enchantments for the player
    public void disableAll() {
        for (CustomEnchantment enchant : Config.allEnchants) {
        	disable(enchant.getId());
        }
    }

    // Enables all enchantments for the player
    public void enableAll() {
        for (CustomEnchantment enchant : Config.allEnchants) {
        	enable(enchant.getId());
        }
    }

    // Returns the EnchantPlayer object associated with the given Player
    public static EnchantPlayer matchPlayer(@NotNull Player player) {
        EnchantPlayer enchPlayer = PLAYERS.get(player.getUniqueId());
        if (enchPlayer == null) {
            return new EnchantPlayer(player);
        } else {
            return enchPlayer;
        }
    }

    /**
     * Removes the corresponding EnchantPlayer from the lookup tables.
     *  This is used to prevent a buildup of EnchantPlayer instances over time.
     *  This should also only be used after a player logged of as otherwise it would be a bit strange
     * @param playerUID The UUID of the player that should be removed.
     * @since 2.2.2
     */
    public static void removePlayer(@NotNull UUID playerUID) {
    	PLAYERS.remove(playerUID);
    }
}
