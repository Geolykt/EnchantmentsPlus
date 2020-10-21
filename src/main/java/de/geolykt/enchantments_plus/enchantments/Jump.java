package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.potion.PotionEffectType.JUMP;

public class Jump extends CustomEnchantment {

    public static final int ID = 30;

    @Override
    public Builder<Jump> defaults() {
        return new Builder<>(Jump::new, ID)
            .all(BaseEnchantments.JUMP,
                    "Gives the player a jump boost",
                    new Tool[]{Tool.BOOTS},
                    "Jump",
                    4, // MAX LVL
                    Hand.NONE,
                    Meador.class);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        Utilities.addPotion(player, JUMP, 610, (int) Math.round(level * power));
        return true;
    }
}
