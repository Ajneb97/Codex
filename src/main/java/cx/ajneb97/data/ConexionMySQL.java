package cx.ajneb97.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import cx.ajneb97.Codex;

public class ConexionMySQL {

	private ConexionHikari connection;
	private String host;
	private String database;
	private String username;
	private String password;
	private int port;
	
	public void setupMySql(Codex plugin,FileConfiguration config){
		try {
			host = config.getString("mysql_database.host");
			port = Integer.valueOf(config.getString("mysql_database.port"));
			database = config.getString("mysql_database.database");		
			username = config.getString("mysql_database.username");
			password = config.getString("mysql_database.password");
			connection = new ConexionHikari(host,port,database,username,password);
			connection.getHikari().getConnection();
			MySQL.createTable(plugin);
			Bukkit.getConsoleSender().sendMessage(plugin.nombrePlugin+ChatColor.GREEN + "Successfully connected to the Database.");
		}catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(plugin.nombrePlugin+ChatColor.RED + "Error while connecting to the Database.");
		}
	}

	public String getDatabase() {
		return this.database;
	}
	
	public Connection getConnection() {
		try {
			return connection.getHikari().getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
