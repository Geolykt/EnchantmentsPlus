package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaLocationIterator;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.concurrent.ThreadLocalRandom;

public class Germination extends CustomEnchantment implements AreaOfEffectable {

    public static final int ID = 19;

    @Override
    public Builder<Germination> defaults() {
        return new Builder<>(Germination::new, ID)
                .all(BaseEnchantments.GERMINATION,
                        "Uses bonemeal from the player's inventory to grow nearby plants",
                        new Tool[]{Tool.HOE},
                        "Germination",
                        3, // MAX LVL
                        Hand.RIGHT);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }
        Player player = evt.getPlayer();
        Location loc = evt.getClickedBlock().getLocation();
        int radiusXZ = (int) getAOESize(level);
        boolean applied = false;
        AreaLocationIterator iter = new AreaLocationIterator(loc, radiusXZ * 2, 4, radiusXZ * 2, -radiusXZ, -3, -radiusXZ);
        while (iter.hasNext()) {
            Block blk = iter.next().getBlock();
            if (blk.getLocation().distanceSquared(loc) < radiusXZ * radiusXZ
                    && Utilities.hasItem(player, Material.BONE_MEAL, 1)
                    && ADAPTER.grow(blk, player)) {

                applied = true;
                if (ThreadLocalRandom.current().nextBoolean()) {
                    ADAPTER.grow(blk, player);
                }

                CompatibilityAdapter.display(Utilities.getCenter(blk), Particle.VILLAGER_HAPPY,
                        25, 1f, .3f, .3f, .3f);

                if (ThreadLocalRandom.current().nextInt(10) <= 3) {
                    CompatibilityAdapter.damageTool(player, 1, usedHand);
                }
                Utilities.removeItem(player, Material.BONE_MEAL, 1);
            }
        }
        return applied;
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
