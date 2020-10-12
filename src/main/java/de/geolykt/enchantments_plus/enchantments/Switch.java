package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.Material.*;

public class Switch extends CustomEnchantment {

    public static final int ID = 60;

    @Override
    public Builder<Switch> defaults() {
        return new Builder<>(Switch::new, ID)
                .maxLevel(1)
                .loreName("Switch")
                .probability(0)
                .enchantable(new Tool[]{Tool.PICKAXE})
                .conflicting(Shred.class, Anthropomorphism.class, Fire.class, Extraction.class, Pierce.class,
            Reveal.class)
                .description("Replaces the clicked block with the leftmost block in your hotbar when sneaking")
                .cooldown(0)
                .power(-1.0)
                .handUse(Hand.RIGHT)
                .base(BaseEnchantments.SWITCH);
    }

    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() == Action.RIGHT_CLICK_BLOCK && evt.getPlayer().isSneaking()) {
            // Make sure clicked block is okay to break
            if (!ADAPTER.isBlockSafeToBreak(evt.getClickedBlock())) {
                return false;
            }

            Player player = evt.getPlayer();
            int c = -1;
            ItemStack switchItem = null;
            for (int i = 0; i < 9; i++) { // Find a suitable block in hotbar
                switchItem = player.getInventory().getItem(i);
                if (switchItem != null
                        && switchItem.getType() != AIR
                        && switchItem.getType().isSolid()
                        && !Storage.COMPATIBILITY_ADAPTER.unbreakableBlocks().contains(switchItem.getType())
                        && !switchItem.getType().isInteractable()) {
                    c = i;
                    break;
                }
            }
            if (c == -1) { // No suitable block in inventory
                return false;
            }

            Block clickedBlock = evt.getClickedBlock();
            
            // Block has been selected, attempt breaking
            if (Spectral.permissionQuery(clickedBlock, player, BaseEnchantments.SWITCH)) {
                evt.getClickedBlock().breakNaturally(player.getInventory().getItemInMainHand());
                CompatibilityAdapter.damageTool(player, 1, true);
            } else {
                return false;
            }

            // Breaking succeeded, begin invasive operations
            Grab.grabLocs.put(clickedBlock.getLocation(), evt.getPlayer());
            evt.setCancelled(true);

            Material mat = switchItem.getType();

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                Grab.grabLocs.remove(clickedBlock.getLocation());
            }, 3);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                ADAPTER.placeBlock(clickedBlock, player, mat, null); // TODO blockData
            }, 1);
            Utilities.removeItem(evt.getPlayer(), mat, 1);
            return true;
        }
        return false;
    }
}
