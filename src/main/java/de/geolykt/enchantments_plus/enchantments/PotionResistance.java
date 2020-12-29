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

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.Map;

public class PotionResistance extends CustomEnchantment {

    public static final int ID = 45;

    @Override
    public Builder<PotionResistance> defaults() {
        return new Builder<>(PotionResistance::new, ID)
            .all("Lessens the effects of all potions on players, even the good ones",
                    new Tool[]{Tool.HELMET, Tool.CHESTPLATE, Tool.LEGGINGS, Tool.BOOTS},
                    "Potion Resistance",
                    4,
                    Hand.NONE);
    }

    public PotionResistance() {
        super(BaseEnchantments.POTION_RESISTANCE);
    }

    @Override
    public boolean onPotionSplash(PotionSplashEvent evt, int level, boolean usedHand) {
        for (LivingEntity ent : evt.getAffectedEntities()) {
            if (ent instanceof Player) {
                int effect = 0;
                for (ItemStack stk : ((Player) ent).getInventory().getArmorContents()) {
                    Map<CustomEnchantment, Integer> map = CustomEnchantment.getEnchants(stk, ent.getWorld(), null);
                    for (CustomEnchantment e : map.keySet()) {
                        if (e.equals(this)) {
                            effect += map.get(e);
                        }
                    }
                }
                evt.setIntensity(ent, evt.getIntensity(ent) / ((effect * power + 1.3) / 2));
            }
        }
        return true;
    }
}
