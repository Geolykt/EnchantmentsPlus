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
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;

public class Reveal extends CustomEnchantment implements AreaOfEffectable {

    public static final HashMap<Location, Entity> GLOWING_BLOCKS = new HashMap<>();
    public static final int ID = 68;

    @Override
    public Builder<Reveal> defaults() {
        return new Builder<>(Reveal::new, ID)
            .all("Makes nearby ores glow white through the stone.",
                    new Tool[]{Tool.PICKAXE},
                    "Reveal",
                    4,
                    Hand.NONE,
                    BaseEnchantments.SWITCH, BaseEnchantments.PIERCE, BaseEnchantments.SPECTRAL)
            .cooldown(100); // TODO cooldown in milliseconds
    }

    public Reveal() {
        super(BaseEnchantments.REVEAL);
    }

    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (evt.getPlayer().isSneaking()) {
                int radius = (int) getAOESize(level);
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
                                entity.setSilent(true);
                                ((LivingEntity)entity).setAI(false);
                                Entity ent = GLOWING_BLOCKS.put(loc, entity);
                                if (ent != null) {
                                    ent.remove();
                                }
                                
                                Bukkit.getServer().getScheduler()
                                    .scheduleSyncDelayedTask(Storage.plugin, () -> {
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
                CompatibilityAdapter.damageTool(evt.getPlayer(), Math.max(16, (int) Math.round(found_blocks * 1.3)), usedHand);

                return true;
            }
        }
        return false;
    }

    /**
     * The Area of effect multiplier used by this enchantment.
     * @since 2.1.6
     * @see AreaOfEffectable
     */
    private double aoe = 1.0;
    
    @Override
    public double getAOESize(int level) {
        return 2 + aoe + level;
    }

    @Override
    public double getAOEMultiplier() {
        return aoe;
    }

    /**
     * Sets the multiplier used for the area of effect size calculation, the multiplier should have in most cases a linear impact,
     * however it's not guaranteed that the AOE Size is linear to the multiplier as some other effects may play a role.<br>
     * <br>
     * Impact formula: <b>2 + AOE + level</b>
     * @param newValue The new value of the multiplier
     * @since 2.1.6
     */
    @Override
    public void setAOEMultiplier(double newValue) {
        aoe = newValue;
    }
}
