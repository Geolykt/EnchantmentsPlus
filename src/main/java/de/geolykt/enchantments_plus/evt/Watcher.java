package de.geolykt.enchantments_plus.evt;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.potion.PotionEffectType;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enchantments.*;
import de.geolykt.enchantments_plus.util.Utilities;
import de.geolykt.enchantments_plus.enums.PermissionTypes;

import java.util.*;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static de.geolykt.enchantments_plus.util.PermissionHandler.hasPermission;

// This contains extraneous watcher methods that are not relevant to arrows or enchantments
public class Watcher implements Listener {

    // Fires a laser effect from dispensers if a tool with the Laser enchantment is
    // dispensed
    @EventHandler
    public void onBlockDispense(BlockDispenseEvent evt) {
        Config config = Config.get(evt.getBlock().getWorld());
        if (evt.getBlock().getType() == DISPENSER) {
            ItemStack stk = evt.getItem();
            if (stk != null) {
                Laser ench = null;
                for (CustomEnchantment e : config.getEnchants()) {
                    if (e.getClass().equals(Laser.class)) {
                        ench = (Laser) e;
                    }
                }
                if (ench == null) {
                    return;
                }
                if (CustomEnchantment.getEnchants(stk, config.getWorld()).containsKey(ench)
                        && !stk.getType().equals(ENCHANTED_BOOK)) {
                    evt.setCancelled(true);
                    int level = CustomEnchantment.getEnchants(stk, config.getWorld()).get(ench);
                    int range = 6 + (int) Math.round(level * ench.getPower() * 3);
                    Block blk = evt.getBlock()
                            .getRelative(((Directional) evt.getBlock().getState().getData()).getFacing(), range);
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
                                            EntityDamageEvent.DamageCause.FIRE, (double) (1 + (level * 2)));
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
        Location loc = evt.getEntity().getLocation();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
            for (Block block : Grab.grabLocs.keySet()) {
                if (block.getLocation().getBlockX() == loc.getBlockX()
                        && block.getLocation().getBlockY() == loc.getBlockY()
                        && block.getLocation().getBlockZ() == loc.getBlockZ()) {
                    evt.getEntity().teleport(Grab.grabLocs.get(block));
                    evt.getEntity().setPickupDelay(0);
                    for (Entity e : evt.getEntity().getNearbyEntities(1, 1, 1)) {
                        if (e instanceof ExperienceOrb) {
                            Storage.COMPATIBILITY_ADAPTER.collectXP(Grab.grabLocs.get(block),
                                    ((ExperienceOrb) e).getExperience());
                            e.remove();
                        }
                    }
                }
            }
            for (Block block : Vortex.vortexLocs.keySet()) {
                if (block.getLocation().getWorld().equals(loc.getWorld())) {
                    if (block.getLocation().distance(loc) < 2) {
                        evt.getEntity().teleport(Vortex.vortexLocs.get(block));
                        evt.getEntity().setPickupDelay(0);
                        for (Entity e : evt.getEntity().getNearbyEntities(1, 1, 1)) {
                            if (e instanceof ExperienceOrb) {
                                Storage.COMPATIBILITY_ADAPTER.collectXP(Vortex.vortexLocs.get(block),
                                        ((ExperienceOrb) e).getExperience());
                                e.remove();
                            }
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

    // Makes glowing shulkers on an ore block disappear if it is uncovered
    @EventHandler
    public void onOreUncover(BlockBreakEvent evt) {
        for (BlockFace face : Storage.CARDINAL_BLOCK_FACES) {
            Entity blockToRemove = Reveal.GLOWING_BLOCKS.remove(evt.getBlock().getRelative(face).getLocation());
            if (blockToRemove != null) {
                blockToRemove.remove();
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/enchant ")) {
            boolean customEnch = !CustomEnchantment
                    .getEnchants(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer().getWorld())
                    .isEmpty();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
                CustomEnchantment.setGlow(event.getPlayer().getInventory().getItemInMainHand(), customEnch,
                        event.getPlayer().getWorld());
            }, 0);
        }
    }

    // Randomly adds CustomEnchantments to an item based off the overall
    // probability, enchantments' relative
    // probability, and the level at which the item is being enchanted if the player
    // has permission
    @EventHandler
    public void onEnchantItem(EnchantItemEvent evt) {
        if (!hasPermission(evt.getEnchanter(), PermissionTypes.GET)
                || (evt.getItem().getType() == FISHING_ROD && evt.getExpLevelCost() <= 4)) {
            return;
        }

        Config config = Config.get(evt.getEnchantBlock().getWorld());

        Map<CustomEnchantment, Integer> existingEnchants = CustomEnchantment.getEnchants(evt.getItem(),
                evt.getEnchantBlock().getWorld());

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
                    if (ench.getConflicting().contains(e.getClass()) || addedEnchants.containsKey(ench)
                            || e.getProbability() <= 0) {
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

            double decision = (Storage.rnd.nextFloat() * totalChance) / Math.pow(config.getEnchantRarity(), l);
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
            pair.getKey().setEnchantment(stk, pair.getValue(), config.getWorld());
        }

        if (evt.getItem().getType().equals(ENCHANTED_BOOK)) {

            List<String> finalLore = stk.getItemMeta().getLore();
            Inventory inv = evt.getInventory();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
                ItemStack book = inv.getItem(0);
                ItemMeta bookMeta = book.getItemMeta();
                bookMeta.setLore(finalLore);
                book.setItemMeta(bookMeta);
                inv.setItem(0, book);
            }, 0);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
            CustomEnchantment.setGlow(stk, !addedEnchants.isEmpty(), config.getWorld());
        }, 0);

    }

    // Removes certain potion effects given by enchantments when the enchanted items
    // are removed
    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getInventory() != null) {
            if (evt.getSlotType().equals(SlotType.ARMOR)) {
                if (evt.getCurrentItem().hasItemMeta()) {
                    if (evt.getCurrentItem().getItemMeta().hasLore()) {
                        Player player = (Player) evt.getWhoClicked();
                        for (CustomEnchantment e : CustomEnchantment
                                .getEnchants(evt.getCurrentItem(), player.getWorld()).keySet()) {
                            if (e.getClass().equals(Jump.class) || e.getClass().equals(Meador.class)) {
                                player.removePotionEffect(PotionEffectType.JUMP);
                            }
                            if (e.getClass().equals(NightVision.class)) {
                                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                            }
                            if (e.getClass().equals(Weight.class)) {
                                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            }
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
                && (Toxic.hungerPlayers.getOrDefault(evt.getPlayer().getUniqueId(), -1l) > System.currentTimeMillis())) {
            evt.setCancelled(true);
        }
    }

    // Prevents arrows with the 'ze.arrow' metadata from being able to be picked up
    // by removing them
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent evt) {
        if (evt.getItem().hasMetadata("ze.arrow")) {
            evt.getItem().remove();
            evt.setCancelled(true);
        }
    }

}
