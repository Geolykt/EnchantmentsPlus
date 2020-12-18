package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.SiphonArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Siphon extends CustomEnchantment {

    public static final int ID = 53;

    public static boolean calcAmour = true;

    @Override
    public Builder<Siphon> defaults() {
        return new Builder<>(Siphon::new, ID)
                .all(BaseEnchantments.SIPHON,
                        "Drains the health of the mob that you attack, giving it to you",
                        new Tool[]{Tool.BOW, Tool.SWORD},
                        "Siphon",
                        4,
                        Hand.BOTH);
    }

    @Override
    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if (evt.getEntity() instanceof LivingEntity
                && ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0, false)) {
            Player player = (Player) evt.getDamager();
            double difference = 0;
            if (calcAmour) {
                difference = 0.17 * level * power * evt.getFinalDamage();
            } else {
                difference = 0.17 * level * power * evt.getDamage();
            }
            player.setHealth(player.getHealth() + 
                    Math.min(difference, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() - player.getHealth()));
        }
        return true;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        SiphonArrow arrow = new SiphonArrow((AbstractArrow) evt.getProjectile(), level, power);
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
