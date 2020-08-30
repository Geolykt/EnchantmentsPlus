package de.geolykt.enchantments_plus.enchantments;

import static de.geolykt.enchantments_plus.enums.Tool.ALL;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

public class Unrepairable extends CustomEnchantment {

    public static final int ID = 73;

    @Override
    public Builder<Unrepairable> defaults() {
        return new Builder<>(Unrepairable::new, ID)
            .maxLevel(1)
            .loreName("Unrepairable")
            .probability(0)
            .enchantable(new Tool[]{ALL})
            .conflicting()
            .description("Prevents an item from being repaired")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.NONE)
            .base(BaseEnchantments.UNREPAIRABLE);
    }
}
