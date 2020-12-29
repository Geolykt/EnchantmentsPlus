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
public final class Tool {

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

    public final void setMaterials(EnumSet<Material> newMaterials) {
        this.materials = newMaterials;
    }

    public final boolean contains(Material m) {
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
        case "shears":
            return SHEARS;
        case "wings":
        case "elytra":
            return WINGS;
        case "all":
            return ALL;
        }
        return null;
    }

    public final Iterable<Material> getMaterials() {
        return materials;
    }
}
