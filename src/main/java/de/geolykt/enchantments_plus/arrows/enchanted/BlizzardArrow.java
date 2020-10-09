package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.Particle;
import org.bukkit.entity.*;

import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.potion.PotionEffectType.SLOW;

public class BlizzardArrow extends EnchantedArrow {

    public BlizzardArrow(Arrow entity, int level, double power) {
        super(entity, level, power);
    }

    public void onImpact() {
        Utilities.spawnParticle(Utilities.getCenter(arrow.getLocation()), Particle.CLOUD, 100 * getLevel(), 0.1f,
                getLevel(), 1.5f, getLevel());
        double radius = 1 + getLevel() * getPower();
        for (Entity e : arrow.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof LivingEntity && !e.equals(arrow.getShooter())
                    && Storage.COMPATIBILITY_ADAPTER.attackEntity(
                            (LivingEntity) e, (Player) arrow.getShooter(), 0)) {
                Utilities.addPotion((LivingEntity) e, SLOW, (int) Math.round(50 + getLevel()
                        * getPower() * 50), (int) Math.round(getLevel() * getPower() * 2));
            }
        }
        die();
    }
}
