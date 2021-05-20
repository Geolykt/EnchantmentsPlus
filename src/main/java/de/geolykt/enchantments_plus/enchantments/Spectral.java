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

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.collect.Sets;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.evt.ench.BlockSpectralChangeEvent;
import de.geolykt.enchantments_plus.util.ColUtil;
import de.geolykt.enchantments_plus.util.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.*;

public class Spectral extends CustomEnchantment {

    /**
     * A hard maximum on the amount of blocks that can be changed while shift + right-clicking.
     *
     * @since 1.0
     */
    private static final int MAX_BLOCKS = 1024;

    public static int[][] SEARCH_FACES = new int[][]{new int[]{}};

    public static final int ID = 54;

    @Override
    public Builder<Spectral> defaults() {
        return new Builder<>(Spectral::new, ID)
                .all("Allows for cycling through a block's types",
                        new Tool[]{Tool.SHOVEL},
                        "Spectral",
                        3,
                        Hand.RIGHT);
    }

    public Spectral() {
        super(BaseEnchantments.SPECTRAL);
    }

    public boolean doEvent(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getClickedBlock() == null) {
            return false;
        }

        if (evt.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }
        Set<Block> potentialBlocks = new HashSet<>();
        potentialBlocks.add(evt.getClickedBlock());
        if (evt.getPlayer().isSneaking()) { 
            potentialBlocks.addAll(Utilities.BFS(evt.getClickedBlock(), MAX_BLOCKS, false, (float) (level * power)+2.0f,
                    SEARCH_FACES, Sets.immutableEnumSet(evt.getClickedBlock().getType()),
                    new HashSet<>(), false, true));
        }
        
        int blocksChanged = 0;
        Player p = evt.getPlayer();

        boolean doInteract = false;
        Material cache = null;
        UUID id = p.getUniqueId();
        String name = p.getName();
        if (enchantmentConfiguration.enableNativepermissionQuery()) {
            for (final Block b : potentialBlocks) {
                if (enchantmentConfiguration.enableSpectralNativePermissionQuery()) {
                    if (!Storage.COMPATIBILITY_ADAPTER.nativeBlockPermissionQueryingSystem(p, b)) {
                        continue;
                    }
                } else {
                    BlockSpectralChangeEvent blockSpectralChangeEvent = new BlockSpectralChangeEvent(b, p, BaseEnchantments.SPECTRAL);
                    Bukkit.getServer().getPluginManager().callEvent(blockSpectralChangeEvent);
                    if (blockSpectralChangeEvent.isCancelled()) {
                        continue;
                    }
                }
                // TODO: Optimise duplicated code.
                if (cache == null) {
                    cache = cycleBlockType(b);

                    BlockState old = null;
                    if (ADAPTER.doLog()) {
                        old = b.getState();
                    }
                    doInteract = blockstateInteract(b);
                    if (doInteract && ADAPTER.doLog()) {
                        ADAPTER.performLog(baseEnum, id, name, old, b);
                    }

                    if (!doInteract && cache == b.getType()) {
                        return false;
                    }
                } else {
                    cycleBlockType(b, cache, doInteract);
                }
                if (cache != null) {
                    blocksChanged++;
                }
            }
        } else {
            for (final Block b : potentialBlocks) {
                // TODO: Optimise duplicated code.
                if (cache == null) {
                    cache = cycleBlockType(b);

                    BlockState old = null;
                    if (ADAPTER.doLog()) {
                        old = b.getState();
                    }
                    doInteract = blockstateInteract(b);
                    if (doInteract && ADAPTER.doLog()) {
                        ADAPTER.performLog(baseEnum, id, name, old, b);
                    }

                    if (!doInteract && cache == b.getType()) {
                        return false;
                    }
                } else {
                    cycleBlockType(b, cache, doInteract);
                }
                if (cache != null) {
                    blocksChanged++;
                }
            }
        }
        
        CompatibilityAdapter.damageTool(evt.getPlayer(), (int) Math.ceil(Math.log(blocksChanged + 1) / 0.30102999566), usedHand);
        evt.setCancelled(true);
        return blocksChanged != 0;
    }

    /**
     * Internal Spectral utility to easy caching, the goal is to not unnecessarily change blockstates very often,
     * which is a resource-consuming task. (this does not even cache my dear, what was I thinking when writing this javadoc)
     *
     * @param block The target block
     * @return True if a change was performed, false otherwise
     * @since 1.2.0
     */
    private static boolean blockstateInteract(Block block) {
        // FIXME this method is coded in a very stupid manner, cleaning that up is an easy optimisation
        BlockData blockData = block.getBlockData();
        boolean performed = false;

        if (blockData instanceof Bisected) {
            Bisected newBlockData = (Bisected) block.getBlockData();
            newBlockData.setHalf(((Bisected) blockData).getHalf());
            block.setBlockData(newBlockData, false);

            Material original = block.getType();
            // Set the second half's data
            if (block.getRelative(BlockFace.UP).getType().equals(original)) {
                newBlockData.setHalf(Bisected.Half.TOP);
                block.getRelative(BlockFace.UP).setBlockData(newBlockData, false);
            }
            if (block.getRelative(BlockFace.DOWN).getType().equals(original)) {
                newBlockData.setHalf(Bisected.Half.BOTTOM);
                block.getRelative(BlockFace.DOWN).setBlockData(newBlockData, false);
            }
            performed = true;
        }

        if (blockData instanceof Bed) {
            Bed newBlockData = (Bed) block.getBlockData();
            newBlockData.setPart(((Bed) blockData).getPart());
            block.setBlockData(newBlockData, false);

            // Set the second bed's part
            BlockFace facing = !newBlockData.getPart().equals(Bed.Part.HEAD)
                    ? ((Bed) blockData).getFacing()
                            : ((Bed) blockData).getFacing().getOppositeFace();
            newBlockData.setPart(((Bed) block.getRelative(facing).getBlockData()).getPart());
            block.getRelative(facing).setBlockData(newBlockData, false);

            // Set the second bed's direction since we never do that later on
            Directional secondaryBlockData = (Directional) block.getRelative(facing).getBlockData();
            secondaryBlockData.setFacing(((Directional) blockData).getFacing());
            block.getRelative(facing).setBlockData(secondaryBlockData, true);
            performed = true;
        }

        // Optimisations would be like first checking the Openable, Powerable etc. instances, then the Door and Gate instances.
        if (blockData instanceof Gate) {
            Gate newBlockData = (Gate) block.getBlockData();
            newBlockData.setInWall(((Gate) blockData).isInWall());
            block.setBlockData(newBlockData, true);
            performed = true;
        }

        if (blockData instanceof Door) {
            Door newBlockData = (Door) block.getBlockData();
            newBlockData.setHinge(((Door) blockData).getHinge());
            block.setBlockData(newBlockData, true);
            performed = true;
        }

        if (blockData instanceof Orientable) {
            Orientable newBlockData = (Orientable) block.getBlockData();
            newBlockData.setAxis(((Orientable) blockData).getAxis());
            block.setBlockData(newBlockData, true);
            performed = true;
        }

        if (blockData instanceof Powerable) {
            Powerable newBlockData = (Powerable) block.getBlockData();
            newBlockData.setPowered(((Powerable) blockData).isPowered());
            block.setBlockData(newBlockData, true);
            performed = true;
        }

        if (blockData instanceof Openable) {
            Openable newBlockData = (Openable) block.getBlockData();
            newBlockData.setOpen(((Openable) blockData).isOpen());
            block.setBlockData(newBlockData, true);
            performed = true;
        }

        if (blockData instanceof Stairs) {
            Stairs newBlockData = (Stairs) block.getBlockData();
            newBlockData.setShape(((Stairs) blockData).getShape());
            block.setBlockData(newBlockData, true);
            performed = true;
        }

        if (blockData instanceof Slab) {
            Slab newBlockData = (Slab) block.getBlockData();
            newBlockData.setType(((Slab) blockData).getType());
            block.setBlockData(newBlockData, true);
            performed = true;
        }
        if (blockData instanceof MultipleFacing) {
            MultipleFacing newBlockData = (MultipleFacing) block.getBlockData();
            for (BlockFace bf : ((MultipleFacing) blockData).getFaces()) {
                newBlockData.setFace(bf, true);
            }
            block.setBlockData(newBlockData, true);
            performed = true;
        }
        if (blockData instanceof Directional) {
            Directional newBlockData = (Directional) block.getBlockData();
            newBlockData.setFacing(((Directional) blockData).getFacing());
            block.setBlockData(newBlockData, true);
            performed = true;
        }
        if (blockData instanceof Waterlogged) {
            Waterlogged newBlockData = (Waterlogged) block.getBlockData();
            newBlockData.setWaterlogged(((Waterlogged) blockData).isWaterlogged());
            block.setBlockData(newBlockData, true);
            performed = true;
        }
        return performed;
    }

    @Override
    public boolean onBlockInteractInteractable(PlayerInteractEvent evt, int level, boolean usedHand) {
        return doEvent(evt, level, usedHand);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        return doEvent(evt, level, usedHand);
    }

    private static DyeColor nextCol(DyeColor oldCol) {
        return DyeColor.values()[(oldCol.ordinal()+1) % DyeColor.values().length]; // TODO this is a hacky approach
    }

    private static Material cycleBlockType(Block block) {
        Material original = block.getType();
        Material newMat = Storage.COMPATIBILITY_ADAPTER.spectralConversionMap().getOrDefault(original, original);
        if ((newMat == original) && ColUtil.isDyeable(original)) {
            newMat = ColUtil.getDyedVariant(ColUtil.getAbstractDyeableType(original), nextCol(ColUtil.getDye(original)));
        }

        if (newMat != original) {
            final Material newMatFinal = newMat; // Why are we doing this?
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {

                block.setType(newMatFinal, false);
                blockstateInteract(block);
            }, 0);
        }
        return newMat;
    }
    
    private static void cycleBlockType(Block block, final Material newMat, final boolean doBlockStateInteraction) {
       Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.plugin, () -> {
           block.setType(newMat, false);
           if (doBlockStateInteraction) {
               blockstateInteract(block);
           }
        }, 0);
    }

}
