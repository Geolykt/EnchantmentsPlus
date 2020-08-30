package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.HOE;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class Harvest extends CustomEnchantment {

    public static final int ID = 26;

    @Override
    public Builder<Harvest> defaults() {
        return new Builder<>(Harvest::new, ID)
                .maxLevel(3)
                .loreName("Harvest")
                .probability(0)
                .enchantable(new Tool[]{HOE})
                .conflicting()
                .description("Harvests fully grown crops within a radius when clicked")
                .cooldown(0)
                .power(1.0)
                .handUse(Hand.RIGHT)
                .base(BaseEnchantments.HARVEST);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }
        Location loc = evt.getClickedBlock().getLocation();
        int radiusXZ = (int) Math.round(power * level + 2);
        int radiusY = 1;
        boolean success = false;

        for (int x = -radiusXZ; x <= radiusXZ; x++) {
            for (int y = -radiusY - 1; y <= radiusY - 1; y++) {
                for (int z = -radiusXZ; z <= radiusXZ; z++) {

                    final Block block = loc.getBlock().getRelative(x, y, z);
                    if (block.getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {

                        if (!Storage.COMPATIBILITY_ADAPTER.grownCrops().contains(block.getType())
                                && !Storage.COMPATIBILITY_ADAPTER.grownMelon().contains(block.getType())) {
                            continue;
                        }

                        BlockData cropState = block.getBlockData();
                        boolean harvestReady = !(cropState instanceof Ageable); // Is this block the crop's mature form?
                        if (!harvestReady) { // Is the mature form not a separate Material but just a particular data value?
                            Ageable ag = (Ageable) cropState;
                            harvestReady = ag.getAge() == ag.getMaximumAge();
                        }
                        if (!harvestReady) {
                            harvestReady = block.getType() == Material.SWEET_BERRY_BUSH;
                        }

                        if (harvestReady) {
                            boolean blockAltered;
                            if (block.getType() == Material.SWEET_BERRY_BUSH) {
                                blockAltered = Storage.COMPATIBILITY_ADAPTER.pickBerries(block, evt.getPlayer());
                            } else {
                                blockAltered = ADAPTER.breakBlockNMS(block, evt.getPlayer());
                            }

                            if (blockAltered) {
                                Utilities.damageTool(evt.getPlayer(), 1, usedHand);
                                Grab.grabLocs.put(block, evt.getPlayer());
                                Bukkit.getServer().getScheduler()
                                        .scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {
                                            Grab.grabLocs.remove(block);
                                        }, 3);
                                success = true;
                            }
                        }
                    }
                }
            }
        }
        return success;
    }
}
