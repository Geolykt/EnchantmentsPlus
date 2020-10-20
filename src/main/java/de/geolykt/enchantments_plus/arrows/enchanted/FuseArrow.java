package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.TNT;

public class FuseArrow extends EnchantedArrow {

    public FuseArrow(Arrow entity) {
        super(entity);
    }

    public void onImpact() {
        Location loc = arrow.getLocation();
        for (int i = 1; i < 5; i++) {
            Vector vec = arrow.getVelocity().multiply(.25 * i);
            Location hitLoc = new Location(loc.getWorld(), loc.getX() + vec.getX(), loc.getY() + vec.getY(),
                loc.getZ() + vec.getZ());
            if (hitLoc.getBlock().getType().equals(TNT)) {
                BlockBreakEvent event = new BlockBreakEvent(hitLoc.getBlock(), (Player) arrow.getShooter());
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    hitLoc.getBlock().setType(AIR);
                    hitLoc.getWorld().spawnEntity(hitLoc, EntityType.PRIMED_TNT);
                    die();
                }
                return;
            }
        }
        die();
    }

    public boolean onImpact(EntityDamageByEntityEvent evt) {
        Location l = evt.getEntity().getLocation();
        if (Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) arrow.getShooter(),
            0)) {
            if (evt.getEntity().getType().equals(EntityType.CREEPER)) {
                Creeper c = (Creeper) evt.getEntity();
                Storage.COMPATIBILITY_ADAPTER.explodeCreeper(c, Config.get(evt.getDamager().getWorld()).explosionBlockBreak());
            } else if (evt.getEntity().getType().equals(EntityType.MUSHROOM_COW)) {
                MushroomCow c = (MushroomCow) evt.getEntity();
                if (c.isAdult()) {
                    CompatibilityAdapter.display(l, Particle.EXPLOSION_LARGE, 1, 1f, 0, 0, 0);
                    evt.getEntity().remove();
                    l.getWorld().spawnEntity(l, EntityType.COW);
                    l.getWorld().dropItemNaturally(l, new ItemStack(Material.RED_MUSHROOM, 5));
                }
            }
        }
        die();
        return true;
    }
}
