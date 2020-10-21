package de.geolykt.enchantments_plus.enchantments;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Unrepairable extends CustomEnchantment {

    public static final int ID = 73;

    @Override
    public Builder<Unrepairable> defaults() {
        return new Builder<>(Unrepairable::new, ID)
            .all(BaseEnchantments.UNREPAIRABLE,
                    "Prevents an item from being repaired",
                    new Tool[]{Tool.ALL},
                    "Unrepairable",
                    1,
                    Hand.NONE);
    }
}
