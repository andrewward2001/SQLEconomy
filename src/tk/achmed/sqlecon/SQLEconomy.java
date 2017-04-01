package tk.achmed.sqlecon;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import java.sql.Connection;

import tk.achmed.sqlecon.mysql.*;

public class SQLEconomy extends JavaPlugin implements Listener {

	private Plugin plugin;

	// Making variables for SQL connection static, escaping errors
	private static String host;
	private static String port;
	private static String database;
	private static String table;
	private static String user;
	private static String pass;
	private static String defMoney;

	private static String moneyUnit;

	private static MySQL MySQL;
	private static Connection c;

	public void onDisable() {
	}

	public void onEnable() {
		this.saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		this.saveConfig();

		host = getConfig().getString("DatabaseHostIP");
		port = getConfig().getString("DatabasePort");
		database = getConfig().getString("DatabaseName");
		table = getConfig().getString("DatabaseTable");
		user = getConfig().getString("DatabaseUsername");
		pass = getConfig().getString("DatabasePassword");
		defMoney = getConfig().getString("DefaultMoney");

		moneyUnit = getConfig().getString("MoneyUnit");

		MySQL = new MySQL(host, port, database, user, pass);
		try {
			c = MySQL.openConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		createTable();

		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}

	public synchronized static boolean playerDataContainsPlayer(Player player) {
		try {
			Statement sql = c.createStatement();
			ResultSet resultSet = sql.executeQuery(
					"SELECT * FROM `" + table + "` WHERE `player_uuid` = '" + player.getUniqueId() + "';");
			boolean containsPlayer = resultSet.next();

			sql.close();
			resultSet.close();

			return containsPlayer;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();

		try {
			if (!playerDataContainsPlayer(player)) {
				PreparedStatement econRegister = c.prepareStatement(
						"INSERT INTO `" + table + "` (player, player_uuid, money, active) VALUES (?, ?, ?, ?);");
				econRegister.setString(1, player.getName());
				econRegister.setString(2, player.getUniqueId().toString());
				econRegister.setString(3, defMoney);
				econRegister.setLong(4, 1);
				econRegister.executeUpdate();
				econRegister.close();
				
				System.out.println("Added user " + player.getName() + " to the economy database.");
			} else {
				// make sure the stored player name is kept current

				Statement statement = c.createStatement();
				statement.executeUpdate("UPDATE `" + table + "` SET player = '" + player.getName()
						+ "' WHERE player_uuid = '" + player.getUniqueId().toString() + "';");
				statement.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createTable() {
		try {
			Statement tableCreate = c.createStatement();
			tableCreate.execute("CREATE TABLE IF NOT EXISTS `" + table + "` (`player_id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT, `player` varchar(255) NOT NULL, `player_uuid` varchar(255) NOT NULL, `money` int(20) NOT NULL, `active` int(1) NOT NULL DEFAULT '1') ENGINE=InnoDB DEFAULT CHARSET=latin1;");

			System.out.println("[SQLEconomy] Created/checked the database table");
			tableCreate.close();
		} catch (MySQLSyntaxErrorException e) {
			e.printStackTrace();
			System.out.println(
					"[SQLEconomy] There was a snag initializing the database. Please send the ENTIRE stack trace above.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean giveMoney(UUID uid, int amount) {
		try {
			PreparedStatement giveMoney = c
					.prepareStatement("UPDATE `" + table + "` SET money = money + ? WHERE player_uuid=?;");
			giveMoney.setInt(1, amount);
			giveMoney.setString(2, uid.toString());
			giveMoney.executeUpdate();

			giveMoney.close();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean giveMoney(String name, int amount) {
		try {
			PreparedStatement giveMoney = c
					.prepareStatement("UPDATE `" + table + "` SET money = money + ? WHERE player=?;");
			giveMoney.setInt(1, amount);
			giveMoney.setString(2, name);
			giveMoney.executeUpdate();

			giveMoney.close();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean removeMoney(UUID uid, int amount) {
		try {
			PreparedStatement removeMoney = c
					.prepareStatement("UPDATE `" + table + "` SET money = money - ? WHERE player_uuid=?;");
			removeMoney.setInt(1, amount);
			removeMoney.setString(2, uid.toString());
			removeMoney.executeUpdate();

			removeMoney.close();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean removeMoney(String name, int amount) {
		try {
			PreparedStatement getBal = c
					.prepareStatement("UPDATE `" + table + "` SET money = money - ? WHERE player=?;");
			getBal.setInt(1, amount);
			getBal.setString(2, name);
			getBal.executeUpdate();

			getBal.close();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public int getMoney(UUID uid) {

		try {
			Statement getMoney = c.createStatement();
			ResultSet res = getMoney
					.executeQuery("SELECT money FROM `" + table + "` WHERE player_uuid = '" + uid.toString() + "';");
			res.next();
			int money = res.getInt("money");

			getMoney.close();
			res.close();

			return money;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;

	}

	public int getMoney(String name) {

		try {
			PreparedStatement getMoney = c.prepareStatement("SELECT money FROM `" + table + "` WHERE player = ?;");
			getMoney.setString(1, name);
			ResultSet res = getMoney.executeQuery();
			res.next();

			int money = 0;
			if (res.getString("money") != null)
				money = res.getInt("money");

			getMoney.close();
			res.close();

			return money;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("money")) {
			if (args.length == 0) {
				Player player;
				if (sender instanceof Player) {
					player = (Player) sender;
					int money = getMoney(player.getUniqueId());

					sender.sendMessage(ChatColor.GREEN + "Money: " + ChatColor.WHITE + money + " " + moneyUnit);
				} else
					sender.sendMessage(ChatColor.RED + "You must be a player to do that!");

			} else if (args.length == 1) {

				if (args[0].equalsIgnoreCase("give")) {
					sender.sendMessage(
							ChatColor.RED + "Incorrect Usage! /money give [player] <amount> --> player is optional.");
				} else if (args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("remove")) {
					sender.sendMessage(
							ChatColor.RED + "Incorrect Usage! /money remove [player] <amount> --> player is optional.");
				} else {
					int money = getMoney(args[0]);

					sender.sendMessage(ChatColor.GREEN + "Money: " + ChatColor.WHITE + money + " " + moneyUnit);
				}
			} else if (args.length == 2) {
				Player player;
				if (sender instanceof Player) {
					player = (Player) sender;
					if (args[0].equalsIgnoreCase("give")) {
						if (isInteger(args[1])) {
							giveMoney(player.getUniqueId(), Integer.parseInt(args[1]));
							sender.sendMessage("Gave " + ChatColor.GREEN + args[1] + ChatColor.WHITE + " " + moneyUnit
									+ " to yourself. Congratulations.");

						} else {
							sender.sendMessage("Amount is not a number!");
						}
					} else if (args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("remove")) {
						if (isInteger(args[1])) {
							removeMoney(player.getUniqueId(), Integer.parseInt(args[1]));
							sender.sendMessage("Removed " + ChatColor.RED + args[1] + ChatColor.WHITE + " " + moneyUnit
									+ " from yourself.");
						} else {
							sender.sendMessage("Amount is not a number!");
						}
					}
				} else
					sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
			} else if (args.length == 3) {
				if (sender instanceof Player) {
					if (args[0].equalsIgnoreCase("give")) {
						if (isInteger(args[2])) {
							giveMoney(args[1], Integer.parseInt(args[2]));
							sender.sendMessage("Gave " + ChatColor.GREEN + args[2] + ChatColor.WHITE + " " + moneyUnit
									+ " to " + args[1] + ".");

						} else {
							sender.sendMessage("Amount is not a number!");
						}
					} else if (args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("remove")) {
						if (isInteger(args[2])) {
							removeMoney(args[1], Integer.parseInt(args[2]));
							sender.sendMessage("Removed " + ChatColor.RED + args[2] + ChatColor.WHITE + " " + moneyUnit
									+ " from " + args[1] + ".");
						} else {
							sender.sendMessage("Amount is not a number!");
						}
					}
				} else
					sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
			}
		}

		return false;
	}

	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

}
