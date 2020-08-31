package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.PICKAXE;

import java.util.HashMap;

public class Reveal extends CustomEnchantment {

    public static final HashMap<Location, Entity> GLOWING_BLOCKS = new HashMap<>();
    public static final int ID = 68;

    @Override
    public Builder<Reveal> defaults() {
        return new Builder<>(Reveal::new, ID)
            .maxLevel(4)
            .loreName("Reveal")
            .probability(0)
            .enchantable(new Tool[]{PICKAXE})
            .conflicting(Switch.class, Pierce.class, Spectral.class)
            .description("Makes nearby ores glow white through the stone.")
            .cooldown(100)
            .power(1.0)
            .handUse(Hand.NONE)
            .base(BaseEnchantments.REVEAL);
    }
    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (evt.getPlayer().isSneaking()) {
                int radius = (int) Math.max(2, Math.round((2 + level) * power));
                int found_blocks = 0;
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            Block blk = evt.getPlayer().getLocation().getBlock().getRelative(x, y, z);
                            if (Storage.COMPATIBILITY_ADAPTER.ores().contains(blk.getType())) {
                                boolean exposed = false;
                                for (BlockFace face : Storage.CARDINAL_BLOCK_FACES) {
                                    if (Storage.COMPATIBILITY_ADAPTER.airs().contains(blk.getRelative(face).getType())) {
                                        exposed = true;
                                        break;
                                    }
                                }
                                if (exposed) {
                                    continue;
                                }
                                found_blocks++;
                                
                                // Show fallingBlock Code
                                Location loc = blk.getLocation();
                                LivingEntity entity = (LivingEntity) blk.getWorld().spawnEntity(loc, EntityType.SHULKER);
                                entity.setGlowing(true);
                                entity.setGravity(false);
                                entity.setInvulnerable(true);
                                ((LivingEntity)entity).setAI(false);
                                GLOWING_BLOCKS.put(loc, entity);
                                
                                Bukkit.getServer().getScheduler()
                                    .scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
                                        // Hide fallingBlockCode
                                        Entity blockToRemove = GLOWING_BLOCKS.remove(loc);
                                        if (blockToRemove != null) {
                                            blockToRemove.remove();
                                        }
                                    }, 100);
                            }
                        }
                    }
                }
                Utilities.damageTool(evt.getPlayer(), Math.max(16, (int) Math.round(found_blocks * 1.3)), usedHand);

                return true;
            }
        }
        return false;
    }

}
