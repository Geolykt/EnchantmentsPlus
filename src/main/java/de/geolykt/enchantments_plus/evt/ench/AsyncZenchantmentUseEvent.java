package de.geolykt.enchantments_plus.evt.ench;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.annotations.AsyncSafe;

/**
 * The asynchronous alternative to the {@link ZenchantmentUseEvent}, however performs the same things, outside of it being used asynchronously.
 * Generally used for the fast scans
 * @since 2.1.1@fast-async
 */
public class AsyncZenchantmentUseEvent extends Event {
    
    private final CustomEnchantment ench;
    private final Integer enchLevel;
    private final EquipmentSlot slot;
    private final Player player;

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    @AsyncSafe
    public AsyncZenchantmentUseEvent(Player who, EquipmentSlot usedSlot, CustomEnchantment enchantment, Integer level) {
        super(true);
        this.player = who;
        this.enchLevel = level;
        this.slot = usedSlot;
        this.ench = enchantment;
        
    }

    public CustomEnchantment getEnchantment() {
        return ench;
    }

    public EquipmentSlot getItemslot() {
        return slot;
    }
    
    public ItemStack getInvolvedItem() {
        return player.getInventory().getItem(slot);
    }

    public Integer getLevel() {
        return enchLevel;
    }
    
    public Player getPlayer() {
        return player;
    }
}
