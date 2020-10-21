package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MysteryFish extends CustomEnchantment {

    // Guardians from the Mystery Fish enchantment and the player they should move towards
    public static final Map<Entity, Player> guardianMove = new HashMap<>();
    public static final int                 ID           = 38;

    @Override
    public Builder<MysteryFish> defaults() {
        return new Builder<>(MysteryFish::new, ID)
            .all(BaseEnchantments.MYSTERY_FISH,
                    "Catches water mobs and fishes",
                    new Tool[]{Tool.ROD},
                    "Mystery Fish",
                    1,
                    Hand.RIGHT);
    }

    @Override
    public boolean onPlayerFish(final PlayerFishEvent evt, int level, boolean usedHand) {
        if (Storage.rnd.nextInt((int) (6-power)) < level) {
            if (evt.getCaught() != null) {
                Location location = evt.getCaught().getLocation();
                switch (Storage.rnd.nextInt(7)) {
                case 0:
                    evt.getPlayer().getWorld().spawnEntity(location, EntityType.SQUID);
                    break;
                case 1:
                    final Entity guardian = evt.getPlayer().getWorld().spawnEntity(location, EntityType.GUARDIAN);
                    guardianMove.put(guardian, evt.getPlayer());
                    break;
                case 2:
                    final Entity elderGuardian = evt.getPlayer().getWorld().spawnEntity(location, EntityType.ELDER_GUARDIAN);
                    guardianMove.put(elderGuardian, evt.getPlayer());
                    break;
                case 3:
                    evt.getPlayer().getWorld().spawnEntity(location, EntityType.COD);
                    break;
                case 4:
                    evt.getPlayer().getWorld().spawnEntity(location, EntityType.PUFFERFISH);
                    break;
                case 5:
                    evt.getPlayer().getWorld().spawnEntity(location, EntityType.SALMON);
                    break;
                case 6:
                    evt.getPlayer().getWorld().spawnEntity(location, EntityType.TROPICAL_FISH);
                    break;
                }
            }
        }
        return true;
    }

    // Move Guardians from MysteryFish towards the player
    public static void guardian() {
        Iterator<Entity> it = guardianMove.keySet().iterator();
        while (it.hasNext()) {
            Guardian g = (Guardian) it.next();
            if (g.getLocation().getWorld().equals(guardianMove.get(g).getWorld())
                    && g.getLocation().distanceSquared(guardianMove.get(g).getLocation()) > 4
                    && g.getTicksLived() < 160) {
                g.setVelocity(
                    guardianMove.get(g).getLocation().toVector().subtract(g.getLocation().toVector()));
            } else {
                it.remove();
            }
        }
    }
}
