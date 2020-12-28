package de.geolykt.enchantments_plus.enchantments;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Bind extends CustomEnchantment {

    public static final int ID = 4;

    @Override
    public Builder<Bind> defaults() {
        return new Builder<>(Bind::new, ID)
                .all("Keeps items with this enchantment in your inventory after death", // DESCRIPTION
                    new Tool[]{Tool.ALL}, // APPLICABLE TOOLS
                    "Bind", // NAME
                    1, // MAX LEVEL
                    Hand.NONE);
    }

    public Bind() {
        super(BaseEnchantments.BIND);
    }
}