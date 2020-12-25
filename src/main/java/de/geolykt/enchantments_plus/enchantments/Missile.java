package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.admin.MissileArrow;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

public class Missile extends CustomEnchantment {

    public static final int ID = 71;

    @Override
    public Builder<Missile> defaults() {
        return new Builder<>(Missile::new, ID)
            .all("Shoots a missile from the bow",
                    new Tool[]{Tool.BOW},
                    "Missile",
                    1,
                    Hand.RIGHT);
    }

    private Missile() {
        super(BaseEnchantments.MISSILE);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        MissileArrow arrow = new MissileArrow((AbstractArrow) evt.getProjectile());
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        evt.setCancelled(true);
        CompatibilityAdapter.damageTool((Player) evt.getEntity(), 1, usedHand);
        Utilities.removeItem(((Player) evt.getEntity()), Material.ARROW, 1);
        return true;
    }
}
