package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.BOOTS;
import static org.bukkit.potion.PotionEffectType.JUMP;

public class Meador extends CustomEnchantment {

    public static final int ID = 36;

    @Override
    public Builder<Meador> defaults() {
        return new Builder<>(Meador::new, ID)
            .maxLevel(1)
            .loreName("Meador")
            .probability(0)
            .enchantable(new Tool[]{BOOTS})
            .conflicting(Weight.class, Speed.class, Jump.class)
            .description("Gives the player both a speed and jump boost")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.NONE);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        player.setWalkSpeed((float) Math.min(.5f + level * power * .05f, 1));
        player.setFlySpeed((float) Math.min(.5f + level * power * .05f, 1));
        player.setMetadata("ze.speed", new FixedMetadataValue(Storage.enchantments_plus, System.currentTimeMillis()));
        Utilities.addPotion(player, JUMP, 610, (int) Math.round(power * level + 2));
        return true;
    }
}
