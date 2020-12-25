package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.block.BlockFace.DOWN;

public class BlazesCurse extends CustomEnchantment {

    private static final float   submergeDamage = 1.5f;
    private static final float   rainDamage     = .5f;
    public static final  int     ID             = 5;

    @Override
    public Builder<BlazesCurse> defaults() {
        return new Builder<>(BlazesCurse::new, ID)
                .all("Causes the player to be unharmed in lava and fire, but damages them in water and rain",
                    new Tool[]{Tool.CHESTPLATE},
                    "Blaze's Curse",
                    1, // MAX LVL
                    Hand.NONE);
    }

    public BlazesCurse() {
        super(BaseEnchantments.BLAZES_CURSE);
    }

    @Override
    public boolean onEntityDamage(EntityDamageEvent evt, int level, boolean usedHand) {
        switch (evt.getCause()) {
        case HOT_FLOOR:
        case LAVA:
        case FIRE:
        case FIRE_TICK:
            evt.setCancelled(true);
            return true;
        default:
            return false;
        }
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
        player.setFireTicks(0);
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
            && !Storage.COMPATIBILITY_ADAPTER.dryBiomes().contains(player.getLocation().getBlock().getBiome())) {
            if (player.getWorld().getHighestBlockYAt(player.getLocation()) < player.getLocation().getBlockY()) {
                ADAPTER.damagePlayer(player, rainDamage, EntityDamageEvent.DamageCause.CUSTOM);
            }
        }
        return true;
    }
}
