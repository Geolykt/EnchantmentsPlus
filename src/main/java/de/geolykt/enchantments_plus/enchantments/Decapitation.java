package de.geolykt.enchantments_plus.enchantments;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;

import static de.geolykt.enchantments_plus.enums.Tool.SWORD;
import static org.bukkit.entity.EntityType.*;

public class Decapitation extends CustomEnchantment {

    private static final int BASE_PLAYER_DROP_CHANCE = 150;
    private static final int BASE_MOB_DROP_CHANCE    = 150;
    public static final  int ID                      = 11;

    @Override
    public Builder<Decapitation> defaults() {
        return new Builder<>(Decapitation::new, ID)
            .maxLevel(4)
            .loreName("Decapitation")
            .probability(0)
            .enchantable(new Tool[]{SWORD})
            .conflicting()
            .description("Increases the chance for dropping the enemies head on death")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.LEFT);
    }

    private static EntityType[] entities = new EntityType[]{PLAYER, SKELETON, WITHER_SKULL, ZOMBIE, CREEPER};
    private static Material[]   skulls   =
        new Material[]{Material.PLAYER_HEAD, Material.SKELETON_SKULL, Material.WITHER_SKELETON_SKULL,
            Material.ZOMBIE_HEAD, Material.CREEPER_HEAD};

    @Override
    public boolean onEntityKill(EntityDeathEvent evt, int level, boolean usedHand) {

        short id = (short) ArrayUtils.indexOf(entities, evt.getEntityType());
        if (id == -1) {
            return false;
        }
        ItemStack stk = new ItemStack(skulls[id], 1);
        if (id == 0) {
            if (Storage.rnd.nextInt(Math.max((int) Math.round(BASE_PLAYER_DROP_CHANCE / (level * power)), 1)) == 0) {

                SkullMeta meta = (SkullMeta) stk.getItemMeta();
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(evt.getEntity().getUniqueId()));
                stk.setItemMeta(meta);
                evt.getEntity().getWorld().dropItemNaturally(evt.getEntity().getLocation(), stk);
                return true;
            }
        } else if (Storage.rnd.nextInt(Math.max((int) Math.round(BASE_MOB_DROP_CHANCE / (level * power)), 1)) == 0) {
            evt.getEntity().getWorld().dropItemNaturally(evt.getEntity().getLocation(), stk);
            return true;
        }
        return false;
    }
}
