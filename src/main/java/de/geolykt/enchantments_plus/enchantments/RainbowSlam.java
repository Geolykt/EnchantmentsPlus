package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.SWORD;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class RainbowSlam extends CustomEnchantment {

    // Entities affected by Rainbow Slam, protected against fall damage in order to deal damage as the attacker
    public static final Set<Entity> rainbowSlamNoFallEntities = new HashSet<>();
    public static final int         ID                        = 48;

    @Override
    public Builder<RainbowSlam> defaults() {
        return new Builder<>(RainbowSlam::new, ID)
            .maxLevel(4)
            .loreName("Rainbow Slam")
            .probability(0)
            .enchantable(new Tool[]{SWORD})
            .conflicting(Force.class, Gust.class)
            .description("Attacks enemy mobs with a powerful swirling slam")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.RIGHT)
            .base(BaseEnchantments.RAINBOW_SLAM);
    }

    @Override
    public boolean onEntityInteract(final PlayerInteractEntityEvent evt, final int level, boolean usedHand) {
        if (!(evt.getRightClicked() instanceof LivingEntity) ||
            !ADAPTER.attackEntity((LivingEntity) evt.getRightClicked(), evt.getPlayer(), 0)) {
            return false;
        }
        Utilities.damageTool(evt.getPlayer(), 9, usedHand);
        final LivingEntity ent = (LivingEntity) evt.getRightClicked();
        final Location l = ent.getLocation().clone();
        ent.teleport(l);
        for (int i = 0; i < 30; i++) {
            final int fI = i;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
                for (int j = 0; j < 40; j++) {
                    if (ent.isDead()) {
                        return;
                    }
                    Location loc = l.clone();
                    float t = 30 * fI + j;
                    loc.setY(loc.getY() + (t / 100));
                    loc.setX(loc.getX() + Math.sin(Math.toRadians(t)) * t / 330);
                    loc.setZ(loc.getZ() + Math.cos(Math.toRadians(t)) * t / 330);

                    ent.getWorld().spawnParticle(Particle.REDSTONE, loc, 1,
                        new Particle.DustOptions(
                            Color.fromRGB(Storage.rnd.nextInt(256), Storage.rnd.nextInt(256),
                                Storage.rnd.nextInt(256)),
                            1.0f));
                    loc.setY(loc.getY() + 1.3);
                    ent.setVelocity(loc.toVector().subtract(ent.getLocation().toVector()));
                }
            }, i);
        }
        AtomicBoolean applied = new AtomicBoolean(false);
        rainbowSlamNoFallEntities.add(ent);
        for (int i = 0; i < 3; i++) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
                // ent.setNoDamageTicks(20); // Prevent fall damage
                ent.setVelocity(l.toVector().subtract(ent.getLocation().toVector()).multiply(.3));
                ent.setFallDistance(0);
                if (ent.isOnGround() && !applied.get()) {
                    applied.set(true);
                    rainbowSlamNoFallEntities.remove(ent);
                    ADAPTER.attackEntity(ent, evt.getPlayer(), level * power);
                    for (int c = 0; c < 1000; c++) {
                        // Vector v = new Vector(Math.sin(Math.toRadians(c)), Storage.rnd.nextFloat(), Math.cos(Math
                        // .toRadians(c))).multiply(.75);
                        ent.getWorld().spawnParticle(Particle.BLOCK_DUST, Utilities.getCenter(l), 10,
                            evt.getPlayer().getLocation().getBlock().getBlockData());
                    }
                }
            }, 35 + (i * 5));
        }
        return true;
    }
}
