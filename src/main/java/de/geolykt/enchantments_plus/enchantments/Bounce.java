package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

import static de.geolykt.enchantments_plus.enums.Tool.BOOTS;
import static org.bukkit.Material.SLIME_BLOCK;

public class Bounce extends CustomEnchantment {

    public static final int ID = 7;

    @Override
    public Builder<Bounce> defaults() {
        return new Builder<>(Bounce::new, ID)
            .maxLevel(5)
            .loreName("Bounce")
            .probability(0)
            .enchantable(new Tool[]{BOOTS})
            .conflicting()
            .description("Shoots you in the air if you jump on slime blocks")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.NONE)
            .base(BaseEnchantments.BOUNCE);
    }

    @Override
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        if (player.getVelocity().getY() < 0 &&
            (player.getLocation().getBlock().getRelative(0, -1, 0).getType() == SLIME_BLOCK
                || player.getLocation().getBlock().getType() == SLIME_BLOCK
                || (player.getLocation().getBlock().getRelative(0, -2, 0).getType() == SLIME_BLOCK) &&
                (level * power) > 2.0)) {
            if (!player.isSneaking()) {
                player.setVelocity(player.getVelocity().setY(.56 * level * power));
                return true;
            }
            player.setFallDistance(0);
        }
        return false;
    }
}
