/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2021 Geolykt and EnchantmentsPlus contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import java.util.concurrent.ThreadLocalRandom;

public class Stream extends CustomEnchantment {

    private static final Particle[] trailTypes = {
        Particle.CLOUD,
        Particle.CRIT,
        Particle.VILLAGER_HAPPY,
        Particle.REDSTONE,
        Particle.HEART};
    public static final int ID = 74;

    @Override
    public Builder<Stream> defaults() {
        return new Builder<>(Stream::new, ID)
            .all("Creates a trail of particles when in flight",
                    new Tool[]{Tool.WINGS},
                    "Stream",
                    1,
                    Hand.NONE);
    }

    public Stream() {
        super(BaseEnchantments.STREAM);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getItem() == null) {
            return false;
        }
        Player player = evt.getPlayer();

        if (!evt.getPlayer().hasMetadata("ze.stream.mode")) {
            player.setMetadata("ze.stream.mode", new FixedMetadataValue(Storage.plugin, 0));
        }
        if (player.isSneaking() && (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK)) {
            int b = player.getMetadata("ze.stream.mode").get(0).asInt();
            b = b == 4 ? 0 : b + 1;
            player.setMetadata("ze.stream.mode", new FixedMetadataValue(Storage.plugin, b));
            switch (b) {
                case 0:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Clouds");
                    break;
                case 1:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gold Sparks");
                    break;
                case 2:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Green Sparks");
                    break;
                case 3:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Rainbow Dust");
                    break;
                case 4:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Hearts");
                    break;
            }
            evt.setCancelled(true);

            // Prevent auto-equipping
            if ((player.getInventory().getChestplate() == null ||
                player.getInventory().getChestplate().getType() == Material.AIR)) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                    // Always null, should remove player.getInventory().getItemInMainHand() == null.
                    if ((player.getInventory().getItemInMainHand() == null ||
                        player.getInventory().getItemInMainHand().getType() == Material.AIR)) {
                        ItemStack stack = player.getInventory().getChestplate();
                        player.getInventory().setItemInMainHand(stack);
                        player.getInventory().setChestplate(new ItemStack(Material.AIR));
                    }
                }, 0);
            }
        }
        return false;
    }

    @Override
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        if (player.isGliding() && player.getVelocity().length() >= 0.5) {
            if (!player.hasMetadata("ze.stream.mode")) {
                player.setMetadata("ze.stream.mode", new FixedMetadataValue(Storage.plugin, 0));
            }
            int b = player.getMetadata("ze.stream.mode").get(0).asInt();

            switch (b) {
                case 0:
                case 1:
                case 2:
                case 4:
                    player.getWorld().spawnParticle(trailTypes[b], player.getLocation(), 3);
                    break;
                case 3:
                    ThreadLocalRandom rand = ThreadLocalRandom.current();
                    player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 2,
                        new Particle.DustOptions(Color.fromRGB(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)), 1.0f));
            }
            if (b != 3) {
                CompatibilityAdapter.display(player.getLocation(), trailTypes[b], 3, 0.1, 0, 0, 0);
            }
            return true;
        }
        return false;
    }
}
