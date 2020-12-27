package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.Config;
import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

public class Ethereal extends CustomEnchantment {

    public static final int ID = 70;

    @Override
    public Builder<Ethereal> defaults() {
        return new Builder<>(Ethereal::new, ID)
            .all("Prevents tools from breaking",
                    new Tool[]{Tool.ALL},
                    "Ethereal",
                    1, // MAX LVL
                    Hand.NONE);
    }

    public Ethereal() {
        super(BaseEnchantments.ETHERAL);
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
        Config config = Config.get(player.getWorld());
        for (ItemStack s : player.getInventory().getArmorContents()) {
            if (s != null && CustomEnchantment.hasEnchantment(config, s, BaseEnchantments.ETHERAL)) {
                CompatibilityAdapter.setDamage(s, 0);
            }
        }
        return true;
    }
}
