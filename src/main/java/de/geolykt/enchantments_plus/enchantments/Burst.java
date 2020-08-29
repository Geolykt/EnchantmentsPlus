package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.MultiArrow;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.BOW;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class Burst extends CustomEnchantment {

    public static final int ID = 8;

    @Override
    public Builder<Burst> defaults() {
        return new Builder<>(Burst::new, ID)
                .maxLevel(3)
                .loreName("Burst")
                .probability(0)
                .enchantable(new Tool[]{BOW})
                .conflicting(Spread.class)
                .description("Rapidly fires arrows in series")
                .cooldown(0)
                .power(1.0)
                .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
        final Player player = evt.getPlayer();
        final ItemStack hand = Utilities.usedStack(player, usedHand);
        boolean result = false;
        if (evt.getAction().equals(RIGHT_CLICK_AIR) || evt.getAction().equals(RIGHT_CLICK_BLOCK)) {
            for (int i = 0; i <= (int) Math.round((power * level) + 1); i++) {
                if ((hand.containsEnchantment(Enchantment.ARROW_INFINITE) && Utilities.hasItem(player, Material.ARROW, 1))
                        || Utilities.removeItem(player, Material.ARROW, 1)) {
                    result = true;
                    Utilities.setHand(player, hand, usedHand);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
                        Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation(),
                                player.getLocation().getDirection(), 1, 0);
                        arrow.setShooter(player);
                        if (hand.containsEnchantment(Enchantment.ARROW_FIRE)) {
                            arrow.setFireTicks(Integer.MAX_VALUE);
                        }
                        arrow.setVelocity(player.getLocation().getDirection().normalize().multiply(1.7));
                        EntityShootBowEvent shootEvent = new EntityShootBowEvent(player, hand, arrow, 1f);
                        ProjectileLaunchEvent launchEvent = new ProjectileLaunchEvent(arrow);
                        Bukkit.getPluginManager().callEvent(shootEvent);
                        Bukkit.getPluginManager().callEvent(launchEvent);
                        if (shootEvent.isCancelled() || launchEvent.isCancelled()) {
                            arrow.remove();
                        } else {
                            arrow.setMetadata("ze.arrow", new FixedMetadataValue(Storage.enchantments_plus, null));
                            arrow.setCritical(true);
                            EnchantedArrow.putArrow(arrow, new MultiArrow(arrow), player);
                            Utilities.damageTool(player, 1, usedHand);
                        }

                    }, i * 2);
                }
            }
        }
        return result;
    }

}
