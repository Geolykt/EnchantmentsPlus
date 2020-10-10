package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.potion.PotionEffectType.SLOW;

public class IceAspect extends CustomEnchantment {

    public static final int ID = 29;

    @Override
    public Builder<IceAspect> defaults() {
        return new Builder<>(IceAspect::new, ID)
            .maxLevel(2)
            .loreName("Ice Aspect")
            .probability(0)
            .enchantable(new Tool[]{Tool.SWORD})
            .conflicting()
            .description("Temporarily freezes the target")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.LEFT)
            .base(BaseEnchantments.ICE_ASPECT);
    }

    @Override
    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        Utilities.addPotion((LivingEntity) evt.getEntity(), SLOW,
            (int) Math.round(40 + level * power * 40), (int) Math.round(power * level * 2));
        Utilities.spawnParticle(Utilities.getCenter(evt.getEntity().getLocation()), Particle.CLOUD, 10, .1f, 1f, 2f, 1f);
        return true;
    }
}
