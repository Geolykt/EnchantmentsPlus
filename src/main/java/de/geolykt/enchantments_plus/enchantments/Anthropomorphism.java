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
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.*;

import static de.geolykt.enchantments_plus.enums.Tool.PICKAXE;
import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.*;

public class Anthropomorphism extends CustomEnchantment {
    // The falling blocks from the Anthropomorphism enchantment that are attacking, moving towards a set target

    public static final Map<FallingBlock, Pair<Double, Vector>> attackBlocks = new HashMap<>();
    // Players currently using the Anthropomorphism enchantment
    private static final List<Entity> anthVortex = new ArrayList<>();
    // The falling blocks from the Anthropomorphism enchantment that are idle, staying within the relative region
    public static final Map<FallingBlock, Entity> idleBlocks = new HashMap<>();
    private static final Material[] MAT = new Material[]{STONE, GRAVEL, DIRT, GRASS_BLOCK};
    public static final int ID = 1;
    // Determines if falling entities from Anthropomorphism should fall up or down
    private static boolean fallBool = false;

    @Override
    public Builder<Anthropomorphism> defaults() {
        return new Builder<>(Anthropomorphism::new, ID)
                .maxLevel(1)
                .loreName("Anthropomorphism")
                .probability(0)
                .enchantable(new Tool[]{PICKAXE})
                .conflicting(Pierce.class, Switch.class)
                .description(
                        "Spawns blocks to protect you when right sneak clicking, and attacks entities when left clicking")
                .cooldown(0)
                .power(1.0)
                .handUse(Hand.BOTH)
                .base(BaseEnchantments.ANTHROPOMORPHISM);
    }

    // Removes Anthropomorphism blocks when they are dead
    public static void removeCheck() {
        Iterator<FallingBlock> it = idleBlocks.keySet().iterator();
        while (it.hasNext()) {
            FallingBlock b = it.next();
            if (b.isDead()) {
                it.remove();
            }
        }
        it = attackBlocks.keySet().iterator();
        while (it.hasNext()) {
            FallingBlock b = it.next();
            if (b.isDead()) {
                it.remove();
            }
        }
    }

    // Moves Anthropomorphism blocks around depending on their state
    public static void entityPhysics() {
        // Move agressive Anthropomorphism Blocks towards a target & attack
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
                                                2.0 * attackBlocks.get(blockEntity).getKey())) {
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
        // Move passive Anthropomorphism Blocks around
        fallBool = !fallBool;
        for (FallingBlock b : idleBlocks.keySet()) {
            if (anthVortex.contains(idleBlocks.get(b))) {
                Location loc = idleBlocks.get(b).getLocation();
                Vector v;
                if (b.getLocation().getWorld().equals(idleBlocks.get(b).getLocation().getWorld())) {
                    if (fallBool && b.getLocation().distance(idleBlocks.get(b).getLocation()) < 10) {
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

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        Player player = evt.getPlayer();
        ItemStack hand = Utilities.usedStack(player, usedHand);

        if (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                if (!anthVortex.contains(player)) {
                    anthVortex.add(player);
                }
                int counter = 0;
                for (Entity p : idleBlocks.values()) {
                    if (p.equals(player)) {
                        counter++;
                    }
                }
                if (counter < 64 && player.getInventory().contains(COBBLESTONE)) {
                    Utilities.removeItem(player, COBBLESTONE, 1);
                    Utilities.damageTool(player, 2, usedHand);
                    Location loc = player.getLocation();
                    FallingBlock blockEntity
                            = loc.getWorld().spawnFallingBlock(loc, Bukkit.createBlockData(MAT[Storage.rnd.nextInt(4)]));
                    blockEntity.setDropItem(false);
                    blockEntity.setGravity(false);
                    blockEntity
                            .setMetadata("ze.anthrothrower", new FixedMetadataValue(Storage.enchantments_plus, player));
                    idleBlocks.put(blockEntity, player);
                    return true;
                }
            }
            return false;
        } else if ((evt.getAction() == LEFT_CLICK_AIR || evt.getAction() == LEFT_CLICK_BLOCK)
                || hand.getType() == AIR) {
            anthVortex.remove(player);
            List<FallingBlock> toRemove = new ArrayList<>();
            for (FallingBlock blk : idleBlocks.keySet()) {
                if (idleBlocks.get(blk).equals(player)) {
                    attackBlocks.put(blk, new Pair<>(power, player.getLocation().getDirection()));
                    toRemove.add(blk);
                    Block targetBlock = player.getTargetBlock(null, 7);
                    blk.setVelocity(targetBlock
                            .getLocation().subtract(player.getLocation()).toVector().multiply(.25));
                }
            }
            for (FallingBlock blk : toRemove) {
                idleBlocks.remove(blk);
                blk.setGravity(true);
                blk.setGlowing(true);
            }
        }
        return false;
    }

    private class Pair<K, V> {

        private K key;
        private V value;

        public Pair(K k, V v) {
            this.key = k;
            this.value = v;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

}
