package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Speed extends CustomEnchantment {

    public static final int ID = 55;

    @Override
    public Builder<Speed> defaults() {
        return new Builder<>(Speed::new, ID)
            .all(BaseEnchantments.SPEED,
                    0,
                    "Gives the player a speed boost",
                    new Tool[]{Tool.BOOTS},
                    "Speed",
                    4, // MAX LVL
                    1.0,
                    Hand.NONE,
                    Meador.class, Weight.class);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, (int) (level * power)));
        return true;
    }
}
