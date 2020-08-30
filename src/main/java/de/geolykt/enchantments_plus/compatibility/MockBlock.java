/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.geolykt.enchantments_plus.compatibility;

import java.util.Collection;
import java.util.List;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper class for firing more accurate events
 */
public class MockBlock implements Block {

    private final Block realBlock;
    private final Material newType;
    private final byte newData;

    public MockBlock(Block realBlock, Material newType, byte newData) {
        this.realBlock = realBlock;
        this.newType = newType;
        this.newData = newData;
    }

    @Override
    public byte getData() {
        return newData;
    }

    @Override public BlockData getBlockData() {
        return realBlock.getBlockData();
    }

    @Override
    public Block getRelative(int i, int i1, int i2) {
        return realBlock.getRelative(i, i1, i2);
    }

    @Override
    public Block getRelative(BlockFace bf) {
        return realBlock.getRelative(bf);
    }

    @Override
    public Block getRelative(BlockFace bf, int i) {
        return realBlock.getRelative(bf, i);
    }

    @Override
    public Material getType() {
        return newType;
    }

    @Override
    public byte getLightLevel() {
        return realBlock.getLightLevel();
    }

    @Override
    public byte getLightFromSky() {
        return realBlock.getLightFromSky();
    }

    @Override
    public byte getLightFromBlocks() {
        return realBlock.getLightFromSky();
    }

    @Override
    public World getWorld() {
        return realBlock.getWorld();
    }

    @Override
    public int getX() {
        return realBlock.getX();
    }

    @Override
    public int getY() {
        return realBlock.getY();
    }

    @Override
    public int getZ() {
        return realBlock.getZ();
    }

    @Override
    public Location getLocation() {
        return realBlock.getLocation();
    }

    @Override
    public Location getLocation(Location lctn) {
        return realBlock.getLocation(lctn);
    }

    @Override
    public Chunk getChunk() {
        return realBlock.getChunk();
    }

    @Override
    public void setBlockData(BlockData data) {
        realBlock.setBlockData(data);
    }

    @Override public void setBlockData(BlockData data, boolean applyPhysics) {
        realBlock.setBlockData(data, applyPhysics);
    }

    @Override
    public void setType(Material mtrl) {
        realBlock.setType(mtrl);
    }

    @Override
    public void setType(Material mtrl, boolean bln) {
        realBlock.setType(mtrl, bln);
    }

    @Override
    public BlockFace getFace(Block block) {
        return realBlock.getFace(block);
    }

    @Override
    public BlockState getState() {
        return new MockBlockState(realBlock, newType, newData);
    }

    @Override
    public Biome getBiome() {
        return realBlock.getBiome();
    }

    @Override
    public void setBiome(Biome biome) {
        realBlock.setBiome(biome);
    }

    @Override
    public boolean isBlockPowered() {
        return realBlock.isBlockPowered();
    }

    @Override
    public boolean isBlockIndirectlyPowered() {
        return realBlock.isBlockIndirectlyPowered();
    }

    @Override
    public boolean isBlockFacePowered(BlockFace bf) {
        return realBlock.isBlockFacePowered(bf);
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(BlockFace bf) {
        return realBlock.isBlockFaceIndirectlyPowered(bf);
    }

    @Override
    public int getBlockPower(BlockFace bf) {
        return realBlock.getBlockPower(bf);
    }

    @Override
    public int getBlockPower() {
        return realBlock.getBlockPower();
    }

    @Override
    public boolean isEmpty() {
        return newType == Material.AIR;
    }

    @Override
    public boolean isLiquid() {
        return false;
    }

    @Override
    public double getTemperature() {
        return realBlock.getTemperature();
    }

    @Override
    public double getHumidity() {
        return realBlock.getHumidity();
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return realBlock.getPistonMoveReaction();
    }

    @Override
    public boolean breakNaturally() {
        return realBlock.breakNaturally();
    }

    @Override
    public boolean breakNaturally(ItemStack is) {
        return realBlock.breakNaturally(is);
    }

    @Override
    public Collection<ItemStack> getDrops() {
        return realBlock.getDrops();
    }

    @Override
    public Collection<ItemStack> getDrops(ItemStack is) {
        return realBlock.getDrops(is);
    }

    @Override public boolean isPassable() {
        return false;
    }

    @Override
    public RayTraceResult rayTrace(Location location, Vector vector, double v, FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    @Override public BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public void setMetadata(String string, MetadataValue mv) {
        realBlock.setMetadata(string, mv);
    }

    @Override
    public List<MetadataValue> getMetadata(String string) {
        return realBlock.getMetadata(string);
    }

    @Override
    public boolean hasMetadata(String string) {
        return realBlock.hasMetadata(string);
    }

    @Override
    public void removeMetadata(String string, Plugin plugin) {
        realBlock.removeMetadata(string, plugin);
    }

    @Override
    public @NotNull Collection<ItemStack> getDrops(@NotNull ItemStack arg0, @Nullable Entity arg1) {
        return realBlock.getDrops(arg0, arg1);
    }

    @Override
    public boolean applyBoneMeal(@NotNull BlockFace face) {
	return false;
    }
}
