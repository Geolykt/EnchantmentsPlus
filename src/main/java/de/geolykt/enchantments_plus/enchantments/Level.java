package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.arrows.EnchantedArrow;
import de.geolykt.enchantments_plus.arrows.enchanted.LevelArrow;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.util.Tool;

public class Level extends CustomEnchantment {

    public static final int ID = 32;

    @Override
    public Builder<Level> defaults() {
        return new Builder<>(Level::new, ID)
                .all(BaseEnchantments.LEVEL,
                        0,
                        "Drops more XP when killing mobs or mining ores",
                        new Tool[]{Tool.BOW, Tool.SWORD},
                        "Level",
                        3, // MAX LVL
                        1.0,
                        Hand.BOTH);
    }

    @Override
    public boolean onEntityKill(EntityDeathEvent evt, int level, boolean usedHand) {
        if (Storage.rnd.nextBoolean()) {
            evt.setDroppedExp((int) (evt.getDroppedExp() * (1.3 + (level * power * .5))));
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if (Storage.rnd.nextBoolean()) {
            evt.setExpToDrop((int) (evt.getExpToDrop() * (1.3 + (level * power * .5))));
            return true;
        }
        return false;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        if (Storage.rnd.nextBoolean()) {
            LevelArrow arrow = new LevelArrow((Arrow) evt.getProjectile(), level, power);
            EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }
        return false;
    }

}
