package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.ReaperArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.potion.PotionEffectType.BLINDNESS;

public class Reaper extends CustomEnchantment {

    public static final int ID = 49;

    @Override
    public Builder<Reaper> defaults() {
        return new Builder<>(Reaper::new, ID)
            .all("Gives the target temporary wither effect and blindness",
                    new Tool[]{Tool.SWORD, Tool.BOW},
                    "Reaper",
                    4, // MAX LVL
                    Hand.BOTH);
    }

    public Reaper() {
        super(BaseEnchantments.REAPER);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        ReaperArrow arrow = new ReaperArrow((AbstractArrow) evt.getProjectile(), level, power);
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

    @Override
    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if (evt.getEntity() instanceof LivingEntity &&
            ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0, false)) {
            int pow = (int) Math.round(level * power);
            int dur = (int) Math.round(10 + level * 20 * power);
            Utilities.addPotion((LivingEntity) evt.getEntity(), PotionEffectType.WITHER, dur, pow);
            Utilities.addPotion((LivingEntity) evt.getEntity(), BLINDNESS, dur, pow);
        }
        return true;
    }
}
