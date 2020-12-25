package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import java.util.concurrent.ThreadLocalRandom;

public class Persephone extends CustomEnchantment implements AreaOfEffectable {

    public static final int ID = 41;

    @Override
    public Builder<Persephone> defaults() {
        return new Builder<>(Persephone::new, ID)
                .all("Plants seeds from the player's inventory around them",
                        new Tool[]{Tool.HOE},
                        "Persephone",
                        3, // MAX LVL
                        Hand.RIGHT);
    }

    private Persephone() {
        super(BaseEnchantments.PERSEPHONE);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() == RIGHT_CLICK_BLOCK) {
            Player player = evt.getPlayer();
            Location loc = evt.getClickedBlock().getLocation();
            int radiusXZ = (int) getAOESize(level);

            if (Storage.COMPATIBILITY_ADAPTER.persephoneCrops().contains(evt.getClickedBlock().getType())) {
                Block block = loc.getBlock();
                for (int x = -radiusXZ; x <= radiusXZ; x++) {
                    for (int y = -2; y <= 0; y++) {
                        for (int z = -radiusXZ; z <= radiusXZ; z++) {

                            if (block.getRelative(x, y, z).getLocation().distanceSquared(loc)
                                    < radiusXZ * radiusXZ) {
                                if (block.getRelative(x, y, z).getType() == FARMLAND
                                        && Storage.COMPATIBILITY_ADAPTER.airs().contains(block.getRelative(x, y + 1, z).getType())) {
                                    if (evt.getPlayer().getInventory().contains(CARROT)) {
                                        if (ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, CARROTS,
                                                null)) {
                                            Utilities.removeItem(player, CARROT, 1);
                                        }
                                    } else if (evt.getPlayer().getInventory().contains(POTATO)) {
                                        if (ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, POTATOES,
                                                null)) {
                                            Utilities.removeItem(player, POTATO, 1);
                                        }
                                    } else if (evt.getPlayer().getInventory().contains(WHEAT_SEEDS)) {
                                        if (ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, WHEAT, null)) {
                                            Utilities.removeItem(player, WHEAT_SEEDS, 1);
                                        }
                                    } else if (evt.getPlayer().getInventory().contains(BEETROOT_SEEDS)) {
                                        if (ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, BEETROOTS,
                                                null)) {
                                            Utilities.removeItem(player, BEETROOT_SEEDS, 1);
                                        }
                                    }
                                } else if (block.getRelative(x, y, z).getType() == SOUL_SAND
                                        && Storage.COMPATIBILITY_ADAPTER.airs().contains(block.getRelative(x, y + 1, z).getType())) {
                                    if (evt.getPlayer().getInventory().contains(NETHER_WART)) {
                                        if (ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, NETHER_WART,
                                                null)) {
                                            Utilities.removeItem(player, NETHER_WART, 1);
                                        }
                                    }
                                } else {
                                    continue;
                                }
                                if (ThreadLocalRandom.current().nextBoolean()) {
                                    CompatibilityAdapter.damageTool(evt.getPlayer(), 1, usedHand);
                                }
                            }
                        }
                    }
                }
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
        return 3 + aoe + level * 2;
    }

    @Override
    public double getAOEMultiplier() {
        return aoe;
    }

    /**
     * Sets the multiplier used for the area of effect size calculation, the multiplier should have in most cases a linear impact,
     * however it's not guaranteed that the AOE Size is linear to the multiplier as some other effects may play a role.<br>
     * <br>
     * Impact formula: <b>3 + AOE + (2 * level)</b>
     * @param newValue The new value of the multiplier
     * @since 2.1.6
     */
    @Override
    public void setAOEMultiplier(double newValue) {
        aoe = newValue;
    }

}
