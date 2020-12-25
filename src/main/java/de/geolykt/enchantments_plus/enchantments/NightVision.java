package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.potion.PotionEffectType.NIGHT_VISION;

public class NightVision extends CustomEnchantment {

    public static final int ID = 40;

    @Override
    public Builder<NightVision> defaults() {
        return new Builder<>(NightVision::new, ID)
            .all("Lets the player see in the darkness",
                    new Tool[]{Tool.HELMET},
                    "Night Vision",
                    1,
                    Hand.NONE);
    }

    public NightVision() {
        super(BaseEnchantments.NIGHT_VISION);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        Utilities.addPotion(player, NIGHT_VISION, 610, 5);
        return true;
    }
}
