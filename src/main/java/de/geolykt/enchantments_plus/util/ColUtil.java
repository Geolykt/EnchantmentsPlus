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
package de.geolykt.enchantments_plus.util;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;

/**
 * Mojang, whyy?
 * <hr>
 * This class exists to make of coled items and blocks and to convert between their different cols<br>
 * This class should be removed once bukkit has an efficient way of converting between cols without requiring 1000 lines of code. <br>
 * The functions within this class are rather fast due to using switch statements
 */
public class ColUtil {

    /**
     * An enumeration of different items or blocks that can 
     */
    public enum AbstractDyeableType {
        BANNER,
        BED,
        CARPET,
        CONCRETE,
        CONCRETE_POWDER,
        GLASS,
        GLASS_PANE,
        GLAZED_TERRACOTTA,
        TERRACOTTA,
        WOOL
    }
    
    public static boolean isDyeable(Material in) {
        return getAbstractDyeableType(in) != null;
    }
    
    public static AbstractDyeableType getAbstractDyeableType(Material old) {
        switch (old) {
        case BLACK_BANNER:
            return AbstractDyeableType.BANNER;
        case BLACK_BED:
            return AbstractDyeableType.BED;
        case BLACK_CARPET:
            return AbstractDyeableType.CARPET;
        case BLACK_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case BLACK_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case BLACK_DYE:
            break;
        case BLACK_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case BLACK_SHULKER_BOX:
            break;
        case BLACK_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case BLACK_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case BLACK_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case BLACK_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case BLACK_WOOL:
            return AbstractDyeableType.WOOL;
        case BLUE_BANNER:
            return AbstractDyeableType.BANNER;
        case BLUE_BED:
            return AbstractDyeableType.BED;
        case BLUE_CARPET:
            return AbstractDyeableType.CARPET;
        case BLUE_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case BLUE_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case BLUE_DYE:
            break;
        case BLUE_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case BLUE_SHULKER_BOX:
            break;
        case BLUE_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case BLUE_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case BLUE_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case BLUE_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case BLUE_WOOL:
            return AbstractDyeableType.WOOL;
        case BROWN_BANNER:
            return AbstractDyeableType.BANNER;
        case BROWN_BED:
            return AbstractDyeableType.BED;
        case BROWN_CARPET:
            return AbstractDyeableType.CARPET;
        case BROWN_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case BROWN_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case BROWN_DYE:
            break;
        case BROWN_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case BROWN_SHULKER_BOX:
            break;
        case BROWN_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case BROWN_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case BROWN_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case BROWN_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case BROWN_WOOL:
            return AbstractDyeableType.WOOL;
        case CYAN_BANNER:
            return AbstractDyeableType.BANNER;
        case CYAN_BED:
            return AbstractDyeableType.BED;
        case CYAN_CARPET:
            return AbstractDyeableType.CARPET;
        case CYAN_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case CYAN_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case CYAN_DYE:
            break;
        case CYAN_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case CYAN_SHULKER_BOX:
            break;
        case CYAN_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case CYAN_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case CYAN_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case CYAN_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case CYAN_WOOL:
            return AbstractDyeableType.WOOL;
        case GLASS:
            return AbstractDyeableType.GLASS;
        case GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case GRAY_BANNER:
            return AbstractDyeableType.BANNER;
        case GRAY_BED:
            return AbstractDyeableType.BED;
        case GRAY_CARPET:
            return AbstractDyeableType.CARPET;
        case GRAY_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case GRAY_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case GRAY_DYE:
            break;
        case GRAY_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case GRAY_SHULKER_BOX:
            break;
        case GRAY_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case GRAY_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case GRAY_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case GRAY_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case GRAY_WOOL:
            return AbstractDyeableType.WOOL;
        case GREEN_BANNER:
            return AbstractDyeableType.BANNER;
        case GREEN_BED:
            return AbstractDyeableType.BED;
        case GREEN_CARPET:
            return AbstractDyeableType.CARPET;
        case GREEN_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case GREEN_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case GREEN_DYE:
            break;
        case GREEN_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case GREEN_SHULKER_BOX:
            break;
        case GREEN_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case GREEN_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case GREEN_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case GREEN_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case GREEN_WOOL:
            return AbstractDyeableType.WOOL;
        case LIGHT_BLUE_BANNER:
            return AbstractDyeableType.BANNER;
        case LIGHT_BLUE_BED:
            return AbstractDyeableType.BED;
        case LIGHT_BLUE_CARPET:
            return AbstractDyeableType.CARPET;
        case LIGHT_BLUE_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case LIGHT_BLUE_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case LIGHT_BLUE_DYE:
            break;
        case LIGHT_BLUE_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case LIGHT_BLUE_SHULKER_BOX:
            break;
        case LIGHT_BLUE_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case LIGHT_BLUE_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case LIGHT_BLUE_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case LIGHT_BLUE_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case LIGHT_BLUE_WOOL:
            return AbstractDyeableType.WOOL;
        case LIGHT_GRAY_BANNER:
            return AbstractDyeableType.BANNER;
        case LIGHT_GRAY_BED:
            return AbstractDyeableType.BED;
        case LIGHT_GRAY_CARPET:
            return AbstractDyeableType.CARPET;
        case LIGHT_GRAY_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case LIGHT_GRAY_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case LIGHT_GRAY_DYE:
            break;
        case LIGHT_GRAY_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case LIGHT_GRAY_SHULKER_BOX:
            break;
        case LIGHT_GRAY_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case LIGHT_GRAY_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case LIGHT_GRAY_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case LIGHT_GRAY_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case LIGHT_GRAY_WOOL:
            return AbstractDyeableType.WOOL;
        case LIME_BANNER:
            return AbstractDyeableType.BANNER;
        case LIME_BED:
            return AbstractDyeableType.BED;
        case LIME_CARPET:
            return AbstractDyeableType.CARPET;
        case LIME_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case LIME_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case LIME_DYE:
            break;
        case LIME_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case LIME_SHULKER_BOX:
            break;
        case LIME_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case LIME_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case LIME_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case LIME_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case LIME_WOOL:
            return AbstractDyeableType.WOOL;
        case MAGENTA_BANNER:
            return AbstractDyeableType.BANNER;
        case MAGENTA_BED:
            return AbstractDyeableType.BED;
        case MAGENTA_CARPET:
            return AbstractDyeableType.CARPET;
        case MAGENTA_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case MAGENTA_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case MAGENTA_DYE:
            break;
        case MAGENTA_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case MAGENTA_SHULKER_BOX:
            break;
        case MAGENTA_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case MAGENTA_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case MAGENTA_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case MAGENTA_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case MAGENTA_WOOL:
            return AbstractDyeableType.WOOL;
        case ORANGE_BANNER:
            return AbstractDyeableType.BANNER;
        case ORANGE_BED:
            return AbstractDyeableType.BED;
        case ORANGE_CARPET:
            return AbstractDyeableType.CARPET;
        case ORANGE_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case ORANGE_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case ORANGE_DYE:
            break;
        case ORANGE_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case ORANGE_SHULKER_BOX:
            break;
        case ORANGE_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case ORANGE_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case ORANGE_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case ORANGE_TULIP:
            break;
        case ORANGE_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case ORANGE_WOOL:
            return AbstractDyeableType.WOOL;
        case PINK_BANNER:
            return AbstractDyeableType.BANNER;
        case PINK_BED:
            return AbstractDyeableType.BED;
        case PINK_CARPET:
            return AbstractDyeableType.CARPET;
        case PINK_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case PINK_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case PINK_DYE:
            break;
        case PINK_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case PINK_SHULKER_BOX:
            break;
        case PINK_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case PINK_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case PINK_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case PINK_TULIP:
            break;
        case PINK_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case PINK_WOOL:
            return AbstractDyeableType.WOOL;
        case PURPLE_BANNER:
            return AbstractDyeableType.BANNER;
        case PURPLE_BED:
            return AbstractDyeableType.BED;
        case PURPLE_CARPET:
            return AbstractDyeableType.CARPET;
        case PURPLE_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case PURPLE_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case PURPLE_DYE:
            break;
        case PURPLE_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case PURPLE_SHULKER_BOX:
            break;
        case PURPLE_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case PURPLE_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case PURPLE_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case PURPLE_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case PURPLE_WOOL:
            return AbstractDyeableType.WOOL;
        case RED_BANNER:
            return AbstractDyeableType.BANNER;
        case RED_BED:
            return AbstractDyeableType.BED;
        case RED_CARPET:
            return AbstractDyeableType.CARPET;
        case RED_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case RED_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case RED_DYE:
            break;
        case RED_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case RED_SHULKER_BOX:
            break;
        case RED_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case RED_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case RED_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case RED_TULIP:
            break;
        case RED_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case RED_WOOL:
            return AbstractDyeableType.WOOL;
        case TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case WHITE_BANNER:
            return AbstractDyeableType.BANNER;
        case WHITE_BED:
            return AbstractDyeableType.BED;
        case WHITE_CARPET:
            return AbstractDyeableType.CARPET;
        case WHITE_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case WHITE_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case WHITE_DYE:
            break;
        case WHITE_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case WHITE_SHULKER_BOX:
            break;
        case WHITE_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case WHITE_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case WHITE_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case WHITE_TULIP:
            break;
        case WHITE_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case WHITE_WOOL:
            return AbstractDyeableType.WOOL;
        case YELLOW_BANNER:
            return AbstractDyeableType.BANNER;
        case YELLOW_BED:
            return AbstractDyeableType.BED;
        case YELLOW_CARPET:
            return AbstractDyeableType.CARPET;
        case YELLOW_CONCRETE:
            return AbstractDyeableType.CONCRETE;
        case YELLOW_CONCRETE_POWDER:
            return AbstractDyeableType.CONCRETE_POWDER;
        case YELLOW_DYE:
            break;
        case YELLOW_GLAZED_TERRACOTTA:
            return AbstractDyeableType.GLAZED_TERRACOTTA;
        case YELLOW_SHULKER_BOX:
            break;
        case YELLOW_STAINED_GLASS:
            return AbstractDyeableType.GLASS;
        case YELLOW_STAINED_GLASS_PANE:
            return AbstractDyeableType.GLASS_PANE;
        case YELLOW_TERRACOTTA:
            return AbstractDyeableType.TERRACOTTA;
        case YELLOW_WALL_BANNER:
            return AbstractDyeableType.BANNER;
        case YELLOW_WOOL:
            return AbstractDyeableType.WOOL;
        default:
            break;
        }
        return null;
    }
    
    public static Material getBannerCol (DyeColor col)  {
        switch (col) {
        case BLACK:
            return Material.BLACK_BANNER;
        case BLUE:
            return Material.BLUE_BANNER;
        case BROWN:
            return Material.BROWN_BANNER;
        case CYAN:
            return Material.CYAN_BANNER;
        case GRAY:
            return Material.GRAY_BANNER;
        case GREEN:
            return Material.GREEN_BANNER;
        case LIGHT_BLUE:
            return Material.LIGHT_BLUE_BANNER;
        case LIGHT_GRAY:
            return Material.LIGHT_GRAY_BANNER;
        case LIME:
            return Material.LIME_BANNER;
        case MAGENTA:
            return Material.MAGENTA_BANNER;
        case ORANGE:
            return Material.ORANGE_BANNER;
        case PINK:
            return Material.PINK_BANNER;
        case PURPLE:
            return Material.PURPLE_BANNER;
        case RED:
            return Material.RED_BANNER;
        case WHITE:
            return Material.WHITE_BANNER;
        case YELLOW:
            return Material.YELLOW_BANNER;
        default:
            return Material.WHITE_BANNER;
        }
    }
    
    public static Material getBedCol (DyeColor col)  {
        switch (col) {
        case BLACK:
            return Material.BLACK_BED;
        case BLUE:
            return Material.BLUE_BED;
        case BROWN:
            return Material.BROWN_BED;
        case CYAN:
            return Material.CYAN_BED;
        case GRAY:
            return Material.GRAY_BED;
        case GREEN:
            return Material.GREEN_BED;
        case LIGHT_BLUE:
            return Material.LIGHT_BLUE_BED;
        case LIGHT_GRAY:
            return Material.LIGHT_GRAY_BED;
        case LIME:
            return Material.LIME_BED;
        case MAGENTA:
            return Material.MAGENTA_BED;
        case ORANGE:
            return Material.ORANGE_BED;
        case PINK:
            return Material.PINK_BED;
        case PURPLE:
            return Material.PURPLE_BED;
        case RED:
            return Material.RED_BED;
        case WHITE:
            return Material.WHITE_BED;
        case YELLOW:
            return Material.YELLOW_BED;
        default:
            return Material.WHITE_BED;
        }
    }

    public static Material getCarpetCol (DyeColor col)  {
        switch (col) {
        case BLACK:
            return Material.BLACK_CARPET;
        case BLUE:
            return Material.BLUE_CARPET;
        case BROWN:
            return Material.BROWN_CARPET;
        case CYAN:
            return Material.CYAN_CARPET;
        case GRAY:
            return Material.GRAY_CARPET;
        case GREEN:
            return Material.GREEN_CARPET;
        case LIGHT_BLUE:
            return Material.LIGHT_BLUE_CARPET;
        case LIGHT_GRAY:
            return Material.LIGHT_GRAY_CARPET;
        case LIME:
            return Material.LIME_CARPET;
        case MAGENTA:
            return Material.MAGENTA_CARPET;
        case ORANGE:
            return Material.ORANGE_CARPET;
        case PINK:
            return Material.PINK_CARPET;
        case PURPLE:
            return Material.PURPLE_CARPET;
        case RED:
            return Material.RED_CARPET;
        case WHITE:
            return Material.WHITE_CARPET;
        case YELLOW:
            return Material.YELLOW_CARPET;
        default:
            return Material.WHITE_CARPET;
        }
    }
    
    public static Material getConcreteCol (DyeColor col)  {
        switch (col) {
        case BLACK:
            return Material.BLACK_CONCRETE;
        case BLUE:
            return Material.BLUE_CONCRETE;
        case BROWN:
            return Material.BROWN_CONCRETE;
        case CYAN:
            return Material.CYAN_CONCRETE;
        case GRAY:
            return Material.GRAY_CONCRETE;
        case GREEN:
            return Material.GREEN_CONCRETE;
        case LIGHT_BLUE:
            return Material.LIGHT_BLUE_CONCRETE;
        case LIGHT_GRAY:
            return Material.LIGHT_GRAY_CONCRETE;
        case LIME:
            return Material.LIME_CONCRETE;
        case MAGENTA:
            return Material.MAGENTA_CONCRETE;
        case ORANGE:
            return Material.ORANGE_CONCRETE;
        case PINK:
            return Material.PINK_CONCRETE;
        case PURPLE:
            return Material.PURPLE_CONCRETE;
        case RED:
            return Material.RED_CONCRETE;
        case WHITE:
            return Material.WHITE_CONCRETE;
        case YELLOW:
            return Material.YELLOW_CONCRETE;
        default:
            return Material.WHITE_CONCRETE;
        }
    }

    public static Material getConcretePowderCol (DyeColor col)  {
        switch (col) {
        case BLACK:
            return Material.BLACK_CONCRETE_POWDER;
        case BLUE:
            return Material.BLUE_CONCRETE_POWDER;
        case BROWN:
            return Material.BROWN_CONCRETE_POWDER;
        case CYAN:
            return Material.CYAN_CONCRETE_POWDER;
        case GRAY:
            return Material.GRAY_CONCRETE_POWDER;
        case GREEN:
            return Material.GREEN_CONCRETE_POWDER;
        case LIGHT_BLUE:
            return Material.LIGHT_BLUE_CONCRETE_POWDER;
        case LIGHT_GRAY:
            return Material.LIGHT_GRAY_CONCRETE_POWDER;
        case LIME:
            return Material.LIME_CONCRETE_POWDER;
        case MAGENTA:
            return Material.MAGENTA_CONCRETE_POWDER;
        case ORANGE:
            return Material.ORANGE_CONCRETE_POWDER;
        case PINK:
            return Material.PINK_CONCRETE_POWDER;
        case PURPLE:
            return Material.PURPLE_CONCRETE_POWDER;
        case RED:
            return Material.RED_CONCRETE_POWDER;
        case WHITE:
            return Material.WHITE_CONCRETE_POWDER;
        case YELLOW:
            return Material.YELLOW_CONCRETE_POWDER;
        default:
            return Material.WHITE_CONCRETE_POWDER;
        }
    }
    
    public static DyeColor getDye(Material old) {
        switch (old) {
        case BLACK_BANNER:
        case BLACK_BED:
        case BLACK_CARPET:
        case BLACK_CONCRETE:
        case BLACK_CONCRETE_POWDER:
        case BLACK_DYE:
        case BLACK_GLAZED_TERRACOTTA:
        case BLACK_SHULKER_BOX:
        case BLACK_STAINED_GLASS:
        case BLACK_STAINED_GLASS_PANE:
        case BLACK_TERRACOTTA:
        case BLACK_WALL_BANNER:
        case BLACK_WOOL:
            return DyeColor.BLACK;
        case BLUE_BANNER:
        case BLUE_BED:
        case BLUE_CARPET:
        case BLUE_CONCRETE:
        case BLUE_CONCRETE_POWDER:
        case BLUE_DYE:
        case BLUE_GLAZED_TERRACOTTA:
        case BLUE_SHULKER_BOX:
        case BLUE_STAINED_GLASS:
        case BLUE_STAINED_GLASS_PANE:
        case BLUE_TERRACOTTA:
        case BLUE_WALL_BANNER:
        case BLUE_WOOL:
            return DyeColor.BLUE;
        case BROWN_BANNER:
        case BROWN_BED:
        case BROWN_CARPET:
        case BROWN_CONCRETE:
        case BROWN_CONCRETE_POWDER:
        case BROWN_DYE:
        case BROWN_GLAZED_TERRACOTTA:
        case BROWN_SHULKER_BOX:
        case BROWN_STAINED_GLASS:
        case BROWN_STAINED_GLASS_PANE:
        case BROWN_TERRACOTTA:
        case BROWN_WALL_BANNER:
        case BROWN_WOOL:
            return DyeColor.BROWN;
        case CYAN_BANNER:
        case CYAN_BED:
        case CYAN_CARPET:
        case CYAN_CONCRETE:
        case CYAN_CONCRETE_POWDER:
        case CYAN_DYE:
        case CYAN_GLAZED_TERRACOTTA:
        case CYAN_SHULKER_BOX:
        case CYAN_STAINED_GLASS:
        case CYAN_STAINED_GLASS_PANE:
        case CYAN_TERRACOTTA:
        case CYAN_WALL_BANNER:
        case CYAN_WOOL:
            return DyeColor.CYAN;
        case GLASS:
        case GLASS_PANE:
            return null;
        case GRAY_BANNER:
        case GRAY_BED:
        case GRAY_CARPET:
        case GRAY_CONCRETE:
        case GRAY_CONCRETE_POWDER:
        case GRAY_DYE:
        case GRAY_GLAZED_TERRACOTTA:
        case GRAY_SHULKER_BOX:
        case GRAY_STAINED_GLASS:
        case GRAY_STAINED_GLASS_PANE:
        case GRAY_TERRACOTTA:
        case GRAY_WALL_BANNER:
        case GRAY_WOOL:
            return DyeColor.GRAY;
        case GREEN_BANNER:
        case GREEN_BED:
        case GREEN_CARPET:
        case GREEN_CONCRETE:
        case GREEN_CONCRETE_POWDER:
        case GREEN_DYE:
        case GREEN_GLAZED_TERRACOTTA:
        case GREEN_SHULKER_BOX:
        case GREEN_STAINED_GLASS:
        case GREEN_STAINED_GLASS_PANE:
        case GREEN_TERRACOTTA:
        case GREEN_WALL_BANNER:
        case GREEN_WOOL:
            return DyeColor.GREEN;
        case LIGHT_BLUE_BANNER:
        case LIGHT_BLUE_BED:
        case LIGHT_BLUE_CARPET:
        case LIGHT_BLUE_CONCRETE:
        case LIGHT_BLUE_CONCRETE_POWDER:
        case LIGHT_BLUE_DYE:
        case LIGHT_BLUE_GLAZED_TERRACOTTA:
        case LIGHT_BLUE_SHULKER_BOX:
        case LIGHT_BLUE_STAINED_GLASS:
        case LIGHT_BLUE_STAINED_GLASS_PANE:
        case LIGHT_BLUE_TERRACOTTA:
        case LIGHT_BLUE_WALL_BANNER:
        case LIGHT_BLUE_WOOL:
            return DyeColor.LIGHT_BLUE;
        case LIGHT_GRAY_BANNER:
        case LIGHT_GRAY_BED:
        case LIGHT_GRAY_CARPET:
        case LIGHT_GRAY_CONCRETE:
        case LIGHT_GRAY_CONCRETE_POWDER:
        case LIGHT_GRAY_DYE:
        case LIGHT_GRAY_GLAZED_TERRACOTTA:
        case LIGHT_GRAY_SHULKER_BOX:
        case LIGHT_GRAY_STAINED_GLASS:
        case LIGHT_GRAY_STAINED_GLASS_PANE:
        case LIGHT_GRAY_TERRACOTTA:
        case LIGHT_GRAY_WALL_BANNER:
        case LIGHT_GRAY_WOOL:
            return DyeColor.LIGHT_GRAY;
        case LIME_BANNER:
        case LIME_BED:
        case LIME_CARPET:
        case LIME_CONCRETE:
        case LIME_CONCRETE_POWDER:
        case LIME_DYE:
        case LIME_GLAZED_TERRACOTTA:
        case LIME_SHULKER_BOX:
        case LIME_STAINED_GLASS:
        case LIME_STAINED_GLASS_PANE:
        case LIME_TERRACOTTA:
        case LIME_WALL_BANNER:
        case LIME_WOOL:
            return DyeColor.LIME;
        case MAGENTA_BANNER:
        case MAGENTA_BED:
        case MAGENTA_CARPET:
        case MAGENTA_CONCRETE:
        case MAGENTA_CONCRETE_POWDER:
        case MAGENTA_DYE:
        case MAGENTA_GLAZED_TERRACOTTA:
        case MAGENTA_SHULKER_BOX:
        case MAGENTA_STAINED_GLASS:
        case MAGENTA_STAINED_GLASS_PANE:
        case MAGENTA_TERRACOTTA:
        case MAGENTA_WALL_BANNER:
        case MAGENTA_WOOL:
            return DyeColor.MAGENTA;
        case ORANGE_BANNER:
        case ORANGE_BED:
        case ORANGE_CARPET:
        case ORANGE_CONCRETE:
        case ORANGE_CONCRETE_POWDER:
        case ORANGE_DYE:
        case ORANGE_GLAZED_TERRACOTTA:
        case ORANGE_SHULKER_BOX:
        case ORANGE_STAINED_GLASS:
        case ORANGE_STAINED_GLASS_PANE:
        case ORANGE_TERRACOTTA:
        case ORANGE_WALL_BANNER:
        case ORANGE_WOOL:
            return DyeColor.ORANGE;
        case PINK_BANNER:
        case PINK_BED:
        case PINK_CARPET:
        case PINK_CONCRETE:
        case PINK_CONCRETE_POWDER:
        case PINK_DYE:
        case PINK_GLAZED_TERRACOTTA:
        case PINK_SHULKER_BOX:
        case PINK_STAINED_GLASS:
        case PINK_STAINED_GLASS_PANE:
        case PINK_TERRACOTTA:
        case PINK_TULIP:
        case PINK_WALL_BANNER:
        case PINK_WOOL:
            return DyeColor.PINK;
        case PURPLE_BANNER:
        case PURPLE_BED:
        case PURPLE_CARPET:
        case PURPLE_CONCRETE:
        case PURPLE_CONCRETE_POWDER:
        case PURPLE_DYE:
        case PURPLE_GLAZED_TERRACOTTA:
        case PURPLE_SHULKER_BOX:
        case PURPLE_STAINED_GLASS:
        case PURPLE_STAINED_GLASS_PANE:
        case PURPLE_TERRACOTTA:
        case PURPLE_WALL_BANNER:
        case PURPLE_WOOL:
            return DyeColor.PURPLE;
        case RED_BANNER:
        case RED_BED:
        case RED_CARPET:
        case RED_CONCRETE:
        case RED_CONCRETE_POWDER:
        case RED_DYE:
        case RED_GLAZED_TERRACOTTA:
        case RED_SHULKER_BOX:
        case RED_STAINED_GLASS:
        case RED_STAINED_GLASS_PANE:
        case RED_TERRACOTTA:
        case RED_WALL_BANNER:
        case RED_WOOL:
            return DyeColor.RED;
        case TERRACOTTA:
            return null;
        case WHITE_BANNER:
        case WHITE_BED:
        case WHITE_CARPET:
        case WHITE_CONCRETE:
        case WHITE_CONCRETE_POWDER:
        case WHITE_DYE:
        case WHITE_GLAZED_TERRACOTTA:
        case WHITE_SHULKER_BOX:
        case WHITE_STAINED_GLASS:
        case WHITE_STAINED_GLASS_PANE:
        case WHITE_TERRACOTTA:
        case WHITE_WALL_BANNER:
        case WHITE_WOOL:
            return DyeColor.WHITE;
        case YELLOW_BANNER:
        case YELLOW_BED:
        case YELLOW_CARPET:
        case YELLOW_CONCRETE:
        case YELLOW_CONCRETE_POWDER:
        case YELLOW_DYE:
        case YELLOW_GLAZED_TERRACOTTA:
        case YELLOW_SHULKER_BOX:
        case YELLOW_STAINED_GLASS:
        case YELLOW_STAINED_GLASS_PANE:
        case YELLOW_TERRACOTTA:
        case YELLOW_WALL_BANNER:
        case YELLOW_WOOL:
            return DyeColor.YELLOW;
        default:
            break;
        }
        return null;
    }
    
    public static Material getDyedVariant (AbstractDyeableType type, DyeColor dye) {
        switch (type) {
        case BANNER:
            return getBannerCol(dye);
        case BED:
            return getBedCol(dye);
        case CARPET:
            return getCarpetCol(dye);
        case CONCRETE:
            return getConcreteCol(dye);
        case CONCRETE_POWDER:
            return getConcretePowderCol(dye);
        case GLASS:
            return getGlassCol(dye);
        case GLASS_PANE:
            return getGlassPaneCol(dye);
        case GLAZED_TERRACOTTA:
            return getGlazedTerracottaCol(dye);
        case TERRACOTTA:
            return getTerracottaCol(dye);
        case WOOL:
            return getWoolCol(dye);
        default:
            Bukkit.getLogger().warning("Outdated class: ColUtil.java in zedly.zenchantments.util");
            return null;
        
        }
    }
    
    public static Material getGlassCol (DyeColor col)  {
        switch (col) {
        case BLACK:
            return Material.BLACK_STAINED_GLASS;
        case BLUE:
            return Material.BLUE_STAINED_GLASS;
        case BROWN:
            return Material.BROWN_STAINED_GLASS;
        case CYAN:
            return Material.CYAN_STAINED_GLASS;
        case GRAY:
            return Material.GRAY_STAINED_GLASS;
        case GREEN:
            return Material.GREEN_STAINED_GLASS;
        case LIGHT_BLUE:
            return Material.LIGHT_BLUE_STAINED_GLASS;
        case LIGHT_GRAY:
            return Material.LIGHT_GRAY_STAINED_GLASS;
        case LIME:
            return Material.LIME_STAINED_GLASS;
        case MAGENTA:
            return Material.MAGENTA_STAINED_GLASS;
        case ORANGE:
            return Material.ORANGE_STAINED_GLASS;
        case PINK:
            return Material.PINK_STAINED_GLASS;
        case PURPLE:
            return Material.PURPLE_STAINED_GLASS;
        case RED:
            return Material.RED_STAINED_GLASS;
        case WHITE:
            return Material.WHITE_STAINED_GLASS;
        case YELLOW:
            return Material.YELLOW_STAINED_GLASS;
        default:
            return Material.GLASS;
        }
    }

    public static Material getGlassPaneCol (DyeColor col)  {
        switch (col) {
        case BLACK:
            return Material.BLACK_STAINED_GLASS_PANE;
        case BLUE:
            return Material.BLUE_STAINED_GLASS_PANE;
        case BROWN:
            return Material.BROWN_STAINED_GLASS_PANE;
        case CYAN:
            return Material.CYAN_STAINED_GLASS_PANE;
        case GRAY:
            return Material.GRAY_STAINED_GLASS_PANE;
        case GREEN:
            return Material.GREEN_STAINED_GLASS_PANE;
        case LIGHT_BLUE:
            return Material.LIGHT_BLUE_STAINED_GLASS_PANE;
        case LIGHT_GRAY:
            return Material.LIGHT_GRAY_STAINED_GLASS_PANE;
        case LIME:
            return Material.LIME_STAINED_GLASS_PANE;
        case MAGENTA:
            return Material.MAGENTA_STAINED_GLASS_PANE;
        case ORANGE:
            return Material.ORANGE_STAINED_GLASS_PANE;
        case PINK:
            return Material.PINK_STAINED_GLASS_PANE;
        case PURPLE:
            return Material.PURPLE_STAINED_GLASS_PANE;
        case RED:
            return Material.RED_STAINED_GLASS_PANE;
        case WHITE:
            return Material.WHITE_STAINED_GLASS_PANE;
        case YELLOW:
            return Material.YELLOW_STAINED_GLASS_PANE;
        default:
            return Material.GLASS_PANE;
        }
    }

    public static Material getGlazedTerracottaCol (DyeColor col)  {
        switch (col) {
        case BLACK:
            return Material.BLACK_GLAZED_TERRACOTTA;
        case BLUE:
            return Material.BLUE_GLAZED_TERRACOTTA;
        case BROWN:
            return Material.BROWN_GLAZED_TERRACOTTA;
        case CYAN:
            return Material.CYAN_GLAZED_TERRACOTTA;
        case GRAY:
            return Material.GRAY_GLAZED_TERRACOTTA;
        case GREEN:
            return Material.GREEN_GLAZED_TERRACOTTA;
        case LIGHT_BLUE:
            return Material.LIGHT_BLUE_GLAZED_TERRACOTTA;
        case LIGHT_GRAY:
            return Material.LIGHT_GRAY_GLAZED_TERRACOTTA;
        case LIME:
            return Material.LIME_GLAZED_TERRACOTTA;
        case MAGENTA:
            return Material.MAGENTA_GLAZED_TERRACOTTA;
        case ORANGE:
            return Material.ORANGE_GLAZED_TERRACOTTA;
        case PINK:
            return Material.PINK_GLAZED_TERRACOTTA;
        case PURPLE:
            return Material.PURPLE_GLAZED_TERRACOTTA;
        case RED:
            return Material.RED_GLAZED_TERRACOTTA;
        case WHITE:
            return Material.WHITE_GLAZED_TERRACOTTA;
        case YELLOW:
            return Material.YELLOW_GLAZED_TERRACOTTA;
        default:
            return Material.WHITE_GLAZED_TERRACOTTA;
        }
    }
    
    public static Material getTerracottaCol (DyeColor col)  {
        switch (col) {
        case BLACK:
            return Material.BLACK_TERRACOTTA;
        case BLUE:
            return Material.BLUE_TERRACOTTA;
        case BROWN:
            return Material.BROWN_TERRACOTTA;
        case CYAN:
            return Material.CYAN_TERRACOTTA;
        case GRAY:
            return Material.GRAY_TERRACOTTA;
        case GREEN:
            return Material.GREEN_TERRACOTTA;
        case LIGHT_BLUE:
            return Material.LIGHT_BLUE_TERRACOTTA;
        case LIGHT_GRAY:
            return Material.LIGHT_GRAY_TERRACOTTA;
        case LIME:
            return Material.LIME_TERRACOTTA;
        case MAGENTA:
            return Material.MAGENTA_TERRACOTTA;
        case ORANGE:
            return Material.ORANGE_TERRACOTTA;
        case PINK:
            return Material.PINK_TERRACOTTA;
        case PURPLE:
            return Material.PURPLE_TERRACOTTA;
        case RED:
            return Material.RED_TERRACOTTA;
        case WHITE:
            return Material.WHITE_TERRACOTTA;
        case YELLOW:
            return Material.YELLOW_TERRACOTTA;
        default:
            return Material.TERRACOTTA;
        }
    }
    
    public static Material getWoolCol (DyeColor col)  {
        switch (col) {
        case BLACK:
            return Material.BLACK_WOOL;
        case BLUE:
            return Material.BLUE_WOOL;
        case BROWN:
            return Material.BROWN_WOOL;
        case CYAN:
            return Material.CYAN_WOOL;
        case GRAY:
            return Material.GRAY_WOOL;
        case GREEN:
            return Material.GREEN_WOOL;
        case LIGHT_BLUE:
            return Material.LIGHT_BLUE_WOOL;
        case LIGHT_GRAY:
            return Material.LIGHT_GRAY_WOOL;
        case LIME:
            return Material.LIME_WOOL;
        case MAGENTA:
            return Material.MAGENTA_WOOL;
        case ORANGE:
            return Material.ORANGE_WOOL;
        case PINK:
            return Material.PINK_WOOL;
        case PURPLE:
            return Material.PURPLE_WOOL;
        case RED:
            return Material.RED_WOOL;
        case WHITE:
            return Material.WHITE_WOOL;
        case YELLOW:
            return Material.YELLOW_WOOL;
        default:
            return Material.WHITE_WOOL;
        }
    }

    public static Color toBukkitColor(String parseString, Color defaultValue) {
        switch (parseString) {
        case "AQUA":
            return Color.AQUA;
        case "BLACK":
            return Color.BLACK;
        case "BLUE":
            return Color.BLUE;
        case "FUCHSIA":
            return Color.FUCHSIA;
        case "GRAY":
        case "GREY":
            return Color.GRAY;
        case "GREEN":
            return Color.GREEN;
        case "LIME":
            return Color.LIME;
        case "MAROON":
            return Color.MAROON;
        case "NAVY":
            return Color.NAVY;
        case "OLIVE":
            return Color.OLIVE;
        case "ORANGE":
            return Color.ORANGE;
        case "PURPLE":
            return Color.PURPLE;
        case "RED":
            return Color.RED;
        case "SILVER":
            return Color.SILVER;
        case "TEAL":
            return Color.TEAL;
        case "WHITE": 
            return Color.WHITE;
        case "YELLOW":
            return Color.YELLOW;
        default:
            return defaultValue;
        }
    }
    
    /*
    public static Material getTerracottaCol (DyeColor col)  {
        switch (col) {
        case BLACK:
            return Material.BLACK_
        case BLUE:
            return Material.BLUE_
        case BROWN:
            return Material.BROWN_
        case CYAN:
            return Material.CYAN_
        case GRAY:
            return Material.GRAY_
        case GREEN:
            return Material.GREEN_
        case LIGHT_BLUE:
            return Material.LIGHT_BLUE_
        case LIGHT_GRAY:
            return Material.LIGHT_GRAY_
        case LIME:
            return Material.LIME_
        case MAGENTA:
            return Material.MAGENTA_
        case ORANGE:
            return Material.ORANGE_
        case PINK:
            return Material.PINK_
        case PURPLE:
            return Material.PURPLE_
        case RED:
            return Material.RED_
        case WHITE:
            return Material.WHITE_
        case YELLOW:
            return Material.YELLOW_
        default:
            return Material.WHITE_
        }
    }*/
}
