package de.geolykt.enchantments_plus.compatibility.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.Action;

public abstract class AbstractAnticheatAdapter {

    protected boolean isPseudoCode = true;
    
    public void onEnable() {
        if (isPseudoCode) {
            Bukkit.getLogger().severe("Please note that Enchantments+ integrations with your current Anticheat hasn't been tested"
                    + " and may be borked.");
        }
    }
    
    public abstract void onEnchantmentFire(CustomEnchantment enchantment, Integer level, Player source, Action why);

}
