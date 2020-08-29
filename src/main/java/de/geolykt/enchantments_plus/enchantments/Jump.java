package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.BOOTS;
import static org.bukkit.potion.PotionEffectType.JUMP;

public class Jump extends CustomEnchantment {

    public static final int ID = 30;

    @Override
    public Builder<Jump> defaults() {
        return new Builder<>(Jump::new, ID)
            .maxLevel(4)
            .loreName("Jump")
            .probability(0)
            .enchantable(new Tool[]{BOOTS})
            .conflicting()
            .description("Gives the player a jump boost")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.NONE);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        Utilities.addPotion(player, JUMP, 610, (int) Math.round(level * power));
        return true;
    }
}
