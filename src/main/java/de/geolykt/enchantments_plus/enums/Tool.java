package de.geolykt.enchantments_plus.enums;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Material.*;

// Enum for Tool sets within the game, used by enchantment classes to easily define what tools can be used on each
//      enchantment. Each enum has a String representation used in the config, and an array of materials it represents
public enum Tool {
    AXE("Axe", new Material[]{WOODEN_AXE, STONE_AXE, GOLDEN_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE}, false),
    SHOVEL("Shovel", new Material[]{WOODEN_SHOVEL, STONE_SHOVEL, GOLDEN_SHOVEL, IRON_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL}, false),
    SWORD("Sword", new Material[]{WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD}, false),
    PICKAXE("Pickaxe", new Material[]{WOODEN_PICKAXE, STONE_PICKAXE, GOLDEN_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE}, false),
    ROD("Rod", new Material[]{FISHING_ROD}, true),
    SHEAR("Shears", new Material[]{SHEARS}, true),
    BOW("Bow", new Material[]{Material.BOW}, true),
    LIGHTER("Lighter", new Material[]{FLINT_AND_STEEL}, true),
    HOE("Hoe", new Material[]{WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE}, true),
    HELMET("Helmet", new Material[]{NETHERITE_HELMET, DIAMOND_HELMET, IRON_HELMET, GOLDEN_HELMET, CHAINMAIL_HELMET, LEATHER_HELMET}, false),
    CHESTPLATE("Chestplate", new Material[]{NETHERITE_CHESTPLATE, DIAMOND_CHESTPLATE, IRON_CHESTPLATE, GOLDEN_CHESTPLATE, CHAINMAIL_CHESTPLATE, LEATHER_CHESTPLATE, ELYTRA}, false),
    LEGGINGS("Leggings", new Material[]{NETHERITE_LEGGINGS, DIAMOND_LEGGINGS, IRON_LEGGINGS, GOLDEN_LEGGINGS, CHAINMAIL_LEGGINGS, LEATHER_LEGGINGS}, false),
    BOOTS("Boots", new Material[]{NETHERITE_BOOTS, DIAMOND_BOOTS, IRON_BOOTS, GOLDEN_BOOTS, CHAINMAIL_BOOTS, LEATHER_BOOTS}, false),
    WINGS("Elytra", new Material[]{ELYTRA}, false),
    ALL("All", new Material[]{WOODEN_AXE, STONE_AXE, GOLDEN_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE, WOODEN_SHOVEL, STONE_SHOVEL, GOLDEN_SHOVEL,
        IRON_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL, WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD, WOODEN_PICKAXE,
        STONE_PICKAXE, GOLDEN_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE, FISHING_ROD, SHEARS, Material.BOW, FLINT_AND_STEEL,
        WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE, NETHERITE_HELMET, DIAMOND_HELMET, IRON_HELMET, GOLDEN_HELMET, CHAINMAIL_HELMET,
        LEATHER_HELMET, NETHERITE_LEGGINGS, DIAMOND_CHESTPLATE, IRON_CHESTPLATE, GOLDEN_CHESTPLATE, CHAINMAIL_CHESTPLATE, LEATHER_CHESTPLATE,
        ELYTRA, DIAMOND_LEGGINGS, IRON_LEGGINGS, GOLDEN_LEGGINGS, CHAINMAIL_LEGGINGS, LEATHER_LEGGINGS, NETHERITE_BOOTS, DIAMOND_BOOTS,
        IRON_BOOTS, GOLDEN_BOOTS, CHAINMAIL_BOOTS, LEATHER_BOOTS, ELYTRA}, false);

    private final String id;              // The identification String of the enum
    private final Material[] materials;   // The array of materials the enum represents
    private final boolean rightClickAction; // If the tool has a right click action

    // Constructs a new enum of given materials with the given ID
    Tool(String id, Material[] materials, boolean rightClickAction) {
        this.id = id;
        this.materials = materials;
        this.rightClickAction = rightClickAction;
    }

    // Returns the String ID of the enum
    public String getID() {
        return id;
    }

    // Returns the array of materials that the enum represents
    public Material[] getMaterials() {
        return materials;
    }

    // Returns whether or not the tool type has a right click action
    public boolean canRightClickAction() {
        return rightClickAction;
    }

    // Returns true if the given material is contained within the enum, otherwise false
    public boolean contains(Material mat) {
        return ArrayUtils.contains(materials, mat);
    }

    // Returns true if the given item stack's material is contained within the enum, otherwise false
    public boolean contains(ItemStack stk) {
        return contains(stk.getType());
    }

    // Returns an enum using the given String, matching this with the enum's ID String
    public static Tool fromString(String text) {
        if (text != null) {
            for (Tool t : Tool.values()) {
                if (text.equalsIgnoreCase(t.id)) {
                    return t;
                }
            }
        }
        return null;
    }

    // Returns an enum using a given Material
    public static Tool fromMaterial(Material mat) {
        for (Tool t : Tool.values()) {
            if (t.contains(mat)) {
                return t;
            }
        }
        return null;
    }

    // Returns an enum using a given ItemStack
    public static Tool fromItemStack(ItemStack stk) {
        return stk == null ? null : fromMaterial(stk.getType());
    }

}