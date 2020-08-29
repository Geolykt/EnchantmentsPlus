package de.geolykt.enchantments_plus.evt.ench;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;

public class ZenchantmentUseEvent extends PlayerEvent {
    
   private final CustomEnchantment ench;
   private final Integer enchLevel;
   private final EquipmentSlot slot;
    
    public ZenchantmentUseEvent(Player who, EquipmentSlot usedSlot, CustomEnchantment enchantment, Integer level) {
        super(who);
        this.enchLevel = level;
        this.slot = usedSlot;
        this.ench = enchantment;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
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

}
