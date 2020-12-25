package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Meador extends CustomEnchantment {

    public static final int ID = 36;

    @Override
    public Builder<Meador> defaults() {
        return new Builder<>(Meador::new, ID)
            .all("Gives the player a speed and jump boost",
                    new Tool[]{Tool.BOOTS},
                    "Meador",
                    1,
                    Hand.NONE,
                    BaseEnchantments.SPEED, BaseEnchantments.WEIGHT, BaseEnchantments.JUMP);
    }

    
    private Meador() {
        super(BaseEnchantments.MEADOR);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, (int) (level * power) + 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, (int) (level * power) + 2));
        return true;
    }
}
