package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import static org.bukkit.Material.AIR;

public class Stock extends CustomEnchantment {

    public static final int ID = 59;

    @Override
    public Builder<Stock> defaults() {
        return new Builder<>(Stock::new, ID)
            .all(BaseEnchantments.STOCK,
                    0,
                    "Refills the player's item in hand when they run out",
                    new Tool[]{Tool.CHESTPLATE},
                    "Stock",
                    1, // MAX LVL
                    1.0,
                    Hand.NONE);
    }

    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
        final ItemStack stk = evt.getPlayer().getInventory().getItemInMainHand().clone();
        if (stk == null || stk.getType() == AIR) {
            return false;
        }
        final Player player = evt.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
            int current = -1;
            ItemStack newHandItem = evt.getPlayer().getInventory().getItemInMainHand();
            if (newHandItem != null && newHandItem.getType() != AIR) {
                return;
            }
            for (int i = 0; i < evt.getPlayer().getInventory().getContents().length; i++) {
                ItemStack s = player.getInventory().getContents()[i];
                if (s != null && s.getType().equals(stk.getType())) {
                    current = i;
                    break;
                }
            }
            if (current != -1) {
                evt.getPlayer().getInventory()
                   .setItemInMainHand(evt.getPlayer().getInventory().getContents()[current]);
                evt.getPlayer().getInventory().setItem(current, new ItemStack(AIR));
            }
        }, 1);
        return false;
    }
}
