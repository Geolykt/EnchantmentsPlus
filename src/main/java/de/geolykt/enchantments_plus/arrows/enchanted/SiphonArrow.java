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
package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import de.geolykt.enchantments_plus.Config.EnchantmentConfiguration;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;

public class SiphonArrow extends EnchantedArrow {

    /**
     * The enchantment configuration that should be used. Required for armour penetration checks
     *
     * @since 4.0.0
     */
    private final EnchantmentConfiguration econfig;

    /**
     * Constructor.
     *
     * @param entity The entity that should be represented by this instance
     * @param level The level of the enchantment that was put on the bow that shot the arrow
     * @param power The power of the effect
     * @param enchConfig The enchantment configuration that should be used. Required for armour penetration checks
     * @since 4.0.0
     */
    public SiphonArrow(AbstractArrow entity, int level, double power, @NotNull EnchantmentConfiguration enchConfig) {
        super(entity, level, power);
        econfig = enchConfig;
    }

    @Override
    public boolean onImpact(EntityDamageByEntityEvent evt) {
        if (evt.getEntity() instanceof LivingEntity && Storage.COMPATIBILITY_ADAPTER.attackEntity(
                (LivingEntity) evt.getEntity(),
                (Player) arrow.getShooter(), 0, false)) {
            Player player = (Player) ((Projectile) evt.getDamager()).getShooter();
            assert player != null;
            double difference = (0.17 * level * power) * (econfig.siphonUseFinalDamage() ? evt.getFinalDamage() : evt.getDamage());
            AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            assert maxHealth != null;
            player.setHealth(player.getHealth() + Math.min(difference, maxHealth.getValue() - player.getHealth()));
        }
        die();
        return true;
    }
}
