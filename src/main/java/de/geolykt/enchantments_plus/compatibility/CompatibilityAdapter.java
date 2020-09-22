package de.geolykt.enchantments_plus.compatibility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import static org.bukkit.entity.EntityType.*;

import org.bukkit.event.block.Action;
import static org.bukkit.potion.PotionEffectType.*;

import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.geolykt.enchantments_plus.Storage;

public class CompatibilityAdapter {
    
    public CompatibilityAdapter() {
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Enchantments_plus"), () -> {
            scanMethods();
        }, 0l);
    }

    private EnumSet<Material> grownCrops;
    private EnumSet<Material> melonCrops;
    private EnumSet<Material> airs;
    private EnumSet<Material> ores;
    private EnumSet<Material> unbreakable;
    private EnumSet<Material> laserDenylist;
    private EnumSet<Material> terraformerAllowlist;
    private EnumSet<Material> shredAllowlistPickaxes;
    private EnumSet<Material> shredAllowlistShovels;

    private EnumSet<Biome> dryBiomes;
    
    private EnumMap<Material, Material> spectralMaterialConversion;
    
    /**
     * Load the magic compatibility file
     * @param config The appropriate FileConfiguration
     */
    public void loadValues(FileConfiguration config) {
        grownCrops = EnumSet.noneOf(Material.class);
        for (String s : config.getStringList("grownCrops")) {
            grownCrops.add(Material.matchMaterial(s));
        }
        melonCrops = EnumSet.noneOf(Material.class);
        for (String s : config.getStringList("melonCrops")) {
            melonCrops.add(Material.matchMaterial(s));
        }
        airs = EnumSet.noneOf(Material.class);
        for (String s : config.getStringList("airs")) {
            airs.add(Material.matchMaterial(s));
        }
        ores = EnumSet.noneOf(Material.class);
        for (String s : config.getStringList("ores")) {
            ores.add(Material.matchMaterial(s));
        }
        unbreakable = EnumSet.noneOf(Material.class);
        for (String s : config.getStringList("unbreakable")) {
            unbreakable.add(Material.matchMaterial(s));
        }
        laserDenylist = EnumSet.noneOf(Material.class);
        for (String s : config.getStringList("laserDenylist")) {
            laserDenylist.add(Material.matchMaterial(s));
        }
        terraformerAllowlist = EnumSet.noneOf(Material.class);
        for (String s : config.getStringList("terraformerAllowlist")) {
            terraformerAllowlist.add(Material.matchMaterial(s));
        }
        for (String s : config.getStringList("terraformerAllowlistTags")) {
            try {
                Field f = Tag.class.getDeclaredField(s);
                @SuppressWarnings("unchecked")
                Tag<? extends Material> tag = (Tag<? extends Material>) f.get(null);
                terraformerAllowlist.addAll(tag.getValues());
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                Bukkit.getLogger().warning("looks like an issue occoured with the Enchantments+ plugin. "
                        + "This is likely the cause of an unsupported minecraft version.");
                e.printStackTrace();
            }
        }
        shredAllowlistPickaxes = EnumSet.noneOf(Material.class);
        for (String s : config.getStringList("shredAllowlistPickaxes")) {
            shredAllowlistPickaxes.add(Material.matchMaterial(s));
        }
        shredAllowlistShovels = EnumSet.noneOf(Material.class);
        for (String s : config.getStringList("shredAllowlistShovels")) {
            shredAllowlistShovels.add(Material.matchMaterial(s));
        }
        dryBiomes = EnumSet.noneOf(Biome.class);
        for (String s : config.getStringList("dryBiomes")) {
            dryBiomes.add(Biome.valueOf(s));
        }
        spectralMaterialConversion = new EnumMap<>(Material.class);
        for (String s : config.getStringList("spectralConversions")) {
            spectralMaterialConversion.put(Material.matchMaterial(s.split(":")[0]), Material.matchMaterial(s.split(":")[1]));
        }
    }

    private static final Random RND = new Random();

    public EnumSet<Material> grownCrops() {
        return grownCrops;
    }

    public EnumSet<Material> grownMelon() {
        return melonCrops;
    }

    public EnumSet<Material> airs() {
        return airs;
    }
    
    public EnumSet<Material> ores() {
        return ores;
    }

    public EnumSet<Biome> dryBiomes() {
        return dryBiomes;
    }

    public EnumSet<Material> unbreakableBlocks() {
        return unbreakable;
    }

    public EnumSet<Material> laserDenylist() {
        return laserDenylist;
    }

    public EnumSet<Material> terraformerMaterials() {
        return terraformerAllowlist;
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

    public EnumSet<Material> shredPicks() {
        return shredAllowlistPickaxes;
    }

    public EnumSet<Material> shredShovels() {
        return shredAllowlistShovels;
    }

    public List<Material> persephoneCrops() {
        return Arrays.asList(Material.WHEAT, Material.POTATO, Material.CARROT, Material.BEETROOT, Material.NETHER_WART,
                Material.SOUL_SAND, Material.FARMLAND);
    }

    public List<PotionEffectType> potionPotions() {
        return Arrays.asList(ABSORPTION,
                DAMAGE_RESISTANCE, FIRE_RESISTANCE, SPEED, JUMP, INVISIBILITY, INCREASE_DAMAGE, HEALTH_BOOST, HEAL,
                REGENERATION, NIGHT_VISION, SATURATION, FAST_DIGGING, WATER_BREATHING, DOLPHINS_GRACE);
    }

    // TODO what do these values even mean?
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
            Material.APPLE, Material.BAKED_POTATO, Material.BEETROOT, 
            Material.BEETROOT_SOUP, Material.BREAD, Material.CARROT, Material.TROPICAL_FISH, Material.COOKED_CHICKEN, 
            Material.COOKED_COD, Material.COOKED_MUTTON, Material.COOKED_PORKCHOP, Material.COOKED_RABBIT,
            Material.COOKED_SALMON, Material.COOKIE, Material.DRIED_KELP, Material.MELON_SLICE, 
            Material.MUSHROOM_STEW, Material.PUMPKIN_PIE, Material.RABBIT_STEW, Material.COOKED_BEEF};

    public Material[] gluttonyFoodItems() {
        return GLUTTONY_FOOD_ITEMS;
    }

    /**
     * The spectral conversion map provides mappings from the source material to the target material, those are meant to represent
     * (former) blockstate changes, which however was now adapted to convert between Materials since the flattening. <br>
     * As such, the Materials may not be totally related to each other, but with the MagicCompat.yml file everything can be changed now<br>
     * The reason an Enum Map is being used over the "traditional" dynamic approach with Tags.
     * @return An Enum Map used by the Spectral enchantment, using it outside of it may have little use. The exact values are user-generated.
     * @since 1.2.0
     */
    public EnumMap<Material, Material> spectralConversionMap() {
        return spectralMaterialConversion;
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
    
    private boolean legacyEntityShootBowEvent = false;
    
    /**
     * Method that scans whether API methods can be used. It also checks whether plugin integrations are possible and enabled
     */
    private void scanMethods() {
        // Test for java.lang.NoSuchMethodError in the Spigot API with the EntityShootBowEvent.
        try {
            EntityShootBowEvent.class.getConstructor(LivingEntity.class, ItemStack.class, ItemStack.class, Entity.class,
                    EquipmentSlot.class, float.class, boolean.class);
        } catch (NoSuchMethodException excepted) {
            Bukkit.getLogger().warning(Storage.MINILOGO + ChatColor.YELLOW + " Enabling potentially untested legacy mode"
                    + " for the EntityShootBowEvent. Handle with care and update to a newer Spigot (or Paper) version.");
            legacyEntityShootBowEvent = true;
        } catch (SecurityException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning(Storage.MINILOGO + ChatColor.YELLOW + " Enabling potentially untested legacy mode"
                    + " for the EntityShootBowEvent. Handle with care and update to a newer Spigot (or Paper) version.");
            legacyEntityShootBowEvent = true;
        }
    }
    
    /**
     * Dynamically constructs an EntityShootBowEvent, whose specification has changed lately. As such, this method will use
     *  the correct constructor without throwing a java.lang.NoSuchMethodError.
     * @param shooter The shooter
     * @param bow The used bow
     * @param consumable Not used in legacy mode. The item that was consumed
     * @param projectile The spawned projectile/arrow
     * @param hand  Not used in legacy mode. The used hand
     * @param force The force at which the bow is drawn
     * @param consumeItem  Not used in legacy mode.  Whether or not to consume the item
     * @return The constructed EntityShootBowEvent
     */
    public EntityShootBowEvent ConstructEntityShootBowEvent (@NotNull LivingEntity shooter, @Nullable ItemStack bow,
            @Nullable ItemStack consumable, @NotNull Entity projectile, @NotNull EquipmentSlot hand, float force, boolean consumeItem) {
        if (legacyEntityShootBowEvent) {
            try {
                return EntityShootBowEvent.class.getConstructor(LivingEntity.class, ItemStack.class, Entity.class, float.class)
                .newInstance(shooter, bow, projectile, force);
            } catch (NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
                Bukkit.getLogger().severe(Storage.LOGO + ChatColor.RED + " Unable to construct EntityShootBowEvent as the method was not found.");
                return null;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                Bukkit.getLogger().severe(Storage.LOGO + ChatColor.RED + " Unable to construct EntityShootBowEvent as argument errors"
                        + " occured (report this to the Issue page).");
                return null;
            }
        } else {
            return new EntityShootBowEvent(shooter, bow, consumable, projectile, hand, force, consumeItem);
        }
    }
}
