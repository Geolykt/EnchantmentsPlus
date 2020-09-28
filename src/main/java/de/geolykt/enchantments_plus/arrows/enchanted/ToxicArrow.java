package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.enchantments.Toxic;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.potion.PotionEffectType.CONFUSION;
import static org.bukkit.potion.PotionEffectType.HUNGER;

public class ToxicArrow extends EnchantedArrow {

    public ToxicArrow(Arrow entity, int level, double power) {
        super(entity, level, power);
    }

    public boolean onImpact(final EntityDamageByEntityEvent evt) {
        if (Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) arrow.getShooter(),
            0)) {
            final int value = (int) Math.round(getLevel() * getPower());
            Utilities.addPotion((LivingEntity) evt.getEntity(), CONFUSION, 80 + 60 * value, 4);
            Utilities.addPotion((LivingEntity) evt.getEntity(), HUNGER, 40 + 60 * value, 4);
            if (evt.getEntity() instanceof Player) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
                    ((LivingEntity) evt.getEntity()).removePotionEffect(HUNGER);
                    Utilities.addPotion((LivingEntity) evt.getEntity(), HUNGER, 60 + 40 * value, 0);
                }, 20 + 60 * value);
                Toxic.hungerPlayers.put(evt.getEntity().getUniqueId(), (1 + value) * 5000 + System.currentTimeMillis());
            }
        }
        die();
        return true;
    }
}
