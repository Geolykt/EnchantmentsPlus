/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2021 Geolykt and EnchantmentsPlus contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.geolykt.enchantments_plus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.geolykt.enchantments_plus.enchantments.Laser;
import de.geolykt.enchantments_plus.enums.BaseEnchantments;
import de.geolykt.enchantments_plus.enums.PermissionTypes;
import de.geolykt.enchantments_plus.util.ColUtil;
import de.geolykt.enchantments_plus.util.Tool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
            Config config = Config.get(((Player) sender).getWorld());
            ItemStack stack = ((Player) sender).getInventory().getItemInMainHand();
            String label = args[0].toLowerCase();
            List<String> results = new LinkedList<>();

            switch (label) {
                case "lasercol":
                    if (PermissionTypes.LASERCOL.hasPermission(sender))  {
                        results.addAll(Arrays.asList("AQUA","BLACK","BLUE","FUCHSIA",
                            ((Player)sender).getLocale().toLowerCase(Locale.ROOT).contains("us") ? "GRAY" : "GREY",
                                    "GREEN","LIME","MAROON","NAVY","OLIVE","ORANGE","PURPLE","RED","SILVER","TEAL","WHITE","YELLOW"));
                        if (args.length != 1)
                            results.removeIf(e -> !e.startsWith(args[1]));
                    }
                case "reload":
                case "list":
                case "help":
                case "version":
                    return results;
                case "give":
                    if (!PermissionTypes.GIVE.hasPermission(sender)) {
                        return results;
                    }
                    if (args.length == 2) {
                        for (Player plyr : Bukkit.getOnlinePlayers()) {
                            if (plyr.getPlayerListName().toLowerCase().startsWith(args[1].toLowerCase())) {
                                results.add(plyr.getPlayerListName());
                            }
                        }
                    } else if (args.length == 3) {
                        boolean hasBook = false;
                        boolean hasEnchantedBook = false;
                        for (Material mat : Tool.ALL.getMaterials()) {
                            if (!hasBook) {
                                hasBook = mat == Material.BOOK;
                            }
                            if (!hasEnchantedBook) {
                                hasEnchantedBook = mat == Material.ENCHANTED_BOOK;
                            }
                            if (mat.toString().toLowerCase().startsWith(args[2].toLowerCase())) {
                                results.add(mat.toString());
                            }
                        }
                        if (!hasBook) {
                            if (Material.BOOK.toString().toLowerCase().startsWith(args[2].toLowerCase())) {
                                results.add(Material.BOOK.toString());
                            }
                        }
                        if (!hasEnchantedBook) {
                            if (Material.ENCHANTED_BOOK.toString().toLowerCase().startsWith(args[2].toLowerCase())) {
                                results.add(Material.ENCHANTED_BOOK.toString());
                            }
                        }
                        // FIXME: Fix out of bounds error below
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
                    return results;
                case "disable":
                case "enable":
                    if (!PermissionTypes.ONOFF.hasPermission(sender)) {
                        return results;
                    }
                    results.add("all");
                    return results;
                case "info":
                    if (!PermissionTypes.INFO.hasPermission(sender)) {
                        return results;
                    }
                    results.addAll(config.getEnchantNames());
                    if (args.length > 1) {
                        results.removeIf(e -> !e.startsWith(args[1]));
                    }
                    return results;
                default:
                    results.addAll(enchantTabCompletion(sender, config, stack, args));
                    if (args.length == 1) {
                        if (PermissionTypes.GIVE.hasPermission(sender)) {
                            results.add("give");
                        }
                        if (PermissionTypes.INFO.hasPermission(sender)) {
                            results.add("info");
                        }
                        if (PermissionTypes.LASERCOL.hasPermission(sender) && 
                                CustomEnchantment.hasEnchantment(config, stack, BaseEnchantments.LASER)) {
                            results.add("lasercol");
                        }
                        if (PermissionTypes.LIST.hasPermission(sender)) {
                            results.add("list");
                        }
                        if (PermissionTypes.ONOFF.hasPermission(sender)) {
                            results.addAll(Arrays.asList("enable", "disable"));
                        }
                        if (PermissionTypes.RELOAD.hasPermission(sender)) {
                            results.add("reload");
                        }
                        results.addAll(Arrays.asList("version", "help"));
                        results.removeIf(e -> !e.startsWith(args[0]));
                    }
                    return results;
            }
        }
        private static Collection<String> enchantTabCompletion (CommandSender sender, Config config, ItemStack stack, String [] args) {

        /**
         * Performs the Tab Completion for the enchanting part of the ench command.
         * @param sender The sender of the tab completion request
         * @param config The configuration to be used
         * @param stack The Itemstack that is held in hand
         * @param args the previous arguments that were used
         * @return The tab completion suggestions.
         * @since 1.0
         */
            if (!PermissionTypes.ENCHANT.hasPermission(sender)) {
                return Arrays.asList();
            }
            LinkedList<String> results = new LinkedList<>();
            switch (args.length) {
            case 1:
                for (Map.Entry<String, CustomEnchantment> ench : config.getSimpleMappings()) {
                    if (ench.getKey().startsWith(args[0].toLowerCase(Locale.ENGLISH)) && (
                            stack.getType() == BOOK
                            || stack.getType() == ENCHANTED_BOOK 
                            || ench.getValue().validMaterial(stack.getType())
                            || stack.getType() == AIR)) {
                        results.add(ench.getKey());
                    }
                }
                results.removeIf(e -> !e.startsWith(args[0]));
                break;
            case 2: {
                CustomEnchantment ench = config.enchantFromString(args[0]);
                if (ench != null) {
                    for (int i = 0; i <= ench.getMaxLevel(); i++) {
                        results.add(i + "");
                    }
                }
                break;
            }
            case 3:
                results.addAll(Arrays.asList("@a", "@p", "@r", "@s"));
                results.removeIf(e -> !e.startsWith(args[2]));
                break;
            case 4:
                results.addAll(Arrays.asList("true", "false"));
                results.removeIf(e -> !e.startsWith(args[3]));
                break;
            case 5:
                results.addAll(Arrays.asList("true", "false"));
                results.removeIf(e -> !e.startsWith(args[4]));
                break;
            }
            return results;
        }
    }

    // Reloads the Enchantments_plus plugin
    private static boolean reload(CommandSender sender) {
        if (!PermissionTypes.RELOAD.hasPermission(sender)) {
            sender.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        sender.sendMessage(Storage.LOGO + "Reloaded Enchantments+ configurations.");
        sender.sendMessage(ChatColor.RED + "Please avoid using the command. It may create memory leaks and inaccurate configurations.");
        Storage.plugin.loadConfigs();
        return true;
    }

    // Gives the given player an item with certain enchantments determined by the arguments
    private static boolean give(CommandSender sender, String[] args) {
        if (!PermissionTypes.GIVE.hasPermission(sender)) {
            sender.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        if (args.length >= 4) {
            String playerName = args[1];
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
            Material mat =  Material.matchMaterial(args[2]);

            if (mat == null) {
                sender.sendMessage(Storage.LOGO + "The material " + ChatColor.DARK_AQUA
                        + args[2].toUpperCase() + ChatColor.AQUA + " is not valid.");
                return true;
            }

            Config config = Config.get(recipient.getWorld());
            Map<CustomEnchantment, Integer> enchantsToAdd = new HashMap<>();
            int i = 3;
            while (i < args.length) {
                String enchantName = args[i++];
                int level = 1;
                if (i < args.length) {
                    try {
                        level = Integer.valueOf(args[i]);
                        if (level < 1) {
                            level = 1;
                        }
                        i++;
                    }  catch (NumberFormatException ignore) {
                        // ignored
                    }
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
                ench.getKey().setEnchantment(stk, ench.getValue(), recipient.getWorld());
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

        Config config = Config.get(((Player) sender).getWorld());
        ItemStack stack = ((Player) sender).getInventory().getItemInMainHand();

        if (!PermissionTypes.LIST.hasPermission(sender)) {
            sender.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        sender.sendMessage(Storage.LOGO + "Enchantment Types:");

        for (CustomEnchantment ench : new TreeSet<>(config.getEnchants())) {
            if (ench.validMaterial(stack)) {
                sender.sendMessage(ChatColor.DARK_AQUA + "- " + ChatColor.AQUA + ench.getLoreName());
            }
        }
        return true;
    }

    // Gives information on each enchantment on the given tool or on the enchantment named in the parameter
    private static boolean infoEnchantment(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Config config = Config.get(((Player) sender).getWorld());

        if (!PermissionTypes.INFO.hasPermission(sender)) {
            sender.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            CustomEnchantment ench = config.enchantFromString(args[1]);
            if (ench != null) {
                sender.sendMessage(Storage.LOGO + ench.loreName + ": "
                        + (EnchantPlayer.isDisabled((Player) sender, ench.asEnum()) ? ChatColor.RED + "**Disabled** " : "")
                        + ChatColor.AQUA + ench.description);
            }
        } else {
            Set<CustomEnchantment> enchs =  CustomEnchantment.Enchantment_Adapter
                    .getEnchants(((Player) sender).getInventory().getItemInMainHand(), true, ((Entity) sender).getWorld(), null)
                    .keySet();
            if (enchs.isEmpty()) {
                sender.sendMessage(Storage.LOGO + "There are no custom enchantments on this tool!");
            } else {
                sender.sendMessage(Storage.LOGO + "Enchantment Info:");
            }

            for (CustomEnchantment ench : enchs) {
                sender.sendMessage(ChatColor.DARK_AQUA + ench.loreName + ": "
                        + (EnchantPlayer.isDisabled((Player) sender, ench.asEnum()) ? ChatColor.RED + "**Disabled** " : "")
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

        Config config = Config.get(((Player) sender).getWorld());

        if (!PermissionTypes.ONOFF.hasPermission(sender)) {
            sender.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            CustomEnchantment ench = config.enchantFromString(args[1]);
            if (ench != null) {
                EnchantPlayer.disable((Player) sender, ench.asEnum());
                sender.sendMessage(Storage.LOGO + "The enchantment " + ChatColor.DARK_AQUA
                        + ench.loreName + ChatColor.AQUA + " has been " + ChatColor.RED + "disabled.");
            } else if (args[1].equalsIgnoreCase("all")) {
                EnchantPlayer.disableAll((Player) sender);
                sender.sendMessage(Storage.LOGO + ChatColor.DARK_AQUA + "All " + ChatColor.AQUA
                        + "enchantments have been " + ChatColor.RED + "disabled.");
            } else {
                sender.sendMessage(Storage.LOGO + "That enchantment does not exist!");
            }
        } else {
            sender.sendMessage(
                    Storage.LOGO + ChatColor.DARK_AQUA + "Usage: " + ChatColor.AQUA + "/ench disable <enchantment/all>");
        }
        return true;
    }

    // Enables the given enchantment for the player
    private static boolean enable(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Config config = Config.get(((Player) sender).getWorld());

        if (!PermissionTypes.ONOFF.hasPermission(sender)) {
            sender.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            CustomEnchantment ench = config.enchantFromString(args[1]);
            if (ench != null) {
                EnchantPlayer.enable((Player) sender, ench.asEnum());
                sender.sendMessage(Storage.LOGO + "The enchantment " + ChatColor.DARK_AQUA
                        + ench.loreName + ChatColor.AQUA + " has been" + ChatColor.GREEN + " enabled.");
            } else if (args[1].equalsIgnoreCase("all")) {
                EnchantPlayer.enableAll((Player) sender);
                sender.sendMessage(Storage.LOGO + ChatColor.DARK_AQUA + "All " + ChatColor.AQUA
                        + "enchantments have been enabled.");
            } else {
                sender.sendMessage(Storage.LOGO + "That enchantment does not exist!");
            }
        } else {
            sender.sendMessage(Storage.LOGO + ChatColor.DARK_AQUA + "Usage: " + ChatColor.AQUA + "/ench enable <enchantment/all>");
        }
        return true;
    }

    // Lists all the commands associated with Custom Enchantments
    private static boolean helpEnchantment(CommandSender player, String label) {
        if (label.isEmpty() || label.equals("help")) {
            player.sendMessage(Storage.LOGO);
            if (PermissionTypes.INFO.hasPermission(player)) {
                player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench info <?enchantment>: " + ChatColor.AQUA
                        + "Returns information about custom enchantments.");
            }
            if (PermissionTypes.LIST.hasPermission(player)) {
                player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench list: " + ChatColor.AQUA
                        + "Returns a list of enchantments for the tool in hand.");
            }
            if (PermissionTypes.GIVE.hasPermission(player)) {
                player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench give <Player> <Material> <enchantment> <?level> ... "
                        + ChatColor.AQUA + "Gives the target a specified enchanted item.");
            }
            if (PermissionTypes.ENCHANT.hasPermission(player)) {
                player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench <enchantment> <?level> <?modifier> <?doNotification> <?force>: "
                        + ChatColor.AQUA + "Enchants the item in hand with the given enchantment and level");
            }
            if (PermissionTypes.ONOFF.hasPermission(player)) {
                player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench <enable/disable> <enchantment/all>: " + ChatColor.AQUA
                        + "Enables/Disables selected enchantment for the user");
            }
            if (PermissionTypes.LASERCOL.hasPermission(player)) {
                player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench lasercol: " + ChatColor.AQUA
                        + "Sets the color of your laser.");
            }
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench version: " + ChatColor.AQUA
                    + "Shows the version the plugin runs on.");
            return true;
        }
        return false;
    }

    private static boolean versionInfo(CommandSender sender) {
        sender.sendMessage(Storage.LOGO + " Using " + Storage.MINILOGO + ChatColor.AQUA + " with version " + ChatColor.RED
                + Storage.version +  ChatColor.AQUA + ".");
        sender.sendMessage(Storage.LOGO + " Brand: " + ChatColor.RED + Storage.BRAND);
        sender.sendMessage(Storage.LOGO + " Distribution: " + ChatColor.RED + Storage.DISTRIBUTION);
        sender.sendMessage(Storage.LOGO + " Download it here:" + ChatColor.DARK_GREEN + " https://github.com/Geolykt/EnchantmentsPlus");
        return true;
    }

    // Control flow for the command processor
    public static boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
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
            case "lasercol":
                return setLaserColor(sender, args);
            case "license":
                return printLicense(sender, args);
            case "help":
            default:
                return helpEnchantment(sender, label) || enchant(sender, args);
            }
        }
        return true;
    }

    private static boolean printLicense(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Storage.LOGO + "This plugin is licensed under the  GNU GENERAL PUBLIC LICENSE Version 3.");
            sender.sendMessage(Storage.LOGO + "Copyright authors:");
            sender.sendMessage(Storage.LOGO + " (C) 2015 - 2020: Zedly and other Zenchantments contributors");
            sender.sendMessage(Storage.LOGO + " (C) 2020 - 2021: Geolykt and other EnchantmentsPlus contributors");
            sender.sendMessage(Storage.LOGO + "To view the full license do /ench license full");
        } else if (args[1].equalsIgnoreCase("full")) {
            if (!sender.hasPermission("enchplus.command.license")) {
                sender.sendMessage(Storage.LOGO + "You do not have the permission to view the license in full.");
                sender.sendMessage(Storage.LOGO + "You may however find the license at "
                        + "https://github.com/Geolykt/EnchantmentsPlus/blob/v3.0.0-rc.2/LICENSE.md.");
                return true;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(Storage.plugin.getResource("LICENSE.md")));
            br.lines().forEachOrdered((line) -> {
                sender.sendMessage(line);
            });
        }
        return true;
    }

    private static boolean enchant(CommandSender sender, String[] args) {
        if (!PermissionTypes.ENCHANT.hasPermission(sender)) {
            sender.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        int level = 1;
        if (args.length > 1) {
            try {
                level = Integer.decode(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Storage.LOGO + ChatColor.RED + "Argument 2 is not a number, however an Integer was expected");
                return true;
            }
        }
        switch (args.length) {
        case 0:
            return false;
        case 1:
        case 2:
            if (sender instanceof Player) {
                return ench(sender,args[0], level, true, false, (Player) sender);
            } else {
                sender.sendMessage(Storage.LOGO + ChatColor.RED + "You need to provide more data as a non-player command sender.");
                return true;
            }
        case 3:
            return ench(sender, args[0], level, args[2], true, false);
        case 4:
            return ench(sender, args[0], level, args[2], Boolean.parseBoolean(args[3]), false);
        case 5:
        default:
            return ench(sender, args[0], level, args[2], Boolean.parseBoolean(args[3]),  Boolean.parseBoolean(args[4]));
        }
    }

    /**
     * Performs the enchantment command with a known target
     * @param infoReciever The receiver that should get the feedback of the command
     * @param eName The name of the enchantment
     * @param level The level of the enchantment
     * @param targetModif Dictates who gets the enchantment
     * @param doNotify True if the targets should receive feedback
     * @param force Whether or not the enchantment should be forced onto the tool, ignoring several things (UNSAFE!)
     * @return True if the enchantment was performed correctly
     * @since 2.2.0
     */
    private static boolean ench(CommandSender infoReciever, String eName, Integer level, String targetModif, boolean doNotify, boolean force) {
        List<Entity> target = Bukkit.selectEntities(infoReciever, targetModif);
        if (target.size() == 0) {
            infoReciever.sendMessage(Storage.LOGO + ChatColor.AQUA + "No entities changed.");
            return true;
        }
        for (Entity e: target) {
            if (e instanceof Player) {
                if (doNotify) {
                    ench((CommandSender) e, eName, level, doNotify, force, (Player) e);
                } else {
                    ench(infoReciever, eName, level, doNotify, force, (Player) e);
                }
            } else if (e instanceof Monster) {
                ItemStack stack = ((Monster) e).getEquipment().getItemInMainHand();
                if (stack != null) {
                    CustomEnchantment.setEnchantment(stack, Config.get(e.getWorld()).enchantFromString(eName), level, e.getWorld());
                    ((Monster) e).getEquipment().setItemInMainHand(stack);
                }
            }
        }
        return true;
    }

    /**
     * Performs the enchantment command on a given target and broadcasts information to the given sender
     * @param infoReciever The receiver that should get the feedback of the command
     * @param enchantmentName The name of the enchantment
     * @param level The level of the enchantment
     * @param doNotify true if the infoReciever should be notified about feedback, false otherwise
     * @param force True if the enchantments should be applied without thinking, if false incompatibilities are sorted out automatically
     * @param target The player that the enchantment should be applied on
     * @since 3.0.0
     */
    private static boolean ench(CommandSender infoReciever, String enchantmentName, Integer level, boolean doNotify, boolean force,
            Player target) {
        Config cfg = Config.get(target.getWorld());
        CustomEnchantment ench = cfg.enchantFromString(enchantmentName);
        ItemStack stack = target.getInventory().getItemInMainHand();
        if (ench == null) {
            if (doNotify) {
                infoReciever.sendMessage(Storage.LOGO + ChatColor.RED + "This is not a valid enchantment");
            }
            return true;
        } else if (stack.getType() == Material.AIR) {
            if (doNotify) {
                infoReciever.sendMessage(Storage.LOGO + ChatColor.RED + " You cannot enchant air");
            }
            return true;
        } else {
            if (force
                    || level <= 0 
                    || CustomEnchantment.getEnchantLevel(cfg, stack, ench.asEnum()) > 0) {
                CustomEnchantment.setEnchantment(stack, ench, level, target.getWorld());
                return true;
            } else {
                Map<CustomEnchantment, Integer> enchs = CustomEnchantment.getEnchants(stack, target.getWorld(), null);
                for (Map.Entry<CustomEnchantment, Integer> potentiallyConflicting : enchs.entrySet()) {
                    for (BaseEnchantments conflict : ench.getConflicts()) {
                        if (potentiallyConflicting.getKey().asEnum() == conflict) {
                            infoReciever.sendMessage(Storage.LOGO + ChatColor.RED + "An incompatible enchantment is already on your tool!");
                            return true;
                        }
                    }
                }
                if (enchs.size() >= cfg.getMaxEnchants()) {
                    if (doNotify) {
                        infoReciever.sendMessage(Storage.LOGO + ChatColor.RED + "You already have too many enchantments on your tool!");
                    }
                    return true;
                } else if (ench.validMaterial(stack) || stack.getType() == Material.ENCHANTED_BOOK || stack.getType() == Material.BOOK) {
                    CustomEnchantment.setEnchantment(stack, ench, level, target.getWorld());
                } else if (doNotify) {
                    infoReciever.sendMessage(Storage.LOGO + ChatColor.RED + "The enchantment cannot be applied on your current tool.");
                    return true;
                }
            }
            if (doNotify) {
                if (level <= 0) {
                    infoReciever.sendMessage(Storage.LOGO + ChatColor.AQUA + "The enchantment " + ChatColor.BLUE + ench.getLoreName() + ChatColor.AQUA + " was removed from your tool.");
                } else if (level == 1) {
                    infoReciever.sendMessage(Storage.LOGO + ChatColor.AQUA + "Your tool in hand was enchanted with " + ChatColor.BLUE + ench.getLoreName() + ChatColor.AQUA + ".");
                } else {
                    infoReciever.sendMessage(Storage.LOGO + ChatColor.AQUA + "Your tool in hand was enchanted with " + ChatColor.BLUE + ench.getLoreName() + ChatColor.AQUA + " at level " + ChatColor.BLUE + level + ChatColor.AQUA + ".");
                }
                return true;
            }
            return true;
        }
    }

    /**
     * Processes the lasercol subcommand
     * @param sender The sender of the request
     * @param args The arguments for the subcommand, where as args[0] should be "lasercol" (not used internally)
     * @return True if the command was performed successfully, false otherwise
     * @since 1.1.1
     */
    private static boolean setLaserColor(CommandSender sender, String[] args) {
        if (!PermissionTypes.LASERCOL.hasPermission(sender)) {
            sender.sendMessage(Storage.LOGO + "You do not have permission to do this!");
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType().isAir()) {
                player.sendMessage(Storage.LOGO + ChatColor.RED + "Did you thought that air was a Laser?");
                return true;
            }
            final ItemStack stk = player.getInventory().getItemInMainHand();
            if (CustomEnchantment.hasEnchantment(Config.get(player.getWorld()), stk, BaseEnchantments.LASER)) {
                Laser.setColor(stk, 
                        args.length != 1 ? ColUtil.toBukkitColor(args[1].toUpperCase(Locale.ROOT), Color.RED) : Color.RED);
                player.getInventory().setItemInMainHand(stk);
                if (player.getLocale().toLowerCase(Locale.ROOT).contains("uk")) {
                    player.sendMessage(Storage.LOGO + ChatColor.RED + "Colour of the laser set successfully.");
                } else {
                    player.sendMessage(Storage.LOGO + ChatColor.RED + "Color of the laser set successfully.");
                }
            } else {
                player.sendMessage(Storage.LOGO + ChatColor.RED + "The item in hand does not have the laser enchantment applied.");
            }
        }
        return true;
    }
}
