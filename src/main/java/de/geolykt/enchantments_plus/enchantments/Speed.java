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
            .all("Gives the player a speed boost",
                    new Tool[]{Tool.BOOTS},
                    "Speed",
                    4,
                    Hand.NONE,
                    BaseEnchantments.MEADOR, BaseEnchantments.WEIGHT);
    }

    public Speed() {
        super(BaseEnchantments.SPEED);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, (int) (level * power)));
        return true;
    }
}
