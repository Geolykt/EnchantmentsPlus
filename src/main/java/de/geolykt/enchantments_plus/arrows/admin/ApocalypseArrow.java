package de.geolykt.enchantments_plus.arrows.admin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;

import static org.bukkit.Material.FIRE;
import static org.bukkit.entity.EntityType.BLAZE;
import static org.bukkit.potion.PotionEffectType.ABSORPTION;
import static org.bukkit.potion.PotionEffectType.HARM;

public class ApocalypseArrow extends EnchantedArrow {

    public ApocalypseArrow(Arrow entity) {
        super(entity);
    }

    public void onImpact() {
        final Config config = Config.get(arrow.getWorld());
        Location l2 = arrow.getLocation().clone();
        l2.setY(l2.getY() + 1);
        Location[] locs = new Location[]{arrow.getLocation(), l2};
        arrow.getWorld().strikeLightning(l2);
        for (int ls = 0; ls < locs.length; ls++) {
            final Location l = locs[ls];
            final int lsf = ls;
            for (int i = 0; i <= 45; i++) {
                final int c = i + 1;
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
                    Entity ent = l.getWorld().spawnFallingBlock(l, Bukkit.createBlockData(FIRE));
                    Vector v = l.toVector();
                    v.setY(Math.abs(Math.sin(c)));
                    if (lsf % 2 == 0) {
                        v.setZ((Math.sin(c) / 2));
                        v.setX((Math.cos(c) / 2));
                    } else {
                        v.setX((Math.sin(c) / 2));
                        v.setZ((Math.cos(c) / 2));
                    }
                    ent.setVelocity(v.multiply(1.5));
                    TNTPrimed prime = (TNTPrimed) arrow.getWorld().spawnEntity(l, EntityType.PRIMED_TNT);
                    prime.setFuseTicks(200);
                    prime.setYield(config.explosionBlockBreak() ? 4 : 0);
                    Blaze blaze = (Blaze) arrow.getWorld().spawnEntity(l, BLAZE);
                    blaze.addPotionEffect(new PotionEffect(ABSORPTION, 150, 100000));
                    blaze.addPotionEffect(new PotionEffect(HARM, 10000, 1));
                    if (config.explosionBlockBreak()) {
                        Entity crystal = arrow.getWorld().spawnEntity(l, EntityType.ENDER_CRYSTAL);
                        ent.addPassenger(prime);
                        crystal.addPassenger(blaze);
                        prime.addPassenger(crystal);
                    } else {
                        ent.addPassenger(prime);
                        prime.addPassenger(blaze);
                    }
                }, c);
            }
        }
        die();
    }
}
