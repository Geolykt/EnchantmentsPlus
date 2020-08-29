/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.geolykt.enchantments_plus.compatibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import static org.bukkit.Material.*;
import static org.bukkit.Material.TROPICAL_FISH;
import static org.bukkit.entity.EntityType.*;
import static org.bukkit.entity.EntityType.PUFFERFISH;
import org.bukkit.event.block.Action;
import static org.bukkit.potion.PotionEffectType.*;
import static org.bukkit.potion.PotionEffectType.DOLPHINS_GRACE;
import static org.bukkit.potion.PotionEffectType.WATER_BREATHING;

import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Sets;

public class CompatibilityAdapter {

    private static final CompatibilityAdapter INSTANCE = new CompatibilityAdapter();
    private static final Random RND = new Random();
    
    public List<Material> grownCrops() {
        return Arrays.asList(WHEAT, POTATOES, CARROTS, COCOA, BEETROOTS, NETHER_WART, SWEET_BERRY_BUSH);
    }
    
    public List<Material> cropYields() {
        return Arrays.asList(WHEAT, POTATOES, CARROTS, COCOA, BEETROOTS, NETHER_WART, SWEET_BERRIES);
    }

    public List<Material> grownMelon() {
        return Arrays.asList(MELON, PUMPKIN);
    }

    public List<Material> melonYields() {
        return Arrays.asList(MELON_SLICE, PUMPKIN);
    }

    public List<Material> mushrooms() {
        return Arrays.asList(RED_MUSHROOM, BROWN_MUSHROOM);
    }
    
    public List<Material> mushroomBlocks() {
        return Arrays.asList(MUSHROOM_STEM, BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK);
    }

    public Set<Material> airs() {
        return Sets.immutableEnumSet(AIR, CAVE_AIR, VOID_AIR);
    }

    protected static List<Material> list_ores;
    
    public List<Material> ores() {
        if (list_ores == null) {
            list_ores = new ArrayList<Material>();
            list_ores.addAll(Tag.GOLD_ORES.getValues());
            list_ores.addAll(Arrays.asList(COAL_ORE, REDSTONE_ORE, DIAMOND_ORE,
                    IRON_ORE, LAPIS_ORE, GLOWSTONE, NETHER_QUARTZ_ORE, EMERALD_ORE, ANCIENT_DEBRIS));
            return list_ores;
        } else {
            return list_ores;
        }
    }

    public List<Material> dirts() {
        return Arrays.asList(DIRT, COARSE_DIRT, MYCELIUM, PODZOL, GRASS_BLOCK, GRASS_PATH);
    }

    public Set<Biome> dryBiomes() {
        return Sets.immutableEnumSet(Biome.DESERT, Biome.FROZEN_OCEAN,
                Biome.FROZEN_RIVER, Biome.SNOWY_TUNDRA, Biome.SNOWY_MOUNTAINS, Biome.DESERT_HILLS, Biome.SNOWY_BEACH,
                Biome.SNOWY_TAIGA, Biome.SNOWY_TAIGA_HILLS, Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.BADLANDS,
                Biome.WOODED_BADLANDS_PLATEAU, Biome.BADLANDS_PLATEAU, Biome.DESERT_LAKES, Biome.ICE_SPIKES,
                Biome.SNOWY_TAIGA_MOUNTAINS, Biome.SHATTERED_SAVANNA, Biome.SHATTERED_SAVANNA_PLATEAU, Biome.ERODED_BADLANDS,
                Biome.MODIFIED_WOODED_BADLANDS_PLATEAU, Biome.MODIFIED_BADLANDS_PLATEAU);
    }
    
    public List<Material> stones() {
        return Arrays.asList(STONE, GRANITE, ANDESITE, DIORITE, BASALT, BLACKSTONE);
    }

    public List<Material> cobblestones() {
        return Arrays.asList(COBBLESTONE, MOSSY_COBBLESTONE);
    }

    public List<Material> netherbricks() {
        return Arrays.asList(NETHER_BRICKS, RED_NETHER_BRICKS);
    }
    
    public List<Material> netherracks() {
        return Arrays.asList(NETHERRACK, WARPED_NYLIUM, CRIMSON_NYLIUM);
    }


    public List<Material> unbreakableBlocks() {
        return Arrays.asList(BARRIER, BEDROCK, AIR, CAVE_AIR, VOID_AIR, COMMAND_BLOCK, REPEATING_COMMAND_BLOCK,
                BUBBLE_COLUMN, DRAGON_BREATH, DRAGON_EGG, END_CRYSTAL, END_GATEWAY, END_PORTAL, END_PORTAL_FRAME, LAVA,
                STRUCTURE_VOID, STRUCTURE_BLOCK, WATER, PISTON_HEAD, MOVING_PISTON);
    }

    public List<Material> laserBlackListBlocks() {
        return Arrays.asList(OBSIDIAN, CRYING_OBSIDIAN);
    }
    
    public List<Material> terracottas() {
        return Arrays.asList(Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA,
                Material.LIGHT_BLUE_TERRACOTTA, Material.BLUE_TERRACOTTA, Material.BLACK_TERRACOTTA, 
                Material.RED_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.CYAN_TERRACOTTA,
                Material.GRAY_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA, Material.YELLOW_TERRACOTTA, LIME_TERRACOTTA,
                Material.PURPLE_TERRACOTTA, Material.PINK_TERRACOTTA);
    }
    
    public List<Material> glazedTerracottas() {
        return Arrays.asList(Material.WHITE_GLAZED_TERRACOTTA, Material.ORANGE_GLAZED_TERRACOTTA,
                Material.MAGENTA_GLAZED_TERRACOTTA, Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
                Material.BLUE_GLAZED_TERRACOTTA, Material.BLACK_GLAZED_TERRACOTTA, Material.RED_GLAZED_TERRACOTTA,
                Material.GREEN_GLAZED_TERRACOTTA, Material.CYAN_GLAZED_TERRACOTTA, Material.GRAY_GLAZED_TERRACOTTA,
                Material.LIGHT_GRAY_GLAZED_TERRACOTTA, Material.YELLOW_GLAZED_TERRACOTTA, LIME_GLAZED_TERRACOTTA,
                Material.PURPLE_GLAZED_TERRACOTTA, Material.PINK_GLAZED_TERRACOTTA);
    }
    
    public List<Material> concretes() {
        return Arrays.asList(Material.WHITE_CONCRETE, Material.ORANGE_CONCRETE, Material.MAGENTA_CONCRETE,
                Material.LIGHT_BLUE_CONCRETE, Material.BLUE_CONCRETE, Material.BLACK_CONCRETE, 
                Material.RED_CONCRETE, Material.GREEN_CONCRETE, Material.CYAN_CONCRETE,
                Material.GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.YELLOW_CONCRETE, LIME_CONCRETE,
                Material.PURPLE_CONCRETE, Material.PINK_CONCRETE);
    }
    
    public List<Material> concretePowders() {
        return Arrays.asList(Material.WHITE_CONCRETE_POWDER, Material.ORANGE_CONCRETE_POWDER, Material.MAGENTA_CONCRETE_POWDER,
                Material.LIGHT_BLUE_CONCRETE_POWDER, Material.BLUE_CONCRETE_POWDER, Material.BLACK_CONCRETE_POWDER, 
                Material.RED_CONCRETE_POWDER, Material.GREEN_CONCRETE_POWDER, Material.CYAN_CONCRETE_POWDER,
                Material.GRAY_CONCRETE_POWDER, Material.LIGHT_GRAY_CONCRETE_POWDER, Material.YELLOW_CONCRETE_POWDER, LIME_CONCRETE_POWDER,
                Material.PURPLE_CONCRETE_POWDER, Material.PINK_CONCRETE_POWDER);
    }
    
    public List<Material> stainedGlasses() {
        return Arrays.asList(Material.WHITE_STAINED_GLASS, Material.ORANGE_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS,
                        Material.LIGHT_BLUE_STAINED_GLASS, Material.BLUE_STAINED_GLASS, Material.BLACK_STAINED_GLASS,
                        Material.RED_STAINED_GLASS, Material.GREEN_STAINED_GLASS, Material.CYAN_STAINED_GLASS,
                        Material.GRAY_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS, Material.YELLOW_STAINED_GLASS, LIME_STAINED_GLASS,
                        Material.PURPLE_STAINED_GLASS, Material.PINK_STAINED_GLASS);
    }
    
    protected static List<Material> terraformer_materials;

    public List<Material> terraformerMaterials() {
        if (terraformer_materials == null) {
            terraformer_materials = Arrays.asList(
                    BRICK, TNT, BOOKSHELF, ICE,
                    STONE_BRICKS, NETHER_BRICK, END_STONE, QUARTZ_BLOCK, WARPED_HYPHAE,
                    PRISMARINE, PACKED_ICE, RED_SANDSTONE, NETHERRACK, WARPED_NYLIUM);
            terraformer_materials.addAll(terracottas());
            terraformer_materials.addAll(glazedTerracottas());
            terraformer_materials.addAll(ores());
            terraformer_materials.addAll(Tag.LOGS.getValues());
            terraformer_materials.addAll(Tag.ENDERMAN_HOLDABLE.getValues());
            terraformer_materials.addAll(Tag.WOOL.getValues());
            terraformer_materials.addAll(concretes());
            terraformer_materials.addAll(concretePowders());
            terraformer_materials.addAll(cobblestones());
            terraformer_materials.addAll(stones());
        }
        return terraformer_materials;
    }

    public List<EntityType> transformationEntityTypesFrom() {
        return Arrays.asList(
                HUSK, WITCH, EntityType.COD, PHANTOM, HORSE, SKELETON, EntityType.CHICKEN, SQUID, OCELOT, POLAR_BEAR, COW, PIG,
                SPIDER, SLIME, GUARDIAN, ENDERMITE, SKELETON_HORSE, EntityType.RABBIT, SHULKER, SNOWMAN, DROWNED, VINDICATOR,
                EntityType.SALMON, BLAZE, DONKEY, STRAY, PARROT, DOLPHIN, WOLF, SHEEP, MUSHROOM_COW, ZOMBIFIED_PIGLIN, CAVE_SPIDER,
                MAGMA_CUBE, ELDER_GUARDIAN, SILVERFISH, ZOMBIE_HORSE, EntityType.RABBIT, ENDERMAN, IRON_GOLEM, ZOMBIE, EVOKER,
                PUFFERFISH, VEX, MULE, WITHER_SKELETON, BAT, TURTLE, ZOMBIE_VILLAGER, VILLAGER, EntityType.TROPICAL_FISH, GHAST,
                LLAMA, CREEPER, EntityType.HOGLIN);
    }

    public List<EntityType> transformationEntityTypesTo() {
        return Arrays.asList(
                DROWNED, VINDICATOR, EntityType.SALMON, BLAZE, DONKEY, STRAY, PARROT, DOLPHIN, WOLF, SHEEP, MUSHROOM_COW,
                ZOMBIFIED_PIGLIN, CAVE_SPIDER, MAGMA_CUBE, ELDER_GUARDIAN, SILVERFISH, ZOMBIE_HORSE, EntityType.RABBIT, ENDERMAN,
                IRON_GOLEM, ZOMBIE, EVOKER, PUFFERFISH, VEX, MULE, WITHER_SKELETON, BAT, TURTLE, OCELOT, POLAR_BEAR, COW, PIG,
                SPIDER, SLIME, GUARDIAN, ENDERMITE, SKELETON_HORSE, EntityType.RABBIT, SHULKER, SNOWMAN, ZOMBIE_VILLAGER,
                VILLAGER, EntityType.TROPICAL_FISH, GHAST, LLAMA, SKELETON, EntityType.CHICKEN, SQUID, HUSK, WITCH,
                EntityType.COD, PHANTOM, HORSE, CREEPER, EntityType.ZOGLIN);
    }

    public LivingEntity transformationCycle(LivingEntity ent, Random rnd) {
        int newTypeID = transformationEntityTypesFrom().indexOf(ent.getType());
        if (newTypeID == -1) {
            return null;
        }
        EntityType newType = transformationEntityTypesTo().get(newTypeID);
        LivingEntity newEnt = (LivingEntity) ent.getWorld().spawnEntity(ent.getLocation(), newType);

        switch (newType) {
        case HORSE:
            ((Horse) newEnt).setColor(Horse.Color.values()[rnd.nextInt(Horse.Color.values().length)]);
            ((Horse) newEnt).setStyle(Horse.Style.values()[rnd.nextInt(Horse.Style.values().length)]);
            break;
        case RABBIT:
            if (((Rabbit) ent).getRabbitType().equals(Rabbit.Type.THE_KILLER_BUNNY)) {
                ((Rabbit) newEnt).setRabbitType(Rabbit.Type.values()[rnd.nextInt(Rabbit.Type.values().length - 1)]);
            } else {
                ((Rabbit) newEnt).setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
            }
            break;
        case VILLAGER:
            Villager.Profession career = Villager.Profession.values()[rnd.nextInt(Villager.Profession.values().length)];
            ((Villager) newEnt).setProfession(career);
            //((Villager) newEnt).setCareer(career);
            break;
        case LLAMA:
            ((Llama) newEnt).setColor(Llama.Color.values()[rnd.nextInt(Llama.Color.values().length)]);
            break;
        case TROPICAL_FISH:
            ((TropicalFish) newEnt).setBodyColor(DyeColor.values()[rnd.nextInt(DyeColor.values().length)]);
            ((TropicalFish) newEnt).setPatternColor(DyeColor.values()[rnd.nextInt(DyeColor.values().length)]);
            ((TropicalFish) newEnt).setPattern(TropicalFish.Pattern.values()[rnd.nextInt(TropicalFish.Pattern.values().length)]);
            break;
        case PARROT:
            ((Parrot) newEnt).setVariant(Parrot.Variant.values()[rnd.nextInt(Parrot.Variant.values().length)]);
            break;
        case SHEEP:
            ((Sheep) newEnt).setColor(DyeColor.values()[rnd.nextInt(DyeColor.values().length)]);
            break;
        case CREEPER:
            ((Creeper) newEnt).setPowered(!((Creeper) ent).isPowered());
            break;
        case MUSHROOM_COW:
            ((MushroomCow) newEnt).setVariant(MushroomCow.Variant.values()[rnd.nextInt(MushroomCow.Variant.values().length)]);
            break;
        default:
            break;
        }
        newEnt.setCustomName(ent.getCustomName());
        newEnt.setCustomNameVisible(ent.isCustomNameVisible());
        return ent;
    }
    
    protected static Set<Material> shredPickSet;

    public Set<Material> shredPicks() {
        if  (shredPickSet == null) {
            List<Material> shredPick = Arrays.asList(STONE, GRANITE,
                    ANDESITE, DIORITE, GLOWSTONE, SANDSTONE, RED_SANDSTONE, ICE, PACKED_ICE, BLUE_ICE, BASALT);
            shredPick.addAll(ores());
            shredPick.addAll(terracottas());
            shredPick.addAll(netherracks());
            shredPickSet = Sets.immutableEnumSet(shredPick);
        }
        return shredPickSet;
    }

    public Set<Material> shredShovels() {
        return Tag.ENDERMAN_HOLDABLE.getValues();
    }

    public List<Material> persephoneCrops() {
        return Arrays.asList(WHEAT, POTATO, CARROT, BEETROOT, NETHER_WART, SOUL_SAND, FARMLAND);
    }

    public List<PotionEffectType> potionPotions() {
        return Arrays.asList(ABSORPTION,
                DAMAGE_RESISTANCE, FIRE_RESISTANCE, SPEED, JUMP, INVISIBILITY, INCREASE_DAMAGE, HEALTH_BOOST, HEAL,
                REGENERATION, NIGHT_VISION, SATURATION, FAST_DIGGING, WATER_BREATHING, DOLPHINS_GRACE);
    }
    
    private static final int[] GLUTTONY_FOOD_LEVELS = {4, 5, 1, 6, 5, 3, 1, 6, 5, 6, 8, 5, 6, 2, 1, 2, 6, 8, 10, 8};

    public int[] gluttonyFoodLevels() {
        return GLUTTONY_FOOD_LEVELS;
    }
    
    private static final double[] GLUTTONY_SATURATIONS = {2.4, 6, 1.2, 7.2, 6, 3.6, 0.2, 7.2, 6, 9.6, 12.8, 6, 9.6, 0.4, 0.6,
            1.2, 7.2, 4.8, 12, 12.8};

    public double[] gluttonySaturations() {
        return GLUTTONY_SATURATIONS;
    }
    
    private final Material[] GLUTTONY_FOOD_ITEMS = new Material[]{
            APPLE, BAKED_POTATO, BEETROOT, BEETROOT_SOUP, BREAD, CARROT, TROPICAL_FISH, COOKED_CHICKEN, COOKED_COD,
            COOKED_MUTTON, COOKED_PORKCHOP, COOKED_RABBIT, COOKED_SALMON, COOKIE, DRIED_KELP, MELON_SLICE, MUSHROOM_STEW,
            PUMPKIN_PIE, RABBIT_STEW, COOKED_BEEF};

    public Material[] gluttonyFoodItems() {
        return GLUTTONY_FOOD_ITEMS;
    }
    
    public static CompatibilityAdapter getInstance() {
        return INSTANCE;
    }

    // Removes the given ItemStack's durability by the given 'damage' and then sets the item direction the given
    // players hand.
    //      This also takes into account the unbreaking enchantment
    public static void damageTool(Player player, int damage, boolean handUsed) {
        if (handUsed) {
            damageToolInSlot(player, damage, player.getInventory().getHeldItemSlot());
        } else {
            player.getInventory().setItemInOffHand(damageItem(player.getInventory().getItemInOffHand(), damage));
        }
    }

    public static void damageToolInSlot(Player player, int damage, int slotIndex) {
        ItemStack stack = damageItem(player.getInventory().getItem(slotIndex), damage);
        if (getDamage(stack) < 0) {
            player.getInventory().clear(slotIndex);
        } else {
            player.getInventory().setItem(slotIndex, stack);
        }
    }

    public static ItemStack damageItem(ItemStack stack, int damage) {
        if (!stack.getItemMeta().isUnbreakable()) {
            for (int i = 0; i < damage; i++) {
                if (RND.nextInt(100) <= (100 / (stack.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY) + 1))) {
                    setDamage(stack, getDamage(stack) + 1);
                }
            }
        }
        return stack;
    }

    // Displays a particle with the given data
    public static void display(Location loc, Particle particle, int amount, double speed, double xO, double yO,
            double zO) {
        loc.getWorld().spawnParticle(particle, loc.getX(), loc.getY(), loc.getZ(), amount, (float) xO, (float) yO,
                (float) zO, (float) speed);
    }

    // Removes the given ItemStack's durability by the given 'damage'
    //      This also takes into account the unbreaking enchantment
    public static void addUnbreaking(Player player, ItemStack is, int damage) {
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            for (int i = 0; i < damage; i++) {
                if (RND.nextInt(100) <= (100 / (is.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY) + 1))) {
                    setDamage(is, getDamage(is) + 1);
                }
            }
        }
    }

    public static void setDamage(ItemStack is, int damage) {
        if (is.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
            org.bukkit.inventory.meta.Damageable dm = ((org.bukkit.inventory.meta.Damageable) is.getItemMeta());
            dm.setDamage(damage);
            is.setItemMeta((ItemMeta) dm);
        }
    }

    public static int getDamage(ItemStack is) {
        if (is.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
            org.bukkit.inventory.meta.Damageable dm = ((org.bukkit.inventory.meta.Damageable) is.getItemMeta());
            return dm.getDamage();
        }
        return 0;
    }

    protected CompatibilityAdapter() {
    }

    public void collectXP(Player player, int amount) {
        player.giveExp(amount);
    }

    public boolean breakBlockNMS(Block block, Player player) {
        BlockBreakEvent evt = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.breakNaturally(player.getInventory().getItemInMainHand());
            damageTool(player, 1, true);
            return true;
        }
        return false;
    }

    /**
     * Places a block on the given player's behalf. Fires a BlockPlaceEvent with
     * (nearly) appropriate parameters to probe the legitimacy (permissions etc)
     * of the action and to communicate to other plugins where the block is
     * coming from.
     *
     * @param blockPlaced the block to be changed
     * @param player the player whose identity to use
     * @param mat the material to set the block to, if allowed
     * @param data the block data to set for the block, if allowed
     *
     * @return true if the block placement has been successful
     */
    public boolean placeBlock(Block blockPlaced, Player player, Material mat, BlockData data) {
        Block blockAgainst = blockPlaced.getRelative((blockPlaced.getY() == 0) ? BlockFace.UP : BlockFace.DOWN);
        ItemStack itemHeld = new ItemStack(mat);
        BlockPlaceEvent placeEvent
        = new BlockPlaceEvent(blockPlaced, blockPlaced.getState(), blockAgainst, itemHeld, player, true,
                EquipmentSlot.HAND);

        Bukkit.getPluginManager().callEvent(placeEvent);
        if (!placeEvent.isCancelled()) {
            blockPlaced.setType(mat);
            if (data != null) {
                blockPlaced.setBlockData(data);
            }
            return true;
        }
        return false;
    }

    public boolean placeBlock(Block blockPlaced, Player player, ItemStack is) {
        return placeBlock(blockPlaced, player, is.getType(), (BlockData) is.getData());
    }


    /**
     * 
     * @return True if damaged, false otherwise
     */
    public boolean attackEntity(LivingEntity target, Player attacker, double damage) {
        return attackEntity(target, attacker, damage, true);
    }

    public boolean attackEntity(LivingEntity target, Player attacker, double damage, boolean performEquipmentDamage) {
        EntityDamageByEntityEvent damageEvent
        = new EntityDamageByEntityEvent(attacker, target, DamageCause.ENTITY_ATTACK, damage);
        Bukkit.getPluginManager().callEvent(damageEvent);
        if (damage == 0) {
            return !damageEvent.isCancelled();
        }
        if (!damageEvent.isCancelled()) {
            target.damage(damage, attacker);
            target.setLastDamageCause(damageEvent);
            if (performEquipmentDamage) {
                damageTool(attacker, 1, true);
            }
            return true;
        }
        return false;
    }

    public boolean shearEntityNMS(Entity target, Player player, boolean mainHand) {
        if ((target instanceof Sheep && !((Sheep) target).isSheared()) || target instanceof MushroomCow) {
            @SuppressWarnings("deprecation")
            PlayerShearEntityEvent evt = new PlayerShearEntityEvent(player, target);
            Bukkit.getPluginManager().callEvent(evt);
            if (!evt.isCancelled()) {
                if (target instanceof Sheep) {
                    target.getWorld().dropItemNaturally(target.getLocation(), new ItemStack(getWoolCol(((Sheep) target).getColor()), RND.nextInt(3) + 1));
                    ((Sheep) target).setSheared(true);

                    // TODO: Apply damage to tool
                } else if (target instanceof MushroomCow) {
                    //MushroomCow cow = (MushroomCow) target;
                    // TODO: DO
                }
                return true;
            }
        }
        return false;
    }

    public boolean haulOrBreakBlock(Block from, Block to, BlockFace face, Player player) {
        BlockState state = from.getState();
        if (state.getClass().getName().endsWith("CraftBlockState")) {
            return false;
        }
        BlockBreakEvent breakEvent = new BlockBreakEvent(from, player);
        Bukkit.getPluginManager().callEvent(breakEvent);
        if (breakEvent.isCancelled()) {
            return false;
        }
        ItemStack stack = new ItemStack(state.getType(), 1);
        from.setType(AIR);
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(to, to.getRelative(face.getOppositeFace()).getState(),
                to.getRelative(face.getOppositeFace()), stack, player, true,
                EquipmentSlot.HAND);
        Bukkit.getPluginManager().callEvent(placeEvent);
        if (placeEvent.isCancelled()) {
            from.getWorld().dropItem(from.getLocation(), stack);
            return true;
        }
        to.setType(state.getType());
        return true;
    }

    public boolean igniteEntity(Entity target, Player player, int duration) {
        EntityCombustByEntityEvent evt = new EntityCombustByEntityEvent(target, player, duration);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            target.setFireTicks(duration);
            return true;
        }
        return false;
    }

    public boolean damagePlayer(Player player, double damage, DamageCause cause) {
        EntityDamageEvent evt = new EntityDamageEvent(player, cause, damage);
        Bukkit.getPluginManager().callEvent(evt);
        if (damage == 0) {
            return !evt.isCancelled();
        }
        if (!evt.isCancelled()) {
            player.setLastDamageCause(evt);
            player.damage(damage);
            return true;
        }
        return false;
    }

    public boolean explodeCreeper(Creeper c, boolean damage) {
        float power;
        Location l = c.getLocation();
        if (c.isPowered()) {
            power = 6f;
        } else {
            power = 3.1f;
        }
        if (damage) {
            c.getWorld().createExplosion(l, power);
        } else {
            c.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), power, false, false);
        }
        c.remove();

        return true;
    }

    public boolean formBlock(Block block, Material mat, Player player) {
        BlockState bs = block.getState();
        bs.setType(mat);
        EntityBlockFormEvent evt = new EntityBlockFormEvent(player, block, bs);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.setType(mat);
            return true;
        }
        return false;
    }

    public boolean showShulker(Block blockToHighlight, int entityId, Player player) {
        // This cannot be done without NMS
        return false;
    }

    public boolean hideShulker(int entityId, Player player) {
        // This cannot be done without NMS
        return false;
    }

    public Entity spawnGuardian(Location loc, boolean elderGuardian) {
        return loc.getWorld().spawnEntity(loc, elderGuardian ? EntityType.ELDER_GUARDIAN : EntityType.GUARDIAN);
    }

    public boolean isZombie(Entity e) {
        return e.getType() == EntityType.ZOMBIE || e.getType() == EntityType.ZOMBIE_VILLAGER || e.getType() == EntityType.HUSK;
    }

    public boolean isBlockSafeToBreak(Block b) {
        Material mat = b.getType();
        return mat.isSolid() && !b.isLiquid() && !mat.isInteractable() && !unbreakableBlocks().contains(mat);
    }

    public boolean grow(Block cropBlock, Player player) {
        Material mat = cropBlock.getType();
        BlockData data = cropBlock.getBlockData();

        switch (mat) {
        case PUMPKIN_STEM:
        case MELON_STEM:
        case CARROTS:
        case WHEAT:
        case POTATOES:
        case COCOA:
        case NETHER_WART:
        case BEETROOTS:

            BlockData cropState = cropBlock.getBlockData();
            if (cropState instanceof Ageable) {
                Ageable ag = (Ageable) cropState;
                if (ag.getAge() >= ag.getMaximumAge()) {
                    return false;
                }
                ag.setAge(ag.getAge() + 1);
                data = ag;
            }
            break;
        case CACTUS:
        case SUGAR_CANE:
            int height = 1;
            if (cropBlock.getRelative(BlockFace.DOWN).getType() == mat) { // Only grow if argument is the base
                // block
                return false;
            }
            while ((cropBlock = cropBlock.getRelative(BlockFace.UP)).getType() == mat) {
                if (++height >= 3) { // Cancel if cactus/cane is fully grown
                    return false;
                }
            }
            if (!airs().contains(cropBlock.getType())) { // Only grow if argument is the base block
                return false;
            }

            break;
        default:
            return false;
        }

        if (player != null) {
            return placeBlock(cropBlock, player, mat, data);
        }

        BlockState bs = cropBlock.getState();
        bs.setType(mat);
        BlockGrowEvent evt = new BlockGrowEvent(cropBlock, bs);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            cropBlock.setType(mat);
            cropBlock.setBlockData(data);
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public boolean pickBerries(Block berryBlock, Player player) {
        BlockData data = berryBlock.getBlockData();
        Ageable a = (Ageable) data;
        if (a.getAge() > 1) { // Age of ripe Berries
            PlayerInteractEvent pie = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getInventory().getItemInMainHand(), berryBlock, player.getFacing());
            Bukkit.getPluginManager().callEvent(pie);
            if (!pie.isCancelled()) {
                int numDropped = (a.getAge() == 3 ? 2 : 1) + (RND.nextBoolean() ? 1 : 0); // Natural drop rate. Age 2 -> 1-2 berries, Age 3 -> 2-3 berries
                a.setAge(1); // Picked adult berry bush
                berryBlock.setBlockData(a);
                berryBlock.getWorld().dropItem(berryBlock.getLocation(),
                        new ItemStack(Material.SWEET_BERRIES, numDropped));
                return true;
            }
        }
        return false;
    }
    
    public Material getWoolCol (DyeColor col)  {
        switch (col) {
        case BLACK:
            return Material.BLACK_WOOL;
        case BLUE:
            return Material.BLUE_WOOL;
        case BROWN:
            return Material.BROWN_WOOL;
        case CYAN:
            return Material.CYAN_WOOL;
        case GRAY:
            return Material.GRAY_WOOL;
        case GREEN:
            return Material.GREEN_WOOL;
        case LIGHT_BLUE:
            return Material.LIGHT_BLUE_WOOL;
        case LIGHT_GRAY:
            return Material.LIGHT_GRAY_WOOL;
        case LIME:
            return Material.LIME_WOOL;
        case MAGENTA:
            return Material.MAGENTA_WOOL;
        case ORANGE:
            return Material.ORANGE_WOOL;
        case PINK:
            return Material.PINK_WOOL;
        case PURPLE:
            return Material.PURPLE_WOOL;
        case RED:
            return Material.RED_WOOL;
        case WHITE:
            return Material.WHITE_WOOL;
        case YELLOW:
            return Material.YELLOW_WOOL;
        default:
            return Material.WHITE_WOOL;
        }
    }
}
