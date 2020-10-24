package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Weight extends CustomEnchantment {

    public static final int ID = 67;
    
    /**
     * Is put on the PDC of a player to mark that the player has the Enchantment active (the slowness was made by the plugin).
     * Used to prevent abuse so players cannot remove permanent slowness effects.
     * @since 2.1.3
     */
    public static final NamespacedKey ACTIVE = new NamespacedKey(Storage.plugin, "weight_active");

    @Override
    public Builder<Weight> defaults() {
        return new Builder<>(Weight::new, ID)
            .all(BaseEnchantments.WEIGHT,
                    "Slows the player down but makes them stronger and more resistant to knockback",
                    new Tool[]{Tool.BOOTS},
                    "Weight",
                    4,
                    Hand.NONE,
                    Meador.class, Speed.class);
    }

    @Override
    public boolean onBeingHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if (!(evt.getEntity() instanceof Player) || //check if victim is a player
            !(evt.getDamager() instanceof LivingEntity) || //check if the damager is alive
            //check if the victim (player) can damage the attacker
            !ADAPTER.attackEntity((LivingEntity) evt.getDamager(), (Player) evt.getEntity(), 0)) {
            return true;
        }
        Player player = (Player) evt.getEntity();
        if (evt.getDamage() < player.getHealth()) {
            //FIXME this looks like bad practice - plugins may not have a say in this and
            //this would prevent the MONITOR priority of being used.
            //This should be changed into something better sometime in the future
            evt.setCancelled(true);
            player.damage(evt.getDamage());
            player.setVelocity(player.getLocation().subtract(evt.getDamager().getLocation()).toVector()
                                         .multiply((float) (1 / (level * power + 1.5))));
            ItemStack[] s = player.getInventory().getArmorContents();
            for (int i = 0; i < 4; i++) {
                if (s[i] != null) {
                    CompatibilityAdapter.damageItem(s[i], 1);
                    if (CompatibilityAdapter.getDamage(s[i]) > s[i].getType().getMaxDurability()) {
                        s[i] = null;
                    }
                }
            }
            player.getInventory().setArmorContents(s);
        }
        return true;
    }

    @Override
    public boolean onPlayerDeath(PlayerDeathEvent evt, int level, boolean usedHand) {
        evt.getEntity().getPersistentDataContainer().remove(ACTIVE);
        return true;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        if (player.hasPotionEffect(PotionEffectType.SLOW)) {
            return false;
        } else {
            player.addPotionEffect(PotionEffectType.SLOW.createEffect(Integer.MAX_VALUE, (int) (level*power)));
            player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(Integer.MAX_VALUE, (int) (level*power)));
            player.getPersistentDataContainer().set(ACTIVE, PersistentDataType.BYTE, (byte) 1);
            return true;
        }
    }
}
