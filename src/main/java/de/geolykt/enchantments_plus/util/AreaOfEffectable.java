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
package de.geolykt.enchantments_plus.util;

/**
 * Marks that the Enchantment has an Area of Effect. <br>
 * This interface provides methods to scale the AOE to some extent as well as getting it's size.
 * @since 2.1.6
 */
public interface AreaOfEffectable {

    /**
     * While this method may get the size of the area of effect for a certain level, this may not always cover all cases
     *  in which the AOE could be affected. <br> Additionally the AOE has the player (or arrow) at it's center and the size
     *  is the distance between the border of the AOE and the center of the AOE. <br>
     *  Implementations should ideally make use of {@link #getAOEMultiplier()}.
     * @param level The level of the enchantment
     * @return The size of the AOE
     * @since 2.1.6
     */
    public double getAOESize(int level);

    /**
     * Returns the multiplier for the area of effect size calculation. Usually only for internal use as the actual size is in
     * most cases more important.
     * @return Multiplier for the Area of Effect.
     * @see #getAOESize(int)
     * @see #setAOEMultiplier(double)
     * @since 2.1.6
     */
    public double getAOEMultiplier();
    
    /**
     * Sets the multiplier used for the area of effect size calculation, the multiplier should have in most cases a linear impact,
     * however it's not guaranteed that the AOE Size is linear to the multiplier as some other effects may play a role.
     * @param newValue The new value of the multiplier
     * @since 2.1.6
     * @see #getAOEMultiplier()
     */
    public void setAOEMultiplier(double newValue);
}
