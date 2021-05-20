/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2021 Geolykt and EnchantmentsPlus contributors
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

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.SiphonArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Siphon extends CustomEnchantment {

    public static final int ID = 53;

    @Override
    public Builder<Siphon> defaults() {
        return new Builder<>(Siphon::new, ID)
                .all("Drains the health of the mob that you attack, giving it to you",
                        new Tool[]{Tool.BOW, Tool.SWORD},
                        "Siphon",
                        4,
                        Hand.BOTH);
    }

    public Siphon() {
        super(BaseEnchantments.SIPHON);
    }

    @Override
    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if (evt.getEntity() instanceof LivingEntity
                && ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0, false)) {
            Player player = (Player) evt.getDamager();
            double difference = (0.17 * level * power) * (enchantmentConfiguration.siphonUseFinalDamage() ? evt.getFinalDamage() : evt.getDamage());
            AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            assert maxHealth != null;
            player.setHealth(player.getHealth() + Math.min(difference, maxHealth.getValue() - player.getHealth()));
        }
        return true;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        SiphonArrow arrow = new SiphonArrow((AbstractArrow) evt.getProjectile(), level, power, enchantmentConfiguration);
        EnchantedArrow.putArrow((AbstractArrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
