package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import java.util.concurrent.ThreadLocalRandom;

public class Plough extends CustomEnchantment implements AreaOfEffectable {

    public static final int ID = 43;

    @Override
    public Builder<Plough> defaults() {
        return new Builder<>(Plough::new, ID)
            .all("Tills all soil within a radius",
                    new Tool[]{Tool.HOE},
                    "Plough",
                    3,
                    Hand.RIGHT);
    }

    public Plough() {
        super(BaseEnchantments.PLOUGH);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() == RIGHT_CLICK_BLOCK) {
            Location loc = evt.getClickedBlock().getLocation();
            int radiusXZ = (int) getAOESize(level);
            for (int x = -(radiusXZ); x <= radiusXZ; x++) {
                for (int y = -2; y <= 0; y++) {
                    for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                        Block block = loc.getBlock();
                        if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {
                            if (((block.getRelative(x, y, z).getType() == DIRT
                                || block.getRelative(x, y, z).getType() == GRASS_BLOCK
                                || block.getRelative(x, y, z).getType() == MYCELIUM))
                                && Storage.COMPATIBILITY_ADAPTER.airs().contains(block.getRelative(x, y + 1, z).getType())) {
                                ADAPTER.placeBlock(block.getRelative(x, y, z), evt.getPlayer(), Material.FARMLAND,
                                    null);
                                if (ThreadLocalRandom.current().nextBoolean()) {
                                    CompatibilityAdapter.damageTool(evt.getPlayer(), 1, usedHand);
                                }
                            }
                        }
                    }
                }
            }
            return true;
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
