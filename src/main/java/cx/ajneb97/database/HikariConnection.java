package cx.ajneb97.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

public class HikariConnection {

    private HikariDataSource hikari;

    public HikariConnection(FileConfiguration config) {
        HikariConfig hikariConfig = new HikariConfig();
        String path = "mysql_database";

        String host = config.getString(path+".host");
        int port = config.getInt(path+".port");
        String database = config.getString(path+".database");
        String username = config.getString(path+".username");
        String password = config.getString(path+".password");

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        if(config.contains(path+".pool.connectionTimeout")){
            hikariConfig.setConnectionTimeout(config.getLong(path+".pool.connectionTimeout"));
        }
        if(config.contains(path+".pool.maximumPoolSize")){
            hikariConfig.setMaximumPoolSize(config.getInt(path+".pool.maximumPoolSize"));
        }
        if(config.contains(path+".pool.connectionTimeout")){
            hikariConfig.setKeepaliveTime(config.getLong(path+".pool.keepaliveTime"));
        }
        if(config.contains(path+".pool.idleTimeout")){
            hikariConfig.setIdleTimeout(config.getLong(path+".pool.idleTimeout"));
        }
        if(config.contains(path+".pool.maxLifetime")){
            hikariConfig.setMaxLifetime(config.getLong(path+".pool.maxLifetime"));
        }

        for(String key : config.getConfigurationSection(path+".advanced").getKeys(false)){
            hikariConfig.addDataSourceProperty(key,config.get(path+".advanced."+key));
        }

        hikari = new HikariDataSource(hikariConfig);
    }

    public HikariDataSource getHikari() {
        return this.hikari;
    }

    public void disable() {
        if(hikari != null) {
            hikari.close();
        }
    }
}
