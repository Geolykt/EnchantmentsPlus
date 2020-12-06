package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.*;

public class Anthropomorphism extends CustomEnchantment {

    /**
     * The falling blocks from the Anthropomorphism enchantment that are attacking, moving towards a set target <br>
     * Warning: not iterating over the map in a thread-safe manner will lead to non-deterministic behaviour 
     * as some code that uses this map is running in async! <br>
     * Key: the falling block that is used <br>
     * Value: Key: The power of the enchantment, Value: Vector <br>
     * <br>Thread safe since 1.2.3
     * @since 1.2.3 (existed with another internal Value type since 1.0) 
     */
    public static final Map<FallingBlock, Entry<Double, Vector>> attackBlocks = 
            Collections.synchronizedMap(new HashMap<FallingBlock, Entry<Double, Vector>>());

    /**
     * The UUIDs of Players currently using the Anthropomorphism enchantment <br>
     * Up until 1.2.3 this represented the direct entities of the players currently using the Anthropomorphism enchantment.
     * @since 1.2.3
     */
    private static final List<UUID> anthVortex = new ArrayList<>();

    /**
     * The falling blocks from the Anthropomorphism enchantment that are idle, staying within the relative region<br>
     * Warning: not iterating over the map in a thread-safe manner will lead to non-deterministic behaviour 
     * as some code that uses this map is running in async! <br>
     * Key: the falling block that is used <br>
     * Value: The player that is linked with the Key<br>
     * <br>Thread safe since 1.2.3
     * @since 1.0, Type of key changed in 1.2.3 from Player to UUID
     */
    public static final Map<FallingBlock, UUID> idleBlocks = Collections.synchronizedMap(new HashMap<FallingBlock, UUID>());

    private static final Material[] MAT = new Material[]{STONE, GRAVEL, DIRT, GRASS_BLOCK};
    public static final int ID = 1;
    // Determines if falling entities from Anthropomorphism should fall up or down
    private static boolean fallBool = false;

    @Override
    public Builder<Anthropomorphism> defaults() {
        return new Builder<>(Anthropomorphism::new, ID)
                .all(BaseEnchantments.ANTHROPOMORPHISM, // BASE
                        "Spawns blocks to protect you when right sneak clicking, and attacks entities when left clicking", // DESCRIPTION
                        new Tool[]{Tool.PICKAXE}, // APPLICABLE TOOLS
                        "Anthropomorphism", // NAME
                        1, // MAX LEVEL
                        Hand.BOTH, // APPLICABLE HANDS
                        Pierce.class, Switch.class); // CONFLICTS
    }

    /**
     * Removes Anthropomorphism blocks when they are dead
     * Thread-safe since 1.2.3
     * @since 1.0
     */
    public static void removeCheck() {
        synchronized (idleBlocks) {
            Iterator<FallingBlock> it = idleBlocks.keySet().iterator();
            while (it.hasNext()) {
                FallingBlock b = it.next();
                if (b.isDead()) {
                    it.remove();
                }
            }
        }
        synchronized (attackBlocks) {
            Iterator<FallingBlock> it = attackBlocks.keySet().iterator();
            while (it.hasNext()) {
                FallingBlock b = it.next();
                if (b.isDead()) {
                    it.remove();
                }
            }
        }
    }

    // Moves Anthropomorphism blocks around depending on their state
    public static void entityPhysics() {
        // Move agressive Anthropomorphism Blocks towards a target & attack
        synchronized (attackBlocks) {
            Iterator<FallingBlock> anthroIterator = attackBlocks.keySet().iterator();
            while (anthroIterator.hasNext()) {
                FallingBlock blockEntity = anthroIterator.next();
                if (!anthVortex.contains(idleBlocks.get(blockEntity))) {
                    for (Entity e : blockEntity.getNearbyEntities(7, 7, 7)) {
                        if (e instanceof Monster) {
                            LivingEntity targetEntity = (LivingEntity) e;

                            Vector playerDir = attackBlocks.get(blockEntity) == null
                                    ? new Vector()
                                    : attackBlocks.get(blockEntity).getValue();

                            blockEntity.setVelocity(e.getLocation().add(playerDir.multiply(.75)).subtract(blockEntity.getLocation()).toVector().multiply(0.25));

                            if (targetEntity.getLocation().getWorld().equals(blockEntity.getLocation().getWorld())) {
                                if (targetEntity.getLocation().distance(blockEntity.getLocation()) < 1.2
                                        && blockEntity.hasMetadata("ze.anthrothrower")) {
                                    Player attacker = (Player) blockEntity.getMetadata("ze.anthrothrower").get(0).value();

                                    if (targetEntity.getNoDamageTicks() == 0 && attackBlocks.get(blockEntity) != null
                                            && Storage.COMPATIBILITY_ADAPTER.attackEntity(targetEntity, attacker,
                                                    2.0 * attackBlocks.get(blockEntity).getKey(), false)) {
                                        targetEntity.setNoDamageTicks(0);
                                        anthroIterator.remove();
                                        blockEntity.remove();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Move passive Anthropomorphism Blocks around
        fallBool = !fallBool;
        synchronized (idleBlocks) {
            for (FallingBlock b : idleBlocks.keySet()) {
                if (anthVortex.contains(idleBlocks.get(b))) {
                    Player player = Bukkit.getPlayer(idleBlocks.get(b));
                    if (player == null) {
                        b.remove();
                        continue;
                    }
                    Location loc = player.getLocation();
                    Vector v;
                    if (b.getLocation().getWorld().equals(loc.getWorld())) { // Check world teleport
                        if (fallBool && b.getLocation().distance(loc) < 10) {
                            v = b.getLocation().subtract(loc).toVector();
                        } else {
                            double x = 6f * Math.sin(b.getTicksLived() / 10f);
                            double z = 6f * Math.cos(b.getTicksLived() / 10f);
                            Location tLoc = loc.clone();
                            tLoc.setX(tLoc.getX() + x);
                            tLoc.setZ(tLoc.getZ() + z);
                            v = tLoc.subtract(b.getLocation()).toVector();
                        }
                        v.multiply(.05);
                        boolean close = false;
                        for (int x = -3; x < 0; x++) {
                            if (b.getLocation().getBlock().getRelative(0, x, 0).getType() != AIR) {
                                close = true;
                            }
                        }
                        if (close) {
                            v.setY(Math.abs(Math.sin(b.getTicksLived() / 10f)));
                        } else {
                            v.setY(0);
                        }
                        b.setVelocity(v);
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        Player player = evt.getPlayer();
        UUID uid = player.getUniqueId();
        ItemStack hand = Utilities.usedStack(player, usedHand);

        if (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                if (!anthVortex.contains(uid)) {
                    anthVortex.add(uid);
                }
                synchronized (idleBlocks.values()) {
                    int counter = 0;
                    for (UUID p : idleBlocks.values()) {
                        if (p.equals(uid) && ++counter > 64) {
                            return false;
                        }
                    }
                }
                if (player.getInventory().contains(COBBLESTONE)) {
                    Utilities.removeItem(player, COBBLESTONE, 1);
                    CompatibilityAdapter.damageTool(player, 2, usedHand);
                    Location loc = player.getLocation();
                    FallingBlock blockEntity
                            = loc.getWorld().spawnFallingBlock(loc, Bukkit.createBlockData(MAT[ThreadLocalRandom.current().nextInt(4)]));
                    blockEntity.setDropItem(false);
                    blockEntity.setGravity(false);
                    blockEntity
                            .setMetadata("ze.anthrothrower", new FixedMetadataValue(Storage.plugin, player));
                    idleBlocks.put(blockEntity, player.getUniqueId());
                    return true;
                }
            }
            return false;
        } else if ((evt.getAction() == LEFT_CLICK_AIR || evt.getAction() == LEFT_CLICK_BLOCK)
                || hand.getType() == AIR) {
            anthVortex.remove(uid);
            List<FallingBlock> toRemove = new ArrayList<>();
            synchronized (idleBlocks) {
                for (FallingBlock blk : idleBlocks.keySet()) {
                    if (idleBlocks.get(blk).equals(uid)) {
                        blk.setGravity(true);
                        blk.setGlowing(true);
                        attackBlocks.put(blk, new SimpleEntry<Double, Vector>(power, player.getLocation().getDirection()));
                        toRemove.add(blk);
                        Block targetBlock = player.getTargetBlock(null, 7);
                        blk.setVelocity(targetBlock
                                .getLocation().subtract(player.getLocation()).toVector().multiply(.25));
                    }
                }
            }
            // This is done because of concurrency issues
            toRemove.forEach(idleBlocks::remove);
        }
        return false;
    }
}
