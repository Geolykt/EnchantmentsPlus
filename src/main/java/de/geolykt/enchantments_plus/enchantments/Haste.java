package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.potion.PotionEffectType.FAST_DIGGING;

public class Haste extends CustomEnchantment {

    public static final int ID = 27;

    @Override
    public Builder<Haste> defaults() {
        return new Builder<>(Haste::new, ID)
            .maxLevel(4)
            .loreName("Haste")
            .probability(0)
            .enchantable(new Tool[]{Tool.PICKAXE, Tool.AXE, Tool.SHOVEL})
            .conflicting()
            .description("Gives the player a mining boost")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.NONE)
            .base(BaseEnchantments.HASTE);
    }

    @Override
    public boolean onScanHands(Player player, int level, boolean usedHand) {
        Utilities.addPotion(player, FAST_DIGGING, 610, (int) Math.round(level * power));
        player.setMetadata("ze.haste", new FixedMetadataValue(Storage.plugin, System.currentTimeMillis()));
        return false;
    }

}
