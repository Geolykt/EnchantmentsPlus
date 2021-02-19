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
package de.geolykt.enchantments_plus.evt;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enchantments.*;
import de.geolykt.enchantments_plus.util.Utilities;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.PermissionTypes;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

// This contains extraneous watcher methods that are not relevant to arrows or enchantments
public class Watcher implements Listener {

    // Fires a laser effect from dispensers if a tool with the Laser enchantment is
    // dispensed
    // TODO This should be outsourced to a different (sub-) plugin
    @EventHandler
    public void onBlockDispense(BlockDispenseEvent evt) {
        Config config = Config.get(evt.getBlock().getWorld());
        if (evt.getBlock().getBlockData() instanceof Directional) {
            ItemStack stk = evt.getItem();
            int level = CustomEnchantment.getEnchantLevel(config, stk,  BaseEnchantments.LASER);
            if (level != 0 && !stk.getType().equals(ENCHANTED_BOOK)) {
                evt.setCancelled(true);
                int range = 6 + (int) Math.round(level * 3); // TODO also calculate with the power of the enchantment
                Block blk = evt.getBlock()
                        .getRelative(((Directional) evt.getBlock().getBlockData()).getFacing(), range);
                Location play = Utilities.getCenter(evt.getBlock());
                Location target = Utilities.getCenter(blk);
                play.setY(play.getY() - .5);
                target.setY(target.getY() + .5);
                play.setY(play.getY() + 1.1);
                double d = target.distance(play);
                for (int i = 0; i < (int) d * 10; i++) {
                    Location tempLoc = target.clone();
                    tempLoc.setX(play.getX() + (i * ((target.getX() - play.getX()) / (d * 10))));
                    tempLoc.setY(play.getY() + (i * ((target.getY() - play.getY()) / (d * 10))));
                    tempLoc.setZ(play.getZ() + (i * ((target.getZ() - play.getZ()) / (d * 10))));
                    tempLoc.getWorld().spawnParticle(Particle.REDSTONE, tempLoc, 1,
                            new Particle.DustOptions(Color.RED, 0.75f));
                    for (Entity ent : Bukkit.getWorld(play.getWorld().getName()).getEntities()) {
                        if (ent.getLocation().distance(tempLoc) < .75) {
                            if (ent instanceof LivingEntity) {
                                EntityDamageEvent event = new EntityDamageEvent(ent,
                                        DamageCause.FIRE, (double) (1 + (level * 2)));
                                Bukkit.getPluginManager().callEvent(event);
                                ent.setLastDamageCause(event);
                                if (!event.isCancelled()) {
                                    ((LivingEntity) ent).damage((double) (1 + (level * 2)));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Prevents falling block entities from Anthropomorphism from becoming solid
    // blocks or disappearing
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent evt) {
        if ((evt.getEntityType() == EntityType.FALLING_BLOCK)) {
            if (Anthropomorphism.idleBlocks.containsKey(evt.getEntity())
                    || Anthropomorphism.attackBlocks.containsKey(evt.getEntity())) {
                evt.setCancelled(true);
            }
        }
    }

    // Prevents mobs affected by Rainbow Slam from being hurt by generic "FALL"
    // event. Damage is instead dealt via an
    // EDBEe in order to make protections and money drops work
    @EventHandler
    public void onEntityFall(EntityDamageEvent evt) {
        if (evt.getCause() == DamageCause.FALL && RainbowSlam.rainbowSlamNoFallEntities.contains(evt.getEntity())) {
            evt.setCancelled(true);
        }
    }

    // Teleports item stacks to a certain location as they are created from breaking
    // a block or killing an entity if
    // a Grab or Vortex enchantment was used
    @EventHandler
    public void onItemSpawn(final ItemSpawnEvent evt) {
        if (Fire.cancelledItemDrops.contains(evt.getLocation().getBlock())) {
            evt.setCancelled(true);
            return;
        }
        final Location loc = evt.getEntity().getLocation();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
            for (Location grabLoc : Grab.grabLocs.keySet()) {
                if (grabLoc.getWorld().equals(loc.getWorld()) && grabLoc.distanceSquared(loc) < 2) {
                    evt.getEntity().teleport(Grab.grabLocs.get(grabLoc));
                    evt.getEntity().setPickupDelay(0);
                    for (Entity e : evt.getEntity().getNearbyEntities(1, 1, 1)) {
                        if (e instanceof ExperienceOrb) {
                            Grab.grabLocs.get(grabLoc).giveExp(((ExperienceOrb) e).getExperience());
                            e.remove();
                        }
                    }
                }
            }
            for (Location vortexLoc : Vortex.vortexLocs.keySet()) {
                if (loc.getWorld().equals(vortexLoc.getWorld()) && (vortexLoc.distanceSquared(loc) < 16)) {
                    evt.getEntity().teleport(Vortex.vortexLocs.get(vortexLoc));
                    evt.getEntity().setPickupDelay(0);
                    for (Entity e : evt.getEntity().getNearbyEntities(1, 1, 1)) {
                        if (e instanceof ExperienceOrb) {
                            Vortex.vortexLocs.get(vortexLoc).giveExp(((ExperienceOrb) e).getExperience());
                            e.remove();
                        }
                    }
                    
                }
            }
        }, 1);
    }

    // Prevents players from harvesting materials from the Water Walker and Fire
    // Walker trails
    @EventHandler
    public void onIceOrLavaBreak(BlockBreakEvent evt) {
        if (FrozenStep.frozenLocs.containsKey(evt.getBlock().getLocation())
                || NetherStep.netherstepLocs.containsKey(evt.getBlock().getLocation())) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/enchant ")) {
            boolean customEnch = !CustomEnchantment
                    .getEnchants(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer().getWorld(), null)
                    .isEmpty();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                CustomEnchantment.setGlow(event.getPlayer().getInventory().getItemInMainHand(), customEnch,
                        Config.get(event.getPlayer().getWorld()));
            }, 0);
        }
    }

    // Randomly adds CustomEnchantments to an item based off the overall
    // probability, enchantments' relative
    // probability, and the level at which the item is being enchanted if the player
    // has permission
    // TODO maybe outsource this to a different plugin or make it toggleable as this may not be used by many professional servers.
    @EventHandler
    public void onEnchantItem(EnchantItemEvent evt) {
        if (!PermissionTypes.GET.hasPermission(evt.getEnchanter())
                || (evt.getItem().getType() == FISHING_ROD && evt.getExpLevelCost() <= 4)) {
            return;
        }

        Config config = Config.get(evt.getEnchantBlock().getWorld());

        Map<CustomEnchantment, Integer> existingEnchants = CustomEnchantment.getEnchants(evt.getItem(),
                evt.getEnchantBlock().getWorld(), null);

        Map<CustomEnchantment, Integer> addedEnchants = new HashMap<>();
        ItemStack stk = evt.getItem();
        for (int l = 1; l <= config.getMaxEnchants() - existingEnchants.size(); l++) {

            float totalChance = 0;
            List<CustomEnchantment> mainPool = new ArrayList<>(config.getEnchants());
            Collections.shuffle(mainPool);
            Set<CustomEnchantment> validPool = new HashSet<>();

            for (CustomEnchantment ench : mainPool) {
                boolean conflicts = false;
                for (CustomEnchantment e : addedEnchants.keySet()) {
                    if (ench.getConflicts().contains(e.asEnum()) || addedEnchants.containsKey(ench)
                            || e.getProbability() <= 0.0) {
                        conflicts = true;
                        break;
                    }
                }
                if (!conflicts
                        && (evt.getItem().getType().equals(BOOK) || ench.validMaterial(evt.getItem().getType()))) {
                    validPool.add(ench);
                    totalChance += ench.getProbability();
                }
            }

            double decision = (ThreadLocalRandom.current().nextFloat() * totalChance) / Math.pow(config.getEnchantRarity(), l);
            float running = 0;
            for (CustomEnchantment ench : validPool) {
                running += ench.getProbability();
                if (running > decision) {
                    int level = Utilities.getEnchantLevel(ench.getMaxLevel(), evt.getExpLevelCost());
                    addedEnchants.put(ench, level);
                    break;
                }
            }
        }
        for (Map.Entry<CustomEnchantment, Integer> pair : addedEnchants.entrySet()) {
            pair.getKey().setEnchantment(stk, pair.getValue(), evt.getEnchantBlock().getWorld());
        }

        if (evt.getItem().getType().equals(ENCHANTED_BOOK)) {

            List<String> finalLore = stk.getItemMeta().getLore();
            Inventory inv = evt.getInventory();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                ItemStack book = inv.getItem(0);
                ItemMeta bookMeta = book.getItemMeta();
                bookMeta.setLore(finalLore);
                book.setItemMeta(bookMeta);
                inv.setItem(0, book);
            }, 0);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
            CustomEnchantment.setGlow(stk, !addedEnchants.isEmpty(), config);
        }, 0);

    }

    // Removes certain potion effects given by enchantments when the enchanted items
    // are removed
    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getSlotType().equals(SlotType.ARMOR)) {
            if (evt.getCurrentItem().hasItemMeta()) {
                if (evt.getCurrentItem().getItemMeta().hasLore()) {
                    Player player = (Player) evt.getWhoClicked();
                    for (CustomEnchantment e : CustomEnchantment.getEnchants(evt.getCurrentItem(), player.getWorld(), null).keySet()) {
                        switch (e.asEnum()) {
                        case MEADOR:
                            player.removePotionEffect(PotionEffectType.SPEED);
                        case JUMP:
                            player.removePotionEffect(PotionEffectType.JUMP);
                            break;
                        case SPEED:
                            player.removePotionEffect(PotionEffectType.SPEED);
                            break;
                        case NIGHT_VISION:
                            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                            break;
                        case WEIGHT:
                            if (player.getPersistentDataContainer().has(Weight.ACTIVE, PersistentDataType.BYTE)) {
                                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                                player.removePotionEffect(PotionEffectType.SLOW);
                                player.getPersistentDataContainer().remove(Weight.ACTIVE);
                            }
                            break;
                        default:
                            break;
                        }
                    }
                }
            }
        }
    }

    // Prevents players from being able to eat if they are stored within the
    // 'hungerPlayers' set in Storage
    @EventHandler
    public void onEat(PlayerInteractEvent evt) {
        if (evt.getPlayer().getInventory().getItemInMainHand().getType().isEdible()
                && (evt.getAction().equals(RIGHT_CLICK_AIR) || evt.getAction().equals(RIGHT_CLICK_BLOCK))
                && (Toxic.hungerPlayers.getOrDefault(evt.getPlayer().getUniqueId(), -1L) > System.currentTimeMillis())) {
            evt.setCancelled(true);
        }
    }

    // Prevents arrows with the 'ze.arrow' metadata from being able to be picked up
    // by removing them
    // FIXME is this even needed nowadays?
    @EventHandler
    @Deprecated(forRemoval = false, since = "3.0.0")
    public void onArrowPickup(PlayerPickupArrowEvent evt) {
        if (evt.getItem().hasMetadata("ze.arrow")) {
            evt.getItem().remove();
            evt.setCancelled(true);
        }
    }

}
