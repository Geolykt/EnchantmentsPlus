package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
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
import de.geolykt.enchantments_plus.enums.Tool;

import static de.geolykt.enchantments_plus.enums.Tool.BOW;
import static de.geolykt.enchantments_plus.enums.Tool.SWORD;

import java.util.HashMap;
import java.util.Map;

public class Vortex extends CustomEnchantment {

    // Locations where Vortex has been used on a block and are waiting for the Watcher to handle their teleportation
    public static final Map<Block, Player> vortexLocs = new HashMap<>();
    public static final int ID = 66;

    @Override
    public Builder<Vortex> defaults() {
        return new Builder<>(Vortex::new, ID)
                .maxLevel(1)
                .loreName("Vortex")
                .probability(0)
                .enchantable(new Tool[]{BOW, SWORD})
                .conflicting()
                .description("Teleports mob loot and XP directly to the player")
                .cooldown(0)
                .power(-1.0)
                .handUse(Hand.BOTH)
                .base(BaseEnchantments.VORTEX);
    }

    @Override
    public boolean onEntityKill(final EntityDeathEvent evt, int level, boolean usedHand) {
        final Block deathBlock = evt.getEntity().getLocation().getBlock();
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
