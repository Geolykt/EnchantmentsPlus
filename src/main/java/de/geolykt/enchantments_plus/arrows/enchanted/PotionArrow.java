package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.util.Utilities;

public class PotionArrow extends EnchantedArrow {

    public PotionArrow(Arrow entity, int level, double power) {
        super(entity, level, power);
    }

    public boolean onImpact(EntityDamageByEntityEvent evt) {
        if (Storage.rnd.nextInt((int) Math.round(10 / (getLevel() * getPower() + 1))) == 1) {
            Utilities.addPotion((LivingEntity) arrow.getShooter(),
                Storage.COMPATIBILITY_ADAPTER.PotionPotions().get(Storage.rnd.nextInt(12)),
                150 + (int) Math.round(getLevel() * getPower() * 50), (int) Math.round(getLevel() * getPower()));
        }
        die();
        return true;
    }
}
