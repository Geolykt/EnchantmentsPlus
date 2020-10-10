package de.geolykt.enchantments_plus.util;

import java.util.EnumSet;
import java.util.Locale;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Tool that can be used for an Enchantment, mostly used to identify when an Enchantment should be applicable and when not. <br>
 * This class replaces the static Enum System that existed prior
 * @since 2.1.0
 *
 */
public class Tool {

    public static final Tool AXE = new Tool();
    public static final Tool HOE = new Tool();
    public static final Tool PICKAXE = new Tool();
    public static final Tool SHOVEL = new Tool();
    
    public static final Tool HELMET = new Tool();
    public static final Tool CHESTPLATE = new Tool();
    public static final Tool WINGS = new Tool();
    public static final Tool LEGGINGS = new Tool();
    public static final Tool BOOTS = new Tool();
    
    public static final Tool BOW = new Tool();
    public static final Tool SWORD = new Tool();

    public static final Tool ROD = new Tool();
    public static final Tool SHEARS = new Tool();
    
    public static final Tool ALL = new Tool();
    
    private EnumSet<Material> materials;
    
    private Tool () {
        materials = EnumSet.noneOf(Material.class);
    }
    
    public void setMaterials(EnumSet<Material> newMaterials) {
        this.materials = newMaterials;
    }

    public boolean contains(Material m) {
        return materials.contains(m);
    }

    public static @Nullable Tool fromString(String s) {
        switch (s.toLowerCase(Locale.ROOT)) {
        case "axe":
            return AXE;
        case "hoe":
            return HOE;
        case "pickaxe":
            return PICKAXE;
        case "shovel":
            return SHOVEL;
        case "helmet":
            return HELMET;
        case "chestplate":
            return CHESTPLATE;
        case "leggings":
            return LEGGINGS;
        case "boots":
            return BOOTS;
        case "bow":
            return BOW;
        case "sword":
            return SWORD;
        case "rod":
            return ROD;
        case "shear":
            return SHEARS;
        case "all":
            return ALL;
        }
        return null;
    }

    public Iterable<Material> getMaterials() {
        return materials;
    }
}
