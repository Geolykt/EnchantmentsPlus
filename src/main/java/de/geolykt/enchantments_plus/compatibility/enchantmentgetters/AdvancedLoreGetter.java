package de.geolykt.enchantments_plus.compatibility.enchantmentgetters;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;

/**
 * A more advanced form of the BasicLoreGetter with an integrated allowlist/denylist feature
 * @since 2.0.0
 */
public class AdvancedLoreGetter extends BasicLoreGetter {

    private final EnumSet<Material> getterAllowlist;

    /**
     *  If true the {@link #getterAllowlist} allowlist will be used as a denylist, false if it should be kept a allowlist
     * @since 2.0.0
     */
    private final boolean isGetterDenylist;

    /**
     * Constructor
     * @param allowlist The allowlist that should be used (items not in the allowlist will always return an empty enchantment list)
     * @param denylistToggle If true the allowlist will be used as a denylist, false if it should be kept a allowlist
     * @since 2.0.0
     */
    public AdvancedLoreGetter(EnumSet<Material> allowlist, boolean denylistToggle) {
        getterAllowlist = allowlist;
        isGetterDenylist = denylistToggle;
    }

    @Override
    public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks, World world,
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
