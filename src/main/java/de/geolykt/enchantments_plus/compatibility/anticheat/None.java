package de.geolykt.enchantments_plus.compatibility.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.Action;

public class None extends AbstractAnticheatAdapter {

    public None() {
        isPseudoCode = false;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Bukkit.getLogger().info("Anticheat integration with Enchantments+ failed or was disabled.");
    }
    
    @Override
    public void onEnchantmentFire(CustomEnchantment enchantment, Integer level, Player source, Action why) {
        return;
    }

}
