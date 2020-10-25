package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Ageable;
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
import de.geolykt.enchantments_plus.util.AreaOfEffectable;
import de.geolykt.enchantments_plus.util.Tool;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class Mow extends CustomEnchantment implements AreaOfEffectable {

    public static final int ID = 37;

    @Override
    public Builder<Mow> defaults() {
        return new Builder<>(Mow::new, ID)
            .all(BaseEnchantments.MOW,
                    "Shears all nearby sheep",
                    new Tool[]{Tool.SHEARS},
                    "Mow",
                    3,
                    Hand.RIGHT);
    }

    private boolean shear(PlayerEvent evt, int level, boolean usedHand) {
        // TODO damage shear proportional to the amount of sheared entities 
        boolean shearedEntity = false;
        int radius = (int) getAOESize(level);
        Player player = evt.getPlayer();
        for (Entity ent : evt.getPlayer().getNearbyEntities(radius, radius, radius)) {
            if (ent instanceof Sheep || ent instanceof MushroomCow && ((Ageable)ent).isAdult()) {
                ADAPTER.shearEntityNMS(ent, player, usedHand);
                shearedEntity = true;
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

    /**
     * The Area of effect multiplier used by this enchantment.
     * @since 2.1.6
     * @see AreaOfEffectable
     */
    private double aoe = 1.0;
    
    @Override
    public double getAOESize(int level) {
        return 2 + aoe + level;
    }

    @Override
    public double getAOEMultiplier() {
        return aoe;
    }

    /**
     * Sets the multiplier used for the area of effect size calculation, the multiplier should have in most cases a linear impact,
     * however it's not guaranteed that the AOE Size is linear to the multiplier as some other effects may play a role.<br>
     * <br>
     * Impact formula: <b>2 + AOE + level</b>
     * @param newValue The new value of the multiplier
     * @since 2.1.6
     */
    @Override
    public void setAOEMultiplier(double newValue) {
        aoe = newValue;
    }

}
