/*
 *  This file is part of EnchantmentsPlus, a bukkit plugin.
 *  Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 *  Copyright (c) 2020 - 2021 Geolykt and EnchantmentsPlus contributors
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

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Combustion extends CustomEnchantment {

    public static final int ID = 9;

    @Override
    public Builder<Combustion> defaults() {
        return new Builder<>(Combustion::new, ID)
                .all("Lights attacking entities on fire when player is attacked",
                    new Tool[]{Tool.CHESTPLATE},
                    "Combustion",
                    4, // MAX LVL
                    Hand.NONE);
    }

    public Combustion() {
        super(BaseEnchantments.COMBUSTION);
    }

    @Override
    public boolean onBeingHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        Entity ent;
        if (evt.getDamager().getType() == EntityType.ARROW) {
            Arrow arrow = (Arrow) evt.getDamager();
            if (arrow.getShooter() instanceof Entity) {
                ent = (Entity) arrow.getShooter();
            } else {
                return false;
            }
        } else {
            ent = evt.getDamager();
        }
        return ADAPTER.igniteEntity(ent, (Player) evt.getEntity(), (int) (50 * level * power));
    }

    public boolean onCombust(EntityCombustByEntityEvent evt, int level, boolean usedHand) {
        evt.setDuration(0);
        return false;
    }
}
