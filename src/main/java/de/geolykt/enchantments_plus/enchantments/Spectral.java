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
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.compatibility.CompatibilityAdapter;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
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
                .all("Allows for cycling through a block's cousin types",
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
                    SEARCH_FACES, EnumSet.of(evt.getClickedBlock().getType()),
                    new HashSet<>(), false, true));
        }

        int blocksChanged = 0;
        Player p = evt.getPlayer();

        Material cache = null;
        UUID id = p.getUniqueId();
        String name = p.getName();
        if (enchantmentConfiguration.enableNativepermissionQuery()) {
            for (final Block b : potentialBlocks) {
                if (enchantmentConfiguration.enableSpectralNativePermissionQuery()) {
                    if (!Storage.COMPATIBILITY_ADAPTER.nativeBlockPermissionQueryingSystem(p, b)) {
                        continue;
                    }
                }
                if (cache == null) {
                    cache = cycleBlockType(b);

                    BlockState old = null;
                    if (ADAPTER.doLog()) {
                        old = b.getState();
                    }
                    if (ADAPTER.doLog()) {
                        ADAPTER.performLog(baseEnum, id, name, old, b);
                    }

                    if (cache == b.getType()) {
                        return false;
                    }
                } else {
                    Material finalMat = cache;
                    // Believe it or not, this task is needed or the plugin behaves a bit strange
                    Bukkit.getScheduler().runTask(Storage.plugin, () -> {
                        b.setType(finalMat, false);
                    });
                    b.setType(cache, false);
                }
                if (cache != null) {
                    blocksChanged++;
                }
            }
        } else {
            for (final Block b : potentialBlocks) {
                if (cache == null) {
                    cache = cycleBlockType(b);

                    BlockState old = null;
                    if (ADAPTER.doLog()) {
                        old = b.getState();
                    }
                    if (ADAPTER.doLog()) {
                        ADAPTER.performLog(baseEnum, id, name, old, b);
                    }

                    if (cache == b.getType()) {
                        return false;
                    }
                } else {
                    b.setType(cache, false);
                }
                if (cache != null) {
                    Material finalMat = cache;
                    // Believe it or not, this task is needed or the plugin behaves a bit strange
                    Bukkit.getScheduler().runTask(Storage.plugin, () -> {
                        b.setType(finalMat, false);
                    });
                    blocksChanged++;
                }
            }
        }

        CompatibilityAdapter.damageTool(evt.getPlayer(), (int) Math.ceil(Math.log(blocksChanged + 1) / 0.30102999566), usedHand);
        evt.setCancelled(true);
        return blocksChanged != 0;
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
        return DyeColor.values()[(oldCol.ordinal()+1) % DyeColor.values().length];
    }

    private static Material cycleBlockType(Block block) {
        Material original = block.getType();
        Material newMat = Storage.COMPATIBILITY_ADAPTER.spectralConversionMap().getOrDefault(original, original);
        if ((newMat == original) && ColUtil.isDyeable(original)) {
            newMat = ColUtil.getDyedVariant(ColUtil.getAbstractDyeableType(original), nextCol(ColUtil.getDye(original)));
        }

        if (newMat != original) {
            Material finalMat = newMat;
            // Believe it or not, this task is needed or the plugin behaves a bit strange
            Bukkit.getScheduler().runTask(Storage.plugin, () -> {
                block.setType(finalMat, false);
            });
        }
        return newMat;
    }
}
