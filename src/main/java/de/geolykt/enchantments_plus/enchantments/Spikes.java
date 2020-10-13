package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.annotations.AsyncSafe;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Spikes extends CustomEnchantment {

    public static final int ID = 56;

    @Override
    public Builder<Spikes> defaults() {
        return new Builder<>(Spikes::new, ID)
            .maxLevel(3)
            .loreName("Spikes")
            .probability(0)
            .enchantable(new Tool[]{Tool.BOOTS})
            .conflicting()
            .description("Damages entities the player jumps onto")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.NONE)
            .base(BaseEnchantments.SPIKES);
    }

    @Override
    @AsyncSafe
    public boolean onFastScan(Player player, int level, boolean usedHand) { // TODO this could be called within another Event
        if (player.getVelocity().getY() < -0.45) {
            double fall = Math.min(player.getFallDistance(), 20.0);
            for (Entity e : player.getNearbyEntities(1, 2, 1)) {
                if (e instanceof LivingEntity) {
                    double damage = power * level * fall * 0.25;
                    Bukkit.getScheduler().callSyncMethod(Storage.plugin, () -> ADAPTER.attackEntity((LivingEntity) e, player, damage, false));
                }
            }
        }
        return true;
    }
}
