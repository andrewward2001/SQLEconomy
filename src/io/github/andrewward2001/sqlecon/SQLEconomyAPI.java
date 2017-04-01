package io.github.andrewward2001.sqlecon;

import java.sql.Connection;

public class SQLEconomyAPI {
	
	private Connection c;
	
	private SQLEconomyActions a;
	
	public SQLEconomyAPI(Connection c) {
		this.c = c;
		this.a = new SQLEconomyActions();
	}
	
	public String currencyName() {
		return SQLEconomy.moneyUnit;
	}
	
	public boolean accountExists(String name) {
		return SQLEconomyActions.playerDataContainsPlayer(name);
	}
	
	public int getBalance(String name) {
		return SQLEconomyActions.getMoney(name);
	}
	
	public boolean hasEnough(String name, double amount) {
		int bal = getBalance(name);
		
		if(bal >= (int) amount) {
			return true;
		}
		
		return false;
	}
	
	public boolean withdraw(String name, double amount) {
		return SQLEconomyActions.removeMoney(name, (int) amount);
	}
	
	public boolean give(String name, double amount) {
		return SQLEconomyActions.giveMoney(name, (int) amount);
	}

}
