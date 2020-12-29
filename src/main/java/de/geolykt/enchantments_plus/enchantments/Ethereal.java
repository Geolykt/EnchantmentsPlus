/*
 * 
 *  This file is part of EnchantmentsPlus, a bukkit plugin.
 *  Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 *  Copyright (c) 2020 Geolykt and EnchantmentsPlus contributors
 *   
 *  This program is free software: you can redistribute it and/or modify  
 *  it under the terms of the GNU General Public License as published by  
 *  the Free Software Foundation, version 3.
 *  
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License 
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
