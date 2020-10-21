package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import java.util.HashSet;

public class Terraformer extends CustomEnchantment {

    public static int[][] SEARCH_FACES = new int[][]{new int[]{-1, 0, 0}, new int[]{1, 0, 0}, new int[]{0, -1, 0}, new int[]{0, 0, -1}, new int[]{0, 0, 1}};

    private static final int MAX_BLOCKS = 64;

    public static final int ID = 61;

    @Override
    public Builder<Terraformer> defaults() {
        return new Builder<>(Terraformer::new, ID)
            .all(BaseEnchantments.TERRAFORMER,
                    "Places the leftmost blocks in the players inventory within a 7 block radius",
                    new Tool[]{Tool.SHOVEL},
                    "Terraformer",
                    1,
                    Hand.RIGHT);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getPlayer().isSneaking()) {
            if (evt.getAction().equals(RIGHT_CLICK_BLOCK)) {
                Block start = evt.getClickedBlock().getRelative(evt.getBlockFace());
                Material mat = AIR;

                for (int i = 0; i < 9; i++) {
                    if (evt.getPlayer().getInventory().getItem(i) != null) {
                        if (evt.getPlayer().getInventory().getItem(i).getType().isBlock() &&
                            Storage.COMPATIBILITY_ADAPTER.terraformerMaterials().contains(
                                evt.getPlayer().getInventory().getItem(i).getType())) {
                            mat = evt.getPlayer().getInventory().getItem(i).getType();
                            break;
                        }
                    }
                }

                for (Block b : Utilities.BFS(start, MAX_BLOCKS, false, 5.f, SEARCH_FACES,
                    Storage.COMPATIBILITY_ADAPTER.airs(), new HashSet<Material>(), false, true)) {
                    if (b.getType().equals(AIR)) {
                        if (Utilities.hasItem(evt.getPlayer(), mat, 1)) {
                            if (Storage.COMPATIBILITY_ADAPTER.placeBlock(b, evt.getPlayer(), mat, null)) {
                                Utilities.removeItem(evt.getPlayer(), mat, 1);
                                if (Storage.rnd.nextInt(10) == 5) {
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


}
