package me.zombie_striker.psudocommands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class licensed under the GNU General Public License v2.0; 
 * see https://github.com/cricri211/PsudoCommands/blob/master/LICENSE for the whole license.
 *  Original can be found at https://github.com/cricri211/PsudoCommands/blob/master/src/me/zombie_striker/psudocommands/CommandUtils.java.
 * <hr>
 *  Source altered to use spaces instead of tabs.
 */
public class CommandUtils {

    /**
     * Use this if you are unsure if a player provided the "@a" tag. This will allow
     * multiple entities to be retrieved.
     * <p>
     * This can return a null variable if no tags are included, or if a value for a
     * tag does not exist (I.e if the tag [type=___] contains an entity that does
     * not exist in the specified world)
     * <p>
     * The may also be empty or null values at the end of the array. Once a null
     * value has been reached, you do not need to loop through any of the higher
     * indexes
     * <p>
     * Currently supports the tags:
     *
     * @param arg    the argument that we are testing for
     * @param sender the sender of the command
     * @return The entities that match the criteria
     * @p , @a , @e , @r
     * <p>
     * Currently supports the selectors: [type=] [r=] [rm=] [c=] [w=] [m=]
     * [name=] [l=] [lm=] [h=] [hm=] [rx=] [rxm=] [ry=] [rym=] [team=]
     * [score_---=] [score_---_min=] [x] [y] [z] [dx] [dy] [dz] [gamemode=]
     * [limit=] [x_rotation] [y_rotation] [tag=] [scores={}] [level=]
     *
     * Note : m, r rm, rx rxm, ry rym, l lm, score_--- score_---_min are old
     * format from 1.12-. They are respectively replaced by gamemode,
     * distance, x_rotation, y_rotation, level, scores={}, in vanilla 1.13+
     * Try to use newer tags if you run on 1.13+
     * <p>
     * All selectors can be inverted.
     */
    public static Entity[] getTargets(CommandSender sender, String arg) {
        Entity[] ents;
        Location loc = null;
        if (sender instanceof Player) {
            loc = ((Player) sender).getLocation();
        } else if (sender instanceof BlockCommandSender) {
            // Center of block.
            loc = ((BlockCommandSender) sender).getBlock().getLocation().add(.5, 0, .5);
        } else if (sender instanceof CommandMinecart) {
            loc = ((CommandMinecart) sender).getLocation();
        }
        String[] tags = getTags(arg);

        // prefab fix
        if (loc != null) {
            for (String s : tags) {
                if (hasTag(SelectorType.X, s)) {
                    loc.setX(s.contains(".") ? getValueAsFloat(s) : getValueAsInteger(s));
                } else if (hasTag(SelectorType.Y, s)) {
                    loc.setY(s.contains(".") ? getValueAsFloat(s) : getValueAsInteger(s));
                } else if (hasTag(SelectorType.Z, s)) {
                    loc.setZ(s.contains(".") ? getValueAsFloat(s) : getValueAsInteger(s));
                }
            }
        }

        if (arg.startsWith("@s")) {
            ents = new Entity[1];
            if (sender instanceof Player) {
                boolean good = true;
                for (int b = 0; b < tags.length; b++) {
                    if (!canBeAccepted(tags[b], (Entity) sender, loc)) {
                        good = false;
                        break;
                    }
                }
                if (good) {
                    ents[0] = (Entity) sender;
                }
            } else {
                return null;
            }
            return ents;
        } else if (arg.startsWith("@a")) {
            // ents = new Entity[maxEnts];
            List<Entity> listOfValidEntities = new ArrayList<>();
            int C = getLimit(tags);

            boolean usePlayers = true;
            for (String tag : tags) {
                if (hasTag(SelectorType.TYPE, tag)) {
                    usePlayers = false;
                    break;
                }
            }
            List<Entity> ea = new ArrayList<Entity>(Bukkit.getOnlinePlayers());
            if (!usePlayers) {
                ea.clear();
                for (World w : getAcceptedWorldsFullString(loc, arg)) {
                    ea.addAll(w.getEntities());
                }
            }
            for (Entity e : ea) {
                if (listOfValidEntities.size() >= C)
                    break;
                boolean isValid = true;
                for (int b = 0; b < tags.length; b++) {
                    if (!canBeAccepted(tags[b], e, loc)) {
                        isValid = false;
                        break;
                    }
                }
                if (isValid) {
                    listOfValidEntities.add(e);
                }
            }

            ents = listOfValidEntities.toArray(new Entity[listOfValidEntities.size()]);

        } else if (arg.startsWith("@p")) {
            ents = new Entity[1];
            double closestInt = Double.MAX_VALUE;
            Entity closest = null;

            for (World w : getAcceptedWorldsFullString(loc, arg)) {
                for (Player e : w.getPlayers()) {
                    Location temp = loc;
                    if (temp == null)
                        temp = e.getWorld().getSpawnLocation();
                    double distance = e.getLocation().distanceSquared(temp);
                    if (closestInt > distance) {
                        boolean good = true;
                        for (String tag : tags) {
                            if (!canBeAccepted(tag, e, temp)) {
                                good = false;
                                break;
                            }
                        }
                        if (good) {
                            closestInt = distance;
                            closest = e;
                        }
                    }
                }
            }
            ents[0] = closest;
        } else if (arg.startsWith("@e")) {
            List<Entity> entities = new ArrayList<>();
            int C = getLimit(tags);
            for (World w : getAcceptedWorldsFullString(loc, arg)) {
                for (Entity e : w.getEntities()) {
                    if (entities.size() >= C) {
                        break;
                    }
                    boolean valid = true;
                    for (String tag : tags) {
                        if (!canBeAccepted(tag, e, loc)) {
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        entities.add(e);
                    }
                }
            }
            ents = entities.toArray(new Entity[entities.size()]);
        } else if (arg.startsWith("@r")) {
            Random r = ThreadLocalRandom.current();
            ents = new Entity[1];

            List<Entity> validEntities = new ArrayList<>();
            for (World w : getAcceptedWorldsFullString(loc, arg)) {
                if (hasTag(SelectorType.TYPE, arg)) {
                    for (Entity e : w.getEntities()) {
                        boolean good = true;
                        for (String tag : tags) {
                            if (!canBeAccepted(tag, e, loc)) {
                                good = false;
                                break;
                            }
                        }
                        if (good)
                            validEntities.add(e);
                    }
                } else {
                    for (Entity e : Bukkit.getOnlinePlayers()) {
                        boolean good = true;
                        for (String tag : tags) {
                            if (!canBeAccepted(tag, e, loc)) {
                                good = false;
                                break;
                            }
                        }
                        if (good)
                            validEntities.add(e);
                    }
                }
            }
            ents[0] = validEntities.get(r.nextInt(validEntities.size()));
        } else {
            ents = new Entity[]{Bukkit.getPlayer(arg)};
        }
        return ents;
    }

    /**
     * Returns one entity. Use this if you know the player will not provide the '@a'
     * tag.
     * <p>
     * This can return a null variable if no tags are included, or if a value for a
     * tag does not exist (I.e if the tag [type=___] contains an entity that does
     * not exist in the specified world)
     *
     * @param sender the command sender
     * @param arg    the argument of the target
     * @return The first entity retrieved.
     */
    public static Entity getTarget(CommandSender sender, String arg) {
        Entity[] e = getTargets(sender, arg);
        if (e == null || e.length == 0)
            return null;
        return e[0];
    }

    /**
     * Returns true if str is a relative, local or world coordinate.
     * E.g. "^-2" or "~1.45" or "-2.4"
     * If firstCoord is true, str has to be only relative or local, with "~" or "^",
     * not only number. Easier to detect the first coordinate of the three.
     * If it is false, str can be a double as string.
     *
     * @param str        The tested coordinate
     * @param firstCoord Is str the first coordinate of the three.
     * @return True if str is a relative coordinate
     */
    public static boolean isRelativeCoord(String str, boolean firstCoord) {
        // return true if it is a used coordinated like ~3.4 or ^40 or 3.1 ...
        if(str.startsWith("~") || str.startsWith("^")) {
            return str.length() == 1 || isDouble(str.substring(1));
        }
        return !firstCoord && isDouble(str);
    }

    /**
     * Parse string coordinates as double coordinates.
     * Each string is a number or starts with "~".
     * Precondition : x, y and z verify isRelativeCoord.
     *
     * @param x      First coordinate
     * @param y      Second coordinate
     * @param z      Third coordinate
     * @param origin The origin location for relative
     * @return The arrival location
     */
    public static Location getRelativeCoord(String x, String y, String z, Location origin) {
        Location arrival = origin.clone();
        if(x.startsWith("~")) {
            arrival.setX(arrival.getX() + getCoordinate(x));
        } else {
            arrival.setX(getCoordinate(x));
        }

        if(y.startsWith("~")) {
            arrival.setY(arrival.getY() + getCoordinate(y));
        } else {
            arrival.setY(getCoordinate(y));
        }

        if(z.startsWith("~")) {
            arrival.setZ(arrival.getZ() + getCoordinate(z));
        } else {
            arrival.setZ(getCoordinate(z));
        }
        return arrival;
    }

    /**
     * Parse string coordinates as double coordinates.
     * Each string starts with "^".
     * Precondition : x, y and z verify isRelativeCoord.
     *
     * @param x      First coordinate
     * @param y      Second coordinate
     * @param z      Third coordinate
     * @param origin The origin location for local
     * @return The arrival location
     */
    public static Location getLocalCoord(String x, String y, String z, Location origin) {
        Location arrival = origin.clone();

        Vector dirX = new Location(arrival.getWorld(), 0, 0, 0, Location.normalizeYaw(arrival.getYaw()-90),
                arrival.getPitch()).getDirection().normalize();
        Vector dirY = new Location(arrival.getWorld(), 0, 0, 0, arrival.getYaw(),
                arrival.getPitch()-90).getDirection().normalize();
        Vector dirZ = arrival.getDirection().normalize();

        return arrival.add(dirX.multiply(getCoordinate(x)))
                .add(dirY.multiply(getCoordinate(y)))
                .add(dirZ.multiply(getCoordinate(z)));
    }

    private static double getCoordinate(String c) {
        // c is like ^3 or ~-1.2 or 489.1
        return c.length() == 1 ? 0 : Double.parseDouble(c.substring(1));
    }

    private static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean canBeAccepted(String arg, Entity e, Location loc) {
        SelectorType type = getTag(arg);
        if (type == null)
            return false;
        switch (type) {
        case X:
        case Y:
        case Z:
        case LIMIT:
        case C:
            return true;
        case DISTANCE:
            return isWithinDistance(arg, loc, e);
        case TYPE:
            return isType(arg, e);
        case TAG:
            return isHasTags(arg, e);
        case NAME:
            return isName(arg, e);
        case TEAM:
            return isTeam(arg, e);
        case X_ROTATION:
            return isWithinYaw(arg, e);
        case Y_ROTATION:
            return isWithinPitch(arg, e);
        case DX:
            return e.getLocation().getWorld() == loc.getWorld() && isDRange(arg, e.getLocation().getX(), loc.getX());
        case DY:
            return e.getLocation().getWorld() == loc.getWorld() && isDRange(arg, e.getLocation().getY(), loc.getY());
        case DZ:
            return e.getLocation().getWorld() == loc.getWorld() && isDRange(arg, e.getLocation().getZ(), loc.getZ());
        case SCORES:
            return isScoreWithin(arg, e);
        case SCORE_FULL:
            return isScore(arg, e);
        case SCORE_MIN:
            return isScoreMin(arg, e);
        case R:
            return isR(arg, loc, e);
        case RM:
            return isRM(arg, loc, e);
        case RX:
            return isRX(arg, e);
        case RXM:
            return isRXM(arg, e);
        case RY:
            return isRY(arg, e);
        case RYM:
            return isRYM(arg, e);
        case LEVEL:
            return isWithinLevel(arg, e);
        case L:
            return isL(arg, e);
        case LMax:
            return isLM(arg, e);
        case GAMEMODE:
        case M:
            return isM(arg, e);
        case H:
            return isH(arg, e);
        case HM:
            return isHM(arg, e);
        case World:
            return isW(arg);
        default:
            return false;
        }
    }

    private static String[] getTags(String arg) {
        if (!arg.contains("["))
            return new String[0];
        String tags = arg.split("\\[")[1].split("\\]")[0];
        return tags.split(",");
    }

    private static int getLimit(String[] tags) {
        for (String s : tags) {
            if (hasTag(SelectorType.LIMIT, s) || hasTag(SelectorType.C, s)) {
                return getValueAsInteger(s);
            }
        }
        return Integer.MAX_VALUE;
    }

    private static String getType(String arg) {
        if (hasTag(SelectorType.TYPE, arg))
            return arg.toLowerCase().split("=")[1].replace("!", "");
        return "Player";
    }

    private static String getName(String arg) {
        String reparg = arg.replace(" ", "_");
        return reparg.replace("!", "").split("=")[1];
    }

    private static World getW(String arg) {
        return Bukkit.getWorld(getString(arg));
    }

    private static String getScoreMinName(String arg) {
        return arg.split("=")[0].substring(0, arg.split("=")[0].length() - 1 - 4).replace("score_", "");
    }

    private static String getScoreName(String arg) {
        return arg.split("=")[0].replace("score_", "");
    }

    private static String getTeam(String arg) {
        return arg.toLowerCase().replace("!", "").split("=")[1];
    }

    private static float getValueAsFloat(String arg) {
        return Float.parseFloat(arg.replace("!", "").split("=")[1]);
    }

    private static int getValueAsInteger(String arg) {
        return Integer.parseInt(arg.replace("!", "").split("=")[1]);
    }

    private static GameMode getM(String arg) {
        String[] split = arg.replace("!", "").toLowerCase().split("=");
        String returnType = split[1];
        if (returnType.equalsIgnoreCase("0") || returnType.equalsIgnoreCase("s")
                || returnType.equalsIgnoreCase("survival"))
            return GameMode.SURVIVAL;
        if (returnType.equalsIgnoreCase("1") || returnType.equalsIgnoreCase("c")
                || returnType.equalsIgnoreCase("creative"))
            return GameMode.CREATIVE;
        if (returnType.equalsIgnoreCase("2") || returnType.equalsIgnoreCase("a")
                || returnType.equalsIgnoreCase("adventure"))
            return GameMode.ADVENTURE;
        if (returnType.equalsIgnoreCase("3") || returnType.equalsIgnoreCase("sp")
                || returnType.equalsIgnoreCase("spectator"))
            return GameMode.SPECTATOR;
        return null;
    }

    private static List<World> getAcceptedWorldsFullString(Location loc, String fullString) {
        String string = null;
        for (String tag : getTags(fullString)) {
            if (hasTag(SelectorType.World, tag)) {
                string = tag;
                break;
            }
        }
        if (string == null) {
            List<World> worlds = new ArrayList<>();
            if (loc == null || loc.getWorld() == null) {
                worlds.addAll(Bukkit.getWorlds());
            } else {
                worlds.add(loc.getWorld());
            }
            return worlds;
        }
        return getAcceptedWorlds(string);
    }

    private static List<World> getAcceptedWorlds(String string) {
        List<World> worlds = new ArrayList<>(Bukkit.getWorlds());
        if (isInverted(string)) {
            worlds.remove(getW(string));
        } else {
            worlds.clear();
            worlds.add(getW(string));
        }
        return worlds;
    }

    private static boolean isDRange(String arg, double value, double base) {
        return value > (base - .35) && value < (base + Double.parseDouble(arg.split("=")[1]) + 1.35);
    }

    private static boolean isTeam(String arg, Entity e) {
        if (!(e instanceof Player))
            return false;
        for (Team t : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            if ((t.getName().equalsIgnoreCase(getTeam(arg)) != isInverted(arg))) {
                if ((t.getEntries().contains(e.getName()) != isInverted(arg)))
                    return true;
            }
        }
        return false;
    }

    private static boolean isWithinPitch(String arg, Entity e) {
        return isWithinDoubleValue(isInverted(arg), arg.split("=")[1], e.getLocation().getPitch());
    }

    private static boolean isWithinYaw(String arg, Entity e) {
        return isWithinDoubleValue(isInverted(arg), arg.split("=")[1], e.getLocation().getYaw());
    }

    private static boolean isWithinDistance(String arg, Location start, Entity e) {
        double distanceMin = 0;
        double distanceMax = Double.MAX_VALUE;
        String distance = arg.split("=")[1];
        if (e.getLocation().getWorld() != start.getWorld())
            return false;
        if (distance.contains("..")) {
            String[] temp = distance.split("\\.\\.");
            if (!temp[0].isEmpty()) {
                distanceMin = Integer.parseInt(temp[0]);
            }
            if (temp.length > 1 && !temp[1].isEmpty()) {
                distanceMax = Double.parseDouble(temp[1]);
            }
            double actDis = start.distanceSquared(e.getLocation());
            return actDis <= distanceMax * distanceMax && distanceMin * distanceMin <= actDis;
        } else {
            int mult = Integer.parseInt(distance);
            mult *= mult;
            return ((int) start.distanceSquared(e.getLocation())) == mult;
        }
    }

    private static boolean isWithinLevel(String arg, Entity e) {
        if (!(e instanceof Player))
            return false;
        double distanceMin = 0;
        double distanceMax = Double.MAX_VALUE;
        String distance = arg.split("=")[1];
        if (distance.contains("..")) {
            String[] temp = distance.split("..");
            if (!temp[0].isEmpty()) {
                distanceMin = Integer.parseInt(temp[0]);
            }
            if (temp[1] != null && !temp[1].isEmpty()) {
                distanceMax = Double.parseDouble(temp[1]);
            }
            double actDis = ((Player) e).getExpToLevel();
            return actDis <= distanceMax * distanceMax && distanceMin * distanceMin <= actDis;
        } else {
            return ((Player) e).getExpToLevel() == Integer.parseInt(distance);
        }
    }

    private static boolean isScore(String arg, Entity e) {
        for (Objective o : Bukkit.getScoreboardManager().getMainScoreboard().getObjectives()) {
            if (o.getName().equalsIgnoreCase(getScoreName(arg))) {
                int score = o.getScore(e instanceof Player ? e.getName() : e.getUniqueId().toString()).getScore();
                if (score <= getValueAsInteger(arg) != isInverted(arg))
                    return true;
            }
        }
        return false;
    }

    private static boolean isScoreWithin(String arg, Entity e) {
        String[] scores = arg.split("\\{")[1].split("\\}")[0].split(",");
        for (int i = 0; i < scores.length; i++) {
            String[] s = scores[i].split("=");
            String name = s[0];

            for (Objective o : Bukkit.getScoreboardManager().getMainScoreboard().getObjectives()) {
                if (o.getName().equalsIgnoreCase(name)) {
                    int score = o.getScore(e instanceof Player ? e.getName() : e.getUniqueId().toString()).getScore();
                    if (!isWithinDoubleValue(isInverted(arg), s[1], score)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean isHasTags(String arg, Entity e) {
        return isInverted(arg) != e.getScoreboardTags().contains(getString(arg));
    }

    private static boolean isScoreMin(String arg, Entity e) {
        for (Objective o : Bukkit.getScoreboardManager().getMainScoreboard().getObjectives()) {
            if (o.getName().equalsIgnoreCase(getScoreMinName(arg))) {
                int score = o.getScore(e instanceof Player ? e.getName() : e.getUniqueId().toString()).getScore();
                if (score >= getValueAsInteger(arg) != isInverted(arg)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isRM(String arg, Location loc, Entity e) {
        if (loc.getWorld() != e.getWorld())
            return false;
        return isGreaterThan(arg, loc.distance(e.getLocation()));
    }

    private static boolean isR(String arg, Location loc, Entity e) {
        if (loc.getWorld() != e.getWorld())
            return false;
        return isLessThan(arg, loc.distance(e.getLocation()));

    }

    private static boolean isRXM(String arg, Entity e) {
        return isLessThan(arg, e.getLocation().getYaw());
    }

    private static boolean isRX(String arg, Entity e) {
        return isGreaterThan(arg, e.getLocation().getYaw());
    }

    private static boolean isRYM(String arg, Entity e) {
        return isLessThan(arg, e.getLocation().getPitch());
    }

    private static boolean isRY(String arg, Entity e) {
        return isGreaterThan(arg, e.getLocation().getPitch());
    }

    private static boolean isL(String arg, Entity e) {
        if (e instanceof Player) {
            isLessThan(arg, ((Player) e).getTotalExperience());
        }
        return false;
    }

    private static boolean isLM(String arg, Entity e) {
        if (e instanceof Player) {
            return isGreaterThan(arg, ((Player) e).getTotalExperience());
        }
        return false;
    }

    private static boolean isH(String arg, Entity e) {
        if (e instanceof Damageable)
            return isGreaterThan(arg, ((Damageable) e).getHealth());
        return false;
    }

    private static boolean isHM(String arg, Entity e) {
        if (e instanceof Damageable)
            return isLessThan(arg, ((Damageable) e).getHealth());
        return false;
    }

    private static boolean isM(String arg, Entity e) {
        if (getM(arg) == null)
            return true;
        if (e instanceof HumanEntity) {
            return isInverted(arg) != (getM(arg) == ((HumanEntity) e).getGameMode());
        }
        return false;
    }

    private static boolean isW(String arg) {
        World w = getW(arg);
        if (w == null) {
            return true;
        }
        return isInverted(arg) != getAcceptedWorlds(arg).contains(w);
    }

    private static boolean isName(String arg, Entity e) {
        if (getName(arg) == null)
            return true;
        if ((isInverted(arg) != (e.getCustomName() != null) && isInverted(arg) != (getName(arg)
                .equals(e.getCustomName().replace(" ", "_"))
                || (e instanceof Player && e.getName().replace(" ", "_").equalsIgnoreCase(getName(arg))))))
            return true;
        return false;
    }

    private static boolean isType(String arg, Entity e) {
        boolean invert = isInverted(arg);
        String type = getType(arg);
        if (invert != e.getType().name().equalsIgnoreCase(type))
            return true;
        return false;

    }

    private static boolean isInverted(String arg) {
        return arg.toLowerCase().split("!").length != 1;
    }

    public static String getString(String arg) {
        return arg.split("=")[1].replaceAll("!", "");
    }

    private static boolean isLessThan(String arg, double value) {
        boolean inverted = isInverted(arg);
        double mult = Double.parseDouble(arg.split("=")[1]);
        return (value < mult) != inverted;
    }

    private static boolean isGreaterThan(String arg, double value) {
        boolean inverted = isInverted(arg);
        double mult = Double.parseDouble(arg.split("=")[1]);
        return (value > mult) != inverted;
    }

    private static boolean isWithinDoubleValue(boolean inverted, String arg, double value) {
        double min = -Double.MAX_VALUE;
        double max = Double.MAX_VALUE;
        if (arg.contains("..")) {
            String[] temp = arg.split("\\.\\.");
            if (!temp[0].isEmpty()) {
                min = Integer.parseInt(temp[0]);
            }
            if (temp.length > 1 && !temp[1].isEmpty()) {
                max = Double.parseDouble(temp[1]);
            }
            return (value <= max && min <= value) != inverted;
        } else {
            double mult = Double.parseDouble(arg);
            return (value == mult) != inverted;
        }
    }

    private static boolean hasTag(SelectorType type, String arg) {
        return arg.toLowerCase().startsWith(type.getName());
    }

    private static SelectorType getTag(String arg) {
        String name = arg.toLowerCase().split("=")[0] + "=";
        for (SelectorType type : SelectorType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    enum SelectorType {
        LEVEL("level="), LMax("lm="), L("l="),
        DISTANCE("distance="), RM("rm="), R("r="),
        X_ROTATION("x_rotation"), RYM("rym="), RY("ry="),
        Y_ROTATION("y_rotation"), RXM("rxm="), RX("rx="),
        LIMIT("limit="), C("c="),
        SCORES("scores="), SCORE_FULL("score="), SCORE_MIN("score_min"),
        GAMEMODE("gamemode="), M("m="),
        X("x="), Y("y="), Z("z="), DX("dx="), DY("dy="), DZ("dz="),
        TYPE("type="), NAME("name="), TEAM("team="), World("w="),
        HM("hm="), H("h="), TAG("tag=");
        String name;

        SelectorType(String s) {
            this.name = s;
        }

        public String getName() {
            return name;
        }
    }
}
