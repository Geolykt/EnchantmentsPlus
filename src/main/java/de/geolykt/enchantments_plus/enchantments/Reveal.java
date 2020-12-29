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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.HashMap;
import java.util.LinkedHashSet;

public class Reveal extends CustomEnchantment implements AreaOfEffectable {

    public static final HashMap<Location, Entity> GLOWING_BLOCKS = new HashMap<>();
    public static final int ID = 68;

    @Override
    public Builder<Reveal> defaults() {
        return new Builder<>(Reveal::new, ID)
            .all("Makes nearby ores glow white through the stone.",
                    new Tool[]{Tool.PICKAXE},
                    "Reveal",
                    4,
                    Hand.NONE,
                    BaseEnchantments.SWITCH, BaseEnchantments.PIERCE, BaseEnchantments.SPECTRAL)
            .cooldownMillis(5000);
    }

    public Reveal() {
        super(BaseEnchantments.REVEAL);
    }

    /**
     * Scans the blocks around the center for uncovered ores.
     * What an uncovered Ore is depends on Storage.COMPATIBILITY_ADAPTER.ores()
     * @param center The central location to iterate over
     * @param radius The radius of the search
     * @return The locations of the covered ores
     * @since 3.0.0-rc.2
     */
    public final LinkedHashSet<Location> scanOres(Block center, int radius) {
        LinkedHashSet<Location> locs = new LinkedHashSet<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block blk = center.getRelative(x, y, z);
                    if (Storage.COMPATIBILITY_ADAPTER.ores().contains(blk.getType())) {
                        boolean exposed = false;
                        for (BlockFace face : Storage.CARDINAL_BLOCK_FACES) {
                            if (Storage.COMPATIBILITY_ADAPTER.airs().contains(blk.getRelative(face).getType())) {
                                exposed = true;
                                break;
                            }
                        }
                        if (!exposed) {
                            locs.add(blk.getLocation());
                        }
                    }
                }
            }
        }
        return locs;
    }

    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (evt.getPlayer().isSneaking()) {
                int radius = (int) getAOESize(level);
                LinkedHashSet<Location> foundBlocks = scanOres(evt.getPlayer().getLocation().getBlock(), radius);
                foundBlocks.forEach((loc) -> {
                    LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.SHULKER);
                    entity.setGlowing(true);
                    entity.setGravity(false);
                    entity.setInvulnerable(true);
                    entity.setSilent(true);
                    entity.setAI(false);
                    Entity ent = GLOWING_BLOCKS.put(loc, entity);
                    if (ent != null) {
                        ent.remove();
                    }
                });
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
                    foundBlocks.forEach((loc) -> {
                        Entity blockToRemove = GLOWING_BLOCKS.remove(loc);
                        if (blockToRemove != null) {
                            blockToRemove.remove();
                        }
                    });
                }, 100);
                CompatibilityAdapter.damageTool(evt.getPlayer(), Math.max(16, (int) Math.round(foundBlocks.size() * 1.3)), usedHand);
                return true;
            }
        }
        return false;
    }

    /**
     * The Area of effect multiplier used by this enchantment.
     * @since 2.1.6
     * @see AreaOfEffectable
     */
    private double aoe = 1.0;
    
    @Override
    public double getAOESize(int level) {
        return 2 + aoe + level;
    }

    @Override
    public double getAOEMultiplier() {
        return aoe;
    }

    /**
     * Sets the multiplier used for the area of effect size calculation, the multiplier should have in most cases a linear impact,
     * however it's not guaranteed that the AOE Size is linear to the multiplier as some other effects may play a role.<br>
     * <br>
     * Impact formula: <b>2 + AOE + level</b>
     * @param newValue The new value of the multiplier
     * @since 2.1.6
     */
    @Override
    public void setAOEMultiplier(double newValue) {
        aoe = newValue;
    }
}
