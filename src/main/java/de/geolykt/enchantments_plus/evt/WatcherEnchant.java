package de.geolykt.enchantments_plus.evt;

import static org.bukkit.Material.AIR;
import static org.bukkit.potion.PotionEffectType.FAST_DIGGING;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.EnchantPlayer;
import de.geolykt.enchantments_plus.HighFrequencyRunnableCache;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enchantments.FrozenStep;
import de.geolykt.enchantments_plus.enchantments.NetherStep;
import de.geolykt.enchantments_plus.enchantments.Weight;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.evt.ench.BlockShredEvent;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

// This is the watcher used by the CustomEnchantment class. Each method checks the enchantments on relevant items,
//      ensures that the item is not an enchantment book, and calls each enchantment's method if the player can
//      perform a certain action and the cooldown time is 0. It will add the given enchantment's cooldown to the player
//      if the action performed is successful, determined by each enchantment in their respective classes.
public class WatcherEnchant implements Listener {

    private static final WatcherEnchant INSTANCE = new WatcherEnchant();
    private static final HighFrequencyRunnableCache cache = new HighFrequencyRunnableCache(WatcherEnchant::feedEnchCache, 5);

    public static boolean apply_patch_piston = true;
    public static boolean apply_patch_explosion = true;
    public static boolean patch_cancel_frozenstep = true;
    public static boolean patch_cancel_netherstep = true;
    public static boolean patch_cancel_explosion = true;

    public static WatcherEnchant instance() {
        return INSTANCE;
    }

    private WatcherEnchant() {} // The class should not be constructible

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent evt) {
        if (!(evt instanceof BlockShredEvent) && evt.getBlock().getType() != AIR) {
            Player player = evt.getPlayer();
            ItemStack usedStack = Utilities.usedStack(player, true);
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onBlockBreak(evt, level, true);
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onBlockShred(BlockShredEvent evt) {
        if (evt.getBlock().getType() != AIR) {
            Player player = evt.getPlayer();
            ItemStack usedStack = Utilities.usedStack(player, true);
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onBlockBreak(evt, level, true);
            });
        }
    }

    /**
     * This event is not thrown within regular spigot, however certain plugins may throw it, which is why this EventHandler exists
     * 
     * @param evt The event
     */
    @EventHandler(ignoreCancelled = false)
    public void onBlockExplodeEvent(BlockExplodeEvent evt) {
        if (!apply_patch_explosion) {
            return;
        }
        for (Block block: evt.blockList()) {
            byte b = protectedBlockQuery(block, !patch_cancel_explosion && patch_cancel_netherstep, !patch_cancel_explosion && patch_cancel_frozenstep);
            if (b == 1 && patch_cancel_netherstep && patch_cancel_explosion) {
                evt.setCancelled(true);
            } else if (b == 2 && patch_cancel_frozenstep && patch_cancel_explosion) {
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onEntityExplodeEvent(EntityExplodeEvent evt) {
        if (!apply_patch_explosion) {
            return;
        }
        for (Block block: evt.blockList()) {
            byte b = protectedBlockQuery(block, !patch_cancel_explosion && patch_cancel_netherstep, !patch_cancel_explosion && patch_cancel_frozenstep);
            if (b == 1 && patch_cancel_netherstep && patch_cancel_explosion) {
                evt.setCancelled(true);
            } else if (b == 2 && patch_cancel_frozenstep && patch_cancel_explosion) {
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent evt) {
        if (!apply_patch_piston) {
            return;
        }
        for (Block block: evt.getBlocks()) {
            byte b = protectedBlockQuery(block, !patch_cancel_netherstep, !patch_cancel_frozenstep);
            if (b == 1 && patch_cancel_netherstep) {
                evt.setCancelled(true);
            } else if (b == 2 && patch_cancel_frozenstep) {
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onBlockPistonRetractEvent(BlockPistonRetractEvent evt) {
        if (!apply_patch_piston) {
            return;
        }
        for (Block block: evt.getBlocks()) {
            byte b = protectedBlockQuery(block, !patch_cancel_netherstep, !patch_cancel_frozenstep);
            if (b == 1 && patch_cancel_netherstep) {
                evt.setCancelled(true);
            } else if (b == 2 && patch_cancel_frozenstep) {
                evt.setCancelled(true);
            }
        }
    }

    /**
     * This method returns whether a block is protected by the plugin and whether it should be considered <br>
     * 
     * @param block The block to query
     * @param netherstep_remove Whether to remove the entry, if found and the entry belonged to a netherstep block
     * @param frozenstep_remove Whether to remove the entry, if found and the entry belonged to a frozenstep block
     * @return non 0 values if the Block is considered protected. <br> 
     * 
     * <ul>
     * <li>0 if the block is unprotected</li>
     * <li>1 if the netherstep protects the block</li>
     * <li>2 if the frozenstep protects the block</li>
     * </ul>
     * @since 1.0.0
     */
    public final byte protectedBlockQuery(Block block, boolean netherstep_remove, boolean frozenstep_remove) {
        Location a = block.getLocation();
        if (netherstep_remove) {
            if (NetherStep.netherstepLocs.remove(a) != null) {
                return 1;
            }
        } else {
            if (NetherStep.netherstepLocs.containsKey(a)) {
                return 1;
            }
        }
        if (frozenstep_remove) {
            if (FrozenStep.frozenLocs.remove(a) != null) {
                return 2;
            }
            
        } else {
            if (FrozenStep.frozenLocs.containsKey(a)) {
                return 2;
            }
        }
        return 0;
    }

    @EventHandler(ignoreCancelled = false)
    public void onBlockInteract(PlayerInteractEvent evt) {
        if (evt.getAction() == Action.PHYSICAL) {
            return;
        }
        Player player = evt.getPlayer();
        if (evt.getClickedBlock() == null || !evt.getClickedBlock().getType().isInteractable()) {
            if (evt.getHand() == EquipmentSlot.HAND) {
                CustomEnchantment.applyForTool(player, player.getInventory().getItemInMainHand(), (ench, level) -> {
                    return ench.onBlockInteract(evt, level, true);
                });
            } else {
                CustomEnchantment.applyForTool(player, player.getInventory().getItemInOffHand(), (ench, level) -> {
                    return ench.onBlockInteract(evt, level, false);
                });
            }
        }
        if (evt.getClickedBlock() != null && evt.getClickedBlock().getType().isInteractable()) {
            if (evt.getHand() == EquipmentSlot.HAND) {
                CustomEnchantment.applyForTool(player, player.getInventory().getItemInMainHand(), (ench, level) -> {
                    return ench.onBlockInteractInteractable(evt, level, true);
                });
            } else {
                CustomEnchantment.applyForTool(player, player.getInventory().getItemInOffHand(), (ench, level) -> {
                    return ench.onBlockInteractInteractable(evt, level, false);
                });
            }
        }
    }

    /**
     * Entities that the onEntityInteract function should ignore.
     * @since 3.0.0
     */
    private static final EnumSet<EntityType> BAD_ENTITIES = EnumSet.of(EntityType.HORSE, EntityType.ARMOR_STAND,
            EntityType.ITEM_FRAME, EntityType.VILLAGER);

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent evt) {
        if (!BAD_ENTITIES.contains(evt.getRightClicked().getType())) {
            Player player = evt.getPlayer();
            CustomEnchantment.applyForTool(player, player.getInventory().getItemInMainHand(), (ench, level) -> {
                return ench.onEntityInteract(evt, level, true);
            });
        }
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent evt) {
        if (evt.getEntity().getKiller() != null) {
            Player player = evt.getEntity().getKiller();
            boolean usedHand = !(evt.getEntity().getLastDamageCause().getCause() == DamageCause.PROJECTILE 
                    && Tool.BOW.contains(player.getInventory().getItemInOffHand().getType()));
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onEntityKill(evt, level, usedHand);
            });

        }
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent evt) {
        if (evt.getDamage() <= 0) {
            return;
        }
        if (evt.getDamager() instanceof Player) {
            Player player = (Player) evt.getDamager();
            if (evt.getEntity() instanceof LivingEntity) {
                for (ItemStack usedStack : Utilities.getArmorAndMainHandItems(player, true)) {
                    CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                        return ench.onEntityHit(evt, level, true);
                    });
                }
            }
        }
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack usedStack : Utilities.getArmorAndMainHandItems(player,
                    true)) { // Only check main hand for some reason
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                    return ench.onBeingHit(evt, level, true);
                });
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack usedStack : Utilities.getArmorAndMainHandItems(player, false)) {
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                    return ench.onEntityDamage(evt, level, false);
                });
            }
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent evt) {
        Player player = evt.getPlayer();
        ItemStack usedStack = Utilities.usedStack(player, Tool.ROD.contains(player.getInventory().getItemInMainHand().getType()));
        CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> ench.onPlayerFish(evt, level, true));
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack usedStack : Utilities.getArmorAndMainHandItems(player, true)) {
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                    return ench.onHungerChange(evt, level, true);
                });
            }
        }
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent evt) {
        Player player = evt.getPlayer();
        ItemStack usedStack = Utilities.usedStack(player, Tool.SHEARS.contains(player.getInventory().getItemInMainHand().getType()));
        CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> ench.onShear(evt, level, true));
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent evt) {
        if (evt.getEntity() instanceof Player && evt.getProjectile() instanceof AbstractArrow) {
            Player player = (Player) evt.getEntity();
            ItemStack usedStack = Utilities.usedStack(player, Tool.BOW.contains(player.getInventory().getItemInMainHand().getType()));
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> ench.onEntityShootBow(evt, level, true));
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent evt) {
        Collection<LivingEntity> affected = evt.getAffectedEntities();
        for (LivingEntity entity : affected) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                AtomicBoolean apply = new AtomicBoolean(true); // TODO this is not multithreaded, so why is it needed?
                for (ItemStack usedStack : Utilities.getArmorAndMainHandItems(player, true)) {
                    CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                        // Only apply one enchantment, which in practice is Potion Resistance.
                        // This will always skip execution of the Lambda and return false after a Lambda returned true
                        // once
                        // Yes, I am bored
                        return apply.get() && apply.compareAndSet(ench.onPotionSplash(evt, level, false), false);
                    });
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent evt) {
        if (evt.getEntity().getShooter() != null && evt.getEntity().getShooter() instanceof Player) {
            Player player = (Player) evt.getEntity().getShooter();
            Material main = player.getInventory().getItemInMainHand().getType();
            boolean usedHand = Tool.BOW.contains(main) || Tool.ROD.contains(main);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> ench.onProjectileLaunch(evt, level, usedHand));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(PlayerDeathEvent evt) {
        if (evt.getKeepInventory()) {
            return;
        }

        // TODO make this work with other plugins
        final Player player = evt.getEntity();
        final ItemStack[] contents = player.getInventory().getContents().clone();
        final List<ItemStack> removed = new ArrayList<>();
        final Config config = Config.get(player.getWorld());

        for (int i = 0; i < contents.length; i++) {
            if (CustomEnchantment.hasEnchantment(config, contents[i], BaseEnchantments.WEIGHT)) {
                player.getPersistentDataContainer().remove(Weight.ACTIVE);
            }
            if (CustomEnchantment.hasEnchantment(config, contents[i], BaseEnchantments.BIND)) {
                removed.add(contents[i]);
                evt.getDrops().remove(contents[i]);
            } else {
                contents[i] = null;
            }
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
            if (evt.getKeepInventory()) {
                evt.getDrops().addAll(removed);
            } else {
                player.getInventory().setContents(contents);
            }
        }, 1);
    }

    @EventHandler
    public void onCombust(EntityCombustByEntityEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack usedStack : (ItemStack[]) ArrayUtils.addAll(player.getInventory().getArmorContents(),
                    player.getInventory().getContents())) {
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> ench.onCombust(evt, level, true));
            }
        }
    }

    // Implicitly scheduled MEDIUM_HIGH due to being called by HighFrequencyEnchCache with interval 5
    private static void feedEnchCache(Player player, Consumer<Supplier<Boolean>> consoomer) {
        for (ItemStack stk : player.getInventory().getArmorContents()) {
            CustomEnchantment.applyForTool(player, stk, (ench, level) -> {
                consoomer.accept(() -> {
                    if (!player.isOnline()) {
                        return false;
                    }
                    if (ench.onFastScan(player, level, true)) {
                        EnchantPlayer.setCooldown(player, ench.asEnum(), ench.getCooldownMillis());
                    }
                    return true;
                });
                return ench.onScan(player, level, true);
            });
        }
        CustomEnchantment.applyForTool(player, player.getInventory().getItemInMainHand(), (ench, level) -> {
            consoomer.accept(() -> {
                if (!player.isOnline()) {
                    return false;
                }
                if (ench.onFastScanHands(player, level, true)) {
                    EnchantPlayer.setCooldown(player, ench.asEnum(), ench.getCooldownMillis());
                }
                return true;
            });
            return ench.onScanHands(player, level, true);
        });
        CustomEnchantment.applyForTool(player, player.getInventory().getItemInOffHand(), (ench, level) -> {
            consoomer.accept(() -> {
                if (!player.isOnline()) {
                    return false;
                }
                if (ench.onFastScanHands(player, level, false)) {
                    EnchantPlayer.setCooldown(player, ench.asEnum(), ench.getCooldownMillis());
                }
                return true;
            });
            return ench.onScanHands(player, level, false);
        });

        if (player.hasMetadata("ze.haste") && (player.getMetadata("ze.haste").get(0).asLong() < System.currentTimeMillis() - 1000)) {
            player.removePotionEffect(FAST_DIGGING);
            player.removeMetadata("ze.haste", Storage.plugin);
        }
    }

    /**
     * Runs the internal cache, this disperses less often used tick based events to avoid lag spikes.
     * @since 3.0.0
     */
    public static void runCache() {
        cache.run();
    }
}
