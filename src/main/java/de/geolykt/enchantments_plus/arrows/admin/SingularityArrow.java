package de.geolykt.enchantments_plus.arrows.admin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;

import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enchantments.Singularity;

public class SingularityArrow extends EnchantedArrow {

    public SingularityArrow(AbstractArrow entity, int level) {
        super(entity, level);
    }

    public void onImpact() {
        final Location l = arrow.getLocation().clone();
        Singularity.blackholes.put(l, true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
            Singularity.blackholes.put(l, false);
        }, 40);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
            Singularity.blackholes.remove(l);
        }, 60);
        for (int i = 1; i <= 61; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                CompatibilityAdapter.display(l, Particle.SMOKE_LARGE, 50, .001f, .75f, .75f, .75f);
                l.getWorld().playSound(l, Sound.ENTITY_ENDER_DRAGON_GROWL, 10f, .1f);
            }, i);
        }
        die();
    }
}
