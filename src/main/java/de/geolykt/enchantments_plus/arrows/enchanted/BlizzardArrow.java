package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.Particle;
import org.bukkit.entity.*;

import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.potion.PotionEffectType.SLOW;

public class BlizzardArrow extends EnchantedArrow {

    /**
     * The Area of effect used by this arrow.
     * @since 2.1.6
     * @see AreaOfEffectable
     */
    private double aoe;
    
    public BlizzardArrow(AbstractArrow entity, int level, double power, double aoeSize) {
        super(entity, level, power);
        aoe = aoeSize;
    }

    public void onImpact() {
        CompatibilityAdapter.display(Utilities.getCenter(arrow.getLocation()), Particle.CLOUD,
                100 * getLevel(), .1f, getLevel(), 1.5f, getLevel());
        for (Entity e : arrow.getNearbyEntities(aoe, aoe, aoe)) {
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
