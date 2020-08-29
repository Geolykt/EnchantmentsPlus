package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.HELMET;
import static org.bukkit.potion.PotionEffectType.NIGHT_VISION;

public class NightVision extends CustomEnchantment {

    public static final int ID = 40;

    @Override
    public Builder<NightVision> defaults() {
        return new Builder<>(NightVision::new, ID)
            .maxLevel(1)
            .loreName("Night Vision")
            .probability(0)
            .enchantable(new Tool[]{HELMET})
            .conflicting()
            .description("Lets the player see in the darkness")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.NONE);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        Utilities.addPotion(player, NIGHT_VISION, 610, 5);
        return true;
    }
}
