package de.geolykt.enchantments_plus.enchantments;

import static de.geolykt.enchantments_plus.enums.Tool.BOOTS;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

public class Speed extends CustomEnchantment {

    public static final int ID = 55;

    @Override
    public Builder<Speed> defaults() {
        return new Builder<>(Speed::new, ID)
            .maxLevel(4)
            .loreName("Speed")
            .probability(0)
            .enchantable(new Tool[]{BOOTS})
            .conflicting(Meador.class, Weight.class)
            .description("Gives the player a speed boost")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.NONE)
            .base(BaseEnchantments.SPEED);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, (int) (level * power)));
        return true;
    }
}
