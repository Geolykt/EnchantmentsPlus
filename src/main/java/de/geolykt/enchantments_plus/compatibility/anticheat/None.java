package de.geolykt.enchantments_plus.compatibility.anticheat;

import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.Action;

public class None extends AbstractAnticheatAdapter {

    public None() {
        isPseudoCode = false;
    }

    @Override
    public void onEnable() {}
    
    @Override
    public void onEnchantmentFire(CustomEnchantment enchantment, Integer level, Player source, Action why) {
        return;
    }

}
