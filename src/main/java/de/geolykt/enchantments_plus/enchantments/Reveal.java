package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Shulker;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaLocationIterator;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;

public class Reveal extends CustomEnchantment implements AreaOfEffectable {

    public static final HashMap<Location, Entity> GLOWING_BLOCKS = new HashMap<>();
    public static final int ID = 68;

    @Override
    public Builder<Reveal> defaults() {
        return new Builder<>(Reveal::new, ID)
            .all(BaseEnchantments.REVEAL,
                    "Makes nearby ores glow white through the stone.",
                    new Tool[]{Tool.PICKAXE},
                    "Reveal",
                    4,
                    Hand.NONE,
                    Switch.class, Pierce.class, Spectral.class)
            .cooldown(100);
    }
    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (evt.getPlayer().isSneaking()) {
                int radius = (int) getAOESize(level);
                int found_blocks = 0;
                AreaLocationIterator iter = new AreaLocationIterator(
                        evt.getClickedBlock().getLocation(), radius * 2, radius * 2, radius * 2, -radius, -radius, -radius);

                while (iter.hasNext()) {
                    final Location nextLoc = iter.next();
                    Block nextBlock = nextLoc.getBlock();
                    if (Storage.COMPATIBILITY_ADAPTER.ores().contains(nextBlock.getType())) {
                        boolean exposed = false;
                        for (BlockFace face : Storage.CARDINAL_BLOCK_FACES) {
                            if (Storage.COMPATIBILITY_ADAPTER.airs().contains(nextBlock.getRelative(face).getType())) {
                                exposed = true;
                                break;
                            }
                        }
                        if (exposed) {
                            continue;
                        }
                        found_blocks++;
                        
                        // Show fallingBlock Code
                        Shulker entity = (Shulker) nextBlock.getWorld().spawnEntity(nextLoc, EntityType.SHULKER);
                        entity.setGlowing(true);
                        entity.setGravity(false);
                        entity.setInvulnerable(true);
                        entity.setSilent(true);
                        entity.setAI(false);
                        GLOWING_BLOCKS.put(nextLoc, entity);
                        
                        Bukkit.getServer().getScheduler()
                            .scheduleSyncDelayedTask(Storage.plugin, () -> {
                                // Hide fallingBlockCode
                                Entity blockToRemove = GLOWING_BLOCKS.remove(nextLoc);
                                if (blockToRemove != null) {
                                    blockToRemove.remove();
                                }
                            }, 100);
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
