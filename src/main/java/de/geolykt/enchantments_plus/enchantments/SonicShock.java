package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.annotations.AsyncSafe;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class SonicShock extends CustomEnchantment {

    public static final int ID = 75;

    @Override
    public Builder<SonicShock> defaults() {
        return new Builder<>(SonicShock::new, ID)
            .maxLevel(3)
            .loreName("Sonic Shock")
            .probability(0)
            .enchantable(new Tool[]{Tool.WINGS})
            .conflicting()
            .description("Damages mobs when flying past at high speed")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.NONE)
            .base(BaseEnchantments.SONIC_SHOCK);
    }

    @Override
    @AsyncSafe
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        if (player.isGliding() && player.getVelocity().length() >= 1) {
            for (Entity e : player.getNearbyEntities(2 + 2 * level, 4, 2 + 2 * level)) {
                double damage = player.getVelocity().length() * 1.5 * level * power;
                // TODO damage the item
                if (e instanceof Monster) {
                    Bukkit.getScheduler().callSyncMethod(Storage.plugin, () -> ADAPTER.attackEntity((Monster) e, player,  damage, false));
                }
            }
        }
        return true;
    }
}
