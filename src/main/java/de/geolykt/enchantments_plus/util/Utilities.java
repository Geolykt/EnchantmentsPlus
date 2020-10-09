package de.geolykt.enchantments_plus.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.geolykt.enchantments_plus.EnchantPlayer;
import de.geolykt.enchantments_plus.Storage;
import de.geolykt.enchantments_plus.enums.PermissionTypes;

import java.util.*;

import static org.bukkit.GameMode.CREATIVE;
import static org.bukkit.Material.AIR;
import static org.bukkit.inventory.EquipmentSlot.HAND;

public class Utilities {

    // Returns true for main hand slots, false otherwise
    public static boolean isMainHand(EquipmentSlot preferred) {
        return preferred == HAND;
    }

    // Returns an ArrayList of ItemStacks of the player's held item and armor
    public static List<ItemStack> getArmorAndMainHandItems(Player player, boolean mainHand) {
        List<ItemStack> stk = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
        stk.add(mainHand ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand());
        stk.removeIf((ItemStack is) -> is == null || is.getType() == Material.AIR);
        return stk;
    }

    // Removes the given ItemStack's durability by the given 'damage' and then sets
    // the item direction the given
    // players hand.
    // This also takes into account the unbreaking enchantment
    public static void damageTool(Player player, int damage, boolean handUsed) {
        if (!player.getGameMode().equals(CREATIVE)) {
            ItemStack hand = handUsed ? player.getInventory().getItemInMainHand()
                    : player.getInventory().getItemInOffHand();
            for (int i = 0; i < damage; i++) {
                if (Storage.rnd.nextInt(100) <= (100
                        / (hand.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY) + 1))) {
                    setDamage(hand, getDamage(hand) + 1);
                }
            }
            if (handUsed) {
                player.getInventory().setItemInMainHand(
                        getDamage(hand) > hand.getType().getMaxDurability() ? new ItemStack(AIR) : hand);
            } else {
                player.getInventory().setItemInOffHand(
                        getDamage(hand) > hand.getType().getMaxDurability() ? new ItemStack(AIR) : hand);
            }
        }
    }

    // Displays a particle with the given data
    public static void display(Location loc, Particle particle, int amount, double speed, double xO, double yO,
            double zO) {
        loc.getWorld().spawnParticle(particle, loc.getX(), loc.getY(), loc.getZ(), amount, (float) xO, (float) yO,
                (float) zO, (float) speed);
    }

    // Removes the given ItemStack's durability by the given 'damage'
    // This also takes into account the unbreaking enchantment
    public static void addUnbreaking(Player player, ItemStack is, int damage) {
        if (!player.getGameMode().equals(CREATIVE)) {
            for (int i = 0; i < damage; i++) {
                if (Storage.rnd.nextInt(
                        100) <= (100 / (is.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY) + 1))) {
                    setDamage(is, getDamage(is) + 1);
                }
            }
        }
    }

    public static void setDamage(ItemStack is, int damage) {
        if (is.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
            org.bukkit.inventory.meta.Damageable dm = ((Damageable) is.getItemMeta());
            dm.setDamage(damage);
            is.setItemMeta((ItemMeta) dm);
        }
    }

    public static int getDamage(ItemStack is) {
        if (is.getItemMeta() instanceof Damageable) {
            return ((Damageable)is.getItemMeta()).getDamage();
        }
        return 0;
    }

    // Returns the item stack direction the player's main or off hand, determined
    // by 'handUsed'
    public static ItemStack usedStack(Player player, boolean handUsed) {
        return handUsed ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
    }

    // Sets the hand the player to the given item stack, determined by 'handUsed'
    public static void setHand(Player player, ItemStack stk, boolean handUsed) {
        if (handUsed) {
            player.getInventory().setItemInMainHand(stk);
        } else {
            player.getInventory().setItemInOffHand(stk);
        }
    }

    // Removes an item stack of the given description from the players inventory
    public static boolean removeItem(Player player, Material mat) {
        return removeItem(player, mat, 1);
    }

    // Removes an item stack of the given description from the players inventory
    public static boolean removeItem(Player player, ItemStack is) {
        return removeItem(player, is.getType(), is.getAmount());
    }

    // Removes a certain number of an item stack of the given description from the
    // players inventory and returns true
    // if the item stack was direction their inventory
    public static boolean removeItem(Player player, Material mat, int amount) {
        if (player.getGameMode().equals(CREATIVE)) {
            return true;
        }
        Inventory inv = player.getInventory();

        if (!hasItem(player, mat, amount)) {
            return false;
        }

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null && inv.getItem(i).getType() == mat) {
                if (inv.getItem(i).getAmount() > amount) {
                    int res = inv.getItem(i).getAmount() - amount;
                    ItemStack rest = inv.getItem(i);
                    rest.setAmount(res);
                    inv.setItem(i, rest);
                    return true;
                } else {
                    amount -= inv.getItem(i).getAmount();
                    inv.setItem(i, null);
                }
            }
        }
        return true;
    }

    // Removes a certain number of an item stack of the given description from the
    // players inventory and returns true
    // if the item stack was direction their inventory
    public static boolean hasItem(Player player, Material mat, int amount) {
        if (player.getGameMode().equals(CREATIVE)) {
            return true;
        }
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null && inv.getItem(i).getType() == mat) {
                if (inv.getItem(i).getAmount() >= amount) {
                    amount = 0;
                } else {
                    amount -= inv.getItem(i).getAmount();
                }
            }
        }

        return amount == 0;
    }

    // Returns a level for the enchant event given the XP level and the enchantments
    // max level
    public static int getEnchantLevel(int maxlevel, int levels) {
        if (maxlevel == 1) {
            return 1;
        }
        int sectionsize = 32 / (maxlevel - 1);
        int position = levels / sectionsize;
        int mod = levels - position * sectionsize;
        if (Storage.rnd.nextInt(2 * sectionsize) >= mod) {
            return position + 1;
        } else {
            return position + 2;
        }
    }

    // Returns the English number representation of the given Roman number string
    public static int getNumber(String numeral) {
        switch (numeral.toUpperCase()) {
            case "-":
                return 0;
            case "I":
                return 1;
            case "II":
                return 2;
            case "III":
                return 3;
            case "IV":
                return 4;
            case "V":
                return 5;
            case "VI":
                return 6;
            case "VII":
                return 7;
            case "VIII":
                return 8;
            case "IX":
                return 9;
            case "X":
                return 10;
            default:
                return 1;
        }
    }

    // Returns the roman number string representation of the given english number
    public static String getRomanString(int number) {
        switch (number) {
            case 0:
                return "-";
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return "I";
        }
    }

    // Returns the roman number string representation of the given english number,
    // capped at the int 'limit'
    public static String getRomanString(int number, int limit) {
        if (number > limit) {
            return getRomanString(limit);
        } else {
            return getRomanString(number);
        }
    }

    // Returns the exact center of a block of a given location
    public static Location getCenter(Location loc) {
        return getCenter(loc, false);
    }

    // Returns the exact center of a block of a given location
    public static Location getCenter(Location loc, boolean centerVertical) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        if (x >= 0) {
            x += .5;
        } else {
            x += .5;
        }
        if (centerVertical) {
            y = (int) y + .5;
        }
        if (z >= 0) {
            z += .5;
        } else {
            z += .5;
        }
        Location lo = loc.clone();
        lo.setX(x);
        lo.setY(y);
        lo.setZ(z);
        return lo;
    }

    // Returns the exact center of a block of a given block
    public static Location getCenter(Block blk) {
        return getCenter(blk.getLocation());
    }

    // Returns the exact center of a block of a given block
    public static Location getCenter(Block blk, boolean centerVertical) {
        return getCenter(blk.getLocation(), centerVertical);
    }

    // Returns the nearby entities at any loction within the given range
    // Returns a direction integer, 0-8, for the given player's pitch and yaw
    public static BlockFace getDirection(Player player) {
        float yaw = player.getLocation().getYaw();
        BlockFace direction = BlockFace.SELF;
        if (yaw < 0) {
            yaw += 360;
        }
        yaw %= 360;
        double i = (double) ((yaw + 8) / 18);
        if (i >= 19 || i < 1) {
            direction = BlockFace.SOUTH;
        } else if (i < 3) {
            direction = BlockFace.SOUTH_WEST;
        } else if (i < 6) {
            direction = BlockFace.WEST;
        } else if (i < 8) {
            direction = BlockFace.NORTH_WEST;
        } else if (i < 11) {

            direction = BlockFace.NORTH;
        } else if (i < 13) {
            direction = BlockFace.NORTH_EAST;
        } else if (i < 16) {
            direction = BlockFace.EAST;
        } else if (i < 18) {
            direction = BlockFace.SOUTH_EAST;
        }
        return direction;
    }

    // Returns a more simple direction integer, 0-6, for the given player's pitch
    // and yaw
    public static BlockFace getCardinalDirection(float yaw, float pitch) {
        BlockFace direction;
        if (yaw < 0) {
            yaw += 360;
        }
        yaw %= 360;
        double i = (double) ((yaw + 8) / 18);
        if (i >= 18 || i < 3) {
            direction = BlockFace.SOUTH;
        } else if (i < 8) {
            direction = BlockFace.WEST;
        } else if (i < 13) {
            direction = BlockFace.NORTH;
        } else {
            direction = BlockFace.EAST;
        }
        if (pitch < -50) {
            direction = BlockFace.UP;
        } else if (pitch > 50) {
            direction = BlockFace.DOWN;
        }
        return direction;
    }

    // Returns true if a player can use a certain enchantment at a certain time
    // (permissions and cooldowns),
    // otherwise false
    public static boolean canUse(Player player, int enchantmentID) {
        return PermissionTypes.USE.hasPermission(player) &&
                EnchantPlayer.matchPlayer(player).getCooldown(enchantmentID) != 0 &&
                EnchantPlayer.matchPlayer(player).isDisabled(enchantmentID);
    }

    // Adds a potion effect of given length and intensity to the given entity.
    public static void addPotion(LivingEntity ent, PotionEffectType type, int length, int intensity) {
        for (PotionEffect eff : ent.getActivePotionEffects()) {
            if (eff.getType().equals(type)) {
                if (eff.getAmplifier() > intensity) {
                    return;
                } else if (eff.getDuration() > length) {
                    return;
                } else {
                    ent.removePotionEffect(type);
                }
            }
        }
        ent.addPotionEffect(new PotionEffect(type, length, intensity));
    }

    public static void selfRemovingArea(Material fill, Material check, int radius, Block center, Player player,
            Map<Location, Long> placed) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                Block possiblePlatformBlock = center.getRelative(x, -1, z);
                Location possiblePlatformLoc = possiblePlatformBlock.getLocation();
                if (possiblePlatformLoc.distanceSquared(center.getLocation()) < radius * radius - 2) {
                    if (placed.containsKey(possiblePlatformLoc)) {
                        placed.put(possiblePlatformLoc, System.nanoTime());
                    } else if (possiblePlatformBlock.getType() == check
                        && Storage.COMPATIBILITY_ADAPTER.airs().contains( possiblePlatformBlock.getRelative(0, 1, 0).getType())) {
                        if (possiblePlatformBlock.getBlockData() instanceof Levelled) {
                            if (((Levelled) possiblePlatformBlock.getBlockData()).getLevel() != 0) {
                                continue;
                            }
                        }
                        if (Storage.COMPATIBILITY_ADAPTER.formBlock(possiblePlatformBlock, fill, player)) {
                            placed.put(possiblePlatformLoc, System.nanoTime());
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns a list of blocks found using the BFS algorithm given the passed
     * search parameters
     * 
     * @param startBlock        The starting position of the BFS algorithm
     * @param maxBlocks         The max number of blocks to found (will return empty
     *                          list if strict is true)
     * @param strictMax         true -> return nothing if maxBlocks number is
     *                          exceeded; false -> return current find if maxBlock
     *                          number is exceeded
     * @param maxDistFromOrigin The max distance the center of a found block can be
     *                          from the center of startBlock to be a valid find
     * @param searchFaces       The block faces to search
     * @param validFind         valid materials for a found block
     * @param validSearch       valid materials for a searched block; Will return
     *                          empty list if not one of these
     * @param strictValidSearch true -> return nothing if denylist block is found;
     *                          false -> return current find if denylist block is
     *                          found
     * @param flipValidSearch   true -> validSearch is a allowlist; false ->
     *                          validSearch is a denylist
     */
    public static List<Block> BFS(Block startBlock, int maxBlocks, boolean strictMax, float maxDistFromOrigin,
            int[][] searchFaces, Set<Material> validFind, Set<Material> validSearch, boolean strictValidSearch,
            boolean flipValidSearch) {

        // Ensure the search list is in the allowlist
        if (!flipValidSearch) {
            HashSet<Material> validSearchNew = new HashSet<Material>();
            validSearchNew.addAll(validSearch);
            validSearchNew.addAll(validFind);
            validSearch = validSearchNew;
        }

        // BFS through the trunk, cancel if forbidden blocks are adjacent or search body
        // becomes too large

        // Searched blocks
        Set<Block> searchedBlocks = new LinkedHashSet<>();

        // Searched blocks that match the allowlist
        List<Block> foundBlocks = new ArrayList<>();

        // Blocks that still need to be searched
        List<Block> toSearch = new ArrayList<>();

        // Add the origin block
        searchedBlocks.add(startBlock);
        toSearch.add(startBlock);

        // Keep searching as long as there's more blocks to search
        while (!toSearch.isEmpty()) {
            // Get the next block to search
            Block searchBlock = toSearch.remove(0);

            // If block is in the search list, add adjacent blocks to search perimeter
            if (validFind.contains(searchBlock.getType())) {
                foundBlocks.add(searchBlock);

                for (int[] blockFace : searchFaces) {
                    // Add the adjacent block
                    Block nextBlock = searchBlock.getRelative(blockFace[0], blockFace[1], blockFace[2]);

                    // See if its been searched before
                    if (!searchedBlocks.contains(nextBlock)) {

                        // Determine if the block is in the allowlist and flip the condition if
                        // flipValidSearch
                        boolean check = validSearch.contains(nextBlock.getType());
                        if (flipValidSearch) {
                            check = !check;
                        }

                        // Add to search body if it meets the condition, else return
                        if (check) {

                            if (nextBlock.getLocation().distance(startBlock.getLocation()) > maxDistFromOrigin) {
                                continue;
                            }

                            toSearch.add(nextBlock);
                            searchedBlocks.add(nextBlock);
                        } else {
                            // Adjacent to a forbidden block. Nothing more to do
                            if (strictValidSearch) {
                                return new ArrayList<>();
                            } else {
                                return foundBlocks;
                            }
                        }
                    }
                }
            }

            if (foundBlocks.size() > maxBlocks) {
                // Allowed size exceeded
                if (strictMax) {
                    return new ArrayList<>();
                } else {
                    return foundBlocks;
                }
            }
        }

        return foundBlocks;
    }
}
