package io.github.andrewward2001.sqlecon;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {
	
	private final SQLEconomy plugin;
	
	private String moneyUnit;
	
	public MoneyCommand(SQLEconomy plugin) {
		this.plugin = plugin;
		
		moneyUnit = SQLEconomy.moneyUnit;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("money")) {
			if (args.length == 0) {
				Player player;
				if (sender instanceof Player) {
					player = (Player) sender;
					int money = SQLEconomyActions.getMoney(player.getUniqueId());

					sender.sendMessage(ChatColor.GREEN + "Money: " + ChatColor.WHITE + money + " " + moneyUnit);
					
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
					
					return false;
				}

			} else if (args.length == 1) {

				if (args[0].equalsIgnoreCase("give")) {
					sender.sendMessage(
							ChatColor.RED + "Incorrect Usage! /money give [player] <amount> --> player is optional.");
					return false;
				} else if (args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("remove")) {
					sender.sendMessage(
							ChatColor.RED + "Incorrect Usage! /money remove [player] <amount> --> player is optional.");
					return false;
				} else {
					int money = SQLEconomyActions.getMoney(args[0]);

					sender.sendMessage(ChatColor.GREEN + "Money: " + ChatColor.WHITE + money + " " + moneyUnit);
					
					return true;
				}
			} else if (args.length == 2) {
				Player player;
				if (sender instanceof Player) {
					player = (Player) sender;
					if (args[0].equalsIgnoreCase("give")) {
						if (plugin.isInteger(args[1])) {
							SQLEconomyActions.giveMoney(player.getUniqueId(), Integer.parseInt(args[1]));
							sender.sendMessage("Gave " + ChatColor.GREEN + args[1] + ChatColor.WHITE + " " + moneyUnit
									+ " to yourself. Congratulations.");

							return true;
						} else {
							sender.sendMessage("Amount is not a number!");
							
							return false;
						}
					} else if (args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("remove")) {
						if (plugin.isInteger(args[1])) {
							SQLEconomyActions.removeMoney(player.getUniqueId(), Integer.parseInt(args[1]));
							sender.sendMessage("Removed " + ChatColor.RED + args[1] + ChatColor.WHITE + " " + moneyUnit
									+ " from yourself.");
							
							return true;
						} else {
							sender.sendMessage("Amount is not a number!");
							
							return false;
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
				
					return false;
				}	
			} else if (args.length == 3) {
				if (sender instanceof Player) {
					if (args[0].equalsIgnoreCase("give")) {
						if (plugin.isInteger(args[2])) {
							SQLEconomyActions.giveMoney(args[1], Integer.parseInt(args[2]));
							sender.sendMessage("Gave " + ChatColor.GREEN + args[2] + ChatColor.WHITE + " " + moneyUnit
									+ " to " + args[1] + ".");

							return true;
						} else {
							sender.sendMessage("Amount is not a number!");
							
							return false;
						}
					} else if (args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("remove")) {
						if (plugin.isInteger(args[2])) {
							SQLEconomyActions.removeMoney(args[1], Integer.parseInt(args[2]));
							sender.sendMessage("Removed " + ChatColor.RED + args[2] + ChatColor.WHITE + " " + moneyUnit
									+ " from " + args[1] + ".");
							
							return true;
						} else {
							sender.sendMessage("Amount is not a number!");
							
							return false;
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
					
					return false;
				}
			}
		}
		
		return false;
	}

}
