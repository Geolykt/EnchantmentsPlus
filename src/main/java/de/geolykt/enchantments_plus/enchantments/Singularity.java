package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.admin.SingularityArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Singularity extends CustomEnchantment {

    // Locations of black holes from the singularity enchantment and whether or not they are attracting or repelling
    public static final Map<Location, Boolean> blackholes = new HashMap<>();
    public static final int                    ID         = 72;

    @Override
    public Builder<Singularity> defaults() {
        return new Builder<>(Singularity::new, ID)
            .all("Creates a black hole that attracts nearby entities and then discharges them",
                    new Tool[]{Tool.BOW},
                    "Singularity",
                    1,
                    Hand.RIGHT);
    }

    public Singularity() {
        super(BaseEnchantments.SINGULARITY);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        SingularityArrow arrow = new SingularityArrow((AbstractArrow) evt.getProjectile(), level);
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

    // Moves entities towards the black hole from the Singularity enchantment in pull state
    // Throws entities in the black hole out in reverse state
    public static void blackholes() {
        for (Location l : blackholes.keySet()) {
            for (Entity e : l.getWorld().getNearbyEntities(l, 10, 10, 10)) {
                if (e instanceof Player && ((Player) e).getGameMode() == GameMode.CREATIVE) {
                    continue;
                }
                ThreadLocalRandom rand = ThreadLocalRandom.current();
                if (blackholes.get(l)) {
                    Vector v = l.clone().subtract(e.getLocation()).toVector();
                    v.setX(v.getX() + (-.5f + rand.nextFloat()) * 10); // TODO lots of math that could likely be simplified
                    v.setY(v.getY() + (-.5f + rand.nextFloat()) * 10);
                    v.setZ(v.getZ() + (-.5f + rand.nextFloat()) * 10);
                    e.setVelocity(v.multiply(.35f));
                    e.setFallDistance(0);
                } else {
                    Vector v = e.getLocation().subtract(l.clone()).toVector();
                    v.setX(v.getX() + (-.5f + rand.nextFloat()) * 2);
                    v.setY(v.getY() + rand.nextFloat());
                    v.setZ(v.getZ() + (-.5f + rand.nextFloat()) * 2);
                    e.setVelocity(v.multiply(.35f));
                }
            }
        }
    }
}
