package io.github.andrewward2001.sqlecon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

public class SQLEconomyActions {

	private static Connection c = SQLEconomy.c;
	private static String table = SQLEconomy.getTable();

	public synchronized static boolean playerDataContainsPlayer(UUID uid) {
		try {
			Statement sql = c.createStatement();
			ResultSet resultSet = sql.executeQuery(
					"SELECT * FROM `" + table + "` WHERE `player_uuid` = '" + uid + "';");
			boolean containsPlayer = resultSet.next();

			sql.close();
			resultSet.close();

			return containsPlayer;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public synchronized static boolean playerDataContainsPlayer(String player) {
		try {
			Statement sql = c.createStatement();
			ResultSet resultSet = sql.executeQuery(
					"SELECT * FROM `" + table + "` WHERE `player` = '" + player + "';");
			boolean containsPlayer = resultSet.next();

			sql.close();
			resultSet.close();

			return containsPlayer;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void createTable() {
		try {
			Statement tableCreate = c.createStatement();
			tableCreate.execute("CREATE TABLE IF NOT EXISTS `" + table
					+ "` (`player_id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT, `player` varchar(255) NOT NULL, `player_uuid` varchar(255) NOT NULL, `money` int(20) NOT NULL, `active` int(1) NOT NULL DEFAULT '1') ENGINE=InnoDB DEFAULT CHARSET=latin1;");

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

	public static boolean giveMoney(UUID uid, int amount) {
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

	public static boolean giveMoney(String name, int amount) {
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

	public static boolean removeMoney(UUID uid, int amount) {
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

	public static boolean removeMoney(String name, int amount) {
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

	public static int getMoney(UUID uid) {

		try {
			Statement getMoney = c.createStatement();
			ResultSet res = getMoney
					.executeQuery("SELECT money FROM `" + table + "` WHERE player_uuid = '" + uid.toString() + "';");
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

	public static int getMoney(String name) {

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
	
	public static boolean createAccount(OfflinePlayer player) {
		try {
			PreparedStatement econRegister = c.prepareStatement(
					"INSERT INTO `" + table + "` (player, player_uuid, money, active) VALUES (?, ?, ?, ?);");
			econRegister.setString(1, player.getName());
			econRegister.setString(2, player.getUniqueId().toString());
			econRegister.setString(3, SQLEconomy.getDefaultMoney());
			econRegister.setLong(4, 1);
			econRegister.executeUpdate();
			econRegister.close();
			
			System.out.println("[SQLEconomy] Added user " + player.getName() + " to the economy database.");
		} catch (SQLException e) {
			System.out.println("[SQLEconomy] Error creating user!");
		}
		
		return false;
	}
	
	public static boolean createAccount(String name) {
		try {
			PreparedStatement econRegister = c.prepareStatement(
					"INSERT INTO `" + table + "` (player, player_uuid, money, active) VALUES (?, ?, ?, ?);");
			econRegister.setString(1, name);
			econRegister.setString(2, UUID.randomUUID().toString());
			econRegister.setString(3, SQLEconomy.getDefaultMoney());
			econRegister.setLong(4, 1);
			econRegister.executeUpdate();
			econRegister.close();
			
			System.out.println("[SQLEconomy] Added user " + name + " to the economy database.");
		} catch (SQLException e) {
			System.out.println("[SQLEconomy] Error creating user!");
		}
		
		return false;
	}

}
