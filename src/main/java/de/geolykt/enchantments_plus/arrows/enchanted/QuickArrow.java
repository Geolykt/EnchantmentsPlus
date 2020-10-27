package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;

import de.geolykt.enchantments_plus.arrows.EnchantedArrow;

import java.util.List;

public class QuickArrow extends EnchantedArrow {

    public QuickArrow(AbstractArrow entity) {
        super(entity);
    }

    public void onLaunch(LivingEntity player, List<String> lore) {
        arrow.setVelocity(arrow.getVelocity().normalize().multiply(3.5f));
    }

    @Override
    public void onImpact() {} // This is done knowingly
}
