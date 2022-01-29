/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2022 Geolykt and EnchantmentsPlus contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.geolykt.enchantments_plus.enchantments;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.LevelArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Level extends CustomEnchantment {

    public static final int ID = 32;

    @Override
    public Builder<Level> defaults() {
        return new Builder<>(Level::new, ID)
                .all("Drops more XP when killing mobs or mining ores",
                        new Tool[]{Tool.BOW, Tool.SWORD, Tool.TRIDENT, Tool.PICKAXE, Tool.CROSSBOW},
                        "Level",
                        3, // MAX LVL
                        Hand.BOTH);
    }

    public Level() {
        super(BaseEnchantments.LEVEL);
    }

    @Override
    public boolean onEntityKill(EntityDeathEvent evt, int level, boolean usedHand) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            evt.setDroppedExp((int) (evt.getDroppedExp() * (1.3 + (level * power * .5))));
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            evt.setExpToDrop((int) (evt.getExpToDrop() * (1.3 + (level * power * .5))));
            return true;
        }
        return false;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            LevelArrow arrow = new LevelArrow((AbstractArrow) evt.getProjectile(), level, power);
            EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }
        return false;
    }

}
