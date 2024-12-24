package cx.ajneb97.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariConnection {

    private HikariDataSource hikari;

    public HikariConnection(String ip, int port, String database, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("autoReconnect", "true");
        config.addDataSourceProperty("leakDetectionThreshold", "true");
        config.addDataSourceProperty("verifyServerCertificate", "false");
        config.addDataSourceProperty("useSSL", "false");
        config.setConnectionTimeout(5000);
        hikari = new HikariDataSource(config);
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
