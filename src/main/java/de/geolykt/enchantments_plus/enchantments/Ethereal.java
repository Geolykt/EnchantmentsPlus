package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.Map;

public class Ethereal extends CustomEnchantment {

    public static final int ID = 70;

    @Override
    public Builder<Ethereal> defaults() {
        return new Builder<>(Ethereal::new, ID)
            .all(BaseEnchantments.ETHERAL,
                    0,
                    "Prevents tools from breaking",
                    new Tool[]{Tool.ALL},
                    "Ethereal",
                    1, // MAX LVL
                    1.0,
                    Hand.NONE);
    }

    @Override
    public boolean onScanHands(Player player, int level, boolean usedHand) {
        ItemStack stk = Utilities.usedStack(player, usedHand);
        int dura = CompatibilityAdapter.getDamage(stk);
        CompatibilityAdapter.setDamage(stk, 0);
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
        //FIXME could maybe be removed
        for (ItemStack s : player.getInventory().getArmorContents()) {
            if (s != null) {
                Map<CustomEnchantment, Integer> map = CustomEnchantment.getEnchants(s, player.getWorld());
                if (map.containsKey(this)) {
                    CompatibilityAdapter.setDamage(s, 0);
                }
            }
        }
        return true;
    }
}
