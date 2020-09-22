package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.collect.Sets;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.evt.ench.BlockSpectralChangeEvent;
import de.geolykt.enchantments_plus.util.ColUtil;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.SHOVEL;

import java.util.*;

public class Spectral extends CustomEnchantment {

    /**
     * A hard maximum on the amount of blocks that can be changed while shift + right-clicking.
     */
    private static final int MAX_BLOCKS = 1024;

    public static int[][] SEARCH_FACES = new int[][]{new int[]{}};

    public static final int ID = 54;

    /**
     * Designates whether or not the Spectral enchantment should query permissions to resolve (regional) world protection. <br>
     * This has the direct effect that the {@link BlockSpectralChangeEvent} cannot be constructed.
     * @since 1.0
     * @implNote Until 1.1.4 (included) this value was ignored and thus had the result of disabling the Spectral enchantment
     */
    public static boolean performWorldProtection = true;
    
    @Override
    public Builder<Spectral> defaults() {
        return new Builder<>(Spectral::new, ID)
                .maxLevel(3)
                .loreName("Spectral")
                .probability(0)
                .enchantable(new Tool[]{SHOVEL})
                .conflicting()
                .description("Allows for cycling through a block's types")
                .cooldown(0)
                .power(1)
                .handUse(Hand.RIGHT)
                .base(BaseEnchantments.SPECTRAL);
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
                    new HashSet<Material>(), false, true));
        }
        
        int blocksChanged = 0;
        Player p = evt.getPlayer();
        
        Material cache = null;
        if (performWorldProtection) {
            BlockSpectralChangeEvent blockSpectralChangeEvent = new BlockSpectralChangeEvent(evt.getClickedBlock(), p);
            for (final Block b : potentialBlocks) {
                blockSpectralChangeEvent.adjustBlock(b);
                Bukkit.getServer().getPluginManager().callEvent(blockSpectralChangeEvent);
                if (cache == null && !blockSpectralChangeEvent.isCancelled()) {
                    cache = cycleBlockType(b);
                    blocksChanged += cache == null ? 0 : 1;
                } else if (!blockSpectralChangeEvent.isCancelled() && cycleBlockType(b, cache)){
                    blocksChanged++;
                }
            }
        } else {
            for (final Block b : potentialBlocks) {
                if (cache == null) {
                    cache = cycleBlockType(b);
                    blocksChanged += cache == null ? 0 : 1;
                } else if (cycleBlockType(b, cache)){
                    blocksChanged++;
                }
            }
        }
        
        Utilities.damageTool(evt.getPlayer(), (int) Math.ceil(Math.log(blocksChanged + 1) / 0.30102999566), usedHand);
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
        return DyeColor.values()[(oldCol.ordinal()+1)%DyeColor.values().length]; // TODO this is a hacky approach
    }

    private Material cycleBlockType(Block block) {
        Material original = block.getType();
        Material newMat = Storage.COMPATIBILITY_ADAPTER.spectralConversionMap().getOrDefault(original, original);
        if ((newMat == original) && ColUtil.isDyeable(original)) {
            newMat = ColUtil.getDyedVariant(ColUtil.getAbstractDyeableType(original), nextCol(ColUtil.getDye(original)));
        }

        if (newMat != original) {
            BlockData blockData = block.getBlockData();
            final Material newMatFinal = newMat; // Why are we doing this?
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {

                block.setType(newMatFinal, false);

                if (blockData instanceof Bisected) {
                    Bisected newBlockData = (Bisected) block.getBlockData();
                    newBlockData.setHalf(((Bisected) blockData).getHalf());
                    block.setBlockData(newBlockData, false);

                    // Set the second half's data
                    if (block.getRelative(BlockFace.UP).getType().equals(original)) {
                        newBlockData.setHalf(Bisected.Half.TOP);
                        block.getRelative(BlockFace.UP).setBlockData(newBlockData, false);
                    }
                    if (block.getRelative(BlockFace.DOWN).getType().equals(original)) {
                        newBlockData.setHalf(Bisected.Half.BOTTOM);
                        block.getRelative(BlockFace.DOWN).setBlockData(newBlockData, false);
                    }
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

                }

                if (blockData instanceof Gate) {
                    Gate newBlockData = (Gate) block.getBlockData();
                    newBlockData.setInWall(((Gate) blockData).isInWall());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Door) {
                    Door newBlockData = (Door) block.getBlockData();
                    newBlockData.setHinge(((Door) blockData).getHinge());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Orientable) {
                    Orientable newBlockData = (Orientable) block.getBlockData();
                    newBlockData.setAxis(((Orientable) blockData).getAxis());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Powerable) {
                    Powerable newBlockData = (Powerable) block.getBlockData();
                    newBlockData.setPowered(((Powerable) blockData).isPowered());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Openable) {
                    Openable newBlockData = (Openable) block.getBlockData();
                    newBlockData.setOpen(((Openable) blockData).isOpen());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Stairs) {
                    Stairs newBlockData = (Stairs) block.getBlockData();
                    newBlockData.setShape(((Stairs) blockData).getShape());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Slab) {
                    Slab newBlockData = (Slab) block.getBlockData();
                    newBlockData.setType(((Slab) blockData).getType());
                    block.setBlockData(newBlockData, true);
                }
                if (blockData instanceof MultipleFacing) {
                    MultipleFacing newBlockData = (MultipleFacing) block.getBlockData();
                    for (BlockFace bf : ((MultipleFacing) blockData).getFaces()) {
                        newBlockData.setFace(bf, true);
                    }
                    block.setBlockData(newBlockData, true);
                }
                if (blockData instanceof Directional) {
                    Directional newBlockData = (Directional) block.getBlockData();
                    newBlockData.setFacing(((Directional) blockData).getFacing());
                    block.setBlockData(newBlockData, true);
                }
                if (blockData instanceof Waterlogged) {
                    Waterlogged newBlockData = (Waterlogged) block.getBlockData();
                    newBlockData.setWaterlogged(((Waterlogged) blockData).isWaterlogged());
                    block.setBlockData(newBlockData, true);
                }
            }, 0);
        }
        return newMat;
    }
    
    private boolean cycleBlockType(Block block, final Material newMat) {
        final Material original = block.getType();
        boolean changed = false;
        
        
        if (!newMat.equals(original)) {
            changed = true;
            BlockData blockData = block.getBlockData();
            final Material newMatFinal = newMat;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.enchantments_plus, () -> {

                block.setType(newMatFinal, false);

                if (blockData instanceof Bisected) {
                    Bisected newBlockData = (Bisected) block.getBlockData();
                    newBlockData.setHalf(((Bisected) blockData).getHalf());
                    block.setBlockData(newBlockData, false);

                    // Set the second half's data
                    if (block.getRelative(BlockFace.UP).getType().equals(original)) {
                        newBlockData.setHalf(Bisected.Half.TOP);
                        block.getRelative(BlockFace.UP).setBlockData(newBlockData, false);
                    }
                    if (block.getRelative(BlockFace.DOWN).getType().equals(original)) {
                        newBlockData.setHalf(Bisected.Half.BOTTOM);
                        block.getRelative(BlockFace.DOWN).setBlockData(newBlockData, false);
                    }
                } else  if (blockData instanceof Bed) {
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

                } else  if (blockData instanceof Gate) {
                    Gate newBlockData = (Gate) block.getBlockData();
                    newBlockData.setInWall(((Gate) blockData).isInWall());
                    block.setBlockData(newBlockData, true);
                } else if (blockData instanceof Door) {
                    Door newBlockData = (Door) block.getBlockData();
                    newBlockData.setHinge(((Door) blockData).getHinge());
                    block.setBlockData(newBlockData, true);
                } else if (blockData instanceof Orientable) {
                    Orientable newBlockData = (Orientable) block.getBlockData();
                    newBlockData.setAxis(((Orientable) blockData).getAxis());
                    block.setBlockData(newBlockData, true);
                } else if (blockData instanceof Powerable) {
                    Powerable newBlockData = (Powerable) block.getBlockData();
                    newBlockData.setPowered(((Powerable) blockData).isPowered());
                    block.setBlockData(newBlockData, true);
                } else if (blockData instanceof Openable) {
                    Openable newBlockData = (Openable) block.getBlockData();
                    newBlockData.setOpen(((Openable) blockData).isOpen());
                    block.setBlockData(newBlockData, true);
                } else if (blockData instanceof Stairs) {
                    Stairs newBlockData = (Stairs) block.getBlockData();
                    newBlockData.setShape(((Stairs) blockData).getShape());
                    block.setBlockData(newBlockData, true);
                } else if (blockData instanceof Slab) {
                    Slab newBlockData = (Slab) block.getBlockData();
                    newBlockData.setType(((Slab) blockData).getType());
                    block.setBlockData(newBlockData, true);
                } else if (blockData instanceof MultipleFacing) {
                    MultipleFacing newBlockData = (MultipleFacing) block.getBlockData();
                    for (BlockFace bf : ((MultipleFacing) blockData).getFaces()) {
                        newBlockData.setFace(bf, true);
                    }
                    block.setBlockData(newBlockData, true);
                } else if (blockData instanceof Directional) {
                    Directional newBlockData = (Directional) block.getBlockData();
                    newBlockData.setFacing(((Directional) blockData).getFacing());
                    block.setBlockData(newBlockData, true);
                } else if (blockData instanceof Waterlogged) {
                    Waterlogged newBlockData = (Waterlogged) block.getBlockData();
                    newBlockData.setWaterlogged(((Waterlogged) blockData).isWaterlogged());
                    block.setBlockData(newBlockData, true);
                }
            }, 0);
        }
        return changed;
    }

}
