package de.geolykt.enchantments_plus.enchantments;

import static de.geolykt.enchantments_plus.enums.Tool.CHESTPLATE;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

public class Combustion extends CustomEnchantment {

    public static final int ID = 9;

    @Override
    public Builder<Combustion> defaults() {
        return new Builder<>(Combustion::new, ID)
            .maxLevel(4)
            .loreName("Combustion")
            .probability(0)
            .enchantable(new Tool[]{CHESTPLATE})
            .conflicting()
            .description("Lights attacking entities on fire when player is attacked")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.NONE);
    }

    @Override
    public boolean onBeingHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        Entity ent;
        if (evt.getDamager().getType() == EntityType.ARROW) {
            Arrow arrow = (Arrow) evt.getDamager();
            if (arrow.getShooter() instanceof LivingEntity) {
                ent = (Entity) arrow.getShooter();
            } else {
                return false;
            }
        } else {
            ent = evt.getDamager();
        }
        return ADAPTER.igniteEntity(ent, (Player) evt.getEntity(), (int) (50 * level * power));
    }

    public boolean onCombust(EntityCombustByEntityEvent evt, int level, boolean usedHand) {
        if (ADAPTER.isZombie(evt.getCombuster())) {
            evt.setDuration(0);
        }
        return false;
    }
}
