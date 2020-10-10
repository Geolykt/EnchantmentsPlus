package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.ToxicArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.potion.PotionEffectType.CONFUSION;
import static org.bukkit.potion.PotionEffectType.HUNGER;

public class Toxic extends CustomEnchantment {

    // Players that have been affected by the Toxic enchantment who cannot currently eat
    public static final Map<UUID, Long> hungerPlayers = new HashMap<>();
    public static final int                  ID            = 62;

    @Override
    public Builder<Toxic> defaults() {
        return new Builder<>(Toxic::new, ID)
            .maxLevel(4)
            .loreName("Toxic")
            .probability(0)
            .enchantable(new Tool[]{Tool.BOW, Tool.SWORD})
            .conflicting()
            .description("Sickens the target, making them nauseous and unable to eat")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.BOTH)
            .base(BaseEnchantments.TOXIC);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        ToxicArrow arrow = new ToxicArrow((Arrow) evt.getProjectile(), level, power);
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

    @Override
    public boolean onEntityHit(final EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if (!(evt.getEntity() instanceof LivingEntity) ||
            !ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0)) {
            final int value = (int) Math.round(level * power);
            Utilities.addPotion((LivingEntity) evt.getEntity(), CONFUSION, 80 + 60 * value, 4);
            Utilities.addPotion((LivingEntity) evt.getEntity(), HUNGER, 40 + 60 * value, 4);
            if (evt.getEntity() instanceof Player) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                    ((LivingEntity) evt.getEntity()).removePotionEffect(HUNGER);
                    Utilities.addPotion((LivingEntity) evt.getEntity(), HUNGER, 60 + 40 * value, 0);
                }, 20 + 60 * value);
                hungerPlayers.put(evt.getEntity().getUniqueId(), (1 + value) * 5000 + System.currentTimeMillis());
            }
        }
        return true;
    }
}
