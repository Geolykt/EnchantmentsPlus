package de.geolykt.enchantments_plus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.enums.Tool;
import me.zombie_striker.psudocommands.CommandUtils;

import java.util.*;

import static org.bukkit.Material.*;

// This class handles all commands used by this plugin
public class CommandProcessor {

    public static class TabCompletion implements TabCompleter {

        @Override
        public List<String> onTabComplete(CommandSender sender, Command commandlabel, String alias, String[] args) {
            if (args.length == 0 || !(sender instanceof Player)) {
                return null;
            }

            EnchantPlayer player = EnchantPlayer.matchPlayer((Player) sender);
            Config config = Config.get(player.getPlayer().getWorld());
            ItemStack stack = player.getPlayer().getInventory().getItemInMainHand();
            String label = args[0].toLowerCase();
            List<String> results = new LinkedList<>();

            switch (label) {
                case "reload":
                case "list":
                case "help":
                case "version":
                case "give":
                    if (args.length == 2) {
                        for (Player plyr : Bukkit.getOnlinePlayers()) {
                            if (plyr.getPlayerListName().toLowerCase().startsWith(args[1].toLowerCase())) {
                                results.add(plyr.getPlayerListName());
                            }
                        }
                    } else if (args.length == 3) {
                        for (Material mat : Tool.ALL.getMaterials()) {
                            if (mat.toString().toLowerCase().startsWith(args[2].toLowerCase())) {
                                results.add(mat.toString());
                            }
                        }
                        // TODO: Fix out of bounds error below
                    } else if (args.length > 1 && config.enchantFromString(args[args.length - 2]) != null) {
                        CustomEnchantment ench = config.enchantFromString(args[args.length - 2]);
                        for (int i = 1; i <= ench.getMaxLevel(); i++) {
                            results.add(i + "");
                        }
                    } else if (args.length != 1) {
                        for (Map.Entry<String, CustomEnchantment> ench : config.getSimpleMappings()) {
                            if (ench.getKey().startsWith(args[args.length - 1]) && (stack.getType() == BOOK
                                    || stack.getType() == ENCHANTED_BOOK
                                    || ench.getValue().validMaterial(Material.matchMaterial(args[2])))) {
                                results.add(ench.getKey());
                            }
                        }
                    }
                    break;
                case "disable":
                case "enable":
                    results.add("all");
                case "info":
                    results = config.getEnchantNames();
                    if (args.length > 1) {
                        results.removeIf(e -> !e.startsWith(args[1]));
                    }
                    break;
                default:
                    if (args.length == 1) {
                        for (Map.Entry<String, CustomEnchantment> ench : config.getSimpleMappings()) {
                            if (ench.getKey().startsWith(args[0].toLowerCase(Locale.ENGLISH)) && (stack.getType() == BOOK
                                    || stack.getType() == ENCHANTED_BOOK || ench.getValue().validMaterial(
                                    stack.getType())
                                    || stack.getType() == AIR)) {
                                results.add(ench.getKey());
                            }
                        }
                    } else if (args.length == 2) {
                        CustomEnchantment ench = config.enchantFromString(args[0]);
                        if (ench != null) {
                            for (int i = 0; i <= ench.getMaxLevel(); i++) {
                                results.add(i + "");
                            }
                        }
                    } else if (args.length == 3) {
                        results.addAll(Arrays.asList("@a", "@p", "@r", "@s"));
                        results.removeIf(e -> !e.startsWith(args[2]));
                    } else if (args.length == 4) {
                        results.addAll(Arrays.asList("true", "false"));
                        results.removeIf(e -> !e.startsWith(args[3]));
                    }
            }
            return results;
        }
    }

    // Reloads the Enchantments_plus plugin
    private static boolean reload(CommandSender player) {
        if (!player.hasPermission("enchplus.command.reload")) {
            player.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        player.sendMessage(Storage.LOGO + "Reloaded Enchantments_plus configurations.");
        player.sendMessage(ChatColor.RED + " Please avoid using the command. It may create memory leaks and inaccurate configurations.");
        Storage.enchantments_plus.loadConfigs();
        return true;
    }

    // Gives the given player an item with certain enchantments determined by the arguments
    private static boolean give(CommandSender sender, String[] args) {
        if (!sender.hasPermission("enchplus.command.give")) {
            sender.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        if (args.length >= 4) {

            Scanner scanner = new Scanner(Arrays.toString(args).replace("[", "").replace("]",
                    "").replace(",", " "));
            scanner.next();
            String playerName = scanner.next();
            Player recipient = null;
            for (Player plyr : Bukkit.getOnlinePlayers()) {
                if (plyr.getName().equalsIgnoreCase(playerName)) {
                    recipient = plyr;
                }
            }
            if (recipient == null) {
                sender.sendMessage(Storage.LOGO + "The player " + ChatColor.DARK_AQUA + playerName
                        + ChatColor.AQUA + " is not online or does not exist.");
                return true;
            }
            Material mat = null;
            if (scanner.hasNextInt()) {
                // TODO: ID MAPPINGS, mat = Material.getMaterial(s.nextInt());
            } else {
                mat = Material.matchMaterial(scanner.next());
            }

            Config config = Config.get(recipient.getWorld());

            if (mat == null) {
                sender.sendMessage(Storage.LOGO + "The material " + ChatColor.DARK_AQUA
                        + args[2].toUpperCase() + ChatColor.AQUA + " is not valid.");
                return true;
            }
            Map<CustomEnchantment, Integer> enchantsToAdd = new HashMap<>();
            while (scanner.hasNext()) {
                String enchantName = scanner.next();
                int level = 1;
                if (scanner.hasNextInt()) {
                    level = Math.max(1, scanner.nextInt());
                }

                CustomEnchantment ench = config.enchantFromString(enchantName);

                if (ench != null) {
                    if (ench.validMaterial(mat) || mat == Material.BOOK || mat == Material.ENCHANTED_BOOK) {
                        enchantsToAdd.put(ench, level);
                    } else {
                        sender.sendMessage(Storage.LOGO + "The enchantment " + ChatColor.DARK_AQUA
                                + ench.loreName + ChatColor.AQUA + " cannot be given with this item.");
                    }
                } else {
                    sender.sendMessage(Storage.LOGO + "The enchantment " + ChatColor.DARK_AQUA
                            + enchantName + ChatColor.AQUA + " does not exist!");
                }
            }

            ItemStack stk = new ItemStack(mat);
            StringBuilder msgBldr
                    = new StringBuilder(Storage.LOGO + "Gave " + ChatColor.DARK_AQUA + recipient.getName()
                            + ChatColor.AQUA + " the enchantments ");

            for (Map.Entry<CustomEnchantment, Integer> ench : enchantsToAdd.entrySet()) {
                ench.getKey().setEnchantment(stk, ench.getValue(), config.getWorld());
                msgBldr.append(ChatColor.stripColor(ench.getKey().getLoreName()));
                msgBldr.append(", ");
            }
            if (!enchantsToAdd.isEmpty()) {
                recipient.getInventory().addItem(stk);
                String message = msgBldr.toString();
                sender.sendMessage(message.substring(0, message.length() - 2) + ".");
            }

        } else {
            sender.sendMessage(Storage.LOGO + ChatColor.DARK_AQUA + "Usage: " + ChatColor.AQUA
                    + "/ench give <Player> <Material> <enchantment> <?level> ...");
        }
        return true;
    }

    // Lists the Custom Enchantments applicable to the held tool
    private static boolean listEnchantment(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }
        
        EnchantPlayer player = EnchantPlayer.matchPlayer((Player) sender);
        Config config = Config.get(player.getPlayer().getWorld());
        ItemStack stack = player.getPlayer().getInventory().getItemInMainHand();
        
        if (!player.hasPermission("enchplus.command.list")) {
            player.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        player.sendMessage(Storage.LOGO + "Enchantment Types:");

        for (CustomEnchantment ench : new TreeSet<>(config.getEnchants())) {
            if (ench.validMaterial(stack)) {
                player.sendMessage(ChatColor.DARK_AQUA + "- " + ChatColor.AQUA + ench.getLoreName());
            }
        }
        return true;
    }

    // Gives information on each enchantment on the given tool or on the enchantment named in the parameter
    private static boolean infoEnchantment(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        
        EnchantPlayer player = EnchantPlayer.matchPlayer((Player) sender);
        Config config = Config.get(player.getPlayer().getWorld());
        
        if (!player.hasPermission("enchplus.command.info")) {
            player.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            CustomEnchantment ench = config.enchantFromString(args[1]);
            if (ench != null) {
                player.sendMessage(Storage.LOGO + ench.loreName + ": "
                        + (player.isDisabled(ench.getId()) ? ChatColor.RED + "**Disabled** " : "")
                        + ChatColor.AQUA + ench.description);
            }
        } else {
            Set<CustomEnchantment> enchs = CustomEnchantment.getEnchants(
                    player.getPlayer().getInventory().getItemInMainHand(), true, config.getWorld()).keySet();
            if (enchs.isEmpty()) {
                player.sendMessage(Storage.LOGO + "There are no custom enchantments on this tool!");
            } else {
                player.sendMessage(Storage.LOGO + "Enchantment Info:");
            }

            for (CustomEnchantment ench : enchs) {
                player.sendMessage(ChatColor.DARK_AQUA + ench.loreName + ": "
                        + (player.isDisabled(ench.getId()) ? ChatColor.RED + "**Disabled** " : "")
                        + ChatColor.AQUA + ench.description);
            }
        }
        return true;
    }

    // Disables the given enchantment for the player
    private static boolean disable(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        
        EnchantPlayer player = EnchantPlayer.matchPlayer((Player) sender);
        Config config = Config.get(player.getPlayer().getWorld());
        
        if (!player.hasPermission("enchplus.command.onoff")) {
            player.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            CustomEnchantment ench = config.enchantFromString(args[1]);
            if (ench != null) {
                player.disable(ench.getId());
                player.sendMessage(Storage.LOGO + "The enchantment " + ChatColor.DARK_AQUA
                        + ench.loreName + ChatColor.AQUA + " has been " + ChatColor.RED + "disabled.");
            } else if (args[1].equalsIgnoreCase("all")) {
                player.disableAll();
                player.sendMessage(Storage.LOGO + ChatColor.DARK_AQUA + "All " + ChatColor.AQUA
                        + "enchantments have been " + ChatColor.RED + "disabled.");
            } else {
                player.sendMessage(Storage.LOGO + "That enchantment does not exist!");
            }
        } else {
            player.sendMessage(
                    Storage.LOGO + ChatColor.DARK_AQUA + "Usage: " + ChatColor.AQUA + "/ench disable <enchantment/all>");
        }
        return true;
    }

    // Enables the given enchantment for the player
    private static boolean enable(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        
        EnchantPlayer player = EnchantPlayer.matchPlayer((Player) sender);
        Config config = Config.get(player.getPlayer().getWorld());
        
        if (!player.hasPermission("enchplus.command.onoff")) {
            player.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            CustomEnchantment ench = config.enchantFromString(args[1]);
            if (ench != null) {
                player.enable(ench.getId());
                player.sendMessage(Storage.LOGO + "The enchantment " + ChatColor.DARK_AQUA
                        + ench.loreName + ChatColor.AQUA + " has been" + ChatColor.GREEN + " enabled.");
            } else if (args[1].equalsIgnoreCase("all")) {
                player.enableAll();
                player.sendMessage(Storage.LOGO + ChatColor.DARK_AQUA + "All " + ChatColor.AQUA
                        + "enchantments have been enabled.");
            } else {
                player.sendMessage(Storage.LOGO + "That enchantment does not exist!");
            }
        } else {
            player.sendMessage(
                    Storage.LOGO + ChatColor.DARK_AQUA + "Usage: " + ChatColor.AQUA + "/ench enable <enchantment/all>");
        }
        return true;
    }

    // Lists all the commands associated with Custom Enchantments
    private static boolean helpEnchantment(CommandSender player, String label) {
        if (label.isEmpty() || label.equals("help")) {
            player.sendMessage(Storage.LOGO);
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench info <?enchantment>: " + ChatColor.AQUA
                    + "Returns information about custom enchantments.");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench list: " + ChatColor.AQUA
                    + "Returns a list of enchantments for the tool in hand.");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench give <Player> <Material> <enchantment> <?level> ... "
                    + ChatColor.AQUA + "Gives the target a specified enchanted item.");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench <enchantment> <?level> <?modifier> <?doNotification>: " + ChatColor.AQUA
                    + "Enchants the item in hand with the given enchantment and level");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench disable <enchantment/all>: " + ChatColor.AQUA
                    + "Disables selected enchantment for the user");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench enable <enchantment/all>: " + ChatColor.AQUA
                    + "Enables selected enchantment for the user");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench version: " + ChatColor.AQUA
                    + "Shows the version the plugin runs on.");
            return true;
        }
        return false;
    }

    private static boolean versionInfo(CommandSender sender) {
        sender.sendMessage(Storage.LOGO + ChatColor.AQUA + "Using Enchantments_plus " + Storage.version + ". Download it here:" + ChatColor.DARK_GREEN + " https://github.com/Geolykt/NMSless-Enchantments_plus");
        return true;
    }

    // Control flow for the command processor
    static boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        if (commandlabel.equalsIgnoreCase("ench")) {
            String label = args.length == 0 ? "" : args[0].toLowerCase();
            switch (label) {
                case "reload":
                    return reload(sender);
                case "give":
                    return give(sender, args);
                case "list":
                    return listEnchantment(sender);
                case "info":
                    return infoEnchantment(sender, args);
                case "disable":
                    return disable(sender, args);
                case "enable":
                    return enable(sender, args);
                case "version":
                    return versionInfo(sender);
                case "help":
                default:
                    return helpEnchantment(sender, label) || enchant(sender, args);
            }
        }
        return true;
    }

    private static boolean enchant(CommandSender sender, String[] args) {
        if (!sender.hasPermission("enchplus.command.enchant")) {
            sender.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        switch (args.length) {
        case 0:
            return false;
        case 1:
            return ench(sender,args[0], 1);
        case 2:
            try {
                return ench(sender, args[0], Integer.decode(args[1]));
            } catch (NumberFormatException e) {
                sender.sendMessage(Storage.LOGO + ChatColor.RED + "Argument 2 is not a number, however an Integer was expected");
                return false;
            }
        case 3:
            try {
                return ench(sender, args[0], Integer.decode(args[1]), args[2], false);
            } catch (NumberFormatException e) {
                sender.sendMessage(Storage.LOGO + ChatColor.RED + "Argument 2 is not a number, however an Integer was expected");
                return false;
            }
        case 4:
        default:
            try {
                return ench(sender, args[0], Integer.decode(args[1]), args[2], Boolean.parseBoolean(args[3]));
            } catch (NumberFormatException e) {
                sender.sendMessage(Storage.LOGO + ChatColor.RED + "Argument 2 is not a number, however an Integer was expected");
                return false;
            }
        }
    }

    private static boolean ench(CommandSender infoReciever, String enchantmentName, Integer level, String targetModif, Boolean doNotify) {
        Entity[] target = CommandUtils.getTargets(infoReciever, targetModif);
        if (target.length == 0) {
            infoReciever.sendMessage(Storage.LOGO + ChatColor.AQUA + "No entities changed.");
            return true;
        }
        for (Entity e: target) {
            if (e instanceof Player) {
                ench(infoReciever, enchantmentName, level, doNotify);
            } else if (e instanceof Monster) {
                ItemStack stack = ((Monster) e).getEquipment().getItemInMainHand();
                if (stack != null) {
                    CustomEnchantment.setEnchantment(stack, Config.get(e.getWorld()).enchantFromString(enchantmentName), level, e.getWorld());
                    ((Monster) e).getEquipment().setItemInMainHand(stack);
                }
            }
        }
        return true;
    }

    private static boolean ench(CommandSender target, String enchantmentName, Integer level, boolean postPlayerFeedback) {
        if (target instanceof Player) {
            Player player = (Player) target;
            CustomEnchantment ench = Config.get(player.getWorld()).enchantFromString(enchantmentName);
            if (ench == null) {
                if (postPlayerFeedback) {
                    player.sendMessage(Storage.LOGO + ChatColor.RED + "This is not a valid enchantment");
                }
                return true;
            } else if (player.getInventory().getItemInMainHand() == null ||
                       player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                if (postPlayerFeedback) {
                    target.sendMessage(Storage.LOGO + ChatColor.RED + " You cannot enchant air");
                }
                return false;
            } else {
                CustomEnchantment.setEnchantment(player.getInventory().getItemInMainHand(), ench, level, player.getWorld());
                if (postPlayerFeedback) {
                    if (level <= 0) {
                        player.sendMessage(Storage.LOGO + ChatColor.AQUA + "The enchantment " + ChatColor.BLUE + ench.getLoreName() + ChatColor.AQUA + " was removed from your tool.");
                    } else if (level == 1) {
                        player.sendMessage(Storage.LOGO + ChatColor.AQUA + "Your tool in hand was enchanted with " + ChatColor.BLUE + ench.getLoreName() + ChatColor.AQUA + ".");
                    } else {
                        player.sendMessage(Storage.LOGO + ChatColor.AQUA + "Your tool in hand was enchanted with " + ChatColor.BLUE + ench.getLoreName() + ChatColor.AQUA + " at level " + ChatColor.BLUE + level + ChatColor.AQUA + ".");
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    private static boolean ench(CommandSender infoReciever, String enchantmentName, Integer level) {
        return ench(infoReciever, enchantmentName, level, true);
    }
}
