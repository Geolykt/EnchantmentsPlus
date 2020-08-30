package de.geolykt.enchantments_plus.enchantments;

import static de.geolykt.enchantments_plus.enums.Tool.BOOTS;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

public class Spikes extends CustomEnchantment {

    public static final int ID = 56;

    @Override
    public Builder<Spikes> defaults() {
        return new Builder<>(Spikes::new, ID)
            .maxLevel(3)
            .loreName("Spikes")
            .probability(0)
            .enchantable(new Tool[]{BOOTS})
            .conflicting()
            .description("Damages entities the player jumps onto")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.NONE)
            .base(BaseEnchantments.SPIKES);
    }

    @Override
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        if (player.getVelocity().getY() < -0.45) {
            for (Entity e : player.getNearbyEntities(1, 2, 1)) {
                double fall = Math.min(player.getFallDistance(), 20.0);
                if (e instanceof LivingEntity) {
                    ADAPTER.attackEntity((LivingEntity) e, player, power * level * fall * 0.25);
                }
            }
        }
        return true;
    }
}
