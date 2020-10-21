package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class SonicShock extends CustomEnchantment {

    public static final int ID = 75;

    @Override
    public Builder<SonicShock> defaults() {
        return new Builder<>(SonicShock::new, ID)
            .all(BaseEnchantments.SONIC_SHOCK,
                    0,
                    "Damages mobs when flying past at high speed",
                    new Tool[]{Tool.WINGS},
                    "Sonic Shock",
                    3, // MAX LVL
                    1.0,
                    Hand.NONE);
    }

    @Override
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        if (player.isGliding() && player.getVelocity().length() >= 1) {
            for (Entity e : player.getNearbyEntities(2 + 2 * level, 4, 2 + 2 * level)) {
                double damage = player.getVelocity().length() * 1.5 * level * power;
                if (e instanceof Monster) {
                    ADAPTER.attackEntity((LivingEntity) e, player,  damage, false);
                }
            }
        }
        return true;
    }
}
