package de.geolykt.enchantments_plus.compatibility.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.enums.Action;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;

public abstract class AbstractAnticheatAdapter {

    protected boolean isPseudoCode = true;
    
    /**
     * Notifies the Adapter that it should load it's routines and prepare whatever stuff it needs to prepare.
     * This can range to stuff like loading or searching for dependency classes
     * 
     * @since 1.1.0
     */
    public void onEnable() {
        if (isPseudoCode) {
            Bukkit.getLogger().severe("Please note that Enchantments+ integrations with your current Anticheat hasn't been tested"
                    + " and may be borked.");
        }
    }

    /**
     * Notifies the adapter that an enchantment was fired.
     * 
     * @param enchantment The enchantment that was fired.
     * @param level The level of the enchantment
     * @param source the source of the enchantment
     * @param why The action that was performed that caused the event to be fired
     * @implNote This is currently not used, at all
     * @since 2.2.0
     */
    public abstract void onEnchantmentFire(BaseEnchantments enchantment, Integer level, Player source, Action why);
}
