package cx.ajneb97.database;

import cx.ajneb97.Codex;
import cx.ajneb97.managers.MessagesManager;
import cx.ajneb97.model.data.PlayerData;
import cx.ajneb97.model.data.PlayerDataCategory;
import cx.ajneb97.model.data.PlayerDataDiscovery;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MySQLConnection {

    private Codex plugin;
    private HikariConnection connection;
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;

    public MySQLConnection(Codex plugin){
        this.plugin = plugin;
    }

    public void setupMySql(){
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();
        try {
            host = config.getString("mysql_database.host");
            port = config.getInt("mysql_database.port");
            database = config.getString("mysql_database.database");
            username = config.getString("mysql_database.username");
            password = config.getString("mysql_database.password");
            connection = new HikariConnection(host,port,database,username,password);
            connection.getHikari().getConnection();
            createTables();
            loadData();
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(plugin.prefix+"&aSuccessfully connected to the Database."));
        }catch(Exception e) {
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(plugin.prefix+"&cError while connecting to the Database."));
        }
    }


    public String getDatabase() {
        return this.database;
    }

    public Connection getConnection() {
        try {
            return connection.getHikari().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadData(){
        Map<UUID, PlayerData> playerMap = new HashMap<>();
        try(Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT codex_players.UUID, codex_players.PLAYER_NAME, " +
                            "(SELECT GROUP_CONCAT(cc.Category) FROM codex_players_completed_categories cc WHERE cc.UUID = codex_players.UUID) AS COMPLETED_CATEGORIES, " +
                            "codex_players_discoveries.CATEGORY AS DISCOVERY_CATEGORY, " +
                            "codex_players_discoveries.DISCOVERY, " +
                            "codex_players_discoveries.DATE " +
                            "FROM codex_players " +
                            "LEFT JOIN codex_players_discoveries ON codex_players.UUID = codex_players_discoveries.UUID");

            ResultSet result = statement.executeQuery();
            while(result.next()){
                UUID uuid = UUID.fromString(result.getString("UUID"));
                String playerName = result.getString("PLAYER_NAME");
                String completedCategories = result.getString("COMPLETED_CATEGORIES");
                String discoveryCategoryName = result.getString("DISCOVERY_CATEGORY");
                String discoveryName = result.getString("DISCOVERY");
                String discoveryDate = result.getString("DATE");

                PlayerData player = playerMap.get(uuid);
                if(player == null){
                    //Create and add it
                    player = new PlayerData(uuid,playerName);
                    playerMap.put(uuid, player);
                }

                if(discoveryCategoryName != null){
                    boolean hasCompletedCategory = completedCategories != null && completedCategories.contains(discoveryCategoryName);
                    PlayerDataCategory playerDataCategory = player.getCategory(discoveryCategoryName);
                    if(playerDataCategory == null){
                        playerDataCategory = new PlayerDataCategory(discoveryCategoryName,hasCompletedCategory,new ArrayList<>());
                        player.getCategories().add(playerDataCategory);
                    }

                    playerDataCategory.getDiscoveries().add(new PlayerDataDiscovery(discoveryName,discoveryDate));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        plugin.getPlayerDataManager().setPlayers(playerMap);
    }

    public void createTables() {
        try(Connection connection = getConnection()){
            PreparedStatement statement1 = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS codex_players" +
                    " (UUID varchar(36) NOT NULL, " +
                    " PLAYER_NAME varchar(50), " +
                    " PRIMARY KEY ( UUID ))"
            );
            statement1.executeUpdate();
            PreparedStatement statement2 = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS codex_players_discoveries" +
                    " (ID int NOT NULL AUTO_INCREMENT, " +
                    " UUID varchar(36) NOT NULL, " +
                    " CATEGORY varchar(100), " +
                    " DISCOVERY varchar(100), " +
                    " DATE varchar(100), " +
                    " PRIMARY KEY ( ID ), " +
                    " FOREIGN KEY (UUID) REFERENCES codex_players(UUID))");
            statement2.executeUpdate();
            PreparedStatement statement3 = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS codex_players_completed_categories" +
                            " (ID int NOT NULL AUTO_INCREMENT, " +
                            " UUID varchar(36) NOT NULL, " +
                            " CATEGORY varchar(100), " +
                            " PRIMARY KEY ( ID ), " +
                            " FOREIGN KEY (UUID) REFERENCES codex_players(UUID))");
            statement3.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getPlayer(String uuid,PlayerCallback callback){
        new BukkitRunnable(){
            @Override
            public void run() {
                PlayerData player = null;
                try(Connection connection = getConnection()){
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT codex_players.UUID, codex_players.PLAYER_NAME, " +
                                    "(SELECT GROUP_CONCAT(cc.Category) FROM codex_players_completed_categories cc WHERE cc.UUID = codex_players.UUID) AS COMPLETED_CATEGORIES, " +
                                    "codex_players_discoveries.CATEGORY AS DISCOVERY_CATEGORY, " +
                                    "codex_players_discoveries.DISCOVERY, " +
                                    "codex_players_discoveries.DATE " +
                                    "FROM codex_players " +
                                    "LEFT JOIN codex_players_discoveries ON codex_players.UUID = codex_players_discoveries.UUID " +
                                    "WHERE codex_players.UUID = ?");

                    statement.setString(1, uuid);
                    ResultSet result = statement.executeQuery();

                    while(result.next()){
                        UUID uuid = UUID.fromString(result.getString("UUID"));
                        String playerName = result.getString("PLAYER_NAME");
                        String completedCategories = result.getString("COMPLETED_CATEGORIES");
                        String discoveryCategoryName = result.getString("DISCOVERY_CATEGORY");
                        String discoveryName = result.getString("DISCOVERY");
                        String discoveryDate = result.getString("DATE");

                        if(player == null){
                            player = new PlayerData(uuid,playerName);
                        }

                        if(discoveryCategoryName != null){
                            boolean hasCompletedCategory = completedCategories != null && completedCategories.contains(discoveryCategoryName);
                            PlayerDataCategory playerDataCategory = player.getCategory(discoveryCategoryName);
                            if(playerDataCategory == null){
                                playerDataCategory = new PlayerDataCategory(discoveryCategoryName,hasCompletedCategory,new ArrayList<>());
                                player.getCategories().add(playerDataCategory);
                            }

                            playerDataCategory.getDiscoveries().add(new PlayerDataDiscovery(discoveryName,discoveryDate));
                        }
                    }

                    PlayerData finalPlayer = player;
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            callback.onDone(finalPlayer);
                        }
                    }.runTask(plugin);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void createPlayer(PlayerData player){
        new BukkitRunnable(){
            @Override
            public void run() {
                try(Connection connection = getConnection()){
                    PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO codex_players " +
                                    "(UUID, PLAYER_NAME) VALUE (?,?)");

                    statement.setString(1, player.getUuid().toString());
                    statement.setString(2, player.getName());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void updatePlayerName(PlayerData player){
        new BukkitRunnable(){
            @Override
            public void run() {
                try(Connection connection = getConnection()){
                    PreparedStatement statement = connection.prepareStatement(
                            "UPDATE codex_players SET " +
                                    "PLAYER_NAME=? WHERE UUID=?");

                    statement.setString(1, player.getName());
                    statement.setString(2, player.getUuid().toString());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void addDiscovery(String uuid,String categoryName,String discoveryName,String discoveryDate){
        new BukkitRunnable(){
            @Override
            public void run() {
                try(Connection connection = getConnection()){
                    PreparedStatement statement = null;
                    statement = connection.prepareStatement(
                            "INSERT INTO codex_players_discoveries " +
                                    "(UUID, CATEGORY, DISCOVERY, DATE) VALUE (?,?,?,?)");

                    statement.setString(1, uuid);
                    statement.setString(2, categoryName);
                    statement.setString(3, discoveryName);
                    statement.setString(4, discoveryDate);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void updateCompletedCategories(String uuid,String categoryName){
        new BukkitRunnable(){
            @Override
            public void run() {
                try(Connection connection = getConnection()){
                    PreparedStatement statement = null;
                    statement = connection.prepareStatement(
                            "INSERT INTO codex_players_completed_categories " +
                                    "(UUID, CATEGORY) VALUE (?,?)");

                    statement.setString(1, uuid);
                    statement.setString(2, categoryName);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void resetDataPlayer(String uuid, String categoryName, String discoveryName){
        new BukkitRunnable(){
            @Override
            public void run() {
                try(Connection connection = getConnection()){
                    PreparedStatement statement = null;
                    PreparedStatement statement2 = null;

                    if(categoryName == null) {
                        if (uuid.equals("*")) {
                            // Reset all data for all players
                            statement = connection.prepareStatement(
                                    "DELETE FROM codex_players_discoveries");
                            statement2 = connection.prepareStatement(
                                    "DELETE FROM codex_players_completed_categories");
                        }else{
                            // Reset all data for player
                            statement = connection.prepareStatement(
                                    "DELETE FROM codex_players_discoveries WHERE UUID=?");
                            statement.setString(1, uuid);
                            statement2 = connection.prepareStatement(
                                    "DELETE FROM codex_players_completed_categories WHERE UUID=?");
                            statement2.setString(1, uuid);
                        }
                    }else{
                        if(discoveryName == null) {
                            if (uuid.equals("*")) {
                                // Reset category for all players
                                statement = connection.prepareStatement(
                                        "DELETE FROM codex_players_discoveries WHERE CATEGORY=?");
                                statement.setString(1, categoryName);
                                statement2 = connection.prepareStatement(
                                        "DELETE FROM codex_players_completed_categories WHERE CATEGORY=?");
                                statement2.setString(1, categoryName);
                            }else{
                                // Reset category for player
                                statement = connection.prepareStatement(
                                        "DELETE FROM codex_players_discoveries WHERE UUID=? AND CATEGORY=?");
                                statement.setString(1, uuid);statement.setString(2, categoryName);
                                statement2 = connection.prepareStatement(
                                        "DELETE FROM codex_players_completed_categories WHERE UUID=? AND CATEGORY=?");
                                statement2.setString(1, uuid);statement2.setString(2, categoryName);
                            }
                        }else{
                            if (uuid.equals("*")) {
                                // Reset discovery for all players
                                statement = connection.prepareStatement(
                                        "DELETE FROM codex_players_discoveries WHERE CATEGORY=? AND DISCOVERY=?");
                                statement.setString(1, categoryName);statement.setString(2, discoveryName);
                            }else{
                                // Reset discovery for player
                                statement = connection.prepareStatement(
                                        "DELETE FROM codex_players_discoveries WHERE UUID=? AND CATEGORY=? AND DISCOVERY=?");
                                statement.setString(1, uuid);statement.setString(2, categoryName);
                                statement.setString(3, discoveryName);
                            }
                        }
                    }

                    statement.executeUpdate();
                    if(statement2 != null){
                        statement2.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

}
