package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

import static de.geolykt.enchantments_plus.enums.Tool.SHEAR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class Mow extends CustomEnchantment {

    public static final int ID = 37;

    @Override
    public Builder<Mow> defaults() {
        return new Builder<>(Mow::new, ID)
            .maxLevel(3)
            .loreName("Mow")
            .probability(0)
            .enchantable(new Tool[]{SHEAR})
            .conflicting()
            .description("Shears all nearby sheep")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.RIGHT)
            .base(BaseEnchantments.MOW);
    }

    private boolean shear(PlayerEvent evt, int level, boolean usedHand) {
        boolean shearedEntity = false;
        int radius = (int) Math.round(level * power + 2);
        Player player = evt.getPlayer();
        for (Entity ent : evt.getPlayer().getNearbyEntities(radius, radius, radius)) {
            if (ent instanceof Sheep) {
                Sheep sheep = (Sheep) ent;
                if (sheep.isAdult()) {
                    ADAPTER.shearEntityNMS(sheep, player, usedHand);
                    shearedEntity = true;
                }
            } else if (ent instanceof MushroomCow) {
                MushroomCow mCow = (MushroomCow) ent;
                if (mCow.isAdult()) {
                    ADAPTER.shearEntityNMS(mCow, player, usedHand);
                    shearedEntity = true;
                }
            }
        }
        return shearedEntity;
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
            return shear(evt, level, usedHand);
        }
        return false;
    }

    @Override
    public boolean onShear(PlayerShearEntityEvent evt, int level, boolean usedHand) {
        return shear(evt, level, usedHand);
    }
}
