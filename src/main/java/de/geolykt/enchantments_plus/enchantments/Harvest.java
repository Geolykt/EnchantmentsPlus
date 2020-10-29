package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
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

public class Harvest extends CustomEnchantment implements AreaOfEffectable {

    public static final int ID = 26;

    @Override
    public Builder<Harvest> defaults() {
        return new Builder<>(Harvest::new, ID)
                .all(BaseEnchantments.HARVEST,
                        "Harvests fully grown crops within a radius when clicked",
                        new Tool[]{Tool.HOE},
                        "Harvest",
                        3, // MAX LVL
                        Hand.RIGHT);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }
        
        Location loc = evt.getClickedBlock().getLocation();
        int radiusXZ = (int) getAOESize(level);
        AreaLocationIterator iter = new AreaLocationIterator(loc, radiusXZ * 2, 3, radiusXZ * 2, -radiusXZ, -2, -radiusXZ);
        boolean success = false;

        while (iter.hasNext()) {
            final Location nextLoc = iter.next();
            if (nextLoc.distanceSquared(loc) < radiusXZ * radiusXZ) {

                final Block nextBlock = nextLoc.getBlock();
                if (!Storage.COMPATIBILITY_ADAPTER.grownCrops().contains(nextBlock.getType())
                        && !Storage.COMPATIBILITY_ADAPTER.grownMelon().contains(nextBlock.getType())) {
                    continue;
                }

                BlockData cropState = nextBlock.getBlockData();
                boolean harvestReady = !(cropState instanceof Ageable); // Is this block the crop's mature form?
                if (!harvestReady) { // Is the mature form not a separate Material but just a particular data value?
                    harvestReady = ((Ageable) cropState).getAge() == ((Ageable) cropState).getMaximumAge();
                    if (!harvestReady) {
                        harvestReady = nextBlock.getType() == Material.SWEET_BERRY_BUSH;
                    }
                }

                if (harvestReady) {
                    boolean blockAltered;
                    if (nextBlock.getType() == Material.SWEET_BERRY_BUSH) {
                        blockAltered = Storage.COMPATIBILITY_ADAPTER.pickBerries(nextBlock, evt.getPlayer());
                    } else {
                        blockAltered = ADAPTER.breakBlockNMS(nextBlock, evt.getPlayer());
                    }

                    if (blockAltered) {
                        CompatibilityAdapter.damageTool(evt.getPlayer(), 1, usedHand);
                        Grab.grabLocs.put(nextLoc, evt.getPlayer());
                        Bukkit.getServer().getScheduler()
                                .scheduleSyncDelayedTask(Storage.plugin, () -> {
                                    Grab.grabLocs.remove(nextBlock.getLocation());
                                }, 3);
                        success = true;
                    }
                }
            }
        }
        return success;
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
