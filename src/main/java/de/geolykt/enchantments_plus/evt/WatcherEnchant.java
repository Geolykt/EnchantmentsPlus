package de.geolykt.enchantments_plus.evt;

import static de.geolykt.enchantments_plus.enums.Tool.BOW;
import static org.bukkit.Material.AIR;
import static org.bukkit.entity.EntityType.HORSE;
import static org.bukkit.entity.EntityType.VILLAGER;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE;
import static org.bukkit.inventory.EquipmentSlot.HAND;
import static org.bukkit.potion.PotionEffectType.FAST_DIGGING;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.EnchantPlayer;
import de.geolykt.enchantments_plus.HighFrequencyRunnableCache;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.annotations.EffectTask;
import de.geolykt.enchantments_plus.enchantments.FrozenStep;
import de.geolykt.enchantments_plus.enchantments.NetherStep;
import de.geolykt.enchantments_plus.enums.Frequency;
import de.geolykt.enchantments_plus.enums.Tool;
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

    private WatcherEnchant() {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent evt) {
        if (!evt.isCancelled() && !(evt instanceof BlockShredEvent) && evt.getBlock().getType() != AIR) {
            Player player = evt.getPlayer();
            ItemStack usedStack = Utilities.usedStack(player, true);
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onBlockBreak(evt, level, true);
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onBlockShred(BlockShredEvent evt) {
        if (!evt.isCancelled() && evt.getBlock().getType() != AIR) {
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
     */
    public byte protectedBlockQuery(Block block, boolean netherstep_remove, boolean frozenstep_remove) {
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
        if (evt.getClickedBlock() == null || !evt.getClickedBlock().getType().isInteractable()) {
            Player player = evt.getPlayer();
            boolean isMainHand = Utilities.isMainHand(evt.getHand());
            for (ItemStack usedStack : Utilities.getArmorAndMainHandItems(player, isMainHand)) {
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                    return ench.onBlockInteract(evt, level, isMainHand);
                });
            }
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onBlockInteractInteractable(PlayerInteractEvent evt) {
        if (evt.getClickedBlock() == null || evt.getClickedBlock().getType().isInteractable()) {
            Player player = evt.getPlayer();
            boolean isMainHand = Utilities.isMainHand(evt.getHand());
            for (ItemStack usedStack : Utilities.getArmorAndMainHandItems(player, isMainHand)) {
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                    return ench.onBlockInteractInteractable(evt, level, isMainHand);
                });
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent evt) {
        final EntityType[] badEnts = new EntityType[]{HORSE, EntityType.ARMOR_STAND, EntityType.ITEM_FRAME, VILLAGER};
        Player player = evt.getPlayer();
        if (!ArrayUtils.contains(badEnts, evt.getRightClicked().getType())) {
            boolean usedHand = Utilities.isMainHand(HAND);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onEntityInteract(evt, level, usedHand);
            });
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent evt) {
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent evt) {
        if (evt.getEntity().getKiller() != null) {
            Player player = evt.getEntity().getKiller();
            EquipmentSlot slot = evt.getEntity().getLastDamageCause().getCause() == PROJECTILE
                    && Tool.fromItemStack(player.getInventory().getItemInOffHand()) == BOW
                    && Tool.fromItemStack(player.getInventory().getItemInMainHand()) != BOW ? EquipmentSlot.OFF_HAND
                    : EquipmentSlot.HAND;
            boolean usedHand = Utilities.isMainHand(slot);
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
            boolean usedHand = Utilities.isMainHand(HAND);
            if (evt.getEntity() instanceof LivingEntity) {
                for (ItemStack usedStack : Utilities.getArmorAndMainHandItems(player, usedHand)) {
                    CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                        return ench.onEntityHit(evt, level, usedHand);
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
        Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
        Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
        boolean usedHand
                = Utilities.isMainHand(main != Tool.ROD && off == Tool.ROD ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
        ItemStack usedStack = Utilities.usedStack(player, usedHand);
        CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
            return ench.onPlayerFish(evt, level, true);
        });
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent evt) {
        if (!evt.isCancelled() && evt.getEntity() instanceof Player) {
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
        Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
        Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
        boolean usedHand
                = Utilities.isMainHand(main != Tool.SHEAR && off == Tool.SHEAR ? EquipmentSlot.OFF_HAND
                        : EquipmentSlot.HAND);
        ItemStack usedStack = Utilities.usedStack(player, usedHand);
        if (!evt.isCancelled()) {
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onShear(evt, level, true);
            });
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
            Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
            boolean usedHand
                    = Utilities.isMainHand(main != BOW && off == BOW ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onEntityShootBow(evt, level, true);
            });
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent evt) {
        Collection<LivingEntity> affected = evt.getAffectedEntities();
        for (LivingEntity entity : affected) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                AtomicBoolean apply = new AtomicBoolean(true);
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
            Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
            Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
            boolean usedHand = Utilities.isMainHand(
                    main != BOW && main != Tool.ROD && (off == BOW || off == Tool.ROD) ? EquipmentSlot.OFF_HAND
                            : EquipmentSlot.HAND);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onProjectileLaunch(evt, level, usedHand);
            });
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent evt) {
        Player player = evt.getEntity();
        for (ItemStack usedStack : (ItemStack[]) ArrayUtils.addAll(player.getInventory().getArmorContents(), (player.getInventory().getContents()))) {
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onPlayerDeath(evt, level, true);
            });
        }
    }

    @EventHandler
    public void onCombust(EntityCombustByEntityEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack usedStack : (ItemStack[]) ArrayUtils.addAll(player.getInventory().getArmorContents(),
                    player.getInventory().getContents())) {
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                    return ench.onCombust(evt, level, true);
                });
            }
        }
    }

    @EffectTask(Frequency.HIGH) // Fast Scan of Player's Armor and their hand to register enchantments
    public static void scanPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            EnchantPlayer enchPlayer = EnchantPlayer.matchPlayer(player);
            if (enchPlayer != null) {
                enchPlayer.tick();
            }
        }

        // Sweeping scan over the player list for armor enchants
        cache.run();
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
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.getId(), ench.getCooldown());
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
                    EnchantPlayer.matchPlayer(player).setCooldown(ench.getId(), ench.getCooldown());
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
                    EnchantPlayer.matchPlayer(player).setCooldown(ench.getId(), ench.getCooldown());
                }
                return true;
            });
            return ench.onScanHands(player, level, false);
        });

        long currentTime = System.currentTimeMillis();
        if (player.hasMetadata("ze.speed") && (player.getMetadata("ze.speed").get(0).asLong() < currentTime - 1000)) {
            player.removeMetadata("ze.speed", Storage.enchantments_plus);
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            player.setFlySpeed(0.1F);
            player.setWalkSpeed(0.2F);
        }

        if (player.hasMetadata("ze.haste") && (player.getMetadata("ze.haste").get(0).asLong() < currentTime - 1000)) {
            player.removePotionEffect(FAST_DIGGING);
            player.removeMetadata("ze.haste", Storage.enchantments_plus);
        }
    }
}
