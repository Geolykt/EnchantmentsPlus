package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.VortexArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;
import java.util.Map;

public class Vortex extends CustomEnchantment {

    // Locations where Vortex has been used on a block and are waiting for the Watcher to handle their teleportation
    public static final Map<Location, Player> vortexLocs = new HashMap<>();
    public static final int ID = 66;

    @Override
    public Builder<Vortex> defaults() {
        return new Builder<>(Vortex::new, ID)
                .all(BaseEnchantments.VORTEX,
                        0,
                        "Teleports mob loot and XP directly to the player",
                        new Tool[]{Tool.BOW, Tool.SWORD},
                        "Vortex",
                        1, // MAX LVL
                        1.0,
                        Hand.BOTH);
    }

    @Override
    public boolean onEntityKill(final EntityDeathEvent evt, int level, boolean usedHand) {
        final Location deathBlock = evt.getEntity().getLocation();
        vortexLocs.put(deathBlock, evt.getEntity().getKiller());
        
        evt.getEntity().getKiller().giveExp(evt.getDroppedExp());
        evt.setDroppedExp(0);
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
            vortexLocs.remove(deathBlock);
        }, 3);
        return true;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        VortexArrow arrow = new VortexArrow((Arrow) evt.getProjectile());
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
