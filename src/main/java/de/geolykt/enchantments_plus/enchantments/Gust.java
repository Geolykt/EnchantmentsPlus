package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.SWORD;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class Gust extends CustomEnchantment {

    public static final int ID = 25;

    @Override
    public Builder<Gust> defaults() {
        return new Builder<>(Gust::new, ID)
            .maxLevel(1)
            .loreName("Gust")
            .probability(0)
            .enchantable(new Tool[]{SWORD})
            .conflicting(Force.class, RainbowSlam.class)
            .description("Pushes the user through the air at the cost of their health")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.RIGHT)
            .base(BaseEnchantments.GUST);
    }

    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, final boolean usedHand) {
        final Player player = evt.getPlayer();
        if (evt.getAction().equals(RIGHT_CLICK_AIR) || evt.getAction().equals(RIGHT_CLICK_BLOCK)) {
            if (player.getHealth() > 2 && (evt.getClickedBlock() == null ||
                evt.getClickedBlock().getLocation().distance(player.getLocation()) > 2)) {
                final Block blk = player.getTargetBlock(null, 10);
                player.setVelocity(blk.getLocation().toVector().subtract(player.getLocation().toVector())
                                      .multiply(.25 * power));
                player.setFallDistance(-40);
                ADAPTER.damagePlayer(player, 3, EntityDamageEvent.DamageCause.MAGIC);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
                    Utilities.damageTool(evt.getPlayer(), 1, usedHand);
                }, 1);
                return true;
            }
        }
        return false;
    }
}
