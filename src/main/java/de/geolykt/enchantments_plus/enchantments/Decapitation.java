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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;

public class Decapitation extends CustomEnchantment {

    private static final int BASE_PLAYER_DROP_CHANCE = 150;
    private static final int BASE_MOB_DROP_CHANCE    = 150;
    public static final  int ID                      = 11;

    @Override
    public Builder<Decapitation> defaults() {
        return new Builder<>(Decapitation::new, ID)
            .all("Increases the chance for dropping the enemies head on death",
                    new Tool[]{Tool.SWORD, Tool.TRIDENT},
                    "Decapitation",
                    4, // MAX LVL
                    Hand.LEFT);
    }

    public Decapitation() {
        super(BaseEnchantments.DECAPITATION);
    }

    /**
     * Maps what Entities drop which Head.
     * @since 3.0.0
     */
    private static final EnumMap<EntityType, Material> entityToSkull = new EnumMap<>(EntityType.class);

    @Override
    public boolean onEntityKill(EntityDeathEvent evt, int level, boolean usedHand) {
        Material mat = entityToSkull.get(evt.getEntityType());
        if (mat == null) {
            return false;
        }
        ItemStack stk = new ItemStack(mat, 1);
        if (mat == Material.PLAYER_HEAD) {
            if (ThreadLocalRandom.current().nextInt(Math.max((int) Math.round(BASE_PLAYER_DROP_CHANCE / (level * power)), 1)) == 0) {

                SkullMeta meta = (SkullMeta) stk.getItemMeta();
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(evt.getEntity().getUniqueId()));
                stk.setItemMeta(meta);
                evt.getEntity().getWorld().dropItemNaturally(evt.getEntity().getLocation(), stk);
                return true;
            }
        } else if (ThreadLocalRandom.current().nextInt(Math.max((int) Math.round(BASE_MOB_DROP_CHANCE / (level * power)), 1)) == 0) {
            evt.getEntity().getWorld().dropItemNaturally(evt.getEntity().getLocation(), stk);
            return true;
        }
        return false;
    }

    static {
        entityToSkull.put(EntityType.PLAYER, Material.PLAYER_HEAD);
        entityToSkull.put(EntityType.SKELETON, Material.SKELETON_SKULL);
        entityToSkull.put(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SKULL);
        entityToSkull.put(EntityType.CREEPER, Material.CREEPER_HEAD);
        entityToSkull.put(EntityType.ENDER_DRAGON, Material.DRAGON_HEAD);
        entityToSkull.put(EntityType.ZOMBIE, Material.ZOMBIE_HEAD);
    }
}
