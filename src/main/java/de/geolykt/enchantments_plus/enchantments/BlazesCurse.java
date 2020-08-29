package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

import static de.geolykt.enchantments_plus.enums.Tool.CHESTPLATE;
import static org.bukkit.Material.*;
import static org.bukkit.block.BlockFace.DOWN;

public class BlazesCurse extends CustomEnchantment {

    private static final float   submergeDamage = 1.5f;
    private static final float   rainDamage     = .5f;
    public static final  int     ID             = 5;

    @Override
    public Builder<BlazesCurse> defaults() {
        return new Builder<>(BlazesCurse::new, ID)
            .maxLevel(1)
            .loreName("Blaze's Curse")
            .probability(0)
            .enchantable(new Tool[]{CHESTPLATE})
            .conflicting()
            .description("Causes the player to be unharmed in lava and fire, but damages them in water and rain")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.NONE);
    }

    @Override
    public boolean onEntityDamage(EntityDamageEvent evt, int level, boolean usedHand) {
        if (evt.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR ||
            evt.getCause() == EntityDamageEvent.DamageCause.LAVA ||
            evt.getCause() == EntityDamageEvent.DamageCause.FIRE ||
            evt.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            evt.setCancelled(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onBeingHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if (evt.getDamager().getType() == EntityType.FIREBALL
            || evt.getDamager().getType() == EntityType.SMALL_FIREBALL) {
            evt.setDamage(0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        Material mat = player.getLocation().getBlock().getType();
        if (mat == WATER) {
            ADAPTER.damagePlayer(player, submergeDamage, EntityDamageEvent.DamageCause.DROWNING);
            return true;
        }
        mat = player.getLocation().getBlock().getRelative(DOWN).getType();
        if (mat == ICE || mat == FROSTED_ICE) {
            ADAPTER.damagePlayer(player, rainDamage, EntityDamageEvent.DamageCause.MELTING);
            return true;
        }
        if (player.getWorld().hasStorm()
            && !Storage.COMPATIBILITY_ADAPTER.DryBiomes().contains(player.getLocation().getBlock().getBiome())) {
            Location check_loc = player.getLocation();
            while (check_loc.getBlockY() < 256) {
                if (!Storage.COMPATIBILITY_ADAPTER.Airs().contains(check_loc.getBlock().getType())) {
                    break;
                }
                check_loc.setY(check_loc.getBlockY() + 1);
            }
            if (check_loc.getBlockY() == 256) {
                ADAPTER.damagePlayer(player, rainDamage, EntityDamageEvent.DamageCause.CUSTOM);
            }
        }
        player.setFireTicks(0);
        return true;
    }
}


