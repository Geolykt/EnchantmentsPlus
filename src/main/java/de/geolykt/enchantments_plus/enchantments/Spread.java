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

import static org.bukkit.inventory.EquipmentSlot.HAND;
import static org.bukkit.inventory.EquipmentSlot.OFF_HAND;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.MultiArrow;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

public class Spread extends CustomEnchantment {

    public static final int ID = 57;

    @Override
    public Builder<Spread> defaults() {
        return new Builder<>(Spread::new, ID)
            .all("Fires an array of arrows simultaneously",
                    new Tool[]{Tool.BOW},
                    "Spread",
                    5,
                    Hand.RIGHT,
                    BaseEnchantments.BURST);
    }

    public Spread() {
        super(BaseEnchantments.SPREAD);
    }

    @Override
    public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level, boolean usedHand) {
        AbstractArrow originalArrow = (AbstractArrow) evt.getEntity();
        Player player = (Player) originalArrow.getShooter();
        ItemStack hand = Utilities.usedStack(player, usedHand);
        MultiArrow ar = new MultiArrow(originalArrow);
        EnchantedArrow.putArrow(originalArrow, ar, player);

        Bukkit.getPluginManager().callEvent(
            Storage.COMPATIBILITY_ADAPTER.constructEntityShootBowEvent(player, hand, null, originalArrow,
                    usedHand ? HAND : OFF_HAND, (float) originalArrow.getVelocity().length(), false));

        CompatibilityAdapter.damageTool(player, (int) Math.round(level / 2.0 + 1), usedHand);

        for (int i = 0; i < (int) Math.round(power * level * 4); i++) {
            Vector v = originalArrow.getVelocity();
            v.setX(v.getX() + Math.max(Math.min(ThreadLocalRandom.current().nextGaussian() / 8, 0.75), -0.75));
            v.setZ(v.getZ() + Math.max(Math.min(ThreadLocalRandom.current().nextGaussian() / 8, 0.75), -0.75));
            AbstractArrow arrow = player.getWorld().spawnArrow(
                player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.0)), v, 1, 0);
            arrow.setShooter(player);
            arrow.setVelocity(v.normalize().multiply(originalArrow.getVelocity().length()));
            arrow.setFireTicks(originalArrow.getFireTicks());
            arrow.setKnockbackStrength(originalArrow.getKnockbackStrength());
            EntityShootBowEvent event = Storage.COMPATIBILITY_ADAPTER.constructEntityShootBowEvent(player, hand,
                    null, arrow, usedHand ? HAND : OFF_HAND, (float) arrow.getVelocity().length(), false);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                arrow.remove();
                return false;
            }
            arrow.setPickupStatus(PickupStatus.DISALLOWED);
            arrow.setCritical(originalArrow.isCritical());
            EnchantedArrow.putArrow(originalArrow, new MultiArrow(originalArrow), player);
        }
        return true;
    }
}
