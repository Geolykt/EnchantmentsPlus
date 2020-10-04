package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

import java.util.List;

import static de.geolykt.enchantments_plus.enums.Tool.SWORD;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class Force extends CustomEnchantment {

    public static final int ID = 16;

    @Override
    public Builder<Force> defaults() {
        return new Builder<>(Force::new, ID)
            .maxLevel(3)
            .loreName("Force")
            .probability(0)
            .enchantable(new Tool[]{SWORD})
            .conflicting(RainbowSlam.class, Gust.class)
            .description("Pushes and pulls nearby mobs, configurable through shift clicking")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.BOTH)
            .base(BaseEnchantments.FORCE);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        Player player = evt.getPlayer();
        if (!evt.getPlayer().hasMetadata("ze.force.direction")) {
            player.setMetadata("ze.force.direction", new FixedMetadataValue(Storage.plugin, true));
        }
        if (player.isSneaking() && (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK)) {
            boolean b = !player.getMetadata("ze.force.direction").get(0).asBoolean();
            player.setMetadata("ze.force.direction", new FixedMetadataValue(Storage.plugin, b));
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + (b ? "Push Mode" : "Pull Mode"));
            return false;
        }
        boolean mode = player.getMetadata("ze.force.direction").get(0).asBoolean();
        if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            List<Entity> nearEnts = player.getNearbyEntities(5, 5, 5);
            if (!nearEnts.isEmpty()) {
                if (player.getFoodLevel() >= 2) {
                    if (Storage.rnd.nextInt(10) == 5) {
                        FoodLevelChangeEvent event = new FoodLevelChangeEvent(player, 2);
                        Bukkit.getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            player.setFoodLevel(player.getFoodLevel() - 2);
                        }
                    }
                    for (Entity ent : nearEnts) {
                        Location playLoc = player.getLocation();
                        Location entLoc = ent.getLocation();
                        Location total = mode ? entLoc.subtract(playLoc) : playLoc.subtract(entLoc);
                        org.bukkit.util.Vector vect = new Vector(total.getX(), total.getY(), total.getZ())
                            .multiply((.1f + (power * level * .2f)));
                        vect.setY(vect.getY() > 1 ? 1 : -1);
                        if (ent instanceof LivingEntity && ADAPTER.attackEntity((LivingEntity) ent, player, 0)) {
                            ent.setVelocity(vect);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
