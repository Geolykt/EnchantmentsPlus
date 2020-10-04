package de.geolykt.enchantments_plus.enchantments;

import static de.geolykt.enchantments_plus.enums.Tool.SWORD;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

public class Conversion extends CustomEnchantment {

    public static final int ID = 10;

    @Override
    public Builder<Conversion> defaults() {
        return new Builder<>(Conversion::new, ID)
            .maxLevel(4)
            .loreName("Conversion")
            .probability(0)
            .enchantable(new Tool[]{SWORD})
            .conflicting()
            .description("Converts XP to health when right clicking and sneaking")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.RIGHT)
            .base(BaseEnchantments.CONVERSION);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Player player = evt.getPlayer();
            if (player.isSneaking()) {
                if (player.getLevel() > 1) {
                    if (player.getHealth() < 20) {
                        player.setLevel((player.getLevel() - 1));
                        player.setHealth(Math.min(20, player.getHealth() + 2 * power * level));
                        for (int i = 0; i < 3; i++) {
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                                Utilities
                                    .display(Utilities.getCenter(player.getLocation()), Particle.HEART, 10, .1f,
                                        .5f, .5f, .5f);
                            }, ((i * 5) + 1));
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
