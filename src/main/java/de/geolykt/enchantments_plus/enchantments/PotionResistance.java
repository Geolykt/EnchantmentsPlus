package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

import java.util.Map;

public class PotionResistance extends CustomEnchantment {

    public static final int ID = 45;

    @Override
    public Builder<PotionResistance> defaults() {
        return new Builder<>(PotionResistance::new, ID)
            .all("Lessens the effects of all potions on players, even the good ones",
                    new Tool[]{Tool.HELMET, Tool.CHESTPLATE, Tool.LEGGINGS, Tool.BOOTS},
                    "Potion Resistance",
                    4,
                    Hand.NONE);
    }

    private PotionResistance() {
        super(BaseEnchantments.POTION_RESISTANCE);
    }

    @Override
    public boolean onPotionSplash(PotionSplashEvent evt, int level, boolean usedHand) {
        for (LivingEntity ent : evt.getAffectedEntities()) {
            if (ent instanceof Player) {
                int effect = 0;
                for (ItemStack stk : ((Player) ent).getInventory().getArmorContents()) {
                    Map<CustomEnchantment, Integer> map = CustomEnchantment.getEnchants(stk, ent.getWorld());
                    for (CustomEnchantment e : map.keySet()) {
                        if (e.equals(this)) {
                            effect += map.get(e);
                        }
                    }
                }
                evt.setIntensity(ent, evt.getIntensity(ent) / ((effect * power + 1.3) / 2));
            }
        }
        return true;
    }
}
