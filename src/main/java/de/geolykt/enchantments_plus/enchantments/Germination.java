package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.HOE;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class Germination extends CustomEnchantment {

    public static final int ID = 19;

    @Override
    public Builder<Germination> defaults() {
        return new Builder<>(Germination::new, ID)
                .maxLevel(3)
                .loreName("Germination")
                .probability(0)
                .enchantable(new Tool[]{HOE})
                .conflicting()
                .description("Uses bonemeal from the player's inventory to grow nearby plants")
                .cooldown(0)
                .power(1.0)
                .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }
        Player player = evt.getPlayer();
        Location loc = evt.getClickedBlock().getLocation();
        Block clickedBlock = evt.getClickedBlock();
        int radiusXZ = (int) Math.round(power * level + 2);
        int radiusY = 2;
        boolean applied = false;
        for (int x = -(radiusXZ); x <= radiusXZ; x++) {
            for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                for (int z = -(radiusXZ); z <= radiusXZ; z++) {

                    Block relativeBlock = clickedBlock.getRelative(x, y, z);
                    if (relativeBlock.getLocation().distanceSquared(loc) < radiusXZ * radiusXZ
                            && Utilities.hasItem(player, Material.BONE_MEAL, 1)
                            && ADAPTER.grow(relativeBlock, player)) {

                        applied = true;
                        if (Storage.rnd.nextBoolean()) {
                            ADAPTER.grow(relativeBlock, player);
                        }

                        Utilities.display(Utilities.getCenter(relativeBlock), Particle.VILLAGER_HAPPY, 30, 1f, .3f,
                                .3f,
                                .3f);

                        if (Storage.rnd.nextInt(10) <= 3) {
                            Utilities.damageTool(player, 1, usedHand);
                        }
                        Utilities.removeItem(player, Material.BONE_MEAL, 1);
                    }
                }
            }
        }
        return applied;
    }
}
