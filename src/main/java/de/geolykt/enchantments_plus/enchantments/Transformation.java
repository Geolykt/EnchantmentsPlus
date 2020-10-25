package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import static org.bukkit.Material.AIR;

import java.util.concurrent.ThreadLocalRandom;

public class Transformation extends CustomEnchantment {

    public static final int ID = 64;

    @Override
    public Builder<Transformation> defaults() {
        return new Builder<>(Transformation::new, ID)
                .all(BaseEnchantments.TRANSFORMATION,
                        "Occasionally causes the attacked mob to be transformed into its \"similar\" cousin",
                        new Tool[]{Tool.SWORD},
                        "Transformation",
                        3,
                        Hand.LEFT);
    }

    @Override
    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if (evt.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || (evt.getEntity() instanceof Tameable && ((Tameable) evt.getEntity()).isTamed())) {
            return false;
        }
        if (evt.getEntity() instanceof LivingEntity
                && ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0)) {
            if (ThreadLocalRandom.current().nextInt(100) > (100 - (level * power * 8))) {
                LivingEntity newEnt = Storage.COMPATIBILITY_ADAPTER.transformationCycle((LivingEntity) evt.getEntity(),
                        ThreadLocalRandom.current());

                if (newEnt != null) {
                    if (evt.getDamage() > ((LivingEntity) evt.getEntity()).getHealth()) {
                        evt.setCancelled(true);
                    }
                    CompatibilityAdapter.display(evt.getEntity().getLocation(), Particle.HEART, 70, .1f,
                            .5f, 2, .5f);

                    double originalHealth = ((LivingEntity) evt.getEntity()).getHealth();
                    for (ItemStack stk : ((LivingEntity) evt.getEntity()).getEquipment().getArmorContents()) {
                        if (stk.getType() != AIR) {
                            newEnt.getWorld().dropItemNaturally(newEnt.getLocation(), stk);
                        }
                    }
                    if (((LivingEntity) evt.getEntity()).getEquipment().getItemInMainHand().getType() != AIR) {
                        newEnt.getWorld().dropItemNaturally(newEnt.getLocation(),
                                ((LivingEntity) evt.getEntity()).getEquipment().getItemInMainHand());
                    }
                    if (((LivingEntity) evt.getEntity()).getEquipment().getItemInOffHand().getType() != AIR) {
                        newEnt.getWorld().dropItemNaturally(newEnt.getLocation(),
                                ((LivingEntity) evt.getEntity()).getEquipment().getItemInOffHand());
                    }

                    evt.getEntity().remove();

                    newEnt.setHealth(Math.max(1,
                            Math.min(originalHealth, newEnt.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())));

                }
            }
        }
        return true;
    }
}
