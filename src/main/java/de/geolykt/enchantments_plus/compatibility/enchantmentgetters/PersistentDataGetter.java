/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2022 Geolykt and EnchantmentsPlus contributors
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
package de.geolykt.enchantments_plus.compatibility.enchantmentgetters;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import de.geolykt.enchantments_plus.CustomEnchantment;


/**
 * The Enchantment gatherer used by <a href="https://github.com/Geolykt/NMSless-Zenchantments">
 *  Geolykt's NMSless-Enchantments_plus</a>, the implementation uses Persistent Data to store
 *  it's data modified for various reasons.<br>
 *  For performance reasons it's not recommended to use it,
 *  however it may be used when stability is a bigger concern as it doesn't use string manipulation. <br>
 *  Only functional for 1.16+
 *  @see BasicLoreGetter
 *  @see LeightweightPDCGetter
 *  @since 2.0.0
 */
public class PersistentDataGetter extends LeightweightPDCGetter {

    private final EnumSet<Material> getterAllowlist;

    /**
     *  If true the {@link #getterAllowlist} allowlist will be used as a denylist, false if it should be kept a allowlist
     * @since 2.0.0
     */
    private final boolean isGetterDenylist;

    /**
     * Constructor. Enchantments are not present in the lore whenever {@link ItemFlag#HIDE_ENCHANTS} is present.
     *
     * @param allowlist      The allowlist that should be used (items not in the allowlist will always return an empty enchantment list)
     * @param denylistToggle If true the allowlist will be used as a denylist, false if it should be kept a allowlist
     * @since 2.0.0
     * @deprecated This constructor does not set the {@link LeightweightPDCGetter#autohideEnchantments} flag to a programmer-defined value.
     */
    @Deprecated(forRemoval = true, since = "4.0.4")
    public PersistentDataGetter(EnumSet<Material> allowlist, boolean denylistToggle) {
        super(true);
        getterAllowlist = allowlist;
        isGetterDenylist = denylistToggle;
    }

    /**
     * Constructor. Whether enchantments are hidden with the hide enchants flag is specified by a parameter.
     *
     * @param allowlist            The allowlist that should be used (items not in the allowlist will always return an empty enchantment list)
     * @param denylistToggle       If true the allowlist will be used as a denylist, false if it should be kept a allowlist
     * @param autohideEnchantments Whether to hide enchantments from the lore when {@link ItemFlag#HIDE_ENCHANTS} is present.
     * @since 4.0.4
     */
    public PersistentDataGetter(EnumSet<Material> allowlist, boolean denylistToggle, boolean autohideEnchantments) {
        super(autohideEnchantments);
        getterAllowlist = allowlist;
        isGetterDenylist = denylistToggle;
    }

    @Override
    public LinkedHashMap<CustomEnchantment, Integer> getEnchants(@Nullable ItemStack stk, boolean acceptBooks, World world,
            List<String> outExtraLore) {
        if (stk != null) {
            if (isGetterDenylist) {
                // if item is not in the allowlist, then return nothing
                if (getterAllowlist.contains(stk.getType())) {
                    return new LinkedHashMap<>();
                }
            } else {
                // if item is in the denylist, then return nothing
                if (!getterAllowlist.contains(stk.getType())) {
                    return new LinkedHashMap<>();
                }
            }
            return super.getEnchants(stk, acceptBooks, world, outExtraLore);
        } else {
            return new LinkedHashMap<>(0);
        }
    }
}
