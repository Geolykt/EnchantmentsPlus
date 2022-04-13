package de.geolykt.enchantments_plus.compatibility;

import java.util.Map;

import org.bukkit.World;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import de.geolykt.enchantments_plus.CustomEnchantment;

import io.github.thebusybiscuit.slimefun4.api.events.AsyncAutoDisenchanterProcessEvent;

public class SlimefunCompatibillityListener implements Listener {

    @NotNull
    private final Plugin plugin;

    public SlimefunCompatibillityListener(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onDisenchant(@NotNull AsyncAutoDisenchanterProcessEvent evt) {
        if (evt.getResult() == Result.DENY) {
            return;
        }
        World world = evt.getMenu().getLocation().getWorld();
        Map<CustomEnchantment, Integer> enchantments = CustomEnchantment.getEnchants(evt.getOutputStack(), world, null);
        if (enchantments.isEmpty()) {
            return;
        }
        if (evt.getResult() == Result.DEFAULT) {
            evt.setResult(Result.ALLOW);
        }
        ItemStack outBook = evt.getOutputBook();
        ItemStack outStack = evt.getOutputStack();
        enchantments.forEach((ench, level) -> {
            // TODO bulk setting
            CustomEnchantment.setEnchantment(outStack, ench, 0, world);
            CustomEnchantment.setEnchantment(outBook, ench, level, world);
            evt.setTransferredEnchantmentsAmount(evt.getTransferredEnchantmentsAmount() + 1);
        });
        evt.setOutputBook(outBook);
        evt.setOutputStack(outStack);
    }
}
