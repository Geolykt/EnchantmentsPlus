package de.geolykt.enchantments_plus.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.CustomEnchantment;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.Hand;
import de.geolykt.enchantments_plus.enums.Tool;
import de.geolykt.enchantments_plus.util.Utilities;

import java.util.Map;

import static de.geolykt.enchantments_plus.enums.Tool.LEGGINGS;
import static org.bukkit.Material.*;

public class GreenThumb extends CustomEnchantment {

    public static final int ID = 24;

    @Override
    public Builder<GreenThumb> defaults() {
        return new Builder<>(GreenThumb::new, ID)
            .maxLevel(3)
            .loreName("Green Thumb")
            .probability(0)
            .enchantable(new Tool[]{LEGGINGS})
            .conflicting()
            .description("Grows the foliage around the player")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.NONE)
            .base(BaseEnchantments.GREEN_THUMB);
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        Location loc = player.getLocation().clone();
        Block centerBlock = loc.getBlock();
        int radius = (int) Math.round(power * level + 2);
        for (int x = -(radius); x <= radius; x++) {
            for (int y = -(radius) - 1; y <= radius - 1; y++) {
                for (int z = -(radius); z <= radius; z++) {
                    Block relativeBlock = centerBlock.getRelative(x, y, z);
                    if (relativeBlock.getLocation().distance(loc) < radius) {
                        if (level != 10 && Storage.rnd.nextInt((int) (300 / (power * level / 2))) != 0) {
                            continue;
                        }
                        boolean applied = false;
                        switch (relativeBlock.getType()) {
                            case DIRT:
                                if (Storage.COMPATIBILITY_ADAPTER.airs().contains(relativeBlock.getRelative(0, 1, 0).getType())) {
                                    Material mat;
                                    switch (centerBlock.getBiome()) {
                                        case MUSHROOM_FIELD_SHORE:
                                        case MUSHROOM_FIELDS:
                                            mat = MYCELIUM;
                                            break;
                                        case GIANT_SPRUCE_TAIGA:
                                        case GIANT_TREE_TAIGA:
                                        case GIANT_SPRUCE_TAIGA_HILLS:
                                        case GIANT_TREE_TAIGA_HILLS:
                                            mat = PODZOL;
                                            break;
                                        default:
                                            mat = GRASS_BLOCK;
                                    }
                                    applied = ADAPTER.placeBlock(relativeBlock, player, mat, null);
                                }
                                break;
                            default:
                                applied = ADAPTER.grow(centerBlock.getRelative(x, y, z), player);
                                break;
                        }
                        if (applied) { // Display particles and damage armor
                            Utilities.display(Utilities.getCenter(centerBlock.getRelative(x, y + 1, z)),
                                Particle.VILLAGER_HAPPY, 20, 1f, .3f, .3f, .3f);
                            int chc = Storage.rnd.nextInt(50);
                            if (chc > 42 && level != 10) {
                                ItemStack[] s = player.getInventory().getArmorContents();
                                for (int i = 0; i < 4; i++) {
                                    if (s[i] != null) {
                                        Map<CustomEnchantment, Integer> map =
                                            CustomEnchantment.getEnchants(s[i], player.getWorld());
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
                        }
                    }
                }
            }
        }

        return true;
    }
}
