package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.ALL;

import java.util.Map;

public class Ethereal extends CustomEnchantment {

    public static final int ID = 70;

    @Override
    public Builder<Ethereal> defaults() {
        return new Builder<>(Ethereal::new, ID)
            .maxLevel(1)
            .loreName("Ethereal")
            .probability(0)
            .enchantable(new Tool[]{ALL})
            .conflicting()
            .description("Prevents tools from breaking")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.NONE);
    }

    @Override
    public boolean onScanHands(Player player, int level, boolean usedHand) {
        ItemStack stk = Utilities.usedStack(player, usedHand);
        int dura = Utilities.getDamage(stk);
        Utilities.setDamage(stk, 0);
        if (dura != 0) {
            if (usedHand) {
                player.getInventory().setItemInMainHand(stk);
            } else {
                player.getInventory().setItemInOffHand(stk);
            }
        }
        return dura != 0;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        for (ItemStack s : player.getInventory().getArmorContents()) {
            if (s != null) {
                Map<CustomEnchantment, Integer> map = CustomEnchantment.getEnchants(s, player.getWorld());
                if (map.containsKey(de.geolykt.enchantments_plus.enchantments.Ethereal.this)) {
                    Utilities.setDamage(s, 0);
                }
            }
        }
        return true;
    }
}
