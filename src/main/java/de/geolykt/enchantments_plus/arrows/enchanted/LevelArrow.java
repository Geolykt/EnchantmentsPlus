package de.geolykt.enchantments_plus.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDeathEvent;

import de.geolykt.enchantments_plus.arrows.EnchantedArrow;

public class LevelArrow extends EnchantedArrow {

    public LevelArrow(Arrow entity, int level, double power) {
        super(entity, level, power);
    }

    public void onKill(EntityDeathEvent evt) {
        die(true);
    }
        
        public void onImpact() {
            die(false);
        }
}
