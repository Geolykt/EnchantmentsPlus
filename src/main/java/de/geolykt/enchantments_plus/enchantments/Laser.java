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

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.EnchantPlayer;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class Laser extends CustomEnchantment {

    public static final int ID = 31;
    
    public static NamespacedKey colorKey;

    public static boolean doShredCooldown = true;
    // TODO look if it can allow swords as the tool in the future

    @Override
    public Builder<Laser> defaults() {
        return new Builder<>(Laser::new, ID)
                .all("Breaks blocks and damages mobs using a powerful beam of light",
                        new Tool[]{Tool.PICKAXE, Tool.AXE},
                        "Laser",
                        3,
                        Hand.RIGHT);
    }

    public Laser() {
        super(BaseEnchantments.LASER);
    }

    public void shoot(Player player, int level, boolean usedHand) {
        // Avoid recursing into other enchantments
        EnchantPlayer.setCooldown(player, BaseEnchantments.LUMBER, 200);
        if (doShredCooldown) {
            EnchantPlayer.setCooldown(player, BaseEnchantments.SHRED, 200);
        }
        Block blk = player.getTargetBlock(null, 6
                + (int) Math.round(level * power * 3));
        Location playLoc = player.getLocation();
        Location target = Utilities.getCenter(blk.getLocation());
        target.setY(target.getY() + .5);
        playLoc.setY(playLoc.getY() + 1.1);

        ItemStack itemInHand = usedHand ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
        ItemMeta itemMeta = itemInHand.getItemMeta();
        Color laserColor = getColor(itemMeta);
        short itemDamage = 0;

        double d = target.distance(playLoc);
        for (int i = 0; i < (int) d * 5; i++) {
            Location tempLoc = target.clone();
            tempLoc.setX(playLoc.getX() + (i * ((target.getX() - playLoc.getX()) / (d * 5))));
            tempLoc.setY(playLoc.getY() + (i * ((target.getY() - playLoc.getY()) / (d * 5))));
            tempLoc.setZ(playLoc.getZ() + (i * ((target.getZ() - playLoc.getZ()) / (d * 5))));

            player.getWorld().spawnParticle(Particle.REDSTONE, tempLoc, 1, new Particle.DustOptions(laserColor, 0.5f));

            for (Entity ent : playLoc.getWorld().getNearbyEntities(tempLoc, .3, .3, .3)) {
                if (ent instanceof LivingEntity && ent != player) {
                    ADAPTER.attackEntity((LivingEntity) ent, player, 1 + (level + power * 2), false);
                    itemDamage++;
                    return;
                }
            }
        }
        if (CompatibilityAdapter.damageMeta(itemMeta, itemDamage, itemInHand.getType())) {
            if (usedHand) {
                player.getInventory().clear(player.getInventory().getHeldItemSlot());
            } else {
                player.getInventory().setItem(EquipmentSlot.OFF_HAND, new ItemStack(Material.AIR));
            }
        }
        if (ADAPTER.isBlockSafeToBreak(blk) && !ADAPTER.laserDenylist().contains(blk.getType())) {
            ADAPTER.breakBlockNMS(blk, player);
        }
    }

    @Override
    public boolean onEntityInteract(PlayerInteractEntityEvent evt, int level, boolean usedHand) {
        if (usedHand && !evt.getPlayer().isSneaking()) {
            shoot(evt.getPlayer(), level, usedHand);
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
        if (usedHand && !evt.getPlayer().isSneaking()
                && (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK)) {
            shoot(evt.getPlayer(), level, usedHand);
            return true;
        }
        return false;
    }

    /**
     * Obtains the laser color of an enchantment based on the ItemMeta.
     * @param itemMeta The itemMeta of the item
     * @return The color of the laser
     * @since 3.0.0-rc.3
     */
    public static org.bukkit.Color getColor(@NotNull ItemMeta itemMeta) {
        if (!itemMeta.getPersistentDataContainer().isEmpty()) {
            return Color.fromRGB(itemMeta.getPersistentDataContainer().getOrDefault(colorKey, PersistentDataType.INTEGER, Color.RED.asRGB()));
        }
        return Color.RED;
    }

    public static void setColor(ItemStack stack, org.bukkit.Color color) {
       ItemMeta im = stack.getItemMeta();
       im.getPersistentDataContainer().set(colorKey, PersistentDataType.INTEGER, color.asRGB());
       stack.setItemMeta(im);
    }
}
