package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

import static de.geolykt.enchantments_plus.enums.Tool.*;

import java.util.Map;

public class PotionResistance extends CustomEnchantment {

    public static final int ID = 45;

    @Override
    public Builder<PotionResistance> defaults() {
        return new Builder<>(PotionResistance::new, ID)
            .maxLevel(4)
            .loreName("Potion Resistance")
            .probability(0)
            .enchantable(new Tool[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS})
            .conflicting()
            .description("Lessens the effects of all potions on players")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.NONE);
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
