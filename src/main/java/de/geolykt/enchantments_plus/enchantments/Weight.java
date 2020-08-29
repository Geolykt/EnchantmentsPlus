package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import static de.geolykt.enchantments_plus.enums.Tool.BOOTS;
import static org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE;

public class Weight extends CustomEnchantment {

    public static final int ID = 67;

    @Override
    public Builder<Weight> defaults() {
        return new Builder<>(Weight::new, ID)
            .maxLevel(4)
            .loreName("Weight")
            .probability(0)
            .enchantable(new Tool[]{BOOTS})
            .conflicting(Meador.class, Speed.class)
            .description("Slows the player down but makes them stronger and more resistant to knockback")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.NONE);
    }

    @Override
    public boolean onBeingHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if (!(evt.getEntity() instanceof Player) || //check if victim is a player
            !(evt.getDamager() instanceof LivingEntity) || //check if the damager is alive
            //check if the victim (player) can damage the attacker
            !ADAPTER.attackEntity((LivingEntity) evt.getDamager(), (Player) evt.getEntity(), 0)) {
            return true;
        }
        Player player = (Player) evt.getEntity();
        if (evt.getDamage() < player.getHealth()) {
            //FIXME this looks like bad practice - plugins may not have a say in this and
            //this would prevent the MONITOR priority of being used.
            //This should be changed into something better sometime in the future
            evt.setCancelled(true);
            player.damage(evt.getDamage());
            player.setVelocity(player.getLocation().subtract(evt.getDamager().getLocation()).toVector()
                                         .multiply((float) (1 / (level * power + 1.5))));
            ItemStack[] s = player.getInventory().getArmorContents();
            for (int i = 0; i < 4; i++) {
                if (s[i] != null) {
                    Utilities.addUnbreaking(player, s[i], 1);
                    if (Utilities.getDamage(s[i]) > s[i].getType().getMaxDurability()) {
                        s[i] = null;
                    }
                }
            }
            player.getInventory().setArmorContents(s);
        }
        return true;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand
    ) {
        player.setWalkSpeed((float) (.164f - level * power * .014f));
        Utilities.addPotion(player, INCREASE_DAMAGE, 610, (int) Math.round(power * level));
        player.setMetadata("ze.speed", new FixedMetadataValue(Storage.enchantments_plus, System.currentTimeMillis()));
        return true;
    }
}
