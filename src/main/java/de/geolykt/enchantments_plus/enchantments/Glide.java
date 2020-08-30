package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.HashMap;
import java.util.Map;

import static de.geolykt.enchantments_plus.enums.Tool.LEGGINGS;
import static org.bukkit.Material.AIR;

public class Glide extends CustomEnchantment {

    // The players using glide and their most recent Y coordinate
    public static final Map<Player, Double> sneakGlide = new HashMap<>();
    public static final int ID = 20;

    @Override
    public Builder<Glide> defaults() {
        return new Builder<>(Glide::new, ID)
                .maxLevel(3)
                .loreName("Glide")
                .probability(0)
                .enchantable(new Tool[]{LEGGINGS})
                .conflicting()
                .description("Gently brings the player back to the ground when sneaking")
                .cooldown(0)
                .power(1.0)
                .handUse(Hand.NONE)
                .base(BaseEnchantments.GLIDE);
    }

    @Override
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        if (!sneakGlide.containsKey(player)) {
            sneakGlide.put(player, player.getLocation().getY());
        }
        if (!player.isSneaking() || sneakGlide.get(player) == player.getLocation().getY()) {
            return false;
        }
        boolean b = false;
        for (int i = -5; i < 0; i++) {
            if (player.getLocation().getBlock().getRelative(0, i, 0).getType() != AIR) {
                b = true;
            }
        }
        if (player.getVelocity().getY() > -0.5) {
            b = true;
        }
        if (!b) {
            double cosPitch = Math.cos(Math.toRadians(player.getLocation().getPitch()));
            double sinYaw = Math.sin(Math.toRadians(player.getLocation().getYaw()));
            double cosYaw = Math.cos(Math.toRadians(player.getLocation().getYaw()));
            Vector v = new Vector(-cosPitch * sinYaw, 0, -1 * (-cosPitch * cosYaw));
            v.multiply(level * power / 2);
            v.setY(-1);
            player.setVelocity(v);
            player.setFallDistance((float) (6 - level * power) - 4);
            Location l = player.getLocation().clone();
            l.setY(l.getY() - 3);
            Utilities.display(l, Particle.CLOUD, 1, .1f, 0, 0, 0);
        }
        if (Storage.rnd.nextInt(5 * level) == 5) { // Slowly damage all armor
            ItemStack[] s = player.getInventory().getArmorContents();
            for (int i = 0; i < 4; i++) {
                if (s[i] != null) {
                    Map<CustomEnchantment, Integer> map = CustomEnchantment.getEnchants(s[i], player.getWorld());
                    if (map.containsKey(this)) {
                        Utilities.addUnbreaking(player, s[i], 1);
                    }
                    if (Utilities.getDamage(s[i]) > s[i].getType().getMaxDurability()) {
                        s[i] = null;
                    }
                }
            }
            player.getInventory().setArmorContents(s);
        }
        sneakGlide.put(player, player.getLocation().getY());
        return true;
    }

}
